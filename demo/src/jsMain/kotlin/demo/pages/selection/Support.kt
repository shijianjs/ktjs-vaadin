package demo.pages.selection

internal const val FRUITS_JSON = """["Apple", "Banana", "Orange", "Pear"]"""

internal const val COUNTRIES_JSON =
    """
    [
      {"id": 1, "name": "Finland"},
      {"id": 2, "name": "Germany"},
      {"id": 3, "name": "France"},
      {"id": 4, "name": "Italy"},
      {"id": 5, "name": "Spain"},
      {"id": 6, "name": "Portugal"},
      {"id": 7, "name": "Netherlands"},
      {"id": 8, "name": "Belgium"}
    ]
    """

/** Maps a fruit to the class name used by the docs theme for combo box items. */
internal fun fruitClass(fruit: String): String =
    when (fruit) {
        "Apple" -> "coral"
        "Banana" -> "gold"
        "Orange" -> "orange"
        "Pear" -> "yellowgreen"
        else -> ""
    }
