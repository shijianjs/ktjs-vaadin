import { expect, test } from '@playwright/test'

/**
 * Shell-level behaviour: the sidebar index, route coverage, history navigation and the Lumo theme
 * switch. The route sweep is data-driven from the sidebar, so a component page that is added
 * without a catalogue entry (or the other way round) fails here.
 */

const navItem = (route: string) => `.shell__nav vaadin-side-nav-item[data-route="${route}"]`

test.beforeEach(async ({ page }) => {
  await page.goto('/')
  await expect(page.locator('.page__title')).toBeVisible()
})

test('renders the shell, the sidebar groups and the component index', async ({ page }) => {
  await expect(page).toHaveTitle(/ktjs-vaadin/)
  await expect(page.locator('.page__title')).toHaveText('组件总览')

  const groups = await page.locator('.nav__group-title').allTextContents()
  expect(groups.length).toBeGreaterThanOrEqual(9)
  expect(groups).toContain('输入字段')
  expect(groups).toContain('数据展示')

  // Every component has a card in the index and an entry in the sidebar.
  const cards = await page.locator('.component-card').count()
  const navItems = await page.locator('.shell__nav vaadin-side-nav-item').count()
  expect(cards).toBeGreaterThan(45)
  expect(navItems).toBeGreaterThan(45)
  expect(navItems).toBeGreaterThanOrEqual(cards)

  // Exactly one sidebar entry is marked as the current page.
  await expect(page.locator('.shell__nav vaadin-side-nav-item.nav__current')).toHaveCount(1)
  await expect(page.locator(navItem('overview'))).toHaveClass(/nav__current/)
})

test('filters the component index', async ({ page }) => {
  const search = page.locator('.page__body input').first()
  await search.fill('grid')
  await expect(page.locator('.component-card:visible')).toHaveCount(1)
  await expect(page.locator('.component-card:visible')).toHaveAttribute('data-route', 'grid')
  await expect(page.locator('.readout').first()).toContainText('1')

  await search.fill('')
  await expect(page.locator('.component-card:visible').first()).toBeVisible()
})

test('every sidebar entry opens a page with documentation and source links', async ({ page }) => {
  test.setTimeout(240_000)

  const entries = await page.locator('.shell__nav vaadin-side-nav-item').evaluateAll((items) =>
    items.map((item) => ({
      route: item.getAttribute('data-route') ?? '',
      label: (item.textContent ?? '').trim(),
    })),
  )
  expect(entries.length).toBeGreaterThan(45)

  const consoleErrors: string[] = []
  page.on('console', (message) => {
    if (message.type() === 'error') consoleErrors.push(message.text())
  })

  for (const entry of entries) {
    await page.evaluate((target) => {
      window.location.hash = `#/${target}`
    }, entry.route)

    const pageRoot = page.locator('.page')
    await expect(pageRoot.locator('.page__title')).toHaveText(entry.label)

    const links = pageRoot.locator('.page__link')
    await expect(links).toHaveCount(2)

    const hrefs = await links.evaluateAll((nodes) => nodes.map((node) => node.getAttribute('href') ?? ''))
    expect(hrefs[0], `official docs link of ${entry.label}`).toMatch(
      /^https:\/\/vaadin\.com\/docs\/latest\/zh\//,
    )
    expect(hrefs[1], `source link of ${entry.label}`).toMatch(
      /^https:\/\/github\.com\/shijianjs\/ktjs-vaadin\/blob\/main\/demo\/src\/jsMain\/kotlin\/demo\//,
    )

    // Every page shows at least one runnable section.
    expect(await pageRoot.locator('.section').count(), `sections of ${entry.label}`).toBeGreaterThan(0)

    // The sidebar highlights exactly the active route.
    await expect(page.locator('.shell__nav vaadin-side-nav-item.nav__current')).toHaveCount(1)
    await expect(page.locator(navItem(entry.route))).toHaveClass(/nav__current/)
  }

  expect(consoleErrors, 'console errors while walking every page').toEqual([])
})

test('history navigation moves between pages', async ({ page }) => {
  await page.locator(navItem('button')).click()
  await expect(page.locator('.page__title')).toHaveText('Button')

  await page.locator(navItem('text-field')).click()
  await expect(page.locator('.page__title')).toHaveText('Text Field')

  await page.goBack()
  await expect(page.locator('.page__title')).toHaveText('Button')

  await page.goForward()
  await expect(page.locator('.page__title')).toHaveText('Text Field')
})

test('an unknown route falls back to the overview', async ({ page }) => {
  await page.evaluate(() => {
    window.location.hash = '#/this-component-does-not-exist'
  })
  await expect(page.locator('.page__title')).toHaveText('组件总览')
})

test('keyboard activation opens a sidebar entry', async ({ page }) => {
  const item = page.locator(navItem('checkbox'))
  await item.focus()
  await page.keyboard.press('Enter')
  await expect(page.locator('.page__title')).toHaveText('Checkbox')
})

test('lumo dark mode switches, persists and does not reset the current page', async ({ page }) => {
  const html = page.locator('html')
  await expect(html).not.toHaveAttribute('theme', 'dark')

  const lightBackground = await page.evaluate(() => getComputedStyle(document.body).backgroundColor)
  const lightScheme = await page.evaluate(() => getComputedStyle(document.documentElement).colorScheme)
  expect(lightScheme).toContain('light')

  await page.locator('.theme-switch vaadin-button', { hasText: '暗色' }).click()
  await expect(html).toHaveAttribute('theme', 'dark')

  // The change comes from Lumo's `color-scheme: dark`, not from a hard-coded page background.
  const darkScheme = await page.evaluate(() => getComputedStyle(document.documentElement).colorScheme)
  expect(darkScheme).toContain('dark')
  const darkBackground = await page.evaluate(() => getComputedStyle(document.body).backgroundColor)
  expect(darkBackground).not.toBe(lightBackground)

  // Navigation keeps the theme.
  await page.locator(navItem('button')).click()
  await expect(page.locator('.page__title')).toHaveText('Button')
  await expect(html).toHaveAttribute('theme', 'dark')

  // A reload restores the stored preference before the first paint.
  await page.reload()
  await expect(html).toHaveAttribute('theme', 'dark')
  await expect(page.locator('.page__title')).toHaveText('Button')

  await page.locator('.theme-switch vaadin-button', { hasText: '亮色' }).click()
  await expect(html).not.toHaveAttribute('theme', 'dark')
})

test('the sidebar collapses into a drawer on narrow screens', async ({ page }) => {
  await page.setViewportSize({ width: 720, height: 900 })

  const nav = page.locator('.shell__nav')
  const menuButton = page.locator('.topbar__menu')
  await expect(menuButton).toBeVisible()
  await expect(nav).not.toHaveClass(/shell__nav--open/)

  await menuButton.click()
  await expect(nav).toHaveClass(/shell__nav--open/)

  // Navigating closes the drawer again.
  await nav.locator('vaadin-side-nav-item[data-route="text-field"]').click()
  await expect(page.locator('.page__title')).toHaveText('Text Field')
  await expect(nav).not.toHaveClass(/shell__nav--open/)
})
