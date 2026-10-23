package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-message-list>` is a Web Component for showing an ordered list of messages. The messages are rendered as <vaadin-message>
 *
 * ### Example
 *
 * To create a new message list, add the component to the page:
 *
 * ```html
 * <vaadin-message-list></vaadin-message-list>
 * ```
 *
 * Provide the messages to the message list with the [`items`](#/elements/vaadin-message-list#property-items) property.
 *
 * ```js
 * document.querySelector('vaadin-message-list').items = [
 *   { text: 'Hello list', time: 'yesterday', userName: 'Matt Mambo', userAbbr: 'MM', userColorIndex: 1 },
 *   { text: 'Another message', time: 'right now', userName: 'Linsey Listy', userAbbr: 'LL', userColorIndex: 2, userImg: '/static/img/avatar.jpg' }
 * ];
 * ```
 *
 * ### Styling
 *
 * The following shadow DOM parts are available for styling:
 *
 * Part name | Description
 * ----------|----------------
 * `list`    | The container wrapping messages.
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                |
 * :--------------------------------- |
 * `--vaadin-message-list-max-width`  |
 * `--vaadin-message-list-padding`    |
 *
 * ### Built-in Theme Variants
 *
 * `<vaadin-message-list>` supports the following theme variants:
 *
 * Theme variant        | Description
 * ---------------------|---------------
 * `theme="bubble"`     | Shows the messages as chat bubbles
 * `theme="one-to-one"` | Hides the avatar and name of every message, for a chat between two participants. Works together with `bubble`
 *
 * See the [`<vaadin-message>`](#/elements/vaadin-message) documentation for the available
 * theme variants, state attributes and stylable shadow parts of message elements.
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/message-list)
 */
external class MessageListElement : HTMLElement {
    /**
     * When set to `true`, new messages are announced to assistive technologies using ARIA live regions.
     */
    var announceMessages: Boolean

    /**
     * An array of objects which will be rendered as messages.
     * The message objects can have the following properties:
     * ```js
     * Array<{
     *   text: string,
     *   time: string,
     *   userName: string,
     *   userAbbr: string,
     *   userImg: string,
     *   userColorIndex: number,
     *   className: string,
     *   theme: string,
     *   attachments: Array<{
     *     name: string,
     *     url: string,
     *     type: string
     *   }>
     * }>
     * ```
     *
     * When a message has attachments, they are rendered in the message's shadow DOM.
     * Image attachments (type starting with "image/") show a thumbnail preview,
     * while other attachments show a document icon with the file name.
     * Clicking an attachment dispatches an `attachment-click` event.
     */
    var items: dynamic

    /**
     * When set to `true`, the message text is parsed as Markdown.
     */
    var markdown: Boolean
}

/**
 * Named exports of `@vaadin/message-list/vaadin-message-list.js`.
 *
 * Referencing the module registers the `<vaadin-message-list>` custom element.
 */
@JsModule("@vaadin/message-list/vaadin-message-list.js")
private external object MessageListModule {
    /** The registered custom element class. */
    val MessageList: dynamic
}

/**
 * Imports `@vaadin/message-list/vaadin-message-list.js`, registering the `<vaadin-message-list>` custom element,
 * and returns the element class.
 */
fun registerMessageList(): dynamic = MessageListModule.MessageList

/**
 * Creates a `<vaadin-message-list>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createMessageList(configure: MessageListElement.() -> Unit = {}): MessageListElement {
    registerMessageList()
    return document.createElement(VaadinTagName.messageList).apply(configure)
}
