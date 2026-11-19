package demo.ui

import io.github.shijianjs.ktjs.demo.page.codeText
import web.cssom.ClassName
import web.dom.Node
import web.dom.TagName
import web.dom.document
import web.html.HTMLAnchorElement
import web.html.HTMLHeadingElement
import web.html.HTMLElement
import web.html.Hidden
import web.html.HtmlTagName
import web.html.`false`
import web.html.`true`
import web.window.WindowTarget
import web.window._blank

/**
 * Small DOM builders used by every demo page.
 *
 * Everything is created node by node and appended in place: no markup string is ever parsed, so
 * the pages follow the same retention rules as the rest of the project (a tree is built once and
 * mutated afterwards).
 */

/**
 * Creates an element of [tagName], optionally with a CSS class, and appends [children].
 *
 * `className` is a [ClassName] rather than a string on purpose: the object is the thing the page's
 * sheet keys and the markup both point at, so it is declared once (`private val X_CLASS =
 * ClassName("x")`) and passed around. String forms of class names only exist at that declaration.
 */
fun <T : HTMLElement> element(tagName: TagName<T>, className: ClassName? = null, vararg children: Node): T {
    val created = document.createElement(tagName)
    if (className != null) created.className = className
    if (children.isNotEmpty()) created.append(*children)
    return created
}

/** Creates a `<div>`. */
fun div(className: ClassName? = null, vararg children: Node): HTMLElement = element(HtmlTagName.div, className, *children)

/** Creates a `<section>`. */
fun section(className: ClassName? = null, vararg children: Node): HTMLElement =
    element(HtmlTagName.section, className, *children)

/** Creates a `<p>` with [text]. */
fun paragraph(text: String, className: ClassName? = null): HTMLElement =
    element(HtmlTagName.p, className).apply { textContent = text }

/** Creates a `<span>` with [text]. */
fun span(text: String, className: ClassName? = null): HTMLElement =
    element(HtmlTagName.span, className).apply { textContent = text }

/** Creates a `<strong>` with [text]. */
fun strong(text: String): HTMLElement = element(HtmlTagName.strong).apply { textContent = text }

/** Creates a heading of [level] (1-4): anything below 1 becomes `<h1>`, anything above 4 `<h4>`. */
fun heading(level: Int, text: String, className: ClassName? = null): HTMLElement {
    val created: HTMLHeadingElement =
        when (level.coerceIn(1, 4)) {
            1 -> element(HtmlTagName.h1)
            2 -> element(HtmlTagName.h2)
            3 -> element(HtmlTagName.h3)
            else -> element(HtmlTagName.h4)
        }
    if (className != null) created.className = className
    created.textContent = text
    return created
}

/** Creates an `<a>` pointing at [href]. External links open in a new tab without a referrer. */
fun anchor(text: String, href: String, className: ClassName? = null, external: Boolean = false): HTMLAnchorElement {
    val created = element(HtmlTagName.a, className)
    created.textContent = text
    created.href = href
    if (external) {
        created.target = WindowTarget.Companion._blank
        created.rel = "noopener noreferrer"
    }
    return created
}

/** Shows or hides the element with the native `hidden` property. */
fun HTMLElement.setHidden(hidden: Boolean) {
    this.hidden = if (hidden) Hidden.Companion.`true` else Hidden.Companion.`false`
}

/** Whether the element carries the native `hidden` attribute. */
fun HTMLElement.isHidden(): Boolean = hasAttribute("hidden")

/** Creates an inline `<code>` fragment; the class and its rule are the kit's, so this delegates. */
fun code(text: String): HTMLElement = codeText(text)

/** Creates a `<ul>` from [items], one `<li>` per entry. */
fun bulletList(items: List<String>, className: ClassName? = null): HTMLElement {
    val list = element(HtmlTagName.ul, className)
    items.forEach { item ->
        list.append(element(HtmlTagName.li).apply { textContent = item })
    }
    return list
}


/**
 * Sets the `theme` attribute from [variants]; no variants removes it.
 *
 * Vaadin exposes `theme` only as an attribute (there is no JS property), so this helper is the
 * single place that touches the raw attribute API.
 */
fun <T : HTMLElement> T.withTheme(vararg variants: String): T {
    if (variants.isEmpty()) {
        removeAttribute("theme")
    } else {
        setAttribute("theme", variants.joinToString(" "))
    }
    return this
}
