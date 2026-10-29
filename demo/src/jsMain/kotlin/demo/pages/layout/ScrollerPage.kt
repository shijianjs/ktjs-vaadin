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
import demo.ui.page
import demo.ui.section
import demo.ui.withTheme
import vaadin.button.createButton
import vaadin.card.createCard
import vaadin.horizontallayout.createHorizontalLayout
import vaadin.icon.createIcon
import vaadin.markdown.createMarkdown
import vaadin.scroller.ScrollerScrollDirections
import vaadin.scroller.createScroller
import web.html.HTMLElement
import web.html.HtmlTagName

val scrollerEntry: CatalogEntry =
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
                docsUrl = "$DOCS_BASE/scroller",
                sourcePath = "pages/layout/ScrollerPage.kt",
            ),
        builder = ::buildScrollerPage,
    )

private fun buildScrollerPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "内容超出固定高度时出现滚动条；overflow-indicators 在顶部与底部显示渐隐指示。",
                "A scrollbar appears when the content exceeds the fixed height; overflow-indicators fades the " +
                    "top and bottom edges.",
            ),
        )
    val scroller = createScroller().withTheme("overflow-indicators")
    scroller.dataset["test"] = "scroller"
    scroller.style.setProperty("max-height", "18rem")
    val markdown = createMarkdown()
    markdown.content = tr(SCROLLER_EVENT_ZH, SCROLLER_EVENT_EN)
    scroller.append(markdown)
    val card = createCard { cardTitle = tr("2025 夏季技术大会", "Summer Tech Conference 2025") }
    card.style.setProperty("max-width", "25rem")
    card.append(
        scroller,
        createButton { textContent = tr("加入日历", "Add to calendar") }.apply { slot = "footer" },
    )
    basic.body.append(example(null, card))
    view.body.append(basic.root)

    val both =
        section(
            tr("双向滚动", "Scrolling in both directions"),
            tr(
                "内容比容器更宽更高时，两个方向都会出现滚动条。",
                "When the content is both wider and taller than the container, both scrollbars appear.",
            ),
        )
    val bothScroller = createScroller()
    bothScroller.dataset["test"] = "scroller-both"
    bothScroller.style.setProperty("height", "18rem")
    bothScroller.style.setProperty("width", "100%")
    // The photo is 800px wide, so cap the scroller to make a horizontal scrollbar appear as well.
    bothScroller.style.setProperty("max-width", "40rem")
    val illustration = element(HtmlTagName.img)
    illustration.src = Images.reindeer
    illustration.alt = tr("漫步在黄昏雪湖岸边的驯鹿", "A reindeer walking on a snowy lake shore at dusk")
    illustration.style.setProperty("display", "block")
    bothScroller.append(illustration)
    both.body.append(example(null, bothScroller))
    view.body.append(both.root)

    val mobile =
        section(
            tr("移动端横向滚动", "Horizontal scrolling on mobile"),
            tr(
                "scrollDirection 设为 horizontal 后只允许横向滚动，适合把一组按钮放在一行里横向滑动。",
                "With scrollDirection set to horizontal only horizontal scrolling is allowed, useful for a row " +
                    "of buttons that slides sideways.",
            ),
        )
    val phone = div().apply {
        style.setProperty("border", "1px solid var(--lumo-contrast-20pct)")
        style.setProperty("border-radius", "var(--lumo-border-radius-l)")
        style.setProperty("max-width", "100%")
        style.setProperty("width", "22.5rem")
    }
    phone.append(
        heading(3, tr("新建…", "Create new...")).apply {
            style.setProperty("padding", "1rem 1rem 0")
            style.setProperty("font-size", "1.375rem")
        },
    )
    val horizontalScroller = createScroller { scrollDirection = ScrollerScrollDirections.horizontal }
    horizontalScroller.append(
        createHorizontalLayout().apply {
            style.setProperty("display", "inline-flex")
            withTheme("padding", "spacing")
            append(
                scrollerAction(tr("审计", "Audit"), "vaadin:clipboard-check"),
                scrollerAction(tr("报表", "Report"), "vaadin:book-dollar"),
                scrollerAction(tr("仪表盘", "Dashboard"), "vaadin:line-chart"),
                scrollerAction(tr("发票", "Invoice"), "vaadin:invoice"),
            )
        },
    )
    phone.append(horizontalScroller)
    mobile.body.append(example(null, phone))
    view.body.append(mobile.root)

    return view
}

private const val SCROLLER_EVENT_ZH =
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
    """

private const val SCROLLER_EVENT_EN =
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
    """

/** Action button of the horizontal mobile scroller example. */
private fun scrollerAction(label: String, icon: String): HTMLElement {
    val button = createButton { textContent = label }
    button.style.setProperty("height", "6.25rem")
    button.append(createIcon { this.icon = icon }.apply { slot = "prefix" })
    return button
}
