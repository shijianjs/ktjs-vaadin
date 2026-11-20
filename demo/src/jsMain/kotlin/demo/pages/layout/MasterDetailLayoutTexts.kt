package demo.pages.layout

import io.github.shijianjs.ktjs.dom.element.invoke

/** Every string this page shows, in one language. */
internal class MasterDetailLayoutTexts(
    val basicHeading: String,
    val basicHint: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val sizesHeading: String,
    val sizesHint: String,
    val master12: String,
    val detail16: String,
    val orientationHeading: String,
    val orientationHint: String,
    val master: String,
    val detail: String,
    val toggleOrientation: String,
    val overlayHeading: String,
    val overlayHint: String,
    val overlayCaption: String,
    val placeholderHeading: String,
    val placeholderHint: String,
    val adaDetails: String,
    val showDetail: String,
    val hideDetail: String,
    val member: (Int) -> String,
    val selectMember: String,
    val nestingHeading: String,
    val nestingHint: String,
    val innerMaster: String,
    val innerDetail: String,
    val outerMaster: String,
)
