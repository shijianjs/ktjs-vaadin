package demo.pages.fields

import io.github.shijianjs.ktjs.demo.page.SectionBlock
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.dom.element.invoke

internal class ValueChangeSection(val root: SectionBlock, private val render: (Lang) -> Unit) {
    fun show(lang: Lang) {
        render(lang)
    }
}
