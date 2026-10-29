package demo.pages.overlays

import demo.pages.fixtures.Person
import demo.pages.fixtures.people
import demo.pages.fixtures.personItems
import demo.tr
import demo.ui.withTheme
import vaadin.button.createButton
import vaadin.contextmenu.ContextMenuItemData
import vaadin.grid.GridElement
import vaadin.grid.createGrid
import vaadin.grid.createGridColumn
import vaadin.grid.createGridSelectionColumn
import vaadin.icon.createIcon
import web.events.EventType
import web.html.HTMLElement
import web.keyboard.KeyboardEvent

/** Grid of people with the columns the overlay examples filter and right-click on. */
internal fun personGrid(list: List<Person> = people(5), withSelection: Boolean = false): GridElement {
    val table = createGrid()
    table.items = personItems(list, withDisplayName = true)
    table.allRowsVisible = true
    if (withSelection) table.append(createGridSelectionColumn())
    table.append(
        createGridColumn {
            header = tr("名", "First name")
            path = "firstName"
        },
        createGridColumn {
            header = tr("姓", "Last name")
            path = "lastName"
        },
        createGridColumn {
            header = tr("邮箱", "Email")
            path = "email"
        },
        createGridColumn {
            header = tr("职务", "Profession")
            path = "profession"
        },
    )
    return table
}

internal val KEY_DOWN: EventType<KeyboardEvent> = EventType("keydown")

internal fun iconButton(name: String, vararg iconNames: String): HTMLElement =
    createButton { textContent = "" }.apply {
        withTheme("icon")
        ariaLabel(name)
        iconNames.forEach { append(createIcon { icon = it }) }
    }

internal fun HTMLElement.ariaLabel(name: String) {
    setAttribute("aria-label", name)
}

internal fun slotted(slotName: String, element: HTMLElement): HTMLElement {
    element.slot = slotName
    return element
}

/**
 * Upstream types the extra data as `ContextMenuItemData<{ value?: string }>`; Kotlin models the
 * generic intersection by extending the wrapper interface.
 */
internal external interface CopyMenuItem : ContextMenuItemData {
    var value: String?
}

internal fun pad2(value: Int): String = if (value < 10) "0$value" else value.toString()
