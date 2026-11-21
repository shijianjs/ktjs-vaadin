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
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import io.github.shijianjs.ktjs.demo.page.Blocks.paragraph
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import demo.CatalogEntry
import demo.Catalog
import demo.Groups
import demo.bi
import demo.pages.fixtures.people
import demo.ui.PageFrame
import demo.ui.PageMeta
import demo.ui.RecordedLog
import demo.ui.listen
import io.github.shijianjs.ktjs.vaadin.button.ButtonElement
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.dialog.DialogElement
import io.github.shijianjs.ktjs.vaadin.dialog.DialogEvents
import io.github.shijianjs.ktjs.vaadin.dialog.createDialog
import io.github.shijianjs.ktjs.vaadin.emailfield.createEmailField
import io.github.shijianjs.ktjs.vaadin.formlayout.createFormLayout
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.vaadin.textarea.createTextArea
import io.github.shijianjs.ktjs.vaadin.textfield.createTextField
import web.dom.clickEvent
import web.events.addHandler

internal class DialogPage : DemoPage() {
    private val texts = Binder(dialogTexts)

    private val frame = PageFrame(ENTRY)

    private val log = RecordedLog<String>(5)

    private val dialog = createDialog()

    private val formDialog = createDialog()

    private val configured = createDialog()

    private val modelessDialog = createDialog()

    private val noteDialog = createDialog()

    private val userDialog = createDialog()

    private val deleteDialog = createDialog()

    private val filterDialog = createDialog()

    private val employeeDialog = createDialog()

    init {
        frame.mount(this)

        fun button(copy: (DialogTexts) -> String): ButtonElement =
            createButton().also { node -> texts.bind(node, copy) { node.textContent = it } }

        fun paragraphOf(copy: (DialogTexts) -> String) =
            paragraph("").also { node -> texts.bind(node, copy) { node.textContent = it } }

        fun title(dialogNode: DialogElement, copy: (DialogTexts) -> String) {
            texts.bind(dialogNode, copy) { dialogNode.headerTitle = it }
        }

        val basic = SectionBlock.NAME { show() }
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        dialog.dataset["test"] = "dialog-basic"
        val content =
            kitCol(
                paragraphOf { it.basicContent },
                paragraphOf { it.basicEscape },
            )
        val closeButton = button { it.close }.addTheme("primary")
        closeButton.clickEvent.addHandler { dialog.opened = false }
        dialog.renderer = { root, _ -> root.append(content) }
        dialog.footerRenderer = { root, _ -> root.append(closeButton) }
        title(dialog) { it.exampleDialog }
        // `opened` is initialised to `false` when the dialog upgrades, which also fires
        // `opened-changed`; the log only starts once the example is actually used.
        var openedByUser = false
        dialog.listen(DialogEvents.openedChanged) {
            if (openedByUser) log.add("open=${dialog.opened}") { token ->
                texts.current.openedLine(token.endsWith("true"))
            }
        }
        dialog.listen(DialogEvents.closed) { log.add("close") { _ -> texts.current.closedLine } }

        val openButton = button { it.openDialog }
        openButton.clickEvent.addHandler {
            openedByUser = true
            dialog.opened = true
        }
        // The dialog element itself has to be in the document; it renders its content into an overlay.
        basic.body.append(exampleBox(null, kitCol(openButton, dialog, log.root)))
        body.append(basic.root)

        val formSection = SectionBlock.NAME { show() }
        texts.bind(formSection.heading) { it.formHeading }
        texts.bind(formSection.hint) { it.formHint }
        val employeeForm =
            createFormLayout {
                autoResponsive = true
                columnWidth = "18rem"
                expandFields = true
            }
        val first = createTextField()
        texts.bind(first, { it.firstName }) { first.label = it }
        val last = createTextField()
        texts.bind(last, { it.lastName }) { last.label = it }
        employeeForm.append(first, last)
        val cancelButton = button { it.cancel }
        cancelButton.clickEvent.addHandler { formDialog.opened = false }
        val addButton = button { it.add }.addTheme("primary")
        addButton.clickEvent.addHandler { formDialog.opened = false }
        formDialog.renderer = { root, _ -> root.append(employeeForm) }
        formDialog.footerRenderer = { root, _ -> root.append(cancelButton, addButton) }
        title(formDialog) { it.newEmployee }
        val openForm = button { it.openForm }
        openForm.clickEvent.addHandler { formDialog.opened = true }
        formSection.body.append(exampleBox(null, kitRow(openForm, formDialog)))
        body.append(formSection.root)

        val options = SectionBlock.NAME { show() }
        texts.bind(options.heading) { it.optionsHeading }
        texts.bind(options.hint) { it.optionsHint }
        title(configured) { it.notClosableTitle }
        configured.width = "28rem"
        configured.noCloseOnEsc = true
        configured.noCloseOnOutsideClick = true
        val configuredContent = paragraphOf { it.onlyButton }
        val configuredClose = button { it.close }
        configuredClose.clickEvent.addHandler { configured.opened = false }
        configured.renderer = { root, _ -> root.append(configuredContent) }
        configured.footerRenderer = { root, _ -> root.append(configuredClose) }

        modelessDialog.modeless = true
        title(modelessDialog) { it.modelessTitle }
        val modelessContent = paragraphOf { it.modelessNote }
        val modelessClose = button { it.close }
        modelessClose.clickEvent.addHandler { modelessDialog.opened = false }
        modelessDialog.renderer = { root, _ -> root.append(modelessContent) }
        modelessDialog.footerRenderer = { root, _ -> root.append(modelessClose) }
        val openCustom = button { it.openCustomSize }.addTheme("tertiary")
        openCustom.clickEvent.addHandler { configured.opened = true }
        val openModeless = button { it.openModeless }.addTheme("tertiary")
        openModeless.clickEvent.addHandler { modelessDialog.opened = true }

        options.body.append(
            exampleBox(
                null,
                kitCol(
                    kitRow(openCustom, openModeless),
                    // Dialogs only render their overlay while they are part of the document.
                    configured,
                    modelessDialog,
                ),
            ),
        )
        body.append(options.root)

        val draggableSection = SectionBlock.NAME { show() }
        texts.bind(draggableSection.heading) { it.draggableHeading }
        texts.bind(draggableSection.hint) { it.draggableHint }
        title(noteDialog) { it.addNoteTitle }
        noteDialog.modeless = true
        noteDialog.draggable = true
        val noteForm =
            createFormLayout {
                autoResponsive = true
                columnWidth = "18rem"
                expandFields = true
            }
        val noteTitle = createTextField()
        texts.bind(noteTitle, { it.noteTitle }) { noteTitle.label = it }
        val noteArea = createTextArea()
        texts.bind(noteArea, { it.noteDescription }) { noteArea.label = it }
        noteForm.append(noteTitle, noteArea)
        val noteCancel = button { it.cancel }
        noteCancel.clickEvent.addHandler { noteDialog.opened = false }
        val noteAdd = button { it.addNote }.addTheme("primary")
        noteAdd.clickEvent.addHandler { noteDialog.opened = false }
        noteDialog.renderer = { root, _ -> root.append(noteForm) }
        noteDialog.footerRenderer = { root, _ -> root.append(noteCancel, noteAdd) }
        val openNote = button { it.openDraggable }
        openNote.clickEvent.addHandler { noteDialog.opened = true }
        draggableSection.body.append(exampleBox(null, kitRow(openNote, noteDialog)))
        body.append(draggableSection.root)

        val headerSection = SectionBlock.NAME { show() }
        texts.bind(headerSection.heading) { it.headerHeading }
        texts.bind(headerSection.hint) { it.headerHint }
        val user = people(1).first()
        title(userDialog) { it.userDetails }
        val headerClose = createButton { textContent = "" }.addTheme("tertiary icon").apply {
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
        val nameField = createTextField { value = user.displayName; readonly = true }
        texts.bind(nameField, { it.name }) { nameField.label = it }
        val emailField = createEmailField { value = user.email; readonly = true }
        texts.bind(emailField, { it.email }) { emailField.label = it }
        val addressField = createTextField { readonly = true }
        texts.bind(addressField, { it.address }) { addressField.label = it }
        texts.bind(addressField, { it.addressValue }) { addressField.value = it }
        detailForm.append(nameField, emailField, addressField)
        userDialog.renderer = { root, _ -> root.append(detailForm) }
        val openUser = button { it.openUserDetails }
        openUser.clickEvent.addHandler { userDialog.opened = true }
        headerSection.body.append(exampleBox(null, kitRow(openUser, userDialog)))
        body.append(headerSection.root)

        val footerSection = SectionBlock.NAME { show() }
        texts.bind(footerSection.heading) { it.footerHeading }
        texts.bind(footerSection.hint) { it.footerHint }
        texts.bind(deleteDialog, { copy -> copy.deleteTitle(user.displayName) }) {
            deleteDialog.headerTitle = it
        }
        val deleteText = paragraphOf { it.deleteQuestion }
        val deleteButton = button { it.delete }.addTheme("primary", "error")
        deleteButton.style.setProperty("margin-inline-end", "auto")
        deleteButton.clickEvent.addHandler { deleteDialog.opened = false }
        val keepButton = button { it.cancel }.addTheme("tertiary")
        keepButton.clickEvent.addHandler { deleteDialog.opened = false }
        deleteDialog.renderer = { root, _ -> root.append(deleteText) }
        deleteDialog.footerRenderer = { root, _ -> root.append(deleteButton, keepButton) }
        val openDelete = button { it.deleteUser }.addTheme("error")
        openDelete.clickEvent.addHandler { deleteDialog.opened = true }
        footerSection.body.append(exampleBox(null, kitRow(openDelete, deleteDialog)))
        body.append(footerSection.root)

        val noPaddingSection = SectionBlock.NAME { show() }
        texts.bind(noPaddingSection.heading) { it.noPaddingHeading }
        texts.bind(noPaddingSection.hint) { it.noPaddingHint }
        title(filterDialog) { it.filterTitle }
        filterDialog.addTheme("no-padding")
        val filterGrid = personGrid(withSelection = true)
        filterGrid.bindHeaders(texts, { it.firstName }, { it.lastName }, { it.email }, { it.professionHeader })
        filterDialog.renderer = { root, _ -> root.append(filterGrid.grid) }
        val filterButton = button { it.filter }.addTheme("primary")
        filterButton.clickEvent.addHandler { filterDialog.opened = false }
        filterDialog.footerRenderer = { root, _ -> root.append(filterButton) }
        val openFilter = button { it.openFilter }
        openFilter.clickEvent.addHandler { filterDialog.opened = true }
        noPaddingSection.body.append(exampleBox(null, kitRow(openFilter, filterDialog)))
        body.append(noPaddingSection.root)

        val resizableSection = SectionBlock.NAME { show() }
        texts.bind(resizableSection.heading) { it.resizableHeading }
        texts.bind(resizableSection.hint) { it.resizableHint }
        title(employeeDialog) { it.employeeList }
        employeeDialog.resizable = true
        employeeDialog.draggable = true
        employeeDialog.width = "40rem"
        val employeeGrid = personGrid(people(8))
        employeeGrid.bindHeaders(texts, { it.firstName }, { it.lastName }, { it.email }, { it.professionHeader })
        employeeDialog.renderer = { root, _ -> root.append(employeeGrid.grid) }
        val openEmployee = button { it.openResizable }
        openEmployee.clickEvent.addHandler { employeeDialog.opened = true }
        resizableSection.body.append(exampleBox(null, kitRow(openEmployee, employeeDialog)))
        body.append(resizableSection.root)

        applyTranslation(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = ENTRY.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        log.replay("close")
    }

    companion object : DialogPageDefaultReference() {
        val ENTRY: CatalogEntry =
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
                        docsUrl = "${Catalog.DOCS_BASE}/dialog",
                        sourcePath = "pages/overlays/DialogPage.kt",
                    ),
                kitPage = { DialogPage.NAME() },
            )

        private val english: DialogTexts =
            DialogTexts(
                basicHeading = "Basic usage",
                basicHint = "renderer injects the content into the overlay; footerRenderer holds the action buttons.",
                basicContent = "The dialog content comes from renderer: append nodes to root inside " +
                    "renderer(root, dialog).",
                basicEscape = "Pressing Esc or clicking the backdrop closes it, unless the matching switch is on.",
                close = "Close",
                exampleDialog = "Example dialog",
                openDialog = "Open dialog",
                openedLine = { "opened-changed · $it" },
                closedLine = "closed",
                formHeading = "Form content",
                formHint = "The form goes into the renderer and the cancel / confirm buttons into footerRenderer.",
                newEmployee = "New employee",
                firstName = "First name",
                lastName = "Last name",
                cancel = "Cancel",
                add = "Add",
                openForm = "Open form dialog",
                optionsHeading = "Size and closing behaviour",
                optionsHint = "width / height set the overlay size; noCloseOnEsc and noCloseOnOutsideClick disable " +
                    "the two default ways to close it.",
                notClosableTitle = "Not closable with Esc / backdrop",
                onlyButton = "Only the button below closes it.",
                openCustomSize = "Open (custom size)",
                modelessTitle = "Modeless dialog",
                modelessNote = "A modeless dialog does not block the rest of the page.",
                openModeless = "Open (modeless)",
                draggableHeading = "Draggable",
                draggableHint = "draggable lets the user move the overlay; a modeless dialog does not take the focus.",
                addNoteTitle = "Add note",
                noteTitle = "Title",
                noteDescription = "Description",
                addNote = "Add note",
                openDraggable = "Open draggable dialog",
                headerHeading = "Custom header",
                headerHint = "headerRenderer fills the area next to the title, which suits extra actions such as " +
                    "a close button.",
                userDetails = "User details",
                name = "Name",
                email = "Email",
                address = "Address",
                addressValue = "700 Wanrong Road, Shanghai, China",
                openUserDetails = "Open user details",
                footerHeading = "Footer layout",
                footerHint = "Buttons in footerRenderer follow insertion order; marginInlineEnd: auto pushes the " +
                    "destructive action to the leading edge.",
                deleteTitle = { "Delete user \"$it\"?" },
                deleteQuestion = "Are you sure you want to delete this user permanently?",
                delete = "Delete",
                deleteUser = "Delete user",
                noPaddingHeading = "No padding",
                noPaddingHint = "theme=\"no-padding\" removes the content padding, which suits components such " +
                    "as a grid.",
                filterTitle = "Filter reports by users:",
                filter = "Filter",
                openFilter = "Open filter dialog",
                resizableHeading = "Resizable",
                resizableHint = "resizable lets the user drag the edges; the minimum size comes from CSS on " +
                    "::part(overlay).",
                employeeList = "Employee list",
                openResizable = "Open resizable dialog",
                professionHeader = "Profession",
            )
        private val chinese: DialogTexts =
            DialogTexts(
                basicHeading = "基本用法",
                basicHint = "内容通过 renderer 注入浮层；footerRenderer 放操作按钮。",
                basicContent = "对话框内容由 renderer 提供：renderer(root, dialog) 里向 root 追加节点。",
                basicEscape = "按 Esc 或点击遮罩会关闭，除非打开对应开关。",
                close = "关闭",
                exampleDialog = "示例对话框",
                openDialog = "打开对话框",
                openedLine = { "opened-changed · $it" },
                closedLine = "closed",
                formHeading = "表单内容",
                formHint = "表单放进 renderer 里，取消与确认按钮放进 footerRenderer。",
                newEmployee = "新员工",
                firstName = "名",
                lastName = "姓",
                cancel = "取消",
                add = "添加",
                openForm = "打开表单对话框",
                optionsHeading = "尺寸与关闭行为",
                optionsHint = "width / height 控制浮层尺寸，noCloseOnEsc 与 noCloseOnOutsideClick 关闭默认的两种" +
                    "关闭方式。",
                notClosableTitle = "不可用 Esc / 遮罩关闭",
                onlyButton = "只能用下面的按钮关闭。",
                openCustomSize = "打开（自定义尺寸）",
                modelessTitle = "非模态对话框",
                modelessNote = "非模态对话框不会阻塞页面其他部分。",
                openModeless = "打开（非模态）",
                draggableHeading = "可拖动",
                draggableHint = "draggable 让用户拖动浮层；非模态时对话框不会抢走焦点。",
                addNoteTitle = "添加笔记",
                noteTitle = "标题",
                noteDescription = "描述",
                addNote = "添加笔记",
                openDraggable = "打开可拖动对话框",
                headerHeading = "自定义头部",
                headerHint = "headerRenderer 放在标题右侧，适合放关闭按钮等附加操作。",
                userDetails = "用户详情",
                name = "姓名",
                email = "邮箱",
                address = "地址",
                addressValue = "万荣路 700 号，上海，中国",
                openUserDetails = "打开用户详情",
                footerHeading = "底部按钮布局",
                footerHint = "footerRenderer 里的按钮按添加顺序排列，用 marginInlineEnd: auto 把危险操作推到左边。",
                deleteTitle = { "删除用户「$it」？" },
                deleteQuestion = "确定要永久删除这个用户吗？",
                delete = "删除",
                deleteUser = "删除用户",
                noPaddingHeading = "无内边距",
                noPaddingHint = "theme=\"no-padding\" 去掉内容区的内边距，适合放表格这类自带留白的组件。",
                filterTitle = "按用户筛选报表：",
                filter = "筛选",
                openFilter = "打开筛选对话框",
                resizableHeading = "可调整大小",
                resizableHint = "resizable 允许拖动边角改变尺寸；最小值通过 ::part(overlay) 上的 CSS 指定。",
                employeeList = "员工列表",
                openResizable = "打开可调整对话框",
                professionHeader = "职务",
            )
        private val dialogTexts: Translations<DialogTexts> = texts(
            Langs.EN to english,
            Langs.ZH_HANS to chinese,
        )
    }
}
