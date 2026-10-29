package demo.pages.data

import demo.pages.fixtures.Person
import demo.tr
import demo.ui.div
import demo.ui.jsArray
import demo.ui.row
import demo.ui.span
import demo.ui.withTheme
import kotlin.js.Date
import kotlin.js.JsAny
import kotlin.js.js
import vaadin.avatar.createAvatar
import vaadin.grid.GridElement
import vaadin.grid.GridSorterDirection
import vaadin.grid.GridSorterDirections
import vaadin.grid.createGrid
import vaadin.icon.createIcon
import web.html.HTMLElement

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

internal fun ageText(iso: String): String {
    val year = iso.take(4).toIntOrNull() ?: return ""
    val age = Date().getFullYear() - year
    return tr("年龄：$age 岁", "Age: $age")
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

/** Header content with a trailing info icon, as used by the header renderer example. */
internal fun headerWithIcon(label: String, icon: String, title: String): HTMLElement =
    row(span(label), createIcon { this.icon = icon }.withTheme("small")).apply {
        setAttribute("title", title)
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

/** Avatar + name + email, the two-line cell the docs render through a Lit renderer. */
internal fun personCell(item: JsAny?): HTMLElement =
    div("person-item").apply {
        append(
            createAvatar {
                name = "${itemText(item, "firstName")} ${itemText(item, "lastName")}"
                img = itemText(item, "pictureUrl")
            },
            div("person-item__text").apply {
                append(span(itemText(item, "displayName"), "person-item__name"), span(itemText(item, "email"), "person-item__email"))
            },
        )
    }

// endregion
