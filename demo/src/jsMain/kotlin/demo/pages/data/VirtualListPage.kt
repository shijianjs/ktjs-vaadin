package demo.pages.data

import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.page.Blocks.col as kitCol
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.page.Blocks.row as kitRow
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import web.html.HtmlTagName
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.ui.PageFrame
import demo.ui.PageMeta
import demo.ui.jsArray
import demo.ui.listen
import kotlin.js.JsAny
import io.github.shijianjs.ktjs.vaadin.avatar.createAvatar
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.details.DetailsEvents
import io.github.shijianjs.ktjs.vaadin.details.createDetails
import io.github.shijianjs.ktjs.vaadin.horizontallayout.createHorizontalLayout
import io.github.shijianjs.ktjs.vaadin.verticallayout.createVerticalLayout
import io.github.shijianjs.ktjs.vaadin.virtuallist.VirtualListElement
import io.github.shijianjs.ktjs.vaadin.virtuallist.createVirtualList
import web.dom.clickEvent
import web.events.addHandler

val virtualListEntry: CatalogEntry =
    CatalogEntry(
        id = "virtual-list",
        group = Groups.DATA,
        meta =
            PageMeta(
                title = bi("Virtual List", "Virtual List"),
                summary =
                    bi(
                        "只用 renderer 渲染可见行的长列表，适合成千上万条数据。",
                        "Long lists that render only visible rows through a renderer, built for " +
                            "thousands of items.",
                    ),
                docsUrl = "$DOCS_BASE/virtual-list",
                sourcePath = "pages/data/VirtualListPage.kt",
            ),
        kitPage = { VirtualListPage.NAME() },
    )

private val english: VirtualListTexts = VirtualListTexts(
    basicHeading = "Basic usage",
    basicHint = "The renderer is called only for visible rows and container nodes are reused while scrolling. " +
        "Each row is a person card, and an expanded contact block stays open across scrolling.",
    mathematician = "Mathematician",
    computerScientist = "Computer scientist",
    cryptanalyst = "Cryptanalyst",
    aerospaceEngineer = "Aerospace engineer",
    contactInfo = "Contact information",
    jumpHeading = "Scrolling to an index",
    jumpHint = "scrollToIndex jumps straight to the given row.",
    goToFirst = "Go to row 1",
    goTo300 = "Go to row 300",
    row = { index -> "Row $index" },
)

private val chinese: VirtualListTexts = VirtualListTexts(
    basicHeading = "基本用法",
    basicHint = "renderer 只对可见行调用，滚动时复用容器节点；每一行是一张人员卡片，展开联系方式后" +
        "滚动离开再回来仍保持展开。",
    mathematician = "数学家",
    computerScientist = "计算机科学家",
    cryptanalyst = "密码学家",
    aerospaceEngineer = "航空航天工程师",
    contactInfo = "联系方式",
    jumpHeading = "定位",
    jumpHint = "scrollToIndex 直接跳到指定行。",
    goToFirst = "跳到第 1 行",
    goTo300 = "跳到第 300 行",
    row = { index -> "行 $index" },
)

private val virtualListTexts: Translations<VirtualListTexts> = texts(
    Langs.EN to english,
    Langs.ZH_HANS to chinese,
)

internal class VirtualListPage : DemoPage() {
    private val texts = Binder(virtualListTexts)

    private val frame = PageFrame(virtualListEntry)

    /**
     * Both lists re-run their renderer on a language switch: the rows are built from the copy, and the
     * visible ones are already on screen when the language changes.
     */
    private val list: VirtualListElement = createVirtualList()

    private val jumpList: VirtualListElement = createVirtualList()

    /** Which rows are expanded; the renderer keeps that state, so it survives a re-render. */
    private val expanded = mutableSetOf<Int>()

    /** The 200 rows, rebuilt from the copy when the language changes: a row's profession is copy. */
    private var people: List<PersonCard> = rows(english)

    init {
        frame.mount(this)

        val basic = SectionBlock.NAME { show() }
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        list.dataset["test"] = "virtual-list"
        list.items = jsArray<JsAny>((0 until people.size).joinToString(",", "[", "]"))
        list.renderer = { container, _, model ->
            val index = model.index.toInt()
            val person = people[index]
            val copy = texts.current

            val details =
                createDetails {
                    summary = copy.contactInfo
                    opened = expanded.contains(index)
                }
            details.listen(DetailsEvents.openedChanged) {
                if (details.opened) expanded.add(index) else expanded.remove(index)
            }
            details.append(
                createVerticalLayout().apply {
                    append(HtmlTagName.span {
                        textContent = person.email
                    }, HtmlTagName.span {
                        textContent = person.phone
                    })
                    addTheme("spacing")
                },
            )

            val text =
                createVerticalLayout().apply {
                    append(HtmlTagName.strong {
                        textContent = person.fullName
                    }, HtmlTagName.span {
                        textContent = person.profession
                    }, details)
                    addTheme("spacing")
                }
            val card =
                createHorizontalLayout().apply {
                    append(
                        createAvatar {
                            name = person.fullName
                            img = person.photo
                        }.addTheme("xlarge"),
                        text,
                    )
                    addTheme("spacing", "margin")
                }
            container.replaceChildren(card)
        }
        list.style.setProperty("height", "20rem")
        basic.body.append(exampleBox(null, list))
        body.append(basic.root)

        val jump = SectionBlock.NAME { show() }
        texts.bind(jump.heading) { it.jumpHeading }
        texts.bind(jump.hint) { it.jumpHint }
        jumpList.items = jsArray<JsAny>((1..500).joinToString(",", "[", "]"))
        jumpList.renderer = { container, _, model ->
            val copy = texts.current
            val row = HtmlTagName.span {
                textContent = copy.row(model.index.toInt() + 1)
            }
            container.replaceChildren(row)
        }
        jumpList.style.setProperty("height", "12rem")
        jump.body.append(
            exampleBox(
                null,
                kitCol(
                    jumpList,
                    kitRow(
                        createButton {
                            textContent = english.goToFirst
                            addTheme("tertiary")
                            clickEvent.addHandler { jumpList.scrollToIndex(0.0) }
                        },
                        createButton {
                            textContent = english.goTo300
                            addTheme("tertiary")
                            clickEvent.addHandler { jumpList.scrollToIndex(299.0) }
                        },
                    ),
                ),
            ),
        )
        body.append(jump.root)

        applyTranslation(Langs.EN)
    }

    /**
     * The five people the docs show, repeated to two hundred rows: repeating the fixed set is enough to
     * show that row containers are reused.
     */
    private fun rows(copy: VirtualListTexts): List<PersonCard> {
        val base =
            listOf(
                PersonCard("Ada", "Lovelace", copy.mathematician, "ada.lovelace@example.com", "+44 20 7946 0101", 0),
                PersonCard(
                    "Grace",
                    "Hopper",
                    copy.computerScientist,
                    "grace.hopper@example.com",
                    "+1 202 555 0142",
                    1,
                ),
                PersonCard("Alan", "Turing", copy.cryptanalyst, "alan.turing@example.com", "+44 20 7946 0193", 2),
                PersonCard(
                    "Katherine",
                    "Johnson",
                    copy.aerospaceEngineer,
                    "katherine.johnson@example.com",
                    "+1 202 555 0177",
                    3,
                ),
                PersonCard(
                    "Edsger",
                    "Dijkstra",
                    copy.computerScientist,
                    "edsger.dijkstra@example.com",
                    "+31 20 555 0124",
                    4,
                ),
            )
        return List(200) { index -> base[index % base.size] }
    }

    override fun applyTranslation(lang: Lang) {
        val meta = virtualListEntry.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        people = rows(texts.current)
        list.requestContentUpdate()
        jumpList.requestContentUpdate()
    }

    companion object : CustomElementReferenceAdapter<VirtualListPage>(VirtualListPage::class.js, "vaadin-demo-virtual-list-page")
}
