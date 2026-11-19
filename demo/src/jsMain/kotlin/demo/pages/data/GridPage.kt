package demo.pages.data

import io.github.shijianjs.ktjs.demo.page.row as kitRow
import io.github.shijianjs.ktjs.demo.page.exampleBox
import io.github.shijianjs.ktjs.demo.page.col as kitCol
import io.github.shijianjs.ktjs.demo.page.sectionBlock


import io.github.shijianjs.ktjs.demo.page.DemoPage
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
import demo.tr
import demo.ui.EventLog
import demo.ui.PageMeta
import demo.ui.configureFrame
import demo.ui.Readout
import demo.ui.jsObject
import demo.ui.listen
import kotlin.js.unsafeCast
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.checkbox.CheckboxEvents
import io.github.shijianjs.ktjs.vaadin.checkbox.createCheckbox
import io.github.shijianjs.ktjs.vaadin.combobox.createComboBox
import io.github.shijianjs.ktjs.vaadin.grid.GridEvents
import io.github.shijianjs.ktjs.vaadin.grid.GridMultiSortPriorities
import io.github.shijianjs.ktjs.vaadin.grid.GridSorterDirections
import io.github.shijianjs.ktjs.vaadin.grid.createGridColumn
import io.github.shijianjs.ktjs.vaadin.grid.createGridColumnGroup
import io.github.shijianjs.ktjs.vaadin.grid.createGridFilterColumn
import io.github.shijianjs.ktjs.vaadin.grid.createGridSelectionColumn
import io.github.shijianjs.ktjs.vaadin.grid.createGridSortColumn
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.vaadin.select.SelectEvents
import io.github.shijianjs.ktjs.vaadin.select.SelectItemData
import io.github.shijianjs.ktjs.vaadin.select.createSelect
import io.github.shijianjs.ktjs.vaadin.textfield.TextFieldEvents
import io.github.shijianjs.ktjs.vaadin.textfield.createTextField
import web.cssom.TextAlign
import web.dom.clickEvent
import web.events.addHandler
import demo.ui.MUTED
import io.github.shijianjs.ktjs.demo.page.grid as kitGrid
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachShadowRoot
import io.github.shijianjs.ktjs.demo.page.paragraph
import demo.ui.jsArray
import io.github.shijianjs.ktjs.dom.element.inlineStyle
import web.cssom.Display
import web.cssom.FlexWrap
import web.cssom.Gap
import web.cssom.px
import js.objects.unsafeJso
import kotlin.js.JsAny
import kotlin.js.js
import io.github.shijianjs.ktjs.vaadin.badge.createBadge
import io.github.shijianjs.ktjs.vaadin.contextmenu.ContextMenuEvents
import io.github.shijianjs.ktjs.vaadin.contextmenu.ContextMenuItemData
import io.github.shijianjs.ktjs.vaadin.contextmenu.createContextMenu
import io.github.shijianjs.ktjs.vaadin.grid.ColumnRenderings
import io.github.shijianjs.ktjs.vaadin.grid.GridColumnElement
import io.github.shijianjs.ktjs.vaadin.grid.GridDropModes
import io.github.shijianjs.ktjs.vaadin.grid.GridElement
import io.github.shijianjs.ktjs.vaadin.grid.GridEventSections
import io.github.shijianjs.ktjs.vaadin.textarea.createTextArea
import io.github.shijianjs.ktjs.vaadin.tooltip.createTooltip
import web.dom.contextMenuEvent
import web.events.Event
import web.html.HTMLElement
import web.pointer.PointerEvent

val gridEntry: CatalogEntry =
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
                sourcePath = "pages/data/GridPage.kt",
            ),
        kitPage = { newGridPage() },
        rebuildsOnLanguage = true,
    )

// endregion

internal class GridPage : DemoPage() {

    init {
        configureFrame(gridEntry)
        appendBasicGrid()
        appendGridColumns()
        appendGridDataBinding()
        appendGridSorting()
        appendGridFeatures()
    }


    private fun appendBasicGrid() {
        val basic =
            sectionBlock(
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
        basic.body.append(exampleBox(null, kitCol(grid, kitRow(selection.root), log.root)))
        body.append(basic.root)
    }

    private fun appendGridColumns() {
        val columns =
            sectionBlock(
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
            exampleBox(
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
                    root.append(HtmlTagName.span {
                                    textContent = euroAmount(itemId(model.item))
                                }.apply {
                        style.setProperty("font-variant-numeric", "tabular-nums")
                    })
                }
            },
        )
        columns.body.append(
            exampleBox(
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
                renderer = { root, _, _ -> root.append(createButton { textContent = tr("编辑", "Edit") }.addTheme("tertiary-inline")) }
            },
        )
        columns.body.append(
            exampleBox(
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
        columns.body.append(exampleBox(tr("列分组", "Column grouping"), grouped))

        // Custom header and footer rendering.
        val headed = newGrid("16rem")
        headed.fill(people(6))
        headed.append(
            createGridColumn {
                header = tr("姓名", "Name")
                path = "displayName"
                footerRenderer = { root, _ -> root.append(HtmlTagName.span {
                                                              textContent = tr("共 200 位成员", "200 total members")
                                                          }) }
            },
            createGridColumn {
                headerRenderer = { root, _ ->
                    root.append(newGridHeader(tr("订阅", "Subscriber"), "vaadin:info-circle", tr("订阅者是付费客户", "Subscribers are paying customers")))
                }
                renderer = { root, _, model -> root.textContent = if (itemFlag(model.item, "subscriber")) tr("是", "Yes") else tr("否", "No") }
                footerRenderer = { root, _ -> root.append(HtmlTagName.span {
                                                              textContent = tr("102 位订阅者", "102 subscribers")
                                                          }) }
            },
            createGridColumn {
                headerRenderer = { root, _ ->
                    root.append(
                        newGridHeader(
                            tr("会员等级", "Membership"),
                            "vaadin:info-circle",
                            tr("会员等级决定可用的功能", "Membership levels determine which features are available"),
                        ),
                    )
                }
                path = "membership"
                footerRenderer = { root, _ -> root.append(HtmlTagName.span {
                                                              textContent = tr("103 普通、71 高级、66 VIP", "103 regular, 71 premium, 66 VIP")
                                                          }) }
            },
        )
        columns.body.append(exampleBox(tr("表头与表尾", "Column headers and footers"), headed))

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
            exampleBox(
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
            kitRow(
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
            exampleBox(
                tr(
                    "列可见性 · hidden 隐藏整列",
                    "Column visibility · hidden removes the whole column",
                ),
                kitCol(visibility, toggles),
            ),
        )

        body.append(columns.root)
    }

    private fun appendGridDataBinding() {
        val binding =
            sectionBlock(
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
            exampleBox(
                tr(
                    "自定义数据提供者 · 分页、排序与搜索都在 dataProvider 里完成",
                    "Custom data provider · paging, sorting and searching happen inside the dataProvider",
                ),
                kitCol(kitRow(searchField), providerGrid),
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
                renderer = { root, _, model -> root.append(newPersonCell(model.item)) }
            },
            createGridColumn { header = tr("邮箱", "Email"); path = "email" },
            createGridColumn { header = tr("职业", "Profession"); path = "profession" },
        )
        externalSearch.listen(TextFieldEvents.valueChanged) {
            externalGrid.fill(filterPeople(sourcePeople, externalSearch.value.trim()))
        }
        binding.body.append(
            exampleBox(
                tr(
                    "外部筛选 · 输入框替换整个 items 数组",
                    "External filtering · the field replaces the whole items array",
                ),
                kitCol(kitRow(externalSearch), externalGrid),
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
            exampleBox(
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
                renderer = { root, _, model -> root.append(newPersonCell(model.item)) }
            },
            createGridColumn { path = "email" },
            createGridColumn { path = "profession" },
        )
        val pageReadout = Readout("")
        val firstButton = createButton { textContent = "«" }.addTheme("small icon")
        val previousButton = createButton { textContent = "‹" }.addTheme("small icon")
        val nextButton = createButton { textContent = "›" }.addTheme("small icon")
        val lastButton = createButton { textContent = "»" }.addTheme("small icon")

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
            exampleBox(
                tr(
                    "手动分页 · all-rows-visible 配合按钮切换页码",
                    "Manual pagination · all-rows-visible with buttons that switch the page",
                ),
                kitCol(
                    pagedGrid,
                    kitRow(HtmlTagName.span {
                            textContent = tr("每页", "Page size")
                        }, pageSizeSelect, firstButton, previousButton, pageReadout.root, nextButton, lastButton),
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
            HtmlTagName.p {
                className = MUTED
                textContent = tr("尚未发出邀请", "No invitation has been sent")
            }
        inviteGrid.append(
            createGridColumn { header = tr("姓名", "Name"); path = "displayName"; autoWidth = true },
            createGridColumn { path = "email" },
            createGridColumn { path = "address.phone" },
            createGridColumn {
                header = tr("操作", "Manage")
                renderer = { root, _, model ->
                    val personId = itemId(model.item)
                    root.append(
                        createButton { textContent = "" }.addTheme("error tertiary icon").apply {
                            setAttribute("aria-label", tr("移除", "Remove"))
                            append(createIcon { icon = "vaadin:trash" })
                            clickEvent.addHandler {
                                invited.removeAll { it.id == personId }
                                inviteGrid.fill(invited)
                                emptyInvite.toggleAttribute("hidden", invited.isNotEmpty())
                                inviteGrid.toggleAttribute("hidden", invited.isEmpty())
                            }
                        },
                    )
                }
            },
        )
        inviteGrid.toggleAttribute("hidden", true)
        val inviteButton = createButton { textContent = tr("发送邀请", "Send invite") }.addTheme("primary")
        inviteButton.clickEvent.addHandler {
            val id = invitedCombo.value.toIntOrNull() ?: return@addHandler
            val person = people(8).firstOrNull { it.id == id } ?: return@addHandler
            if (invited.none { it.id == id }) {
                invited += person
                inviteGrid.fill(invited)
                emptyInvite.toggleAttribute("hidden", true)
                inviteGrid.toggleAttribute("hidden", false)
                invitedCombo.value = ""
            }
        }
        binding.body.append(
            exampleBox(
                tr(
                    "动态高度 · all-rows-visible 让表格随行数增高",
                    "Dynamic height · all-rows-visible lets the grid grow with its rows",
                ),
                kitCol(kitRow(invitedCombo, inviteButton), emptyInvite, inviteGrid),
            ),
        )

        body.append(binding.root)
    }

    private fun appendGridSorting() {
        val sorting =
            sectionBlock(
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
        sorting.body.append(exampleBox(tr("基本排序", "Sorting"), sorted))

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
            exampleBox(
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
            exampleBox(
                tr("程序化排序 · 直接设置 direction", "Programmatic sorting · set direction from code"),
                kitCol(
                    programmatic,
                    kitRow(
                        createButton { textContent = tr("升序", "Ascending") }.addTheme("tertiary").apply {
                            clickEvent.addHandler {
                                programmaticColumn.direction = GridSorterDirections.asc
                                directionReadout.set(tr("排序方向：升序", "Sort direction: ascending"))
                            }
                        },
                        createButton { textContent = tr("降序", "Descending") }.addTheme("tertiary").apply {
                            clickEvent.addHandler {
                                programmaticColumn.direction = GridSorterDirections.desc
                                directionReadout.set(tr("排序方向：降序", "Sort direction: descending"))
                            }
                        },
                        createButton { textContent = tr("清除", "Clear") }.addTheme("tertiary").apply {
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

        body.append(sorting.root)
    }


    internal fun appendGridFeatures() {
        appendGridRowDetails()
        appendGridEmptyState()
        appendGridMenus()
        appendGridFocusAndScroll()
        appendGridDragAndDrop()
        appendGridSelection()
        appendGridStyling()
        appendGridLazyColumns()
    }

    private fun appendGridRowDetails() {
        val details =
            sectionBlock(
                tr("行详情", "Item details"),
                tr(
                    "rowDetailsRenderer 渲染展开区；detailsOpenedItems 决定哪一行展开。",
                    "rowDetailsRenderer renders the expanded area; detailsOpenedItems decides which row is open.",
                ),
            )

        // Details open on the active item.
        val clickGrid = newGrid()
        clickGrid.fill(people(5))
        clickGrid.addTheme("row-stripes")
        clickGrid.append(
            createGridColumn { header = tr("姓名", "Name"); path = "displayName" },
            createGridColumn { path = "profession" },
        )
        clickGrid.rowDetailsRenderer = { root, _, model -> root.append(newPersonDetails(model.item)) }
        clickGrid.listen(GridEvents.activeItemChanged) { event ->
            val item = detailValue(event)
            if (item == null) {
                clickGrid.detailsOpenedItems = emptyArray()
            } else {
                clickGrid.detailsOpenedItems = arrayOf(item)
            }
        }
        details.body.append(
            exampleBox(
                tr("点击行展开详情", "Details open for the active row"),
                clickGrid,
            ),
        )

        // A frozen column with a toggle button.
        val toggleGrid = newGrid()
        toggleGrid.fill(people(5))
        toggleGrid.rowDetailsRenderer = { root, _, model -> root.append(newPersonDetails(model.item)) }
        val openedItems = mutableListOf<JsAny>()
        toggleGrid.append(
            createGridColumn {
                width = "80px"
                flexGrow = 0.0
                frozen = true
                renderer = { root, _, model ->
                    val open = model.detailsOpened == true
                    root.append(
                        createButton { textContent = "" }.addTheme("tertiary icon").apply {
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
            exampleBox(
                tr(
                    "详情切换按钮 · 冻结列里逐行控制",
                    "Details toggle · a frozen column controls each row",
                ),
                toggleGrid,
            ),
        )

        body.append(details.root)
    }

    private fun appendGridEmptyState() {
        val empty =
            sectionBlock(
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
            HtmlTagName.span {
                textContent = tr("没有找到员工。", "No employees found.")
            }.apply { slot = "empty-state" },
        )
        empty.body.append(exampleBox(null, grid))
        body.append(empty.root)
    }

    private fun appendGridMenus() {
        val menus =
            sectionBlock(
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
            exampleBox(
                tr(
                    "行上的右键菜单 · 表头不弹出菜单",
                    "Context menu on rows · the header does not open it",
                ),
                kitCol(menu, copyReadout.root),
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
                        createBadge { textContent = status }.addTheme(if (status == "Available") "success" else "error"),
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
            exampleBox(
                tr(
                    "工具提示生成器 · 按列决定 tooltip 文本",
                    "Tooltip generator · the column decides the tooltip text",
                ),
                tooltipGrid,
            ),
        )

        body.append(menus.root)
    }

    private fun appendGridFocusAndScroll() {
        val focusSection =
            sectionBlock(
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
            exampleBox(
                tr(
                    "单元格焦点 · 用方向键移动后读出上下文",
                    "Cell focus · move with the arrow keys and read the context",
                ),
                kitCol(focusGrid, focusInfo),
            ),
        )

        val scrollGrid = newGrid("14rem")
        val rows = (1..200).map { """{"name": "Row $it"}""" }
        scrollGrid.items = jsArray<JsAny>("[${rows.joinToString(", ")}]")
        scrollGrid.append(createGridColumn { header = tr("名称", "Name"); path = "name" })
        val scrollLog = EventLog(4)
        focusSection.body.append(
            exampleBox(
                tr("程序化滚动 · scrollToIndex", "Programmatic scrolling · scrollToIndex"),
                kitCol(
                    scrollGrid,
                    kitRow(
                        createButton { textContent = tr("跳到第 1 行", "Go to row 1") }.addTheme("tertiary").apply {
                            clickEvent.addHandler {
                                scrollGrid.scrollToIndex(0.0)
                                scrollLog.add("scrollToIndex(0)")
                            }
                        },
                        createButton { textContent = tr("跳到第 50 行", "Go to row 50") }.addTheme("tertiary").apply {
                            clickEvent.addHandler {
                                scrollGrid.scrollToIndex(49.0)
                                scrollLog.add("scrollToIndex(49)")
                            }
                        },
                        createButton { textContent = tr("跳到第 200 行", "Go to row 200") }.addTheme("tertiary").apply {
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

        body.append(focusSection.root)
    }

    private fun appendGridDragAndDrop() {
        val drag =
            sectionBlock(
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
            exampleBox(
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
            exampleBox(
                tr(
                    "跨表拖拽 · dropMode = on-grid",
                    "Dragging between grids · dropMode = on-grid",
                ),
                kitRow(firstPanel, secondPanel),
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
            exampleBox(
                tr(
                    "拖拽过滤 · dragFilter 只放行部分行，dropFilter 限制落点",
                    "Drag filters · dragFilter allows some rows, dropFilter restricts the drop targets",
                ),
                filterGrid,
            ),
        )

        body.append(drag.root)
    }

    private fun appendGridSelection() {
        val selection =
            sectionBlock(
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
            exampleBox(
                tr("单选 · 点击行切换", "Single selection · clicking a row toggles it"),
                kitCol(singleGrid, singleReadout.root),
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
            exampleBox(
                tr("多选 · 选择列与全选", "Multi-selection · the selection column and select all"),
                kitCol(multiGrid, multiReadout.root),
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
            exampleBox(
                tr(
                    "区间选择 · Shift + 点击扩展选择范围",
                    "Range selection · Shift-click extends the selection",
                ),
                kitCol(rangeGrid, rangeReadout.root),
            ),
        )

        body.append(selection.root)
    }

    private fun appendGridStyling() {
        val styling =
            sectionBlock(
                tr("样式", "Styling"),
                tr(
                    "主题变体改变行高与边框；单元格、表头与表尾还能通过 part 名单独着色。",
                    "Theme variants change row density and borders; cells, headers and footers can be coloured " +
                        "individually through part names.",
                ),
            )

        styling.body.append(
            exampleBox(
                tr("主题变体", "Theme variants"),
                kitGrid(260, 
                    kitCol(paragraph("row-stripes"), themedGrid("row-stripes")),
                    kitCol(paragraph("no-row-borders"), themedGrid("no-row-borders")),
                    kitCol(paragraph("column-borders"), themedGrid("column-borders")),
                    kitCol(paragraph("no-border"), themedGrid("no-border")),
                    kitCol(paragraph("compact"), themedGrid("compact")),
                ),
            ),
        )

        val wrapped = newGrid("12rem")
        wrapped.fill(people(5))
        wrapped.addTheme("wrap-cell-content")
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
            exampleBox(
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
            exampleBox(
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
                footerRenderer = { root, _ -> root.append(HtmlTagName.span {
                                                              textContent = tr("平均 5.32", "Avg rating: 5.32")
                                                          }) }
            },
        )
        styling.body.append(
            exampleBox(
                tr(
                    "表头与表尾样式 · header-part-name / footer-part-name",
                    "Header and footer styling · header-part-name / footer-part-name",
                ),
                headerGrid,
            ),
        )

        body.append(styling.root)
    }

    private fun appendGridLazyColumns() {
        val lazy =
            sectionBlock(
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
        lazy.body.append(exampleBox(null, grid))
        body.append(lazy.root)
    }

    companion object : CustomElementReference<GridPage>(GridPage::class.js) {
        val NAME: TagName<GridPage> = TagName("vaadin-demo-grid-page")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<vaadin-demo-grid-page>`. */
internal fun newGridPage(): GridPage {
    GridPage.define()
    return GridPage.NAME()
}
