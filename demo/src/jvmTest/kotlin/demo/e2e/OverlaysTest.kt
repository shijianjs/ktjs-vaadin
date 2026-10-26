package demo.e2e

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import com.microsoft.playwright.junit.UsePlaywright
import com.microsoft.playwright.options.AriaRole
import com.microsoft.playwright.options.MouseButton
import java.util.regex.Pattern
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junitpioneer.jupiter.RetryingTest

/**
 * Port of `demo/e2e/tests/overlays.spec.ts`: open / close, results, and the events each overlay
 * reports.
 */
@UsePlaywright(DemoOptions::class)
class OverlaysTest {
    @RetryingTest(maxAttempts = 2)
    fun `dialog opens with rendered content and closes from its footer`(page: Page) {
        page.navigate("/#/dialog")

        page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("Open dialog")).click()
        val overlay = page.locator("vaadin-dialog-overlay[opened]")
        assertThat(overlay).isVisible()

        // Dialog content and title live in the overlay's shadow root, so they are asserted by text.
        assertThat(page.getByText("The dialog content comes from renderer")).isVisible()
        assertThat(page.getByText("Example dialog")).isVisible()

        page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("Close").setExact(true)).click()
        assertThat(overlay).isHidden()
        assertThat(page.locator(".event-log")).containsText("closed")
    }

    @RetryingTest(maxAttempts = 2)
    fun `dialog can refuse Esc and outside clicks`(page: Page) {
        page.navigate("/#/dialog")

        page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("Open (custom size)")).click()
        val overlay = page.locator("vaadin-dialog-overlay[opened]")
        assertThat(page.getByText("Only the button below closes it.")).isVisible()

        page.keyboard().press("Escape")
        assertThat(overlay).isVisible()

        page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("Close").setExact(true)).click()
        assertThat(overlay).isHidden()
    }

    @RetryingTest(maxAttempts = 2)
    fun `confirm dialog reports confirm, reject and cancel`(page: Page) {
        page.navigate("/#/confirm-dialog")

        page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("Delete record")).click()
        val overlay = page.locator("vaadin-confirm-dialog-overlay[opened]")
        assertThat(overlay).isVisible()
        assertThat(page.getByText("Delete this record?")).isVisible()

        page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("Delete").setExact(true)).click()
        assertThat(page.locator(".event-log").first()).containsText("confirm")

        page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("Open three-button dialog")).click()
        assertThat(page.getByText("Save changes?")).isVisible()
        page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("Discard").setExact(true)).click()
        assertThat(page.locator(".event-log").nth(1)).containsText("reject → Discard")
    }

    @RetryingTest(maxAttempts = 2)
    fun `notification shows at the requested position`(page: Page) {
        page.navigate("/#/notification")

        page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("Top end")).click()
        val card = page.locator("vaadin-notification-card", Page.LocatorOptions().setHasText("Message at the top end"))
        assertThat(card).isVisible()

        // `top-end` means the card sits in the top-right quadrant of the viewport.
        val box = assertNotNull(card.boundingBox(), "notification card box")
        val viewport = assertNotNull(page.viewportSize(), "viewport size")
        assertTrue(box.y < viewport.height / 2.0)
        assertTrue(box.x + box.width / 2 > viewport.width / 2.0)

        page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("success")).click()
        assertThat(page.locator("vaadin-notification-card", Page.LocatorOptions().setHasText("Saved successfully")))
            .isVisible()
    }

    @RetryingTest(maxAttempts = 2)
    fun `persistent notification stays until it is closed`(page: Page) {
        page.navigate("/#/notification")

        page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("Show persistent notification")).click()
        val card =
            page.locator(
                "vaadin-notification-card",
                Page.LocatorOptions().setHasText("This notification stays visible"),
            )
        assertThat(card).isVisible()

        card.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Close")).click()
        assertThat(card).isHidden()
    }

    @RetryingTest(maxAttempts = 2)
    fun `popover opens on click and closes on outside click`(page: Page) {
        page.navigate("/#/popover")

        page.getByTestId("popover-trigger").click()
        val content = page.getByText("Any DOM goes into the popover's default slot.")
        assertThat(content).isVisible()
        assertThat(page.locator(".event-log")).containsText("opened-changed · true")

        page.locator(".page__title").click()
        assertThat(content).isHidden()
        assertThat(page.locator(".event-log")).containsText("opened-changed · false")
    }

    @RetryingTest(maxAttempts = 2)
    fun `tooltip appears on hover`(page: Page) {
        page.navigate("/#/tooltip")

        val trigger = page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("Hover me"))
        trigger.hover()
        assertThat(page.getByText("Hint on the button")).isVisible()
    }

    @RetryingTest(maxAttempts = 2)
    fun `context menu opens on right click and reports the selection`(page: Page) {
        page.navigate("/#/context-menu")

        page.getByTestId("context-menu-surface").click(Locator.ClickOptions().setButton(MouseButton.RIGHT))

        // The menu renders its items into the light DOM once it is opened.
        val item = page.locator("vaadin-context-menu-item", Page.LocatorOptions().setHasText("Copy"))
        assertThat(item).isVisible()

        item.click()
        assertThat(page.locator(".event-log")).containsText("item-selected")
    }

    @RetryingTest(maxAttempts = 2)
    fun `bare overlay opens and closes from its own content`(page: Page) {
        page.navigate("/#/overlay")

        page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("Open overlay")).click()
        val overlay = page.locator("vaadin-overlay", Page.LocatorOptions().setHasText("This is a bare overlay"))
        assertThat(overlay).isVisible()

        overlay.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Close")).click()
        assertThat(overlay).isHidden()
    }

    @RetryingTest(maxAttempts = 2)
    fun `login form validates and can report an error state`(page: Page) {
        page.navigate("/#/login")

        val form = page.getByTestId("login-form")
        assertThat(form).isVisible()

        form.getByRole(
            AriaRole.BUTTON,
            Locator.GetByRoleOptions().setName(Pattern.compile("log ?in|sign ?in|submit", Pattern.CASE_INSENSITIVE)),
        ).click()
        assertThat(form).containsText(Pattern.compile("required", Pattern.CASE_INSENSITIVE))

        page.locator("vaadin-switch", Page.LocatorOptions().setHasText("Show error state")).click()
        assertThat(page.locator(".event-log")).containsText("error-changed · true")
    }
}
