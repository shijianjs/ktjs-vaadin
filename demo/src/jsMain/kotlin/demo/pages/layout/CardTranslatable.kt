package demo.pages.layout

import io.github.shijianjs.ktjs.demo.i18n.Lang

/** A card-page element that holds a text table of its own and can be handed the current language. */
internal interface CardTranslatable {
    fun applyTranslation(lang: Lang)
}
