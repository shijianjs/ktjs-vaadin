package demo.pages.theming

import demo.CatalogEntry
import demo.Groups
import demo.STYLING_BASE
import demo.Theme
import demo.bi
import demo.tr
import demo.ui.EventLog
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.Readout
import demo.ui.code
import demo.ui.column
import demo.ui.div
import demo.ui.example
import demo.ui.page
import demo.ui.paragraph
import demo.ui.row
import demo.ui.section
import demo.ui.withTheme
import vaadin.aura.Aura
import vaadin.button.createButton
import vaadin.card.createCard
import vaadin.icon.createIcon
import vaadin.lumostyles.LumoThemes
import vaadin.textfield.createTextField
import web.cssom.ClassName
import web.dom.clickEvent
import web.events.addHandler
import web.html.HTMLElement

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
        builder = ::buildThemingPage,
    )

private fun buildThemingPage(meta: PageMeta): PageView {
    val view = page(meta)

    // --- Light / dark ---------------------------------------------------------------------------
    val switching =
        section(
            tr("亮色 / 暗色", "Light / dark"),
            tr(
                "Lumo 的暗色不是另一套样式表：在 <html> 上设置 theme=\"dark\" 后，" +
                    "color-scheme.css 会切换 color-scheme，所有设计令牌随之改变。",
                "Lumo's dark mode is not a second stylesheet: once theme=\"dark\" is set on <html>, " +
                    "color-scheme.css switches color-scheme and every design token follows.",
            ),
        )
    val readout = Readout(tr("当前：亮色", "Now: light"))
    val log = EventLog(4)

    fun sync() {
        readout.set(
            tr("当前：", "Now:"),
            if (Theme.isDark) {
                tr("暗色（theme=\"dark\"）", "Dark (theme=\"dark\")")
            } else {
                tr("亮色（无 theme 属性）", "Light (no theme attribute)")
            },
        )
    }
    sync()

    val light = createButton { textContent = tr("亮色", "Light") }
    light.clickEvent.addHandler {
        Theme.apply(false)
        sync()
        log.add("Theme.apply(false)")
    }
    val dark = createButton { textContent = tr("暗色", "Dark") }.withTheme("primary")
    dark.clickEvent.addHandler {
        Theme.apply(true)
        sync()
        log.add("Theme.apply(true)")
    }
    val toggle = createButton { textContent = tr("切换", "Toggle") }.withTheme("tertiary")
    toggle.clickEvent.addHandler {
        Theme.toggle()
        sync()
        log.add("Theme.toggle()")
    }

    switching.body.append(example(null, column(row(light, dark, toggle), readout.root, log.root)))
    view.body.append(switching.root)

    // --- Design tokens --------------------------------------------------------------------------
    val tokens =
        section(
            tr("设计令牌", "Design tokens"),
            tr(
                "页面自身只用这些 CSS 自定义属性排布，因此切换主题时不会出现写死的颜色。" +
                    "下面的色块会随主题变化。",
                "The page itself is laid out with these CSS custom properties only, so a theme switch never " +
                    "runs into a hard-coded colour. The swatches below follow the active theme.",
            ),
        )
    tokens.body.append(
        example(
            null,
            row(
                swatch("--lumo-primary-color", tr("主色", "Primary")),
                swatch("--lumo-success-color", tr("成功", "Success")),
                swatch("--lumo-error-color", tr("错误", "Error")),
                swatch("--lumo-contrast-10pct", tr("分隔线", "Divider")),
                swatch("--lumo-base-color", tr("底色", "Base")),
            ),
        ),
    )
    view.body.append(tokens.root)

    // --- Stylesheet entry points ----------------------------------------------------------------
    val entryPoints =
        section(
            tr("样式表入口", "Stylesheet entry points"),
            tr(
                "wrapper 把上游的样式表路径暴露成常量，避免在 Kotlin 代码里硬编码 npm 路径。",
                "The wrapper exposes the upstream stylesheet paths as constants, so Kotlin code never " +
                    "hard-codes npm paths.",
            ),
        )
    entryPoints.body.append(
        example(
            null,
            column(
                entryPoint("LumoThemes.CSS", LumoThemes.CSS),
                entryPoint("LumoThemes.COMPACT_CSS", LumoThemes.COMPACT_CSS),
                entryPoint("LumoThemes.UTILITY_CSS", LumoThemes.UTILITY_CSS),
                entryPoint("Aura.CSS", Aura.CSS),
                paragraph(
                    tr(
                        "本 Demo 通过 Gradle 任务把 @vaadin/vaadin-lumo-styles 的 dist/lumo.css 复制到页面目录，" +
                            "再由 index.html 以普通样式表引入 —— LumoInjectionMixin 需要从 document.styleSheets " +
                            "读取 @media lumo_* 规则。",
                        "The demo copies dist/lumo.css from @vaadin/vaadin-lumo-styles into the page directory " +
                            "with a Gradle task, then index.html loads it as a plain stylesheet — " +
                            "LumoInjectionMixin reads the @media lumo_* rules from document.styleSheets.",
                    ),
                    "small muted",
                ),
            ),
        ),
    )
    view.body.append(entryPoints.root)

    // --- Component theme variants ---------------------------------------------------------------
    val variants =
        section(
            tr("组件主题变体", "Component theme variants"),
            tr(
                "组件的 theme 属性接受空格分隔的多个变体，与服务端 Vaadin 的写法一致" +
                    "（Kotlin 侧用 setAttribute，属性值不反射）。",
                "The theme property takes several space-separated variants, matching server-side Vaadin " +
                    "(on the Kotlin side setAttribute is used, so the value is not reflected).",
            ),
        )
    val compactField =
        createTextField {
            label = tr("small 变体", "small variant")
            placeholder = tr("更紧凑的输入框", "A more compact field")
        }
    compactField.withTheme("small")
    val helperAbove =
        createTextField {
            label = "helper-above-field"
            helperText = tr("说明文字在字段上方", "The helper text sits above the field")
        }
    helperAbove.withTheme("helper-above-field")
    val card = createCard { cardTitle = tr("elevated 卡片", "Elevated card") }
    card.withTheme("elevated")
    card.append(
        paragraph(tr("主题变体只影响外观，不改变 API。", "Theme variants only change the look, never the API.")),
    )

    variants.body.append(
        example(
            null,
            row(compactField, helperAbove),
        ),
        example(null, card),
    )
    view.body.append(variants.root)

    // --- Component style properties -------------------------------------------------------------
    val properties =
        section(
            tr("样式属性", "Component style properties"),
            tr(
                "每个组件公开一组 --vaadin-* 样式属性，覆盖它们即可调整外观，不必接触 Shadow DOM；" +
                    "属性会从宿主元素继承进影子树。",
                "Every component exposes --vaadin-* style properties; overriding them adjusts the look " +
                    "without touching the Shadow DOM, because custom properties inherit into the shadow tree.",
            ),
        )
    val styled = createButton { textContent = tr("通过样式属性着色", "Styled through properties") }
    styled.style.setProperty("--vaadin-button-background", "var(--lumo-primary-color)")
    styled.style.setProperty("--vaadin-button-text-color", "var(--lumo-base-color)")
    styled.style.setProperty("--vaadin-button-border-radius", "var(--lumo-border-radius-l)")
    properties.body.append(
        example(
            null,
            column(
                styled,
                code("--vaadin-button-background: var(--lumo-primary-color)"),
                code("--vaadin-button-text-color: var(--lumo-base-color)"),
                code("--vaadin-button-border-radius: var(--lumo-border-radius-l)"),
            ),
        ),
    )
    view.body.append(properties.root)

    // --- Shadow parts ---------------------------------------------------------------------------
    val parts =
        section(
            tr("阴影部件", "Shadow parts"),
            tr(
                "组件把内部结构声明为 part，宿主样式表用 ::part() 选中它；这里给按钮的 label 部件换颜色，" +
                    "规则写在 demo.css 里并只作用于本示例。",
                "Components expose their internals as parts that a host stylesheet selects with ::part(); " +
                    "this example recolours the label part, scoped to the demo class in demo.css.",
            ),
        )
    val partButton = createButton { textContent = tr("用 ::part 着色的标签", "A label styled through ::part") }
    partButton.classList.add(ClassName("theming-part"))
    parts.body.append(
        example(
            null,
            column(partButton, code(".theming-part::part(label) { color: var(--lumo-primary-color); }")),
        ),
    )
    view.body.append(parts.root)

    // --- Utility classes ------------------------------------------------------------------------
    val utilities =
        section(
            tr("工具类", "Utility classes"),
            tr(
                "Lumo 自带一套工具类（padding / background / flex / gap / rounded / shadow ...），" +
                    "给普通元素套用即可，不需要写额外样式表；本页通过 utility.css 加载。",
                "Lumo ships utility classes (padding / background / flex / gap / rounded / shadow ...) that " +
                    "can be applied to plain elements without writing extra CSS; this page loads utility.css.",
            ),
        )
    val utilityBox = div("flex gap-m items-center p-m bg-contrast-5 rounded-m shadow-s")
    utilityBox.append(
        createIcon { icon = "lumo:info" },
        paragraph(
            tr("这个盒子只用了工具类排布。", "This box is styled with utility classes only."),
            "small",
        ),
    )
    utilities.body.append(
        example(
            null,
            column(
                utilityBox,
                code("flex gap-m items-center p-m bg-contrast-5 rounded-m shadow-s"),
            ),
        ),
    )
    view.body.append(utilities.root)

    // --- Built-in translations ------------------------------------------------------------------
    val i18n =
        section(
            tr("组件内文案", "Built-in translations"),
            tr(
                "组件自带的文案（Upload 的「Drop files here」、Login 表单的字段名、「Today」等）来自各元素的 " +
                    "i18n 属性，默认是英文。要本地化时替换整个 i18n 对象（或用 Object.assign 保留其余默认值），" +
                    "而不是逐个属性改文本。",
                "Copy that ships with a component (Upload's \"Drop files here\", the Login field names, " +
                    "\"Today\" and so on) comes from each element's i18n property and defaults to English. " +
                    "To localise it, replace the whole i18n object (or use Object.assign to keep the other " +
                    "defaults) instead of editing one property at a time.",
            ),
        )
    i18n.body.append(
        example(
            null,
            paragraph(
                tr(
                    "本 Demo 保持默认的英文文案，以便和上游文档的截图、断言一一对应；" +
                        "页面自身的说明文字则跟随当前语言。",
                    "The demo keeps each component's default English copy so it matches the screenshots and " +
                        "assertions of the upstream documentation, while the page prose follows the " +
                        "selected language.",
                ),
                "small muted",
            ),
        ),
    )
    view.body.append(i18n.root)

    // --- Packages without a dedicated page ------------------------------------------------------
    val internals =
        section(
            tr("没有独立页面的包", "Packages without a dedicated page"),
            tr(
                "wrapper 里还有几个不产出可视组件的包，它们由其他组件使用，因此不单独建页：" +
                    "a11y-base / component-base / field-base / themable-mixin（mixin 与静态工具）、" +
                    "lit-renderer（Lit 指令，只在 Lit 模板里可用）、以及根包的 VaadinTagName（标签名常量）。",
                "A few wrapped packages do not produce a visual component of their own; other components " +
                    "use them, so they have no page: a11y-base / component-base / field-base / " +
                    "themable-mixin (mixins and static helpers), lit-renderer (Lit directives, usable only in " +
                    "Lit templates) and the root package's VaadinTagName (tag name constants).",
            ),
        )
    internals.body.append(
        example(
            null,
            column(
                paragraph(
                    tr(
                        "这些包的公开 API 通过其他页面的示例间接覆盖：",
                        "These packages are covered indirectly by examples on other pages:",
                    ),
                    "small",
                ),
                entryPoint(
                    "vaadin.componentbase.ElementMixin",
                    tr("所有 Element 的基类", "Base class of every Element"),
                ),
                entryPoint(
                    "vaadin.fieldbase.InputControlMixin",
                    tr("TextField / ComboBox / Select 等字段", "Fields such as TextField / ComboBox / Select"),
                ),
                entryPoint("vaadin.themablemixin.CssResult", "registerStyles / LumoStyles"),
                entryPoint(
                    "vaadin.VaadinTagName",
                    tr("createXxx 内部使用的标签名常量", "Tag name constants used inside createXxx"),
                ),
            ),
        ),
    )
    view.body.append(internals.root)

    return view
}

/** A colour chip bound to one Lumo custom property. */
private fun swatch(cssVariable: String, label: String): HTMLElement {
    val chip = div().apply {
        style.setProperty("width", "3rem")
        style.setProperty("height", "3rem")
        style.setProperty("border-radius", "var(--lumo-border-radius-m)")
        style.setProperty("border", "1px solid var(--lumo-contrast-20pct)")
        style.setProperty("background", "var($cssVariable)")
    }
    return column(chip, paragraph(label, "small muted"), code(cssVariable))
}

/** Shows a stylesheet entry-point constant and its value. */
private fun entryPoint(name: String, value: String): HTMLElement =
    row(paragraph(name, "small"), code(value))
