package demo.ui

import io.github.shijianjs.ktjs.dom.css.cssSheet
import io.github.shijianjs.ktjs.demo.style.Palette
import web.cssom.CSSStyleSheet
import web.cssom.px

/**
 * The two typographic tokens every page uses, adopted by [page].
 *
 * `paragraph(...)` and `span(...)` take a class name, so `"muted"` and `"small"` are how a page asks
 * for secondary copy or a smaller line. Those two names are shared by every page and must change
 * together, which is the one case where a shared sheet is right; anything narrower belongs to the
 * page that needs it (`view.addSheet(...)`).
 */
val TEXT_SHEET: CSSStyleSheet = cssSheet {
    ".muted" { color = Palette.muted }
    ".small" { fontSize = 13.px }
}
