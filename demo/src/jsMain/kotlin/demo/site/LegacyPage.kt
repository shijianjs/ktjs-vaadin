package demo.site

import demo.CatalogEntry
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.nav.Page
import io.github.shijianjs.ktjs.demo.style.RESET
import web.components.ShadowRoot
import web.cssom.CSSStyleSheet
import web.cssom.Display
import web.cssom.pct
import io.github.shijianjs.ktjs.dom.css.cssSheet
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachStyledShadow
import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import web.components.CustomElementReference
import web.dom.Node
import web.dom.TagName
import web.html.HTMLElement
import web.html.HtmlTagName

/** The slot's own rule: it spans the column it is placed in. */
private val HOST_SHEET: CSSStyleSheet = cssSheet {
    ":host" {
        display = Display.block
        width = 100.pct
    }
}

/**
 * `<ktjs-page-swap>`: a stable slot for a page that is **rebuilt** when the language changes.
 *
 * Most pages still resolve their copy through `tr(zh, en)` while their DOM is built, so switching
 * language means building the page again rather than rewriting its text. The kit's layout expects a
 * page node it keeps mounted, so this element gives it one fixed node and swaps the page inside.
 *
 * The hosted page is itself a `<ktjs-demo-page>` with its own shadow tree and the kit's sheet, so this
 * slot carries no styling: it is a position in the layout, nothing more.
 */
class PageSwapHost : CustomElementAdapter() {
    // The kit's reset carries `:host { display: block }` and `box-sizing`, which a custom element needs
    // before anything inside it can be laid out. The slot itself must span the layout's column: a
    // vertical layout sizes children to their content, and a page narrower than the column would leave
    // the section cards short of the right edge.
    private val shadow: ShadowRoot = attachStyledShadow(RESET, HOST_SHEET)

    /** Replaces the hosted page; called once at creation and again on every language switch. */
    fun show(node: Node) {
        shadow.replaceChildren(node)
    }

    companion object : CustomElementReference<PageSwapHost>(PageSwapHost::class.js) {
        val NAME: TagName<PageSwapHost> = TagName("ktjs-page-swap")

        /** Creates a defined slot; every rebuilt page goes through here. */
        fun create(): PageSwapHost {
            defineIfMissing(NAME, this)
            return NAME()
        }
    }
}

/**
 * A catalog page behind the kit's [Page] contract, rebuilt on a language switch.
 *
 * Pages that own a `Translations` table rewrite their text in place and do not use this; this is for
 * the ones whose copy is resolved at build time. The slot element stays — only its content is replaced.
 */
internal class RebuiltPage(private val entry: CatalogEntry) : Page {
    private val host: PageSwapHost = PageSwapHost.create()

    override val node: HTMLElement get() = host

    init {
        render()
    }

    override fun applyTranslation(lang: Lang) {
        // `Main` mirrors the kit's language into the demo's own state before this is called, so the
        // builders below resolve their strings against the new language.
        render()
    }

    private fun render() {
        host.show(entry.create() as HTMLElement)
    }
}
