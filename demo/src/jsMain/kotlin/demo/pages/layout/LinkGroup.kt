package demo.pages.layout

import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.details.DetailsElement
import web.html.HTMLAnchorElement

/** One link group of the content example: the details block and the anchors inside it. */
internal class LinkGroup(val details: DetailsElement, val anchors: List<HTMLAnchorElement>)
