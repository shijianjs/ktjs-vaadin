package demo.pages

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.tr
import demo.ui.EventLog
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.Readout
import demo.ui.column
import demo.ui.div
import demo.ui.example
import demo.ui.jsArray
import demo.ui.jsObject
import demo.ui.listen
import demo.ui.page
import demo.ui.paragraph
import demo.ui.row
import demo.ui.section
import demo.ui.setHidden
import demo.ui.span
import demo.ui.withTheme
import kotlin.js.Date
import kotlin.js.JsAny
import kotlin.js.js
import kotlin.js.unsafeCast
import vaadin.avatar.createAvatar
import vaadin.button.createButton
import vaadin.checkbox.CheckboxEvents
import vaadin.checkbox.createCheckbox
import vaadin.combobox.createComboBox
import vaadin.grid.GridElement
import vaadin.grid.GridEvents
import vaadin.grid.GridMultiSortPriorities
import vaadin.grid.GridSorterDirection
import vaadin.grid.GridSorterDirections
import vaadin.grid.createGrid
import vaadin.grid.createGridColumn
import vaadin.grid.createGridColumnGroup
import vaadin.grid.createGridFilterColumn
import vaadin.grid.createGridSelectionColumn
import vaadin.grid.createGridSortColumn
import vaadin.icon.createIcon
import vaadin.select.SelectEvents
import vaadin.select.SelectItemData
import vaadin.select.createSelect
import vaadin.textfield.TextFieldEvents
import vaadin.textfield.createTextField
import web.cssom.TextAlign
import web.dom.clickEvent
import web.events.addHandler
import web.html.HTMLElement

val gridEntries: List<CatalogEntry> =
    listOf(
        CatalogEntry(
            id = "grid",
            group = Groups.DATA,
            meta =
                PageMeta(
                    title = bi("Grid", "Grid"),
                    summary =
                        bi(
                            "数据表格：列、绑定数据、排序、行详情、选择、拖放、样式与懒渲染。",
                            "Data table: columns, data binding, sorting, row details, selection, drag and " +
                                "drop, styling and lazy rendering.",
                        ),
                    docsUrl = "$DOCS_BASE/grid",
                    sourcePath = "pages/Grid.kt",
                ),
            builder = ::buildGridPage,
        ),
    )

// region Fixture

/**
 * Serialises [list] with the fields the grid examples use. `address` stays a nested object so
 * columns can bind dotted paths (`address.phone`).
 */
internal fun gridItems(list: List<Person>, withRating: Boolean = false): Array<JsAny> {
    val entries =
        list.map { person ->
            val fields =
                mutableListOf(
                    "\"id\": ${person.id}",
                    "\"firstName\": \"${person.firstName}\"",
                    "\"lastName\": \"${person.lastName}\"",
                    "\"displayName\": \"${person.displayName}\"",
                    "\"email\": \"${person.email}\"",
                    "\"profession\": \"${person.profession}\"",
                    "\"birthday\": \"${person.birthday}\"",
                    "\"status\": \"${person.status}\"",
                    "\"subscriber\": ${person.subscriber}",
                    "\"membership\": \"${person.membership}\"",
                    "\"pictureUrl\": \"${person.pictureUrl}\"",
                    "\"address\": {\"street\": \"${person.street}\", \"city\": \"${person.city}\", " +
                        "\"zip\": \"${person.zip}\", \"state\": \"${person.state}\", " +
                        "\"phone\": \"${person.phone}\"}",
                )
            if (withRating) fields += "\"customerRating\": ${person.customerRating}"
            "{${fields.joinToString(", ")}}"
        }
    return jsArray("[${entries.joinToString(", ")}]")
}

/** Same as [gridItems], typed for the callbacks that accept nullable items. */
internal fun gridItemsNullable(list: List<Person>, withRating: Boolean = false): Array<JsAny?> =
    gridItems(list, withRating).map { it as JsAny? }.toTypedArray()

/** The `items` array of a grid created empty and filled from code. */
internal fun GridElement.fill(list: List<Person>) {
    items = gridItems(list)
}

internal fun itemText(item: JsAny?, key: String): String =
    js("(item, key) => item == null || item[key] == null ? '' : String(item[key])")(item, key) as String

internal fun itemPathText(item: JsAny?, path: String): String =
    js(
        "(item, path) => {" +
            " let value = item;" +
            " for (const key of path.split('.')) { if (value == null) return ''; value = value[key]; }" +
            " return value == null ? '' : String(value);" +
            "}",
    )(item, path) as String

internal fun itemId(item: JsAny?): Int = (js("(item) => item == null || item.id == null ? -1 : Number(item.id)")(item) as Double).toInt()

internal fun itemFlag(item: JsAny?, key: String): Boolean =
    js("(item, key) => !!(item && item[key])")(item, key) as Boolean

internal fun twoDigits(value: Int): String = if (value < 10) "0$value" else "$value"

/** `YYYY-MM-DD` → `MM/DD/YYYY`, the format the docs examples display. */
internal fun birthdayText(iso: String): String {
    val year = iso.take(4)
    if (year.isEmpty()) return ""
    return "${iso.substring(5, 7)}/${iso.substring(8, 10)}/$year"
}

internal fun ageText(iso: String): String {
    val year = iso.take(4).toIntOrNull() ?: return ""
    val age = Date().getFullYear() - year
    return tr("年龄：$age 岁", "Age: $age")
}

/** `MM/DD/YYYY` of a due date derived from the id, so the example is stable between visits. */
internal fun dueDate(id: Int): String {
    val year = 2030 + (id * 3) % 5
    return "${twoDigits((id * 5) % 12 + 1)}/${twoDigits((id * 7) % 27 + 1)}/$year"
}

/** `€1 234` of an amount derived from the id. */
internal fun euroAmount(id: Int): String {
    val amount = (id * 1234) % 10000
    val thousands = amount / 1000
    val rest = amount % 1000
    return if (thousands == 0) "€$amount" else "€$thousands ${pad3(rest)}"
}

private fun pad3(value: Int): String = if (value < 100) "0${twoDigits(value)}" else "$value"

internal fun newGrid(height: String = "18rem"): GridElement =
    createGrid().apply {
        // Grid defaults to `height: 100%`; inside an auto-height container it needs a fixed height.
        style.setProperty("height", height)
    }

/** Header content with a trailing info icon, as used by the header renderer example. */
internal fun headerWithIcon(label: String, icon: String, title: String): HTMLElement =
    row(span(label), createIcon { this.icon = icon }.withTheme("small")).apply {
        setAttribute("title", title)
    }

// endregion

private fun buildGridPage(meta: PageMeta): PageView {
    val view = page(meta)
    appendBasicGrid(view)
    appendGridColumns(view)
    appendGridDataBinding(view)
    appendGridSorting(view)
    appendGridFeatures(view)
    return view
}

private fun appendBasicGrid(view: PageView) {
    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "列用 path 绑定 items 里的字段；选择列把选中项写进 selectedItems。",
                "Columns bind fields of items through path; the selection column writes selected rows " +
                    "into selectedItems.",
            ),
        )
    val log = EventLog(6)
    val grid = newGrid()
    grid.dataset["test"] = "grid-basic"
    grid.fill(people(8))
    grid.append(
        createGridSelectionColumn(),
        createGridColumn {
            header = tr("姓名", "Name")
            path = "displayName"
        },
        createGridColumn {
            header = tr("职务", "Role")
            path = "profession"
        },
        createGridColumn {
            header = tr("邮箱", "Email")
            path = "email"
        },
    )
    val selection = Readout(tr("已选 0 行", "0 rows selected"))
    grid.listen(GridEvents.selectedItemsChanged) {
        selection.set(tr("已选 ${grid.selectedItems.size} 行", "${grid.selectedItems.size} rows selected"))
    }
    // `cell-activate` only fires for user interaction (double click / Enter), so the log starts
    // empty instead of recording the events the grid fires while it initialises.
    grid.listen(GridEvents.cellActivate) { log.add("cell-activate") }
    basic.body.append(example(null, column(grid, row(selection.root), log.root)))
    view.body.append(basic.root)
}

private fun appendGridColumns(view: PageView) {
    val columns =
        section(
            tr("列", "Columns"),
            tr(
                "列宽、对齐、冻结、分组、表头表尾渲染与可见性都由列元素自身的属性控制。",
                "Width, alignment, freezing, grouping, header and footer rendering and visibility are " +
                    "all controlled by properties of the column element.",
            ),
        )

    // Column width: explicit width, auto width and flex-grow.
    val widths = newGrid("16rem")
    widths.fill(people(6))
    widths.append(
        createGridSelectionColumn(),
        createGridColumn {
            header = tr("名", "First name")
            path = "firstName"
            width = "7rem"
            flexGrow = 0.0
        },
        createGridColumn {
            header = tr("职业", "Profession")
            path = "profession"
            autoWidth = true
            flexGrow = 0.0
        },
        createGridColumn {
            header = tr("邮箱", "Email")
            path = "email"
        },
        createGridColumn {
            header = tr("订阅", "Has Sub")
            width = "6rem"
            flexGrow = 0.0
            renderer = { root, _, model -> root.textContent = if (itemFlag(model.item, "subscriber")) tr("是", "Yes") else tr("否", "No") }
        },
    )
    columns.body.append(
        example(
            tr("列宽", "Column width") + " · width / auto-width / flex-grow",
            widths,
        ),
    )

    // Column alignment: `text-align="end"` on the column plus tabular numbers.
    val aligned = newGrid("14rem")
    aligned.fill(people(5))
    aligned.append(
        createGridColumn {
            header = tr("姓名", "Name")
            path = "displayName"
        },
        createGridColumn {
            header = tr("到期日", "Due")
            renderer = { root, _, model -> root.textContent = dueDate(itemId(model.item)) }
        },
        createGridColumn {
            header = tr("金额", "Amount")
            textAlign = TextAlign.end
            renderer = { root, _, model ->
                root.append(span(euroAmount(itemId(model.item))).apply {
                    style.setProperty("font-variant-numeric", "tabular-nums")
                })
            }
        },
    )
    columns.body.append(
        example(
            tr(
                "列对齐 · 日期与右对齐的金额",
                "Column alignment · dates and right-aligned amounts",
            ),
            aligned,
        ),
    )

    // Column freezing: a frozen first column and a frozen-to-end action column.
    val frozenGrid = newGrid("14rem")
    frozenGrid.fill(people(6))
    frozenGrid.append(
        createGridColumn {
            header = tr("姓名", "Name")
            frozen = true
            autoWidth = true
            flexGrow = 0.0
            renderer = { root, _, model -> root.textContent = "${itemText(model.item, "firstName")} ${itemText(model.item, "lastName")}" }
        },
        createGridColumn {
            path = "email"
            autoWidth = true
        },
        createGridColumn {
            header = tr("电话", "Phone")
            path = "address.phone"
            autoWidth = true
        },
        createGridColumn {
            path = "profession"
            autoWidth = true
        },
        createGridColumn {
            path = "address.street"
            autoWidth = true
        },
        createGridColumn {
            frozenToEnd = true
            autoWidth = true
            flexGrow = 0.0
            renderer = { root, _, _ -> root.append(createButton { textContent = tr("编辑", "Edit") }.withTheme("tertiary-inline")) }
        },
    )
    columns.body.append(
        example(
            tr(
                "列冻结 · 首列与末列固定在左右两侧",
                "Column freezing · first and last column stay pinned",
            ),
            frozenGrid,
        ),
    )

    // Column grouping.
    val grouped = newGrid("16rem")
    grouped.fill(people(6))
    grouped.append(
        createGridColumnGroup {
            header = tr("姓名", "Name")
        }.apply {
            append(
                createGridColumn { path = "firstName" },
                createGridColumn { path = "lastName" },
            )
        },
        createGridColumnGroup {
            header = tr("地址", "Address")
        }.apply {
            append(
                createGridColumn { path = "address.street" },
                createGridColumn { path = "address.city" },
                createGridColumn { path = "address.zip" },
                createGridColumn { path = "address.state" },
            )
        },
    )
    columns.body.append(example(tr("列分组", "Column grouping"), grouped))

    // Custom header and footer rendering.
    val headed = newGrid("16rem")
    headed.fill(people(6))
    headed.append(
        createGridColumn {
            header = tr("姓名", "Name")
            path = "displayName"
            footerRenderer = { root, _ -> root.append(span(tr("共 200 位成员", "200 total members"))) }
        },
        createGridColumn {
            headerRenderer = { root, _ ->
                root.append(headerWithIcon(tr("订阅", "Subscriber"), "vaadin:info-circle", tr("订阅者是付费客户", "Subscribers are paying customers")))
            }
            renderer = { root, _, model -> root.textContent = if (itemFlag(model.item, "subscriber")) tr("是", "Yes") else tr("否", "No") }
            footerRenderer = { root, _ -> root.append(span(tr("102 位订阅者", "102 subscribers"))) }
        },
        createGridColumn {
            headerRenderer = { root, _ ->
                root.append(
                    headerWithIcon(
                        tr("会员等级", "Membership"),
                        "vaadin:info-circle",
                        tr("会员等级决定可用的功能", "Membership levels determine which features are available"),
                    ),
                )
            }
            path = "membership"
            footerRenderer = { root, _ -> root.append(span(tr("103 普通、71 高级、66 VIP", "103 regular, 71 premium, 66 VIP"))) }
        },
    )
    columns.body.append(example(tr("表头与表尾", "Column headers and footers"), headed))

    // Column reordering and resizing.
    val rearrange = newGrid("16rem")
    rearrange.fill(people(6))
    rearrange.columnReorderingAllowed = true
    rearrange.append(
        createGridColumnGroup {
            header = tr("姓名", "Name")
        }.apply {
            append(
                createGridColumn { path = "firstName"; resizable = true },
                createGridColumn { path = "lastName"; resizable = true },
            )
        },
        createGridColumnGroup {
            header = tr("地址", "Address")
        }.apply {
            append(
                createGridColumn { path = "address.street"; resizable = true },
                createGridColumn { path = "address.city"; resizable = true },
                createGridColumn { path = "address.zip"; resizable = true },
                createGridColumn { path = "address.state"; resizable = true },
            )
        },
    )
    columns.body.append(
        example(
            tr(
                "列重排与调整大小 · 拖动表头换位，拖动列边界改宽",
                "Reordering and resizing · drag a header to move, drag the edge to resize",
            ),
            rearrange,
        ),
    )

    // Column visibility: the docs put the toggles in a popover; checkboxes keep it inline.
    val visibility = newGrid("14rem")
    visibility.fill(people(6))
    val emailColumn = createGridColumn { header = tr("邮箱", "Email"); path = "email" }
    val professionColumn = createGridColumn { header = tr("职业", "Profession"); path = "profession" }
    val streetColumn = createGridColumn { header = tr("街道", "Street"); path = "address.street" }
    visibility.append(
        createGridColumn { header = tr("姓名", "Name"); path = "displayName" },
        emailColumn,
        professionColumn,
        streetColumn,
    )
    val toggles =
        row(
            createCheckbox { label = tr("邮箱", "Email"); checked = true }.apply {
                listen(CheckboxEvents.checkedChanged) { emailColumn.hiddenProperty = !checked }
            },
            createCheckbox { label = tr("职业", "Profession"); checked = true }.apply {
                listen(CheckboxEvents.checkedChanged) { professionColumn.hiddenProperty = !checked }
            },
            createCheckbox { label = tr("街道", "Street"); checked = true }.apply {
                listen(CheckboxEvents.checkedChanged) { streetColumn.hiddenProperty = !checked }
            },
        )
    columns.body.append(
        example(
            tr(
                "列可见性 · hidden 隐藏整列",
                "Column visibility · hidden removes the whole column",
            ),
            column(visibility, toggles),
        ),
    )

    view.body.append(columns.root)
}

private fun appendGridDataBinding(view: PageView) {
    val binding =
        section(
            tr("绑定数据", "Data binding"),
            tr(
                "items 接受本地数组；dataProvider 按 page / pageSize / sortOrders 取数，适合大数据集。",
                "items takes a local array; dataProvider loads page by page and receives page / pageSize / " +
                    "sortOrders, which suits larger data sets.",
            ),
        )

    // Lazy loading through a data provider, with a search term that clears the cache.
    val providerGrid = newGrid("16rem")
    val searchField = createTextField { placeholder = tr("搜索", "Search") }
    searchField.style.setProperty("width", "50%")
    searchField.append(createIcon { icon = "vaadin:search"; slot = "prefix" })
    var searchTerm = ""
    val sourcePeople = people(24)
    providerGrid.dataProvider = { params, callback ->
        var result = filterPeople(sourcePeople, searchTerm)
        val sort = params.sortOrders.firstOrNull()
        result = sortPeople(result, sort?.path, sort?.direction)
        val offset = (params.page * params.pageSize).toInt()
        val pageItems = result.drop(offset).take(params.pageSize.toInt())
        callback(gridItemsNullable(pageItems), result.size.toDouble())
    }
    providerGrid.append(
        createGridSortColumn { header = tr("姓名", "Name"); path = "displayName" },
        createGridSortColumn { header = tr("职业", "Profession"); path = "profession" },
    )
    searchField.listen(TextFieldEvents.valueChanged) {
        searchTerm = searchField.value.trim()
        providerGrid.clearCache()
    }
    binding.body.append(
        example(
            tr(
                "自定义数据提供者 · 分页、排序与搜索都在 dataProvider 里完成",
                "Custom data provider · paging, sorting and searching happen inside the dataProvider",
            ),
            column(row(searchField), providerGrid),
        ),
    )

    // External filtering: the items array is replaced on every keystroke.
    val externalGrid = newGrid("16rem")
    val externalSearch = createTextField { placeholder = tr("搜索", "Search") }
    externalSearch.style.setProperty("width", "50%")
    externalSearch.append(createIcon { icon = "vaadin:search"; slot = "prefix" })
    externalGrid.fill(sourcePeople)
    externalGrid.append(
        createGridColumn {
            header = tr("姓名", "Name")
            width = "230px"
            flexGrow = 0.0
            renderer = { root, _, model -> root.append(personCell(model.item)) }
        },
        createGridColumn { header = tr("邮箱", "Email"); path = "email" },
        createGridColumn { header = tr("职业", "Profession"); path = "profession" },
    )
    externalSearch.listen(TextFieldEvents.valueChanged) {
        externalGrid.fill(filterPeople(sourcePeople, externalSearch.value.trim()))
    }
    binding.body.append(
        example(
            tr(
                "外部筛选 · 输入框替换整个 items 数组",
                "External filtering · the field replaces the whole items array",
            ),
            column(row(externalSearch), externalGrid),
        ),
    )

    // Filtering by column: the grid filters its own array items.
    val filterGrid = newGrid("16rem")
    filterGrid.fill(sourcePeople)
    filterGrid.append(
        createGridFilterColumn {
            header = tr("姓名", "Name")
            path = "displayName"
            width = "230px"
            flexGrow = 0.0
        },
        createGridFilterColumn { path = "email" },
        createGridFilterColumn { path = "profession" },
    )
    binding.body.append(
        example(
            tr("列内筛选 · filter-column 自带筛选输入框", "Column filtering · filter-column adds its own field"),
            filterGrid,
        ),
    )

    // Manual pagination: a page-size select and navigation buttons.
    val allPeople = people(18)
    val pageSizeSelect =
        createSelect {
            items =
                jsObject(
                    """[{"label":"5","value":"5"},{"label":"10","value":"10"},{"label":"15","value":"15"}]""",
                ).unsafeCast<Array<SelectItemData>>()
            value = "5"
        }
    pageSizeSelect.style.setProperty("width", "5rem")
    var pageIndex = 0
    var pageSize = 5
    val pagedGrid = newGrid().apply { allRowsVisible = true }
    pagedGrid.append(
        createGridColumn {
            header = tr("姓名", "Name")
            width = "230px"
            flexGrow = 0.0
            renderer = { root, _, model -> root.append(personCell(model.item)) }
        },
        createGridColumn { path = "email" },
        createGridColumn { path = "profession" },
    )
    val pageReadout = Readout("")
    val firstButton = createButton { textContent = "«" }.withTheme("small icon")
    val previousButton = createButton { textContent = "‹" }.withTheme("small icon")
    val nextButton = createButton { textContent = "›" }.withTheme("small icon")
    val lastButton = createButton { textContent = "»" }.withTheme("small icon")

    fun slicePage() {
        val pageCount = maxOf(1, (allPeople.size + pageSize - 1) / pageSize)
        pageIndex = pageIndex.coerceIn(0, pageCount - 1)
        pagedGrid.fill(allPeople.drop(pageIndex * pageSize).take(pageSize))
        pageReadout.set(tr("第 ${pageIndex + 1} / $pageCount 页", "Page ${pageIndex + 1} of $pageCount"))
        firstButton.disabled = pageIndex == 0
        previousButton.disabled = pageIndex == 0
        nextButton.disabled = pageIndex >= pageCount - 1
        lastButton.disabled = pageIndex >= pageCount - 1
    }

    firstButton.clickEvent.addHandler { pageIndex = 0; slicePage() }
    previousButton.clickEvent.addHandler { pageIndex -= 1; slicePage() }
    nextButton.clickEvent.addHandler { pageIndex += 1; slicePage() }
    lastButton.clickEvent.addHandler { pageIndex = Int.MAX_VALUE; slicePage() }
    pageSizeSelect.listen(SelectEvents.valueChanged) {
        pageSize = pageSizeSelect.value.toIntOrNull() ?: 5
        pageIndex = 0
        slicePage()
    }
    slicePage()
    binding.body.append(
        example(
            tr(
                "手动分页 · all-rows-visible 配合按钮切换页码",
                "Manual pagination · all-rows-visible with buttons that switch the page",
            ),
            column(
                pagedGrid,
                row(span(tr("每页", "Page size")), pageSizeSelect, firstButton, previousButton, pageReadout.root, nextButton, lastButton),
            ),
        ),
    )

    // Dynamic height with a grid that grows with its invited people.
    val invited = mutableListOf<Person>()
    val invitedCombo =
        createComboBox {
            items = gridItems(people(8))
            itemLabelPath = "displayName"
            itemValuePath = "id"
        }
    invitedCombo.style.setProperty("flex", "1")
    val inviteGrid = newGrid().apply { allRowsVisible = true }
    inviteGrid.style.setProperty("margin-top", "var(--lumo-space-s)")
    val emptyInvite =
        paragraph(
            tr("尚未发出邀请", "No invitation has been sent"),
            "grid-empty-message",
        )
    inviteGrid.append(
        createGridColumn { header = tr("姓名", "Name"); path = "displayName"; autoWidth = true },
        createGridColumn { path = "email" },
        createGridColumn { path = "address.phone" },
        createGridColumn {
            header = tr("操作", "Manage")
            renderer = { root, _, model ->
                val personId = itemId(model.item)
                root.append(
                    createButton { textContent = "" }.withTheme("error tertiary icon").apply {
                        setAttribute("aria-label", tr("移除", "Remove"))
                        append(createIcon { icon = "vaadin:trash" })
                        clickEvent.addHandler {
                            invited.removeAll { it.id == personId }
                            inviteGrid.fill(invited)
                            emptyInvite.setHidden(invited.isNotEmpty())
                            inviteGrid.setHidden(invited.isEmpty())
                        }
                    },
                )
            }
        },
    )
    inviteGrid.setHidden(true)
    val inviteButton = createButton { textContent = tr("发送邀请", "Send invite") }.withTheme("primary")
    inviteButton.clickEvent.addHandler {
        val id = invitedCombo.value.toIntOrNull() ?: return@addHandler
        val person = people(8).firstOrNull { it.id == id } ?: return@addHandler
        if (invited.none { it.id == id }) {
            invited += person
            inviteGrid.fill(invited)
            emptyInvite.setHidden(true)
            inviteGrid.setHidden(false)
            invitedCombo.value = ""
        }
    }
    binding.body.append(
        example(
            tr(
                "动态高度 · all-rows-visible 让表格随行数增高",
                "Dynamic height · all-rows-visible lets the grid grow with its rows",
            ),
            column(row(invitedCombo, inviteButton), emptyInvite, inviteGrid),
        ),
    )

    view.body.append(binding.root)
}

private fun appendGridSorting(view: PageView) {
    val sorting =
        section(
            tr("排序", "Sorting"),
            tr(
                "sort-column 点击表头在升序 / 降序 / 无序之间切换；multi-sort 支持多列排序。",
                "Clicking a sort-column header cycles ascending / descending / unsorted; multi-sort sorts " +
                    "by several columns.",
            ),
        )

    val sorted = newGrid()
    sorted.dataset["test"] = "grid-sorted"
    sorted.fill(people(8))
    sorted.append(
        createGridSortColumn {
            header = tr("姓名", "Name")
            path = "displayName"
            direction = GridSorterDirections.asc
        },
        createGridSortColumn { path = "email" },
        createGridSortColumn { path = "profession" },
        createGridSortColumn { header = tr("生日", "Birthday"); path = "birthday" },
    )
    sorting.body.append(example(tr("基本排序", "Sorting"), sorted))

    val multi = newGrid("16rem")
    multi.fill(people(8))
    multi.multiSort = true
    multi.multiSortPriority = GridMultiSortPriorities.append
    multi.append(
        createGridSortColumn { header = tr("编号", "Id"); path = "id" },
        createGridSortColumn { header = tr("姓名", "Name"); path = "displayName" },
        createGridSortColumn { path = "profession" },
        createGridSortColumn { header = tr("生日", "Birthday"); path = "birthday" },
    )
    sorting.body.append(
        example(
            tr(
                "多列排序 · 按住 Shift 点击表头追加排序条件",
                "Multi-column sorting · Shift-click a header to append a sort order",
            ),
            multi,
        ),
    )

    val programmatic = newGrid("16rem")
    programmatic.fill(people(8))
    val programmaticColumn = createGridSortColumn { header = tr("姓名", "Name"); path = "displayName" }
    programmatic.append(
        programmaticColumn,
        createGridSortColumn { header = tr("职业", "Profession"); path = "profession" },
    )
    val directionReadout = Readout(tr("排序方向：未排序", "Sort direction: none"))
    sorting.body.append(
        example(
            tr("程序化排序 · 直接设置 direction", "Programmatic sorting · set direction from code"),
            column(
                programmatic,
                row(
                    createButton { textContent = tr("升序", "Ascending") }.withTheme("tertiary").apply {
                        clickEvent.addHandler {
                            programmaticColumn.direction = GridSorterDirections.asc
                            directionReadout.set(tr("排序方向：升序", "Sort direction: ascending"))
                        }
                    },
                    createButton { textContent = tr("降序", "Descending") }.withTheme("tertiary").apply {
                        clickEvent.addHandler {
                            programmaticColumn.direction = GridSorterDirections.desc
                            directionReadout.set(tr("排序方向：降序", "Sort direction: descending"))
                        }
                    },
                    createButton { textContent = tr("清除", "Clear") }.withTheme("tertiary").apply {
                        clickEvent.addHandler {
                            programmaticColumn.direction = null
                            directionReadout.set(tr("排序方向：未排序", "Sort direction: none"))
                        }
                    },
                ),
                directionReadout.root,
            ),
        ),
    )

    view.body.append(sorting.root)
}

// region Shared helpers for the features file

internal fun filterPeople(list: List<Person>, term: String): List<Person> {
    if (term.isEmpty()) return list
    return list.filter { person ->
        person.displayName.contains(term, ignoreCase = true) ||
            person.email.contains(term, ignoreCase = true) ||
            person.profession.contains(term, ignoreCase = true)
    }
}

internal fun sortPeople(list: List<Person>, path: String?, direction: GridSorterDirection?): List<Person> {
    if (path == null || direction == null) return list
    val comparator =
        when (path) {
            "displayName" -> compareBy<Person> { it.displayName }
            "profession" -> compareBy { it.profession }
            "email" -> compareBy { it.email }
            "birthday" -> compareBy { it.birthday }
            "id" -> compareBy { it.id }
            else -> return list
        }
    return if (direction == GridSorterDirections.desc) list.sortedWith(comparator.reversed()) else list.sortedWith(comparator)
}

/** Avatar + name + email, the two-line cell the docs render through a Lit renderer. */
internal fun personCell(item: JsAny?): HTMLElement =
    div("person-item").apply {
        append(
            createAvatar {
                name = "${itemText(item, "firstName")} ${itemText(item, "lastName")}"
                img = itemText(item, "pictureUrl")
            },
            div("person-item__text").apply {
                append(span(itemText(item, "displayName"), "person-item__name"), span(itemText(item, "email"), "person-item__email"))
            },
        )
    }

// endregion
