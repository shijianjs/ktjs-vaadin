"""Print a compact summary of the generated Kotlin API of one wrapper package.

Used while writing tests: it lists the element classes, their own members, the exported objects
(attributes, events, factory functions) and the tag names, without the KDoc noise.
"""

import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent / "ktjs-vaadin-web-components/src/jsMain/kotlin/vaadin"


def strip_kdoc(text):
    return re.sub(r"/\*\*.*?\*/", "", text, flags=re.S)


def summarize(path):
    text = strip_kdoc(path.read_text(encoding="utf-8"))
    lines = []
    for raw in text.splitlines():
        line = raw.strip()
        if not line or line.startswith("//") or line.startswith("package ") or line.startswith("import "):
            continue
        if line.startswith("@"):
            continue
        lines.append(line)
    return lines


def main():
    names = sys.argv[1:]
    packages = sorted(p for p in ROOT.iterdir() if p.is_dir())
    for package in packages:
        if names and package.name not in names:
            continue
        print(f"### {package.name}")
        for file in sorted(package.glob("*.kt")):
            lines = summarize(file)
            body = " ".join(lines)
            body = re.sub(r"\s+", " ", body)
            print(f"-- {file.name}")
            print(body[:4000])
        print()


if __name__ == "__main__":
    main()
