package vaadin.componentbase

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
import vaadin.avatar.AvatarElement
import vaadin.avatar.createAvatar
import vaadin.avatar.registerAvatar
import vaadin.button.ButtonElement
import vaadin.button.createButton
import vaadin.button.registerButton
import vaadin.detachAll
import vaadin.details.DetailsSummaryElement
import vaadin.details.createDetailsSummary
import vaadin.details.registerDetailsSummary
import vaadin.nextRender
import vaadin.nextUpdate
import web.html.HTMLElement

/**
 * Translated from `packages/component-base/test suites`.
 *
 * The mixins are structural, so the upstream suite exercises them through small components. Here
 * they are exercised through the wrapped elements that implement them: `DirMixin` through
 * `<vaadin-details-summary>`, `I18nMixin` through `<vaadin-avatar>` and `ElementMixin` through
 * `<vaadin-button>`.
 */
class ComponentBaseTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerButton()
        registerAvatar()
        registerDetailsSummary()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    // DirMixin

    @Test
    fun dirMixinReflectsTheDirection(): Promise<Unit> {
        val summary: DetailsSummaryElement = attach(createDetailsSummary())

        return nextRender(summary).then {
            summary.setAttribute("dir", "rtl")
            assertEquals("rtl", summary.getAttribute("dir"))
        }
    }

    @Test
    fun dirMixinKeepsTheAttributeOnTheHost(): Promise<Unit> {
        val summary: DetailsSummaryElement = attach(createDetailsSummary())

        return nextRender(summary).then {
            val observed = js("(element) => element.getAttribute('dir')")(summary)
            assertEquals(null, observed)
        }
    }

    // I18nMixin

    @Test
    fun i18nMixinExposesTheBundle(): Promise<Unit> {
        val avatar: AvatarElement = attach(createAvatar())

        return nextRender(avatar).then {
            assertNotNull(js("(avatar) => avatar.i18n")(avatar))
        }
    }

    @Test
    fun i18nMixinAcceptsACustomBundle(): Promise<Unit> {
        val avatar: AvatarElement = attach(createAvatar())

        return nextRender(avatar).then {
            avatar.i18n = js("({ anonymous: 'Anonyme' })")
            assertEquals("Anonyme", js("(avatar) => avatar.i18n.anonymous")(avatar) as String)
        }
    }

    // ElementMixin

    @Test
    fun elementMixinProvidesTheUpdateCycle(): Promise<Unit> {
        val button: ButtonElement = attach(createButton())

        return nextRender(button).then {
            // `updateComplete` comes from the Lit element that `ElementMixin` is applied to.
            assertEquals(true, js("(button) => button.updateComplete instanceof Promise")(button) as Boolean)
        }
    }

    @Test
    fun elementMixinExposesTheVersion(): Promise<Unit> {
        val button: ButtonElement = attach(createButton())

        return nextRender(button).then {
            assertEquals(true, js("(button) => typeof button.constructor.version === 'string'")(button) as Boolean)
        }
    }

    // SlotStylesMixin

    @Test
    fun slotStylesMixinIsImplementedByTheCheckbox(): Promise<Unit> {
        vaadin.checkbox.registerCheckbox()
        val checkbox = attach(vaadin.checkbox.createCheckbox())

        return nextRender(checkbox).then {
            assertTrue(js("(checkbox) => Array.isArray(checkbox.slotStyles)")(checkbox) as Boolean)
        }
    }

    // ResizeMixin

    @Test
    fun resizeMixinNotifiesOnResize(): Promise<Unit> {
        vaadin.avatargroup.registerAvatarGroup()
        val group = attach(vaadin.avatargroup.createAvatarGroup())

        return nextRender(group).then {
            // The mixin installs a ResizeObserver; a size change must not throw.
            group.style.setProperty("width", "100px")
            nextUpdate(group)
        }
    }

    // OverlayClassMixin

    @Test
    fun overlayClassMixinIsImplementedByTheOverlay(): Promise<Unit> {
        vaadin.overlay.registerOverlay()
        val overlay = attach(vaadin.overlay.createOverlay())

        return nextRender(overlay).then {
            // `overlayClass` is contributed by `OverlayClassMixin` and forwarded to the overlay part.
            overlay.setAttribute("overlay-class", "custom-overlay")
            assertEquals("custom-overlay", overlay.getAttribute("overlay-class"))
        }
    }

    // PolylitMixin

    @Test
    fun polylitMixinRendersTheShadowRoot(): Promise<Unit> {
        val button: ButtonElement = attach(createButton())

        return nextRender(button).then {
            assertNotNull(js("(button) => button.shadowRoot")(button).unsafeCast<HTMLElement?>())
        }
    }
}
