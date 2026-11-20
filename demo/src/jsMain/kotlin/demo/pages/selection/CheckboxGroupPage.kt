package demo.pages.selection

import io.github.shijianjs.ktjs.demo.page.col as kitCol
import io.github.shijianjs.ktjs.demo.page.exampleBox
import io.github.shijianjs.ktjs.demo.page.row as kitRow
import io.github.shijianjs.ktjs.demo.page.sectionBlock


import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import web.components.CustomElementReference
import web.dom.TagName
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.ui.PageFrame
import demo.ui.PageMeta
import demo.ui.Readout
import demo.ui.joinValues
import demo.ui.listen
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.checkbox.createCheckbox
import io.github.shijianjs.ktjs.vaadin.checkboxgroup.CheckboxGroupElement
import io.github.shijianjs.ktjs.vaadin.checkboxgroup.CheckboxGroupEvents
import io.github.shijianjs.ktjs.vaadin.checkboxgroup.createCheckboxGroup
import io.github.shijianjs.ktjs.vaadin.tooltip.createTooltip
import web.dom.clickEvent
import web.events.addHandler

val checkboxGroupEntry: CatalogEntry =
    CatalogEntry(
        id = "checkbox-group",
        group = Groups.SELECTION,
        meta =
            PageMeta(
                title = bi("Checkbox Group", "Checkbox Group"),
                summary =
                    bi(
                        "多选组：value 是数组，支持必填校验与整体只读。",
                        "Multi-select group: value is an array, with required validation and a " +
                            "group-wide read-only state.",
                    ),
                docsUrl = "$DOCS_BASE/checkbox",
                docsNote =
                    bi(
                        "Checkbox Group 与 Checkbox 共用一页官方文档。",
                        "Checkbox Group shares the Checkbox documentation page.",
                    ),
                sourcePath = "pages/selection/CheckboxGroupPage.kt",
            ),
        kitPage = { newCheckboxGroupPage() },
    )

/** Every string this page shows, in one language. */
private class CheckboxGroupTexts(
    val basicHeading: String,
    val basicHint: String,
    val groupLabel: String,
    val groupHelper: String,
    val presetHeading: String,
    val presetHint: String,
    val skillsLabel: String,
    val skillsError: String,
    val validate: String,
    val validationPrefix: String,
    val passed: String,
    val failed: String,
    val selectedPrefix: String,
    val featuresHeading: String,
    val featuresHint: String,
    val label: String,
    val helper: String,
    val tooltipText: String,
    val item1: String,
    val item2: String,
    val item3: String,
    val optionA: String,
    val optionB: String,
    val statesHeading: String,
    val readonly: String,
    val disabled: String,
    val engineering: String,
    val humanResources: String,
    val marketing: String,
)

private val english: CheckboxGroupTexts =
    CheckboxGroupTexts(
        basicHeading = "Basic usage",
        basicHint = "The value of a checkbox-group is an array; the items are told apart by their value " +
            "property.",
        groupLabel = "Languages used",
        groupHelper = "Multiple answers are allowed",
        presetHeading = "Preset values and validation",
        presetHint = "value can be preset; required makes validate() fail on an empty selection.",
        skillsLabel = "Skills (required)",
        skillsError = "Select at least one",
        validate = "Validate",
        validationPrefix = "Validation: ",
        passed = "passed",
        failed = "failed",
        selectedPrefix = "Selected: ",
        featuresHeading = "Label, helper text and tooltip",
        featuresHint = "label and helper-text live on the group; a tooltip is attached to the field " +
            "through slot=\"tooltip\".",
        label = "Label",
        helper = "Helper text",
        tooltipText = "Tooltip text",
        item1 = "Item 1",
        item2 = "Item 2",
        item3 = "Item 3",
        optionA = "Option A",
        optionB = "Option B",
        statesHeading = "Read-only and disabled",
        readonly = "Read-only",
        disabled = "Disabled",
        engineering = "Engineering",
        humanResources = "Human Resources",
        marketing = "Marketing",
    )

private val chinese: CheckboxGroupTexts =
    CheckboxGroupTexts(
        basicHeading = "基本用法",
        basicHint = "checkbox-group 的 value 是数组，子项通过 value 属性区分。",
        groupLabel = "使用的语言",
        groupHelper = "可以多选",
        presetHeading = "预设值与校验",
        presetHint = "value 可以预先赋值；required 在空选择时让 validate() 失败。",
        skillsLabel = "技能（必填）",
        skillsError = "至少选择一项",
        validate = "校验",
        validationPrefix = "校验：",
        passed = "通过",
        failed = "失败",
        selectedPrefix = "已选：",
        featuresHeading = "标签、辅助信息与提示",
        featuresHint = "label 与 helper-text 直接写在组上；tooltip 通过 slot=\"tooltip\" 挂到字段上。",
        label = "标签",
        helper = "辅助信息",
        tooltipText = "提示文本",
        item1 = "选项 1",
        item2 = "选项 2",
        item3 = "选项 3",
        optionA = "选项 A",
        optionB = "选项 B",
        statesHeading = "只读与禁用",
        readonly = "只读",
        disabled = "禁用",
        engineering = "工程",
        humanResources = "人力资源",
        marketing = "市场",
    )

private val checkboxGroupTexts: Translations<CheckboxGroupTexts> = texts(
    Langs.EN to english,
    Langs.ZH_HANS to chinese,
)

internal class CheckboxGroupPage : DemoPage() {
    private val texts = Binder(checkboxGroupTexts)

    private val frame = PageFrame(checkboxGroupEntry)

    /** The last line put in the shared result readout, so a language switch rewrites it in place. */
    private var lastResult: ((CheckboxGroupTexts) -> Pair<String, String>)? = null

    private val skills: CheckboxGroupElement = createCheckboxGroup()

    private val result = Readout("value = css, html")

    init {
        frame.mount(this)

        val basic = sectionBlock()
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        val group = createCheckboxGroup().addTheme("vertical")
        texts.bind(group, { it.groupLabel }) { group.label = it }
        texts.bind(group, { it.groupHelper }) { group.helperText = it }
        group.dataset["test"] = "checkbox-group"
        group.append(
            createCheckbox {
                value = "kotlin"
                textContent = "Kotlin"
            },
            createCheckbox {
                value = "java"
                textContent = "Java"
            },
            createCheckbox {
                value = "js"
                textContent = "JavaScript"
            },
        )
        val readout = Readout("value = []")
        group.listen(CheckboxGroupEvents.valueChanged) {
            readout.set("value = ", joinValues(group.value).ifEmpty { "[]" })
        }
        basic.body.append(exampleBox(null, kitCol(group, readout.root)))
        body.append(basic.root)

        val preset = sectionBlock()
        texts.bind(preset.heading) { it.presetHeading }
        texts.bind(preset.hint) { it.presetHint }
        skills.required = true
        texts.bind(skills, { it.skillsLabel }) { skills.label = it }
        texts.bind(skills, { it.skillsError }) { skills.errorMessage = it }
        skills.value = arrayOf("css", "html")
        skills.append(
            createCheckbox {
                value = "css"
                textContent = "CSS"
            },
            createCheckbox {
                value = "html"
                textContent = "HTML"
            },
            createCheckbox {
                value = "kotlin"
                textContent = "Kotlin"
            },
        )
        val validateButton = createButton()
        texts.bind(validateButton) { it.validate }
        validateButton.also {
            clickEvent.addHandler {
                val ok = skills.validate()
                lastResult = { copy -> copy.validationPrefix to if (ok) copy.passed else copy.failed }
                showResult()
            }
        }
        skills.listen(CheckboxGroupEvents.valueChanged) {
            val values = joinValues(skills.value).ifEmpty { "[]" }
            lastResult = { copy -> copy.selectedPrefix to values }
            showResult()
        }
        preset.body.append(exampleBox(null, kitCol(skills, kitRow(validateButton, result.root))))
        body.append(preset.root)

        val features = sectionBlock()
        texts.bind(features.heading) { it.featuresHeading }
        texts.bind(features.hint) { it.featuresHint }
        val plain = createCheckboxGroup()
        texts.bind(plain, { it.label }) { plain.label = it }
        texts.bind(plain, { it.helper }) { plain.helperText = it }
        val languages = createCheckboxGroup().addTheme("vertical")
        texts.bind(languages, { it.label }) { languages.label = it }
        texts.bind(languages, { it.helper }) { languages.helperText = it }
        val tooltip = createTooltip().apply { setAttribute("slot", "tooltip") }
        texts.bind(tooltip, { it.tooltipText }) { tooltip.text = it }
        val item1 = createCheckbox { value = "1" }
        texts.bind(item1) { it.item1 }
        val item2 = createCheckbox { value = "2" }
        texts.bind(item2) { it.item2 }
        val item3 = createCheckbox { value = "3" }
        texts.bind(item3) { it.item3 }
        languages.append(tooltip, item1, item2, item3)
        features.body.append(exampleBox(null, kitCol(plain, languages)))
        body.append(features.root)

        val states = sectionBlock()
        texts.bind(states.heading) { it.statesHeading }
        val readonlyGroup = createCheckboxGroup().addTheme("vertical")
        texts.bind(readonlyGroup, { it.readonly }) { readonlyGroup.label = it }
        readonlyGroup.readonly = true
        readonlyGroup.value = arrayOf("a")
        val optionA = createCheckbox { value = "a" }
        texts.bind(optionA) { it.optionA }
        val optionB = createCheckbox { value = "b" }
        texts.bind(optionB) { it.optionB }
        readonlyGroup.append(optionA, optionB)
        val disabledGroup = createCheckboxGroup().addTheme("vertical")
        texts.bind(disabledGroup, { it.disabled }) { disabledGroup.label = it }
        disabledGroup.disabled = true
        val engineering = createCheckbox { value = "engineering" }
        texts.bind(engineering) { it.engineering }
        val humanResources = createCheckbox { value = "hr" }
        texts.bind(humanResources) { it.humanResources }
        val marketing = createCheckbox { value = "marketing" }
        texts.bind(marketing) { it.marketing }
        disabledGroup.append(engineering, humanResources, marketing)
        states.body.append(exampleBox(null, kitRow(readonlyGroup, disabledGroup)))
        body.append(states.root)

        applyTranslation(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = checkboxGroupEntry.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        showResult()
    }

    /** Writes the shared result readout from whatever the last event asked it to say. */
    private fun showResult() {
        val copy = texts.current
        lastResult?.invoke(copy)?.let { (prefix, value) -> result.set(prefix, value) }
    }

    companion object : CustomElementReference<CheckboxGroupPage>(CheckboxGroupPage::class.js) {
        val NAME: TagName<CheckboxGroupPage> = TagName("vaadin-demo-checkbox-group-page")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<vaadin-demo-checkbox-group-page>`. */
internal fun newCheckboxGroupPage(): CheckboxGroupPage {
    CheckboxGroupPage.define()
    return CheckboxGroupPage.NAME()
}
