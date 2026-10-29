package demo.pages.display

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.pages.overlays.ariaLabel
import demo.tr
import demo.ui.Images
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.code
import demo.ui.column
import demo.ui.example
import demo.ui.page
import demo.ui.paragraph
import demo.ui.row
import demo.ui.section
import demo.ui.withTheme
import vaadin.button.createButton
import vaadin.icon.createIcon
import vaadin.icon.createIconset
import vaadin.icons.registerVaadinIcons
import vaadin.lumostyles.registerLumoIcons
import web.dom.document
import web.html.HTMLElement
import web.svg.SVGGElement
import web.svg.SVG_NAMESPACE
import web.svg.SvgTagName

val iconsEntry: CatalogEntry =
    CatalogEntry(
        id = "icons",
        group = Groups.DISPLAY,
        meta =
            PageMeta(
                title = bi("Icon Sets", "Icon Sets"),
                summary = bi("注册额外的图标集：@vaadin/icons 提供 vaadin:* 的几百个图标。", "Register additional icon sets: @vaadin/icons provides hundreds of vaadin:* icons."),
                docsUrl = "$DOCS_BASE/icons",
                sourcePath = "pages/display/IconSetsPage.kt",
            ),
        builder = ::buildIconsPage,
    )

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
