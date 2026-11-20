package demo

import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import kotlin.js.js

/**
 * Chinese/English metadata for a catalog entry.
 *
 * A page declares its copy where it declares the page — inside its companion — so both languages
 * travel in one declaration instead of one string resolved when the language switches.
 */
class Bilingual(private val zh: String, private val en: String) {
    /** This pair written in [lang]. */
    fun resolve(lang: Lang): String = if (lang == Langs.EN) en else zh
}

/** Chinese/English metadata for a catalog entry. */
fun bi(zh: String, en: String): Bilingual = Bilingual(zh, en)


/** The language a first visit starts in: the browser's, when it asks for Chinese. */
fun detectedLang(): Lang {
    val chinese =
        js(
            "() => ((navigator.languages && navigator.languages[0]) || navigator.language || '')" +
                ".toLowerCase().startsWith('zh')",
        )() as Boolean
    return if (chinese) Langs.ZH_HANS else Langs.EN
}
