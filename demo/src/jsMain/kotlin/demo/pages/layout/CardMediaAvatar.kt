package demo.pages.layout

import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachShadowRoot
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import io.github.shijianjs.ktjs.vaadin.avatar.AvatarElement
import io.github.shijianjs.ktjs.vaadin.avatar.createAvatar
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme

/**
 * `<demo-card-media-avatar>`: the card example's media slot, filled with a large initials avatar.
 *
 * The slot name sits on the host, because that is what the card reads; the avatar itself is the shadow
 * child, so the element hides which component the slot was filled with.
 */
internal class CardMediaAvatar : CustomElementAdapter() {
    private val avatar: AvatarElement = createAvatar().addTheme("large")

    init {
        attachShadowRoot().append(avatar)
    }

    /** The host is the slot the card reads; an attribute may not be set before the element connects. */
    override fun connectedCallback() {
        slot = "media"
    }

    /** Sets the initials the avatar shows. */
    fun show(abbr: String) {
        avatar.abbr = abbr
    }

    companion object : CustomElementReferenceAdapter<CardMediaAvatar>(CardMediaAvatar::class.js, "demo-card-media-avatar")
}

