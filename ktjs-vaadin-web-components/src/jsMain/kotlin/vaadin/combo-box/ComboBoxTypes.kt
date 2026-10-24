package vaadin.combobox

import web.html.HTMLElement

/** The model passed to a combo box item renderer. */
external interface ComboBoxItemModel {
    /** The index of the item in the filtered items. */
    var index: Double

    /** The item data. */
    var item: kotlin.js.JsAny?

    /** Whether the item is selected. */
    var selected: Boolean

    /** Whether the item is focused. */
    var focused: Boolean
}

/** Parameters passed to a [ComboBoxDataProvider]. */
external interface ComboBoxDataProviderParams {
    /** The zero-based page index. */
    var page: Double

    /** The number of items in a page. */
    var pageSize: Double

    /** The current filter string. */
    var filter: String
}

/** Receives a page of items, and optionally the total item count, from a [ComboBoxDataProvider]. */
typealias ComboBoxDataProviderCallback = (Array<kotlin.js.JsAny?>, Double?) -> Unit

/** Loads combo box items lazily, page by page. */
typealias ComboBoxDataProvider = (ComboBoxDataProviderParams, ComboBoxDataProviderCallback) -> Unit

/** Renders the content of a combo box item. */
typealias ComboBoxRenderer = (HTMLElement, ComboBoxElement, ComboBoxItemModel) -> Unit
