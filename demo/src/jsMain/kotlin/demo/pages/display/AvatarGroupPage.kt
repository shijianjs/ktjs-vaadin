package demo.pages.display

import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.Blocks.col as kitCol
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import demo.CatalogEntry
import demo.Catalog
import demo.Groups
import demo.bi
import demo.ui.PageFrame
import demo.ui.PageMeta
import demo.ui.Readout
import demo.ui.jsObject
import demo.ui.listen
import js.objects.unsafeJso
import io.github.shijianjs.ktjs.vaadin.avatargroup.AvatarGroupElement
import io.github.shijianjs.ktjs.vaadin.avatargroup.AvatarGroupItem
import io.github.shijianjs.ktjs.vaadin.avatargroup.createAvatarGroup
import io.github.shijianjs.ktjs.vaadin.button.ButtonElement
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.slider.SliderEvents
import io.github.shijianjs.ktjs.vaadin.slider.createSlider
import web.dom.clickEvent
import web.events.addHandler
import kotlin.js.JsAny

internal class AvatarGroupPage : DemoPage() {
    private val texts = Binder(avatarGroupTexts)

    private val frame = PageFrame(ENTRY)

    /** The localised group: its `i18n` is a property, rewritten on every language change. */
    private val localised: AvatarGroupElement = createAvatarGroup()

    /** The member toggle: its label depends on whether the extra member is in the group. */
    private val memberToggle: ButtonElement = createButton()

    private val extraMember = groupItem(name = "Frances Allen")

    private var hasExtraMember = false

    init {
        frame.mount(this)

        // --- Basic usage (avatar/avatar-group-basic.ts) ---------------------------------------------
        val basic = SectionBlock.NAME { show() }
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        val three = SIMULATED_PEOPLE.take(3).map { groupItem(name = it) }
        val group = createAvatarGroup()
        group.dataset["test"] = "avatar-group"
        group.items = three.toTypedArray()
        basic.body.append(exampleBox(null, group))
        body.append(basic.root)

        // --- Maximum number of items (avatar/avatar-group-max-items.ts) -----------------------------
        val limit = SectionBlock.NAME { show() }
        texts.bind(limit.heading) { it.limitHeading }
        texts.bind(limit.hint) { it.limitHint }
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
        limit.body.append(exampleBox(null, kitCol(limited, slider, limitReadout.root)))
        body.append(limit.root)

        // --- Background colour (avatar/avatar-group-bg-color.ts) -----------------------------------
        val colours = SectionBlock.NAME { show() }
        texts.bind(colours.heading) { it.coloursHeading }
        texts.bind(colours.hint) { it.coloursHint }
        val coloured = createAvatarGroup()
        coloured.items =
            SIMULATED_PEOPLE.mapIndexed { index, name -> groupItem(name = name, colorIndex = index.toDouble()) }
                .toTypedArray()
        colours.body.append(exampleBox(null, coloured))
        body.append(colours.root)

        // --- Internationalisation (avatar/avatar-group-internationalisation.ts) --------------------
        val i18n = SectionBlock.NAME { show() }
        texts.bind(i18n.heading) { it.i18nHeading }
        texts.bind(i18n.hint) { it.i18nHint }
        val baseMembers = listOf(groupItem(), groupItem(name = "Ada Lovelace"), groupItem(name = "Grace Hopper"))
        localised.maxItemsVisible = 2.0
        localised.items = baseMembers.toTypedArray()
        memberToggle.addTheme("tertiary")
        memberToggle.clickEvent.addHandler {
            hasExtraMember = !hasExtraMember
            localised.items = (if (hasExtraMember) baseMembers + extraMember else baseMembers).toTypedArray()
            memberToggle.textContent =
                if (hasExtraMember) texts.current.removeMember else texts.current.addMember
        }
        i18n.body.append(exampleBox(null, kitCol(localised, memberToggle)))
        body.append(i18n.root)

        applyTranslation(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = ENTRY.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        localised.i18n = avatarGroupI18n(texts.current)
        memberToggle.textContent =
            if (hasExtraMember) texts.current.removeMember else texts.current.addMember
    }

    companion object : AvatarGroupPageDefaultReference() {
        val ENTRY: CatalogEntry =
            CatalogEntry(
                id = "avatar-group",
                group = Groups.DISPLAY,
                meta =
                    PageMeta(
                        title = bi("Avatar Group", "Avatar Group"),
                        summary = bi("头像组：超出 maxItemsVisible 的部分折叠成计数。", "Avatar group: items beyond maxItemsVisible collapse into a counter."),
                        docsUrl = "${Catalog.DOCS_BASE}/avatar",
                        docsNote = bi("Avatar Group 与 Avatar 共用一页官方文档。", "Avatar Group shares the Avatar documentation page."),
                        sourcePath = "pages/display/AvatarGroupPage.kt",
                    ),
                kitPage = { AvatarGroupPage.NAME() },
            )

        private val english: AvatarGroupTexts = AvatarGroupTexts(
            basicHeading = "Basic usage",
            basicHint = "Each object in items describes one avatar; the official example maps three people from the " +
                "backend, fixed data is used here.",
            limitHeading = "Maximum number of items",
            limitHint = "maxItemsVisible folds the overflowing avatars into an expandable +N avatar; 0 and 1 behave like 2, " +
                "there are always at least two avatars visible.",
            coloursHeading = "Background colour",
            coloursHint = "Setting colorIndex on every item gives each avatar a different user colour.",
            i18nHeading = "Internationalisation",
            i18nHint = "i18n overrides the anonymous tooltip, the group's accessible label (activeUsers, where {count} is " +
                "replaced) and the joined / left announcements made when members change. Those texts are only exposed to " +
                "assistive technology: use the devtools accessibility pane or a screen reader to see them. The official " +
                "example uses Finnish, this demo follows the UI language.",
            anonymous = "Anonymous",
            activeOne = "Currently one active user",
            activeMany = "Currently {count} active users",
            joined = "{user} joined",
            left = "{user} left",
            addMember = "Add member",
            removeMember = "Remove member",
        )
        private val chinese: AvatarGroupTexts = AvatarGroupTexts(
            basicHeading = "基本用法",
            basicHint = "items 里每个对象描述一个头像；官方示例把后端返回的三个人名映射成 items，这里是固定数据。",
            limitHeading = "最多显示数量",
            limitHint = "maxItemsVisible 把多出来的头像折叠成一个可展开的 +N 头像；0 与 1 都按 2 处理，至少显示两个头像。",
            coloursHeading = "背景颜色",
            coloursHint = "给每个 item 设置 colorIndex，头像使用不同的用户色板颜色。",
            i18nHeading = "本地化",
            i18nHint = "i18n 覆盖匿名用户的提示、组的无障碍标签（activeUsers，{count} 会被替换），以及成员增减时" +
                "读屏播报的 joined / left。这些文本只对辅助技术可见：用开发者工具或读屏软件查看效果。" +
                "官方示例用芬兰语，这里跟随界面语言。",
            anonymous = "匿名",
            activeOne = "当前有 1 位在线用户",
            activeMany = "当前有 {count} 位在线用户",
            joined = "{user} 加入了",
            left = "{user} 离开了",
            addMember = "添加成员",
            removeMember = "移除成员",
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
        private val avatarGroupTexts: Translations<AvatarGroupTexts> = texts(
            Langs.EN to english,
            Langs.ZH_HANS to chinese,
        )
        /** The `i18n` object of the last example; its strings are the group's accessible names. */
        private fun avatarGroupI18n(copy: AvatarGroupTexts): JsAny =
            jsObject(
                """
                {
                  "anonymous": "${copy.anonymous}",
                  "activeUsers": {
                    "one": "${copy.activeOne}",
                    "many": "${copy.activeMany}"
                  },
                  "joined": "${copy.joined}",
                  "left": "${copy.left}"
                }
                """.trimIndent(),
            )
    }
}
