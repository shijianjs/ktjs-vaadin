package demo.pages.display

/** A permissions row of `badge-icons-only-table.ts`: one boolean per guarded action. */
internal external interface PermissionRow {
    var name: String
    var view: Boolean?
    var comment: Boolean?
    var edit: Boolean?
}
