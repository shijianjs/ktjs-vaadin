package demo

import kotlin.js.js
import web.dom.document

/** Languages the showcase is available in. */
enum class Language(val code: String, val htmlLang: String, val label: String) {
    ZH("zh", "zh-CN", "中文"),
    EN("en", "en", "English"),
}

/** A pair of translations, resolved against [LanguageState.current] when the page is built. */
class Bilingual(private val zh: String, private val en: String) {
    fun resolve(): String = tr(zh, en)
}

/** Chinese/English metadata for a catalog entry. */
fun bi(zh: String, en: String): Bilingual = Bilingual(zh, en)

/** Picks [zh] in Chinese mode and [en] in English mode. */
fun tr(zh: String, en: String): String = if (LanguageState.current == Language.EN) en else zh

/**
 * Active UI language.
 *
 * The first visit follows the browser (`navigator.languages[0]`); an explicit switch is remembered
 * in `localStorage`. Every page is built from functions that call [tr], so a switch rebuilds the
 * page tree instead of mutating already-rendered copy.
 */
object LanguageState {
    private const val STORAGE_KEY = "ktjs-vaadin-language"

    private val listeners = mutableListOf<(Language) -> Unit>()

    private var active: Language = stored() ?: detect()

    /** The language currently used to build pages. */
    val current: Language
        get() = active

    /** Applies the active language to `<html lang>`; called once by [main]. */
    fun init() {
        document.documentElement.lang = active.htmlLang
    }

    /** Switches the language, remembers the choice and notifies the shell. */
    fun set(language: Language) {
        if (language == active) return
        active = language
        store(language.code)
        document.documentElement.lang = language.htmlLang
        listeners.toList().forEach { it(language) }
    }

    /** Registers a listener invoked after [set] changed the language. */
    fun onChange(listener: (Language) -> Unit) {
        listeners += listener
    }

    private fun detect(): Language {
        val chinese =
            js(
                "() => ((navigator.languages && navigator.languages[0]) || navigator.language || '')" +
                    ".toLowerCase().startsWith('zh')",
            )() as Boolean
        return if (chinese) Language.ZH else Language.EN
    }

    // `localStorage` has no wrapper in kotlin-browser 2026.10.1, so the two calls go through `js`.
    private fun stored(): Language? {
        val code =
            js("() => { try { return localStorage.getItem('$STORAGE_KEY') } catch (e) { return null } }")() as String?
        return Language.entries.firstOrNull { it.code == code }
    }

    private fun store(code: String) {
        js("(value) => { try { localStorage.setItem('$STORAGE_KEY', value) } catch (e) { /* storage disabled */ } }")(code)
    }
}
