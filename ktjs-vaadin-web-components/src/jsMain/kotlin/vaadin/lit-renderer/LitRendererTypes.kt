package vaadin.litrenderer

/** Anything a Lit renderer may return: a template result, a string, or `null`. */
typealias LitRendererResult = kotlin.js.JsAny?

/** A renderer callback that produces content for a Lit part. */
external interface LitRenderer {
    operator fun invoke(vararg args: kotlin.js.JsAny?): LitRendererResult
}
