package demo.pages.layout

import demo.tr
import demo.ui.listen
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachShadowRoot
import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import io.github.shijianjs.ktjs.vaadin.avatar.createAvatar
import io.github.shijianjs.ktjs.vaadin.badge.createBadge
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.card.CardElement
import io.github.shijianjs.ktjs.vaadin.card.createCard
import io.github.shijianjs.ktjs.vaadin.checkboxgroup.CheckboxGroupElement
import io.github.shijianjs.ktjs.vaadin.checkboxgroup.CheckboxGroupEvents
import io.github.shijianjs.ktjs.vaadin.checkboxgroup.createCheckboxGroup
import io.github.shijianjs.ktjs.vaadin.select.SelectElement
import io.github.shijianjs.ktjs.vaadin.select.SelectEvents
import io.github.shijianjs.ktjs.vaadin.select.createSelect
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import web.components.CustomElementReference
import web.cssom.AlignItems
import web.cssom.Display
import web.cssom.FlexDirection
import web.cssom.FlexWrap
import web.cssom.number
import web.cssom.px
import web.cssom.rem
import web.dom.TagName
import web.html.HTMLElement
import web.html.HtmlTagName

/**
 * `<demo-card-playground>`: the interactive card of the card page — the preview on the left, the slot,
 * media, content and theme controls on the right.
 *
 * The playground owns both the card it edits and the controls that edit it: ticking a slot detaches or
 * reattaches nodes, changing the media or content swaps the node in the same slot, and the theme check
 * boxes rewrite the card's theme list. Nothing is rebuilt, so a language switch keeps every node where
 * the reader left it.
 */
internal class CardPlayground : CustomElementAdapter() {
    private val preview: HTMLElement = HtmlTagName.div()

    private val playground: CardElement = createCard().apply {
        dataset["test"] = "card-playground"
        addTheme("outlined", "elevated", "cover-media")
        style.setProperty("max-width", "24rem")
    }

    private val mediaNodes: Array<HTMLElement> = arrayOf(newCardMediaImage(), newCardMediaIcon(), newCardMediaAvatar("L"))

    private val contentNodes: Array<HTMLElement> = arrayOf(newCardContentText(), newCardContentScroller(), newCardContentImage())

    private var mediaNode: HTMLElement = mediaNodes[0]

    private var contentNode: HTMLElement = contentNodes[0]

    init {
        preview.style.apply {
            setProperty("flex", "1 1 20rem")
            setProperty("min-width", "18rem")
        }
        preview.append(playground)
        attachShadowRoot().append(preview, controls())
    }

    override fun connectedCallback() {
        style.apply {
            setProperty("display", "flex")
            setProperty("flex-wrap", "wrap")
            setProperty("align-items", "flex-start")
            setProperty("column-gap", "24px")
            setProperty("row-gap", "24px")
        }
    }

    /** The right-hand column: slot checkboxes, the two selects and the theme checkboxes. */
    private fun controls(): HTMLElement {
        val nodes =
            linkedMapOf(
                "media" to mediaNode,
                "header-prefix" to createAvatar { abbr = "L" }.apply { slot = "header-prefix" },
                "header" to newCardHeader(),
                "title" to newCardSlotText("title", tr("拉普兰", "Lapland")),
                "subtitle" to newCardSlotText("subtitle", tr("异域北方", "The Exotic North")),
                "header-suffix" to createBadge { textContent = tr("北极圈", "Arctic") }.addTheme("success").apply { slot = "header-suffix" },
                "content" to contentNode,
                "footer" to createButton { textContent = tr("预订假期", "Book Vacation") }.apply { slot = "footer" },
            )
        playground.append(*nodes.values.toTypedArray())

        val slotGroup = createCheckboxGroup { label = tr("插槽", "Slots") }.addTheme("vertical")
        slotGroup.append(
            playgroundCheckbox("media", tr("媒体", "Media")),
            playgroundCheckbox("header-prefix", "Header Prefix"),
            playgroundCheckbox("header", "Header"),
            playgroundCheckbox("title", "Title"),
            playgroundCheckbox("subtitle", "Subtitle"),
            playgroundCheckbox("header-suffix", "Header Suffix"),
            playgroundCheckbox("content", tr("内容", "Content")),
            playgroundCheckbox("footer", "Footer"),
        )

        val mediaSelect =
            createSelect {
                label = tr("媒体内容", "Media")
                items = selectOptions("Image", "Icon", "Avatar")
                value = "0"
            }
        val contentSelect =
            createSelect {
                label = tr("正文内容", "Content")
                items = selectOptions("Text", "Scroller", "Image & Text")
                value = "0"
            }

        val themeGroup = createCheckboxGroup { label = tr("样式变体", "Style variants") }.addTheme("vertical")
        themeGroup.append(
            playgroundCheckbox("outlined", "Outlined"),
            playgroundCheckbox("elevated", "Elevated"),
            playgroundCheckbox("horizontal", "Horizontal"),
            playgroundCheckbox("stretch-media", "Stretch Media"),
            playgroundCheckbox("cover-media", "Cover Media"),
        )
        themeGroup.value = arrayOf("outlined", "elevated", "cover-media")

        fun syncSlots() {
            val checked = slotGroup.value.toSet()
            nodes.forEach { (key, node) ->
                if (key in checked) {
                    if (node.parentNode == null) playground.append(node)
                } else if (node.parentNode === playground) {
                    node.remove()
                }
            }
            mediaSelect.disabled = "media" !in checked
            contentSelect.disabled = "content" !in checked
        }

        slotGroup.value = arrayOf("media", "content", "title", "subtitle", "header-suffix", "footer")
        slotGroup.listen(CheckboxGroupEvents.valueChanged) { syncSlots() }
        syncSlots()

        themeGroup.listen(CheckboxGroupEvents.valueChanged) { playground.addTheme(*themeGroup.value) }

        mediaSelect.listen(SelectEvents.valueChanged) {
            val next = mediaNodes[mediaSelect.value.toIntOrNull() ?: 0]
            if (mediaNode.parentNode === playground) mediaNode.remove()
            mediaNode = next
            nodes["media"] = next
            if ("media" in slotGroup.value) playground.append(next)
        }
        contentSelect.listen(SelectEvents.valueChanged) {
            val next = contentNodes[contentSelect.value.toIntOrNull() ?: 0]
            if (contentNode.parentNode === playground) contentNode.remove()
            contentNode = next
            nodes["content"] = next
            if ("content" in slotGroup.value) playground.append(next)
        }

        return HtmlTagName.div().apply {
            style.apply {
                setProperty("display", "flex")
                setProperty("flex-direction", "column")
                setProperty("row-gap", "16px")
                setProperty("min-width", "14rem")
            }
            append(slotGroup, mediaSelect, contentSelect, themeGroup)
        }
    }

    companion object : CustomElementReference<CardPlayground>(CardPlayground::class.js) {
        val NAME: TagName<CardPlayground> = TagName("demo-card-playground")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<demo-card-playground>`. */
internal fun newCardPlayground(): CardPlayground {
    CardPlayground.define()
    return CardPlayground.NAME()
}
