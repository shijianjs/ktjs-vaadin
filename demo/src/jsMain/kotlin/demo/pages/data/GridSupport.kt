package demo.pages.data

import io.github.shijianjs.ktjs.demo.page.Blocks.row as kitRow
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachShadowRoot
import web.html.HtmlTagName
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import demo.pages.fixtures.Person
import demo.ui.jsArray
import kotlin.js.Date
import kotlin.js.JsAny
import kotlin.js.js
import io.github.shijianjs.ktjs.vaadin.grid.GridElement
import io.github.shijianjs.ktjs.vaadin.grid.GridSorterDirection
import io.github.shijianjs.ktjs.vaadin.grid.GridSorterDirections
import io.github.shijianjs.ktjs.vaadin.grid.createGrid
import io.github.shijianjs.ktjs.vaadin.icon.createIcon

// region Fixture

/**
 * Serialises [list] with the fields the grid examples use. `address` stays a nested object so
 * columns can bind dotted paths (`address.phone`).
 */
internal fun gridItems(list: List<Person>, withRating: Boolean = false): Array<JsAny> {
    val entries =
        list.map { person ->
            val fields =
                mutableListOf(
                    "\"id\": ${person.id}",
                    "\"firstName\": \"${person.firstName}\"",
                    "\"lastName\": \"${person.lastName}\"",
                    "\"displayName\": \"${person.displayName}\"",
                    "\"email\": \"${person.email}\"",
                    "\"profession\": \"${person.profession}\"",
                    "\"birthday\": \"${person.birthday}\"",
                    "\"status\": \"${person.status}\"",
                    "\"subscriber\": ${person.subscriber}",
                    "\"membership\": \"${person.membership}\"",
                    "\"pictureUrl\": \"${person.pictureUrl}\"",
                    "\"address\": {\"street\": \"${person.street}\", \"city\": \"${person.city}\", " +
                        "\"zip\": \"${person.zip}\", \"state\": \"${person.state}\", " +
                        "\"phone\": \"${person.phone}\"}",
                )
            if (withRating) fields += "\"customerRating\": ${person.customerRating}"
            "{${fields.joinToString(", ")}}"
        }
    return jsArray("[${entries.joinToString(", ")}]")
}

/** Same as [gridItems], typed for the callbacks that accept nullable items. */
internal fun gridItemsNullable(list: List<Person>, withRating: Boolean = false): Array<JsAny?> =
    gridItems(list, withRating).map { it as JsAny? }.toTypedArray()

/** The `items` array of a grid created empty and filled from code. */
internal fun GridElement.fill(list: List<Person>) {
    items = gridItems(list)
}

internal fun itemText(item: JsAny?, key: String): String =
    js("(item, key) => item == null || item[key] == null ? '' : String(item[key])")(item, key) as String

internal fun itemPathText(item: JsAny?, path: String): String =
    js(
        "(item, path) => {" +
            " let value = item;" +
            " for (const key of path.split('.')) { if (value == null) return ''; value = value[key]; }" +
            " return value == null ? '' : String(value);" +
            "}",
    )(item, path) as String

internal fun itemId(item: JsAny?): Int = (js("(item) => item == null || item.id == null ? -1 : Number(item.id)")(item) as Double).toInt()

internal fun itemFlag(item: JsAny?, key: String): Boolean =
    js("(item, key) => !!(item && item[key])")(item, key) as Boolean

internal fun twoDigits(value: Int): String = if (value < 10) "0$value" else "$value"

/** `YYYY-MM-DD` → `MM/DD/YYYY`, the format the docs examples display. */
internal fun birthdayText(iso: String): String {
    val year = iso.take(4)
    if (year.isEmpty()) return ""
    return "${iso.substring(5, 7)}/${iso.substring(8, 10)}/$year"
}

internal fun ageOf(iso: String): Int {
    val year = iso.take(4).toIntOrNull() ?: return 0
    return Date().getFullYear() - year
}

/** `MM/DD/YYYY` of a due date derived from the id, so the example is stable between visits. */
internal fun dueDate(id: Int): String {
    val year = 2030 + (id * 3) % 5
    return "${twoDigits((id * 5) % 12 + 1)}/${twoDigits((id * 7) % 27 + 1)}/$year"
}

/** `€1 234` of an amount derived from the id. */
internal fun euroAmount(id: Int): String {
    val amount = (id * 1234) % 10000
    val thousands = amount / 1000
    val rest = amount % 1000
    return if (thousands == 0) "€$amount" else "€$thousands ${pad3(rest)}"
}

private fun pad3(value: Int): String = if (value < 100) "0${twoDigits(value)}" else "$value"

internal fun newGrid(height: String = "18rem"): GridElement =
    createGrid().apply {
        // Grid defaults to `height: 100%`; inside an auto-height container it needs a fixed height.
        style.setProperty("height", height)
    }

/**
 * `<vaadin-demo-grid-header>`: header content with a trailing info icon, as used by the header
 * renderer example. The `title` tooltip sits on the host, which is the node the header shows.
 */
internal class GridHeader : CustomElementAdapter() {
    private val labelNode = HtmlTagName.span()
    private val mark = createIcon { }.addTheme("small")

    init {
        attachShadowRoot().append(kitRow(labelNode, mark))
    }

    /** Sets the label, the icon and the tooltip title. */
    fun show(label: String, icon: String, title: String) {
        this.label = label
        mark.icon = icon
        this.tooltip = title
    }

    /** The header label; a property rather than a text node, so the page binds it with a setter. */
    var label: String = ""
        set(value) {
            field = value
            labelNode.textContent = value
        }

    /** The tooltip on the info icon; kept as an attribute, which a page binds through its setter. */
    var tooltip: String = ""
        set(value) {
            field = value
            setAttribute("title", value)
        }

    companion object : GridHeaderDefaultReference()
}


internal fun sortPeople(list: List<Person>, path: String?, direction: GridSorterDirection?): List<Person> {
    if (path == null || direction == null) return list
    val comparator =
        when (path) {
            "displayName" -> compareBy<Person> { it.displayName }
            "profession" -> compareBy { it.profession }
            "email" -> compareBy { it.email }
            "birthday" -> compareBy { it.birthday }
            "id" -> compareBy { it.id }
            else -> return list
        }
    return if (direction == GridSorterDirections.desc) list.sortedWith(comparator.reversed()) else list.sortedWith(comparator)
}

/**
 * `<vaadin-demo-person-cell>`: avatar + name + email, the two-line cell the docs render through a
 * renderer. The cell is an element so a renderer hands back one node with its own shadow rather than
 * a `div` rebuilt per row.
 */


// endregion
