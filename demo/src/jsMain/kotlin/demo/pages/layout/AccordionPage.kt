package demo.pages.layout

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.tr
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.anchor
import demo.ui.column
import demo.ui.example
import demo.ui.page
import demo.ui.section
import demo.ui.setHidden
import demo.ui.span
import demo.ui.withTheme
import vaadin.accordion.createAccordion
import vaadin.accordion.createAccordionHeading
import vaadin.accordion.createAccordionPanel
import vaadin.button.createButton
import vaadin.horizontallayout.createHorizontalLayout
import vaadin.icon.createIcon
import vaadin.icons.registerVaadinIcons
import vaadin.verticallayout.createVerticalLayout
import web.dom.clickEvent
import web.events.addHandler
import web.html.HTMLElement

val accordionEntry: CatalogEntry =
    CatalogEntry(
        id = "accordion",
        group = Groups.LAYOUT,
        meta =
            PageMeta(
                title = bi("Accordion", "Accordion"),
                summary =
                    bi(
                        "可折叠面板组，支持多开与指定展开下标。",
                        "Group of collapsible panels; several can stay open and the open indexes can be set " +
                            "directly.",
                    ),
                docsUrl = "$DOCS_BASE/accordion",
                sourcePath = "pages/layout/AccordionPage.kt",
            ),
        builder = ::buildAccordionPage,
    )

private fun buildAccordionPage(meta: PageMeta): PageView {
    val view = page(meta)
    registerVaadinIcons()

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "每个 panel 用 summary 定义标题，子节点是展开后的内容；同一时间只展开一个面板（手风琴模式）。",
                "Each panel defines its header through summary and the children are the expanded content; only " +
                    "one panel stays open at a time.",
            ),
        )
    val accordion = infoAccordion()
    accordion.headingLevel = 3.0
    accordion.dataset["test"] = "accordion"
    basic.body.append(example(null, accordion))
    view.body.append(basic.root)

    val summarySection =
        section(
            tr("自定义 summary", "Custom summary"),
            tr(
                "summary 插槽里可以放 accordion-heading 与任意节点；下方向导用 vaadin:check 图标标记已完成的" +
                    "步骤，并在点击按钮后展开下一步。",
                "The summary slot takes an accordion-heading with any content; the wizard below marks completed " +
                    "steps with a vaadin:check icon and opens the next one from its buttons.",
            ),
        )
    val wizard = createAccordion { opened = 0.0 }
    wizard.dataset["test"] = "accordion-wizard"
    val customerIcon = completionIcon()
    val billingIcon = completionIcon()
    val paymentIcon = completionIcon()
    wizard.append(
        wizardStep(tr("客户信息", "Customer details"), customerIcon, customerForm(), tr("继续", "Continue")) {
            customerIcon.setHidden(false)
            wizard.opened = 1.0
        },
        wizardStep(tr("账单地址", "Billing address"), billingIcon, billingForm(), tr("继续", "Continue")) {
            billingIcon.setHidden(false)
            wizard.opened = 2.0
        },
        wizardStep(tr("付款方式", "Payment"), paymentIcon, paymentForm(), tr("完成", "Finish")) {
            paymentIcon.setHidden(false)
            wizard.opened = -1.0
        },
    )
    summarySection.body.append(example(null, wizard))
    view.body.append(summarySection.root)

    val contentSection =
        section(
            tr("内容", "Content"),
            tr(
                "面板内容可以是链接列表之类的任意节点，展开状态由 opened 控制。",
                "Panel content can be any nodes, such as a list of links; opened controls the expanded state.",
            ),
        )
    contentSection.body.append(example(null, linksAccordion()))
    view.body.append(contentSection.root)

    val disabledSection =
        section(
            tr("禁用面板", "Disabled panels"),
            tr(
                "给 panel 设 disabled 后标题不可点击，也无法通过键盘或程序展开。",
                "A disabled panel cannot be toggled by pointer, keyboard or programmatic changes.",
            ),
        )
    disabledSection.body.append(example(null, infoAccordion(disabledIndexes = intArrayOf(1, 2))))
    view.body.append(disabledSection.root)

    val themes =
        section(
            tr("主题变体", "Theme variants"),
            tr(
                "filled、no-padding、reverse、small 是 panel 上的主题变体，可以逐面板设置。",
                "filled, no-padding, reverse and small are panel theme variants and are set per panel.",
            ),
        )
    themes.body.append(
        example("filled", infoAccordion(theme = "filled")),
        example("no-padding", infoAccordion(theme = "no-padding")),
        example("reverse", infoAccordion(theme = "reverse")),
        example("small", infoAccordion(theme = "small")),
    )
    view.body.append(themes.root)

    return view
}

/** Three information panels with the content of the upstream basic example. */
private fun infoAccordion(theme: String? = null, disabledIndexes: IntArray = intArrayOf()) =
    createAccordion().apply {
        val entries =
            listOf(
                tr("个人信息", "Personal information") to
                    listOf("Sophia Williams", "sophia.williams@company.com", "(501) 555-9128"),
                tr("账单地址", "Billing address") to
                    listOf("4027 Amber Lake Canyon", "72333-5884 Cozy Nook", "Arkansas"),
                tr("付款方式", "Payment") to
                    listOf("MasterCard", "1234 5678 9012 3456", tr("到期 06/21", "Expires 06/21")),
            )
        entries.forEachIndexed { index, (summary, lines) ->
            val panel = accordionPanel(summary)
            if (theme != null) panel.withTheme(theme)
            panel.disabled = index in disabledIndexes
            panel.append(column(*lines.map { span(it) }.toTypedArray()))
            append(panel)
        }
    }

/** Three panels whose content is a list of links, as in the upstream content example. */
private fun linksAccordion() =
    createAccordion().apply {
        val entries =
            listOf(
                tr("分析", "Analytics") to listOf(tr("仪表盘", "Dashboard"), tr("报表", "Reports"), tr("数据源", "Data sources")),
                tr("客户", "Customers") to listOf(tr("账户", "Accounts"), tr("联系人", "Contacts")),
                tr("财务", "Finances") to listOf(tr("发票", "Invoices"), tr("交易", "Transactions"), tr("对账单", "Statements")),
            )
        entries.forEach { (summary, links) ->
            val panel = accordionPanel(summary)
            panel.append(column(*links.map { anchor(it, "#/accordion") }.toTypedArray()))
            append(panel)
        }
    }

/** A check icon shown in a wizard summary once the step is complete. */
private fun completionIcon(): HTMLElement =
    createIcon { icon = "vaadin:check" }.apply {
        style.setProperty("--vaadin-icon-size", "1rem")
        setHidden(true)
    }

/** One wizard panel: a custom summary plus a form and its call-to-action button. */
private fun wizardStep(
    title: String,
    icon: HTMLElement,
    form: HTMLElement,
    actionLabel: String,
    onAction: () -> Unit,
) = createAccordionPanel().apply {
    val headingNode = createAccordionHeading()
    headingNode.slot = "summary"
    headingNode.append(createHorizontalLayout { withTheme("spacing") }.apply { append(span(title), icon) })
    val action = createButton { textContent = actionLabel }.withTheme("primary")
    action.clickEvent.addHandler { onAction() }
    append(headingNode, createVerticalLayout { withTheme("spacing") }.apply { append(form, action) })
}

private fun customerForm(): HTMLElement =
    wizardLayout(
        textField(tr("名", "First name")),
        textField(tr("姓", "Last name")),
        emailField(tr("邮箱地址", "Email address")).apply { setAttribute("colspan", "2") },
        textField(tr("电话号码", "Phone number")).apply { setAttribute("colspan", "2") },
    )

private fun billingForm(): HTMLElement =
    wizardLayout(
        textField(tr("地址", "Address")).apply { setAttribute("colspan", "2") },
        textField(tr("邮政编码", "ZIP code")),
        textField(tr("城市", "City")),
        countryComboBox(tr("国家", "Country")),
    )

private fun paymentForm(): HTMLElement =
    wizardLayout(
        textField(tr("卡号", "Card number")).apply { setAttribute("colspan", "2") },
        textField(tr("有效期", "Expiry date")),
        textField("CVV"),
    )
