package vaadin.fieldhighlighter

import kotlin.js.Promise
import kotlin.js.js
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.detachAll
import vaadin.nextRender
import vaadin.nextUpdate
import vaadin.textfield.createTextField
import vaadin.textfield.registerTextField
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/field-highlighter/test/field-highlighter.test.js` and the public part of
 * `packages/field-highlighter/test/field-outline.test.js`.
 *
 * The hand-written wrapper adds `fieldHighlighterUser(...)`, `createFieldHighlighterController(...)`
 * and the static `FieldHighlighter` facade; those are covered here because the upstream suite drives
 * the controller through the internal static API only.
 */
class FieldHighlighterTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newField(): HTMLElement {
        registerTextField()
        return attach(createTextField { label = "Field" })
    }

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerFieldHighlighter()
        val element = attach(createFieldHighlighter())

        assertEquals("vaadin-field-highlighter", element.tagName.lowercase())
        assertEquals("vaadin-field-highlighter", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun fieldOutlineIsDefinedInTheCustomElementRegistry() {
        val ctor = registerFieldOutline()
        val element = attach(createFieldOutline())

        assertEquals("vaadin-field-outline", element.tagName.lowercase())
        assertEquals("vaadin-field-outline", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun userTagIsDefinedInTheCustomElementRegistry() {
        val ctor = registerUserTag()
        val element = attach(createUserTag { name = "Jane"; uid = "1"; colorIndex = 2.0 })

        assertEquals("vaadin-user-tag", element.tagName.lowercase())
        assertEquals("vaadin-user-tag", js("(ctor) => ctor.is")(ctor) as String)
        assertEquals("Jane", element.name)
        assertEquals("1", element.uid)
        assertEquals(2.0, element.colorIndex)
    }

    @Test
    fun createsAUserObject(): Unit {
        val user = fieldHighlighterUser(id = 1.0, name = "Jane", colorIndex = 2.0, fieldIndex = 3.0)

        assertEquals(1.0, user.id)
        assertEquals("Jane", user.name)
        assertEquals(2.0, user.colorIndex)
        assertEquals(3.0, user.fieldIndex)
    }

    @Test
    fun createsAControllerForAField(): Promise<Unit> {
        val field = newField()

        return nextRender(field).then {
            val controller = createFieldHighlighterController(field)
            assertNotNull(controller)
            assertEquals(true, js("(controller, field) => controller.host === field")(controller, field) as Boolean)
        }
    }

    @Test
    fun addsAndRemovesUsers(): Promise<Unit> {
        val field = newField()

        return nextRender(field).then {
            // `FieldHighlighter.init` returns the controller that is wired into the field; a
            // controller created directly would not be registered on the host.
            val controller = FieldHighlighter.init(field)
            val user = fieldHighlighterUser(id = 1.0, name = "Jane", colorIndex = 0.0)

            controller.addUser(user)
            assertEquals(1, controller.users.size)

            controller.removeUser(user)
            assertEquals(0, controller.users.size)
        }
    }

    @Test
    fun setsTheWholeUserList(): Promise<Unit> {
        val field = newField()

        return nextRender(field).then {
            val controller = FieldHighlighter.init(field)
            val users = arrayOf(
                fieldHighlighterUser(id = 1.0, name = "Jane", colorIndex = 0.0),
                fieldHighlighterUser(id = 2.0, name = "John", colorIndex = 1.0),
            )

            controller.setUsers(users)
            assertEquals(2, controller.users.size)
        }
    }

    @Test
    fun tracksTheCurrentUser(): Promise<Unit> {
        val field = newField()

        return nextRender(field).then {
            val controller = FieldHighlighter.init(field)
            val user = fieldHighlighterUser(id = 1.0, name = "Jane", colorIndex = 0.0)

            controller.user = user
            assertEquals(1.0, controller.user?.id)
            controller.user = null
            assertEquals(null, controller.user)
        }
    }

    @Test
    fun redrawsWithoutThrowing(): Promise<Unit> {
        val field = newField()

        return nextRender(field).then {
            val controller = FieldHighlighter.init(field)
            controller.redraw()
        }
    }

    @Test
    fun staticApiSharesAControllerPerField(): Promise<Unit> {
        val field = newField()

        return nextRender(field).then {
            val first = FieldHighlighter.init(field)
            val second = FieldHighlighter.init(field)

            assertEquals(true, js("(a, b) => a === b")(first, second) as Boolean)

            val user = fieldHighlighterUser(id = 3.0, name = "Jane", colorIndex = 1.0)
            FieldHighlighter.addUser(field, user)
            assertEquals(1, first.users.size)
            FieldHighlighter.removeUser(field, user)
            assertEquals(0, first.users.size)
        }
    }

    @Test
    fun staticApiSetsUsers(): Promise<Unit> {
        val field = newField()

        return nextRender(field).then {
            val controller = FieldHighlighter.init(field)
            FieldHighlighter.setUsers(
                field,
                arrayOf(fieldHighlighterUser(id = 1.0, name = "Jane", colorIndex = 0.0)),
            )
            assertEquals(1, controller.users.size)
        }
    }

    @Test
    fun tagNamesAreExposed() {
        assertEquals(
            "vaadin-field-highlighter",
            document.createElement(vaadin.VaadinTagName.fieldHighlighter).tagName.lowercase(),
        )
        assertEquals(
            "vaadin-field-outline",
            document.createElement(vaadin.VaadinTagName.fieldOutline).tagName.lowercase(),
        )
        assertEquals("vaadin-user-tag", document.createElement(vaadin.VaadinTagName.userTag).tagName.lowercase())
    }

    @Test
    fun outlineExposesItsUser(): Promise<Unit> {
        val outline = attach(createFieldOutline())

        return nextUpdate(outline).then {
            outline.user = fieldHighlighterUser(id = 1.0, name = "Jane", colorIndex = 0.0)
            assertEquals("Jane", outline.user?.name)
        }
    }

    @Test
    fun aiFieldMarkerIsDefinedInTheCustomElementRegistry() {
        val ctor = registerAiFieldMarker()
        val element = attach(createAiFieldMarker())

        assertEquals("vaadin-ai-field-marker", element.tagName.lowercase())
        assertEquals("vaadin-ai-field-marker", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun userTagsContainerIsDefined(): Promise<Unit> {
        registerUserTags()
        val tags = attach(createUserTags())

        return nextRender(tags).then {
            assertTrue(js("(tags) => tags.tagName.toLowerCase() === 'vaadin-user-tags'")(tags) as Boolean)
        }
    }
}
