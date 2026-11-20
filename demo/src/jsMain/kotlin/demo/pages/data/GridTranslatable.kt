package demo.pages.data

import io.github.shijianjs.ktjs.demo.i18n.Lang

/** Anything on the grid page that owns text of its own and wants the current language. */
internal interface GridTranslatable {
    fun applyTranslation(lang: Lang)
}
