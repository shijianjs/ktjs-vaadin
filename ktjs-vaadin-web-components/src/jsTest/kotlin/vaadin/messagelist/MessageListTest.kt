package vaadin.messagelist

import vaadin.queryAll
import kotlin.js.Promise
import kotlin.js.js
import kotlin.js.unsafeCast
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.detachAll
import vaadin.fixture
import vaadin.nextRender
import vaadin.nextUpdate
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/message-list/test/message-list.test.js` and the public part of
 * `packages/message-list/test/message.test.js`.
 *
 * The scroll and keyboard cases are skipped; the item API, the `message` attributes and the
 * `MessageList.items` array are translated.
 */
class MessageListTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerMessageList()
        registerMessage()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newList(): MessageListElement = attach(createMessageList())

    private fun messages(host: MessageListElement): Array<HTMLElement> =
        queryAll(host, "vaadin-message")

    private fun listFixture(): MessageListElement = attach(
        fixture(
            """
            <vaadin-message-list>
              <vaadin-message user-name="Jane" time="12:00">Hello</vaadin-message>
              <vaadin-message user-name="John" time="12:01">Hi</vaadin-message>
            </vaadin-message-list>
            """.trimIndent(),
        ).unsafeCast<MessageListElement>(),
    )

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerMessageList()
        val list = newList()

        assertEquals("vaadin-message-list", list.tagName.lowercase())
        assertEquals("vaadin-message-list", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun messageIsDefinedInTheCustomElementRegistry() {
        val ctor = registerMessage()
        val message = attach(createMessage())

        assertEquals("vaadin-message", message.tagName.lowercase())
        assertEquals("vaadin-message", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun rendersTheSlottedMessages(): Promise<Unit> {
        val list = listFixture()

        return nextRender(list).then { assertEquals(2, messages(list).size) }
    }

    @Test
    fun reflectsTheMessageAttributes(): Promise<Unit> {
        val list = listFixture()

        return nextRender(list).then {
            val first = messages(list)[0]
            assertEquals("Jane", first.getAttribute("user-name"))
            assertEquals("12:00", first.getAttribute("time"))
        }
    }

    @Test
    fun exposesTheMessageProperties(): Promise<Unit> {
        val message = attach(
            createMessage {
                userName = "Jane"
                time = "12:00"
                userAbbr = "JA"
                userColorIndex = 2.0
            },
        )

        return nextRender(message).then {
            assertEquals("Jane", message.userName)
            assertEquals("12:00", message.time)
            assertEquals("JA", message.userAbbr)
            assertEquals(2.0, message.userColorIndex)
        }
    }

    @Test
    fun supportsMarkdownMessages(): Promise<Unit> {
        val list = listFixture()

        return nextRender(list)
            .then {
                list.setAttribute("markdown", "")
                nextUpdate(list)
            }
            .then { assertTrue(list.hasAttribute("markdown")) }
    }

    @Test
    fun supportsAnnouncingMessages(): Promise<Unit> {
        val list = newList()

        return nextUpdate(list)
            .then {
                list.setAttribute("announce-messages", "")
                nextUpdate(list)
            }
            .then { assertTrue(list.hasAttribute("announce-messages")) }
    }

    // Kotlin-only coverage

    @Test
    fun createMessageListAppliesTheConfigureBlock() {
        val list = attach(createMessageList())
        list.setAttribute("theme", "compact")

        assertEquals("compact", list.getAttribute("theme"))
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals(
            "vaadin-message-list",
            document.createElement(vaadin.VaadinTagName.messageList).tagName.lowercase(),
        )
        assertEquals("vaadin-message", document.createElement(vaadin.VaadinTagName.message).tagName.lowercase())
        assertEquals("user-name", MessageAttributes.userName)
        assertEquals("user-abbr", MessageAttributes.userAbbr)
        assertEquals("user-img", MessageAttributes.userImg)
        assertEquals("user-color-index", MessageAttributes.userColorIndex)
        assertEquals("time", MessageAttributes.time)
        assertEquals("items", MessageListAttributes.items)
        assertEquals("markdown", MessageListAttributes.markdown)
        assertEquals("announce-messages", MessageListAttributes.announceMessages)
    }

    @Test
    fun eventTypesAreExposed() {
        assertNotNull(MessageListEvents.attachmentClick)
        assertNotNull(MessageEvents.attachmentClick)
    }
}
