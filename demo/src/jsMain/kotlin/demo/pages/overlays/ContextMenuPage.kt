package demo.pages.overlays

import io.github.shijianjs.ktjs.demo.page.col as kitCol
import io.github.shijianjs.ktjs.demo.page.exampleBox
import io.github.shijianjs.ktjs.demo.page.row as kitRow
import io.github.shijianjs.ktjs.demo.page.sectionBlock


import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import web.components.CustomElementReference
import web.dom.TagName
import web.html.HTMLElement
import web.html.HtmlTagName
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import io.github.shijianjs.ktjs.demo.page.paragraph
import io.github.shijianjs.ktjs.demo.page.heading
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.pages.fixtures.people
import demo.ui.PageFrame
import demo.ui.PageMeta
import demo.ui.Readout
import demo.ui.RecordedLog
import demo.ui.jsArray
import demo.ui.listen
import io.github.shijianjs.ktjs.dom.css.cssSheet
import web.cssom.CSSStyleSheet
import web.cssom.Color
import js.objects.unsafeJso
import kotlin.js.JsAny
import kotlin.js.js
import kotlin.js.unsafeCast
import io.github.shijianjs.ktjs.vaadin.avatar.createAvatar
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.contextmenu.ContextMenuElement
import io.github.shijianjs.ktjs.vaadin.contextmenu.ContextMenuEvents
import io.github.shijianjs.ktjs.vaadin.contextmenu.ContextMenuItemData
import io.github.shijianjs.ktjs.vaadin.contextmenu.HTMLElementOrString
import io.github.shijianjs.ktjs.vaadin.contextmenu.createContextMenu
import io.github.shijianjs.ktjs.vaadin.contextmenu.createContextMenuItem
import io.github.shijianjs.ktjs.vaadin.grid.createGrid
import io.github.shijianjs.ktjs.vaadin.grid.createGridColumn
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.vaadin.menubar.MenuBarItem
import io.github.shijianjs.ktjs.vaadin.menubar.createMenuBar
import io.github.shijianjs.ktjs.vaadin.tooltip.createTooltip
import io.github.shijianjs.ktjs.dom.css.dsl.className
import io.github.shijianjs.ktjs.dom.css.dsl.compound
import io.github.shijianjs.ktjs.dom.css.dsl.invoke
import io.github.shijianjs.ktjs.dom.css.dsl.tag
import io.github.shijianjs.ktjs.vaadin.VaadinTagName
import web.cssom.ClassName

val contextMenuEntry: CatalogEntry =
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
                sourcePath = "pages/overlays/ContextMenuPage.kt",
            ),
        kitPage = { newContextMenuPage() },
    )

private fun copyMenuItem(text: String, value: String): CopyMenuItem =
    unsafeJso<CopyMenuItem> {
        this.text = text
        this.value = value
    }

// endregion

// region Context menu

/**
 * `className` from the menu items API lands on the light-DOM item element, so the rule is a plain
 * attribute class selector inside the page's shadow tree.
 */
/** The class this page puts on the "delete" item through the menu data. */
private val CUSTOM: ClassName = ClassName("custom")

private val MENU_SHEET: CSSStyleSheet = cssSheet {
    compound(tag(VaadinTagName.contextMenuItem), className(CUSTOM)).invoke { color = Color("var(--lumo-error-text-color)") }
}

/** One menu entry: a label, optionally disabled, with a submenu, a class or a tooltip. */
private fun entry(
    text: String,
    disabled: Boolean = false,
    children: Array<ContextMenuItemData>? = null,
    className: String? = null,
    tooltip: String? = null,
    tooltipPosition: String? = null,
): ContextMenuItemData =
    unsafeJso {
        this.text = text
        this.disabled = disabled
        this.children = children
        this.className = className
        this.tooltip = tooltip
        this.tooltipPosition = tooltipPosition
    }

/** A divider entry. */
private fun divider(): ContextMenuItemData =
    unsafeJso { component = HTMLElementOrString("hr") }

/** An entry whose content is a node the caller assembled. */
private fun componentEntry(
    node: HTMLElement,
    children: Array<ContextMenuItemData>? = null,
): ContextMenuItemData =
    unsafeJso {
        component = HTMLElementOrString(node)
        this.children = children
    }

/** Every string this page shows, in one language. */
private class ContextMenuTexts(
    val rightClickHeading: String,
    val rightClickHint: String,
    val surface: String,
    val view: String,
    val copy: String,
    val paste: String,
    val more: String,
    val exportCsv: String,
    val exportJson: String,
    val selectedLine: String,
    val openedLine: (Boolean) -> String,
    val gridRowsHeading: String,
    val gridRowsHint: String,
    val edit: String,
    val delete: String,
    val checkableHeading: String,
    val checkableHint: String,
    val assigneePrefix: String,
    val classNamesHeading: String,
    val classNamesHint: String,
    val share: String,
    val duplicate: String,
    val actions: String,
    val customDataHeading: String,
    val customDataHint: String,
    val nothingCopied: String,
    val copiedPrefix: String,
    val copyAsPlain: String,
    val copyAsHtml: String,
    val copyAsMarkdown: String,
    val copyTextHint: String,
    val dividersHeading: String,
    val dividersHint: String,
    val preview: String,
    val export: String,
    val copyLink: String,
    val email: String,
    val leftClickHeading: String,
    val leftClickHint: String,
    val customItemsHeading: String,
    val customItemsHint: String,
    val open: String,
    val assign: String,
    val tooltipsHeading: String,
    val tooltipsHint: String,
    val tooltipEdit: String,
    val tooltipCopyLink: String,
    val tooltipEmail: String,
    val bestPracticeHeading: String,
    val bestPracticeHint: String,
    val fileHeader: String,
    val sizeHeader: String,
)

private val english: ContextMenuTexts =
    ContextMenuTexts(
        rightClickHeading = "Open on right-click",
        rightClickHint = "openOn defaults to contextmenu; listenOn points at the element that receives " +
            "the event.",
        surface = "Right-click in this area",
        view = "View",
        copy = "Copy",
        paste = "Paste",
        more = "More",
        exportCsv = "Export as CSV",
        exportJson = "Export as JSON",
        selectedLine = "item-selected",
        openedLine = { "opened-changed · $it" },
        gridRowsHeading = "Menu on grid rows",
        gridRowsHint = "Put a grid inside a context-menu; right-clicking any row opens the menu for that " +
            "area.",
        edit = "Edit",
        delete = "Delete",
        checkableHeading = "Checkable items",
        checkableHint = "checked marks the current item; the selection is read back after item-selected " +
            "updates it.",
        assigneePrefix = "Assignee: ",
        classNamesHeading = "Custom class name",
        classNamesHint = "className lands on the item element, which page styles can use for destructive " +
            "actions.",
        share = "Share",
        duplicate = "Duplicate",
        actions = "Actions",
        customDataHeading = "Custom item data",
        customDataHint = "An item can carry any extra fields; detail.value of item-selected is the " +
            "clicked item object.",
        nothingCopied = "Nothing copied yet",
        copiedPrefix = "Copied: ",
        copyAsPlain = "Copy as plain text",
        copyAsHtml = "Copy as HTML",
        copyAsMarkdown = "Copy as Markdown",
        copyTextHint = "Right-click this text to copy it in a format.",
        dividersHeading = "Dividers and hierarchy",
        dividersHint = "An item whose component is \"hr\" renders a divider; children open a submenu.",
        preview = "Preview",
        export = "Export",
        copyLink = "Copy link",
        email = "Email",
        leftClickHeading = "Open on click",
        leftClickHint = "With openOn set to click the menu opens on a left click, which suits an actions " +
            "button.",
        customItemsHeading = "Custom item rendering",
        customItemsHint = "component takes an element, so icons and avatars can be used directly.",
        open = "Open",
        assign = "Assign",
        tooltipsHeading = "Item tooltips",
        tooltipsHint = "The tooltip / tooltipPosition fields of the items work together with the tooltip " +
            "slot of the context menu.",
        tooltipEdit = "Edit selected person",
        tooltipCopyLink = "Copy a shareable link to the clipboard",
        tooltipEmail = "Send the contact details by email",
        bestPracticeHeading = "Actions inside a grid",
        bestPracticeHint = "An inline actions button uses a Menu Bar; the same items array drives both " +
            "the context menu and the inline button.",
        fileHeader = "File",
        sizeHeader = "Size",
    )

private val chinese: ContextMenuTexts =
    ContextMenuTexts(
        rightClickHeading = "右键打开",
        rightClickHint = "openOn 默认是 contextmenu；listenOn 指向接收事件的元素。",
        surface = "在这个区域点击右键",
        view = "查看",
        copy = "复制",
        paste = "粘贴",
        more = "更多",
        exportCsv = "导出为 CSV",
        exportJson = "导出为 JSON",
        selectedLine = "item-selected",
        openedLine = { "opened-changed · $it" },
        gridRowsHeading = "表格行菜单",
        gridRowsHint = "把 grid 放进 context-menu，右键任意一行都能对这个区域设置菜单。",
        edit = "编辑",
        delete = "删除",
        checkableHeading = "勾选条目",
        checkableHint = "items 里的 checked 表示当前选中项；item-selected 里更新后在界面上回读。",
        assigneePrefix = "负责人：",
        classNamesHeading = "自定义类名",
        classNamesHint = "className 加到条目元素上，配合页面样式表达危险操作。",
        share = "分享",
        duplicate = "复制副本",
        actions = "操作",
        customDataHeading = "自定义条目数据",
        customDataHint = "条目可以带任意字段；item-selected 的 detail.value 就是被点击的条目对象。",
        nothingCopied = "尚未复制",
        copiedPrefix = "已复制：",
        copyAsPlain = "复制为纯文本",
        copyAsHtml = "复制为 HTML",
        copyAsMarkdown = "复制为 Markdown",
        copyTextHint = "右键这段文字复制它的格式。",
        dividersHeading = "分隔线与层级",
        dividersHint = "component 设成 \"hr\" 的条目渲染成分隔线；children 生成子菜单。",
        preview = "预览",
        export = "导出",
        copyLink = "复制链接",
        email = "邮件",
        leftClickHeading = "左键打开",
        leftClickHint = "openOn 设为 click 时用左键展开，适合做「操作」按钮。",
        customItemsHeading = "自定义条目呈现",
        customItemsHint = "component 接受元素，可以直接放图标和头像。",
        open = "打开",
        assign = "指派",
        tooltipsHeading = "条目提示",
        tooltipsHint = "条目数据里的 tooltip / tooltipPosition 配合 context-menu 里的 tooltip 插槽生效。",
        tooltipEdit = "编辑选中的人",
        tooltipCopyLink = "把可分享的链接复制到剪贴板",
        tooltipEmail = "用邮件发送联系人信息",
        bestPracticeHeading = "表格中的操作",
        bestPracticeHint = "行内操作按钮用 Menu Bar 表达，同一个 items 数组既服务右键菜单也服务行内按钮。",
        fileHeader = "文件",
        sizeHeader = "大小",
    )

private val contextMenuTexts: Translations<ContextMenuTexts> = texts(
    Langs.EN to english,
    Langs.ZH_HANS to chinese,
)

internal class ContextMenuPage : DemoPage() {
    private val texts = Binder(contextMenuTexts)

    private val frame = PageFrame(contextMenuEntry)

    private val log = RecordedLog<String>(5)

    private val menu = createContextMenu()

    private val clickMenu = createContextMenu()

    private val classMenu = createContextMenu()

    private val copyMenu = createContextMenu()

    private val tooltipMenu = createContextMenu()

    /** Each menu plus how to fill its `items` in any language; component menus are filled once. */
    private val menus: MutableList<Pair<ContextMenuElement, (ContextMenuTexts) -> Array<ContextMenuItemData>>> =
        mutableListOf()

    private val assigneeReadout = Readout("")

    private val assigneeValue = HtmlTagName.span { textContent = "Abigail Lewis" }

    private val copyReadout = Readout("")

    private var copied: String? = null

    private val fileColumn = createGridColumn { path = "name" }

    private val sizeColumn = createGridColumn { path = "size"; width = "6rem" }

    private val fileItemsBuilder = { copy: ContextMenuTexts ->
        arrayOf(entry(copy.view), entry(copy.edit), entry(copy.delete))
    }

    init {
        frame.mount(this)
        addSheet(MENU_SHEET)

        fun entryList(build: (ContextMenuTexts) -> Array<ContextMenuItemData>): (ContextMenuTexts) -> Array<ContextMenuItemData> =
            build

        val primaryItems = entryList { copy ->
            arrayOf(
                entry(copy.view),
                entry(copy.copy),
                entry(copy.paste, disabled = true),
                entry(copy.more, children = arrayOf(entry(copy.exportCsv), entry(copy.exportJson))),
            )
        }

        val basic = sectionBlock()
        texts.bind(basic.heading) { it.rightClickHeading }
        texts.bind(basic.hint) { it.rightClickHint }
        // The right-click target: a dashed box that reads as "click me", inline because nothing else on
        // the page shares it.
        val surface = HtmlTagName.div().apply {
            style.setProperty("padding", "var(--lumo-space-l)")
            style.setProperty("border", "1px dashed var(--lumo-contrast-20pct)")
            style.setProperty("border-radius", "var(--lumo-border-radius-l)")
            style.setProperty("text-align", "center")
        }
        texts.bind(surface, { it.surface }) { surface.textContent = it }
        surface.dataset["test"] = "context-menu-surface"
        menu.openOn = "contextmenu"
        menu.listenOn = surface
        menus += menu to primaryItems
        menu.listen(ContextMenuEvents.itemSelected) {
            log.add("selected") { _ -> texts.current.selectedLine }
        }
        menu.listen(ContextMenuEvents.openedChanged) {
            log.add("open=${menu.opened}") { token -> texts.current.openedLine(token.endsWith("true")) }
        }
        basic.body.append(exampleBox(null, kitCol(surface, log.root, menu)))
        body.append(basic.root)

        val gridSection = sectionBlock()
        texts.bind(gridSection.heading) { it.gridRowsHeading }
        texts.bind(gridSection.hint) { it.gridRowsHint }
        val rowMenu = createContextMenu()
        menus += rowMenu to { copy -> arrayOf(entry(copy.view), entry(copy.edit), entry(copy.delete)) }
        rowMenu.append(personGrid(people(5)))
        gridSection.body.append(exampleBox(null, rowMenu))
        body.append(gridSection.root)

        val checkable = sectionBlock()
        texts.bind(checkable.heading) { it.checkableHeading }
        texts.bind(checkable.hint) { it.checkableHint }
        val assignees =
            mutableListOf(
                unsafeJso<ContextMenuItemData> { text = "Abigail Lewis"; checked = true },
                unsafeJso<ContextMenuItemData> { text = "Allison Torres"; checked = false },
                unsafeJso<ContextMenuItemData> { text = "Anna Myers"; checked = false },
                unsafeJso<ContextMenuItemData> { text = "Lauren Wright"; checked = false },
            )
        val assigneeMenu = createContextMenu()
        assigneeMenu.items = assignees.toTypedArray()
        val assigneeLabel = HtmlTagName.span()
        texts.bind(assigneeLabel, { it.assigneePrefix }) { assigneeLabel.textContent = it }
        assigneeLabel.append(assigneeValue)
        assigneeMenu.append(assigneeLabel)
        assigneeMenu.listen(ContextMenuEvents.itemSelected) {
            val chosen = js("(event) => event.detail.value.text")(it.unsafeCast<Any>()) as String
            assignees.forEach { item -> item.checked = item.text == chosen }
            assigneeValue.textContent = chosen
            showAssignee()
        }
        checkable.body.append(exampleBox(null, kitCol(assigneeMenu, assigneeReadout.root)))
        body.append(checkable.root)

        val classNames = sectionBlock()
        texts.bind(classNames.heading) { it.classNamesHeading }
        texts.bind(classNames.hint) { it.classNamesHint }
        classMenu.openOn = "click"
        menus += classMenu to { copy ->
            arrayOf(
                entry(copy.share),
                entry(copy.duplicate),
                entry(copy.delete, className = "custom"),
            )
        }
        val actionsButton = createButton().addTheme("tertiary")
        texts.bind(actionsButton) { it.actions }
        classMenu.append(actionsButton)
        classNames.body.append(exampleBox(null, classMenu))
        body.append(classNames.root)

        val customData = sectionBlock()
        texts.bind(customData.heading) { it.customDataHeading }
        texts.bind(customData.hint) { it.customDataHint }
        menus += copyMenu to { copy ->
            arrayOf(
                copyMenuItem(copy.copyAsPlain, "Context Menu"),
                copyMenuItem(copy.copyAsHtml, "<h1>Context Menu</h1>"),
                copyMenuItem(copy.copyAsMarkdown, "# Context Menu"),
            ).unsafeCast<Array<ContextMenuItemData>>()
        }
        val copyHint = paragraph("")
        texts.bind(copyHint, { it.copyTextHint }) { copyHint.textContent = it }
        copyMenu.append(kitCol(heading(2, "Context Menu"), copyHint))
        copyMenu.listen(ContextMenuEvents.itemSelected) {
            copied = js("(event) => event.detail.value.value")(it.unsafeCast<JsAny>()) as String?
            showCopied()
        }
        customData.body.append(exampleBox(null, kitCol(copyMenu, copyReadout.root)))
        body.append(customData.root)

        val dividers = sectionBlock()
        texts.bind(dividers.heading) { it.dividersHeading }
        texts.bind(dividers.hint) { it.dividersHint }
        val dividerMenu = createContextMenu()
        menus += dividerMenu to { copy ->
            arrayOf(
                entry(copy.preview),
                entry(copy.edit),
                divider(),
                entry(
                    copy.export,
                    children =
                        arrayOf(
                            entry("Portable Document Format (.pdf)"),
                            entry("Rich Text Format (.rtf)"),
                            entry("Plain text (.txt)"),
                        ),
                ),
                entry(copy.share, children = arrayOf(entry(copy.copyLink), entry(copy.email))),
                divider(),
                entry(copy.delete, disabled = true),
            )
        }
        dividerMenu.append(personGrid(people(4)))
        dividers.body.append(exampleBox(null, dividerMenu))
        body.append(dividers.root)

        val leftClick = sectionBlock()
        texts.bind(leftClick.heading) { it.leftClickHeading }
        texts.bind(leftClick.hint) { it.leftClickHint }
        val clickActions = createButton().addTheme("tertiary")
        texts.bind(clickActions) { it.actions }
        clickMenu.openOn = "click"
        clickMenu.listenOn = clickActions
        menus += clickMenu to primaryItems
        leftClick.body.append(exampleBox(null, kitRow(clickActions, clickMenu)))
        body.append(leftClick.root)

        val customItems = sectionBlock()
        texts.bind(customItems.heading) { it.customItemsHeading }
        texts.bind(customItems.hint) { it.customItemsHint }
        fun menuItem(iconName: String, copy: (ContextMenuTexts) -> String): ContextMenuItemData {
            val row = createContextMenuItem()
            row.append(
                createIcon { icon = iconName }.also { iconElement ->
                    iconElement.style.setProperty("color", "var(--vaadin-text-color-secondary)")
                    iconElement.style.setProperty("margin-inline-end", "var(--vaadin-gap-s)")
                },
            )
            val labelNode = HtmlTagName.span()
            texts.bind(labelNode, copy) { labelNode.textContent = it }
            row.append(labelNode)
            return componentEntry(row)
        }
        val assignChildren =
            people(3)
                .map { person ->
                    val row = createContextMenuItem()
                    row.append(createAvatar { img = person.pictureUrl; name = person.displayName })
                    row.append(HtmlTagName.span { textContent = person.displayName })
                    componentEntry(row)
                }
                .toTypedArray()
        val presentMenu = createContextMenu()
        presentMenu.items =
            arrayOf(
                menuItem("vaadin:file-search") { it.open },
                componentEntry(
                    createContextMenuItem().apply {
                        append(createIcon { icon = "vaadin:user-check" })
                        val assignLabel = HtmlTagName.span()
                        texts.bind(assignLabel, { it.assign }) { assignLabel.textContent = it }
                        append(assignLabel)
                    },
                    children = assignChildren,
                ),
                divider(),
                menuItem("vaadin:trash") { it.delete },
            )
        presentMenu.append(personGrid(people(4)))
        customItems.body.append(exampleBox(null, presentMenu))
        body.append(customItems.root)

        val tooltips = sectionBlock()
        texts.bind(tooltips.heading) { it.tooltipsHeading }
        texts.bind(tooltips.hint) { it.tooltipsHint }
        menus += tooltipMenu to { copy ->
            arrayOf(
                entry(copy.edit, tooltip = copy.tooltipEdit),
                entry(
                    copy.share,
                    children =
                        arrayOf(
                            entry(copy.copyLink, tooltip = copy.tooltipCopyLink),
                            entry(copy.email, tooltip = copy.tooltipEmail, tooltipPosition = "end"),
                        ),
                ),
            )
        }
        tooltipMenu.append(createTooltip().also { it.slot = "tooltip" })
        tooltipMenu.append(personGrid(people(4)))
        tooltips.body.append(exampleBox(null, tooltipMenu))
        body.append(tooltips.root)

        val bestPractice = sectionBlock()
        texts.bind(bestPractice.heading) { it.bestPracticeHeading }
        texts.bind(bestPractice.hint) { it.bestPracticeHint }
        val filesMenu = createContextMenu()
        menus += filesMenu to fileItemsBuilder
        texts.bind(fileColumn, { it.fileHeader }) { fileColumn.header = it }
        texts.bind(sizeColumn, { it.sizeHeader }) { sizeColumn.header = it }
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
            fileColumn,
            sizeColumn,
            createGridColumn {
                width = "4rem"
                flexGrow = 0.0
                renderer = { container, _, _ ->
                    val menuBar = createMenuBar { addTheme("tertiary") }
                    menuBar.items = fileItemsBuilder(texts.current).unsafeCast<Array<MenuBarItem>>()
                    container.append(menuBar)
                }
            },
        )
        filesMenu.append(filesGrid)
        bestPractice.body.append(exampleBox(null, filesMenu))
        body.append(bestPractice.root)

        applyTranslation(Langs.EN)
    }

    private fun showAssignee() {
        assigneeReadout.set(texts.current.assigneePrefix, assigneeValue.textContent ?: "")
    }

    private fun showCopied() {
        val value = copied
        copyReadout.set(if (value == null) texts.current.nothingCopied else texts.current.copiedPrefix + value)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = contextMenuEntry.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        menus.forEach { (menuNode, fill) -> menuNode.items = fill(texts.current) }
        showAssignee()
        showCopied()
        log.replay("selected")
    }

    companion object : CustomElementReference<ContextMenuPage>(ContextMenuPage::class.js) {
        val NAME: TagName<ContextMenuPage> = TagName("vaadin-demo-context-menu-page")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<vaadin-demo-context-menu-page>`. */
internal fun newContextMenuPage(): ContextMenuPage {
    ContextMenuPage.define()
    return ContextMenuPage.NAME()
}

// endregion
