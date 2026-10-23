package vaadin

import web.dom.TagName

/**
 * Typed tag names of the wrapped Vaadin custom elements.
 *
 * Use these with `document.createElement` instead of raw strings.
 */
object VaadinTagName {
    /**
     * The `<vaadin-accordion-heading>` custom element.
     */
    inline val accordionHeading: TagName<AccordionHeadingElement>
        get() = TagName("vaadin-accordion-heading")

    /**
     * The `<vaadin-accordion-panel>` custom element.
     */
    inline val accordionPanel: TagName<AccordionPanelElement>
        get() = TagName("vaadin-accordion-panel")

    /**
     * The `<vaadin-accordion>` custom element.
     */
    inline val accordion: TagName<AccordionElement>
        get() = TagName("vaadin-accordion")

    /**
     * The `<vaadin-app-layout>` custom element.
     */
    inline val appLayout: TagName<AppLayoutElement>
        get() = TagName("vaadin-app-layout")

    /**
     * The `<vaadin-drawer-toggle>` custom element.
     */
    inline val drawerToggle: TagName<DrawerToggleElement>
        get() = TagName("vaadin-drawer-toggle")

    /**
     * The `<vaadin-avatar>` custom element.
     */
    inline val avatar: TagName<AvatarElement>
        get() = TagName("vaadin-avatar")

    /**
     * The `<vaadin-avatar-group>` custom element.
     */
    inline val avatarGroup: TagName<AvatarGroupElement>
        get() = TagName("vaadin-avatar-group")

    /**
     * The `<vaadin-badge>` custom element.
     */
    inline val badge: TagName<BadgeElement>
        get() = TagName("vaadin-badge")

    /**
     * The `<vaadin-breadcrumbs-item>` custom element.
     */
    inline val breadcrumbsItem: TagName<BreadcrumbsItemElement>
        get() = TagName("vaadin-breadcrumbs-item")

    /**
     * The `<vaadin-breadcrumbs>` custom element.
     */
    inline val breadcrumbs: TagName<BreadcrumbsElement>
        get() = TagName("vaadin-breadcrumbs")

    /**
     * The `<vaadin-button>` custom element.
     */
    inline val button: TagName<ButtonElement>
        get() = TagName("vaadin-button")

    /**
     * The `<vaadin-card>` custom element.
     */
    inline val card: TagName<CardElement>
        get() = TagName("vaadin-card")

    /**
     * The `<vaadin-checkbox>` custom element.
     */
    inline val checkbox: TagName<CheckboxElement>
        get() = TagName("vaadin-checkbox")

    /**
     * The `<vaadin-checkbox-group>` custom element.
     */
    inline val checkboxGroup: TagName<CheckboxGroupElement>
        get() = TagName("vaadin-checkbox-group")

    /**
     * The `<vaadin-combo-box>` custom element.
     */
    inline val comboBox: TagName<ComboBoxElement>
        get() = TagName("vaadin-combo-box")

    /**
     * The `<vaadin-confirm-dialog>` custom element.
     */
    inline val confirmDialog: TagName<ConfirmDialogElement>
        get() = TagName("vaadin-confirm-dialog")

    /**
     * The `<vaadin-context-menu-item>` custom element.
     */
    inline val contextMenuItem: TagName<ContextMenuItemElement>
        get() = TagName("vaadin-context-menu-item")

    /**
     * The `<vaadin-context-menu-list-box>` custom element.
     */
    inline val contextMenuListBox: TagName<ContextMenuListBoxElement>
        get() = TagName("vaadin-context-menu-list-box")

    /**
     * The `<vaadin-context-menu>` custom element.
     */
    inline val contextMenu: TagName<ContextMenuElement>
        get() = TagName("vaadin-context-menu")

    /**
     * The `<vaadin-custom-field>` custom element.
     */
    inline val customField: TagName<CustomFieldElement>
        get() = TagName("vaadin-custom-field")

    /**
     * The `<vaadin-date-picker>` custom element.
     */
    inline val datePicker: TagName<DatePickerElement>
        get() = TagName("vaadin-date-picker")

    /**
     * The `<vaadin-date-time-picker>` custom element.
     */
    inline val dateTimePicker: TagName<DateTimePickerElement>
        get() = TagName("vaadin-date-time-picker")

    /**
     * The `<vaadin-details-summary>` custom element.
     */
    inline val detailsSummary: TagName<DetailsSummaryElement>
        get() = TagName("vaadin-details-summary")

    /**
     * The `<vaadin-details>` custom element.
     */
    inline val details: TagName<DetailsElement>
        get() = TagName("vaadin-details")

    /**
     * The `<vaadin-dialog>` custom element.
     */
    inline val dialog: TagName<DialogElement>
        get() = TagName("vaadin-dialog")

    /**
     * The `<vaadin-email-field>` custom element.
     */
    inline val emailField: TagName<EmailFieldElement>
        get() = TagName("vaadin-email-field")

    /**
     * The `<vaadin-form-item>` custom element.
     */
    inline val formItem: TagName<FormItemElement>
        get() = TagName("vaadin-form-item")

    /**
     * The `<vaadin-form-layout>` custom element.
     */
    inline val formLayout: TagName<FormLayoutElement>
        get() = TagName("vaadin-form-layout")

    /**
     * The `<vaadin-form-row>` custom element.
     */
    inline val formRow: TagName<FormRowElement>
        get() = TagName("vaadin-form-row")

    /**
     * The `<vaadin-grid-column-group>` custom element.
     */
    inline val gridColumnGroup: TagName<GridColumnGroupElement>
        get() = TagName("vaadin-grid-column-group")

    /**
     * The `<vaadin-grid-column>` custom element.
     */
    inline val gridColumn: TagName<GridColumnElement>
        get() = TagName("vaadin-grid-column")

    /**
     * The `<vaadin-grid-filter-column>` custom element.
     */
    inline val gridFilterColumn: TagName<GridFilterColumnElement>
        get() = TagName("vaadin-grid-filter-column")

    /**
     * The `<vaadin-grid-filter>` custom element.
     */
    inline val gridFilter: TagName<GridFilterElement>
        get() = TagName("vaadin-grid-filter")

    /**
     * The `<vaadin-grid-selection-column>` custom element.
     */
    inline val gridSelectionColumn: TagName<GridSelectionColumnElement>
        get() = TagName("vaadin-grid-selection-column")

    /**
     * The `<vaadin-grid-sort-column>` custom element.
     */
    inline val gridSortColumn: TagName<GridSortColumnElement>
        get() = TagName("vaadin-grid-sort-column")

    /**
     * The `<vaadin-grid-sorter>` custom element.
     */
    inline val gridSorter: TagName<GridSorterElement>
        get() = TagName("vaadin-grid-sorter")

    /**
     * The `<vaadin-grid-tree-column>` custom element.
     */
    inline val gridTreeColumn: TagName<GridTreeColumnElement>
        get() = TagName("vaadin-grid-tree-column")

    /**
     * The `<vaadin-grid-tree-toggle>` custom element.
     */
    inline val gridTreeToggle: TagName<GridTreeToggleElement>
        get() = TagName("vaadin-grid-tree-toggle")

    /**
     * The `<vaadin-grid>` custom element.
     */
    inline val grid: TagName<GridElement>
        get() = TagName("vaadin-grid")

    /**
     * The `<vaadin-horizontal-layout>` custom element.
     */
    inline val horizontalLayout: TagName<HorizontalLayoutElement>
        get() = TagName("vaadin-horizontal-layout")

    /**
     * The `<vaadin-icon>` custom element.
     */
    inline val icon: TagName<IconElement>
        get() = TagName("vaadin-icon")

    /**
     * The `<vaadin-iconset>` custom element.
     */
    inline val iconset: TagName<IconsetElement>
        get() = TagName("vaadin-iconset")

    /**
     * The `<vaadin-integer-field>` custom element.
     */
    inline val integerField: TagName<IntegerFieldElement>
        get() = TagName("vaadin-integer-field")

    /**
     * The `<vaadin-item>` custom element.
     */
    inline val item: TagName<ItemElement>
        get() = TagName("vaadin-item")

    /**
     * The `<vaadin-list-box>` custom element.
     */
    inline val listBox: TagName<ListBoxElement>
        get() = TagName("vaadin-list-box")

    /**
     * The `<vaadin-login-form>` custom element.
     */
    inline val loginForm: TagName<LoginFormElement>
        get() = TagName("vaadin-login-form")

    /**
     * The `<vaadin-login-overlay>` custom element.
     */
    inline val loginOverlay: TagName<LoginOverlayElement>
        get() = TagName("vaadin-login-overlay")

    /**
     * The `<vaadin-markdown>` custom element.
     */
    inline val markdown: TagName<MarkdownElement>
        get() = TagName("vaadin-markdown")

    /**
     * The `<vaadin-master-detail-layout>` custom element.
     */
    inline val masterDetailLayout: TagName<MasterDetailLayoutElement>
        get() = TagName("vaadin-master-detail-layout")

    /**
     * The `<vaadin-menu-bar>` custom element.
     */
    inline val menuBar: TagName<MenuBarElement>
        get() = TagName("vaadin-menu-bar")

    /**
     * The `<vaadin-message-input>` custom element.
     */
    inline val messageInput: TagName<MessageInputElement>
        get() = TagName("vaadin-message-input")

    /**
     * The `<vaadin-message-list>` custom element.
     */
    inline val messageList: TagName<MessageListElement>
        get() = TagName("vaadin-message-list")

    /**
     * The `<vaadin-message>` custom element.
     */
    inline val message: TagName<MessageElement>
        get() = TagName("vaadin-message")

    /**
     * The `<vaadin-multi-select-combo-box>` custom element.
     */
    inline val multiSelectComboBox: TagName<MultiSelectComboBoxElement>
        get() = TagName("vaadin-multi-select-combo-box")

    /**
     * The `<vaadin-notification>` custom element.
     */
    inline val notification: TagName<NotificationElement>
        get() = TagName("vaadin-notification")

    /**
     * The `<vaadin-number-field>` custom element.
     */
    inline val numberField: TagName<NumberFieldElement>
        get() = TagName("vaadin-number-field")

    /**
     * The `<vaadin-password-field>` custom element.
     */
    inline val passwordField: TagName<PasswordFieldElement>
        get() = TagName("vaadin-password-field")

    /**
     * The `<vaadin-popover>` custom element.
     */
    inline val popover: TagName<PopoverElement>
        get() = TagName("vaadin-popover")

    /**
     * The `<vaadin-progress-bar>` custom element.
     */
    inline val progressBar: TagName<ProgressBarElement>
        get() = TagName("vaadin-progress-bar")

    /**
     * The `<vaadin-radio-button>` custom element.
     */
    inline val radioButton: TagName<RadioButtonElement>
        get() = TagName("vaadin-radio-button")

    /**
     * The `<vaadin-radio-group>` custom element.
     */
    inline val radioGroup: TagName<RadioGroupElement>
        get() = TagName("vaadin-radio-group")

    /**
     * The `<vaadin-scroller>` custom element.
     */
    inline val scroller: TagName<ScrollerElement>
        get() = TagName("vaadin-scroller")

    /**
     * The `<vaadin-select-item>` custom element.
     */
    inline val selectItem: TagName<SelectItemElement>
        get() = TagName("vaadin-select-item")

    /**
     * The `<vaadin-select-list-box>` custom element.
     */
    inline val selectListBox: TagName<SelectListBoxElement>
        get() = TagName("vaadin-select-list-box")

    /**
     * The `<vaadin-select>` custom element.
     */
    inline val select: TagName<SelectElement>
        get() = TagName("vaadin-select")

    /**
     * The `<vaadin-side-nav-item>` custom element.
     */
    inline val sideNavItem: TagName<SideNavItemElement>
        get() = TagName("vaadin-side-nav-item")

    /**
     * The `<vaadin-side-nav>` custom element.
     */
    inline val sideNav: TagName<SideNavElement>
        get() = TagName("vaadin-side-nav")

    /**
     * The `<vaadin-range-slider>` custom element.
     */
    inline val rangeSlider: TagName<RangeSliderElement>
        get() = TagName("vaadin-range-slider")

    /**
     * The `<vaadin-slider>` custom element.
     */
    inline val slider: TagName<SliderElement>
        get() = TagName("vaadin-slider")

    /**
     * The `<vaadin-split-layout>` custom element.
     */
    inline val splitLayout: TagName<SplitLayoutElement>
        get() = TagName("vaadin-split-layout")

    /**
     * The `<vaadin-switch>` custom element.
     */
    inline val switch: TagName<SwitchElement>
        get() = TagName("vaadin-switch")

    /**
     * The `<vaadin-tab>` custom element.
     */
    inline val tab: TagName<TabElement>
        get() = TagName("vaadin-tab")

    /**
     * The `<vaadin-tabs>` custom element.
     */
    inline val tabs: TagName<TabsElement>
        get() = TagName("vaadin-tabs")

    /**
     * The `<vaadin-tabsheet>` custom element.
     */
    inline val tabsheet: TagName<TabSheetElement>
        get() = TagName("vaadin-tabsheet")

    /**
     * The `<vaadin-text-area>` custom element.
     */
    inline val textArea: TagName<TextAreaElement>
        get() = TagName("vaadin-text-area")

    /**
     * The `<vaadin-text-field>` custom element.
     */
    inline val textField: TagName<TextFieldElement>
        get() = TagName("vaadin-text-field")

    /**
     * The `<vaadin-time-picker>` custom element.
     */
    inline val timePicker: TagName<TimePickerElement>
        get() = TagName("vaadin-time-picker")

    /**
     * The `<vaadin-tooltip>` custom element.
     */
    inline val tooltip: TagName<TooltipElement>
        get() = TagName("vaadin-tooltip")

    /**
     * The `<vaadin-upload-button>` custom element.
     */
    inline val uploadButton: TagName<UploadButtonElement>
        get() = TagName("vaadin-upload-button")

    /**
     * The `<vaadin-upload-drop-zone>` custom element.
     */
    inline val uploadDropZone: TagName<UploadDropZoneElement>
        get() = TagName("vaadin-upload-drop-zone")

    /**
     * The `<vaadin-upload-file-list>` custom element.
     */
    inline val uploadFileList: TagName<UploadFileListElement>
        get() = TagName("vaadin-upload-file-list")

    /**
     * The `<vaadin-upload-file>` custom element.
     */
    inline val uploadFile: TagName<UploadFileElement>
        get() = TagName("vaadin-upload-file")

    /**
     * The `<vaadin-upload>` custom element.
     */
    inline val upload: TagName<UploadElement>
        get() = TagName("vaadin-upload")

    /**
     * The `<vaadin-vertical-layout>` custom element.
     */
    inline val verticalLayout: TagName<VerticalLayoutElement>
        get() = TagName("vaadin-vertical-layout")

    /**
     * The `<vaadin-virtual-list>` custom element.
     */
    inline val virtualList: TagName<VirtualListElement>
        get() = TagName("vaadin-virtual-list")
}
