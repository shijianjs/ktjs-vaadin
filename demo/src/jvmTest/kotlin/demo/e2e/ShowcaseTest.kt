package demo.e2e

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import com.microsoft.playwright.junit.UsePlaywright
import java.util.regex.Pattern
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.Timeout
import org.junitpioneer.jupiter.RetryingTest

/**
 * Port of `demo/e2e/tests/showcase.spec.ts`, driving the showcase through the demo kit's chrome.
 *
 * The shell is `<ktjs-demo-layout>`: a `vaadin-app-layout` with the page menu in the drawer slot, so
 * the drawer entries are `vaadin-side-nav-item[data-route]`, a group is a `vaadin-side-nav-item` without
 * a route, the active page carries `current`, and the top bar holds one `data-menu` button per menu
 * (`docs` / `lang` / `theme`) whose items are `data-action` buttons. Pages themselves keep the
 * `page__title` / `section__title` vocabulary regardless of whether they are built by the kit or by
 * the pre-kit builders.
 */
@UsePlaywright(DemoOptions::class)
class ShowcaseTest {
    private fun navItem(route: String): String = "vaadin-side-nav-item[data-route='$route']"

    private fun groupItem(page: Page, label: String): Locator =
        page.locator("vaadin-side-nav-item:not([data-route])", Page.LocatorOptions().setHasText(label))

    /** Expands the group holding [route] when it is folded and returns the item, without clicking it. */
    private fun revealNavItem(page: Page, route: String): Locator {
        val item = page.locator(navItem(route))
        if (!item.isVisible()) {
            val group = page.locator("vaadin-side-nav-item:not([data-route])").filter(
                Locator.FilterOptions().setHas(page.locator(navItem(route))),
            )
            group.click()
        }
        return item
    }

    private fun openNavItem(page: Page, route: String): Locator = revealNavItem(page, route).also { it.click() }

    /** Toggles a group header through the component's own header link. */
    private fun toggleGroup(group: Locator) {
        group.locator("[part='link']").first().click()
    }

    private fun openMenu(page: Page, menu: String) {
        page.locator("vaadin-menu-bar-item[data-menu='$menu']").click()
    }

    @BeforeTest
    fun openDemo(page: Page) {
        page.navigate("/")
        assertThat(page.locator(".page__title")).isVisible()
    }

    @RetryingTest(maxAttempts = 2)
    fun `renders the layout, the drawer groups and the component index`(page: Page) {
        assertThat(page).hasTitle(Pattern.compile("ktjs-vaadin"))
        assertThat(page.locator(".page__title")).hasText("Component overview")

        val groups = page.locator("vaadin-side-nav-item:not([data-route])").allTextContents()
        assertTrue(groups.size >= 8, "drawer groups: $groups")
        assertTrue(groups.any { it.startsWith("Input fields") })
        assertTrue(groups.any { it.startsWith("Data display") })

        // A group holding a single page is flattened: it would be a parent row whose only job is to
        // hold the page under it, so the page moves up and keeps the group's mark. The overview and
        // the theming page are those two, and neither may come back as a group row.
        assertTrue(groups.none { it.startsWith("Overview") }, "flattened overview group: $groups")
        assertTrue(groups.none { it.startsWith("Theming") }, "flattened theming group: $groups")
        assertThat(page.locator(navItem("overview"))).hasCount(1)
        assertThat(page.locator(navItem("theming"))).hasCount(1)

        // A top-level row must not declare `slot=children`, or the drawer would project it into the
        // previous group's body instead of placing it beside the groups.
        assertThat(page.locator("${navItem("overview")}[slot]")).hasCount(0)
        assertThat(page.locator("${navItem("overview")} iconify-icon")).hasCount(1)

        // Every component has a card in the index and an entry in the drawer. The overview is the page
        // itself, so the index lists the other pages.
        val cards = page.locator(".component-card").count()
        val navItems = page.locator("vaadin-side-nav-item[data-route]").count()
        assertTrue(cards > 45, "index cards: $cards")
        assertTrue(navItems > 45, "drawer items: $navItems")
        assertEquals(navItems, cards + 1, "one card per page except the overview")

        // Exactly one drawer entry is the current page.
        assertThat(page.locator("vaadin-side-nav-item[data-route][current]")).hasCount(1)
        assertThat(page.locator(navItem("overview"))).hasAttribute("current", "")
    }

    @RetryingTest(maxAttempts = 2)
    fun `filters the component index`(page: Page) {
        val search = page.locator("vaadin-text-field input").first()
        search.fill("virtual")
        assertThat(page.locator(".component-card:visible")).hasCount(1)
        assertThat(page.locator(".component-card:visible")).hasAttribute("data-route", "virtual-list")
        assertThat(page.locator(".readout").first()).containsText("1")

        search.fill("")
        assertThat(page.locator(".component-card:visible").first()).isVisible()
    }

    @RetryingTest(maxAttempts = 2)
    @Timeout(240)
    @Suppress("UNCHECKED_CAST")
    fun `every drawer entry opens a page with documentation, source and API links`(page: Page) {
        val entries = drawerEntries(page)
        assertTrue(entries.size > 45)

        val consoleErrors = mutableListOf<String>()
        page.onConsoleMessage { message ->
            if (message.type() == "error") consoleErrors += message.text()
        }

        for ((route, label) in entries) {
            page.evaluate("(target) => { window.location.hash = '#/' + target }", route)

            val pageRoot = page.locator(".page__title")
            assertThat(pageRoot).hasText(label)

            val links = page.locator(".page__link")
            assertThat(links).hasCount(3)

            val hrefs = links.evaluateAll("(nodes) => nodes.map((node) => node.getAttribute('href') || '')") as List<String>
            assertTrue(
                hrefs[0].matches(Regex("""https://vaadin\.com/docs/latest.*""")),
                "official docs link of $label",
            )
            assertTrue(
                hrefs[1].matches(Regex("""https://github\.com/shijianjs/ktjs-vaadin/blob/main/demo/src/jsMain/kotlin/demo/.*""")),
                "source link of $label",
            )
            assertEquals(expectedApiUrl(route), hrefs[2], "API link of $label")

            // Every page shows at least one runnable section.
            assertTrue(page.locator(".section").count() > 0, "sections of $label")

            // The drawer highlights exactly the active route.
            assertThat(page.locator("vaadin-side-nav-item[data-route][current]")).hasCount(1)
            assertThat(page.locator(navItem(route))).hasAttribute("current", "")
        }

        assertTrue(consoleErrors.isEmpty(), "console errors while walking every page: $consoleErrors")
    }

    @RetryingTest(maxAttempts = 2)
    fun `drawer groups fold and reopening shows their pages again`(page: Page) {
        // A multi-page group: single-page groups carry no parent row at all, so they cannot fold.
        val actionsGroup = groupItem(page, "Buttons & actions")

        // Every group starts expanded, so no page is more than one click away.
        assertThat(page.locator(navItem("button"))).isVisible()

        toggleGroup(actionsGroup)
        assertThat(page.locator(navItem("button"))).not().isVisible()

        toggleGroup(actionsGroup)
        assertThat(page.locator(navItem("button"))).isVisible()

        // A route change highlights its own row and keeps the group open.
        page.evaluate("() => { window.location.hash = '#/text-field' }")
        assertThat(page.locator(".page__title")).hasText("Text Field")
        assertThat(page.locator(navItem("text-field"))).hasAttribute("current", "")
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
        assertThat(page.locator(".page__title")).hasText("Component overview")
    }

    @RetryingTest(maxAttempts = 2)
    fun `clicking a drawer entry opens its page`(page: Page) {
        revealNavItem(page, "checkbox").click()
        assertThat(page.locator(".page__title")).hasText("Checkbox")
    }

    @RetryingTest(maxAttempts = 2)
    fun `lumo dark mode switches through the theme menu, persists and keeps the page`(page: Page) {
        val html = page.locator("html")
        assertThat(html).not().hasAttribute("theme", "dark")

        val lightScheme = page.evaluate("() => getComputedStyle(document.documentElement).colorScheme") as String
        assertTrue(lightScheme.contains("light"))

        openMenu(page, "theme")
        page.locator("[data-action='theme:dark']").click()
        assertThat(html).hasAttribute("theme", "dark")

        // The change comes from Lumo's `color-scheme: dark`, not from a hard-coded page background.
        val darkScheme = page.evaluate("() => getComputedStyle(document.documentElement).colorScheme") as String
        assertTrue(darkScheme.contains("dark"))
        val darkBackground = page.evaluate("() => getComputedStyle(document.body).backgroundColor") as String
        assertTrue(darkScheme.contains("dark") && darkBackground.isNotEmpty())

        // Navigation keeps the theme.
        openNavItem(page, "button")
        assertThat(page.locator(".page__title")).hasText("Button")
        assertThat(html).hasAttribute("theme", "dark")

        // A reload restores the stored preference before the first paint.
        page.reload()
        assertThat(html).hasAttribute("theme", "dark")
        assertThat(page.locator(".page__title")).hasText("Button")

        openMenu(page, "theme")
        page.locator("[data-action='theme:light']").click()
        assertThat(html).not().hasAttribute("theme", "dark")
    }

    @RetryingTest(maxAttempts = 2)
    fun `the drawer collapses on narrow screens and closes after navigation`(page: Page) {
        page.setViewportSize(720, 900)

        val toggle = page.locator("vaadin-drawer-toggle")
        assertThat(toggle).isVisible()
        // Below the breakpoint the app layout starts with the drawer closed and the toggle opens it.
        assertTrue(!drawerOpened(page), "the drawer starts closed")

        toggle.click()
        assertTrue(drawerOpened(page), "the toggle opens the drawer")

        // The open drawer is an overlay that slides in; click the entry itself rather than waiting for
        // its animation to settle inside Playwright's actionability window.
        revealNavItem(page, "text-field").evaluate("(item) => item.click()")
        assertThat(page.locator(".page__title")).hasText("Text Field")
        assertTrue(!drawerOpened(page), "navigating closes the drawer")

        toggle.click()
        assertTrue(drawerOpened(page), "the toggle opens the drawer again")
    }

    /** Reads the app layout's own state: the drawer element stays in the light DOM when closed. */
    private fun drawerOpened(page: Page): Boolean = page.evaluate(
        """() => document.querySelector('ktjs-demo-layout').shadowRoot
                .querySelector('vaadin-app-layout').drawerOpened""",
    ) as Boolean

    @RetryingTest(maxAttempts = 2)
    fun `language switch rewrites the copy, keeps the route and persists`(page: Page) {
        val html = page.locator("html")
        assertThat(html).hasAttribute("lang", "en")
        assertThat(page.locator(".page__title")).hasText("Component overview")

        openNavItem(page, "text-field")
        assertThat(page.locator(".page__title")).hasText("Text Field")

        openMenu(page, "lang")
        page.locator("[data-action='lang:zh-Hans']").click()

        // The page behind the kit contract rebuilds its copy; the chrome reads its own translations.
        assertThat(html).hasAttribute("lang", "zh-Hans")
        assertThat(page.locator(".page__title")).hasText("Text Field")
        assertThat(page.locator(".page__summary")).containsText("单行文本输入")
        assertThat(page.locator(".section__title").first()).hasText("基本用法")
        assertThat(page.locator("vaadin-breadcrumbs-item").first()).hasText(Pattern.compile("输入字段"))
        assertTrue(
            page.locator("vaadin-side-nav-item:not([data-route])").allTextContents().any { it.startsWith("输入字段") },
        )

        val docsHref = page.locator(".page__link").first().getAttribute("href") ?: ""
        assertTrue(docsHref.startsWith("https://vaadin.com/docs/latest/zh/components"), "Chinese docs link: $docsHref")

        // The choice is remembered and the route survives the reload.
        page.reload()
        assertThat(html).hasAttribute("lang", "zh-Hans")
        assertThat(page.locator(".page__title")).hasText("Text Field")
        assertThat(page.locator(".section__title").first()).hasText("基本用法")

        openMenu(page, "lang")
        page.locator("[data-action='lang:en']").click()
        assertThat(html).hasAttribute("lang", "en")
        assertThat(page.locator(".section__title").first()).hasText("Basic usage")
    }

    @Suppress("UNCHECKED_CAST")
    private fun drawerEntries(page: Page): List<Pair<String, String>> {
        val raw = page.locator("vaadin-side-nav-item[data-route]").evaluateAll(
            """(items) => items.map((item) => ({
                route: item.getAttribute('data-route') || '',
                label: (item.textContent || '').trim(),
            }))""",
        ) as List<Map<String, Any?>>
        return raw.map { (it["route"] as String) to (it["label"] as String) }
    }

    /** Dokka package page the demo links to; mirrors `componentApiUrl` in `Catalog.kt`. */
    private fun expectedApiUrl(route: String): String =
        when (route) {
            "overview" -> "api/ktjs-vaadin-web-components/index.html"
            "theming" -> "api/ktjs-vaadin-web-components/vaadin.lumostyles/index.html"
            else -> "api/ktjs-vaadin-web-components/vaadin.${route.replace("-", "")}/index.html"
        }
}
