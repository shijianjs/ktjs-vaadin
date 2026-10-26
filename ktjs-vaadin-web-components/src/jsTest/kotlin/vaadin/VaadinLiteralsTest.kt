package vaadin

import kotlin.js.unsafeCast
import kotlin.test.Test
import kotlin.test.assertEquals
import vaadin.a11ybase.ListOrientations
import vaadin.applayout.AppLayoutPrimarySection
import vaadin.applayout.AppLayoutPrimarySections
import vaadin.combobox.ComboBoxPartialMatchModes
import vaadin.contextmenu.ContextMenuTooltipTriggers
import vaadin.fieldhighlighter.AiFieldMarkerConfidences
import vaadin.formlayout.FormLayoutLabelsPositions
import vaadin.grid.ColumnRenderings
import vaadin.grid.GridDropModes
import vaadin.grid.GridEventSections
import vaadin.grid.GridMultiSortPriorities
import vaadin.grid.GridSorterDirections
import vaadin.masterdetaillayout.AnimationEffects
import vaadin.masterdetaillayout.MasterDetailLayoutOrientations
import vaadin.masterdetaillayout.MasterDetailLayoutOverlayContainments
import vaadin.overlay.OverlayHorizontalAligns
import vaadin.overlay.OverlayVerticalAligns
import vaadin.popover.PopoverTriggers
import vaadin.scroller.ScrollerScrollDirections
import vaadin.splitlayout.SplitLayoutOrientations
import vaadin.tooltip.TooltipAriaLinkModes
import vaadin.upload.UploadFormats
import vaadin.upload.UploadMethods

/**
 * Kotlin-only coverage: every string-literal union must wrap exactly the value declared by the
 * upstream `.d.ts`. A typo in a constant silently changes the value passed to the component, so
 * each constant is asserted individually.
 */
class VaadinLiteralsTest {
    @Test
    fun listOrientationValues() {
        assertEquals("horizontal", ListOrientations.horizontal.unsafeCast<String>())
        assertEquals("vertical", ListOrientations.vertical.unsafeCast<String>())
    }

    @Test
    fun appLayoutPrimarySectionValues() {
        assertEquals("drawer", AppLayoutPrimarySections.drawer.unsafeCast<String>())
        assertEquals("navbar", AppLayoutPrimarySections.navbar.unsafeCast<String>())
        assertEquals("custom", AppLayoutPrimarySection("custom").unsafeCast<String>())
    }

    @Test
    fun comboBoxPartialMatchModeValues() {
        assertEquals("first-match", ComboBoxPartialMatchModes.firstMatch.unsafeCast<String>())
        assertEquals("none", ComboBoxPartialMatchModes.none.unsafeCast<String>())
        assertEquals("only-match", ComboBoxPartialMatchModes.onlyMatch.unsafeCast<String>())
    }

    @Test
    fun contextMenuTooltipTriggerValues() {
        assertEquals("hover", ContextMenuTooltipTriggers.hover.unsafeCast<String>())
        assertEquals("focus", ContextMenuTooltipTriggers.focus.unsafeCast<String>())
    }

    @Test
    fun aiFieldMarkerConfidenceValues() {
        assertEquals("high", AiFieldMarkerConfidences.high.unsafeCast<String>())
        assertEquals("low", AiFieldMarkerConfidences.low.unsafeCast<String>())
        assertEquals("medium", AiFieldMarkerConfidences.medium.unsafeCast<String>())
    }

    @Test
    fun formLayoutLabelsPositionValues() {
        assertEquals("aside", FormLayoutLabelsPositions.aside.unsafeCast<String>())
        assertEquals("top", FormLayoutLabelsPositions.top.unsafeCast<String>())
    }

    @Test
    fun gridColumnRenderingValues() {
        assertEquals("eager", ColumnRenderings.eager.unsafeCast<String>())
        assertEquals("lazy", ColumnRenderings.lazy.unsafeCast<String>())
    }

    @Test
    fun gridDropModeValues() {
        assertEquals("between", GridDropModes.between.unsafeCast<String>())
        assertEquals("on-grid", GridDropModes.onGrid.unsafeCast<String>())
        assertEquals("on-top-or-between", GridDropModes.onTopOrBetween.unsafeCast<String>())
        assertEquals("on-top", GridDropModes.onTop.unsafeCast<String>())
    }

    @Test
    fun gridEventSectionValues() {
        assertEquals("body", GridEventSections.body.unsafeCast<String>())
        assertEquals("details", GridEventSections.details.unsafeCast<String>())
        assertEquals("footer", GridEventSections.footer.unsafeCast<String>())
        assertEquals("header", GridEventSections.header.unsafeCast<String>())
    }

    @Test
    fun gridMultiSortPriorityValues() {
        assertEquals("append", GridMultiSortPriorities.append.unsafeCast<String>())
        assertEquals("prepend", GridMultiSortPriorities.prepend.unsafeCast<String>())
    }

    @Test
    fun gridSorterDirectionValues() {
        assertEquals("asc", GridSorterDirections.asc.unsafeCast<String>())
        assertEquals("desc", GridSorterDirections.desc.unsafeCast<String>())
    }

    @Test
    fun masterDetailLayoutAnimationEffectValues() {
        assertEquals("fade", AnimationEffects.fade.unsafeCast<String>())
        assertEquals("slide", AnimationEffects.slide.unsafeCast<String>())
    }

    @Test
    fun masterDetailLayoutOrientationValues() {
        assertEquals("horizontal", MasterDetailLayoutOrientations.horizontal.unsafeCast<String>())
        assertEquals("vertical", MasterDetailLayoutOrientations.vertical.unsafeCast<String>())
    }

    @Test
    fun masterDetailLayoutOverlayContainmentValues() {
        assertEquals("layout", MasterDetailLayoutOverlayContainments.layout.unsafeCast<String>())
        assertEquals("page", MasterDetailLayoutOverlayContainments.page.unsafeCast<String>())
    }

    @Test
    fun overlayAlignmentValues() {
        assertEquals("end", OverlayHorizontalAligns.end.unsafeCast<String>())
        assertEquals("start", OverlayHorizontalAligns.start.unsafeCast<String>())
        assertEquals("bottom", OverlayVerticalAligns.bottom.unsafeCast<String>())
        assertEquals("top", OverlayVerticalAligns.top.unsafeCast<String>())
    }

    @Test
    fun popoverTriggerValues() {
        assertEquals("click", PopoverTriggers.click.unsafeCast<String>())
        assertEquals("focus", PopoverTriggers.focus.unsafeCast<String>())
        assertEquals("hover", PopoverTriggers.hover.unsafeCast<String>())
    }

    @Test
    fun scrollerScrollDirectionValues() {
        assertEquals("horizontal", ScrollerScrollDirections.horizontal.unsafeCast<String>())
        assertEquals("none", ScrollerScrollDirections.none.unsafeCast<String>())
        assertEquals("vertical", ScrollerScrollDirections.vertical.unsafeCast<String>())
    }

    @Test
    fun splitLayoutOrientationValues() {
        assertEquals("horizontal", SplitLayoutOrientations.horizontal.unsafeCast<String>())
        assertEquals("vertical", SplitLayoutOrientations.vertical.unsafeCast<String>())
    }

    @Test
    fun tooltipAriaLinkModeValues() {
        assertEquals("aria-describedby", TooltipAriaLinkModes.ariaDescribedBy.unsafeCast<String>())
        assertEquals("aria-labelledby", TooltipAriaLinkModes.ariaLabelledBy.unsafeCast<String>())
        assertEquals("none", TooltipAriaLinkModes.none.unsafeCast<String>())
    }

    @Test
    fun uploadMethodValues() {
        assertEquals("POST", UploadMethods.post.unsafeCast<String>())
        assertEquals("PUT", UploadMethods.put.unsafeCast<String>())
    }

    @Test
    fun uploadFormatValues() {
        assertEquals("raw", UploadFormats.raw.unsafeCast<String>())
        assertEquals("multipart", UploadFormats.multipart.unsafeCast<String>())
    }
}
