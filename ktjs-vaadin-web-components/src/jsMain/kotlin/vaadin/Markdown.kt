package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-markdown>` is a web component for rendering Markdown content.
 * It takes Markdown source as input and renders the corresponding HTML.
 *
 * ### Styling
 *
 * The component does not have specific shadow DOM parts for styling the rendered Markdown content itself,
 * as the content is rendered directly into the component's light DOM via a slot.
 * You can style the rendered HTML elements using standard CSS selectors targeting the `vaadin-markdown` element.
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/markdown)
 */
external class MarkdownElement : HTMLElement {
    /**
     * The Markdown content.
     */
    var content: String?

    /**
     * When set to `true`, single line breaks ("soft breaks") within a
     * paragraph are converted into `<br>` elements, the same way chat and
     * message-style Markdown behaves.
     *
     * By default, following the original Markdown specification, single line
     * breaks are ignored (collapsed into a space) and a blank line is
     * required to start a new paragraph.
     */
    var lineBreaks: Boolean
}

/**
 * Named exports of `@vaadin/markdown/vaadin-markdown.js`.
 *
 * Referencing the module registers the `<vaadin-markdown>` custom element.
 */
@JsModule("@vaadin/markdown/vaadin-markdown.js")
private external object MarkdownModule {
    /** The registered custom element class. */
    val Markdown: dynamic
}

/**
 * Imports `@vaadin/markdown/vaadin-markdown.js`, registering the `<vaadin-markdown>` custom element,
 * and returns the element class.
 */
fun registerMarkdown(): dynamic = MarkdownModule.Markdown

/**
 * Creates a `<vaadin-markdown>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createMarkdown(configure: MarkdownElement.() -> Unit = {}): MarkdownElement {
    registerMarkdown()
    return document.createElement(VaadinTagName.markdown).apply(configure)
}
