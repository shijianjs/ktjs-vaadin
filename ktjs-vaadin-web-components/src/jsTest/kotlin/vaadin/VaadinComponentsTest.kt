package vaadin

import kotlin.js.js
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import web.dom.document
import web.html.HTMLElement
import web.html.HTMLUnknownElement

/**
 * Registers and creates every wrapped custom element, then checks that each documented
 * property and method reaches the instance.
 */
class VaadinComponentsTest {
    private fun assertRegistered(tag: String, element: HTMLElement) {
        assertEquals(tag, element.tagName.lowercase())
        assertFalse(document.createElement(tag) is HTMLUnknownElement, "$tag is not registered")
    }

    private fun assertMember(element: HTMLElement, name: String) {
        assertTrue(js("(element, name) => name in element")(element, name) as Boolean, "$name is missing")
    }

    private fun assertMethod(element: HTMLElement, name: String) {
        assertTrue(
            js("(element, name) => typeof element[name] === 'function'")(element, name) as Boolean,
            "$name is not a method",
        )
    }

    private fun assertStaticMember(ctor: dynamic, name: String) {
        assertTrue(js("(ctor, name) => name in ctor")(ctor, name) as Boolean, "static $name is missing")
    }

    private fun assertStaticMethod(ctor: dynamic, name: String) {
        assertTrue(
            js("(ctor, name) => typeof ctor[name] === 'function'")(ctor, name) as Boolean,
            "static $name is not a method",
        )
    }

    @Test
    fun accordionHeading() {
        val ctor = registerAccordionHeading()
        val element = createAccordionHeading()
        assertRegistered("vaadin-accordion-heading", element)
        assertMember(element, "disabled")
        assertMember(element, "opened")
    }

    @Test
    fun accordionPanel() {
        val ctor = registerAccordionPanel()
        val element = createAccordionPanel()
        assertRegistered("vaadin-accordion-panel", element)
        assertMember(element, "autofocus")
        assertMember(element, "disabled")
        assertMember(element, "opened")
        assertMember(element, "summary")
    }

    @Test
    fun accordion() {
        val ctor = registerAccordion()
        val element = createAccordion()
        assertRegistered("vaadin-accordion", element)
        assertMember(element, "headingLevel")
        assertMember(element, "items")
        assertMember(element, "opened")
    }

    @Test
    fun appLayout() {
        val ctor = registerAppLayout()
        val element = createAppLayout()
        assertRegistered("vaadin-app-layout", element)
        assertMember(element, "closeDrawerOn")
        assertMember(element, "drawerOpened")
        assertMember(element, "i18n")
        assertMember(element, "overlay")
        assertMember(element, "primarySection")
        assertStaticMethod(ctor, "dispatchCloseOverlayDrawerEvent")
    }

    @Test
    fun drawerToggle() {
        val ctor = registerDrawerToggle()
        val element = createDrawerToggle()
        assertRegistered("vaadin-drawer-toggle", element)
        assertMember(element, "ariaLabel")
        assertMember(element, "disabled")
    }

    @Test
    fun avatar() {
        val ctor = registerAvatar()
        val element = createAvatar()
        assertRegistered("vaadin-avatar", element)
        assertMember(element, "abbr")
        assertMember(element, "colorIndex")
        assertMember(element, "i18n")
        assertMember(element, "img")
        assertMember(element, "name")
        assertMember(element, "withTooltip")
    }

    @Test
    fun avatarGroup() {
        val ctor = registerAvatarGroup()
        val element = createAvatarGroup()
        assertRegistered("vaadin-avatar-group", element)
        assertMember(element, "i18n")
        assertMember(element, "items")
        assertMember(element, "maxItemsVisible")
    }

    @Test
    fun badge() {
        val ctor = registerBadge()
        val element = createBadge()
        assertRegistered("vaadin-badge", element)
        assertMember(element, "number")
    }

    @Test
    fun breadcrumbsItem() {
        val ctor = registerBreadcrumbsItem()
        val element = createBreadcrumbsItem()
        assertRegistered("vaadin-breadcrumbs-item", element)
        assertMember(element, "current")
        assertMember(element, "disabled")
        assertMember(element, "path")
    }

    @Test
    fun breadcrumbs() {
        val ctor = registerBreadcrumbs()
        val element = createBreadcrumbs()
        assertRegistered("vaadin-breadcrumbs", element)
        assertMember(element, "i18n")
    }

    @Test
    fun button() {
        val ctor = registerButton()
        val element = createButton()
        assertRegistered("vaadin-button", element)
        assertMember(element, "disabled")
    }

    @Test
    fun card() {
        val ctor = registerCard()
        val element = createCard()
        assertRegistered("vaadin-card", element)
        assertMember(element, "cardTitle")
        assertMember(element, "titleHeadingLevel")
    }

    @Test
    fun checkbox() {
        val ctor = registerCheckbox()
        val element = createCheckbox()
        assertRegistered("vaadin-checkbox", element)
        assertMember(element, "accessibleDescriptionRef")
        assertMember(element, "accessibleName")
        assertMember(element, "accessibleNameRef")
        assertMember(element, "autofocus")
        assertMember(element, "checked")
        assertMember(element, "disabled")
        assertMember(element, "errorMessage")
        assertMember(element, "helperText")
        assertMember(element, "indeterminate")
        assertMember(element, "invalid")
        assertMember(element, "label")
        assertMember(element, "manualValidation")
        assertMember(element, "name")
        assertMember(element, "readonly")
        assertMember(element, "required")
        assertMember(element, "value")
        assertMethod(element, "checkValidity")
        assertMethod(element, "clear")
        assertMethod(element, "validate")
    }

    @Test
    fun checkboxGroup() {
        val ctor = registerCheckboxGroup()
        val element = createCheckboxGroup()
        assertRegistered("vaadin-checkbox-group", element)
        assertMember(element, "accessibleDescriptionRef")
        assertMember(element, "accessibleName")
        assertMember(element, "accessibleNameRef")
        assertMember(element, "disabled")
        assertMember(element, "errorMessage")
        assertMember(element, "helperText")
        assertMember(element, "invalid")
        assertMember(element, "label")
        assertMember(element, "manualValidation")
        assertMember(element, "readonly")
        assertMember(element, "required")
        assertMember(element, "value")
        assertMethod(element, "checkValidity")
        assertMethod(element, "validate")
    }

    @Test
    fun comboBox() {
        val ctor = registerComboBox()
        val element = createComboBox()
        assertRegistered("vaadin-combo-box", element)
        assertMember(element, "accessibleDescriptionRef")
        assertMember(element, "accessibleName")
        assertMember(element, "accessibleNameRef")
        assertMember(element, "allowCustomValue")
        assertMember(element, "allowedCharPattern")
        assertMember(element, "autofocus")
        assertMember(element, "autoOpenDisabled")
        assertMember(element, "autoselect")
        assertMember(element, "clearButtonVisible")
        assertMember(element, "dataProvider")
        assertMember(element, "disabled")
        assertMember(element, "errorMessage")
        assertMember(element, "filter")
        assertMember(element, "filteredItems")
        assertMember(element, "helperText")
        assertMember(element, "invalid")
        assertMember(element, "itemClassNameGenerator")
        assertMember(element, "itemIdPath")
        assertMember(element, "itemLabelGenerator")
        assertMember(element, "itemLabelPath")
        assertMember(element, "items")
        assertMember(element, "itemValuePath")
        assertMember(element, "label")
        assertMember(element, "loading")
        assertMember(element, "manualValidation")
        assertMember(element, "name")
        assertMember(element, "opened")
        assertMember(element, "pageSize")
        assertMember(element, "partialMatchMode")
        assertMember(element, "pattern")
        assertMember(element, "placeholder")
        assertMember(element, "readonly")
        assertMember(element, "renderer")
        assertMember(element, "required")
        assertMember(element, "selectedItem")
        assertMember(element, "size")
        assertMember(element, "title")
        assertMember(element, "value")
        assertMethod(element, "cancel")
        assertMethod(element, "checkValidity")
        assertMethod(element, "clear")
        assertMethod(element, "clearCache")
        assertMethod(element, "close")
        assertMethod(element, "open")
        assertMethod(element, "requestContentUpdate")
        assertMethod(element, "validate")
    }

    @Test
    fun confirmDialog() {
        val ctor = registerConfirmDialog()
        val element = createConfirmDialog()
        assertRegistered("vaadin-confirm-dialog", element)
        assertMember(element, "accessibleDescriptionRef")
        assertMember(element, "cancelButtonVisible")
        assertMember(element, "cancelText")
        assertMember(element, "cancelTheme")
        assertMember(element, "confirmText")
        assertMember(element, "confirmTheme")
        assertMember(element, "header")
        assertMember(element, "height")
        assertMember(element, "message")
        assertMember(element, "noCloseOnEsc")
        assertMember(element, "opened")
        assertMember(element, "rejectButtonVisible")
        assertMember(element, "rejectText")
        assertMember(element, "rejectTheme")
        assertMember(element, "width")
    }

    @Test
    fun contextMenuItem() {
        val ctor = registerContextMenuItem()
        val element = createContextMenuItem()
        assertRegistered("vaadin-context-menu-item", element)
        assertMember(element, "disabled")
        assertMember(element, "selected")
        assertMember(element, "value")
    }

    @Test
    fun contextMenuListBox() {
        val ctor = registerContextMenuListBox()
        val element = createContextMenuListBox()
        assertRegistered("vaadin-context-menu-list-box", element)
        assertMember(element, "disabled")
        assertMember(element, "items")
        assertMember(element, "orientation")
        assertMember(element, "selected")
    }

    @Test
    fun contextMenu() {
        val ctor = registerContextMenu()
        val element = createContextMenu()
        assertRegistered("vaadin-context-menu", element)
        assertMember(element, "closeOn")
        assertMember(element, "items")
        assertMember(element, "listenOn")
        assertMember(element, "opened")
        assertMember(element, "openOn")
        assertMember(element, "position")
        assertMember(element, "renderer")
        assertMember(element, "selector")
        assertMethod(element, "close")
        assertMethod(element, "open")
        assertMethod(element, "requestContentUpdate")
    }

    @Test
    fun customField() {
        val ctor = registerCustomField()
        val element = createCustomField()
        assertRegistered("vaadin-custom-field", element)
        assertMember(element, "accessibleDescriptionRef")
        assertMember(element, "accessibleName")
        assertMember(element, "accessibleNameRef")
        assertMember(element, "errorMessage")
        assertMember(element, "formatValue")
        assertMember(element, "helperText")
        assertMember(element, "inputs")
        assertMember(element, "invalid")
        assertMember(element, "label")
        assertMember(element, "manualValidation")
        assertMember(element, "name")
        assertMember(element, "parseValue")
        assertMember(element, "required")
        assertMember(element, "value")
        assertMethod(element, "checkValidity")
        assertMethod(element, "validate")
    }

    @Test
    fun datePicker() {
        val ctor = registerDatePicker()
        val element = createDatePicker()
        assertRegistered("vaadin-date-picker", element)
        assertMember(element, "accessibleDescriptionRef")
        assertMember(element, "accessibleName")
        assertMember(element, "accessibleNameRef")
        assertMember(element, "allowedCharPattern")
        assertMember(element, "autofocus")
        assertMember(element, "autoOpenDisabled")
        assertMember(element, "autoselect")
        assertMember(element, "clearButtonVisible")
        assertMember(element, "dateMetadataProvider")
        assertMember(element, "disabled")
        assertMember(element, "errorMessage")
        assertMember(element, "helperText")
        assertMember(element, "i18n")
        assertMember(element, "initialPosition")
        assertMember(element, "invalid")
        assertMember(element, "isDateDisabled")
        assertMember(element, "label")
        assertMember(element, "manualValidation")
        assertMember(element, "max")
        assertMember(element, "min")
        assertMember(element, "name")
        assertMember(element, "opened")
        assertMember(element, "placeholder")
        assertMember(element, "readonly")
        assertMember(element, "required")
        assertMember(element, "showWeekNumbers")
        assertMember(element, "title")
        assertMember(element, "value")
        assertMethod(element, "checkValidity")
        assertMethod(element, "clear")
        assertMethod(element, "clearCache")
        assertMethod(element, "close")
        assertMethod(element, "open")
        assertMethod(element, "validate")
    }

    @Test
    fun dateTimePicker() {
        val ctor = registerDateTimePicker()
        val element = createDateTimePicker()
        assertRegistered("vaadin-date-time-picker", element)
        assertMember(element, "accessibleDescriptionRef")
        assertMember(element, "accessibleName")
        assertMember(element, "accessibleNameRef")
        assertMember(element, "autofocus")
        assertMember(element, "autoOpenDisabled")
        assertMember(element, "dateMetadataProvider")
        assertMember(element, "datePlaceholder")
        assertMember(element, "defaultTime")
        assertMember(element, "disabled")
        assertMember(element, "errorMessage")
        assertMember(element, "helperText")
        assertMember(element, "i18n")
        assertMember(element, "initialPosition")
        assertMember(element, "invalid")
        assertMember(element, "label")
        assertMember(element, "manualValidation")
        assertMember(element, "max")
        assertMember(element, "min")
        assertMember(element, "name")
        assertMember(element, "readonly")
        assertMember(element, "required")
        assertMember(element, "showWeekNumbers")
        assertMember(element, "step")
        assertMember(element, "timePlaceholder")
        assertMember(element, "value")
        assertMethod(element, "checkValidity")
        assertMethod(element, "clearCache")
        assertMethod(element, "validate")
    }

    @Test
    fun detailsSummary() {
        val ctor = registerDetailsSummary()
        val element = createDetailsSummary()
        assertRegistered("vaadin-details-summary", element)
        assertMember(element, "disabled")
        assertMember(element, "opened")
    }

    @Test
    fun details() {
        val ctor = registerDetails()
        val element = createDetails()
        assertRegistered("vaadin-details", element)
        assertMember(element, "autofocus")
        assertMember(element, "disabled")
        assertMember(element, "opened")
        assertMember(element, "summary")
    }

    @Test
    fun dialog() {
        val ctor = registerDialog()
        val element = createDialog()
        assertRegistered("vaadin-dialog", element)
        assertMember(element, "draggable")
        assertMember(element, "footerRenderer")
        assertMember(element, "headerRenderer")
        assertMember(element, "headerTitle")
        assertMember(element, "height")
        assertMember(element, "keepInViewport")
        assertMember(element, "left")
        assertMember(element, "modeless")
        assertMember(element, "noAutofocus")
        assertMember(element, "noCloseOnEsc")
        assertMember(element, "noCloseOnOutsideClick")
        assertMember(element, "noFocusTrap")
        assertMember(element, "opened")
        assertMember(element, "overlayRole")
        assertMember(element, "renderer")
        assertMember(element, "resizable")
        assertMember(element, "top")
        assertMember(element, "width")
        assertMethod(element, "requestContentUpdate")
    }

    @Test
    fun emailField() {
        val ctor = registerEmailField()
        val element = createEmailField()
        assertRegistered("vaadin-email-field", element)
        assertMember(element, "accessibleDescriptionRef")
        assertMember(element, "accessibleName")
        assertMember(element, "accessibleNameRef")
        assertMember(element, "allowedCharPattern")
        assertMember(element, "autocapitalize")
        assertMember(element, "autocomplete")
        assertMember(element, "autocorrect")
        assertMember(element, "autofocus")
        assertMember(element, "autoselect")
        assertMember(element, "clearButtonVisible")
        assertMember(element, "disabled")
        assertMember(element, "errorMessage")
        assertMember(element, "helperText")
        assertMember(element, "inputMode")
        assertMember(element, "invalid")
        assertMember(element, "label")
        assertMember(element, "manualValidation")
        assertMember(element, "maxlength")
        assertMember(element, "minlength")
        assertMember(element, "name")
        assertMember(element, "pattern")
        assertMember(element, "placeholder")
        assertMember(element, "readonly")
        assertMember(element, "required")
        assertMember(element, "title")
        assertMember(element, "value")
        assertMethod(element, "checkValidity")
        assertMethod(element, "clear")
        assertMethod(element, "validate")
    }

    @Test
    fun formItem() {
        val ctor = registerFormItem()
        val element = createFormItem()
        assertRegistered("vaadin-form-item", element)
    }

    @Test
    fun formLayout() {
        val ctor = registerFormLayout()
        val element = createFormLayout()
        assertRegistered("vaadin-form-layout", element)
        assertMember(element, "autoResponsive")
        assertMember(element, "autoRows")
        assertMember(element, "columnWidth")
        assertMember(element, "expandColumns")
        assertMember(element, "expandFields")
        assertMember(element, "labelsAside")
        assertMember(element, "maxColumns")
        assertMember(element, "minColumns")
        assertMember(element, "responsiveSteps")
    }

    @Test
    fun formRow() {
        val ctor = registerFormRow()
        val element = createFormRow()
        assertRegistered("vaadin-form-row", element)
    }

    @Test
    fun gridColumnGroup() {
        val ctor = registerGridColumnGroup()
        val element = createGridColumnGroup()
        assertRegistered("vaadin-grid-column-group", element)
        assertMember(element, "flexGrow")
        assertMember(element, "footerPartName")
        assertMember(element, "footerRenderer")
        assertMember(element, "frozen")
        assertMember(element, "frozenToEnd")
        assertMember(element, "header")
        assertMember(element, "headerPartName")
        assertMember(element, "headerRenderer")
        assertMember(element, "hidden")
        assertMember(element, "resizable")
        assertMember(element, "rowHeader")
        assertMember(element, "textAlign")
        assertMember(element, "width")
    }

    @Test
    fun gridColumn() {
        val ctor = registerGridColumn()
        val element = createGridColumn()
        assertRegistered("vaadin-grid-column", element)
        assertMember(element, "autoWidth")
        assertMember(element, "flexGrow")
        assertMember(element, "footerPartName")
        assertMember(element, "footerRenderer")
        assertMember(element, "frozen")
        assertMember(element, "frozenToEnd")
        assertMember(element, "header")
        assertMember(element, "headerPartName")
        assertMember(element, "headerRenderer")
        assertMember(element, "hidden")
        assertMember(element, "path")
        assertMember(element, "renderer")
        assertMember(element, "resizable")
        assertMember(element, "rowHeader")
        assertMember(element, "textAlign")
        assertMember(element, "width")
    }

    @Test
    fun gridFilterColumn() {
        val ctor = registerGridFilterColumn()
        val element = createGridFilterColumn()
        assertRegistered("vaadin-grid-filter-column", element)
        assertMember(element, "autoWidth")
        assertMember(element, "flexGrow")
        assertMember(element, "footerPartName")
        assertMember(element, "footerRenderer")
        assertMember(element, "frozen")
        assertMember(element, "frozenToEnd")
        assertMember(element, "header")
        assertMember(element, "headerPartName")
        assertMember(element, "headerRenderer")
        assertMember(element, "hidden")
        assertMember(element, "path")
        assertMember(element, "renderer")
        assertMember(element, "resizable")
        assertMember(element, "rowHeader")
        assertMember(element, "textAlign")
        assertMember(element, "width")
    }

    @Test
    fun gridFilter() {
        val ctor = registerGridFilter()
        val element = createGridFilter()
        assertRegistered("vaadin-grid-filter", element)
        assertMember(element, "path")
        assertMember(element, "value")
        assertMethod(element, "focus")
    }

    @Test
    fun gridSelectionColumn() {
        val ctor = registerGridSelectionColumn()
        val element = createGridSelectionColumn()
        assertRegistered("vaadin-grid-selection-column", element)
        assertMember(element, "autoSelect")
        assertMember(element, "autoWidth")
        assertMember(element, "dragSelect")
        assertMember(element, "flexGrow")
        assertMember(element, "footerPartName")
        assertMember(element, "footerRenderer")
        assertMember(element, "frozen")
        assertMember(element, "frozenToEnd")
        assertMember(element, "header")
        assertMember(element, "headerPartName")
        assertMember(element, "headerRenderer")
        assertMember(element, "hidden")
        assertMember(element, "path")
        assertMember(element, "renderer")
        assertMember(element, "resizable")
        assertMember(element, "rowHeader")
        assertMember(element, "selectAll")
        assertMember(element, "textAlign")
        assertMember(element, "width")
    }

    @Test
    fun gridSortColumn() {
        val ctor = registerGridSortColumn()
        val element = createGridSortColumn()
        assertRegistered("vaadin-grid-sort-column", element)
        assertMember(element, "autoWidth")
        assertMember(element, "direction")
        assertMember(element, "flexGrow")
        assertMember(element, "footerPartName")
        assertMember(element, "footerRenderer")
        assertMember(element, "frozen")
        assertMember(element, "frozenToEnd")
        assertMember(element, "header")
        assertMember(element, "headerPartName")
        assertMember(element, "headerRenderer")
        assertMember(element, "hidden")
        assertMember(element, "path")
        assertMember(element, "renderer")
        assertMember(element, "resizable")
        assertMember(element, "rowHeader")
        assertMember(element, "textAlign")
        assertMember(element, "width")
    }

    @Test
    fun gridSorter() {
        val ctor = registerGridSorter()
        val element = createGridSorter()
        assertRegistered("vaadin-grid-sorter", element)
        assertMember(element, "direction")
        assertMember(element, "path")
    }

    @Test
    fun gridTreeColumn() {
        val ctor = registerGridTreeColumn()
        val element = createGridTreeColumn()
        assertRegistered("vaadin-grid-tree-column", element)
        assertMember(element, "autoWidth")
        assertMember(element, "flexGrow")
        assertMember(element, "footerPartName")
        assertMember(element, "footerRenderer")
        assertMember(element, "frozen")
        assertMember(element, "frozenToEnd")
        assertMember(element, "header")
        assertMember(element, "headerPartName")
        assertMember(element, "headerRenderer")
        assertMember(element, "hidden")
        assertMember(element, "path")
        assertMember(element, "renderer")
        assertMember(element, "resizable")
        assertMember(element, "rowHeader")
        assertMember(element, "textAlign")
        assertMember(element, "width")
    }

    @Test
    fun gridTreeToggle() {
        val ctor = registerGridTreeToggle()
        val element = createGridTreeToggle()
        assertRegistered("vaadin-grid-tree-toggle", element)
        assertMember(element, "expanded")
        assertMember(element, "leaf")
        assertMember(element, "level")
    }

    @Test
    fun grid() {
        val ctor = registerGrid()
        val element = createGrid()
        assertRegistered("vaadin-grid", element)
        assertMember(element, "accessibleName")
        assertMember(element, "activeItem")
        assertMember(element, "allRowsVisible")
        assertMember(element, "cellPartNameGenerator")
        assertMember(element, "columnRendering")
        assertMember(element, "columnReorderingAllowed")
        assertMember(element, "dataProvider")
        assertMember(element, "detailsOpenedItems")
        assertMember(element, "disabled")
        assertMember(element, "dragFilter")
        assertMember(element, "dropFilter")
        assertMember(element, "dropMode")
        assertMember(element, "expandedItems")
        assertMember(element, "i18n")
        assertMember(element, "isItemSelectable")
        assertMember(element, "itemHasChildrenPath")
        assertMember(element, "itemIdPath")
        assertMember(element, "items")
        assertMember(element, "loading")
        assertMember(element, "multiSort")
        assertMember(element, "multiSortOnShiftClick")
        assertMember(element, "multiSortPriority")
        assertMember(element, "pageSize")
        assertMember(element, "rowDetailsRenderer")
        assertMember(element, "rowsDraggable")
        assertMember(element, "selectedItems")
        assertMember(element, "size")
        assertMethod(element, "clearCache")
        assertMethod(element, "closeItemDetails")
        assertMethod(element, "collapseItem")
        assertMethod(element, "deselectItem")
        assertMethod(element, "expandItem")
        assertMethod(element, "filterDragAndDrop")
        assertMethod(element, "generateCellPartNames")
        assertMethod(element, "getEventContext")
        assertMethod(element, "getItemId")
        assertMethod(element, "openItemDetails")
        assertMethod(element, "recalculateColumnWidths")
        assertMethod(element, "requestContentUpdate")
        assertMethod(element, "scrollToColumn")
        assertMethod(element, "scrollToIndex")
        assertMethod(element, "selectItem")
        assertStaticMethod(ctor, "setDefaultMultiSortPriority")
    }

    @Test
    fun horizontalLayout() {
        val ctor = registerHorizontalLayout()
        val element = createHorizontalLayout()
        assertRegistered("vaadin-horizontal-layout", element)
    }

    @Test
    fun icon() {
        val ctor = registerIcon()
        val element = createIcon()
        assertRegistered("vaadin-icon", element)
        assertMember(element, "char")
        assertMember(element, "fontFamily")
        assertMember(element, "icon")
        assertMember(element, "iconClass")
        assertMember(element, "ligature")
        assertMember(element, "size")
        assertMember(element, "src")
        assertMember(element, "svg")
        assertMember(element, "symbol")
    }

    @Test
    fun iconset() {
        val ctor = registerIconset()
        val element = createIconset()
        assertRegistered("vaadin-iconset", element)
        assertMember(element, "name")
        assertMember(element, "size")
        assertStaticMember(ctor, "attachedIcons")
        assertStaticMethod(ctor, "getIconset")
        assertStaticMethod(ctor, "getIconSvg")
        assertStaticMethod(ctor, "register")
    }

    @Test
    fun integerField() {
        val ctor = registerIntegerField()
        val element = createIntegerField()
        assertRegistered("vaadin-integer-field", element)
        assertMember(element, "accessibleDescriptionRef")
        assertMember(element, "accessibleName")
        assertMember(element, "accessibleNameRef")
        assertMember(element, "allowedCharPattern")
        assertMember(element, "autocapitalize")
        assertMember(element, "autocomplete")
        assertMember(element, "autocorrect")
        assertMember(element, "autofocus")
        assertMember(element, "autoselect")
        assertMember(element, "clearButtonVisible")
        assertMember(element, "disabled")
        assertMember(element, "errorMessage")
        assertMember(element, "helperText")
        assertMember(element, "invalid")
        assertMember(element, "label")
        assertMember(element, "manualValidation")
        assertMember(element, "max")
        assertMember(element, "min")
        assertMember(element, "name")
        assertMember(element, "placeholder")
        assertMember(element, "readonly")
        assertMember(element, "required")
        assertMember(element, "step")
        assertMember(element, "stepButtonsVisible")
        assertMember(element, "title")
        assertMember(element, "value")
        assertMethod(element, "checkValidity")
        assertMethod(element, "clear")
        assertMethod(element, "validate")
    }

    @Test
    fun item() {
        val ctor = registerItem()
        val element = createItem()
        assertRegistered("vaadin-item", element)
        assertMember(element, "disabled")
        assertMember(element, "selected")
        assertMember(element, "value")
    }

    @Test
    fun listBox() {
        val ctor = registerListBox()
        val element = createListBox()
        assertRegistered("vaadin-list-box", element)
        assertMember(element, "disabled")
        assertMember(element, "items")
        assertMember(element, "multiple")
        assertMember(element, "orientation")
        assertMember(element, "selected")
        assertMember(element, "selectedValues")
    }

    @Test
    fun loginForm() {
        val ctor = registerLoginForm()
        val element = createLoginForm()
        assertRegistered("vaadin-login-form", element)
        assertMember(element, "action")
        assertMember(element, "disabled")
        assertMember(element, "error")
        assertMember(element, "headingLevel")
        assertMember(element, "i18n")
        assertMember(element, "noAutofocus")
        assertMember(element, "noForgotPassword")
        assertMethod(element, "submit")
    }

    @Test
    fun loginOverlay() {
        val ctor = registerLoginOverlay()
        val element = createLoginOverlay()
        assertRegistered("vaadin-login-overlay", element)
        assertMember(element, "action")
        assertMember(element, "description")
        assertMember(element, "disabled")
        assertMember(element, "error")
        assertMember(element, "headingLevel")
        assertMember(element, "i18n")
        assertMember(element, "noAutofocus")
        assertMember(element, "noForgotPassword")
        assertMember(element, "opened")
        assertMember(element, "title")
        assertMethod(element, "submit")
    }

    @Test
    fun markdown() {
        val ctor = registerMarkdown()
        val element = createMarkdown()
        assertRegistered("vaadin-markdown", element)
        assertMember(element, "content")
        assertMember(element, "lineBreaks")
    }

    @Test
    fun masterDetailLayout() {
        val ctor = registerMasterDetailLayout()
        val element = createMasterDetailLayout()
        assertRegistered("vaadin-master-detail-layout", element)
        assertMember(element, "detailSize")
        assertMember(element, "expandDetail")
        assertMember(element, "expandMaster")
        assertMember(element, "forceOverlay")
        assertMember(element, "masterSize")
        assertMember(element, "noAnimation")
        assertMember(element, "orientation")
        assertMember(element, "overlayContainment")
        assertMember(element, "overlaySize")
        assertMethod(element, "recalculateLayout")
    }

    @Test
    fun menuBar() {
        val ctor = registerMenuBar()
        val element = createMenuBar()
        assertRegistered("vaadin-menu-bar", element)
        assertMember(element, "disabled")
        assertMember(element, "i18n")
        assertMember(element, "items")
        assertMember(element, "openOnHover")
        assertMember(element, "reverseCollapse")
        assertMember(element, "tabNavigation")
        assertMethod(element, "close")
    }

    @Test
    fun messageInput() {
        val ctor = registerMessageInput()
        val element = createMessageInput()
        assertRegistered("vaadin-message-input", element)
        assertMember(element, "disabled")
        assertMember(element, "i18n")
        assertMember(element, "value")
    }

    @Test
    fun messageList() {
        val ctor = registerMessageList()
        val element = createMessageList()
        assertRegistered("vaadin-message-list", element)
        assertMember(element, "announceMessages")
        assertMember(element, "items")
        assertMember(element, "markdown")
    }

    @Test
    fun message() {
        val ctor = registerMessage()
        val element = createMessage()
        assertRegistered("vaadin-message", element)
        assertMember(element, "attachments")
        assertMember(element, "time")
        assertMember(element, "userAbbr")
        assertMember(element, "userColorIndex")
        assertMember(element, "userImg")
        assertMember(element, "userName")
    }

    @Test
    fun multiSelectComboBox() {
        val ctor = registerMultiSelectComboBox()
        val element = createMultiSelectComboBox()
        assertRegistered("vaadin-multi-select-combo-box", element)
        assertMember(element, "accessibleDescriptionRef")
        assertMember(element, "accessibleName")
        assertMember(element, "accessibleNameRef")
        assertMember(element, "allowCustomValue")
        assertMember(element, "allowedCharPattern")
        assertMember(element, "autoExpandHorizontally")
        assertMember(element, "autoExpandVertically")
        assertMember(element, "autofocus")
        assertMember(element, "autoOpenDisabled")
        assertMember(element, "autoselect")
        assertMember(element, "clearButtonVisible")
        assertMember(element, "collapseChips")
        assertMember(element, "dataProvider")
        assertMember(element, "disabled")
        assertMember(element, "errorMessage")
        assertMember(element, "filter")
        assertMember(element, "filteredItems")
        assertMember(element, "helperText")
        assertMember(element, "i18n")
        assertMember(element, "invalid")
        assertMember(element, "itemClassNameGenerator")
        assertMember(element, "itemIdPath")
        assertMember(element, "itemLabelGenerator")
        assertMember(element, "itemLabelPath")
        assertMember(element, "items")
        assertMember(element, "itemValuePath")
        assertMember(element, "keepFilter")
        assertMember(element, "label")
        assertMember(element, "loading")
        assertMember(element, "manualValidation")
        assertMember(element, "name")
        assertMember(element, "opened")
        assertMember(element, "pageSize")
        assertMember(element, "partialMatchMode")
        assertMember(element, "placeholder")
        assertMember(element, "readonly")
        assertMember(element, "renderer")
        assertMember(element, "required")
        assertMember(element, "selectedItems")
        assertMember(element, "selectedItemsOnTop")
        assertMember(element, "size")
        assertMember(element, "title")
        assertMethod(element, "checkValidity")
        assertMethod(element, "clear")
        assertMethod(element, "clearCache")
        assertMethod(element, "close")
        assertMethod(element, "open")
        assertMethod(element, "requestContentUpdate")
        assertMethod(element, "validate")
    }

    @Test
    fun notification() {
        val ctor = registerNotification()
        val element = createNotification()
        assertRegistered("vaadin-notification", element)
        assertMember(element, "assertive")
        assertMember(element, "duration")
        assertMember(element, "opened")
        assertMember(element, "overlayClass")
        assertMember(element, "position")
        assertMember(element, "renderer")
        assertMethod(element, "close")
        assertMethod(element, "open")
        assertMethod(element, "requestContentUpdate")
        assertStaticMethod(ctor, "show")
    }

    @Test
    fun numberField() {
        val ctor = registerNumberField()
        val element = createNumberField()
        assertRegistered("vaadin-number-field", element)
        assertMember(element, "accessibleDescriptionRef")
        assertMember(element, "accessibleName")
        assertMember(element, "accessibleNameRef")
        assertMember(element, "allowedCharPattern")
        assertMember(element, "autocapitalize")
        assertMember(element, "autocomplete")
        assertMember(element, "autocorrect")
        assertMember(element, "autofocus")
        assertMember(element, "autoselect")
        assertMember(element, "clearButtonVisible")
        assertMember(element, "disabled")
        assertMember(element, "errorMessage")
        assertMember(element, "helperText")
        assertMember(element, "invalid")
        assertMember(element, "label")
        assertMember(element, "manualValidation")
        assertMember(element, "max")
        assertMember(element, "min")
        assertMember(element, "name")
        assertMember(element, "placeholder")
        assertMember(element, "readonly")
        assertMember(element, "required")
        assertMember(element, "step")
        assertMember(element, "stepButtonsVisible")
        assertMember(element, "title")
        assertMember(element, "value")
        assertMethod(element, "checkValidity")
        assertMethod(element, "clear")
        assertMethod(element, "validate")
    }

    @Test
    fun passwordField() {
        val ctor = registerPasswordField()
        val element = createPasswordField()
        assertRegistered("vaadin-password-field", element)
        assertMember(element, "accessibleDescriptionRef")
        assertMember(element, "accessibleName")
        assertMember(element, "accessibleNameRef")
        assertMember(element, "allowedCharPattern")
        assertMember(element, "autocapitalize")
        assertMember(element, "autocomplete")
        assertMember(element, "autocorrect")
        assertMember(element, "autofocus")
        assertMember(element, "autoselect")
        assertMember(element, "clearButtonVisible")
        assertMember(element, "disabled")
        assertMember(element, "errorMessage")
        assertMember(element, "helperText")
        assertMember(element, "i18n")
        assertMember(element, "inputMode")
        assertMember(element, "invalid")
        assertMember(element, "label")
        assertMember(element, "manualValidation")
        assertMember(element, "maxlength")
        assertMember(element, "minlength")
        assertMember(element, "name")
        assertMember(element, "passwordVisible")
        assertMember(element, "pattern")
        assertMember(element, "placeholder")
        assertMember(element, "readonly")
        assertMember(element, "required")
        assertMember(element, "revealButtonHidden")
        assertMember(element, "title")
        assertMember(element, "value")
        assertMethod(element, "checkValidity")
        assertMethod(element, "clear")
        assertMethod(element, "validate")
    }

    @Test
    fun popover() {
        val ctor = registerPopover()
        val element = createPopover()
        assertRegistered("vaadin-popover", element)
        assertMember(element, "accessibleName")
        assertMember(element, "accessibleNameRef")
        assertMember(element, "autofocus")
        assertMember(element, "focusDelay")
        assertMember(element, "for")
        assertMember(element, "height")
        assertMember(element, "hideDelay")
        assertMember(element, "hoverDelay")
        assertMember(element, "modal")
        assertMember(element, "noCloseOnEsc")
        assertMember(element, "noCloseOnOutsideClick")
        assertMember(element, "noTabFocus")
        assertMember(element, "opened")
        assertMember(element, "overlayRole")
        assertMember(element, "position")
        assertMember(element, "renderer")
        assertMember(element, "role")
        assertMember(element, "target")
        assertMember(element, "trigger")
        assertMember(element, "width")
        assertMember(element, "withBackdrop")
        assertMethod(element, "requestContentUpdate")
        assertStaticMethod(ctor, "setDefaultFocusDelay")
        assertStaticMethod(ctor, "setDefaultHideDelay")
        assertStaticMethod(ctor, "setDefaultHoverDelay")
    }

    @Test
    fun progressBar() {
        val ctor = registerProgressBar()
        val element = createProgressBar()
        assertRegistered("vaadin-progress-bar", element)
        assertMember(element, "indeterminate")
        assertMember(element, "max")
        assertMember(element, "min")
        assertMember(element, "value")
    }

    @Test
    fun radioButton() {
        val ctor = registerRadioButton()
        val element = createRadioButton()
        assertRegistered("vaadin-radio-button", element)
        assertMember(element, "autofocus")
        assertMember(element, "checked")
        assertMember(element, "disabled")
        assertMember(element, "label")
        assertMember(element, "name")
        assertMember(element, "value")
        assertMethod(element, "clear")
    }

    @Test
    fun radioGroup() {
        val ctor = registerRadioGroup()
        val element = createRadioGroup()
        assertRegistered("vaadin-radio-group", element)
        assertMember(element, "accessibleDescriptionRef")
        assertMember(element, "accessibleName")
        assertMember(element, "accessibleNameRef")
        assertMember(element, "disabled")
        assertMember(element, "errorMessage")
        assertMember(element, "helperText")
        assertMember(element, "invalid")
        assertMember(element, "label")
        assertMember(element, "manualValidation")
        assertMember(element, "name")
        assertMember(element, "readonly")
        assertMember(element, "required")
        assertMember(element, "value")
        assertMethod(element, "checkValidity")
        assertMethod(element, "validate")
    }

    @Test
    fun scroller() {
        val ctor = registerScroller()
        val element = createScroller()
        assertRegistered("vaadin-scroller", element)
        assertMember(element, "scrollDirection")
    }

    @Test
    fun selectItem() {
        val ctor = registerSelectItem()
        val element = createSelectItem()
        assertRegistered("vaadin-select-item", element)
        assertMember(element, "disabled")
        assertMember(element, "selected")
        assertMember(element, "value")
    }

    @Test
    fun selectListBox() {
        val ctor = registerSelectListBox()
        val element = createSelectListBox()
        assertRegistered("vaadin-select-list-box", element)
        assertMember(element, "disabled")
        assertMember(element, "items")
        assertMember(element, "orientation")
        assertMember(element, "selected")
    }

    @Test
    fun select() {
        val ctor = registerSelect()
        val element = createSelect()
        assertRegistered("vaadin-select", element)
        assertMember(element, "accessibleDescriptionRef")
        assertMember(element, "accessibleName")
        assertMember(element, "accessibleNameRef")
        assertMember(element, "autofocus")
        assertMember(element, "disabled")
        assertMember(element, "errorMessage")
        assertMember(element, "helperText")
        assertMember(element, "invalid")
        assertMember(element, "items")
        assertMember(element, "label")
        assertMember(element, "manualValidation")
        assertMember(element, "name")
        assertMember(element, "noVerticalOverlap")
        assertMember(element, "opened")
        assertMember(element, "placeholder")
        assertMember(element, "readonly")
        assertMember(element, "renderer")
        assertMember(element, "required")
        assertMember(element, "value")
        assertMethod(element, "checkValidity")
        assertMethod(element, "requestContentUpdate")
        assertMethod(element, "validate")
    }

    @Test
    fun sideNavItem() {
        val ctor = registerSideNavItem()
        val element = createSideNavItem()
        assertRegistered("vaadin-side-nav-item", element)
        assertMember(element, "current")
        assertMember(element, "disabled")
        assertMember(element, "expanded")
        assertMember(element, "i18n")
        assertMember(element, "matchNested")
        assertMember(element, "path")
        assertMember(element, "pathAliases")
        assertMember(element, "routerIgnore")
        assertMember(element, "target")
    }

    @Test
    fun sideNav() {
        val ctor = registerSideNav()
        val element = createSideNav()
        assertRegistered("vaadin-side-nav", element)
        assertMember(element, "collapsed")
        assertMember(element, "collapsible")
        assertMember(element, "i18n")
        assertMember(element, "location")
        assertMember(element, "noAutoExpand")
        assertMember(element, "onNavigate")
    }

    @Test
    fun rangeSlider() {
        val ctor = registerRangeSlider()
        val element = createRangeSlider()
        assertRegistered("vaadin-range-slider", element)
        assertMember(element, "accessibleDescriptionRef")
        assertMember(element, "accessibleName")
        assertMember(element, "accessibleNameEnd")
        assertMember(element, "accessibleNameRef")
        assertMember(element, "accessibleNameStart")
        assertMember(element, "disabled")
        assertMember(element, "errorMessage")
        assertMember(element, "helperText")
        assertMember(element, "invalid")
        assertMember(element, "label")
        assertMember(element, "manualValidation")
        assertMember(element, "max")
        assertMember(element, "min")
        assertMember(element, "minMaxVisible")
        assertMember(element, "readonly")
        assertMember(element, "required")
        assertMember(element, "step")
        assertMember(element, "value")
        assertMember(element, "valueAlwaysVisible")
        assertMethod(element, "checkValidity")
        assertMethod(element, "validate")
    }

    @Test
    fun slider() {
        val ctor = registerSlider()
        val element = createSlider()
        assertRegistered("vaadin-slider", element)
        assertMember(element, "accessibleDescriptionRef")
        assertMember(element, "accessibleName")
        assertMember(element, "accessibleNameRef")
        assertMember(element, "disabled")
        assertMember(element, "errorMessage")
        assertMember(element, "helperText")
        assertMember(element, "invalid")
        assertMember(element, "label")
        assertMember(element, "manualValidation")
        assertMember(element, "max")
        assertMember(element, "min")
        assertMember(element, "minMaxVisible")
        assertMember(element, "readonly")
        assertMember(element, "required")
        assertMember(element, "step")
        assertMember(element, "value")
        assertMember(element, "valueAlwaysVisible")
        assertMethod(element, "checkValidity")
        assertMethod(element, "validate")
    }

    @Test
    fun splitLayout() {
        val ctor = registerSplitLayout()
        val element = createSplitLayout()
        assertRegistered("vaadin-split-layout", element)
        assertMember(element, "i18n")
        assertMember(element, "orientation")
    }

    @Test
    fun switch() {
        val ctor = registerSwitch()
        val element = createSwitch()
        assertRegistered("vaadin-switch", element)
        assertMember(element, "accessibleDescriptionRef")
        assertMember(element, "accessibleName")
        assertMember(element, "accessibleNameRef")
        assertMember(element, "autofocus")
        assertMember(element, "checked")
        assertMember(element, "disabled")
        assertMember(element, "errorMessage")
        assertMember(element, "helperText")
        assertMember(element, "invalid")
        assertMember(element, "label")
        assertMember(element, "manualValidation")
        assertMember(element, "name")
        assertMember(element, "readonly")
        assertMember(element, "required")
        assertMember(element, "value")
        assertMethod(element, "checkValidity")
        assertMethod(element, "clear")
        assertMethod(element, "validate")
    }

    @Test
    fun tab() {
        val ctor = registerTab()
        val element = createTab()
        assertRegistered("vaadin-tab", element)
        assertMember(element, "disabled")
        assertMember(element, "selected")
        assertMember(element, "value")
    }

    @Test
    fun tabs() {
        val ctor = registerTabs()
        val element = createTabs()
        assertRegistered("vaadin-tabs", element)
        assertMember(element, "disabled")
        assertMember(element, "items")
        assertMember(element, "orientation")
        assertMember(element, "selected")
    }

    @Test
    fun tabsheet() {
        val ctor = registerTabSheet()
        val element = createTabSheet()
        assertRegistered("vaadin-tabsheet", element)
        assertMember(element, "items")
        assertMember(element, "selected")
    }

    @Test
    fun textArea() {
        val ctor = registerTextArea()
        val element = createTextArea()
        assertRegistered("vaadin-text-area", element)
        assertMember(element, "accessibleDescriptionRef")
        assertMember(element, "accessibleName")
        assertMember(element, "accessibleNameRef")
        assertMember(element, "allowedCharPattern")
        assertMember(element, "autocapitalize")
        assertMember(element, "autocomplete")
        assertMember(element, "autocorrect")
        assertMember(element, "autofocus")
        assertMember(element, "autoselect")
        assertMember(element, "clearButtonVisible")
        assertMember(element, "disabled")
        assertMember(element, "errorMessage")
        assertMember(element, "helperText")
        assertMember(element, "invalid")
        assertMember(element, "label")
        assertMember(element, "manualValidation")
        assertMember(element, "maxlength")
        assertMember(element, "maxRows")
        assertMember(element, "minlength")
        assertMember(element, "minRows")
        assertMember(element, "name")
        assertMember(element, "pattern")
        assertMember(element, "placeholder")
        assertMember(element, "readonly")
        assertMember(element, "required")
        assertMember(element, "title")
        assertMember(element, "value")
        assertMethod(element, "checkValidity")
        assertMethod(element, "clear")
        assertMethod(element, "scrollToEnd")
        assertMethod(element, "scrollToStart")
        assertMethod(element, "validate")
    }

    @Test
    fun textField() {
        val ctor = registerTextField()
        val element = createTextField()
        assertRegistered("vaadin-text-field", element)
        assertMember(element, "accessibleDescriptionRef")
        assertMember(element, "accessibleName")
        assertMember(element, "accessibleNameRef")
        assertMember(element, "allowedCharPattern")
        assertMember(element, "autocapitalize")
        assertMember(element, "autocomplete")
        assertMember(element, "autocorrect")
        assertMember(element, "autofocus")
        assertMember(element, "autoselect")
        assertMember(element, "clearButtonVisible")
        assertMember(element, "disabled")
        assertMember(element, "errorMessage")
        assertMember(element, "helperText")
        assertMember(element, "inputMode")
        assertMember(element, "invalid")
        assertMember(element, "label")
        assertMember(element, "manualValidation")
        assertMember(element, "maxlength")
        assertMember(element, "minlength")
        assertMember(element, "name")
        assertMember(element, "pattern")
        assertMember(element, "placeholder")
        assertMember(element, "readonly")
        assertMember(element, "required")
        assertMember(element, "title")
        assertMember(element, "value")
        assertMethod(element, "checkValidity")
        assertMethod(element, "clear")
        assertMethod(element, "validate")
    }

    @Test
    fun timePicker() {
        val ctor = registerTimePicker()
        val element = createTimePicker()
        assertRegistered("vaadin-time-picker", element)
        assertMember(element, "accessibleDescriptionRef")
        assertMember(element, "accessibleName")
        assertMember(element, "accessibleNameRef")
        assertMember(element, "allowedCharPattern")
        assertMember(element, "autofocus")
        assertMember(element, "autoOpenDisabled")
        assertMember(element, "autoselect")
        assertMember(element, "clearButtonVisible")
        assertMember(element, "disabled")
        assertMember(element, "errorMessage")
        assertMember(element, "helperText")
        assertMember(element, "i18n")
        assertMember(element, "invalid")
        assertMember(element, "label")
        assertMember(element, "manualValidation")
        assertMember(element, "max")
        assertMember(element, "min")
        assertMember(element, "name")
        assertMember(element, "opened")
        assertMember(element, "pattern")
        assertMember(element, "placeholder")
        assertMember(element, "readonly")
        assertMember(element, "required")
        assertMember(element, "step")
        assertMember(element, "title")
        assertMember(element, "value")
        assertMethod(element, "checkValidity")
        assertMethod(element, "clear")
        assertMethod(element, "close")
        assertMethod(element, "open")
        assertMethod(element, "validate")
    }

    @Test
    fun tooltip() {
        val ctor = registerTooltip()
        val element = createTooltip()
        assertRegistered("vaadin-tooltip", element)
        assertMember(element, "ariaLinkMode")
        assertMember(element, "ariaTarget")
        assertMember(element, "context")
        assertMember(element, "focusDelay")
        assertMember(element, "for")
        assertMember(element, "generator")
        assertMember(element, "hideDelay")
        assertMember(element, "hoverDelay")
        assertMember(element, "manual")
        assertMember(element, "markdown")
        assertMember(element, "opened")
        assertMember(element, "position")
        assertMember(element, "shouldShow")
        assertMember(element, "target")
        assertMember(element, "text")
        assertStaticMethod(ctor, "setDefaultFocusDelay")
        assertStaticMethod(ctor, "setDefaultHideDelay")
        assertStaticMethod(ctor, "setDefaultHoverDelay")
    }

    @Test
    fun uploadButton() {
        val ctor = registerUploadButton()
        val element = createUploadButton()
        assertRegistered("vaadin-upload-button", element)
        assertMember(element, "capture")
        assertMember(element, "disabled")
        assertMember(element, "manager")
        assertMember(element, "maxFilesReached")
        assertMethod(element, "openFilePicker")
    }

    @Test
    fun uploadDropZone() {
        val ctor = registerUploadDropZone()
        val element = createUploadDropZone()
        assertRegistered("vaadin-upload-drop-zone", element)
        assertMember(element, "disabled")
        assertMember(element, "manager")
        assertMember(element, "maxFilesReached")
    }

    @Test
    fun uploadFileList() {
        val ctor = registerUploadFileList()
        val element = createUploadFileList()
        assertRegistered("vaadin-upload-file-list", element)
        assertMember(element, "disabled")
        assertMember(element, "i18n")
        assertMember(element, "items")
        assertMember(element, "manager")
    }

    @Test
    fun uploadFile() {
        val ctor = registerUploadFile()
        val element = createUploadFile()
        assertRegistered("vaadin-upload-file", element)
        assertMember(element, "complete")
        assertMember(element, "disabled")
        assertMember(element, "errorMessage")
        assertMember(element, "file")
        assertMember(element, "fileName")
        assertMember(element, "held")
        assertMember(element, "i18n")
        assertMember(element, "indeterminate")
        assertMember(element, "progress")
        assertMember(element, "status")
        assertMember(element, "uploading")
    }

    @Test
    fun upload() {
        val ctor = registerUpload()
        val element = createUpload()
        assertRegistered("vaadin-upload", element)
        assertMember(element, "accept")
        assertMember(element, "capture")
        assertMember(element, "disabled")
        assertMember(element, "files")
        assertMember(element, "formDataName")
        assertMember(element, "headers")
        assertMember(element, "i18n")
        assertMember(element, "maxConcurrentUploads")
        assertMember(element, "maxFiles")
        assertMember(element, "maxFileSize")
        assertMember(element, "maxFilesReached")
        assertMember(element, "method")
        assertMember(element, "noAuto")
        assertMember(element, "nodrop")
        assertMember(element, "target")
        assertMember(element, "timeout")
        assertMember(element, "uploadFormat")
        assertMember(element, "withCredentials")
        assertMethod(element, "uploadFiles")
    }

    @Test
    fun verticalLayout() {
        val ctor = registerVerticalLayout()
        val element = createVerticalLayout()
        assertRegistered("vaadin-vertical-layout", element)
    }

    @Test
    fun virtualList() {
        val ctor = registerVirtualList()
        val element = createVirtualList()
        assertRegistered("vaadin-virtual-list", element)
        assertMember(element, "firstVisibleIndex")
        assertMember(element, "itemAccessibleNameGenerator")
        assertMember(element, "items")
        assertMember(element, "lastVisibleIndex")
        assertMember(element, "renderer")
        assertMethod(element, "requestContentUpdate")
        assertMethod(element, "scrollToIndex")
    }
}
