package demo.pages

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.ui.EventLog
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.Readout
import demo.ui.column
import demo.ui.div
import demo.ui.example
import demo.ui.heading
import demo.ui.jsObject
import demo.ui.listen
import demo.ui.page
import demo.ui.paragraph
import demo.ui.row
import demo.ui.section
import demo.ui.withTheme
import demo.tr
import js.objects.unsafeJso
import kotlin.js.js
import kotlin.js.unsafeCast
import vaadin.button.createButton
import vaadin.confirmdialog.ConfirmDialogEvents
import vaadin.confirmdialog.createConfirmDialog
import vaadin.contextmenu.ContextMenuEvents
import vaadin.contextmenu.ContextMenuItemData
import vaadin.contextmenu.createContextMenu
import vaadin.dialog.createDialog
import vaadin.login.LoginFormEvents
import vaadin.login.createLoginForm
import vaadin.login.createLoginOverlay
import vaadin.notification.Notification
import vaadin.notification.NotificationShowOptions
import vaadin.notification.createNotification
import vaadin.overlay.createOverlay
import vaadin.popover.PopoverEvents
import vaadin.popover.createPopover
import vaadin.switch.createSwitch
import vaadin.switch.SwitchEvents
import vaadin.tooltip.createTooltip
import web.dom.ElementId
import web.dom.clickEvent
import web.dom.document
import web.events.addHandler
import web.html.HTMLElement

val overlayEntries: List<CatalogEntry> =
    listOf(
        CatalogEntry(
            id = "dialog",
            group = Groups.OVERLAYS,
            meta =
                PageMeta(
                    title = bi("Dialog", "Dialog"),
                    summary =
                        bi(
                            "模态对话框：renderer 提供内容，header / footer 渲染器提供标题与操作区。",
                            "Modal dialog: renderer supplies the content, and the header / footer renderers " +
                                "supply the title and the actions.",
                        ),
                    docsUrl = "$DOCS_BASE/dialog",
                    sourcePath = "pages/Overlays.kt",
                ),
            builder = ::buildDialogPage,
        ),
        CatalogEntry(
            id = "confirm-dialog",
            group = Groups.OVERLAYS,
            meta =
                PageMeta(
                    title = bi("Confirm Dialog", "Confirm Dialog"),
                    summary =
                        bi(
                            "确认对话框：确认 / 取消 / 拒绝三种结果与对应事件。",
                            "Confirmation dialog: the confirm / cancel / reject outcomes and their events.",
                        ),
                    docsUrl = "$DOCS_BASE/confirm-dialog",
                    sourcePath = "pages/Overlays.kt",
                ),
            builder = ::buildConfirmDialogPage,
        ),
        CatalogEntry(
            id = "notification",
            group = Groups.OVERLAYS,
            meta =
                PageMeta(
                    title = bi("Notification", "Notification"),
                    summary =
                        bi(
                            "轻量提示：位置、时长、主题变体，以及需要手动关闭的常驻通知。",
                            "Short messages: position, duration, theme variants and persistent notices " +
                                "that are closed by hand.",
                        ),
                    docsUrl = "$DOCS_BASE/notification",
                    sourcePath = "pages/Overlays.kt",
                ),
            builder = ::buildNotificationPage,
        ),
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
                    sourcePath = "pages/Overlays.kt",
                ),
            builder = ::buildPopoverPage,
        ),
        CatalogEntry(
            id = "tooltip",
            group = Groups.OVERLAYS,
            meta =
                PageMeta(
                    title = bi("Tooltip", "Tooltip"),
                    summary =
                        bi(
                            "悬停提示：可以放进组件的 tooltip 插槽，也可以用 for 关联任意元素。",
                            "Hover hint: goes into a component's tooltip slot, or is linked to any element " +
                                "with for.",
                        ),
                    docsUrl = "$DOCS_BASE/tooltip",
                    sourcePath = "pages/Overlays.kt",
                ),
            builder = ::buildTooltipPage,
        ),
        CatalogEntry(
            id = "context-menu",
            group = Groups.OVERLAYS,
            meta =
                PageMeta(
                    title = bi("Context Menu", "Context Menu"),
                    summary =
                        bi(
                            "右键 / 点击菜单：items 是树形结构，选择后触发 item-selected。",
                            "Right-click or click menu: items form a tree and choosing one fires item-selected.",
                        ),
                    docsUrl = "$DOCS_BASE/context-menu",
                    sourcePath = "pages/Overlays.kt",
                ),
            builder = ::buildContextMenuPage,
        ),
        CatalogEntry(
            id = "overlay",
            group = Groups.OVERLAYS,
            meta =
                PageMeta(
                    title = bi("Overlay", "Overlay"),
                    summary =
                        bi(
                            "底层浮层元素：Dialog / Select / ComboBox 都构建在它之上。",
                            "Low-level overlay element: Dialog / Select / ComboBox are all built on it.",
                        ),
                    docsUrl = "$DOCS_BASE/dialog",
                    docsNote =
                        bi(
                            "vaadin-overlay 是内部基础元素，官方文档见 Dialog。",
                            "vaadin-overlay is an internal building block; the official documentation is " +
                                "under Dialog.",
                        ),
                    sourcePath = "pages/Overlays.kt",
                ),
            builder = ::buildOverlayPage,
        ),
        CatalogEntry(
            id = "login",
            group = Groups.OVERLAYS,
            meta =
                PageMeta(
                    title = bi("Login", "Login"),
                    summary =
                        bi(
                            "登录表单与登录浮层：用户名 / 密码校验、错误状态与 forgot-password 事件。",
                            "Login form and login overlay: username / password validation, the error state and " +
                                "the forgot-password event.",
                        ),
                    docsUrl = "$DOCS_BASE/login",
                    sourcePath = "pages/Overlays.kt",
                ),
            builder = ::buildLoginPage,
        ),
    )

private fun buildDialogPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "内容通过 renderer 注入浮层；footerRenderer 放操作按钮。",
                "renderer injects the content into the overlay; footerRenderer holds the action buttons.",
            ),
        )
    val log = EventLog(5)
    val dialog = createDialog()
    dialog.dataset["test"] = "dialog-basic"
    val content =
        column(
            paragraph(
                tr(
                    "对话框内容由 renderer 提供：renderer(root, dialog) 里向 root 追加节点。",
                    "The dialog content comes from renderer: append nodes to root inside renderer(root, dialog).",
                ),
            ),
            paragraph(
                tr(
                    "按 Esc 或点击遮罩会关闭，除非打开对应开关。",
                    "Pressing Esc or clicking the backdrop closes it, unless the matching switch is on.",
                ),
            ),
        )
    val closeButton = createButton { textContent = tr("关闭", "Close") }.withTheme("primary")
    closeButton.clickEvent.addHandler { dialog.opened = false }
    dialog.renderer = { root, _ -> root.append(content) }
    dialog.footerRenderer = { root, _ -> root.append(closeButton) }
    dialog.headerTitle = tr("示例对话框", "Example dialog")
    // `opened` is initialised to `false` when the dialog upgrades, which also fires
    // `opened-changed`; the log only starts once the example is actually used.
    var openedByUser = false
    dialog.listen(vaadin.dialog.DialogEvents.openedChanged) {
        if (openedByUser) log.add("opened-changed · ${dialog.opened}")
    }
    dialog.listen(vaadin.dialog.DialogEvents.closed) { log.add("closed") }

    val openButton =
        createButton { textContent = tr("打开对话框", "Open dialog") }.apply {
            clickEvent.addHandler {
                openedByUser = true
                dialog.opened = true
            }
        }
    // The dialog element itself has to be in the document; it renders its content into an overlay.
    basic.body.append(example(null, column(openButton, dialog, log.root)))
    view.body.append(basic.root)

    val options =
        section(
            tr("尺寸与关闭行为", "Size and closing behaviour"),
            tr(
                "width / height 控制浮层尺寸，noCloseOnEsc 与 noCloseOnOutsideClick 关闭默认的两种关闭方式。",
                "width / height set the overlay size; noCloseOnEsc and noCloseOnOutsideClick disable the two " +
                    "default ways to close it.",
            ),
        )
    val configured = createDialog()
    configured.headerTitle = tr("不可用 Esc / 遮罩关闭", "Not closable with Esc / backdrop")
    configured.width = "28rem"
    configured.noCloseOnEsc = true
    configured.noCloseOnOutsideClick = true
    val configuredContent = paragraph(tr("只能用下面的按钮关闭。", "Only the button below closes it."))
    val configuredClose = createButton { textContent = tr("关闭", "Close") }
    configuredClose.clickEvent.addHandler { configured.opened = false }
    configured.renderer = { root, _ -> root.append(configuredContent) }
    configured.footerRenderer = { root, _ -> root.append(configuredClose) }

    val modeless = createDialog()
    modeless.modeless = true
    modeless.headerTitle = tr("非模态对话框", "Modeless dialog")
    val modelessContent =
        paragraph(tr("非模态对话框不会阻塞页面其他部分。", "A modeless dialog does not block the rest of the page."))
    val modelessClose = createButton { textContent = tr("关闭", "Close") }
    modelessClose.clickEvent.addHandler { modeless.opened = false }
    modeless.renderer = { root, _ -> root.append(modelessContent) }
    modeless.footerRenderer = { root, _ -> root.append(modelessClose) }

    options.body.append(
        example(
            null,
            column(
                row(
                    createButton { textContent = tr("打开（自定义尺寸）", "Open (custom size)") }
                        .withTheme("tertiary").apply {
                            clickEvent.addHandler { configured.opened = true }
                        },
                    createButton { textContent = tr("打开（非模态）", "Open (modeless)") }
                        .withTheme("tertiary").apply {
                            clickEvent.addHandler { modeless.opened = true }
                        },
                ),
                // Dialogs only render their overlay while they are part of the document.
                configured,
                modeless,
            ),
        ),
    )
    view.body.append(options.root)

    return view
}

private fun buildConfirmDialogPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "确认 / 取消会分别触发 confirm 与 cancel，之后触发 closed。",
                "Confirm / cancel fire confirm and cancel respectively, followed by closed.",
            ),
        )
    val log = EventLog(6)
    val confirm = createConfirmDialog()
    confirm.dataset["test"] = "confirm-dialog-basic"
    confirm.header = tr("删除这条记录？", "Delete this record?")
    confirm.message = tr("删除后无法恢复。", "This cannot be undone.")
    confirm.confirmText = tr("删除", "Delete")
    confirm.cancelText = tr("保留", "Keep")
    confirm.listen(ConfirmDialogEvents.confirm) { log.add("confirm") }
    confirm.listen(ConfirmDialogEvents.cancel) { log.add("cancel") }
    confirm.listen(ConfirmDialogEvents.closed) { log.add("closed") }

    val openButton =
        createButton { textContent = tr("删除记录", "Delete record") }.withTheme("error").apply {
            clickEvent.addHandler { confirm.opened = true }
        }
    basic.body.append(example(null, column(openButton, confirm, log.root)))
    view.body.append(basic.root)

    val threeWay =
        section(
            tr("三种结果", "Three outcomes"),
            tr(
                "rejectButtonVisible 打开第三个按钮，事件为 reject。",
                "rejectButtonVisible adds a third button, which fires reject.",
            ),
        )
    val threeLog = EventLog(4)
    val threeWayDialog = createConfirmDialog()
    threeWayDialog.header = tr("保存修改？", "Save changes?")
    threeWayDialog.message =
        tr("可以保存、放弃或返回继续编辑。", "You can save, discard or go back to keep editing.")
    threeWayDialog.confirmText = tr("保存", "Save")
    threeWayDialog.rejectText = tr("放弃", "Discard")
    threeWayDialog.cancelText = tr("返回", "Back")
    threeWayDialog.rejectButtonVisible = true
    threeWayDialog.rejectTheme = "error tertiary"
    threeWayDialog.listen(ConfirmDialogEvents.confirm) { threeLog.add(tr("confirm → 保存", "confirm → Save")) }
    threeWayDialog.listen(ConfirmDialogEvents.reject) { threeLog.add(tr("reject → 放弃", "reject → Discard")) }
    threeWayDialog.listen(ConfirmDialogEvents.cancel) { threeLog.add(tr("cancel → 返回", "cancel → Back")) }

    threeWay.body.append(
        example(
            null,
            column(
                createButton { textContent = tr("打开三按钮对话框", "Open three-button dialog") }
                    .withTheme("tertiary").apply {
                        clickEvent.addHandler { threeWayDialog.opened = true }
                    },
                threeWayDialog,
                threeLog.root,
            ),
        ),
    )
    view.body.append(threeWay.root)

    return view
}

private fun buildNotificationPage(meta: PageMeta): PageView {
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
    document.body.appendChild(persistentNote)

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
            ),
        ),
    )
    view.body.append(persistent.root)

    return view
}

private fun buildPopoverPage(meta: PageMeta): PageView {
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

    val positions =
        section(
            tr("位置", "Position"),
            tr(
                "position 接受 top / bottom / start / end 等值，改变浮层相对触发元素的位置。",
                "position takes values such as top / bottom / start / end and moves the overlay around the trigger.",
            ),
        )
    val positionedButtons = mutableListOf<HTMLElement>()
    listOf("top", "bottom", "start", "end").forEachIndexed { index, position ->
        val id = "popover-position-$index"
        val button = createButton { textContent = position }.withTheme("tertiary")
        button.id = ElementId(id)
        val positioned = createPopover()
        positioned.`for` = id
        positioned.position = position
        positioned.append(paragraph("position = $position"))
        positionedButtons += row(button, positioned)
    }
    positions.body.append(example(null, row(*positionedButtons.toTypedArray())))
    view.body.append(positions.root)

    val modal =
        section(
            tr("模态与背景", "Modal and backdrop"),
            tr(
                "modal 会阻止页面其他交互，withBackdrop 打开遮罩。",
                "modal blocks the rest of the page and withBackdrop shows the backdrop.",
            ),
        )
    val modalTrigger = createButton { textContent = tr("打开模态浮层", "Open modal popover") }.withTheme("contrast")
    modalTrigger.id = ElementId("popover-modal-trigger")
    val modalPopover = createPopover()
    modalPopover.`for` = "popover-modal-trigger"
    modalPopover.modal = true
    modalPopover.withBackdrop = true
    val modalClose = createButton { textContent = tr("关闭", "Close") }
    modalClose.clickEvent.addHandler { modalPopover.opened = false }
    modalPopover.append(column(paragraph(tr("这是模态浮层。", "This is a modal popover.")), modalClose))
    modal.body.append(example(null, row(modalTrigger, modalPopover)))
    view.body.append(modal.root)

    return view
}

private fun buildTooltipPage(meta: PageMeta): PageView {
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
    slotted.body.append(example(null, row(buttonWithTooltip, fieldWithTooltip)))
    view.body.append(slotted.root)

    val linked =
        section(
            tr("用 for 关联", "Linked with for"),
            tr(
                "不在组件内部时，用 for 指向目标元素的 id。",
                "Outside a component, point for at the id of the target element.",
            ),
        )
    val target = div("example").apply { textContent = tr("这个区块带有一个提示", "This block has a hint") }
    target.id = ElementId("tooltip-target")
    target.style.setProperty("padding", "var(--lumo-space-m)")
    val tooltip = createTooltip { text = tr("关联到任意元素的提示", "Hint linked to any element") }
    tooltip.`for` = "tooltip-target"
    linked.body.append(example(null, column(target), tooltip))
    view.body.append(linked.root)

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
    delays.body.append(example(null, row(mdTarget, mdTooltip)))
    view.body.append(delays.root)

    return view
}

private fun buildContextMenuPage(meta: PageMeta): PageView {
    val view = page(meta)

    val items: Array<ContextMenuItemData> =
        jsObject(
            """
            [
              {"text": "${tr("剪切", "Cut")}"},
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

    val clickMode =
        section(
            tr("左键打开", "Open on click"),
            tr(
                "openOn 设为 click 时用左键展开，适合做「操作」按钮。",
                "With openOn set to click the menu opens on a left click, which suits an actions button.",
            ),
        )
    val trigger = createButton { textContent = tr("操作", "Actions") }.withTheme("tertiary")
    val clickMenu = createContextMenu()
    clickMenu.openOn = "click"
    clickMenu.listenOn = trigger
    clickMenu.items = items
    clickMode.body.append(example(null, row(trigger, clickMenu)))
    view.body.append(clickMode.root)

    return view
}

private fun buildOverlayPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "renderer(root, owner, model) 负责内容；withBackdrop 打开遮罩。",
                "renderer(root, owner, model) supplies the content; withBackdrop shows the backdrop.",
            ),
        )
    val overlay = createOverlay()
    overlay.dataset["test"] = "overlay-basic"
    overlay.withBackdrop = true
    val overlayContent =
        column(
            paragraph(
                tr(
                    "这是一个裸 overlay，没有 dialog 的头部与底部。",
                    "This is a bare overlay, without the dialog header and footer.",
                ),
            ),
        )
    val overlayClose = createButton { textContent = tr("关闭", "Close") }
    overlayClose.clickEvent.addHandler { overlay.opened = false }
    overlayContent.append(overlayClose)
    overlay.renderer = { root, _, _ -> root.append(overlayContent) }

    val openButton =
        createButton { textContent = tr("打开 overlay", "Open overlay") }.apply {
            clickEvent.addHandler { overlay.opened = true }
        }
    basic.body.append(example(null, row(openButton, overlay)))
    view.body.append(basic.root)

    val modeless =
        section(
            tr("无遮罩 / 非模态", "No backdrop / modeless"),
            tr(
                "modeless 让浮层不抢焦点；withBackdrop=false 去掉遮罩。",
                "modeless keeps the overlay from taking focus; withBackdrop=false removes the backdrop.",
            ),
        )
    val modelessOverlay = createOverlay()
    modelessOverlay.withBackdrop = false
    modelessOverlay.modeless = true
    val modelessContent =
        column(
            paragraph(tr("非模态浮层，页面其他部分仍可交互。", "Modeless overlay: the rest of the page stays interactive.")),
        )
    val modelessClose = createButton { textContent = tr("关闭", "Close") }
    modelessClose.clickEvent.addHandler { modelessOverlay.opened = false }
    modelessContent.append(modelessClose)
    modelessOverlay.renderer = { root, _, _ -> root.append(modelessContent) }
    modeless.body.append(
        example(
            null,
            row(
                createButton { textContent = tr("打开非模态 overlay", "Open modeless overlay") }
                    .withTheme("tertiary").apply {
                        clickEvent.addHandler { modelessOverlay.opened = true }
                    },
                modelessOverlay,
            ),
        ),
    )
    view.body.append(modeless.root)

    return view
}

private fun buildLoginPage(meta: PageMeta): PageView {
    val view = page(meta)

    val form =
        section(
            tr("登录表单", "Login form"),
            tr(
                "点击提交后字段会先校验，然后触发 login 事件（本页不连后端）。",
                "Submitting validates the fields first and then fires login (this page has no backend).",
            ),
        )
    val log = EventLog(5)
    val loginForm = createLoginForm()
    loginForm.dataset["test"] = "login-form"
    loginForm.listen(LoginFormEvents.login) { log.add("login") }
    loginForm.listen(LoginFormEvents.forgotPassword) { log.add("forgot-password") }
    // `error` starts as `false`, so only the transitions to the error state are interesting.
    loginForm.listen(LoginFormEvents.errorChanged) { if (loginForm.error) log.add("error-changed · true") }

    val errorSwitch = createSwitch { label = tr("显示错误状态", "Show error state") }
    errorSwitch.listen(SwitchEvents.checkedChanged) { loginForm.error = errorSwitch.checked }
    val disabledSwitch = createSwitch { label = tr("禁用表单", "Disable form") }
    disabledSwitch.listen(SwitchEvents.checkedChanged) { loginForm.disabled = disabledSwitch.checked }

    form.body.append(example(null, row(loginForm, column(errorSwitch, disabledSwitch, log.root))))
    view.body.append(form.root)

    val overlay =
        section(
            tr("登录浮层", "Login overlay"),
            tr(
                "LoginOverlay 把同一个表单放进全屏浮层，适合应用入口。",
                "LoginOverlay puts the same form into a full-screen overlay, which suits an app entry point.",
            ),
        )
    val loginOverlay = createLoginOverlay()
    loginOverlay.dataset["test"] = "login-overlay"
    loginOverlay.description = tr("使用任意用户名和密码体验交互", "Use any username and password to try it")
    loginOverlay.listen(LoginFormEvents.login) { loginOverlay.opened = false }
    overlay.body.append(
        example(
            null,
            row(
                createButton { textContent = tr("打开登录浮层", "Open login overlay") }.withTheme("primary").apply {
                    clickEvent.addHandler { loginOverlay.opened = true }
                },
                loginOverlay,
            ),
        ),
    )
    view.body.append(overlay.root)

    return view
}
