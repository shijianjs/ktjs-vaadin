package demo.pages.selection

import io.github.shijianjs.ktjs.demo.page.Blocks.col as kitCol
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.page.Blocks.row as kitRow
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.pages.fixtures.Person
import demo.pages.fixtures.people
import demo.pages.fixtures.personItems
import demo.ui.PageFrame
import demo.ui.PageMeta
import demo.ui.Readout
import demo.ui.detailText
import demo.ui.jsArray
import demo.ui.listen
import kotlin.js.JsAny
import kotlin.js.js
import kotlin.js.unsafeCast
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.combobox.ComboBoxElement
import io.github.shijianjs.ktjs.vaadin.combobox.ComboBoxEvents
import io.github.shijianjs.ktjs.vaadin.combobox.ComboBoxPartialMatchModes
import io.github.shijianjs.ktjs.vaadin.combobox.createComboBox
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.vaadin.tooltip.createTooltip
import web.dom.clickEvent
import web.events.addHandler

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
        kitPage = { ComboBoxPage.NAME() },
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

private val english: ComboBoxTexts =
    ComboBoxTexts(
        basicHeading = "Basic usage",
        basicHint = "With itemLabelPath and itemValuePath set, items can be any object.",
        owner = "Owner",
        empty = "(empty)",
        featuresHeading = "Basic features",
        featuresHint = "helper-text, placeholder, clear-button-visible, a prefix icon and a tooltip are " +
            "all field-level settings.",
        label = "Label",
        helper = "Helper text",
        placeholder = "Placeholder",
        tooltip = "Tooltip text",
        customHeading = "Custom value entry",
        customHint = "allowCustomValue accepts values outside the list; the custom-value-set event " +
            "carries the new value, which can then be appended to items.",
        browser = "Browser",
        browserHelper = "Select or type a browser",
        customValuePrefix = "Custom value: ",
        customValueEmpty = "Custom value: (empty)",
        classNamesHeading = "Item class names",
        classNamesHint = "The class name returned by itemClassNameGenerator is added to every dropdown " +
            "item, which is enough to colour it.",
        fruit = "Fruit",
        autoOpenHeading = "Auto open",
        autoOpenHint = "auto-open-disabled keeps the dropdown closed on focus or typing; only the " +
            "toggle button opens it.",
        country = "Country",
        popupHeading = "Popup width",
        popupHint = "--vaadin-combo-box-overlay-width makes the dropdown wider than the field, which " +
            "suits long labels.",
        employee = "Employee",
        filterHeading = "Custom filtering",
        filterHint = "Setting filteredItems and listening to filter-changed takes over the matching " +
            "logic entirely.",
        totalItems = { "$it items" },
        matchedItems = { "$it items match" },
        partialHeading = "Partial match mode",
        partialHint = "partial-match-mode decides whether the typed text matches the start, any " +
            "position, or must match exactly.",
        lazyHeading = "Lazy loading",
        lazyHint = "dataProvider pages the data: it receives page, pageSize and the current filter, " +
            "and calls back with one page plus the total count.",
        noRequest = "No request yet",
        pageLine = { page, size, total -> "Page $page · $size per page · $total in total" },
        validationHeading = "Validation",
        validationHint = "required and allowed-char-pattern constrain the input together; " +
            "error-message supplies the failure text.",
        countryCode = "Country code",
        codeHelper = "2-letter uppercase ISO country code",
        requiredError = "Field is required",
        validate = "Validate",
        notValidated = "Not validated yet",
        checkPrefix = "Validation: ",
        passed = "passed",
        failed = "failed",
        presentationHeading = "Custom item presentation",
        presentationHint = "renderer builds each item's DOM itself: avatar, name and profession, " +
            "assembled node by node like every other retained-mode example.",
        chooseDoctor = "Choose doctor",
        statesHeading = "Read-only and disabled",
        readonly = "Read-only",
        disabled = "Disabled",
        stylesHeading = "Style variants",
        stylesHint = "align-right, small and helper-above-field work for the combo box as well.",
        value = "Value",
    )

private val chinese: ComboBoxTexts =
    ComboBoxTexts(
        basicHeading = "基本用法",
        basicHint = "给定 itemLabelPath 与 itemValuePath 后，items 可以是任意对象。",
        owner = "负责人",
        empty = "（空）",
        featuresHeading = "基本特性",
        featuresHint = "helper-text、placeholder、clear-button-visible、prefix 图标与 tooltip 都是字段级配置。",
        label = "标签",
        helper = "辅助信息",
        placeholder = "占位提示",
        tooltip = "提示文本",
        customHeading = "自定义值",
        customHint = "allowCustomValue 允许输入列表之外的值；custom-value-set 事件携带新值，可以把它" +
            "追加进 items。",
        browser = "浏览器",
        browserHelper = "选择或输入一个浏览器",
        customValuePrefix = "自定义值：",
        customValueEmpty = "自定义值：（空）",
        classNamesHeading = "选项类名",
        classNamesHint = "itemClassNameGenerator 返回的类名会加到每个下拉条目上，可以据此改变颜色。",
        fruit = "水果",
        autoOpenHeading = "自动展开",
        autoOpenHint = "auto-open-disabled 阻止聚焦或输入时自动展开下拉，只在点击切换按钮时打开。",
        country = "国家",
        popupHeading = "浮层宽度",
        popupHint = "--vaadin-combo-box-overlay-width 让下拉比输入框更宽，适合展示较长的标签。",
        employee = "员工",
        filterHeading = "自定义筛选",
        filterHint = "设置 filteredItems 并监听 filter-changed，可以完全接管匹配逻辑。",
        totalItems = { "共 $it 项" },
        matchedItems = { "匹配 $it 项" },
        partialHeading = "部分匹配模式",
        partialHint = "partial-match-mode 决定输入内容只匹配开头、匹配任意位置还是必须完全匹配。",
        lazyHeading = "懒加载",
        lazyHint = "dataProvider 按页取数：参数带 page、pageSize 与当前 filter，回调返回这一页与总数。",
        noRequest = "尚未请求",
        pageLine = { page, size, total -> "第 $page 页 · 每页 $size 项 · 共 $total 项" },
        validationHeading = "校验",
        validationHint = "required 与 allowed-char-pattern 一起约束输入；错误文案由 error-message 提供。",
        countryCode = "国家代码",
        codeHelper = "两位大写 ISO 国家代码",
        requiredError = "此项为必填",
        validate = "校验",
        notValidated = "尚未校验",
        checkPrefix = "校验：",
        passed = "通过",
        failed = "失败",
        presentationHeading = "自定义条目呈现",
        presentationHint = "renderer 自己构建条目 DOM：头像、姓名与职业，与保留模式的其他示例一样" +
            "逐个节点组装。",
        chooseDoctor = "选择医生",
        statesHeading = "只读与禁用",
        readonly = "只读",
        disabled = "禁用",
        stylesHeading = "样式变体",
        stylesHint = "align-right、small 与 helper-above-field 同样适用于 combo-box。",
        value = "值",
    )

private val comboBoxTexts: Translations<ComboBoxTexts> = texts(
    Langs.EN to english,
    Langs.ZH_HANS to chinese,
)

internal class ComboBoxPage : DemoPage() {
    private val texts = Binder(comboBoxTexts)

    private val frame = PageFrame(comboBoxEntry)

    private val owners = createComboBox()

    private val readout = Readout("")

    private val browser = createComboBox()

    private val customReadout = Readout("")

    private var customValue: String? = null

    private val filterReadout = Readout("")

    private var filterMatches: Int? = null

    private val lazy = createComboBox()

    private val lazyReadout = Readout("")

    private var lazyPage: Triple<Int, Int, Int>? = null

    private val countryCode = createComboBox()

    private val validationResult = Readout("")

    private var validated: Boolean? = null

    init {
        frame.mount(this)
        addSheet(FRUIT_SHEET)

        fun field(
            configure: ComboBoxElement.() -> Unit = {},
        ): ComboBoxElement = createComboBox(configure)

        val basic = SectionBlock.NAME { show() }
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        owners.itemLabelPath = "name"
        owners.itemValuePath = "id"
        owners.items = jsArray<JsAny>(OWNERS_JSON)
        owners.value = "grace"
        owners.clearButtonVisible = true
        texts.bind(owners, { it.owner }) { owners.label = it }
        owners.dataset["test"] = "combo-box-basic"
        owners.listen(ComboBoxEvents.valueChanged) { showOwner() }
        basic.body.append(exampleBox(null, kitCol(owners, readout.root)))
        body.append(basic.root)

        val features = SectionBlock.NAME { show() }
        texts.bind(features.heading) { it.featuresHeading }
        texts.bind(features.hint) { it.featuresHint }
        val featured = field()
        texts.bind(featured, { it.label }) { featured.label = it }
        texts.bind(featured, { it.helper }) { featured.helperText = it }
        texts.bind(featured, { it.placeholder }) { featured.placeholder = it }
        featured.clearButtonVisible = true
        featured.items = jsArray("""["Value"]""")
        val featuredTooltip = createTooltip().apply { setAttribute("slot", "tooltip") }
        texts.bind(featuredTooltip, { it.tooltip }) { featuredTooltip.text = it }
        featured.append(
            featuredTooltip,
            createIcon { icon = "vaadin:search" }.apply { setAttribute("slot", "prefix") },
        )
        features.body.append(exampleBox(null, featured))
        body.append(features.root)

        val customValueSection = SectionBlock.NAME { show() }
        texts.bind(customValueSection.heading) { it.customHeading }
        texts.bind(customValueSection.hint) { it.customHint }
        val browsers = jsArray<JsAny>("""["Chrome", "Edge", "Firefox", "Safari"]""")
        texts.bind(browser, { it.browser }) { browser.label = it }
        texts.bind(browser, { it.browserHelper }) { browser.helperText = it }
        browser.allowCustomValue = true
        browser.items = browsers
        browser.listen(ComboBoxEvents.customValueSet) {
            val value = detailText(it)
            customValue = value
            showCustom()
            browser.items = appendItem(browser.items ?: browsers, value)
        }
        customValueSection.body.append(exampleBox(null, kitCol(browser, customReadout.root)))
        body.append(customValueSection.root)

        val classNames = SectionBlock.NAME { show() }
        texts.bind(classNames.heading) { it.classNamesHeading }
        texts.bind(classNames.hint) { it.classNamesHint }
        val fruit = field()
        texts.bind(fruit, { it.fruit }) { fruit.label = it }
        fruit.items = jsArray(FRUITS_JSON)
        fruit.itemClassNameGenerator = { item -> fruitClass(item.unsafeCast<String>()) }
        classNames.body.append(exampleBox(null, fruit))
        body.append(classNames.root)

        val autoOpen = SectionBlock.NAME { show() }
        texts.bind(autoOpen.heading) { it.autoOpenHeading }
        texts.bind(autoOpen.hint) { it.autoOpenHint }
        val country = field()
        texts.bind(country, { it.country }) { country.label = it }
        country.itemLabelPath = "name"
        country.itemValuePath = "id"
        country.items = jsArray<JsAny>(COUNTRIES_JSON)
        country.autoOpenDisabled = true
        autoOpen.body.append(exampleBox(null, country))
        body.append(autoOpen.root)

        val popupWidth = SectionBlock.NAME { show() }
        texts.bind(popupWidth.heading) { it.popupHeading }
        texts.bind(popupWidth.hint) { it.popupHint }
        val employeeItems = personItems(people(5), withDisplayName = true)
        val employee = field()
        texts.bind(employee, { it.employee }) { employee.label = it }
        employee.itemLabelPath = "displayName"
        employee.itemValuePath = "id"
        employee.items = employeeItems
        employee.style.setProperty("--vaadin-combo-box-overlay-width", "350px")
        popupWidth.body.append(exampleBox(null, employee))
        body.append(popupWidth.root)

        val customFiltering = SectionBlock.NAME { show() }
        texts.bind(customFiltering.heading) { it.filterHeading }
        texts.bind(customFiltering.hint) { it.filterHint }
        val filterPeople = people(6)
        val filtered = createComboBox()
        val filterItems = personItems(filterPeople, withDisplayName = true)
        texts.bind(filtered, { it.employee }) { filtered.label = it }
        filtered.itemLabelPath = "displayName"
        filtered.items = filterItems
        filtered.filteredItems = filterItems
        filtered.listen(ComboBoxEvents.filterChanged) {
            val filter = detailText(it).lowercase()
            val matches =
                filterItems.filterIndexed { index, _ ->
                    filterPeople[index].displayName.lowercase().startsWith(filter)
                }.toTypedArray()
            filtered.filteredItems = matches
            filterMatches = matches.size
            showFilter()
        }
        customFiltering.body.append(exampleBox(null, kitCol(filtered, filterReadout.root)))
        body.append(customFiltering.root)

        val partialMatch = SectionBlock.NAME { show() }
        texts.bind(partialMatch.heading) { it.partialHeading }
        texts.bind(partialMatch.hint) { it.partialHint }
        val partial = field()
        texts.bind(partial, { it.country }) { partial.label = it }
        partial.itemLabelPath = "name"
        partial.itemValuePath = "id"
        partial.items = jsArray<JsAny>(COUNTRIES_JSON)
        partial.partialMatchMode = ComboBoxPartialMatchModes.firstMatch
        partialMatch.body.append(exampleBox(null, partial))
        body.append(partialMatch.root)

        val lazyLoading = SectionBlock.NAME { show() }
        texts.bind(lazyLoading.heading) { it.lazyHeading }
        texts.bind(lazyLoading.hint) { it.lazyHint }
        texts.bind(lazy, { it.country }) { lazy.label = it }
        lazy.dataProvider = { params, callback ->
            val filter = params.filter.lowercase()
            val matching = countryNames.filter { it.lowercase().startsWith(filter) }
            val page = params.page.toInt()
            val pageSize = params.pageSize.toInt()
            val slice = matching.drop(page * pageSize).take(pageSize)
            lazyPage = Triple(page + 1, pageSize, matching.size)
            showLazy()
            callback(slice.map { it.unsafeCast<JsAny?>() }.toTypedArray(), matching.size.toDouble())
        }
        lazyLoading.body.append(exampleBox(null, kitCol(lazy, lazyReadout.root)))
        body.append(lazyLoading.root)

        val validation = SectionBlock.NAME { show() }
        texts.bind(validation.heading) { it.validationHeading }
        texts.bind(validation.hint) { it.validationHint }
        texts.bind(countryCode, { it.countryCode }) { countryCode.label = it }
        texts.bind(countryCode, { it.codeHelper }) { countryCode.helperText = it }
        countryCode.required = true
        countryCode.allowedCharPattern = "[A-Z]"
        texts.bind(countryCode, { it.requiredError }) { countryCode.errorMessage = it }
        countryCode.allowCustomValue = true
        countryCode.items = jsArray("""["DE", "FI", "US"]""")
        val validateButton = createButton()
        texts.bind(validateButton) { it.validate }
        validateButton.clickEvent.addHandler {
            validated = countryCode.validate()
            showValidation()
        }
        validation.body.append(
            exampleBox(null, kitCol(countryCode, kitRow(validateButton, validationResult.root))),
        )
        body.append(validation.root)

        val presentation = SectionBlock.NAME { show() }
        texts.bind(presentation.heading) { it.presentationHeading }
        texts.bind(presentation.hint) { it.presentationHint }
        val doctors = people(6)
        val doctorItems = personItems(doctors, withDisplayName = true)
        // `model.index` counts within `filteredItems`, so the renderer needs the list behind the current
        // filter rather than the full one.
        var visibleDoctors = doctors
        val doctorCombo = field()
        texts.bind(doctorCombo, { it.chooseDoctor }) { doctorCombo.label = it }
        doctorCombo.itemLabelPath = "displayName"
        doctorCombo.items = doctorItems
        doctorCombo.filteredItems = doctorItems
        doctorCombo.renderer = { container, _, model ->
            visibleDoctors.getOrNull(model.index.toInt())?.let { person ->
                container.replaceChildren(newComboPersonRow(person))
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

        val states = SectionBlock.NAME { show() }
        texts.bind(states.heading) { it.statesHeading }
        val readonlyField = field { items = jsArray("""["Value"]"""); value = "Value"; readonly = true }
        texts.bind(readonlyField, { it.readonly }) { readonlyField.label = it }
        val disabledField = field { disabled = true }
        texts.bind(disabledField, { it.disabled }) { disabledField.label = it }
        states.body.append(exampleBox(null, kitRow(readonlyField, disabledField)))
        body.append(states.root)

        val styles = SectionBlock.NAME { show() }
        texts.bind(styles.heading) { it.stylesHeading }
        texts.bind(styles.hint) { it.stylesHint }
        val styled = field { items = jsArray("""["Value"]"""); value = "Value" }
        styled.addTheme("align-right", "small", "helper-above-field")
        texts.bind(styled, { it.label }) { styled.label = it }
        texts.bind(styled, { it.helper }) { styled.helperText = it }
        styles.body.append(exampleBox(null, styled))
        body.append(styles.root)

        applyTranslation(Langs.EN)
    }

    private fun showOwner() {
        readout.set("value = ", owners.value.ifEmpty { texts.current.empty })
    }

    private fun showCustom() {
        val value = customValue
        customReadout.set(
            if (value == null) texts.current.customValueEmpty
            else texts.current.customValuePrefix + value,
        )
    }

    private fun showLazy() {
        val copy = texts.current
        val request = lazyPage
        lazyReadout.set(
            if (request == null) copy.noRequest else copy.pageLine(request.first, request.second, request.third),
        )
    }

    private fun showValidation() {
        val copy = texts.current
        validationResult.set(
            copy.checkPrefix,
            if (validated == null) copy.notValidated else if (validated == true) copy.passed else copy.failed,
        )
    }

    override fun applyTranslation(lang: Lang) {
        val meta = comboBoxEntry.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        showOwner()
        showCustom()
        showLazy()
        showValidation()
        showFilter()
    }

    private fun showFilter() {
        val count = filterMatches
        filterReadout.set(if (count == null) texts.current.totalItems(6) else texts.current.matchedItems(count))
    }

    companion object : CustomElementReferenceAdapter<ComboBoxPage>(ComboBoxPage::class.js, "vaadin-demo-combo-box-page")
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

/** Creates a defined `<vaadin-demo-combo-person>` already showing [person]. */
internal fun newComboPersonRow(person: Person): ComboPersonRow {
    // The companion registers the tag in its `init`; reading `NAME` upgrades the element the page already spells.
    ComboPersonRow.NAME
    return ComboPersonRow.NAME().apply { show(person) }
}
