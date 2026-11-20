package demo.pages.selection

import io.github.shijianjs.ktjs.demo.page.col as kitCol
import io.github.shijianjs.ktjs.demo.page.exampleBox
import io.github.shijianjs.ktjs.demo.page.row as kitRow
import io.github.shijianjs.ktjs.demo.page.sectionBlock


import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import web.components.CustomElementReference
import web.dom.TagName
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.ui.PageFrame
import demo.ui.PageMeta
import demo.ui.Readout
import demo.ui.jsArray
import demo.ui.jsObject
import demo.ui.listen
import kotlin.js.JsAny
import kotlin.js.js
import kotlin.js.unsafeCast
import io.github.shijianjs.ktjs.vaadin.checkbox.CheckboxEvents
import io.github.shijianjs.ktjs.vaadin.checkbox.createCheckbox
import io.github.shijianjs.ktjs.vaadin.multiselectcombobox.MultiSelectComboBoxElement
import io.github.shijianjs.ktjs.vaadin.multiselectcombobox.MultiSelectComboBoxEvents
import io.github.shijianjs.ktjs.vaadin.multiselectcombobox.createMultiSelectComboBox
import io.github.shijianjs.ktjs.vaadin.textarea.createTextArea

val multiSelectComboBoxEntry: CatalogEntry =
    CatalogEntry(
        id = "multi-select-combo-box",
        group = Groups.SELECTION,
        meta =
            PageMeta(
                title = bi("Multi-Select Combo Box", "Multi-Select Combo Box"),
                summary =
                    bi(
                        "多选下拉：选中项显示为 chip，selectedItems 是选中的对象数组。",
                        "Multi-select dropdown: selections show as chips and selectedItems holds the " +
                            "selected objects.",
                    ),
                docsUrl = "$DOCS_BASE/multi-select-combo-box",
                sourcePath = "pages/selection/MultiSelectComboBoxPage.kt",
            ),
        kitPage = { newMultiSelectComboBoxPage() },
    )

/** Every string this page shows, in one language. */
private class MultiSelectComboBoxTexts(
    val basicHeading: String,
    val basicHint: String,
    val countries: String,
    val selectedCount: (Int) -> String,
    val selectedPrefix: (Int) -> String,
    val presetHeading: String,
    val presetHint: String,
    val changeHeading: String,
    val changeHint: String,
    val selectedCountries: String,
    val readonlyHeading: String,
    val readonlyHint: String,
    val expandHeading: String,
    val expandHint: String,
    val onTopHeading: String,
    val onTopHint: String,
    val collapseHeading: String,
    val collapseHint: String,
    val collapseChips: String,
    val classNamesHeading: String,
    val classNamesHint: String,
    val fruit: String,
    val i18nHeading: String,
    val i18nHint: String,
)

private val english: MultiSelectComboBoxTexts =
    MultiSelectComboBoxTexts(
        basicHeading = "Basic usage",
        basicHint = "item-id-path compares object identity; selectedItems holds the selected objects.",
        countries = "Countries",
        selectedCount = { count -> "$count selected" },
        selectedPrefix = { count -> "$count selected: " },
        presetHeading = "Preselected items",
        presetHint = "selectedItems can be assigned directly; the number of chips follows it.",
        changeHeading = "Selection change",
        changeHint = "The detail of selected-items-changed carries the current selection, which can " +
            "drive another field.",
        selectedCountries = "Selected Countries",
        readonlyHeading = "Read-only",
        readonlyHint = "readonly keeps the chips visible but blocks adding and removing.",
        expandHeading = "Auto expand",
        expandHint = "auto-expand-horizontally / auto-expand-vertically let the field grow with the " +
            "chips instead of scrolling.",
        onTopHeading = "Selected items on top",
        onTopHint = "selected-items-on-top moves the selected entries to the top of the dropdown, " +
            "which helps in long lists.",
        collapseHeading = "Collapse chips",
        collapseHint = "collapseChips folds the overflowing chips into a counter and can be toggled live.",
        collapseChips = "Collapse chips",
        classNamesHeading = "Item class names",
        classNamesHint = "itemClassNameGenerator applies to both the chips and the dropdown items.",
        fruit = "Fruit",
        i18nHeading = "Internationalization",
        i18nHint = "i18n overrides the cleared / selected / deselected / counter messages; the example " +
            "keeps the German strings of the official documentation.",
    )

private val chinese: MultiSelectComboBoxTexts =
    MultiSelectComboBoxTexts(
        basicHeading = "基本用法",
        basicHint = "item-id-path 用来比较对象身份；selectedItems 是选中对象的数组。",
        countries = "国家",
        selectedCount = { count -> "已选 $count 项" },
        selectedPrefix = { count -> "已选 $count 项：" },
        presetHeading = "预设选中项",
        presetHint = "selectedItems 可以直接赋值，chip 数量随选中项变化。",
        changeHeading = "选择变更",
        changeHint = "selected-items-changed 的 detail 带当前选中数组，可以同步到其他字段。",
        selectedCountries = "已选国家",
        readonlyHeading = "只读",
        readonlyHint = "readonly 保留 chip 展示，但用户不能添加或移除。",
        expandHeading = "自动展开",
        expandHint = "auto-expand-horizontally / auto-expand-vertically 让字段随 chip 数量变大，而不是滚动。",
        onTopHeading = "选中项置顶",
        onTopHint = "selected-items-on-top 把已选项排到下拉列表最前面，长列表里更容易核对。",
        collapseHeading = "折叠 chip",
        collapseHint = "collapseChips 把溢出的 chip 折叠成计数，可以由页面上的开关实时切换。",
        collapseChips = "折叠 chip",
        classNamesHeading = "选项类名",
        classNamesHint = "itemClassNameGenerator 同时作用在 chip 与下拉条目上。",
        fruit = "水果",
        i18nHeading = "国际化",
        i18nHint = "i18n 覆盖清除、选中、取消与计数的提示文案；示例沿用官方文档的德语字符串。",
    )

private val multiSelectComboBoxTexts: Translations<MultiSelectComboBoxTexts> = texts(
    Langs.EN to english,
    Langs.ZH_HANS to chinese,
)

internal class MultiSelectComboBoxPage : DemoPage() {
    private val texts = Binder(multiSelectComboBoxTexts)

    private val frame = PageFrame(multiSelectComboBoxEntry)

    /** The field whose selection the readout follows, kept as a field so a switch can re-render it. */
    private val multi: MultiSelectComboBoxElement = createMultiSelectComboBox()

    private val readout = Readout("")

    init {
        frame.mount(this)
        addSheet(FRUIT_SHEET)

        val countries = jsArray<JsAny>(COUNTRIES_JSON)

        val basic = sectionBlock()
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        multi.itemLabelPath = "name"
        multi.itemIdPath = "id"
        multi.items = countries
        texts.bind(multi, { it.countries }) { multi.label = it }
        multi.dataset["test"] = "multi-select-combo-box"
        multi.style.width = "300px"
        multi.listen(MultiSelectComboBoxEvents.selectedItemsChanged) { showSelection() }
        basic.body.append(exampleBox(null, kitCol(multi, readout.root)))
        body.append(basic.root)

        val selection = sectionBlock()
        texts.bind(selection.heading) { it.presetHeading }
        texts.bind(selection.hint) { it.presetHint }
        val preset =
            createMultiSelectComboBox {
                itemLabelPath = "name"
                itemIdPath = "id"
                items = countries
                selectedItems = countries.copyOfRange(0, 4)
            }
        texts.bind(preset, { it.countries }) { preset.label = it }
        preset.style.width = "300px"
        selection.body.append(exampleBox(null, preset))
        body.append(selection.root)

        val selectionChange = sectionBlock()
        texts.bind(selectionChange.heading) { it.changeHeading }
        texts.bind(selectionChange.hint) { it.changeHint }
        val summary = createTextArea { readonly = true }
        texts.bind(summary, { it.selectedCountries }) { summary.label = it }
        val changing =
            createMultiSelectComboBox {
                itemLabelPath = "name"
                itemIdPath = "id"
                items = countries
            }
        texts.bind(changing, { it.countries }) { changing.label = it }
        changing.listen(MultiSelectComboBoxEvents.selectedItemsChanged) {
            summary.value = itemNames(changing.selectedItems)
        }
        selectionChange.body.append(exampleBox(null, kitRow(changing, summary)))
        body.append(selectionChange.root)

        val readonly = sectionBlock()
        texts.bind(readonly.heading) { it.readonlyHeading }
        texts.bind(readonly.hint) { it.readonlyHint }
        val readonlyBox =
            createMultiSelectComboBox {
                itemLabelPath = "name"
                itemIdPath = "id"
                items = countries
                selectedItems = countries.copyOfRange(0, 4)
                this.readonly = true
            }
        texts.bind(readonlyBox, { it.countries }) { readonlyBox.label = it }
        readonlyBox.style.width = "300px"
        readonly.body.append(exampleBox(null, readonlyBox))
        body.append(readonly.root)

        val autoExpand = sectionBlock()
        texts.bind(autoExpand.heading) { it.expandHeading }
        texts.bind(autoExpand.hint) { it.expandHint }
        val expanding =
            createMultiSelectComboBox {
                itemLabelPath = "name"
                itemIdPath = "id"
                items = countries
                selectedItems = countries.copyOfRange(0, 4)
                autoExpandHorizontally = true
                autoExpandVertically = true
            }
        texts.bind(expanding, { it.countries }) { expanding.label = it }
        autoExpand.body.append(exampleBox(null, expanding))
        body.append(autoExpand.root)

        val selectedOnTop = sectionBlock()
        texts.bind(selectedOnTop.heading) { it.onTopHeading }
        texts.bind(selectedOnTop.hint) { it.onTopHint }
        val onTop =
            createMultiSelectComboBox {
                itemLabelPath = "name"
                itemIdPath = "id"
                items = countries
                selectedItems = countries.copyOfRange(4, 6)
                selectedItemsOnTop = true
            }
        texts.bind(onTop, { it.countries }) { onTop.label = it }
        selectedOnTop.body.append(exampleBox(null, onTop))
        body.append(selectedOnTop.root)

        val collapseChipsSection = sectionBlock()
        texts.bind(collapseChipsSection.heading) { it.collapseHeading }
        texts.bind(collapseChipsSection.hint) { it.collapseHint }
        val collapsing =
            createMultiSelectComboBox {
                itemLabelPath = "name"
                itemIdPath = "id"
                items = countries
                selectedItems = countries.copyOfRange(0, 3)
                collapseChips = true
            }
        texts.bind(collapsing, { it.countries }) { collapsing.label = it }
        collapsing.style.width = "250px"
        val collapseToggle = createCheckbox()
        texts.bind(collapseToggle, { it.collapseChips }) { collapseToggle.label = it }
        collapseToggle.checked = true
        collapseToggle.listen(CheckboxEvents.checkedChanged) { collapsing.collapseChips = collapseToggle.checked }
        collapseChipsSection.body.append(exampleBox(null, kitCol(collapsing, collapseToggle)))
        body.append(collapseChipsSection.root)

        val classNames = sectionBlock()
        texts.bind(classNames.heading) { it.classNamesHeading }
        texts.bind(classNames.hint) { it.classNamesHint }
        val fruits = jsArray<JsAny>(FRUITS_JSON)
        val fruit =
            createMultiSelectComboBox {
                items = fruits
                selectedItems = fruits.copyOfRange(0, 2)
                itemClassNameGenerator = { item -> fruitClass(item.unsafeCast<String>()) }
            }
        texts.bind(fruit, { it.fruit }) { fruit.label = it }
        fruit.style.width = "300px"
        classNames.body.append(exampleBox(null, fruit))
        body.append(classNames.root)

        val i18nSection = sectionBlock()
        texts.bind(i18nSection.heading) { it.i18nHeading }
        texts.bind(i18nSection.hint) { it.i18nHint }
        val localized =
            createMultiSelectComboBox {
                label = "Länder"
                itemLabelPath = "name"
                itemIdPath = "id"
                items = countries
                i18n =
                    jsObject(
                        """
                        {
                          "cleared": "Alle Einträge entfernt",
                          "focused": " ausgewählt. Drücke Rücktaste zum Entfernen",
                          "selected": " hinzugefügt",
                          "deselected": " entfernt",
                          "total": "{count} Einträge ausgewählt"
                        }
                        """,
                    )
            }
        localized.style.width = "300px"
        i18nSection.body.append(exampleBox(null, localized))
        body.append(i18nSection.root)

        applyTranslation(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = multiSelectComboBoxEntry.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        showSelection()
    }

    /** The readout follows the chips: the count alone while empty, the count plus the names after. */
    private fun showSelection() {
        val copy = texts.current
        val count = multi.selectedItems.size
        if (count == 0) {
            readout.set(copy.selectedCount(0))
        } else {
            readout.set(copy.selectedPrefix(count), itemNames(multi.selectedItems))
        }
    }

    companion object : CustomElementReference<MultiSelectComboBoxPage>(MultiSelectComboBoxPage::class.js) {
        val NAME: TagName<MultiSelectComboBoxPage> = TagName("vaadin-demo-multi-select-combo-box-page")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<vaadin-demo-multi-select-combo-box-page>`. */
internal fun newMultiSelectComboBoxPage(): MultiSelectComboBoxPage {
    MultiSelectComboBoxPage.define()
    return MultiSelectComboBoxPage.NAME()
}

/** Reads the `name` field of the selected country objects. */
private fun itemNames(items: Array<JsAny>): String =
    items.joinToString(", ") { item -> js("(item) => String(item.name)")(item) as String }
