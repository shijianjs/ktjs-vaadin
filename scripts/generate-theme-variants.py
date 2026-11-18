#!/usr/bin/env python3
"""Generates the typed theme-variant declarations next to the components they belong to.

The source of truth is Vaadin Flow's Java API, not the web components: the components only know a
space-separated `theme` attribute, and Flow is where each token gets a name, a doc line and a type
(`ThemeVariant`). Upstream clone: `S:/workspace/github/vaadin/flow-components`.

Usage:
    python scripts/generate-theme-variants.py [--upstream <flow-components root>] [--check]

- `--check` only reports what would change; it writes nothing.
- Deprecated constants are dropped (they are aliases kept for binary compatibility upstream).
- Each Flow variant class is written into the Kotlin package of the component it applies to
  (`ButtonVariant` → `io.github.shijianjs.ktjs.vaadin.button`), because that is where a reader looks
  for it. The base `ThemeVariant` stays in `…vaadin.theme`, which is also where the skipped list is
  documented.
- Components this module does not wrap are skipped; the skip list is printed and also written into
  the base KDoc so nobody wonders where `ChartVariant` went.
"""

from __future__ import annotations

import argparse
import re
import sys
from dataclasses import dataclass
from pathlib import Path

DEFAULT_UPSTREAM = Path("S:/workspace/github/vaadin/flow-components")

# Kotlin sources of the published module: variant files are written next to their component, so the
# generator has to know which package declared which element.
SOURCE = Path("ktjs-vaadin-web-components/src/jsMain/kotlin/io/github/shijianjs/ktjs/vaadin")
THEME_PACKAGE = "io.github.shijianjs.ktjs.vaadin.theme"
TEST_FILE = Path(
    "ktjs-vaadin-web-components/src/jsTest/kotlin/io/github/shijianjs/ktjs/vaadin"
    "/themablemixin/ThemeVariantsTest.kt"
)

# Flow classes whose component the wrapper module does not ship (no npm package wrapped here).
NOT_WRAPPED = {
    "ChartVariant": "no `vaadin-charts` wrapper",
    "CrudVariant": "no `vaadin-crud` wrapper",
    "DashboardVariant": "no `vaadin-dashboard` wrapper",
    "GridProVariant": "no `vaadin-grid-pro` wrapper",
    "MapVariant": "no `vaadin-map` wrapper",
    "RichTextEditorVariant": "no `vaadin-rich-text-editor` wrapper",
}

ENTRY = re.compile(r'([A-Z][A-Z0-9_]*)\s*\(\s*"([^"]+)"\s*\)')
APPLIES = re.compile(r"applicable for\s+(?:the\s+)?(?:(?:{@code|`)\s*([^`}]+)\s*[}`])", re.IGNORECASE)
PREFIXES = ("LUMO_", "AURA_")
PACKAGE = re.compile(r"^package\s+(\S+)", re.MULTILINE)
ELEMENT_CLASS = re.compile(r"^abstract external class (\w+Element)\b", re.MULTILINE)

# Flow names its message component `MessageListItem`, while the web component is
# `<vaadin-message>` and this module calls it `MessageElement`.
COMPONENT_ALIASES = {"MessageListItem": "Message"}


@dataclass
class Variant:
    java_name: str
    kotlin_name: str
    value: str
    doc: str


@dataclass
class VariantClass:
    name: str
    applies_to: str
    doc: str
    variants: list[Variant]
    package: str
    directory: Path


def lower_camel(java_name: str) -> str:
    parts = java_name.lower().split("_")
    return parts[0] + "".join(part.capitalize() for part in parts[1:])


def plain_name(java_name: str) -> str:
    for prefix in PREFIXES:
        if java_name.startswith(prefix):
            return java_name[len(prefix):]
    return java_name


def parse(path: Path) -> VariantClass:
    text = path.read_text(encoding="utf-8")
    class_name = path.stem

    javadoc = re.search(r"/\*\*(.*?)\*/", text, re.DOTALL)
    doc = ""
    applies_to = ""
    if javadoc:
        body = javadoc.group(1)
        lines = []
        for raw in body.splitlines():
            line = raw.strip().lstrip("*").strip()
            if not line or line.startswith("@"):
                continue
            lines.append(line)
        doc = " ".join(lines).strip()
        match = APPLIES.search(doc)
        if match:
            applies_to = match.group(1)

    # One window per constant, so a `@Deprecated` only applies to the constant it precedes.
    candidates: list[tuple[str, str]] = []
    previous_end = 0
    for match in ENTRY.finditer(text):
        java_name, value = match.group(1), match.group(2)
        window = text[previous_end : match.start()]
        previous_end = match.end()
        if "@Deprecated" in window:
            continue
        candidates.append((java_name, value))

    # `LUMO_PRIMARY` and `PRIMARY` are the same token; keep the plain name, in declaration order.
    order: list[str] = []
    best: dict[str, str] = {}
    for java_name, value in candidates:
        if value not in best:
            order.append(value)
            best[value] = java_name
        elif plain_name(best[value]) != best[value] and plain_name(java_name) == java_name:
            best[value] = java_name

    variants = []
    for value in order:
        name = plain_name(best[value])
        variants.append(Variant(best[value], lower_camel(name), value, ""))

    return VariantClass(class_name, applies_to, doc, variants, "", Path())


def component_index() -> dict[str, tuple[str, Path]]:
    """`ButtonElement` → (`io.github.shijianjs.ktjs.vaadin.button`, that file's directory)."""
    index: dict[str, tuple[str, Path]] = {}
    for path in sorted(SOURCE.rglob("*.kt")):
        text = path.read_text(encoding="utf-8")
        package = PACKAGE.search(text)
        for element in ELEMENT_CLASS.findall(text):
            if package:
                index[element] = (package.group(1), path.parent)
    return index


def render(cls: VariantClass) -> str:
    interface = cls.name
    object_name = f"{interface}s"
    applies = f"`{cls.applies_to}`" if cls.applies_to else "the component"
    lines = [
        f"package {cls.package}",
        "",
        f"import {THEME_PACKAGE}.ThemeVariant",
        "import js.reflect.unsafeCast",
        "",
        "/**",
        f" * Theme variants of {applies}: mirrors Vaadin Flow's `{cls.name}`.",
        " *",
        " * Deprecated upstream aliases are dropped — they only exist there for binary compatibility, and",
        " * several of them repeat a token that already has a current name.",
        " */",
        f"sealed external interface {interface} : ThemeVariant",
        "",
        f"/** Wraps a variant name from JavaScript. Prefer the constants in [{object_name}]. */",
        f"fun {interface}(value: String): {interface} = unsafeCast(value)",
        "",
        f"/** All variants of [{interface}]. */",
        f"object {object_name} {{",
    ]
    for variant in cls.variants:
        # `val x = X("x")`, not `inline val x get() = X("x")`: the token is a static literal, so there is
        # nothing to compute on each access — the constant is built once when the object initialises.
        lines.append(f'    val {variant.kotlin_name}: {interface} = {interface}("{variant.value}")')
    lines += ["}", ""]
    return "\n".join(lines)


def render_base(included: list[VariantClass], skipped: dict[str, str]) -> str:
    skipped_lines = "\n".join(
        f" * - `{name}` — {reason}" for name, reason in sorted(skipped.items())
    )
    return f"""package {THEME_PACKAGE}

import js.reflect.unsafeCast

/**
 * Base definition of a typed theme variant, mirroring Vaadin Flow's `ThemeVariant`.
 *
 * At runtime a variant *is* its attribute token — a plain string — so the type only exists in Kotlin
 * and in the docs. Every component declares its own subtype (`ButtonVariant`, `BadgeVariant`, …) in
 * its own package, next to the element it applies to, plus a constants object next to it, which is
 * what makes a variant list read as
 * [addTheme(ButtonVariants.primary)][io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme]
 * instead of a bare `"primary"`.
 *
 * ```kotlin
 * button.addTheme(ButtonVariants.primary, ButtonVariants.small)
 * button.hasTheme(ButtonVariants.tertiaryInline)
 * ```
 *
 * Variants of these Flow classes are **not** generated here, because this module does not wrap the
 * corresponding component:
{skipped_lines}
 *
 * Not `sealed`: a subtype has to live in the same module *and package* as a sealed parent, and each
 * component keeps its variants in its own package (`…vaadin.button.ButtonVariant`). Closure comes from
 * the constants objects instead — a component's vocabulary is the one its `XxxVariants` lists, and
 * anything else is a custom token, which the attribute has always allowed.
 */
external interface ThemeVariant

/** Wraps a variant token from JavaScript. Prefer a constants object, e.g.
 * [io.github.shijianjs.ktjs.vaadin.button.ButtonVariants]. */
fun ThemeVariant(value: String): ThemeVariant = unsafeCast(value)

/** The attribute token of this variant, e.g. `primary` or `tertiary-inline`. */
val ThemeVariant.name: String get() = unsafeCast<String>()
"""


def render_tests(included: list[VariantClass]) -> str:
    lines = [
        "package io.github.shijianjs.ktjs.vaadin.themablemixin",
        "",
    ]
    for cls in sorted(included, key=lambda item: item.package):
        lines.append(f"import {cls.package}.{cls.name}s")
    lines += [
        f"import {THEME_PACKAGE}.name",
        "import io.github.shijianjs.ktjs.vaadin.button.createButton",
        "import kotlin.test.Test",
        "import kotlin.test.assertEquals",
        "",
        "/**",
        " * The tokens are generated from Vaadin Flow's Java enums; these assertions are the transcription",
        " * check — a typo in either side shows up here instead of in a browser.",
        " */",
        "class ThemeVariantsTest {",
        "",
        "    @Test",
        "    fun `theMixinAttributeIsTheThemeAttribute`() {",
        '        assertEquals("theme", createButton().themeAttr.name)',
        "    }",
    ]
    for cls in included:
        object_name = f"{cls.name}s"
        method = re.sub(r"Variant$", "", cls.name)
        method = method[:1].lower() + method[1:] + "TokensMatchUpstream"
        lines += [
            "",
            "    @Test",
            f"    fun `{method}`() {{",
        ]
        for variant in cls.variants:
            lines.append(
                f'        assertEquals("{variant.value}", {object_name}.{variant.kotlin_name}.name)'
            )
        lines += ["    }"]
    lines += [
        "",
        "    @Test",
        "    fun `a typed variant can be applied and read back`() {",
        '        assertEquals("primary", ButtonVariants.primary.name)',
        '        assertEquals("tertiary-inline", ButtonVariants.tertiaryInline.name)',
        "    }",
        "}",
        "",
    ]
    return "\n".join(lines)


def main() -> int:
    global SOURCE

    parser = argparse.ArgumentParser()
    parser.add_argument("--upstream", type=Path, default=DEFAULT_UPSTREAM)
    parser.add_argument("--source", type=Path, default=SOURCE)
    parser.add_argument("--test", type=Path, default=TEST_FILE)
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()

    if not args.upstream.is_dir():
        print(f"upstream clone not found: {args.upstream}", file=sys.stderr)
        return 2

    SOURCE = args.source
    index = component_index()

    included: list[VariantClass] = []
    skipped: dict[str, str] = dict(NOT_WRAPPED)
    for path in sorted(args.upstream.rglob("*Variant.java")):
        if "/test/" in path.as_posix():
            continue
        if path.stem in {"ThemeVariant", "HasThemeVariant"}:
            continue
        if path.stem in NOT_WRAPPED:
            continue
        parsed = parse(path)
        if not parsed.variants:
            skipped[parsed.name] = "no constants"
            continue
        component = parsed.name.removesuffix("Variant")
        component = COMPONENT_ALIASES.get(component, component)
        entry = index.get(f"{component}Element")
        if entry is None:
            skipped[parsed.name] = f"no `{component}Element` in this module"
            continue
        parsed.package, parsed.directory = entry
        included.append(parsed)

    files: list[tuple[Path, str]] = [(SOURCE / "theme" / "ThemeVariant.kt", render_base(included, skipped))]
    for cls in included:
        files.append((cls.directory / f"{cls.name}s.kt", render(cls)))
    files.append((args.test, render_tests(included)))

    changed = 0
    for path, content in files:
        if path.exists() and path.read_text(encoding="utf-8") == content:
            continue
        changed += 1
        print(f"{'would write' if args.check else 'write'} {path}")
        if not args.check:
            path.parent.mkdir(parents=True, exist_ok=True)
            path.write_text(content, encoding="utf-8", newline="\n")

    # Variant files used to live in one `theme` package; drop the ones this run did not write so a
    # later component move cannot leave a second copy behind.
    written = {path.resolve() for path, _ in files}
    for stale in sorted((SOURCE / "theme").glob("*Variant*.kt")):
        if stale.resolve() in written:
            continue
        changed += 1
        print(f"{'would remove' if args.check else 'remove'} {stale}")
        if not args.check:
            stale.unlink()

    print(f"{len(included)} variant classes, {changed} file(s) changed, skipped {len(skipped)}")
    for name, reason in sorted(skipped.items()):
        print(f"  skipped {name}: {reason}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
