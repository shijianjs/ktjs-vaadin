package demo.e2e

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import com.microsoft.playwright.junit.UsePlaywright
import com.microsoft.playwright.options.AriaRole
import kotlin.test.assertTrue
import org.junitpioneer.jupiter.RetryingTest

/** Interactions of the layout and navigation pages: app layout, form layout, split layout, scroller. */
@UsePlaywright(DemoOptions::class)
class LayoutsTest {
    @RetryingTest(maxAttempts = 2)
    fun `app layout drawer toggles and reports its state`(page: Page) {
        // The drawer turns into an overlay at viewport widths up to 800px.
        page.setViewportSize(780, 900)
        page.navigate("/#/app-layout")

        val layout = page.getByTestId("app-layout")
        val opened = layout.evaluate("el => el.drawerOpened") as Boolean

        // Keep the toggle clear of the sticky demo topbar, which would otherwise receive the click.
        layout.evaluate(
            "el => window.scrollTo(0, el.getBoundingClientRect().top + window.scrollY - 160)",
        )
        layout.locator("vaadin-drawer-toggle").click()

        assertThat(layout).hasJSProperty("drawerOpened", !opened)
        assertThat(page.locator(".readout", Page.LocatorOptions().setHasText("drawerOpened")))
            .containsText("drawerOpened = ${!opened}")
    }

    @RetryingTest(maxAttempts = 2)
    fun `app layout switches the primary section`(page: Page) {
        page.navigate("/#/app-layout")

        val layout = page.getByTestId("app-layout-placement")
        assertThat(layout).hasJSProperty("primarySection", "drawer")

        page.getByRole(AriaRole.BUTTON, Page.GetByRoleOptions().setName("Toggle primarySection")).click()

        assertThat(layout).hasJSProperty("primarySection", "navbar")
        assertThat(page.locator(".readout", Page.LocatorOptions().setHasText("primarySection")))
            .containsText("primarySection = navbar")
    }

    @RetryingTest(maxAttempts = 2)
    fun `form layout groups rows and spans columns`(page: Page) {
        page.navigate("/#/form-layout")

        val form = page.getByTestId("form-layout")
        assertThat(form.locator("vaadin-form-row")).hasCount(3)
        assertThat(form.locator("vaadin-form-row").first().locator("input")).hasCount(2)

        val aside = page.getByTestId("form-labels-aside")
        assertThat(aside.locator("vaadin-form-item").first()).containsText("First name")

        val colspan = page.getByTestId("form-colspan")
        assertThat(colspan.locator("[data-colspan='3']")).hasCount(1)
        assertThat(colspan.locator("vaadin-form-row")).hasCount(3)
    }

    @RetryingTest(maxAttempts = 2)
    fun `split layout collapses and expands its sidebar`(page: Page) {
        page.navigate("/#/split-layout")

        val sidebar = page.getByTestId("split-sidebar")
        val button = page.getByTestId("split-collapse-button")
        val expanded = (sidebar.evaluate("el => el.getBoundingClientRect().width") as Number).toDouble()
        assertThat(button.locator("vaadin-icon")).hasAttribute("icon", "vaadin:arrow-left")

        button.click()

        val collapsed = (sidebar.evaluate("el => el.getBoundingClientRect().width") as Number).toDouble()
        assertTrue(collapsed < expanded / 2, "sidebar shrinks from $expanded to $collapsed")
        assertThat(button.locator("vaadin-icon")).hasAttribute("icon", "vaadin:arrow-right")

        button.click()
        assertThat(button.locator("vaadin-icon")).hasAttribute("icon", "vaadin:arrow-left")
    }

    @RetryingTest(maxAttempts = 2)
    fun `scroller overflows in both directions`(page: Page) {
        page.navigate("/#/scroller")

        val scroller = page.getByTestId("scroller-both")
        val overflows =
            scroller.evaluate(
                "el => el.scrollWidth > el.clientWidth && el.scrollHeight > el.clientHeight",
            ) as Boolean
        assertTrue(overflows, "the illustration is larger than the scroller")
    }

    @RetryingTest(maxAttempts = 2)
    fun `app layout navigation links stay on the hash route`(page: Page) {
        page.navigate("/#/app-layout")

        page.getByTestId("app-layout-navbar")
            .getByRole(AriaRole.LINK, Locator.GetByRoleOptions().setName("Orders"))
            .click()

        assertTrue(page.url().endsWith("#/app-layout"), "stays on the demo route: ${page.url()}")
        assertThat(page.getByTestId("app-layout")).isVisible()
    }

    @RetryingTest(maxAttempts = 2)
    fun `side nav intercepts same-origin navigation`(page: Page) {
        page.navigate("/#/side-nav")

        val nav = page.getByTestId("side-nav")
        nav.locator("vaadin-side-nav-item", Locator.LocatorOptions().setHasText("Inbox")).click()

        assertTrue(page.url().endsWith("#/side-nav"), "stays on the demo route: ${page.url()}")
        assertThat(page.locator(".event-log")).containsText("onNavigate · /inbox")
    }

    @RetryingTest(maxAttempts = 2)
    fun `side nav highlights the item matching the current path`(page: Page) {
        page.navigate("/#/side-nav")

        val items = page.getByTestId("side-nav-current").locator("vaadin-side-nav-item")
        assertThat(items.first()).hasJSProperty("current", true)
        assertThat(items.first().locator("a")).hasAttribute("aria-current", "page")
        assertThat(items.nth(1)).hasJSProperty("current", false)
    }

    @RetryingTest(maxAttempts = 2)
    fun `side nav collapses a labelled list`(page: Page) {
        page.navigate("/#/side-nav")

        val nav = page.getByTestId("side-nav-collapsible")
        nav.evaluate("el => { el.collapsed = true }")

        assertThat(page.locator(".readout", Page.LocatorOptions().setHasText("collapsed")))
            .containsText("collapsed = true")
    }

    @RetryingTest(maxAttempts = 2)
    fun `breadcrumbs mark the last item as the current page`(page: Page) {
        page.navigate("/#/breadcrumbs")

        val items = page.getByTestId("breadcrumbs").locator("vaadin-breadcrumbs-item")
        assertThat(items.last()).hasJSProperty("current", true)
        assertThat(items.last().locator("[part='nolink']")).hasAttribute("aria-current", "page")
        assertThat(items.first()).hasJSProperty("current", false)
    }

    @RetryingTest(maxAttempts = 2)
    fun `breadcrumbs collapse leading items into the overflow menu`(page: Page) {
        page.navigate("/#/breadcrumbs")

        val crumbs = page.getByTestId("breadcrumbs-overflow")
        val catalog = crumbs.locator("vaadin-breadcrumbs-item", Locator.LocatorOptions().setHasText("Catalog"))
        assertThat(crumbs).hasAttribute("has-overflow", "")
        assertThat(catalog).isHidden()

        crumbs.locator("[part='overflow-button']").click()
        assertThat(catalog).isVisible()
    }

    @RetryingTest(maxAttempts = 2)
    fun `breadcrumbs localize the overflow button label`(page: Page) {
        page.navigate("/#/breadcrumbs")

        val crumbs = page.getByTestId("breadcrumbs-i18n")
        assertThat(crumbs.locator("[part='overflow-button']")).hasAttribute("aria-label", "Show hidden trail")
    }
}
