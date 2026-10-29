package demo.pages.selection

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
import demo.ui.PageView
import demo.ui.Readout
import demo.ui.column
import demo.ui.detailText
import demo.ui.example
import demo.ui.jsArray
import demo.ui.listen
import demo.ui.page
import demo.ui.row
import demo.ui.section
import demo.ui.span
import demo.ui.withClass
import demo.ui.withTheme
import kotlin.js.JsAny
import kotlin.js.js
import kotlin.js.unsafeCast
import vaadin.avatar.createAvatar
import vaadin.button.createButton
import vaadin.combobox.ComboBoxEvents
import vaadin.combobox.ComboBoxItemModel
import vaadin.combobox.ComboBoxPartialMatchModes
import vaadin.combobox.createComboBox
import vaadin.icon.createIcon
import vaadin.tooltip.createTooltip
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
        builder = ::buildComboBoxPage,
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

private fun buildComboBoxPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
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
    basic.body.append(example(null, column(owners, readout.root)))
    view.body.append(basic.root)

    val features =
        section(
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
    features.body.append(example(null, featured))
    view.body.append(features.root)

    val customValue =
        section(
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
    customValue.body.append(example(null, column(browser, customReadout.root)))
    view.body.append(customValue.root)

    val classNames =
        section(
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
    classNames.body.append(example(null, fruit))
    view.body.append(classNames.root)

    val autoOpen =
        section(
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
    autoOpen.body.append(example(null, country))
    view.body.append(autoOpen.root)

    val popupWidth =
        section(
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
    popupWidth.body.append(example(null, employee))
    view.body.append(popupWidth.root)

    val customFiltering =
        section(
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
    customFiltering.body.append(example(null, column(filtered, filterReadout.root)))
    view.body.append(customFiltering.root)

    val partialMatch =
        section(
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
    partialMatch.body.append(example(null, partial))
    view.body.append(partialMatch.root)

    val lazyLoading =
        section(
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
    lazyLoading.body.append(example(null, column(lazy, lazyReadout.root)))
    view.body.append(lazyLoading.root)

    val validation =
        section(
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
    validation.body.append(example(null, column(countryCode, row(validateButton, validationResult.root))))
    view.body.append(validation.root)

    val presentation =
        section(
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
            renderer = { container, _, model -> renderPersonItem(container, model, visibleDoctors) }
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
    presentation.body.append(example(null, doctorCombo))
    view.body.append(presentation.root)

    val states = section(tr("只读与禁用", "Read-only and disabled"))
    states.body.append(
        example(
            null,
            row(
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
    view.body.append(states.root)

    val styles =
        section(
            tr("样式变体", "Style variants"),
            tr(
                "align-right、small 与 helper-above-field 同样适用于 combo-box。",
                "align-right, small and helper-above-field work for the combo box as well.",
            ),
        )
    styles.body.append(
        example(
            null,
            createComboBox {
                label = tr("标签", "Label")
                helperText = tr("辅助信息", "Helper text")
                items = jsArray("""["Value"]""")
                value = "Value"
            }.withTheme("align-right", "small", "helper-above-field"),
        ),
    )
    view.body.append(styles.root)

    return view
}

/** Appends a typed string to a JS array without rebuilding the original items. */
private fun appendItem(items: Array<JsAny>, value: String): Array<JsAny> =
    js("(items, value) => [...items, value]")(items, value).unsafeCast<Array<JsAny>>()

/** Renders one combo box item as avatar + name + profession. */
private fun renderPersonItem(container: HTMLElement, model: ComboBoxItemModel, peopleList: List<Person>) {
    val index = model.index.toInt()
    val person = peopleList.getOrNull(index) ?: return
    container.replaceChildren(
        row(
            vaadin.avatar.createAvatar {
                name = person.displayName
                img = person.pictureUrl
            },
            column(span(person.displayName), span(person.profession)),
        ).withClass("person-item"),
    )
}
