package demo.pages.theming

import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter

import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import web.components.CustomElementReference
import web.dom.TagName
import web.html.HTMLAnchorElement
import web.html.HtmlTagName
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import io.github.shijianjs.ktjs.demo.page.paragraph
import io.github.shijianjs.ktjs.demo.page.codeText
import demo.Language
import demo.LanguageState
import demo.componentApiUrl
import demo.ui.REPO_SOURCE_ROOT
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.page.anchor as kitAnchor
import io.github.shijianjs.ktjs.demo.page.exampleBox
import io.github.shijianjs.ktjs.demo.page.row as kitRow
import io.github.shijianjs.ktjs.demo.page.col as kitCol
import io.github.shijianjs.ktjs.demo.page.sectionBlock
import demo.CatalogEntry
import demo.Groups
import demo.STYLING_BASE
import demo.Theme
import demo.bi
import demo.tr
import demo.ui.EventLog
import demo.ui.PageMeta
import demo.ui.Readout
import io.github.shijianjs.ktjs.vaadin.aura.Aura
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.card.createCard
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.vaadin.lumostyles.LumoThemes
import io.github.shijianjs.ktjs.vaadin.textfield.createTextField
import io.github.shijianjs.ktjs.dom.css.cssSheet
import web.cssom.CSSStyleSheet
import web.cssom.ClassName
import web.cssom.Color
import web.dom.clickEvent
import web.events.addHandler
import web.html.HTMLElement
import demo.ui.SMALL
import demo.ui.MUTED_SMALL
import io.github.shijianjs.ktjs.dom.css.dsl.className
import io.github.shijianjs.ktjs.dom.css.dsl.invoke
import io.github.shijianjs.ktjs.dom.css.dsl.pseudo
import io.github.shijianjs.ktjs.dom.css.dsl.pseudoElement
import web.cssom.ident

/** The utility classes this page shows off, kept in one `ClassName`: Lumo reads them as a list. */
private val UTILITY_BOX: ClassName = ClassName("flex gap-m items-center p-m bg-contrast-5 rounded-m shadow-s")

/**
 * The one rule this page owns: a `::part()` selector has to live in the same shadow tree as the
 * component, and nothing in the kit's blocks can express it. Declared here, next to the example that
 * needs it, instead of in a site-wide stylesheet.
 */
/** The class the example puts on the button so its label part can be reached. */
private val THEMING_PART: ClassName = ClassName("theming-part")

private val THEMING_SHEET: CSSStyleSheet = cssSheet {
    ".theming-part::part(label)" {
        color = Color("var(--lumo-primary-color)")
    }
}

val themingEntry: CatalogEntry =
    CatalogEntry(
        id = "theming",
        group = Groups.THEMING,
        meta =
            PageMeta(
                title = bi("主题与样式", "Theming and styling"),
                summary =
                    bi(
                        "Demo 使用 Lumo 主题：亮色 / 暗色切换、设计令牌与组件主题变体。",
                        "The demo uses the Lumo theme: light / dark switching, design tokens and component " +
                            "theme variants.",
                    ),
                docsUrl = "$STYLING_BASE/themes/lumo",
                sourcePath = "pages/theming/ThemingPage.kt",
            ),
        kitPage = { newThemingPage() },
    )

/**
 * `<vaadin-demo-swatch-chip>`: a colour chip bound to one Lumo custom property.
 *
 * The element itself is the chip, so the property lands on its own style instead of on a child.
 */
internal class SwatchChip : CustomElementAdapter() {
    private var cssVariable: String? = null

    /** Shows the colour held by [cssVariable]. */
    fun show(cssVariable: String) {
        this.cssVariable = cssVariable
        style.setProperty("display", "block")
        style.setProperty("width", "3rem")
        style.setProperty("height", "3rem")
        style.setProperty("border-radius", "var(--lumo-border-radius-m)")
        style.setProperty("border", "1px solid var(--lumo-contrast-20pct)")
        style.setProperty("background", "var($cssVariable)")
    }

    override fun connectedCallback() {
        cssVariable?.let { show(it) }
    }

    companion object : CustomElementReference<SwatchChip>(SwatchChip::class.js) {
        val NAME: TagName<SwatchChip> = TagName("vaadin-demo-swatch-chip")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<vaadin-demo-swatch-chip>`. */
internal fun newSwatchChip(cssVariable: String): SwatchChip {
    SwatchChip.define()
    return SwatchChip.NAME().also { it.show(cssVariable) }
}

/**
 * `<vaadin-demo-entry-point>`: a stylesheet entry-point constant and the value behind it.
 *
 * The element puts its two nodes into itself rather than into a shadow root, because the paragraph
 * and the code block are kit pieces whose look comes from the page's sheet.
 */
internal class EntryPoint : CustomElementAdapter() {
    private var name: String? = null
    private var value: String? = null
    private var built = false

    /** Sets the constant and its value; the row is built once, whichever call comes first. */
    fun show(name: String, value: String) {
        this.name = name
        this.value = value
        build()
    }

    override fun connectedCallback() {
        build()
    }

    private fun build() {
        val name = name ?: return
        val value = value ?: return
        if (built) return
        built = true
        append(
            kitRow(HtmlTagName.p { className = SMALL; textContent = name }, codeText(value)),
        )
    }

    companion object : CustomElementReference<EntryPoint>(EntryPoint::class.js) {
        val NAME: TagName<EntryPoint> = TagName("vaadin-demo-entry-point")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<vaadin-demo-entry-point>`. */
internal fun newEntryPoint(name: String, value: String): EntryPoint {
    EntryPoint.define()
    return EntryPoint.NAME().also { it.show(name, value) }
}

/**
 * `<vaadin-demo-described-entry-point>`: the same row, one whose description is translated through
 * the page's binder.
 */
internal class DescribedEntryPoint : CustomElementAdapter() {
    private val description = HtmlTagName.p { className = SMALL }
    private var built = false

    /** Registers [field] with [binder] and builds the row once. */
    fun show(name: String, binder: Binder<ThemingTexts>, field: (ThemingTexts) -> String) {
        binder.bind(description, field)
        if (built) return
        built = true
        append(kitRow(HtmlTagName.p { className = SMALL; textContent = name }, description))
    }

    companion object : CustomElementReference<DescribedEntryPoint>(DescribedEntryPoint::class.js) {
        val NAME: TagName<DescribedEntryPoint> = TagName("vaadin-demo-described-entry-point")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<vaadin-demo-described-entry-point>`. */
internal fun newDescribedEntryPoint(
    name: String,
    binder: Binder<ThemingTexts>,
    field: (ThemingTexts) -> String,
): DescribedEntryPoint {
    DescribedEntryPoint.define()
    return DescribedEntryPoint.NAME().also { it.show(name, binder, field) }
}

/** Every string this page shows, in one language. */
internal class ThemingTexts(
    val docsLink: String,
    val sourceLink: String,
    val apiLink: String,
    val switchTitle: String,
    val switchHint: String,
    val now: String,
    val nowLight: String,
    val nowDark: String,
    val light: String,
    val dark: String,
    val toggle: String,
    val tokensTitle: String,
    val tokensHint: String,
    val primary: String,
    val success: String,
    val error: String,
    val divider: String,
    val base: String,
    val entryTitle: String,
    val entryHint: String,
    val entryNote: String,
    val variantsTitle: String,
    val variantsHint: String,
    val smallLabel: String,
    val smallPlaceholder: String,
    val helperText: String,
    val cardTitle: String,
    val cardText: String,
    val propertiesTitle: String,
    val propertiesHint: String,
    val styledButton: String,
    val partsTitle: String,
    val partsHint: String,
    val partButton: String,
    val utilitiesTitle: String,
    val utilitiesHint: String,
    val utilitiesText: String,
    val i18nTitle: String,
    val i18nHint: String,
    val i18nText: String,
    val internalsTitle: String,
    val internalsHint: String,
    val internalsIntro: String,
    val elementMixin: String,
    val inputControl: String,
    val tagNames: String,
)

private val themingEnglish: ThemingTexts = ThemingTexts(
    docsLink = "Vaadin documentation",
    sourceLink = "Kotlin source",
    apiLink = "Kotlin API reference",
    switchTitle = "Light / dark",
    switchHint = "Lumo's dark mode is not a second stylesheet: once theme=\"dark\" is set on <html>, " +
        "color-scheme.css switches color-scheme and every design token follows.",
    now = "Now: ",
    nowLight = "Light (no theme attribute)",
    nowDark = "Dark (theme=\"dark\")",
    light = "Light",
    dark = "Dark",
    toggle = "Toggle",
    tokensTitle = "Design tokens",
    tokensHint = "The page itself is laid out with these CSS custom properties only, so a theme switch never " +
        "runs into a hard-coded colour. The swatches below follow the active theme.",
    primary = "Primary",
    success = "Success",
    error = "Error",
    divider = "Divider",
    base = "Base",
    entryTitle = "Stylesheet entry points",
    entryHint = "The wrapper exposes the upstream stylesheet paths as constants, so Kotlin code never " +
        "hard-codes npm paths.",
    entryNote = "The demo copies dist/lumo.css from @vaadin/vaadin-lumo-styles into the page directory " +
        "with a Gradle task, then index.html loads it as a plain stylesheet — LumoInjectionMixin reads " +
        "the @media lumo_* rules from document.styleSheets.",
    variantsTitle = "Component theme variants",
    variantsHint = "The theme property takes several space-separated variants, matching server-side Vaadin " +
        "(on the Kotlin side setAttribute is used, so the value is not reflected).",
    smallLabel = "small variant",
    smallPlaceholder = "A more compact field",
    helperText = "The helper text sits above the field",
    cardTitle = "Elevated card",
    cardText = "Theme variants only change the look, never the API.",
    propertiesTitle = "Component style properties",
    propertiesHint = "Every component exposes --vaadin-* style properties; overriding them adjusts the look " +
        "without touching the Shadow DOM, because custom properties inherit into the shadow tree.",
    styledButton = "Styled through properties",
    partsTitle = "Shadow parts",
    partsHint = "Components expose their internals as parts that a host stylesheet selects with ::part(); " +
        "this example recolours the label part with a rule the page declares itself.",
    partButton = "A label styled through ::part",
    utilitiesTitle = "Utility classes",
    utilitiesHint = "Lumo ships utility classes (padding / background / flex / gap / rounded / shadow ...) that " +
        "can be applied to plain elements without writing extra CSS; this page loads utility.css.",
    utilitiesText = "This box is styled with utility classes only.",
    i18nTitle = "Built-in translations",
    i18nHint = "Copy that ships with a component (Upload's \"Drop files here\", the Login field names, " +
        "\"Today\" and so on) comes from each element's i18n property and defaults to English. To localise " +
        "it, replace the whole i18n object (or use Object.assign to keep the other defaults) instead of " +
        "editing one property at a time.",
    i18nText = "The demo keeps each component's default English copy so it matches the screenshots and " +
        "assertions of the upstream documentation, while the page prose follows the selected language.",
    internalsTitle = "Packages without a dedicated page",
    internalsHint = "A few wrapped packages do not produce a visual component of their own; other components " +
        "use them, so they have no page: a11y-base / component-base / field-base / themable-mixin (mixins " +
        "and static helpers), lit-renderer (Lit directives, usable only in Lit templates) and the root " +
        "package's VaadinTagName (tag name constants).",
    internalsIntro = "These packages are covered indirectly by examples on other pages:",
    elementMixin = "Base class of every Element",
    inputControl = "Fields such as TextField / ComboBox / Select",
    tagNames = "Tag name constants used inside createXxx",
)

private val themingChinese: ThemingTexts = ThemingTexts(
    docsLink = "Vaadin 官方文档",
    sourceLink = "查看 Kotlin 源码",
    apiLink = "Kotlin API 文档",
    switchTitle = "亮色 / 暗色",
    switchHint = "Lumo 的暗色不是另一套样式表：在 <html> 上设置 theme=\"dark\" 后，" +
        "color-scheme.css 会切换 color-scheme，所有设计令牌随之改变。",
    now = "当前：",
    nowLight = "亮色（无 theme 属性）",
    nowDark = "暗色（theme=\"dark\"）",
    light = "亮色",
    dark = "暗色",
    toggle = "切换",
    tokensTitle = "设计令牌",
    tokensHint = "页面自身只用这些 CSS 自定义属性排布，因此切换主题时不会出现写死的颜色。" +
        "下面的色块会随主题变化。",
    primary = "主色",
    success = "成功",
    error = "错误",
    divider = "分隔线",
    base = "底色",
    entryTitle = "样式表入口",
    entryHint = "wrapper 把上游的样式表路径暴露成常量，避免在 Kotlin 代码里硬编码 npm 路径。",
    entryNote = "本 Demo 通过 Gradle 任务把 @vaadin/vaadin-lumo-styles 的 dist/lumo.css 复制到页面目录，" +
        "再由 index.html 以普通样式表引入 —— LumoInjectionMixin 需要从 document.styleSheets " +
        "读取 @media lumo_* 规则。",
    variantsTitle = "组件主题变体",
    variantsHint = "组件的 theme 属性接受空格分隔的多个变体，与服务端 Vaadin 的写法一致" +
        "（Kotlin 侧用 setAttribute，属性值不反射）。",
    smallLabel = "small 变体",
    smallPlaceholder = "更紧凑的输入框",
    helperText = "说明文字在字段上方",
    cardTitle = "elevated 卡片",
    cardText = "主题变体只影响外观，不改变 API。",
    propertiesTitle = "样式属性",
    propertiesHint = "每个组件公开一组 --vaadin-* 样式属性，覆盖它们即可调整外观，不必接触 Shadow DOM；" +
        "属性会从宿主元素继承进影子树。",
    styledButton = "通过样式属性着色",
    partsTitle = "阴影部件",
    partsHint = "组件把内部结构声明为 part，宿主样式表用 ::part() 选中它；这里给按钮的 label 部件换颜色，" +
        "规则由本页自己声明（一个 `::part()` 选择器），只作用于本示例。",
    partButton = "用 ::part 着色的标签",
    utilitiesTitle = "工具类",
    utilitiesHint = "Lumo 自带一套工具类（padding / background / flex / gap / rounded / shadow ...），" +
        "给普通元素套用即可，不需要写额外样式表；本页通过 utility.css 加载。",
    utilitiesText = "这个盒子只用了工具类排布。",
    i18nTitle = "组件内文案",
    i18nHint = "组件自带的文案（Upload 的「Drop files here」、Login 表单的字段名、「Today」等）来自各元素的 " +
        "i18n 属性，默认是英文。要本地化时替换整个 i18n 对象（或用 Object.assign 保留其余默认值），" +
        "而不是逐个属性改文本。",
    i18nText = "本 Demo 保持默认的英文文案，以便和上游文档的截图、断言一一对应；" +
        "页面自身的说明文字则跟随当前语言。",
    internalsTitle = "没有独立页面的包",
    internalsHint = "wrapper 里还有几个不产出可视组件的包，它们由其他组件使用，因此不单独建页：" +
        "a11y-base / component-base / field-base / themable-mixin（mixin 与静态工具）、" +
        "lit-renderer（Lit 指令，只在 Lit 模板里可用）、以及根包的 VaadinTagName（标签名常量）。",
    internalsIntro = "这些包的公开 API 通过其他页面的示例间接覆盖：",
    elementMixin = "所有 Element 的基类",
    inputControl = "TextField / ComboBox / Select 等字段",
    tagNames = "createXxx 内部使用的标签名常量",
)

private val themingTexts: Translations<ThemingTexts> = texts(
    Langs.EN to themingEnglish,
    Langs.ZH_HANS to themingChinese,
)

/**
 * The same page on kit blocks.
 *
 * This is the page where a language switch has the most state to preserve: the light / dark choice,
 * the event log and the styled button all stay as they were, because nothing is rebuilt — only the
 * text of the nodes is rewritten.
 */
internal class ThemingPage : DemoPage() {
    private val meta: PageMeta = themingEntry.meta


    private val texts = Binder(themingTexts)

    private val readout = Readout("")

    private val log = EventLog(4)

    private val lightButton = createButton()

    private val darkButton = createButton().addTheme("primary")

    private val toggleButton = createButton().addTheme("tertiary")

    private val compactField = createTextField()

    private val helperField = createTextField()

    private val elevatedCard = createCard()

    private val cardText = paragraph("")

    private val styledButton = createButton()

    private val partButton = createButton().apply { className = THEMING_PART }

    private val utilityBox = HtmlTagName.div { className = UTILITY_BOX }

    private val utilityText = HtmlTagName.p { className = SMALL }

    private val entryNote = HtmlTagName.p { className = MUTED_SMALL }

    private val links = mutableListOf<HTMLAnchorElement>()


    init {
        addSheet(THEMING_SHEET)

        val docs = kitAnchor("", meta.docsUrl)
        val source = kitAnchor("", "$REPO_SOURCE_ROOT/${meta.sourcePath}")
        val api = kitAnchor("", componentApiUrl(themingEntry.id))
        links += docs
        links += source
        links += api
        addLink(texts.bind(docs, { it.docsLink }))
        addLink(texts.bind(source, { it.sourceLink }))
        addLink(texts.bind(api, { it.apiLink }))

        buildSwitch()
        buildTokens()
        buildEntryPoints()
        buildVariants()
        buildProperties()
        buildParts()
        buildUtilities()
        buildBuiltInCopy()
        buildInternals()

        applyTranslation(Langs.EN)
    }

    private fun buildSwitch() {
        val section = sectionBlock()
        texts.bind(section.title, { it.switchTitle })
        texts.bind(section.hint, { it.switchHint })
        texts.bind(lightButton, { it.light })
        texts.bind(darkButton, { it.dark })
        texts.bind(toggleButton, { it.toggle })
        lightButton.clickEvent.addHandler {
            Theme.apply(false)
            sync()
            log.add("Theme.apply(false)")
        }
        darkButton.clickEvent.addHandler {
            Theme.apply(true)
            sync()
            log.add("Theme.apply(true)")
        }
        toggleButton.clickEvent.addHandler {
            Theme.toggle()
            sync()
            log.add("Theme.toggle()")
        }
        section.body.append(
            exampleBox(null, kitCol(kitRow(lightButton, darkButton, toggleButton), readout.root, log.root)),
        )
        body.append(section.root)
    }

    private fun buildTokens() {
        val section = sectionBlock()
        texts.bind(section.title, { it.tokensTitle })
        texts.bind(section.hint, { it.tokensHint })
        val swatches =
            listOf(
                "--lumo-primary-color" to { copy: ThemingTexts -> copy.primary },
                "--lumo-success-color" to { copy: ThemingTexts -> copy.success },
                "--lumo-error-color" to { copy: ThemingTexts -> copy.error },
                "--lumo-contrast-10pct" to { copy: ThemingTexts -> copy.divider },
                "--lumo-base-color" to { copy: ThemingTexts -> copy.base },
            )
        val nodes = swatches.map { (variable, field) ->
            val label = HtmlTagName.p { className = MUTED_SMALL }
            texts.bind(label, field)
            kitCol(newSwatchChip(variable), label, codeText(variable))
        }
        section.body.append(exampleBox(null, kitRow(*nodes.toTypedArray())))
        body.append(section.root)
    }

    private fun buildEntryPoints() {
        val section = sectionBlock()
        texts.bind(section.title, { it.entryTitle })
        texts.bind(section.hint, { it.entryHint })
        texts.bind(entryNote, { it.entryNote })
        section.body.append(
            exampleBox(
                null,
                kitCol(
                    newEntryPoint("LumoThemes.CSS", LumoThemes.CSS),
                    newEntryPoint("LumoThemes.COMPACT_CSS", LumoThemes.COMPACT_CSS),
                    newEntryPoint("LumoThemes.UTILITY_CSS", LumoThemes.UTILITY_CSS),
                    newEntryPoint("Aura.CSS", Aura.CSS),
                    entryNote,
                ),
            ),
        )
        body.append(section.root)
    }

    private fun buildVariants() {
        val section = sectionBlock()
        texts.bind(section.title, { it.variantsTitle })
        texts.bind(section.hint, { it.variantsHint })
        texts.bind(compactField, { it.smallLabel }) { text -> compactField.label = text }
        texts.bind(compactField, { it.smallPlaceholder }) { text -> compactField.placeholder = text }
        compactField.addTheme("small")
        helperField.label = "helper-above-field"
        texts.bind(helperField, { it.helperText }) { text -> helperField.helperText = text }
        helperField.addTheme("helper-above-field")
        texts.bind(elevatedCard, { it.cardTitle }) { text -> elevatedCard.cardTitle = text }
        elevatedCard.addTheme("elevated")
        texts.bind(cardText, { it.cardText })
        elevatedCard.append(cardText)
        section.body.append(
            exampleBox(null, kitRow(compactField, helperField)),
            exampleBox(null, elevatedCard),
        )
        body.append(section.root)
    }

    private fun buildProperties() {
        val section = sectionBlock()
        texts.bind(section.title, { it.propertiesTitle })
        texts.bind(section.hint, { it.propertiesHint })
        texts.bind(styledButton, { it.styledButton })
        styledButton.style.setProperty("--vaadin-button-background", "var(--lumo-primary-color)")
        styledButton.style.setProperty("--vaadin-button-text-color", "var(--lumo-base-color)")
        styledButton.style.setProperty("--vaadin-button-border-radius", "var(--lumo-border-radius-l)")
        section.body.append(
            exampleBox(
                null,
                kitCol(
                    styledButton,
                    codeText("--vaadin-button-background: var(--lumo-primary-color)"),
                    codeText("--vaadin-button-text-color: var(--lumo-base-color)"),
                    codeText("--vaadin-button-border-radius: var(--lumo-border-radius-l)"),
                ),
            ),
        )
        body.append(section.root)
    }

    private fun buildParts() {
        val section = sectionBlock()
        texts.bind(section.title, { it.partsTitle })
        texts.bind(section.hint, { it.partsHint })
        texts.bind(partButton, { it.partButton })
        section.body.append(
            exampleBox(
                null,
                kitCol(partButton, codeText(".theming-part::part(label) { color: var(--lumo-primary-color); }")),
            ),
        )
        body.append(section.root)
    }

    private fun buildUtilities() {
        val section = sectionBlock()
        texts.bind(section.title, { it.utilitiesTitle })
        texts.bind(section.hint, { it.utilitiesHint })
        texts.bind(utilityText, { it.utilitiesText })
        utilityBox.append(createIcon { icon = "lumo:info" }, utilityText)
        section.body.append(
            exampleBox(
                null,
                kitCol(utilityBox, codeText("flex gap-m items-center p-m bg-contrast-5 rounded-m shadow-s")),
            ),
        )
        body.append(section.root)
    }

    private fun buildBuiltInCopy() {
        val section = sectionBlock()
        texts.bind(section.title, { it.i18nTitle })
        texts.bind(section.hint, { it.i18nHint })
        val note = HtmlTagName.p { className = MUTED_SMALL }
        texts.bind(note, { it.i18nText })
        section.body.append(exampleBox(null, note))
        body.append(section.root)
    }

    private fun buildInternals() {
        val section = sectionBlock()
        texts.bind(section.title, { it.internalsTitle })
        texts.bind(section.hint, { it.internalsHint })
        val intro = HtmlTagName.p { className = SMALL }
        texts.bind(intro, { it.internalsIntro })
        section.body.append(
            exampleBox(
                null,
                kitCol(
                    intro,
                    newDescribedEntryPoint("io.github.shijianjs.ktjs.vaadin.componentbase.ElementMixin", texts, { it.elementMixin }),
                    newDescribedEntryPoint("io.github.shijianjs.ktjs.vaadin.fieldbase.InputControlMixin", texts, { it.inputControl }),
                    newEntryPoint("io.github.shijianjs.ktjs.vaadin.themablemixin.CssResult", "registerStyles / LumoStyles"),
                    newDescribedEntryPoint("io.github.shijianjs.ktjs.vaadin.VaadinTagName", texts, { it.tagNames }),
                ),
            ),
        )
        body.append(section.root)
    }

    override fun applyTranslation(lang: Lang) {
        texts.apply(lang)
        val language = if (lang == Langs.EN) Language.EN else Language.ZH
        setTitle(meta.titleFor(language))
        setSummary(meta.summaryFor(language))
        links.first().href = meta.docsUrl
        sync()
    }

    private fun sync() {
        val copy = texts.current
        readout.set(copy.now + if (Theme.isDark) copy.nowDark else copy.nowLight)
    }

    companion object : CustomElementReference<ThemingPage>(ThemingPage::class.js) {
        val NAME: TagName<ThemingPage> = TagName("vaadin-demo-theming-page")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<vaadin-demo-theming-page>`. */
internal fun newThemingPage(): ThemingPage {
    ThemingPage.define()
    return ThemingPage.NAME()
}


