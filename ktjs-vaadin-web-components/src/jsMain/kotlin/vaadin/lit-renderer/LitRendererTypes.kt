package vaadin.litrenderer

/** Anything a Lit renderer may return: a template result, a string, or `null`. */
typealias LitRendererResult = kotlin.js.JsAny?

/**
 * A renderer callback that produces content for a Lit part.
 *
 * A plain JS function is a valid renderer, so the type is a Kotlin function type: an external
 * interface with an `operator fun invoke` compiles the call to `renderer.invoke(...)`, which a JS
 * function does not have.
 */
typealias LitRenderer = (container: kotlin.js.JsAny?, value: kotlin.js.JsAny?) -> LitRendererResult
