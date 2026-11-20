package demo.pages.data

import io.github.shijianjs.ktjs.dom.element.invoke

/** Every string this page shows, in one language. */
internal class VirtualListTexts(
    val basicHeading: String,
    val basicHint: String,
    val mathematician: String,
    val computerScientist: String,
    val cryptanalyst: String,
    val aerospaceEngineer: String,
    val contactInfo: String,
    val jumpHeading: String,
    val jumpHint: String,
    val goToFirst: String,
    val goTo300: String,
    /** The label of the [index]th row in the second list. */
    val row: (Int) -> String,
)
