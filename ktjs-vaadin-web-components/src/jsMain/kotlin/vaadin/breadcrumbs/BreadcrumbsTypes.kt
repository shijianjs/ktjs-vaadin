package vaadin.breadcrumbs

/**
 * Localization of `<vaadin-breadcrumbs>`.
 *
 * Upstream declares `i18n` with this type in `vaadin-breadcrumbs.d.ts`; the manifest only records
 * the inherited, untyped `I18nMixin` member, so the element exposes it as [BreadcrumbsElement.i18nProperty].
 */
external interface BreadcrumbsI18n {
    /**
     * Accessible label of the button that reveals the items collapsed into the overflow menu.
     *
     * Defaults to `More items`.
     */
    var moreItems: String?
}
