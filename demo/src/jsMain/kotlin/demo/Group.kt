package demo

import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs

/**
 * A sidebar group. [id] is stable across languages; [label] is resolved for display.
 *
 * [icon] is an Iconify name. It marks the section, not the row, so it appears twice: before the
 * group in the drawer, and before the group's heading on the component index. Keeping it here means
 * both places read it from one declaration instead of each carrying its own table.
 */
class Group(val id: String, private val zh: String, private val en: String, val icon: String) {
    /** The group's name in [lang], for the sidebar. */
    fun labelFor(lang: Lang): String = if (lang == Langs.EN) en else zh
}
