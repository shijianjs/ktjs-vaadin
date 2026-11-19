package demo.pages.overlays

import io.github.shijianjs.ktjs.dom.element.invoke

import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import io.github.shijianjs.ktjs.dom.attribute.tokenListAttribute
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachShadowRoot
import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import web.components.CustomElementReference
import web.dom.TagName
import demo.pages.fixtures.Person
import demo.pages.fixtures.people
import demo.pages.fixtures.personItems
import demo.tr
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.contextmenu.ContextMenuItemData
import io.github.shijianjs.ktjs.vaadin.grid.GridElement
import io.github.shijianjs.ktjs.vaadin.grid.createGrid
import io.github.shijianjs.ktjs.vaadin.grid.createGridColumn
import io.github.shijianjs.ktjs.vaadin.grid.createGridSelectionColumn
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
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

/**
 * `<vaadin-demo-icon-button>`: the icon-only button the overlay examples put into their toolbars.
 *
 * The host wraps the button because the theme a caller adds (`tertiary` on the tooltip targets) belongs
 * on the button, and [addTheme] hands it there.
 */
internal class IconButton : CustomElementAdapter() {
    private val button = createButton { textContent = "" }.apply { addTheme("icon") }

    init {
        attachShadowRoot().append(button)
    }

    /** Sets the accessible name and the icons the button shows. */
    fun show(name: String, vararg iconNames: String) {
        button.setAttribute("aria-label", name)
        button.textContent = ""
        iconNames.forEach { button.append(createIcon { icon = it }) }
    }

    /** Adds [themes] to the wrapped button, not to the host. */
    fun addTheme(vararg themes: String) {
        button.tokenListAttribute("theme").add(*themes)
    }

    companion object : CustomElementReference<IconButton>(IconButton::class.js) {
        val NAME: TagName<IconButton> = TagName("vaadin-demo-icon-button")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<vaadin-demo-icon-button>`. */
internal fun newIconButton(name: String, vararg iconNames: String): IconButton {
    IconButton.define()
    return IconButton.NAME().also { it.show(name, *iconNames) }
}

internal fun HTMLElement.ariaLabel(name: String) {
    setAttribute("aria-label", name)
}

/**
 * Upstream types the extra data as `ContextMenuItemData<{ value?: string }>`; Kotlin models the
 * generic intersection by extending the wrapper interface.
 */
internal external interface CopyMenuItem : ContextMenuItemData {
    var value: String?
}

internal fun pad2(value: Int): String = if (value < 10) "0$value" else value.toString()
