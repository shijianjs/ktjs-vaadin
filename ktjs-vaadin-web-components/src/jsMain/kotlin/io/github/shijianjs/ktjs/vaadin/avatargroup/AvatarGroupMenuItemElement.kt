package io.github.shijianjs.ktjs.vaadin.avatargroup

/**
 * `<vaadin-avatar-group-menu-item>` — one row of [AvatarGroupMenuElement].
 *
 * `@protected` upstream (missing from the manifest); its API is `ItemMixin`'s, like
 * `<vaadin-item>`: `selected`, `value`, `disabled`.
 */
abstract external class AvatarGroupMenuItemElement : web.html.HTMLElement, io.github.shijianjs.ktjs.vaadin.item.ItemMixin, io.github.shijianjs.ktjs.vaadin.themablemixin.ThemableMixin, io.github.shijianjs.ktjs.vaadin.componentbase.DirMixin, io.github.shijianjs.ktjs.vaadin.componentbase.PolylitMixin, io.github.shijianjs.ktjs.vaadin.themablemixin.LumoInjectionMixin

@JsModule("@vaadin/avatar-group/src/vaadin-avatar-group-menu-item.js")
private external object AvatarGroupMenuItemElementModule {
    val AvatarGroupMenuItem: web.components.CustomElementConstructor<AvatarGroupMenuItemElement>
}

/** Imports `@vaadin/avatar-group/src/vaadin-avatar-group-menu-item.js`, registering `<vaadin-avatar-group-menu-item>`. */
fun registerAvatarGroupMenuItem(): web.components.CustomElementConstructor<AvatarGroupMenuItemElement> = AvatarGroupMenuItemElementModule.AvatarGroupMenuItem

/** Creates a `<vaadin-avatar-group-menu-item>` element. The custom element is registered on first use. */
fun createAvatarGroupMenuItem(configure: AvatarGroupMenuItemElement.() -> Unit = {}): AvatarGroupMenuItemElement {
    registerAvatarGroupMenuItem()
    return web.dom.document.createElement(io.github.shijianjs.ktjs.vaadin.VaadinTagName.avatarGroupMenuItem).apply(configure)
}
