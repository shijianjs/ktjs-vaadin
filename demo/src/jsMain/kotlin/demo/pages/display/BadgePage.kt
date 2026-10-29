package demo.pages.display

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.pages.overlays.ariaLabel
import demo.tr
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.Readout
import demo.ui.column
import demo.ui.example
import demo.ui.jsArray
import demo.ui.listen
import demo.ui.page
import demo.ui.row
import demo.ui.section
import demo.ui.span
import demo.ui.withTheme
import kotlin.js.JsAny
import kotlin.js.unsafeCast
import vaadin.badge.BadgeElement
import vaadin.badge.createBadge
import vaadin.button.createButton
import vaadin.combobox.ComboBoxEvents
import vaadin.combobox.createComboBox
import vaadin.grid.GridColumnElement
import vaadin.grid.createGrid
import vaadin.grid.createGridColumn
import vaadin.icon.createIcon
import vaadin.icons.registerVaadinIcons
import vaadin.tabs.createTab
import vaadin.tabs.createTabs
import web.dom.clickEvent
import web.events.addHandler
import web.html.HTMLElement

val badgeEntry: CatalogEntry =
    CatalogEntry(
        id = "badge",
        group = Groups.DISPLAY,
        meta =
            PageMeta(
                title = bi("Badge", "Badge"),
                summary = bi("数字或文本标记：number 决定显示的数字，theme 决定颜色。", "Numeric or text badge: number sets the displayed count and theme sets the colour."),
                docsUrl = "$DOCS_BASE/badge",
                sourcePath = "pages/display/BadgePage.kt",
            ),
        builder = ::buildBadgePage,
    )

// ---------------------------------------------------------------------------------------------
// Badge
// ---------------------------------------------------------------------------------------------

/** A report row of `badge-highlight.ts`; the status is one of the keys handled by [statusBadge]. */
private external interface ReportRow {
    var report: String
    var due: String
    var assignee: String
    var status: String
}

/** Local stand-in for `getReports()` of the docs backend. */
private const val SIMULATED_REPORTS =
    """
    [
      {"report": "Q3 revenue", "due": "Mar 12, 2026", "assignee": "Ada Lovelace", "status": "completed"},
      {"report": "Customer survey", "due": "Mar 18, 2026", "assignee": "Grace Hopper", "status": "in-progress"},
      {"report": "Roadmap", "due": "Apr 2, 2026", "assignee": "Margaret Hamilton", "status": "on-hold"},
      {"report": "Budget review", "due": "Apr 9, 2026", "assignee": "Frances Allen", "status": "cancelled"}
    ]
    """

/** Local stand-in for `getUserPermissions()` of the docs backend. */
private const val SIMULATED_PERMISSIONS =
    """
    [
      {"name": "Ada Lovelace", "view": true, "comment": true, "edit": true},
      {"name": "Grace Hopper", "view": true, "comment": true, "edit": false},
      {"name": "Margaret Hamilton", "view": true, "comment": false, "edit": false},
      {"name": "Frances Allen", "view": false, "comment": false, "edit": false}
    ]
    """

/** A text badge; [themes] are theme variants such as `success` or `success filled`. */
private fun textBadge(text: String, vararg themes: String): BadgeElement =
    createBadge { textContent = text }.withTheme(*themes)

/** The status badge of the reports table. */
private fun statusBadge(status: String): BadgeElement {
    val text =
        when (status) {
            "completed" -> tr("已完成", "Completed")
            "in-progress" -> tr("进行中", "In progress")
            "on-hold" -> tr("暂缓", "On hold")
            else -> tr("已取消", "Cancelled")
        }
    val themes =
        when (status) {
            "completed" -> arrayOf("success", "filled")
            "in-progress" -> arrayOf("filled")
            "on-hold" -> arrayOf("warning", "filled")
            else -> arrayOf("error", "filled")
        }
    return textBadge(text, *themes)
}

/** A boolean column of the permissions table; the field is read through [granted]. */
private fun permissionColumn(header: String, granted: (PermissionRow) -> Boolean?): GridColumnElement =
    createGridColumn {
        this.header = header
        renderer = { cell, _, model ->
            cell.replaceChildren(permissionBadge(granted(model.item.unsafeCast<PermissionRow>()) == true))
        }
    }

private fun buildBadgePage(meta: PageMeta): PageView {
    val view = page(meta)
    // `vaadin:check` and the other icons of the table examples come from @vaadin/icons.
    registerVaadinIcons()

    // --- Label (badge/badge-basic.ts) -----------------------------------------------------------
    val labelSection =
        section(
            tr("标签", "Label"),
            tr(
                "默认插槽放文本，theme 的 success / warning / error 表达状态；只给文本、不带数字时，徽标就是" +
                    "一个带底色的标签。",
                "The default slot holds the text and the success / warning / error themes express the state; " +
                    "with text only, the badge is a coloured label.",
            ),
        )
    val numeric = createBadge { number = 4.0 }
    numeric.dataset["test"] = "badge"
    labelSection.body.append(
        example(
            null,
            row(
                textBadge(tr("待处理", "Pending")),
                textBadge(tr("已确认", "Confirmed"), "success"),
                textBadge(tr("警告", "Warning"), "warning"),
                textBadge(tr("已拒绝", "Denied"), "error"),
                numeric,
            ),
        ),
    )
    view.body.append(labelSection.root)

    // --- Colour variants (badge/badge-color.ts) -----------------------------------------------
    val colour =
        section(
            tr("颜色", "Colour"),
            tr(
                "默认是浅色底；再加 filled 变体得到实心徽标，两种风格可以并排比较。",
                "The default is a tinted background; adding the filled variant gives a solid badge, so both " +
                    "styles can be compared side by side.",
            ),
        )
    colour.body.append(
        example(
            tr("默认", "Default"),
            row(
                textBadge(tr("待处理", "Pending")),
                textBadge(tr("已确认", "Confirmed"), "success"),
                textBadge(tr("警告", "Warning"), "warning"),
                textBadge(tr("已拒绝", "Denied"), "error"),
            ),
        ),
        example(
            tr("filled", "filled"),
            row(
                textBadge(tr("待处理", "Pending"), "filled"),
                textBadge(tr("已确认", "Confirmed"), "success filled"),
                textBadge(tr("警告", "Warning"), "warning filled"),
                textBadge(tr("已拒绝", "Denied"), "error filled"),
            ),
        ),
    )
    view.body.append(colour.root)

    // --- Icons (badge/badge-icons.ts) ----------------------------------------------------------
    val withIcons =
        section(
            tr("图标", "Icons"),
            tr(
                "slot=\"icon\" 在文字前面放图标；徽标的 has-icon 状态属性由插槽内容自动维护。",
                "slot=\"icon\" places an icon before the text; the has-icon state attribute follows the slot " +
                    "content automatically.",
            ),
        )
    withIcons.body.append(
        example(
            null,
            row(
                iconBadge("vaadin:clock", tr("待处理", "Pending")),
                iconBadge("vaadin:check", tr("已确认", "Confirmed"), "success"),
                iconBadge("vaadin:warning", tr("警告", "Warning"), "warning"),
                iconBadge("vaadin:exclamation-circle", tr("已拒绝", "Denied"), "error"),
            ),
        ),
    )
    view.body.append(withIcons.root)

    // --- Icon-only + icon-only table (badge-icons-only.ts, badge-icons-only-table.ts) ----------
    val iconOnly =
        section(
            tr("只有图标", "Icon-only"),
            tr(
                "theme=\"icon-only\" 隐藏文字但保留为无障碍名称；表格里用同样的徽标表示布尔值，" +
                    "官方示例的数据来自 getUserPermissions()，这里是固定数据。",
                "theme=\"icon-only\" hides the text but keeps it as the accessible name. The table uses the same " +
                    "badges for booleans; the official example loads getUserPermissions(), this is fixed data.",
            ),
        )
    val permissions = createGrid()
    permissions.style.setProperty("height", "14rem")
    permissions.items = jsArray<JsAny>(SIMULATED_PERMISSIONS)
    permissions.append(
        createGridColumn {
            header = tr("姓名", "Name")
            path = "name"
        },
        permissionColumn(tr("查看", "View")) { it.view },
        permissionColumn(tr("评论", "Comment")) { it.comment },
        permissionColumn(tr("编辑", "Edit")) { it.edit },
    )
    iconOnly.body.append(
        example(
            null,
            row(
                iconBadge("vaadin:check", tr("已确认", "Confirmed"), "success", "icon-only"),
                iconBadge("vaadin:close", tr("已取消", "Cancelled"), "error", "icon-only"),
            ),
        ),
        example(tr("权限表格", "Permissions table"), permissions),
    )
    view.body.append(iconOnly.root)

    // --- Number (badge/badge-number.ts) --------------------------------------------------------
    val numberSection =
        section(
            tr("数字", "Number"),
            tr(
                "number 在文字前显示计数；它和默认插槽的文本一起被朗读。",
                "number shows a count before the text; it is announced together with the text of the default " +
                    "slot.",
            ),
        )
    numberSection.body.append(
        example(
            null,
            row(
                createBadge {
                    number = 12.0
                    textContent = tr("新消息", "New messages")
                },
                createBadge {
                    number = 3.0
                    textContent = tr("已完成", "Completed")
                }.withTheme("success"),
                createBadge {
                    number = 1.0
                    textContent = tr("失败", "Failed")
                }.withTheme("error"),
            ),
        ),
    )
    view.body.append(numberSection.root)

    // --- Number-only (badge/badge-number-only.ts) ---------------------------------------------
    val numberOnly =
        section(
            tr("仅数字", "Number-only"),
            tr(
                "theme=\"number-only\" 只显示数字，文本仍是可访问名称——适合放在 tab 或图标旁边。",
                "theme=\"number-only\" shows the number alone while the text stays the accessible name — a good " +
                    "fit next to tabs and icons.",
            ),
        )
    numberOnly.body.append(
        example(
            null,
            row(
                createBadge {
                    number = 12.0
                    textContent = tr("新消息", "New messages")
                }.withTheme("number-only"),
                createBadge {
                    number = 3.0
                    textContent = tr("提醒", "Alerts")
                }.withTheme("error", "number-only"),
            ),
        ),
    )
    view.body.append(numberOnly.root)

    // --- Highlighting information (badge/badge-highlight.ts) ----------------------------------
    val highlight =
        section(
            tr("区分信息", "Highlighting information"),
            tr(
                "徽标最适合把状态列变成一眼可读的标记；官方示例的列渲染器用 Lit，这里用 renderer lambda " +
                    "直接构造 vaadin-badge。",
                "Badges turn a status column into something readable at a glance; the official column renderer " +
                    "uses Lit, here a renderer lambda builds the vaadin-badge directly.",
            ),
        )
    val reports = createGrid()
    reports.style.setProperty("height", "14rem")
    reports.items = jsArray<JsAny>(SIMULATED_REPORTS)
    reports.append(
        createGridColumn {
            header = tr("报告", "Report")
            path = "report"
        },
        createGridColumn {
            header = tr("截止日期", "Due date")
            path = "due"
        },
        createGridColumn {
            header = tr("负责人", "Assignee")
            path = "assignee"
        },
        createGridColumn {
            header = tr("状态", "Status")
            renderer = { cell, _, model -> cell.replaceChildren(statusBadge(model.item.unsafeCast<ReportRow>().status)) }
        },
    )
    highlight.body.append(example(null, reports))
    view.body.append(highlight.root)

    // --- Interactive content (badge/badge-interactive.ts) -------------------------------------
    val interactive =
        section(
            tr("可交互内容", "Interactive content"),
            tr(
                "徽标里可以放按钮：选中职业后加一个带清除按钮的徽标。官方示例的职业列表来自后端，这里用固定" +
                    "列表模拟。",
                "A badge can hold a button: picking a profession adds a badge with a clear button. The official " +
                    "example loads the professions from the backend, a fixed list stands in here.",
            ),
        )
    val professions =
        listOf(
            tr("工程师", "Engineer"),
            tr("设计师", "Designer"),
            tr("产品经理", "Product manager"),
            tr("研究员", "Researcher"),
        )
    val chips = row()
    val chipsByProfession = mutableMapOf<String, HTMLElement>()
    val filterReadout = Readout(tr("没有筛选条件", "No filters"))
    val selectedProfessions = mutableListOf<String>()

    fun syncFilters() {
        filterReadout.set(
            if (selectedProfessions.isEmpty()) {
                tr("没有筛选条件", "No filters")
            } else {
                tr("筛选：", "Filters: ") + selectedProfessions.joinToString(" · ")
            },
        )
    }

    fun removeProfession(profession: String) {
        val chip = chipsByProfession.remove(profession) ?: return
        chip.remove()
        selectedProfessions.remove(profession)
        syncFilters()
    }

    fun addProfession(profession: String) {
        if (chipsByProfession.containsKey(profession)) return
        selectedProfessions.add(profession)
        val clear = createButton().withTheme("tertiary")
        clear.ariaLabel = tr("清除筛选：$profession", "Clear filter: $profession")
        clear.title = tr("清除筛选：$profession", "Clear filter: $profession")
        clear.style.setProperty("height", "1.5rem")
        clear.style.setProperty("min-width", "1.5rem")
        clear.style.setProperty("margin", "0")
        clear.style.setProperty("padding", "0")
        clear.append(createIcon { icon = "vaadin:close" })
        clear.clickEvent.addHandler { removeProfession(profession) }
        val chip = createBadge()
        chip.style.setProperty("padding-right", "0")
        chip.append(span(profession), clear)
        chipsByProfession[profession] = chip
        chips.append(chip)
        syncFilters()
    }

    val professionCombo =
        createComboBox {
            label = tr("职业", "Profession")
            items = jsArray<JsAny>(professions.joinToString(",", "[", "]") { "\"$it\"" })
        }
    professionCombo.listen(ComboBoxEvents.change) {
        val item = professionCombo.selectedItem ?: return@listen
        addProfession(item.unsafeCast<String>())
    }
    interactive.body.append(example(null, column(professionCombo, chips, filterReadout.root)))
    view.body.append(interactive.root)

    // --- Counter (badge/badge-counter.ts) -----------------------------------------------------
    val counter =
        section(
            tr("计数器", "Counter"),
            tr(
                "选项卡里的 number-only 徽标是未读计数；文字部分是读屏使用的说明。",
                "Number-only badges inside tabs show unread counts; the text part is what screen readers " +
                    "announce.",
            ),
        )
    val tabs = createTabs()
    fun counterTab(label: String, count: Double): HTMLElement =
        createTab().apply {
            append(span(label))
            append(
                createBadge {
                    number = count
                    textContent = tr("未读消息", "unread messages")
                }.withTheme("filled", "number-only"),
            )
        }
    tabs.append(
        counterTab(tr("收件箱", "Inbox"), 12.0),
        counterTab(tr("重要", "Important"), 3.0),
        counterTab(tr("垃圾邮件", "Spam"), 45.0),
        counterTab(tr("归档", "Archive"), 23.0),
    )
    counter.body.append(example(null, tabs))
    view.body.append(counter.root)

    // --- Dot (badge/badge-dot.ts) -------------------------------------------------------------
    val dot =
        section(
            tr("圆点", "Dot"),
            tr(
                "theme=\"dot\" 只显示一个圆点（数字与文本都隐藏），适合表达「有新内容」；也可以放进按钮的" +
                    "suffix 插槽，用绝对定位压在图标角上。",
                "theme=\"dot\" shows a single dot (number and text are hidden), which is enough to say \"there " +
                    "is something new\". It can also sit in the suffix slot of a button, absolutely positioned " +
                    "over the icon.",
            ),
        )
    val download = createButton().withTheme("icon")
    download.ariaLabel = tr("下载", "Downloads")
    download.append(createIcon { icon = "lumo:download" })
    val completed = createBadge {
        number = 3.0
        textContent = tr("已完成", "completed")
    }.withTheme("dot", "success")
    completed.slot = "suffix"
    completed.style.setProperty("position", "absolute")
    completed.style.setProperty("top", "0.3em")
    completed.style.setProperty("right", "0.3em")
    download.append(completed)
    dot.body.append(
        example(
            null,
            row(
                textBadge(tr("待处理", "Pending"), "dot"),
                textBadge(tr("已确认", "Confirmed"), "dot", "success"),
                textBadge(tr("警告", "Warning"), "dot", "warning"),
                textBadge(tr("已拒绝", "Denied"), "dot", "error"),
                download,
            ),
        ),
    )
    view.body.append(dot.root)

    // --- Live number (wrapper extra, exercised by the end-to-end suite) ------------------------
    val live =
        section(
            tr("动态更新", "Live updates"),
            tr(
                "number 可以随时修改，徽标立即更新；计数为 0 时仍然显示（与「不显示」用 hidden 属性区分）。",
                "number can change at any time and the badge updates immediately; a count of 0 stays visible " +
                    "(use the hidden attribute to hide the badge instead).",
            ),
        )
    val liveBadge = createBadge { number = 0.0 }
    liveBadge.dataset["test"] = "badge-live"
    val addOne =
        createButton { textContent = tr("加一", "Add one") }.withTheme("tertiary").apply {
            clickEvent.addHandler { liveBadge.number += 1.0 }
        }
    live.body.append(example(null, row(liveBadge, addOne)))
    view.body.append(live.root)

    return view
}
