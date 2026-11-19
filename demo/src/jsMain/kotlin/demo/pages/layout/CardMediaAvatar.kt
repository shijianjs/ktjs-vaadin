package demo.pages.layout

import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachShadowRoot
import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import io.github.shijianjs.ktjs.vaadin.avatar.AvatarElement
import io.github.shijianjs.ktjs.vaadin.avatar.createAvatar
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import web.components.CustomElementReference
import web.dom.TagName

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

    companion object : CustomElementReference<CardMediaAvatar>(CardMediaAvatar::class.js) {
        val NAME: TagName<CardMediaAvatar> = TagName("demo-card-media-avatar")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<demo-card-media-avatar>` showing [abbr]. */
internal fun newCardMediaAvatar(abbr: String): CardMediaAvatar {
    CardMediaAvatar.define()
    return CardMediaAvatar.NAME().also { it.show(abbr) }
}
