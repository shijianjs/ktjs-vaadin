package demo.pages.overlays

import io.github.shijianjs.ktjs.dom.element.invoke

/** Every string this page shows, in one language. */
internal class OverlayTexts(
    val basicHeading: String,
    val basicHint: String,
    val bareOverlay: String,
    val close: String,
    val openOverlay: String,
    val modelessHeading: String,
    val modelessHint: String,
    val modelessBody: String,
    val openModeless: String,
    val sizedHeading: String,
    val sizedHint: String,
    val scrollableContent: String,
    val line: (Int) -> String,
    val openSized: String,
)
