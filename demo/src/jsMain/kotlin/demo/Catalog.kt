package demo

import demo.page.overview.OverviewPage
import demo.pages.buttons.ButtonPage
import demo.pages.buttons.MenuBarPage
import demo.pages.fields.TextFieldPage
import demo.pages.fields.TextAreaPage
import demo.pages.fields.EmailFieldPage
import demo.pages.fields.PasswordFieldPage
import demo.pages.fields.NumberFieldPage
import demo.pages.fields.IntegerFieldPage
import demo.pages.fields.CustomFieldPage
import demo.pages.fields.FieldHighlighterPage
import demo.pages.selection.CheckboxPage
import demo.pages.selection.CheckboxGroupPage
import demo.pages.selection.RadioGroupPage
import demo.pages.selection.SwitchPage
import demo.pages.selection.ListBoxPage
import demo.pages.selection.ItemPage
import demo.pages.selection.SliderPage
import demo.pages.selection.ComboBoxPage
import demo.pages.selection.SelectPage
import demo.pages.selection.MultiSelectComboBoxPage
import demo.pages.pickers.DatePickerPage
import demo.pages.pickers.TimePickerPage
import demo.pages.pickers.DateTimePickerPage
import demo.pages.overlays.DialogPage
import demo.pages.overlays.ConfirmDialogPage
import demo.pages.overlays.OverlayPage
import demo.pages.overlays.LoginPage
import demo.pages.overlays.PopoverPage
import demo.pages.overlays.NotificationPage
import demo.pages.overlays.ContextMenuPage
import demo.pages.overlays.TooltipPage
import demo.pages.data.VirtualListPage
import demo.pages.data.MessageListPage
import demo.pages.data.MessageInputPage
import demo.pages.data.MarkdownPage
import demo.pages.data.GridPage
import demo.pages.upload.UploadPage
import demo.pages.layout.AppLayoutPage
import demo.pages.layout.HorizontalLayoutPage
import demo.pages.layout.VerticalLayoutPage
import demo.pages.layout.FormLayoutPage
import demo.pages.layout.SplitLayoutPage
import demo.pages.layout.ScrollerPage
import demo.pages.layout.MasterDetailLayoutPage
import demo.pages.layout.CardPage
import demo.pages.layout.AccordionPage
import demo.pages.layout.DetailsPage
import demo.pages.layout.TabsPage
import demo.pages.layout.TabSheetPage
import demo.pages.layout.SideNavPage
import demo.pages.layout.BreadcrumbsPage
import demo.pages.display.IconPage
import demo.pages.display.AvatarGroupPage
import demo.pages.display.AvatarPage
import demo.pages.display.BadgePage
import demo.pages.display.IconSetsPage
import demo.pages.display.ProgressBarPage
import demo.pages.display.InputContainerPage
import demo.pages.theming.ThemingPage

/**
 * The showcase's one registry: every wrapped component, its sidebar group, and the route table.
 *
 * The entries themselves live on their pages (`XxxPage.ENTRY`), because an entry is that page's own
 * metadata — route id, group, bilingual copy, and the tag it builds. This object only collects them.
 *
 * Every member is `by lazy`: the catalog is assembled from sixty companions in eleven packages,
 * and Kotlin/JS initialises file-level properties in an order nobody controls. Reading them here,
 * on first use, is what makes the collection independent of that order.
 */
object Catalog {
    /** Base URL of the Vaadin component documentation. Every page links to its own page below it. */
    const val DOCS_BASE: String = "https://vaadin.com/docs/latest/zh/components"

    /** Base URL of the Vaadin styling documentation (themes, style properties, utility classes). */
    const val STYLING_BASE: String = "https://vaadin.com/docs/latest/zh/styling"

    /** Dokka output assembled by `pagesSite` next to the demo. */
    const val API_ROOT: String = "api/ktjs-vaadin-web-components"

    /** Top-bar entry point of the API reference. */
    const val API_DOCS_URL: String = "$API_ROOT/index.html"

    /**
     * Every wrapped component, one page each.
     *
     * The order of the groups is the order of the sidebar; within a group the order below is the
     * navigation order. The index page reads this list, so a new entry shows up without a second
     * registration.
     */
    val ENTRIES: List<CatalogEntry> by lazy {
        listOf(
            // overview
            OverviewPage.ENTRY,
            // buttons
            ButtonPage.ENTRY, MenuBarPage.ENTRY,
            // fields
            TextFieldPage.ENTRY, TextAreaPage.ENTRY, EmailFieldPage.ENTRY, PasswordFieldPage.ENTRY,
            NumberFieldPage.ENTRY, IntegerFieldPage.ENTRY, CustomFieldPage.ENTRY, FieldHighlighterPage.ENTRY,
            // selection
            CheckboxPage.ENTRY, CheckboxGroupPage.ENTRY, RadioGroupPage.ENTRY, SwitchPage.ENTRY,
            ListBoxPage.ENTRY, ItemPage.ENTRY, SliderPage.ENTRY, ComboBoxPage.ENTRY,
            SelectPage.ENTRY, MultiSelectComboBoxPage.ENTRY,
            // pickers
            DatePickerPage.ENTRY, TimePickerPage.ENTRY, DateTimePickerPage.ENTRY,
            // overlays
            DialogPage.ENTRY, ConfirmDialogPage.ENTRY, OverlayPage.ENTRY, LoginPage.ENTRY,
            PopoverPage.ENTRY, NotificationPage.ENTRY, ContextMenuPage.ENTRY, TooltipPage.ENTRY,
            // data
            VirtualListPage.ENTRY, MessageListPage.ENTRY, MessageInputPage.ENTRY, MarkdownPage.ENTRY,
            GridPage.ENTRY,
            // upload
            UploadPage.ENTRY,
            // layout
            AppLayoutPage.ENTRY, HorizontalLayoutPage.ENTRY, VerticalLayoutPage.ENTRY, FormLayoutPage.ENTRY,
            SplitLayoutPage.ENTRY, ScrollerPage.ENTRY, MasterDetailLayoutPage.ENTRY, CardPage.ENTRY,
            AccordionPage.ENTRY, DetailsPage.ENTRY, TabsPage.ENTRY, TabSheetPage.ENTRY,
            SideNavPage.ENTRY, BreadcrumbsPage.ENTRY,
            // display
            IconPage.ENTRY, AvatarGroupPage.ENTRY, AvatarPage.ENTRY, BadgePage.ENTRY,
            IconSetsPage.ENTRY, ProgressBarPage.ENTRY, InputContainerPage.ENTRY,
            // theming
            ThemingPage.ENTRY,
        )
    }

    /** Sidebar groups that actually have entries, in navigation order. */
    val GROUPS: List<Group> by lazy {
        ENTRIES.map { it.group }.distinct()
    }

    private val BY_ID: Map<String, CatalogEntry> by lazy { ENTRIES.associateBy { it.id } }

    /** Looks up an entry by route id. */
    fun entry(id: String): CatalogEntry? = BY_ID[id]

    /** Entries of one sidebar group, in declaration order. */
    fun entriesOf(group: Group): List<CatalogEntry> = ENTRIES.filter { it.group == group }

    /**
     * Dokka package page of the component behind [id].
     *
     * Wrapper packages mirror the npm package names with the hyphens removed (`text-field` →
     * `io.github.shijianjs.ktjs.vaadin.textfield`), so the route id is enough to locate the package.
     * `overview` and `theming` cover several packages and link to the module index / Lumo package instead.
     */
    fun componentApiUrl(id: String): String =
        when (id) {
            "overview" -> API_DOCS_URL
            "theming" -> "$API_ROOT/vaadin.lumostyles/index.html"
            else -> "$API_ROOT/vaadin.${id.replace("-", "")}/index.html"
        }
}
