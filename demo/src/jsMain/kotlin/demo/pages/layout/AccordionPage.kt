package demo.pages.layout

import io.github.shijianjs.ktjs.demo.page.col as kitCol
import io.github.shijianjs.ktjs.demo.page.exampleBox
import io.github.shijianjs.ktjs.demo.page.sectionBlock

import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachShadowRoot

import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import web.components.CustomElementReference
import web.dom.TagName
import web.html.HtmlTagName
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import io.github.shijianjs.ktjs.demo.page.anchor as kitAnchor
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.tr
import demo.ui.PageMeta
import demo.ui.configureFrame
import io.github.shijianjs.ktjs.vaadin.accordion.createAccordion
import io.github.shijianjs.ktjs.vaadin.accordion.createAccordionHeading
import io.github.shijianjs.ktjs.vaadin.accordion.createAccordionPanel
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.horizontallayout.createHorizontalLayout
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.vaadin.icons.registerVaadinIcons
import io.github.shijianjs.ktjs.vaadin.verticallayout.createVerticalLayout
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
        kitPage = { newAccordionPage() },
        rebuildsOnLanguage = true,
    )

internal class AccordionPage : DemoPage() {

    init {
        configureFrame(accordionEntry)
        registerVaadinIcons()

        val basic =
            sectionBlock(
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
        basic.body.append(exampleBox(null, accordion))
        body.append(basic.root)

        val summarySection =
            sectionBlock(
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
        val customerIcon = newCompletionIcon()
        val billingIcon = newCompletionIcon()
        val paymentIcon = newCompletionIcon()
        wizard.append(
            wizardStep(
                tr("客户信息", "Customer details"),
                customerIcon,
                newWizardLayout(
                    textField(tr("名", "First name")),
                    textField(tr("姓", "Last name")),
                    emailField(tr("邮箱地址", "Email address")).apply { setAttribute("colspan", "2") },
                    textField(tr("电话号码", "Phone number")).apply { setAttribute("colspan", "2") },
                ),
                tr("继续", "Continue"),
            ) {
                customerIcon.toggleAttribute("hidden", false)
                wizard.opened = 1.0
            },
            wizardStep(
                tr("账单地址", "Billing address"),
                billingIcon,
                newWizardLayout(
                    textField(tr("地址", "Address")).apply { setAttribute("colspan", "2") },
                    textField(tr("邮政编码", "ZIP code")),
                    textField(tr("城市", "City")),
                    countryComboBox(tr("国家", "Country")),
                ),
                tr("继续", "Continue"),
            ) {
                billingIcon.toggleAttribute("hidden", false)
                wizard.opened = 2.0
            },
            wizardStep(
                tr("付款方式", "Payment"),
                paymentIcon,
                newWizardLayout(
                    textField(tr("卡号", "Card number")).apply { setAttribute("colspan", "2") },
                    textField(tr("有效期", "Expiry date")),
                    textField("CVV"),
                ),
                tr("完成", "Finish"),
            ) {
                paymentIcon.toggleAttribute("hidden", false)
                wizard.opened = -1.0
            },
        )
        summarySection.body.append(exampleBox(null, wizard))
        body.append(summarySection.root)

        val contentSection =
            sectionBlock(
                tr("内容", "Content"),
                tr(
                    "面板内容可以是链接列表之类的任意节点，展开状态由 opened 控制。",
                    "Panel content can be any nodes, such as a list of links; opened controls the expanded state.",
                ),
            )
        contentSection.body.append(exampleBox(null, linksAccordion()))
        body.append(contentSection.root)

        val disabledSection =
            sectionBlock(
                tr("禁用面板", "Disabled panels"),
                tr(
                    "给 panel 设 disabled 后标题不可点击，也无法通过键盘或程序展开。",
                    "A disabled panel cannot be toggled by pointer, keyboard or programmatic changes.",
                ),
            )
        disabledSection.body.append(exampleBox(null, infoAccordion(disabledIndexes = intArrayOf(1, 2))))
        body.append(disabledSection.root)

        val themes =
            sectionBlock(
                tr("主题变体", "Theme variants"),
                tr(
                    "filled、no-padding、reverse、small 是 panel 上的主题变体，可以逐面板设置。",
                    "filled, no-padding, reverse and small are panel theme variants and are set per panel.",
                ),
            )
        themes.body.append(
            exampleBox("filled", infoAccordion(theme = "filled")),
            exampleBox("no-padding", infoAccordion(theme = "no-padding")),
            exampleBox("reverse", infoAccordion(theme = "reverse")),
            exampleBox("small", infoAccordion(theme = "small")),
        )
        body.append(themes.root)

    }

    companion object : CustomElementReference<AccordionPage>(AccordionPage::class.js) {
        val NAME: TagName<AccordionPage> = TagName("vaadin-demo-accordion-page")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<vaadin-demo-accordion-page>`. */
internal fun newAccordionPage(): AccordionPage {
    AccordionPage.define()
    return AccordionPage.NAME()
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
            if (theme != null) panel.addTheme(theme)
            panel.disabled = index in disabledIndexes
            panel.append(kitCol(*lines.map { HtmlTagName.span {
                                                 textContent = it
                                             } }.toTypedArray()))
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
            panel.append(kitCol(*links.map { kitAnchor(it, "#/accordion") }.toTypedArray()))
            append(panel)
        }
    }

/**
 * `<vaadin-demo-completion-icon>`: the check that marks a completed wizard step in its summary.
 *
 * The icon is hidden until the step's button opens the next panel, and the step toggles `hidden` on
 * the host, so the host is the icon's wrapper rather than something the page has to reach into.
 */
internal class CompletionIcon : CustomElementAdapter() {
    private val icon = createIcon {
        icon = "vaadin:check"
        style.setProperty("--vaadin-icon-size", "1rem")
    }

    private var startedHidden = false

    init {
        attachShadowRoot().append(icon)
    }

    /** The step's button clears `hidden`; a reconnect must not put it back. */
    override fun connectedCallback() {
        if (startedHidden) return
        startedHidden = true
        toggleAttribute("hidden", true)
    }

    companion object : CustomElementReference<CompletionIcon>(CompletionIcon::class.js) {
        val NAME: TagName<CompletionIcon> = TagName("vaadin-demo-completion-icon")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<vaadin-demo-completion-icon>`. */
internal fun newCompletionIcon(): CompletionIcon {
    CompletionIcon.define()
    return CompletionIcon.NAME()
}

/**
 * One wizard panel: a custom summary plus a form and its call-to-action button.
 *
 * It stays a factory rather than an element because the accordion reads its children as panels, so
 * what goes into the accordion has to be the panel itself, not a wrapper around it.
 */
private fun wizardStep(
    title: String,
    icon: HTMLElement,
    form: HTMLElement,
    actionLabel: String,
    onAction: () -> Unit,
) = createAccordionPanel().apply {
    val headingNode = createAccordionHeading()
    headingNode.slot = "summary"
    headingNode.append(createHorizontalLayout { addTheme("spacing") }.apply { append(HtmlTagName.span {
                                                                                          textContent = title
                                                                                      }, icon) })
    val action = createButton { textContent = actionLabel }.addTheme("primary")
    action.clickEvent.addHandler { onAction() }
    append(headingNode, createVerticalLayout { addTheme("spacing") }.apply { append(form, action) })
}

