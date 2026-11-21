package demo.pages.selection

import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.demo.page.Blocks.anchor as kitAnchor
import demo.CatalogEntry
import demo.Catalog
import demo.Groups
import demo.bi
import demo.ui.PageMeta
import demo.ui.REPO_SOURCE_ROOT
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.page.Blocks.row as kitRow
import io.github.shijianjs.ktjs.demo.page.Blocks.col as kitCol
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import io.github.shijianjs.ktjs.demo.page.Readout
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.item.createItem
import web.dom.clickEvent
import web.events.addHandler
import web.html.HTMLAnchorElement

/**
 * The legacy builder, kept until the whole catalog has moved to kit blocks.
 *
 * Every migrated page keeps its old builder for one reason: reverting a page that turns out to
 * render wrong is then deleting one line from the catalog instead of rewriting the page again.
 */
/**
 * The same page on kit blocks.
 *
 * The nodes are created once and the switch only rewrites their text, so a language change keeps the
 * examples' state — the item someone toggled stays toggled.
 */
internal class ItemPage : DemoPage() {
    private val meta: PageMeta = ENTRY.meta

    private val texts = Binder(itemTexts)

    private val readout = Readout.NAME { show("selected = true") }

    private val itemOne = createItem { value = "one" }.apply {
        dataset["test"] = "item-one"
        texts.bind(this) { it.itemLabel }
    }

    private val itemTwo = createItem()

    private val itemThree = createItem { disabled = true }

    private val selectedItem = createItem { selected = true }

    private val links = mutableListOf<HTMLAnchorElement>()

    init {
        val docs = kitAnchor("", meta.docsUrlFor(Langs.EN))
        val source = kitAnchor("", "$REPO_SOURCE_ROOT/${meta.sourcePath}")
        val api = kitAnchor("", Catalog.componentApiUrl(ENTRY.id))
        links += docs
        links += source
        links += api
        addLink(texts.bind(docs, { it.docsLink }))
        addLink(texts.bind(source, { it.sourceLink }))
        addLink(texts.bind(api, { it.apiLink }))

        val basic = SectionBlock.NAME { show() }
        texts.bind(basic.heading, { it.basicTitle })
        texts.bind(basic.hint, { it.basicHint })
        texts.bind(itemTwo, { it.itemSlot })
        texts.bind(itemThree, { it.itemDisabled })
        basic.body.append(exampleBox(null, kitRow(itemOne, itemTwo, itemThree)))
        body.append(basic.root)

        val selection = SectionBlock.NAME { show() }
        texts.bind(selection.heading, { it.selectedTitle })
        texts.bind(selection.hint, { it.selectedHint })
        texts.bind(selectedItem, { it.selectedItem })
        val toggle = createButton()
        texts.bind(toggle, { it.toggle })
        toggle.clickEvent.addHandler {
            selectedItem.selected = !selectedItem.selected
            readout.set("selected = ${selectedItem.selected}")
        }
        selection.body.append(
            exampleBox(null, kitCol(selectedItem, toggle)),
            readout.root,
        )
        body.append(selection.root)

        applyTranslation(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        texts.apply(lang)
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        // The docs URL itself follows the language (`/latest/zh/` vs `/latest/`).
        links.first().href = meta.docsUrlFor(lang)
    }

    companion object : ItemPageDefaultReference() {
        val ENTRY: CatalogEntry =
            CatalogEntry(
                id = "item",
                group = Groups.SELECTION,
                meta =
                    PageMeta(
                        title = bi("Item", "Item"),
                        summary =
                            bi(
                                "List Box / Select / Context Menu 使用的条目元素，也可以单独使用。",
                                "Item element used by List Box / Select / Context Menu; it also works on its own.",
                            ),
                        docsUrl = "${Catalog.DOCS_BASE}/list-box",
                        docsNote =
                            bi(
                                "vaadin-item 没有独立文档页面，用法见 List Box。",
                                "vaadin-item has no dedicated documentation page; see List Box.",
                            ),
                        sourcePath = "pages/selection/ItemPage.kt",
                    ),
                kitPage = { ItemPage.NAME() },
            )

        private val itemEnglish: ItemTexts = ItemTexts(
            docsLink = "Vaadin documentation",
            sourceLink = "Kotlin source",
            apiLink = "Kotlin API reference",
            basicTitle = "Standalone use",
            basicHint = "vaadin-item is usually created inside List Box / Select / Context Menu; this page shows " +
                "its properties on its own.",
            itemLabel = "Item with a label",
            itemSlot = "Default slot text",
            itemDisabled = "Disabled",
            selectedTitle = "Selected state",
            selectedHint = "selected is purely visual; the item does not maintain selection logic itself.",
            selectedItem = "Selected item",
            toggle = "Toggle selected",
        )
        private val itemChinese: ItemTexts = ItemTexts(
            docsLink = "Vaadin 官方文档",
            sourceLink = "查看 Kotlin 源码",
            apiLink = "Kotlin API 文档",
            basicTitle = "独立使用",
            basicHint = "vaadin-item 通常由 List Box / Select / Context Menu 内部创建；这里单独展示它的属性。",
            itemLabel = "带 label 的条目",
            itemSlot = "默认插槽文本",
            itemDisabled = "已禁用",
            selectedTitle = "选中状态",
            selectedHint = "selected 只是视觉状态，条目本身不维护选中逻辑。",
            selectedItem = "被选中的条目",
            toggle = "切换 selected",
        )
        private val itemTexts: Translations<ItemTexts> = texts(
            Langs.EN to itemEnglish,
            Langs.ZH_HANS to itemChinese,
        )
    }
}
