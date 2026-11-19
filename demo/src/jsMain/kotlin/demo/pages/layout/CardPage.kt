package demo.pages.layout


import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import web.components.CustomElementReference
import web.dom.TagName
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import io.github.shijianjs.ktjs.demo.page.paragraph
import io.github.shijianjs.ktjs.demo.page.heading
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.tr
import demo.ui.Images
import demo.ui.PageMeta
import demo.ui.configureFrame
import demo.ui.example
import demo.ui.grid
import demo.ui.listen
import demo.ui.section
import io.github.shijianjs.ktjs.dom.css.cssSheet
import web.cssom.AlignItems
import web.cssom.Flex
import web.cssom.CSSStyleSheet
import web.cssom.Display
import web.cssom.FlexDirection
import web.cssom.FlexWrap
import web.cssom.number
import web.cssom.px
import web.cssom.rem
import io.github.shijianjs.ktjs.vaadin.avatar.createAvatar
import io.github.shijianjs.ktjs.vaadin.badge.createBadge
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.card.createCard
import io.github.shijianjs.ktjs.vaadin.checkboxgroup.CheckboxGroupEvents
import io.github.shijianjs.ktjs.vaadin.checkboxgroup.createCheckboxGroup
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.vaadin.icons.registerVaadinIcons
import io.github.shijianjs.ktjs.vaadin.select.SelectEvents
import io.github.shijianjs.ktjs.vaadin.select.createSelect
import web.html.HTMLElement
import web.html.HtmlTagName
import web.cssom.ClassName
import io.github.shijianjs.ktjs.dom.css.dsl.className
import io.github.shijianjs.ktjs.dom.css.dsl.descendant
import io.github.shijianjs.ktjs.dom.css.dsl.invoke
import io.github.shijianjs.ktjs.dom.css.dsl.tag
import io.github.shijianjs.ktjs.vaadin.VaadinTagName

/** The single-card width the docs examples use; the sheet keys on this same object. */
private val CARD_DEMO: ClassName = ClassName("card-demo")


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
        kitPage = { newCardPage() },
        rebuildsOnLanguage = true,
    )

/**
 * The page's own layout: a single card keeps the width the documentation examples use instead of
 * filling the box. The multi-card examples use the kit's grid block and need no rule of their own.
 */
private val CARD_SHEET: CSSStyleSheet = cssSheet {
    CARD_DEMO { maxWidth = 20.rem }
}
internal class CardPage : DemoPage() {

    init {
        configureFrame(cardEntry)
        addSheet(CARD_SHEET)
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
            newCardSlotText("title", tr("拉普兰", "Lapland")),
            newCardSlotText("subtitle", tr("异域北方", "The Exotic North")),
            HtmlTagName.div().apply { textContent = laplandShortText() },
        )
        val propertyTitle = createCard {
            cardTitle = tr("拉普兰", "Lapland")
            titleHeadingLevel = 3.0
        }
        propertyTitle.append(HtmlTagName.div().apply { textContent = laplandShortText() })
        slotTitles.className = CARD_DEMO
        propertyTitle.className = CARD_DEMO
        titles.body.append(example(null, grid(slotTitles, propertyTitle)))
        body.append(titles.root)

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
        headerCard.append(newCardHeader(), HtmlTagName.div().apply { textContent = laplandShortText() })
        val prefixCard = createCard()
        prefixCard.append(
            createAvatar { name = tr("拉普兰", "Lapland") }.apply { slot = "header-prefix" },
            newCardSlotText("title", tr("拉普兰", "Lapland")),
            newCardSlotText("subtitle", tr("异域北方", "The Exotic North")),
            HtmlTagName.div().apply { textContent = laplandShortText() },
        )
        val suffixCard = createCard()
        suffixCard.append(
            newCardSlotText("title", tr("拉普兰", "Lapland")),
            newCardSlotText("subtitle", tr("异域北方", "The Exotic North")),
            createBadge { textContent = tr("北极圈", "Arctic") }.addTheme("success").apply { slot = "header-suffix" },
            HtmlTagName.div().apply { textContent = laplandShortText() },
        )
        header.body.append(example(null, grid(headerCard, prefixCard, suffixCard)))
        body.append(header.root)

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
        imageCard.append(newCardMediaImage(100), HtmlTagName.div().apply { textContent = laplandShortText() })
        val iconCard = createCard()
        iconCard.append(newCardMediaIcon(), HtmlTagName.div().apply { textContent = laplandShortText() })
        val avatarCard = createCard()
        avatarCard.append(
            createAvatar { name = tr("拉普兰", "Lapland") }.apply { slot = "media" },
            HtmlTagName.div().apply { textContent = laplandShortText() },
        )
        media.body.append(example(null, grid(imageCard, iconCard, avatarCard)))
        body.append(media.root)

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
            newCardSlotText("title", tr("拉普兰", "Lapland")),
            HtmlTagName.div().apply { textContent = laplandShortText() },
            createButton { textContent = tr("预订假期", "Book Vacation") }.apply { slot = "footer" },
            createButton { textContent = tr("了解更多", "Learn More") }.apply { slot = "footer" },
        )
        footerCard.className = CARD_DEMO
        footer.body.append(example(null, footerCard))
        body.append(footer.root)

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
                grid(
                    createCard { cardTitle = "Default" }.apply { append(HtmlTagName.div().apply { textContent = tr("默认卡片样式。", "This is the default card style.") }) },
                    createCard { cardTitle = "Outlined" }.addTheme("outlined").apply {
                        append(HtmlTagName.div().apply { textContent = tr("在卡片四周加一圈实线边框。", "Adds a solid outline around the card.") })
                    },
                    createCard { cardTitle = "Elevated" }.addTheme("elevated").apply {
                        append(HtmlTagName.div().apply { textContent = tr("适合有底色背景的阴影样式。", "This variant works better on a shaded background.") })
                    },
                ),
            ),
        )
        body.append(variants.root)

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
        combinedCard.addTheme("outlined", "elevated", "horizontal", "cover-media")
        combinedCard.style.setProperty("max-width", "34rem")
        combinedCard.append(
            newCardMediaImage(),
            newCardSlotText("title", tr("拉普兰", "Lapland")),
            newCardSlotText("subtitle", tr("异域北方", "The Exotic North")),
            HtmlTagName.div().apply { textContent = laplandShortText() },
        )
        val stretchCard = createCard()
        stretchCard.addTheme("stretch-media")
        stretchCard.append(
            newCardMediaImage(),
            newCardSlotText("title", tr("拉普兰", "Lapland")),
            newCardSlotText("subtitle", tr("异域北方", "The Exotic North")),
            HtmlTagName.div().apply { textContent = laplandShortText() },
        )
        val horizontalCard = createCard()
        horizontalCard.addTheme("horizontal")
        horizontalCard.append(
            createAvatar { name = tr("拉普兰", "Lapland") }.apply { slot = "media" },
            newCardSlotText("title", tr("拉普兰", "Lapland")),
            newCardSlotText("subtitle", tr("异域北方", "The Exotic North")),
            HtmlTagName.div().apply { textContent = laplandShortText() },
        )
        combined.body.append(example(null, grid(combinedCard, stretchCard, horizontalCard)))
        body.append(combined.root)

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
        playgroundSection.body.append(example(null, newCardPlayground()))
        body.append(playgroundSection.root)

    }

    companion object : CustomElementReference<CardPage>(CardPage::class.js) {
        val NAME: TagName<CardPage> = TagName("vaadin-demo-card-page")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<vaadin-demo-card-page>`. */
internal fun newCardPage(): CardPage {
    CardPage.define()
    return CardPage.NAME()
}
