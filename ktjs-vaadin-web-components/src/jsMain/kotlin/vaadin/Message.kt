package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-message>` is a Web Component for showing a single message with an author, message and time.
 *
 * ```html
 * <vaadin-message
 *   time="2021-01-28 10:43"
 *   user-name="Bob Ross"
 *   user-abbr="BR"
 *   user-img="/static/img/avatar.jpg"
 * >
 *  There is no real ending. It's just the place where you stop the story.
 * </vaadin-message>
 * ```
 *
 * ### Styling
 *
 * The following shadow DOM parts are available for styling:
 *
 * Part name           | Description
 * --------------------|----------------
 * `name`              | Author's name
 * `time`              | When the message was posted
 * `content`           | The message itself as a slotted content
 * `attachments`       | Container for the attachments
 * `attachment`        | Individual attachment button
 * `attachment-image`  | Image attachment button (in addition to `attachment`)
 * `attachment-file`   | File attachment button (in addition to `attachment`)
 * `attachment-preview`| Image preview inside an image attachment
 * `attachment-icon`   | File icon inside a file attachment
 * `attachment-name`   | File name inside a file attachment
 *
 * The following state attributes are available for styling:
 *
 * Attribute           | Description
 * --------------------|-------------
 * `focus-ring`        | Set when the message is focused using the keyboard.
 * `focused`           | Set when the message is focused.
 * `typing-indicator`  | Set when the message is rendered as a typing indicator. The value is the indicator style: empty, `ellipsis` or `minimal`.
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                          |
 * :------------------------------------------- |
 * `--vaadin-message-attachment-background`     |
 * `--vaadin-message-attachment-border-color`   |
 * `--vaadin-message-attachment-border-radius`  |
 * `--vaadin-message-attachment-border-width`   |
 * `--vaadin-message-attachment-font-size`      |
 * `--vaadin-message-attachment-font-weight`    |
 * `--vaadin-message-attachment-gap`            |
 * `--vaadin-message-attachment-line-height`    |
 * `--vaadin-message-attachment-padding`        |
 * `--vaadin-message-attachment-text-color`     |
 * `--vaadin-message-attachments-alignment`     |
 * `--vaadin-message-content-background`        |
 * `--vaadin-message-content-border-radius`     |
 * `--vaadin-message-content-padding`           |
 * `--vaadin-message-font-size`                 |
 * `--vaadin-message-font-weight`               |
 * `--vaadin-message-gap`                       |
 * `--vaadin-message-header-line-height`        |
 * `--vaadin-message-line-height`               |
 * `--vaadin-message-name-color`                |
 * `--vaadin-message-name-font-size`            |
 * `--vaadin-message-name-font-weight`          |
 * `--vaadin-message-padding`                   |
 * `--vaadin-message-text-color`                |
 * `--vaadin-message-time-color`                |
 * `--vaadin-message-time-font-size`            |
 * `--vaadin-message-time-font-weight`          |
 * `--vaadin-message-user-color`                |
 *
 * ### Built-in Theme Variants
 *
 * `<vaadin-message>` supports the following theme variants. Both of them require the parent
 * [`<vaadin-message-list>`](#/elements/vaadin-message-list) to use `theme="bubble"`:
 *
 * Theme variant         | Description
 * ----------------------|---------------
 * `theme="self"`        | Shows the message as sent by the current user, hides the avatar and name
 * `theme="full-width"`  | Removes the bubble and the width restriction, for an assistant or AI response
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/message-list)
 */
external class MessageElement : HTMLElement {
    /**
     * An array of attachment objects to display with the message.
     * Each attachment object can have the following properties:
     * - `name`: The name of the attachment file
     * - `url`: The URL of the attachment
     * - `type`: The MIME type of the attachment (e.g., 'image/png', 'application/pdf')
     *
     * Image attachments (type starting with "image/") show a thumbnail preview,
     * while other attachments show a document icon with the file name.
     */
    var attachments: dynamic

    /**
     * Time of sending the message. It is rendered as-is to the part='time' slot,
     * so the formatting is up to you.
     */
    var time: String?

    /**
     * The abbreviation of the user.
     * The abbreviation will be passed on to avatar of the message.
     * If the user does not have an avatar picture set with `userImg`, `userAbbr` will be shown in the avatar.
     * Example: `message.userAbbr = "JJ";`
     */
    var userAbbr: String?

    /**
     * A color index to be used to render the color of the avatar.
     * Also sets `--vaadin-message-user-color` custom CSS property.
     */
    var userColorIndex: Double?

    /**
     * An URL for a user image.
     * The image will be used in the avatar component to show who has sent the message.
     * Example: `message.userImg = "/static/img/avatar.jpg";`
     */
    var userImg: String?

    /**
     * The name of the user posting the message.
     * It will be placed in the name part to indicate who has sent the message.
     * It is also used as a tooltip for the avatar.
     * Example: `message.userName = "Jessica Jacobs";`
     */
    var userName: String?
}

/**
 * Named exports of `@vaadin/message-list/vaadin-message.js`.
 *
 * Referencing the module registers the `<vaadin-message>` custom element.
 */
@JsModule("@vaadin/message-list/vaadin-message.js")
private external object MessageModule {
    /** The registered custom element class. */
    val Message: dynamic
}

/**
 * Imports `@vaadin/message-list/vaadin-message.js`, registering the `<vaadin-message>` custom element,
 * and returns the element class.
 */
fun registerMessage(): dynamic = MessageModule.Message

/**
 * Creates a `<vaadin-message>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createMessage(configure: MessageElement.() -> Unit = {}): MessageElement {
    registerMessage()
    return document.createElement(VaadinTagName.message).apply(configure)
}
