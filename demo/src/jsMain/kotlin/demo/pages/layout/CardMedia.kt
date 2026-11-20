package demo.pages.layout

import demo.ui.Images
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachShadowRoot
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import web.html.HTMLImageElement
import web.html.HtmlTagName

/**
 * `<demo-card-media-image>`: the card's media slot filled with the official Lapland photo.
 *
 * The image keeps its 100px width, which is what the plain media example shows; a card that stretches or
 * covers its media takes the host's box, and the image fills it. The slot name sits on the host, because
 * that is the node the card reads.
 */
internal class CardMediaImage : CustomElementAdapter() {
    private val image: HTMLImageElement = HtmlTagName.img().apply {
        src = Images.lapland
        alt = ""
        style.setProperty("display", "block")
        style.setProperty("width", "100%")
        style.setProperty("height", "100%")
        style.setProperty("object-fit", "cover")
    }

    private var fixedWidth: Int? = null

    init {
        attachShadowRoot().append(image)
    }

    /**
     * The host is the slot the card reads; an attribute may not be set before the element connects.
     *
     * A card that stretches or covers its media sizes the host, and the image follows it. The plain
     * media example pins [fixedWidth] instead, so the thumbnail keeps the size the docs show.
     */
    override fun connectedCallback() {
        slot = "media"
        style.setProperty("display", "block")
        show(fixedWidth)
    }

    /** Pins the thumbnail's width; without one the host takes the box the card gives its media. */
    fun show(width: Int?) {
        fixedWidth = width
        if (width != null) {
            style.setProperty("width", "${width}px")
            style.setProperty("height", "auto")
        } else {
            style.setProperty("width", "100%")
            style.setProperty("height", "100%")
        }
    }

    companion object : CustomElementReferenceAdapter<CardMediaImage>(CardMediaImage::class.js, "demo-card-media-image")
}

