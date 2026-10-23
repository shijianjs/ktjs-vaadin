package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-message-input>` is a Web Component for sending messages.
 * It consists of a text area that grows on along with the content, and a send button to send message.
 *
 * The message can be sent by one of the following actions:
 * - by pressing Enter (use Shift + Enter to add a new line)
 * - by clicking `submit` button.
 *
 * ```html
 * <vaadin-message-input></vaadin-message-input>
 * ```
 *
 * ### Slots
 *
 * The following slots are available for adding content to the message input:
 *
 * Name       | Description
 * -----------|-------------
 * `header`   | Content displayed above the text area and controls
 * `prefix`   | Content displayed before the text area
 * `button`   | Button that submits the message, replacing the default one
 * `footer`   | Content displayed below the text area and controls
 *
 * When a custom button has no text content or accessible label, `aria-label` attribute
 * is set based on the [`i18n`](#/elements/vaadin-message-input#property-i18n) property.
 *
 * ### Styling
 *
 * The following state attributes are available for styling:
 *
 * Attribute      | Description
 * ---------------|---------------------------------
 * `disabled`     | Set when the element is disabled
 * `focused`      | Set when the text area is focused
 * `focus-ring`   | Set when the text area is focused using the keyboard
 * `has-header`   | Set when the element has content in the header slot
 * `has-prefix`   | Set when the element has content in the prefix slot
 * `has-footer`   | Set when the element has content in the footer slot
 * `has-tooltip`  | Set when the element has a slotted tooltip
 *
 * ### Internal components
 *
 * In addition to `<vaadin-message-input>` itself, the following internal
 * components are themable:
 *
 * - `<vaadin-message-input-button>` - has the same API as `<vaadin-button>`
 * - `<vaadin-text-area>`
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/message-input)
 */
external class MessageInputElement : HTMLElement {
    /**
     * Set to true to disable this element.
     */
    var disabled: Boolean

    /**
     * The object used to localize this component. To change the default
     * localization, replace this with an object that provides all properties, or
     * just the individual properties you want to change.
     *
     * The object has the following JSON structure and default values:
     * ```js
     * {
     *   // Used as the button label
     *   send: 'Send',
     *
     *   // Used as the input field's placeholder and aria-label
     *   message: 'Message'
     * }
     * ```
     */
    var i18n: dynamic

    /**
     * Current content of the text input field
     */
    var value: String?
}

/**
 * Named exports of `@vaadin/message-input/vaadin-message-input.js`.
 *
 * Referencing the module registers the `<vaadin-message-input>` custom element.
 */
@JsModule("@vaadin/message-input/vaadin-message-input.js")
private external object MessageInputModule {
    /** The registered custom element class. */
    val MessageInput: dynamic
}

/**
 * Imports `@vaadin/message-input/vaadin-message-input.js`, registering the `<vaadin-message-input>` custom element,
 * and returns the element class.
 */
fun registerMessageInput(): dynamic = MessageInputModule.MessageInput

/**
 * Creates a `<vaadin-message-input>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createMessageInput(configure: MessageInputElement.() -> Unit = {}): MessageInputElement {
    registerMessageInput()
    return document.createElement(VaadinTagName.messageInput).apply(configure)
}
