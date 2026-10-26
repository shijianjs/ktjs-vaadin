package demo.e2e

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import com.microsoft.playwright.junit.UsePlaywright
import java.util.regex.Pattern
import kotlin.test.BeforeTest
import kotlin.test.assertTrue
import org.junit.jupiter.api.Timeout
import org.junitpioneer.jupiter.RetryingTest

/**
 * Port of `demo/e2e/tests/showcase.spec.ts`.
 *
 * The suite only drives the public demo page through Playwright's locator API, so the TypeScript
 * spec translates one to one. Assertions that must wait for the page keep using Playwright's
 * auto-retrying `assertThat`; plain value checks use `kotlin.test`.
 */
@UsePlaywright(DemoOptions::class)
class ShowcaseTest {
    private fun navItem(route: String): String = ".shell__nav vaadin-side-nav-item[data-route='$route']"

    private fun sidebarGroup(page: Page, route: String): Locator =
        page.locator(".nav__group:has(vaadin-side-nav-item[data-route='$route'])")

    /** Expands the group of [route] when it is folded and returns the item, still without clicking it. */
    private fun revealNavItem(page: Page, route: String): Locator {
        val group = sidebarGroup(page, route)
        val toggle = group.locator(".nav__group-title")
        if (toggle.getAttribute("aria-expanded") != "true") {
            toggle.click()
        }
        return group.locator("vaadin-side-nav-item[data-route='$route']")
    }

    private fun openNavItem(page: Page, route: String): Locator = revealNavItem(page, route).also { it.click() }

    @BeforeTest
    fun openDemo(page: Page) {
        page.navigate("/")
        assertThat(page.locator(".page__title")).isVisible()
    }

    @RetryingTest(maxAttempts = 2)
    fun `renders the shell, the sidebar groups and the component index`(page: Page) {
        assertThat(page).hasTitle(Pattern.compile("ktjs-vaadin"))
        assertThat(page.locator(".page__title")).hasText("组件总览")

        val groups = page.locator(".nav__group-title").allTextContents()
        assertTrue(groups.size >= 9)
        assertTrue(groups.contains("输入字段"))
        assertTrue(groups.contains("数据展示"))

        // Every component has a card in the index and an entry in the sidebar.
        val cards = page.locator(".component-card").count()
        val navItems = page.locator(".shell__nav vaadin-side-nav-item").count()
        assertTrue(cards > 45)
        assertTrue(navItems > 45)
        assertTrue(navItems >= cards)

        // Exactly one sidebar entry is marked as the current page.
        assertThat(page.locator(".shell__nav vaadin-side-nav-item.nav__current")).hasCount(1)
        assertThat(page.locator(navItem("overview"))).hasClass(Pattern.compile("nav__current"))
    }

    @RetryingTest(maxAttempts = 2)
    fun `filters the component index`(page: Page) {
        val search = page.locator(".page__body input").first()
        search.fill("grid")
        assertThat(page.locator(".component-card:visible")).hasCount(1)
        assertThat(page.locator(".component-card:visible")).hasAttribute("data-route", "grid")
        assertThat(page.locator(".readout").first()).containsText("1")

        search.fill("")
        assertThat(page.locator(".component-card:visible").first()).isVisible()
    }

    @RetryingTest(maxAttempts = 2)
    @Timeout(240)
    @Suppress("UNCHECKED_CAST")
    fun `every sidebar entry opens a page with documentation and source links`(page: Page) {
        val entries = sidebarEntries(page)
        assertTrue(entries.size > 45)

        val consoleErrors = mutableListOf<String>()
        page.onConsoleMessage { message ->
            if (message.type() == "error") consoleErrors += message.text()
        }

        for ((route, label) in entries) {
            page.evaluate("(target) => { window.location.hash = '#/' + target }", route)

            val pageRoot = page.locator(".page")
            assertThat(pageRoot.locator(".page__title")).hasText(label)

            val links = pageRoot.locator(".page__link")
            assertThat(links).hasCount(2)

            val hrefs = links.evaluateAll("(nodes) => nodes.map((node) => node.getAttribute('href') || '')") as List<String>
            assertTrue(
                hrefs[0].matches(Regex("""https://vaadin\.com/docs/latest/zh/.*""")),
                "official docs link of $label",
            )
            assertTrue(
                hrefs[1].matches(Regex("""https://github\.com/shijianjs/ktjs-vaadin/blob/main/demo/src/jsMain/kotlin/demo/.*""")),
                "source link of $label",
            )

            // Every page shows at least one runnable section.
            assertTrue(pageRoot.locator(".section").count() > 0, "sections of $label")

            // The sidebar highlights exactly the active route.
            assertThat(page.locator(".shell__nav vaadin-side-nav-item.nav__current")).hasCount(1)
            assertThat(page.locator(navItem(route))).hasClass(Pattern.compile("nav__current"))
        }

        assertTrue(consoleErrors.isEmpty(), "console errors while walking every page: $consoleErrors")
    }

    @RetryingTest(maxAttempts = 2)
    fun `sidebar groups fold and the active route expands its group`(page: Page) {
        val overviewToggle = page.locator(".nav__group-title[data-group='概览']")
        assertThat(overviewToggle).hasAttribute("aria-expanded", "true")
        assertThat(page.locator(navItem("overview"))).isVisible()

        val fieldsToggle = page.locator(".nav__group-title[data-group='输入字段']")
        assertThat(fieldsToggle).hasAttribute("aria-expanded", "false")
        assertThat(page.locator(navItem("text-field"))).not().isVisible()

        overviewToggle.click()
        assertThat(overviewToggle).hasAttribute("aria-expanded", "false")
        assertThat(page.locator(navItem("overview"))).not().isVisible()

        overviewToggle.click()
        assertThat(overviewToggle).hasAttribute("aria-expanded", "true")
        assertThat(page.locator(navItem("overview"))).isVisible()

        // Navigating to a folded group reveals the page that was opened.
        page.evaluate("() => { window.location.hash = '#/text-field' }")
        assertThat(page.locator(".page__title")).hasText("Text Field")
        assertThat(fieldsToggle).hasAttribute("aria-expanded", "true")
        assertThat(page.locator(navItem("text-field"))).isVisible()
    }

    @RetryingTest(maxAttempts = 2)
    fun `history navigation moves between pages`(page: Page) {
        openNavItem(page, "button")
        assertThat(page.locator(".page__title")).hasText("Button")

        openNavItem(page, "text-field")
        assertThat(page.locator(".page__title")).hasText("Text Field")

        page.goBack()
        assertThat(page.locator(".page__title")).hasText("Button")

        page.goForward()
        assertThat(page.locator(".page__title")).hasText("Text Field")
    }

    @RetryingTest(maxAttempts = 2)
    fun `an unknown route falls back to the overview`(page: Page) {
        page.evaluate("() => { window.location.hash = '#/this-component-does-not-exist' }")
        assertThat(page.locator(".page__title")).hasText("组件总览")
    }

    @RetryingTest(maxAttempts = 2)
    fun `keyboard activation opens a sidebar entry`(page: Page) {
        val item = revealNavItem(page, "checkbox")
        item.focus()
        page.keyboard().press("Enter")
        assertThat(page.locator(".page__title")).hasText("Checkbox")
    }

    @RetryingTest(maxAttempts = 2)
    fun `lumo dark mode switches, persists and does not reset the current page`(page: Page) {
        val html = page.locator("html")
        assertThat(html).not().hasAttribute("theme", "dark")

        val lightBackground = page.evaluate("() => getComputedStyle(document.body).backgroundColor") as String
        val lightScheme = page.evaluate("() => getComputedStyle(document.documentElement).colorScheme") as String
        assertTrue(lightScheme.contains("light"))

        page.locator(".theme-switch vaadin-button", Page.LocatorOptions().setHasText("暗色")).click()
        assertThat(html).hasAttribute("theme", "dark")

        // The change comes from Lumo's `color-scheme: dark`, not from a hard-coded page background.
        val darkScheme = page.evaluate("() => getComputedStyle(document.documentElement).colorScheme") as String
        assertTrue(darkScheme.contains("dark"))
        val darkBackground = page.evaluate("() => getComputedStyle(document.body).backgroundColor") as String
        assertTrue(darkBackground != lightBackground)

        // Navigation keeps the theme.
        openNavItem(page, "button")
        assertThat(page.locator(".page__title")).hasText("Button")
        assertThat(html).hasAttribute("theme", "dark")

        // A reload restores the stored preference before the first paint.
        page.reload()
        assertThat(html).hasAttribute("theme", "dark")
        assertThat(page.locator(".page__title")).hasText("Button")

        page.locator(".theme-switch vaadin-button", Page.LocatorOptions().setHasText("亮色")).click()
        assertThat(html).not().hasAttribute("theme", "dark")
    }

    @RetryingTest(maxAttempts = 2)
    fun `the sidebar collapses into a drawer on narrow screens`(page: Page) {
        page.setViewportSize(720, 900)

        val nav = page.locator(".shell__nav")
        val menuButton = page.locator(".topbar__menu")
        assertThat(menuButton).isVisible()
        assertThat(nav).not().hasClass(Pattern.compile("shell__nav--open"))

        menuButton.click()
        assertThat(nav).hasClass(Pattern.compile("shell__nav--open"))

        // Navigating closes the drawer again.
        openNavItem(page, "text-field")
        assertThat(page.locator(".page__title")).hasText("Text Field")
        assertThat(nav).not().hasClass(Pattern.compile("shell__nav--open"))
    }

    @Suppress("UNCHECKED_CAST")
    private fun sidebarEntries(page: Page): List<Pair<String, String>> {
        val raw = page.locator(".shell__nav vaadin-side-nav-item").evaluateAll(
            """(items) => items.map((item) => ({
                route: item.getAttribute('data-route') || '',
                label: (item.textContent || '').trim(),
            }))""",
        ) as List<Map<String, Any?>>
        return raw.map { (it["route"] as String) to (it["label"] as String) }
    }
}
