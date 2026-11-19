package demo.pages.selection

import io.github.shijianjs.ktjs.demo.page.col as kitCol
import io.github.shijianjs.ktjs.demo.page.exampleBox
import io.github.shijianjs.ktjs.demo.page.row as kitRow
import io.github.shijianjs.ktjs.demo.page.sectionBlock


import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import web.components.CustomElementReference
import web.dom.TagName
import web.html.HtmlTagName
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.pages.fixtures.Person
import demo.pages.fixtures.filterPeople
import demo.pages.fixtures.people
import demo.pages.fixtures.personItems
import demo.tr
import demo.ui.PageMeta
import demo.ui.configureFrame
import demo.ui.Readout
import demo.ui.detailText
import demo.ui.jsArray
import demo.ui.listen
import kotlin.js.JsAny
import kotlin.js.js
import kotlin.js.unsafeCast
import io.github.shijianjs.ktjs.vaadin.avatar.createAvatar
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.combobox.ComboBoxEvents
import io.github.shijianjs.ktjs.vaadin.combobox.ComboBoxItemModel
import io.github.shijianjs.ktjs.vaadin.combobox.ComboBoxPartialMatchModes
import io.github.shijianjs.ktjs.vaadin.combobox.createComboBox
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.vaadin.tooltip.createTooltip
import web.dom.clickEvent
import web.events.addHandler
import web.html.HTMLElement

val comboBoxEntry: CatalogEntry =
    CatalogEntry(
        id = "combo-box",
        group = Groups.SELECTION,
        meta =
            PageMeta(
                title = bi("Combo Box", "Combo Box"),
                summary =
                    bi(
                        "可筛选的下拉选择：对象选项、itemLabelPath / itemValuePath、自定义值。",
                        "Filterable dropdown: object items, itemLabelPath / itemValuePath and custom " +
                            "values.",
                    ),
                docsUrl = "$DOCS_BASE/combo-box",
                sourcePath = "pages/selection/ComboBoxPage.kt",
            ),
        kitPage = { newComboBoxPage() },
        rebuildsOnLanguage = true,
    )

// The docs examples load these fixtures from a backend; the ids stay strings so the selected value
// of a combo box is a readable string, and two names are kept for the "Grace" filtering example.
private const val OWNERS_JSON =
    """
    [
      {"id": "ada", "name": "Ada Lovelace"},
      {"id": "grace", "name": "Grace Hopper"},
      {"id": "margaret", "name": "Margaret Hamilton"},
      {"id": "katherine", "name": "Katherine Johnson"}
    ]
    """

private val countryNames: List<String> =
    listOf(
        "Finland", "Germany", "France", "Italy", "Spain", "Portugal", "Netherlands", "Belgium",
        "Sweden", "Norway", "Denmark", "Iceland", "Ireland", "Austria", "Switzerland", "Poland",
        "Czechia", "Slovakia", "Hungary", "Romania", "Bulgaria", "Greece", "Croatia", "Slovenia",
        "Estonia", "Latvia", "Lithuania", "Luxembourg", "Malta", "Cyprus",
    )

internal class ComboBoxPage : DemoPage() {

    init {
        configureFrame(comboBoxEntry)
        addSheet(FRUIT_SHEET)

        val basic =
            sectionBlock(
                tr("基本用法", "Basic usage"),
                tr(
                    "给定 itemLabelPath 与 itemValuePath 后，items 可以是任意对象。",
                    "With itemLabelPath and itemValuePath set, items can be any object.",
                ),
            )
        val owners =
            createComboBox {
                label = tr("负责人", "Owner")
                itemLabelPath = "name"
                itemValuePath = "id"
                items = jsArray<JsAny>(OWNERS_JSON)
                value = "grace"
                clearButtonVisible = true
            }
        owners.dataset["test"] = "combo-box-basic"
        val readout = Readout("value = grace")
        owners.listen(ComboBoxEvents.valueChanged) {
            readout.set("value = ", owners.value.ifEmpty { tr("（空）", "(empty)") })
        }
        basic.body.append(exampleBox(null, kitCol(owners, readout.root)))
        body.append(basic.root)

        val features =
            sectionBlock(
                tr("基本特性", "Basic features"),
                tr(
                    "helper-text、placeholder、clear-button-visible、prefix 图标与 tooltip 都是字段级配置。",
                    "helper-text, placeholder, clear-button-visible, a prefix icon and a tooltip are all " +
                        "field-level settings.",
                ),
            )
        val featured =
            createComboBox {
                label = tr("标签", "Label")
                helperText = tr("辅助信息", "Helper text")
                placeholder = tr("占位提示", "Placeholder")
                clearButtonVisible = true
                items = jsArray("""["Value"]""")
            }
        featured.append(
            createTooltip { text = tr("提示文本", "Tooltip text") }.apply { setAttribute("slot", "tooltip") },
            createIcon { icon = "vaadin:search" }.apply { setAttribute("slot", "prefix") },
        )
        features.body.append(exampleBox(null, featured))
        body.append(features.root)

        val customValue =
            sectionBlock(
                tr("自定义值", "Custom value entry"),
                tr(
                    "allowCustomValue 允许输入列表之外的值；custom-value-set 事件携带新值，可以把它追加进 items。",
                    "allowCustomValue accepts values outside the list; the custom-value-set event carries the " +
                        "new value, which can then be appended to items.",
                ),
            )
        val browsers = jsArray<JsAny>("""["Chrome", "Edge", "Firefox", "Safari"]""")
        val browser =
            createComboBox {
                label = tr("浏览器", "Browser")
                helperText = tr("选择或输入一个浏览器", "Select or type a browser")
                allowCustomValue = true
                items = browsers
            }
        val customReadout = Readout(tr("自定义值：（空）", "Custom value: (empty)"))
        browser.listen(ComboBoxEvents.customValueSet) {
            val value = detailText(it)
            customReadout.set(tr("自定义值：", "Custom value: "), value)
            browser.items = appendItem(browser.items ?: browsers, value)
        }
        customValue.body.append(exampleBox(null, kitCol(browser, customReadout.root)))
        body.append(customValue.root)

        val classNames =
            sectionBlock(
                tr("选项类名", "Item class names"),
                tr(
                    "itemClassNameGenerator 返回的类名会加到每个下拉条目上，可以据此改变颜色。",
                    "The class name returned by itemClassNameGenerator is added to every dropdown item, " +
                        "which is enough to colour it.",
                ),
            )
        val fruit =
            createComboBox {
                label = tr("水果", "Fruit")
                items = jsArray(FRUITS_JSON)
                itemClassNameGenerator = { item -> fruitClass(item.unsafeCast<String>()) }
            }
        classNames.body.append(exampleBox(null, fruit))
        body.append(classNames.root)

        val autoOpen =
            sectionBlock(
                tr("自动展开", "Auto open"),
                tr(
                    "auto-open-disabled 阻止聚焦或输入时自动展开下拉，只在点击切换按钮时打开。",
                    "auto-open-disabled keeps the dropdown closed on focus or typing; only the toggle button " +
                        "opens it.",
                ),
            )
        val country =
            createComboBox {
                label = tr("国家", "Country")
                itemLabelPath = "name"
                itemValuePath = "id"
                items = jsArray<JsAny>(COUNTRIES_JSON)
                autoOpenDisabled = true
            }
        autoOpen.body.append(exampleBox(null, country))
        body.append(autoOpen.root)

        val popupWidth =
            sectionBlock(
                tr("浮层宽度", "Popup width"),
                tr(
                    "--vaadin-combo-box-overlay-width 让下拉比输入框更宽，适合展示较长的标签。",
                    "--vaadin-combo-box-overlay-width makes the dropdown wider than the field, which suits " +
                        "long labels.",
                ),
            )
        val employeeItems = personItems(people(5), withDisplayName = true)
        val employee =
            createComboBox {
                label = tr("员工", "Employee")
                itemLabelPath = "displayName"
                itemValuePath = "id"
                items = employeeItems
            }
        employee.style.setProperty("--vaadin-combo-box-overlay-width", "350px")
        popupWidth.body.append(exampleBox(null, employee))
        body.append(popupWidth.root)

        val customFiltering =
            sectionBlock(
                tr("自定义筛选", "Custom filtering"),
                tr(
                    "设置 filteredItems 并监听 filter-changed，可以完全接管匹配逻辑。",
                    "Setting filteredItems and listening to filter-changed takes over the matching logic " +
                        "entirely.",
                ),
            )
        val filterPeople = people(6)
        val filterItems = personItems(filterPeople, withDisplayName = true)
        val filterReadout = Readout(tr("共 6 项", "6 items"))
        val filtered =
            createComboBox {
                label = tr("员工", "Employee")
                itemLabelPath = "displayName"
                items = filterItems
                filteredItems = filterItems
            }
        filtered.listen(ComboBoxEvents.filterChanged) {
            val filter = detailText(it).lowercase()
            val matches =
                filterItems.filterIndexed { index, _ ->
                    filterPeople[index].displayName.lowercase().startsWith(filter)
                }.toTypedArray()
            filtered.filteredItems = matches
            filterReadout.set(tr("匹配 ${matches.size} 项", "${matches.size} items match"))
        }
        customFiltering.body.append(exampleBox(null, kitCol(filtered, filterReadout.root)))
        body.append(customFiltering.root)

        val partialMatch =
            sectionBlock(
                tr("部分匹配模式", "Partial match mode"),
                tr(
                    "partial-match-mode 决定输入内容只匹配开头、匹配任意位置还是必须完全匹配。",
                    "partial-match-mode decides whether the typed text matches the start, any position, or " +
                        "must match exactly.",
                ),
            )
        val partial =
            createComboBox {
                label = tr("国家", "Country")
                itemLabelPath = "name"
                itemValuePath = "id"
                items = jsArray<JsAny>(COUNTRIES_JSON)
                partialMatchMode = ComboBoxPartialMatchModes.firstMatch
            }
        partialMatch.body.append(exampleBox(null, partial))
        body.append(partialMatch.root)

        val lazyLoading =
            sectionBlock(
                tr("懒加载", "Lazy loading"),
                tr(
                    "dataProvider 按页取数：参数带 page、pageSize 与当前 filter，回调返回这一页与总数。",
                    "dataProvider pages the data: it receives page, pageSize and the current filter, and " +
                        "calls back with one page plus the total count.",
                ),
            )
        val lazyReadout = Readout(tr("尚未请求", "No request yet"))
        val lazy =
            createComboBox {
                label = tr("国家", "Country")
                dataProvider = { params, callback ->
                    val filter = params.filter.lowercase()
                    val matching = countryNames.filter { it.lowercase().startsWith(filter) }
                    val page = params.page.toInt()
                    val pageSize = params.pageSize.toInt()
                    val slice = matching.drop(page * pageSize).take(pageSize)
                    lazyReadout.set(
                        tr(
                            "第 ${page + 1} 页 · 每页 $pageSize 项 · 共 ${matching.size} 项",
                            "Page ${page + 1} · $pageSize per page · ${matching.size} in total",
                        ),
                    )
                    callback(slice.map { it.unsafeCast<JsAny?>() }.toTypedArray(), matching.size.toDouble())
                }
            }
        lazyLoading.body.append(exampleBox(null, kitCol(lazy, lazyReadout.root)))
        body.append(lazyLoading.root)

        val validation =
            sectionBlock(
                tr("校验", "Validation"),
                tr(
                    "required 与 allowed-char-pattern 一起约束输入；错误文案由 error-message 提供。",
                    "required and allowed-char-pattern constrain the input together; error-message supplies " +
                        "the failure text.",
                ),
            )
        val countryCode =
            createComboBox {
                label = tr("国家代码", "Country code")
                helperText = tr("两位大写 ISO 国家代码", "2-letter uppercase ISO country code")
                this.required = true
                allowedCharPattern = "[A-Z]"
                errorMessage = tr("此项为必填", "Field is required")
                allowCustomValue = true
                items = jsArray("""["DE", "FI", "US"]""")
            }
        val validationResult = Readout(tr("尚未校验", "Not validated yet"))
        val validateButton =
            createButton { textContent = tr("校验", "Validate") }.apply {
                clickEvent.addHandler {
                    validationResult.set(
                        tr("校验：", "Validation: "),
                        if (countryCode.validate()) tr("通过", "passed") else tr("失败", "failed"),
                    )
                }
            }
        validation.body.append(exampleBox(null, kitCol(countryCode, kitRow(validateButton, validationResult.root))))
        body.append(validation.root)

        val presentation =
            sectionBlock(
                tr("自定义条目呈现", "Custom item presentation"),
                tr(
                    "renderer 自己构建条目 DOM：头像、姓名与职业，与保留模式的其他示例一样逐个节点组装。",
                    "renderer builds each item's DOM itself: avatar, name and profession, assembled node by " +
                        "node like every other retained-mode example.",
                ),
            )
        val doctors = people(6)
        val doctorItems = personItems(doctors, withDisplayName = true)
        // `model.index` counts within `filteredItems`, so the renderer needs the list behind the current
        // filter rather than the full one.
        var visibleDoctors = doctors
        val doctorCombo =
            createComboBox {
                label = tr("选择医生", "Choose doctor")
                itemLabelPath = "displayName"
                items = doctorItems
                filteredItems = doctorItems
                renderer = { container, _, model ->
                    visibleDoctors.getOrNull(model.index.toInt())?.let { person ->
                        container.replaceChildren(newComboPersonRow(person))
                    }
                }
            }
        doctorCombo.style.setProperty("--vaadin-combo-box-overlay-width", "16em")
        doctorCombo.listen(ComboBoxEvents.filterChanged) {
            val filter = detailText(it).lowercase()
            visibleDoctors =
                doctors.filter { person ->
                    "${person.displayName} ${person.profession}".lowercase().contains(filter)
                }
            doctorCombo.filteredItems = personItems(visibleDoctors, withDisplayName = true)
            doctorCombo.requestContentUpdate()
        }
        presentation.body.append(exampleBox(null, doctorCombo))
        body.append(presentation.root)

        val states = sectionBlock(tr("只读与禁用", "Read-only and disabled"))
        states.body.append(
            exampleBox(
                null,
                kitRow(
                    createComboBox {
                        label = tr("只读", "Read-only")
                        items = jsArray("""["Value"]""")
                        value = "Value"
                        readonly = true
                    },
                    createComboBox {
                        label = tr("禁用", "Disabled")
                        disabled = true
                    },
                ),
            ),
        )
        body.append(states.root)

        val styles =
            sectionBlock(
                tr("样式变体", "Style variants"),
                tr(
                    "align-right、small 与 helper-above-field 同样适用于 combo-box。",
                    "align-right, small and helper-above-field work for the combo box as well.",
                ),
            )
        styles.body.append(
            exampleBox(
                null,
                createComboBox {
                    label = tr("标签", "Label")
                    helperText = tr("辅助信息", "Helper text")
                    items = jsArray("""["Value"]""")
                    value = "Value"
                }.addTheme("align-right", "small", "helper-above-field"),
            ),
        )
        body.append(styles.root)

    }

    companion object : CustomElementReference<ComboBoxPage>(ComboBoxPage::class.js) {
        val NAME: TagName<ComboBoxPage> = TagName("vaadin-demo-combo-box-page")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<vaadin-demo-combo-box-page>`. */
internal fun newComboBoxPage(): ComboBoxPage {
    ComboBoxPage.define()
    return ComboBoxPage.NAME()
}

/** Appends a typed string to a JS array without rebuilding the original items. */
private fun appendItem(items: Array<JsAny>, value: String): Array<JsAny> =
    js("(items, value) => [...items, value]")(items, value).unsafeCast<Array<JsAny>>()

/**
 * `<vaadin-demo-combo-person>`: one combo box item — avatar, name and profession.
 *
 * The combo box hands its renderer a container and asks it to fill that container, so the item has to
 * be a node of its own; it is an element, which keeps the assembling inside a component class.
 */
internal class ComboPersonRow : CustomElementAdapter() {
    /** Fills the row from [person]; the item content fills the overlay row, so the host is a block. */
    fun show(person: Person) {
        style.setProperty("display", "block")
        replaceChildren(
            kitRow(
                createAvatar {
                    name = person.displayName
                    img = person.pictureUrl
                },
                kitCol(
                    HtmlTagName.span { textContent = person.displayName },
                    HtmlTagName.span { textContent = person.profession },
                ),
            ),
        )
    }

    companion object : CustomElementReference<ComboPersonRow>(ComboPersonRow::class.js) {
        val NAME: TagName<ComboPersonRow> = TagName("vaadin-demo-combo-person")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<vaadin-demo-combo-person>` already showing [person]. */
internal fun newComboPersonRow(person: Person): ComboPersonRow {
    ComboPersonRow.define()
    return ComboPersonRow.NAME().apply { show(person) }
}

