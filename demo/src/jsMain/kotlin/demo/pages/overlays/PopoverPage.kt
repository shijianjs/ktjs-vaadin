package demo.pages.overlays

import io.github.shijianjs.ktjs.demo.page.col as kitCol
import io.github.shijianjs.ktjs.demo.page.exampleBox
import io.github.shijianjs.ktjs.demo.page.row as kitRow
import io.github.shijianjs.ktjs.demo.page.sectionBlock


import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import web.components.CustomElementReference
import web.dom.TagName
import web.html.HtmlTagName
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import io.github.shijianjs.ktjs.demo.page.anchor as kitAnchor
import io.github.shijianjs.ktjs.demo.page.paragraph
import io.github.shijianjs.ktjs.demo.page.heading
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.pages.fixtures.people
import demo.pages.fixtures.personItems
import demo.tr
import demo.ui.EventLog
import demo.ui.PageMeta
import demo.ui.configureFrame
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

val popoverEntry: CatalogEntry =
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
                docsUrl = "$DOCS_BASE/popover",
                sourcePath = "pages/overlays/PopoverPage.kt",
            ),
        kitPage = { newPopoverPage() },
        rebuildsOnLanguage = true,
    )

/** Select items described by a JSON array of `{label, value}` objects. */
private fun selectItems(json: String): Array<SelectItemData> =
    jsArray<JsAny>(json).unsafeCast<Array<SelectItemData>>()

private fun selectData(vararg pairs: Pair<String, String>): Array<SelectItemData> =
    pairs
        .map { (label, value) -> unsafeJso<SelectItemData> { this.label = label; this.value = value } }
        .toTypedArray()

// endregion

// region Popover

internal class PopoverPage : DemoPage() {

    init {
        configureFrame(popoverEntry)

        val basic =
            sectionBlock(
                tr("基本用法", "Basic usage"),
                tr(
                    "popover 通过 for 指向触发元素的 id；默认点击触发、点击外部关闭。",
                    "popover points at the trigger's id through for; it opens on click and closes on an outside click.",
                ),
            )
        val log = EventLog(4)
        val trigger = createButton { textContent = tr("点击查看", "Click to open") }.addTheme("primary")
        trigger.id = ElementId("popover-trigger")
        trigger.dataset["test"] = "popover-trigger"

        val popover = createPopover()
        popover.`for` = "popover-trigger"
        popover.append(
            kitCol(
                heading(3, tr("浮层内容", "Overlay content")),
                paragraph(tr("任意 DOM 都可以放进 popover 的默认插槽。", "Any DOM goes into the popover's default slot.")),
            ),
        )
        popover.listen(PopoverEvents.openedChanged) { log.add("opened-changed · ${popover.opened}") }

        basic.body.append(exampleBox(null, kitCol(kitRow(trigger, popover), log.root)))
        body.append(basic.root)

        val positioning =
            sectionBlock(
                tr("位置", "Position"),
                tr(
                    "position 接受 top / bottom / start / end 与它们的组合，共十二个值。",
                    "position accepts top / bottom / start / end and their combinations, twelve values in total.",
                ),
            )
        val positionTarget = createButton { textContent = tr("打开", "Open") }.addTheme("primary")
        positionTarget.id = ElementId("popover-position-target")
        val positionSelect =
            createSelect {
                label = tr("位置", "Position")
                items =
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
                value = "bottom"
            }
        val positionPopover = createPopover()
        positionPopover.`for` = "popover-position-target"
        positionPopover.position = "bottom"
        positionPopover.append(paragraph(tr("浮层内容", "Popover content")))
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

        val arrow =
            sectionBlock(
                tr("箭头与图标触发器", "Arrow and icon trigger"),
                tr(
                    "theme=\"arrow\" 在浮层边缘画一个指向触发元素的箭头。",
                    "theme=\"arrow\" draws an arrow on the overlay edge that points at the trigger.",
                ),
            )
        val bell = newIconButton(tr("通知", "Notifications"), "lumo:bell")
        bell.id = ElementId("popover-arrow-trigger")
        val arrowPopover = createPopover { addTheme("arrow") }
        arrowPopover.`for` = "popover-arrow-trigger"
        arrowPopover.append(HtmlTagName.div().apply { textContent = tr("没有新通知", "No new notifications") })
        arrow.body.append(exampleBox(null, kitRow(bell, arrowPopover)))
        body.append(arrow.root)

        val modal =
            sectionBlock(
                tr("模态与背景", "Modal and backdrop"),
                tr(
                    "modal 会阻止页面其他交互，withBackdrop 打开遮罩。",
                    "modal blocks the rest of the page and withBackdrop shows the backdrop.",
                ),
            )
        val discountButton = createButton { textContent = tr("折扣", "Discount") }
        discountButton.id = ElementId("popover-modal-trigger")
        val modalPopover = createPopover()
        modalPopover.`for` = "popover-modal-trigger"
        modalPopover.modal = true
        modalPopover.withBackdrop = true
        val discountField = createTextField { label = tr("折扣码", "Discount code") }
        val applyDiscount = createButton { textContent = tr("应用", "Apply") }
        applyDiscount.clickEvent.addHandler { modalPopover.opened = false }
        modalPopover.append(kitCol(discountField, applyDiscount))
        modal.body.append(
            exampleBox(
                null,
                kitCol(
                    kitRow(discountButton, modalPopover),
                    createButton { textContent = tr("打开模态浮层", "Open modal popover") }.addTheme("tertiary").apply {
                        clickEvent.addHandler { modalPopover.opened = true }
                    },
                ),
            ),
        )
        body.append(modal.root)

        val interactive =
            sectionBlock(
                tr("交互式提示", "Interactive hint"),
                tr(
                    "trigger 决定哪些交互会打开浮层；这里悬停与聚焦都会打开，且可以在浮层里点击链接。",
                    "trigger decides which interactions open the overlay; here both hover and focus open it, " +
                        "and the link inside stays clickable.",
                ),
            )
        val cvvField = createIntegerField { label = "CVV" }
        cvvField.id = ElementId("popover-cvv-field")
        cvvField.style.setProperty("width", "6rem")
        val cvvPopover = createPopover { addTheme("arrow") }
        cvvPopover.`for` = "popover-cvv-field"
        cvvPopover.trigger = arrayOf(PopoverTriggers.hover, PopoverTriggers.focus)
        cvvPopover.position = "top"
        cvvPopover.append(
            heading(3, tr("卡片验证码", "Card Verification Value")).apply { style.setProperty("font-size", "1rem") },
            paragraph(
                tr(
                    "通常印在卡片背面签名条旁边或末尾的三到四位数。",
                    "A three or four digit code, usually printed on the back of the card, next to or at the end " +
                        "of the signature strip.",
                ),
            ).apply { style.setProperty("max-width", "20rem") },
            kitAnchor(tr("查看不同卡片上的 CVV 位置", "See where to find CVV on different cards"), "https://www.cvvnumber.com/cvv.html"),
        )
        interactive.body.append(exampleBox(null, cvvField))
        interactive.body.append(cvvPopover)
        body.append(interactive.root)

        val rangeField =
            sectionBlock(
                tr("下拉字段", "Dropdown field"),
                tr(
                    "浮层里放 Select 与两个日期选择器；按下箭头键也能打开浮层。",
                    "The overlay holds a Select and two date pickers; pressing ArrowDown also opens it.",
                ),
            )
        val rangeInput = createTextField {
            label = tr("搜索日期范围", "Search date range")
            readonly = true
        }
        rangeInput.id = ElementId("popover-range-field")
        rangeInput.style.setProperty("width", "340px")
        rangeInput.append(createIcon { icon = "lumo:dropdown" }.also { it.slot = "suffix" })
        val presets =
            createSelect {
                label = tr("常用范围", "Common ranges")
                placeholder = tr("选择预设", "Select preset")
                items =
                    selectData(
                        tr("今天", "Today") to "today",
                        tr("上周", "Last week") to "last-week",
                        tr("上个月", "Last month") to "last-month",
                        tr("今年至今", "Year to date") to "year-to-date",
                        tr("去年", "Last year") to "last-year",
                        tr("过去五年", "Past 5 years") to "past-5-years",
                    )
            }
        presets.style.setProperty("width", "100%")
        val fromPicker = createDatePicker { label = tr("从", "From") }
        fromPicker.style.setProperty("width", "150px")
        val toPicker = createDatePicker { label = tr("到", "To") }
        toPicker.style.setProperty("width", "150px")
        val rangePopover = createPopover()
        rangePopover.`for` = "popover-range-field"
        rangePopover.modal = true
        rangePopover.position = "bottom-start"
        rangePopover.width = "340px"
        rangePopover.setAttribute("aria-label", tr("选择日期范围", "Select a date range"))
        rangePopover.append(
            kitCol(
                presets,
                kitRow(fromPicker, HtmlTagName.div().apply { textContent = "−" }, toPicker),
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

        val panel =
            sectionBlock(
                tr("通知面板", "Notification panel"),
                tr(
                    "浮层可以承载完整的面板：Tabsheet 切换未读与全部消息，Message List 渲染条目。",
                    "An overlay can carry a whole panel: a TabSheet switches between unread and all messages, " +
                        "and a Message List renders the items.",
                ),
            )
        val notificationBell = newIconButton(tr("通知", "Notifications"), "lumo:bell")
        notificationBell.id = ElementId("popover-notifications-trigger")
        val notificationsPopover = createPopover { addTheme("arrow", "no-padding") }
        notificationsPopover.`for` = "popover-notifications-trigger"
        notificationsPopover.modal = true
        notificationsPopover.width = "300px"
        notificationsPopover.position = "bottom"

        val unreadItems = notificationMessages(3)
        val allItems = notificationMessages(4)
        val unreadList = createMessageList()
        unreadList.items = unreadItems
        val allList = createMessageList()
        allList.items = allItems
        val markAllRead =
            createButton { textContent = tr("全部标为已读", "Mark all read") }.addTheme("small").apply {
                clickEvent.addHandler { unreadList.items = emptyArray() }
            }
        val panelHeader =
            createHorizontalLayout().apply {
                style.setProperty("align-items", "center")
                style.setProperty("padding", "var(--vaadin-padding-l) var(--vaadin-padding-l) var(--vaadin-padding-s)")
                append(heading(4, tr("通知", "Notifications")), markAllRead.also { it.slot = "end" })
            }
        val tabs = createTabs {
            append(
                createTab { id = ElementId("unread-tab"); textContent = tr("未读", "Unread") },
                createTab { id = ElementId("all-tab"); textContent = tr("全部", "All") },
            )
        }
        tabs.slot = "tabs"
        val tabSheet = createTabSheet { addTheme("small", "no-padding", "no-border") }
        tabSheet.append(tabs)
        tabSheet.append(HtmlTagName.div {
                            append(unreadList)
                        }.apply { setAttribute("tab", "unread-tab") })
        tabSheet.append(HtmlTagName.div {
                            append(allList)
                        }.apply { setAttribute("tab", "all-tab") })
        notificationsPopover.append(panelHeader, tabSheet)
        panel.body.append(exampleBox(null, kitRow(notificationBell, notificationsPopover)))
        body.append(panel.root)

        val userMenu =
            sectionBlock(
                tr("用户菜单", "User menu"),
                tr(
                    "头像按钮打开一个无内边距的浮层，里面是账号信息和菜单项。",
                    "The avatar button opens a popover without padding that holds the account details and links.",
                ),
            )
        val user = people(1).first()
        val avatarButton = createButton { textContent = "" }.addTheme("icon", "tertiary").apply {
            style.setProperty("padding", "0")
            style.setProperty("border-radius", "50%")
            append(
                createAvatar {
                    img = user.pictureUrl
                    name = user.displayName
                }.also { it.setAttribute("tabindex", "-1") },
            )
        }
        avatarButton.id = ElementId("popover-avatar-trigger")
        val userPopover = createPopover { addTheme("no-padding") }
        userPopover.`for` = "popover-avatar-trigger"
        userPopover.modal = true
        userPopover.position = "bottom-end"
        userPopover.setAttribute("role", "menu")
        userPopover.setAttribute("aria-label", tr("用户菜单", "User menu"))
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
                    createAvatar {
                        img = user.pictureUrl
                        name = user.displayName
                    },
                    HtmlTagName.span {
                        textContent = user.displayName
                    },
                    HtmlTagName.span {
                        textContent = nickName
                    },
                )
            }
        // Menu rows: the anchor fills the popover width and has no underline, so the whole row is the
        // target. The popover itself is a `no-padding` one; the padding lives here.
        val menuLinks = HtmlTagName.div()
        listOf(tr("用户资料", "User profile"), tr("偏好设置", "Preferences"), tr("退出登录", "Sign out")).forEach { label ->
            menuLinks.append(
                kitAnchor(label, "#").apply {
                    setAttribute("role", "menuitem")
                    style.setProperty("display", "block")
                    style.setProperty("padding", "4px 16px")
                    style.setProperty("text-decoration", "none")
                },
            )
        }
        userPopover.append(kitCol(userCard, menuLinks))
        userMenu.body.append(exampleBox(null, kitRow(avatarButton, userPopover)))
        body.append(userMenu.root)

        val columns =
            sectionBlock(
                tr("浮层里的表单", "Form in a popover"),
                tr(
                    "Checkbox Group 的选中值直接控制 Grid 列的 hidden；浮层关闭时改动仍然保留。",
                    "The checked values of the Checkbox Group drive the hidden state of the grid columns and " +
                        "survive closing the overlay.",
                ),
            )
        val grid = createGrid()
        grid.items = personItems(people(5))
        grid.allRowsVisible = true
        val columnDefs =
            listOf(
                "firstName" to tr("名", "First name"),
                "lastName" to tr("姓", "Last name"),
                "email" to tr("邮箱", "Email"),
                "profession" to tr("职务", "Profession"),
            )
        val columnElements = mutableListOf<Pair<String, io.github.shijianjs.ktjs.vaadin.grid.GridColumnElement>>()
        columnDefs.forEach { (path, label) ->
            val column = createGridColumn {
                header = label
                this.path = path
            }
            columnElements += path to column
            grid.append(column)
        }
        val columnsButton = newIconButton(tr("显示 / 隐藏列", "Show / hide columns"), "vaadin:grid-h")
        columnsButton.id = ElementId("popover-columns-trigger")
        val columnsPopover = createPopover()
        columnsPopover.`for` = "popover-columns-trigger"
        columnsPopover.modal = true
        columnsPopover.withBackdrop = true
        columnsPopover.position = "bottom-end"
        val columnGroup = createCheckboxGroup { addTheme("vertical") }
        columnGroup.value = jsArray<String>("""["firstName", "lastName", "email", "profession"]""")
        columnDefs.forEach { (path, label) ->
            columnGroup.append(
                createCheckbox {
                    value = path
                    textContent = label
                    checked = true
                },
            )
        }
        columnGroup.listen(CheckboxGroupEvents.valueChanged) {
            val visible = columnGroup.value.toList()
            columnElements.forEach { (path, column) -> column.hiddenProperty = path !in visible }
        }
        val columnsActions = createHorizontalLayout().apply {
            style.setProperty("justify-content", "space-between")
            style.setProperty("gap", "var(--vaadin-gap-s)")
            append(
                createButton { textContent = tr("全部显示", "Show all") }.addTheme("small").apply {
                    clickEvent.addHandler {
                        columnGroup.value = jsArray<String>("""["firstName", "lastName", "email", "profession"]""")
                    }
                },
                createButton { textContent = tr("重置", "Reset") }.addTheme("small").apply {
                    clickEvent.addHandler {
                        columnGroup.value = jsArray<String>("""["firstName", "lastName", "email", "profession"]""")
                    }
                },
            )
        }
        columnsPopover.append(
            kitCol(
                HtmlTagName.strong {
                    textContent = tr("配置列", "Configure columns")
                },
                columnGroup,
                columnsActions,
            ),
        )
        columns.body.append(
            exampleBox(
                null,
                kitCol(kitCol(columnsButton, columnsPopover), grid),
            ),
        )
        body.append(columns.root)

    }

    companion object : CustomElementReference<PopoverPage>(PopoverPage::class.js) {
        val NAME: TagName<PopoverPage> = TagName("vaadin-demo-popover-page")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<vaadin-demo-popover-page>`. */
internal fun newPopoverPage(): PopoverPage {
    PopoverPage.define()
    return PopoverPage.NAME()
}

private fun notificationMessages(count: Int): Array<MessageListItem> {
    val list = people(count)
    return list.mapIndexed { index, person ->
        unsafeJso<MessageListItem> {
            userName = person.displayName
            userImg = person.pictureUrl
            time = "${9 + index}:0${index % 6}"
            text =
                listOf(
                    tr("可以发我 ACME 项目最新的 TPS 报告吗？", "Could you send me the latest TPS report from the ACME project?"),
                    tr("下个月的展会我们还来得及吗？", "Hey, are we on track for the trade show next month?"),
                    tr("报告看起来不错，我转给 Alliyah。", "TPS reports look good! I'm going to pass it on to Alliyah next."),
                    tr("明天的头脑风暴你会来吗？", "Hi, are you going to attend the brainstorming session tomorrow?"),
                )[index % 4]
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

