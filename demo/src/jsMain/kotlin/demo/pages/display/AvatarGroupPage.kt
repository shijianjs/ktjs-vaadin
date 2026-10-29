package demo.pages.display

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.tr
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.Readout
import demo.ui.column
import demo.ui.example
import demo.ui.jsObject
import demo.ui.listen
import demo.ui.page
import demo.ui.section
import demo.ui.withTheme
import js.objects.unsafeJso
import vaadin.avatargroup.AvatarGroupItem
import vaadin.avatargroup.createAvatarGroup
import vaadin.button.createButton
import vaadin.slider.SliderEvents
import vaadin.slider.createSlider
import web.dom.clickEvent
import web.events.addHandler

val avatarGroupEntry: CatalogEntry =
    CatalogEntry(
        id = "avatar-group",
        group = Groups.DISPLAY,
        meta =
            PageMeta(
                title = bi("Avatar Group", "Avatar Group"),
                summary = bi("头像组：超出 maxItemsVisible 的部分折叠成计数。", "Avatar group: items beyond maxItemsVisible collapse into a counter."),
                docsUrl = "$DOCS_BASE/avatar",
                docsNote = bi("Avatar Group 与 Avatar 共用一页官方文档。", "Avatar Group shares the Avatar documentation page."),
                sourcePath = "pages/display/AvatarGroupPage.kt",
            ),
        builder = ::buildAvatarGroupPage,
    )

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
