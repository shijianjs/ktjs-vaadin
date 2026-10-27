package vaadin.notification

import web.html.HTMLElement

/** Renders the content of a notification. */
typealias NotificationRenderer = (HTMLElement, NotificationElement) -> Unit

/** The options accepted by the `show` entry point. */
external interface NotificationShowOptions {
    /** Whether the notification is announced immediately and interrupts other announcements. */
    var assertive: Boolean?

    /** The class name of the notification. */
    var className: String?

    /** Duration in milliseconds before the notification is closed automatically. */
    var duration: Double?

    /** Where to position the notification. */
    var position: String?

    /** The Lumo theme variants applied to the notification. */
    var theme: String?
}
