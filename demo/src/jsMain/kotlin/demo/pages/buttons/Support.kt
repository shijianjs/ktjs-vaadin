package demo.pages.buttons

/**
 * Repeating timer used by the long-running example. kotlin-browser has no timer wrapper, so the
 * two browser calls stay behind these helpers instead of spreading `js(...)` through the page.
 */
internal var intervalHandle: Int? = null
