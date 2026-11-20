package demo.pages.data

import io.github.shijianjs.ktjs.demo.page.Blocks.row as kitRow
import io.github.shijianjs.ktjs.demo.page.Blocks.col as kitCol
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleCaption
import io.github.shijianjs.ktjs.demo.page.Blocks.paragraph
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.page.Readout
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import io.github.shijianjs.ktjs.demo.page.Grid
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import web.html.HtmlTagName
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import demo.CatalogEntry
import demo.Catalog
import demo.Groups
import demo.bi
import demo.pages.fixtures.Person
import demo.pages.fixtures.filterPeople
import demo.pages.fixtures.people
import demo.ui.EventLog
import demo.ui.MUTED
import demo.ui.PageFrame
import demo.ui.PageMeta
import demo.ui.jsArray
import demo.ui.jsObject
import demo.ui.listen
import kotlin.js.unsafeCast
import io.github.shijianjs.ktjs.vaadin.badge.createBadge
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.checkbox.CheckboxEvents
import io.github.shijianjs.ktjs.vaadin.checkbox.createCheckbox
import io.github.shijianjs.ktjs.vaadin.combobox.createComboBox
import io.github.shijianjs.ktjs.vaadin.contextmenu.ContextMenuEvents
import io.github.shijianjs.ktjs.vaadin.contextmenu.createContextMenu
import io.github.shijianjs.ktjs.vaadin.grid.ColumnRenderings
import io.github.shijianjs.ktjs.vaadin.grid.GridColumnElement
import io.github.shijianjs.ktjs.vaadin.grid.GridDropModes
import io.github.shijianjs.ktjs.vaadin.grid.GridElement
import io.github.shijianjs.ktjs.vaadin.grid.GridEventSections
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
import io.github.shijianjs.ktjs.vaadin.textarea.createTextArea
import io.github.shijianjs.ktjs.vaadin.textfield.TextFieldEvents
import io.github.shijianjs.ktjs.vaadin.textfield.createTextField
import io.github.shijianjs.ktjs.vaadin.tooltip.createTooltip
import web.cssom.TextAlign
import web.dom.clickEvent
import web.dom.contextMenuEvent
import web.events.addHandler
import kotlin.js.JsAny
import kotlin.js.js
import web.html.HTMLElement
import web.pointer.PointerEvent

/**
 * `<vaadin-demo-grid-page>`: every grid example of the docs, from basic usage to lazy columns.
 *
 * All the grids, columns and controls are created once. Column headers, section headings, captions and
 * readouts are bound to the page's text table, so a language switch rewrites them in place. The few
 * cells whose text is copy rather than data (yes / no, the manage buttons) read the table when they
 * render, and their grids clear their cache on a switch so the cells are drawn again.
 */
internal class GridPage : DemoPage(), GridTranslatable {

    private val texts: Binder<GridTexts> = gridTexts

    private val frame: PageFrame = PageFrame(ENTRY)

    /** Cards and elements that own a text table of their own. */
    private val translatables: MutableList<GridTranslatable> = mutableListOf()

    /** Grids whose cells show copy; they redraw when the language changes. */
    private val cells: MutableList<GridElement> = mutableListOf()

    private val selectionReadout: Readout = Readout.NAME { show() }

    private var selectedRows: Int = 0

    private val directionReadout: Readout = Readout.NAME { show() }

    private var sortState: SortState = SortState.NONE

    private val menuReadout: Readout = Readout.NAME { show() }

    private var menuPerson: String = ""

    private var menuValue: String = ""

    private val singleReadout: Readout = Readout.NAME { show() }

    private var singleName: String? = null

    private val multiReadout: Readout = Readout.NAME { show() }

    private var multiNames: List<String> = emptyList()

    private val rangeReadout: Readout = Readout.NAME { show() }

    private val rangeSelectedIds: MutableSet<Int> = mutableSetOf()

    private val pageReadout: Readout = Readout.NAME { show() }

    private var pageIndex: Int = 0

    private var pageCount: Int = 1

    private val focusInfo: io.github.shijianjs.ktjs.vaadin.textarea.TextAreaElement = createTextArea { readonly = true }

    private var focusSectionName: String = ""

    private var focusRow: String = ""

    private var focusColumn: String = ""

    private var focusPerson: String = ""

    init {
        frame.mount(this)
        appendBasicGrid()
        appendGridColumns()
        appendGridDataBinding()
        appendGridSorting()
        appendGridFeatures()
        applyTranslation(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = ENTRY.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        // Cells draw their copy from `texts.current`, so their grids have to render them again.
        cells.forEach { it.clearCache() }
        showSelection()
        showSortState()
        showMenuSelection()
        showSingleSelection()
        showMultiSelection()
        showRangeSelection()
        showPage()
        showFocus()
        translatables.forEach { it.applyTranslation(lang) }
    }

    /** A section whose heading and hint are both bound to the table. */
    private fun section(heading: (GridTexts) -> String, hint: (GridTexts) -> String): SectionBlock =
        SectionBlock.NAME { show() }.apply {
            texts.bind(this.heading, heading)
            texts.bind(this.hint, hint)
        }

    /** An example caption that follows the language. */
    private fun caption(field: (GridTexts) -> String): HTMLElement = texts.bind(exampleCaption(), field)

    /** Binds a column header: `header` is a property, so it needs its own setter. */
    private fun header(column: GridColumnElement, field: (GridTexts) -> String) {
        texts.bind(column, field) { column.header = it }
    }

    /** A plain text node bound to the table. */
    private fun textNode(field: (GridTexts) -> String, vararg setup: (HTMLElement) -> Unit): HTMLElement =
        HtmlTagName.span().also { node ->
            setup.forEach { it(node) }
            texts.bind(node, field) { node.textContent = it }
        }

    /** A grid of this page; tracked so a language switch can redraw its cells. */
    private fun grid(height: String = "18rem"): GridElement = newGrid(height).also { cells += it }

    private fun showSelection() {
        selectionReadout.set(texts.current.selectedRows(selectedRows))
    }

    private fun showSortState() {
        val copy = texts.current
        directionReadout.set(
            when (sortState) {
                SortState.NONE -> copy.sortNone
                SortState.ASCENDING -> copy.sortAscending
                SortState.DESCENDING -> copy.sortDescending
            },
        )
    }

    private fun showMenuSelection() {
        val copy = texts.current
        if (menuPerson.isEmpty()) {
            menuReadout.set(copy.noMenuSelected)
        } else {
            menuReadout.set(copy.selectedPrefix + "$menuPerson · $menuValue")
        }
    }

    private fun showSingleSelection() {
        val copy = texts.current
        val name = singleName
        if (name == null) {
            singleReadout.set(copy.noRow)
        } else {
            singleReadout.set(copy.selectedPrefix + name)
        }
    }

    private fun showMultiSelection() {
        multiReadout.set(texts.current.multiCount(multiNames.size) + multiNames.joinToString(", "))
    }

    private fun showRangeSelection() {
        rangeReadout.set(texts.current.rangeCount(rangeSelectedIds.size))
    }

    private fun showPage() {
        pageReadout.set(texts.current.pageOf(pageIndex + 1, pageCount))
    }

    private fun showFocus() {
        focusInfo.value = texts.current.cellFocusLines(focusSectionName, focusRow, focusColumn, focusPerson)
    }

    // region Basic usage

    private fun appendBasicGrid() {
        val basic = section({ it.basicHeading }, { it.basicHint })
        val log = EventLog(6)
        val basicGrid = grid()
        basicGrid.dataset["test"] = "grid-basic"
        basicGrid.fill(people(8))
        val nameColumn = createGridColumn { path = "displayName" }
        header(nameColumn) { it.columnName }
        val roleColumn = createGridColumn { path = "profession" }
        header(roleColumn) { it.columnRole }
        val emailColumn = createGridColumn { path = "email" }
        header(emailColumn) { it.columnEmail }
        basicGrid.append(createGridSelectionColumn(), nameColumn, roleColumn, emailColumn)
        basicGrid.listen(GridEvents.selectedItemsChanged) {
            selectedRows = basicGrid.selectedItems.size
            showSelection()
        }
        // `cell-activate` only fires for user interaction (double click / Enter), so the log starts
        // empty instead of recording the events the grid fires while it initialises.
        basicGrid.listen(GridEvents.cellActivate) { log.add("cell-activate") }
        basic.body.append(exampleBox(null, kitCol(basicGrid, kitRow(selectionReadout.root), log.root)))
        body.append(basic.root)
    }

    // endregion

    // region Columns

    private fun appendGridColumns() {
        val columns = section({ it.columnsHeading }, { it.columnsHint })

        // Column width: explicit width, auto width and flex-grow.
        val widths = grid("16rem")
        widths.fill(people(6))
        val firstColumn = createGridColumn {
            path = "firstName"
            width = "7rem"
            flexGrow = 0.0
        }
        header(firstColumn) { it.columnFirstName }
        val professionColumn = createGridColumn {
            path = "profession"
            autoWidth = true
            flexGrow = 0.0
        }
        header(professionColumn) { it.columnProfession }
        val subscriberColumn = createGridColumn {
            path = "subscriber"
            width = "6rem"
            flexGrow = 0.0
            renderer = { root, _, model ->
                val copy = texts.current
                root.textContent = if (itemFlag(model.item, "subscriber")) copy.yes else copy.no
            }
        }
        header(subscriberColumn) { it.columnSubscriber }
        widths.append(createGridSelectionColumn(), firstColumn, professionColumn, subscriberColumn)
        columns.body.append(exampleBox(null, caption { it.widthCaption }, widths))

        // Column alignment: `text-align="end"` on the column plus tabular numbers.
        val aligned = grid("14rem")
        aligned.fill(people(5))
        val alignedName = createGridColumn { path = "displayName" }
        header(alignedName) { it.columnName }
        val dueColumn = createGridColumn {
            renderer = { root, _, model -> root.textContent = dueDate(itemId(model.item)) }
        }
        header(dueColumn) { it.columnDue }
        val amountColumn = createGridColumn {
            textAlign = TextAlign.end
            renderer = { root, _, model ->
                root.append(
                    HtmlTagName.span {
                        textContent = euroAmount(itemId(model.item))
                    }.apply { style.setProperty("font-variant-numeric", "tabular-nums") },
                )
            }
        }
        header(amountColumn) { it.columnAmount }
        aligned.append(alignedName, dueColumn, amountColumn)
        columns.body.append(exampleBox(null, caption { it.alignCaption }, aligned))

        // Column freezing: a frozen first column and a frozen-to-end action column.
        val frozenGrid = grid("14rem")
        frozenGrid.fill(people(6))
        val frozenName = createGridColumn {
            frozen = true
            autoWidth = true
            flexGrow = 0.0
            renderer = { root, _, model -> root.textContent = "${itemText(model.item, "firstName")} ${itemText(model.item, "lastName")}" }
        }
        header(frozenName) { it.columnName }
        val phoneColumn = createGridColumn {
            path = "address.phone"
            autoWidth = true
        }
        header(phoneColumn) { it.columnPhone }
        val actionColumn = createGridColumn {
            frozenToEnd = true
            autoWidth = true
            flexGrow = 0.0
            renderer = { root, _, _ ->
                val copy = texts.current
                root.append(createButton { textContent = copy.edit }.addTheme("tertiary-inline"))
            }
        }
        frozenGrid.append(
            frozenName,
            createGridColumn { path = "email"; autoWidth = true },
            phoneColumn,
            createGridColumn { path = "profession"; autoWidth = true },
            createGridColumn { path = "address.street"; autoWidth = true },
            actionColumn,
        )
        columns.body.append(exampleBox(null, caption { it.frozenCaption }, frozenGrid))

        // Column grouping.
        val grouped = grid("16rem")
        grouped.fill(people(6))
        val nameGroup = createGridColumnGroup()
        nameGroup.append(createGridColumn { path = "firstName" }, createGridColumn { path = "lastName" })
        texts.bind(nameGroup, { it.columnName }) { nameGroup.header = it }
        val addressGroup = createGridColumnGroup()
        addressGroup.append(
            createGridColumn { path = "address.street" },
            createGridColumn { path = "address.city" },
            createGridColumn { path = "address.zip" },
            createGridColumn { path = "address.state" },
        )
        texts.bind(addressGroup, { it.columnAddress }) { addressGroup.header = it }
        grouped.append(nameGroup, addressGroup)
        columns.body.append(exampleBox(null, caption { it.groupingCaption }, grouped))

        // Custom header and footer rendering, with the info icon of the docs example.
        val headed = grid("16rem")
        headed.fill(people(6))
        val totalMembers = textNode({ it.totalMembers })
        val subscriberHeader = GridHeader.NAME { show("", "vaadin:info-circle", "") }
        texts.bind(subscriberHeader, { it.columnSubscriber }) { subscriberHeader.label = it }
        texts.bind(subscriberHeader, { it.subscriberTooltip }) { subscriberHeader.tooltip = it }
        val subscriberCount = textNode({ it.subscriberCount })
        val membershipHeader = GridHeader.NAME { show("", "vaadin:info-circle", "") }
        texts.bind(membershipHeader, { it.membershipHeading }) { membershipHeader.label = it }
        texts.bind(membershipHeader, { it.membershipTooltip }) { membershipHeader.tooltip = it }
        val membershipBreakdown = textNode({ it.membershipBreakdown })
        val headedName = createGridColumn {
            path = "displayName"
            footerRenderer = { root, _ -> root.replaceChildren(totalMembers) }
        }
        header(headedName) { it.columnName }
        val subscriberColumnHeaded = createGridColumn {
            headerRenderer = { root, _ -> root.replaceChildren(subscriberHeader) }
            renderer = { root, _, model ->
                val copy = texts.current
                root.textContent = if (itemFlag(model.item, "subscriber")) copy.yes else copy.no
            }
            footerRenderer = { root, _ -> root.replaceChildren(subscriberCount) }
        }
        val membershipColumn = createGridColumn {
            path = "membership"
            headerRenderer = { root, _ -> root.replaceChildren(membershipHeader) }
            footerRenderer = { root, _ -> root.replaceChildren(membershipBreakdown) }
        }
        headed.append(headedName, subscriberColumnHeaded, membershipColumn)
        columns.body.append(exampleBox(null, caption { it.headersCaption }, headed))

        // Column reordering and resizing.
        val rearrange = grid("16rem")
        rearrange.fill(people(6))
        rearrange.columnReorderingAllowed = true
        val rearrangeName = createGridColumnGroup()
        rearrangeName.append(
            createGridColumn { path = "firstName"; resizable = true },
            createGridColumn { path = "lastName"; resizable = true },
        )
        texts.bind(rearrangeName, { it.columnName }) { rearrangeName.header = it }
        val rearrangeAddress = createGridColumnGroup()
        rearrangeAddress.append(
            createGridColumn { path = "address.street"; resizable = true },
            createGridColumn { path = "address.city"; resizable = true },
            createGridColumn { path = "address.zip"; resizable = true },
            createGridColumn { path = "address.state"; resizable = true },
        )
        texts.bind(rearrangeAddress, { it.columnAddress }) { rearrangeAddress.header = it }
        rearrange.append(rearrangeName, rearrangeAddress)
        columns.body.append(exampleBox(null, caption { it.rearrangeCaption }, rearrange))

        // Column visibility: the docs put the toggles in a popover; checkboxes keep it inline.
        val visibility = grid("14rem")
        visibility.fill(people(6))
        val visibleName = createGridColumn { path = "displayName" }
        header(visibleName) { it.columnName }
        val visibleEmail = createGridColumn { path = "email" }
        header(visibleEmail) { it.columnEmail }
        val visibleProfession = createGridColumn { path = "profession" }
        header(visibleProfession) { it.columnProfession }
        val visibleStreet = createGridColumn { path = "address.street" }
        header(visibleStreet) { it.columnStreet }
        visibility.append(visibleName, visibleEmail, visibleProfession, visibleStreet)

        fun toggle(column: GridColumnElement, field: (GridTexts) -> String) =
            createCheckbox { checked = true }.also { box ->
                texts.bind(box, field) { box.label = it }
                box.listen(CheckboxEvents.checkedChanged) { column.hiddenProperty = !box.checked }
            }

        val toggles =
            kitRow(
                toggle(visibleEmail) { it.columnEmail },
                toggle(visibleProfession) { it.columnProfession },
                toggle(visibleStreet) { it.columnStreet },
            )
        columns.body.append(exampleBox(null, caption { it.visibilityCaption }, kitCol(visibility, toggles)))

        body.append(columns.root)
    }

    // endregion

    // region Data binding

    private fun appendGridDataBinding() {
        val binding = section({ it.bindingHeading }, { it.bindingHint })

        // Lazy loading through a data provider, with a search term that clears the cache.
        val providerGrid = grid("16rem")
        val searchField = createTextField().also { field -> texts.bind(field, { it.search }) { field.placeholder = it } }
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
        val providerName = createGridSortColumn { path = "displayName" }
        header(providerName) { it.columnName }
        val providerProfession = createGridSortColumn { path = "profession" }
        header(providerProfession) { it.columnProfession }
        providerGrid.append(providerName, providerProfession)
        searchField.listen(TextFieldEvents.valueChanged) {
            searchTerm = searchField.value.trim()
            providerGrid.clearCache()
        }
        binding.body.append(
            exampleBox(null, kitCol(caption { it.providerCaption }, kitRow(searchField), providerGrid)),
        )

        // External filtering: the items array is replaced on every keystroke.
        val externalGrid = grid("16rem")
        val externalSearch = createTextField().also { field -> texts.bind(field, { it.search }) { field.placeholder = it } }
        externalSearch.style.setProperty("width", "50%")
        externalSearch.append(createIcon { icon = "vaadin:search"; slot = "prefix" })
        externalGrid.fill(sourcePeople)
        val externalName = createGridColumn {
            width = "230px"
            flexGrow = 0.0
            renderer = { root, _, model -> root.append(PersonCell.NAME { show(model.item) }) }
        }
        header(externalName) { it.columnName }
        val externalEmail = createGridColumn { path = "email" }
        header(externalEmail) { it.columnEmail }
        val externalProfession = createGridColumn { path = "profession" }
        header(externalProfession) { it.columnProfession }
        externalGrid.append(externalName, externalEmail, externalProfession)
        externalSearch.listen(TextFieldEvents.valueChanged) {
            externalGrid.fill(filterPeople(sourcePeople, externalSearch.value.trim()))
        }
        binding.body.append(
            exampleBox(null, kitCol(caption { it.externalCaption }, kitRow(externalSearch), externalGrid)),
        )

        // Filtering by column: the grid filters its own array items.
        val filterGrid = grid("16rem")
        filterGrid.fill(sourcePeople)
        val filterName = createGridFilterColumn {
            path = "displayName"
            width = "230px"
            flexGrow = 0.0
        }
        header(filterName) { it.columnName }
        filterGrid.append(filterName, createGridFilterColumn { path = "email" }, createGridFilterColumn { path = "profession" })
        binding.body.append(exampleBox(null, caption { it.filterCaption }, filterGrid))

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
        var pageSize = 5
        val pagedGrid = grid().apply { allRowsVisible = true }
        val pagedName = createGridColumn {
            width = "230px"
            flexGrow = 0.0
            renderer = { root, _, model -> root.append(PersonCell.NAME { show(model.item) }) }
        }
        header(pagedName) { it.columnName }
        pagedGrid.append(pagedName, createGridColumn { path = "email" }, createGridColumn { path = "profession" })
        val firstButton = createButton { textContent = "«" }.addTheme("small icon")
        val previousButton = createButton { textContent = "‹" }.addTheme("small icon")
        val nextButton = createButton { textContent = "›" }.addTheme("small icon")
        val lastButton = createButton { textContent = "»" }.addTheme("small icon")
        val pageSizeLabel = textNode({ it.pageSize })

        fun slicePage() {
            pageCount = maxOf(1, (allPeople.size + pageSize - 1) / pageSize)
            pageIndex = pageIndex.coerceIn(0, pageCount - 1)
            pagedGrid.fill(allPeople.drop(pageIndex * pageSize).take(pageSize))
            showPage()
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
                null,
                kitCol(
                    pagedGrid,
                    kitRow(pageSizeLabel, pageSizeSelect, firstButton, previousButton, pageReadout.root, nextButton, lastButton),
                    caption { it.invitedCaption },
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
        val inviteGrid = grid().apply { allRowsVisible = true }
        inviteGrid.style.setProperty("margin-top", "var(--lumo-space-s)")
        val emptyInvite =
            HtmlTagName.p().also { node ->
                node.className = MUTED
                texts.bind(node, { it.noInvite }) { node.textContent = it }
            }
        val inviteName = createGridColumn { path = "displayName"; autoWidth = true }
        header(inviteName) { it.columnName }
        val inviteManage = createGridColumn {
            renderer = { root, _, model ->
                val copy = texts.current
                val personId = itemId(model.item)
                root.append(
                    createButton { textContent = "" }.addTheme("error tertiary icon").apply {
                        setAttribute("aria-label", copy.remove)
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
        }
        header(inviteManage) { it.columnManage }
        inviteGrid.append(inviteName, createGridColumn { path = "email" }, createGridColumn { path = "address.phone" }, inviteManage)
        inviteGrid.toggleAttribute("hidden", true)
        val inviteButton = createButton().addTheme("primary")
        texts.bind(inviteButton, { it.sendInvite }) { inviteButton.textContent = it }
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
                null,
                kitCol(kitRow(invitedCombo, inviteButton), emptyInvite, inviteGrid, caption { it.invitedCaption }),
            ),
        )

        body.append(binding.root)
    }

    // endregion

    // region Sorting

    private fun appendGridSorting() {
        val sorting = section({ it.sortingHeading }, { it.sortingHint })

        val sorted = grid()
        sorted.dataset["test"] = "grid-sorted"
        sorted.fill(people(8))
        val sortedName = createGridSortColumn {
            path = "displayName"
            direction = GridSorterDirections.asc
        }
        header(sortedName) { it.columnName }
        val sortedBirthday = createGridSortColumn { path = "birthday" }
        header(sortedBirthday) { it.columnBirthday }
        sorted.append(sortedName, createGridSortColumn { path = "email" }, createGridSortColumn { path = "profession" }, sortedBirthday)
        sorting.body.append(exampleBox(null, caption { it.basicSortCaption }, sorted))

        val multi = grid("16rem")
        multi.fill(people(8))
        multi.multiSort = true
        multi.multiSortPriority = GridMultiSortPriorities.append
        val multiId = createGridSortColumn { path = "id" }
        header(multiId) { it.columnId }
        val multiName = createGridSortColumn { path = "displayName" }
        header(multiName) { it.columnName }
        val multiBirthday = createGridSortColumn { path = "birthday" }
        header(multiBirthday) { it.columnBirthday }
        multi.append(multiId, multiName, createGridSortColumn { path = "profession" }, multiBirthday)
        sorting.body.append(exampleBox(null, caption { it.multiSortCaption }, multi))

        val programmatic = grid("16rem")
        programmatic.fill(people(8))
        val programmaticName = createGridSortColumn { path = "displayName" }
        header(programmaticName) { it.columnName }
        val programmaticProfession = createGridSortColumn { path = "profession" }
        header(programmaticProfession) { it.columnProfession }
        programmatic.append(programmaticName, programmaticProfession)

        fun applyDirection(state: SortState) {
            sortState = state
            programmaticName.direction =
                when (state) {
                    SortState.NONE -> null
                    SortState.ASCENDING -> GridSorterDirections.asc
                    SortState.DESCENDING -> GridSorterDirections.desc
                }
            showSortState()
        }

        val ascendingButton = createButton().addTheme("tertiary")
        texts.bind(ascendingButton, { it.ascending }) { ascendingButton.textContent = it }
        ascendingButton.clickEvent.addHandler { applyDirection(SortState.ASCENDING) }
        val descendingButton = createButton().addTheme("tertiary")
        texts.bind(descendingButton, { it.descending }) { descendingButton.textContent = it }
        descendingButton.clickEvent.addHandler { applyDirection(SortState.DESCENDING) }
        val clearButton = createButton().addTheme("tertiary")
        texts.bind(clearButton, { it.clear }) { clearButton.textContent = it }
        clearButton.clickEvent.addHandler { applyDirection(SortState.NONE) }

        sorting.body.append(
            exampleBox(
                null,
                kitCol(programmatic, kitRow(ascendingButton, descendingButton, clearButton), directionReadout.root),
            ),
        )

        body.append(sorting.root)
    }

    // endregion

    // region Row details, empty state, menus, focus and scrolling

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
        val details = section({ it.detailsHeading }, { it.detailsHint })

        // Details open on the active item.
        val clickGrid = grid()
        clickGrid.fill(people(5))
        clickGrid.addTheme("row-stripes")
        val clickName = createGridColumn { path = "displayName" }
        header(clickName) { it.columnName }
        clickGrid.append(clickName, createGridColumn { path = "profession" })
        clickGrid.rowDetailsRenderer = { root, _, model -> root.append(PersonDetails.NAME { show(model.item) }) }
        clickGrid.listen(GridEvents.activeItemChanged) { event ->
            val item = detailValue(event)
            if (item == null) {
                clickGrid.detailsOpenedItems = emptyArray()
            } else {
                clickGrid.detailsOpenedItems = arrayOf(item)
            }
        }
        details.body.append(exampleBox(null, caption { it.clickDetailsCaption }, clickGrid))

        // A frozen column with a toggle button.
        val toggleGrid = grid()
        toggleGrid.fill(people(5))
        toggleGrid.rowDetailsRenderer = { root, _, model -> root.append(PersonDetails.NAME { show(model.item) }) }
        val openedItems = mutableListOf<JsAny>()
        val toggleColumn = createGridColumn {
            width = "80px"
            flexGrow = 0.0
            frozen = true
            renderer = { root, _, model ->
                val copy = texts.current
                val open = model.detailsOpened == true
                root.append(
                    createButton { textContent = "" }.addTheme("tertiary icon").apply {
                        setAttribute("aria-label", copy.toggleDetails)
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
        }
        val toggleName = createGridColumn { path = "displayName" }
        header(toggleName) { it.columnName }
        toggleGrid.append(toggleColumn, toggleName, createGridColumn { path = "profession" })
        details.body.append(exampleBox(null, caption { it.toggleCaption }, toggleGrid))

        body.append(details.root)
    }

    private fun appendGridEmptyState() {
        val empty = section({ it.emptyHeading }, { it.emptyHint })
        val grid = grid("10rem")
        val noEmployees =
            HtmlTagName.span().also { node ->
                node.slot = "empty-state"
                texts.bind(node, { it.noEmployees }) { node.textContent = it }
            }
        grid.append(
            createGridColumn { path = "firstName" },
            createGridColumn { path = "lastName" },
            createGridColumn { path = "email" },
            createGridColumn { path = "profession" },
            noEmployees,
        )
        empty.body.append(exampleBox(null, grid))
        body.append(empty.root)
    }

    private fun appendGridMenus() {
        val menus = section({ it.menusHeading }, { it.menusHint })

        val menuGrid = grid("16rem")
        menuGrid.fill(people(6))
        menuGrid.append(
            createGridColumn { path = "firstName" },
            createGridColumn { path = "lastName" },
            createGridColumn { path = "email" },
            createGridColumn { path = "profession" },
        )
        val menu = createContextMenu()
        var personName = ""
        menu.listen(ContextMenuEvents.itemSelected) { event ->
            menuPerson = personName
            menuValue = detailField(event, "value")?.let { itemText(it, "text") } ?: ""
            showMenuSelection()
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
            val copy = texts.current
            menu.items = menuItems(copy.edit, copy.delete, copy.sendEmail, copy.call)
        }
        menu.append(menuGrid)
        menus.body.append(exampleBox(null, kitCol(menu, menuReadout.root, caption { it.menuCaption })))

        val tooltipGrid = grid("16rem")
        tooltipGrid.fill(people(6))
        val birthdayColumn = createGridColumn {
            path = "birthday"
            renderer = { root, _, model -> root.textContent = birthdayText(itemText(model.item, "birthday")) }
        }
        header(birthdayColumn) { it.columnBirthday }
        val statusColumn = createGridColumn {
            path = "status"
            renderer = { root, _, model ->
                val status = itemText(model.item, "status")
                root.append(
                    createBadge { textContent = status }.addTheme(if (status == "Available") "success" else "error"),
                )
            }
        }
        header(statusColumn) { it.columnStatus }
        tooltipGrid.append(
            createGridColumn { path = "firstName" },
            createGridColumn { path = "lastName" },
            birthdayColumn,
            statusColumn,
        )
        val tooltip = createTooltip().apply { slot = "tooltip" }
        // The generator runs for every cell the pointer enters; the column path decides the text.
        tooltip.generator = { context ->
            val copy = texts.current
            val path = js("(context) => context && context.column ? String(context.column.path) : ''")(context) as String
            val item = js("(context) => context && context.item ? context.item : null")(context)
            when (path) {
                "birthday" -> copy.age(ageOf(itemPathText(item, "birthday")))
                "status" -> itemPathText(item, "status")
                else -> itemText(item, "firstName")
            }
        }
        tooltipGrid.append(tooltip)
        menus.body.append(exampleBox(null, caption { it.tooltipCaption }, tooltipGrid))

        body.append(menus.root)
    }

    private fun appendGridFocusAndScroll() {
        val focusSection = section({ it.focusHeading }, { it.focusHint })

        val focusGrid = grid("16rem")
        focusGrid.fill(people(8))
        focusGrid.append(
            createGridColumn { path = "firstName" },
            createGridColumn { path = "lastName" },
            createGridColumn { path = "email" },
            createGridColumn { path = "profession" },
        )
        texts.bind(focusInfo, { it.focusInfoLabel }) { focusInfo.label = it }
        focusInfo.style.setProperty("width", "100%")
        focusInfo.style.setProperty("margin-top", "var(--lumo-space-l)")
        focusGrid.listen(GridEvents.cellFocus) { event ->
            val copy = texts.current
            val context = focusGrid.getEventContext(event)
            focusSectionName = context.section?.let { js("(section) => String(section)")(it) as String } ?: copy.notAvailable
            focusRow = context.index?.let { "${it.toInt()}" } ?: copy.notAvailable
            focusColumn = context.column?.path ?: copy.notAvailable
            focusPerson = itemText(context.item, "displayName").ifEmpty { copy.notAvailable }
            showFocus()
        }
        focusSection.body.append(exampleBox(null, kitCol(focusGrid, focusInfo, caption { it.focusCaption })))

        val scrollGrid = grid("14rem")
        val rows = (1..200).map { """{"name": "Row $it"}""" }
        scrollGrid.items = jsArray<JsAny>("[${rows.joinToString(", ")}]")
        val scrollName = createGridColumn { path = "name" }
        header(scrollName) { it.rowName }
        scrollGrid.append(scrollName)
        val scrollLog = EventLog(4)

        val goFirst = createButton().addTheme("tertiary")
        texts.bind(goFirst, { it.goRow1 }) { goFirst.textContent = it }
        goFirst.clickEvent.addHandler {
            scrollGrid.scrollToIndex(0.0)
            scrollLog.add("scrollToIndex(0)")
        }
        val goFiftieth = createButton().addTheme("tertiary")
        texts.bind(goFiftieth, { it.goRow50 }) { goFiftieth.textContent = it }
        goFiftieth.clickEvent.addHandler {
            scrollGrid.scrollToIndex(49.0)
            scrollLog.add("scrollToIndex(49)")
        }
        val goLast = createButton().addTheme("tertiary")
        texts.bind(goLast, { it.goRow200 }) { goLast.textContent = it }
        goLast.clickEvent.addHandler {
            scrollGrid.scrollToIndex(199.0)
            scrollLog.add("scrollToIndex(199)")
        }

        focusSection.body.append(
            exampleBox(
                null,
                kitCol(scrollGrid, kitRow(goFirst, goFiftieth, goLast), scrollLog.root, caption { it.scrollCaption }),
            ),
        )

        body.append(focusSection.root)
    }

    // endregion

    // region Drag and drop

    private fun appendGridDragAndDrop() {
        val drag = section({ it.dragHeading }, { it.dragHint })

        // Row reordering.
        val reorderItems = people(6).toMutableList()
        val reorderGrid = grid("14rem")
        reorderGrid.fill(reorderItems)
        reorderGrid.rowsDraggable = true
        var draggedId = -1
        val reorderName = createGridColumn { path = "displayName" }
        header(reorderName) { it.columnName }
        reorderGrid.append(reorderName, createGridColumn { path = "profession" })
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
        drag.body.append(exampleBox(null, caption { it.reorderCaption }, reorderGrid))

        // Moving rows between two grids.
        val panelOne = people(8).take(4).toMutableList()
        val panelTwo = people(8).drop(4).toMutableList()
        var draggedPersonId = -1
        val firstPanel = dragGrid(panelOne, onDragStart = { draggedPersonId = it })
        val secondPanel = dragGrid(panelTwo, onDragStart = { draggedPersonId = it })
        cells += firstPanel
        cells += secondPanel

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
        drag.body.append(exampleBox(null, kitRow(firstPanel, secondPanel, caption { it.betweenCaption })))

        // Drag and drop filters.
        val filterItems = people(6).toMutableList()
        val filterGrid = grid("14rem")
        filterGrid.fill(filterItems)
        filterGrid.rowsDraggable = true
        filterGrid.dragFilter = { model -> itemId(model.item) % 2 == 1 }
        filterGrid.dropFilter = { model -> itemFlag(model.item, "subscriber") }
        val filterDragName = createGridColumn { path = "displayName" }
        header(filterDragName) { it.columnName }
        val filterDragSubscriber = createGridColumn { path = "subscriber" }
        header(filterDragSubscriber) { it.columnSubscriber }
        filterGrid.append(filterDragName, filterDragSubscriber)
        drag.body.append(exampleBox(null, caption { it.dragFilterCaption }, filterGrid))

        body.append(drag.root)
    }

    // endregion

    // region Selection and styling

    private fun appendGridSelection() {
        val selection = section({ it.selectionHeading }, { it.selectionHint })

        val singleGrid = grid("14rem")
        singleGrid.fill(people(6))
        singleGrid.append(
            createGridColumn { path = "firstName" },
            createGridColumn { path = "lastName" },
            createGridColumn { path = "email" },
        )
        singleGrid.listen(GridEvents.activeItemChanged) { event ->
            val item = detailValue(event)
            if (item == null) {
                singleGrid.selectedItems = emptyArray()
                singleName = null
            } else {
                singleGrid.selectedItems = arrayOf(item)
                singleName = itemText(item, "displayName")
            }
            showSingleSelection()
        }
        selection.body.append(exampleBox(null, kitCol(singleGrid, singleReadout.root, caption { it.singleCaption })))

        val multiGrid = grid("14rem")
        multiGrid.fill(people(6))
        multiGrid.append(
            createGridSelectionColumn(),
            createGridColumn { path = "firstName" },
            createGridColumn { path = "lastName" },
            createGridColumn { path = "email" },
        )
        multiGrid.listen(GridEvents.selectedItemsChanged) {
            multiNames = multiGrid.selectedItems.map { itemText(it, "displayName") }
            showMultiSelection()
        }
        selection.body.append(exampleBox(null, kitCol(multiGrid, multiReadout.root, caption { it.multiCaption })))

        val rangeGrid = grid("14rem")
        rangeGrid.itemIdPath = "id"
        rangeGrid.fill(people(8))
        rangeGrid.append(
            createGridSelectionColumn(),
            createGridColumn { path = "firstName" },
            createGridColumn { path = "lastName" },
            createGridColumn { path = "email" },
        )
        var anchorId = -1
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
                        if (selected) rangeSelectedIds.add(itemId(item)) else rangeSelectedIds.remove(itemId(item))
                    }
                    rangeGrid.selectedItems = rangeGrid.items?.filter { rangeSelectedIds.contains(itemId(it)) }?.toTypedArray() ?: emptyArray()
                }
            } else {
                if (selected) rangeSelectedIds.add(toggledId) else rangeSelectedIds.remove(toggledId)
            }
            anchorId = toggledId
            showRangeSelection()
        }
        selection.body.append(exampleBox(null, kitCol(rangeGrid, rangeReadout.root, caption { it.rangeCaption })))

        body.append(selection.root)
    }

    private fun appendGridStyling() {
        val styling = section({ it.stylingHeading }, { it.stylingHint })

        val variantColumns =
            listOf("row-stripes", "no-row-borders", "column-borders", "no-border", "compact")
        styling.body.append(
            exampleBox(
                null,
                caption { it.variantsCaption },
                Grid.NAME { show(260); add(*variantColumns.map { theme -> kitCol(paragraph(theme), themedGrid(theme)) }.toTypedArray()) },
            ),
        )

        val wrapped = grid("12rem")
        wrapped.fill(people(5))
        wrapped.addTheme("wrap-cell-content")
        val wrappedAddress = createGridColumn {
            renderer = { root, _, model ->
                root.textContent =
                    listOf("street", "city", "zip", "state").joinToString(" ") { itemPathText(model.item, "address.$it") }
            }
        }
        header(wrappedAddress) { it.columnAddress }
        wrapped.append(createGridColumn { path = "firstName" }, createGridColumn { path = "lastName" }, wrappedAddress)
        styling.body.append(exampleBox(null, caption { it.wrapCaption }, wrapped))

        val partGrid = grid("14rem")
        partGrid.items = gridItems(people(6), withRating = true)
        val ratingColumn = createGridColumn {
            renderer = { root, _, model -> root.textContent = fixed2(itemRating(model.item)) }
        }
        header(ratingColumn) { it.columnRating }
        partGrid.append(createGridColumn { path = "lastName" }, createGridColumn { path = "profession" }, ratingColumn)
        partGrid.cellPartNameGenerator = { column, model ->
            var parts = ""
            if (headerStartsWith(column, texts.current.columnRating)) parts += " font-weight-bold"
            val rating = itemRating(model.item)
            parts +=
                when {
                    rating >= 8.0 -> " high-rating"
                    rating <= 4.0 -> " low-rating"
                    else -> ""
                }
            parts
        }
        styling.body.append(exampleBox(null, caption { it.dynamicCaption }, partGrid))

        val headerGrid = grid("12rem")
        headerGrid.fill(people(6))
        val avgRating = textNode({ it.avgRating })
        val headerRating = createGridColumn {
            headerPartName = "rating-header"
            footerPartName = "rating-footer"
            renderer = { root, _, model -> root.textContent = fixed2(itemRating(model.item)) }
            footerRenderer = { root, _ -> root.replaceChildren(avgRating) }
        }
        header(headerRating) { it.columnRating }
        headerGrid.append(
            createGridColumn { path = "firstName" },
            createGridColumn { path = "lastName" },
            createGridColumn { path = "profession" },
            headerRating,
        )
        styling.body.append(exampleBox(null, caption { it.headerFooterCaption }, headerGrid))

        body.append(styling.root)
    }

    private fun appendGridLazyColumns() {
        val lazy = section({ it.lazyHeading }, { it.lazyHint })
        val grid = grid("12rem")
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

    companion object : CustomElementReferenceAdapter<GridPage>(GridPage::class.js, "vaadin-demo-grid-page") {        val ENTRY: CatalogEntry =
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
                        docsUrl = "${Catalog.DOCS_BASE}/grid",
                        sourcePath = "pages/data/GridPage.kt",
                    ),
                kitPage = { GridPage.NAME() },
            )



}
}
