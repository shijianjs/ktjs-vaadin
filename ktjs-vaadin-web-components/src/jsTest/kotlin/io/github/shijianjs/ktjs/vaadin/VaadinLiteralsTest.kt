package io.github.shijianjs.ktjs.vaadin

import kotlin.js.unsafeCast
import kotlin.test.Test
import kotlin.test.assertEquals
import io.github.shijianjs.ktjs.vaadin.a11ybase.ListOrientations
import io.github.shijianjs.ktjs.vaadin.applayout.AppLayoutPrimarySection
import io.github.shijianjs.ktjs.vaadin.applayout.AppLayoutPrimarySections
import io.github.shijianjs.ktjs.vaadin.combobox.ComboBoxPartialMatchModes
import io.github.shijianjs.ktjs.vaadin.contextmenu.ContextMenuTooltipTriggers
import io.github.shijianjs.ktjs.vaadin.fieldhighlighter.AiFieldMarkerConfidences
import io.github.shijianjs.ktjs.vaadin.formlayout.FormLayoutLabelsPositions
import io.github.shijianjs.ktjs.vaadin.grid.ColumnRenderings
import io.github.shijianjs.ktjs.vaadin.grid.GridDropModes
import io.github.shijianjs.ktjs.vaadin.grid.GridEventSections
import io.github.shijianjs.ktjs.vaadin.grid.GridMultiSortPriorities
import io.github.shijianjs.ktjs.vaadin.grid.GridSorterDirections
import io.github.shijianjs.ktjs.vaadin.masterdetaillayout.AnimationEffects
import io.github.shijianjs.ktjs.vaadin.masterdetaillayout.MasterDetailLayoutOrientations
import io.github.shijianjs.ktjs.vaadin.masterdetaillayout.MasterDetailLayoutOverlayContainments
import io.github.shijianjs.ktjs.vaadin.overlay.OverlayHorizontalAligns
import io.github.shijianjs.ktjs.vaadin.overlay.OverlayVerticalAligns
import io.github.shijianjs.ktjs.vaadin.popover.PopoverTriggers
import io.github.shijianjs.ktjs.vaadin.scroller.ScrollerScrollDirections
import io.github.shijianjs.ktjs.vaadin.splitlayout.SplitLayoutOrientations
import io.github.shijianjs.ktjs.vaadin.tooltip.TooltipAriaLinkModes
import io.github.shijianjs.ktjs.vaadin.upload.UploadFormats
import io.github.shijianjs.ktjs.vaadin.upload.UploadMethods

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
