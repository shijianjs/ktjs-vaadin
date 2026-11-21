package demo.pages.data

import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachShadowRoot
import web.events.Event
import web.html.HtmlTagName
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import demo.pages.fixtures.Person
import demo.pages.fixtures.people
import demo.ui.jsArray
import demo.ui.listen
import io.github.shijianjs.ktjs.dom.element.inlineStyle
import web.cssom.Display
import web.cssom.FlexWrap
import web.cssom.px
import js.objects.unsafeJso
import kotlin.js.JsAny
import kotlin.js.js
import io.github.shijianjs.ktjs.vaadin.contextmenu.ContextMenuItemData
import io.github.shijianjs.ktjs.vaadin.grid.GridDropModes
import io.github.shijianjs.ktjs.vaadin.grid.GridElement
import io.github.shijianjs.ktjs.vaadin.grid.GridEvents
import io.github.shijianjs.ktjs.vaadin.grid.GridColumnElement
import io.github.shijianjs.ktjs.vaadin.grid.createGridColumn
import io.github.shijianjs.ktjs.vaadin.textfield.TextFieldElement
import io.github.shijianjs.ktjs.vaadin.textfield.createTextField

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
 * the same flex-wrap rule the docs use.
 *
 * The fields are created once and their labels are bound to the page's text table, so a language switch
 * renames them in place; only the values come from the item, through [show].
 */
internal class PersonDetails : CustomElementAdapter(), GridTranslatable {
    private val fields = HtmlTagName.div().apply {
        inlineStyle.apply {
            display = Display.flex
            flexWrap = FlexWrap.wrap
        }
        style.setProperty("gap", "8px")
    }

    private val emailField: TextFieldElement = readOnlyField()
    private val phoneField: TextFieldElement = readOnlyField()
    private val streetField: TextFieldElement = readOnlyField()
    private val zipField: TextFieldElement = readOnlyField()
    private val cityField: TextFieldElement = readOnlyField()
    private val stateField: TextFieldElement = readOnlyField()

    init {
        gridTexts.bind(emailField, { it.fieldEmailAddress }) { emailField.label = it }
        gridTexts.bind(phoneField, { it.fieldPhone }) { phoneField.label = it }
        gridTexts.bind(streetField, { it.fieldStreet }) { streetField.label = it }
        gridTexts.bind(zipField, { it.fieldZip }) { zipField.label = it }
        gridTexts.bind(cityField, { it.fieldCity }) { cityField.label = it }
        gridTexts.bind(stateField, { it.fieldState }) { stateField.label = it }
        fields.append(emailField, phoneField, streetField, zipField, cityField, stateField)
        attachShadowRoot().append(fields)
        gridTexts.apply(Langs.EN)
    }

    /** Fills the six fields from the grid's item. */
    fun show(item: JsAny?) {
        emailField.value = itemPathText(item, "email")
        phoneField.value = itemPathText(item, "address.phone")
        streetField.value = itemPathText(item, "address.street")
        zipField.value = itemPathText(item, "address.zip")
        cityField.value = itemPathText(item, "address.city")
        stateField.value = itemPathText(item, "address.state")
    }

    override fun applyTranslation(lang: Lang) {
        gridTexts.apply(lang)
    }

    companion object : PersonDetailsDefaultReference() {
        /** A read-only field the details panel fills from the item. */
        private fun readOnlyField(): TextFieldElement = createTextField { readonly = true }
    }
}


internal fun menuItems(vararg labels: String): Array<ContextMenuItemData> =
    labels.map { unsafeJso<ContextMenuItemData> { text = it } }.toTypedArray()

internal fun dragGrid(items: MutableList<Person>, onDragStart: (Int) -> Unit): GridElement {
    val grid = newGrid("12rem")
    grid.style.setProperty("width", "20rem")
    grid.fill(items)
    grid.rowsDraggable = true
    val nameColumn = createGridColumn {
        renderer = { root, _, model -> root.textContent = "${itemText(model.item, "firstName")} ${itemText(model.item, "lastName")}" }
    }
    gridTexts.bind(nameColumn, { it.columnFullName }) { nameColumn.header = it }
    grid.append(nameColumn, createGridColumn { path = "profession" })
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
    val nameColumn = createGridColumn {
        path = "firstName"
    }
    gridTexts.bind(nameColumn, { it.columnFirstShort }) { nameColumn.header = it }
    grid.append(nameColumn, createGridColumn { path = "lastName" }, createGridColumn { path = "email" })
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
