#!/usr/bin/env python3
"""Download the 78 scans listed in SOURCES.md into DIR and check each one's SHA-1.

    python3 fetch-scans.py DIR
"""
import hashlib
import os
import re
import sys
import time
import urllib.request

HERE = os.path.dirname(os.path.abspath(__file__))
UA = {"User-Agent": "tarot-art-fetch/0.1 (https://github.com/wanderwildwood/tarot)"}


def main():
    out = sys.argv[1]
    os.makedirs(out, exist_ok=True)
    rows = re.findall(r"\| \S+ \| \[([^\]]+)\]\((https://upload[^)]+)\) \| \S+ \| `([0-9a-f]{40})` \|",
                      open(os.path.join(HERE, "SOURCES.md")).read())
    assert len(rows) == 78, len(rows)
    for title, url, sha in rows:
        path = os.path.join(out, title.replace(" ", "_"))
        if not os.path.exists(path):
            data = urllib.request.urlopen(urllib.request.Request(url, headers=UA)).read()
            with open(path, "wb") as f:
                f.write(data)
            time.sleep(0.5)
        got = hashlib.sha1(open(path, "rb").read()).hexdigest()
        if got != sha:
            sys.exit(f"{title}: SHA-1 {got}, expected {sha}")
        print(title)


if __name__ == "__main__":
    main()
