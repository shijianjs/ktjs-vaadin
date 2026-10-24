import { expect, test } from '@playwright/test'

/** Field behaviour: typing, validation, selection and the values the pages read back. */

test('text field reflects typed input and runs validation', async ({ page }) => {
  await page.goto('/#/text-field')

  const field = page.getByTestId('text-field-basic')
  await field.locator('input').fill('Ada')
  await expect(page.locator('.readout', { hasText: '当前值' })).toContainText('Ada')

  const validated = page.getByTestId('text-field-validated')
  await validated.locator('input').fill('AB')
  await page.getByRole('button', { name: '校验', exact: true }).click()
  await expect(page.locator('.readout', { hasText: '校验结果' })).toContainText('失败')

  await validated.locator('input').fill('abc')
  await page.getByRole('button', { name: '校验', exact: true }).click()
  await expect(page.locator('.readout', { hasText: '校验结果' })).toContainText('通过')
})

test('text area counts characters and enforces required', async ({ page }) => {
  await page.goto('/#/text-area')

  const area = page.getByTestId('text-area-basic')
  await area.locator('textarea').fill('你好')
  await expect(page.locator('.readout', { hasText: '字数' })).toContainText('2 / 200')

  await page.getByRole('button', { name: '校验' }).click()
  await expect(page.locator('.readout', { hasText: '校验结果' })).toContainText('失败')

  await page.getByTestId('text-area-required').locator('textarea').fill('有内容了')
  await page.getByRole('button', { name: '校验' }).click()
  await expect(page.locator('.readout', { hasText: '校验结果' })).toContainText('通过')
})

test('email field reports validity while typing', async ({ page }) => {
  await page.goto('/#/email-field')

  const field = page.getByTestId('email-field')
  await field.locator('input').fill('not-an-email')
  await expect(page.locator('.readout', { hasText: 'valid' })).toContainText('valid = false')

  await field.locator('input').fill('ada@example.com')
  await expect(page.locator('.readout', { hasText: 'valid' })).toContainText('valid = true')
})

test('password field toggles the reveal button', async ({ page }) => {
  await page.goto('/#/password-field')

  const field = page.getByTestId('password-field')
  await field.locator('input').fill('secret-123')
  await expect(page.locator('.readout', { hasText: 'passwordVisible' })).toContainText(
    'passwordVisible = false',
  )

  await field.locator('[part~="reveal-button"]').click()
  await expect(page.locator('.readout', { hasText: 'passwordVisible' })).toContainText(
    'passwordVisible = true',
  )
})

test('number field reports its value and custom range check', async ({ page }) => {
  await page.goto('/#/number-field')

  const field = page.getByTestId('number-field-basic')
  await field.locator('input').fill('42')
  await expect(page.locator('.readout', { hasText: '当前值' })).toContainText('42')

  // min / max do not invalidate the field on their own; the page validates the value itself.
  const ranged = page.getByTestId('number-field-range')
  await ranged.locator('input').fill('500')
  await page.getByRole('button', { name: '校验' }).click()
  await expect(page.locator('.readout', { hasText: '校验结果' })).toContainText('失败')

  await ranged.locator('input').fill('50')
  await page.getByRole('button', { name: '校验' }).click()
  await expect(page.locator('.readout', { hasText: '校验结果' })).toContainText('通过')
})

test('checkbox fires change on user input and checked-changed on code updates', async ({ page }) => {
  await page.goto('/#/checkbox')

  const box = page.getByTestId('checkbox-basic')
  await box.locator('input').click()
  await expect(box).toHaveJSProperty('checked', true)
  await expect(page.locator('.event-log')).toContainText('change · true')
  await expect(page.locator('.readout', { hasText: 'checked' })).toContainText('checked = true')

  await page.getByRole('button', { name: '程序化切换' }).click()
  await expect(box).toHaveJSProperty('checked', false)
  await expect(page.locator('.event-log')).toContainText('checked-changed（程序设置）· false')
})

test('switch drives a dependent component', async ({ page }) => {
  await page.goto('/#/switch')

  await page.getByTestId('switch-basic').locator('input').click()
  await expect(page.locator('.readout', { hasText: '状态' })).toContainText('已关闭')
})

test('checkbox group reports the selected values as an array', async ({ page }) => {
  await page.goto('/#/checkbox-group')

  const group = page.getByTestId('checkbox-group')
  await group.locator('vaadin-checkbox').first().locator('input').click()
  await expect(page.locator('.readout', { hasText: 'value =' })).toContainText('kotlin')

  await group.locator('vaadin-checkbox').nth(1).locator('input').click()
  await expect(page.locator('.readout', { hasText: 'value =' })).toContainText('kotlin, java')
})

test('radio group keeps a single selection', async ({ page }) => {
  await page.goto('/#/radio-group')

  const group = page.getByTestId('radio-group')
  await group.locator('vaadin-radio-button', { hasText: '专业版' }).locator('input').click()
  await expect(page.locator('.readout', { hasText: 'value =' })).toContainText('pro')
})

test('select opens its overlay and updates the value', async ({ page }) => {
  await page.goto('/#/select')

  await page.getByTestId('select-basic').click()
  const option = page.locator('vaadin-select-item', { hasText: 'Berlin' })
  await expect(option).toBeVisible()
  await option.click()

  await expect(page.locator('.readout', { hasText: 'value =' })).toContainText('berlin')
})

test('combo box filters its items while typing', async ({ page }) => {
  await page.goto('/#/combo-box')

  const combo = page.getByTestId('combo-box-basic')
  await combo.locator('input').fill('Grace')
  const option = page.locator('vaadin-combo-box-item', { hasText: 'Grace Hopper' })
  await expect(option).toBeVisible()
  await option.click()

  await expect(page.locator('.readout', { hasText: 'value =' })).toContainText('grace')
})

test('slider follows keyboard input', async ({ page }) => {
  await page.goto('/#/slider')

  const slider = page.getByTestId('slider-basic')
  await slider.locator('input').first().focus()
  await page.keyboard.press('ArrowRight')

  await expect(page.locator('.readout', { hasText: '数值' }).first()).toContainText('45')
})

test('date picker opens its calendar and selects through the today button', async ({ page }) => {
  await page.goto('/#/date-picker')

  const picker = page.getByTestId('date-picker-basic')
  await picker.locator('[part~="toggle-button"]').click()

  // Every picker on the page owns an overlay element; only the open one carries `opened`.
  const overlay = page.locator('vaadin-date-picker-overlay[opened]')
  await expect(overlay).toBeVisible()

  // Locale-independent interaction: pick "today" from the overlay and compare with the value the
  // page read back. The config pins the browser locale and time zone, so both sides agree.
  await page.getByRole('button', { name: 'Today', exact: true }).click()

  const today = new Date().toISOString().slice(0, 10)
  await expect(page.locator('.readout', { hasText: 'value =' })).toContainText(today)
  await expect(overlay).toBeHidden()
})
