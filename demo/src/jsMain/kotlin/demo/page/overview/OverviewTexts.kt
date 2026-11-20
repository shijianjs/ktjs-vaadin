package demo.page.overview

import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.css.dsl.invoke

/** Every string this page shows, in one language. */
internal class OverviewTexts(
    val title: String,
    val summary: String,
    val docsLink: String,
    val sourceLink: String,
    val apiLink: String,
    val whatHeading: String,
    val whatBody: String,
    val indexHeading: String,
    val indexHint: String,
    val findLabel: String,
    val findPlaceholder: String,
    val showing: (Int, Int) -> String,
)
