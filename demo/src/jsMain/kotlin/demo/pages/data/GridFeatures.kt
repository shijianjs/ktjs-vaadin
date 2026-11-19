package demo.pages.data


import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachShadowRoot
import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import web.components.CustomElementReference
import web.dom.TagName
import web.html.HtmlTagName
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import demo.pages.fixtures.Person
import demo.pages.fixtures.people
import demo.tr
import demo.ui.EventLog
import demo.ui.Readout
import demo.ui.jsArray
import demo.ui.listen
import io.github.shijianjs.ktjs.dom.element.inlineStyle
import web.cssom.Display
import web.cssom.FlexWrap
import web.cssom.Gap
import web.cssom.px
import js.objects.unsafeJso
import kotlin.js.JsAny
import kotlin.js.js
import io.github.shijianjs.ktjs.vaadin.badge.createBadge
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.contextmenu.ContextMenuEvents
import io.github.shijianjs.ktjs.vaadin.contextmenu.ContextMenuItemData
import io.github.shijianjs.ktjs.vaadin.contextmenu.createContextMenu
import io.github.shijianjs.ktjs.vaadin.grid.ColumnRenderings
import io.github.shijianjs.ktjs.vaadin.grid.GridColumnElement
import io.github.shijianjs.ktjs.vaadin.grid.GridDropModes
import io.github.shijianjs.ktjs.vaadin.grid.GridElement
import io.github.shijianjs.ktjs.vaadin.grid.GridEventSections
import io.github.shijianjs.ktjs.vaadin.grid.GridEvents
import io.github.shijianjs.ktjs.vaadin.grid.createGridColumn
import io.github.shijianjs.ktjs.vaadin.grid.createGridSelectionColumn
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.vaadin.textarea.createTextArea
import io.github.shijianjs.ktjs.vaadin.textfield.createTextField
import io.github.shijianjs.ktjs.vaadin.tooltip.createTooltip
import web.dom.clickEvent
import web.dom.contextMenuEvent
import web.events.Event
import web.events.addHandler
import web.html.HTMLElement
import web.pointer.PointerEvent

// region Event detail readers

internal fun detailValue(event: Event): JsAny? =
    js("(event) => event.detail == null || event.detail.value === undefined ? null : event.detail.value")(event)

internal fun detailField(event: Event, key: String): JsAny? =
    js("(event, key) => event.detail == null ? null : event.detail[key]")(event, key)

internal fun detailString(event: Event, key: String): String =
    js("(event, key) => event.detail == null || event.detail[key] == null ? '' : String(event.detail[key])")(event, key) as String

internal fun detailFlag(event: Event, key: String): Boolean =
    js("(event, key) => !!(event.detail && event.detail[key])")(event, key) as Boolean

internal fun detailItems(event: Event, key: String): Array<JsAny> =
    js("(event, key) => event.detail && Array.isArray(event.detail[key]) ? event.detail[key] : []")(event, key) as Array<JsAny>

// endregion

/**
 * `<vaadin-demo-person-details>`: six read-only fields in the row details panel, wrapping into rows —
 * the same flex-wrap rule the docs use. The fields are rebuilt from the item on [show], because the row
 * details panel hands its content one item at a time.
 */
internal class PersonDetails : CustomElementAdapter() {
    private val fields = HtmlTagName.div().apply {
        inlineStyle.apply {
            display = Display.flex
            flexWrap = FlexWrap.wrap
        }
        style.setProperty("gap", "8px")
    }

    init {
        attachShadowRoot().append(fields)
    }

    /** Fills the six fields from the grid's item. */
    fun show(item: JsAny?) {
        fields.replaceChildren(
            createTextField { label = tr("邮箱地址", "Email address"); value = itemPathText(item, "email"); readonly = true },
            createTextField { label = tr("电话", "Phone number"); value = itemPathText(item, "address.phone"); readonly = true },
            createTextField { label = tr("街道", "Street address"); value = itemPathText(item, "address.street"); readonly = true },
            createTextField { label = tr("邮编", "ZIP code"); value = itemPathText(item, "address.zip"); readonly = true },
            createTextField { label = tr("城市", "City"); value = itemPathText(item, "address.city"); readonly = true },
            createTextField { label = tr("州", "State"); value = itemPathText(item, "address.state"); readonly = true },
        )
    }

    companion object : CustomElementReference<PersonDetails>(PersonDetails::class.js) {
        val NAME: TagName<PersonDetails> = TagName("vaadin-demo-person-details")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<vaadin-demo-person-details>`. */
internal fun newPersonDetails(item: JsAny?): PersonDetails {
    PersonDetails.define()
    return PersonDetails.NAME().also { it.show(item) }
}

internal fun menuItems(vararg labels: String): Array<ContextMenuItemData> =
    labels.map { unsafeJso<ContextMenuItemData> { text = it } }.toTypedArray()

internal fun dragGrid(items: MutableList<Person>, onDragStart: (Int) -> Unit): GridElement {
    val grid = newGrid("12rem")
    grid.style.setProperty("width", "20rem")
    grid.fill(items)
    grid.rowsDraggable = true
    grid.append(
        createGridColumn {
            header = tr("全名", "Full name")
            renderer = { root, _, model -> root.textContent = "${itemText(model.item, "firstName")} ${itemText(model.item, "lastName")}" }
        },
        createGridColumn { path = "profession" },
    )
    grid.listen(GridEvents.gridDragstart) { event ->
        grid.dropMode = GridDropModes.onGrid
        onDragStart(itemId(detailItems(event, "draggedItems").firstOrNull()))
    }
    grid.listen(GridEvents.gridDragend) { grid.dropMode = null }
    return grid
}

internal fun themedGrid(theme: String): GridElement {
    val grid = newGrid("11rem")
    grid.fill(people(4))
    grid.addTheme(theme)
    grid.append(
        createGridColumn { header = tr("名", "First name"); path = "firstName" },
        createGridColumn { path = "lastName" },
        createGridColumn { path = "email" },
    )
    return grid
}

internal fun itemRating(item: JsAny?): Double =
    (js("(item) => item == null || item.customerRating == null ? 0 : Number(item.customerRating)")(item) as Double)

/**
 * The grid passes `undefined` as the column for details cells, so a plain `column.header` read
 * throws; the docs example guards with `column.header?.startsWith` for the same reason.
 */
internal fun headerStartsWith(column: GridColumnElement, prefix: String): Boolean =
    js("(column, prefix) => !!column && !!column.header && column.header.startsWith(prefix)")(column, prefix) as Boolean

internal fun fixed2(value: Double): String = js("(value) => value.toFixed(2)")(value) as String
