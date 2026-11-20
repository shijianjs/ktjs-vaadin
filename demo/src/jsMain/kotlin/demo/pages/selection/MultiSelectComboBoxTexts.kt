package demo.pages.selection

import io.github.shijianjs.ktjs.dom.element.invoke

/** Every string this page shows, in one language. */
internal class MultiSelectComboBoxTexts(
    val basicHeading: String,
    val basicHint: String,
    val countries: String,
    val selectedCount: (Int) -> String,
    val selectedPrefix: (Int) -> String,
    val presetHeading: String,
    val presetHint: String,
    val changeHeading: String,
    val changeHint: String,
    val selectedCountries: String,
    val readonlyHeading: String,
    val readonlyHint: String,
    val expandHeading: String,
    val expandHint: String,
    val onTopHeading: String,
    val onTopHint: String,
    val collapseHeading: String,
    val collapseHint: String,
    val collapseChips: String,
    val classNamesHeading: String,
    val classNamesHint: String,
    val fruit: String,
    val i18nHeading: String,
    val i18nHint: String,
)
