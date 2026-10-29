package demo.pages

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.tr
import demo.ui.EventLog
import demo.ui.Images
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.Readout
import demo.ui.column
import demo.ui.div
import demo.ui.detailText
import demo.ui.element
import demo.ui.example
import demo.ui.jsArray
import demo.ui.jsObject
import demo.ui.listen
import demo.ui.page
import demo.ui.row
import demo.ui.section
import demo.ui.withTheme
import js.objects.unsafeJso
import kotlin.js.JsAny
import kotlin.js.unsafeCast
import vaadin.button.createButton
import vaadin.emailfield.EmailFieldEvents
import vaadin.emailfield.createEmailField
import vaadin.formlayout.FormLayoutLabelsPositions
import vaadin.formlayout.FormLayoutResponsiveStep
import vaadin.formlayout.StringOrDouble
import vaadin.formlayout.createFormLayout
import vaadin.grid.GridEvents
import vaadin.grid.createGrid
import vaadin.grid.createGridColumn
import vaadin.grid.createGridSelectionColumn
import vaadin.icon.createIcon
import vaadin.icons.registerVaadinIcons
import vaadin.menubar.HTMLElementOrString
import vaadin.menubar.MenuBarEvents
import vaadin.menubar.MenuBarI18n
import vaadin.menubar.MenuBarItem
import vaadin.menubar.SubMenuItem
import vaadin.menubar.createMenuBar
import vaadin.progressbar.createProgressBar
import vaadin.switch.SwitchEvents
import vaadin.switch.createSwitch
import vaadin.splitlayout.createSplitLayout
import vaadin.textfield.createTextField
import vaadin.tooltip.createTooltip
import web.dom.clickEvent
import web.dom.document
import web.events.addHandler
import web.html.HTMLElement
import web.html.HtmlTagName
import web.window.window

val buttonEntries: List<CatalogEntry> =
    listOf(
        CatalogEntry(
            id = "button",
            group = Groups.BUTTONS,
            meta =
                PageMeta(
                    title = bi("Button", "Button"),
                    summary =
                        bi(
                            "可点击的按钮：主题变体、尺寸、图标插槽、禁用状态与点击事件。",
                            "Clickable buttons: theme variants, sizes, icon slots, disabled state and click events.",
                        ),
                    docsUrl = "$DOCS_BASE/button",
                    sourcePath = "pages/Buttons.kt",
                ),
            builder = ::buildButtonPage,
        ),
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
                    sourcePath = "pages/Buttons.kt",
                ),
            builder = ::buildMenuBarPage,
        ),
    )

/**
 * Repeating timer used by the long-running example. kotlin-browser has no timer wrapper, so the
 * two browser calls stay behind these helpers instead of spreading `js(...)` through the page.
 */
private var intervalHandle: Int? = null

private fun startInterval(millis: Int, tick: () -> Unit) {
    stopInterval()
    intervalHandle = js("(fn, ms) => window.setInterval(fn, ms)")(tick, millis) as Int
}

private fun stopInterval() {
    val handle = intervalHandle ?: return
    js("(handle) => window.clearInterval(handle)")(handle)
    intervalHandle = null
}

private const val BUTTON_GRID_USERS =
    """
    [
      {"firstName": "Ada", "lastName": "Lovelace", "email": "ada@example.com"},
      {"firstName": "Grace", "lastName": "Hopper", "email": "grace@example.com"},
      {"firstName": "Margaret", "lastName": "Hamilton", "email": "margaret@example.com"},
      {"firstName": "Katherine", "lastName": "Johnson", "email": "katherine@example.com"}
    ]
    """

private fun buildButtonPage(meta: PageMeta): PageView {
    val view = page(meta)
    registerVaadinIcons()

    // --- Basic usage ----------------------------------------------------------------------------
    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "点击按钮触发 click 事件，页面把次数和事件流写在下面。",
                "Clicking a button fires the click event; the count and the event log are shown below.",
            ),
        )
    val clicks = Readout(tr("点击次数：0", "Clicks: 0"))
    val log = EventLog()
    var count = 0
    val saveButton = createButton { textContent = tr("保存更改", "Save changes") }
    saveButton.dataset["test"] = "basic-button"
    saveButton.clickEvent.addHandler {
        count++
        clicks.set(tr("点击次数：$count", "Clicks: $count"))
        log.add(tr("click · 第 $count 次", "click · attempt $count"))
    }
    basic.body.append(
        example(null, row(saveButton, clicks.root)),
        example(tr("事件流", "Event log"), log.root),
    )
    view.body.append(basic.root)

    // --- Buttons with icons ---------------------------------------------------------------------
    val iconSection =
        section(
            tr("图标按钮", "Buttons with icons"),
            tr(
                "只有图标的按钮要用 aria-label 提供文本替代；tooltip 插槽可以补充说明；prefix / suffix " +
                    "插槽把图标放在文字两侧。",
                "Icon-only buttons need an aria-label as a textual alternative; the tooltip slot adds a " +
                    "description; the prefix / suffix slots place icons next to the label.",
            ),
        )
    val addItem = createButton()
    addItem.ariaLabel = tr("添加项目", "Add item")
    addItem.append(createIcon { icon = "vaadin:plus" })
    val closeButton = createButton()
    closeButton.ariaLabel = tr("关闭", "Close")
    closeButton.append(createIcon { icon = "vaadin:close" })
    closeButton.append(createTooltip { text = tr("关闭对话框", "Close the dialog") }.apply { slot = "tooltip" })
    val moveLeft = createButton { textContent = tr("向左", "Left") }
    moveLeft.append(createIcon { icon = "vaadin:arrow-left" }.apply { slot = "prefix" })
    val moveRight = createButton { textContent = tr("向右", "Right") }
    moveRight.append(createIcon { icon = "vaadin:arrow-right" }.apply { slot = "suffix" })
    iconSection.body.append(example(null, row(addItem, closeButton, moveLeft, moveRight)))
    view.body.append(iconSection.root)

    // --- Buttons with images --------------------------------------------------------------------
    val imageSection =
        section(
            tr("图片按钮", "Buttons with images"),
            tr(
                "图标位置也可以放普通图片，用 alt 文本描述图片内容。",
                "The icon slots accept plain images as well; describe them with alt text.",
            ),
        )
    val logoButton = createButton()
    val logo = document.createElement(HtmlTagName.img)
    logo.src = Images.vaadinLogo
    logo.width = 100
    logo.alt = tr("Vaadin 标志", "Vaadin logo")
    logoButton.append(logo)
    imageSection.body.append(example(null, logoButton))
    view.body.append(imageSection.root)

    // --- Disabled -------------------------------------------------------------------------------
    val disabledSection =
        section(
            tr("禁用状态", "Disabled"),
            tr(
                "禁用按钮不响应点击，也不会提交表单；下面可以实时切换。",
                "A disabled button ignores clicks and does not submit forms; toggle it live below.",
            ),
        )
    val target = createButton { textContent = tr("启用中的按钮", "Enabled button") }.withTheme("primary")
    val stateReadout = Readout(tr("当前状态：启用", "State: enabled"))
    target.clickEvent.addHandler {
        stateReadout.set(tr("当前状态：启用，刚刚被点击", "State: enabled, just clicked"))
    }
    val toggleButton =
        createButton { textContent = tr("切换到禁用", "Disable") }.apply {
            clickEvent.addHandler {
                target.disabled = !target.disabled
                textContent = if (target.disabled) tr("切换到启用", "Enable") else tr("切换到禁用", "Disable")
                stateReadout.set(
                    tr(
                        if (target.disabled) "当前状态：禁用" else "当前状态：启用",
                        if (target.disabled) "State: disabled" else "State: enabled",
                    ),
                )
            }
        }

    disabledSection.body.append(
        example(
            null,
            column(
                row(
                    createButton { textContent = "primary" }.withTheme("primary").apply { this.disabled = true },
                    createButton { textContent = "secondary" }.withTheme("secondary").apply { this.disabled = true },
                    createButton { textContent = "tertiary" }.withTheme("tertiary").apply { this.disabled = true },
                ),
                row(target, toggleButton, stateReadout.root),
            ),
        ),
    )
    view.body.append(disabledSection.root)

    // --- Keyboard focus -------------------------------------------------------------------------
    val focus =
        section(
            tr("键盘焦点", "Keyboard focus"),
            tr(
                "focus-ring 只在使用键盘聚焦时出现，鼠标点击不会显示；下面用属性直接展示该状态。",
                "focus-ring appears only when the button is focused with the keyboard, not on mouse click; " +
                    "the example sets the state attribute directly.",
            ),
        )
    val focusButton = createButton { textContent = tr("键盘焦点", "Keyboard focus") }
    focusButton.setAttribute("focus-ring", "")
    focus.body.append(
        example(
            null,
            column(
                row(focusButton, createButton { textContent = tr("普通按钮（按 Tab 试试）", "Plain button (try Tab)") }),
            ),
        ),
    )
    view.body.append(focus.root)

    // --- Buttons in forms -----------------------------------------------------------------------
    val forms =
        section(
            tr("表单与对话框中的按钮", "Buttons in forms and dialogs"),
            tr(
                "表单底部的按钮按主次排列；对话框把取消 / 确认放在 slot=\"end\"，让它们靠右对齐。",
                "The form actions follow the primary / secondary order; a dialog puts cancel / confirm in " +
                    "slot=\"end\" so they align to the trailing edge.",
            ),
        )
    val form = createFormLayout()
    form.responsiveSteps =
        arrayOf(
            unsafeJso<FormLayoutResponsiveStep> {
                minWidth = StringOrDouble("0px")
                columns = 2.0
                labelsPosition = FormLayoutLabelsPositions.top
            },
        )
    val emailField =
        createEmailField {
            label = tr("邮箱地址", "Email address")
            value = "john.smith@example.com"
        }.apply { setAttribute("colspan", "2") }
    form.append(
        createTextField {
            label = tr("名", "First name")
            value = "John"
        },
        createTextField {
            label = tr("姓", "Last name")
            value = "Smith"
        },
        emailField,
    )
    val formActions =
        row(
            createButton { textContent = tr("创建账户", "Create account") }.withTheme("primary"),
            createButton { textContent = tr("取消", "Cancel") }.withTheme("secondary"),
        )
    val dialogActions =
        row(
            createButton { textContent = tr("删除", "Delete") }.withTheme("error"),
            createButton { textContent = tr("取消", "Cancel") }.apply { slot = "end" },
            createButton { textContent = tr("创建账户", "Create account") }.withTheme("primary").apply { slot = "end" },
        )
    forms.body.append(
        example(tr("表单操作按钮", "Form actions"), column(form, formActions)),
        example(tr("对话框页脚", "Dialog footer"), dialogActions),
    )
    view.body.append(forms.root)

    // --- Button labels --------------------------------------------------------------------------
    val labels =
        section(
            tr("按钮标签", "Button labels"),
            tr(
                "并排的字段和按钮用行内布局对齐基线，按钮文字直接说明作用于哪个字段。",
                "A field and its button sit in one row aligned on the baseline, and the button label names " +
                    "the field it acts on.",
            ),
        )
    fun emailRow(label: String, initial: String): HTMLElement {
        val field =
            createEmailField {
                this.label = label
                value = initial
            }
        val remove =
            createButton { textContent = tr("移除", "Remove") }.apply {
                ariaLabel = tr("移除 $label", "Remove $label")
                clickEvent.addHandler { field.value = "" }
            }
        return row(field, remove)
    }
    labels.body.append(
        example(
            null,
            column(
                emailRow(tr("首选邮箱", "Primary email address"), "foo@example.com"),
                emailRow(tr("备用邮箱", "Secondary email address"), "bar@example.com"),
            ),
        ),
    )
    view.body.append(labels.root)

    // --- Sizes ----------------------------------------------------------------------------------
    val sizing =
        section(
            tr("尺寸", "Sizes"),
            tr(
                "large / normal / small 改变内边距与字号，三者可以并排使用。",
                "large / normal / small change the padding and font size; they can be mixed in one row.",
            ),
        )
    sizing.body.append(
        example(
            null,
            row(
                createButton { textContent = "Large" }.withTheme("large"),
                createButton { textContent = "Normal" }.withTheme("normal"),
                createButton { textContent = "Small" }.withTheme("small"),
            ),
        ),
    )
    view.body.append(sizing.root)

    // --- Style variants -------------------------------------------------------------------------
    val styles =
        section(
            tr("样式变体", "Style variants"),
            tr(
                "theme 属性接受空格分隔的多个变体；primary / secondary / tertiary 表达操作层级，" +
                    "tertiary-inline 用于正文中的行内操作。",
                "The theme property accepts several space-separated variants; primary / secondary / " +
                    "tertiary express the action hierarchy and tertiary-inline is for inline actions.",
            ),
        )
    val variantLog = EventLog(4)
    styles.body.append(
        example(
            null,
            row(
                createButton { textContent = "Primary" }.withTheme("primary"),
                createButton { textContent = "Secondary" }.withTheme("secondary"),
                createButton { textContent = "Tertiary" }.withTheme("tertiary"),
                createButton { textContent = "Tertiary inline" }.withTheme("tertiary-inline"),
                createButton { textContent = "Contrast" }.withTheme("contrast"),
            ),
        ),
        example(
            tr("点击任意变体记录事件", "Click any variant to record an event"),
            row(
                *listOf(
                    tr("默认", "Default") to listOf<String>(),
                    "primary" to listOf("primary"),
                    "tertiary" to listOf("tertiary"),
                    "tertiary-inline" to listOf("tertiary-inline"),
                    "contrast" to listOf("contrast"),
                ).map { (label, themes) ->
                    createButton { textContent = label }.withTheme(*themes.toTypedArray()).apply {
                        clickEvent.addHandler { variantLog.add("click · $label") }
                    }
                }.toTypedArray(),
            ),
            variantLog.root,
        ),
    )
    view.body.append(styles.root)

    // --- Semantic colours -----------------------------------------------------------------------
    val colours =
        section(
            tr("语义颜色", "Semantic colours"),
            tr(
                "success / warning / error 与操作层级组合使用，传达操作的结果或风险。",
                "success / warning / error combine with the action hierarchy to convey the outcome or the " +
                    "risk of the action.",
            ),
        )
    fun colourRow(variant: String): HTMLElement =
        row(
            createButton { textContent = "Primary" }.withTheme("primary", variant),
            createButton { textContent = "Secondary" }.withTheme("secondary", variant),
            createButton { textContent = "Tertiary" }.withTheme("tertiary", variant),
        )
    colours.body.append(
        example(tr("success", "success"), colourRow("success")),
        example(tr("warning", "warning"), colourRow("warning")),
        example(tr("error", "error"), colourRow("error")),
    )
    view.body.append(colours.root)

    // --- Buttons in a grid toolbar --------------------------------------------------------------
    val toolbar =
        section(
            tr("表格中的按钮", "Buttons in a grid toolbar"),
            tr(
                "工具栏按钮按选中行数启停：编辑类操作要求恰好选中一行，删除在有选中项时可用。",
                "Toolbar buttons depend on the selection: edit-style actions need exactly one row, delete " +
                    "is enabled as soon as something is selected.",
            ),
        )
    val users = createGrid()
    users.dataset["test"] = "button-grid"
    users.style.setProperty("height", "14rem")
    users.items = jsArray<JsAny>(BUTTON_GRID_USERS)
    users.append(
        createGridSelectionColumn { autoSelect = true },
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
    )
    val editProfile = createButton { textContent = tr("编辑资料", "Edit profile") }
    val managePermissions = createButton { textContent = tr("管理权限", "Manage permissions") }
    val resetPassword = createButton { textContent = tr("重置密码", "Reset password") }
    val deleteUser = createButton { textContent = tr("删除", "Delete") }.withTheme("error")
    val singleSelection = listOf(editProfile, managePermissions, resetPassword)
    val selectionReadout = Readout(tr("已选 0 行", "0 rows selected"))
    fun syncSelection() {
        val size = users.selectedItems.size
        singleSelection.forEach { it.disabled = size != 1 }
        deleteUser.disabled = size == 0
        selectionReadout.set(tr("已选 $size 行", "$size rows selected"))
    }
    users.listen(GridEvents.selectedItemsChanged) { syncSelection() }
    syncSelection()
    val usersTitle = element(HtmlTagName.h3).apply { textContent = tr("用户", "Users") }
    val addUser = createButton { textContent = tr("添加用户", "Add user") }
    toolbar.body.append(
        example(
            null,
            column(
                row(usersTitle, addUser),
                users,
                row(*singleSelection.toTypedArray(), deleteUser),
                selectionReadout.root,
            ),
        ),
    )
    view.body.append(toolbar.root)

    // --- Long-running action --------------------------------------------------------------------
    val longRunning =
        section(
            tr("长时操作", "Long-running action"),
            tr(
                "操作进行中禁用按钮，避免重复提交；进度条只在本地模拟，没有后端请求。",
                "The button is disabled while the action runs so it cannot be submitted twice; the progress " +
                    "bar is simulated locally, there is no backend request.",
            ),
        )
    val progress = createProgressBar { value = 0.0 }
    progress.style.setProperty("flex", "1")
    val perform = createButton { textContent = tr("执行操作", "Perform action") }.withTheme("primary")
    perform.clickEvent.addHandler {
        perform.disabled = true
        progress.value = 0.0
        startInterval(40) {
            progress.value = (progress.value + 0.04).coerceAtMost(1.0)
            if (progress.value >= 1.0) {
                stopInterval()
                perform.disabled = false
            }
        }
    }
    longRunning.body.append(example(null, row(perform, progress)))
    view.body.append(longRunning.root)

    return view
}

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

private fun buildMenuBarPage(meta: PageMeta): PageView {
    val view = page(meta)
    registerVaadinIcons()

    // --- Basic usage ----------------------------------------------------------------------------
    val basic =
        section(
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
        example(null, column(menuBar, readout.root)),
        example(tr("事件流", "Event log"), log.root),
    )
    view.body.append(basic.root)

    // --- Checkable items ------------------------------------------------------------------------
    val checkable =
        section(
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
    checkable.body.append(example(null, checkBar))
    view.body.append(checkable.root)

    // --- Dividers -------------------------------------------------------------------------------
    val dividers =
        section(
            tr("分隔线", "Dividers"),
            tr(
                "把 component 设为 \"hr\" 的条目渲染成分隔线，用来分组子菜单项。",
                "An item with component set to \"hr\" renders as a divider that groups sub-menu entries.",
            ),
        )
    dividers.body.append(
        example(
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
    view.body.append(dividers.root)

    // --- Drop-down ------------------------------------------------------------------------------
    val dropDown =
        section(
            tr("下拉菜单", "Drop-down menu"),
            tr(
                "只有一个按钮时，菜单栏就是普通的下拉菜单，常见于用户账户入口。",
                "With a single button the menu bar is a plain drop-down, the usual shape of an account menu.",
            ),
        )
    dropDown.body.append(
        example(
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
    view.body.append(dropDown.root)

    // --- Icons ----------------------------------------------------------------------------------
    val icons =
        section(
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
    iconBar.withTheme("icon")
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
    iconOnlyBar.withTheme("tertiary-inline")
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
    comboBar.withTheme("icon", "primary")
    icons.body.append(
        example(tr("图标加文字", "Icons with text"), iconBar),
        example(tr("只有图标", "Icon-only buttons"), iconOnlyBar),
        example(tr("组合按钮", "Combo buttons"), comboBar),
    )
    view.body.append(icons.root)

    // --- Custom item data -----------------------------------------------------------------------
    val customData =
        section(
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
    customData.body.append(example(null, column(copyBar, copied.root)))
    view.body.append(customData.root)

    // --- Disabled items -------------------------------------------------------------------------
    val disabled =
        section(
            tr("禁用项", "Disabled items"),
            tr(
                "disabled 可以设在顶层按钮或子菜单项上，禁用的条目不能点击。",
                "disabled works on top-level buttons and on sub-menu entries; disabled items cannot be chosen.",
            ),
        )
    disabled.body.append(
        example(
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
    view.body.append(disabled.root)

    // --- Class names ----------------------------------------------------------------------------
    val classNames =
        section(
            tr("自定义类名", "Item class names"),
            tr(
                "className 加到生成的按钮上，配合主题样式做更细的定制。",
                "className is added to the generated button for finer-grained styling.",
            ),
        )
    classNames.body.append(
        example(
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
    view.body.append(classNames.root)

    // --- Drop-down indicators -------------------------------------------------------------------
    val indicators =
        section(
            tr("展开指示器", "Drop-down indicators"),
            tr(
                "theme=\"dropdown-indicators\" 给带子菜单的按钮加上下拉箭头。",
                "theme=\"dropdown-indicators\" adds a chevron to the buttons that have a submenu.",
            ),
        )
    indicators.body.append(
        example(
            null,
            createMenuBar { items = sharedMenuItems() }.withTheme("dropdown-indicators"),
        ),
    )
    view.body.append(indicators.root)

    // --- Overflow -------------------------------------------------------------------------------
    val overflow =
        section(
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
        div("col").apply {
            textContent = tr("拖动分隔条观察溢出", "Move the splitter to see overflow")
        },
    )
    overflow.body.append(example(null, split))
    view.body.append(overflow.root)

    // --- Right-aligned --------------------------------------------------------------------------
    val rightAligned =
        section(
            tr("右对齐", "Right-aligned"),
            tr(
                "theme=\"end-aligned\" 把按钮组靠右排列，适合放在工具栏末端。",
                "theme=\"end-aligned\" pushes the buttons to the trailing edge, as in a toolbar.",
            ),
        )
    val rightBar = createMenuBar { items = sharedMenuItems() }.withTheme("end-aligned")
    rightBar.style.setProperty("width", "100%")
    rightAligned.body.append(example(null, rightBar))
    view.body.append(rightAligned.root)

    // --- Style variants -------------------------------------------------------------------------
    val styles =
        section(
            tr("样式变体", "Style variants"),
            tr(
                "顶层按钮默认使用 tertiary 外观；primary、small 与默认样式对比见下。",
                "Top-level buttons use the tertiary look by default; compare primary and small below.",
            ),
        )
    fun styleBar(label: String, vararg themes: String): HTMLElement =
        createMenuBar {
            items =
                menuItems(
                    """[{"text": "$label", "children": [{"text": "${tr("条目", "Item")}"}]}]""",
                )
        }.withTheme(*themes)
    styles.body.append(
        example(
            null,
            row(
                styleBar(tr("默认", "Default")),
                styleBar(tr("三级", "Tertiary"), "tertiary"),
                styleBar(tr("主要", "Primary"), "primary"),
                styleBar(tr("小号", "Small"), "small"),
            ),
        ),
    )
    view.body.append(styles.root)

    // --- Internationalization -------------------------------------------------------------------
    val i18n =
        section(
            tr("本地化", "Internationalization"),
            tr(
                "i18n 覆盖组件自带的文案，例如溢出按钮给屏幕阅读器读的 moreOptions。",
                "i18n overrides the built-in texts, such as moreOptions that screen readers announce for " +
                    "the overflow button.",
            ),
        )
    i18n.body.append(
        example(
            null,
            createMenuBar {
                items = sharedMenuItems()
                this.i18n = unsafeJso<MenuBarI18n> { moreOptions = tr("更多操作", "More actions") }
            },
        ),
    )
    view.body.append(i18n.root)

    // --- Tooltips -------------------------------------------------------------------------------
    val tooltips =
        section(
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
    tooltipBar.withTheme("icon")
    tooltipBar.append(createTooltip().apply { slot = "tooltip" })
    tooltips.body.append(example(null, tooltipBar))
    view.body.append(tooltips.root)

    // --- Behaviour switches ---------------------------------------------------------------------
    val behaviour =
        section(
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
        example(null, column(hoverSwitch, tabSwitch, disabledSwitch, reactiveBar, switchReadout.root)),
    )
    view.body.append(behaviour.root)

    return view
}
