package demo.pages.theming

import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import web.html.HtmlTagName
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.page.Blocks.row as kitRow
import demo.ui.SMALL
import io.github.shijianjs.ktjs.dom.css.dsl.className
import io.github.shijianjs.ktjs.dom.css.dsl.invoke

internal class DescribedEntryPoint : CustomElementAdapter() {
    private val description = HtmlTagName.p { className = SMALL }
    private var built = false

    /** Registers [field] with [binder] and builds the row once. */
    fun show(name: String, binder: Binder<ThemingTexts>, field: (ThemingTexts) -> String) {
        binder.bind(description, field)
        if (built) return
        built = true
        append(kitRow(HtmlTagName.p { className = SMALL; textContent = name }, description))
    }

    companion object : DescribedEntryPointDefaultReference()
}
