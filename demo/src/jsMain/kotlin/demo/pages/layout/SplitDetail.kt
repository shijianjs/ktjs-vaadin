package demo.pages.layout

import io.github.shijianjs.ktjs.dom.element.inlineStyle
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachShadowRoot
import web.cssom.`var`
import web.cssom.Border
import web.cssom.LineStyle
import web.cssom.px
import web.html.HtmlTagName
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.lumostyles.LumoCustomProperties
import web.html.HTMLElement

/**
 * `<vaadin-demo-split-detail>`: the fake detail form of the split layout examples — ten labelled
 * inputs, grey blocks only, enough shape to read as a form.
 */

internal class SplitDetail : CustomElementAdapter() {
    init {
        attachShadowRoot().append(fields())
    }

    override fun connectedCallback() {
        style.setProperty("display", "block")
        inlineStyle.padding = `var`(LumoCustomProperties.SPACE_S)
    }

    /** Label above input, both grey blocks: enough shape to read as a form. */
    private fun fields(): HTMLElement =
        HtmlTagName.div {
            repeat(10) {
                val field =
                    HtmlTagName.div {
                        inlineStyle.marginBottom = `var`(LumoCustomProperties.SPACE_S)
                    }
                val label =
                    HtmlTagName.div {
                        style.setProperty("width", "40%")
                        style.setProperty("height", "8px")
                        style.setProperty("margin-bottom", "4px")
                        style.setProperty("border-radius", "2px")
                        inlineStyle.background = `var`(LumoCustomProperties.CONTRAST_20PCT)
                    }
                val input =
                    HtmlTagName.div {
                        style.setProperty("height", "24px")
                        inlineStyle.border = Border(1.px, LineStyle.solid, `var`(LumoCustomProperties.CONTRAST_20PCT))
                        inlineStyle.borderRadius = `var`(LumoCustomProperties.BORDER_RADIUS_S)
                    }
                field.append(label, input)
                append(field)
            }
        }

    companion object : SplitDetailDefaultReference()
}
