package demo.pages.pickers

import io.github.shijianjs.ktjs.dom.element.invoke

/** Which message the validation examples show; the copy is chosen when the page is drawn. */
internal enum class ValidationMessage { NONE, BAD_INPUT, INCOMPLETE, REQUIRED, EARLY, LATE, UNAVAILABLE }
