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
import demo.ui.span
import demo.ui.withTheme
import js.objects.unsafeJso
import kotlin.js.JsAny
import kotlin.js.js
import kotlin.js.unsafeCast
import vaadin.avatar.createAvatar
import vaadin.button.createButton
import vaadin.contextmenu.ContextMenuEvents
import vaadin.contextmenu.ContextMenuItemData
import vaadin.contextmenu.HTMLElementOrString
import vaadin.contextmenu.createContextMenu
import vaadin.contextmenu.createContextMenuItem
import vaadin.grid.createGrid
import vaadin.grid.createGridColumn
import vaadin.icon.createIcon
import vaadin.menubar.MenuBarItem
import vaadin.menubar.createMenuBar
import vaadin.tooltip.createTooltip

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
        builder = ::buildContextMenuPage,
    )

private fun copyMenuItem(text: String, value: String): CopyMenuItem =
    unsafeJso<CopyMenuItem> {
        this.text = text
        this.value = value
    }

// endregion

// region Context menu

private fun buildContextMenuPage(meta: PageMeta): PageView {
    val view = page(meta)

    val items: Array<ContextMenuItemData> =
        jsObject(
            """
            [
              {"text": "${tr("查看", "View")}"},
              {"text": "${tr("复制", "Copy")}"},
              {"text": "${tr("粘贴", "Paste")}", "disabled": true},
              {"text": "${tr("更多", "More")}", "children": [
                {"text": "${tr("导出为 CSV", "Export as CSV")}"},
                {"text": "${tr("导出为 JSON", "Export as JSON")}"}
              ]}
            ]
            """.trimIndent(),
        ).unsafeCast<Array<ContextMenuItemData>>()

    val basic =
        section(
            tr("右键打开", "Open on right-click"),
            tr(
                "openOn 默认是 contextmenu；listenOn 指向接收事件的元素。",
                "openOn defaults to contextmenu; listenOn points at the element that receives the event.",
            ),
        )
    val log = EventLog(5)
    val surface = div("example").apply { textContent = tr("在这个区域点击右键", "Right-click in this area") }
    surface.dataset["test"] = "context-menu-surface"
    val menu = createContextMenu()
    menu.openOn = "contextmenu"
    menu.listenOn = surface
    menu.items = items
    menu.listen(ContextMenuEvents.itemSelected) { log.add("item-selected") }
    menu.listen(ContextMenuEvents.openedChanged) { log.add("opened-changed · ${menu.opened}") }
    basic.body.append(example(null, column(surface, log.root, menu)))
    view.body.append(basic.root)

    val gridSection =
        section(
            tr("表格行菜单", "Menu on grid rows"),
            tr(
                "把 grid 放进 context-menu，右键任意一行都能对这个区域设置菜单。",
                "Put a grid inside a context-menu; right-clicking any row opens the menu for that area.",
            ),
        )
    val rowMenu = createContextMenu()
    rowMenu.items =
        jsObject(
            """
            [
              {"text": "${tr("查看", "View")}"},
              {"text": "${tr("编辑", "Edit")}"},
              {"text": "${tr("删除", "Delete")}"}
            ]
            """.trimIndent(),
        ).unsafeCast<Array<ContextMenuItemData>>()
    val rowGrid = personGrid(people(5))
    rowMenu.append(rowGrid)
    gridSection.body.append(example(null, rowMenu))
    view.body.append(gridSection.root)

    val checkable =
        section(
            tr("勾选条目", "Checkable items"),
            tr(
                "items 里的 checked 表示当前选中项；item-selected 里更新后在界面上回读。",
                "checked marks the current item; the selection is read back after item-selected updates it.",
            ),
        )
    val assignees =
        mutableListOf(
            unsafeJso<ContextMenuItemData> { text = "Abigail Lewis"; checked = true },
            unsafeJso<ContextMenuItemData> { text = "Allison Torres"; checked = false },
            unsafeJso<ContextMenuItemData> { text = "Anna Myers"; checked = false },
            unsafeJso<ContextMenuItemData> { text = "Lauren Wright"; checked = false },
        )
    val assigneeReadout = Readout(tr("负责人：Abigail Lewis", "Assignee: Abigail Lewis"))
    val assigneeMenu = createContextMenu()
    assigneeMenu.items = assignees.toTypedArray()
    val assigneeLabel = span(tr("负责人：", "Assignee: "))
    val assigneeValue = span("Abigail Lewis")
    assigneeLabel.append(assigneeValue)
    assigneeMenu.append(assigneeLabel)
    assigneeMenu.listen(ContextMenuEvents.itemSelected) {
        val chosen = js("(event) => event.detail.value.text")(it.unsafeCast<Any>()) as String
        assignees.forEach { item -> item.checked = item.text == chosen }
        assigneeValue.textContent = chosen
        assigneeReadout.set(tr("负责人：", "Assignee: "), chosen)
    }
    checkable.body.append(example(null, column(assigneeMenu, assigneeReadout.root)))
    view.body.append(checkable.root)

    val classNames =
        section(
            tr("自定义类名", "Custom class name"),
            tr(
                "className 加到条目元素上，配合页面样式表达危险操作。",
                "className lands on the item element, which page styles can use for destructive actions.",
            ),
        )
    val classMenu = createContextMenu { openOn = "click" }
    classMenu.items =
        jsObject(
            """
            [
              {"text": "${tr("分享", "Share")}"},
              {"text": "${tr("复制副本", "Duplicate")}"},
              {"text": "${tr("删除", "Delete")}", "className": "custom"}
            ]
            """.trimIndent(),
        ).unsafeCast<Array<ContextMenuItemData>>()
    classMenu.append(createButton { textContent = tr("操作", "Actions") }.withTheme("tertiary"))
    classNames.body.append(example(null, classMenu))
    view.body.append(classNames.root)

    val customData =
        section(
            tr("自定义条目数据", "Custom item data"),
            tr(
                "条目可以带任意字段；item-selected 的 detail.value 就是被点击的条目对象。",
                "An item can carry any extra fields; detail.value of item-selected is the clicked item object.",
            ),
        )
    val copyReadout = Readout(tr("尚未复制", "Nothing copied yet"))
    val copyMenu = createContextMenu()
    // `items` takes the closed wrapper type; the generic extra data arrives through the subtype.
    copyMenu.items =
        arrayOf(
            copyMenuItem(tr("复制为纯文本", "Copy as plain text"), "Context Menu"),
            copyMenuItem(tr("复制为 HTML", "Copy as HTML"), "<h1>Context Menu</h1>"),
            copyMenuItem(tr("复制为 Markdown", "Copy as Markdown"), "# Context Menu"),
        ).unsafeCast<Array<ContextMenuItemData>>()
    copyMenu.append(column(heading(2, "Context Menu"), paragraph(tr("右键这段文字复制它的格式。", "Right-click this text to copy it in a format."))))
    copyMenu.listen(ContextMenuEvents.itemSelected) {
        val value = js("(event) => event.detail.value.value")(it.unsafeCast<JsAny>()) as String?
        copyReadout.set(tr("已复制：", "Copied: "), value ?: "")
    }
    customData.body.append(example(null, column(copyMenu, copyReadout.root)))
    view.body.append(customData.root)

    val dividers =
        section(
            tr("分隔线与层级", "Dividers and hierarchy"),
            tr(
                "component 设成 \"hr\" 的条目渲染成分隔线；children 生成子菜单。",
                "An item whose component is \"hr\" renders a divider; children open a submenu.",
            ),
        )
    val dividerMenu = createContextMenu()
    dividerMenu.items =
        jsObject(
            """
            [
              {"text": "${tr("预览", "Preview")}"},
              {"text": "${tr("编辑", "Edit")}"},
              {"component": "hr"},
              {"text": "${tr("导出", "Export")}", "children": [
                {"text": "Portable Document Format (.pdf)"},
                {"text": "Rich Text Format (.rtf)"},
                {"text": "Plain text (.txt)"}
              ]},
              {"text": "${tr("分享", "Share")}", "children": [
                {"text": "${tr("复制链接", "Copy link")}"},
                {"text": "${tr("邮件", "Email")}"}
              ]},
              {"component": "hr"},
              {"text": "${tr("删除", "Delete")}", "disabled": true}
            ]
            """.trimIndent(),
        ).unsafeCast<Array<ContextMenuItemData>>()
    val dividerGrid = personGrid(people(4))
    dividerMenu.append(dividerGrid)
    dividers.body.append(example(null, dividerMenu))
    view.body.append(dividers.root)

    val leftClick =
        section(
            tr("左键打开", "Open on click"),
            tr(
                "openOn 设为 click 时用左键展开，适合做「操作」按钮。",
                "With openOn set to click the menu opens on a left click, which suits an actions button.",
            ),
        )
    val actionsButton = createButton { textContent = tr("操作", "Actions") }.withTheme("tertiary")
    val clickMenu = createContextMenu()
    clickMenu.openOn = "click"
    clickMenu.listenOn = actionsButton
    clickMenu.items = items
    leftClick.body.append(example(null, row(actionsButton, clickMenu)))
    view.body.append(leftClick.root)

    val customItems =
        section(
            tr("自定义条目呈现", "Custom item rendering"),
            tr(
                "component 接受元素，可以直接放图标和头像。",
                "component takes an element, so icons and avatars can be used directly.",
            ),
        )
    fun menuItem(iconName: String, label: String): ContextMenuItemData =
        unsafeJso<ContextMenuItemData> {
            component =
                HTMLElementOrString(
                    createContextMenuItem().apply {
                        append(createIcon { icon = iconName }.also { iconElement ->
                            iconElement.style.setProperty("color", "var(--vaadin-text-color-secondary)")
                            iconElement.style.setProperty("margin-inline-end", "var(--vaadin-gap-s)")
                        })
                        append(span(label))
                    },
                )
        }
    val presentMenu = createContextMenu()
    presentMenu.items =
        arrayOf(
            menuItem("vaadin:file-search", tr("打开", "Open")),
            unsafeJso<ContextMenuItemData> {
                component =
                    HTMLElementOrString(
                        createContextMenuItem().apply {
                            append(createIcon { icon = "vaadin:user-check" })
                            append(span(tr("指派", "Assign")))
                        },
                    )
                children =
                    people(3)
                        .map { person ->
                            unsafeJso<ContextMenuItemData> {
                                component =
                                    HTMLElementOrString(
                                        createContextMenuItem().apply {
                                            append(
                                                createAvatar {
                                                    img = person.pictureUrl
                                                    name = person.displayName
                                                },
                                            )
                                            append(span(person.displayName))
                                        },
                                    )
                            }
                        }
                        .toTypedArray()
            },
            unsafeJso<ContextMenuItemData> { component = HTMLElementOrString("hr") },
            menuItem("vaadin:trash", tr("删除", "Delete")),
        )
    val presentGrid = personGrid(people(4))
    presentMenu.append(presentGrid)
    customItems.body.append(example(null, presentMenu))
    view.body.append(customItems.root)

    val tooltips =
        section(
            tr("条目提示", "Item tooltips"),
            tr(
                "条目数据里的 tooltip / tooltipPosition 配合 context-menu 里的 tooltip 插槽生效。",
                "The tooltip / tooltipPosition fields of the items work together with the tooltip slot of the " +
                    "context menu.",
            ),
        )
    val tooltipMenu = createContextMenu()
    tooltipMenu.items =
        jsObject(
            """
            [
              {"text": "${tr("编辑", "Edit")}", "tooltip": "${tr("编辑选中的人", "Edit selected person")}"},
              {"text": "${tr("分享", "Share")}", "children": [
                {"text": "${tr("复制链接", "Copy link")}", "tooltip": "${tr("把可分享的链接复制到剪贴板", "Copy a shareable link to the clipboard")}"},
                {"text": "${tr("邮件", "Email")}", "tooltip": "${tr("用邮件发送联系人信息", "Send the contact details by email")}", "tooltipPosition": "end"}
              ]}
            ]
            """.trimIndent(),
        ).unsafeCast<Array<ContextMenuItemData>>()
    tooltipMenu.append(createTooltip().also { it.slot = "tooltip" })
    val tooltipGrid = personGrid(people(4))
    tooltipMenu.append(tooltipGrid)
    tooltips.body.append(example(null, tooltipMenu))
    view.body.append(tooltips.root)

    val bestPractice =
        section(
            tr("表格中的操作", "Actions inside a grid"),
            tr(
                "行内操作按钮用 Menu Bar 表达，同一个 items 数组既服务右键菜单也服务行内按钮。",
                "An inline actions button uses a Menu Bar; the same items array drives both the context menu " +
                    "and the inline button.",
            ),
        )
    val fileItems: Array<ContextMenuItemData> =
        jsObject(
            """
            [
              {"text": "${tr("查看", "View")}"},
              {"text": "${tr("编辑", "Edit")}"},
              {"text": "${tr("删除", "Delete")}"}
            ]
            """.trimIndent(),
        ).unsafeCast<Array<ContextMenuItemData>>()
    val filesMenu = createContextMenu()
    filesMenu.items = fileItems
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
        createGridColumn {
            header = tr("文件", "File")
            path = "name"
        },
        createGridColumn {
            header = tr("大小", "Size")
            path = "size"
            width = "6rem"
        },
        createGridColumn {
            width = "4rem"
            flexGrow = 0.0
            renderer = { container, _, _ ->
                val menuBar = createMenuBar { withTheme("tertiary") }
                menuBar.items = fileItems.unsafeCast<Array<MenuBarItem>>()
                container.append(menuBar)
            }
        },
    )
    filesMenu.append(filesGrid)
    bestPractice.body.append(example(null, filesMenu))
    view.body.append(bestPractice.root)

    return view
}

// endregion
