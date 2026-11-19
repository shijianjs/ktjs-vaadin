package demo.pages.buttons

import demo.ui.Readout
import io.github.shijianjs.ktjs.demo.page.PAGE_SHEET
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.demo.page.row
import io.github.shijianjs.ktjs.demo.style.RESET
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachStyledShadow
import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import web.components.CustomElementReference
import web.dom.TagName
import web.dom.clickEvent
import web.events.addHandler

/**
 * `<vaadin-demo-enabled-toggle>`: a button that can be disabled live, its switch, and the state readout.
 *
 * Both texts depend on the state — the switch offers "enable" exactly while the button is disabled —
 * so every label is a named setter that re-renders the two nodes it owns, never the whole tree.
 */
class EnabledToggle : CustomElementAdapter() {
    private val target = createButton().addTheme("primary")

    private val toggle = createButton()

    private val readout = Readout("")

    private var clicked = false

    /** Text on the button that gets disabled. */
    var targetLabel: String = ""
        set(value) {
            field = value
            target.textContent = value
        }

    /** Text of the switch while the button is disabled, i.e. the action it offers. */
    var enableLabel: String = ""
        set(value) {
            field = value
            render()
        }

    /** Text of the switch while the button is enabled. */
    var disableLabel: String = ""
        set(value) {
            field = value
            render()
        }

    /** Prefix of the state readout. */
    var statePrefix: String = ""
        set(value) {
            field = value
            render()
        }

    /** State word when the button is enabled. */
    var stateEnabled: String = ""
        set(value) {
            field = value
            render()
        }

    /** State word after a click on the enabled button. */
    var stateClicked: String = ""
        set(value) {
            field = value
            render()
        }

    /** State word when the button is disabled. */
    var stateDisabled: String = ""
        set(value) {
            field = value
            render()
        }

    init {
        target.clickEvent.addHandler {
            clicked = true
            render()
        }
        toggle.clickEvent.addHandler {
            target.disabled = !target.disabled
            clicked = false
            render()
        }
        attachStyledShadow(RESET, PAGE_SHEET).append(row(target, toggle, readout.root))
    }

    private fun render() {
        toggle.textContent = if (target.disabled) enableLabel else disableLabel
        val state = when {
            target.disabled -> stateDisabled
            clicked -> stateClicked
            else -> stateEnabled
        }
        readout.set(statePrefix + state)
    }

    companion object : CustomElementReference<EnabledToggle>(EnabledToggle::class.js) {
        val NAME: TagName<EnabledToggle> = TagName("vaadin-demo-enabled-toggle")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<vaadin-demo-enabled-toggle>`. */
fun newEnabledToggle(): EnabledToggle {
    EnabledToggle.define()
    return EnabledToggle.NAME()
}
