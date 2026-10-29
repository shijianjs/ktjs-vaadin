package demo.pages.selection

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.tr
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.Readout
import demo.ui.column
import demo.ui.example
import demo.ui.jsArray
import demo.ui.jsObject
import demo.ui.listen
import demo.ui.page
import demo.ui.row
import demo.ui.section
import kotlin.js.JsAny
import kotlin.js.js
import kotlin.js.unsafeCast
import vaadin.checkbox.CheckboxEvents
import vaadin.checkbox.createCheckbox
import vaadin.multiselectcombobox.MultiSelectComboBoxEvents
import vaadin.multiselectcombobox.createMultiSelectComboBox
import vaadin.textarea.createTextArea

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
        builder = ::buildMultiSelectComboBoxPage,
    )

private fun buildMultiSelectComboBoxPage(meta: PageMeta): PageView {
    val view = page(meta)

    val countries = jsArray<JsAny>(COUNTRIES_JSON)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "item-id-path 用来比较对象身份；selectedItems 是选中对象的数组。",
                "item-id-path compares object identity; selectedItems holds the selected objects.",
            ),
        )
    val multi =
        createMultiSelectComboBox {
            label = tr("国家", "Countries")
            itemLabelPath = "name"
            itemIdPath = "id"
            items = countries
        }
    multi.dataset["test"] = "multi-select-combo-box"
    multi.style.width = "300px"
    val readout = Readout(tr("已选 0 项", "0 selected"))
    multi.listen(MultiSelectComboBoxEvents.selectedItemsChanged) {
        readout.set(
            tr("已选 ${multi.selectedItems.size} 项：", "${multi.selectedItems.size} selected: "),
            itemNames(multi.selectedItems),
        )
    }
    basic.body.append(example(null, column(multi, readout.root)))
    view.body.append(basic.root)

    val selection =
        section(
            tr("预设选中项", "Preselected items"),
            tr(
                "selectedItems 可以直接赋值，chip 数量随选中项变化。",
                "selectedItems can be assigned directly; the number of chips follows it.",
            ),
        )
    val preset =
        createMultiSelectComboBox {
            label = tr("国家", "Countries")
            itemLabelPath = "name"
            itemIdPath = "id"
            items = countries
            selectedItems = countries.copyOfRange(0, 4)
        }
    preset.style.width = "300px"
    selection.body.append(example(null, preset))
    view.body.append(selection.root)

    val selectionChange =
        section(
            tr("选择变更", "Selection change"),
            tr(
                "selected-items-changed 的 detail 带当前选中数组，可以同步到其他字段。",
                "The detail of selected-items-changed carries the current selection, which can drive " +
                    "another field.",
            ),
        )
    val summary = createTextArea {
        label = tr("已选国家", "Selected Countries")
        readonly = true
    }
    val changing =
        createMultiSelectComboBox {
            label = tr("国家", "Countries")
            itemLabelPath = "name"
            itemIdPath = "id"
            items = countries
        }
    changing.listen(MultiSelectComboBoxEvents.selectedItemsChanged) {
        summary.value = itemNames(changing.selectedItems)
    }
    selectionChange.body.append(example(null, row(changing, summary)))
    view.body.append(selectionChange.root)

    val readonly =
        section(
            tr("只读", "Read-only"),
            tr(
                "readonly 保留 chip 展示，但用户不能添加或移除。",
                "readonly keeps the chips visible but blocks adding and removing.",
            ),
        )
    val readonlyBox =
        createMultiSelectComboBox {
            label = tr("国家", "Countries")
            itemLabelPath = "name"
            itemIdPath = "id"
            items = countries
            selectedItems = countries.copyOfRange(0, 4)
            this.readonly = true
        }
    readonlyBox.style.width = "300px"
    readonly.body.append(example(null, readonlyBox))
    view.body.append(readonly.root)

    val autoExpand =
        section(
            tr("自动展开", "Auto expand"),
            tr(
                "auto-expand-horizontally / auto-expand-vertically 让字段随 chip 数量变大，而不是滚动。",
                "auto-expand-horizontally / auto-expand-vertically let the field grow with the chips " +
                    "instead of scrolling.",
            ),
        )
    val expanding =
        createMultiSelectComboBox {
            label = tr("国家", "Countries")
            itemLabelPath = "name"
            itemIdPath = "id"
            items = countries
            selectedItems = countries.copyOfRange(0, 4)
            autoExpandHorizontally = true
            autoExpandVertically = true
        }
    autoExpand.body.append(example(null, expanding))
    view.body.append(autoExpand.root)

    val selectedOnTop =
        section(
            tr("选中项置顶", "Selected items on top"),
            tr(
                "selected-items-on-top 把已选项排到下拉列表最前面，长列表里更容易核对。",
                "selected-items-on-top moves the selected entries to the top of the dropdown, which helps " +
                    "in long lists.",
            ),
        )
    val onTop =
        createMultiSelectComboBox {
            label = tr("国家", "Countries")
            itemLabelPath = "name"
            itemIdPath = "id"
            items = countries
            selectedItems = countries.copyOfRange(4, 6)
            selectedItemsOnTop = true
        }
    selectedOnTop.body.append(example(null, onTop))
    view.body.append(selectedOnTop.root)

    val collapseChipsSection =
        section(
            tr("折叠 chip", "Collapse chips"),
            tr(
                "collapseChips 把溢出的 chip 折叠成计数，可以由页面上的开关实时切换。",
                "collapseChips folds the overflowing chips into a counter and can be toggled live.",
            ),
        )
    val collapsing =
        createMultiSelectComboBox {
            label = tr("国家", "Countries")
            itemLabelPath = "name"
            itemIdPath = "id"
            items = countries
            selectedItems = countries.copyOfRange(0, 3)
            collapseChips = true
        }
    collapsing.style.width = "250px"
    val collapseToggle = createCheckbox { label = tr("折叠 chip", "Collapse chips") }
    collapseToggle.checked = true
    collapseToggle.listen(CheckboxEvents.checkedChanged) { collapsing.collapseChips = collapseToggle.checked }
    collapseChipsSection.body.append(example(null, column(collapsing, collapseToggle)))
    view.body.append(collapseChipsSection.root)

    val classNames =
        section(
            tr("选项类名", "Item class names"),
            tr(
                "itemClassNameGenerator 同时作用在 chip 与下拉条目上。",
                "itemClassNameGenerator applies to both the chips and the dropdown items.",
            ),
        )
    val fruits = jsArray<JsAny>(FRUITS_JSON)
    val fruit =
        createMultiSelectComboBox {
            label = tr("水果", "Fruit")
            items = fruits
            selectedItems = fruits.copyOfRange(0, 2)
            itemClassNameGenerator = { item -> fruitClass(item.unsafeCast<String>()) }
        }
    fruit.style.width = "300px"
    classNames.body.append(example(null, fruit))
    view.body.append(classNames.root)

    val i18nSection =
        section(
            tr("国际化", "Internationalization"),
            tr(
                "i18n 覆盖清除、选中、取消与计数的提示文案；示例沿用官方文档的德语字符串。",
                "i18n overrides the cleared / selected / deselected / counter messages; the example keeps " +
                    "the German strings of the official documentation.",
            ),
        )
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
    i18nSection.body.append(example(null, localized))
    view.body.append(i18nSection.root)

    return view
}

/** Reads the `name` field of the selected country objects. */
private fun itemNames(items: Array<JsAny>): String =
    items.joinToString(", ") { item -> js("(item) => String(item.name)")(item) as String }
