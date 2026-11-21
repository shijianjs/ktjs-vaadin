package demo.pages.display

import io.github.shijianjs.ktjs.demo.page.Blocks.row as kitRow
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.page.Blocks.col as kitCol
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import web.html.HtmlTagName
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import demo.CatalogEntry
import demo.Catalog
import demo.Groups
import demo.bi
import demo.ui.PageFrame
import demo.ui.PageMeta
import demo.ui.Readout
import demo.ui.jsArray
import demo.ui.listen
import kotlin.js.JsAny
import kotlin.js.unsafeCast
import io.github.shijianjs.ktjs.vaadin.badge.BadgeElement
import io.github.shijianjs.ktjs.vaadin.badge.createBadge
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.combobox.ComboBoxEvents
import io.github.shijianjs.ktjs.vaadin.combobox.createComboBox
import io.github.shijianjs.ktjs.vaadin.grid.GridColumnElement
import io.github.shijianjs.ktjs.vaadin.grid.createGrid
import io.github.shijianjs.ktjs.vaadin.grid.createGridColumn
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.vaadin.icons.registerVaadinIcons
import io.github.shijianjs.ktjs.vaadin.tabs.createTab
import io.github.shijianjs.ktjs.vaadin.tabs.createTabs
import web.dom.clickEvent
import web.events.addHandler
import web.html.HTMLElement
import demo.pages.overlays.ariaLabel

// ---------------------------------------------------------------------------------------------
// Badge
// ---------------------------------------------------------------------------------------------

/** A report row of `badge-highlight.ts`; the status is one of the keys handled by [BadgePage.statusBadge]. */
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
    createBadge { textContent = text }.addTheme(*themes)

/** An icon badge; the icon sits in the `icon` slot and the label in a span, so the label can be rebound. */
private fun iconBadge(icon: String, vararg themes: String): BadgeElement =
    createBadge().addTheme(*themes).also { badge ->
        badge.append(createIcon { this.icon = icon }.also { it.slot = "icon" })
        badge.append(HtmlTagName.span())
    }

/** The span an [iconBadge] keeps its label in. */
private fun BadgeElement.labelSpan(): HTMLElement = children.item(1) as HTMLElement

/** The four professions the interactive example offers, in the order they appear. */
private val PROFESSIONS = listOf("engineer", "designer", "product-manager", "researcher")

private val english: BadgeTexts =
    BadgeTexts(
        labelHeading = "Label",
        labelHint = "The default slot holds the text and the success / warning / error themes express the " +
            "state; with text only, the badge is a coloured label.",
        pending = "Pending",
        confirmed = "Confirmed",
        warning = "Warning",
        denied = "Denied",
        colourHeading = "Colour",
        colourHint = "The default is a tinted background; adding the filled variant gives a solid badge, so " +
            "both styles can be compared side by side.",
        defaultCaption = "Default",
        filledCaption = "filled",
        iconsHeading = "Icons",
        iconsHint = "slot=\"icon\" places an icon before the text; the has-icon state attribute follows the " +
            "slot content automatically.",
        iconOnlyHeading = "Icon-only",
        iconOnlyHint = "theme=\"icon-only\" hides the text but keeps it as the accessible name. The table uses " +
            "the same badges for booleans; the official example loads getUserPermissions(), this is fixed data.",
        name = "Name",
        view = "View",
        comment = "Comment",
        edit = "Edit",
        cancelled = "Cancelled",
        permissionsCaption = "Permissions table",
        numberHeading = "Number",
        numberHint = "number shows a count before the text; it is announced together with the text of the " +
            "default slot.",
        newMessages = "New messages",
        completed = "Completed",
        failed = "Failed",
        numberOnlyHeading = "Number-only",
        numberOnlyHint = "theme=\"number-only\" shows the number alone while the text stays the accessible " +
            "name — a good fit next to tabs and icons.",
        alerts = "Alerts",
        highlightHeading = "Highlighting information",
        highlightHint = "Badges turn a status column into something readable at a glance; the official column " +
            "renderer uses Lit, here a renderer lambda builds the vaadin-badge directly.",
        report = "Report",
        dueDate = "Due date",
        assignee = "Assignee",
        status = "Status",
        inProgress = "In progress",
        onHold = "On hold",
        interactiveHeading = "Interactive content",
        interactiveHint = "A badge can hold a button: picking a profession adds a badge with a clear button. " +
            "The official example loads the professions from the backend, a fixed list stands in here.",
        profession = "Profession",
        professionName = {
            when (it) {
                "engineer" -> "Engineer"
                "designer" -> "Designer"
                "product-manager" -> "Product manager"
                else -> "Researcher"
            }
        },
        noFilters = "No filters",
        filtersPrefix = "Filters: ",
        clearFilter = { "Clear filter: $it" },
        counterHeading = "Counter",
        counterHint = "Number-only badges inside tabs show unread counts; the text part is what screen " +
            "readers announce.",
        unreadMessages = "unread messages",
        inbox = "Inbox",
        important = "Important",
        spam = "Spam",
        archive = "Archive",
        dotHeading = "Dot",
        dotHint = "theme=\"dot\" shows a single dot (number and text are hidden), which is enough to say " +
            "\"there is something new\". It can also sit in the suffix slot of a button, absolutely " +
            "positioned over the icon.",
        downloads = "Downloads",
        completedLower = "completed",
        liveHeading = "Live updates",
        liveHint = "number can change at any time and the badge updates immediately; a count of 0 stays " +
            "visible (use the hidden attribute to hide the badge instead).",
        addOne = "Add one",
    )

private val chinese: BadgeTexts =
    BadgeTexts(
        labelHeading = "标签",
        labelHint = "默认插槽放文本，theme 的 success / warning / error 表达状态；只给文本、不带数字时，徽标就是一个带底色的标签。",
        pending = "待处理",
        confirmed = "已确认",
        warning = "警告",
        denied = "已拒绝",
        colourHeading = "颜色",
        colourHint = "默认是浅色底；再加 filled 变体得到实心徽标，两种风格可以并排比较。",
        defaultCaption = "默认",
        filledCaption = "filled",
        iconsHeading = "图标",
        iconsHint = "slot=\"icon\" 在文字前面放图标；徽标的 has-icon 状态属性由插槽内容自动维护。",
        iconOnlyHeading = "只有图标",
        iconOnlyHint = "theme=\"icon-only\" 隐藏文字但保留为无障碍名称；表格里用同样的徽标表示布尔值，" +
            "官方示例的数据来自 getUserPermissions()，这里是固定数据。",
        name = "姓名",
        view = "查看",
        comment = "评论",
        edit = "编辑",
        cancelled = "已取消",
        permissionsCaption = "权限表格",
        numberHeading = "数字",
        numberHint = "number 在文字前显示计数；它和默认插槽的文本一起被朗读。",
        newMessages = "新消息",
        completed = "已完成",
        failed = "失败",
        numberOnlyHeading = "仅数字",
        numberOnlyHint = "theme=\"number-only\" 只显示数字，文本仍是可访问名称——适合放在 tab 或图标旁边。",
        alerts = "提醒",
        highlightHeading = "区分信息",
        highlightHint = "徽标最适合把状态列变成一眼可读的标记；官方示例的列渲染器用 Lit，这里用 renderer lambda " +
            "直接构造 vaadin-badge。",
        report = "报告",
        dueDate = "截止日期",
        assignee = "负责人",
        status = "状态",
        inProgress = "进行中",
        onHold = "暂缓",
        interactiveHeading = "可交互内容",
        interactiveHint = "徽标里可以放按钮：选中职业后加一个带清除按钮的徽标。官方示例的职业列表来自后端，这里用固定列表模拟。",
        profession = "职业",
        professionName = {
            when (it) {
                "engineer" -> "工程师"
                "designer" -> "设计师"
                "product-manager" -> "产品经理"
                else -> "研究员"
            }
        },
        noFilters = "没有筛选条件",
        filtersPrefix = "筛选：",
        clearFilter = { "清除筛选：$it" },
        counterHeading = "计数器",
        counterHint = "选项卡里的 number-only 徽标是未读计数；文字部分是读屏使用的说明。",
        unreadMessages = "未读消息",
        inbox = "收件箱",
        important = "重要",
        spam = "垃圾邮件",
        archive = "归档",
        dotHeading = "圆点",
        dotHint = "theme=\"dot\" 只显示一个圆点（数字与文本都隐藏），适合表达「有新内容」；也可以放进按钮的 suffix " +
            "插槽，用绝对定位压在图标角上。",
        downloads = "下载",
        completedLower = "已完成",
        liveHeading = "动态更新",
        liveHint = "number 可以随时修改，徽标立即更新；计数为 0 时仍然显示（与「不显示」用 hidden 属性区分）。",
        addOne = "加一",
    )

private val badgeTexts: Translations<BadgeTexts> = texts(
    Langs.EN to english,
    Langs.ZH_HANS to chinese,
)

internal class BadgePage : DemoPage() {
    private val texts = Binder(badgeTexts)

    private val frame = PageFrame(ENTRY)

    private val permissions = createGrid()

    private val reports = createGrid()

    private val professionCombo = createComboBox()

    private val chips = kitRow()

    private val filterReadout = Readout("")

    private val selectedProfessions = mutableListOf<String>()

    private val chipsByProfession = mutableMapOf<String, HTMLElement>()

    /** The status badge of the reports table; the copy is read when the cell is rendered. */
    fun statusBadge(status: String): BadgeElement {
        val copy = texts.current
        val text =
            when (status) {
                "completed" -> copy.completed
                "in-progress" -> copy.inProgress
                "on-hold" -> copy.onHold
                else -> copy.cancelled
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

    private fun permissionBadge(granted: Boolean): BadgeElement {
        val copy = texts.current
        return if (granted) {
            iconBadge("vaadin:check", "success", "icon-only").also { badge ->
                texts.bind(badge.labelSpan(), { it.confirmed }) { badge.labelSpan().textContent = it }
            }
        } else {
            iconBadge("vaadin:close", "error", "icon-only").also { badge ->
                texts.bind(badge.labelSpan(), { it.cancelled }) { badge.labelSpan().textContent = it }
            }
        }
    }

    /** A boolean column of the permissions table; the field is read through [granted]. */
    private fun permissionColumn(copy: (BadgeTexts) -> String, granted: (PermissionRow) -> Boolean?): GridColumnElement {
        val column = createGridColumn {
            renderer = { cell, _, model ->
                cell.replaceChildren(permissionBadge(granted(model.item.unsafeCast<PermissionRow>()) == true))
            }
        }
        texts.bind(column, copy) { column.header = it }
        return column
    }

    private fun syncFilters() {
        val copy = texts.current
        filterReadout.set(
            if (selectedProfessions.isEmpty()) {
                copy.noFilters
            } else {
                copy.filtersPrefix + selectedProfessions.map { copy.professionName(it) }.joinToString(" · ")
            },
        )
    }

    private fun removeProfession(key: String) {
        val chip = chipsByProfession.remove(key) ?: return
        chip.remove()
        selectedProfessions.remove(key)
        syncFilters()
    }

    private fun addProfession(key: String) {
        if (chipsByProfession.containsKey(key)) return
        selectedProfessions.add(key)
        val copy = texts.current
        val label = copy.professionName(key)
        val clear = createButton().addTheme("tertiary")
        clear.ariaLabel = copy.clearFilter(label)
        clear.title = copy.clearFilter(label)
        clear.style.setProperty("height", "1.5rem")
        clear.style.setProperty("min-width", "1.5rem")
        clear.style.setProperty("margin", "0")
        clear.style.setProperty("padding", "0")
        clear.append(createIcon { icon = "vaadin:close" })
        clear.clickEvent.addHandler { removeProfession(key) }
        val chip = createBadge()
        chip.style.setProperty("padding-right", "0")
        val chipLabel = HtmlTagName.span()
        texts.bind(chipLabel, { copy -> copy.professionName(key) }) { chipLabel.textContent = it }
        texts.bind(clear, { copy -> copy.clearFilter(copy.professionName(key)) }) {
            clear.ariaLabel = it
            clear.title = it
        }
        chip.append(chipLabel, clear)
        chipsByProfession[key] = chip
        chips.append(chip)
        syncFilters()
    }

    private fun professionItems(): Array<JsAny> {
        val copy = texts.current
        return jsArray<JsAny>(PROFESSIONS.joinToString(",", "[", "]") { "\"${copy.professionName(it)}\"" })
    }

    init {
        frame.mount(this)
        // `vaadin:check` and the other icons of the table examples come from @vaadin/icons.
        registerVaadinIcons()

        // --- Label (badge/badge-basic.ts) -----------------------------------------------------------
        val labelSection = SectionBlock.NAME { show() }
        texts.bind(labelSection.heading) { it.labelHeading }
        texts.bind(labelSection.hint) { it.labelHint }
        val numeric = createBadge { number = 4.0 }
        numeric.dataset["test"] = "badge"
        val pendingBadge = textBadge("")
        texts.bind(pendingBadge) { it.pending }
        val confirmedBadge = textBadge("", "success")
        texts.bind(confirmedBadge) { it.confirmed }
        val warningBadge = textBadge("", "warning")
        texts.bind(warningBadge) { it.warning }
        val deniedBadge = textBadge("", "error")
        texts.bind(deniedBadge) { it.denied }
        labelSection.body.append(
            exampleBox(null, kitRow(pendingBadge, confirmedBadge, warningBadge, deniedBadge, numeric)),
        )
        body.append(labelSection.root)

        // --- Colour variants (badge/badge-color.ts) -----------------------------------------------
        val colour = SectionBlock.NAME { show() }
        texts.bind(colour.heading) { it.colourHeading }
        texts.bind(colour.hint) { it.colourHint }
        fun badgeSet(vararg themes: String): HTMLElement {
            val row = kitRow()
            val list =
                listOf<(BadgeTexts) -> String>(
                    { it.pending },
                    { it.confirmed },
                    { it.warning },
                    { it.denied },
                )
            val extraThemes = listOf("", "success", "warning", "error")
            list.forEachIndexed { index, copy ->
                val badge = textBadge("")
                val theme = extraThemes[index]
                badge.addTheme(*(if (theme.isEmpty()) themes else themes + theme).toList().toTypedArray())
                texts.bind(badge, copy) { badge.textContent = it }
                row.append(badge)
            }
            return row
        }
        val defaultCaption = HtmlTagName.p()
        texts.bind(defaultCaption, { it.defaultCaption }) { defaultCaption.textContent = it }
        val filledCaption = HtmlTagName.p()
        texts.bind(filledCaption, { it.filledCaption }) { filledCaption.textContent = it }
        colour.body.append(
            exampleBox(null, kitCol(defaultCaption, badgeSet())),
            exampleBox(null, kitCol(filledCaption, badgeSet("filled"))),
        )
        body.append(colour.root)

        // --- Icons (badge/badge-icons.ts) ----------------------------------------------------------
        val withIcons = SectionBlock.NAME { show() }
        texts.bind(withIcons.heading) { it.iconsHeading }
        texts.bind(withIcons.hint) { it.iconsHint }
        fun iconRow(vararg icons: String): HTMLElement {
            val row = kitRow()
            val copies =
                listOf<(BadgeTexts) -> String>({ it.pending }, { it.confirmed }, { it.warning }, { it.denied })
            val themes = listOf("", "success", "warning", "error")
            icons.forEachIndexed { index, icon ->
                val badge = iconBadge(icon)
                if (themes[index].isNotEmpty()) badge.addTheme(themes[index])
                texts.bind(badge.labelSpan(), copies[index]) { badge.labelSpan().textContent = it }
                row.append(badge)
            }
            return row
        }
        withIcons.body.append(
            exampleBox(
                null,
                iconRow("vaadin:clock", "vaadin:check", "vaadin:warning", "vaadin:exclamation-circle"),
            ),
        )
        body.append(withIcons.root)

        // --- Icon-only + icon-only table (badge-icons-only.ts, badge-icons-only-table.ts) ----------
        val iconOnly = SectionBlock.NAME { show() }
        texts.bind(iconOnly.heading) { it.iconOnlyHeading }
        texts.bind(iconOnly.hint) { it.iconOnlyHint }
        permissions.style.setProperty("height", "14rem")
        val nameColumn = createGridColumn { path = "name" }
        texts.bind(nameColumn, { it.name }) { nameColumn.header = it }
        permissions.append(
            nameColumn,
            permissionColumn({ it.view }) { it.view },
            permissionColumn({ it.comment }) { it.comment },
            permissionColumn({ it.edit }) { it.edit },
        )
        val confirmedOnly = iconBadge("vaadin:check", "success", "icon-only")
        texts.bind(confirmedOnly.labelSpan(), { it.confirmed }) { confirmedOnly.labelSpan().textContent = it }
        val cancelledOnly = iconBadge("vaadin:close", "error", "icon-only")
        texts.bind(cancelledOnly.labelSpan(), { it.cancelled }) { cancelledOnly.labelSpan().textContent = it }
        val permissionsCaption = HtmlTagName.p()
        texts.bind(permissionsCaption, { it.permissionsCaption }) { permissionsCaption.textContent = it }
        iconOnly.body.append(
            exampleBox(null, kitRow(confirmedOnly, cancelledOnly)),
            exampleBox(null, kitCol(permissionsCaption, permissions)),
        )
        body.append(iconOnly.root)

        // --- Number (badge/badge-number.ts) --------------------------------------------------------
        val numberSection = SectionBlock.NAME { show() }
        texts.bind(numberSection.heading) { it.numberHeading }
        texts.bind(numberSection.hint) { it.numberHint }
        fun numberBadge(number: Double, copy: (BadgeTexts) -> String, vararg themes: String): BadgeElement {
            val badge = createBadge { this.number = number }
            if (themes.isNotEmpty()) badge.addTheme(*themes)
            texts.bind(badge, copy) { badge.textContent = it }
            return badge
        }
        numberSection.body.append(
            exampleBox(
                null,
                kitRow(
                    numberBadge(12.0, { it.newMessages }),
                    numberBadge(3.0, { it.completed }, "success"),
                    numberBadge(1.0, { it.failed }, "error"),
                ),
            ),
        )
        body.append(numberSection.root)

        // --- Number-only (badge/badge-number-only.ts) ---------------------------------------------
        val numberOnly = SectionBlock.NAME { show() }
        texts.bind(numberOnly.heading) { it.numberOnlyHeading }
        texts.bind(numberOnly.hint) { it.numberOnlyHint }
        numberOnly.body.append(
            exampleBox(
                null,
                kitRow(
                    numberBadge(12.0, { it.newMessages }, "number-only"),
                    numberBadge(3.0, { it.alerts }, "error", "number-only"),
                ),
            ),
        )
        body.append(numberOnly.root)

        // --- Highlighting information (badge/badge-highlight.ts) ----------------------------------
        val highlight = SectionBlock.NAME { show() }
        texts.bind(highlight.heading) { it.highlightHeading }
        texts.bind(highlight.hint) { it.highlightHint }
        reports.style.setProperty("height", "14rem")
        val reportColumn = createGridColumn { path = "report" }
        texts.bind(reportColumn, { it.report }) { reportColumn.header = it }
        val dueColumn = createGridColumn { path = "due" }
        texts.bind(dueColumn, { it.dueDate }) { dueColumn.header = it }
        val assigneeColumn = createGridColumn { path = "assignee" }
        texts.bind(assigneeColumn, { it.assignee }) { assigneeColumn.header = it }
        val statusColumn =
            createGridColumn {
                renderer = { cell, _, model -> cell.replaceChildren(statusBadge(model.item.unsafeCast<ReportRow>().status)) }
            }
        texts.bind(statusColumn, { it.status }) { statusColumn.header = it }
        reports.append(reportColumn, dueColumn, assigneeColumn, statusColumn)
        highlight.body.append(exampleBox(null, reports))
        body.append(highlight.root)

        // --- Interactive content (badge/badge-interactive.ts) -------------------------------------
        val interactive = SectionBlock.NAME { show() }
        texts.bind(interactive.heading) { it.interactiveHeading }
        texts.bind(interactive.hint) { it.interactiveHint }
        texts.bind(professionCombo, { it.profession }) { professionCombo.label = it }
        professionCombo.listen(ComboBoxEvents.change) {
            val item = professionCombo.selectedItem ?: return@listen
            val label = item.unsafeCast<String>()
            val key = PROFESSIONS.firstOrNull { texts.current.professionName(it) == label } ?: return@listen
            addProfession(key)
        }
        interactive.body.append(exampleBox(null, kitCol(professionCombo, chips, filterReadout.root)))
        body.append(interactive.root)

        // --- Counter (badge/badge-counter.ts) -----------------------------------------------------
        val counter = SectionBlock.NAME { show() }
        texts.bind(counter.heading) { it.counterHeading }
        texts.bind(counter.hint) { it.counterHint }
        val tabs = createTabs()
        fun counterTab(copy: (BadgeTexts) -> String, count: Double): HTMLElement {
            val tab = createTab()
            val label = HtmlTagName.span()
            texts.bind(label, copy) { label.textContent = it }
            tab.append(label)
            tab.append(
                createBadge { number = count }.addTheme("filled", "number-only").also { badge ->
                    texts.bind(badge, { copy -> copy.unreadMessages }) { badge.textContent = it }
                },
            )
            return tab
        }
        tabs.append(
            counterTab({ it.inbox }, 12.0),
            counterTab({ it.important }, 3.0),
            counterTab({ it.spam }, 45.0),
            counterTab({ it.archive }, 23.0),
        )
        counter.body.append(exampleBox(null, tabs))
        body.append(counter.root)

        // --- Dot (badge/badge-dot.ts) ------------------------------------------------------------
        val dot = SectionBlock.NAME { show() }
        texts.bind(dot.heading) { it.dotHeading }
        texts.bind(dot.hint) { it.dotHint }
        val download = createButton().addTheme("icon")
        texts.bind(download, { it.downloads }) { download.ariaLabel = it }
        download.append(createIcon { icon = "lumo:download" })
        val completed = createBadge { number = 3.0 }.addTheme("dot", "success")
        texts.bind(completed) { it.completedLower }
        completed.slot = "suffix"
        completed.style.setProperty("position", "absolute")
        completed.style.setProperty("top", "0.3em")
        completed.style.setProperty("right", "0.3em")
        download.append(completed)
        fun dotBadge(copy: (BadgeTexts) -> String, vararg themes: String): BadgeElement {
            val badge = textBadge("", "dot", *themes)
            texts.bind(badge, copy) { badge.textContent = it }
            return badge
        }
        dot.body.append(
            exampleBox(
                null,
                kitRow(
                    dotBadge({ it.pending }),
                    dotBadge({ it.confirmed }, "success"),
                    dotBadge({ it.warning }, "warning"),
                    dotBadge({ it.denied }, "error"),
                    download,
                ),
            ),
        )
        body.append(dot.root)

        // --- Live number (wrapper extra, exercised by the end-to-end suite) ------------------------
        val live = SectionBlock.NAME { show() }
        texts.bind(live.heading) { it.liveHeading }
        texts.bind(live.hint) { it.liveHint }
        val liveBadge = createBadge { number = 0.0 }
        liveBadge.dataset["test"] = "badge-live"
        val addOne = createButton().addTheme("tertiary")
        texts.bind(addOne) { it.addOne }
        addOne.clickEvent.addHandler { liveBadge.number += 1.0 }
        live.body.append(exampleBox(null, kitRow(liveBadge, addOne)))
        body.append(live.root)

        applyTranslation(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = ENTRY.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        // The item lists and the cell renderers read the copy when they run, so both tables are
        // handed their data again to re-render in the new language.
        professionCombo.items = professionItems()
        permissions.items = jsArray<JsAny>(SIMULATED_PERMISSIONS)
        reports.items = jsArray<JsAny>(SIMULATED_REPORTS)
        syncFilters()
    }

    companion object : BadgePageDefaultReference() {        val ENTRY: CatalogEntry =
            CatalogEntry(
                id = "badge",
                group = Groups.DISPLAY,
                meta =
                    PageMeta(
                        title = bi("Badge", "Badge"),
                        summary = bi("数字或文本标记：number 决定显示的数字，theme 决定颜色。", "Numeric or text badge: number sets the displayed count and theme sets the colour."),
                        docsUrl = "${Catalog.DOCS_BASE}/badge",
                        sourcePath = "pages/display/BadgePage.kt",
                    ),
                kitPage = { BadgePage.NAME() },
            )



}
}
