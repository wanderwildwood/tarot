#!/usr/bin/env python3
"""Turn the 1909 Rider-Waite-Smith scans into greyscale card art for the Kompakt.

The Kompakt's panel shows sixteen greys, so each card is stored as sixteen greys at about the
scan's own resolution, and the app shrinks it to the screen and lets you zoom in. The
processing is kept to what the panel needs and nothing that would eat Smith's fine lines:

1. Square the scan up. The projection-profile method on the black keyline: a straight frame
   line squared up piles all its ink into one row, so the angle at which the profiles are most
   sharply peaked wins. Rotated by that angle, bicubic, and only when it is a tenth of a degree
   or more.
2. Crop just outside the frame Smith drew round the picture and its title, dropping the cream
   card margin (the app draws its own edge).
3. Grey. The cream paper and Smith's stencilled yellow differ almost only in blue, and the
   yellow and the pale blue differ mostly in red, so the mix (MIX) weighs red most and gives blue
   a real share; plain luminance makes the yellow sky the same grey as the paper. A gentle curve
   (GAMMA) then opens up the light end, where paper, yellow and blue all sit. On the palette
   clustered from the scans, paper, yellow and blue land two to three of the sixteen levels apart.
4. Levels: the paper goes to white and the keyline to black, measured per card because the scans
   differ. White is the brighter of the cream margin outside the frame and the lightest paper
   inside it (the margins are often dirtier than the picture).
5. Sixteen levels, no dithering: the stencilled areas are flat, so there is no gradient to band.

Writes card_<id>.webp (lossless) at full size and thumb_<id>.webp at THUMB pixels wide, shrunk
with Lanczos for the spreads. Card ids: m00..m21, wands01..14, cups.., swords.., pentacles...

    python3 make-art.py SRC_DIR OUT_DIR [--only m00,m10]
"""
import argparse
import os
import re

import numpy as np
from PIL import Image
from scipy import ndimage

INK = 118       # ink score at or under this is keyline (0..510 scale), for finding the frame
SOFT = 40       # ramp width above INK
WOBBLE = 5      # rows (or columns) a frame line may still drift across after squaring up
FRAME = 0.85    # a frame candidate must be this close to the strongest line near the edge
BAND = 0.12     # how far in from each edge of the scan to look for the frame
ASPECT = 1.735  # height / width of the printed frame, the median over all 78 scans
MARGIN = 0.008  # crop this fraction of the width outside the drawn frame

MIX = (0.50, 0.25, 0.25)  # red, green, blue
GAMMA = 1.35              # > 1 spreads the light greys apart and deepens the mid-tones
LEVELS = 16
THUMB = 240               # spread-size width

SUITS = {"Wands": "wands", "Cups": "cups", "Swords": "swords", "Pentacles": "pentacles"}


def card_id(filename):
    m = re.match(r"RWS1909_-_(\d\d)_", filename)
    if m:
        return "m" + m.group(1)
    m = re.match(r"RWS1909_-_(\w+?)_(\d\d)\.", filename)
    return SUITS[m.group(1)] + m.group(2)


def ink_score(rgb):
    """Low for black ink: ink is dark in all three channels at once, paint is not."""
    a = rgb.astype(np.int16)
    mx = a.max(axis=2)
    return mx + (mx - a.min(axis=2))


def ink_amount(score):
    return np.clip((INK + SOFT - score) / SOFT, 0.0, 1.0)


def skew_angle(ink):
    small = ink[::2, ::2]
    best, best_score = 0.0, -1.0
    for tenth in range(-20, 21):
        a = tenth / 10.0
        r = ndimage.rotate(small, a, reshape=False, order=1)
        score = (r.sum(axis=1) ** 2).sum() + (r.sum(axis=0) ** 2).sum()
        if score > best_score:
            best, best_score = a, score
    return best


def frame_box(ink):
    """The frame Smith drew round the picture (and its title box).

    The sides first: near each side, the outermost column nearly as strong as the strongest
    there. Top and bottom are harder, because a card's edge in the scanner's shadow also reads
    as a dark line and the picture's own lower edge sits just above the title box; but every
    frame in the deck is the same printed rectangle, so of the strong lines near the top and
    the bottom, take the pair whose distance apart best matches ASPECT times the width.
    """
    h, w = ink.shape
    hard = ink > 0.5
    rows = ndimage.maximum_filter1d(hard, size=WOBBLE, axis=0).mean(axis=1)
    cols = ndimage.maximum_filter1d(hard, size=WOBBLE, axis=1).mean(axis=0)

    def edge(profile, n, rev):
        band = int(n * BAND)
        idx = list(range(n - 1, n - 1 - band, -1)) if rev else list(range(band))
        top = max(profile[i] for i in idx)
        for i in idx:
            if profile[i] >= FRAME * top:
                return i
        return idx[0]

    def lines(profile, idx):
        found, inside = [], False
        for i in idx:
            strong = profile[i] >= 0.5
            if strong and not inside:
                found.append(i)
            inside = strong
        return found or [idx[0]]

    x0, x1 = edge(cols, w, False), edge(cols, w, True)
    band = int(h * BAND * 1.5)
    tops = lines(rows, list(range(band)))
    bottoms = lines(rows, list(range(h - 1, h - 1 - band, -1)))
    want = ASPECT * (x1 - x0)
    y0, y1 = min(((t, b) for t in tops for b in bottoms), key=lambda tb: abs(tb[1] - tb[0] - want))
    return x0, y0, x1, y1


def grey(rgb):
    return rgb[..., 0] * MIX[0] + rgb[..., 1] * MIX[1] + rgb[..., 2] * MIX[2]


def convert(path):
    """The card as an 8-bit greyscale image holding sixteen levels, at scan resolution."""
    im = Image.open(path).convert("RGB")
    rgb = np.asarray(im)
    a = skew_angle(ink_amount(ink_score(rgb)))
    if abs(a) >= 0.1:
        im = im.rotate(a, resample=Image.BICUBIC, fillcolor=(255, 255, 255))
        rgb = np.asarray(im)
    ink = ink_amount(ink_score(rgb))
    x0, y0, x1, y1 = frame_box(ink)
    g = grey(rgb.astype(np.float64))

    # The paper: a strip of the cream margin just outside the frame, all round.
    h, w = ink.shape
    strip = np.zeros_like(ink, dtype=bool)
    o, d = 6, 14
    strip[max(0, y0 - d):max(0, y0 - o), x0:x1] = True
    strip[min(h, y1 + o):min(h, y1 + d), x0:x1] = True
    strip[y0:y1, max(0, x0 - d):max(0, x0 - o)] = True
    strip[y0:y1, min(w, x1 + o):min(w, x1 + d)] = True
    margin = g[strip & (ink < 0.05)]
    inside = g[y0:y1, x0:x1]
    white = max(np.percentile(margin, 90) if margin.size > 200 else 0, np.percentile(inside, 99))
    black = np.median(g[ink > 0.95])

    m = int(round(MARGIN * (x1 - x0)))
    g = g[max(0, y0 - m):min(h, y1 + m + 1), max(0, x0 - m):min(w, x1 + m + 1)]
    t = np.clip((g - black) / (white - black), 0.0, 1.0) ** GAMMA
    q = np.round(t * (LEVELS - 1)) * (255 // (LEVELS - 1))
    return Image.fromarray(q.astype(np.uint8), "L")


def quantise(im):
    a = np.asarray(im, dtype=np.float64) / 255
    return Image.fromarray((np.round(a * (LEVELS - 1)) * (255 // (LEVELS - 1))).astype(np.uint8), "L")


def thumb(card):
    height = int(round(THUMB * card.height / card.width))
    return quantise(card.resize((THUMB, height), Image.LANCZOS))


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("src")
    ap.add_argument("out")
    ap.add_argument("--only", default="")
    a = ap.parse_args()
    only = set(filter(None, a.only.split(",")))
    os.makedirs(a.out, exist_ok=True)
    for f in sorted(os.listdir(a.src)):
        if not f.startswith("RWS1909_-_"):
            continue
        cid = card_id(f)
        if only and cid not in only:
            continue
        card = convert(os.path.join(a.src, f))
        card.save(os.path.join(a.out, f"card_{cid}.webp"), lossless=True, quality=100, method=6)
        thumb(card).save(os.path.join(a.out, f"thumb_{cid}.webp"), lossless=True, quality=100, method=6)
        print(cid, card.size)


if __name__ == "__main__":
    main()
