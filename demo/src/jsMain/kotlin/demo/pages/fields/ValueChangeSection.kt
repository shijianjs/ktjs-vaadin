package demo.pages.fields

import io.github.shijianjs.ktjs.demo.page.SectionBlock
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.dom.element.invoke

/**
 * The shared section plus the one thing a language switch has to do to it: rewrite the two state
 * lines and the mode items from the new copy, keeping the values the reader has typed so far.
 */
internal class ValueChangeSection(val root: SectionBlock, private val render: (Lang) -> Unit) {
    fun show(lang: Lang) {
        render(lang)
    }
}
