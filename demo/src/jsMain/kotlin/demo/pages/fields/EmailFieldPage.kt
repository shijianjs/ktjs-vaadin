package demo.pages.fields

import io.github.shijianjs.ktjs.demo.page.Blocks.row as kitRow
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.page.Blocks.col as kitCol
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import demo.CatalogEntry
import demo.Catalog
import demo.Groups
import demo.bi
import demo.ui.PageFrame
import demo.ui.PageMeta
import demo.ui.Readout
import demo.ui.listen
import io.github.shijianjs.ktjs.vaadin.emailfield.EmailFieldElement
import io.github.shijianjs.ktjs.vaadin.emailfield.EmailFieldEvents
import io.github.shijianjs.ktjs.vaadin.emailfield.createEmailField
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.vaadin.icons.registerVaadinIcons
import io.github.shijianjs.ktjs.vaadin.tooltip.createTooltip
import web.html.HTMLParagraphElement

internal class EmailFieldPage : DemoPage() {
    private val texts = Binder(emailFieldTexts)

    private val frame = PageFrame(ENTRY)

    /** The field whose value the validity readout follows, so a switch can rewrite that line. */
    private val emailField: EmailFieldElement = createEmailField()

    private val validityReadout = Readout("")

    /** The validation example: the error message and the verdict the last validated event left. */
    private val restricted: EmailFieldElement = createEmailField()

    private val restrictedResult = Readout("")

    private var restrictedMessage: ((EmailFieldTexts) -> String)? = null

    private var restrictedPassed: Boolean? = null

    private var modes: ValueChangeSection? = null

    init {
        frame.mount(this)
        registerVaadinIcons()

        // --- Basic usage (email-field-basic.ts) -----------------------------------------------------
        val basic = SectionBlock.NAME { show() }
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        val validEmail =
            createEmailField {
                name = "email"
                value = "julia.scheider@email.com"
                clearButtonVisible = true
            }
        texts.bind(validEmail, { it.emailAddress }) { validEmail.label = it }
        texts.bind(validEmail, { it.invalidMessage }) { validEmail.errorMessage = it }
        val invalidEmail =
            createEmailField {
                name = "email"
                value = "This is not an email"
                clearButtonVisible = true
                invalid = true
            }
        texts.bind(invalidEmail, { it.emailAddress }) { invalidEmail.label = it }
        texts.bind(invalidEmail, { it.invalidMessage }) { invalidEmail.errorMessage = it }
        emailField.placeholder = "ada@example.com"
        emailField.required = true
        emailField.clearButtonVisible = true
        texts.bind(emailField, { it.emailAddress }) { emailField.label = it }
        texts.bind(emailField, { it.helper }) { emailField.helperText = it }
        texts.bind(emailField, { it.invalidMessage }) { emailField.errorMessage = it }
        emailField.dataset["test"] = "email-field"
        emailField.append(createIcon { icon = "lumo:user" }.apply { slot = "prefix" })
        fun syncValidity() {
            showValidity()
        }
        emailField.listen(EmailFieldEvents.input) { syncValidity() }
        emailField.listen(EmailFieldEvents.change) { syncValidity() }
        val validInvalidCard = exampleBox("", kitRow(validEmail, invalidEmail))
        texts.bind(validInvalidCard.firstElementChild as HTMLParagraphElement) { it.validInvalidCaption }
        val validityCard = exampleBox("", kitCol(emailField, validityReadout.root))
        texts.bind(validityCard.firstElementChild as HTMLParagraphElement) { it.validityCaption }
        basic.body.append(validInvalidCard, validityCard)
        body.append(basic.root)

        // --- Basic features (email-field-basic-features.ts) -----------------------------------------
        val features = SectionBlock.NAME { show() }
        texts.bind(features.heading) { it.featuresHeading }
        texts.bind(features.hint) { it.featuresHint }
        val featureField = createEmailField { clearButtonVisible = true }
        texts.bind(featureField, { it.label }) { featureField.label = it }
        texts.bind(featureField, { it.helperText }) { featureField.helperText = it }
        texts.bind(featureField, { it.placeholder }) { featureField.placeholder = it }
        val featureTooltip = createTooltip().apply { slot = "tooltip" }
        texts.bind(featureTooltip, { it.tooltipText }) { featureTooltip.text = it }
        featureField.append(
            featureTooltip,
            createIcon { icon = "vaadin:envelope" }.apply { slot = "prefix" },
        )
        features.body.append(exampleBox(null, featureField))
        body.append(features.root)

        // --- Validation (email-field-validation.ts) -------------------------------------------------
        val validation = SectionBlock.NAME { show() }
        texts.bind(validation.heading) { it.validationHeading }
        texts.bind(validation.hint) { it.validationHint }
        restricted.required = true
        restricted.pattern = EMAIL_EXAMPLE_PATTERN
        texts.bind(restricted, { it.emailAddress }) { restricted.label = it }
        texts.bind(restricted, { it.onlyExample }) { restricted.helperText = it }
        restricted.listen(EmailFieldEvents.validated) {
            val validity = validityOf(restricted)
            val message: ((EmailFieldTexts) -> String)? =
                when {
                    validity.valueMissing -> { copy -> copy.requiredMessage }
                    validity.patternMismatch -> { copy -> copy.patternMessage }
                    else -> null
                }
            restrictedMessage = message
            restrictedPassed = message == null
            showRestricted()
        }
        validation.body.append(exampleBox(null, kitCol(restricted, restrictedResult.root)))
        body.append(validation.root)

        // --- Read-only and disabled (email-field-readonly-and-disabled.ts) --------------------------
        val states = SectionBlock.NAME { show() }
        texts.bind(states.heading) { it.statesHeading }
        texts.bind(states.hint) { it.statesHint }
        val readonlyField = createEmailField {
            value = "example@example.com"
            readonly = true
        }
        texts.bind(readonlyField, { it.readonly }) { readonlyField.label = it }
        val disabledField = createEmailField()
        texts.bind(disabledField, { it.disabled }) { disabledField.label = it }
        states.body.append(exampleBox(null, kitRow(readonlyField, disabledField)))
        body.append(states.root)

        // --- Style variants (email-field-styles.ts) -------------------------------------------------
        val styles = SectionBlock.NAME { show() }
        texts.bind(styles.heading) { it.stylesHeading }
        texts.bind(styles.hint) { it.stylesHint }
        val styledField =
            createEmailField { value = "foo@bar.com" }.addTheme("align-right", "small", "helper-above-field")
        texts.bind(styledField, { it.label }) { styledField.label = it }
        texts.bind(styledField, { it.helperText }) { styledField.helperText = it }
        styles.body.append(exampleBox(null, styledField))
        body.append(styles.root)

        // --- Value change modes (email-field-value-change-mode.ts, Flow-only example) ---------------
        val modeField = createEmailField { placeholder = "ada@example.com" }
        texts.bind(modeField, { it.emailAddress }) { modeField.label = it }
        val section = valueChangeModesSection(modeField, EmailFieldEvents.valueChanged, EmailFieldEvents.change) {
            modeField.value
        }
        modes = section
        body.append(section.root)

        applyTranslation(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = ENTRY.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        showValidity()
        showRestricted()
        modes?.show(lang)
    }

    /** The live validity line of the basic example. */
    private fun showValidity() {
        validityReadout.set(texts.current.validityLine(emailField.checkValidity(), emailField.value))
    }

    /** The validation example's readout and error message, from the last validated event. */
    private fun showRestricted() {
        val copy = texts.current
        restricted.errorMessage = restrictedMessage?.invoke(copy) ?: ""
        val passed = restrictedPassed
        if (passed == null) {
            restrictedResult.set(copy.notChecked)
        } else {
            restrictedResult.set(copy.checkPrefix, if (passed) copy.passed else copy.failed)
        }
    }

    companion object : EmailFieldPageDefaultReference() {
        val ENTRY: CatalogEntry =
            CatalogEntry(
                id = "email-field",
                group = Groups.FIELDS,
                meta =
                    PageMeta(
                        title = bi("Email Field", "Email Field"),
                        summary =
                            bi(
                                "带邮箱格式语义的文本输入，继承 Text Field 的全部属性。",
                                "Text input with email semantics; inherits every Text Field property.",
                            ),
                        docsUrl = "${Catalog.DOCS_BASE}/email-field",
                        sourcePath = "pages/fields/EmailFieldPage.kt",
                    ),
                kitPage = { EmailFieldPage.NAME() },
            )

        private val english: EmailFieldTexts =
            EmailFieldTexts(
                basicHeading = "Basic usage",
                basicHint = "Email Field inherits every Text Field feature and validates the address against " +
                    "RFC 5322; an invalid value highlights the field and shows the error message.",
                emailAddress = "Email address",
                invalidMessage = "Enter a valid email address",
                helper = "Used for notifications",
                validInvalidCaption = "Valid and invalid addresses",
                validityCaption = "Validity while typing",
                validityLine = { valid, value -> "valid = $valid · value: ${value.ifEmpty { "(empty)" }}" },
                featuresHeading = "Basic features",
                featuresHint = "The label, helper text, placeholder, tooltip, clear button and prefix / suffix " +
                    "slots work exactly as in Text Field.",
                label = "Label",
                helperText = "Helper text",
                placeholder = "Placeholder",
                tooltipText = "Tooltip text",
                validationHeading = "Validation",
                validationHint = "Beyond the built-in email check, pattern can restrict the domain further. The " +
                    "example only accepts example.com addresses and distinguishes missing and malformed values " +
                    "in the validated event.",
                onlyExample = "Only example.com addresses allowed",
                notChecked = "Not checked yet",
                requiredMessage = "Field is required",
                patternMessage = "Enter a valid example.com email address",
                checkPrefix = "Check: ",
                passed = "passed",
                failed = "failed",
                statesHeading = "Read-only and disabled",
                statesHint = "readonly allows focus, selection and copying but no edits; disabled removes " +
                    "interaction entirely.",
                readonly = "Read-only",
                disabled = "Disabled",
                stylesHeading = "Style variants",
                stylesHint = "The theme variants match Text Field: align-right, small and helper-above-field can " +
                    "be combined.",
            )
        private val chinese: EmailFieldTexts =
            EmailFieldTexts(
                basicHeading = "基本用法",
                basicHint = "Email Field 继承 Text Field 的全部功能，并按 RFC 5322 校验邮箱格式；值无效时字段高亮并显示错误信息。",
                emailAddress = "邮箱地址",
                invalidMessage = "请输入合法的邮箱地址",
                helper = "用于接收通知",
                validInvalidCaption = "合法与非法邮箱",
                validityCaption = "实时校验结果",
                validityLine = { valid, value -> "valid = $valid · 值：${value.ifEmpty { "（空）" }}" },
                featuresHeading = "基本特性",
                featuresHint = "标签、辅助文本、占位符、提示、清空按钮与前后缀插槽与 Text Field 完全一致。",
                label = "标签",
                helperText = "辅助文本",
                placeholder = "占位符",
                tooltipText = "提示文本",
                validationHeading = "校验",
                validationHint = "内置的邮箱格式校验之外，pattern 可以进一步限制域名。示例只接受 example.com 地址，" +
                    "并在 validated 事件里区分“必填”与“格式不符”。",
                onlyExample = "只接受 example.com 域名",
                notChecked = "尚未校验",
                requiredMessage = "这一项必填",
                patternMessage = "请输入 example.com 域名下的合法邮箱地址",
                checkPrefix = "校验：",
                passed = "通过",
                failed = "未通过",
                statesHeading = "只读与禁用",
                statesHint = "readonly 可以聚焦、选择和复制但不能修改；disabled 完全移除交互。",
                readonly = "只读",
                disabled = "禁用",
                stylesHeading = "样式变体",
                stylesHint = "theme 变体与 Text Field 相同：align-right、small 与 helper-above-field 可以组合使用。",
            )
        /** Pattern of `email-field-validation.ts`: only addresses in the `example.com` domain. */
        private const val EMAIL_EXAMPLE_PATTERN = "^[a-zA-Z0-9_\\-+]+(?:\\.[a-zA-Z0-9_\\-+]+)*@example\\.com$"
        private val emailFieldTexts: Translations<EmailFieldTexts> = texts(
            Langs.EN to english,
            Langs.ZH_HANS to chinese,
        )
    }
}
