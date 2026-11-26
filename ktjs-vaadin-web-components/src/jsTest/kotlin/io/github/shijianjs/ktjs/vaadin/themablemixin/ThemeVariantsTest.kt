package io.github.shijianjs.ktjs.vaadin.themablemixin

import io.github.shijianjs.ktjs.vaadin.avatar.AvatarVariants
import io.github.shijianjs.ktjs.vaadin.avatargroup.AvatarGroupVariants
import io.github.shijianjs.ktjs.vaadin.badge.BadgeVariants
import io.github.shijianjs.ktjs.vaadin.breadcrumbs.BreadcrumbsVariants
import io.github.shijianjs.ktjs.vaadin.button.ButtonVariants
import io.github.shijianjs.ktjs.vaadin.card.CardVariants
import io.github.shijianjs.ktjs.vaadin.checkbox.CheckboxVariants
import io.github.shijianjs.ktjs.vaadin.checkboxgroup.CheckboxGroupVariants
import io.github.shijianjs.ktjs.vaadin.combobox.ComboBoxVariants
import io.github.shijianjs.ktjs.vaadin.customfield.CustomFieldVariants
import io.github.shijianjs.ktjs.vaadin.datepicker.DatePickerVariants
import io.github.shijianjs.ktjs.vaadin.datetimepicker.DateTimePickerVariants
import io.github.shijianjs.ktjs.vaadin.details.DetailsVariants
import io.github.shijianjs.ktjs.vaadin.dialog.DialogVariants
import io.github.shijianjs.ktjs.vaadin.grid.GridVariants
import io.github.shijianjs.ktjs.vaadin.horizontallayout.HorizontalLayoutVariants
import io.github.shijianjs.ktjs.vaadin.menubar.MenuBarVariants
import io.github.shijianjs.ktjs.vaadin.messageinput.MessageInputVariants
import io.github.shijianjs.ktjs.vaadin.messagelist.MessageListItemVariants
import io.github.shijianjs.ktjs.vaadin.messagelist.MessageListVariants
import io.github.shijianjs.ktjs.vaadin.multiselectcombobox.MultiSelectComboBoxVariants
import io.github.shijianjs.ktjs.vaadin.notification.NotificationVariants
import io.github.shijianjs.ktjs.vaadin.popover.PopoverVariants
import io.github.shijianjs.ktjs.vaadin.progressbar.ProgressBarVariants
import io.github.shijianjs.ktjs.vaadin.radiogroup.RadioGroupVariants
import io.github.shijianjs.ktjs.vaadin.scroller.ScrollerVariants
import io.github.shijianjs.ktjs.vaadin.select.SelectVariants
import io.github.shijianjs.ktjs.vaadin.sidenav.SideNavVariants
import io.github.shijianjs.ktjs.vaadin.slider.SliderVariants
import io.github.shijianjs.ktjs.vaadin.splitlayout.SplitLayoutVariants
import io.github.shijianjs.ktjs.vaadin.switch.SwitchVariants
import io.github.shijianjs.ktjs.vaadin.tabs.TabsVariants
import io.github.shijianjs.ktjs.vaadin.tabs.TabVariants
import io.github.shijianjs.ktjs.vaadin.tabsheet.TabSheetVariants
import io.github.shijianjs.ktjs.vaadin.textarea.TextAreaVariants
import io.github.shijianjs.ktjs.vaadin.textfield.TextFieldVariants
import io.github.shijianjs.ktjs.vaadin.timepicker.TimePickerVariants
import io.github.shijianjs.ktjs.vaadin.upload.UploadFileListVariants
import io.github.shijianjs.ktjs.vaadin.upload.UploadVariants
import io.github.shijianjs.ktjs.vaadin.verticallayout.VerticalLayoutVariants
import io.github.shijianjs.ktjs.vaadin.virtuallist.VirtualListVariants
import io.github.shijianjs.ktjs.vaadin.theme.ThemeVariant
import io.github.shijianjs.ktjs.vaadin.theme.name
import io.github.shijianjs.ktjs.vaadin.button.createButton
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * The tokens are generated from Vaadin Flow's Java enums; these assertions are the transcription
 * check — a typo in either side shows up here instead of in a browser.
 */
class ThemeVariantsTest {

    @Test
    fun `theMixinAttributeIsTheThemeAttribute`() {
        assertEquals("theme", createButton().themeAttr.name)
    }

    @Test
    fun `avatarGroupTokensMatchUpstream`() {
        assertEquals("xlarge", AvatarGroupVariants.xlarge.name)
        assertEquals("large", AvatarGroupVariants.large.name)
        assertEquals("small", AvatarGroupVariants.small.name)
        assertEquals("xsmall", AvatarGroupVariants.xsmall.name)
        assertEquals("reverse", AvatarGroupVariants.reverse.name)
        assertEquals("filled", AvatarGroupVariants.filled.name)
    }

    @Test
    fun `avatarTokensMatchUpstream`() {
        assertEquals("xlarge", AvatarVariants.xlarge.name)
        assertEquals("large", AvatarVariants.large.name)
        assertEquals("small", AvatarVariants.small.name)
        assertEquals("xsmall", AvatarVariants.xsmall.name)
        assertEquals("filled", AvatarVariants.filled.name)
    }

    @Test
    fun `badgeTokensMatchUpstream`() {
        assertEquals("contrast", BadgeVariants.contrast.name)
        assertEquals("dot", BadgeVariants.dot.name)
        assertEquals("error", BadgeVariants.error.name)
        assertEquals("filled", BadgeVariants.filled.name)
        assertEquals("icon-only", BadgeVariants.iconOnly.name)
        assertEquals("number-only", BadgeVariants.numberOnly.name)
        assertEquals("success", BadgeVariants.success.name)
        assertEquals("warning", BadgeVariants.warning.name)
        assertEquals("small", BadgeVariants.small.name)
    }

    @Test
    fun `breadcrumbsTokensMatchUpstream`() {
        assertEquals("slash", BreadcrumbsVariants.slash.name)
        assertEquals("primary", BreadcrumbsVariants.primary.name)
        assertEquals("accent", BreadcrumbsVariants.accent.name)
    }

    @Test
    fun `buttonTokensMatchUpstream`() {
        assertEquals("small", ButtonVariants.small.name)
        assertEquals("large", ButtonVariants.large.name)
        assertEquals("tertiary", ButtonVariants.tertiary.name)
        assertEquals("tertiary-inline", ButtonVariants.tertiaryInline.name)
        assertEquals("primary", ButtonVariants.primary.name)
        assertEquals("success", ButtonVariants.success.name)
        assertEquals("warning", ButtonVariants.warning.name)
        assertEquals("error", ButtonVariants.error.name)
        assertEquals("contrast", ButtonVariants.contrast.name)
        assertEquals("icon", ButtonVariants.icon.name)
    }

    @Test
    fun `cardTokensMatchUpstream`() {
        assertEquals("elevated", CardVariants.elevated.name)
        assertEquals("outlined", CardVariants.outlined.name)
        assertEquals("horizontal", CardVariants.horizontal.name)
        assertEquals("stretch-media", CardVariants.stretchMedia.name)
        assertEquals("cover-media", CardVariants.coverMedia.name)
    }

    @Test
    fun `checkboxGroupTokensMatchUpstream`() {
        assertEquals("vertical", CheckboxGroupVariants.vertical.name)
        assertEquals("helper-above-field", CheckboxGroupVariants.helperAbove.name)
        assertEquals("horizontal", CheckboxGroupVariants.horizontal.name)
        assertEquals("label-aside", CheckboxGroupVariants.labelAside.name)
    }

    @Test
    fun `checkboxTokensMatchUpstream`() {
        assertEquals("reverse", CheckboxVariants.reverse.name)
        assertEquals("small", CheckboxVariants.small.name)
    }

    @Test
    fun `switchTokensMatchUpstream`() {
        assertEquals("reverse", SwitchVariants.reverse.name)
        assertEquals("small", SwitchVariants.small.name)
        assertEquals("icon", SwitchVariants.icon.name)
    }

    @Test
    fun `comboBoxTokensMatchUpstream`() {
        assertEquals("small", ComboBoxVariants.small.name)
        assertEquals("align-left", ComboBoxVariants.alignLeft.name)
        assertEquals("align-center", ComboBoxVariants.alignCenter.name)
        assertEquals("align-right", ComboBoxVariants.alignRight.name)
        assertEquals("helper-above-field", ComboBoxVariants.helperAbove.name)
        assertEquals("filled", ComboBoxVariants.filled.name)
        assertEquals("align-start", ComboBoxVariants.alignStart.name)
        assertEquals("align-end", ComboBoxVariants.alignEnd.name)
        assertEquals("label-aside", ComboBoxVariants.labelAside.name)
    }

    @Test
    fun `multiSelectComboBoxTokensMatchUpstream`() {
        assertEquals("small", MultiSelectComboBoxVariants.small.name)
        assertEquals("align-left", MultiSelectComboBoxVariants.alignLeft.name)
        assertEquals("align-center", MultiSelectComboBoxVariants.alignCenter.name)
        assertEquals("align-right", MultiSelectComboBoxVariants.alignRight.name)
        assertEquals("helper-above-field", MultiSelectComboBoxVariants.helperAbove.name)
        assertEquals("filled", MultiSelectComboBoxVariants.filled.name)
        assertEquals("align-start", MultiSelectComboBoxVariants.alignStart.name)
        assertEquals("align-end", MultiSelectComboBoxVariants.alignEnd.name)
        assertEquals("label-aside", MultiSelectComboBoxVariants.labelAside.name)
    }

    @Test
    fun `customFieldTokensMatchUpstream`() {
        assertEquals("small", CustomFieldVariants.small.name)
        assertEquals("helper-above-field", CustomFieldVariants.helperAbove.name)
        assertEquals("label-aside", CustomFieldVariants.labelAside.name)
    }

    @Test
    fun `datePickerTokensMatchUpstream`() {
        assertEquals("small", DatePickerVariants.small.name)
        assertEquals("align-left", DatePickerVariants.alignLeft.name)
        assertEquals("align-center", DatePickerVariants.alignCenter.name)
        assertEquals("align-right", DatePickerVariants.alignRight.name)
        assertEquals("helper-above-field", DatePickerVariants.helperAbove.name)
        assertEquals("align-start", DatePickerVariants.alignStart.name)
        assertEquals("align-end", DatePickerVariants.alignEnd.name)
        assertEquals("label-aside", DatePickerVariants.labelAside.name)
    }

    @Test
    fun `dateTimePickerTokensMatchUpstream`() {
        assertEquals("small", DateTimePickerVariants.small.name)
        assertEquals("align-left", DateTimePickerVariants.alignLeft.name)
        assertEquals("align-center", DateTimePickerVariants.alignCenter.name)
        assertEquals("align-right", DateTimePickerVariants.alignRight.name)
        assertEquals("helper-above-field", DateTimePickerVariants.helperAbove.name)
        assertEquals("align-start", DateTimePickerVariants.alignStart.name)
        assertEquals("align-end", DateTimePickerVariants.alignEnd.name)
        assertEquals("label-aside", DateTimePickerVariants.labelAside.name)
    }

    @Test
    fun `detailsTokensMatchUpstream`() {
        assertEquals("filled", DetailsVariants.filled.name)
        assertEquals("reverse", DetailsVariants.reverse.name)
        assertEquals("small", DetailsVariants.small.name)
        assertEquals("no-padding", DetailsVariants.noPadding.name)
    }

    @Test
    fun `dialogTokensMatchUpstream`() {
        assertEquals("no-padding", DialogVariants.noPadding.name)
    }

    @Test
    fun `gridTokensMatchUpstream`() {
        assertEquals("no-border", GridVariants.noBorder.name)
        assertEquals("no-row-borders", GridVariants.noRowBorders.name)
        assertEquals("column-borders", GridVariants.columnBorders.name)
        assertEquals("row-stripes", GridVariants.rowStripes.name)
        assertEquals("compact", GridVariants.compact.name)
        assertEquals("wrap-cell-content", GridVariants.wrapCellContent.name)
    }

    @Test
    fun `menuBarTokensMatchUpstream`() {
        assertEquals("small", MenuBarVariants.small.name)
        assertEquals("large", MenuBarVariants.large.name)
        assertEquals("tertiary", MenuBarVariants.tertiary.name)
        assertEquals("tertiary-inline", MenuBarVariants.tertiaryInline.name)
        assertEquals("primary", MenuBarVariants.primary.name)
        assertEquals("contrast", MenuBarVariants.contrast.name)
        assertEquals("icon", MenuBarVariants.icon.name)
        assertEquals("end-aligned", MenuBarVariants.alignEnd.name)
        assertEquals("dropdown-indicators", MenuBarVariants.dropdownIndicators.name)
        assertEquals("filled", MenuBarVariants.filled.name)
        assertEquals("success", MenuBarVariants.success.name)
        assertEquals("warning", MenuBarVariants.warning.name)
        assertEquals("error", MenuBarVariants.error.name)
    }

    @Test
    fun `messageInputTokensMatchUpstream`() {
        assertEquals("icon-button", MessageInputVariants.iconButton.name)
    }

    @Test
    fun `messageListItemTokensMatchUpstream`() {
        assertEquals("self", MessageListItemVariants.self.name)
        assertEquals("full-width", MessageListItemVariants.fullWidth.name)
    }

    @Test
    fun `messageListTokensMatchUpstream`() {
        assertEquals("bubble", MessageListVariants.bubble.name)
        assertEquals("one-to-one", MessageListVariants.oneToOne.name)
    }

    @Test
    fun `notificationTokensMatchUpstream`() {
        assertEquals("primary", NotificationVariants.primary.name)
        assertEquals("contrast", NotificationVariants.contrast.name)
        assertEquals("success", NotificationVariants.success.name)
        assertEquals("error", NotificationVariants.error.name)
        assertEquals("warning", NotificationVariants.warning.name)
        assertEquals("info", NotificationVariants.info.name)
    }

    @Test
    fun `horizontalLayoutTokensMatchUpstream`() {
        assertEquals("margin", HorizontalLayoutVariants.margin.name)
        assertEquals("padding", HorizontalLayoutVariants.padding.name)
        assertEquals("spacing-xs", HorizontalLayoutVariants.spacingXs.name)
        assertEquals("spacing-s", HorizontalLayoutVariants.spacingS.name)
        assertEquals("spacing", HorizontalLayoutVariants.spacing.name)
        assertEquals("spacing-l", HorizontalLayoutVariants.spacingL.name)
        assertEquals("spacing-xl", HorizontalLayoutVariants.spacingXl.name)
        assertEquals("wrap", HorizontalLayoutVariants.wrap.name)
    }

    @Test
    fun `scrollerTokensMatchUpstream`() {
        assertEquals("overflow-indicators", ScrollerVariants.overflowIndicators.name)
        assertEquals("overflow-indicator-top", ScrollerVariants.overflowIndicatorTop.name)
        assertEquals("overflow-indicator-bottom", ScrollerVariants.overflowIndicatorBottom.name)
    }

    @Test
    fun `verticalLayoutTokensMatchUpstream`() {
        assertEquals("margin", VerticalLayoutVariants.margin.name)
        assertEquals("padding", VerticalLayoutVariants.padding.name)
        assertEquals("spacing-xs", VerticalLayoutVariants.spacingXs.name)
        assertEquals("spacing-s", VerticalLayoutVariants.spacingS.name)
        assertEquals("spacing", VerticalLayoutVariants.spacing.name)
        assertEquals("spacing-l", VerticalLayoutVariants.spacingL.name)
        assertEquals("spacing-xl", VerticalLayoutVariants.spacingXl.name)
        assertEquals("wrap", VerticalLayoutVariants.wrap.name)
    }

    @Test
    fun `popoverTokensMatchUpstream`() {
        assertEquals("no-padding", PopoverVariants.noPadding.name)
        assertEquals("arrow", PopoverVariants.arrow.name)
    }

    @Test
    fun `progressBarTokensMatchUpstream`() {
        assertEquals("contrast", ProgressBarVariants.contrast.name)
        assertEquals("error", ProgressBarVariants.error.name)
        assertEquals("success", ProgressBarVariants.success.name)
    }

    @Test
    fun `radioGroupTokensMatchUpstream`() {
        assertEquals("vertical", RadioGroupVariants.vertical.name)
        assertEquals("helper-above-field", RadioGroupVariants.helperAbove.name)
        assertEquals("horizontal", RadioGroupVariants.horizontal.name)
        assertEquals("label-aside", RadioGroupVariants.labelAside.name)
    }

    @Test
    fun `selectTokensMatchUpstream`() {
        assertEquals("small", SelectVariants.small.name)
        assertEquals("align-left", SelectVariants.alignLeft.name)
        assertEquals("align-center", SelectVariants.alignCenter.name)
        assertEquals("align-right", SelectVariants.alignRight.name)
        assertEquals("helper-above-field", SelectVariants.helperAbove.name)
        assertEquals("filled", SelectVariants.filled.name)
        assertEquals("align-start", SelectVariants.alignStart.name)
        assertEquals("align-end", SelectVariants.alignEnd.name)
        assertEquals("label-aside", SelectVariants.labelAside.name)
    }

    @Test
    fun `sideNavTokensMatchUpstream`() {
        assertEquals("filled", SideNavVariants.filled.name)
    }

    @Test
    fun `sliderTokensMatchUpstream`() {
        assertEquals("helper-above-field", SliderVariants.helperAbove.name)
        assertEquals("label-aside", SliderVariants.labelAside.name)
    }

    @Test
    fun `splitLayoutTokensMatchUpstream`() {
        assertEquals("small", SplitLayoutVariants.small.name)
        assertEquals("minimal", SplitLayoutVariants.minimal.name)
    }

    @Test
    fun `tabSheetTokensMatchUpstream`() {
        assertEquals("centered", TabSheetVariants.tabsCentered.name)
        assertEquals("small", TabSheetVariants.tabsSmall.name)
        assertEquals("minimal", TabSheetVariants.tabsMinimal.name)
        assertEquals("hide-scroll-buttons", TabSheetVariants.tabsHideScrollButtons.name)
        assertEquals("equal-width-tabs", TabSheetVariants.tabsEqualWidthTabs.name)
        assertEquals("bordered", TabSheetVariants.bordered.name)
        assertEquals("no-padding", TabSheetVariants.noPadding.name)
        assertEquals("show-scroll-buttons", TabSheetVariants.tabsShowScrollButtons.name)
        assertEquals("no-border", TabSheetVariants.noBorder.name)
        assertEquals("filled", TabSheetVariants.tabsFilled.name)
    }

    @Test
    fun `tabsTokensMatchUpstream`() {
        assertEquals("centered", TabsVariants.centered.name)
        assertEquals("small", TabsVariants.small.name)
        assertEquals("minimal", TabsVariants.minimal.name)
        assertEquals("hide-scroll-buttons", TabsVariants.hideScrollButtons.name)
        assertEquals("equal-width-tabs", TabsVariants.equalWidthTabs.name)
        assertEquals("show-scroll-buttons", TabsVariants.showScrollButtons.name)
        assertEquals("filled", TabsVariants.filled.name)
    }

    @Test
    fun `tabTokensMatchUpstream`() {
        assertEquals("icon-on-top", TabVariants.iconOnTop.name)
    }

    @Test
    fun `textAreaTokensMatchUpstream`() {
        assertEquals("small", TextAreaVariants.small.name)
        assertEquals("align-center", TextAreaVariants.alignCenter.name)
        assertEquals("align-right", TextAreaVariants.alignRight.name)
        assertEquals("helper-above-field", TextAreaVariants.helperAbove.name)
        assertEquals("align-left", TextAreaVariants.alignLeft.name)
        assertEquals("align-start", TextAreaVariants.alignStart.name)
        assertEquals("align-end", TextAreaVariants.alignEnd.name)
        assertEquals("label-aside", TextAreaVariants.labelAside.name)
    }

    @Test
    fun `textFieldTokensMatchUpstream`() {
        assertEquals("small", TextFieldVariants.small.name)
        assertEquals("align-center", TextFieldVariants.alignCenter.name)
        assertEquals("align-right", TextFieldVariants.alignRight.name)
        assertEquals("helper-above-field", TextFieldVariants.helperAbove.name)
        assertEquals("align-left", TextFieldVariants.alignLeft.name)
        assertEquals("align-start", TextFieldVariants.alignStart.name)
        assertEquals("align-end", TextFieldVariants.alignEnd.name)
        assertEquals("label-aside", TextFieldVariants.labelAside.name)
    }

    @Test
    fun `timePickerTokensMatchUpstream`() {
        assertEquals("small", TimePickerVariants.small.name)
        assertEquals("align-left", TimePickerVariants.alignLeft.name)
        assertEquals("align-center", TimePickerVariants.alignCenter.name)
        assertEquals("align-right", TimePickerVariants.alignRight.name)
        assertEquals("helper-above-field", TimePickerVariants.helperAbove.name)
        assertEquals("filled", TimePickerVariants.filled.name)
        assertEquals("align-start", TimePickerVariants.alignStart.name)
        assertEquals("align-end", TimePickerVariants.alignEnd.name)
        assertEquals("label-aside", TimePickerVariants.labelAside.name)
    }

    @Test
    fun `uploadFileListTokensMatchUpstream`() {
        assertEquals("thumbnails", UploadFileListVariants.thumbnails.name)
    }

    @Test
    fun `uploadTokensMatchUpstream`() {
        assertEquals("no-border", UploadVariants.noBorder.name)
    }

    @Test
    fun `virtualListTokensMatchUpstream`() {
        assertEquals("overflow-indicators", VirtualListVariants.overflowIndicators.name)
        assertEquals("overflow-indicator-top", VirtualListVariants.overflowIndicatorTop.name)
        assertEquals("overflow-indicator-bottom", VirtualListVariants.overflowIndicatorBottom.name)
    }

    @Test
    fun `a typed variant can be applied and read back`() {
        assertEquals("primary", ButtonVariants.primary.name)
        assertEquals("tertiary-inline", ButtonVariants.tertiaryInline.name)
    }

    // One `theme` attribute, two vocabularies: typed variants and raw tokens are the same list, so
    // `themes`, `themeAttr.values` and the DOM text can never disagree.

    @Test
    fun `typed and raw tokens share one attribute`() {
        val button = createButton()
        button.addTheme(ButtonVariants.primary)
        button.addTheme("acme-brand")

        assertEquals("primary acme-brand", button.getAttribute("theme"))
        assertEquals(listOf("primary", "acme-brand"), button.themes)
        assertEquals(listOf(ButtonVariants.primary, ThemeVariant("acme-brand")), button.themeAttr.values)
        assertTrue(button.hasTheme(ButtonVariants.primary))
        assertTrue(button.hasTheme("acme-brand"))
    }

    @Test
    fun `a typed write keeps custom tokens`() {
        val button = createButton().apply { addTheme("acme-brand") }
        button.addTheme(ButtonVariants.primary, ButtonVariants.small)

        assertEquals("acme-brand primary small", button.getAttribute("theme"))
        assertEquals(3, button.themes.size)
    }

    @Test
    fun `setting typed variants replaces the whole theme`() {
        val button = createButton().apply { addTheme("acme-brand") }
        button.setTheme(ButtonVariants.primary, ButtonVariants.small)

        assertEquals("primary small", button.getAttribute("theme"))
        assertEquals(listOf(ButtonVariants.primary, ButtonVariants.small), button.themeAttr.values)
    }

    @Test
    fun `setting raw tokens normalises the text`() {
        val button = createButton().apply { addTheme("acme-brand") }
        button.setTheme("primary  small", "primary")

        assertEquals("primary small", button.getAttribute("theme"))
        assertEquals(listOf("primary", "small"), button.themes)
    }

    @Test
    fun `removing a typed variant leaves the other tokens`() {
        val button = createButton().apply {
            addTheme(ButtonVariants.primary)
            addTheme("acme-brand")
        }
        button.removeTheme(ButtonVariants.primary)

        assertEquals("acme-brand", button.getAttribute("theme"))
        assertFalse(button.hasTheme(ButtonVariants.primary))
        assertTrue(button.hasTheme("acme-brand"))
    }

    @Test
    fun `setting no tokens removes the attribute`() {
        val button = createButton().apply { addTheme(ButtonVariants.primary) }
        button.setTheme()

        assertEquals(null, button.getAttribute("theme"))
        assertTrue(button.themes.isEmpty())
    }
}
