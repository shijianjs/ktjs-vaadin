package vaadin.login

/**
 * The object used to localize the login form and the login overlay.
 *
 * Upstream declares `interface LoginI18n` in `vaadin-login-mixin.d.ts`; the manifest only records
 * the member as a bare `object`, so this file is hand-written and the generator points the typed
 * accessor at it. Every member is optional: the components merge the object into their own bundle.
 */
external interface LoginI18n {
    /** Texts of the two fields and the submit button. */
    var form: LoginI18nForm?

    /** Texts of the failure banner shown when `error` is set. */
    var errorMessage: LoginI18nErrorMessage?

    /** Overrides `title` and `description` of the login overlay. */
    var header: LoginI18nHeader?

    /** Extra information shown below the form; the default bundle has none. */
    var additionalInformation: String?
}

/** Texts rendered inside the login form. */
external interface LoginI18nForm {
    /** Title above the form. */
    var title: String?

    /** Label of the username field. */
    var username: String?

    /** Label of the password field. */
    var password: String?

    /** Text of the submit button. */
    var submit: String?

    /** Text of the forgot-password button. */
    var forgotPassword: String?
}

/** Texts of the failure banner. */
external interface LoginI18nErrorMessage {
    /** Title of the banner. */
    var title: String?

    /** Explanation shown under the title. */
    var message: String?

    /** Validation message of an empty username. */
    var username: String?

    /** Validation message of an empty password. */
    var password: String?
}

/** Header texts of the login overlay. */
external interface LoginI18nHeader {
    /** Name of the application. */
    var title: String?

    /** Description shown under the title. */
    var description: String?
}
