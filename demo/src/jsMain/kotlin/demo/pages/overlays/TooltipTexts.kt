package demo.pages.overlays

import io.github.shijianjs.ktjs.dom.element.invoke

/** Every string this page shows, in one language. */
internal class TooltipTexts(
    val slottedHeading: String,
    val slottedHint: String,
    val hoverMe: String,
    val buttonHint: String,
    val fieldLabel: String,
    val fieldPlaceholder: String,
    val fieldHint: String,
    val search: String,
    val exactPhrase: String,
    val linkedHeading: String,
    val linkedHint: String,
    val blockText: String,
    val linkedHintText: String,
    val headingText: String,
    val headingHintText: String,
    val manualHeading: String,
    val manualHint: String,
    val markdownHeading: String,
    val markdownHint: String,
    val markdownButton: String,
    val markdownText: String,
    val slowButton: String,
    val slowText: String,
    val generatedHeading: String,
    val generatedHint: String,
    val chart: String,
    val table: String,
    val map: String,
    val generatedHintFor: (Int) -> String,
    val positionsHeading: String,
    val positionsHint: String,
)
