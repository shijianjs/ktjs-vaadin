package demo

import kotlin.js.js
import web.dom.document

/**
 * Lumo light/dark switching.
 *
 * Lumo 25 keys its dark token set off `[theme~='dark']` (see `color-scheme.css`), so the switch is
 * the `theme` attribute on `<html>` — the same attribute a Vaadin application sets. Nothing else in
 * the page has to change: components read the inherited custom properties, and their shadow roots
 * get `color-scheme: dark` through the same attribute.
 *
 * The choice is remembered in `localStorage`; `index.html` applies it before first paint so a stored
 * dark preference does not flash the light theme.
 */
object Theme {
    private const val STORAGE_KEY = "ktjs-vaadin-theme"
    private const val DARK = "dark"

    /** Whether the dark token set is active. */
    val isDark: Boolean
        get() = document.documentElement.hasAttribute("theme")

    /** Re-applies the theme stored by a previous visit. Called once by [main]. */
    fun applyStored() {
        if (storedValue() == DARK) apply(true)
    }

    /** Applies [dark] to the document and remembers the choice. */
    fun apply(dark: Boolean) {
        val root = document.documentElement
        if (dark) {
            root.setAttribute("theme", DARK)
        } else {
            root.removeAttribute("theme")
        }
        store(if (dark) DARK else "light")
    }

    /** Flips the theme and returns the new state. */
    fun toggle(): Boolean {
        val next = !isDark
        apply(next)
        return next
    }

    // `localStorage` has no wrapper in kotlin-browser 2026.10.1, so the two calls go through `js`.
    private fun storedValue(): String? = js("() => { try { return localStorage.getItem('$STORAGE_KEY') } catch (e) { return null } }")()

    private fun store(value: String) {
        js("(value) => { try { localStorage.setItem('$STORAGE_KEY', value) } catch (e) { /* storage disabled */ } }")(value)
    }
}
