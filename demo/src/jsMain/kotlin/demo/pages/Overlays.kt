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
import demo.ui.heading
import demo.ui.jsArray
import demo.ui.jsObject
import demo.ui.listen
import demo.ui.page
import demo.ui.paragraph
import demo.ui.row
import demo.ui.section
import demo.ui.setHidden
import demo.ui.withClass
import demo.ui.withTheme
import js.objects.unsafeJso
import kotlin.js.JsAny
import kotlin.js.js
import kotlin.js.unsafeCast
import vaadin.button.createButton
import vaadin.confirmdialog.ConfirmDialogEvents
import vaadin.confirmdialog.createConfirmDialog
import vaadin.dialog.DialogEvents
import vaadin.dialog.createDialog
import vaadin.emailfield.createEmailField
import vaadin.formlayout.createFormLayout
import vaadin.grid.GridElement
import vaadin.grid.createGrid
import vaadin.grid.createGridColumn
import vaadin.grid.createGridSelectionColumn
import vaadin.icon.createIcon
import vaadin.integerfield.createIntegerField
import vaadin.login.LoginFormEvents
import vaadin.login.LoginI18n
import vaadin.login.LoginI18nErrorMessage
import vaadin.login.LoginI18nForm
import vaadin.login.LoginI18nHeader
import vaadin.login.createLoginForm
import vaadin.login.createLoginOverlay
import vaadin.overlay.createOverlay
import vaadin.switch.SwitchEvents
import vaadin.switch.createSwitch
import vaadin.textarea.createTextArea
import vaadin.textfield.createTextField
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
                    sourcePath = "pages/OverlayPopups.kt",
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
                    sourcePath = "pages/OverlayPopups.kt",
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
                    sourcePath = "pages/OverlayPopups.kt",
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
                    sourcePath = "pages/OverlayPopups.kt",
                ),
            builder = ::buildContextMenuPage,
        ),
    )

/** Grid of people with the columns the overlay examples filter and right-click on. */
internal fun personGrid(list: List<Person> = people(5), withSelection: Boolean = false): GridElement {
    val table = createGrid()
    table.items = personItems(list, withDisplayName = true)
    table.allRowsVisible = true
    if (withSelection) table.append(createGridSelectionColumn())
    table.append(
        createGridColumn {
            header = tr("名", "First name")
            path = "firstName"
        },
        createGridColumn {
            header = tr("姓", "Last name")
            path = "lastName"
        },
        createGridColumn {
            header = tr("邮箱", "Email")
            path = "email"
        },
        createGridColumn {
            header = tr("职务", "Profession")
            path = "profession"
        },
    )
    return table
}

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
    dialog.listen(DialogEvents.openedChanged) {
        if (openedByUser) log.add("opened-changed · ${dialog.opened}")
    }
    dialog.listen(DialogEvents.closed) { log.add("closed") }

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

    val formSection =
        section(
            tr("表单内容", "Form content"),
            tr(
                "表单放进 renderer 里，取消与确认按钮放进 footerRenderer。",
                "The form goes into the renderer and the cancel / confirm buttons into footerRenderer.",
            ),
        )
    val formDialog = createDialog { headerTitle = tr("新员工", "New employee") }
    val employeeForm =
        createFormLayout {
            autoResponsive = true
            columnWidth = "18rem"
            expandFields = true
        }
    employeeForm.append(
        createTextField { label = tr("名", "First name") },
        createTextField { label = tr("姓", "Last name") },
    )
    val cancelButton = createButton { textContent = tr("取消", "Cancel") }
    cancelButton.clickEvent.addHandler { formDialog.opened = false }
    val addButton = createButton { textContent = tr("添加", "Add") }.withTheme("primary")
    addButton.clickEvent.addHandler { formDialog.opened = false }
    formDialog.renderer = { root, _ -> root.append(employeeForm) }
    formDialog.footerRenderer = { root, _ -> root.append(cancelButton, addButton) }
    formSection.body.append(
        example(
            null,
            row(
                createButton { textContent = tr("打开表单对话框", "Open form dialog") }.apply {
                    clickEvent.addHandler { formDialog.opened = true }
                },
                formDialog,
            ),
        ),
    )
    view.body.append(formSection.root)

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

    val modelessDialog = createDialog()
    modelessDialog.modeless = true
    modelessDialog.headerTitle = tr("非模态对话框", "Modeless dialog")
    val modelessContent =
        paragraph(tr("非模态对话框不会阻塞页面其他部分。", "A modeless dialog does not block the rest of the page."))
    val modelessClose = createButton { textContent = tr("关闭", "Close") }
    modelessClose.clickEvent.addHandler { modelessDialog.opened = false }
    modelessDialog.renderer = { root, _ -> root.append(modelessContent) }
    modelessDialog.footerRenderer = { root, _ -> root.append(modelessClose) }

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
                            clickEvent.addHandler { modelessDialog.opened = true }
                        },
                ),
                // Dialogs only render their overlay while they are part of the document.
                configured,
                modelessDialog,
            ),
        ),
    )
    view.body.append(options.root)

    val draggableSection =
        section(
            tr("可拖动", "Draggable"),
            tr(
                "draggable 让用户拖动浮层；非模态时对话框不会抢走焦点。",
                "draggable lets the user move the overlay; a modeless dialog does not take the focus.",
            ),
        )
    val noteDialog = createDialog {
        headerTitle = tr("添加笔记", "Add note")
        modeless = true
    }
    noteDialog.draggable = true
    val noteForm =
        createFormLayout {
            autoResponsive = true
            columnWidth = "18rem"
            expandFields = true
        }
    noteForm.append(
        createTextField { label = tr("标题", "Title") },
        createTextArea { label = tr("描述", "Description") },
    )
    val noteCancel = createButton { textContent = tr("取消", "Cancel") }
    noteCancel.clickEvent.addHandler { noteDialog.opened = false }
    val noteAdd = createButton { textContent = tr("添加笔记", "Add note") }.withTheme("primary")
    noteAdd.clickEvent.addHandler { noteDialog.opened = false }
    noteDialog.renderer = { root, _ -> root.append(noteForm) }
    noteDialog.footerRenderer = { root, _ -> root.append(noteCancel, noteAdd) }
    draggableSection.body.append(
        example(
            null,
            row(
                createButton { textContent = tr("打开可拖动对话框", "Open draggable dialog") }.apply {
                    clickEvent.addHandler { noteDialog.opened = true }
                },
                noteDialog,
            ),
        ),
    )
    view.body.append(draggableSection.root)

    val headerSection =
        section(
            tr("自定义头部", "Custom header"),
            tr(
                "headerRenderer 放在标题右侧，适合放关闭按钮等附加操作。",
                "headerRenderer fills the area next to the title, which suits extra actions such as a close button.",
            ),
        )
    val user = people(1).first()
    val userDialog = createDialog { headerTitle = tr("用户详情", "User details") }
    val headerClose = createButton { textContent = "" }.withTheme("tertiary icon").apply {
        append(createIcon { icon = "lumo:cross" })
        clickEvent.addHandler { userDialog.opened = false }
    }
    userDialog.headerRenderer = { root, _ -> root.append(headerClose) }
    val detailForm =
        createFormLayout {
            autoResponsive = true
            columnWidth = "18rem"
            expandFields = true
        }
    detailForm.append(
        createTextField {
            label = tr("姓名", "Name")
            value = user.displayName
            readonly = true
        },
        createEmailField {
            label = tr("邮箱", "Email")
            value = user.email
            readonly = true
        },
        createTextField {
            label = tr("地址", "Address")
            value = tr("万荣路 700 号，上海，中国", "700 Wanrong Road, Shanghai, China")
            readonly = true
        },
    )
    userDialog.renderer = { root, _ -> root.append(detailForm) }
    headerSection.body.append(
        example(
            null,
            row(
                createButton { textContent = tr("打开用户详情", "Open user details") }.apply {
                    clickEvent.addHandler { userDialog.opened = true }
                },
                userDialog,
            ),
        ),
    )
    view.body.append(headerSection.root)

    val footerSection =
        section(
            tr("底部按钮布局", "Footer layout"),
            tr(
                "footerRenderer 里的按钮按添加顺序排列，用 marginInlineEnd: auto 把危险操作推到左边。",
                "Buttons in footerRenderer follow insertion order; marginInlineEnd: auto pushes the destructive " +
                    "action to the leading edge.",
            ),
        )
    val deleteDialog = createDialog { headerTitle = tr("删除用户「${user.displayName}」？", "Delete user \"${user.displayName}\"?") }
    val deleteText = paragraph(tr("确定要永久删除这个用户吗？", "Are you sure you want to delete this user permanently?"))
    val deleteButton = createButton { textContent = tr("删除", "Delete") }.withTheme("primary", "error")
    deleteButton.style.setProperty("margin-inline-end", "auto")
    deleteButton.clickEvent.addHandler { deleteDialog.opened = false }
    val keepButton = createButton { textContent = tr("取消", "Cancel") }.withTheme("tertiary")
    keepButton.clickEvent.addHandler { deleteDialog.opened = false }
    deleteDialog.renderer = { root, _ -> root.append(deleteText) }
    deleteDialog.footerRenderer = { root, _ -> root.append(deleteButton, keepButton) }
    footerSection.body.append(
        example(
            null,
            row(
                createButton { textContent = tr("删除用户", "Delete user") }.withTheme("error").apply {
                    clickEvent.addHandler { deleteDialog.opened = true }
                },
                deleteDialog,
            ),
        ),
    )
    view.body.append(footerSection.root)

    val noPaddingSection =
        section(
            tr("无内边距", "No padding"),
            tr(
                "theme=\"no-padding\" 去掉内容区的内边距，适合放表格这类自带留白的组件。",
                "theme=\"no-padding\" removes the content padding, which suits components such as a grid.",
            ),
        )
    val filterDialog = createDialog { headerTitle = tr("按用户筛选报表：", "Filter reports by users:") }
    filterDialog.withTheme("no-padding")
    val filterGrid = personGrid(withSelection = true)
    filterDialog.renderer = { root, _ -> root.append(filterGrid) }
    val filterButton = createButton { textContent = tr("筛选", "Filter") }.withTheme("primary")
    filterButton.clickEvent.addHandler { filterDialog.opened = false }
    filterDialog.footerRenderer = { root, _ -> root.append(filterButton) }
    noPaddingSection.body.append(
        example(
            null,
            row(
                createButton { textContent = tr("打开筛选对话框", "Open filter dialog") }.apply {
                    clickEvent.addHandler { filterDialog.opened = true }
                },
                filterDialog,
            ),
        ),
    )
    view.body.append(noPaddingSection.root)

    val resizableSection =
        section(
            tr("可调整大小", "Resizable"),
            tr(
                "resizable 允许拖动边角改变尺寸；最小值通过 ::part(overlay) 上的 CSS 指定。",
                "resizable lets the user drag the edges; the minimum size comes from CSS on ::part(overlay).",
            ),
        )
    val employeeDialog = createDialog { headerTitle = tr("员工列表", "Employee list") }
    employeeDialog.resizable = true
    employeeDialog.draggable = true
    employeeDialog.withClass("resizable-dialog")
    employeeDialog.width = "40rem"
    val employeeGrid = personGrid(people(8))
    employeeDialog.renderer = { root, _ -> root.append(employeeGrid) }
    resizableSection.body.append(
        example(
            null,
            row(
                createButton { textContent = tr("打开可调整对话框", "Open resizable dialog") }.apply {
                    clickEvent.addHandler { employeeDialog.opened = true }
                },
                employeeDialog,
            ),
        ),
    )
    view.body.append(resizableSection.root)

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
    val status = Readout(tr("状态：", "Status: "))
    val confirm = createConfirmDialog()
    confirm.dataset["test"] = "confirm-dialog-basic"
    confirm.header = tr("删除这条记录？", "Delete this record?")
    confirm.message = tr("删除后无法恢复。", "This cannot be undone.")
    confirm.confirmText = tr("删除", "Delete")
    confirm.cancelText = tr("保留", "Keep")
    confirm.cancelButtonVisible = true
    confirm.listen(ConfirmDialogEvents.confirm) {
        log.add("confirm")
        status.set(tr("状态：", "Status: "), tr("已删除", "Deleted"))
    }
    confirm.listen(ConfirmDialogEvents.cancel) {
        log.add("cancel")
        status.set(tr("状态：", "Status: "), tr("已取消", "Canceled"))
    }
    confirm.listen(ConfirmDialogEvents.closed) { log.add("closed") }

    val openButton =
        createButton { textContent = tr("删除记录", "Delete record") }.withTheme("error").apply {
            clickEvent.addHandler {
                confirm.opened = true
                status.set(tr("状态：", "Status: "))
            }
        }
    basic.body.append(example(null, column(row(openButton, status.root), confirm, log.root)))
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
    threeWayDialog.cancelButtonVisible = true
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

    val cancelSection =
        section(
            tr("取消按钮", "Cancel button"),
            tr(
                "cancelButtonVisible 显示取消按钮，confirmTheme 让确认按钮表达危险操作。",
                "cancelButtonVisible shows the cancel button; confirmTheme marks the confirm action as destructive.",
            ),
        )
    val deleteStatus = Readout(tr("状态：", "Status: "))
    val deleteDialog = createConfirmDialog()
    deleteDialog.header = tr("删除「Q4 报告」？", "Delete \"Report Q4\"?")
    deleteDialog.message = tr("确定要永久删除这一项吗？", "Are you sure you want to permanently delete this item?")
    deleteDialog.confirmText = tr("删除", "Delete")
    deleteDialog.confirmTheme = "error primary"
    deleteDialog.cancelButtonVisible = true
    deleteDialog.listen(ConfirmDialogEvents.confirm) {
        deleteStatus.set(tr("状态：", "Status: "), tr("已删除", "Deleted"))
    }
    deleteDialog.listen(ConfirmDialogEvents.cancel) {
        deleteStatus.set(tr("状态：", "Status: "), tr("已取消", "Canceled"))
    }
    cancelSection.body.append(
        example(
            null,
            column(
                row(
                    createButton { textContent = tr("删除报告", "Delete report") }.withTheme("error").apply {
                        clickEvent.addHandler { deleteDialog.opened = true }
                    },
                    deleteStatus.root,
                ),
                deleteDialog,
            ),
        ),
    )
    view.body.append(cancelSection.root)

    val confirmOnlySection =
        section(
            tr("仅确认按钮", "Confirm only"),
            tr(
                "只保留一个确认按钮时，它用于告知结果；消息支持任意 DOM，包括粗体与链接。",
                "With only a confirm button left it acknowledges a result; the message takes any DOM, " +
                    "including bold text and links.",
            ),
        )
    val exportStatus = Readout(tr("状态：", "Status: "))
    val exportDialog = createConfirmDialog()
    exportDialog.header = tr("导出失败", "Export failed")
    exportDialog.confirmText = tr("好", "OK")
    exportDialog.cancelButtonVisible = false
    exportDialog.listen(ConfirmDialogEvents.confirm) {
        exportStatus.set(tr("状态：", "Status: "), tr("已确认", "Acknowledged"))
    }
    val exportMessage =
        paragraph(
            tr(
                "导出「Q4 报告」时发生错误，请重试。若问题仍然存在，请联系 support@company.com。",
                "An error occurred while exporting Report Q4. Please try again. If the problem persists, " +
                    "contact support@company.com.",
            ),
        )
    exportDialog.append(exportMessage)
    confirmOnlySection.body.append(
        example(
            null,
            column(
                row(
                    createButton { textContent = tr("导出报告", "Export report") }.withTheme("tertiary").apply {
                        clickEvent.addHandler { exportDialog.opened = true }
                    },
                    exportStatus.root,
                ),
                exportDialog,
            ),
        ),
    )
    view.body.append(confirmOnlySection.root)

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

    val sized =
        section(
            tr("尺寸与滚动", "Size and scrolling"),
            tr(
                "setBounds 写入浮层的宽度与高度，内容超出时由内容区滚动。",
                "setBounds sets the width and height of the overlay; overflowing content scrolls inside " +
                    "the content area.",
            ),
        )
    val sizedOverlay = createOverlay()
    val longContent = column(paragraph(tr("可滚动的内容：", "Scrollable content:")))
    repeat(12) { index ->
        longContent.append(paragraph(tr("第 ${index + 1} 行内容", "Line ${index + 1}")))
    }
    val sizedClose = createButton { textContent = tr("关闭", "Close") }
    sizedClose.clickEvent.addHandler { sizedOverlay.opened = false }
    longContent.append(sizedClose)
    sizedOverlay.renderer = { root, _, _ -> root.append(longContent) }
    sized.body.append(
        example(
            null,
            row(
                createButton { textContent = tr("打开固定尺寸 overlay", "Open sized overlay") }
                    .withTheme("tertiary").apply {
                        clickEvent.addHandler {
                            // `setBounds` writes to the overlay's internal parts, which exist only once
                            // the element has been upgraded and opened.
                            sizedOverlay.setBounds(
                                unsafeJso<vaadin.overlay.OverlayBounds> {
                                    width = vaadin.overlay.DoubleOrString("22rem")
                                    height = vaadin.overlay.DoubleOrString("14rem")
                                },
                            )
                            sizedOverlay.opened = true
                        }
                    },
                sizedOverlay,
            ),
        ),
    )
    view.body.append(sized.root)

    return view
}

/**
 * A trigger for a login overlay; the overlays stay closed so they do not cover the page, and a
 * successful submit closes the overlay again because `LoginOverlay` has no close control of its own.
 */
private fun openOverlayButton(label: String, overlay: vaadin.login.LoginOverlayElement): vaadin.button.ButtonElement =
    createButton { textContent = label }.withTheme("tertiary").apply {
        overlay.listen(LoginFormEvents.login) { overlay.opened = false }
        clickEvent.addHandler { overlay.opened = true }
    }

private fun buildLoginPage(meta: PageMeta): PageView {
    val view = page(meta)

    val formSection =
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
    loginForm.noAutofocus = true
    loginForm.listen(LoginFormEvents.login) { log.add("login") }
    loginForm.listen(LoginFormEvents.forgotPassword) { log.add("forgot-password") }
    // `error` starts as `false`, so only the transitions to the error state are interesting.
    loginForm.listen(LoginFormEvents.errorChanged) { if (loginForm.error) log.add("error-changed · true") }

    val errorSwitch = createSwitch { label = tr("显示错误状态", "Show error state") }
    errorSwitch.listen(SwitchEvents.checkedChanged) { loginForm.error = errorSwitch.checked }
    val disabledSwitch = createSwitch { label = tr("禁用表单", "Disable form") }
    disabledSwitch.listen(SwitchEvents.checkedChanged) { loginForm.disabled = disabledSwitch.checked }

    formSection.body.append(example(null, row(loginForm, column(errorSwitch, disabledSwitch, log.root))))
    view.body.append(formSection.root)

    val eventSection =
        section(
            tr("禁用与重新启用", "Disabling during submit"),
            tr(
                "disabled 会锁住提交按钮；真实应用可以在 login 事件里禁用，等请求返回后再放开。",
                "disabled locks the submit button; a real application can disable it on login and re-enable " +
                    "it after the request returns.",
            ),
        )
    val slowForm = createLoginForm()
    slowForm.noAutofocus = true
    val slowStatus = Readout(tr("状态：可提交", "Status: ready"))
    slowForm.listen(LoginFormEvents.login) {
        slowForm.disabled = true
        slowStatus.set(tr("状态：提交中…", "Status: submitting…"))
        js("(callback, delay) => setTimeout(callback, delay)")(
            {
                slowForm.disabled = false
                slowStatus.set(tr("状态：可提交", "Status: ready"))
            },
            1000.0,
        )
    }
    eventSection.body.append(example(null, row(slowForm, column(slowStatus.root))))
    view.body.append(eventSection.root)

    val i18nSection =
        section(
            tr("国际化", "Localization"),
            tr(
                "i18n 覆盖表单里的全部文案；只提供需要的字段时会与默认值合并。",
                "i18n replaces the texts inside the form; the object is merged into the default bundle, so " +
                    "only the changed fields are required.",
            ),
        )
    val finnishForm = createLoginForm()
    finnishForm.noAutofocus = true
    finnishForm.i18nProperty =
        unsafeJso<LoginI18n> {
            form =
                unsafeJso<LoginI18nForm> {
                    title = "Kirjaudu sisään"
                    username = "Käyttäjänimi"
                    password = "Salasana"
                    submit = "Kirjaudu sisään"
                    forgotPassword = "Unohtuiko salasana?"
                }
            errorMessage =
                unsafeJso<LoginI18nErrorMessage> {
                    title = "Väärä käyttäjätunnus tai salasana"
                    message = "Tarkista että käyttäjätunnus ja salasana ovat oikein ja yritä uudestaan."
                    username = "Käyttäjätunnus vaaditaan"
                    password = "Salasana vaaditaan"
                }
        }
    i18nSection.body.append(example(null, finnishForm))
    view.body.append(i18nSection.root)

    val overlay =
        section(
            tr("登录浮层", "Login overlay"),
            tr(
                "LoginOverlay 把同一个表单放进全屏浮层，适合应用入口。",
                "LoginOverlay puts the same form into a full-screen overlay, which suits an app entry point.",
            ),
        )
    val loginOverlay = createLoginOverlay { opened = false }
    loginOverlay.dataset["test"] = "login-overlay"
    loginOverlay.description = tr("使用任意用户名和密码体验交互", "Use any username and password to try it")
    loginOverlay.noAutofocus = true
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

    val overlayParts =
        section(
            tr("浮层插槽", "Overlay slots"),
            tr(
                "custom-form-area 把额外字段放进表单区域，footer 在按钮下方追加内容。",
                "custom-form-area adds fields to the form area and footer appends content below the buttons.",
            ),
        )
    val codeOverlay = createLoginOverlay { opened = false }
    codeOverlay.noAutofocus = true
    codeOverlay.append(
        createIntegerField {
            label = tr("一次性验证码", "One-time code")
            slot = "custom-form-area"
        },
    )
    val footerOverlay = createLoginOverlay { opened = false }
    footerOverlay.noAutofocus = true
    footerOverlay.append(
        paragraph(tr("永远不要把密码告诉任何人", "Never tell your password to anyone")).apply {
            slot = "footer"
            style.setProperty("text-align", "center")
        },
    )
    overlayParts.body.append(
        example(
            null,
            row(
                openOverlayButton(tr("打开验证码浮层", "Open code overlay"), codeOverlay),
                openOverlayButton(tr("打开底部插槽浮层", "Open footer overlay"), footerOverlay),
                codeOverlay,
                footerOverlay,
            ),
        ),
    )
    view.body.append(overlayParts.root)

    val overlayHeader =
        section(
            tr("浮层标题与本地化", "Overlay header and localization"),
            tr(
                "title / description 覆盖头部文案；i18n.header 与 additionalInformation 一并生效。",
                "title / description override the header; i18n.header and additionalInformation join in.",
            ),
        )
    val brandedOverlay = createLoginOverlay { opened = false }
    brandedOverlay.noAutofocus = true
    brandedOverlay.title = "TaskMob"
    brandedOverlay.description = tr("用 ♥ 构建", "Built with ♥ by Vaadin")

    val localizedOverlay = createLoginOverlay { opened = false }
    localizedOverlay.noAutofocus = true
    localizedOverlay.i18nProperty =
        unsafeJso<LoginI18n> {
            header =
                unsafeJso<LoginI18nHeader> {
                    title = "Sovelluksen nimi"
                    description = "Sovelluksen kuvaus"
                }
            form =
                unsafeJso<LoginI18nForm> {
                    title = "Kirjaudu sisään"
                    submit = "Kirjaudu sisään"
                }
            additionalInformation = "Jos tarvitset lisätietoja käyttäjälle."
        }
    overlayHeader.body.append(
        example(
            null,
            row(
                openOverlayButton(tr("打开品牌浮层", "Open branded overlay"), brandedOverlay),
                openOverlayButton(tr("打开本地化浮层", "Open localized overlay"), localizedOverlay),
                brandedOverlay,
                localizedOverlay,
            ),
        ),
    )
    view.body.append(overlayHeader.root)

    val states =
        section(
            tr("错误状态与附加信息", "Error state and extra information"),
            tr(
                "error 直接打开失败横幅；additionalInformation 在表单下方追加说明。",
                "error shows the failure banner; additionalInformation adds a note below the form.",
            ),
        )
    val errorOverlay = createLoginOverlay { opened = false }
    errorOverlay.noAutofocus = true
    errorOverlay.error = true
    val infoOverlay = createLoginOverlay { opened = false }
    infoOverlay.noAutofocus = true
    infoOverlay.i18nProperty =
        unsafeJso<LoginI18n> {
            additionalInformation =
                tr(
                    "登录遇到问题时请联系 admin@company.com",
                    "Contact admin@company.com if you're experiencing issues logging into your account",
                )
        }
    states.body.append(
        example(
            null,
            row(
                openOverlayButton(tr("打开错误状态浮层", "Open error overlay"), errorOverlay),
                openOverlayButton(tr("打开附加信息浮层", "Open extra information overlay"), infoOverlay),
                errorOverlay,
                infoOverlay,
            ),
        ),
    )
    view.body.append(states.root)

    val rich =
        section(
            tr("富内容背景", "Rich background"),
            tr(
                "登录表单可以放在任意背景上；深色背景配合 theme=\"dark\" 保持对比度。",
                "The login form sits on any background; a dark surface pairs with theme=\"dark\" to keep contrast.",
            ),
        )
    val richForm = createLoginForm()
    richForm.noAutofocus = true
    richForm.withTheme("dark")
    val richHost = div("login-rich-content", richForm)
    rich.body.append(example(null, richHost))
    view.body.append(rich.root)

    return view
}
