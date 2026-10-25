package demo.e2e

import com.microsoft.playwright.Browser
import com.microsoft.playwright.assertions.PlaywrightAssertions
import com.microsoft.playwright.junit.Options
import com.microsoft.playwright.junit.OptionsFactory
import java.nio.file.Path

/**
 * Mirrors the Playwright Test configuration that used to live in `demo/e2e/playwright.config.ts`.
 */
class DemoOptions : OptionsFactory {
    override fun getOptions(): Options {
        // Milliseconds, not seconds: the Playwright Test config's `expect.timeout: 7000`.
        PlaywrightAssertions.setDefaultAssertionTimeout(7_000.0)
        return Options()
            .setBaseUrl(DemoServer.baseUrl)
            .setTestIdAttribute("data-test")
            .setTrace(Options.Trace.RETAIN_ON_FAILURE)
            .setOutputDir(Path.of(System.getProperty("e2e.output", "build/e2e-results")))
            .setContextOptions(
                Browser.NewContextOptions()
                    .setLocale("en-US")
                    .setTimezoneId("UTC")
                    .setViewportSize(1280, 900),
            )
    }
}
