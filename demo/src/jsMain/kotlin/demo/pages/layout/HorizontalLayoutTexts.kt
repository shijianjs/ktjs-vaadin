package demo.pages.layout

import io.github.shijianjs.ktjs.dom.element.invoke

/** Every string this page shows, in one language. */
internal class HorizontalLayoutTexts(
    val basicHeading: String,
    val basicHint: String,
    val verticalHeading: String,
    val verticalHint: String,
    val individualHeading: String,
    val individualHint: String,
    val horizontalHeading: String,
    val horizontalHint: String,
    val spacingHeading: String,
    val spacingHint: String,
    val paddingHeading: String,
    val paddingHint: String,
    val marginHeading: String,
    val marginHint: String,
    val wrappingHeading: String,
    val wrappingHint: String,
    val wrappingSlotsHeading: String,
    val wrappingSlotsHint: String,
    val expandingHeading: String,
    val expandingHint: String,
    val item: (Int) -> String,
    val start: String,
    val middle: String,
    val end: String,
    val withoutSpacing: String,
    val withSpacing: String,
    val withoutPadding: String,
    val withPadding: String,
    val withoutMargin: String,
    val withMargin: String,
    val withoutWrap: String,
    val withWrap: String,
)
