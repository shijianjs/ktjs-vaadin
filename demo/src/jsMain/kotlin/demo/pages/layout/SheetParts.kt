package demo.pages.layout

import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.tabs.TabElement
import io.github.shijianjs.ktjs.vaadin.tabsheet.TabSheetElement
import web.html.HTMLElement

/** A sheet with its three tab labels and three panel bodies, so the page can rewrite them. */
internal class SheetParts(val sheet: TabSheetElement, val tabs: List<TabElement>, val panels: List<HTMLElement>)
