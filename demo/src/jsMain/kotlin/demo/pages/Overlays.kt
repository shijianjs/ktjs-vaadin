package demo.pages

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.ui.EventLog
import demo.ui.PageMeta
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
                    title = "Dialog",
                    summary = "模态对话框：renderer 提供内容，header / footer 渲染器提供标题与操作区。",
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
                    title = "Confirm Dialog",
                    summary = "确认对话框：确认 / 取消 / 拒绝三种结果与对应事件。",
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
                    title = "Notification",
                    summary = "轻量提示：位置、时长、主题变体，以及需要手动关闭的常驻通知。",
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
                    title = "Popover",
                    summary = "锚定到某个元素的浮层，支持点击 / 悬停触发、位置与模态模式。",
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
                    title = "Tooltip",
                    summary = "悬停提示：可以放进组件的 tooltip 插槽，也可以用 for 关联任意元素。",
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
                    title = "Context Menu",
                    summary = "右键 / 点击菜单：items 是树形结构，选择后触发 item-selected。",
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
                    title = "Overlay",
                    summary = "底层浮层元素：Dialog / Select / ComboBox 都构建在它之上。",
                    docsUrl = "$DOCS_BASE/dialog",
                    docsNote = "vaadin-overlay 是内部基础元素，官方文档见 Dialog。",
                    sourcePath = "pages/Overlays.kt",
                ),
            builder = ::buildOverlayPage,
        ),
        CatalogEntry(
            id = "login",
            group = Groups.OVERLAYS,
            meta =
                PageMeta(
                    title = "Login",
                    summary = "登录表单与登录浮层：用户名 / 密码校验、错误状态与 forgot-password 事件。",
                    docsUrl = "$DOCS_BASE/login",
                    sourcePath = "pages/Overlays.kt",
                ),
            builder = ::buildLoginPage,
        ),
    )

private fun buildDialogPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("基本用法", "内容通过 renderer 注入浮层；footerRenderer 放操作按钮。")
    val log = EventLog(5)
    val dialog = createDialog()
    dialog.dataset["test"] = "dialog-basic"
    val content =
        column(
            paragraph("对话框内容由 renderer 提供：renderer(root, dialog) 里向 root 追加节点。"),
            paragraph("按 Esc 或点击遮罩会关闭，除非打开对应开关。"),
        )
    val closeButton = createButton { textContent = "关闭" }.withTheme("primary")
    closeButton.clickEvent.addHandler { dialog.opened = false }
    dialog.renderer = { root, _ -> root.append(content) }
    dialog.footerRenderer = { root, _ -> root.append(closeButton) }
    dialog.headerTitle = "示例对话框"
    // `opened` is initialised to `false` when the dialog upgrades, which also fires
    // `opened-changed`; the log only starts once the example is actually used.
    var openedByUser = false
    dialog.listen(vaadin.dialog.DialogEvents.openedChanged) {
        if (openedByUser) log.add("opened-changed · ${dialog.opened}")
    }
    dialog.listen(vaadin.dialog.DialogEvents.closed) { log.add("closed") }

    val openButton =
        createButton { textContent = "打开对话框" }.apply {
            clickEvent.addHandler {
                openedByUser = true
                dialog.opened = true
            }
        }
    // The dialog element itself has to be in the document; it renders its content into an overlay.
    basic.body.append(example(null, column(openButton, dialog, log.root)))
    view.body.append(basic.root)

    val options = section("尺寸与关闭行为", "width / height 控制浮层尺寸，noCloseOnEsc 与 noCloseOnOutsideClick 关闭默认的两种关闭方式。")
    val configured = createDialog()
    configured.headerTitle = "不可用 Esc / 遮罩关闭"
    configured.width = "28rem"
    configured.noCloseOnEsc = true
    configured.noCloseOnOutsideClick = true
    val configuredContent = paragraph("只能用下面的按钮关闭。")
    val configuredClose = createButton { textContent = "关闭" }
    configuredClose.clickEvent.addHandler { configured.opened = false }
    configured.renderer = { root, _ -> root.append(configuredContent) }
    configured.footerRenderer = { root, _ -> root.append(configuredClose) }

    val modeless = createDialog()
    modeless.modeless = true
    modeless.headerTitle = "非模态对话框"
    val modelessContent = paragraph("非模态对话框不会阻塞页面其他部分。")
    val modelessClose = createButton { textContent = "关闭" }
    modelessClose.clickEvent.addHandler { modeless.opened = false }
    modeless.renderer = { root, _ -> root.append(modelessContent) }
    modeless.footerRenderer = { root, _ -> root.append(modelessClose) }

    options.body.append(
        example(
            null,
            column(
                row(
                    createButton { textContent = "打开（自定义尺寸）" }.withTheme("tertiary").apply {
                        clickEvent.addHandler { configured.opened = true }
                    },
                    createButton { textContent = "打开（非模态）" }.withTheme("tertiary").apply {
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

    return view.root
}

private fun buildConfirmDialogPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("基本用法", "确认 / 取消会分别触发 confirm 与 cancel，之后触发 closed。")
    val log = EventLog(6)
    val confirm = createConfirmDialog()
    confirm.dataset["test"] = "confirm-dialog-basic"
    confirm.header = "删除这条记录？"
    confirm.message = "删除后无法恢复。"
    confirm.confirmText = "删除"
    confirm.cancelText = "保留"
    confirm.listen(ConfirmDialogEvents.confirm) { log.add("confirm") }
    confirm.listen(ConfirmDialogEvents.cancel) { log.add("cancel") }
    confirm.listen(ConfirmDialogEvents.closed) { log.add("closed") }

    val openButton =
        createButton { textContent = "删除记录" }.withTheme("error").apply {
            clickEvent.addHandler { confirm.opened = true }
        }
    basic.body.append(example(null, column(openButton, confirm, log.root)))
    view.body.append(basic.root)

    val threeWay = section("三种结果", "rejectButtonVisible 打开第三个按钮，事件为 reject。")
    val threeLog = EventLog(4)
    val threeWayDialog = createConfirmDialog()
    threeWayDialog.header = "保存修改？"
    threeWayDialog.message = "可以保存、放弃或返回继续编辑。"
    threeWayDialog.confirmText = "保存"
    threeWayDialog.rejectText = "放弃"
    threeWayDialog.cancelText = "返回"
    threeWayDialog.rejectButtonVisible = true
    threeWayDialog.rejectTheme = "error tertiary"
    threeWayDialog.listen(ConfirmDialogEvents.confirm) { threeLog.add("confirm → 保存") }
    threeWayDialog.listen(ConfirmDialogEvents.reject) { threeLog.add("reject → 放弃") }
    threeWayDialog.listen(ConfirmDialogEvents.cancel) { threeLog.add("cancel → 返回") }

    threeWay.body.append(
        example(
            null,
            column(
                createButton { textContent = "打开三按钮对话框" }.withTheme("tertiary").apply {
                    clickEvent.addHandler { threeWayDialog.opened = true }
                },
                threeWayDialog,
                threeLog.root,
            ),
        ),
    )
    view.body.append(threeWay.root)

    return view.root
}

private fun buildNotificationPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val positions =
        section("位置与时长", "Notification.show 是一次性提示；options 可以指定 position、duration 与 theme。")
    positions.body.append(
        example(
            null,
            row(
                createButton { textContent = "右上角" }.withTheme("tertiary").apply {
                    clickEvent.addHandler {
                        Notification.show("右上角的提示", jsObject("""{"position":"top-end","duration":3000}"""))
                    }
                },
                createButton { textContent = "顶部居中" }.withTheme("tertiary").apply {
                    clickEvent.addHandler {
                        Notification.show("顶部居中的提示", jsObject("""{"position":"top-center","duration":3000}"""))
                    }
                },
                createButton { textContent = "底部居中（默认）" }.withTheme("tertiary").apply {
                    clickEvent.addHandler { Notification.show("底部居中的提示") }
                },
            ),
        ),
    )
    view.body.append(positions.root)

    val themes = section("主题变体", "theme 会传播到内部的 notification-card。")
    themes.body.append(
        example(
            null,
            row(
                createButton { textContent = "success" }.withTheme("success").apply {
                    clickEvent.addHandler {
                        Notification.show("保存成功", jsObject("""{"theme":"success","position":"bottom-end"}"""))
                    }
                },
                createButton { textContent = "error" }.withTheme("error").apply {
                    clickEvent.addHandler {
                        Notification.show("保存失败", jsObject("""{"theme":"error","position":"bottom-end"}"""))
                    }
                },
                createButton { textContent = "contrast" }.withTheme("contrast").apply {
                    clickEvent.addHandler {
                        Notification.show("对比色提示", jsObject("""{"theme":"contrast","position":"bottom-end"}"""))
                    }
                },
            ),
        ),
    )
    view.body.append(themes.root)

    val persistent = section("常驻通知", "duration 设为 0 时不自动关闭，需要在 renderer 里放一个关闭按钮。")
    val persistentNote = createNotification()
    persistentNote.duration = 0.0
    persistentNote.position = "middle"
    val noteContent =
        row(
            paragraph("这条通知会一直显示，直到点击关闭。"),
            createButton { textContent = "关闭" }.withTheme("tertiary small").apply {
                clickEvent.addHandler { persistentNote.opened = false }
            },
        )
    persistentNote.renderer = { root, _ -> root.append(noteContent) }
    document.body.appendChild(persistentNote)

    persistent.body.append(
        example(
            null,
            row(
                createButton { textContent = "显示常驻通知" }.apply {
                    clickEvent.addHandler { persistentNote.opened = true }
                },
                createButton { textContent = "关闭" }.withTheme("tertiary").apply {
                    clickEvent.addHandler { persistentNote.opened = false }
                },
            ),
        ),
    )
    view.body.append(persistent.root)

    return view.root
}

private fun buildPopoverPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("基本用法", "popover 通过 for 指向触发元素的 id；默认点击触发、点击外部关闭。")
    val log = EventLog(4)
    val trigger = createButton { textContent = "点击查看" }.withTheme("primary")
    trigger.id = ElementId("popover-trigger")
    trigger.dataset["test"] = "popover-trigger"

    val popover = createPopover()
    popover.`for` = "popover-trigger"
    popover.append(
        column(
            heading(3, "浮层内容"),
            paragraph("任意 DOM 都可以放进 popover 的默认插槽。"),
        ),
    )
    popover.listen(PopoverEvents.openedChanged) { log.add("opened-changed · ${popover.opened}") }

    basic.body.append(example(null, column(row(trigger, popover), log.root)))
    view.body.append(basic.root)

    val positions = section("位置", "position 接受 top / bottom / start / end 等值，改变浮层相对触发元素的位置。")
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

    val modal = section("模态与背景", "modal 会阻止页面其他交互，withBackdrop 打开遮罩。")
    val modalTrigger = createButton { textContent = "打开模态浮层" }.withTheme("contrast")
    modalTrigger.id = ElementId("popover-modal-trigger")
    val modalPopover = createPopover()
    modalPopover.`for` = "popover-modal-trigger"
    modalPopover.modal = true
    modalPopover.withBackdrop = true
    val modalClose = createButton { textContent = "关闭" }
    modalClose.clickEvent.addHandler { modalPopover.opened = false }
    modalPopover.append(column(paragraph("这是模态浮层。"), modalClose))
    modal.body.append(example(null, row(modalTrigger, modalPopover)))
    view.body.append(modal.root)

    return view.root
}

private fun buildTooltipPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val slotted = section("放进插槽", "把 tooltip 作为子元素并设置 slot=\"tooltip\"，组件会在悬停 / 聚焦时显示它。")
    val buttonWithTooltip = createButton { textContent = "悬停我" }
    buttonWithTooltip.append(createTooltip { text = "这是按钮上的提示" }.apply { slot = "tooltip" })
    val fieldWithTooltip =
        vaadin.textfield.createTextField {
            label = "字段的提示"
            placeholder = "聚焦后显示"
        }
    fieldWithTooltip.append(createTooltip { text = "字段级的提示文本" }.apply { slot = "tooltip" })
    slotted.body.append(example(null, row(buttonWithTooltip, fieldWithTooltip)))
    view.body.append(slotted.root)

    val linked = section("用 for 关联", "不在组件内部时，用 for 指向目标元素的 id。")
    val target = div("example").apply { textContent = "这个区块带有一个提示" }
    target.id = ElementId("tooltip-target")
    target.style.setProperty("padding", "var(--lumo-space-m)")
    val tooltip = createTooltip { text = "关联到任意元素的提示" }
    tooltip.`for` = "tooltip-target"
    linked.body.append(example(null, column(target), tooltip))
    view.body.append(linked.root)

    val delays = section("延迟与 Markdown", "hoverDelay / focusDelay / hideDelay 以毫秒为单位；markdown 让提示文本按 Markdown 解析。")
    val mdTarget = createButton { textContent = "Markdown 提示" }
    mdTarget.id = ElementId("tooltip-markdown")
    val mdTooltip =
        createTooltip {
            text = "**粗体** 与 *斜体*，以及 `代码`"
            markdown = true
            hoverDelay = 0.0
        }
    mdTooltip.`for` = "tooltip-markdown"
    delays.body.append(example(null, row(mdTarget, mdTooltip)))
    view.body.append(delays.root)

    return view.root
}

private fun buildContextMenuPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val items: Array<ContextMenuItemData> =
        jsObject(
            """
            [
              {"text": "剪切"},
              {"text": "复制"},
              {"text": "粘贴", "disabled": true},
              {"text": "更多", "children": [
                {"text": "导出为 CSV"},
                {"text": "导出为 JSON"}
              ]}
            ]
            """.trimIndent(),
        ).unsafeCast<Array<ContextMenuItemData>>()

    val basic = section("右键打开", "openOn 默认是 contextmenu；listenOn 指向接收事件的元素。")
    val log = EventLog(5)
    val surface = div("example").apply { textContent = "在这个区域点击右键" }
    surface.dataset["test"] = "context-menu-surface"
    val menu = createContextMenu()
    menu.openOn = "contextmenu"
    menu.listenOn = surface
    menu.items = items
    menu.listen(ContextMenuEvents.itemSelected) { log.add("item-selected") }
    menu.listen(ContextMenuEvents.openedChanged) { log.add("opened-changed · ${menu.opened}") }
    basic.body.append(example(null, column(surface, log.root, menu)))
    view.body.append(basic.root)

    val clickMode = section("左键打开", "openOn 设为 click 时用左键展开，适合做「操作」按钮。")
    val trigger = createButton { textContent = "操作" }.withTheme("tertiary")
    val clickMenu = createContextMenu()
    clickMenu.openOn = "click"
    clickMenu.listenOn = trigger
    clickMenu.items = items
    clickMode.body.append(example(null, row(trigger, clickMenu)))
    view.body.append(clickMode.root)

    return view.root
}

private fun buildOverlayPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("基本用法", "renderer(root, owner, model) 负责内容；withBackdrop 打开遮罩。")
    val overlay = createOverlay()
    overlay.dataset["test"] = "overlay-basic"
    overlay.withBackdrop = true
    val overlayContent = column(paragraph("这是一个裸 overlay，没有 dialog 的头部与底部。"))
    val overlayClose = createButton { textContent = "关闭" }
    overlayClose.clickEvent.addHandler { overlay.opened = false }
    overlayContent.append(overlayClose)
    overlay.renderer = { root, _, _ -> root.append(overlayContent) }

    val openButton =
        createButton { textContent = "打开 overlay" }.apply {
            clickEvent.addHandler { overlay.opened = true }
        }
    basic.body.append(example(null, row(openButton, overlay)))
    view.body.append(basic.root)

    val modeless = section("无遮罩 / 非模态", "modeless 让浮层不抢焦点；withBackdrop=false 去掉遮罩。")
    val modelessOverlay = createOverlay()
    modelessOverlay.withBackdrop = false
    modelessOverlay.modeless = true
    val modelessContent = column(paragraph("非模态浮层，页面其他部分仍可交互。"))
    val modelessClose = createButton { textContent = "关闭" }
    modelessClose.clickEvent.addHandler { modelessOverlay.opened = false }
    modelessContent.append(modelessClose)
    modelessOverlay.renderer = { root, _, _ -> root.append(modelessContent) }
    modeless.body.append(
        example(
            null,
            row(
                createButton { textContent = "打开非模态 overlay" }.withTheme("tertiary").apply {
                    clickEvent.addHandler { modelessOverlay.opened = true }
                },
                modelessOverlay,
            ),
        ),
    )
    view.body.append(modeless.root)

    return view.root
}

private fun buildLoginPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val form = section("登录表单", "点击提交后字段会先校验，然后触发 login 事件（本页不连后端）。")
    val log = EventLog(5)
    val loginForm = createLoginForm()
    loginForm.dataset["test"] = "login-form"
    loginForm.listen(LoginFormEvents.login) { log.add("login") }
    loginForm.listen(LoginFormEvents.forgotPassword) { log.add("forgot-password") }
    // `error` starts as `false`, so only the transitions to the error state are interesting.
    loginForm.listen(LoginFormEvents.errorChanged) { if (loginForm.error) log.add("error-changed · true") }

    val errorSwitch = createSwitch { label = "显示错误状态" }
    errorSwitch.listen(SwitchEvents.checkedChanged) { loginForm.error = errorSwitch.checked }
    val disabledSwitch = createSwitch { label = "禁用表单" }
    disabledSwitch.listen(SwitchEvents.checkedChanged) { loginForm.disabled = disabledSwitch.checked }

    form.body.append(example(null, row(loginForm, column(errorSwitch, disabledSwitch, log.root))))
    view.body.append(form.root)

    val overlay = section("登录浮层", "LoginOverlay 把同一个表单放进全屏浮层，适合应用入口。")
    val loginOverlay = createLoginOverlay()
    loginOverlay.dataset["test"] = "login-overlay"
    loginOverlay.description = "使用任意用户名和密码体验交互"
    loginOverlay.listen(LoginFormEvents.login) { loginOverlay.opened = false }
    overlay.body.append(
        example(
            null,
            row(
                createButton { textContent = "打开登录浮层" }.withTheme("primary").apply {
                    clickEvent.addHandler { loginOverlay.opened = true }
                },
                loginOverlay,
            ),
        ),
    )
    view.body.append(overlay.root)

    return view.root
}
