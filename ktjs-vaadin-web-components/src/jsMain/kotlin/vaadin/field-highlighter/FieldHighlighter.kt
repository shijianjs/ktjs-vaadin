package vaadin.fieldhighlighter

import kotlin.js.js
import vaadin.VaadinTagName

/**
 * A user shown by the field highlighter, typically provided by a collaboration backend.
 */
external interface FieldHighlighterUser {
    var id: Double
    var name: String
    var colorIndex: Double
    var fieldIndex: Double?
}

/**
 * Creates a [FieldHighlighterUser] object suitable for the highlighter API.
 */
fun fieldHighlighterUser(
    id: Double,
    name: String,
    colorIndex: Double,
    fieldIndex: Double? = null,
): FieldHighlighterUser {
    val user: FieldHighlighterUser = js("({})")
    user.id = id
    user.name = name
    user.colorIndex = colorIndex
    user.fieldIndex = fieldIndex
    return user
}

/**
 * A field controller that shows a coloured outline while other users edit the same field, and an
 * overlay listing those users.
 *
 * Create it with [createFieldHighlighterController] and attach it to the field's host element.
 */
external class FieldHighlighterController {
    /** The controller host element. */
    val host: web.html.HTMLElement

    /** The user currently editing the field, or `null`. */
    var user: FieldHighlighterUser?

    /** Users who have focused the field. */
    var users: Array<FieldHighlighterUser>

    fun hostConnected()

    fun addUser(user: FieldHighlighterUser)

    fun setUsers(users: Array<FieldHighlighterUser>)

    fun removeUser(user: FieldHighlighterUser)

    fun redraw()
}

private val constructController = js("(Controller, host) => new Controller(host)")

/**
 * Creates a [FieldHighlighterController] for the given field.
 */
fun createFieldHighlighterController(
    host: web.html.HTMLElement,
): FieldHighlighterController =
    constructController(FieldHighlighterModule.FieldHighlighterController, host)

/**
 * Static API of `<vaadin-field-highlighter>` for sharing a controller between fields.
 */
object FieldHighlighter {
    /** Returns the controller associated with a field, creating it on first use. */
    fun init(field: web.html.HTMLElement): FieldHighlighterController =
        FieldHighlighterModule.FieldHighlighter.init(field)

    fun addUser(field: web.html.HTMLElement, user: FieldHighlighterUser) {
        FieldHighlighterModule.FieldHighlighter.addUser(field, user)
    }

    fun removeUser(field: web.html.HTMLElement, user: FieldHighlighterUser) {
        FieldHighlighterModule.FieldHighlighter.removeUser(field, user)
    }

    fun setUsers(field: web.html.HTMLElement, users: Array<FieldHighlighterUser>) {
        FieldHighlighterModule.FieldHighlighter.setUsers(field, users)
    }
}

external class FieldHighlighterElement : web.html.HTMLElement

fun registerFieldHighlighter(): kotlin.js.JsAny = FieldHighlighterModule.FieldHighlighter

fun createFieldHighlighter(
    configure: FieldHighlighterElement.() -> Unit = {},
): FieldHighlighterElement {
    registerFieldHighlighter()
    return web.dom.document.createElement(VaadinTagName.fieldHighlighter).apply(configure)
}

/**
 * The coloured outline drawn around a highlighted field.
 */
external class FieldOutlineElement :
    web.html.HTMLElement,
    vaadin.themablemixin.ThemableMixin,
    vaadin.themablemixin.LumoInjectionMixin,
    vaadin.componentbase.DirMixin,
    vaadin.componentbase.PolylitMixin {
    /** The user who last interacted with the field. */
    var user: FieldHighlighterUser?
}

fun registerFieldOutline(): kotlin.js.JsAny = FieldOutlineModule.FieldOutline

fun createFieldOutline(configure: FieldOutlineElement.() -> Unit = {}): FieldOutlineElement {
    registerFieldOutline()
    return web.dom.document.createElement(VaadinTagName.fieldOutline).apply(configure)
}

/**
 * A single user tag displayed by [UserTagsElement].
 */
external class UserTagElement :
    web.html.HTMLElement,
    vaadin.themablemixin.ThemableMixin,
    vaadin.themablemixin.LumoInjectionMixin,
    vaadin.componentbase.DirMixin,
    vaadin.componentbase.PolylitMixin {
    /** Name of the user. */
    var name: String

    /** Id of the user, taken from [FieldHighlighterUser.id]. */
    var uid: String

    /** Colour index of the user. */
    var colorIndex: Double
}

fun registerUserTag(): kotlin.js.JsAny = UserTagModule.UserTag

fun createUserTag(configure: UserTagElement.() -> Unit = {}): UserTagElement {
    registerUserTag()
    return web.dom.document.createElement(VaadinTagName.userTag).apply(configure)
}

/** The user tags added and removed by one update. */
external interface ChangedUserTags {
    /** Tags of added users. */
    var added: Array<UserTagElement?>

    /** Tags of removed users. */
    var removed: Array<UserTagElement?>
}

/**
 * The group of user tags shown by the highlighter overlay.
 */
external class UserTagsElement : web.html.HTMLElement, vaadin.componentbase.PolylitMixin {
    /** True while the highlighted field has focus; the overlay stays visible. */
    var hasFocus: Boolean

    /** True while the overlay is open. */
    var opened: Boolean

    /** True while the tags are flashing after another user started editing. */
    var flashing: Boolean

    /** The element the overlay is positioned to. */
    var target: web.html.HTMLElement?

    var users: Array<FieldHighlighterUser>

    /** Fade animation duration in milliseconds. */
    var duration: Double

    /** Delay before the overlay hides itself, in milliseconds. */
    var delay: Double

    fun createUserTag(user: FieldHighlighterUser): UserTagElement

    fun getTagForUser(user: FieldHighlighterUser): UserTagElement?

    fun getChangedTags(
        addedUsers: Array<FieldHighlighterUser>,
        removedUsers: Array<FieldHighlighterUser>,
    ): ChangedUserTags

    fun applyTagsStart(changed: ChangedUserTags)

    fun applyTagsEnd(changed: ChangedUserTags)

    fun setUsers(users: Array<FieldHighlighterUser>)

    fun flashTags(added: Array<FieldHighlighterUser>)

    fun stopFlash()

    fun updateTags(users: Array<FieldHighlighterUser>, changed: ChangedUserTags)

    fun updateTagsSync(users: Array<FieldHighlighterUser>, changed: ChangedUserTags)

    fun show()

    fun hide()

    fun requestContentUpdate()
}

fun registerUserTags(): kotlin.js.JsAny = UserTagsModule.UserTags

fun createUserTags(configure: UserTagsElement.() -> Unit = {}): UserTagsElement {
    registerUserTags()
    return web.dom.document.createElement(VaadinTagName.userTags).apply(configure)
}

/** Localization texts of the AI field marker. */
external interface AiFieldMarkerI18n {
    /** Message shown in the popover explaining the AI fill. */
    var message: String?

    /** Label of the revert control. */
    var revert: String?

    /** Accessible label of the badge button and the popover dialog. */
    var badgeLabel: String?

    /** Tooltip text of the badge button. */
    var badgeTooltip: String?

    /** Texts of the confidence indicator. */
    var confidence: AiFieldMarkerConfidenceI18n?
}

/** Localized texts of the confidence indicator. */
external interface AiFieldMarkerConfidenceI18n {
    var low: String?
    var medium: String?
    var high: String?
}

/**
 * The marker rendered inside a field while an AI assistant is working on its value.
 *
 * Add it as a direct child of the field to mark the field as AI-filled; removing it clears the
 * mark. While [working] is `true` the field shows a shimmer and is made read-only on the client.
 *
 * Activating the revert control fires a bubbling `ai-field-revert` event from the field; listen
 * for it on the field or a container and restore the previous value.
 */
external class AiFieldMarkerElement :
    web.html.HTMLElement,
    vaadin.componentbase.DirMixin,
    vaadin.componentbase.PolylitMixin,
    vaadin.componentbase.SlotStylesMixin {
    /** The object used to localize this component. */
    var i18n: AiFieldMarkerI18n

    /** A DOM node shown between the message and the revert control. */
    var content: web.dom.Node?

    /** Whether the marker is in its working state. */
    var working: Boolean

    /** Confidence level of the AI-filled value, or `null` to hide the indicator. */
    var confidence: String?
}

fun registerAiFieldMarker(): kotlin.js.JsAny = AiFieldMarkerModule.AiFieldMarker

fun createAiFieldMarker(
    configure: AiFieldMarkerElement.() -> Unit = {},
): AiFieldMarkerElement {
    registerAiFieldMarker()
    return web.dom.document.createElement(VaadinTagName.aiFieldMarker).apply(configure)
}

private external interface FieldHighlighterStatics {
    /** Returns the controller associated with a field, creating it on first use. */
    fun init(field: web.html.HTMLElement): FieldHighlighterController

    fun addUser(field: web.html.HTMLElement, user: FieldHighlighterUser)

    fun removeUser(field: web.html.HTMLElement, user: FieldHighlighterUser)

    fun setUsers(field: web.html.HTMLElement, users: Array<FieldHighlighterUser>)
}

@JsModule("@vaadin/field-highlighter/src/vaadin-field-highlighter.js")
private external object FieldHighlighterModule {
    val FieldHighlighterController: kotlin.js.JsAny
    val FieldHighlighter: FieldHighlighterStatics
}

@JsModule("@vaadin/field-highlighter/src/vaadin-field-outline.js")
private external object FieldOutlineModule {
    val FieldOutline: kotlin.js.JsAny
}

@JsModule("@vaadin/field-highlighter/src/vaadin-user-tag.js")
private external object UserTagModule {
    val UserTag: kotlin.js.JsAny
}

@JsModule("@vaadin/field-highlighter/src/vaadin-user-tags.js")
private external object UserTagsModule {
    val UserTags: kotlin.js.JsAny
}

@JsModule("@vaadin/field-highlighter/src/vaadin-ai-field-marker.js")
private external object AiFieldMarkerModule {
    val AiFieldMarker: kotlin.js.JsAny
}
