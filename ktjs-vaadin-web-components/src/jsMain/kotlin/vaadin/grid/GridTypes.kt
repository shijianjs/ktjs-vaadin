package vaadin.grid

import js.reflect.unsafeCast
import web.html.HTMLElement

/** The model passed to grid item renderers and item-based callbacks. */
external interface GridItemModel {
    /** The index of the item in the grid. */
    var index: Double

    /** The item data. */
    var item: kotlin.js.JsAny?

    /** Whether the item is selected. */
    var selected: Boolean?

    /** Whether the item is expanded. */
    var expanded: Boolean?

    /** The tree level of the item. */
    var level: Double?

    /** Whether the row details of the item are open. */
    var detailsOpened: Boolean?

    /** Whether the item has children. */
    var hasChildren: Boolean?
}

/** A filter applied by a [GridDataProvider]. */
external interface GridFilterDefinition {
    /** The property path of the filtered field. */
    var path: String

    /** The filter value. */
    var value: String
}

/** A sort order applied by a [GridDataProvider]. */
external interface GridSorterDefinition {
    /** The property path of the sorted field. */
    var path: String

    /** The sort direction. */
    var direction: GridSorterDirection?
}

/** Parameters passed to a [GridDataProvider]. */
external interface GridDataProviderParams {
    /** The zero-based page index. */
    var page: Double

    /** The number of items in a page. */
    var pageSize: Double

    /** The active filters. */
    var filters: Array<GridFilterDefinition>

    /** The active sort orders. */
    var sortOrders: Array<GridSorterDefinition>

    /** The parent item, when loading the children of a tree grid item. */
    var parentItem: kotlin.js.JsAny?
}

/** Receives a page of items, and optionally the total item count, from a [GridDataProvider]. */
typealias GridDataProviderCallback = (Array<kotlin.js.JsAny?>, Double?) -> Unit

/** Loads grid items lazily, page by page. */
typealias GridDataProvider = (GridDataProviderParams, GridDataProviderCallback) -> Unit

/** Renders a grid body cell. */
typealias GridBodyRenderer = (HTMLElement, GridColumnElement, GridItemModel) -> Unit

/** Renders a grid header or footer cell. */
typealias GridHeaderFooterRenderer = (HTMLElement, GridColumnElement) -> Unit

/** Renders the row details of an item. */
typealias GridRowDetailsRenderer = (HTMLElement, GridElement, GridItemModel) -> Unit

/** Generates CSS `part` names for grid cells. */
typealias GridCellPartNameGenerator = (GridColumnElement, GridItemModel) -> String

/** Decides whether an item can be dragged. */
typealias GridDragAndDropFilter = (GridItemModel) -> Boolean

/** Context about the event target inside a grid. */
external interface GridEventContext {
    /** The targeted section. */
    var section: GridEventSection?

    /** The item of the targeted row. */
    var item: kotlin.js.JsAny?

    /** The column of the targeted cell. */
    var column: GridColumnElement?

    /** The index of the item. */
    var index: Double?

    /** Whether the item is selected. */
    var selected: Boolean?

    /** Whether the row details are open. */
    var detailsOpened: Boolean?

    /** Whether the item has children. */
    var hasChildren: Boolean?

    /** Whether the item is expanded. */
    var expanded: Boolean?

    /** The tree level of the item. */
    var level: Double?
}

/** Section of a grid targeted by an event: `body`, `details`, `footer` or `header`. */
sealed external interface GridEventSection

/** Wraps a value from JavaScript. Prefer the constants in [GridEventSections]. */
fun GridEventSection(value: String): GridEventSection = unsafeCast(value)

/** All values of [GridEventSection]. */
object GridEventSections {
    inline val body: GridEventSection get() = GridEventSection("body")
    inline val details: GridEventSection get() = GridEventSection("details")
    inline val footer: GridEventSection get() = GridEventSection("footer")
    inline val header: GridEventSection get() = GridEventSection("header")
}
