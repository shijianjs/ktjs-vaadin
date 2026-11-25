package io.github.shijianjs.ktjs.vaadin.messageinput

/**
 * `<vaadin-message-input-button>` — the send button `<vaadin-message-input>` renders.
 *
 * `@protected` upstream, so the manifest never describes it and it gets no generated wrapper. Its
 * API is `ButtonMixin`'s (the same mixin `<vaadin-button>` is built from), not a `Button` subclass.
 */
abstract external class MessageInputButtonElement : web.html.HTMLElement, io.github.shijianjs.ktjs.vaadin.button.ButtonMixin, io.github.shijianjs.ktjs.vaadin.componentbase.DirMixin, io.github.shijianjs.ktjs.vaadin.themablemixin.ThemableMixin, io.github.shijianjs.ktjs.vaadin.componentbase.PolylitMixin, io.github.shijianjs.ktjs.vaadin.themablemixin.LumoInjectionMixin

@JsModule("@vaadin/message-input/src/vaadin-message-input-button.js")
private external object MessageInputButtonElementModule {
    val MessageInputButton: web.components.CustomElementConstructor<MessageInputButtonElement>
}

/** Imports `@vaadin/message-input/src/vaadin-message-input-button.js`, registering `<vaadin-message-input-button>`. */
fun registerMessageInputButton(): web.components.CustomElementConstructor<MessageInputButtonElement> = MessageInputButtonElementModule.MessageInputButton

/** Creates a `<vaadin-message-input-button>` element. The custom element is registered on first use. */
fun createMessageInputButton(configure: MessageInputButtonElement.() -> Unit = {}): MessageInputButtonElement {
    registerMessageInputButton()
    return web.dom.document.createElement(io.github.shijianjs.ktjs.vaadin.VaadinTagName.messageInputButton).apply(configure)
}
