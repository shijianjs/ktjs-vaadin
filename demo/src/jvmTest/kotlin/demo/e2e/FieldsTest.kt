package demo.e2e

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import com.microsoft.playwright.junit.UsePlaywright
import com.microsoft.playwright.options.AriaRole
import java.time.LocalDate
import java.time.ZoneOffset
import org.junitpioneer.jupiter.RetryingTest

/**
 * Port of `demo/e2e/tests/fields.spec.ts`: typing, validation, selection and the values each page
 * reads back.
 */
@UsePlaywright(DemoOptions::class)
class FieldsTest {
    @RetryingTest(maxAttempts = 2)
    fun `text field reflects typed input and runs validation`(page: Page) {
        page.navigate("/#/text-field")

        val field = page.getByTestId("text-field-basic")
        field.locator("input").fill("Ada")
        assertThat(page.locator(".readout", Page.LocatorOptions().setHasText("当前值"))).containsText("Ada")

        val validated = page.getByTestId("text-field-validated")
        validated.locator("input").fill("AB")
        page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("校验").setExact(true)).click()
        assertThat(page.locator(".readout", Page.LocatorOptions().setHasText("校验结果"))).containsText("失败")

        validated.locator("input").fill("abc")
        page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("校验").setExact(true)).click()
        assertThat(page.locator(".readout", Page.LocatorOptions().setHasText("校验结果"))).containsText("通过")
    }

    @RetryingTest(maxAttempts = 2)
    fun `text area counts characters and enforces required`(page: Page) {
        page.navigate("/#/text-area")

        val area = page.getByTestId("text-area-basic")
        area.locator("textarea").fill("你好")
        assertThat(page.locator(".readout", Page.LocatorOptions().setHasText("字数"))).containsText("2 / 200")

        page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("校验")).click()
        assertThat(page.locator(".readout", Page.LocatorOptions().setHasText("校验结果"))).containsText("失败")

        page.getByTestId("text-area-required").locator("textarea").fill("有内容了")
        page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("校验")).click()
        assertThat(page.locator(".readout", Page.LocatorOptions().setHasText("校验结果"))).containsText("通过")
    }

    @RetryingTest(maxAttempts = 2)
    fun `email field reports validity while typing`(page: Page) {
        page.navigate("/#/email-field")

        val field = page.getByTestId("email-field")
        field.locator("input").fill("not-an-email")
        assertThat(page.locator(".readout", Page.LocatorOptions().setHasText("valid"))).containsText("valid = false")

        field.locator("input").fill("ada@example.com")
        assertThat(page.locator(".readout", Page.LocatorOptions().setHasText("valid"))).containsText("valid = true")
    }

    @RetryingTest(maxAttempts = 2)
    fun `password field toggles the reveal button`(page: Page) {
        page.navigate("/#/password-field")

        val field = page.getByTestId("password-field")
        field.locator("input").fill("secret-123")
        assertThat(page.locator(".readout", Page.LocatorOptions().setHasText("passwordVisible")))
            .containsText("passwordVisible = false")

        field.locator("""[part~="reveal-button"]""").click()
        assertThat(page.locator(".readout", Page.LocatorOptions().setHasText("passwordVisible")))
            .containsText("passwordVisible = true")
    }

    @RetryingTest(maxAttempts = 2)
    fun `number field reports its value and custom range check`(page: Page) {
        page.navigate("/#/number-field")

        val field = page.getByTestId("number-field-basic")
        field.locator("input").fill("42")
        assertThat(page.locator(".readout", Page.LocatorOptions().setHasText("当前值"))).containsText("42")

        // min / max do not invalidate the field on their own; the page validates the value itself.
        val ranged = page.getByTestId("number-field-range")
        ranged.locator("input").fill("500")
        page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("校验")).click()
        assertThat(page.locator(".readout", Page.LocatorOptions().setHasText("校验结果"))).containsText("失败")

        ranged.locator("input").fill("50")
        page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("校验")).click()
        assertThat(page.locator(".readout", Page.LocatorOptions().setHasText("校验结果"))).containsText("通过")
    }

    @RetryingTest(maxAttempts = 2)
    fun `checkbox fires change on user input and checked-changed on code updates`(page: Page) {
        page.navigate("/#/checkbox")

        val box = page.getByTestId("checkbox-basic")
        box.locator("input").click()
        assertThat(box).hasJSProperty("checked", true)
        assertThat(page.locator(".event-log")).containsText("change · true")
        assertThat(page.locator(".readout", Page.LocatorOptions().setHasText("checked")))
            .containsText("checked = true")

        page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("程序化切换")).click()
        assertThat(box).hasJSProperty("checked", false)
        assertThat(page.locator(".event-log")).containsText("checked-changed（程序设置）· false")
    }

    @RetryingTest(maxAttempts = 2)
    fun `switch drives a dependent component`(page: Page) {
        page.navigate("/#/switch")

        page.getByTestId("switch-basic").locator("input").click()
        assertThat(page.locator(".readout", Page.LocatorOptions().setHasText("状态"))).containsText("已关闭")
    }

    @RetryingTest(maxAttempts = 2)
    fun `checkbox group reports the selected values as an array`(page: Page) {
        page.navigate("/#/checkbox-group")

        val group = page.getByTestId("checkbox-group")
        group.locator("vaadin-checkbox").first().locator("input").click()
        assertThat(page.locator(".readout", Page.LocatorOptions().setHasText("value ="))).containsText("kotlin")

        group.locator("vaadin-checkbox").nth(1).locator("input").click()
        assertThat(page.locator(".readout", Page.LocatorOptions().setHasText("value ="))).containsText("kotlin, java")
    }

    @RetryingTest(maxAttempts = 2)
    fun `radio group keeps a single selection`(page: Page) {
        page.navigate("/#/radio-group")

        val group = page.getByTestId("radio-group")
        group.locator("vaadin-radio-button", Locator.LocatorOptions().setHasText("专业版"))
            .locator("input")
            .click()
        assertThat(page.locator(".readout", Page.LocatorOptions().setHasText("value ="))).containsText("pro")
    }

    @RetryingTest(maxAttempts = 2)
    fun `select opens its overlay and updates the value`(page: Page) {
        page.navigate("/#/select")

        page.getByTestId("select-basic").click()
        val option = page.locator("vaadin-select-item", Page.LocatorOptions().setHasText("Berlin"))
        assertThat(option).isVisible()
        option.click()

        assertThat(page.locator(".readout", Page.LocatorOptions().setHasText("value ="))).containsText("berlin")
    }

    @RetryingTest(maxAttempts = 2)
    fun `combo box filters its items while typing`(page: Page) {
        page.navigate("/#/combo-box")

        val combo = page.getByTestId("combo-box-basic")
        combo.locator("input").fill("Grace")
        val option = page.locator("vaadin-combo-box-item", Page.LocatorOptions().setHasText("Grace Hopper"))
        assertThat(option).isVisible()
        option.click()

        assertThat(page.locator(".readout", Page.LocatorOptions().setHasText("value ="))).containsText("grace")
    }

    @RetryingTest(maxAttempts = 2)
    fun `slider follows keyboard input`(page: Page) {
        page.navigate("/#/slider")

        val slider = page.getByTestId("slider-basic")
        slider.locator("input").first().focus()
        page.keyboard().press("ArrowRight")

        assertThat(page.locator(".readout", Page.LocatorOptions().setHasText("数值")).first()).containsText("45")
    }

    @RetryingTest(maxAttempts = 2)
    fun `date picker opens its calendar and selects through the today button`(page: Page) {
        page.navigate("/#/date-picker")

        val picker = page.getByTestId("date-picker-basic")
        picker.locator("""[part~="toggle-button"]""").click()

        // Every picker on the page owns an overlay element; only the open one carries `opened`.
        val overlay = page.locator("vaadin-date-picker-overlay[opened]")
        assertThat(overlay).isVisible()

        // Locale-independent interaction: pick "today" from the overlay and compare with the value the
        // page read back. The context pins the time zone, so both sides agree.
        page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("Today").setExact(true)).click()

        val today = LocalDate.now(ZoneOffset.UTC).toString()
        assertThat(page.locator(".readout", Page.LocatorOptions().setHasText("value ="))).containsText(today)
        assertThat(overlay).isHidden()
    }
}
