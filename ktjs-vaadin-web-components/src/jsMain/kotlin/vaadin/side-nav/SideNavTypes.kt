package vaadin.sidenav

/**
 * The event object passed to [SideNavElement.onNavigate].
 *
 * Call `preventDefault()` on [originalEvent] to cancel the navigation.
 */
external interface NavigateEvent {
    /** The path of the clicked item. */
    var path: String?

    /** The link target of the clicked item. */
    var target: String?

    /** Whether the clicked item is the current one. */
    var current: Boolean

    /** Whether the clicked item is expanded. */
    var expanded: Boolean

    /** All paths of the clicked item. */
    var pathAliases: Array<String>

    /** The original click event. */
    var originalEvent: web.mouse.MouseEvent
}
