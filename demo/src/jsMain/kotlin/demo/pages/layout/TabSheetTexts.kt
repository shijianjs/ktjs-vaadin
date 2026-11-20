package demo.pages.layout

import io.github.shijianjs.ktjs.dom.element.invoke

/** Every string this page shows, in one language. */
internal class TabSheetTexts(
    val basicHeading: String,
    val basicHint: String,
    val lazyHeading: String,
    val lazyHint: String,
    val prefixHeading: String,
    val prefixHint: String,
    val themeHeading: String,
    val themeHint: String,
    /** The labels of the three tabs, in tab order. */
    val tabLabels: List<String>,
    /** The content of the three panels, in the same order as the tabs. */
    val panelContents: List<String>,
    val closeAll: String,
    val addTab: String,
)
