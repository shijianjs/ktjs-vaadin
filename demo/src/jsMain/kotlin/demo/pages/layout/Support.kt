package demo.pages.layout

import demo.tr
import demo.ui.Images
import demo.ui.div
import demo.ui.element
import demo.ui.span
import demo.ui.withTheme
import js.objects.unsafeJso
import vaadin.accordion.createAccordionPanel
import vaadin.avatar.createAvatar
import vaadin.checkbox.createCheckbox
import vaadin.emailfield.createEmailField
import vaadin.formlayout.FormLayoutResponsiveStep
import vaadin.formlayout.StringOrDouble
import vaadin.formlayout.createFormLayout
import vaadin.icon.createIcon
import vaadin.scroller.createScroller
import vaadin.select.SelectItemData
import vaadin.sidenav.SideNavElement
import vaadin.sidenav.SideNavItemElement
import vaadin.sidenav.createSideNav
import vaadin.sidenav.createSideNavItem
import web.html.HTMLElement
import web.html.HtmlTagName

/** The seven top-level destinations shared by the app layout examples. */
internal fun appSideNav(): SideNavElement =
    createSideNav().apply {
        style.setProperty("margin", "var(--lumo-space-s)")
        append(
            appNavItem("vaadin:dashboard", tr("仪表盘", "Dashboard")),
            appNavItem("vaadin:cart", tr("订单", "Orders")),
            appNavItem("vaadin:user-heart", tr("客户", "Customers")),
            appNavItem("vaadin:package", tr("产品", "Products")),
            appNavItem("vaadin:records", tr("文档", "Documents")),
            appNavItem("vaadin:list", tr("任务", "Tasks")),
            appNavItem("vaadin:chart", tr("分析", "Analytics")),
        )
    }

/**
 * A side navigation item without `path`: the demo is a hash-routed single page, so items render as
 * plain text instead of links that would navigate away.
 */
private fun appNavItem(icon: String, label: String): SideNavItemElement =
    createSideNavItem().apply {
        append(createIcon { this.icon = icon }.apply { slot = "prefix" }, span(label))
    }

internal fun playgroundCheckbox(value: String, label: String) = createCheckbox { this.value = value; this.label = label }

internal fun selectOptions(vararg entries: String): Array<SelectItemData> =
    entries
        .mapIndexed { index, label ->
            unsafeJso<SelectItemData> {
                this.label = label
                value = index.toString()
            }
        }.toTypedArray()

internal fun cardMediaAvatar(abbr: String): HTMLElement = createAvatar { this.abbr = abbr }.withTheme("large").apply { slot = "media" }

internal fun cardContentText(): HTMLElement = div().apply { textContent = laplandShortText() }

internal fun cardContentScroller(): HTMLElement =
    createScroller { withTheme("overflow-indicators") }.apply {
        style.setProperty("max-height", "100px")
        style.setProperty("min-height", "100%")
        append(div().apply { textContent = "${laplandLongText()} ${laplandSamiText()}" })
    }

internal fun cardContentImage(): HTMLElement =
    div().apply {
        append(
            element(HtmlTagName.img).apply {
            src = Images.lapland
                alt = ""
                style.setProperty("max-width", "100%")
                style.setProperty("border-radius", "var(--lumo-border-radius-m)")
            },
            div().apply { textContent = laplandShortText() },
        )
    }

internal fun wizardLayout(vararg fields: HTMLElement): HTMLElement =
    createFormLayout().apply {
        responsiveSteps =
            arrayOf(
                unsafeJso<FormLayoutResponsiveStep> {
                    minWidth = StringOrDouble(0.0)
                    columns = 1.0
                },
                unsafeJso<FormLayoutResponsiveStep> {
                    minWidth = StringOrDouble("20em")
                    columns = 2.0
                },
            )
        append(*fields)
    }

/** A tinted placeholder box used by the layout examples. */
internal fun box(text: String, height: String? = null): HTMLElement =
    div().apply {
        textContent = text
        style.setProperty("padding", "var(--lumo-space-s) var(--lumo-space-m)")
        style.setProperty("border-radius", "var(--lumo-border-radius-m)")
        style.setProperty("background", "var(--lumo-primary-color-10pct)")
        if (height != null) style.setProperty("height", height)
    }

internal fun emailField(label: String): HTMLElement = createEmailField { this.label = label }

/** An accordion panel with a summary and one content child. */
internal fun accordionPanel(summary: String): vaadin.accordion.AccordionPanelElement =
    createAccordionPanel { this.summary = summary }

internal fun laplandShortText(): String =
    tr("拉普兰是芬兰最北端的地区，也是活跃的户外目的地。", "Lapland is the northern-most region of Finland and an active outdoor destination.")

internal fun laplandLongText(): String =
    tr(
        "拉普兰是芬兰最北端的地区，也是活跃的户外目的地：这里一年四季都有神奇的光现象、广袤的北极自然" +
            "与圣诞老人。",
        "Lapland is the northern-most region of Finland and an active outdoor destination that's known for its " +
            "incredible, year-round light phenomena, vast arctic nature, and Santa Claus.",
    )

internal fun laplandSamiText(): String =
    tr(
        "萨米人的家园——萨米或萨普米——也横跨该地区的北部。来探索芬兰拉普兰这片欧洲最后的荒野吧！",
        "The land of the indigenous Sámi people, known as Sámi homeland or Sápmi, also crosses the northern part " +
            "of the region. Explore Finnish Lapland, Europe's last great wilderness!",
    )
