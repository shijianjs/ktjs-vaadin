package vaadin

import kotlin.js.js
import kotlin.js.unsafeCast
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import web.dom.document
import web.components.HTMLTemplateElement
import web.html.HTMLElement
import vaadin.a11ybase.DisabledMixin
import vaadin.aura.Aura
import vaadin.button.createButton
import vaadin.checkbox.createCheckbox
import vaadin.datepicker.createDatePicker
import vaadin.dialog.createDialog
import vaadin.fieldbase.InputFieldMixin
import vaadin.fieldhighlighter.AiFieldMarkerConfidences
import vaadin.fieldhighlighter.FieldHighlighter
import vaadin.fieldhighlighter.createAiFieldMarker
import vaadin.fieldhighlighter.fieldHighlighterUser
import vaadin.grid.Grid
import vaadin.grid.GridMultiSortPriorities
import vaadin.grid.createGrid
import vaadin.grid.createGridColumn
import vaadin.icon.Iconset
import vaadin.icon.createIcon
import vaadin.icon.ensureSvgLiteral
import vaadin.icon.isValidSvg
import vaadin.icon.registerIconset
import vaadin.icon.unsafeSvgLiteral
import vaadin.lumostyles.LumoThemes
import vaadin.notification.Notification
import vaadin.numberfield.createNumberField
import vaadin.overlay.OverlayMixin
import vaadin.overlay.createOverlay
import vaadin.popover.Popover
import vaadin.progressbar.createProgressBar
import vaadin.select.createSelect
import vaadin.tabs.createTabs
import vaadin.textfield.TextFieldMixin
import vaadin.textfield.createTextField
import vaadin.themablemixin.registerStyles
import vaadin.themablemixin.unsafeCSS
import vaadin.tooltip.Tooltip

/**
 * Exercises the wrapper API against real component behavior: property round-trips, event
 * listeners, DOM composition and the static entry points.
 */
class VaadinBehaviorTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T {
        document.body!!.appendChild(element)
        attached += element
        return element
    }

    @AfterTest
    fun detachAll() {
        attached.forEach { it.remove() }
        attached.clear()
    }

    @Test
    fun textFieldRoundTripsProperties() {
        val field = createTextField {
            label = "Name"
            placeholder = "Type here"
            required = true
            value = "Ada"
        }
        attach(field)

        assertEquals("Name", field.label)
        assertEquals("Type here", field.placeholder)
        assertTrue(field.required)
        assertEquals("Ada", field.value)
        assertTrue(field.checkValidity())

        field.value = ""
        assertFalse(field.checkValidity())

        field.value = "Ada"
        field.clear()
        assertEquals("", field.value)
    }

    @Test
    fun checkboxRoundTripsChecked() {
        val checkbox = createCheckbox {
            label = "Remember me"
            checked = true
        }
        attach(checkbox)

        assertTrue(checkbox.checked)
        assertEquals("on", checkbox.value)

        checkbox.checked = false
        assertFalse(checkbox.checked)
    }

    @Test
    fun buttonDispatchesClick() {
        val button = attach(createButton { disabled = false })
        var clicks = 0
        js("(button, handler) => button.addEventListener('click', handler)")(button, { clicks++ })

        button.click()

        assertEquals(1, clicks)
    }

    @Test
    fun composedElementsKeepTheirChildren() {
        val grid = attach(createGrid())
        val column = createGridColumn()
        grid.appendChild(column)

        assertEquals(1, grid.childElementCount)
        assertTrue(grid.firstElementChild === column)
    }

    @Test
    fun gridAcceptsItems() {
        val grid = attach(createGrid())
        grid.items = js("([{ name: 'a' }, { name: 'b' }])")

        assertEquals(2, js("(grid) => grid.items.length")(grid) as Int)
    }

    @Test
    fun dialogTogglesOpened() {
        val dialog = attach(createDialog())
        dialog.opened = true

        assertTrue(dialog.opened)

        dialog.opened = false
        assertFalse(dialog.opened)
    }

    @Test
    fun notificationShowCreatesAnOpenElement() {
        val notification = Notification.show("Saved")

        assertTrue(notification.opened)
        assertTrue(document.body!!.contains(notification))

        notification.opened = false
        notification.remove()
    }

    @Test
    fun tabsRoundTripsSelectedIndex() {
        val tabs = attach(createTabs())
        tabs.selected = 1.0

        assertEquals(1.0, tabs.selected)
    }

    @Test
    fun selectRoundTripsValue() {
        val select = attach(createSelect())
        select.value = "b"

        assertEquals("b", select.value)
    }

    @Test
    fun dateAndNumberFieldsRoundTripValues() {
        val datePicker = attach(createDatePicker())
        datePicker.value = "2026-10-24"
        assertEquals("2026-10-24", datePicker.value)

        val numberField = attach(createNumberField())
        numberField.value = "42"
        assertEquals("42", numberField.value)
    }

    @Test
    fun progressBarRoundTripsValue() {
        val progressBar = attach(createProgressBar())
        progressBar.value = 0.75

        assertEquals(0.75, progressBar.value)
    }

    @Test
    fun staticConfigurationSettersAcceptValues() {
        Grid.setDefaultMultiSortPriority(GridMultiSortPriorities.append)
        Popover.setDefaultHideDelay(250.0)
        Tooltip.setDefaultHoverDelay(250.0)
    }

    @Test
    fun elementInterfacesExposeMixinTyping() {
        val field = createTextField { label = "Name" }

        val disabled: DisabledMixin = field
        disabled.disabled = true
        assertTrue(field.disabled)

        val text: TextFieldMixin = field
        text.maxlength = 20.0
        assertEquals(20.0, field.maxlength)
    }

    @Test
    fun mixinMembersWithDomNameClashesMapToTheSameJsProperty() {
        val field = createTextField()
        val input: InputFieldMixin = field
        input.autocapitalizeProperty = "characters"
        assertEquals("characters", js("(element) => element.autocapitalize")(field) as String)

        val overlay = attach(createOverlay())
        val overlayMixin: OverlayMixin = overlay
        overlayMixin.hiddenProperty = true
        assertTrue(js("(element) => element.hidden")(overlay) as Boolean)
    }

    @Test
    fun fieldHighlighterTracksUsersOnAField() {
        val field = attach(createTextField())
        val ada = fieldHighlighterUser(1.0, "Ada", 2.0)
        val grace = fieldHighlighterUser(2.0, "Grace", 1.0)

        val controller = FieldHighlighter.init(field)
        controller.addUser(ada)
        controller.addUser(grace)

        assertEquals(2, controller.users.size)
        assertEquals("Grace", controller.user?.name)

        FieldHighlighter.removeUser(field, grace)

        assertEquals(1, controller.users.size)
        assertEquals("Ada", controller.user?.name)
    }

    @Test
    fun aiFieldMarkerRoundTripsItsState() {
        val marker = attach(createAiFieldMarker())
        marker.working = true
        marker.confidence = AiFieldMarkerConfidences.high

        assertTrue(marker.working)
        assertEquals("high", marker.confidence.unsafeCast<String>())
    }

    @Test
    fun iconSvgLiteralsAreTypedAndValidated() {
        val literal = unsafeSvgLiteral("""<path d="M12 2 2 22h20z"></path>""")
        assertTrue(isValidSvg(literal))
        assertFalse(isValidSvg("plain text"))
        assertTrue(isValidSvg(ensureSvgLiteral(literal)))

        val icon = createIcon()
        icon.svg = literal
    }

    @Test
    fun iconsetResolvesAnIconAsAnSvgLiteral() {
        registerIconset()
        val template = document.createElement("template").unsafeCast<HTMLTemplateElement>()
        val svg = document.createElementNS("http://www.w3.org/2000/svg", "svg")
        svg.setAttribute("id", "ktjs:triangle")
        svg.setAttribute("viewBox", "0 0 24 24")
        template.content.appendChild(svg)

        Iconset.register("ktjs", 24.0, template)

        val icon = Iconset.getIconSvg("ktjs:triangle", null)
        assertTrue(isValidSvg(icon.svg))
        assertEquals(24.0, icon.size)
        assertEquals("0 0 24 24", icon.viewBox)
    }

    @Test
    fun themesExposeStyleEntryPoints() {
        assertEquals("@vaadin/vaadin-lumo-styles/lumo.css", LumoThemes.CSS)
        assertEquals("@vaadin/aura/aura.css", Aura.CSS)

        val styles = unsafeCSS(":host { color: red; }")
        assertTrue(styles.cssText.contains("color: red"))

        registerStyles("vaadin-button", styles)
    }
}
