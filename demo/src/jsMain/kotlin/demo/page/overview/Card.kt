package demo.page.overview

import demo.CatalogEntry
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.card.CardElement
import io.github.shijianjs.ktjs.dom.css.dsl.invoke
import web.html.HTMLAnchorElement
import web.html.HTMLElement

/** The card and the link around it; a language switch rewrites the title and the summary. */
internal class Card(
    val entry: CatalogEntry,
    val node: CardElement,
    val link: HTMLAnchorElement,
    val summary: HTMLElement,
)
