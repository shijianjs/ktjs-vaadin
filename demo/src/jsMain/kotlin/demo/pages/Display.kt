package demo.pages

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.tr
import demo.ui.Images
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.Readout
import demo.ui.code
import demo.ui.column
import demo.ui.div
import demo.ui.element
import demo.ui.example
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
import kotlin.js.unsafeCast
import vaadin.avatar.createAvatar
import vaadin.avatargroup.AvatarGroupItem
import vaadin.avatargroup.createAvatarGroup
import vaadin.badge.BadgeElement
import vaadin.badge.createBadge
import vaadin.button.createButton
import vaadin.combobox.ComboBoxEvents
import vaadin.combobox.createComboBox
import vaadin.grid.GridColumnElement
import vaadin.grid.createGrid
import vaadin.grid.createGridColumn
import vaadin.icon.Iconset
import vaadin.icon.createIcon
import vaadin.icon.createIconset
import vaadin.icon.unsafeSvgLiteral
import vaadin.icons.registerVaadinIcons
import vaadin.inputcontainer.createInputContainer
import vaadin.lumostyles.registerLumoIcons
import vaadin.progressbar.createProgressBar
import vaadin.slider.SliderEvents
import vaadin.slider.createSlider
import vaadin.switch.SwitchEvents
import vaadin.switch.createSwitch
import vaadin.tabs.createTab
import vaadin.tabs.createTabs
import web.dom.clickEvent
import web.dom.document
import web.events.addHandler
import web.html.HTMLElement
import web.html.HtmlTagName
import web.svg.SVG_NAMESPACE
import web.svg.SVGGElement
import web.svg.SvgTagName

val displayEntries: List<CatalogEntry> =
    listOf(
        CatalogEntry(
            id = "avatar",
            group = Groups.DISPLAY,
            meta =
                PageMeta(
                    title = bi("Avatar", "Avatar"),
                    summary = bi("用户头像：缩写、图片、颜色索引与悬停提示。", "User avatars: initials, images, colour index and hover tooltip."),
                    docsUrl = "$DOCS_BASE/avatar",
                    sourcePath = "pages/Display.kt",
                ),
            builder = ::buildAvatarPage,
        ),
        CatalogEntry(
            id = "avatar-group",
            group = Groups.DISPLAY,
            meta =
                PageMeta(
                    title = bi("Avatar Group", "Avatar Group"),
                    summary = bi("头像组：超出 maxItemsVisible 的部分折叠成计数。", "Avatar group: items beyond maxItemsVisible collapse into a counter."),
                    docsUrl = "$DOCS_BASE/avatar",
                    docsNote = bi("Avatar Group 与 Avatar 共用一页官方文档。", "Avatar Group shares the Avatar documentation page."),
                    sourcePath = "pages/Display.kt",
                ),
            builder = ::buildAvatarGroupPage,
        ),
        CatalogEntry(
            id = "badge",
            group = Groups.DISPLAY,
            meta =
                PageMeta(
                    title = bi("Badge", "Badge"),
                    summary = bi("数字或文本标记：number 决定显示的数字，theme 决定颜色。", "Numeric or text badge: number sets the displayed count and theme sets the colour."),
                    docsUrl = "$DOCS_BASE/badge",
                    sourcePath = "pages/Display.kt",
                ),
            builder = ::buildBadgePage,
        ),
        CatalogEntry(
            id = "icon",
            group = Groups.DISPLAY,
            meta =
                PageMeta(
                    title = bi("Icon", "Icon"),
                    summary = bi("图标元素：icon 名称、尺寸、SVG symbol 与字体图标。", "Icon element: icon name, size, SVG symbol and font icons."),
                    docsUrl = "$DOCS_BASE/icons",
                    sourcePath = "pages/Display.kt",
                ),
            builder = ::buildIconPage,
        ),
        CatalogEntry(
            id = "icons",
            group = Groups.DISPLAY,
            meta =
                PageMeta(
                    title = bi("Icon Sets", "Icon Sets"),
                    summary = bi("注册额外的图标集：@vaadin/icons 提供 vaadin:* 的几百个图标。", "Register additional icon sets: @vaadin/icons provides hundreds of vaadin:* icons."),
                    docsUrl = "$DOCS_BASE/icons",
                    sourcePath = "pages/Display.kt",
                ),
            builder = ::buildIconsPage,
        ),
        CatalogEntry(
            id = "progress-bar",
            group = Groups.DISPLAY,
            meta =
                PageMeta(
                    title = bi("Progress Bar", "Progress Bar"),
                    summary = bi("进度条：确定值与不确定状态，min / max 决定比例。", "Progress bar: determinate value and indeterminate state; min / max set the ratio."),
                    docsUrl = "$DOCS_BASE/progress-bar",
                    sourcePath = "pages/Display.kt",
                ),
            builder = ::buildProgressBarPage,
        ),
        CatalogEntry(
            id = "input-container",
            group = Groups.DISPLAY,
            meta =
                PageMeta(
                    title = bi("Input Container", "Input Container"),
                    summary =
                        bi(
                            "输入类字段的底层容器：前缀 / 后缀插槽、invalid 与 readonly 状态。",
                            "Low-level container for input fields: prefix/suffix slots plus invalid and readonly states.",
                        ),
                    docsUrl = "$DOCS_BASE/text-field",
                    docsNote =
                        bi(
                            "vaadin-input-container 是内部基础元素，官方文档见 Text Field。",
                            "vaadin-input-container is an internal building block; the official documentation is under Text Field.",
                        ),
                    sourcePath = "pages/Display.kt",
                ),
            builder = ::buildInputContainerPage,
        ),
    )

/** A single star, passed to [unsafeSvgLiteral] by the SVG literal example. */
private const val STAR_SVG_FRAGMENT =
    "<path d='M12 2.5l2.9 5.9 6.6.9-4.8 4.7 1.2 6.6-5.9-3.1-5.9 3.1 1.2-6.6L2.5 9.3l6.6-.9z' " +
        "fill='currentColor'/>"

// ---------------------------------------------------------------------------------------------
// Avatar
// ---------------------------------------------------------------------------------------------

/** Shared names of the simulated people; the docs examples load them from the backend. */
private val SIMULATED_PEOPLE =
    listOf(
        "Ada Lovelace",
        "Grace Hopper",
        "Margaret Hamilton",
        "Katherine Johnson",
        "Barbara Liskov",
        "Frances Allen",
    )

private fun buildAvatarPage(meta: PageMeta): PageView {
    val view = page(meta)

    // --- Basic usage (avatar/avatar-basic.ts) ---------------------------------------------------
    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "有 img 时显示图片，否则显示缩写；name 用作无障碍名称与提示文本。官方示例从后端的 " +
                    "getPeople() 取人像，演示站使用固定的本地人物，照片与文档示例相同。",
                "Shows the image when img is set, otherwise the abbreviation; name is the accessible name and " +
                    "the tooltip text. The official example loads a person with getPeople(); the demo uses " +
                    "fixed local people with the same portraits as the docs.",
            ),
        )
    val anonymous = createAvatar()
    anonymous.dataset["test"] = "avatar-anonymous"
    basic.body.append(
        example(
            null,
            row(
                anonymous,
                createAvatar { name = "Ada Lovelace" },
                createAvatar {
                    name = "Grace Hopper"
                    img = Images.portrait(3)
                },
            ),
        ),
    )
    view.body.append(basic.root)

    // --- Name and abbreviation (avatar/avatar-name.ts, avatar/avatar-abbreviation.ts) -----------
    val naming =
        section(
            tr("姓名与缩写", "Name and abbreviation"),
            tr(
                "只给 name 时缩写由姓名推导；abbr 显式覆盖。withTooltip 打开后，悬停或聚焦会显示 name " +
                    "（没有名字时用 i18n.anonymous）。",
                "With only name the abbreviation is derived from it; abbr overrides it explicitly. " +
                    "withTooltip shows name on hover or focus (i18n.anonymous when there is no name).",
            ),
        )
    naming.body.append(
        example(
            tr("姓名推导缩写 / 显式缩写", "Derived initials / explicit abbreviation"),
            row(
                createAvatar {
                    name = "Augusta Ada King"
                    withTooltip = true
                },
                createAvatar {
                    name = "Augusta Ada King"
                    abbr = "AK"
                    withTooltip = true
                },
            ),
        ),
    )
    view.body.append(naming.root)

    // --- Image (avatar/avatar-image.ts) ---------------------------------------------------------
    val image =
        section(
            tr("图片", "Image"),
            tr(
                "img 可以是任意图片地址；官方示例展示人物照片与公司标志，demo 直接使用从官方文档复制来的同一组图片。",
                "img accepts any image source; the official example shows a person photo and a company logo, " +
                    "and the demo uses the same pictures copied from the official documentation.",
            ),
        )
    image.body.append(
        example(
            null,
            row(
                createAvatar {
                    name = "Ada Lovelace"
                    img = Images.portrait(1)
                },
                createAvatar {
                    name = "Company Inc."
                    img = Images.companyLogo
                },
            ),
        ),
    )
    view.body.append(image.root)

    // --- Sizes (avatar/avatar-sizes.ts) ---------------------------------------------------------
    val sizes =
        section(
            tr("尺寸", "Sizes"),
            tr(
                "theme 提供 xlarge / large / small / xsmall 四个尺寸，未设置时使用默认尺寸。",
                "The theme offers xlarge / large / small / xsmall; without a theme the default size is used.",
            ),
        )
    sizes.body.append(
        example(
            null,
            row(
                createAvatar { name = "Ada Lovelace" }.withTheme("xlarge"),
                createAvatar { name = "Ada Lovelace" }.withTheme("large"),
                createAvatar { name = "Ada Lovelace" },
                createAvatar { name = "Ada Lovelace" }.withTheme("small"),
                createAvatar { name = "Ada Lovelace" }.withTheme("xsmall"),
            ),
        ),
    )
    view.body.append(sizes.root)

    // --- Background colour (wrapper extra, see avatar-group-bg-color.ts) ------------------------
    val colours =
        section(
            tr("颜色索引", "Colour index"),
            tr(
                "colorIndex 选择 Lumo 的用户色板，共 7 个颜色；avatar-group 的例子用同样的属性。",
                "colorIndex selects one of the seven Lumo user colours; the avatar group examples use the " +
                    "same property.",
            ),
        )
    colours.body.append(
        example(
            null,
            row(*(0..6).map { index ->
                createAvatar {
                    name = tr("用户 $index", "User $index")
                    abbr = "U$index"
                    colorIndex = index.toDouble()
                }
            }.toTypedArray()),
        ),
    )
    view.body.append(colours.root)

    return view
}

// ---------------------------------------------------------------------------------------------
// Avatar group
// ---------------------------------------------------------------------------------------------

/** One `items` entry of `<vaadin-avatar-group>`; every field is optional. */
private fun groupItem(name: String? = null, colorIndex: Double? = null, img: String? = null): AvatarGroupItem =
    unsafeJso {
        name?.let { this.name = it }
        colorIndex?.let { this.colorIndex = it }
        img?.let { this.img = it }
    }

private fun buildAvatarGroupPage(meta: PageMeta): PageView {
    val view = page(meta)

    // --- Basic usage (avatar/avatar-group-basic.ts) ---------------------------------------------
    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "items 里每个对象描述一个头像；官方示例把后端返回的三个人名映射成 items，这里是固定数据。",
                "Each object in items describes one avatar; the official example maps three people from the " +
                    "backend, fixed data is used here.",
            ),
        )
    val three = SIMULATED_PEOPLE.take(3).map { groupItem(name = it) }
    val group = createAvatarGroup()
    group.dataset["test"] = "avatar-group"
    group.items = three.toTypedArray()
    basic.body.append(example(null, group))
    view.body.append(basic.root)

    // --- Maximum number of items (avatar/avatar-group-max-items.ts) -----------------------------
    val limit =
        section(
            tr("最多显示数量", "Maximum number of items"),
            tr(
                "maxItemsVisible 把多出来的头像折叠成一个可展开的 +N 头像；0 与 1 都按 2 处理，至少显示两个头像。",
                "maxItemsVisible folds the overflowing avatars into an expandable +N avatar; 0 and 1 behave " +
                    "like 2, there are always at least two avatars visible.",
            ),
        )
    val six = SIMULATED_PEOPLE.map { groupItem(name = it) }
    val limited = createAvatarGroup { maxItemsVisible = 3.0 }
    limited.items = six.toTypedArray()
    val limitReadout = Readout("maxItemsVisible = 3")
    val slider =
        createSlider {
            label = "maxItemsVisible"
            min = 1.0
            max = 6.0
            step = 1.0
            value = 3.0
            valueAlwaysVisible = true
        }
    slider.listen(SliderEvents.valueChanged) {
        val visible = slider.value.toInt()
        limited.maxItemsVisible = visible.toDouble()
        limitReadout.set("maxItemsVisible = $visible")
    }
    limit.body.append(example(null, column(limited, slider, limitReadout.root)))
    view.body.append(limit.root)

    // --- Background colour (avatar/avatar-group-bg-color.ts) -----------------------------------
    val colours =
        section(
            tr("背景颜色", "Background colour"),
            tr(
                "给每个 item 设置 colorIndex，头像使用不同的用户色板颜色。",
                "Setting colorIndex on every item gives each avatar a different user colour.",
            ),
        )
    val coloured = createAvatarGroup()
    coloured.items =
        SIMULATED_PEOPLE.mapIndexed { index, name ->
            groupItem(name = name, colorIndex = index.toDouble())
        }.toTypedArray()
    colours.body.append(example(null, coloured))
    view.body.append(colours.root)

    // --- Internationalisation (avatar/avatar-group-internationalisation.ts) --------------------
    val i18n =
        section(
            tr("本地化", "Internationalisation"),
            tr(
                "i18n 覆盖匿名用户的提示、组的无障碍标签（activeUsers，{count} 会被替换），以及成员增减时" +
                    "读屏播报的 joined / left。这些文本只对辅助技术可见：用开发者工具或读屏软件查看效果。" +
                    "官方示例用芬兰语，这里跟随界面语言。",
                "i18n overrides the anonymous tooltip, the group's accessible label (activeUsers, where " +
                    "{count} is replaced) and the joined / left announcements made when members change. Those " +
                    "texts are only exposed to assistive technology: use the devtools accessibility pane or a " +
                    "screen reader to see them. The official example uses Finnish, this demo follows the UI " +
                    "language.",
            ),
        )
    val groupI18n =
        jsObject(
            """
            {
              "anonymous": "${tr("匿名", "Anonymous")}",
              "activeUsers": {
                "one": "${tr("当前有 1 位在线用户", "Currently one active user")}",
                "many": "${tr("当前有 {count} 位在线用户", "Currently {count} active users")}"
              },
              "joined": "${tr("{user} 加入了", "{user} joined")}",
              "left": "${tr("{user} 离开了", "{user} left")}"
            }
            """.trimIndent(),
        )
    val baseMembers = listOf(groupItem(), groupItem(name = "Ada Lovelace"), groupItem(name = "Grace Hopper"))
    val localised = createAvatarGroup { maxItemsVisible = 2.0 }
    localised.i18n = groupI18n
    localised.items = baseMembers.toTypedArray()
    var extraMember: AvatarGroupItem? = null
    val memberToggle = createButton { textContent = tr("添加成员", "Add member") }.withTheme("tertiary")
    memberToggle.clickEvent.addHandler {
        val current = extraMember
        if (current == null) {
            val added = groupItem(name = "Frances Allen")
            extraMember = added
            localised.items = (baseMembers + added).toTypedArray()
            memberToggle.textContent = tr("移除成员", "Remove member")
        } else {
            extraMember = null
            localised.items = baseMembers.toTypedArray()
            memberToggle.textContent = tr("添加成员", "Add member")
        }
    }
    i18n.body.append(example(null, column(localised, memberToggle)))
    view.body.append(i18n.root)

    return view
}

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

/** A permissions row of `badge-icons-only-table.ts`: one boolean per guarded action. */
private external interface PermissionRow {
    var name: String
    var view: Boolean?
    var comment: Boolean?
    var edit: Boolean?
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

/** A badge with an icon in the `icon` slot, before the text. */
private fun iconBadge(icon: String, text: String, vararg themes: String): BadgeElement =
    createBadge()
        .withTheme(*themes)
        .apply {
            append(createIcon { this.icon = icon }.apply { slot = "icon" })
            append(document.createTextNode(text))
        }

/** The "Yes" / "No" badge of the permissions table. */
private fun permissionBadge(granted: Boolean): BadgeElement =
    iconBadge(
        if (granted) "vaadin:check" else "vaadin:close",
        tr(if (granted) "是" else "否", if (granted) "Yes" else "No"),
        if (granted) "success" else "error",
        "icon-only",
    )

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

// ---------------------------------------------------------------------------------------------
// Icon (single icon: name, size, colour, fonts, svg literal)
// ---------------------------------------------------------------------------------------------

private fun buildIconPage(meta: PageMeta): PageView {
    val view = page(meta)
    registerLumoIcons()
    registerVaadinIcons()

    // --- Basic usage (icons/icon-basic.ts) -----------------------------------------------------
    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "icon 属性接受 图标集:名称。registerLumoIcons() 与 registerVaadinIcons() 保证两个内置集合" +
                    "已经注册，否则图标只在开发模式下可见。",
                "The icon property takes icon-set:name. registerLumoIcons() and registerVaadinIcons() make sure " +
                    "the two built-in sets are registered, otherwise the icons only show up in development mode.",
            ),
        )
    val gallery =
        row(
            createIcon { icon = "lumo:checkmark" },
            createIcon { icon = "lumo:photo" },
            createIcon { icon = "lumo:calendar" },
            createIcon { icon = "vaadin:phone" },
        )
    val picker = createIcon { icon = "lumo:user" }
    picker.style.setProperty("--vaadin-icon-size", "2rem")
    val iconReadout = Readout("icon = lumo:user")
    val iconNames = listOf("lumo:user", "lumo:bell", "vaadin:user", "vaadin:bell")
    var iconIndex = 0
    val nextIcon =
        createButton { textContent = tr("下一个图标", "Next icon") }.withTheme("tertiary").apply {
            clickEvent.addHandler {
                iconIndex = (iconIndex + 1) % iconNames.size
                picker.icon = iconNames[iconIndex]
                iconReadout.set("icon = ${iconNames[iconIndex]}")
            }
        }
    basic.body.append(example(null, column(gallery, row(picker, nextIcon, iconReadout.root))))
    view.body.append(basic.root)

    // --- Size (icons/icons-sizing.ts) ---------------------------------------------------------
    val sizing =
        section(
            tr("尺寸", "Size"),
            tr(
                "--vaadin-icon-size 设置图标的外框尺寸（默认跟随行高 1lh），--vaadin-icon-visual-size 在此基础上" +
                    "调整绘制区域；size 属性只影响 svg 字面量的 viewBox，见下面的 SVG 字面量一节。",
                "--vaadin-icon-size sets the outer size of the icon (1lh by default) and " +
                    "--vaadin-icon-visual-size adjusts the painted area inside it; the size property only affects " +
                    "the viewBox of svg literals, see the SVG literals section below.",
            ),
        )
    fun sizedIcon(size: String): HTMLElement =
        createIcon { src = Images.codeBranchIcon }.apply {
            style.setProperty("--vaadin-icon-size", size)
        }
    sizing.body.append(
        example(null, row(sizedIcon("1rem"), sizedIcon("2rem"), sizedIcon("3rem"), sizedIcon("4rem"))),
    )
    view.body.append(sizing.root)

    // --- Colour (icons/icons-color.ts) -------------------------------------------------------
    val colours =
        section(
            tr("颜色", "Colour"),
            tr(
                "图标使用 currentColor：设置文字颜色或 --vaadin-icon-color 就能改填充与描边，SVG 图标和字体" +
                    "图标都一样。官方示例的第二个图标用 FontAwesome 类名，这里换成 Lumo 字体图标。",
                "Icons use currentColor: setting the text colour or --vaadin-icon-color changes the fill and " +
                    "stroke, for SVG and font icons alike. The second icon of the official example uses a " +
                    "FontAwesome class, replaced by a Lumo font icon here.",
            ),
        )
    val redSvg = createIcon { src = Images.codeBranchIcon }.apply { style.color = "red" }
    val redFont =
        createIcon {
            fontFamily = "lumo-icons"
            char = "ea26"
        }.apply { style.color = "red" }
    fun tintedIcon(fill: String): HTMLElement =
        createIcon { icon = "lumo:bell" }.apply { style.setProperty("--vaadin-icon-color", fill) }
    colours.body.append(example(null, row(redSvg, redFont, tintedIcon("green"), tintedIcon("rgb(0 102 240)"))))
    view.body.append(colours.root)

    // --- Font icons (icons/icon-fonts.ts) ----------------------------------------------------
    val fonts =
        section(
            tr("字体图标", "Font icons"),
            tr(
                "fontFamily 选择图标字体，char 接受十六进制码点（Lumo 字体：ea0f 对勾、ea04 下箭头、ea26 图片），" +
                    "也可以直接给单个字符；ligature 需要字体本身支持连字（例如 Material Icons），iconClass 需要" +
                    "字体自带的样式表。演示站没有引入第三方字体，因此只展示 Lumo 字体按码点渲染的效果。",
                "fontFamily selects the icon font and char takes a hexadecimal code point (Lumo: ea0f checkmark, " +
                    "ea04 angle down, ea26 photo), or a single character; ligature needs a font that provides " +
                    "ligatures (Material Icons, for example) and iconClass needs the stylesheet shipped with the " +
                    "font. The demo ships no third-party font, so only the Lumo code points are rendered.",
            ),
        )
    fun fontIcon(char: String, size: String = "1.5rem"): HTMLElement =
        createIcon {
            fontFamily = "lumo-icons"
            this.char = char
        }.apply { style.setProperty("--vaadin-icon-size", size) }
    fonts.body.append(example(null, row(fontIcon("ea0f"), fontIcon("ea04"), fontIcon("ea26"), fontIcon("ea0f", "3rem"))))
    view.body.append(fonts.root)

    // --- SVG literals (wrapper API; see vaadin/icon/IconTypes.kt) -----------------------------
    val literals =
        section(
            tr("SVG 字面量", "SVG literals"),
            tr(
                "Kotlin/JS 写不出 Lit 的 svg 模板字面量：用 unsafeSvgLiteral() 从字符串构造一个，或通过 " +
                    "Iconset.getIconSvg() 取出已注册图标集里的图形，两者都赋给 svg 属性，size 决定 viewBox。",
                "Kotlin/JS cannot author Lit's svg tagged template: unsafeSvgLiteral() builds one from a string, " +
                    "and Iconset.getIconSvg() returns the graphic of a registered icon set. Both are assigned to " +
                    "the svg property, while size sets the viewBox.",
            ),
        )
    val star = createIcon {
        svg = unsafeSvgLiteral(STAR_SVG_FRAGMENT)
        size = 24.0
    }
    star.style.setProperty("--vaadin-icon-size", "2rem")
    star.style.color = "var(--lumo-primary-color)"
    val fromSet = Iconset.getIconSvg("lumo:photo", null)
    val reused = createIcon {
        svg = fromSet.svg
        size = fromSet.size ?: 24.0
    }
    reused.style.setProperty("--vaadin-icon-size", "2rem")
    literals.body.append(example(null, row(star, reused)))
    view.body.append(literals.root)

    return view
}

// ---------------------------------------------------------------------------------------------
// Icon sets (registration, sprites, accessibility, padding, generator)
// ---------------------------------------------------------------------------------------------

private fun buildIconsPage(meta: PageMeta): PageView {
    val view = page(meta)
    // The `vaadin` set is a few hundred SVGs; registering it here keeps the cost on this page.
    // The `lumo` set is registered by the shell at start-up, the call below keeps the page standalone.
    registerVaadinIcons()
    registerLumoIcons()

    // --- Vaadin icons (icons/vaadin-icons.ts) --------------------------------------------------
    val vaadinSet =
        section(
            tr("Vaadin 图标集", "Vaadin icons"),
            tr(
                "registerVaadinIcons() 注册 @vaadin/icons，共六百多个图标，用 vaadin: 前缀引用。",
                "registerVaadinIcons() registers @vaadin/icons, more than six hundred icons used with the " +
                    "vaadin: prefix.",
            ),
        )
    val vaadinButton = createButton().withTheme("icon")
    vaadinButton.ariaLabel = tr("通知", "Notifications")
    vaadinButton.append(createIcon { icon = "vaadin:bell" })
    vaadinSet.body.append(
        example(
            null,
            row(
                createIcon { icon = "vaadin:phone" },
                createIcon { icon = "vaadin:calendar" },
                createIcon { icon = "vaadin:alarm" },
                vaadinButton,
            ),
        ),
    )
    view.body.append(vaadinSet.root)

    // --- Lumo icons (icons/lumo-icons.ts) ----------------------------------------------------
    val lumoSet =
        section(
            tr("Lumo 图标集", "Lumo icons"),
            tr(
                "Lumo 图标是主题自带的 24×24 图形（16×16 有效区域加 4 像素留白），用 registerLumoIcons() 注册。",
                "Lumo icons are the theme's own 24×24 graphics (16×16 active area plus 4 pixels of whitespace), " +
                    "registered by registerLumoIcons().",
            ),
        )
    val lumoButton = createButton().withTheme("icon")
    lumoButton.ariaLabel = tr("通知", "Notifications")
    lumoButton.append(createIcon { icon = "lumo:bell" })
    lumoSet.body.append(
        example(
            null,
            row(
                createIcon { icon = "lumo:photo" },
                createIcon { icon = "lumo:calendar" },
                createIcon { icon = "lumo:clock" },
                lumoButton,
            ),
        ),
    )
    view.body.append(lumoSet.root)

    // --- Mixing both sets (wrapper extra) ----------------------------------------------------
    val mixing =
        section(
            tr("同时使用两个图标集", "Mixing two icon sets"),
            tr(
                "图标集按名字隔离，lumo: 与 vaadin: 可以在同一页混用，同名图标（这里都有 bell）来自不同集合。",
                "Icon sets are isolated by name, so lumo: and vaadin: can be mixed on one page; the same icon " +
                    "name (both have bell) comes from different sets.",
            ),
        )
    mixing.body.append(
        example(
            null,
            row(
                createIcon { icon = "lumo:checkmark" },
                createIcon { icon = "vaadin:check" },
                createIcon { icon = "lumo:bell" },
                createIcon { icon = "vaadin:bell" },
            ),
        ),
    )
    view.body.append(mixing.root)

    // --- Accessibility (icons/icons-accessibility.ts) ---------------------------------------
    val accessibility =
        section(
            tr("无障碍", "Accessibility"),
            tr(
                "图标本身对辅助技术完全隐藏，所以承载它的组件要提供可访问名称；图标单独传达信息时，" +
                    "再加 role=\"img\" 与 aria-label。",
                "The icon itself is hidden from assistive technology, so the component around it must carry the " +
                    "accessible name; when an icon conveys information on its own, add role=\"img\" and aria-label.",
            ),
        )
    val closeButton = createButton().withTheme("icon")
    closeButton.ariaLabel = tr("关闭对话框", "Close dialog")
    closeButton.append(createIcon { icon = "vaadin:close" })
    val favourite = createIcon { icon = "vaadin:star" }
    favourite.setAttribute("role", "img")
    favourite.ariaLabel = tr("收藏", "Favorite")
    accessibility.body.append(
        example(null, row(closeButton, favourite)),
        example(
            tr("第二个图标带 role=\"img\" 与 aria-label=\"收藏\"", "The second icon has role=\"img\" and an aria-label"),
            paragraph(
                tr(
                    "用开发者工具的无障碍面板查看两者的名称。",
                    "Check both accessible names in the accessibility pane of the devtools.",
                ),
                "small muted",
            ),
        ),
    )
    view.body.append(accessibility.root)

    // --- Standalone SVG (icons/svg-standalone.ts) --------------------------------------------
    val standalone =
        section(
            tr("独立 SVG 文件", "Standalone SVG images"),
            tr(
                "src 可以是任何 URL，图标会被内联成 SVG，因此可以用 CSS 着色；demo 使用从官方文档复制来的同一份 SVG 文件。",
                "src accepts any URL and the graphic is inlined as SVG, so CSS can tint it; the demo uses the " +
                    "same SVG file copied from the official documentation.",
            ),
        )
    standalone.body.append(example(null, createIcon { src = Images.codeBranchIcon }))
    view.body.append(standalone.root)

    // --- SVG sprites (icons/svg-sprites.ts) -------------------------------------------------
    val sprites =
        section(
            tr("SVG sprite", "SVG sprites"),
            tr(
                "sprite 是包含多个 <symbol> 的 SVG 文件：src 指向文件，symbol 指定要用的 id。demo 使用与官方示例相同的 sprite 文件。",
                "A sprite is an SVG file holding several <symbol> elements: src points at the file and symbol " +
                    "names the id to use. The demo uses the same sprite file as the official example.",
            ),
        )
    sprites.body.append(
        example(
            null,
            row(
                createIcon {
                    src = Images.solidSprite
                    symbol = "code-branch"
                },
                createIcon {
                    src = Images.solidSprite
                    symbol = "user"
                },
            ),
        ),
    )
    view.body.append(sprites.root)

    // --- Padding (icons/icons-padding.ts) ---------------------------------------------------
    val padding =
        section(
            tr("内边距", "Padding"),
            tr(
                "外框固定为 3rem，同时把 --vaadin-icon-visual-size 调小，就能给图形四周留出空白；" +
                    "外框本身的缩放见 Icon 页面的「尺寸」一节。",
                "With a 3rem box and a smaller --vaadin-icon-visual-size the graphic gains whitespace around it; " +
                    "scaling the box itself is covered by the Size section of the Icon page.",
            ),
        )
    fun paddedIcon(visualSize: String?): HTMLElement =
        createIcon { src = Images.codeBranchIcon }.apply {
            style.setProperty("--vaadin-icon-size", "3rem")
            if (visualSize != null) style.setProperty("--vaadin-icon-visual-size", visualSize)
        }
    val paddedRow = row(paddedIcon(null), paddedIcon("2rem"), paddedIcon("1rem"))
    paddedRow.style.setProperty("align-items", "flex-end")
    padding.body.append(example(null, paddedRow))
    view.body.append(padding.root)

    // --- Icon set generator (icons/iconset-generator.ts, icons/iconset-helpers.ts) -----------
    val generator =
        section(
            tr("图标集生成器", "Icon set generator"),
            tr(
                "官方页面上的生成器把上传的 SVG 文件夹转成 <vaadin-iconset> 定义（.js）和 Java 枚举。演示站不" +
                    "接收文件，改为在运行时用同样的结构建立图标集：每个图标是一个带 id 的 <g>，id 以「集合名:」" +
                    "开头，size 声明正方形画布尺寸。",
                "The generator on the docs page turns an uploaded folder of SVGs into a <vaadin-iconset> " +
                    "definition (.js) plus a Java enum. The demo takes no files and instead builds the same " +
                    "structure at runtime: every icon is a <g> with an id prefixed by \"set-name:\", and size " +
                    "declares the square canvas.",
            ),
        )
    val iconset = createIconset()
    val iconsetSvg = document.createElementNS(SVG_NAMESPACE, SvgTagName.svg)
    val defs = document.createElementNS(SVG_NAMESPACE, SvgTagName.defs)
    defs.append(
        svgIconGroup(
            "demo:heart",
            "M12 20.5 4.8 13.3a4.6 4.6 0 0 1 6.5-6.5l.7.7.7-.7a4.6 4.6 0 0 1 6.5 6.5z",
        ),
        svgIconGroup(
            "demo:star",
            "M12 2.5l2.9 5.9 6.6.9-4.8 4.7 1.2 6.6-5.9-3.1-5.9 3.1 1.2-6.6L2.5 9.3l6.6-.9z",
        ),
    )
    iconsetSvg.append(defs)
    iconset.append(iconsetSvg)
    iconset.size = 24.0
    // Setting the name last registers the set: the element collects every `[id]` child at that point.
    iconset.name = "demo"
    generator.body.append(
        example(
            tr("生成的文件等价于下面的结构", "The generated file is equivalent to the structure below"),
            column(
                code("<vaadin-iconset name=\"demo\" size=\"24\">…</vaadin-iconset>"),
                row(
                    createIcon { icon = "demo:heart" }.apply { style.color = "crimson" },
                    createIcon { icon = "demo:star" }.apply { style.color = "goldenrod" },
                    iconset,
                ),
            ),
        ),
    )
    view.body.append(generator.root)

    return view
}

/** Builds one `<g id="name">` entry of an iconset from a single path, filled with `currentColor`. */
private fun svgIconGroup(id: String, path: String): SVGGElement {
    val group = document.createElementNS(SVG_NAMESPACE, SvgTagName.g)
    group.setAttribute("id", id)
    group.setAttribute("fill", "currentColor")
    val shape = document.createElementNS(SVG_NAMESPACE, SvgTagName.path)
    shape.setAttribute("d", path)
    group.append(shape)
    return group
}

// ---------------------------------------------------------------------------------------------
// Progress bar
// ---------------------------------------------------------------------------------------------

private fun buildProgressBarPage(meta: PageMeta): PageView {
    val view = page(meta)

    // --- Basic usage (progressbar/progress-bar-basic.ts) --------------------------------------
    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "默认范围是 0–1，value 是当前进度；下面的进度条停在 50%。",
                "The default range is 0–1 and value is the current progress; the bar below sits at 50%.",
            ),
        )
    val half = createProgressBar { value = 0.5 }
    basic.body.append(example(null, half))
    view.body.append(basic.root)

    // --- Determinate (progressbar/progress-bar-determinate.ts) --------------------------------
    val determinate =
        section(
            tr("确定进度", "Determinate"),
            tr(
                "确定模式下 value 按 (value - min) / (max - min) 的比例填充；官方示例只展示静态的 0.5，" +
                    "这里用按钮把它变成可操作的例子。",
                "In determinate mode the bar fills by (value - min) / (max - min); the official example only " +
                    "shows a static 0.5, buttons make it interactive here.",
            ),
        )
    val progress =
        createProgressBar {
            min = 0.0
            max = 100.0
            value = 35.0
        }
    val readout = Readout("value = 35")
    val increase =
        createButton { textContent = "+10" }.withTheme("tertiary").apply {
            clickEvent.addHandler {
                progress.value = (progress.value + 10.0).coerceAtMost(progress.max)
                readout.set("value = ${progress.value}")
            }
        }
    val reset =
        createButton { textContent = tr("重置", "Reset") }.withTheme("tertiary").apply {
            clickEvent.addHandler {
                progress.value = 0.0
                readout.set("value = 0.0")
            }
        }
    determinate.body.append(example(null, column(progress, row(increase, reset, readout.root))))
    view.body.append(determinate.root)

    // --- Indeterminate (progressbar/progress-bar-indeterminate.ts) ---------------------------
    val indeterminateSection =
        section(
            tr("不确定进度", "Indeterminate"),
            tr(
                "indeterminate 优先于 min / max / value：打开后忽略数值，显示循环动画。",
                "indeterminate takes precedence over min / max / value: the value is ignored and a looping " +
                    "animation is shown.",
            ),
        )
    val indeterminateBar = createProgressBar { indeterminate = true }
    val toggle = createSwitch { label = "indeterminate" }
    toggle.checked = true
    toggle.listen(SwitchEvents.checkedChanged) { indeterminateBar.indeterminate = toggle.checked }
    indeterminateSection.body.append(example(null, column(indeterminateBar, toggle)))
    view.body.append(indeterminateSection.root)

    // --- Bounds and initial value (progressbar/progress-bar-custom-range.ts) -----------------
    val range =
        section(
            tr("范围与初始值", "Bounds and initial value"),
            tr(
                "min / max 不必是 0–1；0–100 配 value=\"50\" 与默认范围的 0.5 等价，只是数值读起来更直观。",
                "min / max do not have to be 0–1; 0–100 with value=\"50\" is the same as 0.5 in the default " +
                    "range, only easier to read in code.",
            ),
        )
    range.body.append(
        example(
            null,
            createProgressBar {
                min = 0.0
                max = 100.0
                value = 50.0
            },
        ),
    )
    view.body.append(range.root)

    // --- Provide a label (progressbar/progress-bar-label.ts) --------------------------------
    val labelSection =
        section(
            tr("提供标签", "Provide a label"),
            tr(
                "进度条没有可见标签：用 aria-labelledby 把外部的 label 关联起来，百分比放在行尾。",
                "The bar has no visible label: aria-labelledby associates an external label, and the percentage " +
                    "sits at the trailing edge.",
            ),
        )
    val labelId = "progress-bar-label"
    val barLabel =
        element(HtmlTagName.label).apply {
            textContent = tr("正在处理 Financials.xlsx", "Processing Financials.xlsx")
            setAttribute("id", labelId)
        }
    val labelled = createProgressBar { value = 0.5 }
    labelled.setAttribute("aria-labelledby", labelId)
    val labelledHeader = row(barLabel, span("50%"))
    labelledHeader.style.setProperty("justify-content", "space-between")
    labelledHeader.style.setProperty("margin-bottom", "var(--lumo-space-s)")
    labelSection.body.append(example(null, column(labelledHeader, labelled)))
    view.body.append(labelSection.root)

    // --- Estimate completion time (progressbar/progress-bar-completion-time.ts) --------------
    val completion =
        section(
            tr("估计完成时间", "Estimate completion time"),
            tr(
                "时间未知时用不确定进度，并用 aria-describedby 说明大概要多久；文字提示同时让所有人看到。",
                "Use an indeterminate bar when the time is unknown and aria-describedby to explain roughly how " +
                    "long it takes; the hint is visible to everyone as well.",
            ),
        )
    val titleId = "progress-bar-title"
    val hintId = "progress-bar-hint"
    val title =
        element(HtmlTagName.label).apply {
            textContent = tr("正在生成报表…", "Generating report...")
            setAttribute("id", titleId)
        }
    val waiting = createProgressBar { indeterminate = true }
    waiting.setAttribute("aria-labelledby", titleId)
    waiting.setAttribute("aria-describedby", hintId)
    waiting.style.setProperty("margin", "var(--lumo-space-s) 0")
    val hint =
        span(tr("处理可能需要十分钟以上", "Process can take upwards of 10 minutes"), "small muted").apply {
            setAttribute("id", hintId)
        }
    completion.body.append(example(null, column(title, waiting, hint)))
    view.body.append(completion.root)

    return view
}

// ---------------------------------------------------------------------------------------------
// Input container (internal element, documented under Text Field)
// ---------------------------------------------------------------------------------------------

private fun buildInputContainerPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "容器只负责布局与状态，输入元素由使用方提供；prefix / suffix 插槽放图标或按钮。",
                "The container only handles layout and state; the input element is supplied by the caller. " +
                    "The prefix/suffix slots hold icons or buttons.",
            ),
        )
    val container = createInputContainer()
    container.dataset["test"] = "input-container"
    val input = element(HtmlTagName.input)
    input.style.setProperty("border", "none")
    input.style.setProperty("outline", "none")
    input.style.setProperty("background", "transparent")
    input.style.setProperty("font", "inherit")
    input.style.setProperty("color", "inherit")
    input.style.setProperty("width", "100%")
    container.append(createIcon { icon = "lumo:search" }.apply { slot = "prefix" }, input)
    container.append(createIcon { icon = "lumo:checkmark" }.apply { slot = "suffix" })
    basic.body.append(example(null, container))
    view.body.append(basic.root)

    val states =
        section(
            tr("状态", "States"),
            tr(
                "readonly / invalid / disabled 三个属性直接对应视觉状态。",
                "The readonly / invalid / disabled properties map directly to visual states.",
            ),
        )
    states.body.append(
        example(
            null,
            row(
                plainContainer(tr("只读", "Read-only"), readonly = true),
                plainContainer(tr("无效", "Invalid"), invalid = true),
                plainContainer(tr("禁用", "Disabled"), disabled = true),
            ),
        ),
    )
    view.body.append(states.root)

    return view
}

/** Builds an input container with a plain text input inside, for the state examples. */
private fun plainContainer(label: String, readonly: Boolean = false, invalid: Boolean = false, disabled: Boolean = false): HTMLElement {
    val container = createInputContainer()
    container.readonly = readonly
    container.invalid = invalid
    container.disabled = disabled
    val input = element(HtmlTagName.input)
    input.value = label
    input.ariaLabel = label
    input.style.setProperty("border", "none")
    input.style.setProperty("outline", "none")
    input.style.setProperty("background", "transparent")
    input.style.setProperty("font", "inherit")
    input.style.setProperty("color", "inherit")
    input.style.setProperty("width", "100%")
    container.append(input)
    return div().apply {
        style.setProperty("width", "12rem")
        append(container, paragraph(label, "small muted"))
    }
}
