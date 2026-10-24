import { expect, test } from '@playwright/test'

/** Data and layout behaviour: grid, tabs, accordion, markdown and the display widgets. */

test('grid renders the items and reports the selection', async ({ page }) => {
  await page.goto('/#/grid')

  const grid = page.getByTestId('grid-basic')
  await expect(grid).toContainText('Ada Lovelace')
  await expect(grid).toContainText('Grace Hopper')

  const cells = grid.locator('vaadin-grid-cell-content')
  expect(await cells.count()).toBeGreaterThan(10)

  await grid.locator('vaadin-checkbox').last().click()
  await expect(page.locator('.readout', { hasText: '已选' })).toContainText('已选 1 行')
})

test('grid sort column flips its direction on header click', async ({ page }) => {
  await page.goto('/#/grid')

  const sorted = page.getByTestId('grid-sorted')
  const column = sorted.locator('vaadin-grid-sort-column').first()
  await expect(column).toHaveJSProperty('direction', 'asc')

  await sorted.getByText('姓名', { exact: true }).first().click()
  await expect(column).toHaveJSProperty('direction', 'desc')
})

test('tabs switch the selected index', async ({ page }) => {
  await page.goto('/#/tabs')

  const tabs = page.getByTestId('tabs')
  await tabs.locator('vaadin-tab', { hasText: '设置' }).click()
  await expect(tabs).toHaveJSProperty('selected', 2)
  await expect(page.locator('.event-log')).toContainText('selected-changed · 2')
})

test('tab sheet swaps the visible panel', async ({ page }) => {
  await page.goto('/#/tabsheet')

  const sheet = page.getByTestId('tab-sheet')
  const firstPanel = sheet.getByText('这一页的内容由第一个面板提供。')
  const thirdPanel = sheet.getByText('第三个面板。')

  await expect(firstPanel).toBeVisible()
  await expect(thirdPanel).toBeHidden()

  await sheet.locator('vaadin-tab', { hasText: '权限' }).click()
  await expect(thirdPanel).toBeVisible()
  await expect(firstPanel).toBeHidden()
  await expect(page.locator('.readout', { hasText: 'selected' })).toContainText('selected = 2')
  await expect(sheet).not.toHaveAttribute('loading', '')
})

test('accordion expands a panel and can be driven programmatically', async ({ page }) => {
  await page.goto('/#/accordion')

  const accordion = page.getByTestId('accordion')
  const panel = accordion.locator('vaadin-accordion-panel', { hasText: '付款方式' })
  await expect(panel.getByText('面板二的内容')).toBeHidden()

  await panel.locator('vaadin-accordion-heading').click()
  await expect(panel.getByText('面板二的内容')).toBeVisible()

  await page.getByRole('button', { name: '展开第一项' }).click()
  await expect(page.locator('.readout', { hasText: 'opened' })).toContainText('opened = 0')
})

test('details toggles through its summary', async ({ page }) => {
  await page.goto('/#/details')

  const details = page.getByTestId('details')
  await expect(details).toHaveJSProperty('opened', false)

  await details.locator('vaadin-details-summary').click()
  await expect(details).toHaveJSProperty('opened', true)
  await expect(page.locator('.event-log')).toContainText('opened-changed · true')
})

test('markdown renders headings, emphasis and lists', async ({ page }) => {
  await page.goto('/#/markdown')

  const markdown = page.getByTestId('markdown')
  await expect(markdown.locator('h1')).toHaveText('标题')
  await expect(markdown.locator('strong')).toHaveText('粗体')
  await expect(markdown.locator('li')).toHaveCount(2)
})

test('virtual list renders visible rows and can jump', async ({ page }) => {
  await page.goto('/#/virtual-list')

  const lists = page.locator('vaadin-virtual-list')
  await expect(lists.first()).toContainText('第 1 行')

  await page.getByRole('button', { name: '跳到第 300 行' }).click()
  await expect(lists.nth(1)).toContainText('行 300')
})

test('badge updates its number in place', async ({ page }) => {
  await page.goto('/#/badge')

  await expect(page.getByTestId('badge').locator('[part="number"]')).toHaveText('4')

  const live = page.getByTestId('badge-live').locator('[part="number"]')
  await expect(live).toHaveText('0')
  await page.getByRole('button', { name: '加一' }).click()
  await expect(live).toHaveText('1')
})

test('progress bar advances and can be indeterminate', async ({ page }) => {
  await page.goto('/#/progress-bar')

  await page.getByRole('button', { name: '+10' }).click()
  await expect(page.locator('.readout', { hasText: 'value' })).toContainText('45')

  await page.locator('vaadin-switch', { hasText: 'indeterminate' }).click()
  await expect(page.locator('vaadin-progress-bar').first()).toHaveJSProperty('indeterminate', false)
})

test('icon renders an inline svg from the lumo set', async ({ page }) => {
  await page.goto('/#/icon')

  const icon = page.locator('.page vaadin-icon').first()
  await expect(icon).toHaveAttribute('icon', 'lumo:checkmark')
  await expect(icon.locator('svg')).toHaveCount(1)
})
