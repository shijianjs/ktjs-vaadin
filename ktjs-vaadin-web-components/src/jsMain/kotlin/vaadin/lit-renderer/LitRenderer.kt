@file:JsModule("@vaadin/lit-renderer/src/lit-renderer.js")

package vaadin.litrenderer

/**
 * A Lit directive that connects a renderer callback to a Vaadin component.
 *
 * This is the low-level directive used by the components' `renderer` properties. In Kotlin/JS the
 * renderer callbacks themselves are ordinary lambdas assigned to the generated `renderer`
 * properties, so the directive is rarely needed directly.
 */
external class LitRendererDirective {
    /** Runs the renderer callback with the current value. */
    fun render(renderer: LitRenderer, value: kotlin.js.JsAny? = definedExternally): kotlin.js.JsAny

    /** Updates the directive part with the renderer and its value. */
    fun update(part: kotlin.js.JsAny, props: Array<kotlin.js.JsAny?>): kotlin.js.JsAny

    /** Adds the renderer callback to the element. */
    fun addRenderer()

    /** Runs the renderer callback on the element. */
    fun runRenderer()

    /** Removes the renderer callback from the element. */
    fun removeRenderer()

    /** Renders the result of a renderer callback into a container. */
    fun renderRenderer(container: web.dom.Element, vararg args: kotlin.js.JsAny?)
}
