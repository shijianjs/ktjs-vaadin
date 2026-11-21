package demo.site

/**
 * The site's own identity: the version label the navbar shows and the three URLs the layout links to.
 *
 * These are pure configuration — they read nothing else and nothing else reads them at file init — but
 * they belong together: the layout hands all four to the shell at mount time, so one named home keeps
 * the site's identity out of the menu's own file.
 */
internal object Site {
    /** The wrapper's version, shown under the site name: the npm packages it wraps. */
    const val VERSION: String = "Vaadin 25"

    /** The documentation site the demo is published under; the link menu opens it in a new tab. */
    const val DOCS_URL: String = "https://shijianjs.github.io/ktjs-vaadin/"

    /** Dokka reference assembled by `pagesSite` next to the demo. */
    const val API_URL: String = "https://shijianjs.github.io/ktjs-vaadin/api/index.html"

    /** Source repository of the wrappers and of this demo. */
    const val SOURCE_URL: String = "https://github.com/shijianjs/ktjs-vaadin"
}
