package demo.pages.layout


import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import web.components.CustomElementReference
import web.dom.TagName
import web.html.HtmlTagName
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import io.github.shijianjs.ktjs.demo.page.anchor as kitAnchor
import io.github.shijianjs.ktjs.demo.page.bulletList
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.tr
import demo.ui.PageMeta
import demo.ui.configureFrame
import demo.ui.column
import demo.ui.example
import demo.ui.jsArray
import demo.ui.section
import js.objects.unsafeJso
import kotlin.js.JsAny
import io.github.shijianjs.ktjs.vaadin.badge.createBadge
import io.github.shijianjs.ktjs.vaadin.combobox.createComboBox
import io.github.shijianjs.ktjs.vaadin.details.createDetails
import io.github.shijianjs.ktjs.vaadin.details.createDetailsSummary
import io.github.shijianjs.ktjs.vaadin.formlayout.FormLayoutResponsiveStep
import io.github.shijianjs.ktjs.vaadin.formlayout.StringOrDouble
import io.github.shijianjs.ktjs.vaadin.formlayout.createFormLayout
import io.github.shijianjs.ktjs.vaadin.horizontallayout.createHorizontalLayout
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.vaadin.icons.registerVaadinIcons
import io.github.shijianjs.ktjs.vaadin.textfield.createTextField
import web.html.HTMLElement

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
        kitPage = { newDetailsPage() },
        rebuildsOnLanguage = true,
    )

/** A combo box over a fixed list of countries, labelled through `item-label-path`. */
internal fun countryComboBox(label: String) =
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

    init {
        configureFrame(detailsEntry)
        registerVaadinIcons()

        val basic =
            section(
                tr("基本用法", "Basic usage"),
                tr(
                    "summary 是标题文本，子节点是展开区内容；opened 可读写，默认关闭。",
                    "summary is the header text and the children are the expanded content; opened is read/write and " +
                        "starts as false.",
                ),
            )
        val details = createDetails {
            summary = tr("联系方式", "Contact information")
            opened = true
        }
        details.dataset["test"] = "details"
        details.append(column(HtmlTagName.span {
                                  textContent = "Sophia Williams"
                              }, HtmlTagName.span {
                                     textContent = "sophia.williams@company.com"
                                 }, HtmlTagName.span {
                                        textContent = "(501) 555-9128"
                                    }))
        basic.body.append(example(null, details))
        body.append(basic.root)

        val contentSection =
            section(
                tr("内容", "Content"),
                tr(
                    "内容区可以放任意节点，示例里是三组链接列表，全部默认展开。",
                    "The content area takes any nodes; the example shows three groups of links, all opened.",
                ),
            )
        val links =
            listOf(
                tr("分析", "Analytics") to listOf(tr("仪表盘", "Dashboard"), tr("报表", "Reports"), tr("数据源", "Data sources")),
                tr("客户", "Customers") to listOf(tr("账户", "Accounts"), tr("联系人", "Contacts")),
                tr("财务", "Finances") to listOf(tr("发票", "Invoices"), tr("交易", "Transactions"), tr("对账单", "Statements")),
            )
        contentSection.body.append(
            example(
                null,
                column(
                    *links
                        .map { (summary, items) ->
                            linkDetails(summary, items)
                        }.toTypedArray(),
                ),
            ),
        )
        body.append(contentSection.root)

        val summarySection =
            section(
                tr("自定义 summary", "Custom summary"),
                tr(
                    "summary 插槽里放 details-summary，可以组合文本、徽标和图标；这里用错误徽标提示未填字段。",
                    "The summary slot takes a details-summary that can combine text, badges and icons; here an error " +
                        "badge points at the missing fields.",
                ),
            )
        val custom = createDetails { opened = true }
        custom.dataset["test"] = "details-custom-summary"
        val summaryNode = createDetailsSummary()
        summaryNode.slot = "summary"
        val errorBadge = createBadge { number = 2.0 }.addTheme("error")
        errorBadge.append(
            createIcon { icon = "vaadin:exclamation-circle" }.apply { slot = "icon" },
            HtmlTagName.span {
                textContent = tr(" 个错误", " errors")
            },
        )
        summaryNode.append(
            createHorizontalLayout { addTheme("spacing") }.apply {
                style.setProperty("align-items", "center")
                append(HtmlTagName.span {
                           textContent = tr("联系方式", "Contact information")
                       }, errorBadge)
            },
        )
        val addressField = createTextField {
            label = tr("地址", "Address")
            value = "4027 Amber Lake Canyon"
        }
        addressField.setAttribute("colspan", "2")
        val zipField = createTextField {
            label = tr("邮政编码", "ZIP code")
            required = true
        }
        val cityField = createTextField {
            label = tr("城市", "City")
            required = true
        }
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
        form.append(addressField, zipField, cityField, countryComboBox(tr("国家", "Country")))
        custom.append(summaryNode, form)
        summarySection.body.append(example(null, custom))
        body.append(summarySection.root)

        val disabledSection =
            section(
                tr("禁用", "Disabling"),
                tr(
                    "disabled 让区块无法展开，标题也不会响应点击与键盘。",
                    "disabled keeps the block from expanding and the header ignores pointer and keyboard input.",
                ),
            )
        val disabledDetails = createDetails {
            summary = tr("成员（8）", "Members (8)")
            disabled = true
        }
        disabledDetails.append(
            bulletList(
                listOf(
                    "Blake Martin",
                    "Caroline Clark",
                    "Avery Torres",
                    "Khloe Scott",
                    "Camila Fisher",
                    "Gavin Lewis",
                    "Isabella Powell",
                    "Zoe Wilson",
                ),
            ),
        )
        disabledSection.body.append(example(null, disabledDetails))
        body.append(disabledSection.root)

        val themes =
            section(
                tr("主题变体", "Theme variants"),
                tr(
                    "filled、reverse、small 是 details 上的主题变体，可叠加在展开的区块上。",
                    "filled, reverse and small are theme variants of details and can be combined with an opened block.",
                ),
            )
        themes.body.append(
            example("filled", membersDetails(theme = "filled")),
            example("reverse", membersDetails(theme = "reverse")),
            example("small", membersDetails(theme = "small")),
        )
        body.append(themes.root)

    }

    companion object : CustomElementReference<DetailsPage>(DetailsPage::class.js) {
        val NAME: TagName<DetailsPage> = TagName("vaadin-demo-details-page")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<vaadin-demo-details-page>`. */
internal fun newDetailsPage(): DetailsPage {
    DetailsPage.define()
    return DetailsPage.NAME()
}

private fun linkDetails(summary: String, items: List<String>) =
    createDetails {
        this.summary = summary
        opened = true
    }.apply {
        append(column(*items.map { kitAnchor(it, "#/details") }.toTypedArray()))
    }

/**
 * A details block with the eight members inside, one per theme variant.
 *
 * It stays a factory rather than an element because `vaadin-details` reads what the examples append to
 * it as its own content, so the node that goes into the page has to be the details element itself.
 */
private fun membersDetails(theme: String) =
    createDetails {
        summary = tr("成员（8）", "Members (8)")
        opened = true
    }.apply {
        addTheme(theme)
        append(
            bulletList(
                listOf(
                    "Blake Martin",
                    "Caroline Clark",
                    "Avery Torres",
                    "Khloe Scott",
                    "Camila Fisher",
                    "Gavin Lewis",
                    "Isabella Powell",
                    "Zoe Wilson",
                ),
            ),
        )
    }

