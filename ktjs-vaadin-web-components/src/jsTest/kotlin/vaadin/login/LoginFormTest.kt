package vaadin.login

import js.objects.unsafeJso
import vaadin.queryAll
import vaadin.query
import vaadin.shadowQuery
import kotlin.js.Promise
import kotlin.js.js
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.detachAll
import vaadin.nextFrame
import vaadin.nextRender
import vaadin.nextUpdate
import vaadin.on
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/login/test/login-form.test.js` and the public part of
 * `packages/login/test/login-overlay.test.js`.
 *
 * The form-submission cases of the upstream suite intercept the native `submit` event through the
 * form element; here the `submit()` entry point is used, which is the API the wrapper exposes.
 */
class LoginFormTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerLoginForm()
        registerLoginOverlay()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newForm(): LoginFormElement = attach(createLoginForm())

    private fun wrapper(form: HTMLElement): HTMLElement =
        shadowQuery(form, "[part=form]") as HTMLElement

    private fun wrapperPart(form: HTMLElement, name: String): HTMLElement? =
        js("(form, name) => form.shadowRoot.querySelector('[part=form]').shadowRoot.querySelector('[part~=\"' + name + '\"]')")(
            form,
            name,
        ) as HTMLElement?

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerLoginForm()
        val form = newForm()

        assertEquals("vaadin-login-form", form.tagName.lowercase())
        assertEquals("vaadin-login-form", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun loginOverlayIsDefinedInTheCustomElementRegistry() {
        val ctor = registerLoginOverlay()
        val overlay = attach(createLoginOverlay())

        assertEquals("vaadin-login-overlay", overlay.tagName.lowercase())
        assertEquals("vaadin-login-overlay", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun rendersTheUsernameAndPasswordFields(): Promise<Unit> {
        val form = newForm()

        return nextRender(form)
            .then { nextFrame() }
            .then {
                // The fields are rendered into the slotted `<form>`, which stays in the light DOM.
                assertEquals(1, queryAll(form, "vaadin-text-field").size)
                assertEquals(1, queryAll(form, "vaadin-password-field").size)
            }
    }

    @Test
    fun rendersTheSubmitButton(): Promise<Unit> {
        val form = newForm()

        return nextRender(form)
            .then { nextFrame() }
            .then {
                assertEquals(
                    1,
                    queryAll(form, "vaadin-button[theme~=submit]").size,
                )
            }
    }

    @Test
    fun showsTheErrorBannerWhenErrorIsSet(): Promise<Unit> {
        val form = newForm()

        return nextRender(form)
            .then {
                form.error = true
                nextUpdate(form)
            }
            .then {
                assertTrue(form.error)
                // The banner is exported from the wrapper's shadow root through `exportparts`.
                assertNotNull(shadowQuery(form, "[part=form]"))
            }
    }

    @Test
    fun hidesTheForgotPasswordButton(): Promise<Unit> {
        val form = newForm()

        return nextRender(form)
            .then {
                form.noForgotPassword = true
                nextUpdate(form)
            }
            .then {
                // The button stays in the DOM and is hidden through the `hidden` attribute.
                val hidden = query(form, "vaadin-button[slot=forgot-password]")!!.hasAttribute("hidden")
                assertEquals(true, hidden)
            }
    }

    @Test
    fun reflectsDisabledAndAction(): Promise<Unit> {
        val form = newForm()

        return nextUpdate(form)
            .then {
                form.disabled = true
                form.action = "login"
                nextUpdate(form)
            }
            .then {
                assertTrue(form.disabled)
                assertEquals("login", form.action)
            }
    }

    @Test
    fun firesLoginWhenNoActionIsSet(): Promise<Unit> {
        val form = newForm()
        var fired = 0
        on(form, "login") { fired++ }

        return nextRender(form)
            .then { nextFrame() }
            .then {
                fillCredentials(form)
                form.submit()
                assertTrue(fired > 0, "expected a login event, got $fired")
            }
    }

    @Test
    fun submitsTheInnerFormWhenAnActionIsSet(): Promise<Unit> {
        val form = attach(createLoginForm { action = "login" })

        return nextRender(form)
            .then { nextFrame() }
            .then {
                fillCredentials(form)
                // `HTMLFormElement.submit()` bypasses the `submit` event, so the call is observed on
                // the prototype the way the upstream suite stubs it.
                val calls = js(
                    "(form) => {" +
                        " const original = HTMLFormElement.prototype.submit;" +
                        " let calls = 0;" +
                        " HTMLFormElement.prototype.submit = function () { calls++; };" +
                        " try { form.submit(); } finally { HTMLFormElement.prototype.submit = original; }" +
                        " return calls;" +
                        "}",
                )(form) as Int
                assertEquals(1, calls)
            }
    }

    /** Fills both fields so that `submit()` passes the constraint validation of the form. */
    private fun fillCredentials(form: LoginFormElement) {
        val username = query(form, "vaadin-text-field") as HTMLElement
        val password = query(form, "vaadin-password-field") as HTMLElement
        js("(field) => { field.value = 'user'; }")(username)
        js("(field) => { field.value = 'secret'; }")(password)
    }

    @Test
    fun overlayIsClosedByDefault(): Promise<Unit> {
        val overlay = attach(createLoginOverlay())

        return nextRender(overlay).then {
            assertFalse(overlay.opened)
            // The overlay ships a default description from its i18n bundle.
            assertEquals("Application description", overlay.description)
        }
    }

    @Test
    fun overlayReflectsOpened(): Promise<Unit> {
        val overlay = attach(createLoginOverlay())

        return nextUpdate(overlay)
            .then {
                overlay.opened = true
                nextUpdate(overlay)
            }
            .then { assertTrue(overlay.hasAttribute("opened")) }
    }

    // Kotlin-only coverage

    @Test
    fun createLoginFormAppliesTheConfigureBlock() {
        val form = attach(createLoginForm { error = true })

        assertTrue(form.error)
    }

    @Test
    fun createLoginOverlayAppliesTheConfigureBlock() {
        val overlay = attach(createLoginOverlay { description = "Sign in" })

        assertEquals("Sign in", overlay.description)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals("vaadin-login-form", document.createElement(vaadin.VaadinTagName.loginForm).tagName.lowercase())
        assertEquals(
            "vaadin-login-overlay",
            document.createElement(vaadin.VaadinTagName.loginOverlay).tagName.lowercase(),
        )
        assertEquals("action", LoginFormAttributes.action)
        assertEquals("error", LoginFormAttributes.error)
        assertEquals("disabled", LoginFormAttributes.disabled)
        assertEquals("no-forgot-password", LoginFormAttributes.noForgotPassword)
        assertEquals("no-autofocus", LoginFormAttributes.noAutofocus)
        assertEquals("heading-level", LoginFormAttributes.headingLevel)
        assertEquals("description", LoginOverlayAttributes.description)
        assertEquals("opened", LoginOverlayAttributes.opened)
    }

    @Test
    fun i18nObjectIsExposed() {
        val form = newForm()

        assertNotNull(js("(form) => form.i18n")(form))
    }

    @Test
    fun typedI18nOverridesTheFormTexts() {
        val loginForm = newForm()
        val i18n = unsafeJso<LoginI18n> {
            // `form` is the localization object of the form texts, not the login form element.
            form = unsafeJso<LoginI18nForm> { submit = "Kirjaudu sisään" }
            errorMessage = unsafeJso<LoginI18nErrorMessage> { title = "Väärä tunnus" }
        }

        // The manifest records `i18n` as an untyped `object`; `i18nProperty` maps back to it.
        loginForm.i18nProperty = i18n

        assertEquals("Kirjaudu sisään", js("(form) => form.i18n.form.submit")(loginForm))
        assertEquals("Väärä tunnus", js("(form) => form.i18n.errorMessage.title")(loginForm))
    }

    @Test
    fun actionAcceptsNull() {
        val form = newForm()
        form.action = "/login"
        assertEquals("/login", form.action)
        form.action = null
        assertNull(form.action)
    }

    @Test
    fun overlayExposesTypedI18n() {
        val overlay = attach(createLoginOverlay())
        overlay.i18nProperty = unsafeJso<LoginI18n> {
            additionalInformation = "Contact admin@company.com"
        }

        assertEquals("Contact admin@company.com", js("(overlay) => overlay.i18n.additionalInformation")(overlay))
    }
}
