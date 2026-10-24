package vaadin.avatar

import kotlin.js.Promise
import kotlin.js.js
import kotlin.js.unsafeCast
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.detachAll
import vaadin.fixture
import vaadin.nextUpdate
import vaadin.shadowQuery
import vaadin.shadowQueryNode
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/avatar/test/avatar.test.js`.
 *
 * The icon and abbreviation visibility cases read the `[part="icon"]` / `[part="abbr"]` elements of
 * the shadow root, exactly like upstream. The tooltip and keyboard cases are not translated: they
 * depend on `@vaadin/tooltip` internals and on real focus sequences.
 */
class AvatarTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        // Markup fixtures need the definition; `createAvatar` registers it as a side effect.
        registerAvatar()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newAvatar(): AvatarElement = attach(createAvatar())

    private fun icon(avatar: AvatarElement): HTMLElement = assertNotNull(shadowQuery(avatar, "[part=\"icon\"]"))

    private fun abbrPart(avatar: AvatarElement): web.dom.Element = assertNotNull(shadowQueryNode(avatar, "[part=\"abbr\"]"))

    private val validImageSrc =
        "data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz48c3ZnIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgd2lkdGg9IjUxMiIgaGVpZ2h0PSI1MTIiPjxjaXJjbGUgY3g9IjI1NiIgY3k9IjI1NiIgcj0iMjU2Ii8+PC9zdmc+"

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerAvatar()
        val avatar = newAvatar()

        assertEquals("vaadin-avatar", avatar.tagName.lowercase())
        assertEquals("vaadin-avatar", js("(ctor) => ctor.is")(ctor) as String)
    }

    // img

    @Test
    fun imgIsUndefinedByDefault() {
        val avatar = newAvatar()

        assertEquals(null, js("(element) => element.img")(avatar))
    }

    @Test
    fun reflectsImgToTheAttribute(): Promise<Unit> {
        val avatar = newAvatar()

        return nextUpdate(avatar)
            .then {
                avatar.img = validImageSrc
                nextUpdate(avatar)
            }
            .then { assertEquals(validImageSrc, avatar.getAttribute("img")) }
    }

    @Test
    fun hidesTheIconWhenImgIsSet(): Promise<Unit> {
        val avatar = newAvatar()

        return nextUpdate(avatar)
            .then {
                assertFalse(icon(avatar).hasAttribute("hidden"))
                avatar.img = validImageSrc
                nextUpdate(avatar)
            }
            .then { assertTrue(icon(avatar).hasAttribute("hidden")) }
    }

    @Test
    fun hidesTheAbbreviationWhenImgIsSet(): Promise<Unit> {
        val avatar = newAvatar()

        return nextUpdate(avatar)
            .then {
                avatar.abbr = "YY"
                nextUpdate(avatar)
            }
            .then {
                assertFalse(abbrPart(avatar).hasAttribute("hidden"))
                avatar.img = validImageSrc
                nextUpdate(avatar)
            }
            .then { assertTrue(abbrPart(avatar).hasAttribute("hidden")) }
    }

    // abbr

    @Test
    fun reflectsAbbrToTheAttribute(): Promise<Unit> {
        val avatar = newAvatar()

        return nextUpdate(avatar)
            .then {
                avatar.abbr = "YY"
                nextUpdate(avatar)
            }
            .then { assertEquals("YY", avatar.getAttribute("abbr")) }
    }

    @Test
    fun showsTheAbbreviationWhenAbbrIsSet(): Promise<Unit> {
        val avatar = newAvatar()

        return nextUpdate(avatar)
            .then {
                assertTrue(abbrPart(avatar).hasAttribute("hidden"))
                avatar.abbr = "YY"
                nextUpdate(avatar)
            }
            .then { assertFalse(abbrPart(avatar).hasAttribute("hidden")) }
    }

    @Test
    fun generatesTheAbbreviationFromTheName(): Promise<Unit> {
        val avatar = newAvatar()

        return nextUpdate(avatar)
            .then {
                avatar.name = "Foo Bar"
                nextUpdate(avatar)
            }
            .then { assertEquals("FB", avatar.abbr) }
    }

    @Test
    fun keepsAnExplicitAbbreviationWhenTheNameChanges(): Promise<Unit> {
        val avatar = newAvatar()

        return nextUpdate(avatar)
            .then {
                avatar.abbr = "BB"
                avatar.name = "Foo Bar"
                nextUpdate(avatar)
            }
            .then { assertEquals("BB", avatar.abbr) }
    }

    @Test
    fun regeneratesTheAbbreviationWhenItIsUnset(): Promise<Unit> {
        val avatar = newAvatar()

        return nextUpdate(avatar)
            .then {
                avatar.abbr = "BB"
                avatar.name = "Foo Bar"
                nextUpdate(avatar)
            }
            .then {
                avatar.abbr = ""
                nextUpdate(avatar)
            }
            .then { assertEquals("FB", avatar.abbr) }
    }

    // name and color index

    @Test
    fun reflectsNameToTheAttribute(): Promise<Unit> {
        val avatar = newAvatar()

        return nextUpdate(avatar)
            .then {
                avatar.name = "Foo Bar"
                nextUpdate(avatar)
            }
            .then { assertEquals("Foo Bar", avatar.getAttribute("name")) }
    }

    @Test
    fun colorIndexRoundTripsThroughTheProperty(): Promise<Unit> {
        val avatar = newAvatar()

        return nextUpdate(avatar)
            .then {
                avatar.colorIndex = 2.0
                nextUpdate(avatar)
            }
            .then { assertEquals(2.0, avatar.colorIndex) }
    }

    // Kotlin-only coverage

    @Test
    fun createAvatarAppliesTheConfigureBlock() {
        val avatar = attach(createAvatar { name = "Jane Doe" })

        assertEquals("Jane Doe", avatar.name)
        assertEquals("JD", avatar.abbr)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals("vaadin-avatar", document.createElement(vaadin.VaadinTagName.avatar).tagName.lowercase())
        assertEquals("abbr", AvatarAttributes.abbr)
        assertEquals("img", AvatarAttributes.img)
        assertEquals("name", AvatarAttributes.name)
        assertEquals("color-index", AvatarAttributes.colorIndex)
        assertEquals("with-tooltip", AvatarAttributes.withTooltip)
        assertEquals("i18n", AvatarAttributes.i18n)
        assertEquals("theme", AvatarAttributes.theme)
    }

    @Test
    fun withTooltipRoundTripsThroughTheProperty() {
        val avatar = attach(createAvatar())

        avatar.withTooltip = true

        assertTrue(avatar.withTooltip)
    }

    @Test
    fun i18nObjectIsExposed() {
        val avatar = attach(createAvatar())

        assertNotNull(js("(element) => element.i18n")(avatar))
    }

    @Test
    fun markupFixtureIsUpgraded() {
        val avatar = attach(vaadin.fixture("<vaadin-avatar name=\"Foo Bar\"></vaadin-avatar>").unsafeCast<AvatarElement>())

        assertEquals("FB", avatar.abbr)
    }
}
