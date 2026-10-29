package demo.pages.overlays

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.pages.fixtures.people
import demo.tr
import demo.ui.EventLog
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.column
import demo.ui.example
import demo.ui.listen
import demo.ui.page
import demo.ui.paragraph
import demo.ui.row
import demo.ui.section
import demo.ui.withClass
import demo.ui.withTheme
import vaadin.button.createButton
import vaadin.dialog.DialogEvents
import vaadin.dialog.createDialog
import vaadin.emailfield.createEmailField
import vaadin.formlayout.createFormLayout
import vaadin.icon.createIcon
import vaadin.textarea.createTextArea
import vaadin.textfield.createTextField
import web.dom.clickEvent
import web.events.addHandler

val dialogEntry: CatalogEntry =
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
                sourcePath = "pages/overlays/DialogPage.kt",
            ),
        builder = ::buildDialogPage,
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
