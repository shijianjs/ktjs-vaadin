package vaadin.messagelist

/** An attachment displayed with a message. */
external interface MessageAttachment {
    /** The attachment file name. */
    var name: String?

    /** The URL the attachment points to. */
    var url: String?

    /** The MIME type of the attachment, for example `image/png`. */
    var type: String?
}

/** An item rendered by `<vaadin-message-list>`. */
external interface MessageListItem {
    /** The message text. */
    var text: String?

    /** The time of sending the message, rendered as-is. */
    var time: String?

    /** The name of the user posting the message. */
    var userName: String?

    /** The abbreviation of the user, shown in the avatar. */
    var userAbbr: String?

    /** The URL of the user avatar image. */
    var userImg: String?

    /** A color index used to render the avatar. */
    var userColorIndex: Double?

    /** A theme name for the message. */
    var theme: String?

    /** A CSS class name for the message. */
    var className: String?

    /** The attachments displayed with the message. */
    var attachments: Array<MessageAttachment>?
}
