import { expect, test } from '@playwright/test'

/** Overlay behaviour: open / close, results, and the events each overlay reports. */

test('dialog opens with rendered content and closes from its footer', async ({ page }) => {
  await page.goto('/#/dialog')

  await page.getByRole('button', { name: '打开对话框' }).click()
  const overlay = page.locator('vaadin-dialog-overlay[opened]')
  await expect(overlay).toBeVisible()

  // Dialog content and title live in the overlay's shadow root, so they are asserted by text.
  await expect(page.getByText('对话框内容由 renderer 提供', { exact: false })).toBeVisible()
  await expect(page.getByText('示例对话框')).toBeVisible()

  await page.getByRole('button', { name: '关闭', exact: true }).click()
  await expect(overlay).toBeHidden()
  await expect(page.locator('.event-log')).toContainText('closed')
})

test('dialog can refuse Esc and outside clicks', async ({ page }) => {
  await page.goto('/#/dialog')

  await page.getByRole('button', { name: '打开（自定义尺寸）' }).click()
  const overlay = page.locator('vaadin-dialog-overlay[opened]')
  await expect(page.getByText('只能用下面的按钮关闭')).toBeVisible()

  await page.keyboard.press('Escape')
  await expect(overlay).toBeVisible()

  await page.getByRole('button', { name: '关闭', exact: true }).click()
  await expect(overlay).toBeHidden()
})

test('confirm dialog reports confirm, reject and cancel', async ({ page }) => {
  await page.goto('/#/confirm-dialog')

  await page.getByRole('button', { name: '删除记录' }).click()
  const overlay = page.locator('vaadin-confirm-dialog-overlay[opened]')
  await expect(overlay).toBeVisible()
  await expect(page.getByText('删除这条记录？')).toBeVisible()

  await page.getByRole('button', { name: '删除', exact: true }).click()
  await expect(page.locator('.event-log').first()).toContainText('confirm')

  await page.getByRole('button', { name: '打开三按钮对话框' }).click()
  const threeWay = page.locator('vaadin-confirm-dialog-overlay[opened]')
  await expect(page.getByText('保存修改？')).toBeVisible()
  await page.getByRole('button', { name: '放弃', exact: true }).click()
  await expect(page.locator('.event-log').nth(1)).toContainText('reject → 放弃')
})

test('notification shows at the requested position', async ({ page }) => {
  await page.goto('/#/notification')

  await page.getByRole('button', { name: '右上角' }).click()
  const card = page.locator('vaadin-notification-card').filter({ hasText: '右上角的提示' })
  await expect(card).toBeVisible()

  // `top-end` means the card sits in the top-right quadrant of the viewport.
  const box = await card.boundingBox()
  const viewport = page.viewportSize()
  expect(box, 'notification card box').not.toBeNull()
  expect(viewport, 'viewport size').not.toBeNull()
  if (box && viewport) {
    expect(box.y).toBeLessThan(viewport.height / 2)
    expect(box.x + box.width / 2).toBeGreaterThan(viewport.width / 2)
  }

  await page.getByRole('button', { name: 'success' }).click()
  await expect(page.locator('vaadin-notification-card').filter({ hasText: '保存成功' })).toBeVisible()
})

test('persistent notification stays until it is closed', async ({ page }) => {
  await page.goto('/#/notification')

  await page.getByRole('button', { name: '显示常驻通知' }).click()
  const card = page.locator('vaadin-notification-card').filter({ hasText: '这条通知会一直显示' })
  await expect(card).toBeVisible()

  await card.getByRole('button', { name: '关闭' }).click()
  await expect(card).toBeHidden()
})

test('popover opens on click and closes on outside click', async ({ page }) => {
  await page.goto('/#/popover')

  await page.getByTestId('popover-trigger').click()
  const content = page.getByText('任意 DOM 都可以放进 popover 的默认插槽。')
  await expect(content).toBeVisible()
  await expect(page.locator('.event-log')).toContainText('opened-changed · true')

  await page.locator('.page__title').click()
  await expect(content).toBeHidden()
  await expect(page.locator('.event-log')).toContainText('opened-changed · false')
})

test('tooltip appears on hover', async ({ page }) => {
  await page.goto('/#/tooltip')

  const trigger = page.getByRole('button', { name: '悬停我' })
  await trigger.hover()
  await expect(page.getByText('这是按钮上的提示')).toBeVisible()
})

test('context menu opens on right click and reports the selection', async ({ page }) => {
  await page.goto('/#/context-menu')

  await page.getByTestId('context-menu-surface').click({ button: 'right' })

  // The menu renders its items into the light DOM once it is opened.
  const item = page.locator('vaadin-context-menu-item', { hasText: '复制' })
  await expect(item).toBeVisible()

  await item.click()
  await expect(page.locator('.event-log')).toContainText('item-selected')
})

test('bare overlay opens and closes from its own content', async ({ page }) => {
  await page.goto('/#/overlay')

  await page.getByRole('button', { name: '打开 overlay' }).click()
  const overlay = page.locator('vaadin-overlay').filter({ hasText: '这是一个裸 overlay' })
  await expect(overlay).toBeVisible()

  await overlay.getByRole('button', { name: '关闭' }).click()
  await expect(overlay).toBeHidden()
})

test('login form validates and can report an error state', async ({ page }) => {
  await page.goto('/#/login')

  const form = page.getByTestId('login-form')
  await expect(form).toBeVisible()

  await form.getByRole('button', { name: /log ?in|sign ?in|submit/i }).click()
  await expect(form).toContainText(/required|必填/i)

  const errorSwitch = page.locator('vaadin-switch', { hasText: '显示错误状态' })
  await errorSwitch.click()
  await expect(page.locator('.event-log')).toContainText('error-changed · true')
})
