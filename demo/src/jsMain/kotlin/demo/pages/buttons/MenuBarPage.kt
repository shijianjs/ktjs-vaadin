package demo.pages.buttons

import io.github.shijianjs.ktjs.demo.page.col as kitCol
import io.github.shijianjs.ktjs.demo.page.exampleBox
import io.github.shijianjs.ktjs.demo.page.row as kitRow
import io.github.shijianjs.ktjs.demo.page.sectionBlock


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
import demo.tr
import demo.ui.EventLog
import demo.ui.PageMeta
import demo.ui.configureFrame
import demo.ui.Readout
import demo.ui.jsObject
import demo.ui.listen
import js.objects.unsafeJso
import kotlin.js.unsafeCast
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.vaadin.icons.registerVaadinIcons
import io.github.shijianjs.ktjs.vaadin.menubar.HTMLElementOrString
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
        rebuildsOnLanguage = true,
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

private fun sharedMenuItems(): Array<MenuBarItem> =
    menuItems(
        """
        [
          {"text": "${tr("视图", "View")}"},
          {"text": "${tr("编辑", "Edit")}"},
          {"text": "${tr("共享", "Share")}", "children": [
            {"text": "${tr("在社交媒体上", "On social media")}", "children": [
              {"text": "Facebook"}, {"text": "Twitter"}, {"text": "Instagram"}
            ]},
            {"text": "${tr("通过邮件", "By email")}"},
            {"text": "${tr("获取链接", "Get link")}"}
          ]},
          {"text": "${tr("移动", "Move")}", "children": [
            {"text": "${tr("到文件夹", "To folder")}"},
            {"text": "${tr("到回收站", "To trash")}"}
          ]},
          {"text": "${tr("创建副本", "Duplicate")}"}
        ]
        """.trimIndent(),
    )

internal class MenuBarPage : DemoPage() {

    init {
        configureFrame(menuBarEntry)
        registerVaadinIcons()

        // --- Basic usage ----------------------------------------------------------------------------
        val basic =
            sectionBlock(
                tr("基本用法", "Basic usage"),
                tr(
                    "items 接受树形结构；打开子菜单后选择一项会触发 item-selected。",
                    "items takes a tree; choosing an entry from an opened submenu fires item-selected.",
                ),
            )
        val log = EventLog(6)
        val readout = Readout(tr("尚未选择菜单项", "No menu item selected"))
        val menuBar = createMenuBar { items = sharedMenuItems() }
        menuBar.dataset["test"] = "menu-bar"
        menuBar.listen(MenuBarEvents.itemSelected) { event ->
            val text = selectedMenuItem(event).text ?: ""
            readout.set(tr("已点击：", "Clicked item: "), text)
            log.add("item-selected · $text")
        }
        basic.body.append(
            exampleBox(null, kitCol(menuBar, readout.root)),
            exampleBox(tr("事件流", "Event log"), log.root),
        )
        body.append(basic.root)

        // --- Checkable items ------------------------------------------------------------------------
        val checkable =
            sectionBlock(
                tr("可勾选项", "Checkable items"),
                tr(
                    "子菜单项的 checked 由应用自己维护：在 item-selected 里取反，组件按状态显示勾选标记。",
                    "The application owns the checked state of sub-menu items: toggle it in item-selected " +
                        "and the component renders the mark.",
                ),
            )
        val options =
            menuItems(
                """
                [
                  {"text": "${tr("选项", "Options")}", "children": [
                    {"text": "${tr("自动保存", "Save automatically")}", "checked": true},
                    {"text": "${tr("通知关注者", "Notify watchers")}"}
                  ]}
                ]
                """.trimIndent(),
            )
        val checkBar = createMenuBar { items = options }
        checkBar.listen(MenuBarEvents.itemSelected) { event ->
            val item = selectedMenuItem(event).unsafeCast<SubMenuItem>()
            item.checked = !(item.checked ?: false)
        }
        checkable.body.append(exampleBox(null, checkBar))
        body.append(checkable.root)

        // --- Dividers -------------------------------------------------------------------------------
        val dividers =
            sectionBlock(
                tr("分隔线", "Dividers"),
                tr(
                    "把 component 设为 \"hr\" 的条目渲染成分隔线，用来分组子菜单项。",
                    "An item with component set to \"hr\" renders as a divider that groups sub-menu entries.",
                ),
            )
        dividers.body.append(
            exampleBox(
                null,
                createMenuBar {
                    items =
                        menuItems(
                            """
                            [
                              {"text": "${tr("共享", "Share")}", "children": [
                                {"text": "Facebook"}, {"text": "Twitter"}, {"text": "Instagram"},
                                {"component": "hr"},
                                {"text": "${tr("通过邮件", "By email")}"},
                                {"text": "${tr("获取链接", "Get link")}"},
                                {"component": "hr"},
                                {"text": "${tr("设置权限", "Set permissions")}"}
                              ]}
                            ]
                            """.trimIndent(),
                        )
                },
            ),
        )
        body.append(dividers.root)

        // --- Drop-down ------------------------------------------------------------------------------
        val dropDown =
            sectionBlock(
                tr("下拉菜单", "Drop-down menu"),
                tr(
                    "只有一个按钮时，菜单栏就是普通的下拉菜单，常见于用户账户入口。",
                    "With a single button the menu bar is a plain drop-down, the usual shape of an account menu.",
                ),
            )
        dropDown.body.append(
            exampleBox(
                null,
                createMenuBar {
                    items =
                        menuItems(
                            """
                            [
                              {"text": "John Smith", "children": [
                                {"text": "${tr("个人资料", "Profile")}"},
                                {"text": "${tr("账户", "Account")}"},
                                {"text": "${tr("偏好设置", "Preferences")}"},
                                {"component": "hr"},
                                {"text": "${tr("退出登录", "Sign out")}"}
                              ]}
                            ]
                            """.trimIndent(),
                        )
                },
            ),
        )
        body.append(dropDown.root)

        // --- Icons ----------------------------------------------------------------------------------
        val icons =
            sectionBlock(
                tr("图标", "Icons"),
                tr(
                    "item 的 component 接受任意元素：这里放 vaadin-menu-bar-item，里面是图标加文字；" +
                        "theme=\"icon\" 让按钮只保留图标。",
                    "The component of an item accepts any element: here a vaadin-menu-bar-item holding an icon " +
                        "and text; theme=\"icon\" keeps only the icon in the buttons.",
                ),
            )
        val shareChildren =
            arrayOf<SubMenuItem>(
                unsafeJso { component = HTMLElementOrString(menuBarItemElement(icon = "share", text = tr("通过邮件", "By email"), child = true)) },
                unsafeJso { component = HTMLElementOrString(menuBarItemElement(icon = "link", text = tr("获取链接", "Get link"), child = true)) },
            )
        val iconBar =
            createMenuBar {
                items =
                    arrayOf(
                        componentItem(
                            menuBarItemElement(label = tr("共享", "Share"), icon = "share"),
                            children = shareChildren,
                        ),
                        componentItem(menuBarItemElement(label = tr("创建副本", "Duplicate"), icon = "copy")),
                    )
            }
        iconBar.addTheme("icon")
        val iconOnlyBar =
            createMenuBar {
                items =
                    arrayOf(
                        componentItem(menuBarItemElement(label = tr("查看", "View"), icon = "eye")),
                        componentItem(menuBarItemElement(label = tr("编辑", "Edit"), icon = "pencil")),
                        componentItem(
                            menuBarItemElement(label = tr("共享", "Share"), icon = "share"),
                            children = shareChildren,
                        ),
                        componentItem(
                            menuBarItemElement(label = tr("移动", "Move"), icon = "folder"),
                            children =
                                arrayOf(
                                    unsafeJso { text = tr("到文件夹", "To folder") },
                                    unsafeJso { text = tr("到回收站", "To trash") },
                                ),
                        ),
                        componentItem(menuBarItemElement(label = tr("创建副本", "Duplicate"), icon = "copy")),
                    )
            }
        iconOnlyBar.addTheme("tertiary-inline")
        val comboBar =
            createMenuBar {
                items =
                    arrayOf(
                        unsafeJso { text = tr("保存", "Save") },
                        componentItem(
                            menuBarItemElement(label = tr("其他保存选项", "Other save options"), icon = "chevron-down"),
                            children =
                                arrayOf(
                                    unsafeJso { text = tr("存为草稿", "Save as draft") },
                                    unsafeJso { text = tr("存为副本", "Save as copy") },
                                    unsafeJso { text = tr("保存并发布", "Save and publish") },
                                ),
                        ),
                    )
            }
        comboBar.addTheme("icon", "primary")
        icons.body.append(
            exampleBox(tr("图标加文字", "Icons with text"), iconBar),
            exampleBox(tr("只有图标", "Icon-only buttons"), iconOnlyBar),
            exampleBox(tr("组合按钮", "Combo buttons"), comboBar),
        )
        body.append(icons.root)

        // --- Custom item data -----------------------------------------------------------------------
        val customData =
            sectionBlock(
                tr("自定义条目数据", "Custom item data"),
                tr(
                    "条目里可以带业务字段（这里是 value），item-selected 读出来后直接使用；示例把内容写进剪贴板。",
                    "Items can carry application fields (value here) that item-selected reads back; the example " +
                        "copies the text to the clipboard.",
                ),
            )
        val copied = Readout(tr("尚未复制", "Nothing copied yet"))
        val copyBar =
            createMenuBar {
                items =
                    menuItems(
                        """
                        [
                          {"text": "${tr("复制", "Copy")}", "children": [
                            {"text": "${tr("复制为纯文本", "Copy as plain text")}", "value": "Menu Bar"},
                            {"text": "${tr("复制为 HTML", "Copy as HTML")}", "value": "<h1>Menu Bar</h1>"},
                            {"text": "${tr("复制为 Markdown", "Copy as Markdown")}", "value": "# Menu Bar"}
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
                copied.set(tr("已复制：", "Copied: "), value)
            }
        }
        customData.body.append(exampleBox(null, kitCol(copyBar, copied.root)))
        body.append(customData.root)

        // --- Disabled items -------------------------------------------------------------------------
        val disabled =
            sectionBlock(
                tr("禁用项", "Disabled items"),
                tr(
                    "disabled 可以设在顶层按钮或子菜单项上，禁用的条目不能点击。",
                    "disabled works on top-level buttons and on sub-menu entries; disabled items cannot be chosen.",
                ),
            )
        disabled.body.append(
            exampleBox(
                null,
                createMenuBar {
                    items =
                        menuItems(
                            """
                            [
                              {"text": "${tr("视图", "View")}"},
                              {"text": "${tr("编辑", "Edit")}", "disabled": true},
                              {"text": "${tr("共享", "Share")}", "children": [
                                {"text": "${tr("通过邮件", "By email")}", "disabled": true},
                                {"text": "${tr("获取链接", "Get link")}"}
                              ]}
                            ]
                            """.trimIndent(),
                        )
                },
            ),
        )
        body.append(disabled.root)

        // --- Class names ----------------------------------------------------------------------------
        val classNames =
            sectionBlock(
                tr("自定义类名", "Item class names"),
                tr(
                    "className 加到生成的按钮上，配合主题样式做更细的定制。",
                    "className is added to the generated button for finer-grained styling.",
                ),
            )
        classNames.body.append(
            exampleBox(
                null,
                createMenuBar {
                    items =
                        menuItems(
                            """
                            [
                              {"text": "${tr("视图", "View")}", "className": "custom"},
                              {"text": "${tr("编辑", "Edit")}"},
                              {"text": "${tr("共享", "Share")}", "children": [
                                {"text": "${tr("通过邮件", "By email")}", "className": "custom"},
                                {"text": "${tr("获取链接", "Get link")}"}
                              ]}
                            ]
                            """.trimIndent(),
                        )
                },
            ),
        )
        body.append(classNames.root)

        // --- Drop-down indicators -------------------------------------------------------------------
        val indicators =
            sectionBlock(
                tr("展开指示器", "Drop-down indicators"),
                tr(
                    "theme=\"dropdown-indicators\" 给带子菜单的按钮加上下拉箭头。",
                    "theme=\"dropdown-indicators\" adds a chevron to the buttons that have a submenu.",
                ),
            )
        indicators.body.append(
            exampleBox(
                null,
                createMenuBar { items = sharedMenuItems() }.addTheme("dropdown-indicators"),
            ),
        )
        body.append(indicators.root)

        // --- Overflow -------------------------------------------------------------------------------
        val overflow =
            sectionBlock(
                tr("溢出折叠", "Overflow"),
                tr(
                    "空间不足时，放不下的按钮收进溢出菜单；拖动分隔条可以看到折叠过程。",
                    "Buttons that no longer fit move into the overflow menu; drag the splitter to watch it happen.",
                ),
            )
        val split = createSplitLayout()
        split.style.setProperty("height", "6rem")
        split.append(
            createMenuBar { items = sharedMenuItems() },
            paragraph(tr("拖动分隔条观察溢出", "Move the splitter to see overflow")),
        )
        overflow.body.append(exampleBox(null, split))
        body.append(overflow.root)

        // --- Right-aligned --------------------------------------------------------------------------
        val rightAligned =
            sectionBlock(
                tr("右对齐", "Right-aligned"),
                tr(
                    "theme=\"end-aligned\" 把按钮组靠右排列，适合放在工具栏末端。",
                    "theme=\"end-aligned\" pushes the buttons to the trailing edge, as in a toolbar.",
                ),
            )
        val rightBar = createMenuBar { items = sharedMenuItems() }.addTheme("end-aligned")
        rightBar.style.setProperty("width", "100%")
        rightAligned.body.append(exampleBox(null, rightBar))
        body.append(rightAligned.root)

        // --- Style variants -------------------------------------------------------------------------
        val styles =
            sectionBlock(
                tr("样式变体", "Style variants"),
                tr(
                    "顶层按钮默认使用 tertiary 外观；primary、small 与默认样式对比见下。",
                    "Top-level buttons use the tertiary look by default; compare primary and small below.",
                ),
            )

        styles.body.append(
            exampleBox(
                null,
                kitRow(
                    menuBarVariant(tr("默认", "Default")),
                    menuBarVariant(tr("三级", "Tertiary"), "tertiary"),
                    menuBarVariant(tr("主要", "Primary"), "primary"),
                    menuBarVariant(tr("小号", "Small"), "small"),
                ),
            ),
        )
        body.append(styles.root)

        // --- Internationalization -------------------------------------------------------------------
        val i18n =
            sectionBlock(
                tr("本地化", "Internationalization"),
                tr(
                    "i18n 覆盖组件自带的文案，例如溢出按钮给屏幕阅读器读的 moreOptions。",
                    "i18n overrides the built-in texts, such as moreOptions that screen readers announce for " +
                        "the overflow button.",
                ),
            )
        i18n.body.append(
            exampleBox(
                null,
                createMenuBar {
                    items = sharedMenuItems()
                    this.i18n = unsafeJso<MenuBarI18n> { moreOptions = tr("更多操作", "More actions") }
                },
            ),
        )
        body.append(i18n.root)

        // --- Tooltips -------------------------------------------------------------------------------
        val tooltips =
            sectionBlock(
                tr("条目提示", "Item tooltips"),
                tr(
                    "给每个条目设置 tooltip，再在菜单栏里放一个 slot=\"tooltip\" 的 vaadin-tooltip，悬停和键盘聚焦时都会显示。",
                    "Set tooltip on the items and add a slotted vaadin-tooltip; it shows on hover and keyboard focus.",
                ),
            )
        val tooltipBar =
            createMenuBar {
                items =
                    arrayOf(
                        componentItem(menuBarItemElement(label = tr("查看", "View"), icon = "eye"), tooltip = tr("查看", "View")),
                        componentItem(menuBarItemElement(label = tr("编辑", "Edit"), icon = "pencil"), tooltip = tr("编辑", "Edit")),
                        componentItem(
                            menuBarItemElement(label = tr("移动", "Move"), icon = "folder"),
                            tooltip = tr("移动", "Move"),
                            children =
                                arrayOf(
                                    unsafeJso {
                                        text = tr("到文件夹…", "To folder…")
                                        tooltip = tr("选择目标文件夹", "Choose a destination folder")
                                    },
                                    unsafeJso {
                                        text = tr("归档", "To archive")
                                        tooltip = tr("移动到归档", "Move to archive")
                                        tooltipPosition = "end"
                                    },
                                ),
                        ),
                        componentItem(menuBarItemElement(label = tr("创建副本", "Duplicate"), icon = "copy"), tooltip = tr("创建副本", "Duplicate")),
                        unsafeJso {
                            component = HTMLElementOrString(menuBarItemElement(label = tr("删除", "Delete"), icon = "trash"))
                            tooltip = tr("删除", "Delete")
                            this.disabled = true
                        },
                    )
            }
        tooltipBar.addTheme("icon")
        tooltipBar.append(createTooltip().apply { slot = "tooltip" })
        tooltips.body.append(exampleBox(null, tooltipBar))
        body.append(tooltips.root)

        // --- Behaviour switches ---------------------------------------------------------------------
        val behaviour =
            sectionBlock(
                tr("行为开关", "Behaviour switches"),
                tr(
                    "openOnHover 决定子菜单是否悬停展开，tabNavigation 让 Tab 键在顶层项之间移动。",
                    "openOnHover controls whether submenus open on hover; tabNavigation moves the Tab key " +
                        "between top-level items.",
                ),
            )
        val hoverSwitch = createSwitch { label = tr("openOnHover（悬停展开）", "openOnHover (open on hover)") }
        hoverSwitch.checked = false
        val tabSwitch = createSwitch { label = tr("tabNavigation（Tab 键导航）", "tabNavigation (Tab key navigation)") }
        tabSwitch.checked = true
        val disabledSwitch = createSwitch { label = tr("disabled（整体禁用）", "disabled (whole menu bar)") }
        val switchReadout = Readout("openOnHover=false · tabNavigation=true · disabled=false")

        val reactiveBar = createMenuBar { items = sharedMenuItems() }
        val sync = {
            reactiveBar.openOnHover = hoverSwitch.checked
            reactiveBar.tabNavigation = tabSwitch.checked
            reactiveBar.disabled = disabledSwitch.checked
            switchReadout.set(
                "openOnHover=${hoverSwitch.checked} · tabNavigation=${tabSwitch.checked} · " +
                    "disabled=${disabledSwitch.checked}",
            )
        }
        hoverSwitch.listen(SwitchEvents.checkedChanged) { sync() }
        tabSwitch.listen(SwitchEvents.checkedChanged) { sync() }
        disabledSwitch.listen(SwitchEvents.checkedChanged) { sync() }
        sync()

        behaviour.body.append(
            exampleBox(null, kitCol(hoverSwitch, tabSwitch, disabledSwitch, reactiveBar, switchReadout.root)),
        )
        body.append(behaviour.root)

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

/**
 * One menu bar in the style-variant comparison.
 *
 * It stays a factory rather than an element because a menu bar's items are read off the component
 * itself (`items`), so what goes into the row has to be the `vaadin-menu-bar`.
 */
private fun menuBarVariant(label: String, vararg themes: String): HTMLElement =
    createMenuBar {
        items =
            menuItems(
                """[{"text": "$label", "children": [{"text": "${tr("条目", "Item")}"}]}]""",
            )
    }.addTheme(*themes)
