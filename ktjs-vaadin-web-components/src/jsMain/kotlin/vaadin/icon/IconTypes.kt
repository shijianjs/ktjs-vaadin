package vaadin.icon

/**
 * An SVG literal produced by Lit's `svg` tagged template: an `SVGTemplateResult`, or the `nothing`
 * sentinel when there is no content.
 *
 * Kotlin/JS cannot author tagged template literals, so the value stays opaque here. Obtain one from
 * [unsafeSvgLiteral], [ensureSvgLiteral], [cloneSvgNode] or [Iconset.getIconSvg], and assign it to
 * [IconMixin.svg].
 */
typealias IconSvgLiteral = kotlin.js.JsAny

/**
 * An icon of a registered iconset, as returned by [Iconset.getIconSvg].
 *
 * Assign [svg] to [IconMixin.svg] to render the icon.
 */
external interface IconSvgInfo {
    /** The icon content as an SVG literal, or the `nothing` sentinel when the icon was not found. */
    val svg: IconSvgLiteral

    /** The size in pixels declared by the iconset. */
    val size: Double?

    /** The `viewBox` attribute of the iconset entry, or `null` when the entry has none. */
    val viewBox: String?
}
