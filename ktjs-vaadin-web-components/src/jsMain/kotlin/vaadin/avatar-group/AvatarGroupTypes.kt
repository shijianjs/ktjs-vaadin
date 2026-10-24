package vaadin.avatargroup

/**
 * An avatar in the `items` array of `<vaadin-avatar-group>`.
 *
 * Every property is optional. When neither [img] nor [abbr] is set, initials are derived from
 * [name].
 */
external interface AvatarGroupItem {
    /** The name of the person. */
    var name: String?

    /** The abbreviated name rendered when [img] is not available. */
    var abbr: String?

    /** The URL of the avatar image. */
    var img: String?

    /** The index of the color palette entry used by the avatar. */
    var colorIndex: Double?

    /** A CSS class name added to the generated `<vaadin-avatar>` element. */
    var className: String?
}
