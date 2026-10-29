package demo.pages

import demo.tr
import demo.ui.EventLog
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.Readout
import demo.ui.anchor
import demo.ui.column
import demo.ui.div
import demo.ui.example
import demo.ui.heading
import demo.ui.jsArray
import demo.ui.jsObject
import demo.ui.listen
import demo.ui.page
import demo.ui.paragraph
import demo.ui.row
import demo.ui.rowStart
import demo.ui.section
import demo.ui.span
import demo.ui.withClass
import demo.ui.withTheme
import js.objects.Record
import js.objects.unsafeJso
import kotlin.js.Date
import kotlin.js.JsAny
import kotlin.js.JsString
import kotlin.js.js
import kotlin.js.unsafeCast
import vaadin.avatar.createAvatar
import vaadin.badge.createBadge
import vaadin.button.createButton
import vaadin.checkbox.createCheckbox
import vaadin.checkboxgroup.CheckboxGroupEvents
import vaadin.checkboxgroup.createCheckboxGroup
import vaadin.contextmenu.ContextMenuEvents
import vaadin.contextmenu.ContextMenuItemData
import vaadin.contextmenu.HTMLElementOrString
import vaadin.contextmenu.createContextMenu
import vaadin.contextmenu.createContextMenuItem
import vaadin.datepicker.DatePickerEvents
import vaadin.datepicker.createDatePicker
import vaadin.grid.GridElement
import vaadin.grid.createGrid
import vaadin.grid.createGridColumn
import vaadin.horizontallayout.createHorizontalLayout
import vaadin.icon.createIcon
import vaadin.integerfield.createIntegerField
import vaadin.menubar.MenuBarItem
import vaadin.menubar.createMenuBar
import vaadin.messagelist.MessageListItem
import vaadin.messagelist.createMessageList
import vaadin.notification.Notification
import vaadin.notification.NotificationShowOptions
import vaadin.notification.createNotification
import vaadin.popover.PopoverEvents
import vaadin.popover.PopoverTriggers
import vaadin.popover.createPopover
import vaadin.select.SelectEvents
import vaadin.select.SelectItemData
import vaadin.select.createSelect
import vaadin.tabs.createTab
import vaadin.tabs.createTabs
import vaadin.tabsheet.createTabSheet
import vaadin.textfield.createTextField
import vaadin.tooltip.createTooltip
import web.dom.ElementId
import web.dom.Node
import web.dom.document
import web.dom.clickEvent
import web.events.EventType
import web.events.addHandler
import web.html.HTMLElement
import web.keyboard.KeyboardEvent

private val KEY_DOWN: EventType<KeyboardEvent> = EventType("keydown")

private fun iconButton(name: String, vararg iconNames: String): HTMLElement =
    createButton { textContent = "" }.apply {
        withTheme("icon")
        ariaLabel(name)
        iconNames.forEach { append(createIcon { icon = it }) }
    }

private fun HTMLElement.ariaLabel(name: String) {
    setAttribute("aria-label", name)
}

/** Select items described by a JSON array of `{label, value}` objects. */
private fun selectItems(json: String): Array<SelectItemData> =
    jsArray<JsAny>(json).unsafeCast<Array<SelectItemData>>()

private fun selectData(vararg pairs: Pair<String, String>): Array<SelectItemData> =
    pairs
        .map { (label, value) -> unsafeJso<SelectItemData> { this.label = label; this.value = value } }
        .toTypedArray()

// region Notification

internal fun buildNotificationPage(meta: PageMeta): PageView {
    val view = page(meta)

    val positions =
        section(
            tr("位置与时长", "Position and duration"),
            tr(
                "Notification.show 是一次性提示；options 可以指定 position、duration 与 theme。",
                "Notification.show shows a one-off message; options can set position, duration and theme.",
            ),
        )
    positions.body.append(
        example(
            null,
            row(
                createButton { textContent = tr("右上角", "Top end") }.withTheme("tertiary").apply {
                    clickEvent.addHandler {
                        Notification.show(
                            tr("右上角的提示", "Message at the top end"),
                            unsafeJso<NotificationShowOptions> {
                                position = "top-end"
                                duration = 3000.0
                            },
                        )
                    }
                },
                createButton { textContent = tr("顶部居中", "Top center") }.withTheme("tertiary").apply {
                    clickEvent.addHandler {
                        Notification.show(
                            tr("顶部居中的提示", "Message at the top center"),
                            unsafeJso<NotificationShowOptions> {
                                position = "top-center"
                                duration = 3000.0
                            },
                        )
                    }
                },
                createButton { textContent = tr("底部居中（默认）", "Bottom center (default)") }
                    .withTheme("tertiary").apply {
                        clickEvent.addHandler {
                            Notification.show(tr("底部居中的提示", "Message at the bottom center"))
                        }
                    },
            ),
        ),
    )
    view.body.append(positions.root)

    val allPositions =
        section(
            tr("全部位置", "Every position"),
            tr(
                "position 接受九种取值：顶部 / 中间 / 底部，各三种水平对齐，stretch 占满整行。",
                "position takes nine values: top / middle / bottom, each with three horizontal alignments; " +
                    "stretch spans the whole row.",
            ),
        )
    val positionNames =
        listOf(
            "top-stretch",
            "top-start",
            "top-center",
            "top-end",
            "middle",
            "bottom-start",
            "bottom-center",
            "bottom-end",
            "bottom-stretch",
        )
    allPositions.body.append(
        example(
            null,
            row(
                *positionNames
                    .map { name ->
                        createButton { textContent = name }.withTheme("tertiary small").apply {
                            clickEvent.addHandler {
                                Notification.show(
                                    name,
                                    unsafeJso<NotificationShowOptions> { position = name },
                                )
                            }
                        }
                    }
                    .toTypedArray(),
            ),
        ),
    )
    view.body.append(allPositions.root)

    val themes =
        section(
            tr("主题变体", "Theme variants"),
            tr("theme 会传播到内部的 notification-card。", "theme propagates to the inner notification-card."),
        )
    themes.body.append(
        example(
            null,
            row(
                createButton { textContent = "success" }.withTheme("success").apply {
                    clickEvent.addHandler {
                        Notification.show(
                            tr("保存成功", "Saved successfully"),
                            unsafeJso<NotificationShowOptions> {
                                theme = "success"
                                position = "bottom-end"
                            },
                        )
                    }
                },
                createButton { textContent = "error" }.withTheme("error").apply {
                    clickEvent.addHandler {
                        Notification.show(
                            tr("保存失败", "Save failed"),
                            unsafeJso<NotificationShowOptions> {
                                theme = "error"
                                position = "bottom-end"
                            },
                        )
                    }
                },
                createButton { textContent = "contrast" }.withTheme("contrast").apply {
                    clickEvent.addHandler {
                        Notification.show(
                            tr("对比色提示", "Contrast message"),
                            unsafeJso<NotificationShowOptions> {
                                theme = "contrast"
                                position = "bottom-end"
                            },
                        )
                    }
                },
                createButton { textContent = "info" }.withTheme("tertiary").apply {
                    clickEvent.addHandler {
                        Notification.show(
                            tr("新项目计划已就绪", "New project plan available"),
                            unsafeJso<NotificationShowOptions> {
                                theme = "info"
                                position = "bottom-end"
                            },
                        )
                    }
                },
                createButton { textContent = "warning" }.withTheme("tertiary").apply {
                    clickEvent.addHandler {
                        Notification.show(
                            tr("会话即将过期", "Your session is about to expire"),
                            unsafeJso<NotificationShowOptions> {
                                theme = "warning"
                                position = "bottom-end"
                            },
                        )
                    }
                },
            ),
        ),
    )
    view.body.append(themes.root)

    val persistent =
        section(
            tr("常驻通知", "Persistent notification"),
            tr(
                "duration 设为 0 时不自动关闭，需要在 renderer 里放一个关闭按钮。",
                "With duration set to 0 the notification stays open; put a close button in the renderer.",
            ),
        )
    val persistentNote = createNotification()
    persistentNote.duration = 0.0
    persistentNote.position = "middle"
    val noteContent =
        row(
            paragraph(tr("这条通知会一直显示，直到点击关闭。", "This notification stays visible until it is closed.")),
            createButton { textContent = tr("关闭", "Close") }.withTheme("tertiary small").apply {
                clickEvent.addHandler { persistentNote.opened = false }
            },
        )
    persistentNote.renderer = { root, _ -> root.append(noteContent) }

    persistent.body.append(
        example(
            null,
            row(
                createButton { textContent = tr("显示常驻通知", "Show persistent notification") }.apply {
                    clickEvent.addHandler { persistentNote.opened = true }
                },
                createButton { textContent = tr("关闭", "Close") }.withTheme("tertiary").apply {
                    clickEvent.addHandler { persistentNote.opened = false }
                },
                persistentNote,
            ),
        ),
    )
    view.body.append(persistent.root)

    val rich =
        section(
            tr("富内容通知", "Rich notifications"),
            tr(
                "renderer 里可以放图标、头像、链接与操作按钮；通知卡片在 renderer 与组件之间共享。",
                "The renderer can hold icons, avatars, links and actions; the card is shared between the " +
                    "renderer and the component.",
            ),
        )
    val richHost = column()

    val successNote = createNotification()
    successNote.duration = 0.0
    successNote.position = "middle"
    successNote.withTheme("success")
    successNote.renderer = { root, _ ->
        val bar = horizontalBar()
        bar.append(createIcon { icon = "vaadin:check-circle" })
        bar.append(span(tr("申请已提交！", "Application submitted!")))
        bar.append(slotted("end", createButton { textContent = tr("查看", "View") }))
        bar.append(closeIcon(tr("关闭", "Close")) { successNote.opened = false }.also { it.slot = "end" })
        root.append(bar)
    }

    val failedNote = createNotification()
    failedNote.duration = 0.0
    failedNote.position = "middle"
    failedNote.withTheme("error")
    failedNote.renderer = { root, _ ->
        val bar = horizontalBar()
        bar.append(createIcon { icon = "vaadin:warning" })
        bar.append(span(tr("报表生成失败", "Failed to generate report")))
        bar.append(slotted("end", createButton { textContent = tr("重试", "Retry") }))
        bar.append(closeIcon(tr("关闭", "Close")) { failedNote.opened = false }.also { it.slot = "end" })
        root.append(bar)
    }

    val mentionNote = createNotification()
    mentionNote.duration = 0.0
    mentionNote.position = "middle"
    mentionNote.renderer = { root, _ ->
        val bar = horizontalBar()
        bar.append(createAvatar { name = "Jason Bailey" })
        bar.append(
            span(tr("Jason Bailey 在", "Jason Bailey mentioned you in")).apply {
                append(anchor(tr("项目 Q4", "Project Q4"), "#"))
            },
        )
        bar.append(closeIcon(tr("关闭", "Close")) { mentionNote.opened = false }.also { it.slot = "end" })
        root.append(bar)
    }
    richHost.append(
        row(
            createButton { textContent = tr("成功通知", "Show rich notification") }.withTheme("success").apply {
                clickEvent.addHandler { successNote.opened = true }
            },
            createButton { textContent = tr("错误通知", "error") }.withTheme("error").apply {
                clickEvent.addHandler { failedNote.opened = true }
            },
            createButton { textContent = tr("提及通知", "mention") }.withTheme("tertiary").apply {
                clickEvent.addHandler { mentionNote.opened = true }
            },
        ),
        successNote,
        failedNote,
        mentionNote,
    )
    rich.body.append(example(null, richHost))
    view.body.append(rich.root)

    val actions =
        section(
            tr("带操作的通知", "Notifications with actions"),
            tr(
                "撤销、重试与链接操作都放在 renderer 里；关闭按钮用图标按钮表达。",
                "Undo, retry and link actions all live in the renderer; a close button is an icon button.",
            ),
        )
    val actionLog = EventLog(5)
    val undoNote = createNotification()
    undoNote.duration = 10000.0
    undoNote.position = "middle"
    undoNote.renderer = { root, _ ->
        val bar = horizontalBar()
        bar.append(span(tr("已删除 5 个任务", "5 tasks deleted")))
        bar.append(
            slotted("end", createButton { textContent = tr("撤销", "Undo") }.apply {
                clickEvent.addHandler {
                    actionLog.add("undo")
                    undoNote.opened = false
                }
            }),
        )
        bar.append(closeIcon(tr("关闭", "Close")) { undoNote.opened = false }.also { it.slot = "end" })
        root.append(bar)
    }

    val linkNote = createNotification()
    linkNote.duration = 0.0
    linkNote.position = "middle"
    linkNote.renderer = { root, _ ->
        val bar = horizontalBar()
        bar.append(
            span(tr("Jason Bailey 提到了你的", "Jason Bailey mentioned you in")).apply {
                append(anchor(tr("项目 Q4", "Project Q4"), "#"))
            },
        )
        bar.append(closeIcon(tr("关闭", "Close")) { linkNote.opened = false }.also { it.slot = "end" })
        root.append(bar)
    }

    val retryNote = createNotification()
    retryNote.duration = 0.0
    retryNote.position = "middle"
    retryNote.withTheme("error")
    retryNote.renderer = { root, _ ->
        val bar = horizontalBar()
        bar.append(span(tr("报表生成失败", "Failed to generate report")))
        bar.append(
            slotted("end", createButton { textContent = tr("重试", "Retry") }.apply {
                clickEvent.addHandler {
                    actionLog.add("retry")
                    retryNote.opened = false
                }
            }),
        )
        bar.append(closeIcon(tr("关闭", "Close")) { retryNote.opened = false }.also { it.slot = "end" })
        root.append(bar)
    }

    actions.body.append(
        example(
            null,
            column(
                row(
                    createButton { textContent = tr("撤销通知", "Undo notification") }.withTheme("tertiary").apply {
                        clickEvent.addHandler { undoNote.opened = true }
                    },
                    createButton { textContent = tr("链接通知", "Link notification") }.withTheme("tertiary").apply {
                        clickEvent.addHandler { linkNote.opened = true }
                    },
                    createButton { textContent = tr("重试通知", "Retry notification") }.withTheme("error").apply {
                        clickEvent.addHandler { retryNote.opened = true }
                    },
                ),
                undoNote,
                linkNote,
                retryNote,
                actionLog.root,
            ),
        ),
    )
    view.body.append(actions.root)

    val keyboard =
        section(
            tr("键盘快捷键", "Keyboard shortcut"),
            tr(
                "通知打开时按 Ctrl-Z（macOS 上是 ⌘-Z）也能撤销；快捷键只在通知可见时生效。",
                "Ctrl-Z (⌘-Z on macOS) undoes while the notification is open; the shortcut only works " +
                    "while it is visible.",
            ),
        )
    val keyboardNote = createNotification()
    keyboardNote.duration = 10000.0
    keyboardNote.position = "middle"
    keyboardNote.renderer = { root, _ ->
        val bar = horizontalBar()
        bar.append(span(tr("已删除 5 个任务", "5 tasks deleted")))
        bar.append(
            slotted("end", createButton { textContent = tr("撤销 Ctrl-Z", "Undo Ctrl-Z") }.apply {
                clickEvent.addHandler { keyboardNote.opened = false }
            }),
        )
        root.append(bar)
    }
    // The listener lives on `document`, so it is registered once per page build and stops reacting
    // as soon as the notification is closed.
    document.listen(KEY_DOWN) { event: KeyboardEvent ->
        if (keyboardNote.opened && (event.metaKey || event.ctrlKey) && event.key == "z") {
            event.preventDefault()
            keyboardNote.opened = false
        }
    }
    keyboard.body.append(
        example(
            null,
            column(
                createButton { textContent = tr("显示通知", "Show notification") }.apply {
                    clickEvent.addHandler { keyboardNote.opened = true }
                },
                keyboardNote,
            ),
        ),
    )
    view.body.append(keyboard.root)

    val lengths =
        section(
            tr("内容长度", "Content length"),
            tr(
                "通知只放结论与一个主要操作；细节留在页面里，避免出现长段落。",
                "Keep a notification to the outcome and one primary action; details belong on the page, " +
                    "not in a long paragraph.",
            ),
        )
    val goodNote = createNotification()
    goodNote.duration = 0.0
    goodNote.position = "middle"
    goodNote.renderer = { root, _ ->
        val bar = horizontalBar()
        bar.append(
            column(
                span(tr("Aria Bailey", "Aria Bailey")),
                span(tr("好的，不过你能帮我……", "Yeah, I know. But could you help me with...")).apply {
                    style.setProperty("font-size", "0.875rem")
                    style.setProperty("color", "var(--vaadin-text-color-secondary)")
                },
            ),
        )
        bar.append(slotted("end", createButton { textContent = tr("查看", "View") }))
        bar.append(closeIcon(tr("关闭", "Close")) { goodNote.opened = false }.also { it.slot = "end" })
        root.append(bar)
    }
    val longText =
        tr(
            "我知道，不过你能帮我看看吗？我不确定我的 CSS 里 bug 在哪里：勾选标记没有显示成正确的颜色，我用了设计系统的 CSS 自定义属性，但好像没有生效。",
            "Yeah, I know. But could you help me with this. I'm not sure where the bug is in my CSS? " +
                "The checkmark doesn't get the right color. I'm trying to use the CSS custom properties " +
                "from our design system, but for some reason it's not working.",
        )
    val badNote = createNotification()
    badNote.duration = 0.0
    badNote.position = "middle"
    badNote.renderer = { root, _ ->
        val bar = horizontalBar()
        bar.append(
            column(
                span(tr("来自 Aria Bailey 的新消息", "New message from Aria Bailey")),
                span(longText).apply {
                    style.setProperty("font-size", "0.875rem")
                    style.setProperty("color", "var(--vaadin-text-color-secondary)")
                },
            ),
        )
        bar.append(closeIcon(tr("关闭", "Close")) { badNote.opened = false }.also { it.slot = "end" })
        root.append(bar)
    }
    lengths.body.append(
        example(
            tr("推荐：一行结论 + 一个操作", "Do: outcome plus one action"),
            column(
                createButton { textContent = tr("显示推荐示例", "Show recommended") }.withTheme("tertiary").apply {
                    clickEvent.addHandler { goodNote.opened = true }
                },
                goodNote,
            ),
        ),
        example(
            tr("不推荐：整段对话塞进通知", "Don't: a whole paragraph in a notification"),
            column(
                createButton { textContent = tr("显示不推荐示例", "Show discouraged") }.withTheme("tertiary").apply {
                    clickEvent.addHandler { badNote.opened = true }
                },
                badNote,
            ),
        ),
    )
    view.body.append(lengths.root)

    return view
}

private fun horizontalBar(): HTMLElement {
    val bar = createHorizontalLayout { withTheme("spacing") }
    bar.style.setProperty("align-items", "center")
    bar.style.setProperty("min-width", "300px")
    return bar
}

private fun slotted(slotName: String, element: HTMLElement): HTMLElement {
    element.slot = slotName
    return element
}

private fun closeIcon(name: String, onClick: () -> Unit): HTMLElement =
    createButton { textContent = "" }.withTheme("icon").apply {
        setAttribute("aria-label", name)
        append(createIcon { icon = "lumo:cross" })
        clickEvent.addHandler { onClick() }
    }

// endregion

// region Popover

internal fun buildPopoverPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "popover 通过 for 指向触发元素的 id；默认点击触发、点击外部关闭。",
                "popover points at the trigger's id through for; it opens on click and closes on an outside click.",
            ),
        )
    val log = EventLog(4)
    val trigger = createButton { textContent = tr("点击查看", "Click to open") }.withTheme("primary")
    trigger.id = ElementId("popover-trigger")
    trigger.dataset["test"] = "popover-trigger"

    val popover = createPopover()
    popover.`for` = "popover-trigger"
    popover.append(
        column(
            heading(3, tr("浮层内容", "Overlay content")),
            paragraph(tr("任意 DOM 都可以放进 popover 的默认插槽。", "Any DOM goes into the popover's default slot.")),
        ),
    )
    popover.listen(PopoverEvents.openedChanged) { log.add("opened-changed · ${popover.opened}") }

    basic.body.append(example(null, column(row(trigger, popover), log.root)))
    view.body.append(basic.root)

    val positioning =
        section(
            tr("位置", "Position"),
            tr(
                "position 接受 top / bottom / start / end 与它们的组合，共十二个值。",
                "position accepts top / bottom / start / end and their combinations, twelve values in total.",
            ),
        )
    val positionTarget = createButton { textContent = tr("打开", "Open") }.withTheme("primary")
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
        example(null, column(rowStart(positionTarget, positionSelect), positionPopover)),
    )
    view.body.append(positioning.root)

    val arrow =
        section(
            tr("箭头与图标触发器", "Arrow and icon trigger"),
            tr(
                "theme=\"arrow\" 在浮层边缘画一个指向触发元素的箭头。",
                "theme=\"arrow\" draws an arrow on the overlay edge that points at the trigger.",
            ),
        )
    val bell = iconButton(tr("通知", "Notifications"), "lumo:bell")
    bell.id = ElementId("popover-arrow-trigger")
    val arrowPopover = createPopover { withTheme("arrow") }
    arrowPopover.`for` = "popover-arrow-trigger"
    arrowPopover.append(div().apply { textContent = tr("没有新通知", "No new notifications") })
    arrow.body.append(example(null, row(bell, arrowPopover)))
    view.body.append(arrow.root)

    val modal =
        section(
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
    modalPopover.append(column(discountField, applyDiscount))
    modal.body.append(
        example(
            null,
            column(
                row(discountButton, modalPopover),
                createButton { textContent = tr("打开模态浮层", "Open modal popover") }.withTheme("tertiary").apply {
                    clickEvent.addHandler { modalPopover.opened = true }
                },
            ),
        ),
    )
    view.body.append(modal.root)

    val interactive =
        section(
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
    val cvvPopover = createPopover { withTheme("arrow") }
    cvvPopover.`for` = "popover-cvv-field"
    cvvPopover.trigger = arrayOf(PopoverTriggers.hover, PopoverTriggers.focus)
    cvvPopover.position = "top"
    cvvPopover.append(
        heading(3, tr("卡片验证码", "Card Verification Value")).apply { style.setProperty("font-size", "1rem") },
        div(tr("通常印在卡片背面签名条旁边或末尾的三到四位数。", "A three or four digit code, usually printed on the back of the card, next to or at the end of the signature strip.")).apply {
            style.setProperty("max-width", "300px")
        },
        anchor(tr("查看不同卡片上的 CVV 位置", "See where to find CVV on different cards"), "https://www.cvvnumber.com/cvv.html", external = true),
    )
    interactive.body.append(example(null, cvvField))
    interactive.body.append(cvvPopover)
    view.body.append(interactive.root)

    val rangeField =
        section(
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
        column(
            presets,
            row(fromPicker, div().apply { textContent = "−" }, toPicker),
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
    rangeField.body.append(example(null, column(rangeInput, rangePopover)))
    view.body.append(rangeField.root)

    val panel =
        section(
            tr("通知面板", "Notification panel"),
            tr(
                "浮层可以承载完整的面板：Tabsheet 切换未读与全部消息，Message List 渲染条目。",
                "An overlay can carry a whole panel: a TabSheet switches between unread and all messages, " +
                    "and a Message List renders the items.",
            ),
        )
    val notificationBell = iconButton(tr("通知", "Notifications"), "lumo:bell")
    notificationBell.id = ElementId("popover-notifications-trigger")
    val notificationsPopover = createPopover { withTheme("arrow", "no-padding") }
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
        createButton { textContent = tr("全部标为已读", "Mark all read") }.withTheme("small").apply {
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
    val tabSheet = createTabSheet { withTheme("small", "no-padding", "no-border") }
    tabSheet.append(tabs)
    tabSheet.append(div(null, unreadList).apply { setAttribute("tab", "unread-tab") })
    tabSheet.append(div(null, allList).apply { setAttribute("tab", "all-tab") })
    notificationsPopover.append(panelHeader, tabSheet)
    panel.body.append(example(null, row(notificationBell, notificationsPopover)))
    view.body.append(panel.root)

    val userMenu =
        section(
            tr("用户菜单", "User menu"),
            tr(
                "头像按钮打开一个无内边距的浮层，里面是账号信息和菜单项。",
                "The avatar button opens a popover without padding that holds the account details and links.",
            ),
        )
    val user = people(1).first()
    val avatarButton = createButton { textContent = "" }.withTheme("icon", "tertiary").apply {
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
    val userPopover = createPopover { withTheme("no-padding") }
    userPopover.`for` = "popover-avatar-trigger"
    userPopover.modal = true
    userPopover.position = "bottom-end"
    userPopover.setAttribute("role", "menu")
    userPopover.setAttribute("aria-label", tr("用户菜单", "User menu"))
    val nickName = "@${user.firstName}${user.lastName}".lowercase()
    val userCard =
        div("person-item").apply {
            style.setProperty("padding", "var(--vaadin-padding-s)")
            append(
                createAvatar {
                    img = user.pictureUrl
                    name = user.displayName
                },
                span(user.displayName),
                span(nickName),
            )
        }
    val menuLinks = div("user-menu-links")
    listOf(tr("用户资料", "User profile"), tr("偏好设置", "Preferences"), tr("退出登录", "Sign out")).forEach { label ->
        menuLinks.append(
            anchor(label, "#").apply {
                setAttribute("role", "menuitem")
            },
        )
    }
    userPopover.append(column(userCard, menuLinks))
    userMenu.body.append(example(null, row(avatarButton, userPopover)))
    view.body.append(userMenu.root)

    val columns =
        section(
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
    val columnElements = mutableListOf<Pair<String, vaadin.grid.GridColumnElement>>()
    columnDefs.forEach { (path, label) ->
        val column = createGridColumn {
            header = label
            this.path = path
        }
        columnElements += path to column
        grid.append(column)
    }
    val columnsButton = iconButton(tr("显示 / 隐藏列", "Show / hide columns"), "vaadin:grid-h")
    columnsButton.id = ElementId("popover-columns-trigger")
    val columnsPopover = createPopover()
    columnsPopover.`for` = "popover-columns-trigger"
    columnsPopover.modal = true
    columnsPopover.withBackdrop = true
    columnsPopover.position = "bottom-end"
    val columnGroup = createCheckboxGroup { withTheme("vertical") }
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
            createButton { textContent = tr("全部显示", "Show all") }.withTheme("small").apply {
                clickEvent.addHandler {
                    columnGroup.value = jsArray<String>("""["firstName", "lastName", "email", "profession"]""")
                }
            },
            createButton { textContent = tr("重置", "Reset") }.withTheme("small").apply {
                clickEvent.addHandler {
                    columnGroup.value = jsArray<String>("""["firstName", "lastName", "email", "profession"]""")
                }
            },
        )
    }
    columnsPopover.append(
        column(
            div(tr("配置列", "Configure columns")).apply { style.setProperty("font-weight", "600") },
            columnGroup,
            columnsActions,
        ),
    )
    columns.body.append(
        example(
            null,
            column(column(columnsButton, columnsPopover), grid),
        ),
    )
    view.body.append(columns.root)

    return view
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

/**
 * Upstream types the extra data as `ContextMenuItemData<{ value?: string }>`; Kotlin models the
 * generic intersection by extending the wrapper interface.
 */
private external interface CopyMenuItem : ContextMenuItemData {
    var value: String?
}

private fun copyMenuItem(text: String, value: String): CopyMenuItem =
    unsafeJso<CopyMenuItem> {
        this.text = text
        this.value = value
    }

private fun isoDate(date: Date): String {
    val year = date.getFullYear()
    val month = date.getMonth() + 1
    val day = date.getDate()
    return "$year-${pad2(month)}-${pad2(day)}"
}

private fun pad2(value: Int): String = if (value < 10) "0$value" else value.toString()

// endregion

// region Tooltip

internal fun buildTooltipPage(meta: PageMeta): PageView {
    val view = page(meta)

    val slotted =
        section(
            tr("放进插槽", "Inside a slot"),
            tr(
                "把 tooltip 作为子元素并设置 slot=\"tooltip\"，组件会在悬停 / 聚焦时显示它。",
                "Add the tooltip as a child with slot=\"tooltip\"; the component shows it on hover / focus.",
            ),
        )
    val buttonWithTooltip = createButton { textContent = tr("悬停我", "Hover me") }
    buttonWithTooltip.append(
        createTooltip { text = tr("这是按钮上的提示", "Hint on the button") }.apply { slot = "tooltip" },
    )
    val fieldWithTooltip =
        vaadin.textfield.createTextField {
            label = tr("字段的提示", "Field hint")
            placeholder = tr("聚焦后显示", "Shown on focus")
        }
    fieldWithTooltip.append(
        createTooltip { text = tr("字段级的提示文本", "Field-level hint text") }.apply { slot = "tooltip" },
    )
    val searchField =
        vaadin.textfield.createTextField {
            placeholder = tr("搜索", "Search")
        }
    searchField.append(
        createIcon { icon = "lumo:search" }.also { it.slot = "prefix" },
        createTooltip { text = tr("用「引号」包裹可以精确匹配短语", "Wrap in “quotes” for exact phrase") }
            .also { it.slot = "tooltip" },
    )
    slotted.body.append(example(null, row(buttonWithTooltip, fieldWithTooltip, searchField)))
    view.body.append(slotted.root)

    val linked =
        section(
            tr("用 for 关联", "Linked with for"),
            tr(
                "不在组件内部时，用 for 指向目标元素的 id；position 决定浮层落在哪一侧。",
                "Outside a component, point for at the id of the target element; position picks the side.",
            ),
        )
    val target = div("example").apply { textContent = tr("这个区块带有一个提示", "This block has a hint") }
    target.id = ElementId("tooltip-target")
    target.style.setProperty("padding", "var(--lumo-space-m)")
    val tooltip = createTooltip { text = tr("关联到任意元素的提示", "Hint linked to any element") }
    tooltip.`for` = "tooltip-target"
    val headingTarget = heading(3, tr("带提示的标题", "Heading with tooltip"))
    headingTarget.id = ElementId("tooltip-heading")
    val headingTooltip = createTooltip { text = tr("这是标题上的提示", "This is a tooltip") }
    headingTooltip.`for` = "tooltip-heading"
    headingTooltip.position = "top-start"
    linked.body.append(example(null, column(target, headingTarget), tooltip, headingTooltip))
    view.body.append(linked.root)

    val manualSection =
        section(
            tr("手动打开", "Manual mode"),
            tr(
                "manual 关闭自动显示，由 opened 控制；这里用后缀图标按钮切换。",
                "manual turns off the automatic behaviour and leaves it to opened; the suffix icon button toggles it.",
            ),
        )
    val manualField = vaadin.textfield.createTextField {
        placeholder = tr("搜索", "Search")
    }
    val manualTooltip = createTooltip {
        text = tr("用「引号」包裹可以精确匹配短语", "Wrap in “quotes” for exact phrase")
        manual = true
    }
    manualTooltip.slot = "tooltip"
    val infoButton = createButton { textContent = "" }.withTheme("tertiary").apply {
        style.setProperty("padding", "0")
        style.setProperty("border", "0")
        style.setProperty("min-width", "0")
        append(createIcon { icon = "vaadin:info-circle" })
        clickEvent.addHandler { manualTooltip.opened = !manualTooltip.opened }
    }
    infoButton.slot = "suffix"
    manualField.append(createIcon { icon = "lumo:search" }.also { it.slot = "prefix" }, manualTooltip, infoButton)
    manualSection.body.append(example(null, manualField))
    view.body.append(manualSection.root)

    val delays =
        section(
            tr("延迟与 Markdown", "Delays and Markdown"),
            tr(
                "hoverDelay / focusDelay / hideDelay 以毫秒为单位；markdown 让提示文本按 Markdown 解析。",
                "hoverDelay / focusDelay / hideDelay are in milliseconds; markdown parses the hint as Markdown.",
            ),
        )
    val mdTarget = createButton { textContent = tr("Markdown 提示", "Markdown hint") }
    mdTarget.id = ElementId("tooltip-markdown")
    val mdTooltip =
        createTooltip {
            text = tr("**粗体** 与 *斜体*，以及 `代码`", "**Bold** and *italic*, plus `code`")
            markdown = true
            hoverDelay = 0.0
        }
    mdTooltip.`for` = "tooltip-markdown"
    val slowTarget = createButton { textContent = tr("延迟 1 秒", "One second delay") }
    slowTarget.id = ElementId("tooltip-slow")
    val slowTooltip =
        createTooltip {
            text = tr("悬停一秒后出现", "Shown after one second")
            hoverDelay = 1000.0
            hideDelay = 500.0
        }
    slowTooltip.`for` = "tooltip-slow"
    delays.body.append(example(null, row(mdTarget, mdTooltip, slowTarget, slowTooltip)))
    view.body.append(delays.root)

    val generated =
        section(
            tr("动态文本", "Generated text"),
            tr(
                "generator 接收 context，返回要显示的文本；同一个 tooltip 可以服务多个元素。",
                "generator receives context and returns the text; one tooltip can serve several elements.",
            ),
        )
    val generatedTargets = mutableListOf<HTMLElement>()
    listOf(tr("图表", "Chart"), tr("表格", "Table"), tr("地图", "Map")).forEachIndexed { index, label ->
        val element = createButton { textContent = label }.withTheme("tertiary")
        element.id = ElementId("tooltip-generated-$index")
        generatedTargets += element
    }
    val generatedTooltip = createTooltip()
    generatedTooltip.`for` = "tooltip-generated-0"
    // The generator reads whatever `context` carries; the property is a plain record on the element.
    generatedTooltip.context =
        jsObject("""{"index": 0}""").unsafeCast<Record<JsString, JsAny>>()
    generatedTooltip.generator = { context ->
        val index: Int = js("(context) => context.index | 0")(context)
        tr("第 ${index + 1} 个元素的说明", "Hint for element ${index + 1}")
    }
    generated.body.append(example(null, row(*generatedTargets.toTypedArray(), generatedTooltip)))
    view.body.append(generated.root)

    val positions =
        section(
            tr("位置", "Position"),
            tr(
                "position 决定浮层相对触发元素的位置，这里演示四个常用值。",
                "position picks the side the overlay opens on; four common values are shown here.",
            ),
        )
    val positionedButtons = mutableListOf<HTMLElement>()
    listOf("top", "bottom", "start", "end").forEachIndexed { index, position ->
        val element = iconButton(position, "lumo:info-circle").withTheme("tertiary")
        element.id = ElementId("tooltip-position-$index")
        val positioned = createTooltip { text = position }
        positioned.`for` = "tooltip-position-$index"
        positioned.position = position
        positionedButtons += element
        positionedButtons += positioned
    }
    positions.body.append(example(null, row(*positionedButtons.toTypedArray())))
    view.body.append(positions.root)

    return view
}

// endregion

// region Context menu

internal fun buildContextMenuPage(meta: PageMeta): PageView {
    val view = page(meta)

    val items: Array<ContextMenuItemData> =
        jsObject(
            """
            [
              {"text": "${tr("查看", "View")}"},
              {"text": "${tr("复制", "Copy")}"},
              {"text": "${tr("粘贴", "Paste")}", "disabled": true},
              {"text": "${tr("更多", "More")}", "children": [
                {"text": "${tr("导出为 CSV", "Export as CSV")}"},
                {"text": "${tr("导出为 JSON", "Export as JSON")}"}
              ]}
            ]
            """.trimIndent(),
        ).unsafeCast<Array<ContextMenuItemData>>()

    val basic =
        section(
            tr("右键打开", "Open on right-click"),
            tr(
                "openOn 默认是 contextmenu；listenOn 指向接收事件的元素。",
                "openOn defaults to contextmenu; listenOn points at the element that receives the event.",
            ),
        )
    val log = EventLog(5)
    val surface = div("example").apply { textContent = tr("在这个区域点击右键", "Right-click in this area") }
    surface.dataset["test"] = "context-menu-surface"
    val menu = createContextMenu()
    menu.openOn = "contextmenu"
    menu.listenOn = surface
    menu.items = items
    menu.listen(ContextMenuEvents.itemSelected) { log.add("item-selected") }
    menu.listen(ContextMenuEvents.openedChanged) { log.add("opened-changed · ${menu.opened}") }
    basic.body.append(example(null, column(surface, log.root, menu)))
    view.body.append(basic.root)

    val gridSection =
        section(
            tr("表格行菜单", "Menu on grid rows"),
            tr(
                "把 grid 放进 context-menu，右键任意一行都能对这个区域设置菜单。",
                "Put a grid inside a context-menu; right-clicking any row opens the menu for that area.",
            ),
        )
    val rowMenu = createContextMenu()
    rowMenu.items =
        jsObject(
            """
            [
              {"text": "${tr("查看", "View")}"},
              {"text": "${tr("编辑", "Edit")}"},
              {"text": "${tr("删除", "Delete")}"}
            ]
            """.trimIndent(),
        ).unsafeCast<Array<ContextMenuItemData>>()
    val rowGrid = personGrid(people(5))
    rowMenu.append(rowGrid)
    gridSection.body.append(example(null, rowMenu))
    view.body.append(gridSection.root)

    val checkable =
        section(
            tr("勾选条目", "Checkable items"),
            tr(
                "items 里的 checked 表示当前选中项；item-selected 里更新后在界面上回读。",
                "checked marks the current item; the selection is read back after item-selected updates it.",
            ),
        )
    val assignees =
        mutableListOf(
            unsafeJso<ContextMenuItemData> { text = "Abigail Lewis"; checked = true },
            unsafeJso<ContextMenuItemData> { text = "Allison Torres"; checked = false },
            unsafeJso<ContextMenuItemData> { text = "Anna Myers"; checked = false },
            unsafeJso<ContextMenuItemData> { text = "Lauren Wright"; checked = false },
        )
    val assigneeReadout = Readout(tr("负责人：Abigail Lewis", "Assignee: Abigail Lewis"))
    val assigneeMenu = createContextMenu()
    assigneeMenu.items = assignees.toTypedArray()
    val assigneeLabel = span(tr("负责人：", "Assignee: "))
    val assigneeValue = span("Abigail Lewis")
    assigneeLabel.append(assigneeValue)
    assigneeMenu.append(assigneeLabel)
    assigneeMenu.listen(ContextMenuEvents.itemSelected) {
        val chosen = js("(event) => event.detail.value.text")(it.unsafeCast<Any>()) as String
        assignees.forEach { item -> item.checked = item.text == chosen }
        assigneeValue.textContent = chosen
        assigneeReadout.set(tr("负责人：", "Assignee: "), chosen)
    }
    checkable.body.append(example(null, column(assigneeMenu, assigneeReadout.root)))
    view.body.append(checkable.root)

    val classNames =
        section(
            tr("自定义类名", "Custom class name"),
            tr(
                "className 加到条目元素上，配合页面样式表达危险操作。",
                "className lands on the item element, which page styles can use for destructive actions.",
            ),
        )
    val classMenu = createContextMenu { openOn = "click" }
    classMenu.items =
        jsObject(
            """
            [
              {"text": "${tr("分享", "Share")}"},
              {"text": "${tr("复制副本", "Duplicate")}"},
              {"text": "${tr("删除", "Delete")}", "className": "custom"}
            ]
            """.trimIndent(),
        ).unsafeCast<Array<ContextMenuItemData>>()
    classMenu.append(createButton { textContent = tr("操作", "Actions") }.withTheme("tertiary"))
    classNames.body.append(example(null, classMenu))
    view.body.append(classNames.root)

    val customData =
        section(
            tr("自定义条目数据", "Custom item data"),
            tr(
                "条目可以带任意字段；item-selected 的 detail.value 就是被点击的条目对象。",
                "An item can carry any extra fields; detail.value of item-selected is the clicked item object.",
            ),
        )
    val copyReadout = Readout(tr("尚未复制", "Nothing copied yet"))
    val copyMenu = createContextMenu()
    // `items` takes the closed wrapper type; the generic extra data arrives through the subtype.
    copyMenu.items =
        arrayOf(
            copyMenuItem(tr("复制为纯文本", "Copy as plain text"), "Context Menu"),
            copyMenuItem(tr("复制为 HTML", "Copy as HTML"), "<h1>Context Menu</h1>"),
            copyMenuItem(tr("复制为 Markdown", "Copy as Markdown"), "# Context Menu"),
        ).unsafeCast<Array<ContextMenuItemData>>()
    copyMenu.append(column(heading(2, "Context Menu"), paragraph(tr("右键这段文字复制它的格式。", "Right-click this text to copy it in a format."))))
    copyMenu.listen(ContextMenuEvents.itemSelected) {
        val value = js("(event) => event.detail.value.value")(it.unsafeCast<JsAny>()) as String?
        copyReadout.set(tr("已复制：", "Copied: "), value ?: "")
    }
    customData.body.append(example(null, column(copyMenu, copyReadout.root)))
    view.body.append(customData.root)

    val dividers =
        section(
            tr("分隔线与层级", "Dividers and hierarchy"),
            tr(
                "component 设成 \"hr\" 的条目渲染成分隔线；children 生成子菜单。",
                "An item whose component is \"hr\" renders a divider; children open a submenu.",
            ),
        )
    val dividerMenu = createContextMenu()
    dividerMenu.items =
        jsObject(
            """
            [
              {"text": "${tr("预览", "Preview")}"},
              {"text": "${tr("编辑", "Edit")}"},
              {"component": "hr"},
              {"text": "${tr("导出", "Export")}", "children": [
                {"text": "Portable Document Format (.pdf)"},
                {"text": "Rich Text Format (.rtf)"},
                {"text": "Plain text (.txt)"}
              ]},
              {"text": "${tr("分享", "Share")}", "children": [
                {"text": "${tr("复制链接", "Copy link")}"},
                {"text": "${tr("邮件", "Email")}"}
              ]},
              {"component": "hr"},
              {"text": "${tr("删除", "Delete")}", "disabled": true}
            ]
            """.trimIndent(),
        ).unsafeCast<Array<ContextMenuItemData>>()
    val dividerGrid = personGrid(people(4))
    dividerMenu.append(dividerGrid)
    dividers.body.append(example(null, dividerMenu))
    view.body.append(dividers.root)

    val leftClick =
        section(
            tr("左键打开", "Open on click"),
            tr(
                "openOn 设为 click 时用左键展开，适合做「操作」按钮。",
                "With openOn set to click the menu opens on a left click, which suits an actions button.",
            ),
        )
    val actionsButton = createButton { textContent = tr("操作", "Actions") }.withTheme("tertiary")
    val clickMenu = createContextMenu()
    clickMenu.openOn = "click"
    clickMenu.listenOn = actionsButton
    clickMenu.items = items
    leftClick.body.append(example(null, row(actionsButton, clickMenu)))
    view.body.append(leftClick.root)

    val customItems =
        section(
            tr("自定义条目呈现", "Custom item rendering"),
            tr(
                "component 接受元素，可以直接放图标和头像。",
                "component takes an element, so icons and avatars can be used directly.",
            ),
        )
    fun menuItem(iconName: String, label: String): ContextMenuItemData =
        unsafeJso<ContextMenuItemData> {
            component =
                HTMLElementOrString(
                    createContextMenuItem().apply {
                        append(createIcon { icon = iconName }.also { iconElement ->
                            iconElement.style.setProperty("color", "var(--vaadin-text-color-secondary)")
                            iconElement.style.setProperty("margin-inline-end", "var(--vaadin-gap-s)")
                        })
                        append(span(label))
                    },
                )
        }
    val presentMenu = createContextMenu()
    presentMenu.items =
        arrayOf(
            menuItem("vaadin:file-search", tr("打开", "Open")),
            unsafeJso<ContextMenuItemData> {
                component =
                    HTMLElementOrString(
                        createContextMenuItem().apply {
                            append(createIcon { icon = "vaadin:user-check" })
                            append(span(tr("指派", "Assign")))
                        },
                    )
                children =
                    people(3)
                        .map { person ->
                            unsafeJso<ContextMenuItemData> {
                                component =
                                    HTMLElementOrString(
                                        createContextMenuItem().apply {
                                            append(
                                                createAvatar {
                                                    img = person.pictureUrl
                                                    name = person.displayName
                                                },
                                            )
                                            append(span(person.displayName))
                                        },
                                    )
                            }
                        }
                        .toTypedArray()
            },
            unsafeJso<ContextMenuItemData> { component = HTMLElementOrString("hr") },
            menuItem("vaadin:trash", tr("删除", "Delete")),
        )
    val presentGrid = personGrid(people(4))
    presentMenu.append(presentGrid)
    customItems.body.append(example(null, presentMenu))
    view.body.append(customItems.root)

    val tooltips =
        section(
            tr("条目提示", "Item tooltips"),
            tr(
                "条目数据里的 tooltip / tooltipPosition 配合 context-menu 里的 tooltip 插槽生效。",
                "The tooltip / tooltipPosition fields of the items work together with the tooltip slot of the " +
                    "context menu.",
            ),
        )
    val tooltipMenu = createContextMenu()
    tooltipMenu.items =
        jsObject(
            """
            [
              {"text": "${tr("编辑", "Edit")}", "tooltip": "${tr("编辑选中的人", "Edit selected person")}"},
              {"text": "${tr("分享", "Share")}", "children": [
                {"text": "${tr("复制链接", "Copy link")}", "tooltip": "${tr("把可分享的链接复制到剪贴板", "Copy a shareable link to the clipboard")}"},
                {"text": "${tr("邮件", "Email")}", "tooltip": "${tr("用邮件发送联系人信息", "Send the contact details by email")}", "tooltipPosition": "end"}
              ]}
            ]
            """.trimIndent(),
        ).unsafeCast<Array<ContextMenuItemData>>()
    tooltipMenu.append(createTooltip().also { it.slot = "tooltip" })
    val tooltipGrid = personGrid(people(4))
    tooltipMenu.append(tooltipGrid)
    tooltips.body.append(example(null, tooltipMenu))
    view.body.append(tooltips.root)

    val bestPractice =
        section(
            tr("表格中的操作", "Actions inside a grid"),
            tr(
                "行内操作按钮用 Menu Bar 表达，同一个 items 数组既服务右键菜单也服务行内按钮。",
                "An inline actions button uses a Menu Bar; the same items array drives both the context menu " +
                    "and the inline button.",
            ),
        )
    val fileItems: Array<ContextMenuItemData> =
        jsObject(
            """
            [
              {"text": "${tr("查看", "View")}"},
              {"text": "${tr("编辑", "Edit")}"},
              {"text": "${tr("删除", "Delete")}"}
            ]
            """.trimIndent(),
        ).unsafeCast<Array<ContextMenuItemData>>()
    val filesMenu = createContextMenu()
    filesMenu.items = fileItems
    val filesGrid = createGrid()
    filesGrid.allRowsVisible = true
    filesGrid.items =
        jsArray<JsAny>(
            """
            [
              {"name": "Annual Report.docx", "size": "24 MB"},
              {"name": "Financials.xlsx", "size": "42 MB"}
            ]
            """.trimIndent(),
        )
    filesGrid.append(
        createGridColumn {
            header = tr("文件", "File")
            path = "name"
        },
        createGridColumn {
            header = tr("大小", "Size")
            path = "size"
            width = "6rem"
        },
        createGridColumn {
            width = "4rem"
            flexGrow = 0.0
            renderer = { container, _, _ ->
                val menuBar = createMenuBar { withTheme("tertiary") }
                menuBar.items = fileItems.unsafeCast<Array<MenuBarItem>>()
                container.append(menuBar)
            }
        },
    )
    filesMenu.append(filesGrid)
    bestPractice.body.append(example(null, filesMenu))
    view.body.append(bestPractice.root)

    return view
}

// endregion
