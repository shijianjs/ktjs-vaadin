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
import demo.ui.listen
import demo.ui.page
import demo.ui.paragraph
import demo.ui.row
import demo.ui.section
import demo.ui.withTheme
import kotlin.js.unsafeCast
import vaadin.button.ButtonElement
import vaadin.button.createButton
import vaadin.icon.createIcon
import vaadin.menubar.MenuBarEvents
import vaadin.menubar.MenuBarItem
import vaadin.menubar.createMenuBar
import vaadin.switch.createSwitch
import demo.ui.detailText
import demo.ui.jsObject
import demo.tr
import web.dom.clickEvent
import web.events.addHandler
import web.html.HTMLElement

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

private fun buildButtonPage(meta: PageMeta): PageView {
    val view = page(meta)

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
        example(null, saveButton, clicks.root),
        example(tr("事件流", "Event log"), log.root),
    )
    view.body.append(basic.root)

    // --- Theme variants -------------------------------------------------------------------------
    val variants =
        section(
            tr("主题变体", "Theme variants"),
            tr(
                "theme 属性接受空格分隔的多个变体，切换后立即按 Lumo 主题重新着色。",
                "The theme property accepts several space-separated variants and recolours the button " +
                    "according to the Lumo theme immediately.",
            ),
        )
    val variantLog = EventLog(4)
    val variantRow =
        row(
            *listOf(
                tr("默认", "Default") to listOf<String>(),
                "primary" to listOf("primary"),
                "tertiary" to listOf("tertiary"),
                "tertiary-inline" to listOf("tertiary-inline"),
                "contrast" to listOf("contrast"),
                "success" to listOf("success"),
                "error" to listOf("error"),
            ).map { (label, themes) ->
                createButton { textContent = label }.withTheme(*themes.toTypedArray()).apply {
                    clickEvent.addHandler { variantLog.add("click · $label") }
                }
            }.toTypedArray(),
        )
    variants.body.append(example(null, variantRow), example(tr("最近的事件", "Recent events"), variantLog.root))
    view.body.append(variants.root)

    // --- Sizes and icons ------------------------------------------------------------------------
    val sizing =
        section(
            tr("尺寸与图标", "Sizes and icons"),
            tr(
                "small / large 改变内边距与字号；icon 只保留图标；prefix / suffix 插槽放任意节点。",
                "small / large change the padding and font size; icon keeps only the icon; the " +
                    "prefix / suffix slots hold any node.",
            ),
        )
    val withPrefix =
        createButton { textContent = tr("新建", "New") }
    withPrefix.append(createIcon { icon = "lumo:plus" }.apply { slot = "prefix" })
    val withSuffix =
        createButton { textContent = tr("下载", "Download") }
    withSuffix.append(createIcon { icon = "lumo:download" }.apply { slot = "suffix" })
    val iconOnly = createButton()
    iconOnly.withTheme("icon")
    iconOnly.append(createIcon { icon = "lumo:search" })
    iconOnly.ariaLabel = tr("搜索", "Search")

    sizing.body.append(
        example(
            null,
            row(
                createButton { textContent = "small" }.withTheme("small"),
                createButton { textContent = tr("默认尺寸", "Default size") },
                createButton { textContent = "large" }.withTheme("large"),
            ),
        ),
        example(
            null,
            row(
                withPrefix,
                withSuffix,
                iconOnly,
                createButton { textContent = tr("primary + 图标", "primary + icon") }
                    .withTheme("primary")
                    .apply { append(createIcon { icon = "lumo:checkmark" }.apply { slot = "prefix" }) },
            ),
        ),
    )
    view.body.append(sizing.root)

    // --- Disabled state -------------------------------------------------------------------------
    val states =
        section(
            tr("禁用与状态", "Disabled state"),
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

    states.body.append(
        example(
            null,
            column(
                row(
                    target,
                    createButton { textContent = tr("永远禁用", "Always disabled") }.apply { disabled = true },
                    createButton { textContent = tr("禁用的 primary", "Disabled primary") }
                        .withTheme("primary")
                        .apply { disabled = true },
                ),
                row(toggleButton, stateReadout.root),
            ),
        ),
    )
    view.body.append(states.root)

    return view
}

private fun buildMenuBarPage(meta: PageMeta): PageView {
    val view = page(meta)

    val menubarItems: Array<MenuBarItem> =
        jsObject(
            """
            [
              {"text": "${tr("文件", "File")}", "children": [
                {"text": "${tr("新建", "New")}"},
                {"text": "${tr("打开", "Open")}"},
                {"text": "${tr("另存为", "Save as")}", "disabled": true}
              ]},
              {"text": "${tr("编辑", "Edit")}", "children": [
                {"text": "${tr("撤销", "Undo")}"},
                {"text": "${tr("重做", "Redo")}"},
                {"text": "${tr("查找", "Find")}"}
              ]},
              {"text": "${tr("视图", "View")}", "children": [
                {"text": "${tr("紧凑模式", "Compact mode")}", "checked": true},
                {"text": "${tr("行条纹", "Row stripes")}"}
              ]},
              {"text": "${tr("帮助", "Help")}"}
            ]
            """.trimIndent(),
        ).unsafeCast<Array<MenuBarItem>>()

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
    val menuBar = createMenuBar { items = menubarItems }
    menuBar.dataset["test"] = "menu-bar"
    menuBar.listen(MenuBarEvents.itemSelected) { event ->
        val value = detailText(event)
        readout.set(tr("已选择：", "Selected: "), value)
        log.add("item-selected · $value")
    }
    basic.body.append(
        example(null, column(menuBar, readout.root)),
        example(tr("事件流", "Event log"), log.root),
    )
    view.body.append(basic.root)

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

    val reactiveBar = createMenuBar { items = menubarItems }
    val sync = {
        reactiveBar.openOnHover = hoverSwitch.checked
        reactiveBar.tabNavigation = tabSwitch.checked
        reactiveBar.disabled = disabledSwitch.checked
        switchReadout.set(
            "openOnHover=${hoverSwitch.checked} · tabNavigation=${tabSwitch.checked} · " +
                "disabled=${disabledSwitch.checked}",
        )
    }
    hoverSwitch.listen(vaadin.switch.SwitchEvents.checkedChanged) { sync() }
    tabSwitch.listen(vaadin.switch.SwitchEvents.checkedChanged) { sync() }
    disabledSwitch.listen(vaadin.switch.SwitchEvents.checkedChanged) { sync() }
    sync()

    behaviour.body.append(
        example(null, column(hoverSwitch, tabSwitch, disabledSwitch, reactiveBar, switchReadout.root)),
    )
    view.body.append(behaviour.root)

    return view
}
