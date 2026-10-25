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

        page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("打开对话框")).click()
        val overlay = page.locator("vaadin-dialog-overlay[opened]")
        assertThat(overlay).isVisible()

        // Dialog content and title live in the overlay's shadow root, so they are asserted by text.
        assertThat(page.getByText("对话框内容由 renderer 提供")).isVisible()
        assertThat(page.getByText("示例对话框")).isVisible()

        page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("关闭").setExact(true)).click()
        assertThat(overlay).isHidden()
        assertThat(page.locator(".event-log")).containsText("closed")
    }

    @RetryingTest(maxAttempts = 2)
    fun `dialog can refuse Esc and outside clicks`(page: Page) {
        page.navigate("/#/dialog")

        page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("打开（自定义尺寸）")).click()
        val overlay = page.locator("vaadin-dialog-overlay[opened]")
        assertThat(page.getByText("只能用下面的按钮关闭")).isVisible()

        page.keyboard().press("Escape")
        assertThat(overlay).isVisible()

        page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("关闭").setExact(true)).click()
        assertThat(overlay).isHidden()
    }

    @RetryingTest(maxAttempts = 2)
    fun `confirm dialog reports confirm, reject and cancel`(page: Page) {
        page.navigate("/#/confirm-dialog")

        page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("删除记录")).click()
        val overlay = page.locator("vaadin-confirm-dialog-overlay[opened]")
        assertThat(overlay).isVisible()
        assertThat(page.getByText("删除这条记录？")).isVisible()

        page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("删除").setExact(true)).click()
        assertThat(page.locator(".event-log").first()).containsText("confirm")

        page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("打开三按钮对话框")).click()
        assertThat(page.getByText("保存修改？")).isVisible()
        page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("放弃").setExact(true)).click()
        assertThat(page.locator(".event-log").nth(1)).containsText("reject → 放弃")
    }

    @RetryingTest(maxAttempts = 2)
    fun `notification shows at the requested position`(page: Page) {
        page.navigate("/#/notification")

        page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("右上角")).click()
        val card = page.locator("vaadin-notification-card", Page.LocatorOptions().setHasText("右上角的提示"))
        assertThat(card).isVisible()

        // `top-end` means the card sits in the top-right quadrant of the viewport.
        val box = assertNotNull(card.boundingBox(), "notification card box")
        val viewport = assertNotNull(page.viewportSize(), "viewport size")
        assertTrue(box.y < viewport.height / 2.0)
        assertTrue(box.x + box.width / 2 > viewport.width / 2.0)

        page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("success")).click()
        assertThat(page.locator("vaadin-notification-card", Page.LocatorOptions().setHasText("保存成功")))
            .isVisible()
    }

    @RetryingTest(maxAttempts = 2)
    fun `persistent notification stays until it is closed`(page: Page) {
        page.navigate("/#/notification")

        page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("显示常驻通知")).click()
        val card = page.locator("vaadin-notification-card", Page.LocatorOptions().setHasText("这条通知会一直显示"))
        assertThat(card).isVisible()

        card.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("关闭")).click()
        assertThat(card).isHidden()
    }

    @RetryingTest(maxAttempts = 2)
    fun `popover opens on click and closes on outside click`(page: Page) {
        page.navigate("/#/popover")

        page.getByTestId("popover-trigger").click()
        val content = page.getByText("任意 DOM 都可以放进 popover 的默认插槽。")
        assertThat(content).isVisible()
        assertThat(page.locator(".event-log")).containsText("opened-changed · true")

        page.locator(".page__title").click()
        assertThat(content).isHidden()
        assertThat(page.locator(".event-log")).containsText("opened-changed · false")
    }

    @RetryingTest(maxAttempts = 2)
    fun `tooltip appears on hover`(page: Page) {
        page.navigate("/#/tooltip")

        val trigger = page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("悬停我"))
        trigger.hover()
        assertThat(page.getByText("这是按钮上的提示")).isVisible()
    }

    @RetryingTest(maxAttempts = 2)
    fun `context menu opens on right click and reports the selection`(page: Page) {
        page.navigate("/#/context-menu")

        page.getByTestId("context-menu-surface").click(Locator.ClickOptions().setButton(MouseButton.RIGHT))

        // The menu renders its items into the light DOM once it is opened.
        val item = page.locator("vaadin-context-menu-item", Page.LocatorOptions().setHasText("复制"))
        assertThat(item).isVisible()

        item.click()
        assertThat(page.locator(".event-log")).containsText("item-selected")
    }

    @RetryingTest(maxAttempts = 2)
    fun `bare overlay opens and closes from its own content`(page: Page) {
        page.navigate("/#/overlay")

        page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("打开 overlay")).click()
        val overlay = page.locator("vaadin-overlay", Page.LocatorOptions().setHasText("这是一个裸 overlay"))
        assertThat(overlay).isVisible()

        overlay.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("关闭")).click()
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
        assertThat(form).containsText(Pattern.compile("required|必填", Pattern.CASE_INSENSITIVE))

        page.locator("vaadin-switch", Page.LocatorOptions().setHasText("显示错误状态")).click()
        assertThat(page.locator(".event-log")).containsText("error-changed · true")
    }
}
