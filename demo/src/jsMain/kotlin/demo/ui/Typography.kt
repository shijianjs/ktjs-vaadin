package demo.ui

import io.github.shijianjs.ktjs.demo.style.Palette
import io.github.shijianjs.ktjs.dom.css.cssSheet
import web.cssom.ClassName
import web.cssom.CSSStyleSheet
import web.cssom.px

/** Secondary copy. Declared once, referenced by both the sheet and every `paragraph(...)` call. */
val MUTED: ClassName = ClassName("muted")

/** A smaller line, used for captions and metadata. */
val SMALL: ClassName = ClassName("small")

/** Both at once: the caption form most pages use. */
val MUTED_SMALL: ClassName = ClassName("$SMALL $MUTED")

/**
 * The typographic tokens every page uses, adopted by [page].
 *
 * [MUTED] and [SMALL] are the only class names shared across pages, and they must change together —
 * the one case where a shared sheet is right. Anything narrower belongs to the page that needs it
 * (`view.addSheet(...)`). Both the rules here and the call sites point at the same `ClassName`
 * objects, so a rename is one declaration rather than a string search.
 */
val TEXT_SHEET: CSSStyleSheet = cssSheet {
    MUTED { color = Palette.muted }

    SMALL { fontSize = 13.px }
}
