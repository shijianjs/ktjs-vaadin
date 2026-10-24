package vaadin.notification

import web.html.HTMLElement

/** Renders the content of a notification. */
typealias NotificationRenderer = (HTMLElement, NotificationElement) -> Unit
