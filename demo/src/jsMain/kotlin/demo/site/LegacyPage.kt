package demo.site

import demo.CatalogEntry
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.nav.Page
import io.github.shijianjs.ktjs.demo.style.RESET
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachStyledShadow
import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import web.components.CustomElementReference
import web.dom.Node
import web.dom.TagName
import web.html.HTMLElement
import web.html.HtmlTagName

/**
 * `<ktjs-legacy-page>`: a page built by the pre-kit code, kept inside its own shadow tree.
 *
 * The forty remaining page builders still create their DOM against the class names of `demo.css`
 * (`page`, `section`, `example`, `row`, …). The kit mounts pages inside its own shadow root, where
 * document-level rules cannot reach, so this element brings the page's stylesheet with it: a `<link>`
 * in the same shadow tree scopes `demo.css` to exactly the pages that still use it. Pages migrate to
 * kit blocks one group at a time and each one leaves this host behind; when the last page moves, the
 * element and the stylesheet both go.
 */
class LegacyPageHost : CustomElementAdapter() {
    private val stylesheet = HtmlTagName.link {
        rel = "stylesheet"
        href = "demo.css"
    }

    private val content = HtmlTagName.div {}

    init {
        // The kit's reset first: it carries `:host { display: block }` and `box-sizing`, which a
        // custom element needs before any of the page's own rules can lay it out.
        attachStyledShadow(RESET).append(stylesheet, content)
    }

    /** Replaces the hosted page; called once at creation and again on every language switch. */
    fun show(node: Node) {
        content.replaceChildren(node)
    }

    companion object : CustomElementReference<LegacyPageHost>(LegacyPageHost::class.js) {
        val NAME: TagName<LegacyPageHost> = TagName("ktjs-legacy-page")

        /** Creates a defined host; every page creation goes through here. */
        fun create(): LegacyPageHost {
            defineIfMissing(NAME, this)
            return NAME()
        }
    }
}

/**
 * A catalog page behind the kit's [Page] contract.
 *
 * The page's copy is resolved by `tr(zh, en)` while its DOM is built, so a language switch rebuilds
 * the hosted tree rather than rewriting it — the same behaviour the pre-kit shell had, kept until
 * each page is converted to its own `Translations` table. The host element (and the page's scroll
 * position inside the layout) stays: only its children are replaced.
 */
internal class LegacyPage(private val entry: CatalogEntry) : Page {
    private val host: LegacyPageHost = LegacyPageHost.create()

    override val node: HTMLElement get() = host

    init {
        render()
    }

    override fun applyTranslation(lang: Lang) {
        // `main` mirrors the kit's language into the demo's own state before this is called, so the
        // builders below resolve their strings against the new language.
        render()
    }

    private fun render() {
        host.show(entry.create() as HTMLElement)
    }
}
