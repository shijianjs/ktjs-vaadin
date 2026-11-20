package demo.pages.display

import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.page.Blocks.row as kitRow
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.page.Blocks.col as kitCol
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.ui.Images
import demo.ui.PageFrame
import demo.ui.PageMeta
import demo.ui.Readout
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.icon.Iconset
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.vaadin.icon.unsafeSvgLiteral
import io.github.shijianjs.ktjs.vaadin.icons.registerVaadinIcons
import io.github.shijianjs.ktjs.vaadin.lumostyles.registerLumoIcons
import web.dom.clickEvent
import web.events.addHandler
import web.html.HTMLElement

val iconEntry: CatalogEntry =
    CatalogEntry(
        id = "icon",
        group = Groups.DISPLAY,
        meta =
            PageMeta(
                title = bi("Icon", "Icon"),
                summary = bi("图标元素：icon 名称、尺寸、SVG symbol 与字体图标。", "Icon element: icon name, size, SVG symbol and font icons."),
                docsUrl = "$DOCS_BASE/icons",
                sourcePath = "pages/display/IconPage.kt",
            ),
        kitPage = { IconPage.NAME() },
    )

// ---------------------------------------------------------------------------------------------
// Icon (single icon: name, size, colour, fonts, svg literal)
// ---------------------------------------------------------------------------------------------

internal class IconPage : DemoPage() {
    private val texts = Binder(iconTexts)

    private val frame = PageFrame(iconEntry)

    /** The icon the picker cycles through; the readout names it, so it moves with the copy. */
    private val picker = createIcon { icon = "lumo:user" }

    private val iconReadout = Readout("icon = lumo:user")

    private val nextIcon = createButton().addTheme("tertiary")

    private val iconNames = listOf("lumo:user", "lumo:bell", "vaadin:user", "vaadin:bell")

    private var iconIndex = 0

    init {
        frame.mount(this)
        registerLumoIcons()
        registerVaadinIcons()

        // --- Basic usage (icons/icon-basic.ts) -----------------------------------------------------
        val basic = SectionBlock.NAME { show() }
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        val gallery =
            kitRow(
                createIcon { icon = "lumo:checkmark" },
                createIcon { icon = "lumo:photo" },
                createIcon { icon = "lumo:calendar" },
                createIcon { icon = "vaadin:phone" },
            )
        picker.style.setProperty("--vaadin-icon-size", "2rem")
        texts.bind(nextIcon) { it.nextIcon }
        nextIcon.clickEvent.addHandler {
            iconIndex = (iconIndex + 1) % iconNames.size
            picker.icon = iconNames[iconIndex]
            iconReadout.set("icon = ${iconNames[iconIndex]}")
        }
        basic.body.append(exampleBox(null, kitCol(gallery, kitRow(picker, nextIcon, iconReadout.root))))
        body.append(basic.root)

        // --- Size (icons/icons-sizing.ts) ---------------------------------------------------------
        val sizing = SectionBlock.NAME { show() }
        texts.bind(sizing.heading) { it.sizingHeading }
        texts.bind(sizing.hint) { it.sizingHint }
        fun sizedIcon(size: String): HTMLElement =
            createIcon { src = Images.codeBranchIcon }.apply {
                style.setProperty("--vaadin-icon-size", size)
            }
        sizing.body.append(
            exampleBox(null, kitRow(sizedIcon("1rem"), sizedIcon("2rem"), sizedIcon("3rem"), sizedIcon("4rem"))),
        )
        body.append(sizing.root)

        // --- Colour (icons/icons-color.ts) -------------------------------------------------------
        val colours = SectionBlock.NAME { show() }
        texts.bind(colours.heading) { it.colourHeading }
        texts.bind(colours.hint) { it.colourHint }
        val redSvg = createIcon { src = Images.codeBranchIcon }.apply { style.color = "red" }
        val redFont =
            createIcon {
                fontFamily = "lumo-icons"
                char = "ea26"
            }.apply { style.color = "red" }
        fun tintedIcon(fill: String): HTMLElement =
            createIcon { icon = "lumo:bell" }.apply { style.setProperty("--vaadin-icon-color", fill) }
        colours.body.append(
            exampleBox(null, kitRow(redSvg, redFont, tintedIcon("green"), tintedIcon("rgb(0 102 240)"))),
        )
        body.append(colours.root)

        // --- Font icons (icons/icon-fonts.ts) ----------------------------------------------------
        val fonts = SectionBlock.NAME { show() }
        texts.bind(fonts.heading) { it.fontsHeading }
        texts.bind(fonts.hint) { it.fontsHint }
        fun fontIcon(char: String, size: String = "1.5rem"): HTMLElement =
            createIcon {
                fontFamily = "lumo-icons"
                this.char = char
            }.apply { style.setProperty("--vaadin-icon-size", size) }
        fonts.body.append(
            exampleBox(null, kitRow(fontIcon("ea0f"), fontIcon("ea04"), fontIcon("ea26"), fontIcon("ea0f", "3rem"))),
        )
        body.append(fonts.root)

        // --- SVG literals (wrapper API; see vaadin/icon/IconTypes.kt) -----------------------------
        val literals = SectionBlock.NAME { show() }
        texts.bind(literals.heading) { it.literalsHeading }
        texts.bind(literals.hint) { it.literalsHint }
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
        literals.body.append(exampleBox(null, kitRow(star, reused)))
        body.append(literals.root)

        applyTranslation(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = iconEntry.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
    }

    companion object : CustomElementReferenceAdapter<IconPage>(IconPage::class.js, "vaadin-demo-icon-page") {
        private val english: IconTexts = IconTexts(
            basicHeading = "Basic usage",
            basicHint = "The icon property takes icon-set:name. registerLumoIcons() and registerVaadinIcons() make sure " +
                "the two built-in sets are registered, otherwise the icons only show up in development mode.",
            nextIcon = "Next icon",
            sizingHeading = "Size",
            sizingHint = "--vaadin-icon-size sets the outer size of the icon (1lh by default) and " +
                "--vaadin-icon-visual-size adjusts the painted area inside it; the size property only affects " +
                "the viewBox of svg literals, see the SVG literals section below.",
            colourHeading = "Colour",
            colourHint = "Icons use currentColor: setting the text colour or --vaadin-icon-color changes the fill and " +
                "stroke, for SVG and font icons alike. The second icon of the official example uses a " +
                "FontAwesome class, replaced by a Lumo font icon here.",
            fontsHeading = "Font icons",
            fontsHint = "fontFamily selects the icon font and char takes a hexadecimal code point (Lumo: ea0f checkmark, " +
                "ea04 angle down, ea26 photo), or a single character; ligature needs a font that provides " +
                "ligatures (Material Icons, for example) and iconClass needs the stylesheet shipped with the " +
                "font. The demo ships no third-party font, so only the Lumo code points are rendered.",
            literalsHeading = "SVG literals",
            literalsHint = "Kotlin/JS cannot author Lit's svg tagged template: unsafeSvgLiteral() builds one from a string, " +
                "and Iconset.getIconSvg() returns the graphic of a registered icon set. Both are assigned to " +
                "the svg property, while size sets the viewBox.",
        )
        private val chinese: IconTexts = IconTexts(
            basicHeading = "基本用法",
            basicHint = "icon 属性接受 图标集:名称。registerLumoIcons() 与 registerVaadinIcons() 保证两个内置集合" +
                "已经注册，否则图标只在开发模式下可见。",
            nextIcon = "下一个图标",
            sizingHeading = "尺寸",
            sizingHint = "--vaadin-icon-size 设置图标的外框尺寸（默认跟随行高 1lh），--vaadin-icon-visual-size 在此基础上" +
                "调整绘制区域；size 属性只影响 svg 字面量的 viewBox，见下面的 SVG 字面量一节。",
            colourHeading = "颜色",
            colourHint = "图标使用 currentColor：设置文字颜色或 --vaadin-icon-color 就能改填充与描边，SVG 图标和字体" +
                "图标都一样。官方示例的第二个图标用 FontAwesome 类名，这里换成 Lumo 字体图标。",
            fontsHeading = "字体图标",
            fontsHint = "fontFamily 选择图标字体，char 接受十六进制码点（Lumo 字体：ea0f 对勾、ea04 下箭头、ea26 图片），" +
                "也可以直接给单个字符；ligature 需要字体本身支持连字（例如 Material Icons），iconClass 需要" +
                "字体自带的样式表。演示站没有引入第三方字体，因此只展示 Lumo 字体按码点渲染的效果。",
            literalsHeading = "SVG 字面量",
            literalsHint = "Kotlin/JS 写不出 Lit 的 svg 模板字面量：用 unsafeSvgLiteral() 从字符串构造一个，或通过 " +
                "Iconset.getIconSvg() 取出已注册图标集里的图形，两者都赋给 svg 属性，size 决定 viewBox。",
        )
        /** A single star, passed to [unsafeSvgLiteral] by the SVG literal example. */
        private const val STAR_SVG_FRAGMENT =
            "<path d='M12 2.5l2.9 5.9 6.6.9-4.8 4.7 1.2 6.6-5.9-3.1-5.9 3.1 1.2-6.6L2.5 9.3l6.6-.9z' " +
                "fill='currentColor'/>"
        private val iconTexts: Translations<IconTexts> = texts(
            Langs.EN to english,
            Langs.ZH_HANS to chinese,
        )
    }
}
