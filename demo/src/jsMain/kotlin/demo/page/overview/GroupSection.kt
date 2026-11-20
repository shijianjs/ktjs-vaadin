package demo.page.overview

import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.css.dsl.invoke
import web.html.HTMLElement

internal class GroupSection(val title: HTMLElement, val label: HTMLElement, val cards: List<Card>)
