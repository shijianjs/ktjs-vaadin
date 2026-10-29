package demo.pages.fixtures

import demo.pages.pickers.pad2
import demo.tr
import demo.ui.Images
import demo.ui.jsArray
import kotlin.js.JsAny

// ---------------------------------------------------------------------------------------------
// Shared data
//
// The docs examples load fixture data from a backend. The demo has none, so the same shapes are
// kept in local JSON that the pages parse into the wrapper's `items` arrays.
// ---------------------------------------------------------------------------------------------

internal const val CITIES_JSON =
    """
    [
      {"label": "Tallinn", "value": "tallinn"},
      {"label": "Helsinki", "value": "helsinki"},
      {"label": "Berlin", "value": "berlin"},
      {"label": "Lisbon", "value": "lisbon"}
    ]
    """

internal const val SORT_ITEMS_JSON =
    """
    [
      {"label": "Most recent first", "value": "recent"},
      {"label": "Rating: high to low", "value": "rating-desc"},
      {"label": "Rating: low to high", "value": "rating-asc"},
      {"label": "Price: high to low", "value": "price-desc"},
      {"label": "Price: low to high", "value": "price-asc"}
    ]
    """

/** One fixture person; the fields mirror the `Person` DTO the docs examples load from the backend. */
internal class Person(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val profession: String,
) {
    val displayName: String
        get() = "$firstName $lastName"

    val email: String
        get() = "${firstName.lowercase()}.${lastName.lowercase()}@example.com"

    val pictureUrl: String
        get() = Images.portrait(id)

    // The grid examples need the remaining fields of the upstream `Person` DTO; they are derived
    // from the id so that every example shows the same person on every visit.

    val birthday: String
        get() = "${1970 + (id * 3) % 30}-${pad2((id * 5) % 12 + 1)}-${pad2((id * 7) % 27 + 1)}"

    val status: String
        get() = if (id % 3 == 0) "Unavailable" else "Available"

    val subscriber: Boolean
        get() = id % 2 == 1

    val membership: String
        get() = MEMBERSHIPS[id % MEMBERSHIPS.size]

    val customerRating: Double
        get() = (id * 37 % 100) / 10.0

    val street: String
        get() = "${100 + id * 7} Elm Street"

    val city: String
        get() = CITIES[id % CITIES.size]

    val zip: String
        get() = "02${100 + id}"

    val state: String
        get() = STATES[id % STATES.size]

    val phone: String
        get() = "+1 ${300 + id} 555 0${100 + id}"

    private fun pad2(value: Int): String = if (value < 10) "0$value" else "$value"
}

internal val MEMBERSHIPS = listOf("Regular", "Premium", "VIP")

internal val CITIES = listOf("Boston", "Austin", "Denver", "Portland", "Chicago", "Seattle")

internal val STATES = listOf("MA", "TX", "CO", "OR", "IL", "WA")

internal fun people(count: Int = 6): List<Person> {
    val firstNames = listOf("Ada", "Grace", "Katherine", "Alan", "Barbara", "Edsger", "Margaret", "Dennis")
    val lastNames = listOf("Lovelace", "Hopper", "Johnson", "Turing", "Liskov", "Dijkstra", "Hamilton", "Ritchie")
    val professions =
        listOf(
            tr("软件工程师", "Software Engineer"),
            tr("产品设计师", "Product Designer"),
            tr("数据分析师", "Data Analyst"),
            tr("前端开发", "Frontend Developer"),
            tr("架构师", "Architect"),
            tr("测试工程师", "Test Engineer"),
        )
    return (0 until count).map { index ->
        Person(
            id = index + 1,
            firstName = firstNames[index % firstNames.size],
            lastName = lastNames[index % lastNames.size],
            profession = professions[index % professions.size],
        )
    }
}

/** Serialises [list] into the object array the item-based wrappers take. */
internal fun personItems(list: List<Person>, withDisplayName: Boolean = false): Array<JsAny> {
    val entries =
        list.map { person ->
            val fields =
                mutableListOf(
                    "\"id\": ${person.id}",
                    "\"firstName\": \"${person.firstName}\"",
                    "\"lastName\": \"${person.lastName}\"",
                    "\"profession\": \"${person.profession}\"",
                    "\"email\": \"${person.email}\"",
                    "\"pictureUrl\": \"${person.pictureUrl}\"",
                )
            if (withDisplayName) fields += "\"displayName\": \"${person.displayName}\""
            "{${fields.joinToString(", ")}}"
        }
    return jsArray("[${entries.joinToString(", ")}]")
}

// region Shared helpers for the features file

internal fun filterPeople(list: List<Person>, term: String): List<Person> {
    if (term.isEmpty()) return list
    return list.filter { person ->
        person.displayName.contains(term, ignoreCase = true) ||
            person.email.contains(term, ignoreCase = true) ||
            person.profession.contains(term, ignoreCase = true)
    }
}
