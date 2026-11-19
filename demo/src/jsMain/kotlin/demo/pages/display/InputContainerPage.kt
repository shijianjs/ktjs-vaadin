package demo.pages.display

import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import web.components.CustomElementReference
import web.dom.TagName
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.demo.page.anchor as kitAnchor
import io.github.shijianjs.ktjs.demo.page.paragraph
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.Language
import demo.bi
import demo.componentApiUrl
import demo.pages.overlays.ariaLabel
import demo.tr
import demo.ui.MUTED_SMALL
import demo.ui.PageMeta
import demo.ui.REPO_SOURCE_ROOT
import demo.ui.example
import demo.ui.row
import demo.ui.section
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.page.anchor
import io.github.shijianjs.ktjs.demo.page.exampleBox
import io.github.shijianjs.ktjs.demo.page.row as kitRow
import io.github.shijianjs.ktjs.demo.page.sectionBlock
import io.github.shijianjs.ktjs.dom.element.inlineStyle
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.vaadin.inputcontainer.createInputContainer
import web.cssom.Border
import web.cssom.Color
import web.cssom.LineStyle
import web.cssom.Outline
import web.cssom.pct
import web.cssom.px
import web.cssom.rem
import web.html.HTMLAnchorElement
import web.html.HTMLElement
import web.html.HtmlTagName

val inputContainerEntry: CatalogEntry =
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
                sourcePath = "pages/display/InputContainerPage.kt",
            ),
        kitPage = { newInputContainerPage() },
    )

// ---------------------------------------------------------------------------------------------
// Input container (internal element, documented under Text Field)
// ---------------------------------------------------------------------------------------------

/** Builds an input container with a plain text input inside, for the state examples. */
private fun plainContainer(label: String, readonly: Boolean = false, invalid: Boolean = false, disabled: Boolean = false): HTMLElement {
    val container = createInputContainer()
    container.readonly = readonly
    container.invalid = invalid
    container.disabled = disabled
    val input = containerInput(label)
    container.append(input)
    return HtmlTagName.div().apply {
        style.setProperty("width", "12rem")
        append(container, HtmlTagName.p {
                              className = MUTED_SMALL
                              textContent = label
                          })
    }
}

/**
 * The input that sits inside a container: no frame of its own, so the container's border and its
 * state are the only chrome a reader sees.
 *
 * `font: inherit` cannot be written through `web.cssom` — the border/background/size longhands each
 * have a typed value, but `font` is a shorthand of seven properties and the generated factories all
 * take concrete sizes rather than the `inherit` keyword, so this one declaration stays a style
 * property. Everything else on this element is a typed value.
 */
private fun containerInput(value: String = "") =
    HtmlTagName.input().apply {
        this.value = value
        inlineStyle.apply {
            border = Border(0.px, LineStyle.solid)
            outline = Outline(0.px, LineStyle.solid)
            backgroundColor = Color("transparent")
            color = Color("inherit")
            width = 100.pct
        }
        style.setProperty("font", "inherit")
    }


// ---------------------------------------------------------------------------------------------
// The same page on kit blocks
// ---------------------------------------------------------------------------------------------

/** Every string this page shows, in one language. */
private class InputContainerTexts(
    val docsLink: String,
    val sourceLink: String,
    val apiLink: String,
    val basicTitle: String,
    val basicHint: String,
    val statesTitle: String,
    val statesHint: String,
    val readOnly: String,
    val invalid: String,
    val disabled: String,
)

private val inputContainerEnglish: InputContainerTexts = InputContainerTexts(
    docsLink = "Vaadin documentation",
    sourceLink = "Kotlin source",
    apiLink = "Kotlin API reference",
    basicTitle = "Basic usage",
    basicHint = "The container only handles layout and state; the input element is supplied by the caller. " +
        "The prefix/suffix slots hold icons or buttons.",
    statesTitle = "States",
    statesHint = "The readonly / invalid / disabled properties map directly to visual states.",
    readOnly = "Read-only",
    invalid = "Invalid",
    disabled = "Disabled",
)

private val inputContainerChinese: InputContainerTexts = InputContainerTexts(
    docsLink = "Vaadin 官方文档",
    sourceLink = "查看 Kotlin 源码",
    apiLink = "Kotlin API 文档",
    basicTitle = "基本用法",
    basicHint = "容器只负责布局与状态，输入元素由使用方提供；prefix / suffix 插槽放图标或按钮。",
    statesTitle = "状态",
    statesHint = "readonly / invalid / disabled 三个属性直接对应视觉状态。",
    readOnly = "只读",
    invalid = "无效",
    disabled = "禁用",
)

private val inputContainerTexts: Translations<InputContainerTexts> = texts(
    Langs.EN to inputContainerEnglish,
    Langs.ZH_HANS to inputContainerChinese,
)

/** One state example: the container, the input inside it, and the caption under it. */
private class StateExample(readOnly: Boolean = false, invalid: Boolean = false, disabled: Boolean = false) {
    val input = containerInput()

    val container: HTMLElement = createInputContainer().apply {
        this.readonly = readOnly
        this.invalid = invalid
        this.disabled = disabled
        append(input)
    }

    val caption: HTMLElement = paragraph("")

    val root: HTMLElement = HtmlTagName.div().apply {
        inlineStyle.apply { width = 12.rem }
        append(container, caption)
    }
}

/**
 * The page on kit blocks: nodes are built once and a language switch only rewrites their text.
 *
 * The three state containers carry no text of their own except what a reader typed; their labels are
 * the captions below them, and the input's value repeats the caption so the state is legible.
 */
internal class InputContainerPage : DemoPage() {
    private val meta: PageMeta = inputContainerEntry.meta


    private val texts = Binder(inputContainerTexts)

    private val basicContainer: HTMLElement = createInputContainer().apply {
        dataset["test"] = "input-container"
        append(createIcon { icon = "lumo:search" }.apply { slot = "prefix" })
        append(containerInput())
        append(createIcon { icon = "lumo:checkmark" }.apply { slot = "suffix" })
    }

    private val readOnly = StateExample(readOnly = true)

    private val invalid = StateExample(invalid = true)

    private val disabled = StateExample(disabled = true)

    private val links = mutableListOf<HTMLAnchorElement>()


    init {
        val docs = kitAnchor("", meta.docsUrl)
        val source = kitAnchor("", "$REPO_SOURCE_ROOT/${meta.sourcePath}")
        val api = kitAnchor("", componentApiUrl(inputContainerEntry.id))
        links += docs
        links += source
        links += api
        addLink(texts.bind(docs, { it.docsLink }))
        addLink(texts.bind(source, { it.sourceLink }))
        addLink(texts.bind(api, { it.apiLink }))

        val basic = sectionBlock()
        texts.bind(basic.title, { it.basicTitle })
        texts.bind(basic.hint, { it.basicHint })
        basic.body.append(exampleBox(null, basicContainer))
        body.append(basic.root)

        val states = sectionBlock()
        texts.bind(states.title, { it.statesTitle })
        texts.bind(states.hint, { it.statesHint })
        texts.bind(readOnly.caption, { it.readOnly })
        texts.bind(invalid.caption, { it.invalid })
        texts.bind(disabled.caption, { it.disabled })
        states.body.append(exampleBox(null, kitRow(readOnly.root, invalid.root, disabled.root)))
        body.append(states.root)

        applyTranslation(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        texts.apply(lang)
        val copy = inputContainerTexts[lang]
        readOnly.input.value = copy.readOnly
        invalid.input.value = copy.invalid
        disabled.input.value = copy.disabled
        val language = if (lang == Langs.EN) Language.EN else Language.ZH
        setTitle(meta.titleFor(language))
        setSummary(meta.summaryFor(language))
        links.first().href = meta.docsUrl
    }

    companion object : CustomElementReference<InputContainerPage>(InputContainerPage::class.js) {
        val NAME: TagName<InputContainerPage> = TagName("vaadin-demo-input-container-page")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<vaadin-demo-input-container-page>`. */
internal fun newInputContainerPage(): InputContainerPage {
    InputContainerPage.define()
    return InputContainerPage.NAME()
}


