package demo.pages.data

import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachShadowRoot
import web.html.HtmlTagName
import io.github.shijianjs.ktjs.dom.element.inlineStyle
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import io.github.shijianjs.ktjs.demo.style.Palette
import kotlin.js.JsAny
import kotlin.js.js
import io.github.shijianjs.ktjs.vaadin.avatar.createAvatar
import web.cssom.AlignItems
import web.cssom.Display
import web.cssom.FlexDirection
import web.cssom.px

internal class PersonCell : CustomElementAdapter() {
    private val avatar = createAvatar { }
    private val displayName = HtmlTagName.span().apply { style.setProperty("font-weight", "600") }
    private val email = HtmlTagName.span().apply {
        inlineStyle.apply {
            color = Palette.muted
            fontSize = 12.px
        }
    }

    init {
        val lines = HtmlTagName.div().apply {
            inlineStyle.apply {
                display = Display.flex
                flexDirection = FlexDirection.column
            }
        }
        lines.append(displayName, email)
        val root = HtmlTagName.div().apply {
            inlineStyle.apply {
                display = Display.flex
                alignItems = AlignItems.center
                columnGap = 8.px
            }
        }
        root.append(avatar, lines)
        attachShadowRoot().append(root)
    }

    /** Fills the cell from the grid's item. */
    fun show(item: JsAny?) {
        avatar.name = "${itemText(item, "firstName")} ${itemText(item, "lastName")}"
        avatar.img = itemText(item, "pictureUrl")
        displayName.textContent = itemText(item, "displayName")
        email.textContent = itemText(item, "email")
    }

    companion object : CustomElementReferenceAdapter<PersonCell>(PersonCell::class.js, "vaadin-demo-person-cell")
}
