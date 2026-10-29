package demo.pages.display

import demo.tr
import demo.ui.withTheme
import vaadin.badge.BadgeElement
import vaadin.badge.createBadge
import vaadin.icon.createIcon
import web.dom.document

/** A permissions row of `badge-icons-only-table.ts`: one boolean per guarded action. */
internal external interface PermissionRow {
    var name: String
    var view: Boolean?
    var comment: Boolean?
    var edit: Boolean?
}

/** The "Yes" / "No" badge of the permissions table. */
internal fun permissionBadge(granted: Boolean): BadgeElement =
    iconBadge(
        if (granted) "vaadin:check" else "vaadin:close",
        tr(if (granted) "是" else "否", if (granted) "Yes" else "No"),
        if (granted) "success" else "error",
        "icon-only",
    )

/** A badge with an icon in the `icon` slot, before the text. */
internal fun iconBadge(icon: String, text: String, vararg themes: String): BadgeElement =
    createBadge()
        .withTheme(*themes)
        .apply {
            append(createIcon { this.icon = icon }.apply { slot = "icon" })
            append(document.createTextNode(text))
        }
