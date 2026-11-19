package demo.pages.overview

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.page.overview.OverviewPage
import demo.ui.PageMeta

/**
 * The landing entry of the catalog.
 *
 * Its page is built on kit blocks (`demo.page.overview`), so this file only carries the metadata the
 * sidebar and the route table need. It used to own the builder too; the legacy version is gone.
 */
val overviewEntry: CatalogEntry =
    CatalogEntry(
        id = "overview",
        group = Groups.OVERVIEW,
        meta =
            PageMeta(
                title = bi("组件总览", "Component overview"),
                summary =
                    bi(
                        "免费版 Vaadin Web Components 的 Kotlin/JS 封装，逐组件演示属性、变体与交互。",
                        "Kotlin/JS wrappers for the free Vaadin Web Components, demonstrating properties, " +
                            "variants and interactions component by component.",
                    ),
                docsUrl = DOCS_BASE,
                docsNote = bi("官方文档的组件索引页", "Component index of the official documentation"),
                sourcePath = "page/overview/OverviewPage.kt",
            ),
        kitPage = ::OverviewPage,
    )
