package demo.pages.selection

import io.github.shijianjs.ktjs.dom.css.cssSheet
import web.cssom.CSSStyleSheet
import web.cssom.Color

/**
 * The colours the "customise items" examples give to fruits, shared by the two selection pages that
 * use [fruitClass]: a combo box item is coloured by its text, a multi-select chip by its background.
 *
 * The class names live on light-DOM items that sit in the same shadow tree as the page, so one sheet
 * covers both pages; it is adopted by each of them through `view.addSheet(...)`.
 */
internal val FRUIT_SHEET: CSSStyleSheet = cssSheet {
    "vaadin-combo-box-item.coral" { color = Color("coral") }
    "vaadin-combo-box-item.gold" { color = Color("gold") }
    "vaadin-combo-box-item.orange" { color = Color("orange") }
    "vaadin-combo-box-item.yellowgreen" { color = Color("yellowgreen") }

    "vaadin-multi-select-combo-box-item.coral" { color = Color("coral") }
    "vaadin-multi-select-combo-box-item.gold" { color = Color("gold") }
    "vaadin-multi-select-combo-box-item.orange" { color = Color("orange") }
    "vaadin-multi-select-combo-box-item.yellowgreen" { color = Color("yellowgreen") }

    "vaadin-multi-select-combo-box-chip.coral" { backgroundColor = Color("coral") }
    "vaadin-multi-select-combo-box-chip.gold" { backgroundColor = Color("gold") }
    "vaadin-multi-select-combo-box-chip.orange" { backgroundColor = Color("orange") }
    "vaadin-multi-select-combo-box-chip.yellowgreen" { backgroundColor = Color("yellowgreen") }
}
