package demo.pages.display

import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.Blocks.row as kitRow
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.page.Blocks.col as kitCol
import io.github.shijianjs.ktjs.demo.page.DemoPage
import web.aria.AriaRole
import web.aria.img
import web.dom.ElementId
import web.html.HtmlTagName
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import io.github.shijianjs.ktjs.demo.page.Blocks.codeText
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import demo.CatalogEntry
import demo.Catalog
import demo.Groups
import demo.bi
import demo.ui.Images
import demo.ui.PageFrame
import demo.ui.PageMeta
import io.github.shijianjs.ktjs.vaadin.button.ButtonElement
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.icon.IconElement
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.dom.element.inlineStyle
import io.github.shijianjs.ktjs.vaadin.icon.IconCustomProperties
import io.github.shijianjs.ktjs.vaadin.icon.createIconset
import io.github.shijianjs.ktjs.vaadin.icons.registerVaadinIcons
import io.github.shijianjs.ktjs.vaadin.lumostyles.registerLumoIcons
import web.dom.document
import web.cssom.Length
import web.cssom.rem
import web.html.HTMLParagraphElement
import web.html.HTMLElement
import web.svg.SVGGElement
import web.svg.SVG_NAMESPACE
import web.svg.SvgTagName
import demo.ui.MUTED_SMALL

// ---------------------------------------------------------------------------------------------
// Icon sets (registration, sprites, accessibility, padding, generator)
// ---------------------------------------------------------------------------------------------

internal class IconSetsPage : DemoPage() {
    private val texts = Binder(iconSetsTexts)

    private val frame = PageFrame(ENTRY)

    /** The two icon-only buttons and the standalone icon: their names are accessible-name attributes. */
    private val vaadinButton: ButtonElement = createButton()
    private val lumoButton: ButtonElement = createButton()
    private val closeButton: ButtonElement = createButton()
    private val favourite: IconElement = createIcon()

    /** The note under the favourite icon and the caption of the generator example. */
    private val favouriteNote: HTMLParagraphElement = HtmlTagName.p()

    init {
        frame.mount(this)
        // The `vaadin` set is a few hundred SVGs; registering it here keeps the cost on this page.
        // The `lumo` set is registered by the shell at start-up, the call below keeps the page standalone.
        registerVaadinIcons()
        registerLumoIcons()

        // --- Vaadin icons (icons/vaadin-icons.ts) --------------------------------------------------
        val vaadinSet = SectionBlock.NAME { show() }
        texts.bind(vaadinSet.heading) { it.vaadinHeading }
        texts.bind(vaadinSet.hint) { it.vaadinHint }
        vaadinButton.addTheme("icon")
        vaadinButton.append(createIcon { icon = "vaadin:bell" })
        vaadinSet.body.append(
            exampleBox(
                null,
                kitRow(
                    createIcon { icon = "vaadin:phone" },
                    createIcon { icon = "vaadin:calendar" },
                    createIcon { icon = "vaadin:alarm" },
                    vaadinButton,
                ),
            ),
        )
        body.append(vaadinSet.root)

        // --- Lumo icons (icons/lumo-icons.ts) ----------------------------------------------------
        val lumoSet = SectionBlock.NAME { show() }
        texts.bind(lumoSet.heading) { it.lumoHeading }
        texts.bind(lumoSet.hint) { it.lumoHint }
        lumoButton.addTheme("icon")
        lumoButton.append(createIcon { icon = "lumo:bell" })
        lumoSet.body.append(
            exampleBox(
                null,
                kitRow(
                    createIcon { icon = "lumo:photo" },
                    createIcon { icon = "lumo:calendar" },
                    createIcon { icon = "lumo:clock" },
                    lumoButton,
                ),
            ),
        )
        body.append(lumoSet.root)

        // --- Mixing both sets (wrapper extra) ----------------------------------------------------
        val mixing = SectionBlock.NAME { show() }
        texts.bind(mixing.heading) { it.mixingHeading }
        texts.bind(mixing.hint) { it.mixingHint }
        mixing.body.append(
            exampleBox(
                null,
                kitRow(
                    createIcon { icon = "lumo:checkmark" },
                    createIcon { icon = "vaadin:check" },
                    createIcon { icon = "lumo:bell" },
                    createIcon { icon = "vaadin:bell" },
                ),
            ),
        )
        body.append(mixing.root)

        // --- Accessibility (icons/icons-accessibility.ts) ---------------------------------------
        val accessibility = SectionBlock.NAME { show() }
        texts.bind(accessibility.heading) { it.a11yHeading }
        texts.bind(accessibility.hint) { it.a11yHint }
        closeButton.addTheme("icon")
        closeButton.append(createIcon { icon = "vaadin:close" })
        favourite.icon = "vaadin:star"
        favourite.role = AriaRole.img
        favouriteNote.className = MUTED_SMALL
        texts.bind(favouriteNote) { it.favoriteNote }
        // The kit draws the caption as the card's first child; an empty caption is bound afterwards.
        val noteCard = exampleBox("", favouriteNote)
        val noteCaption = noteCard.firstElementChild as HTMLParagraphElement
        texts.bind(noteCaption) { it.favoriteCaption }
        accessibility.body.append(exampleBox(null, kitRow(closeButton, favourite)), noteCard)
        body.append(accessibility.root)

        // --- Standalone SVG (icons/svg-standalone.ts) --------------------------------------------
        val standalone = SectionBlock.NAME { show() }
        texts.bind(standalone.heading) { it.standaloneHeading }
        texts.bind(standalone.hint) { it.standaloneHint }
        standalone.body.append(exampleBox(null, createIcon { src = Images.codeBranchIcon }))
        body.append(standalone.root)

        // --- SVG sprites (icons/svg-sprites.ts) -------------------------------------------------
        val sprites = SectionBlock.NAME { show() }
        texts.bind(sprites.heading) { it.spritesHeading }
        texts.bind(sprites.hint) { it.spritesHint }
        sprites.body.append(
            exampleBox(
                null,
                kitRow(
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
        body.append(sprites.root)

        // --- Padding (icons/icons-padding.ts) ---------------------------------------------------
        val padding = SectionBlock.NAME { show() }
        texts.bind(padding.heading) { it.paddingHeading }
        texts.bind(padding.hint) { it.paddingHint }
        fun paddedIcon(visualSize: Length?): HTMLElement =
            createIcon { src = Images.codeBranchIcon }.apply {
                inlineStyle[IconCustomProperties.SIZE] = 3.rem
                if (visualSize != null) inlineStyle[IconCustomProperties.VISUAL_SIZE] = visualSize
            }
        val paddedRow = kitRow(paddedIcon(null), paddedIcon(2.rem), paddedIcon(1.rem))
        paddedRow.style.setProperty("align-items", "flex-end")
        padding.body.append(exampleBox(null, paddedRow))
        body.append(padding.root)

        // --- Icon set generator (icons/iconset-generator.ts, icons/iconset-helpers.ts) -----------
        val generator = SectionBlock.NAME { show() }
        texts.bind(generator.heading) { it.generatorHeading }
        texts.bind(generator.hint) { it.generatorHint }
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
        val generatorCard = exampleBox(
            "",
            kitCol(
                codeText("<vaadin-iconset name=\"demo\" size=\"24\">…</vaadin-iconset>"),
                kitRow(
                    createIcon { icon = "demo:heart" }.apply { style.color = "crimson" },
                    createIcon { icon = "demo:star" }.apply { style.color = "goldenrod" },
                    iconset,
                ),
            ),
        )
        texts.bind(generatorCard.firstElementChild as HTMLParagraphElement) { it.generatorCaption }
        generator.body.append(generatorCard)
        body.append(generator.root)

        applyTranslation(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = ENTRY.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        val copy = texts.current
        vaadinButton.ariaLabel = copy.notifications
        lumoButton.ariaLabel = copy.notifications
        closeButton.ariaLabel = copy.closeDialog
        favourite.ariaLabel = copy.favorite
    }

    companion object : IconSetsPageDefaultReference() {
        val ENTRY: CatalogEntry =
            CatalogEntry(
                id = "icons",
                group = Groups.DISPLAY,
                meta =
                    PageMeta(
                        title = bi("Icon Sets", "Icon Sets"),
                        summary = bi("注册额外的图标集：@vaadin/icons 提供 vaadin:* 的几百个图标。", "Register additional icon sets: @vaadin/icons provides hundreds of vaadin:* icons."),
                        docsUrl = "${Catalog.DOCS_BASE}/icons",
                        sourcePath = "pages/display/IconSetsPage.kt",
                    ),
                kitPage = { IconSetsPage.NAME() },
            )

        private val english: IconSetsTexts = IconSetsTexts(
            vaadinHeading = "Vaadin icons",
            vaadinHint = "registerVaadinIcons() registers @vaadin/icons, more than six hundred icons used with the " +
                "vaadin: prefix.",
            notifications = "Notifications",
            lumoHeading = "Lumo icons",
            lumoHint = "Lumo icons are the theme's own 24×24 graphics (16×16 active area plus 4 pixels of whitespace), " +
                "registered by registerLumoIcons().",
            mixingHeading = "Mixing two icon sets",
            mixingHint = "Icon sets are isolated by name, so lumo: and vaadin: can be mixed on one page; the same icon " +
                "name (both have bell) comes from different sets.",
            a11yHeading = "Accessibility",
            a11yHint = "The icon itself is hidden from assistive technology, so the component around it must carry the " +
                "accessible name; when an icon conveys information on its own, add role=\"img\" and aria-label.",
            closeDialog = "Close dialog",
            favorite = "Favorite",
            favoriteCaption = "The second icon has role=\"img\" and an aria-label",
            favoriteNote = "Check both accessible names in the accessibility pane of the devtools.",
            standaloneHeading = "Standalone SVG images",
            standaloneHint = "src accepts any URL and the graphic is inlined as SVG, so CSS can tint it; the demo uses " +
                "the same SVG file copied from the official documentation.",
            spritesHeading = "SVG sprites",
            spritesHint = "A sprite is an SVG file holding several <symbol> elements: src points at the file and symbol " +
                "names the id to use. The demo uses the same sprite file as the official example.",
            paddingHeading = "Padding",
            paddingHint = "With a 3rem box and a smaller --vaadin-icon-visual-size the graphic gains whitespace around " +
                "it; scaling the box itself is covered by the Size section of the Icon page.",
            generatorHeading = "Icon set generator",
            generatorHint = "The generator on the docs page turns an uploaded folder of SVGs into a <vaadin-iconset> " +
                "definition (.js) plus a Java enum. The demo takes no files and instead builds the same structure at " +
                "runtime: every icon is a <g> with an id prefixed by \"set-name:\", and size declares the square canvas.",
            generatorCaption = "The generated file is equivalent to the structure below",
        )
        private val chinese: IconSetsTexts = IconSetsTexts(
            vaadinHeading = "Vaadin 图标集",
            vaadinHint = "registerVaadinIcons() 注册 @vaadin/icons，共六百多个图标，用 vaadin: 前缀引用。",
            notifications = "通知",
            lumoHeading = "Lumo 图标集",
            lumoHint = "Lumo 图标是主题自带的 24×24 图形（16×16 有效区域加 4 像素留白），用 registerLumoIcons() 注册。",
            mixingHeading = "同时使用两个图标集",
            mixingHint = "图标集按名字隔离，lumo: 与 vaadin: 可以在同一页混用，同名图标（这里都有 bell）来自不同集合。",
            a11yHeading = "无障碍",
            a11yHint = "图标本身对辅助技术完全隐藏，所以承载它的组件要提供可访问名称；图标单独传达信息时，" +
                "再加 role=\"img\" 与 aria-label。",
            closeDialog = "关闭对话框",
            favorite = "收藏",
            favoriteCaption = "第二个图标带 role=\"img\" 与 aria-label=\"收藏\"",
            favoriteNote = "用开发者工具的无障碍面板查看两者的名称。",
            standaloneHeading = "独立 SVG 文件",
            standaloneHint = "src 可以是任何 URL，图标会被内联成 SVG，因此可以用 CSS 着色；demo 使用从官方文档复制来的" +
                "同一份 SVG 文件。",
            spritesHeading = "SVG sprite",
            spritesHint = "sprite 是包含多个 <symbol> 的 SVG 文件：src 指向文件，symbol 指定要用的 id。" +
                "demo 使用与官方示例相同的 sprite 文件。",
            paddingHeading = "内边距",
            paddingHint = "外框固定为 3rem，同时把 --vaadin-icon-visual-size 调小，就能给图形四周留出空白；" +
                "外框本身的缩放见 Icon 页面的「尺寸」一节。",
            generatorHeading = "图标集生成器",
            generatorHint = "官方页面上的生成器把上传的 SVG 文件夹转成 <vaadin-iconset> 定义（.js）和 Java 枚举。" +
                "演示站不接收文件，改为在运行时用同样的结构建立图标集：每个图标是一个带 id 的 <g>，id 以「集合名:」" +
                "开头，size 声明正方形画布尺寸。",
            generatorCaption = "生成的文件等价于下面的结构",
        )
        private val iconSetsTexts: Translations<IconSetsTexts> = texts(
            Langs.EN to english,
            Langs.ZH_HANS to chinese,
        )
        /** Builds one `<g id="name">` entry of an iconset from a single path, filled with `currentColor`. */
        private fun svgIconGroup(id: String, path: String): SVGGElement {
            val group = document.createElementNS(SVG_NAMESPACE, SvgTagName.g)
            group.id = ElementId(id)
            group.setAttribute("fill", "currentColor")
            val shape = document.createElementNS(SVG_NAMESPACE, SvgTagName.path)
            shape.setAttribute("d", path)
            group.append(shape)
            return group
        }
    }
}
