package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-login-overlay>` is a web component which renders a login form in an overlay and
 * provides an additional `brand` part for application title and description.
 *
 * ```html
 * <vaadin-login-overlay opened></vaadin-login-overlay>
 * ```
 *
 * ### Styling
 *
 * The following shadow DOM parts are available for styling:
 *
 * Part name                    | Description
 * -----------------------------|--------------------------------
 * `backdrop`                   | Backdrop of the overlay
 * `overlay`                    | The overlay container element
 * `content`                    | The overlay content element
 * `card`                       | Container for the brand and form wrapper
 * `brand`                      | Container for application title and description
 * `description`                | The application description
 * `form-wrapper`               | The login form wrapper element
 * `form`                       | The login form element
 * `form-title`                 | Title of the login form
 * `error-message`              | Container for error message
 * `error-message-title`        | Container for error message title
 * `error-message-description`  | Container for error message description
 * `footer`                     | Container for the footer element
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                                |
 * :--------------------------------------------------|
 * | `--vaadin-login-overlay-background`              |
 * | `--vaadin-login-overlay-border-color`            |
 * | `--vaadin-login-overlay-border-radius`           |
 * | `--vaadin-login-overlay-border-width`            |
 * | `--vaadin-login-overlay-brand-background`        |
 * | `--vaadin-login-overlay-brand-padding`           |
 * | `--vaadin-login-overlay-description-color`       |
 * | `--vaadin-login-overlay-description-font-size`   |
 * | `--vaadin-login-overlay-description-font-weight` |
 * | `--vaadin-login-overlay-description-line-height` |
 * | `--vaadin-login-overlay-shadow`                  |
 * | `--vaadin-login-overlay-text-color`              |
 * | `--vaadin-login-overlay-title-color`             |
 * | `--vaadin-login-overlay-title-font-size`         |
 * | `--vaadin-login-overlay-title-font-weight`       |
 * | `--vaadin-login-overlay-title-line-height`       |
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/login)
 */
external class LoginOverlayElement : HTMLElement {
    /**
     * If set, a synchronous POST call will be fired to the path defined.
     * The `login` event is also dispatched, so `event.preventDefault()` can be called to prevent the POST call.
     */
    var action: String?

    /**
     * Defines the application description
     */
    var description: String?

    /**
     * If set, disable the "Log in" button and prevent user from submitting login form.
     * It is re-enabled automatically, when error is set to true, allowing form resubmission
     * after user makes changes.
     */
    var disabled: Boolean

    /**
     * If set, the error message is shown. The message is hidden by default.
     * When set, it changes the disabled state of the submit button.
     */
    var error: Boolean

    /**
     * Sets the root heading level (`aria-level`) for the heading hierarchy. Default value: 1.
     * Child headings automatically increment from this base level i.e. standalone login form
     * renders its title as `<h1>`, whereas the form in the overlay uses `<h2>`, as the `<h1>`
     * element is used by the overlay's own title.
     */
    var headingLevel: Double?

    /**
     * The object used to localize this component. To change the default
     * localization, replace this with an object that provides all properties, or
     * just the individual properties you want to change.
     *
     * The object has the following JSON structure (by default it doesn't include `additionalInformation`
     * and `header` sections, `header` can be added to override `title` and `description` properties
     * in `vaadin-login-overlay`):
     *
     * ```js
     * {
     *   header: {
     *     title: 'App name',
     *     description: 'Inspiring application description'
     *   },
     *   form: {
     *     title: 'Log in',
     *     username: 'Username',
     *     password: 'Password',
     *     submit: 'Log in',
     *     forgotPassword: 'Forgot password'
     *   },
     *   errorMessage: {
     *     title: 'Incorrect username or password',
     *     message: 'Check that you have entered the correct username and password and try again.',
     *     username: 'Username is required',
     *     password: 'Password is required'
     *   },
     *   additionalInformation: 'In case you need to provide some additional info for the user.'
     * }
     * ```
     */
    var i18n: dynamic

    /**
     * If set, the user name field automatically receives focus when the component is attached to the document.
     */
    var noAutofocus: Boolean

    /**
     * Whether to hide the forgot password button. The button is visible by default.
     */
    var noForgotPassword: Boolean

    /**
     * True if the overlay is currently displayed.
     */
    var opened: Boolean

    /**
     * Submits the form.
     */
    fun submit()
}

/**
 * Named exports of `@vaadin/login/vaadin-login-overlay.js`.
 *
 * Referencing the module registers the `<vaadin-login-overlay>` custom element.
 */
@JsModule("@vaadin/login/vaadin-login-overlay.js")
private external object LoginOverlayModule {
    /** The registered custom element class. */
    val LoginOverlay: dynamic
}

/**
 * Imports `@vaadin/login/vaadin-login-overlay.js`, registering the `<vaadin-login-overlay>` custom element,
 * and returns the element class.
 */
fun registerLoginOverlay(): dynamic = LoginOverlayModule.LoginOverlay

/**
 * Creates a `<vaadin-login-overlay>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createLoginOverlay(configure: LoginOverlayElement.() -> Unit = {}): LoginOverlayElement {
    registerLoginOverlay()
    return document.createElement(VaadinTagName.loginOverlay).apply(configure)
}
