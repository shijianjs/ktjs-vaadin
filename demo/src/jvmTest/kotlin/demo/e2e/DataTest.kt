package demo.e2e

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import com.microsoft.playwright.junit.UsePlaywright
import com.microsoft.playwright.options.AriaRole
import com.microsoft.playwright.options.FilePayload
import kotlin.test.assertTrue
import org.junitpioneer.jupiter.RetryingTest

/**
 * Port of `demo/e2e/tests/data.spec.ts`: grid, tabs, accordion, markdown and the display widgets.
 */
@UsePlaywright(DemoOptions::class)
class DataTest {
    @RetryingTest(maxAttempts = 2)
    fun `grid renders the items and reports the selection`(page: Page) {
        page.navigate("/#/grid")

        val grid = page.getByTestId("grid-basic")
        assertThat(grid).containsText("Ada Lovelace")
        assertThat(grid).containsText("Grace Hopper")

        val cells = grid.locator("vaadin-grid-cell-content")
        assertTrue(cells.count() > 10)

        grid.locator("vaadin-checkbox").last().click()
        assertThat(page.locator(".readout", Page.LocatorOptions().setHasText("rows selected")))
            .containsText("1 rows selected")
    }

    @RetryingTest(maxAttempts = 2)
    fun `grid sort column flips its direction on header click`(page: Page) {
        page.navigate("/#/grid")

        val sorted = page.getByTestId("grid-sorted")
        val column = sorted.locator("vaadin-grid-sort-column").first()
        assertThat(column).hasJSProperty("direction", "asc")

        sorted.getByText("Name", Locator.GetByTextOptions().setExact(true)).first().click()
        assertThat(column).hasJSProperty("direction", "desc")
    }

    @RetryingTest(maxAttempts = 2)
    fun `upload lists fake files and completes a simulated upload`(page: Page) {
        page.navigate("/#/upload")

        val basic = page.getByTestId("upload-basic")
        assertThat(basic.locator("vaadin-upload-file")).hasCount(3)
        assertThat(basic.getByText("Workflow.pdf")).isVisible()

        page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("Upload All Files")).click()
        assertThat(page.locator(".readout", Page.LocatorOptions().setHasText("Upload status"))).containsText("%")
    }

    @RetryingTest(maxAttempts = 2)
    fun `upload rejects a file outside the accepted format`(page: Page) {
        page.navigate("/#/upload")

        page.getByTestId("upload-format")
            .locator("input[type=file]")
            .setInputFiles(FilePayload("notes.txt", "text/plain", "x".toByteArray()))

        assertThat(page.locator("vaadin-notification-card"))
            .hasText("The provided file does not have the correct format (PDF document).")
    }

    @RetryingTest(maxAttempts = 2)
    fun `tabs switch the selected index`(page: Page) {
        page.navigate("/#/tabs")

        val tabs = page.getByTestId("tabs")
        tabs.locator("vaadin-tab", Locator.LocatorOptions().setHasText("Settings")).click()
        assertThat(tabs).hasJSProperty("selected", 2)
        assertThat(page.locator(".event-log")).containsText("selected-changed · 2")
    }

    @RetryingTest(maxAttempts = 2)
    fun `tab sheet swaps the visible panel`(page: Page) {
        page.navigate("/#/tabsheet")

        val sheet = page.getByTestId("tab-sheet")
        val firstPanel = sheet.getByText("The content of this tab comes from the first panel.")
        val thirdPanel = sheet.getByText("The third panel.")

        assertThat(firstPanel).isVisible()
        assertThat(thirdPanel).isHidden()

        sheet.locator("vaadin-tab", Locator.LocatorOptions().setHasText("Permissions")).click()
        assertThat(thirdPanel).isVisible()
        assertThat(firstPanel).isHidden()
        assertThat(page.locator(".readout", Page.LocatorOptions().setHasText("selected"))).containsText("selected = 2")
        assertThat(sheet).not().hasAttribute("loading", "")
    }

    @RetryingTest(maxAttempts = 2)
    fun `accordion expands a panel and can be driven programmatically`(page: Page) {
        page.navigate("/#/accordion")

        val accordion = page.getByTestId("accordion")
        val panel = accordion.locator("vaadin-accordion-panel", Locator.LocatorOptions().setHasText("Payment"))
        assertThat(panel.getByText("Content of the second panel.")).isHidden()

        panel.locator("vaadin-accordion-heading").click()
        assertThat(panel.getByText("Content of the second panel.")).isVisible()

        page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("Open the first panel")).click()
        assertThat(page.locator(".readout", Page.LocatorOptions().setHasText("opened"))).containsText("opened = 0")
    }

    @RetryingTest(maxAttempts = 2)
    fun `details toggles through its summary`(page: Page) {
        page.navigate("/#/details")

        val details = page.getByTestId("details")
        assertThat(details).hasJSProperty("opened", false)

        details.locator("vaadin-details-summary").click()
        assertThat(details).hasJSProperty("opened", true)
        assertThat(page.locator(".event-log")).containsText("opened-changed · true")
    }

    @RetryingTest(maxAttempts = 2)
    fun `markdown renders headings, emphasis and lists`(page: Page) {
        page.navigate("/#/markdown")

        val markdown = page.getByTestId("markdown")
        assertThat(markdown.locator("h2").first()).hasText("Rich Text Formatting")
        assertThat(markdown.locator("strong").first()).hasText("bold text")
        assertThat(markdown.locator("li")).hasCount(10)
        assertThat(markdown.getByText("Visit Vaadin website")).isVisible()
    }

    @RetryingTest(maxAttempts = 2)
    fun `virtual list renders person cards and can jump`(page: Page) {
        page.navigate("/#/virtual-list")

        val lists = page.locator("vaadin-virtual-list")
        val basic = page.getByTestId("virtual-list")
        assertThat(basic).containsText("Ada Lovelace")
        assertThat(basic).containsText("Mathematician")

        val email = basic.getByText("ada.lovelace@example.com").first()
        assertThat(email).isHidden()
        basic.getByText("Contact information").first().click()
        assertThat(email).isVisible()

        page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("Go to row 300")).click()
        assertThat(lists.nth(1)).containsText("Row 300")
    }

    @RetryingTest(maxAttempts = 2)
    fun `message list reports the clicked attachment`(page: Page) {
        page.navigate("/#/message-list")

        val readout = page.locator(".readout", Page.LocatorOptions().setHasText("Clicked"))
        page.getByText("project-proposal.pdf").click()
        assertThat(readout).containsText("project-proposal.pdf")
    }

    @RetryingTest(maxAttempts = 2)
    fun `badge updates its number in place`(page: Page) {
        page.navigate("/#/badge")

        assertThat(page.getByTestId("badge").locator("""[part="number"]""")).hasText("4")

        val live = page.getByTestId("badge-live").locator("""[part="number"]""")
        assertThat(live).hasText("0")
        page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("Add one")).click()
        assertThat(live).hasText("1")
    }

    @RetryingTest(maxAttempts = 2)
    fun `progress bar advances and can be indeterminate`(page: Page) {
        page.navigate("/#/progress-bar")

        page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("+10")).click()
        assertThat(page.locator(".readout", Page.LocatorOptions().setHasText("value"))).containsText("45")

        page.locator("vaadin-switch", Page.LocatorOptions().setHasText("indeterminate")).click()
        assertThat(page.locator("vaadin-progress-bar").first()).hasJSProperty("indeterminate", false)
    }

    @RetryingTest(maxAttempts = 2)
    fun `icon renders an inline svg from the lumo set`(page: Page) {
        page.navigate("/#/icon")

        val icon = page.locator(".page vaadin-icon").first()
        assertThat(icon).hasAttribute("icon", "lumo:checkmark")
        assertThat(icon.locator("svg")).hasCount(1)
    }
}
