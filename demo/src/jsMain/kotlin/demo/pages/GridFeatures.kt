package demo.pages

import demo.tr
import demo.ui.EventLog
import demo.ui.PageView
import demo.ui.Readout
import demo.ui.column
import demo.ui.div
import demo.ui.example
import demo.ui.grid
import demo.ui.jsArray
import demo.ui.listen
import demo.ui.paragraph
import demo.ui.row
import demo.ui.section
import demo.ui.span
import demo.ui.withTheme
import js.objects.unsafeJso
import kotlin.js.JsAny
import kotlin.js.js
import vaadin.badge.createBadge
import vaadin.button.createButton
import vaadin.contextmenu.ContextMenuEvents
import vaadin.contextmenu.ContextMenuItemData
import vaadin.contextmenu.HTMLElementOrString
import vaadin.contextmenu.createContextMenu
import vaadin.grid.ColumnRenderings
import vaadin.grid.GridDropModes
import vaadin.grid.GridColumnElement
import vaadin.grid.GridElement
import vaadin.grid.GridEventSections
import vaadin.grid.GridEvents
import vaadin.grid.createGridColumn
import vaadin.grid.createGridSelectionColumn
import vaadin.icon.createIcon
import vaadin.textarea.createTextArea
import vaadin.textfield.createTextField
import vaadin.tooltip.createTooltip
import web.dom.clickEvent
import web.dom.contextMenuEvent
import web.events.Event
import web.events.addHandler
import web.html.HTMLElement
import web.pointer.PointerEvent

internal fun appendGridFeatures(view: PageView) {
    appendGridRowDetails(view)
    appendGridEmptyState(view)
    appendGridMenus(view)
    appendGridFocusAndScroll(view)
    appendGridDragAndDrop(view)
    appendGridSelection(view)
    appendGridStyling(view)
    appendGridLazyColumns(view)
}

// region Event detail readers

private fun detailValue(event: Event): JsAny? =
    js("(event) => event.detail == null || event.detail.value === undefined ? null : event.detail.value")(event)

private fun detailField(event: Event, key: String): JsAny? =
    js("(event, key) => event.detail == null ? null : event.detail[key]")(event, key)

private fun detailString(event: Event, key: String): String =
    js("(event, key) => event.detail == null || event.detail[key] == null ? '' : String(event.detail[key])")(event, key) as String

private fun detailFlag(event: Event, key: String): Boolean =
    js("(event, key) => !!(event.detail && event.detail[key])")(event, key) as Boolean

private fun detailItems(event: Event, key: String): Array<JsAny> =
    js("(event, key) => event.detail && Array.isArray(event.detail[key]) ? event.detail[key] : []")(event, key) as Array<JsAny>

// endregion

private fun personDetails(item: JsAny?): HTMLElement =
    div("details-form").apply {
        append(
            createTextField { label = tr("邮箱地址", "Email address"); value = itemPathText(item, "email"); readonly = true },
            createTextField { label = tr("电话", "Phone number"); value = itemPathText(item, "address.phone"); readonly = true },
            createTextField { label = tr("街道", "Street address"); value = itemPathText(item, "address.street"); readonly = true },
            createTextField { label = tr("邮编", "ZIP code"); value = itemPathText(item, "address.zip"); readonly = true },
            createTextField { label = tr("城市", "City"); value = itemPathText(item, "address.city"); readonly = true },
            createTextField { label = tr("州", "State"); value = itemPathText(item, "address.state"); readonly = true },
        )
    }

private fun appendGridRowDetails(view: PageView) {
    val details =
        section(
            tr("行详情", "Item details"),
            tr(
                "rowDetailsRenderer 渲染展开区；detailsOpenedItems 决定哪一行展开。",
                "rowDetailsRenderer renders the expanded area; detailsOpenedItems decides which row is open.",
            ),
        )

    // Details open on the active item.
    val clickGrid = newGrid()
    clickGrid.fill(people(5))
    clickGrid.withTheme("row-stripes")
    clickGrid.append(
        createGridColumn { header = tr("姓名", "Name"); path = "displayName" },
        createGridColumn { path = "profession" },
    )
    clickGrid.rowDetailsRenderer = { root, _, model -> root.append(personDetails(model.item)) }
    clickGrid.listen(GridEvents.activeItemChanged) { event ->
        val item = detailValue(event)
        if (item == null) {
            clickGrid.detailsOpenedItems = emptyArray()
        } else {
            clickGrid.detailsOpenedItems = arrayOf(item)
        }
    }
    details.body.append(
        example(
            tr("点击行展开详情", "Details open for the active row"),
            clickGrid,
        ),
    )

    // A frozen column with a toggle button.
    val toggleGrid = newGrid()
    toggleGrid.fill(people(5))
    toggleGrid.rowDetailsRenderer = { root, _, model -> root.append(personDetails(model.item)) }
    val openedItems = mutableListOf<JsAny>()
    toggleGrid.append(
        createGridColumn {
            width = "80px"
            flexGrow = 0.0
            frozen = true
            renderer = { root, _, model ->
                val open = model.detailsOpened == true
                root.append(
                    createButton { textContent = "" }.withTheme("tertiary icon").apply {
                        setAttribute("aria-label", tr("切换详情", "Toggle details"))
                        setAttribute("aria-expanded", open.toString())
                        append(createIcon { icon = if (open) "vaadin:angle-down" else "vaadin:angle-right" })
                        clickEvent.addHandler {
                            val item = model.item ?: return@addHandler
                            if (open) openedItems.remove(item) else openedItems.add(item)
                            toggleGrid.detailsOpenedItems = openedItems.toTypedArray()
                        }
                    },
                )
            }
        },
        createGridColumn { header = tr("姓名", "Name"); path = "displayName" },
        createGridColumn { path = "profession" },
    )
    details.body.append(
        example(
            tr(
                "详情切换按钮 · 冻结列里逐行控制",
                "Details toggle · a frozen column controls each row",
            ),
            toggleGrid,
        ),
    )

    view.body.append(details.root)
}

private fun appendGridEmptyState(view: PageView) {
    val empty =
        section(
            tr("空状态", "Empty state"),
            tr(
                "没有 items 时显示 empty-state 插槽的内容。",
                "With no items the grid shows the content of the empty-state slot.",
            ),
        )
    val grid = newGrid("10rem")
    grid.append(
        createGridColumn { path = "firstName" },
        createGridColumn { path = "lastName" },
        createGridColumn { path = "email" },
        createGridColumn { path = "profession" },
        span(tr("没有找到员工。", "No employees found.")).apply { slot = "empty-state" },
    )
    empty.body.append(example(null, grid))
    view.body.append(empty.root)
}

private fun menuItems(vararg labels: String): Array<ContextMenuItemData> =
    labels.map { unsafeJso<ContextMenuItemData> { text = it } }.toTypedArray()

private fun appendGridMenus(view: PageView) {
    val menus =
        section(
            tr("右键菜单与工具提示", "Context menu and tooltips"),
            tr(
                "表格单元格也能挂上下文菜单与 tooltip：先用 getEventContext 判断点在哪个单元格上。",
                "Cells can carry context menus and tooltips as well; getEventContext tells which cell was hit.",
            ),
        )

    val menuGrid = newGrid("16rem")
    menuGrid.fill(people(6))
    menuGrid.append(
        createGridColumn { path = "firstName" },
        createGridColumn { path = "lastName" },
        createGridColumn { path = "email" },
        createGridColumn { path = "profession" },
    )
    val menu = createContextMenu()
    val copyReadout = Readout(tr("尚未选择菜单项", "No menu item selected yet"))
    var personName = ""
    menu.listen(ContextMenuEvents.itemSelected) { event ->
        copyReadout.set(tr("已选择：", "Selected: "), "$personName · ${detailField(event, "value")?.let { itemText(it, "text") } ?: ""}")
    }
    // The menu is the parent of the grid, so stopping propagation for header cells keeps the menu
    // closed there; setting the items in the same handler makes sure they match the clicked row.
    menuGrid.contextMenuEvent.addHandler { event: PointerEvent ->
        val context = menuGrid.getEventContext(event)
        if (context.section != GridEventSections.body || context.item == null) {
            event.stopPropagation()
            return@addHandler
        }
        personName = itemText(context.item, "displayName")
        menu.items = menuItems(tr("编辑", "Edit"), tr("删除", "Delete"), tr("发邮件", "Email"), tr("打电话", "Call"))
    }
    menu.append(menuGrid)
    menus.body.append(
        example(
            tr(
                "行上的右键菜单 · 表头不弹出菜单",
                "Context menu on rows · the header does not open it",
            ),
            column(menu, copyReadout.root),
        ),
    )

    val tooltipGrid = newGrid("16rem")
    tooltipGrid.fill(people(6))
    tooltipGrid.append(
        createGridColumn { path = "firstName" },
        createGridColumn { path = "lastName" },
        createGridColumn { header = tr("生日", "Birthday"); path = "birthday"; renderer = { root, _, model -> root.textContent = birthdayText(itemText(model.item, "birthday")) } },
        createGridColumn {
            header = tr("状态", "Status")
            path = "status"
            renderer = { root, _, model ->
                val status = itemText(model.item, "status")
                root.append(
                    createBadge { textContent = status }.withTheme(if (status == "Available") "success" else "error"),
                )
            }
        },
    )
    val tooltip = createTooltip().apply { slot = "tooltip" }
    // The generator runs for every cell the pointer enters; the column path decides the text.
    tooltip.generator = { context ->
        val path = js("(context) => context && context.column ? String(context.column.path) : ''")(context) as String
        val item = js("(context) => context && context.item ? context.item : null")(context)
        when (path) {
            "birthday" -> ageText(itemPathText(item, "birthday"))
            "status" -> itemPathText(item, "status")
            else -> itemText(item, "firstName")
        }
    }
    tooltipGrid.append(tooltip)
    menus.body.append(
        example(
            tr(
                "工具提示生成器 · 按列决定 tooltip 文本",
                "Tooltip generator · the column decides the tooltip text",
            ),
            tooltipGrid,
        ),
    )

    view.body.append(menus.root)
}

private fun appendGridFocusAndScroll(view: PageView) {
    val focusSection =
        section(
            tr("单元格焦点与滚动", "Cell focus and scrolling"),
            tr(
                "cell-focus 配合 getEventContext 读出焦点所在的区块、行与列；scrollToIndex 直接定位到某一行。",
                "cell-focus plus getEventContext report the focused section, row and column; scrollToIndex jumps " +
                    "straight to a row.",
            ),
        )

    val focusGrid = newGrid("16rem")
    focusGrid.fill(people(8))
    focusGrid.append(
        createGridColumn { path = "firstName" },
        createGridColumn { path = "lastName" },
        createGridColumn { path = "email" },
        createGridColumn { path = "profession" },
    )
    val focusInfo =
        createTextArea {
            label = tr("焦点单元格信息", "Cell focus event information")
            readonly = true
        }
    focusInfo.style.setProperty("width", "100%")
    focusInfo.style.setProperty("margin-top", "var(--lumo-space-l)")
    focusGrid.listen(GridEvents.cellFocus) { event ->
        val context = focusGrid.getEventContext(event)
        val section = context.section?.let { js("(section) => String(section)")(it) as String } ?: tr("未知", "Not available")
        val row = context.index?.let { "${it.toInt()}" } ?: tr("未知", "Not available")
        val column = context.column?.path ?: tr("未知", "Not available")
        val name = itemText(context.item, "displayName").ifEmpty { tr("未知", "Not available") }
        focusInfo.value = "Section: $section\nRow: $row\nColumn: $column\nPerson: $name"
    }
    focusSection.body.append(
        example(
            tr(
                "单元格焦点 · 用方向键移动后读出上下文",
                "Cell focus · move with the arrow keys and read the context",
            ),
            column(focusGrid, focusInfo),
        ),
    )

    val scrollGrid = newGrid("14rem")
    val rows = (1..200).map { """{"name": "Row $it"}""" }
    scrollGrid.items = jsArray<JsAny>("[${rows.joinToString(", ")}]")
    scrollGrid.append(createGridColumn { header = tr("名称", "Name"); path = "name" })
    val scrollLog = EventLog(4)
    focusSection.body.append(
        example(
            tr("程序化滚动 · scrollToIndex", "Programmatic scrolling · scrollToIndex"),
            column(
                scrollGrid,
                row(
                    createButton { textContent = tr("跳到第 1 行", "Go to row 1") }.withTheme("tertiary").apply {
                        clickEvent.addHandler {
                            scrollGrid.scrollToIndex(0.0)
                            scrollLog.add("scrollToIndex(0)")
                        }
                    },
                    createButton { textContent = tr("跳到第 50 行", "Go to row 50") }.withTheme("tertiary").apply {
                        clickEvent.addHandler {
                            scrollGrid.scrollToIndex(49.0)
                            scrollLog.add("scrollToIndex(49)")
                        }
                    },
                    createButton { textContent = tr("跳到第 200 行", "Go to row 200") }.withTheme("tertiary").apply {
                        clickEvent.addHandler {
                            scrollGrid.scrollToIndex(199.0)
                            scrollLog.add("scrollToIndex(199)")
                        }
                    },
                ),
                scrollLog.root,
            ),
        ),
    )

    view.body.append(focusSection.root)
}

private fun appendGridDragAndDrop(view: PageView) {
    val drag =
        section(
            tr("拖放", "Drag and drop"),
            tr(
                "rowsDraggable 打开拖拽；dropMode 决定落点，grid-dragstart / grid-drop 里更新 items。" +
                    "真实拖拽需要鼠标序列，这里的行重排、跨表移动与拖拽过滤都按同一套事件实现。",
                "rowsDraggable enables dragging, dropMode decides where a drop lands and grid-dragstart / " +
                    "grid-drop update the items. Real dragging needs mouse gestures; the row reordering, " +
                    "moving between grids and drag filters below all go through the same events.",
            ),
        )

    // Row reordering.
    val reorderItems = people(6).toMutableList()
    val reorderGrid = newGrid("14rem")
    reorderGrid.fill(reorderItems)
    reorderGrid.rowsDraggable = true
    var draggedId = -1
    reorderGrid.append(
        createGridColumn { header = tr("姓名", "Name"); path = "displayName" },
        createGridColumn { path = "profession" },
    )
    reorderGrid.listen(GridEvents.gridDragstart) { event ->
        draggedId = itemId(detailItems(event, "draggedItems").firstOrNull())
        reorderGrid.dropMode = GridDropModes.between
    }
    reorderGrid.listen(GridEvents.gridDragend) { reorderGrid.dropMode = null }
    reorderGrid.listen(GridEvents.gridDrop) { event ->
        val targetId = itemId(detailField(event, "dropTargetItem"))
        val below = detailString(event, "dropLocation") == "below"
        val draggedPerson = reorderItems.firstOrNull { it.id == draggedId }
        val targetPerson = reorderItems.firstOrNull { it.id == targetId }
        if (draggedPerson != null && targetPerson != null && draggedPerson != targetPerson) {
            reorderItems.remove(draggedPerson)
            reorderItems.add(reorderItems.indexOf(targetPerson) + if (below) 1 else 0, draggedPerson)
            reorderGrid.fill(reorderItems)
        }
        draggedId = -1
        reorderGrid.dropMode = null
    }
    drag.body.append(
        example(
            tr(
                "行重排 · dropMode = between",
                "Row reordering · dropMode = between",
            ),
            reorderGrid,
        ),
    )

    // Moving rows between two grids.
    val panelOne = people(8).take(4).toMutableList()
    val panelTwo = people(8).drop(4).toMutableList()
    var draggedPersonId = -1
    val firstPanel = dragGrid(panelOne, onDragStart = { draggedPersonId = it })
    val secondPanel = dragGrid(panelTwo, onDragStart = { draggedPersonId = it })
    fun moveBetween(source: MutableList<Person>, target: MutableList<Person>) {
        val person = source.firstOrNull { it.id == draggedPersonId } ?: return
        source.remove(person)
        target.add(person)
        firstPanel.fill(panelOne)
        secondPanel.fill(panelTwo)
        draggedPersonId = -1
    }
    firstPanel.listen(GridEvents.gridDrop) { moveBetween(panelTwo, panelOne) }
    secondPanel.listen(GridEvents.gridDrop) { moveBetween(panelOne, panelTwo) }
    drag.body.append(
        example(
            tr(
                "跨表拖拽 · dropMode = on-grid",
                "Dragging between grids · dropMode = on-grid",
            ),
            row(firstPanel, secondPanel),
        ),
    )

    // Drag and drop filters.
    val filterItems = people(6).toMutableList()
    val filterGrid = newGrid("14rem")
    filterGrid.fill(filterItems)
    filterGrid.rowsDraggable = true
    filterGrid.dragFilter = { model -> itemId(model.item) % 2 == 1 }
    filterGrid.dropFilter = { model -> itemFlag(model.item, "subscriber") }
    filterGrid.append(
        createGridColumn { header = tr("姓名", "Name"); path = "displayName" },
        createGridColumn { header = tr("订阅", "Subscriber"); path = "subscriber" },
    )
    drag.body.append(
        example(
            tr(
                "拖拽过滤 · dragFilter 只放行部分行，dropFilter 限制落点",
                "Drag filters · dragFilter allows some rows, dropFilter restricts the drop targets",
            ),
            filterGrid,
        ),
    )

    view.body.append(drag.root)
}

private fun dragGrid(items: MutableList<Person>, onDragStart: (Int) -> Unit): GridElement {
    val grid = newGrid("12rem")
    grid.style.setProperty("width", "20rem")
    grid.fill(items)
    grid.rowsDraggable = true
    grid.append(
        createGridColumn {
            header = tr("全名", "Full name")
            renderer = { root, _, model -> root.textContent = "${itemText(model.item, "firstName")} ${itemText(model.item, "lastName")}" }
        },
        createGridColumn { path = "profession" },
    )
    grid.listen(GridEvents.gridDragstart) { event ->
        grid.dropMode = GridDropModes.onGrid
        onDragStart(itemId(detailItems(event, "draggedItems").firstOrNull()))
    }
    grid.listen(GridEvents.gridDragend) { grid.dropMode = null }
    return grid
}

private fun appendGridSelection(view: PageView) {
    val selection =
        section(
            tr("选择", "Selection"),
            tr(
                "单选用 active-item-changed 维护 selectedItems；多选加选择列；按住 Shift 点击可扩展区间。",
                "Single selection maintains selectedItems from active-item-changed; multi-selection adds the " +
                    "selection column; Shift-click extends a range.",
            ),
        )

    val singleGrid = newGrid("14rem")
    singleGrid.fill(people(6))
    singleGrid.append(
        createGridColumn { path = "firstName" },
        createGridColumn { path = "lastName" },
        createGridColumn { path = "email" },
    )
    val singleReadout = Readout(tr("未选择行", "No row selected"))
    singleGrid.listen(GridEvents.activeItemChanged) { event ->
        val item = detailValue(event)
        if (item == null) {
            singleGrid.selectedItems = emptyArray()
            singleReadout.set(tr("未选择行", "No row selected"))
        } else {
            singleGrid.selectedItems = arrayOf(item)
            singleReadout.set(tr("已选：", "Selected: ") + itemText(item, "displayName"))
        }
    }
    selection.body.append(
        example(
            tr("单选 · 点击行切换", "Single selection · clicking a row toggles it"),
            column(singleGrid, singleReadout.root),
        ),
    )

    val multiGrid = newGrid("14rem")
    multiGrid.fill(people(6))
    multiGrid.append(
        createGridSelectionColumn(),
        createGridColumn { path = "firstName" },
        createGridColumn { path = "lastName" },
        createGridColumn { path = "email" },
    )
    val multiReadout = Readout(tr("多选：0 行", "Multi-select: 0 rows"))
    multiGrid.listen(GridEvents.selectedItemsChanged) {
        multiReadout.set(
            tr("多选：${multiGrid.selectedItems.size} 行：", "Multi-select: ${multiGrid.selectedItems.size} rows: "),
            multiGrid.selectedItems.joinToString(", ") { itemText(it, "displayName") },
        )
    }
    selection.body.append(
        example(
            tr("多选 · 选择列与全选", "Multi-selection · the selection column and select all"),
            column(multiGrid, multiReadout.root),
        ),
    )

    val rangeGrid = newGrid("14rem")
    rangeGrid.itemIdPath = "id"
    rangeGrid.fill(people(8))
    rangeGrid.append(
        createGridSelectionColumn(),
        createGridColumn { path = "firstName" },
        createGridColumn { path = "lastName" },
        createGridColumn { path = "email" },
    )
    val rangeReadout = Readout(tr("区间：0 行", "Range: 0 rows"))
    var anchorId = -1
    val rangeSelected = mutableSetOf<Int>()
    rangeGrid.listen(GridEvents.itemToggle) { event ->
        val toggledId = itemId(detailField(event, "item"))
        val selected = detailFlag(event, "selected")
        val shift = detailFlag(event, "shiftKey")
        if (anchorId < 0) anchorId = toggledId
        if (shift) {
            val anchor = rangeGrid.items?.toList()?.indexOfFirst { itemId(it) == anchorId } ?: -1
            val current = rangeGrid.items?.toList()?.indexOfFirst { itemId(it) == toggledId } ?: -1
            if (anchor >= 0 && current >= 0) {
                val from = minOf(anchor, current)
                val to = maxOf(anchor, current)
                val range = rangeGrid.items?.toList()?.subList(from, to + 1) ?: emptyList()
                range.forEach { item ->
                    if (selected) rangeSelected.add(itemId(item)) else rangeSelected.remove(itemId(item))
                }
                rangeGrid.selectedItems = rangeGrid.items?.filter { rangeSelected.contains(itemId(it)) }?.toTypedArray() ?: emptyArray()
            }
        } else {
            if (selected) rangeSelected.add(toggledId) else rangeSelected.remove(toggledId)
        }
        anchorId = toggledId
        rangeReadout.set(tr("区间：${rangeSelected.size} 行", "Range: ${rangeSelected.size} rows"))
    }
    selection.body.append(
        example(
            tr(
                "区间选择 · Shift + 点击扩展选择范围",
                "Range selection · Shift-click extends the selection",
            ),
            column(rangeGrid, rangeReadout.root),
        ),
    )

    view.body.append(selection.root)
}

private fun themedGrid(theme: String): GridElement {
    val grid = newGrid("11rem")
    grid.fill(people(4))
    grid.withTheme(theme)
    grid.append(
        createGridColumn { header = tr("名", "First name"); path = "firstName" },
        createGridColumn { path = "lastName" },
        createGridColumn { path = "email" },
    )
    return grid
}

private fun appendGridStyling(view: PageView) {
    val styling =
        section(
            tr("样式", "Styling"),
            tr(
                "主题变体改变行高与边框；单元格、表头与表尾还能通过 part 名单独着色。",
                "Theme variants change row density and borders; cells, headers and footers can be coloured " +
                    "individually through part names.",
            ),
        )

    styling.body.append(
        example(
            tr("主题变体", "Theme variants"),
            grid(
                column(paragraph("row-stripes"), themedGrid("row-stripes")),
                column(paragraph("no-row-borders"), themedGrid("no-row-borders")),
                column(paragraph("column-borders"), themedGrid("column-borders")),
                column(paragraph("no-border"), themedGrid("no-border")),
                column(paragraph("compact"), themedGrid("compact")),
            ),
        ),
    )

    val wrapped = newGrid("12rem")
    wrapped.fill(people(5))
    wrapped.withTheme("wrap-cell-content")
    wrapped.append(
        createGridColumn { path = "firstName" },
        createGridColumn { path = "lastName" },
        createGridColumn {
            header = tr("地址", "Address")
            renderer = { root, _, model ->
                root.textContent =
                    listOf("street", "city", "zip", "state").joinToString(" ") { itemPathText(model.item, "address.$it") }
            }
        },
    )
    styling.body.append(
        example(
            tr("换行 · wrap-cell-content", "Wrapping · wrap-cell-content"),
            wrapped,
        ),
    )

    val partGrid = newGrid("14rem")
    partGrid.items = gridItems(people(6), withRating = true)
    partGrid.append(
        createGridColumn { path = "lastName" },
        createGridColumn { path = "profession" },
        createGridColumn {
            header = tr("客户评分（0-10）", "Customer rating (0-10)")
            renderer = { root, _, model -> root.textContent = fixed2(itemRating(model.item)) }
        },
    )
    partGrid.cellPartNameGenerator = { column, model ->
        var parts = ""
        if (headerStartsWith(column, tr("客户评分", "Customer rating"))) parts += " font-weight-bold"
        val rating = itemRating(model.item)
        parts +=
            when {
                rating >= 8.0 -> " high-rating"
                rating <= 4.0 -> " low-rating"
                else -> ""
            }
        parts
    }
    styling.body.append(
        example(
            tr(
                "动态行样式 · cellPartNameGenerator 给高分 / 低分单元格加 part",
                "Dynamic row styling · cellPartNameGenerator adds parts for high and low ratings",
            ),
            partGrid,
        ),
    )

    val headerGrid = newGrid("12rem")
    headerGrid.fill(people(6))
    headerGrid.append(
        createGridColumn { path = "firstName" },
        createGridColumn { path = "lastName" },
        createGridColumn { path = "profession" },
        createGridColumn {
            header = tr("客户评分（0-10）", "Customer rating (0-10)")
            headerPartName = "rating-header"
            footerPartName = "rating-footer"
            renderer = { root, _, model -> root.textContent = fixed2(itemRating(model.item)) }
            footerRenderer = { root, _ -> root.append(span(tr("平均 5.32", "Avg rating: 5.32"))) }
        },
    )
    styling.body.append(
        example(
            tr(
                "表头与表尾样式 · header-part-name / footer-part-name",
                "Header and footer styling · header-part-name / footer-part-name",
            ),
            headerGrid,
        ),
    )

    view.body.append(styling.root)
}

private fun itemRating(item: JsAny?): Double =
    (js("(item) => item == null || item.customerRating == null ? 0 : Number(item.customerRating)")(item) as Double)

/**
 * The grid passes `undefined` as the column for details cells, so a plain `column.header` read
 * throws; the docs example guards with `column.header?.startsWith` for the same reason.
 */
private fun headerStartsWith(column: GridColumnElement, prefix: String): Boolean =
    js("(column, prefix) => !!column && !!column.header && column.header.startsWith(prefix)")(column, prefix) as Boolean

private fun fixed2(value: Double): String = js("(value) => value.toFixed(2)")(value) as String

private fun appendGridLazyColumns(view: PageView) {
    val lazy =
        section(
            tr("懒列渲染", "Lazy column rendering"),
            tr(
                "列很多时把 columnRendering 设为 lazy，只渲染视口内的单元格。",
                "With many columns, columnRendering = lazy renders only the cells inside the viewport.",
            ),
        )
    val grid = newGrid("12rem")
    grid.columnRendering = ColumnRenderings.lazy
    grid.fill(people(6))
    val columns = mutableListOf<HTMLElement>()
    columns +=
        createGridColumn {
            frozen = true
            renderer = { root, _, model -> root.textContent = "Row ${model.index.toInt()}" }
        }
    for (index in 0 until 100) {
        columns +=
            createGridColumn {
                header = "Col $index"
                dataset["index"] = "$index"
                renderer = { root, column, model -> root.textContent = "${column.dataset["index"]} - ${model.index.toInt()}" }
            }
    }
    grid.append(*columns.toTypedArray())
    lazy.body.append(example(null, grid))
    view.body.append(lazy.root)
}
