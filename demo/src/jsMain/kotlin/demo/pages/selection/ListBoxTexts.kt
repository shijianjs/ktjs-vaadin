package demo.pages.selection

import io.github.shijianjs.ktjs.dom.element.invoke
import demo.pages.fixtures.people

/** Every string this page shows, in one language. */
internal class ListBoxTexts(
    val dividersHeading: String,
    val dividersHint: String,
    val assignee: String,
    val dueDate: String,
    val status: String,
    val thumbnail: String,
    val preview: String,
    val disabledHeading: String,
    val disabledHint: String,
    val inProgressWithCount: String,
    val doneWithCount: String,
    val cancelledWithCount: String,
    val singleHeading: String,
    val singleHint: String,
    val inProgress: String,
    val done: String,
    val cancelled: String,
    val multipleHeading: String,
    val multipleHint: String,
    val presentationHeading: String,
    val presentationHint: String,
    val orientationHeading: String,
    val orientationHint: String,
    val one: String,
    val two: String,
    val three: String,
    val disabledItemOne: String,
    val disabledItemTwo: String,
    /** The professions of the fixture people, in the order [people] assigns them. */
    val professions: List<String>,
)
