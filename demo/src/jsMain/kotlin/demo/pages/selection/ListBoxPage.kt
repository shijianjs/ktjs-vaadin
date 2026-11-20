package demo.pages.selection

import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.Blocks.col as kitCol
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.page.Blocks.row as kitRow
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.pages.fixtures.people
import demo.ui.PageFrame
import demo.ui.PageMeta
import demo.ui.Readout
import demo.ui.joinValues
import demo.ui.listen
import io.github.shijianjs.ktjs.vaadin.a11ybase.ListOrientations
import io.github.shijianjs.ktjs.vaadin.avatar.createAvatar
import io.github.shijianjs.ktjs.vaadin.item.ItemElement
import io.github.shijianjs.ktjs.vaadin.item.createItem
import io.github.shijianjs.ktjs.vaadin.listbox.ListBoxEvents
import io.github.shijianjs.ktjs.vaadin.listbox.createListBox
import web.html.HTMLSpanElement
import web.html.HtmlTagName

val listBoxEntry: CatalogEntry =
    CatalogEntry(
        id = "list-box",
        group = Groups.SELECTION,
        meta =
            PageMeta(
                title = bi("List Box", "List Box"),
                summary =
                    bi(
                        "可滚动的选项列表，支持分隔线、禁用条目、单选与多选。",
                        "Scrollable list of options with dividers, disabled items, single and multiple " +
                            "selection.",
                    ),
                docsUrl = "$DOCS_BASE/list-box",
                sourcePath = "pages/selection/ListBoxPage.kt",
            ),
        kitPage = { ListBoxPage.NAME() },
    )

private val english: ListBoxTexts = ListBoxTexts(
    dividersHeading = "Dividers",
    dividersHint = "A list can mix in <hr> elements as group dividers; they take no part in selection.",
    assignee = "Show assignee",
    dueDate = "Show due date",
    status = "Show status",
    thumbnail = "Show thumbnail",
    preview = "Show preview",
    disabledHeading = "Disabled items",
    disabledHint = "Individual items can be disabled while the rest of the list stays interactive.",
    inProgressWithCount = "In progress (2)",
    doneWithCount = "Done (4)",
    cancelledWithCount = "Cancelled (0)",
    singleHeading = "Single selection",
    singleHint = "selected is the index of the selected item (Double); it is undefined when nothing is selected.",
    inProgress = "In progress",
    done = "Done",
    cancelled = "Cancelled",
    multipleHeading = "Multiple selection",
    multipleHint = "With multiple on, selectedValues reads and writes every selected index; the list scrolls " +
        "inside a fixed height.",
    presentationHeading = "Custom item presentation",
    presentationHint = "Item content is plain DOM: avatar, name and profession, highlighted together with the " +
        "selection state.",
    orientationHeading = "Orientation and disabled state",
    orientationHint = "orientation sets the layout direction; disabling blocks the whole group.",
    one = "One",
    two = "Two",
    three = "Three",
    disabledItemOne = "Disabled item 1",
    disabledItemTwo = "Disabled item 2",
    professions = listOf(
        "Software Engineer",
        "Product Designer",
        "Data Analyst",
        "Frontend Developer",
        "Architect",
        "Test Engineer",
    ),
)

private val chinese: ListBoxTexts = ListBoxTexts(
    dividersHeading = "分隔线",
    dividersHint = "列表里可以混入 <hr> 作为分组分隔线，它不参与选择。",
    assignee = "显示负责人",
    dueDate = "显示截止日期",
    status = "显示状态",
    thumbnail = "显示缩略图",
    preview = "显示预览",
    disabledHeading = "禁用条目",
    disabledHint = "单个条目可以单独禁用，列表其余部分仍可交互。",
    inProgressWithCount = "进行中 (2)",
    doneWithCount = "已完成 (4)",
    cancelledWithCount = "已取消 (0)",
    singleHeading = "单选",
    singleHint = "selected 是选中项的下标（Double），未选中时为 undefined。",
    inProgress = "进行中",
    done = "已完成",
    cancelled = "已取消",
    multipleHeading = "多选",
    multipleHint = "multiple 打开后用 selectedValues 读写所有选中下标；列表可滚动，固定高度即可。",
    presentationHeading = "自定义条目呈现",
    presentationHint = "条目内容是普通 DOM：头像 + 姓名 + 职业，随选中状态一并高亮。",
    orientationHeading = "方向与禁用",
    orientationHint = "orientation 控制排布方向，禁用后整组不可交互。",
    one = "一",
    two = "二",
    three = "三",
    disabledItemOne = "禁用项一",
    disabledItemTwo = "禁用项二",
    professions = listOf("软件工程师", "产品设计师", "数据分析师", "前端开发", "架构师", "测试工程师"),
)

private val listBoxTexts: Translations<ListBoxTexts> = texts(
    Langs.EN to english,
    Langs.ZH_HANS to chinese,
)

internal class ListBoxPage : DemoPage() {
    private val texts = Binder(listBoxTexts)

    private val frame = PageFrame(listBoxEntry)

    /** The five toggle items of the first example, in list order. */
    private val toggleItems: List<ItemElement> = List(5) { createItem() }

    /** The three status items of the second example, in list order. */
    private val statusItems: List<ItemElement> = List(3) { createItem() }

    /** The three status items of the third example, in list order. */
    private val singleItems: List<ItemElement> = List(3) { createItem() }

    /** The profession spans of the rich example: the only part of those items that is copy. */
    private val professionSpans: List<HTMLSpanElement> = List(5) { HtmlTagName.span() }

    private val horizontalItems: List<ItemElement> = List(3) { createItem() }

    private val disabledItems: List<ItemElement> = List(2) { createItem() }

    init {
        frame.mount(this)

        val dividers = SectionBlock.NAME { show() }
        texts.bind(dividers.heading) { it.dividersHeading }
        texts.bind(dividers.hint) { it.dividersHint }
        val separated = createListBox { multiple = true }
        separated.selectedValues = arrayOf(0.0, 2.0, 3.0)
        texts.bind(toggleItems[0]) { it.assignee }
        texts.bind(toggleItems[1]) { it.dueDate }
        texts.bind(toggleItems[2]) { it.status }
        texts.bind(toggleItems[3]) { it.thumbnail }
        texts.bind(toggleItems[4]) { it.preview }
        separated.append(
            toggleItems[0],
            toggleItems[1],
            toggleItems[2],
            HtmlTagName.hr(),
            toggleItems[3],
            toggleItems[4],
        )
        val separatedReadout = Readout("selectedValues = 0, 2, 3")
        separated.listen(ListBoxEvents.selectedValuesChanged) {
            separatedReadout.set("selectedValues = ", joinValues(separated.selectedValues).ifEmpty { "[]" })
        }
        dividers.body.append(exampleBox(null, kitCol(separated, separatedReadout.root)))
        body.append(dividers.root)

        val disabledItemsSection = SectionBlock.NAME { show() }
        texts.bind(disabledItemsSection.heading) { it.disabledHeading }
        texts.bind(disabledItemsSection.hint) { it.disabledHint }
        val withDisabled = createListBox()
        withDisabled.selected = 0.0
        texts.bind(statusItems[0]) { it.inProgressWithCount }
        texts.bind(statusItems[1]) { it.doneWithCount }
        texts.bind(statusItems[2]) { it.cancelledWithCount }
        statusItems[2].disabled = true
        withDisabled.append(*statusItems.toTypedArray())
        disabledItemsSection.body.append(exampleBox(null, withDisabled))
        body.append(disabledItemsSection.root)

        val single = SectionBlock.NAME { show() }
        texts.bind(single.heading) { it.singleHeading }
        texts.bind(single.hint) { it.singleHint }
        val singleBox = createListBox()
        singleBox.dataset["test"] = "list-box-single"
        singleBox.selected = 0.0
        texts.bind(singleItems[0]) { it.inProgress }
        texts.bind(singleItems[1]) { it.done }
        texts.bind(singleItems[2]) { it.cancelled }
        singleBox.append(*singleItems.toTypedArray())
        val readout = Readout("selected = 0")
        singleBox.listen(ListBoxEvents.selectedChanged) {
            readout.set("selected = ", singleBox.selected.toString())
        }
        single.body.append(exampleBox(null, kitRow(singleBox, readout.root)))
        body.append(single.root)

        val multipleSection = SectionBlock.NAME { show() }
        texts.bind(multipleSection.heading) { it.multipleHeading }
        texts.bind(multipleSection.hint) { it.multipleHint }
        val multiBox = createListBox { multiple = true }
        multiBox.selectedValues = arrayOf(0.0, 3.0)
        multiBox.style.height = "200px"
        multiBox.append(
            *people(20).map { person -> createItem { textContent = person.displayName } }.toTypedArray(),
        )
        val multiReadout = Readout("selectedValues = 0, 3")
        multiBox.listen(ListBoxEvents.selectedValuesChanged) {
            multiReadout.set("selectedValues = ", joinValues(multiBox.selectedValues).ifEmpty { "[]" })
        }
        multipleSection.body.append(exampleBox(null, kitCol(multiBox, multiReadout.root)))
        body.append(multipleSection.root)

        val presentation = SectionBlock.NAME { show() }
        texts.bind(presentation.heading) { it.presentationHeading }
        texts.bind(presentation.hint) { it.presentationHint }
        val rich = createListBox { multiple = true }
        rich.selectedValues = arrayOf(0.0, 2.0)
        val people = people(5)
        rich.append(
            *people.mapIndexed { index, person ->
                createItem {
                    append(
                        kitRow(
                            createAvatar {
                                name = person.displayName
                                img = person.pictureUrl
                            },
                            kitCol(HtmlTagName.span { textContent = person.displayName }, professionSpans[index]),
                        ),
                    )
                }
            }.toTypedArray(),
        )
        professionSpans.forEachIndexed { index, span ->
            texts.bind(span) { copy -> copy.professions[index % copy.professions.size] }
        }
        presentation.body.append(exampleBox(null, rich))
        body.append(presentation.root)

        val orientationSection = SectionBlock.NAME { show() }
        texts.bind(orientationSection.heading) { it.orientationHeading }
        texts.bind(orientationSection.hint) { it.orientationHint }
        val horizontal = createListBox { orientation = ListOrientations.horizontal }
        texts.bind(horizontalItems[0]) { it.one }
        texts.bind(horizontalItems[1]) { it.two }
        texts.bind(horizontalItems[2]) { it.three }
        horizontal.append(*horizontalItems.toTypedArray())
        val disabledBox = createListBox()
        disabledBox.disabled = true
        texts.bind(disabledItems[0]) { it.disabledItemOne }
        texts.bind(disabledItems[1]) { it.disabledItemTwo }
        disabledBox.append(*disabledItems.toTypedArray())
        orientationSection.body.append(exampleBox(null, kitRow(horizontal, disabledBox)))
        body.append(orientationSection.root)

        applyTranslation(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = listBoxEntry.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
    }

    companion object : CustomElementReferenceAdapter<ListBoxPage>(ListBoxPage::class.js, "vaadin-demo-list-box-page")
}
