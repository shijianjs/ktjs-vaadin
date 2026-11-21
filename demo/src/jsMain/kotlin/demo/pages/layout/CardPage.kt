package demo.pages.layout

import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox

import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import io.github.shijianjs.ktjs.demo.page.Blocks.paragraph
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import io.github.shijianjs.ktjs.demo.page.Grid
import demo.CatalogEntry
import demo.Catalog
import demo.Groups
import demo.bi
import demo.ui.PageMeta
import demo.ui.PageFrame
import io.github.shijianjs.ktjs.dom.css.cssSheet
import web.cssom.CSSStyleSheet
import web.cssom.number
import web.cssom.rem
import io.github.shijianjs.ktjs.vaadin.avatar.createAvatar
import io.github.shijianjs.ktjs.vaadin.badge.createBadge
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.card.createCard
import io.github.shijianjs.ktjs.vaadin.icons.registerVaadinIcons
import web.html.HTMLElement
import web.html.HtmlTagName
import web.cssom.ClassName
import io.github.shijianjs.ktjs.dom.css.dsl.className

internal class CardPage : DemoPage() {

    private val texts: Binder<CardTexts> = cardTexts

    private val frame: PageFrame = PageFrame(ENTRY)

    /** Card elements that own a text table of their own and want the current language. */
    private val translatables: MutableList<CardTranslatable> = mutableListOf()

    init {
        frame.mount(this)
        addSheet(CARD_SHEET)
        registerVaadinIcons()

        val content = SectionBlock.NAME { show() }
        texts.bind(
            content.heading,
            { it.contentHeading },
        ) { content.heading.textContent = it }
        texts.bind(content.hint, { it.contentHint }) { content.hint.textContent = it }
        val contentCard = createCard()
        contentCard.append(paragraph(""), paragraph(""))
        val contentParagraphs = contentCard.children
        texts.bind(contentParagraphs.item(0) as HTMLElement, { it.longText }) {
            (contentParagraphs.item(0) as HTMLElement).textContent = it
        }
        texts.bind(contentParagraphs.item(1) as HTMLElement, { it.samiText }) {
            (contentParagraphs.item(1) as HTMLElement).textContent = it
        }
        content.body.append(exampleBox(null, contentCard))
        body.append(content.root)

        val titles = SectionBlock.NAME { show() }
        texts.bind(titles.heading, { it.titlesHeading }) { titles.heading.textContent = it }
        texts.bind(titles.hint, { it.titlesHint }) { titles.hint.textContent = it }
        val slotTitles = createCard()
        slotTitles.append(
            CardSlotText.NAME { show("title", "") }.also { node -> texts.bind(node, { it.lapland }) { node.textContent = it } },
            CardSlotText.NAME { show("subtitle", "") }.also { node -> texts.bind(node, { it.exoticNorth }) { node.textContent = it } },
            HtmlTagName.div().also { node -> texts.bind(node, { it.shortText }) { node.textContent = it } },
        )
        val propertyTitle = createCard {
            titleHeadingLevel = 3.0
        }
        texts.bind(propertyTitle, { it.lapland }) { propertyTitle.cardTitle = it }
        propertyTitle.append(HtmlTagName.div().also { node -> texts.bind(node, { it.shortText }) { node.textContent = it } })
        slotTitles.className = CARD_DEMO
        propertyTitle.className = CARD_DEMO
        titles.body.append(exampleBox(null, Grid.NAME { show(260); add(slotTitles, propertyTitle) }))
        body.append(titles.root)

        val header = SectionBlock.NAME { show() }
        texts.bind(header.heading, { it.headerHeading }) { header.heading.textContent = it }
        texts.bind(header.hint, { it.headerHint }) { header.hint.textContent = it }
        val headerCard = CardHeader.NAME().also { translatables += it }
        headerCard.append(HtmlTagName.div().also { node -> texts.bind(node, { it.shortText }) { node.textContent = it } })
        val prefixCard = createCard()
        prefixCard.append(
            createAvatar {}.apply {
                slot = "header-prefix"
                texts.bind(this, { it.lapland }) { name = it }
            },
            CardSlotText.NAME { show("title", "") }.also { node -> texts.bind(node, { it.lapland }) { node.textContent = it } },
            CardSlotText.NAME { show("subtitle", "") }.also { node -> texts.bind(node, { it.exoticNorth }) { node.textContent = it } },
            HtmlTagName.div().also { node -> texts.bind(node, { it.shortText }) { node.textContent = it } },
        )
        val suffixCard = createCard()
        suffixCard.append(
            CardSlotText.NAME { show("title", "") }.also { node -> texts.bind(node, { it.lapland }) { node.textContent = it } },
            CardSlotText.NAME { show("subtitle", "") }.also { node -> texts.bind(node, { it.exoticNorth }) { node.textContent = it } },
            createBadge().addTheme("success").apply {
                slot = "header-suffix"
                texts.bind(this, { it.arctic }) { textContent = it }
            },
            HtmlTagName.div().also { node -> texts.bind(node, { it.shortText }) { node.textContent = it } },
        )
        header.body.append(exampleBox(null, Grid.NAME { show(260); add(headerCard, prefixCard, suffixCard) }))
        body.append(header.root)

        val media = SectionBlock.NAME { show() }
        texts.bind(media.heading, { it.mediaHeading }) { media.heading.textContent = it }
        texts.bind(media.hint, { it.mediaHint }) { media.hint.textContent = it }
        val imageCard = createCard()
        imageCard.append(
            CardMediaImage.NAME { show(100) },
            HtmlTagName.div().also { node -> texts.bind(node, { it.shortText }) { node.textContent = it } },
        )
        val iconCard = createCard()
        iconCard.append(
            CardMediaIcon.NAME(),
            HtmlTagName.div().also { node -> texts.bind(node, { it.shortText }) { node.textContent = it } },
        )
        val avatarCard = createCard()
        avatarCard.append(
            createAvatar {}.apply {
                slot = "media"
                texts.bind(this, { it.lapland }) { name = it }
            },
            HtmlTagName.div().also { node -> texts.bind(node, { it.shortText }) { node.textContent = it } },
        )
        media.body.append(exampleBox(null, Grid.NAME { show(260); add(imageCard, iconCard, avatarCard) }))
        body.append(media.root)

        val footer = SectionBlock.NAME { show() }
        texts.bind(footer.heading, { it.footerHeading }) { footer.heading.textContent = it }
        texts.bind(footer.hint, { it.footerHint }) { footer.hint.textContent = it }
        val footerCard = createCard()
        footerCard.style.setProperty("height", "15rem")
        footerCard.append(
            CardSlotText.NAME { show("title", "") }.also { node -> texts.bind(node, { it.lapland }) { node.textContent = it } },
            HtmlTagName.div().also { node -> texts.bind(node, { it.shortText }) { node.textContent = it } },
            createButton().apply {
                slot = "footer"
                texts.bind(this, { it.bookVacation }) { textContent = it }
            },
            createButton().apply {
                slot = "footer"
                texts.bind(this, { it.learnMore }) { textContent = it }
            },
        )
        footerCard.className = CARD_DEMO
        footer.body.append(exampleBox(null, footerCard))
        body.append(footer.root)

        val variants = SectionBlock.NAME { show() }
        texts.bind(variants.heading, { it.variantsHeading }) { variants.heading.textContent = it }
        texts.bind(variants.hint, { it.variantsHint }) { variants.hint.textContent = it }
        variants.body.append(
            exampleBox(
                null,
                Grid.NAME { show(260); add(createCard { cardTitle = "Default" }.apply {
                        append(HtmlTagName.div().also { node -> texts.bind(node, { it.defaultCardText }) { node.textContent = it } })
                    }, createCard { cardTitle = "Outlined" }.addTheme("outlined").apply {
                        append(HtmlTagName.div().also { node -> texts.bind(node, { it.outlinedCardText }) { node.textContent = it } })
                    }, createCard { cardTitle = "Elevated" }.addTheme("elevated").apply {
                        append(HtmlTagName.div().also { node -> texts.bind(node, { it.elevatedCardText }) { node.textContent = it } })
                    }) },
            ),
        )
        body.append(variants.root)

        val combined = SectionBlock.NAME { show() }
        texts.bind(combined.heading, { it.combinedHeading }) { combined.heading.textContent = it }
        texts.bind(combined.hint, { it.combinedHint }) { combined.hint.textContent = it }
        val combinedCard = createCard()
        combinedCard.addTheme("outlined", "elevated", "horizontal", "cover-media")
        combinedCard.style.setProperty("max-width", "34rem")
        combinedCard.append(
            CardMediaImage.NAME { show(null) },
            CardSlotText.NAME { show("title", "") }.also { node -> texts.bind(node, { it.lapland }) { node.textContent = it } },
            CardSlotText.NAME { show("subtitle", "") }.also { node -> texts.bind(node, { it.exoticNorth }) { node.textContent = it } },
            HtmlTagName.div().also { node -> texts.bind(node, { it.shortText }) { node.textContent = it } },
        )
        val stretchCard = createCard()
        stretchCard.addTheme("stretch-media")
        stretchCard.append(
            CardMediaImage.NAME { show(null) },
            CardSlotText.NAME { show("title", "") }.also { node -> texts.bind(node, { it.lapland }) { node.textContent = it } },
            CardSlotText.NAME { show("subtitle", "") }.also { node -> texts.bind(node, { it.exoticNorth }) { node.textContent = it } },
            HtmlTagName.div().also { node -> texts.bind(node, { it.shortText }) { node.textContent = it } },
        )
        val horizontalCard = createCard()
        horizontalCard.addTheme("horizontal")
        horizontalCard.append(
            createAvatar {}.apply {
                slot = "media"
                texts.bind(this, { it.lapland }) { name = it }
            },
            CardSlotText.NAME { show("title", "") }.also { node -> texts.bind(node, { it.lapland }) { node.textContent = it } },
            CardSlotText.NAME { show("subtitle", "") }.also { node -> texts.bind(node, { it.exoticNorth }) { node.textContent = it } },
            HtmlTagName.div().also { node -> texts.bind(node, { it.shortText }) { node.textContent = it } },
        )
        combined.body.append(exampleBox(null, Grid.NAME { show(260); add(combinedCard, stretchCard, horizontalCard) }))
        body.append(combined.root)

        val playgroundSection = SectionBlock.NAME { show() }
        texts.bind(playgroundSection.heading, { it.playgroundHeading }) {
            playgroundSection.heading.textContent = it
        }
        texts.bind(playgroundSection.hint, { it.playgroundHint }) { playgroundSection.hint.textContent = it }
        val playground = CardPlayground.NAME().also { translatables += it }
        playgroundSection.body.append(exampleBox(null, playground))
        body.append(playgroundSection.root)

        applyTranslation(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = ENTRY.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        translatables.forEach { it.applyTranslation(lang) }
    }

    companion object : CardPageDefaultReference() {
        val ENTRY: CatalogEntry =
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
                        docsUrl = "${Catalog.DOCS_BASE}/card",
                        sourcePath = "pages/layout/CardPage.kt",
                    ),
                kitPage = { CardPage.NAME() },
            )

        /** The single-card width the docs examples use; the sheet keys on this same object. */
        private val CARD_DEMO: ClassName = ClassName("card-demo")
        /**
         * The page's own layout: a single card keeps the width the documentation examples use instead of
         * filling the box. The multi-card examples use the kit's grid block and need no rule of their own.
         */
        private val CARD_SHEET: CSSStyleSheet = cssSheet {
            CARD_DEMO { maxWidth = 20.rem }
        }
    }
}
