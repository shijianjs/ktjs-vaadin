package vaadin.combobox

import kotlin.js.JsAny
import kotlin.js.js
import kotlin.js.unsafeCast
import vaadin.queryAll
import web.html.HTMLElement

/**
 * Helpers shared by the combo box suites, mirroring `packages/combo-box/test/helpers.js`.
 *
 * The upstream helpers read the dropdown through `comboBox._scroller`, which is a protected member
 * and therefore off limits. The scroller is appended to the combo box light DOM with
 * `slot="overlay"`, so the rendered items are reachable with a plain descendant query instead.
 * `inputElement` also has no typed wrapper, hence the single `js(...)` call in [setComboBoxInput].
 */

/** Returns the rendered dropdown items of [comboBox], skipping the hidden (recycled) ones. */
internal fun comboBoxItems(comboBox: ComboBoxElement): List<HTMLElement> =
    queryAll(comboBox, "vaadin-combo-box-item").filter { !isElementHidden(it) }

/** Returns the first rendered dropdown item of [comboBox], mirroring `getFirstItem`. */
internal fun comboBoxFirstItem(comboBox: ComboBoxElement): HTMLElement? = comboBoxItems(comboBox).firstOrNull()

/** Reads the `hidden` property of [element]. kotlin-browser does not expose it on `HTMLElement`. */
internal fun isElementHidden(element: HTMLElement): Boolean = js("(element) => element.hidden")(element) as Boolean

/** Reads the text currently shown in the input of [comboBox]. */
internal fun comboBoxInputValue(comboBox: ComboBoxElement): String =
    js("(comboBox) => comboBox.inputElement.value")(comboBox) as String

/** Emulates the user typing [value] into the combo box input, mirroring `setInputValue`. */
internal fun setComboBoxInput(comboBox: ComboBoxElement, value: String) {
    js(
        "(comboBox, value) => {" +
            " comboBox.inputElement.value = value;" +
            " comboBox.inputElement.dispatchEvent(new Event('input', { bubbles: true, composed: true }));" +
            "}",
    )(comboBox, value)
}

/** Sends an `Enter` keydown to the combo box input, mirroring the upstream `enter()` helper. */
internal fun pressEnterInComboBox(comboBox: ComboBoxElement) {
    js(
        "(comboBox) => comboBox.inputElement.dispatchEvent(" +
            "new KeyboardEvent('keydown', { key: 'Enter', bubbles: true, composed: true, cancelable: true }))",
    )(comboBox)
}

/** Builds a plain string item array, as used by most upstream cases. */
internal fun stringItems(vararg values: String): Array<JsAny> = arrayOf(*values).unsafeCast<Array<JsAny>>()

/**
 * Answers a data provider request without reporting a total size, mirroring the upstream
 * `callback(items)` / `callback(items, undefined)` calls.
 *
 * Kotlin's `null` reaches JS as `null`, which the component treats as a real size, so the second
 * argument has to be omitted from JS to mean "unknown".
 */
internal fun callbackWithoutSize(callback: ComboBoxDataProviderCallback, vararg items: String) {
    js("(callback, items) => callback(items, undefined)")(callback, stringItems(*items))
}

/**
 * Builds a plain JS object from [json].
 *
 * `js("(...) => ({ ... })")` is not an option here: the Kotlin/JS compiler parses the string itself
 * and rewrites object literals (see the note on `js()` in `AGENTS.md`).
 */
internal fun json(json: String): JsAny = js("(text) => JSON.parse(text)")(json) as JsAny

/** Builds a plain JS array from the JSON [text], used for the object item fixtures. */
internal fun jsonItems(text: String): Array<JsAny> = js("(text) => JSON.parse(text)")(text).unsafeCast<Array<JsAny>>()

/** Builds `count` string items named `item 0` … `item n`, mirroring `makeItems` of the helpers. */
internal fun makeItems(count: Int): Array<JsAny> = Array(count) { "item $it".unsafeCast<JsAny>() }

/** Returns the text of every rendered dropdown item of [comboBox]. */
internal fun comboBoxItemTexts(comboBox: ComboBoxElement): List<String> = comboBoxItems(comboBox).map { it.textContent ?: "" }

/*
 * Upstream files of `packages/combo-box/test/` that are not translated at all, and why:
 *
 * - `keyboard.test.js`, `interactions.test.js`, `focus-index.test.js`, `partial-match-mode.test.js`:
 *   they drive the component with `sendKeys`, `arrowDownKeyDown`, `escKeyDown` and pointer drags, and
 *   assert on `_focusedIndex` / `_scroller` internals. The public API cannot express those sequences.
 * - `overlay-position.test.js` and the layout half of `overlay-opening.test.js`: they assert overlay
 *   geometry (`getBoundingClientRect`, `align-items`, `--vaadin-combo-box-overlay-width`) after
 *   scrolling the page, i.e. visual placement rather than component state.
 * - `lit.test.js` and `lit-renderer-directives.test.js`: the Lit directive entries (`lit.js`) are not
 *   wrapped on purpose, see the Lit section of `AGENTS.md`.
 * - `aria.test.js` is partially translated; the announcements it asserts go through the internal
 *   `_scroller` / `_focusedIndex` state.
 */

