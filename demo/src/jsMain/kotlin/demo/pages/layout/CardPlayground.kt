package demo.pages.layout

import demo.ui.listen
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachShadowRoot
import io.github.shijianjs.ktjs.vaadin.avatar.createAvatar
import io.github.shijianjs.ktjs.vaadin.badge.createBadge
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.card.CardElement
import io.github.shijianjs.ktjs.vaadin.card.createCard
import io.github.shijianjs.ktjs.vaadin.checkboxgroup.CheckboxGroupEvents
import io.github.shijianjs.ktjs.vaadin.checkboxgroup.createCheckboxGroup
import io.github.shijianjs.ktjs.vaadin.select.SelectEvents
import io.github.shijianjs.ktjs.vaadin.select.createSelect
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
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
internal class CardPlayground : CustomElementAdapter(), CardTranslatable {
    private val preview: HTMLElement = HtmlTagName.div()

    private val playground: CardElement = createCard().apply {
        dataset["test"] = "card-playground"
        addTheme("outlined", "elevated", "cover-media")
        style.setProperty("max-width", "24rem")
    }

    private val mediaNodes: Array<HTMLElement> = arrayOf(CardMediaImage.NAME { show(null) }, CardMediaIcon.NAME(), CardMediaAvatar.NAME { show("L") })

    private val contentNodes: Array<HTMLElement> =
        arrayOf(CardContentText.NAME(), CardContentScroller.NAME(), CardContentImage.NAME())

    private var mediaNode: HTMLElement = mediaNodes[0]

    private var contentNode: HTMLElement = contentNodes[0]

    private val header = CardHeader.NAME()

    private val titleNode = CardSlotText.NAME { show("title", "") }

    private val subtitleNode = CardSlotText.NAME { show("subtitle", "") }

    private val suffixBadge = createBadge().addTheme("success").apply { slot = "header-suffix" }

    private val footerButton = createButton().apply { slot = "footer" }

    init {
        preview.style.apply {
            setProperty("flex", "1 1 20rem")
            setProperty("min-width", "18rem")
        }
        preview.append(playground)

        cardTexts.bind(titleNode, { it.lapland }) { titleNode.textContent = it }
        cardTexts.bind(subtitleNode, { it.exoticNorth }) { subtitleNode.textContent = it }
        cardTexts.bind(suffixBadge, { it.arctic }) { suffixBadge.textContent = it }
        cardTexts.bind(footerButton, { it.bookVacation }) { footerButton.textContent = it }

        attachShadowRoot().append(preview, controls())
        applyTranslation(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        cardTexts.apply(lang)
        mediaNodes.filterIsInstance<CardTranslatable>().forEach { it.applyTranslation(lang) }
        contentNodes.filterIsInstance<CardTranslatable>().forEach { it.applyTranslation(lang) }
        header.applyTranslation(lang)
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
                "header" to header,
                "title" to titleNode,
                "subtitle" to subtitleNode,
                "header-suffix" to suffixBadge,
                "content" to contentNode,
                "footer" to footerButton,
            )
        playground.append(*nodes.values.toTypedArray())

        val slotGroup = createCheckboxGroup().addTheme("vertical")
        cardTexts.bind(slotGroup, { it.slotsLabel }) { slotGroup.label = it }
        val mediaSlotBox = playgroundCheckbox("media", "")
        cardTexts.bind(mediaSlotBox, { it.mediaSlot }) { mediaSlotBox.label = it }
        val contentSlotBox = playgroundCheckbox("content", "")
        cardTexts.bind(contentSlotBox, { it.contentSlot }) { contentSlotBox.label = it }
        slotGroup.append(
            mediaSlotBox,
            playgroundCheckbox("header-prefix", "Header Prefix"),
            playgroundCheckbox("header", "Header"),
            playgroundCheckbox("title", "Title"),
            playgroundCheckbox("subtitle", "Subtitle"),
            playgroundCheckbox("header-suffix", "Header Suffix"),
            contentSlotBox,
            playgroundCheckbox("footer", "Footer"),
        )

        val mediaSelect =
            createSelect {
                items = selectOptions("Image", "Icon", "Avatar")
                value = "0"
            }
        cardTexts.bind(mediaSelect, { it.mediaLabel }) { mediaSelect.label = it }
        val contentSelect =
            createSelect {
                items = selectOptions("Text", "Scroller", "Image & Text")
                value = "0"
            }
        cardTexts.bind(contentSelect, { it.contentLabel }) { contentSelect.label = it }

        val themeGroup = createCheckboxGroup().addTheme("vertical")
        cardTexts.bind(themeGroup, { it.themeLabel }) { themeGroup.label = it }
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
            contentSelect.disabled = "content" !in checked
            mediaSelect.disabled = "media" !in checked
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

    companion object : CardPlaygroundDefaultReference(name = "demo-card-playground")
}
