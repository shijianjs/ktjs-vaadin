package demo.pages.fields

import demo.tr
import demo.ui.Readout
import demo.ui.Section
import demo.ui.column
import demo.ui.example
import demo.ui.listen
import demo.ui.section
import js.objects.unsafeJso
import kotlin.js.js
import kotlin.js.unsafeCast
import vaadin.fieldbase.InputMixin
import vaadin.select.SelectEvents
import vaadin.select.SelectItemData
import vaadin.select.createSelect
import web.events.Event
import web.events.EventType
import web.html.HTMLElement
import web.html.HTMLInputElement
import web.validation.ValidityState

private const val VALUE_CHANGE_ON_CHANGE = "on-change"

private const val VALUE_CHANGE_EAGER = "eager"

private const val VALUE_CHANGE_LAZY = "lazy"

private const val VALUE_CHANGE_TIMEOUT = "timeout"

/**
 * Reads the native validity state behind a field.
 *
 * The validation examples of the documentation map the individual `validity` flags to their own
 * error messages. `inputElement` is part of `vaadin.fieldbase.InputMixin`; the upstream type is
 * `HTMLElement`, so it is narrowed to the `<input>` that actually backs a text field.
 */
internal fun validityOf(field: InputMixin): ValidityState =
    field.inputElement.unsafeCast<HTMLInputElement>().validity

/** Timer helpers for the local value-change-mode simulation; kotlin-browser has no timer wrapper. */
private fun startTimeout(millis: Int, tick: () -> Unit): Int =
    js("(fn, ms) => window.setTimeout(fn, ms)")(tick, millis) as Int

private fun clearTimeout(handle: Int) {
    js("(handle) => window.clearTimeout(handle)")(handle)
}

/**
 * Simulates Flow's `ValueChangeMode`, which is a server-side API without a web-component
 * counterpart. The select switches when the client value is copied to the "server" read-out.
 */
private fun valueChangeModes(
    field: HTMLElement,
    valueChanged: EventType<Event>,
    change: EventType<Event>,
    readValue: () -> String,
): HTMLElement {
    val typed = Readout(tr("输入框内容：（空）", "Typed here: (empty)"))
    val synced = Readout(tr("服务端收到：（空）", "Server received: (empty)"))
    var mode = VALUE_CHANGE_ON_CHANGE
    var timer: Int? = null

    fun cancelTimer() {
        timer?.let { clearTimeout(it) }
        timer = null
    }

    fun showSynced() {
        cancelTimer()
        synced.set(
            tr("服务端收到：", "Server received: "),
            readValue().ifEmpty { tr("（空）", "(empty)") },
        )
    }

    val modes =
        createSelect {
            label = tr("同步模式", "Sync mode")
            items =
                arrayOf(
                    unsafeJso<SelectItemData> {
                        label = tr("on change（默认）", "on change (default)")
                        value = VALUE_CHANGE_ON_CHANGE
                    },
                    unsafeJso<SelectItemData> {
                        label = "eager"
                        value = VALUE_CHANGE_EAGER
                    },
                    unsafeJso<SelectItemData> {
                        label = tr("lazy（400 ms 防抖）", "lazy (400 ms debounce)")
                        value = VALUE_CHANGE_LAZY
                    },
                    unsafeJso<SelectItemData> {
                        label = tr("timeout（每 400 ms）", "timeout (every 400 ms)")
                        value = VALUE_CHANGE_TIMEOUT
                    },
                )
            value = VALUE_CHANGE_ON_CHANGE
        }

    modes.listen(SelectEvents.valueChanged) {
        mode = modes.value
        showSynced()
    }

    field.listen(valueChanged) {
        typed.set(
            tr("输入框内容：", "Typed here: "),
            readValue().ifEmpty { tr("（空）", "(empty)") },
        )
        when (mode) {
            VALUE_CHANGE_EAGER -> showSynced()
            VALUE_CHANGE_LAZY -> {
                cancelTimer()
                timer = startTimeout(400) { showSynced() }
            }
            VALUE_CHANGE_TIMEOUT -> if (timer == null) timer = startTimeout(400) { showSynced() }
        }
    }
    field.listen(change) { showSynced() }

    return column(modes, field, typed.root, synced.root)
}

/** Section wrapper around [valueChangeModes], shared by the four field pages. */
internal fun valueChangeModesSection(
    field: HTMLElement,
    valueChanged: EventType<Event>,
    change: EventType<Event>,
    readValue: () -> String,
): Section {
    val modes =
        section(
            tr("值变化模式", "Value change modes"),
            tr(
                "ValueChangeMode 是 Flow 的服务端 API，Web Component 没有对应属性；这里用本地定时器模拟" +
                    "四种模式，“服务端收到”一行就是服务端最终会拿到的值。",
                "ValueChangeMode is a Flow server-side API without a web-component counterpart; this " +
                    "example simulates the four modes with local timers, and the server received line " +
                    "shows the value the server would end up with.",
            ),
        )
    modes.body.append(example(null, valueChangeModes(field, valueChanged, change, readValue)))
    return modes
}
