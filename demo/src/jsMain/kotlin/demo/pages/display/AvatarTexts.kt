package demo.pages.display

import io.github.shijianjs.ktjs.dom.element.invoke

/** Every string this page shows, in one language. */
internal class AvatarTexts(
    val basicHeading: String,
    val basicHint: String,
    val namingHeading: String,
    val namingHint: String,
    val namingCaption: String,
    val imageHeading: String,
    val imageHint: String,
    val sizesHeading: String,
    val sizesHint: String,
    val coloursHeading: String,
    val coloursHint: String,
    /** The name of the [index]th avatar in the colour row. */
    val user: (Int) -> String,
)
