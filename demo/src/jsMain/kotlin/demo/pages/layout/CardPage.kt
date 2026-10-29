package demo.pages.layout

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.tr
import demo.ui.Images
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.div
import demo.ui.element
import demo.ui.example
import demo.ui.heading
import demo.ui.listen
import demo.ui.page
import demo.ui.paragraph
import demo.ui.section
import demo.ui.withClass
import demo.ui.withTheme
import vaadin.avatar.createAvatar
import vaadin.badge.createBadge
import vaadin.button.createButton
import vaadin.card.createCard
import vaadin.checkboxgroup.CheckboxGroupEvents
import vaadin.checkboxgroup.createCheckboxGroup
import vaadin.icon.createIcon
import vaadin.icons.registerVaadinIcons
import vaadin.select.SelectEvents
import vaadin.select.createSelect
import web.html.HTMLElement
import web.html.HtmlTagName

val cardEntry: CatalogEntry =
    CatalogEntry(
        id = "card",
        group = Groups.LAYOUT,
        meta =
            PageMeta(
                title = bi("Card", "Card"),
                summary =
                    bi(
                        "内容卡片：标题层级、elevated / outlined / stretch-media 变体。",
                        "Content card: title heading level plus the elevated / outlined / stretch-media " +
                            "variants.",
                    ),
                docsUrl = "$DOCS_BASE/card",
                sourcePath = "pages/layout/CardPage.kt",
            ),
        builder = ::buildCardPage,
    )

private fun buildCardPage(meta: PageMeta): PageView {
    val view = page(meta)
    registerVaadinIcons()

    val content =
        section(
            tr("内容", "Content"),
            tr(
                "默认插槽里的节点组成卡片正文，可以是任意 DOM 片段。",
                "Nodes in the default slot form the card content; any DOM fragment is allowed.",
            ),
        )
    val contentCard = createCard()
    contentCard.append(paragraph(laplandLongText()), paragraph(laplandSamiText()))
    content.body.append(example(null, contentCard))

    val titles =
        section(
            tr("标题与副标题", "Title and subtitle"),
            tr(
                "title / subtitle 插槽放自定义的标题节点；cardTitle 属性则直接生成标题元素，标题层级由 " +
                    "titleHeadingLevel 决定。",
                "The title / subtitle slots take custom heading nodes, while the cardTitle property generates " +
                    "the heading itself with titleHeadingLevel as its level.",
            ),
        )
    val slotTitles = createCard()
    slotTitles.append(
        cardSlotText("title", tr("拉普兰", "Lapland")),
        cardSlotText("subtitle", tr("异域北方", "The Exotic North")),
        div().apply { textContent = laplandShortText() },
    )
    val propertyTitle = createCard {
        cardTitle = tr("拉普兰", "Lapland")
        titleHeadingLevel = 3.0
    }
    propertyTitle.append(div().apply { textContent = laplandShortText() })
    titles.body.append(example(null, cardGrid(slotTitles.withClass("card-demo"), propertyTitle.withClass("card-demo"))))
    view.body.append(titles.root)

    val header =
        section(
            tr("头部区域", "Header area"),
            tr(
                "header 插槽替换默认的标题行，header-prefix / header-suffix 在标题两侧各放一个节点，常见的是" +
                    "头像与徽标。",
                "The header slot replaces the default title row; header-prefix / header-suffix place one node " +
                    "on each side, typically an avatar and a badge.",
            ),
        )
    val headerCard = createCard()
    headerCard.append(cardHeaderBlock(), div().apply { textContent = laplandShortText() })
    val prefixCard = createCard()
    prefixCard.append(
        createAvatar { name = tr("拉普兰", "Lapland") }.apply { slot = "header-prefix" },
        cardSlotText("title", tr("拉普兰", "Lapland")),
        cardSlotText("subtitle", tr("异域北方", "The Exotic North")),
        div().apply { textContent = laplandShortText() },
    )
    val suffixCard = createCard()
    suffixCard.append(
        cardSlotText("title", tr("拉普兰", "Lapland")),
        cardSlotText("subtitle", tr("异域北方", "The Exotic North")),
        createBadge { textContent = tr("北极圈", "Arctic") }.withTheme("success").apply { slot = "header-suffix" },
        div().apply { textContent = laplandShortText() },
    )
    header.body.append(example(null, cardGrid(headerCard, prefixCard, suffixCard)))
    view.body.append(header.root)

    val media =
        section(
            tr("媒体", "Media"),
            tr(
                "media 插槽放图片、图标或头像；宽高由卡片根据当前变体决定。",
                "The media slot takes an image, an icon or an avatar; the card sizes it according to the active " +
                    "variants.",
            ),
        )
    val imageCard = createCard()
    imageCard.append(cardMediaImage(), div().apply { textContent = laplandShortText() })
    val iconCard = createCard()
    iconCard.append(cardMediaIcon(), div().apply { textContent = laplandShortText() })
    val avatarCard = createCard()
    avatarCard.append(
        createAvatar { name = tr("拉普兰", "Lapland") }.apply { slot = "media" },
        div().apply { textContent = laplandShortText() },
    )
    media.body.append(example(null, cardGrid(imageCard, iconCard, avatarCard)))
    view.body.append(media.root)

    val footer =
        section(
            tr("底部操作", "Footer"),
            tr(
                "footer 插槽里的按钮固定在卡片底部，配合固定高度的卡片使用。",
                "Buttons in the footer slot stay at the bottom of the card, which pays off with a fixed height.",
            ),
        )
    val footerCard = createCard()
    footerCard.style.setProperty("height", "15rem")
    footerCard.append(
        cardSlotText("title", tr("拉普兰", "Lapland")),
        div().apply { textContent = laplandShortText() },
        createButton { textContent = tr("预订假期", "Book Vacation") }.apply { slot = "footer" },
        createButton { textContent = tr("了解更多", "Learn More") }.apply { slot = "footer" },
    )
    footer.body.append(example(null, footerCard.withClass("card-demo")))
    view.body.append(footer.root)

    val variants =
        section(
            tr("样式变体", "Style variants"),
            tr(
                "默认样式之外，outlined 加边框，elevated 加阴影（更适合有底色的背景）。",
                "Beyond the default style, outlined adds a border and elevated adds a shadow that works best on " +
                    "a shaded background.",
            ),
        )
    variants.body.append(
        example(
            null,
            cardGrid(
                createCard { cardTitle = "Default" }.apply { append(div().apply { textContent = tr("默认卡片样式。", "This is the default card style.") }) },
                createCard { cardTitle = "Outlined" }.withTheme("outlined").apply {
                    append(div().apply { textContent = tr("在卡片四周加一圈实线边框。", "Adds a solid outline around the card.") })
                },
                createCard { cardTitle = "Elevated" }.withTheme("elevated").apply {
                    append(div().apply { textContent = tr("适合有底色背景的阴影样式。", "This variant works better on a shaded background.") })
                },
            ),
        ),
    )
    view.body.append(variants.root)

    val combined =
        section(
            tr("组合变体", "Combining variants"),
            tr(
                "horizontal 让媒体与内容并排，stretch-media 拉伸媒体填满剩余空间，cover-media 让媒体铺满卡片" +
                    "上方。多个 theme 可以叠加。",
                "horizontal puts media next to the content, stretch-media stretches the media into the remaining " +
                    "space and cover-media makes it cover the top of the card. Themes can be combined.",
            ),
        )
    val combinedCard = createCard()
    combinedCard.withTheme("outlined", "elevated", "horizontal", "cover-media")
    combinedCard.style.setProperty("max-width", "34rem")
    combinedCard.append(
        cardMediaImage(),
        cardSlotText("title", tr("拉普兰", "Lapland")),
        cardSlotText("subtitle", tr("异域北方", "The Exotic North")),
        div().apply { textContent = laplandShortText() },
    )
    val stretchCard = createCard()
    stretchCard.withTheme("stretch-media")
    stretchCard.append(
        cardMediaImage(),
        cardSlotText("title", tr("拉普兰", "Lapland")),
        cardSlotText("subtitle", tr("异域北方", "The Exotic North")),
        div().apply { textContent = laplandShortText() },
    )
    val horizontalCard = createCard()
    horizontalCard.withTheme("horizontal")
    horizontalCard.append(
        createAvatar { name = tr("拉普兰", "Lapland") }.apply { slot = "media" },
        cardSlotText("title", tr("拉普兰", "Lapland")),
        cardSlotText("subtitle", tr("异域北方", "The Exotic North")),
        div().apply { textContent = laplandShortText() },
    )
    combined.body.append(example(null, cardGrid(combinedCard, stretchCard, horizontalCard)))
    view.body.append(combined.root)

    val playgroundSection =
        section(
            tr("交互式配置", "Interactive configuration"),
            tr(
                "勾选插槽与样式变体、切换媒体与正文内容，预览会就地更新：节点只被摘下来或重新挂回去，" +
                    "不会重建整张卡片。",
                "Ticking slots and style variants, or switching the media and content, updates the preview in " +
                    "place: nodes are only detached and reattached, the card is never rebuilt.",
            ),
        )
    playgroundSection.body.append(example(null, cardPlayground()))
    view.body.append(playgroundSection.root)

    return view
}

/** Builds the interactive card playground: slot checkboxes, media/content selects and theme variants. */
private fun cardPlayground(): HTMLElement {
    val playground = createCard()
    playground.dataset["test"] = "card-playground"
    playground.withTheme("outlined", "elevated", "cover-media")

    val mediaNodes = arrayOf(cardMediaImage(), cardMediaIcon(), cardMediaAvatar("L"))
    val contentNodes = arrayOf(cardContentText(), cardContentScroller(), cardContentImage())
    var mediaNode = mediaNodes[0]
    var contentNode = contentNodes[0]

    val nodes =
        linkedMapOf(
            "media" to mediaNode,
            "header-prefix" to createAvatar { abbr = "L" }.apply { slot = "header-prefix" },
            "header" to cardHeaderBlock(),
            "title" to cardSlotText("title", tr("拉普兰", "Lapland")),
            "subtitle" to cardSlotText("subtitle", tr("异域北方", "The Exotic North")),
            "header-suffix" to createBadge { textContent = tr("北极圈", "Arctic") }.withTheme("success").apply { slot = "header-suffix" },
            "content" to contentNode,
            "footer" to createButton { textContent = tr("预订假期", "Book Vacation") }.apply { slot = "footer" },
        )
    playground.append(*nodes.values.toTypedArray())

    val slotGroup = createCheckboxGroup { label = tr("插槽", "Slots") }.withTheme("vertical")
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

    val themeGroup = createCheckboxGroup { label = tr("样式变体", "Style variants") }.withTheme("vertical")
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

    themeGroup.listen(CheckboxGroupEvents.valueChanged) { playground.withTheme(*themeGroup.value) }

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

    return div("card-playground").apply {
        append(
            div("card-playground__preview", playground),
            div("card-playground__options", slotGroup, mediaSelect, contentSelect, themeGroup),
        )
    }
}

private fun cardGrid(vararg cards: HTMLElement): HTMLElement = div("card-grid", *cards)

/** A `<div slot="…">` holding [text], for the title / subtitle slots of a card. */
private fun cardSlotText(slotName: String, text: String): HTMLElement =
    div().apply {
        slot = slotName
        textContent = text
    }

/** The custom header of the docs header example: an uppercase kicker above the title. */
private fun cardHeaderBlock(): HTMLElement =
    div().apply {
        slot = "header"
        style.setProperty("line-height", "1.25")
        append(
            div().apply {
                style.setProperty("font-size", "0.8125rem")
                style.setProperty("text-transform", "uppercase")
                textContent = tr("异域北方", "The Exotic North")
            },
            heading(2, tr("拉普兰", "Lapland")),
        )
    }

/** The official Lapland photo in the media slot. */
private fun cardMediaImage(): HTMLElement =
    element(HtmlTagName.img).apply {
        slot = "media"
        src = Images.lapland
        alt = ""
        width = 100
    }

private fun cardMediaIcon(): HTMLElement =
    createIcon { icon = "lumo:photo" }.apply {
        slot = "media"
        style.setProperty("background", "var(--lumo-primary-color-10pct)")
        style.setProperty("color", "var(--lumo-primary-color)")
    }
