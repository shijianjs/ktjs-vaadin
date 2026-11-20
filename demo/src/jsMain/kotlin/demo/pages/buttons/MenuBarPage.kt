package demo.pages.buttons

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
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import web.components.CustomElementReference
import web.dom.TagName
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import io.github.shijianjs.ktjs.demo.page.paragraph
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.ui.EventLog
import demo.ui.PageFrame
import demo.ui.PageMeta
import demo.ui.Readout
import demo.ui.jsObject
import demo.ui.listen
import js.objects.unsafeJso
import kotlin.js.unsafeCast
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.vaadin.icons.registerVaadinIcons
import io.github.shijianjs.ktjs.vaadin.menubar.HTMLElementOrString
import io.github.shijianjs.ktjs.vaadin.menubar.MenuBarElement
import io.github.shijianjs.ktjs.vaadin.menubar.MenuBarEvents
import io.github.shijianjs.ktjs.vaadin.menubar.MenuBarI18n
import io.github.shijianjs.ktjs.vaadin.menubar.MenuBarItem
import io.github.shijianjs.ktjs.vaadin.menubar.SubMenuItem
import io.github.shijianjs.ktjs.vaadin.menubar.createMenuBar
import io.github.shijianjs.ktjs.vaadin.splitlayout.createSplitLayout
import io.github.shijianjs.ktjs.vaadin.switch.SwitchEvents
import io.github.shijianjs.ktjs.vaadin.switch.createSwitch
import io.github.shijianjs.ktjs.vaadin.tooltip.createTooltip
import web.dom.document
import web.html.HTMLElement
import web.html.HtmlTagName

val menuBarEntry: CatalogEntry =
    CatalogEntry(
        id = "menu-bar",
        group = Groups.BUTTONS,
        meta =
            PageMeta(
                title = bi("Menu Bar", "Menu Bar"),
                summary =
                    bi(
                        "横向菜单栏，支持子菜单、禁用项、悬停展开与键盘导航。",
                        "Horizontal menu bar with submenus, disabled items, hover expansion and keyboard " +
                            "navigation.",
                    ),
                docsUrl = "$DOCS_BASE/menu-bar",
                sourcePath = "pages/buttons/MenuBarPage.kt",
            ),
        kitPage = { newMenuBarPage() },
    )

/** Parses a declarative menu tree into the typed `items` array. */
private fun menuItems(json: String): Array<MenuBarItem> = jsObject(json).unsafeCast<Array<MenuBarItem>>()

/** Item that was chosen in an `item-selected` event, read from the event detail. */
private fun selectedMenuItem(event: web.events.Event): MenuBarItem =
    js("(event) => event.detail.value")(event).unsafeCast<MenuBarItem>()

/** Builds one `vaadin-menu-bar-item` with an optional icon and text, used for `component` items. */
private fun menuBarItemElement(
    label: String? = null,
    icon: String? = null,
    text: String? = null,
    child: Boolean = false,
): HTMLElement {
    val item = document.createElement(web.dom.TagName<HTMLElement>("vaadin-menu-bar-item"))
    if (label != null) item.setAttribute("aria-label", label)
    if (icon != null) {
        val iconElement = createIcon { this.icon = "vaadin:$icon" }
        if (child) {
            iconElement.style.width = "1.25rem"
            iconElement.style.height = "1.25rem"
            iconElement.style.marginRight = "var(--vaadin-gap-s)"
        }
        item.append(iconElement)
    }
    if (text != null) item.append(document.createTextNode(text))
    return item
}

private fun componentItem(
    element: HTMLElement,
    tooltip: String? = null,
    children: Array<SubMenuItem>? = null,
): MenuBarItem =
    unsafeJso {
        component = HTMLElementOrString(element)
        if (tooltip != null) this.tooltip = tooltip
        if (children != null) this.children = children
    }

/** Every string this page shows, in one language. */
private class MenuBarTexts(
    val basicHeading: String,
    val basicHint: String,
    val nothingSelected: String,
    val clickedItem: String,
    val eventLog: String,
    val checkableHeading: String,
    val checkableHint: String,
    val options: String,
    val saveAutomatically: String,
    val notifyWatchers: String,
    val dividersHeading: String,
    val dividersHint: String,
    val dropDownHeading: String,
    val dropDownHint: String,
    val iconsHeading: String,
    val iconsHint: String,
    val iconsWithText: String,
    val iconOnly: String,
    val comboButtons: String,
    val customDataHeading: String,
    val customDataHint: String,
    val nothingCopied: String,
    val copied: String,
    val disabledHeading: String,
    val disabledHint: String,
    val classNamesHeading: String,
    val classNamesHint: String,
    val indicatorsHeading: String,
    val indicatorsHint: String,
    val overflowHeading: String,
    val overflowHint: String,
    val moveSplitter: String,
    val rightAlignedHeading: String,
    val rightAlignedHint: String,
    val stylesHeading: String,
    val stylesHint: String,
    val defaultVariant: String,
    val tertiaryVariant: String,
    val primaryVariant: String,
    val smallVariant: String,
    val itemText: String,
    val i18nHeading: String,
    val i18nHint: String,
    val moreActions: String,
    val tooltipsHeading: String,
    val tooltipsHint: String,
    val chooseFolder: String,
    val toArchive: String,
    val moveToArchive: String,
    val behaviourHeading: String,
    val behaviourHint: String,
    val openOnHover: String,
    val tabNavigation: String,
    val disabledSwitch: String,
    val view: String,
    val edit: String,
    val share: String,
    val onSocialMedia: String,
    val byEmail: String,
    val getLink: String,
    val move: String,
    val toFolder: String,
    val toFolderEllipsis: String,
    val toTrash: String,
    val duplicate: String,
    val setPermissions: String,
    val profile: String,
    val account: String,
    val preferences: String,
    val signOut: String,
    val save: String,
    val otherSaveOptions: String,
    val saveAsDraft: String,
    val saveAsCopy: String,
    val saveAndPublish: String,
    val copy: String,
    val copyAsPlainText: String,
    val copyAsHtml: String,
    val copyAsMarkdown: String,
    val delete: String,
)

private val english: MenuBarTexts =
    MenuBarTexts(
        basicHeading = "Basic usage",
        basicHint = "items takes a tree; choosing an entry from an opened submenu fires item-selected.",
        nothingSelected = "No menu item selected",
        clickedItem = "Clicked item: ",
        eventLog = "Event log",
        checkableHeading = "Checkable items",
        checkableHint = "The application owns the checked state of sub-menu items: toggle it in " +
            "item-selected and the component renders the mark.",
        options = "Options",
        saveAutomatically = "Save automatically",
        notifyWatchers = "Notify watchers",
        dividersHeading = "Dividers",
        dividersHint = "An item with component set to \"hr\" renders as a divider that groups sub-menu entries.",
        dropDownHeading = "Drop-down menu",
        dropDownHint = "With a single button the menu bar is a plain drop-down, the usual shape of an account " +
            "menu.",
        iconsHeading = "Icons",
        iconsHint = "The component of an item accepts any element: here a vaadin-menu-bar-item holding an icon " +
            "and text; theme=\"icon\" keeps only the icon in the buttons.",
        iconsWithText = "Icons with text",
        iconOnly = "Icon-only buttons",
        comboButtons = "Combo buttons",
        customDataHeading = "Custom item data",
        customDataHint = "Items can carry application fields (value here) that item-selected reads back; the " +
            "example copies the text to the clipboard.",
        nothingCopied = "Nothing copied yet",
        copied = "Copied: ",
        disabledHeading = "Disabled items",
        disabledHint = "disabled works on top-level buttons and on sub-menu entries; disabled items cannot be " +
            "chosen.",
        classNamesHeading = "Item class names",
        classNamesHint = "className is added to the generated button for finer-grained styling.",
        indicatorsHeading = "Drop-down indicators",
        indicatorsHint = "theme=\"dropdown-indicators\" adds a chevron to the buttons that have a submenu.",
        overflowHeading = "Overflow",
        overflowHint = "Buttons that no longer fit move into the overflow menu; drag the splitter to watch it " +
            "happen.",
        moveSplitter = "Move the splitter to see overflow",
        rightAlignedHeading = "Right-aligned",
        rightAlignedHint = "theme=\"end-aligned\" pushes the buttons to the trailing edge, as in a toolbar.",
        stylesHeading = "Style variants",
        stylesHint = "Top-level buttons use the tertiary look by default; compare primary and small below.",
        defaultVariant = "Default",
        tertiaryVariant = "Tertiary",
        primaryVariant = "Primary",
        smallVariant = "Small",
        itemText = "Item",
        i18nHeading = "Internationalization",
        i18nHint = "i18n overrides the built-in texts, such as moreOptions that screen readers announce for " +
            "the overflow button.",
        moreActions = "More actions",
        tooltipsHeading = "Item tooltips",
        tooltipsHint = "Set tooltip on the items and add a slotted vaadin-tooltip; it shows on hover and " +
            "keyboard focus.",
        chooseFolder = "Choose a destination folder",
        toArchive = "To archive",
        moveToArchive = "Move to archive",
        behaviourHeading = "Behaviour switches",
        behaviourHint = "openOnHover controls whether submenus open on hover; tabNavigation moves the Tab key " +
            "between top-level items.",
        openOnHover = "openOnHover (open on hover)",
        tabNavigation = "tabNavigation (Tab key navigation)",
        disabledSwitch = "disabled (whole menu bar)",
        view = "View",
        edit = "Edit",
        share = "Share",
        onSocialMedia = "On social media",
        byEmail = "By email",
        getLink = "Get link",
        move = "Move",
        toFolder = "To folder",
        toFolderEllipsis = "To folder…",
        toTrash = "To trash",
        duplicate = "Duplicate",
        setPermissions = "Set permissions",
        profile = "Profile",
        account = "Account",
        preferences = "Preferences",
        signOut = "Sign out",
        save = "Save",
        otherSaveOptions = "Other save options",
        saveAsDraft = "Save as draft",
        saveAsCopy = "Save as copy",
        saveAndPublish = "Save and publish",
        copy = "Copy",
        copyAsPlainText = "Copy as plain text",
        copyAsHtml = "Copy as HTML",
        copyAsMarkdown = "Copy as Markdown",
        delete = "Delete",
    )

private val chinese: MenuBarTexts =
    MenuBarTexts(
        basicHeading = "基本用法",
        basicHint = "items 接受树形结构；打开子菜单后选择一项会触发 item-selected。",
        nothingSelected = "尚未选择菜单项",
        clickedItem = "已点击：",
        eventLog = "事件流",
        checkableHeading = "可勾选项",
        checkableHint = "子菜单项的 checked 由应用自己维护：在 item-selected 里取反，组件按状态显示勾选标记。",
        options = "选项",
        saveAutomatically = "自动保存",
        notifyWatchers = "通知关注者",
        dividersHeading = "分隔线",
        dividersHint = "把 component 设为 \"hr\" 的条目渲染成分隔线，用来分组子菜单项。",
        dropDownHeading = "下拉菜单",
        dropDownHint = "只有一个按钮时，菜单栏就是普通的下拉菜单，常见于用户账户入口。",
        iconsHeading = "图标",
        iconsHint = "item 的 component 接受任意元素：这里放 vaadin-menu-bar-item，里面是图标加文字；" +
            "theme=\"icon\" 让按钮只保留图标。",
        iconsWithText = "图标加文字",
        iconOnly = "只有图标",
        comboButtons = "组合按钮",
        customDataHeading = "自定义条目数据",
        customDataHint = "条目里可以带业务字段（这里是 value），item-selected 读出来后直接使用；示例把内容写进剪贴板。",
        nothingCopied = "尚未复制",
        copied = "已复制：",
        disabledHeading = "禁用项",
        disabledHint = "disabled 可以设在顶层按钮或子菜单项上，禁用的条目不能点击。",
        classNamesHeading = "自定义类名",
        classNamesHint = "className 加到生成的按钮上，配合主题样式做更细的定制。",
        indicatorsHeading = "展开指示器",
        indicatorsHint = "theme=\"dropdown-indicators\" 给带子菜单的按钮加上下拉箭头。",
        overflowHeading = "溢出折叠",
        overflowHint = "空间不足时，放不下的按钮收进溢出菜单；拖动分隔条可以看到折叠过程。",
        moveSplitter = "拖动分隔条观察溢出",
        rightAlignedHeading = "右对齐",
        rightAlignedHint = "theme=\"end-aligned\" 把按钮组靠右排列，适合放在工具栏末端。",
        stylesHeading = "样式变体",
        stylesHint = "顶层按钮默认使用 tertiary 外观；primary、small 与默认样式对比见下。",
        defaultVariant = "默认",
        tertiaryVariant = "三级",
        primaryVariant = "主要",
        smallVariant = "小号",
        itemText = "条目",
        i18nHeading = "本地化",
        i18nHint = "i18n 覆盖组件自带的文案，例如溢出按钮给屏幕阅读器读的 moreOptions。",
        moreActions = "更多操作",
        tooltipsHeading = "条目提示",
        tooltipsHint = "给每个条目设置 tooltip，再在菜单栏里放一个 slot=\"tooltip\" 的 vaadin-tooltip，" +
            "悬停和键盘聚焦时都会显示。",
        chooseFolder = "选择目标文件夹",
        toArchive = "归档",
        moveToArchive = "移动到归档",
        behaviourHeading = "行为开关",
        behaviourHint = "openOnHover 决定子菜单是否悬停展开，tabNavigation 让 Tab 键在顶层项之间移动。",
        openOnHover = "openOnHover（悬停展开）",
        tabNavigation = "tabNavigation（Tab 键导航）",
        disabledSwitch = "disabled（整体禁用）",
        view = "视图",
        edit = "编辑",
        share = "共享",
        onSocialMedia = "在社交媒体上",
        byEmail = "通过邮件",
        getLink = "获取链接",
        move = "移动",
        toFolder = "到文件夹",
        toFolderEllipsis = "到文件夹…",
        toTrash = "到回收站",
        duplicate = "创建副本",
        setPermissions = "设置权限",
        profile = "个人资料",
        account = "账户",
        preferences = "偏好设置",
        signOut = "退出登录",
        save = "保存",
        otherSaveOptions = "其他保存选项",
        saveAsDraft = "存为草稿",
        saveAsCopy = "存为副本",
        saveAndPublish = "保存并发布",
        copy = "复制",
        copyAsPlainText = "复制为纯文本",
        copyAsHtml = "复制为 HTML",
        copyAsMarkdown = "复制为 Markdown",
        delete = "删除",
    )

private val menuBarTexts: Translations<MenuBarTexts> = texts(
    Langs.EN to english,
    Langs.ZH_HANS to chinese,
)

/** The shared "View / Edit / Share / Move / Duplicate" tree of the examples. */
private fun sharedMenuItems(copy: MenuBarTexts): Array<MenuBarItem> =
    menuItems(
        """
        [
          {"text": "${copy.view}"},
          {"text": "${copy.edit}"},
          {"text": "${copy.share}", "children": [
            {"text": "${copy.onSocialMedia}", "children": [
              {"text": "Facebook"}, {"text": "Twitter"}, {"text": "Instagram"}
            ]},
            {"text": "${copy.byEmail}"},
            {"text": "${copy.getLink}"}
          ]},
          {"text": "${copy.move}", "children": [
            {"text": "${copy.toFolder}"},
            {"text": "${copy.toTrash}"}
          ]},
          {"text": "${copy.duplicate}"}
        ]
        """.trimIndent(),
    )

internal class MenuBarPage : DemoPage() {
    private val texts = Binder(menuBarTexts)

    private val frame = PageFrame(menuBarEntry)

    private val log = EventLog(6)

    private val readout = Readout("")

    private val copied = Readout("")

    /** The value last written to the clipboard; `null` until nothing has been copied. */
    private var copiedValue: String? = null

    /** The item last chosen in the basic example; `null` means nothing has been chosen. */
    private var selectedText: String? = null

    /** Menus whose `items` tree is rebuilt whenever the language changes. */
    private val menuFills: MutableList<Pair<MenuBarElement, (MenuBarTexts) -> Array<MenuBarItem>>> = mutableListOf()

    private fun items(menuBar: MenuBarElement, fill: (MenuBarTexts) -> Array<MenuBarItem>) {
        menuFills += menuBar to fill
    }

    /** The tree the checkable example currently shows, rebuilt from its builder. */
    private fun itemsFor(menuBar: MenuBarElement): Array<MenuBarItem> =
        menuFills.firstOrNull { it.first == menuBar }?.second?.invoke(texts.current) ?: emptyArray()

    /** Whether the "Save automatically" entry of the checkable example is ticked. */
    private var checkedState: Boolean = true

    /** The menu bar whose `moreOptions` text is translated separately from its items. */
    private var localizedBarI18n: MenuBarElement? = null

    private val hoverSwitch = createSwitch()
    private val tabSwitch = createSwitch()
    private val disabledSwitch = createSwitch()
    private val switchReadout = Readout("openOnHover=false · tabNavigation=true · disabled=false")
    private val reactiveBar = createMenuBar()

    private fun sync() {
        reactiveBar.openOnHover = hoverSwitch.checked
        reactiveBar.tabNavigation = tabSwitch.checked
        reactiveBar.disabled = disabledSwitch.checked
        switchReadout.set(
            "openOnHover=${hoverSwitch.checked} · tabNavigation=${tabSwitch.checked} · " +
                "disabled=${disabledSwitch.checked}",
        )
    }

    init {
        frame.mount(this)
        registerVaadinIcons()

        // --- Basic usage ----------------------------------------------------------------------------
        val basic = sectionBlock()
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        val menuBar = createMenuBar()
        items(menuBar) { sharedMenuItems(it) }
        menuBar.dataset["test"] = "menu-bar"
        menuBar.listen(MenuBarEvents.itemSelected) { event ->
            val text = selectedMenuItem(event).text ?: ""
            selectedText = text
            showReadouts()
            log.add("item-selected · $text")
        }
        val eventCaption = HtmlTagName.p()
        texts.bind(eventCaption, { it.eventLog }) { eventCaption.textContent = it }
        basic.body.append(
            exampleBox(null, kitCol(menuBar, readout.root)),
            exampleBox(null, kitCol(eventCaption, log.root)),
        )
        body.append(basic.root)

        // --- Checkable items ------------------------------------------------------------------------
        val checkable = sectionBlock()
        texts.bind(checkable.heading) { it.checkableHeading }
        texts.bind(checkable.hint) { it.checkableHint }
        checkedState = true
        val checkBar = createMenuBar()
        items(checkBar) { copy ->
            menuItems(
                """
                [
                  {"text": "${copy.options}", "children": [
                    {"text": "${copy.saveAutomatically}", "checked": $checkedState},
                    {"text": "${copy.notifyWatchers}"}
                  ]}
                ]
                """.trimIndent(),
            )
        }
        checkBar.listen(MenuBarEvents.itemSelected) {
            // The tree is rebuilt for every language, so the toggled state lives here and is applied
            // whenever the tree is handed to the menu bar again.
            checkedState = !checkedState
            checkBar.items = itemsFor(checkBar)
        }
        checkable.body.append(exampleBox(null, checkBar))
        body.append(checkable.root)

        // --- Dividers -------------------------------------------------------------------------------
        val dividers = sectionBlock()
        texts.bind(dividers.heading) { it.dividersHeading }
        texts.bind(dividers.hint) { it.dividersHint }
        val dividerBar = createMenuBar()
        items(dividerBar) { copy ->
            menuItems(
                """
                [
                  {"text": "${copy.share}", "children": [
                    {"text": "Facebook"}, {"text": "Twitter"}, {"text": "Instagram"},
                    {"component": "hr"},
                    {"text": "${copy.byEmail}"},
                    {"text": "${copy.getLink}"},
                    {"component": "hr"},
                    {"text": "${copy.setPermissions}"}
                  ]}
                ]
                """.trimIndent(),
            )
        }
        dividers.body.append(exampleBox(null, dividerBar))
        body.append(dividers.root)

        // --- Drop-down ------------------------------------------------------------------------------
        val dropDown = sectionBlock()
        texts.bind(dropDown.heading) { it.dropDownHeading }
        texts.bind(dropDown.hint) { it.dropDownHint }
        val accountBar = createMenuBar()
        items(accountBar) { copy ->
            menuItems(
                """
                [
                  {"text": "John Smith", "children": [
                    {"text": "${copy.profile}"},
                    {"text": "${copy.account}"},
                    {"text": "${copy.preferences}"},
                    {"component": "hr"},
                    {"text": "${copy.signOut}"}
                  ]}
                ]
                """.trimIndent(),
            )
        }
        dropDown.body.append(exampleBox(null, accountBar))
        body.append(dropDown.root)

        // --- Icons ----------------------------------------------------------------------------------
        val icons = sectionBlock()
        texts.bind(icons.heading) { it.iconsHeading }
        texts.bind(icons.hint) { it.iconsHint }
        val iconBar = createMenuBar()
        // The component items hold their own elements, so the icons and labels are rebuilt with the tree.
        fun shareChildren(copy: MenuBarTexts): Array<SubMenuItem> =
            arrayOf<SubMenuItem>(
                unsafeJso {
                    component = HTMLElementOrString(
                        menuBarItemElement(icon = "share", text = copy.byEmail, child = true),
                    )
                },
                unsafeJso {
                    component = HTMLElementOrString(
                        menuBarItemElement(icon = "link", text = copy.getLink, child = true),
                    )
                },
            )
        items(iconBar) { copy ->
            arrayOf(
                componentItem(
                    menuBarItemElement(label = copy.share, icon = "share"),
                    children = shareChildren(copy),
                ),
                componentItem(menuBarItemElement(label = copy.duplicate, icon = "copy")),
            )
        }
        iconBar.addTheme("icon")
        val iconOnlyBar = createMenuBar()
        items(iconOnlyBar) { copy ->
            arrayOf(
                componentItem(menuBarItemElement(label = copy.view, icon = "eye")),
                componentItem(menuBarItemElement(label = copy.edit, icon = "pencil")),
                componentItem(
                    menuBarItemElement(label = copy.share, icon = "share"),
                    children = shareChildren(copy),
                ),
                componentItem(
                    menuBarItemElement(label = copy.move, icon = "folder"),
                    children =
                        arrayOf(
                            unsafeJso { text = copy.toFolder },
                            unsafeJso { text = copy.toTrash },
                        ),
                ),
                componentItem(menuBarItemElement(label = copy.duplicate, icon = "copy")),
            )
        }
        iconOnlyBar.addTheme("tertiary-inline")
        val comboBar = createMenuBar()
        items(comboBar) { copy ->
            arrayOf(
                unsafeJso { text = copy.save },
                componentItem(
                    menuBarItemElement(label = copy.otherSaveOptions, icon = "chevron-down"),
                    children =
                        arrayOf(
                            unsafeJso { text = copy.saveAsDraft },
                            unsafeJso { text = copy.saveAsCopy },
                            unsafeJso { text = copy.saveAndPublish },
                        ),
                ),
            )
        }
        comboBar.addTheme("icon", "primary")
        val iconsWithTextCaption = HtmlTagName.p()
        texts.bind(iconsWithTextCaption, { it.iconsWithText }) { iconsWithTextCaption.textContent = it }
        val iconOnlyCaption = HtmlTagName.p()
        texts.bind(iconOnlyCaption, { it.iconOnly }) { iconOnlyCaption.textContent = it }
        val comboCaption = HtmlTagName.p()
        texts.bind(comboCaption, { it.comboButtons }) { comboCaption.textContent = it }
        icons.body.append(
            exampleBox(null, kitCol(iconsWithTextCaption, iconBar)),
            exampleBox(null, kitCol(iconOnlyCaption, iconOnlyBar)),
            exampleBox(null, kitCol(comboCaption, comboBar)),
        )
        body.append(icons.root)

        // --- Custom item data -----------------------------------------------------------------------
        val customData = sectionBlock()
        texts.bind(customData.heading) { it.customDataHeading }
        texts.bind(customData.hint) { it.customDataHint }
        val copyBar = createMenuBar()
        items(copyBar) { copy ->
            menuItems(
                """
                [
                  {"text": "${copy.copy}", "children": [
                    {"text": "${copy.copyAsPlainText}", "value": "Menu Bar"},
                    {"text": "${copy.copyAsHtml}", "value": "<h1>Menu Bar</h1>"},
                    {"text": "${copy.copyAsMarkdown}", "value": "# Menu Bar"}
                  ]}
                ]
                """.trimIndent(),
            )
        }
        copyBar.listen(MenuBarEvents.itemSelected) { event ->
            val item = selectedMenuItem(event)
            val value = js("(item) => item.value || ''")(item) as String
            if (value.isNotEmpty()) {
                js("(text) => { try { if (navigator.clipboard) navigator.clipboard.writeText(text); } catch (e) {} }")(value)
                copiedValue = value
                showReadouts()
            }
        }
        customData.body.append(exampleBox(null, kitCol(copyBar, copied.root)))
        body.append(customData.root)

        // --- Disabled items -------------------------------------------------------------------------
        val disabled = sectionBlock()
        texts.bind(disabled.heading) { it.disabledHeading }
        texts.bind(disabled.hint) { it.disabledHint }
        val disabledBar = createMenuBar()
        items(disabledBar) { copy ->
            menuItems(
                """
                [
                  {"text": "${copy.view}"},
                  {"text": "${copy.edit}", "disabled": true},
                  {"text": "${copy.share}", "children": [
                    {"text": "${copy.byEmail}", "disabled": true},
                    {"text": "${copy.getLink}"}
                  ]}
                ]
                """.trimIndent(),
            )
        }
        disabled.body.append(exampleBox(null, disabledBar))
        body.append(disabled.root)

        // --- Class names ----------------------------------------------------------------------------
        val classNames = sectionBlock()
        texts.bind(classNames.heading) { it.classNamesHeading }
        texts.bind(classNames.hint) { it.classNamesHint }
        val classBar = createMenuBar()
        items(classBar) { copy ->
            menuItems(
                """
                [
                  {"text": "${copy.view}", "className": "custom"},
                  {"text": "${copy.edit}"},
                  {"text": "${copy.share}", "children": [
                    {"text": "${copy.byEmail}", "className": "custom"},
                    {"text": "${copy.getLink}"}
                  ]}
                ]
                """.trimIndent(),
            )
        }
        classNames.body.append(exampleBox(null, classBar))
        body.append(classNames.root)

        // --- Drop-down indicators -------------------------------------------------------------------
        val indicators = sectionBlock()
        texts.bind(indicators.heading) { it.indicatorsHeading }
        texts.bind(indicators.hint) { it.indicatorsHint }
        val indicatorBar = createMenuBar().addTheme("dropdown-indicators")
        items(indicatorBar) { sharedMenuItems(it) }
        indicators.body.append(exampleBox(null, indicatorBar))
        body.append(indicators.root)

        // --- Overflow -------------------------------------------------------------------------------
        val overflow = sectionBlock()
        texts.bind(overflow.heading) { it.overflowHeading }
        texts.bind(overflow.hint) { it.overflowHint }
        val split = createSplitLayout()
        split.style.setProperty("height", "6rem")
        val splitBar = createMenuBar()
        items(splitBar) { sharedMenuItems(it) }
        val splitHint = paragraph("")
        texts.bind(splitHint, { it.moveSplitter }) { splitHint.textContent = it }
        split.append(splitBar, splitHint)
        overflow.body.append(exampleBox(null, split))
        body.append(overflow.root)

        // --- Right-aligned --------------------------------------------------------------------------
        val rightAligned = sectionBlock()
        texts.bind(rightAligned.heading) { it.rightAlignedHeading }
        texts.bind(rightAligned.hint) { it.rightAlignedHint }
        val rightBar = createMenuBar().addTheme("end-aligned")
        items(rightBar) { sharedMenuItems(it) }
        rightBar.style.setProperty("width", "100%")
        rightAligned.body.append(exampleBox(null, rightBar))
        body.append(rightAligned.root)

        // --- Style variants -------------------------------------------------------------------------
        val styles = sectionBlock()
        texts.bind(styles.heading) { it.stylesHeading }
        texts.bind(styles.hint) { it.stylesHint }
        fun menuBarVariant(copy: (MenuBarTexts) -> String, vararg themes: String): HTMLElement {
            val bar = createMenuBar()
            items(bar) { copyTexts ->
                menuItems("""[{"text": "${copy(copyTexts)}", "children": [{"text": "${copyTexts.itemText}"}]}]""")
            }
            return bar.addTheme(*themes)
        }
        styles.body.append(
            exampleBox(
                null,
                kitRow(
                    menuBarVariant({ it.defaultVariant }),
                    menuBarVariant({ it.tertiaryVariant }, "tertiary"),
                    menuBarVariant({ it.primaryVariant }, "primary"),
                    menuBarVariant({ it.smallVariant }, "small"),
                ),
            ),
        )
        body.append(styles.root)

        // --- Internationalization -------------------------------------------------------------------
        val i18nSection = sectionBlock()
        texts.bind(i18nSection.heading) { it.i18nHeading }
        texts.bind(i18nSection.hint) { it.i18nHint }
        val localizedBar = createMenuBar()
        items(localizedBar) { sharedMenuItems(it) }
        localizedBar.i18n = unsafeJso<MenuBarI18n> { moreOptions = texts.current.moreActions }
        localizedBarI18n = localizedBar
        i18nSection.body.append(exampleBox(null, localizedBar))
        body.append(i18nSection.root)

        // --- Tooltips -------------------------------------------------------------------------------
        val tooltips = sectionBlock()
        texts.bind(tooltips.heading) { it.tooltipsHeading }
        texts.bind(tooltips.hint) { it.tooltipsHint }
        val tooltipBar = createMenuBar()
        items(tooltipBar) { copy ->
            arrayOf(
                componentItem(menuBarItemElement(label = copy.view, icon = "eye"), tooltip = copy.view),
                componentItem(menuBarItemElement(label = copy.edit, icon = "pencil"), tooltip = copy.edit),
                componentItem(
                    menuBarItemElement(label = copy.move, icon = "folder"),
                    tooltip = copy.move,
                    children =
                        arrayOf(
                            unsafeJso {
                                text = copy.toFolderEllipsis
                                tooltip = copy.chooseFolder
                            },
                            unsafeJso {
                                text = copy.toArchive
                                tooltip = copy.moveToArchive
                                tooltipPosition = "end"
                            },
                        ),
                ),
                componentItem(menuBarItemElement(label = copy.duplicate, icon = "copy"), tooltip = copy.duplicate),
                unsafeJso {
                    component = HTMLElementOrString(menuBarItemElement(label = copy.delete, icon = "trash"))
                    tooltip = copy.delete
                    this.disabled = true
                },
            )
        }
        tooltipBar.addTheme("icon")
        tooltipBar.append(createTooltip().apply { slot = "tooltip" })
        tooltips.body.append(exampleBox(null, tooltipBar))
        body.append(tooltips.root)

        // --- Behaviour switches ---------------------------------------------------------------------
        val behaviour = sectionBlock()
        texts.bind(behaviour.heading) { it.behaviourHeading }
        texts.bind(behaviour.hint) { it.behaviourHint }
        texts.bind(hoverSwitch, { it.openOnHover }) { hoverSwitch.label = it }
        hoverSwitch.checked = false
        texts.bind(tabSwitch, { it.tabNavigation }) { tabSwitch.label = it }
        tabSwitch.checked = true
        texts.bind(disabledSwitch, { it.disabledSwitch }) { disabledSwitch.label = it }
        items(reactiveBar) { sharedMenuItems(it) }
        hoverSwitch.listen(SwitchEvents.checkedChanged) { sync() }
        tabSwitch.listen(SwitchEvents.checkedChanged) { sync() }
        disabledSwitch.listen(SwitchEvents.checkedChanged) { sync() }
        sync()

        behaviour.body.append(
            exampleBox(null, kitCol(hoverSwitch, tabSwitch, disabledSwitch, reactiveBar, switchReadout.root)),
        )
        body.append(behaviour.root)

        applyTranslation(Langs.EN)
    }

    private fun showReadouts() {
        val copy = texts.current
        readout.set(
            copy.clickedItem,
            selectedText ?: copy.nothingSelected,
        )
        copied.set(copy.copied, copiedValue ?: "")
        if (copiedValue == null) copied.set(copy.nothingCopied)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = menuBarEntry.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        val copy = texts.current
        menuFills.forEach { (bar, fill) -> bar.items = fill(copy) }
        localizedBarI18n?.let { bar -> bar.i18n = unsafeJso<MenuBarI18n> { moreOptions = copy.moreActions } }
        showReadouts()
    }

    companion object : CustomElementReference<MenuBarPage>(MenuBarPage::class.js) {
        val NAME: TagName<MenuBarPage> = TagName("vaadin-demo-menu-bar-page")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<vaadin-demo-menu-bar-page>`. */
internal fun newMenuBarPage(): MenuBarPage {
    MenuBarPage.define()
    return MenuBarPage.NAME()
}
