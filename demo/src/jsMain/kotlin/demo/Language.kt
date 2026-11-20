package demo

import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import kotlin.js.js

/**
 * Chinese/English metadata for a catalog entry.
 *
 * The catalog is declared before anything is built and has to carry every language's copy in the
 * same breath, so a title is a pair rather than a string resolved at read time.
 */
class Bilingual(private val zh: String, private val en: String) {
    /** This pair written in [lang]. */
    fun resolve(lang: Lang): String = if (lang == Langs.EN) en else zh
}

/** Chinese/English metadata for a catalog entry. */
fun bi(zh: String, en: String): Bilingual = Bilingual(zh, en)

/**
 * Picks [zh] in Chinese mode and [en] in English mode.
 *
 * Only the pages that have not moved to a text table of their own still call this: they resolve their
 * copy while their DOM is built, so the language has to be settled before they run. Every other page
 * binds its copy to a `Translations` table and never reads the active language.
 */
fun tr(zh: String, en: String): String = if (LanguageState.current == Langs.EN) en else zh

/**
 * The language the pages that still build their copy with [tr] are built in.
 *
 * A mirror of the kit's own `LanguageState`, updated by `main` before the layout mounts and on every
 * switch. It exists only until the last page owns a text table; a page that owns one does not read it.
 */
object LanguageState {
    var current: Lang = Langs.EN
        private set

    /** Mirrors [lang]; called by `main` right after the kit's state settles. */
    fun set(lang: Lang) {
        current = lang
    }
}

/** The language a first visit starts in: the browser's, when it asks for Chinese. */
fun detectedLang(): Lang {
    val chinese =
        js(
            "() => ((navigator.languages && navigator.languages[0]) || navigator.language || '')" +
                ".toLowerCase().startsWith('zh')",
        )() as Boolean
    return if (chinese) Langs.ZH_HANS else Langs.EN
}
