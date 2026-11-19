package demo.pages.selection


import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import web.components.CustomElementReference
import web.dom.TagName
import io.github.shijianjs.ktjs.demo.page.anchor as kitAnchor
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.Language
import demo.LanguageState
import demo.bi
import demo.componentApiUrl
import demo.tr
import demo.ui.PageMeta
import demo.ui.REPO_SOURCE_ROOT
import demo.ui.column
import demo.ui.example
import demo.ui.row
import demo.ui.section
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.page.Readout
import io.github.shijianjs.ktjs.demo.page.anchor
import io.github.shijianjs.ktjs.demo.page.exampleBox
import io.github.shijianjs.ktjs.demo.page.row as kitRow
import io.github.shijianjs.ktjs.demo.page.col as kitCol
import io.github.shijianjs.ktjs.demo.page.sectionBlock
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.item.createItem
import web.dom.clickEvent
import web.events.addHandler
import web.html.HTMLAnchorElement

/** Every string this page shows, in one language. */
private class ItemTexts(
    val docsLink: String,
    val sourceLink: String,
    val apiLink: String,
    val basicTitle: String,
    val basicHint: String,
    val itemLabel: String,
    val itemSlot: String,
    val itemDisabled: String,
    val selectedTitle: String,
    val selectedHint: String,
    val selectedItem: String,
    val toggle: String,
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

val itemEntry: CatalogEntry =
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
                docsUrl = "$DOCS_BASE/list-box",
                docsNote =
                    bi(
                        "vaadin-item 没有独立文档页面，用法见 List Box。",
                        "vaadin-item has no dedicated documentation page; see List Box.",
                    ),
                sourcePath = "pages/selection/ItemPage.kt",
            ),
        kitPage = { newItemPage() },
    )

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
    private val meta: PageMeta = itemEntry.meta


    private val texts = Binder(itemTexts)

    private val readout = Readout("selected = true")

    private val itemOne = createItem { value = "one" }.apply {
        dataset["test"] = "item-one"
        texts.bind(this) { it.itemLabel }
    }

    private val itemTwo = createItem()

    private val itemThree = createItem { disabled = true }

    private val selectedItem = createItem { selected = true }

    private val links = mutableListOf<HTMLAnchorElement>()


    init {
        val docs = kitAnchor("", meta.docsUrl)
        val source = kitAnchor("", "$REPO_SOURCE_ROOT/${meta.sourcePath}")
        val api = kitAnchor("", componentApiUrl(itemEntry.id))
        links += docs
        links += source
        links += api
        addLink(texts.bind(docs, { it.docsLink }))
        addLink(texts.bind(source, { it.sourceLink }))
        addLink(texts.bind(api, { it.apiLink }))

        val basic = sectionBlock()
        texts.bind(basic.title, { it.basicTitle })
        texts.bind(basic.hint, { it.basicHint })
        texts.bind(itemTwo, { it.itemSlot })
        texts.bind(itemThree, { it.itemDisabled })
        basic.body.append(exampleBox(null, kitRow(itemOne, itemTwo, itemThree)))
        body.append(basic.root)

        val selection = sectionBlock()
        texts.bind(selection.title, { it.selectedTitle })
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
        val language = if (lang == Langs.EN) Language.EN else Language.ZH
        setTitle(meta.titleFor(language))
        setSummary(meta.summaryFor(language))
        // The docs URL itself follows the language (`/latest/zh/` vs `/latest/`).
        links.first().href = meta.docsUrl
    }

    companion object : CustomElementReference<ItemPage>(ItemPage::class.js) {
        val NAME: TagName<ItemPage> = TagName("vaadin-demo-item-page")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<vaadin-demo-item-page>`. */
internal fun newItemPage(): ItemPage {
    ItemPage.define()
    return ItemPage.NAME()
}


