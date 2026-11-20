package demo.pages.fields

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
import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import web.components.CustomElementReference
import web.dom.TagName
import web.html.HTMLElement
import web.html.HtmlTagName
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.ui.PageFrame
import demo.ui.PageMeta
import demo.ui.Readout
import demo.ui.RecordedLog
import demo.ui.listen
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.vaadin.icons.registerVaadinIcons
import io.github.shijianjs.ktjs.vaadin.switch.SwitchEvents
import io.github.shijianjs.ktjs.vaadin.switch.createSwitch
import io.github.shijianjs.ktjs.vaadin.textfield.TextFieldElement
import io.github.shijianjs.ktjs.vaadin.textfield.TextFieldEvents
import io.github.shijianjs.ktjs.vaadin.textfield.createTextField
import io.github.shijianjs.ktjs.vaadin.tooltip.createTooltip
import web.dom.clickEvent
import web.events.addHandler

val textFieldEntry: CatalogEntry =
    CatalogEntry(
        id = "text-field",
        group = Groups.FIELDS,
        meta =
            PageMeta(
                title = bi("Text Field", "Text Field"),
                summary =
                    bi(
                        "单行文本输入：标签、占位符、前缀/后缀插槽、清空按钮、只读、禁用与校验。",
                        "Single-line text input: label, placeholder, prefix/suffix slots, clear button, " +
                            "read-only, disabled and validation.",
                    ),
                docsUrl = "$DOCS_BASE/text-field",
                sourcePath = "pages/fields/TextFieldPage.kt",
            ),
        kitPage = { newTextFieldPage() },
    )

/** Phone pattern of `text-field-validation.ts` (documents grouped digits with optional separators). */
private const val PHONE_PATTERN = "^[+]?\\(?[0-9]{3}\\)?-?[0-9]{3}-?[0-9]{4,6}$"

/** Every string this page shows, in one language. */
private class TextFieldTexts(
    val basicHeading: String,
    val basicHint: String,
    val streetAddress: String,
    val addressCaption: String,
    val valuePrefix: String,
    val empty: String,
    val readoutCaption: String,
    val logCaption: String,
    val name: String,
    val namePlaceholder: String,
    val nameHelper: String,
    val changeLine: (String) -> String,
    val valueChangedLine: (String) -> String,
    val featuresHeading: String,
    val featuresHint: String,
    val label: String,
    val helperText: String,
    val placeholder: String,
    val tooltipText: String,
    val validationHeading: String,
    val validationHint: String,
    val phone: String,
    val phoneHelper: String,
    val notValidated: String,
    val required: String,
    val tooShort: (Int) -> String,
    val tooLong: (Int) -> String,
    val phonePattern: String,
    val checkPrefix: String,
    val passed: String,
    val failed: String,
    val constraintsCaption: String,
    val code: String,
    val codeHelper: String,
    val codeError: String,
    val validate: String,
    val valid: String,
    val invalidWith: (Boolean) -> String,
    val reset: String,
    val resetPrefix: String,
    val manualCaption: String,
    val resultPrefix: String,
    val statesHeading: String,
    val statesHint: String,
    val readonly: String,
    val disabled: String,
    val value: String,
    val autoselectHeading: String,
    val autoselectHint: String,
    val autoselect: String,
    val inputModeHeading: String,
    val inputModeHint: String,
    val url: String,
    val email: String,
    val search: String,
    val stylesHeading: String,
    val stylesHint: String,
    val stylesCaption: String,
    val alignmentCaption: String,
    val typeHere: String,
    val typeHerePlaceholder: String,
)

private val english: TextFieldTexts =
    TextFieldTexts(
        basicHeading = "Basic usage",
        basicHint = "A text field supports a label, prefix / suffix slots and a built-in clear button; " +
            "input fires after every keystroke and change on commit (Enter or blur).",
        streetAddress = "Street address",
        addressCaption = "Street address: prefix icon and clear button",
        valuePrefix = "Value: ",
        empty = "(empty)",
        readoutCaption = "Read-out",
        logCaption = "Event log",
        name = "Name",
        namePlaceholder = "e.g. Ada Lovelace",
        nameHelper = "Shown below the input",
        changeLine = { "change · $it" },
        valueChangedLine = { "value-changed · $it" },
        featuresHeading = "Basic features",
        featuresHint = "The label names the field, the helper text adds guidance and the placeholder " +
            "shows an example; the tooltip slot explains the field, and the prefix / suffix slots hold " +
            "icons or units.",
        label = "Label",
        helperText = "Helper text",
        placeholder = "Placeholder",
        tooltipText = "Tooltip text",
        validationHeading = "Validation",
        validationHint = "required, minlength / maxlength, pattern and allowedCharPattern constrain the " +
            "input together. The example reads the native validity in the validated event and maps each " +
            "failure to its own error message.",
        phone = "Phone number",
        phoneHelper = "Format: +(123)456-7890",
        notValidated = "Not validated yet",
        required = "Field is required",
        tooShort = { "Minimum length is $it characters" },
        tooLong = { "Maximum length is $it characters" },
        phonePattern = "Invalid phone number format",
        checkPrefix = "validated · valid = ",
        passed = "true",
        failed = "false",
        constraintsCaption = "Constraints and error messages",
        code = "Invitation code",
        codeHelper = "3-6 lowercase letters",
        codeError = "Enter 3-6 lowercase letters",
        validate = "Validate",
        valid = "Valid",
        invalidWith = { "Failed (invalid=$it)" },
        reset = "Reset",
        resetPrefix = "Reset, value: ",
        manualCaption = "Manual validation (validate())",
        resultPrefix = "Result: ",
        statesHeading = "Read-only and disabled",
        statesHint = "readonly allows focus, selection and copying but no edits; disabled removes " +
            "interaction entirely.",
        readonly = "Read-only",
        disabled = "Disabled",
        value = "Value",
        autoselectHeading = "Autoselect",
        autoselectHint = "With autoselect enabled, focusing the field with either the pointer or the " +
            "keyboard selects the whole value so it can be replaced; with it off, a click places the caret " +
            "where you clicked.",
        autoselect = "autoselect",
        inputModeHeading = "Input mode",
        inputModeHint = "inputmode hints the browser which virtual keyboard to show on mobile devices; " +
            "desktop browsers show no visible difference.",
        url = "URL",
        email = "Email",
        search = "Search",
        stylesHeading = "Style variants",
        stylesHint = "The theme property takes space-separated variants: align-right right-aligns the " +
            "text (useful for numbers in vertical groups), small renders a compact field and " +
            "helper-above-field moves the helper text above the field.",
        stylesCaption = "align-right + small + helper-above-field",
        alignmentCaption = "Text alignment",
        typeHere = "Type here",
        typeHerePlaceholder = "Type to watch the sync result",
    )

private val chinese: TextFieldTexts =
    TextFieldTexts(
        basicHeading = "基本用法",
        basicHint = "文本输入支持标签、前缀 / 后缀插槽和内置清空按钮；input 在每次按键后触发，change 在" +
            "提交（回车或失焦）后触发。",
        streetAddress = "街道地址",
        addressCaption = "街道地址：前缀图标与清空按钮",
        valuePrefix = "当前值：",
        empty = "（空）",
        readoutCaption = "读值",
        logCaption = "事件流",
        name = "姓名",
        namePlaceholder = "例如 Ada Lovelace",
        nameHelper = "显示在输入框下方",
        changeLine = { "change · $it" },
        valueChangedLine = { "value-changed · $it" },
        featuresHeading = "基本特性",
        featuresHint = "标签说明字段用途，辅助文本补充说明，占位符给出输入示例；tooltip 插槽在悬停与" +
            "键盘聚焦时显示提示，前后缀插槽放置图标或单位。",
        label = "标签",
        helperText = "辅助文本",
        placeholder = "占位符",
        tooltipText = "提示文本",
        validationHeading = "校验",
        validationHint = "required、minlength / maxlength、pattern 与 allowedCharPattern 一起约束输入。" +
            "示例在 validated 事件里读取原生 validity，为每种失败给出对应的错误文案。",
        phone = "电话号码",
        phoneHelper = "格式：+(123)456-7890",
        notValidated = "尚未校验",
        required = "这一项必填",
        tooShort = { "最短 $it 个字符" },
        tooLong = { "最长 $it 个字符" },
        phonePattern = "电话号码格式不正确",
        checkPrefix = "校验：",
        passed = "通过",
        failed = "未通过",
        constraintsCaption = "约束与错误文案",
        code = "邀请码",
        codeHelper = "3-6 位小写字母",
        codeError = "请输入 3-6 位小写字母",
        validate = "校验",
        valid = "通过",
        invalidWith = { "失败（invalid=$it）" },
        reset = "重置",
        resetPrefix = "已重置，值：",
        manualCaption = "手动校验（validate）",
        resultPrefix = "校验结果：",
        statesHeading = "只读与禁用",
        statesHint = "readonly 可以聚焦、选择和复制但不能修改；disabled 完全移除交互。",
        readonly = "只读",
        disabled = "禁用",
        value = "值",
        autoselectHeading = "自动全选",
        autoselectHint = "autoselect 打开时，用鼠标或键盘聚焦都会选中全部内容，便于直接替换；关闭时" +
            "鼠标点击会把光标放在点击处。",
        autoselect = "autoselect",
        inputModeHeading = "输入模式",
        inputModeHint = "inputmode 是给移动端浏览器的提示，决定聚焦时弹出哪种虚拟键盘；桌面浏览器没有" +
            "可见效果。",
        url = "网址",
        email = "邮箱",
        search = "搜索",
        stylesHeading = "样式变体",
        stylesHint = "theme 属性接受空格分隔的变体：align-right 右对齐文本（适合纵向排列的数值），" +
            "small 更紧凑，helper-above-field 把辅助文本放在字段上方。",
        stylesCaption = "align-right + small + helper-above-field",
        alignmentCaption = "文本对齐",
        typeHere = "输入内容",
        typeHerePlaceholder = "边输入边观察同步结果",
    )

private val textFieldTexts: Translations<TextFieldTexts> = texts(
    Langs.EN to english,
    Langs.ZH_HANS to chinese,
)

internal class TextFieldPage : DemoPage() {
    private val texts = Binder(textFieldTexts)

    private val frame = PageFrame(textFieldEntry)

    private val readout = Readout("")

    private val log = RecordedLog<String>(6)

    private val nameField = createTextField()

    private val phone = createTextField()

    private val phoneResult = Readout("")

    private var phoneKind: String? = null

    private val codeField = createTextField()

    private val result = Readout("")

    private var manualStatus: String? = null

    private var modes: ValueChangeSection? = null

    init {
        frame.mount(this)
        registerVaadinIcons()

        fun field(copy: (TextFieldTexts) -> String): TextFieldElement =
            createTextField().also { node -> texts.bind(node, copy) { node.label = it } }

        fun caption(copy: (TextFieldTexts) -> String): HTMLElement =
            HtmlTagName.p().apply {
                style.setProperty("margin", "0 0 0.75rem")
                style.setProperty("font-size", "0.8125rem")
                style.setProperty("color", "var(--lumo-secondary-text-color)")
                texts.bind(this, copy) { textContent = it }
            }

        // --- Basic usage (text-field-basic.ts) ------------------------------------------------------
        val basic = sectionBlock()
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        val address = field { it.streetAddress }
        address.value = "Ruukinkatu 2"
        address.clearButtonVisible = true
        address.append(createIcon { icon = "vaadin:map-map-marker" }.apply { slot = "prefix" })
        texts.bind(nameField, { it.name }) { nameField.label = it }
        texts.bind(nameField, { it.namePlaceholder }) { nameField.placeholder = it }
        texts.bind(nameField, { it.nameHelper }) { nameField.helperText = it }
        nameField.dataset["test"] = "text-field-basic"
        nameField.listen(TextFieldEvents.input) { showValue() }
        nameField.listen(TextFieldEvents.change) {
            log.add("change:${nameField.value}") { token ->
                val value = token.substringAfter(':')
                texts.current.changeLine(value)
            }
        }
        nameField.listen(TextFieldEvents.valueChanged) {
            log.add("value:${nameField.value}") { token ->
                val value = token.substringAfter(':')
                texts.current.valueChangedLine(value)
            }
        }
        basic.body.append(
            exampleBox(null, caption { it.addressCaption }, address),
            exampleBox(null, caption { it.readoutCaption }, kitCol(nameField, readout.root)),
            exampleBox(null, caption { it.logCaption }, log.root),
        )
        body.append(basic.root)

        // --- Basic features (text-field-basic-features.ts) ------------------------------------------
        val features = sectionBlock()
        texts.bind(features.heading) { it.featuresHeading }
        texts.bind(features.hint) { it.featuresHint }
        val featureField = createTextField()
        texts.bind(featureField, { it.label }) { featureField.label = it }
        texts.bind(featureField, { it.helperText }) { featureField.helperText = it }
        texts.bind(featureField, { it.placeholder }) { featureField.placeholder = it }
        featureField.clearButtonVisible = true
        val featureTooltip = createTooltip().apply { slot = "tooltip" }
        texts.bind(featureTooltip, { it.tooltipText }) { featureTooltip.text = it }
        featureField.append(
            featureTooltip,
            createIcon { icon = "vaadin:vaadin-h" }.apply { slot = "prefix" },
            HtmlTagName.span { textContent = ":)" }.apply { slot = "suffix" },
        )
        features.body.append(exampleBox(null, featureField))
        body.append(features.root)

        // --- Validation (text-field-validation.ts) --------------------------------------------------
        val validation = sectionBlock()
        texts.bind(validation.heading) { it.validationHeading }
        texts.bind(validation.hint) { it.validationHint }
        texts.bind(phone, { it.phone }) { phone.label = it }
        texts.bind(phone, { it.phoneHelper }) { phone.helperText = it }
        phone.required = true
        phone.minlength = 5.0
        phone.maxlength = 18.0
        phone.pattern = PHONE_PATTERN
        phone.allowedCharPattern = "[0-9()+-]"
        phone.listen(TextFieldEvents.validated) {
            val validity = validityOf(phone)
            phoneKind =
                when {
                    validity.valueMissing -> "required"
                    validity.tooShort -> "tooShort"
                    validity.tooLong -> "tooLong"
                    validity.patternMismatch -> "pattern"
                    else -> "ok"
                }
            showPhone()
        }
        texts.bind(codeField, { it.code }) { codeField.label = it }
        texts.bind(codeField, { it.codeHelper }) { codeField.helperText = it }
        codeField.required = true
        codeField.pattern = "[a-z]{3,6}"
        texts.bind(codeField, { it.codeError }) { codeField.errorMessage = it }
        codeField.dataset["test"] = "text-field-validated"
        val validateButton = createButton()
        texts.bind(validateButton) { it.validate }
        validateButton.clickEvent.addHandler {
            manualStatus = if (codeField.validate()) "valid" else "invalid"
            showManual()
        }
        val resetButton = createButton().addTheme("tertiary")
        texts.bind(resetButton) { it.reset }
        resetButton.clickEvent.addHandler {
            codeField.clear()
            codeField.invalid = false
            manualStatus = "reset"
            showManual()
        }
        validation.body.append(
            exampleBox(null, caption { it.constraintsCaption }, kitCol(phone, phoneResult.root)),
            exampleBox(
                null,
                caption { it.manualCaption },
                kitCol(codeField, kitRow(validateButton, resetButton), result.root),
            ),
        )
        body.append(validation.root)

        // --- Read-only and disabled (text-field-readonly-and-disabled.ts) ---------------------------
        val states = sectionBlock()
        texts.bind(states.heading) { it.statesHeading }
        texts.bind(states.hint) { it.statesHint }
        val readonlyField = field { it.readonly }
        texts.bind(readonlyField, { it.value }) { readonlyField.value = it }
        readonlyField.readonly = true
        states.body.append(exampleBox(null, kitRow(readonlyField, field { it.disabled })))
        body.append(states.root)

        // --- Autoselect -----------------------------------------------------------------------------
        val autoselectSection = sectionBlock()
        texts.bind(autoselectSection.heading) { it.autoselectHeading }
        texts.bind(autoselectSection.hint) { it.autoselectHint }
        val autoselectField = field { it.value }
        autoselectField.value = "Ruukinkatu 2"
        autoselectField.autoselect = true
        val autoselectSwitch = createSwitch { checked = true }
        texts.bind(autoselectSwitch, { it.autoselect }) { autoselectSwitch.label = it }
        autoselectSwitch.listen(SwitchEvents.checkedChanged) {
            autoselectField.autoselect = autoselectSwitch.checked
        }
        autoselectSection.body.append(exampleBox(null, kitCol(autoselectSwitch, autoselectField)))
        body.append(autoselectSection.root)

        // --- Input mode -----------------------------------------------------------------------------
        val inputMode = sectionBlock()
        texts.bind(inputMode.heading) { it.inputModeHeading }
        texts.bind(inputMode.hint) { it.inputModeHint }
        inputMode.body.append(
            exampleBox(
                null,
                kitRow(
                    createTextField {
                        inputModeProperty = "url"
                        helperText = "inputmode=\"url\""
                    }.also { node -> texts.bind(node, { it.url }) { node.label = it } },
                    createTextField {
                        inputModeProperty = "email"
                        helperText = "inputmode=\"email\""
                    }.also { node -> texts.bind(node, { it.email }) { node.label = it } },
                    createTextField {
                        inputModeProperty = "search"
                        helperText = "inputmode=\"search\""
                    }.also { node -> texts.bind(node, { it.search }) { node.label = it } },
                ),
            ),
        )
        body.append(inputMode.root)

        // --- Style variants (text-field-styles.ts) --------------------------------------------------
        val styles = sectionBlock()
        texts.bind(styles.heading) { it.stylesHeading }
        texts.bind(styles.hint) { it.stylesHint }
        val styledField = field { it.label }
        texts.bind(styledField, { it.helperText }) { styledField.helperText = it }
        texts.bind(styledField, { it.value }) { styledField.value = it }
        styledField.addTheme("align-right", "small", "helper-above-field")
        styles.body.append(
            exampleBox(null, caption { it.stylesCaption }, styledField),
            exampleBox(
                null,
                caption { it.alignmentCaption },
                kitRow(
                    createTextField { label = "align-left"; value = "1234" }.addTheme("align-left"),
                    createTextField { label = "align-center"; value = "1234" }.addTheme("align-center"),
                    createTextField { label = "align-right"; value = "1234" }.addTheme("align-right"),
                ),
            ),
        )
        body.append(styles.root)

        // --- Value change modes (text-field-value-change-mode.ts, Flow-only example) ----------------
        val modeField = field { it.typeHere }
        texts.bind(modeField, { it.typeHerePlaceholder }) { modeField.placeholder = it }
        modes =
            valueChangeModesSection(modeField, TextFieldEvents.valueChanged, TextFieldEvents.change) { modeField.value }
        body.append(modes!!.root)

        applyTranslation(Langs.EN)
    }

    private fun showValue() {
        readout.set(texts.current.valuePrefix, nameField.value.ifEmpty { texts.current.empty })
    }

    private fun showPhone() {
        val copy = texts.current
        val message =
            when (phoneKind) {
                "required" -> copy.required
                "tooShort" -> copy.tooShort(phone.minlength.toInt())
                "tooLong" -> copy.tooLong(phone.maxlength.toInt())
                "pattern" -> copy.phonePattern
                else -> ""
            }
        phone.errorMessage = message
        phoneResult.set(
            copy.checkPrefix,
            if (phoneKind == null) copy.notValidated else if (message.isEmpty()) copy.passed else copy.failed,
        )
    }

    private fun showManual() {
        val copy = texts.current
        when (manualStatus) {
            null -> result.set(copy.notValidated)
            "valid" -> result.set(copy.resultPrefix, copy.valid)
            "reset" -> result.set(copy.resetPrefix, copy.empty)
            else -> result.set(copy.resultPrefix, copy.invalidWith(codeField.invalid))
        }
    }

    override fun applyTranslation(lang: Lang) {
        val meta = textFieldEntry.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        showValue()
        showPhone()
        showManual()
        modes?.show(lang)
    }

    companion object : CustomElementReference<TextFieldPage>(TextFieldPage::class.js) {
        val NAME: TagName<TextFieldPage> = TagName("vaadin-demo-text-field-page")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<vaadin-demo-text-field-page>`. */
internal fun newTextFieldPage(): TextFieldPage {
    TextFieldPage.define()
    return TextFieldPage.NAME()
}
