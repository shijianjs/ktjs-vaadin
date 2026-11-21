package demo.pages.layout

import io.github.shijianjs.ktjs.demo.page.Blocks.col as kitCol
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import web.html.HtmlTagName
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import io.github.shijianjs.ktjs.demo.page.Blocks.anchor as kitAnchor
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import demo.CatalogEntry
import demo.Catalog
import demo.Groups
import demo.bi
import demo.ui.PageFrame
import demo.ui.PageMeta
import io.github.shijianjs.ktjs.vaadin.accordion.AccordionElement
import io.github.shijianjs.ktjs.vaadin.accordion.AccordionPanelElement
import io.github.shijianjs.ktjs.vaadin.accordion.createAccordion
import io.github.shijianjs.ktjs.vaadin.accordion.createAccordionHeading
import io.github.shijianjs.ktjs.vaadin.accordion.createAccordionPanel
import io.github.shijianjs.ktjs.vaadin.button.ButtonElement
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.horizontallayout.createHorizontalLayout
import io.github.shijianjs.ktjs.vaadin.icons.registerVaadinIcons
import io.github.shijianjs.ktjs.vaadin.verticallayout.createVerticalLayout
import io.github.shijianjs.ktjs.vaadin.textfield.TextFieldElement
import io.github.shijianjs.ktjs.vaadin.textfield.createTextField
import web.dom.clickEvent
import web.events.addHandler
import web.html.HTMLElement

internal class AccordionPage : DemoPage() {
    private val texts = Binder(accordionTexts)

    private val frame = PageFrame(ENTRY)

    init {
        frame.mount(this)
        registerVaadinIcons()

        val basic = SectionBlock.NAME { show() }
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        val accordion = infoAccordion()
        accordion.headingLevel = 3.0
        accordion.dataset["test"] = "accordion"
        basic.body.append(exampleBox(null, accordion))
        body.append(basic.root)

        val summarySection = SectionBlock.NAME { show() }
        texts.bind(summarySection.heading) { it.summaryHeading }
        texts.bind(summarySection.hint) { it.summaryHint }
        val wizard = createAccordion { opened = 0.0 }
        wizard.dataset["test"] = "accordion-wizard"
        val customerIcon = CompletionIcon.NAME()
        val billingIcon = CompletionIcon.NAME()
        val paymentIcon = CompletionIcon.NAME()

        fun step(
            icon: HTMLElement,
            copy: (AccordionTexts) -> String,
            action: (AccordionTexts) -> String,
            form: HTMLElement,
            onAction: () -> Unit,
        ): AccordionPanelElement {
            val panel = createAccordionPanel()
            val headingNode = createAccordionHeading()
            headingNode.slot = "summary"
            val titleNode = HtmlTagName.span()
            texts.bind(titleNode, copy) { titleNode.textContent = it }
            headingNode.append(createHorizontalLayout { addTheme("spacing") }.apply { append(titleNode, icon) })
            val button: ButtonElement = createButton { }.addTheme("primary")
            texts.bind(button, action) { button.textContent = it }
            button.clickEvent.addHandler { onAction() }
            panel.append(headingNode, createVerticalLayout { addTheme("spacing") }.apply { append(form, button) })
            return panel
        }

        fun field(label: (AccordionTexts) -> String): TextFieldElement = createTextField().also { field ->
            texts.bind(field, label) { field.label = it }
        }

        val customerForm =
            WizardLayout.NAME { add(field { it.firstName },
                field { it.lastName },
                emailField("").apply { setAttribute("colspan", "2") }.also { email ->
                    texts.bind(email, { it.emailAddress }) { email.label = it }
                },
                field { it.phoneNumber }.apply { setAttribute("colspan", "2") },) }
        val billingForm =
            WizardLayout.NAME { add(field { it.address }.apply { setAttribute("colspan", "2") },
                field { it.zip },
                field { it.city },
                countryComboBox(""),) }
        val paymentForm =
            WizardLayout.NAME { add(field { it.cardNumber }.apply { setAttribute("colspan", "2") },
                field { it.expiry },
                textField("CVV"),) }
        wizard.append(
            step(customerIcon, { it.customerStep }, { it.continueLabel }, customerForm) {
                customerIcon.toggleAttribute("hidden", false)
                wizard.opened = 1.0
            },
            step(billingIcon, { it.billingStep }, { it.continueLabel }, billingForm) {
                billingIcon.toggleAttribute("hidden", false)
                wizard.opened = 2.0
            },
            step(paymentIcon, { it.paymentStep }, { it.finish }, paymentForm) {
                paymentIcon.toggleAttribute("hidden", false)
                wizard.opened = -1.0
            },
        )
        summarySection.body.append(exampleBox(null, wizard))
        body.append(summarySection.root)

        val contentSection = SectionBlock.NAME { show() }
        texts.bind(contentSection.heading) { it.contentHeading }
        texts.bind(contentSection.hint) { it.contentHint }
        contentSection.body.append(exampleBox(null, linksAccordion()))
        body.append(contentSection.root)

        val disabledSection = SectionBlock.NAME { show() }
        texts.bind(disabledSection.heading) { it.disabledHeading }
        texts.bind(disabledSection.hint) { it.disabledHint }
        disabledSection.body.append(exampleBox(null, infoAccordion(disabledIndexes = intArrayOf(1, 2))))
        body.append(disabledSection.root)

        val themes = SectionBlock.NAME { show() }
        texts.bind(themes.heading) { it.themesHeading }
        texts.bind(themes.hint) { it.themesHint }
        themes.body.append(
            exampleBox("filled", infoAccordion(theme = "filled")),
            exampleBox("no-padding", infoAccordion(theme = "no-padding")),
            exampleBox("reverse", infoAccordion(theme = "reverse")),
            exampleBox("small", infoAccordion(theme = "small")),
        )
        body.append(themes.root)

        applyTranslation(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = ENTRY.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
    }

    /** Three information panels with the content of the upstream basic example. */
    private fun infoAccordion(theme: String? = null, disabledIndexes: IntArray = intArrayOf()): AccordionElement =
        createAccordion().apply {
            for (index in 0..2) {
                val panel = createAccordionPanel()
                val summary = when (index) {
                    0 -> { copy: AccordionTexts -> copy.personalInfo }
                    1 -> { copy: AccordionTexts -> copy.billingAddressSummary }
                    else -> { copy: AccordionTexts -> copy.paymentSummary }
                }
                texts.bind(panel, summary) { panel.summary = it }
                if (theme != null) panel.addTheme(theme)
                panel.disabled = index in disabledIndexes
                val lines = kitCol()
                when (index) {
                    0 -> listOf("Sophia Williams", "sophia.williams@company.com", "(501) 555-9128")
                    1 -> listOf("4027 Amber Lake Canyon", "72333-5884 Cozy Nook", "Arkansas")
                    else -> listOf("MasterCard", "1234 5678 9012 3456")
                }.forEach { line -> lines.append(HtmlTagName.span { textContent = line }) }
                if (index == 2) {
                    val expires = HtmlTagName.span()
                    texts.bind(expires) { it.expiresAt }
                    lines.append(expires)
                }
                panel.append(lines)
                append(panel)
            }
        }

    /** Three panels whose content is a list of links, as in the upstream content example. */
    private fun linksAccordion(): AccordionElement =
        createAccordion().apply {
            val entries =
                listOf(
                    ({ copy: AccordionTexts -> copy.analytics }) to
                        listOf<((AccordionTexts) -> String)>(
                            { copy -> copy.dashboard },
                            { copy -> copy.reports },
                            { copy -> copy.dataSources },
                        ),
                    ({ copy: AccordionTexts -> copy.customers }) to
                        listOf<((AccordionTexts) -> String)>(
                            { copy -> copy.accounts },
                            { copy -> copy.contacts },
                        ),
                    ({ copy: AccordionTexts -> copy.finances }) to
                        listOf<((AccordionTexts) -> String)>(
                            { copy -> copy.invoices },
                            { copy -> copy.transactions },
                            { copy -> copy.statements },
                        ),
                )
            for ((summary, links) in entries) {
                val panel = createAccordionPanel()
                texts.bind(panel, summary) { panel.summary = it }
                val column = kitCol()
                links.forEach { link ->
                    val anchor = kitAnchor("", "#/accordion")
                    texts.bind(anchor, link) { anchor.textContent = it }
                    column.append(anchor)
                }
                panel.append(column)
                append(panel)
            }
        }

    companion object : AccordionPageDefaultReference() {
        val ENTRY: CatalogEntry =
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
                        docsUrl = "${Catalog.DOCS_BASE}/accordion",
                        sourcePath = "pages/layout/AccordionPage.kt",
                    ),
                kitPage = { AccordionPage.NAME() },
            )

        private val english: AccordionTexts =
            AccordionTexts(
                basicHeading = "Basic usage",
                basicHint = "Each panel defines its header through summary and the children are the expanded " +
                    "content; only one panel stays open at a time.",
                summaryHeading = "Custom summary",
                summaryHint = "The summary slot takes an accordion-heading with any content; the wizard below " +
                    "marks completed steps with a vaadin:check icon and opens the next one from its buttons.",
                customerStep = "Customer details",
                billingStep = "Billing address",
                paymentStep = "Payment",
                continueLabel = "Continue",
                finish = "Finish",
                firstName = "First name",
                lastName = "Last name",
                emailAddress = "Email address",
                phoneNumber = "Phone number",
                address = "Address",
                zip = "ZIP code",
                city = "City",
                country = "Country",
                cardNumber = "Card number",
                expiry = "Expiry date",
                contentHeading = "Content",
                contentHint = "Panel content can be any nodes, such as a list of links; opened controls the " +
                    "expanded state.",
                disabledHeading = "Disabled panels",
                disabledHint = "A disabled panel cannot be toggled by pointer, keyboard or programmatic changes.",
                themesHeading = "Theme variants",
                themesHint = "filled, no-padding, reverse and small are panel theme variants and are set per panel.",
                personalInfo = "Personal information",
                billingAddressSummary = "Billing address",
                paymentSummary = "Payment",
                expiresAt = "Expires 06/21",
                analytics = "Analytics",
                dashboard = "Dashboard",
                reports = "Reports",
                dataSources = "Data sources",
                customers = "Customers",
                accounts = "Accounts",
                contacts = "Contacts",
                finances = "Finances",
                invoices = "Invoices",
                transactions = "Transactions",
                statements = "Statements",
            )
        private val chinese: AccordionTexts =
            AccordionTexts(
                basicHeading = "基本用法",
                basicHint = "每个 panel 用 summary 定义标题，子节点是展开后的内容；同一时间只展开一个面板（手风琴模式）。",
                summaryHeading = "自定义 summary",
                summaryHint = "summary 插槽里可以放 accordion-heading 与任意节点；下方向导用 vaadin:check 图标标记已完成的步骤，" +
                    "并在点击按钮后展开下一步。",
                customerStep = "客户信息",
                billingStep = "账单地址",
                paymentStep = "付款方式",
                continueLabel = "继续",
                finish = "完成",
                firstName = "名",
                lastName = "姓",
                emailAddress = "邮箱地址",
                phoneNumber = "电话号码",
                address = "地址",
                zip = "邮政编码",
                city = "城市",
                country = "国家",
                cardNumber = "卡号",
                expiry = "有效期",
                contentHeading = "内容",
                contentHint = "面板内容可以是链接列表之类的任意节点，展开状态由 opened 控制。",
                disabledHeading = "禁用面板",
                disabledHint = "给 panel 设 disabled 后标题不可点击，也无法通过键盘或程序展开。",
                themesHeading = "主题变体",
                themesHint = "filled、no-padding、reverse、small 是 panel 上的主题变体，可以逐面板设置。",
                personalInfo = "个人信息",
                billingAddressSummary = "账单地址",
                paymentSummary = "付款方式",
                expiresAt = "到期 06/21",
                analytics = "分析",
                dashboard = "仪表盘",
                reports = "报表",
                dataSources = "数据源",
                customers = "客户",
                accounts = "账户",
                contacts = "联系人",
                finances = "财务",
                invoices = "发票",
                transactions = "交易",
                statements = "对账单",
            )
        private val accordionTexts: Translations<AccordionTexts> = texts(
            Langs.EN to english,
            Langs.ZH_HANS to chinese,
        )
    }
}
