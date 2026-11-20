package demo.pages.pickers

import io.github.shijianjs.ktjs.dom.element.invoke

/** The message a validation example shows; the copy is chosen when the page is drawn. */
internal enum class DateMessage { NONE, BAD_FORMAT, REQUIRED, TOO_EARLY, TOO_LATE, CLOSED, WEEKDAY }
