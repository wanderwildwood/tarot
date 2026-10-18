# The card art

## Where it comes from

All 78 pictures are Pamela Colman Smith's, drawn for the deck A. E. Waite devised and William
Rider & Son published in London in 1909.

They are taken from one set of scans on Wikimedia Commons:
[Rider-Waite tarot deck (Roses & Lilies)](https://commons.wikimedia.org/wiki/Category:Rider-Waite_tarot_deck_(Roses_%26_Lilies)),
the files named `RWS1909 - …`. Each is a scan of a card from an original 1909 "Roses & Lilies"
printing, made by Saskia Jansen from her own deck (via
<https://blog.goo.ne.jp/valet_de_coupe/e/10b39ab41d023e6b44bb7d7b7209c772>), about 820 x 1430
pixels.

**Public domain.** Every file page carries `{{PD-old-70-expired}}`. The cards were published in
1909, before 1929, so they are in the public domain in the United States, and Smith died in 1951,
more than 70 years ago, so they are in the public domain where copyright runs for the author's
life plus 70 years. A faithful scan of a two-dimensional public-domain work adds no copyright of
its own in the US.

[tools/SOURCES.md](tools/SOURCES.md) lists the exact Commons file for every card, with its size
and SHA-1.

## What was done to it

Greyscale from the 1909 scans, for a panel that shows sixteen greys. Approved as shown
(16 greys, the paper grain kept) on 2026-10-17. `tools/make-art.py` does it, in this order and
nothing more:

1. **Square it up.** Each scan is rotated, bicubic, by the angle (in tenths of a degree, up to two)
   at which Smith's black frame lines line up best with the pixels, and only when that is a
   tenth of a degree or more.
2. **Crop to the frame.** Just outside the frame she drew round the picture and its title; the
   cream card margin goes.
3. **Grey.** Red 0.50, green 0.25, blue 0.25. Plain luminance makes the stencilled yellow the same
   grey as the paper, since the two differ almost only in blue; this mix keeps paper, yellow and
   the pale blue two to three of the sixteen levels apart (the Fool's yellow sky against his
   white sun, the Moon's yellow against her blue sky). A curve of 1.35 then spreads the light end
   apart.
4. **Levels.** Per card, the paper goes to white and Smith's keyline to black.
5. **Sixteen levels**, with no dithering: the stencilled areas are flat, so there is nothing to
   band. No despeckling, no smoothing, no sharpening: the paper's grain is the scan's, and it stays.

Each card is stored at that size (about 780 x 1340) as lossless WebP, `card_<id>.webp`, and
shrunk with Lanczos to 240 pixels wide for the spreads, `thumb_<id>.webp`. Together they are
about 23 MB.

To rebuild:

```
python3 tools/fetch-scans.py /tmp/rws
python3 tools/make-art.py /tmp/rws app/src/main/res/drawable-nodpi
```

The output is byte-for-byte the same as what is committed.

## The words

The names, keywords and meanings in `app/src/main/res/values/cards.xml`, and the Guide, are
written for this app. They draw on A. E. Waite's *The Pictorial Key to the Tarot* (London: William
Rider & Son, 1910), which is in the public domain for the same reasons as the pictures. Nothing is
taken from a modern book.
