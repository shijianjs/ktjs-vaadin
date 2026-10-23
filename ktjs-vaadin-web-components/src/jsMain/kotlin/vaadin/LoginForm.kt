package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-login-form>` is a Web Component providing an easy way to require users
 * to log in into an application. Note that component has no shadowRoot.
 *
 * ```html
 * <vaadin-login-form></vaadin-login-form>
 * ```
 *
 * Component has to be accessible from the `document` layer in order to allow password managers to work properly with form values.
 * Using `<vaadin-login-overlay>` allows to always attach the component to the document body.
 *
 * ### Styling
 *
 * The following shadow DOM parts are available for styling:
 *
 * Part name      | Description
 * ---------------|---------------------------------------------------------|
 * `form`         | Container for the entire component's content
 * `form-title`   | Title of the login form
 * `error-message`| Container for error message, contains error-message-title and error-message-description parts. Hidden by default.
 * `error-message-title`       | Container for error message title
 * `error-message-description` | Container for error message description
 * `footer`  | Container additional information text from `i18n` object
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                        |
 * :------------------------------------------|
 * | `--vaadin-login-form-background`         |
 * | `--vaadin-login-form-border-radius`      |
 * | `--vaadin-login-form-error-color`        |
 * | `--vaadin-login-form-error-font-size`    |
 * | `--vaadin-login-form-error-font-weight`  |
 * | `--vaadin-login-form-error-gap`          |
 * | `--vaadin-login-form-error-line-height`  |
 * | `--vaadin-login-form-gap`                |
 * | `--vaadin-login-form-padding`            |
 * | `--vaadin-login-form-title-color`        |
 * | `--vaadin-login-form-title-font-size`    |
 * | `--vaadin-login-form-title-font-weight`  |
 * | `--vaadin-login-form-title-line-height`  |
 * | `--vaadin-login-form-width`              |
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/login)
 */
external class LoginFormElement : HTMLElement {
    /**
     * If set, a synchronous POST call will be fired to the path defined.
     * The `login` event is also dispatched, so `event.preventDefault()` can be called to prevent the POST call.
     */
    var action: String?

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
     * Submits the form.
     */
    fun submit()
}

/**
 * Named exports of `@vaadin/login/vaadin-login-form.js`.
 *
 * Referencing the module registers the `<vaadin-login-form>` custom element.
 */
@JsModule("@vaadin/login/vaadin-login-form.js")
private external object LoginFormModule {
    /** The registered custom element class. */
    val LoginForm: dynamic
}

/**
 * Imports `@vaadin/login/vaadin-login-form.js`, registering the `<vaadin-login-form>` custom element,
 * and returns the element class.
 */
fun registerLoginForm(): dynamic = LoginFormModule.LoginForm

/**
 * Creates a `<vaadin-login-form>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createLoginForm(configure: LoginFormElement.() -> Unit = {}): LoginFormElement {
    registerLoginForm()
    return document.createElement(VaadinTagName.loginForm).apply(configure)
}
