package demo.pages.data

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.tr
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.example
import demo.ui.page
import demo.ui.row
import demo.ui.section
import vaadin.markdown.createMarkdown

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
        builder = ::buildMarkdownPage,
    )

// ---------------------------------------------------------------------------------------------
// Markdown
//
// docs: markdown/markdown-basic.ts and markdown/markdown-line-breaks.ts.
// ---------------------------------------------------------------------------------------------

private fun buildMarkdownPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "content 是 Markdown 源码，组件把它渲染成 HTML 并做净化：标题、粗体、斜体、行内代码、删除线、" +
                    "有序 / 无序列表、引用与链接都能直接用。",
                "content holds Markdown source, which the component renders as sanitized HTML: headings, " +
                    "bold, italic, inline code, strikethrough, ordered and unordered lists, quotes and links " +
                    "all work out of the box.",
            ),
        )
    val markdown = createMarkdown()
    markdown.dataset["test"] = "markdown"
    markdown.content =
        tr(
            """
            ## 富文本格式

            用简单的 Markdown 语法就能写出 **粗体**、*斜体* 和 `行内代码`。
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
            """
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
        )
    basic.body.append(example(null, markdown))
    view.body.append(basic.root)

    val lineBreakSection =
        section(
            tr("换行", "Line breaks"),
            tr(
                "默认遵循原始 Markdown 规范：段落里的单个换行会合并成空格，换段需要空行。" +
                    "打开 lineBreaks 后单个换行（软换行）也会渲染成真正的换行，像聊天消息那样；空行仍然换段。",
                "By default the original Markdown spec applies: a single line break inside a paragraph " +
                    "collapses into a space and a blank line starts a new paragraph. With lineBreaks on, " +
                    "single (soft) breaks render as real line breaks like chat messages, while blank lines " +
                    "still start a new paragraph.",
            ),
        )
    val letter =
        tr(
            """
            你好，Maria：
            订单今早已发出。
            预计周四送达。

            此致
            Tom
            """.trimIndent(),
            """
            Hi Maria,
            Your order shipped this morning.
            It should arrive by Thursday.

            Best regards,
            Tom
            """.trimIndent(),
        )
    val withBreaks = createMarkdown { lineBreaks = true }
    withBreaks.content = letter
    val withoutBreaks = createMarkdown()
    withoutBreaks.content = letter
    lineBreakSection.body.append(
        example(tr("左：lineBreaks 打开　右：默认", "Left: lineBreaks on. Right: default."), row(withBreaks, withoutBreaks)),
    )
    view.body.append(lineBreakSection.root)

    return view
}
