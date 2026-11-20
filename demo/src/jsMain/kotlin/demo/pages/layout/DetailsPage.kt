package demo.pages.layout

import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.Blocks.col as kitCol
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.page.DemoPage
import web.html.HtmlTagName
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import io.github.shijianjs.ktjs.demo.page.Blocks.anchor as kitAnchor
import io.github.shijianjs.ktjs.demo.page.Blocks.bulletList
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.ui.PageFrame
import demo.ui.PageMeta
import demo.ui.jsArray
import js.objects.unsafeJso
import kotlin.js.JsAny
import io.github.shijianjs.ktjs.vaadin.badge.BadgeElement
import io.github.shijianjs.ktjs.vaadin.badge.createBadge
import io.github.shijianjs.ktjs.vaadin.combobox.ComboBoxElement
import io.github.shijianjs.ktjs.vaadin.combobox.createComboBox
import io.github.shijianjs.ktjs.vaadin.details.DetailsElement
import io.github.shijianjs.ktjs.vaadin.details.createDetails
import io.github.shijianjs.ktjs.vaadin.details.createDetailsSummary
import io.github.shijianjs.ktjs.vaadin.formlayout.FormLayoutResponsiveStep
import io.github.shijianjs.ktjs.vaadin.formlayout.StringOrDouble
import io.github.shijianjs.ktjs.vaadin.formlayout.createFormLayout
import io.github.shijianjs.ktjs.vaadin.horizontallayout.createHorizontalLayout
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.vaadin.icons.registerVaadinIcons
import io.github.shijianjs.ktjs.vaadin.textfield.TextFieldElement
import io.github.shijianjs.ktjs.vaadin.textfield.createTextField
import web.html.HTMLSpanElement

val detailsEntry: CatalogEntry =
    CatalogEntry(
        id = "details",
        group = Groups.LAYOUT,
        meta =
            PageMeta(
                title = bi("Details", "Details"),
                summary =
                    bi(
                        "单个可折叠区块，summary 是标题，子节点是内容。",
                        "A single collapsible block: summary is the heading and the children are the content.",
                    ),
                docsUrl = "$DOCS_BASE/details",
                sourcePath = "pages/layout/DetailsPage.kt",
            ),
        kitPage = { DetailsPage.NAME() },
    )

/** A combo box over a fixed list of countries, labelled through `item-label-path`. */
internal fun countryComboBox(label: String): ComboBoxElement =
    createComboBox {
        this.label = label
        itemLabelPath = "name"
        itemValuePath = "name"
        items =
            jsArray<JsAny>(
                """
                [{"name": "Finland"}, {"name": "Sweden"}, {"name": "Norway"},
                 {"name": "Denmark"}, {"name": "Iceland"}, {"name": "Estonia"}]
                """,
            )
    }

internal class DetailsPage : DemoPage() {
    private val texts = Binder(detailsTexts)

    private val frame = PageFrame(detailsEntry)

    private val basicDetails: DetailsElement = createDetails()

    private val analytics = linkGroup(3)

    private val customers = linkGroup(2)

    private val finances = linkGroup(3)

    /** The custom-summary example: its heading text and the badge suffix. */
    private val customDetails: DetailsElement = createDetails { opened = true }

    private val summaryHeading: HTMLSpanElement = HtmlTagName.span()

    private val errorsSuffix: HTMLSpanElement = HtmlTagName.span()

    private val errorBadge: BadgeElement = createBadge { number = 2.0 }

    private val addressField: TextFieldElement = createTextField()

    private val zipField: TextFieldElement = createTextField()

    private val cityField: TextFieldElement = createTextField()

    private val countryField: ComboBoxElement = countryComboBox("Country")

    private val disabledDetails: DetailsElement = createDetails()

    /** The three themed blocks; their only copy is the summary. */
    private val themedDetails: List<DetailsElement> = listOf(createDetails(), createDetails(), createDetails())

    init {
        frame.mount(this)
        registerVaadinIcons()

        val basic = SectionBlock.NAME { show() }
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        basicDetails.opened = true
        basicDetails.dataset["test"] = "details"
        basicDetails.append(
            kitCol(
                HtmlTagName.span { textContent = "Sophia Williams" },
                HtmlTagName.span { textContent = "sophia.williams@company.com" },
                HtmlTagName.span { textContent = "(501) 555-9128" },
            ),
        )
        basic.body.append(exampleBox(null, basicDetails))
        body.append(basic.root)

        val contentSection = SectionBlock.NAME { show() }
        texts.bind(contentSection.heading) { it.contentHeading }
        texts.bind(contentSection.hint) { it.contentHint }

        contentSection.body.append(
            exampleBox(null, kitCol(analytics.details, customers.details, finances.details)),
        )
        body.append(contentSection.root)

        val summarySection = SectionBlock.NAME { show() }
        texts.bind(summarySection.heading) { it.summaryHeading }
        texts.bind(summarySection.hint) { it.summaryHint }
        customDetails.dataset["test"] = "details-custom-summary"
        val summaryNode = createDetailsSummary()
        summaryNode.slot = "summary"
        errorBadge.addTheme("error")
        errorBadge.append(createIcon { icon = "vaadin:exclamation-circle" }.apply { slot = "icon" }, errorsSuffix)
        summaryNode.append(
            createHorizontalLayout { addTheme("spacing") }.apply {
                style.setProperty("align-items", "center")
                append(summaryHeading, errorBadge)
            },
        )
        addressField.value = "4027 Amber Lake Canyon"
        addressField.setAttribute("colspan", "2")
        zipField.required = true
        cityField.required = true
        val form = createFormLayout()
        form.responsiveSteps =
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
        form.append(addressField, zipField, cityField, countryField)
        customDetails.append(summaryNode, form)
        summarySection.body.append(exampleBox(null, customDetails))
        body.append(summarySection.root)

        val disabledSection = SectionBlock.NAME { show() }
        texts.bind(disabledSection.heading) { it.disabledHeading }
        texts.bind(disabledSection.hint) { it.disabledHint }
        disabledDetails.disabled = true
        disabledDetails.append(bulletList(MEMBERS))
        disabledSection.body.append(exampleBox(null, disabledDetails))
        body.append(disabledSection.root)

        val themes = SectionBlock.NAME { show() }
        texts.bind(themes.heading) { it.themesHeading }
        texts.bind(themes.hint) { it.themesHint }
        val themeNames = listOf("filled", "reverse", "small")
        themedDetails.forEachIndexed { index, details ->
            details.opened = true
            details.addTheme(themeNames[index])
            details.append(bulletList(MEMBERS))
        }
        themes.body.append(exampleBox("filled", themedDetails[0]))
        themes.body.append(exampleBox("reverse", themedDetails[1]))
        themes.body.append(exampleBox("small", themedDetails[2]))
        body.append(themes.root)

        applyTranslation(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = detailsEntry.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        val copy = texts.current
        basicDetails.summary = copy.contactInformation
        summaryHeading.textContent = copy.contactInformation
        errorsSuffix.textContent = copy.errorsSuffix
        addressField.label = copy.address
        zipField.label = copy.zip
        cityField.label = copy.city
        countryField.label = copy.country
        disabledDetails.summary = copy.members
        themedDetails.forEach { it.summary = copy.members }
        bindGroup(analytics, copy.analytics, copy.analyticsLinks)
        bindGroup(customers, copy.customers, copy.customerLinks)
        bindGroup(finances, copy.finances, copy.financeLinks)
    }

    /** Writes the heading and the link labels of one group; the shape of both languages is the same. */
    private fun bindGroup(group: LinkGroup, heading: String, links: List<String>) {
        group.details.summary = heading
        group.anchors.forEachIndexed { index, anchor -> anchor.textContent = links[index] }
    }

    companion object : CustomElementReferenceAdapter<DetailsPage>(DetailsPage::class.js, "vaadin-demo-details-page") {
        private val english: DetailsTexts = DetailsTexts(
            basicHeading = "Basic usage",
            basicHint = "summary is the header text and the children are the expanded content; opened is read/write and " +
                "starts as false.",
            contactInformation = "Contact information",
            contentHeading = "Content",
            contentHint = "The content area takes any nodes; the example shows three groups of links, all opened.",
            analytics = "Analytics",
            analyticsLinks = listOf("Dashboard", "Reports", "Data sources"),
            customers = "Customers",
            customerLinks = listOf("Accounts", "Contacts"),
            finances = "Finances",
            financeLinks = listOf("Invoices", "Transactions", "Statements"),
            summaryHeading = "Custom summary",
            summaryHint = "The summary slot takes a details-summary that can combine text, badges and icons; here an " +
                "error badge points at the missing fields.",
            errorsSuffix = " errors",
            address = "Address",
            zip = "ZIP code",
            city = "City",
            country = "Country",
            disabledHeading = "Disabling",
            disabledHint = "disabled keeps the block from expanding and the header ignores pointer and keyboard input.",
            members = "Members (8)",
            themesHeading = "Theme variants",
            themesHint = "filled, reverse and small are theme variants of details and can be combined with an opened block.",
        )
        private val chinese: DetailsTexts = DetailsTexts(
            basicHeading = "基本用法",
            basicHint = "summary 是标题文本，子节点是展开区内容；opened 可读写，默认关闭。",
            contactInformation = "联系方式",
            contentHeading = "内容",
            contentHint = "内容区可以放任意节点，示例里是三组链接列表，全部默认展开。",
            analytics = "分析",
            analyticsLinks = listOf("仪表盘", "报表", "数据源"),
            customers = "客户",
            customerLinks = listOf("账户", "联系人"),
            finances = "财务",
            financeLinks = listOf("发票", "交易", "对账单"),
            summaryHeading = "自定义 summary",
            summaryHint = "summary 插槽里放 details-summary，可以组合文本、徽标和图标；这里用错误徽标提示未填字段。",
            errorsSuffix = " 个错误",
            address = "地址",
            zip = "邮政编码",
            city = "城市",
            country = "国家",
            disabledHeading = "禁用",
            disabledHint = "disabled 让区块无法展开，标题也不会响应点击与键盘。",
            members = "成员（8）",
            themesHeading = "主题变体",
            themesHint = "filled、reverse、small 是 details 上的主题变体，可叠加在展开的区块上。",
        )
        private val detailsTexts: Translations<DetailsTexts> = texts(
            Langs.EN to english,
            Langs.ZH_HANS to chinese,
        )
        /** The eight fixture members shown inside the themed blocks; names, so they need no translation. */
        private val MEMBERS = listOf(
            "Blake Martin",
            "Caroline Clark",
            "Avery Torres",
            "Khloe Scott",
            "Camila Fisher",
            "Gavin Lewis",
            "Isabella Powell",
            "Zoe Wilson",
        )
        private fun linkGroup(itemCount: Int): LinkGroup {
            val anchors = List(itemCount) { kitAnchor("", "#/details") }
            val details = createDetails { opened = true }
            details.append(kitCol(*anchors.toTypedArray()))
            return LinkGroup(details, anchors)
        }
    }
}
