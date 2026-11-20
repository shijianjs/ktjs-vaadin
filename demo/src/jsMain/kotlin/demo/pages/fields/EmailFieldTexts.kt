package demo.pages.fields

import io.github.shijianjs.ktjs.dom.element.invoke

/** Every string this page shows, in one language. */
internal class EmailFieldTexts(
    val basicHeading: String,
    val basicHint: String,
    val emailAddress: String,
    val invalidMessage: String,
    val helper: String,
    val validInvalidCaption: String,
    val validityCaption: String,
    val validityLine: (Boolean, String) -> String,
    val featuresHeading: String,
    val featuresHint: String,
    val label: String,
    val helperText: String,
    val placeholder: String,
    val tooltipText: String,
    val validationHeading: String,
    val validationHint: String,
    val onlyExample: String,
    val notChecked: String,
    val requiredMessage: String,
    val patternMessage: String,
    val checkPrefix: String,
    val passed: String,
    val failed: String,
    val statesHeading: String,
    val statesHint: String,
    val readonly: String,
    val disabled: String,
    val stylesHeading: String,
    val stylesHint: String,
)
