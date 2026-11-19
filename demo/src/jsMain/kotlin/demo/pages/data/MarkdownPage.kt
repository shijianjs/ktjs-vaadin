package demo.pages.data

import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import web.components.CustomElementReference
import web.dom.TagName
import io.github.shijianjs.ktjs.demo.page.anchor as kitAnchor
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.tr
import demo.ui.PageMeta
import demo.ui.example
import demo.ui.row
import demo.Language
import demo.componentApiUrl
import demo.ui.REPO_SOURCE_ROOT
import demo.ui.section
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.page.anchor
import io.github.shijianjs.ktjs.demo.page.col as kitCol
import io.github.shijianjs.ktjs.demo.page.exampleBox
import io.github.shijianjs.ktjs.demo.page.paragraph
import io.github.shijianjs.ktjs.demo.page.row as kitRow
import io.github.shijianjs.ktjs.demo.page.sectionBlock
import io.github.shijianjs.ktjs.vaadin.markdown.MarkdownElement
import io.github.shijianjs.ktjs.vaadin.markdown.createMarkdown
import web.html.HTMLAnchorElement

val markdownEntry: CatalogEntry =
    CatalogEntry(
        id = "markdown",
        group = Groups.DATA,
        meta =
            PageMeta(
                title = bi("Markdown", "Markdown"),
                summary =
                    bi(
                        "把 Markdown 字符串渲染成富文本，可选择是否保留换行。",
                        "Renders a Markdown string as rich text, optionally keeping single line breaks.",
                    ),
                docsUrl = "$DOCS_BASE/markdown",
                sourcePath = "pages/data/MarkdownPage.kt",
            ),
        kitPage = { newMarkdownPage() },
    )

// ---------------------------------------------------------------------------------------------
// Markdown
//
// docs: markdown/markdown-basic.ts and markdown/markdown-line-breaks.ts.
// ---------------------------------------------------------------------------------------------

// ---------------------------------------------------------------------------------------------
// The same page on kit blocks
// ---------------------------------------------------------------------------------------------

/** Every string this page shows, in one language. */
private class MarkdownTexts(
    val docsLink: String,
    val sourceLink: String,
    val apiLink: String,
    val basicTitle: String,
    val basicHint: String,
    val richContent: String,
    val lineTitle: String,
    val lineHint: String,
    val letterContent: String,
    val lineCaption: String,
)

private val markdownEnglish: MarkdownTexts = MarkdownTexts(
    docsLink = "Vaadin documentation",
    sourceLink = "Kotlin source",
    apiLink = "Kotlin API reference",
    basicTitle = "Basic usage",
    basicHint = "content holds Markdown source, which the component renders as sanitized HTML: headings, " +
        "bold, italic, inline code, strikethrough, ordered and unordered lists, quotes and links all work " +
        "out of the box.",
    richContent = """
        ## Rich Text Formatting

        You can create **bold text**, *italicized text*, and `inline code` with simple Markdown syntax.
        You can also ~~strike through~~ text when needed.

        ## Lists

        ### Ordered List:
        1. First item
        2. Second item
        3. Third item with **bold text**

        ### Unordered List:
        - Fruits
            - Apples 🍎
            - Bananas 🍌
            - Oranges 🍊
        - Vegetables
            - Carrots
            - Broccoli

        ## Links & Quotes

        > Markdown is a lightweight markup language that you can use to add formatting elements to plaintext documents.

        [Visit Vaadin website](https://vaadin.com) | [Learn more about Markdown](https://www.markdownguide.org/)
    """.trimIndent(),
    lineTitle = "Line breaks",
    lineHint = "By default the original Markdown spec applies: a single line break inside a paragraph " +
        "collapses into a space and a blank line starts a new paragraph. With lineBreaks on, single (soft) " +
        "breaks render as real line breaks like chat messages, while blank lines still start a new paragraph.",
    letterContent = """
        Hi Maria,
        Your order shipped this morning.
        It should arrive by Thursday.

        Best regards,
        Tom
    """.trimIndent(),
    lineCaption = "Left: lineBreaks on. Right: default.",
)

private val markdownChinese: MarkdownTexts = MarkdownTexts(
    docsLink = "Vaadin 官方文档",
    sourceLink = "查看 Kotlin 源码",
    apiLink = "Kotlin API 文档",
    basicTitle = "基本用法",
    basicHint = "content 是 Markdown 源码，组件把它渲染成 HTML 并做净化：标题、粗体、斜体、行内代码、" +
        "删除线、有序 / 无序列表、引用与链接都能直接用。",
    richContent = """
        ## 富文本格式

        用简单的 Markdown 语法就能写出 **粗体**、*斜体* 和 ` + "`行内代码`" + `。
        需要时还可以 ~~删除线~~。

        ## 列表

        ### 有序列表：
        1. 第一项
        2. 第二项
        3. 带 **粗体** 的第三项

        ### 无序列表：
        - 水果
            - 苹果 🍎
            - 香蕉 🍌
            - 橙子 🍊
        - 蔬菜
            - 胡萝卜
            - 西兰花

        ## 链接与引用

        > Markdown 是一种轻量标记语言，可以为纯文本添加格式。

        [访问 Vaadin 官网](https://vaadin.com) | [了解 Markdown](https://www.markdownguide.org/)
    """.trimIndent(),
    lineTitle = "换行",
    lineHint = "默认遵循原始 Markdown 规范：段落里的单个换行会合并成空格，换段需要空行。" +
        "打开 lineBreaks 后单个换行（软换行）也会渲染成真正的换行，像聊天消息那样；空行仍然换段。",
    letterContent = """
        你好，Maria：
        订单今早已发出。
        预计周四送达。

        此致
        Tom
    """.trimIndent(),
    lineCaption = "左：lineBreaks 打开　右：默认",
)

private val markdownTexts: Translations<MarkdownTexts> = texts(
    Langs.EN to markdownEnglish,
    Langs.ZH_HANS to markdownChinese,
)

/**
 * The page on kit blocks.
 *
 * Both examples render Markdown from the text class, so a language switch swaps the source string
 * and lets the component re-render it — no page rebuild, and the two line-break examples stay next
 * to each other.
 */
internal class MarkdownPage : DemoPage() {
    private val meta: PageMeta = markdownEntry.meta


    private val texts = Binder(markdownTexts)

    private var language: Lang = Langs.EN

    private val copy: MarkdownTexts get() = markdownTexts[language]

    private val basic = sectionBlock()

    private val lineBreaks = sectionBlock()

    private val rich: MarkdownElement = createMarkdown().apply { dataset["test"] = "markdown" }

    private val withBreaks: MarkdownElement = createMarkdown { lineBreaks = true }

    private val withoutBreaks: MarkdownElement = createMarkdown()

    private val caption = paragraph("")

    private val links = mutableListOf<HTMLAnchorElement>()


    init {
        val docs = kitAnchor("", "$DOCS_BASE/markdown")
        val source = kitAnchor("", "$REPO_SOURCE_ROOT/${meta.sourcePath}")
        val api = kitAnchor("", componentApiUrl(markdownEntry.id))
        links += docs
        links += source
        links += api
        addLink(texts.bind(docs, { it.docsLink }))
        addLink(texts.bind(source, { it.sourceLink }))
        addLink(texts.bind(api, { it.apiLink }))

        basic.body.append(exampleBox(null, rich))
        body.append(basic.root)

        lineBreaks.body.append(exampleBox(null, kitCol(caption, kitRow(withBreaks, withoutBreaks))))
        body.append(lineBreaks.root)

        applyTranslation(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        language = lang
        texts.apply(lang)
        basic.setText(copy.basicTitle, copy.basicHint)
        lineBreaks.setText(copy.lineTitle, copy.lineHint)
        rich.content = copy.richContent
        withBreaks.content = copy.letterContent
        withoutBreaks.content = copy.letterContent
        caption.textContent = copy.lineCaption
        val pageLanguage = if (lang == Langs.EN) Language.EN else Language.ZH
        setTitle(meta.titleFor(pageLanguage))
        setSummary(meta.summaryFor(pageLanguage))
        links.first().href = meta.docsUrl
    }

    companion object : CustomElementReference<MarkdownPage>(MarkdownPage::class.js) {
        val NAME: TagName<MarkdownPage> = TagName("vaadin-demo-markdown-page")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<vaadin-demo-markdown-page>`. */
internal fun newMarkdownPage(): MarkdownPage {
    MarkdownPage.define()
    return MarkdownPage.NAME()
}


