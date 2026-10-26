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
                    title = "Button",
                    summary = "可点击的按钮：主题变体、尺寸、图标插槽、禁用状态与点击事件。",
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
                    title = "Menu Bar",
                    summary = "横向菜单栏，支持子菜单、禁用项、悬停展开与键盘导航。",
                    docsUrl = "$DOCS_BASE/menu-bar",
                    sourcePath = "pages/Buttons.kt",
                ),
            builder = ::buildMenuBarPage,
        ),
    )

private fun buildButtonPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    // --- 基本用法 -------------------------------------------------------------------------------
    val basic = section("基本用法", "点击按钮触发 click 事件，页面把次数和事件流写在下面。")
    val clicks = Readout("点击次数：0")
    val log = EventLog()
    var count = 0
    val saveButton = createButton { textContent = "保存更改" }
    saveButton.dataset["test"] = "basic-button"
    saveButton.clickEvent.addHandler {
        count++
        clicks.set("点击次数：$count")
        log.add("click · 第 $count 次")
    }
    basic.body.append(
        example(null, saveButton, clicks.root),
        example("事件流", log.root),
    )
    view.body.append(basic.root)

    // --- 主题变体 -------------------------------------------------------------------------------
    val variants = section("主题变体", "theme 属性接受空格分隔的多个变体，切换后立即按 Lumo 主题重新着色。")
    val variantLog = EventLog(4)
    val variantRow =
        row(
            *listOf(
                "默认" to listOf<String>(),
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
    variants.body.append(example(null, variantRow), example("最近的事件", variantLog.root))
    view.body.append(variants.root)

    // --- 尺寸与图标 -----------------------------------------------------------------------------
    val sizing = section("尺寸与图标", "small / large 改变内边距与字号；icon 只保留图标；prefix / suffix 插槽放任意节点。")
    val withPrefix =
        createButton { textContent = "新建" }
    withPrefix.append(createIcon { icon = "lumo:plus" }.apply { slot = "prefix" })
    val withSuffix =
        createButton { textContent = "下载" }
    withSuffix.append(createIcon { icon = "lumo:download" }.apply { slot = "suffix" })
    val iconOnly = createButton()
    iconOnly.withTheme("icon")
    iconOnly.append(createIcon { icon = "lumo:search" })
    iconOnly.ariaLabel = "搜索"

    sizing.body.append(
        example(
            null,
            row(
                createButton { textContent = "small" }.withTheme("small"),
                createButton { textContent = "默认尺寸" },
                createButton { textContent = "large" }.withTheme("large"),
            ),
        ),
        example(
            null,
            row(
                withPrefix,
                withSuffix,
                iconOnly,
                createButton { textContent = "primary + 图标" }
                    .withTheme("primary")
                    .apply { append(createIcon { icon = "lumo:checkmark" }.apply { slot = "prefix" }) },
            ),
        ),
    )
    view.body.append(sizing.root)

    // --- 禁用与状态 -----------------------------------------------------------------------------
    val states = section("禁用与状态", "禁用按钮不响应点击，也不会提交表单；下面可以实时切换。")
    val target = createButton { textContent = "启用中的按钮" }.withTheme("primary")
    val stateReadout = Readout("当前状态：启用")
    target.clickEvent.addHandler { stateReadout.set("当前状态：启用，刚刚被点击") }
    val toggleButton =
        createButton { textContent = "切换到禁用" }.apply {
            clickEvent.addHandler {
                target.disabled = !target.disabled
                textContent = if (target.disabled) "切换到启用" else "切换到禁用"
                stateReadout.set("当前状态：" + if (target.disabled) "禁用" else "启用")
            }
        }

    states.body.append(
        example(
            null,
            column(
                row(
                    target,
                    createButton { textContent = "永远禁用" }.apply { disabled = true },
                    createButton { textContent = "禁用的 primary" }.withTheme("primary").apply { disabled = true },
                ),
                row(toggleButton, stateReadout.root),
            ),
        ),
    )
    view.body.append(states.root)

    return view.root
}

private fun buildMenuBarPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val menubarItems: Array<MenuBarItem> =
        jsObject(
            """
            [
              {"text": "文件", "children": [
                {"text": "新建"},
                {"text": "打开"},
                {"text": "另存为", "disabled": true}
              ]},
              {"text": "编辑", "children": [
                {"text": "撤销"},
                {"text": "重做"},
                {"text": "查找"}
              ]},
              {"text": "视图", "children": [
                {"text": "紧凑模式", "checked": true},
                {"text": "行条纹"}
              ]},
              {"text": "帮助"}
            ]
            """.trimIndent(),
        ).unsafeCast<Array<MenuBarItem>>()

    // --- 基本用法 -------------------------------------------------------------------------------
    val basic = section("基本用法", "items 接受树形结构；打开子菜单后选择一项会触发 item-selected。")
    val log = EventLog(6)
    val readout = Readout("尚未选择菜单项")
    val menuBar = createMenuBar { items = menubarItems }
    menuBar.dataset["test"] = "menu-bar"
    menuBar.listen(MenuBarEvents.itemSelected) { event ->
        val value = detailText(event)
        readout.set("已选择：", value)
        log.add("item-selected · $value")
    }
    basic.body.append(example(null, column(menuBar, readout.root)), example("事件流", log.root))
    view.body.append(basic.root)

    // --- 行为开关 -------------------------------------------------------------------------------
    val behaviour = section("行为开关", "openOnHover 决定子菜单是否悬停展开，tabNavigation 让 Tab 键在顶层项之间移动。")
    val hoverSwitch = createSwitch { label = "openOnHover（悬停展开）" }
    hoverSwitch.checked = false
    val tabSwitch = createSwitch { label = "tabNavigation（Tab 键导航）" }
    tabSwitch.checked = true
    val disabledSwitch = createSwitch { label = "disabled（整体禁用）" }
    val switchReadout = Readout("openOnHover=false · tabNavigation=true · disabled=false")

    val reactiveBar = createMenuBar { items = menubarItems }
    val sync = {
        reactiveBar.openOnHover = hoverSwitch.checked
        reactiveBar.tabNavigation = tabSwitch.checked
        reactiveBar.disabled = disabledSwitch.checked
        switchReadout.set(
            "openOnHover=${hoverSwitch.checked} · tabNavigation=${tabSwitch.checked} · disabled=${disabledSwitch.checked}",
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

    return view.root
}
