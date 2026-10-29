package demo.pages.selection

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.pages.fixtures.people
import demo.tr
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.Readout
import demo.ui.column
import demo.ui.element
import demo.ui.example
import demo.ui.joinValues
import demo.ui.listen
import demo.ui.page
import demo.ui.row
import demo.ui.section
import demo.ui.span
import demo.ui.withClass
import vaadin.a11ybase.ListOrientations
import vaadin.avatar.createAvatar
import vaadin.item.createItem
import vaadin.listbox.ListBoxEvents
import vaadin.listbox.createListBox
import web.html.HtmlTagName

val listBoxEntry: CatalogEntry =
    CatalogEntry(
        id = "list-box",
        group = Groups.SELECTION,
        meta =
            PageMeta(
                title = bi("List Box", "List Box"),
                summary =
                    bi(
                        "可滚动的选项列表，支持分隔线、禁用条目、单选与多选。",
                        "Scrollable list of options with dividers, disabled items, single and multiple " +
                            "selection.",
                    ),
                docsUrl = "$DOCS_BASE/list-box",
                sourcePath = "pages/selection/ListBoxPage.kt",
            ),
        builder = ::buildListBoxPage,
    )

private fun buildListBoxPage(meta: PageMeta): PageView {
    val view = page(meta)

    val dividers =
        section(
            tr("分隔线", "Dividers"),
            tr(
                "列表里可以混入 <hr> 作为分组分隔线，它不参与选择。",
                "A list can mix in <hr> elements as group dividers; they take no part in selection.",
            ),
        )
    val separated = createListBox { multiple = true }
    separated.selectedValues = arrayOf(0.0, 2.0, 3.0)
    separated.append(
        createItem { textContent = tr("显示负责人", "Show assignee") },
        createItem { textContent = tr("显示截止日期", "Show due date") },
        createItem { textContent = tr("显示状态", "Show status") },
        element(HtmlTagName.hr),
        createItem { textContent = tr("显示缩略图", "Show thumbnail") },
        createItem { textContent = tr("显示预览", "Show preview") },
    )
    val separatedReadout = Readout("selectedValues = 0, 2, 3")
    separated.listen(ListBoxEvents.selectedValuesChanged) {
        separatedReadout.set("selectedValues = ", joinValues(separated.selectedValues).ifEmpty { "[]" })
    }
    dividers.body.append(example(null, column(separated, separatedReadout.root)))
    view.body.append(dividers.root)

    val disabledItems =
        section(
            tr("禁用条目", "Disabled items"),
            tr(
                "单个条目可以单独禁用，列表其余部分仍可交互。",
                "Individual items can be disabled while the rest of the list stays interactive.",
            ),
        )
    val withDisabled = createListBox()
    withDisabled.selected = 0.0
    withDisabled.append(
        createItem { textContent = tr("进行中 (2)", "In progress (2)") },
        createItem { textContent = tr("已完成 (4)", "Done (4)") },
        createItem {
            textContent = tr("已取消 (0)", "Cancelled (0)")
            disabled = true
        },
    )
    disabledItems.body.append(example(null, withDisabled))
    view.body.append(disabledItems.root)

    val single =
        section(
            tr("单选", "Single selection"),
            tr(
                "selected 是选中项的下标（Double），未选中时为 undefined。",
                "selected is the index of the selected item (Double); it is undefined when nothing is " +
                    "selected.",
            ),
        )
    val singleBox = createListBox()
    singleBox.dataset["test"] = "list-box-single"
    singleBox.selected = 0.0
    singleBox.append(
        createItem { textContent = tr("进行中", "In progress") },
        createItem { textContent = tr("已完成", "Done") },
        createItem { textContent = tr("已取消", "Cancelled") },
    )
    val readout = Readout("selected = 0")
    singleBox.listen(ListBoxEvents.selectedChanged) {
        readout.set("selected = ", singleBox.selected.toString())
    }
    single.body.append(example(null, row(singleBox, readout.root)))
    view.body.append(single.root)

    val multipleSection =
        section(
            tr("多选", "Multiple selection"),
            tr(
                "multiple 打开后用 selectedValues 读写所有选中下标；列表可滚动，固定高度即可。",
                "With multiple on, selectedValues reads and writes every selected index; the list " +
                    "scrolls inside a fixed height.",
            ),
        )
    val multiBox = createListBox { multiple = true }
    multiBox.selectedValues = arrayOf(0.0, 3.0)
    multiBox.style.height = "200px"
    multiBox.append(
        *people(20).map { person ->
            createItem { textContent = person.displayName }
        }.toTypedArray(),
    )
    val multiReadout = Readout("selectedValues = 0, 3")
    multiBox.listen(ListBoxEvents.selectedValuesChanged) {
        multiReadout.set("selectedValues = ", joinValues(multiBox.selectedValues).ifEmpty { "[]" })
    }
    multipleSection.body.append(example(null, column(multiBox, multiReadout.root)))
    view.body.append(multipleSection.root)

    val presentation =
        section(
            tr("自定义条目呈现", "Custom item presentation"),
            tr(
                "条目内容是普通 DOM：头像 + 姓名 + 职业，随选中状态一并高亮。",
                "Item content is plain DOM: avatar, name and profession, highlighted together with the " +
                    "selection state.",
            ),
        )
    val rich = createListBox { multiple = true }
    rich.selectedValues = arrayOf(0.0, 2.0)
    rich.append(
        *people(5).map { person ->
            createItem {
                append(
                    row(
                        createAvatar {
                            name = person.displayName
                            img = person.pictureUrl
                        },
                        column(span(person.displayName), span(person.profession)),
                    ).withClass("person-item"),
                )
            }
        }.toTypedArray(),
    )
    presentation.body.append(example(null, rich))
    view.body.append(presentation.root)

    val orientationSection =
        section(
            tr("方向与禁用", "Orientation and disabled state"),
            tr(
                "orientation 控制排布方向，禁用后整组不可交互。",
                "orientation sets the layout direction; disabling blocks the whole group.",
            ),
        )
    val horizontal = createListBox { orientation = ListOrientations.horizontal }
    horizontal.append(
        createItem { textContent = tr("一", "One") },
        createItem { textContent = tr("二", "Two") },
        createItem { textContent = tr("三", "Three") },
    )
    val disabledBox = createListBox()
    disabledBox.disabled = true
    disabledBox.append(
        createItem { textContent = tr("禁用项一", "Disabled item 1") },
        createItem { textContent = tr("禁用项二", "Disabled item 2") },
    )
    orientationSection.body.append(example(null, row(horizontal, disabledBox)))
    view.body.append(orientationSection.root)

    return view
}
