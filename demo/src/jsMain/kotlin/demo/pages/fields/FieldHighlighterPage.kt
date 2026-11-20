package demo.pages.fields

import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import io.github.shijianjs.ktjs.demo.page.Blocks.anchor as kitAnchor
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.ui.PageMeta
import demo.ui.Readout
import demo.componentApiUrl
import demo.ui.REPO_SOURCE_ROOT
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.fieldhighlighter.AiFieldMarkerConfidences
import io.github.shijianjs.ktjs.vaadin.fieldhighlighter.FieldHighlighter
import io.github.shijianjs.ktjs.vaadin.fieldhighlighter.FieldHighlighterUser
import io.github.shijianjs.ktjs.vaadin.fieldhighlighter.createAiFieldMarker
import io.github.shijianjs.ktjs.vaadin.fieldhighlighter.fieldHighlighterUser
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
import io.github.shijianjs.ktjs.vaadin.button.ButtonElement
import io.github.shijianjs.ktjs.vaadin.fieldhighlighter.AiFieldMarkerElement
import io.github.shijianjs.ktjs.vaadin.textfield.TextFieldElement
import io.github.shijianjs.ktjs.vaadin.textfield.createTextField
import io.github.shijianjs.ktjs.dom.element.inlineStyle
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import web.cssom.pct
import web.html.HTMLAnchorElement
import web.dom.clickEvent
import web.events.addHandler

val fieldHighlighterEntry: CatalogEntry =
    CatalogEntry(
        id = "field-highlighter",
        group = Groups.FIELDS,
        meta =
            PageMeta(
                title = bi("Field Highlighter", "Field Highlighter"),
                summary =
                    bi(
                        "协作编辑指示：给字段加上其他用户正在编辑的彩色轮廓与用户标签。",
                        "Collaboration indicator: adds a coloured outline and a user tag for other people " +
                            "editing the field.",
                    ),
                docsUrl = DOCS_BASE,
                docsNote =
                    bi(
                        "Field Highlighter 是内部协作组件，官方文档没有独立页面。",
                        "Field Highlighter is an internal collaboration component without a dedicated " +
                            "documentation page.",
                    ),
                sourcePath = "pages/fields/FieldHighlighterPage.kt",
            ),
        kitPage = { FieldHighlighterPage.NAME() },
    )

// ---------------------------------------------------------------------------------------------
// The same page on kit blocks
// ---------------------------------------------------------------------------------------------

private val fieldHighlighterEnglish: FieldHighlighterTexts = FieldHighlighterTexts(
    docsLink = "Vaadin documentation",
    sourceLink = "Kotlin source",
    apiLink = "Kotlin API reference",
    collabTitle = "Collaboration indicators",
    collabHint = "FieldHighlighter.addUser marks the field as edited by another user; focusing the field also " +
        "shows a user tag.",
    fieldLabel = "Shared title",
    fieldValue = "Quarterly report draft",
    fieldHelper = "The buttons below simulate other users joining and leaving",
    idle = "No other users right now",
    editingPrefix = "Editing: ",
    namesAlice = "Alice",
    namesBoth = "Alice, Bob",
    addAlice = "Alice starts editing",
    addBob = "Bob starts editing",
    removeAll = "Everyone leaves",
    aiTitle = "AI marker",
    aiHint = "ai-field-marker is a direct child of the field: working shows progress and confidence shows " +
        "the confidence level.",
    aiFieldLabel = "AI-filled field",
    aiFieldValue = "Automatic summary…",
    finishGeneration = "Finish AI generation",
    restartGeneration = "Restart generation",
)

private val fieldHighlighterChinese: FieldHighlighterTexts = FieldHighlighterTexts(
    docsLink = "Vaadin 官方文档",
    sourceLink = "查看 Kotlin 源码",
    apiLink = "Kotlin API 文档",
    collabTitle = "协作用户指示",
    collabHint = "FieldHighlighter.addUser 让字段显示其他用户正在编辑；焦点进入字段时还会出现用户标签。",
    fieldLabel = "共享标题",
    fieldValue = "季度报告草稿",
    fieldHelper = "下面的按钮模拟其他用户开始 / 结束编辑",
    idle = "当前没有其他用户",
    editingPrefix = "正在编辑：",
    namesAlice = "Alice",
    namesBoth = "Alice、Bob",
    addAlice = "Alice 开始编辑",
    addBob = "Bob 开始编辑",
    removeAll = "全部离开",
    aiTitle = "AI 标记",
    aiHint = "ai-field-marker 是字段的直接子节点：working 时显示进度，confidence 显示置信度。",
    aiFieldLabel = "AI 填充的字段",
    aiFieldValue = "自动摘要……",
    finishGeneration = "结束 AI 生成",
    restartGeneration = "重新开始生成",
)

/**
 * The page on kit blocks.
 *
 * The example keeps its state in two fields — who is editing, and whether the marker is still
 * generating — and every visible string is derived from that state plus the text class. A language
 * switch therefore rewrites the copy and leaves the state alone: a field Alice marked stays marked,
 * and the readout still names her.
 */
internal class FieldHighlighterPage : DemoPage() {
    private val meta: PageMeta = fieldHighlighterEntry.meta

    private val texts = Binder(fieldHighlighterTexts)

    private val collab = SectionBlock.NAME { show() }

    private val ai = SectionBlock.NAME { show() }

    private var language: Lang = Langs.EN

    /** The copy of [language]; readers below take their strings from here, never from the binder. */
    private val copy: FieldHighlighterTexts get() = fieldHighlighterTexts[language]

    private val field: TextFieldElement = createTextField().apply {
        dataset["test"] = "highlighted-field"
        inlineStyle.width = 100.pct
    }

    private val readout: Readout = Readout("")

    private val alice: FieldHighlighterUser = fieldHighlighterUser(1.0, "Alice", 0.0)

    private val bob: FieldHighlighterUser = fieldHighlighterUser(2.0, "Bob", 1.0)

    private val addAlice: ButtonElement = createButton()

    private val addBob: ButtonElement = createButton().addTheme("tertiary")

    private val removeAll: ButtonElement = createButton().addTheme("tertiary")

    private val aiField: TextFieldElement = createTextField().apply { inlineStyle.width = 100.pct }

    private val marker: AiFieldMarkerElement = createAiFieldMarker { working = true }.apply {
        confidence = AiFieldMarkerConfidences.high
    }

    private val toggleGeneration: ButtonElement = createButton()

    private val links = mutableListOf<HTMLAnchorElement>()

    /** Which users are currently marked as editing. */
    private var editing: Set<String> = emptySet()

    /** Whether the marker still shows progress. */
    private var generating: Boolean = true

    init {
        val docs = kitAnchor("", meta.docsUrlFor(Langs.EN))
        val source = kitAnchor("", "$REPO_SOURCE_ROOT/${meta.sourcePath}")
        val api = kitAnchor("", componentApiUrl(fieldHighlighterEntry.id))
        links += docs
        links += source
        links += api
        addLink(texts.bind(docs, { it.docsLink }))
        addLink(texts.bind(source, { it.sourceLink }))
        addLink(texts.bind(api, { it.apiLink }))

        addAlice.clickEvent.addHandler { markEditing(editing + "Alice", alice) }
        addBob.clickEvent.addHandler { markEditing(editing + "Bob", bob) }
        removeAll.clickEvent.addHandler {
            FieldHighlighter.removeUser(field, alice)
            FieldHighlighter.removeUser(field, bob)
            setEditing(emptySet())
        }
        toggleGeneration.clickEvent.addHandler { setGenerating(!generating) }

        aiField.append(marker)

        collab.body.append(
            exampleBox(null, kitCol(field, kitRow(addAlice, addBob, removeAll), readout.root)),
        )
        body.append(collab.root)

        ai.body.append(exampleBox(null, kitCol(aiField, toggleGeneration)))
        body.append(ai.root)

        applyTranslation(Langs.EN)
    }

    /** Marks [user] as editing and records that in the readout. */
    private fun markEditing(next: Set<String>, user: FieldHighlighterUser) {
        FieldHighlighter.addUser(field, user)
        setEditing(next)
    }

    /** Records who is editing; rewrites the readout only. */
    private fun setEditing(next: Set<String>) {
        editing = next
        readout.set(readoutText())
    }

    /** Records whether the marker generates; rewrites the marker and the button's own label. */
    private fun setGenerating(next: Boolean) {
        generating = next
        marker.working = next
        toggleGeneration.textContent =
            if (next) copy.finishGeneration else copy.restartGeneration
    }

    /** The readout line for the users currently editing. */
    private fun readoutText(): String =
        when {
            editing.isEmpty() -> copy.idle
            editing.size > 1 -> copy.editingPrefix + copy.namesBoth
            else -> copy.editingPrefix + copy.namesAlice
        }

    override fun applyTranslation(lang: Lang) {
        language = lang
        texts.apply(lang)
        collab.setText(copy.collabTitle, copy.collabHint)
        ai.setText(copy.aiTitle, copy.aiHint)
        field.label = copy.fieldLabel
        field.value = copy.fieldValue
        field.helperText = copy.fieldHelper
        aiField.label = copy.aiFieldLabel
        aiField.value = copy.aiFieldValue
        addAlice.textContent = copy.addAlice
        addBob.textContent = copy.addBob
        removeAll.textContent = copy.removeAll
        setEditing(editing)
        setGenerating(generating)
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        links.first().href = meta.docsUrlFor(lang)
    }

    companion object : CustomElementReferenceAdapter<FieldHighlighterPage>(FieldHighlighterPage::class.js, "vaadin-demo-field-highlighter-page") {
        private val fieldHighlighterTexts: Translations<FieldHighlighterTexts> = texts(
            Langs.EN to fieldHighlighterEnglish,
            Langs.ZH_HANS to fieldHighlighterChinese,
        )
    }
}
