package demo.pages.overlays

import io.github.shijianjs.ktjs.demo.page.Blocks.col as kitCol
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.page.Blocks.row as kitRow
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import web.aria.AriaRole
import web.aria.menu
import web.aria.menuitem
import web.html.HtmlTagName
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import io.github.shijianjs.ktjs.demo.page.Blocks.anchor as kitAnchor
import io.github.shijianjs.ktjs.demo.page.Blocks.paragraph
import io.github.shijianjs.ktjs.demo.page.Blocks.heading
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import demo.CatalogEntry
import demo.Catalog
import demo.Groups
import demo.bi
import demo.pages.fixtures.people
import demo.pages.fixtures.personItems
import demo.ui.PageFrame
import demo.ui.PageMeta
import demo.ui.RecordedLog
import demo.ui.jsArray
import demo.ui.listen
import io.github.shijianjs.ktjs.dom.element.inlineStyle
import web.cssom.AlignItems
import web.cssom.Display
import web.cssom.px
import js.objects.unsafeJso
import kotlin.js.Date
import kotlin.js.JsAny
import kotlin.js.unsafeCast
import io.github.shijianjs.ktjs.vaadin.avatar.createAvatar
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.checkbox.createCheckbox
import io.github.shijianjs.ktjs.vaadin.checkboxgroup.CheckboxGroupEvents
import io.github.shijianjs.ktjs.vaadin.checkboxgroup.createCheckboxGroup
import io.github.shijianjs.ktjs.vaadin.datepicker.DatePickerEvents
import io.github.shijianjs.ktjs.vaadin.datepicker.createDatePicker
import io.github.shijianjs.ktjs.vaadin.grid.GridColumnElement
import io.github.shijianjs.ktjs.vaadin.grid.createGrid
import io.github.shijianjs.ktjs.vaadin.grid.createGridColumn
import io.github.shijianjs.ktjs.vaadin.horizontallayout.createHorizontalLayout
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.vaadin.integerfield.createIntegerField
import io.github.shijianjs.ktjs.vaadin.messagelist.MessageListItem
import io.github.shijianjs.ktjs.vaadin.messagelist.createMessageList
import io.github.shijianjs.ktjs.vaadin.popover.PopoverEvents
import io.github.shijianjs.ktjs.vaadin.popover.PopoverTriggers
import io.github.shijianjs.ktjs.vaadin.popover.createPopover
import io.github.shijianjs.ktjs.vaadin.select.SelectEvents
import io.github.shijianjs.ktjs.vaadin.select.SelectItemData
import io.github.shijianjs.ktjs.vaadin.select.createSelect
import io.github.shijianjs.ktjs.vaadin.tabs.createTab
import io.github.shijianjs.ktjs.vaadin.tabs.createTabs
import io.github.shijianjs.ktjs.vaadin.tabsheet.createTabSheet
import io.github.shijianjs.ktjs.vaadin.textfield.createTextField
import web.dom.ElementId
import web.dom.clickEvent
import web.events.addHandler

internal class PopoverPage : DemoPage() {
    private val texts = Binder(popoverTexts)

    private val frame = PageFrame(ENTRY)

    private val log = RecordedLog<Boolean>(4)

    private val basicPopover = createPopover()

    private val positionPopover = createPopover()

    private val positionSelect = createSelect()

    private val arrowPopover = createPopover { addTheme("arrow") }

    private val arrowMessage = HtmlTagName.div()

    private val modalPopover = createPopover()

    private val discountField = createTextField()

    private val applyButton = createButton()

    private val discountButton = createButton()

    private val openModalButton = createButton().addTheme("tertiary")

    private val cvvField = createIntegerField { label = "CVV" }

    private val cvvPopover = createPopover { addTheme("arrow") }

    private val rangeInput = createTextField { readonly = true }

    private val presets = createSelect()

    private val fromPicker = createDatePicker()

    private val toPicker = createDatePicker()

    private val rangePopover = createPopover()

    private val unreadList = createMessageList()

    private val allList = createMessageList()

    private val markAllRead = createButton().addTheme("small")

    private val panelHeadingNode = HtmlTagName.h4()

    private val unreadTab = createTab { id = ElementId("unread-tab") }

    private val allTab = createTab { id = ElementId("all-tab") }

    private val userPopover = createPopover { addTheme("no-padding") }

    private val avatarButton = createButton().addTheme("icon", "tertiary")

    private val menuLinks = kitCol()

    private val columnsPopover = createPopover()

    private val columnGroup = createCheckboxGroup { addTheme("vertical") }

    private val columnHeaders =
        mutableListOf<Triple<GridColumnElement, (PopoverTexts) -> String, (PopoverTexts) -> String>>()

    private val columnsButton = createButton().addTheme("icon", "tertiary")

    private val configureColumns = HtmlTagName.strong()

    private val showAllButton = createButton().addTheme("small")

    private val resetButton = createButton().addTheme("small")

    init {
        frame.mount(this)

        val basic = SectionBlock.NAME { show() }
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        val trigger = createButton().addTheme("primary")
        texts.bind(trigger) { it.clickToOpen }
        trigger.id = ElementId("popover-trigger")
        trigger.dataset["test"] = "popover-trigger"
        basicPopover.`for` = "popover-trigger"
        val overlayHeading = HtmlTagName.h3()
        texts.bind(overlayHeading, { it.overlayContent }) { overlayHeading.textContent = it }
        val overlayText = paragraph("")
        texts.bind(overlayText, { it.anyDom }) { overlayText.textContent = it }
        basicPopover.append(kitCol(overlayHeading, overlayText))
        basicPopover.listen(PopoverEvents.openedChanged) {
            log.add(basicPopover.opened) { opened -> texts.current.openedLine(opened) }
        }
        basic.body.append(exampleBox(null, kitCol(kitRow(trigger, basicPopover), log.root)))
        body.append(basic.root)

        val positioning = SectionBlock.NAME { show() }
        texts.bind(positioning.heading) { it.positionHeading }
        texts.bind(positioning.hint) { it.positionHint }
        val positionTarget = createButton().addTheme("primary")
        texts.bind(positionTarget) { it.open }
        positionTarget.id = ElementId("popover-position-target")
        texts.bind(positionSelect, { it.position }) { positionSelect.label = it }
        positionSelect.value = "bottom"
        positionPopover.`for` = "popover-position-target"
        positionPopover.position = "bottom"
        val positionContent = paragraph("")
        texts.bind(positionContent, { it.popoverContent }) { positionContent.textContent = it }
        positionPopover.append(positionContent)
        positionSelect.listen(SelectEvents.change) {
            positionPopover.position = positionSelect.value
        }
        positioning.body.append(
            exampleBox(
                null,
                kitCol(
                    kitRow(positionTarget, positionSelect).apply { style.setProperty("align-items", "flex-start") },
                    positionPopover,
                ),
            ),
        )
        body.append(positioning.root)

        val arrow = SectionBlock.NAME { show() }
        texts.bind(arrow.heading) { it.arrowHeading }
        texts.bind(arrow.hint) { it.arrowHint }
        val bell = newIconButton("lumo:bell")
        texts.bind(bell, { it.notifications }) { bell.ariaLabel = it }
        bell.id = ElementId("popover-arrow-trigger")
        arrowPopover.`for` = "popover-arrow-trigger"
        texts.bind(arrowMessage, { it.noNotifications }) { arrowMessage.textContent = it }
        arrowPopover.append(arrowMessage)
        arrow.body.append(exampleBox(null, kitRow(bell, arrowPopover)))
        body.append(arrow.root)

        val modal = SectionBlock.NAME { show() }
        texts.bind(modal.heading) { it.modalHeading }
        texts.bind(modal.hint) { it.modalHint }
        texts.bind(discountButton) { it.discount }
        discountButton.id = ElementId("popover-modal-trigger")
        modalPopover.`for` = "popover-modal-trigger"
        modalPopover.modal = true
        modalPopover.withBackdrop = true
        texts.bind(discountField, { it.discountCode }) { discountField.label = it }
        texts.bind(applyButton) { it.apply }
        applyButton.clickEvent.addHandler { modalPopover.opened = false }
        modalPopover.append(kitCol(discountField, applyButton))
        texts.bind(openModalButton) { it.openModal }
        openModalButton.clickEvent.addHandler { modalPopover.opened = true }
        modal.body.append(exampleBox(null, kitCol(kitRow(discountButton, modalPopover), openModalButton)))
        body.append(modal.root)

        val interactive = SectionBlock.NAME { show() }
        texts.bind(interactive.heading) { it.interactiveHeading }
        texts.bind(interactive.hint) { it.interactiveHint }
        cvvField.id = ElementId("popover-cvv-field")
        cvvField.style.setProperty("width", "6rem")
        cvvPopover.`for` = "popover-cvv-field"
        cvvPopover.trigger = arrayOf(PopoverTriggers.hover, PopoverTriggers.focus)
        cvvPopover.position = "top"
        val cvvTitle = HtmlTagName.h3().apply { style.setProperty("font-size", "1rem") }
        texts.bind(cvvTitle, { it.cvvHeading }) { cvvTitle.textContent = it }
        val cvvText = paragraph("").apply { style.setProperty("max-width", "20rem") }
        texts.bind(cvvText, { it.cvvText }) { cvvText.textContent = it }
        val cvvAnchor = kitAnchor("", "https://www.cvvnumber.com/cvv.html")
        texts.bind(cvvAnchor, { it.cvvLink }) { cvvAnchor.textContent = it }
        cvvPopover.append(cvvTitle, cvvText, cvvAnchor)
        interactive.body.append(exampleBox(null, cvvField))
        interactive.body.append(cvvPopover)
        body.append(interactive.root)

        val rangeField = SectionBlock.NAME { show() }
        texts.bind(rangeField.heading) { it.rangeHeading }
        texts.bind(rangeField.hint) { it.rangeHint }
        texts.bind(rangeInput, { it.searchRange }) { rangeInput.label = it }
        rangeInput.id = ElementId("popover-range-field")
        rangeInput.style.setProperty("width", "340px")
        rangeInput.append(createIcon { icon = "lumo:dropdown" }.also { it.slot = "suffix" })
        texts.bind(presets, { it.commonRanges }) { presets.label = it }
        texts.bind(presets, { it.selectPreset }) { presets.placeholder = it }
        presets.style.setProperty("width", "100%")
        texts.bind(fromPicker, { it.from }) { fromPicker.label = it }
        fromPicker.style.setProperty("width", "150px")
        texts.bind(toPicker, { it.to }) { toPicker.label = it }
        toPicker.style.setProperty("width", "150px")
        rangePopover.`for` = "popover-range-field"
        rangePopover.modal = true
        rangePopover.position = "bottom-start"
        rangePopover.width = "340px"
        texts.bind(rangePopover, { it.rangeAria }) { rangePopover.ariaLabel = it }
        rangePopover.append(
            kitCol(
                presets,
                kitRow(fromPicker, HtmlTagName.div { textContent = "−" }, toPicker),
            ),
        )
        rangeInput.listen(KEY_DOWN) { event ->
            if (event.key == "ArrowDown") rangePopover.opened = true
        }
        presets.listen(SelectEvents.change) {
            val today = Date()
            val from =
                when (presets.value) {
                    "last-week" -> Date(today.getTime() - 7 * MILLIS_PER_DAY)
                    "last-month" -> Date(today.getFullYear(), today.getMonth() - 1, today.getDate())
                    "year-to-date" -> Date(today.getFullYear(), 0, 1)
                    "last-year" -> Date(today.getFullYear() - 1, today.getMonth(), today.getDate())
                    "past-5-years" -> Date(today.getFullYear() - 5, today.getMonth(), today.getDate())
                    else -> Date(today.getTime())
                }
            fromPicker.value = isoDate(from)
            toPicker.value = isoDate(today)
            rangeInput.value = "${isoDate(from)} − ${isoDate(today)}"
            rangePopover.opened = false
        }
        fromPicker.listen(DatePickerEvents.change) {
            presets.value = ""
            rangeInput.value = "${fromPicker.value} − ${toPicker.value}"
        }
        toPicker.listen(DatePickerEvents.change) {
            presets.value = ""
            rangeInput.value = "${fromPicker.value} − ${toPicker.value}"
        }
        rangeField.body.append(exampleBox(null, kitCol(rangeInput, rangePopover)))
        body.append(rangeField.root)

        val panel = SectionBlock.NAME { show() }
        texts.bind(panel.heading) { it.panelHeading }
        texts.bind(panel.hint) { it.panelHint }
        val notificationBell = newIconButton("lumo:bell")
        texts.bind(notificationBell, { it.notifications }) { notificationBell.ariaLabel = it }
        notificationBell.id = ElementId("popover-notifications-trigger")
        val notificationsPopover = createPopover { addTheme("arrow", "no-padding") }
        notificationsPopover.`for` = "popover-notifications-trigger"
        notificationsPopover.modal = true
        notificationsPopover.width = "300px"
        notificationsPopover.position = "bottom"
        texts.bind(markAllRead) { it.markAllRead }
        markAllRead.clickEvent.addHandler { unreadList.items = emptyArray() }
        texts.bind(panelHeadingNode, { it.notifications }) { panelHeadingNode.textContent = it }
        val panelHeader =
            createHorizontalLayout().apply {
                style.setProperty("align-items", "center")
                style.setProperty("padding", "var(--vaadin-padding-l) var(--vaadin-padding-l) var(--vaadin-padding-s)")
                append(panelHeadingNode, markAllRead.also { it.slot = "end" })
            }
        texts.bind(unreadTab, { it.unread }) { unreadTab.textContent = it }
        texts.bind(allTab, { it.all }) { allTab.textContent = it }
        val tabs = createTabs { append(unreadTab, allTab) }
        tabs.slot = "tabs"
        val tabSheet = createTabSheet { addTheme("small", "no-padding", "no-border") }
        tabSheet.append(tabs)
        tabSheet.append(HtmlTagName.div { append(unreadList) }.apply { setAttribute("tab", "unread-tab") })
        tabSheet.append(HtmlTagName.div { append(allList) }.apply { setAttribute("tab", "all-tab") })
        notificationsPopover.append(panelHeader, tabSheet)
        panel.body.append(exampleBox(null, kitRow(notificationBell, notificationsPopover)))
        body.append(panel.root)

        val userMenuSection = SectionBlock.NAME { show() }
        texts.bind(userMenuSection.heading) { it.userMenuHeading }
        texts.bind(userMenuSection.hint) { it.userMenuHint }
        val user = people(1).first()
        avatarButton.style.setProperty("padding", "0")
        avatarButton.style.setProperty("border-radius", "50%")
        avatarButton.append(
            createAvatar { img = user.pictureUrl; name = user.displayName }
                .also { it.tabIndex = -1 },
        )
        avatarButton.id = ElementId("popover-avatar-trigger")
        userPopover.`for` = "popover-avatar-trigger"
        userPopover.modal = true
        userPopover.position = "bottom-end"
        userPopover.role = AriaRole.menu
        texts.bind(userPopover, { it.userMenu }) { userPopover.ariaLabel = it }
        val nickName = "@${user.firstName}${user.lastName}".lowercase()
        val userCard =
            HtmlTagName.div().apply {
                inlineStyle.apply {
                    display = Display.flex
                    alignItems = AlignItems.center
                    columnGap = 8.px
                    padding = 8.px
                }
                append(
                    createAvatar { img = user.pictureUrl; name = user.displayName },
                    HtmlTagName.span { textContent = user.displayName },
                    HtmlTagName.span { textContent = nickName },
                )
            }
        // Menu rows: the anchor fills the popover width and has no underline, so the whole row is the
        // target. The popover itself is a `no-padding` one; the padding lives here.
        fun menuLink(copy: (PopoverTexts) -> String) {
            val anchor = kitAnchor("", "#").apply {
                role = AriaRole.menuitem
                style.setProperty("display", "block")
                style.setProperty("padding", "4px 16px")
                style.setProperty("text-decoration", "none")
            }
            texts.bind(anchor, copy) { anchor.textContent = it }
            menuLinks.append(anchor)
        }
        menuLink { it.userProfile }
        menuLink { it.preferences }
        menuLink { it.signOut }
        userPopover.append(kitCol(userCard, menuLinks))
        userMenuSection.body.append(exampleBox(null, kitRow(avatarButton, userPopover)))
        body.append(userMenuSection.root)

        val columns = SectionBlock.NAME { show() }
        texts.bind(columns.heading) { it.columnsHeading }
        texts.bind(columns.hint) { it.columnsHint }
        val grid = createGrid()
        grid.items = personItems(people(5))
        grid.allRowsVisible = true
        val columnDefs: List<Pair<String, (PopoverTexts) -> String>> =
            listOf(
                "firstName" to { copy: PopoverTexts -> copy.firstName },
                "lastName" to { copy: PopoverTexts -> copy.lastName },
                "email" to { copy: PopoverTexts -> copy.email },
                "profession" to { copy: PopoverTexts -> copy.profession },
            )
        val columnElements = mutableListOf<Pair<String, GridColumnElement>>()
        columnDefs.forEach { (path, copy) ->
            val column = createGridColumn { this.path = path }
            texts.bind(column, copy) { column.header = it }
            columnElements += path to column
            grid.append(column)
        }
        texts.bind(columnsButton, { it.showHideColumns }) { columnsButton.ariaLabel = it }
        columnsButton.append(createIcon { icon = "vaadin:grid-h" })
        columnsButton.id = ElementId("popover-columns-trigger")
        columnsPopover.`for` = "popover-columns-trigger"
        columnsPopover.modal = true
        columnsPopover.withBackdrop = true
        columnsPopover.position = "bottom-end"
        columnGroup.value = jsArray<String>("""["firstName", "lastName", "email", "profession"]""")
        columnDefs.forEach { (path, copy) ->
            val node = createCheckbox { value = path; checked = true }
            texts.bind(node, copy) { node.textContent = it }
            columnGroup.append(node)
        }
        columnGroup.listen(CheckboxGroupEvents.valueChanged) {
            val visible = columnGroup.value.toList()
            columnElements.forEach { (path, column) -> column.hiddenProperty = path !in visible }
        }
        texts.bind(configureColumns, { it.configureColumns }) { configureColumns.textContent = it }
        texts.bind(showAllButton) { it.showAll }
        showAllButton.clickEvent.addHandler {
            columnGroup.value = jsArray<String>("""["firstName", "lastName", "email", "profession"]""")
        }
        texts.bind(resetButton) { it.reset }
        resetButton.clickEvent.addHandler {
            columnGroup.value = jsArray<String>("""["firstName", "lastName", "email", "profession"]""")
        }
        val columnsActions =
            createHorizontalLayout().apply {
                style.setProperty("justify-content", "space-between")
                style.setProperty("gap", "var(--vaadin-gap-s)")
                append(showAllButton, resetButton)
            }
        columnsPopover.append(kitCol(configureColumns, columnGroup, columnsActions))
        columns.body.append(exampleBox(null, kitCol(kitCol(columnsButton, columnsPopover), grid)))
        body.append(columns.root)

        applyTranslation(Langs.EN)
    }

    private fun newIconButton(icon: String): io.github.shijianjs.ktjs.vaadin.button.ButtonElement =
        createButton { textContent = "" }.addTheme("icon", "tertiary").also { node ->
            node.append(createIcon { this.icon = icon })
        }

    override fun applyTranslation(lang: Lang) {
        val meta = ENTRY.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        positionSelect.items =
            selectItems(
                """
                [
                  {"label": "Top Start", "value": "top-start"},
                  {"label": "Top", "value": "top"},
                  {"label": "Top End", "value": "top-end"},
                  {"label": "Bottom Start", "value": "bottom-start"},
                  {"label": "Bottom", "value": "bottom"},
                  {"label": "Bottom End", "value": "bottom-end"},
                  {"label": "Start Top", "value": "start-top"},
                  {"label": "Start", "value": "start"},
                  {"label": "Start Bottom", "value": "start-bottom"},
                  {"label": "End Top", "value": "end-top"},
                  {"label": "End", "value": "end"},
                  {"label": "End Bottom", "value": "end-bottom"}
                ]
                """.trimIndent(),
            )
        positionSelect.value = positionPopover.position
        val copy = texts.current
        presets.items =
            selectData(
                copy.today to "today",
                copy.lastWeek to "last-week",
                copy.lastMonth to "last-month",
                copy.yearToDate to "year-to-date",
                copy.lastYear to "last-year",
                copy.past5Years to "past-5-years",
            )
        presets.value = ""
        unreadList.items = notificationMessages(3, copy)
        allList.items = notificationMessages(4, copy)
        log.replay(false)
    }

    companion object : PopoverPageDefaultReference() {
        val ENTRY: CatalogEntry =
            CatalogEntry(
                id = "popover",
                group = Groups.OVERLAYS,
                meta =
                    PageMeta(
                        title = bi("Popover", "Popover"),
                        summary =
                            bi(
                                "锚定到某个元素的浮层，支持点击 / 悬停触发、位置与模态模式。",
                                "An overlay anchored to an element, with click / hover triggers, positions and " +
                                    "a modal mode.",
                            ),
                        docsUrl = "${Catalog.DOCS_BASE}/popover",
                        sourcePath = "pages/overlays/PopoverPage.kt",
                    ),
                kitPage = { PopoverPage.NAME() },
            )

        // endregion

        // region Popover

        private val english: PopoverTexts =
            PopoverTexts(
                basicHeading = "Basic usage",
                basicHint = "popover points at the trigger's id through for; it opens on click and closes on an " +
                    "outside click.",
                clickToOpen = "Click to open",
                overlayContent = "Overlay content",
                anyDom = "Any DOM goes into the popover's default slot.",
                openedLine = { "opened-changed · $it" },
                positionHeading = "Position",
                positionHint = "position accepts top / bottom / start / end and their combinations, twelve values " +
                    "in total.",
                open = "Open",
                position = "Position",
                popoverContent = "Popover content",
                arrowHeading = "Arrow and icon trigger",
                arrowHint = "theme=\"arrow\" draws an arrow on the overlay edge that points at the trigger.",
                notifications = "Notifications",
                noNotifications = "No new notifications",
                modalHeading = "Modal and backdrop",
                modalHint = "modal blocks the rest of the page and withBackdrop shows the backdrop.",
                discount = "Discount",
                discountCode = "Discount code",
                apply = "Apply",
                openModal = "Open modal popover",
                interactiveHeading = "Interactive hint",
                interactiveHint = "trigger decides which interactions open the overlay; here both hover and " +
                    "focus open it, and the link inside stays clickable.",
                cvvHeading = "Card Verification Value",
                cvvText = "A three or four digit code, usually printed on the back of the card, next to or at " +
                    "the end of the signature strip.",
                cvvLink = "See where to find CVV on different cards",
                rangeHeading = "Dropdown field",
                rangeHint = "The overlay holds a Select and two date pickers; pressing ArrowDown also opens it.",
                searchRange = "Search date range",
                commonRanges = "Common ranges",
                selectPreset = "Select preset",
                today = "Today",
                lastWeek = "Last week",
                lastMonth = "Last month",
                yearToDate = "Year to date",
                lastYear = "Last year",
                past5Years = "Past 5 years",
                from = "From",
                to = "To",
                rangeAria = "Select a date range",
                panelHeading = "Notification panel",
                panelHint = "An overlay can carry a whole panel: a TabSheet switches between unread and all " +
                    "messages, and a Message List renders the items.",
                markAllRead = "Mark all read",
                unread = "Unread",
                all = "All",
                message = { index ->
                    listOf(
                        "Could you send me the latest TPS report from the ACME project?",
                        "Hey, are we on track for the trade show next month?",
                        "TPS reports look good! I'm going to pass it on to Alliyah next.",
                        "Hi, are you going to attend the brainstorming session tomorrow?",
                    )[index % 4]
                },
                userMenuHeading = "User menu",
                userMenuHint = "The avatar button opens a popover without padding that holds the account details " +
                    "and links.",
                userMenu = "User menu",
                userProfile = "User profile",
                preferences = "Preferences",
                signOut = "Sign out",
                columnsHeading = "Form in a popover",
                columnsHint = "The checked values of the Checkbox Group drive the hidden state of the grid columns " +
                    "and survive closing the overlay.",
                showHideColumns = "Show / hide columns",
                firstName = "First name",
                lastName = "Last name",
                email = "Email",
                profession = "Profession",
                configureColumns = "Configure columns",
                showAll = "Show all",
                reset = "Reset",
            )
        private val chinese: PopoverTexts =
            PopoverTexts(
                basicHeading = "基本用法",
                basicHint = "popover 通过 for 指向触发元素的 id；默认点击触发、点击外部关闭。",
                clickToOpen = "点击查看",
                overlayContent = "浮层内容",
                anyDom = "任意 DOM 都可以放进 popover 的默认插槽。",
                openedLine = { "opened-changed · $it" },
                positionHeading = "位置",
                positionHint = "position 接受 top / bottom / start / end 与它们的组合，共十二个值。",
                open = "打开",
                position = "位置",
                popoverContent = "浮层内容",
                arrowHeading = "箭头与图标触发器",
                arrowHint = "theme=\"arrow\" 在浮层边缘画一个指向触发元素的箭头。",
                notifications = "通知",
                noNotifications = "没有新通知",
                modalHeading = "模态与背景",
                modalHint = "modal 会阻止页面其他交互，withBackdrop 打开遮罩。",
                discount = "折扣",
                discountCode = "折扣码",
                apply = "应用",
                openModal = "打开模态浮层",
                interactiveHeading = "交互式提示",
                interactiveHint = "trigger 决定哪些交互会打开浮层；这里悬停与聚焦都会打开，且可以在浮层里点击链接。",
                cvvHeading = "卡片验证码",
                cvvText = "通常印在卡片背面签名条旁边或末尾的三到四位数。",
                cvvLink = "查看不同卡片上的 CVV 位置",
                rangeHeading = "下拉字段",
                rangeHint = "浮层里放 Select 与两个日期选择器；按下箭头键也能打开浮层。",
                searchRange = "搜索日期范围",
                commonRanges = "常用范围",
                selectPreset = "选择预设",
                today = "今天",
                lastWeek = "上周",
                lastMonth = "上个月",
                yearToDate = "今年至今",
                lastYear = "去年",
                past5Years = "过去五年",
                from = "从",
                to = "到",
                rangeAria = "选择日期范围",
                panelHeading = "通知面板",
                panelHint = "浮层可以承载完整的面板：Tabsheet 切换未读与全部消息，Message List 渲染条目。",
                markAllRead = "全部标为已读",
                unread = "未读",
                all = "全部",
                message = { index ->
                    listOf(
                        "可以发我 ACME 项目最新的 TPS 报告吗？",
                        "下个月的展会我们还来得及吗？",
                        "报告看起来不错，我转给 Alliyah。",
                        "明天的头脑风暴你会来吗？",
                    )[index % 4]
                },
                userMenuHeading = "用户菜单",
                userMenuHint = "头像按钮打开一个无内边距的浮层，里面是账号信息和菜单项。",
                userMenu = "用户菜单",
                userProfile = "用户资料",
                preferences = "偏好设置",
                signOut = "退出登录",
                columnsHeading = "浮层里的表单",
                columnsHint = "Checkbox Group 的选中值直接控制 Grid 列的 hidden；浮层关闭时改动仍然保留。",
                showHideColumns = "显示 / 隐藏列",
                firstName = "名",
                lastName = "姓",
                email = "邮箱",
                profession = "职务",
                configureColumns = "配置列",
                showAll = "全部显示",
                reset = "重置",
            )
        /** Select items described by a JSON array of `{label, value}` objects. */
        private fun selectItems(json: String): Array<SelectItemData> =
            jsArray<JsAny>(json).unsafeCast<Array<SelectItemData>>()
        private fun selectData(vararg pairs: Pair<String, String>): Array<SelectItemData> =
            pairs
                .map { (label, value) -> unsafeJso<SelectItemData> { this.label = label; this.value = value } }
                .toTypedArray()
        private val popoverTexts: Translations<PopoverTexts> = texts(
            Langs.EN to english,
            Langs.ZH_HANS to chinese,
        )
        private fun notificationMessages(count: Int, copy: PopoverTexts): Array<MessageListItem> {
            val list = people(count)
            return list.mapIndexed { index, person ->
                unsafeJso<MessageListItem> {
                    userName = person.displayName
                    userImg = person.pictureUrl
                    time = "${9 + index}:0${index % 6}"
                    text = copy.message(index)
                }
            }.toTypedArray()
        }
        private const val MILLIS_PER_DAY = 86_400_000.0
        private fun isoDate(date: Date): String {
            val year = date.getFullYear()
            val month = date.getMonth() + 1
            val day = date.getDate()
            return "$year-${pad2(month)}-${pad2(day)}"
        }
    }
}
