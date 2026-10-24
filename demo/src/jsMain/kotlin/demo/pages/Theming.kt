package demo.pages

import demo.CatalogEntry
import demo.Groups
import demo.STYLING_BASE
import demo.Theme
import demo.ui.EventLog
import demo.ui.PageMeta
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
import vaadin.lumostyles.LumoThemes
import vaadin.textfield.createTextField
import web.dom.clickEvent
import web.events.addHandler
import web.html.HTMLElement

val themingEntries: List<CatalogEntry> =
    listOf(
        CatalogEntry(
            id = "theming",
            group = Groups.THEMING,
            meta =
                PageMeta(
                    title = "主题与样式",
                    summary = "Demo 使用 Lumo 主题：亮色 / 暗色切换、设计令牌与组件主题变体。",
                    docsUrl = "$STYLING_BASE/themes/lumo",
                    sourcePath = "pages/Theming.kt",
                ),
            builder = ::buildThemingPage,
        ),
    )

private fun buildThemingPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    // --- 亮色 / 暗色 -----------------------------------------------------------------------------
    val switching =
        section(
            "亮色 / 暗色",
            "Lumo 的暗色不是另一套样式表：在 <html> 上设置 theme=\"dark\" 后，" +
                "color-scheme.css 会切换 color-scheme，所有设计令牌随之改变。",
        )
    val readout = Readout("当前：亮色")
    val log = EventLog(4)

    fun sync() {
        readout.set("当前：", if (Theme.isDark) "暗色（theme=\"dark\"）" else "亮色（无 theme 属性）")
    }
    sync()

    val light = createButton { textContent = "亮色" }
    light.clickEvent.addHandler {
        Theme.apply(false)
        sync()
        log.add("Theme.apply(false)")
    }
    val dark = createButton { textContent = "暗色" }.withTheme("primary")
    dark.clickEvent.addHandler {
        Theme.apply(true)
        sync()
        log.add("Theme.apply(true)")
    }
    val toggle = createButton { textContent = "切换" }.withTheme("tertiary")
    toggle.clickEvent.addHandler {
        Theme.toggle()
        sync()
        log.add("Theme.toggle()")
    }

    switching.body.append(example(null, column(row(light, dark, toggle), readout.root, log.root)))
    view.body.append(switching.root)

    // --- 设计令牌 -------------------------------------------------------------------------------
    val tokens =
        section(
            "设计令牌",
            "页面自身只用这些 CSS 自定义属性排布，因此切换主题时不会出现写死的颜色。" +
                "下面的色块会随主题变化。",
        )
    tokens.body.append(
        example(
            null,
            row(
                swatch("--lumo-primary-color", "主色"),
                swatch("--lumo-success-color", "成功"),
                swatch("--lumo-error-color", "错误"),
                swatch("--lumo-contrast-10pct", "分隔线"),
                swatch("--lumo-base-color", "底色"),
            ),
        ),
    )
    view.body.append(tokens.root)

    // --- 样式表入口 -----------------------------------------------------------------------------
    val entryPoints =
        section("样式表入口", "wrapper 把上游的样式表路径暴露成常量，避免在 Kotlin 代码里硬编码 npm 路径。")
    entryPoints.body.append(
        example(
            null,
            column(
                entryPoint("LumoThemes.CSS", LumoThemes.CSS),
                entryPoint("LumoThemes.COMPACT_CSS", LumoThemes.COMPACT_CSS),
                entryPoint("LumoThemes.UTILITY_CSS", LumoThemes.UTILITY_CSS),
                entryPoint("Aura.CSS", Aura.CSS),
                paragraph(
                    "本 Demo 通过 Gradle 任务把 @vaadin/vaadin-lumo-styles 的 dist/lumo.css 复制到页面目录，" +
                        "再由 index.html 以普通样式表引入 —— LumoInjectionMixin 需要从 document.styleSheets 读取 @media lumo_* 规则。",
                    "small muted",
                ),
            ),
        ),
    )
    view.body.append(entryPoints.root)

    // --- 组件主题变体 ---------------------------------------------------------------------------
    val variants =
        section(
            "组件主题变体",
            "组件的 theme 属性接受空格分隔的多个变体，与服务端 Vaadin 的写法一致（Kotlin 侧用 setAttribute，属性值不反射）。",
        )
    val compactField =
        createTextField {
            label = "small 变体"
            placeholder = "更紧凑的输入框"
        }
    compactField.setAttribute("theme", "small")
    val helperAbove =
        createTextField {
            label = "helper-above-field"
            helperText = "说明文字在字段上方"
        }
    helperAbove.setAttribute("theme", "helper-above-field")
    val card = createCard { cardTitle = "elevated 卡片" }
    card.setAttribute("theme", "elevated")
    card.append(paragraph("主题变体只影响外观，不改变 API。"))

    variants.body.append(
        example(
            null,
            row(compactField, helperAbove),
        ),
        example(null, card),
    )
    view.body.append(variants.root)

    // --- 组件内文案 -----------------------------------------------------------------------------
    val i18n =
        section(
            "组件内文案",
            "组件自带的文案（Upload 的「Drop files here」、Login 表单的字段名、「Today」等）来自各元素的 i18n 属性，" +
                "默认是英文。要本地化时替换整个 i18n 对象（或用 Object.assign 保留其余默认值），而不是逐个属性改文本。",
        )
    i18n.body.append(
        example(
            null,
            paragraph(
                "本 Demo 保持默认的英文文案，以便和上游文档的截图、断言一一对应；页面自身的说明文字是中文。",
                "small muted",
            ),
        ),
    )
    view.body.append(i18n.root)

    // --- 没有独立页面的包 -----------------------------------------------------------------------
    val internals =
        section(
            "没有独立页面的包",
            "wrapper 里还有几个不产出可视组件的包，它们由其他组件使用，因此不单独建页：" +
                "a11y-base / component-base / field-base / themable-mixin（mixin 与静态工具）、" +
                "lit-renderer（Lit 指令，只在 Lit 模板里可用）、以及根包的 VaadinTagName（标签名常量）。",
        )
    internals.body.append(
        example(
            null,
            column(
                paragraph("这些包的公开 API 通过其他页面的示例间接覆盖：", "small"),
                entryPoint("vaadin.componentbase.ElementMixin", "所有 Element 的基类"),
                entryPoint("vaadin.fieldbase.InputControlMixin", "TextField / ComboBox / Select 等字段"),
                entryPoint("vaadin.themablemixin.CssResult", "registerStyles / LumoStyles"),
                entryPoint("vaadin.VaadinTagName", "createXxx 内部使用的标签名常量"),
            ),
        ),
    )
    view.body.append(internals.root)

    return view.root
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
