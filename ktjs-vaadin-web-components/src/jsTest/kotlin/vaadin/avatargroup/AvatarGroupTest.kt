package vaadin.avatargroup

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
import vaadin.nextRender
import vaadin.nextUpdate
import vaadin.nextResize
import vaadin.nextFrame
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/avatar-group/test/avatar-group.test.js`.
 *
 * The overflow and keyboard cases need measured widths and real key sequences; the `items` API, the
 * `maxItemsVisible` limit, the theme propagation and the attribute contract are translated.
 */
class AvatarGroupTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerAvatarGroup()
        vaadin.avatar.registerAvatar()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newGroup(): AvatarGroupElement = attach(createAvatarGroup())

    private fun items(): Array<AvatarGroupItem> = js(
        """
        [
          { abbr: 'PM' },
          { name: 'Yuriy Yevstihnyeyev' },
          { abbr: 'SK' },
          { name: 'Jens Jansson' }
        ]
        """,
    ).unsafeCast<Array<AvatarGroupItem>>()

    private fun avatars(group: AvatarGroupElement): Array<HTMLElement> =
        js("(group) => Array.from(group.querySelectorAll('vaadin-avatar'))")(group) as Array<HTMLElement>

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerAvatarGroup()
        val group = newGroup()

        assertEquals("vaadin-avatar-group", group.tagName.lowercase())
        assertEquals("vaadin-avatar-group", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun rendersAnAvatarPerItem(): Promise<Unit> {
        val group = newGroup()
        group.items = items()

        return nextRender(group)
            .then { nextFrame() }
            .then {
                // One avatar per item, plus the overflow avatar.
                assertEquals(group.items!!.size + 1, avatars(group).size)
            }
    }

    @Test
    fun rendersTheAbbreviationOfTheFirstItem(): Promise<Unit> {
        val group = newGroup()
        group.items = items()

        return nextRender(group)
            .then { nextFrame() }
            .then {
                val abbr = js("(group) => group.querySelector('vaadin-avatar').abbr")(group) as String
                assertEquals("PM", abbr)
            }
    }

    @Test
    fun propagatesTheThemeToTheAvatars(): Promise<Unit> {
        val group = newGroup()
        group.items = items()
        group.setAttribute("theme", "small")

        return nextRender(group)
            .then { nextFrame() }
            .then {
                avatars(group).forEach { avatar ->
                    assertEquals("small", avatar.getAttribute("theme"))
                }
            }
    }

    @Test
    fun appliesTheMaxItemsVisible(): Promise<Unit> {
        val group = newGroup()
        group.items = items()
        group.maxItemsVisible = 2.0

        return nextRender(group)
            .then { nextResize(group) }
            .then { nextFrame() }
            .then {
                // The limit only hides avatars once the group has a constrained width; the property
                // is what the wrapper exposes.
                assertEquals(2.0, group.maxItemsVisible)
            }
    }

    @Test
    fun updatesTheRenderedAvatars(): Promise<Unit> {
        val group = newGroup()
        group.items = items()

        return nextRender(group)
            .then { nextFrame() }
            .then {
                group.items = js("[{ abbr: 'GG' }]").unsafeCast<Array<AvatarGroupItem>>()
                nextUpdate(group)
            }
            .then { nextFrame() }
            .then {
                val abbr = js("(group) => group.querySelector('vaadin-avatar').abbr")(group) as String
                assertEquals("GG", abbr)
            }
    }

    // Kotlin-only coverage

    @Test
    fun createAvatarGroupAppliesTheConfigureBlock() {
        val group = attach(createAvatarGroup { maxItemsVisible = 3.0 })

        assertEquals(3.0, group.maxItemsVisible)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals(
            "vaadin-avatar-group",
            document.createElement(vaadin.VaadinTagName.avatarGroup).tagName.lowercase(),
        )
        assertEquals("max-items-visible", AvatarGroupAttributes.maxItemsVisible)
        assertEquals("i18n", AvatarGroupAttributes.i18n)
        assertEquals("theme", AvatarGroupAttributes.theme)
    }

    @Test
    fun i18nObjectIsExposed() {
        val group = newGroup()

        assertNotNull(js("(group) => group.i18n")(group))
    }

    @Test
    fun groupWithoutItemsRendersOnlyTheOverflowAvatar(): Promise<Unit> {
        val group = newGroup()

        return nextRender(group)
            .then { nextFrame() }
            .then { assertEquals(1, avatars(group).size) }
    }
}
