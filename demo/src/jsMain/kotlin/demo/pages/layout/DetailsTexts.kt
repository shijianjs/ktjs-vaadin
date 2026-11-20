package demo.pages.layout

import io.github.shijianjs.ktjs.dom.element.invoke

/** Every string this page shows, in one language. */
internal class DetailsTexts(
    val basicHeading: String,
    val basicHint: String,
    val contactInformation: String,
    val contentHeading: String,
    val contentHint: String,
    val analytics: String,
    val analyticsLinks: List<String>,
    val customers: String,
    val customerLinks: List<String>,
    val finances: String,
    val financeLinks: List<String>,
    val summaryHeading: String,
    val summaryHint: String,
    val errorsSuffix: String,
    val address: String,
    val zip: String,
    val city: String,
    val country: String,
    val disabledHeading: String,
    val disabledHint: String,
    val members: String,
    val themesHeading: String,
    val themesHint: String,
)
