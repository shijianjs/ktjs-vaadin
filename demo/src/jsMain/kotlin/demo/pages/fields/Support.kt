package demo.pages.fields

import io.github.shijianjs.ktjs.demo.page.Blocks.col as kitCol
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import demo.ui.Readout
import demo.ui.listen
import js.objects.unsafeJso
import kotlin.js.js
import kotlin.js.unsafeCast
import io.github.shijianjs.ktjs.vaadin.fieldbase.InputMixin
import io.github.shijianjs.ktjs.vaadin.select.SelectEvents
import io.github.shijianjs.ktjs.vaadin.select.SelectItemData
import io.github.shijianjs.ktjs.vaadin.select.createSelect
import web.events.Event
import web.events.EventType
import web.html.HTMLElement
import web.html.HTMLInputElement
import web.validation.ValidityState
import io.github.shijianjs.ktjs.dom.element.invoke

private const val VALUE_CHANGE_ON_CHANGE = "on-change"

private const val VALUE_CHANGE_EAGER = "eager"

private const val VALUE_CHANGE_LAZY = "lazy"

private const val VALUE_CHANGE_TIMEOUT = "timeout"

/**
 * Reads the native validity state behind a field.
 *
 * The validation examples of the documentation map the individual `validity` flags to their own
 * error messages. `inputElement` is part of `io.github.shijianjs.ktjs.vaadin.fieldbase.InputMixin`; the upstream type is
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

/** The copy of the shared "value change modes" section, in one language. */
private class ValueChangeTexts(
    val heading: String,
    val hint: String,
    val syncMode: String,
    val onChange: String,
    val lazy: String,
    val timeout: String,
    val typedEmpty: String,
    val typedPrefix: String,
    val serverEmpty: String,
    val serverPrefix: String,
    val empty: String,
)

private val valueChangeEnglish: ValueChangeTexts =
    ValueChangeTexts(
        heading = "Value change modes",
        hint = "ValueChangeMode is a Flow server-side API without a web-component counterpart; this " +
            "example simulates the four modes with local timers, and the server received line shows " +
            "the value the server would end up with.",
        syncMode = "Sync mode",
        onChange = "on change (default)",
        lazy = "lazy (400 ms debounce)",
        timeout = "timeout (every 400 ms)",
        typedEmpty = "Typed here: (empty)",
        typedPrefix = "Typed here: ",
        serverEmpty = "Server received: (empty)",
        serverPrefix = "Server received: ",
        empty = "(empty)",
    )

private val valueChangeChinese: ValueChangeTexts =
    ValueChangeTexts(
        heading = "值变化模式",
        hint = "ValueChangeMode 是 Flow 的服务端 API，Web Component 没有对应属性；这里用本地定时器模拟四种模式，" +
            "“服务端收到”一行就是服务端最终会拿到的值。",
        syncMode = "同步模式",
        onChange = "on change（默认）",
        lazy = "lazy（400 ms 防抖）",
        timeout = "timeout（每 400 ms）",
        typedEmpty = "输入框内容：（空）",
        typedPrefix = "输入框内容：",
        serverEmpty = "服务端收到：（空）",
        serverPrefix = "服务端收到：",
        empty = "（空）",
    )

private val valueChangeTexts: Translations<ValueChangeTexts> = texts(
    Langs.EN to valueChangeEnglish,
    Langs.ZH_HANS to valueChangeChinese,
)

/**
 * Simulates Flow's `ValueChangeMode`, which is a server-side API without a web-component
 * counterpart. The select switches when the client value is copied to the "server" read-out.
 */
internal fun valueChangeModesSection(
    field: HTMLElement,
    valueChanged: EventType<Event>,
    change: EventType<Event>,
    readValue: () -> String,
): ValueChangeSection {
    val binder = Binder(valueChangeTexts)
    val typed = Readout("")
    val synced = Readout("")
    var lastTyped: String? = null
    var lastSynced: String? = null
    var mode = VALUE_CHANGE_ON_CHANGE
    var timer: Int? = null

    fun cancelTimer() {
        timer?.let { clearTimeout(it) }
        timer = null
    }

    fun modeItems(copy: ValueChangeTexts): Array<SelectItemData> =
        arrayOf(
            unsafeJso<SelectItemData> {
                label = copy.onChange
                value = VALUE_CHANGE_ON_CHANGE
            },
            unsafeJso<SelectItemData> {
                label = "eager"
                value = VALUE_CHANGE_EAGER
            },
            unsafeJso<SelectItemData> {
                label = copy.lazy
                value = VALUE_CHANGE_LAZY
            },
            unsafeJso<SelectItemData> {
                label = copy.timeout
                value = VALUE_CHANGE_TIMEOUT
            },
        )

    val modes =
        createSelect {
            label = ""
            items = modeItems(valueChangeEnglish)
            value = VALUE_CHANGE_ON_CHANGE
        }
    binder.bind(modes, { it.syncMode }) { modes.label = it }

    fun nowTyped(copy: ValueChangeTexts) {
        val text = lastTyped
        if (text == null) typed.set(copy.typedEmpty) else typed.set(copy.typedPrefix, text.ifEmpty { copy.empty })
    }

    fun nowSynced(copy: ValueChangeTexts) {
        val text = lastSynced
        if (text == null) synced.set(copy.serverEmpty) else synced.set(copy.serverPrefix, text.ifEmpty { copy.empty })
    }

    fun showSynced() {
        cancelTimer()
        val value = readValue()
        lastSynced = value
        nowSynced(binder.current)
    }

    modes.listen(SelectEvents.valueChanged) {
        mode = modes.value
        showSynced()
    }

    field.listen(valueChanged) {
        lastTyped = readValue()
        nowTyped(binder.current)
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

    val section = SectionBlock.NAME { show() }
    binder.bind(section.heading) { it.heading }
    binder.bind(section.hint) { it.hint }
    section.body.append(exampleBox(null, kitCol(modes, field, typed.root, synced.root)))

    return ValueChangeSection(section) { lang ->
        binder.apply(lang)
        val copy = binder.current
        modes.items = modeItems(copy)
        modes.value = mode
        nowTyped(copy)
        nowSynced(copy)
    }
}
