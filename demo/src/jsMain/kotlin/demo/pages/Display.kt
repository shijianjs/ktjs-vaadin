package demo.pages

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.ui.EventLog
import demo.ui.PageMeta
import demo.ui.Readout
import demo.ui.column
import demo.ui.div
import demo.ui.element
import demo.ui.example
import demo.ui.jsArray
import demo.ui.listen
import demo.ui.page
import demo.ui.paragraph
import demo.ui.row
import demo.ui.section
import demo.ui.withTheme
import vaadin.avatar.createAvatar
import vaadin.avatargroup.AvatarGroupItem
import vaadin.avatargroup.createAvatarGroup
import vaadin.badge.createBadge
import vaadin.button.createButton
import vaadin.icon.createIcon
import vaadin.icons.registerVaadinIcons
import vaadin.inputcontainer.createInputContainer
import vaadin.progressbar.createProgressBar
import vaadin.switch.SwitchEvents
import vaadin.switch.createSwitch
import web.dom.clickEvent
import web.events.addHandler
import web.html.HTMLElement
import web.html.HtmlTagName

val displayEntries: List<CatalogEntry> =
    listOf(
        CatalogEntry(
            id = "avatar",
            group = Groups.DISPLAY,
            meta =
                PageMeta(
                    title = "Avatar",
                    summary = "用户头像：缩写、图片、颜色索引与悬停提示。",
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
                    title = "Avatar Group",
                    summary = "头像组：超出 maxItemsVisible 的部分折叠成计数。",
                    docsUrl = "$DOCS_BASE/avatar",
                    docsNote = "Avatar Group 与 Avatar 共用一页官方文档。",
                    sourcePath = "pages/Display.kt",
                ),
            builder = ::buildAvatarGroupPage,
        ),
        CatalogEntry(
            id = "badge",
            group = Groups.DISPLAY,
            meta =
                PageMeta(
                    title = "Badge",
                    summary = "数字或文本标记：number 决定显示的数字，theme 决定颜色。",
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
                    title = "Icon",
                    summary = "图标元素：icon 名称、尺寸、SVG symbol 与字体图标。",
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
                    title = "Icon Sets",
                    summary = "注册额外的图标集：@vaadin/icons 提供 vaadin:* 的几百个图标。",
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
                    title = "Progress Bar",
                    summary = "进度条：确定值与不确定状态，min / max 决定比例。",
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
                    title = "Input Container",
                    summary = "输入类字段的底层容器：前缀 / 后缀插槽、invalid 与 readonly 状态。",
                    docsUrl = "$DOCS_BASE/text-field",
                    docsNote = "vaadin-input-container 是内部基础元素，官方文档见 Text Field。",
                    sourcePath = "pages/Display.kt",
                ),
            builder = ::buildInputContainerPage,
        ),
    )

/**
 * Inline SVG avatar, so the page never depends on a remote image (the end-to-end suite fails on any
 * failed request).
 */
private const val AVATAR_DATA_URI =
    "data:image/svg+xml;utf8," +
        "%3Csvg xmlns='http://www.w3.org/2000/svg' width='80' height='80'%3E" +
        "%3Crect width='80' height='80' fill='%235b8def'/%3E" +
        "%3Ccircle cx='40' cy='30' r='14' fill='%23fff'/%3E" +
        "%3Cpath d='M12 80c0-16 12-26 28-26s28 10 28 26z' fill='%23fff'/%3E" +
        "%3C/svg%3E"

private fun buildAvatarPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("基本用法", "有 img 时显示图片，否则显示 abbr；name 用作无障碍名称。")
    basic.body.append(
        example(
            null,
            row(
                createAvatar {
                    name = "Ada Lovelace"
                    abbr = "AL"
                    colorIndex = 0.0
                },
                createAvatar {
                    name = "Grace Hopper"
                    abbr = "GH"
                    colorIndex = 1.0
                    withTooltip = true
                },
                createAvatar { name = "无缩写" },
            ),
        ),
    )
    view.body.append(basic.root)

    val colors = section("颜色索引", "colorIndex 选择 Lumo 的用户色板，共 7 个颜色。")
    colors.body.append(
        example(
            null,
            row(*(0..6).map { index ->
                createAvatar {
                    name = "用户 $index"
                    abbr = "U$index"
                    colorIndex = index.toDouble()
                }
            }.toTypedArray()),
        ),
    )
    view.body.append(colors.root)

    val image = section("图片", "img 使用头像图片，同时保留 abbr 作为加载失败时的回退。")
    image.body.append(
        example(
            null,
            row(
                createAvatar {
                    name = "带图片的头像"
                    abbr = "IMG"
                    img = AVATAR_DATA_URI
                },
            ),
        ),
    )
    view.body.append(image.root)

    return view.root
}

private fun buildAvatarGroupPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val items: Array<AvatarGroupItem> =
        kotlin.js.js(
            """([
              {"name": "Ada Lovelace", "abbr": "AL", "colorIndex": 0},
              {"name": "Grace Hopper", "abbr": "GH", "colorIndex": 1},
              {"name": "Margaret Hamilton", "abbr": "MH", "colorIndex": 2},
              {"name": "Katherine Johnson", "abbr": "KJ", "colorIndex": 3},
              {"name": "Barbara Liskov", "abbr": "BL", "colorIndex": 4}
            ])""",
        )

    val basic = section("基本用法", "items 里每个对象描述一个头像，超过 maxItemsVisible 的部分折叠成 +N。")
    val group = createAvatarGroup { maxItemsVisible = 3.0 }
    group.dataset["test"] = "avatar-group"
    group.items = items
    basic.body.append(example(null, group))
    view.body.append(basic.root)

    val limit = section("可见数量", "maxItemsVisible 越小，折叠得越多。")
    val readout = Readout("maxItemsVisible = 3")
    val limited = createAvatarGroup { maxItemsVisible = 3.0 }
    limited.items = items
    val slider =
        vaadin.slider.createSlider {
            label = "maxItemsVisible"
            min = 1.0
            max = 5.0
            step = 1.0
            value = 3.0
            valueAlwaysVisible = true
        }
    slider.listen(vaadin.slider.SliderEvents.valueChanged) {
        val visible = slider.value.toInt()
        limited.maxItemsVisible = visible.toDouble()
        readout.set("maxItemsVisible = $visible")
    }
    limit.body.append(example(null, column(limited, slider, readout.root)))
    view.body.append(limit.root)

    return view.root
}

private fun buildBadgePage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("基本用法", "number 是显示的数字；没有 number 时显示默认插槽文本。")
    val badge = createBadge { number = 4.0 }
    badge.dataset["test"] = "badge"
    basic.body.append(
        example(
            null,
            row(
                badge,
                createBadge { number = 128.0 },
                createBadge().apply { textContent = "NEW" },
            ),
        ),
    )
    view.body.append(basic.root)

    val themes = section("主题变体", "success / error / contrast / pill 改变颜色与形状。")
    themes.body.append(
        example(
            null,
            row(
                createBadge { number = 1.0 }.withTheme("success"),
                createBadge { number = 2.0 }.withTheme("error"),
                createBadge { number = 3.0 }.withTheme("contrast"),
                createBadge { number = 12.0 }.withTheme("pill"),
            ),
        ),
    )
    view.body.append(themes.root)

    val dynamic = section("动态更新", "number 可以随时修改，徽标会立即更新。")
    val live = createBadge { number = 0.0 }
    live.dataset["test"] = "badge-live"
    val log = EventLog(3)
    val addButton =
        createButton { textContent = "加一" }.withTheme("tertiary").apply {
            clickEvent.addHandler {
                live.number += 1.0
                log.add("number = ${live.number}")
            }
        }
    dynamic.body.append(example(null, column(row(live, addButton), log.root)))
    view.body.append(dynamic.root)

    return view.root
}

private fun buildIconPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("基本用法", "icon 属性接受 图标集:名称，Lumo 图标集在本页启动时已注册。")
    basic.body.append(
        example(
            null,
            row(
                createIcon { icon = "lumo:checkmark" },
                createIcon { icon = "lumo:bell" },
                createIcon { icon = "lumo:calendar" },
                createIcon { icon = "lumo:download" },
                createIcon { icon = "lumo:eye" },
                createIcon { icon = "lumo:search" },
            ),
        ),
    )
    view.body.append(basic.root)

    val sizing = section("尺寸", "size 以像素为单位设置图标大小。")
    sizing.body.append(
        example(
            null,
            row(*(16..40 step 8).map { size ->
                createIcon {
                    icon = "lumo:user"
                    this.size = size.toDouble()
                }
            }.toTypedArray()),
        ),
    )
    view.body.append(sizing.root)

    val fontIcon = section("字体图标", "ligature + fontFamily 使用图标字体，char 直接指定字符。")
    fontIcon.body.append(
        example(
            null,
            row(
                createIcon {
                    ligature = "check"
                    fontFamily = "lumo-icons"
                },
                createIcon { char = "★" },
            ),
        ),
    )
    view.body.append(fontIcon.root)

    return view.root
}

private fun buildIconsPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    // The `vaadin` icon set is a few hundred SVGs; registering it here keeps the cost on this page.
    registerVaadinIcons()

    val basic = section("vaadin 图标集", "registerVaadinIcons() 注册 @vaadin/icons；之后可以直接用 vaadin: 前缀。")
    basic.body.append(
        example(
            null,
            row(
                createIcon { icon = "vaadin:home" },
                createIcon { icon = "vaadin:user" },
                createIcon { icon = "vaadin:calendar" },
                createIcon { icon = "vaadin:cog" },
                createIcon { icon = "vaadin:chart" },
                createIcon { icon = "vaadin:cloud" },
            ),
        ),
    )
    view.body.append(basic.root)

    val both = section("同时使用两个图标集", "图标集按名字隔离，lumo: 与 vaadin: 可以在同一页混用。")
    both.body.append(
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
    view.body.append(both.root)

    return view.root
}

private fun buildProgressBarPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("基本用法", "value 与 min / max 决定进度；不确定进度用 indeterminate。")
    val progress = createProgressBar {
        min = 0.0
        max = 100.0
        value = 35.0
    }
    progress.dataset["test"] = "progress-bar"
    val readout = Readout("value = 35")
    val addButton =
        createButton { textContent = "+10" }.withTheme("tertiary").apply {
            clickEvent.addHandler {
                progress.value = (progress.value + 10.0).coerceAtMost(100.0)
                readout.set("value = ${progress.value}")
            }
        }
    val resetButton =
        createButton { textContent = "重置" }.withTheme("tertiary").apply {
            clickEvent.addHandler {
                progress.value = 0.0
                readout.set("value = 0")
            }
        }
    basic.body.append(example(null, column(progress, row(addButton, resetButton, readout.root))))
    view.body.append(basic.root)

    val indeterminate = section("不确定进度", "indeterminate 打开后忽略 value，显示循环动画。")
    val indeterminateBar = createProgressBar()
    indeterminateBar.indeterminate = true
    val toggle =
        createSwitch { label = "indeterminate" }
    toggle.checked = true
    toggle.listen(SwitchEvents.checkedChanged) {
        indeterminateBar.indeterminate = toggle.checked
    }
    indeterminate.body.append(example(null, column(indeterminateBar, toggle)))
    view.body.append(indeterminate.root)

    val custom = section("自定义范围", "min / max 不必是 0-100。")
    custom.body.append(
        example(
            null,
            createProgressBar {
                min = 0.0
                max = 10.0
                value = 7.0
            },
        ),
    )
    view.body.append(custom.root)

    return view.root
}

private fun buildInputContainerPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic =
        section(
            "基本用法",
            "容器只负责布局与状态，输入元素由使用方提供；prefix / suffix 插槽放图标或按钮。",
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

    val states = section("状态", "readonly / invalid / disabled 三个属性直接对应视觉状态。")
    states.body.append(
        example(
            null,
            row(
                plainContainer("只读", readonly = true),
                plainContainer("无效", invalid = true),
                plainContainer("禁用", disabled = true),
            ),
        ),
    )
    view.body.append(states.root)

    return view.root
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
