package demo.pages.selection

import io.github.shijianjs.ktjs.demo.page.Blocks.col as kitCol
import io.github.shijianjs.ktjs.demo.page.Blocks.row as kitRow
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import web.html.HtmlTagName
import io.github.shijianjs.ktjs.dom.element.invoke
import demo.pages.fixtures.Person
import kotlin.js.js
import io.github.shijianjs.ktjs.vaadin.avatar.createAvatar

        /**
         * `<vaadin-demo-combo-person>`: one combo box item — avatar, name and profession.
         *
         * The combo box hands its renderer a container and asks it to fill that container, so the item has to
         * be a node of its own; it is an element, which keeps the assembling inside a component class.
         */

internal class ComboPersonRow : CustomElementAdapter() {
    /** Fills the row from [person]; the item content fills the overlay row, so the host is a block. */
    fun show(person: Person) {
        style.setProperty("display", "block")
        replaceChildren(
            kitRow(
                createAvatar {
                    name = person.displayName
                    img = person.pictureUrl
                },
                kitCol(
                    HtmlTagName.span { textContent = person.displayName },
                    HtmlTagName.span { textContent = person.profession },
                ),
            ),
        )
    }

    companion object : ComboPersonRowDefaultReference(name = "vaadin-demo-combo-person")
}
