package demo.pages.layout

import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.dom.element.inlineStyle
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.lumostyles.LumoCustomProperties
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import io.github.shijianjs.ktjs.demo.page.Blocks.heading
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import demo.CatalogEntry
import demo.Catalog
import demo.Groups
import demo.bi
import demo.ui.Images
import demo.ui.PageFrame
import demo.ui.PageMeta
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.card.CardElement
import io.github.shijianjs.ktjs.vaadin.card.createCard
import io.github.shijianjs.ktjs.vaadin.horizontallayout.createHorizontalLayout
import io.github.shijianjs.ktjs.vaadin.markdown.createMarkdown
import io.github.shijianjs.ktjs.vaadin.scroller.ScrollerScrollDirections
import io.github.shijianjs.ktjs.vaadin.scroller.createScroller
import web.cssom.`var`
import web.cssom.Border
import web.cssom.LineStyle
import web.cssom.px
import web.html.HTMLHeadingElement
import web.html.HTMLImageElement
import web.html.HtmlTagName

internal class ScrollerPage : DemoPage() {
    private val texts = Binder(scrollerTexts)

    private val frame = PageFrame(ENTRY)

    /** The event card: its title and footer button are the card's own properties, not page text. */
    private val eventCard: CardElement = createCard()

    private val addToCalendar = createButton()

    private val illustration: HTMLImageElement = HtmlTagName.img()

    private val phoneHeading: HTMLHeadingElement = heading(3)

    /** The event description, carried by the card's scroller: markdown source, so it is a property. */
    private val eventMarkdown = createMarkdown()

    /** The icons of the horizontal action row; the labels come from the copy, the marks do not. */
    private val actionIcons = listOf("vaadin:clipboard-check", "vaadin:book-dollar", "vaadin:line-chart", "vaadin:invoice")

    private val actions: List<ScrollerAction> =
        scrollerTexts[Langs.EN].actions.mapIndexed { index, label -> ScrollerAction.NAME { show(label, actionIcons[index]) } }

    init {
        frame.mount(this)

        val basic = SectionBlock.NAME { show() }
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        val scroller = createScroller().addTheme("overflow-indicators")
        scroller.dataset["test"] = "scroller"
        scroller.style.setProperty("max-height", "18rem")
        scroller.append(eventMarkdown)
        eventCard.style.setProperty("max-width", "25rem")
        addToCalendar.slot = "footer"
        eventCard.append(scroller, addToCalendar)
        basic.body.append(exampleBox(null, eventCard))
        body.append(basic.root)

        val both = SectionBlock.NAME { show() }
        texts.bind(both.heading) { it.bothHeading }
        texts.bind(both.hint) { it.bothHint }
        val bothScroller = createScroller()
        bothScroller.dataset["test"] = "scroller-both"
        bothScroller.style.setProperty("height", "18rem")
        bothScroller.style.setProperty("width", "100%")
        // The photo is 800px wide, so cap the scroller to make a horizontal scrollbar appear as well.
        bothScroller.style.setProperty("max-width", "40rem")
        illustration.src = Images.reindeer
        illustration.style.setProperty("display", "block")
        bothScroller.append(illustration)
        both.body.append(exampleBox(null, bothScroller))
        body.append(both.root)

        val mobile = SectionBlock.NAME { show() }
        texts.bind(mobile.heading) { it.mobileHeading }
        texts.bind(mobile.hint) { it.mobileHint }
        val phone = HtmlTagName.div().apply {
            inlineStyle.border = Border(1.px, LineStyle.solid, `var`(LumoCustomProperties.CONTRAST_20PCT))
            inlineStyle.borderRadius = `var`(LumoCustomProperties.BORDER_RADIUS_L)
            style.setProperty("max-width", "100%")
            style.setProperty("width", "22.5rem")
        }
        phoneHeading.style.setProperty("padding", "1rem 1rem 0")
        phoneHeading.style.setProperty("font-size", "1.375rem")
        phone.append(phoneHeading)
        val horizontalScroller = createScroller { scrollDirection = ScrollerScrollDirections.horizontal }
        horizontalScroller.append(
            createHorizontalLayout().apply {
                style.setProperty("display", "inline-flex")
                addTheme("padding", "spacing")
                append(*actions.toTypedArray())
            },
        )
        phone.append(horizontalScroller)
        mobile.body.append(exampleBox(null, phone))
        body.append(mobile.root)

        applyTranslation(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = ENTRY.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        val copy = texts.current
        // These four are the components' own properties: a card title, an image alt, a heading and the
        // label inside an action element. None of them is `textContent` of a node this page owns.
        eventCard.cardTitle = copy.cardTitle
        addToCalendar.textContent = copy.addToCalendar
        illustration.alt = copy.illustrationAlt
        phoneHeading.textContent = copy.createNew
        eventMarkdown.content = copy.eventContent
        actions.forEachIndexed { index, action -> action.updateLabel(copy.actions[index]) }
    }

    companion object : ScrollerPageDefaultReference() {
        val ENTRY: CatalogEntry =
            CatalogEntry(
                id = "scroller",
                group = Groups.LAYOUT,
                meta =
                    PageMeta(
                        title = bi("Scroller", "Scroller"),
                        summary =
                            bi(
                                "带样式的滚动容器，scrollDirection 控制滚动轴。",
                                "Styled scrolling container; scrollDirection picks the scroll axis.",
                            ),
                        docsUrl = "${Catalog.DOCS_BASE}/scroller",
                        sourcePath = "pages/layout/ScrollerPage.kt",
                    ),
                kitPage = { ScrollerPage.NAME() },
            )

        private val english: ScrollerTexts = ScrollerTexts(
            basicHeading = "Basic usage",
            basicHint = "A scrollbar appears when the content exceeds the fixed height; overflow-indicators fades the " +
                "top and bottom edges.",
            eventContent =
                """
                **Date & Time**

                Saturday, June 20, 2026
                9:00 AM – 5:00 PM

                **About This Event**

                Join us for a full day of inspiring talks, hands-on workshops and networking. Whether you are a seasoned
                developer or just starting out, there is something for everyone.

                **Schedule**

                - **9:00 AM** – Registration & Breakfast
                - **10:00 AM** – Opening Keynote
                - **11:30 AM** – Breakout Sessions
                - **1:00 PM** – Lunch & Networking
                - **2:30 PM** – Afternoon Workshops
                - **4:30 PM** – Closing Remarks & Raffle

                **What to Bring**

                - Photo ID for check-in
                - Laptop (optional, for workshops)
                - Business cards for networking

                **Parking**

                Free parking in Lot B. Street parking is limited.

                **Contact**

                Questions? Email us at events@techconf.example.
                """,
            cardTitle = "Summer Tech Conference 2025",
            addToCalendar = "Add to calendar",
            bothHeading = "Scrolling in both directions",
            bothHint = "When the content is both wider and taller than the container, both scrollbars appear.",
            illustrationAlt = "A reindeer walking on a snowy lake shore at dusk",
            mobileHeading = "Horizontal scrolling on mobile",
            mobileHint = "With scrollDirection set to horizontal only horizontal scrolling is allowed, useful for a row " +
                "of buttons that slides sideways.",
            createNew = "Create new...",
            actions = listOf("Audit", "Report", "Dashboard", "Invoice"),
        )
        private val chinese: ScrollerTexts = ScrollerTexts(
            basicHeading = "基本用法",
            basicHint = "内容超出固定高度时出现滚动条；overflow-indicators 在顶部与底部显示渐隐指示。",
            eventContent =
                """
                **日期与时间**

                2026 年 6 月 20 日（周六）
                上午 9:00 – 下午 5:00

                **关于这场活动**

                一整天的演讲、动手工作坊与交流环节：无论你是资深开发者还是刚入门，都能找到适合自己的内容。

                **日程**

                - **9:00** – 签到与早餐
                - **10:00** – 开场主题演讲
                - **11:30** – 分会场
                - **13:00** – 午餐与自由交流
                - **14:30** – 下午工作坊
                - **16:30** – 闭幕与抽奖

                **需要携带**

                - 签到时出示证件
                - 笔记本电脑（可选，工作坊用）
                - 名片，方便交流

                **停车**

                B 停车场免费，路边车位有限。

                **联系我们**

                有问题？发邮件到 events@techconf.example。
                """,
            cardTitle = "2025 夏季技术大会",
            addToCalendar = "加入日历",
            bothHeading = "双向滚动",
            bothHint = "内容比容器更宽更高时，两个方向都会出现滚动条。",
            illustrationAlt = "漫步在黄昏雪湖岸边的驯鹿",
            mobileHeading = "移动端横向滚动",
            mobileHint = "scrollDirection 设为 horizontal 后只允许横向滚动，适合把一组按钮放在一行里横向滑动。",
            createNew = "新建…",
            actions = listOf("审计", "报表", "仪表盘", "发票"),
        )
        private val scrollerTexts: Translations<ScrollerTexts> = texts(
            Langs.EN to english,
            Langs.ZH_HANS to chinese,
        )
        /**
         * `<vaadin-demo-scroller-action>`: the tall icon-prefixed button of the horizontal mobile scroller
         * example. The host is the button's wrapper, so the icon's `prefix` slot survives the wrap.
         */

    }
}
