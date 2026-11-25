package io.github.shijianjs.ktjs.vaadin.avatargroup

/**
 * `<vaadin-avatar-group-menu>` — the overflow menu `<vaadin-avatar-group>` opens for avatars that
 * do not fit.
 *
 * `@protected` upstream, so the manifest does not describe it and it gets no generated wrapper;
 * its API is `ListMixin`'s, the same as a list box.
 */
abstract external class AvatarGroupMenuElement : web.html.HTMLElement, io.github.shijianjs.ktjs.vaadin.a11ybase.ListMixin, io.github.shijianjs.ktjs.vaadin.themablemixin.ThemableMixin, io.github.shijianjs.ktjs.vaadin.componentbase.DirMixin, io.github.shijianjs.ktjs.vaadin.componentbase.PolylitMixin, io.github.shijianjs.ktjs.vaadin.themablemixin.LumoInjectionMixin

@JsModule("@vaadin/avatar-group/src/vaadin-avatar-group-menu.js")
private external object AvatarGroupMenuElementModule {
    val AvatarGroupMenu: web.components.CustomElementConstructor<AvatarGroupMenuElement>
}

/** Imports `@vaadin/avatar-group/src/vaadin-avatar-group-menu.js`, registering `<vaadin-avatar-group-menu>`. */
fun registerAvatarGroupMenu(): web.components.CustomElementConstructor<AvatarGroupMenuElement> = AvatarGroupMenuElementModule.AvatarGroupMenu

/** Creates a `<vaadin-avatar-group-menu>` element. The custom element is registered on first use. */
fun createAvatarGroupMenu(configure: AvatarGroupMenuElement.() -> Unit = {}): AvatarGroupMenuElement {
    registerAvatarGroupMenu()
    return web.dom.document.createElement(io.github.shijianjs.ktjs.vaadin.VaadinTagName.avatarGroupMenu).apply(configure)
}
