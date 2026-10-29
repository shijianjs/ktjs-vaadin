package demo.ui

/**
 * Images and icons copied from the Vaadin documentation repository, so the examples show the same
 * pictures as the official demos. Provenance and licensing are documented in the `ATTRIBUTION.md`
 * files next to the assets under `demo/src/jsMain/resources/`.
 *
 * Paths are relative to `index.html`, which keeps the demo working under any base path without
 * requesting anything from the network.
 */
object Images {
    const val vaadinLogo: String = "images/vaadin-logo-dark.png"
    const val companyLogo: String = "images/company-logo.png"
    const val lapland: String = "images/lapland.avif"
    const val reindeer: String = "images/reindeer.jpg"

    const val codeBranchIcon: String = "icons/code-branch.svg"
    const val solidSprite: String = "icons/solid.svg"

    private val portraits: List<String> =
        (1..20).map { index -> "images/people/person-${index.toString().padStart(2, '0')}.jpg" }

    /** One of the twenty portraits the docs demo ships in `peopleImages.json`; [index] wraps. */
    fun portrait(index: Int): String {
        val size = portraits.size
        return portraits[((index % size) + size) % size]
    }
}
