package vaadin

import web.dom.TagName
import web.dom.document
import web.html.HTMLElement

/**
 * `<vaadin-master-detail-layout>` is a web component for building UIs with a master
 * (or primary) area and a detail (or secondary) area that is displayed next to, or
 * overlaid on top of, the master area, depending on configuration and viewport size.
 *
 * ### Slots
 *
 * The component has two main content areas: the master area (default slot)
 * and the detail area (`detail` slot). When the detail doesn't fit next to
 * the master, it is shown as an overlay on top of the master area:
 *
 * ```html
 * <vaadin-master-detail-layout>
 *   <div>Master content</div>
 *   <div slot="detail">Detail content</div>
 * </vaadin-master-detail-layout>
 * ```
 *
 * The component also supports a `detail-placeholder` slot for content shown
 * in the detail area when no detail is selected. Unlike the `detail` slot,
 * the placeholder is simply hidden when it doesn't fit next to the master area,
 * rather than shown as an overlay:
 *
 * ```html
 * <vaadin-master-detail-layout>
 *   <div>Master content</div>
 *   <div slot="detail-placeholder">Select an item</div>
 * </vaadin-master-detail-layout>
 * ```
 *
 * ### Styling
 *
 * The following shadow DOM parts are available for styling:
 *
 * Part name             | Description
 * ----------------------|----------------------
 * `backdrop`            | Backdrop covering the master area in the overlay mode
 * `master`              | The master area
 * `detail`              | The detail area
 * `detail-placeholder`  | The detail placeholder area
 *
 * The following state attributes are available for styling:
 *
 * Attribute                 | Description
 * --------------------------|----------------------
 * `expand-master`           | Set when the master area expands to fill available space.
 * `expand-detail`           | Set when the detail area expands to fill available space.
 * `orientation`             | Set to `horizontal` or `vertical` depending on the orientation.
 * `has-detail`              | Set when the detail content is provided and visible.
 * `has-detail-placeholder`  | Set when the detail placeholder content is provided.
 * `overlay`                 | Set when columns don't fit and the detail is shown as an overlay.
 * `overlay-containment`     | Set to `layout` or `page`.
 *
 * The following custom CSS properties are available for styling:
 *
 * Custom CSS property                                  |
 * :----------------------------------------------------|
 * | `--vaadin-master-detail-layout-border-color`       |
 * | `--vaadin-master-detail-layout-border-width`       |
 * | `--vaadin-master-detail-layout-detail-background`  |
 * | `--vaadin-master-detail-layout-detail-shadow`      |
 * | `--vaadin-overlay-backdrop-background`             |
 *
 * See [Styling Components](https://vaadin.com/docs/latest/styling/styling-components) documentation.
 *
 * [Upstream package](https://github.com/vaadin/web-components/tree/main/packages/master-detail-layout)
 */
external class MasterDetailLayoutElement : HTMLElement {
    /**
     * Size (in CSS length units) to be set on the detail area in
     * the CSS grid layout. When there is not enough space to show
     * master and detail areas next to each other, the detail area
     * is shown as an overlay.
     * <p>
     * If not specified, the size is determined automatically by measuring
     * the detail content in a `min-content` CSS grid column when it first
     * becomes visible, and then caching the resulting intrinsic size. To
     * recalculate the cached intrinsic size, use the `recalculateLayout`
     * method.
     */
    var detailSize: String?

    /**
     * When true, the detail area grows to fill the available space.
     * If `expandMaster` is also true, both areas share the available
     * space equally.
     */
    var expandDetail: Boolean

    /**
     * When true, the master area grows to fill the available space.
     * If `expandDetail` is also true, both areas share the available
     * space equally.
     */
    var expandMaster: Boolean

    /**
     * When true, the layout forces the detail area to be shown as an overlay,
     * even if there is enough space for master and detail to be shown next to
     * each other using the default (split) mode.
     */
    var forceOverlay: Boolean

    /**
     * Size (in CSS length units) to be set on the master area in
     * the CSS grid layout. If there is not enough space to show
     * master and detail areas next to each other, the detail area
     * is shown as an overlay. Defaults to 30em.
     */
    var masterSize: String?

    /**
     * When true, the layout does not use animated transitions for the detail area.
     */
    var noAnimation: Boolean

    /**
     * Define how master and detail areas are shown next to each other,
     * and the way how size and min-size properties are applied to them.
     * Possible values are: `horizontal` or `vertical`.
     * Defaults to horizontal.
     */
    var orientation: String?

    /**
     * Defines the containment of the detail area when the layout is in
     * overlay mode. When set to `layout`, the overlay is confined to the
     * layout. When set to `page`, the overlay is confined to the
     * browser's viewport. Defaults to `layout`.
     */
    var overlayContainment: String?

    /**
     * Size (in CSS length units) for the detail area when shown as an
     * overlay. When not set, falls back to `detailSize`. Set to `100%`
     * to make the detail cover the full layout.
     */
    var overlaySize: String?

    /**
     * When `detailSize` is not explicitly set, re-measures the cached intrinsic size of
     * the detail content by placing it in a min-content CSS grid column, then repeats
     * this process for ancestor master-detail layouts without an explicit `detailSize`,
     * if any, so that their detail areas also adapt.
     *
     * Call this method after changing the detail content in a way that affects its intrinsic
     * size — for example, when opening a detail in a nested master-detail layout that was
     * not previously visible.
     *
     * NOTE: This method can be expensive in large layouts as it triggers consecutive
     * synchronous DOM reads and writes.
     */
    fun recalculateLayout()
}

/**
 * Named exports of `@vaadin/master-detail-layout/vaadin-master-detail-layout.js`.
 *
 * Referencing the module registers the `<vaadin-master-detail-layout>` custom element.
 */
@JsModule("@vaadin/master-detail-layout/vaadin-master-detail-layout.js")
private external object MasterDetailLayoutModule {
    /** The registered custom element class. */
    val MasterDetailLayout: dynamic
}

/**
 * Imports `@vaadin/master-detail-layout/vaadin-master-detail-layout.js`, registering the `<vaadin-master-detail-layout>` custom element,
 * and returns the element class.
 */
fun registerMasterDetailLayout(): dynamic = MasterDetailLayoutModule.MasterDetailLayout

/**
 * Creates a `<vaadin-master-detail-layout>` element.
 *
 * The element is not attached to the document. The custom element is registered on first use.
 *
 * @param configure applied to the new element.
 */
fun createMasterDetailLayout(configure: MasterDetailLayoutElement.() -> Unit = {}): MasterDetailLayoutElement {
    registerMasterDetailLayout()
    return document.createElement(VaadinTagName.masterDetailLayout).apply(configure)
}
