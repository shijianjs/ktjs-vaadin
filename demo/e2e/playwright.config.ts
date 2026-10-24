import { defineConfig } from '@playwright/test'

const port = Number(process.env.PORT ?? 4173)

/**
 * The demo is a static bundle, so the suite drives the production build exactly as GitHub Pages
 * serves it. `webServer` starts the tiny static server in `tools/serve.mjs`; `DEMO_DIST` is passed
 * by the Gradle `e2eTest` task and defaults to `demo/build/dist/js/productionExecutable`.
 */
export default defineConfig({
  testDir: './tests',
  fullyParallel: false,
  workers: 1,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 1 : 0,
  reporter: process.env.CI ? [['list'], ['html', { open: 'never' }]] : [['list']],
  timeout: 30_000,
  expect: { timeout: 7_000 },

  use: {
    baseURL: `http://127.0.0.1:${port}`,
    viewport: { width: 1280, height: 900 },
    testIdAttribute: 'data-test',
    locale: 'en-US',
    timezoneId: 'UTC',
    trace: 'retain-on-failure',
  },

  webServer: {
    command: 'node tools/serve.mjs',
    url: `http://127.0.0.1:${port}/`,
    reuseExistingServer: !process.env.CI,
    timeout: 30_000,
    env: {
      PORT: String(port),
      DEMO_DIST: process.env.DEMO_DIST ?? '',
    },
  },
})
