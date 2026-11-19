package demo.pages.selection

import io.github.shijianjs.ktjs.dom.css.cssSheet
import io.github.shijianjs.ktjs.dom.css.dsl.className
import io.github.shijianjs.ktjs.dom.css.dsl.compound
import io.github.shijianjs.ktjs.dom.css.dsl.invoke
import io.github.shijianjs.ktjs.dom.css.dsl.tag
import io.github.shijianjs.ktjs.vaadin.VaadinTagName
import web.cssom.CSSStyleSheet
import web.cssom.ClassName
import web.cssom.Color

/**
 * The colours the "customise items" examples give to fruits, shared by the two selection pages that
 * use [fruitClass]: a combo box item is coloured by its text, a multi-select chip by its background.
 *
 * The class names live on light-DOM items that sit in the same shadow tree as the page, so one sheet
 * covers both pages; it is adopted by each of them through `view.addSheet(...)`.
 */
internal val FRUIT_SHEET: CSSStyleSheet = cssSheet {
    // One `ClassName` + colour per fruit, so each name is declared once and the three item tags
    // reuse it instead of spelling the class name out three times.
    val fruitNames: List<Pair<ClassName, Color>> =
        listOf(
            ClassName("coral") to Color("coral"),
            ClassName("gold") to Color("gold"),
            ClassName("orange") to Color("orange"),
            ClassName("yellowgreen") to Color("yellowgreen"),
        )

    fruitNames.forEach { (fruit, colour) ->
        compound(tag(VaadinTagName.comboBoxItem), className(fruit)).invoke { color = colour }
        compound(tag(VaadinTagName.multiSelectComboBoxItem), className(fruit)).invoke { color = colour }
        compound(tag(VaadinTagName.multiSelectComboBoxChip), className(fruit)).invoke { backgroundColor = colour }
    }
}
