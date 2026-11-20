package demo.pages.fields

import io.github.shijianjs.ktjs.demo.page.Blocks.col as kitCol
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.page.Blocks.row as kitRow
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import web.html.HTMLElement
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
import demo.ui.Readout
import demo.ui.listen
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.vaadin.icons.registerVaadinIcons
import io.github.shijianjs.ktjs.vaadin.passwordfield.PasswordFieldElement
import io.github.shijianjs.ktjs.vaadin.passwordfield.PasswordFieldEvents
import io.github.shijianjs.ktjs.vaadin.passwordfield.createPasswordField
import io.github.shijianjs.ktjs.vaadin.tooltip.createTooltip
import web.dom.clickEvent
import web.events.addHandler

val passwordFieldEntry: CatalogEntry =
    CatalogEntry(
        id = "password-field",
        group = Groups.FIELDS,
        meta =
            PageMeta(
                title = bi("Password Field", "Password Field"),
                summary =
                    bi(
                        "密码输入：自带明文切换按钮，可隐藏该按钮。",
                        "Password input with a built-in reveal toggle that can be hidden.",
                    ),
                docsUrl = "$DOCS_BASE/password-field",
                sourcePath = "pages/fields/PasswordFieldPage.kt",
            ),
        kitPage = { PasswordFieldPage.NAME() },
    )

private val english: PasswordFieldTexts =
    PasswordFieldTexts(
        basicHeading = "Basic usage",
        basicHint = "The value is masked by default and the eye button temporarily reveals it; the " +
            "read-only passwordVisible property reflects the current state.",
        password = "Password",
        atLeastEight = "At least 8 characters",
        visible = { visible, length -> "passwordVisible = $visible · length $length" },
        revealCaption = "Reveal toggle and read-out",
        revealHeading = "Reveal button",
        revealHint = "The eye button lets users confirm what they typed, which helps on mobile; " +
            "revealButtonHidden removes it for one-time codes and other values that should not be revealed.",
        revealShown = "Reveal button shown",
        revealHidden = "Reveal button hidden",
        featuresHeading = "Basic features",
        featuresHint = "The label, helper text, placeholder, tooltip, clear button and prefix / suffix " +
            "slots work exactly as in Text Field.",
        label = "Label",
        helperText = "Helper text",
        placeholder = "Placeholder",
        tooltipText = "Tooltip text",
        validationHeading = "Validation",
        validationHint = "required, minlength / maxlength and pattern constrain the password together; " +
            "the example maps each failure to its own message in the validated event.",
        validationHelper = "6 to 12 characters. Only letters A-Z and numbers supported.",
        notValidated = "Not validated yet",
        required = "Field is required",
        tooShort = { "Minimum length is $it characters" },
        tooLong = { "Maximum length is $it characters" },
        pattern = "Only letters A-Z and numbers are allowed",
        checkPrefix = "Check: ",
        passed = "passed",
        failed = "failed",
        statesHeading = "Read-only and disabled",
        statesHint = "readonly allows focus and copying but no edits; disabled removes interaction " +
            "entirely. Both keep the masked rendering.",
        readonly = "Read-only",
        disabled = "Disabled",
        stylesHeading = "Style variants",
        stylesHint = "The theme variants match Text Field: align-right, small and helper-above-field " +
            "can be combined.",
        practicesHeading = "Best practices",
        practicesHint = "State the password requirements in the helper text so users do not have to " +
            "guess; the strength can go into the helper text and a suffix icon as well. The example derives " +
            "weak / moderate / strong from the length only; real strength estimation belongs in the " +
            "application.",
        requirementsHelper = "A password must be at least 8 characters. It has to have at least one " +
            "letter and one digit.",
        invalidPassword = "Not a valid password",
        requirementsCaption = "Requirements in the helper text",
        strengthCaption = "Showing password strength",
        strengthPrefix = "Password strength: ",
        weak = "weak",
        moderate = "moderate",
        strong = "strong",
    )

private val chinese: PasswordFieldTexts =
    PasswordFieldTexts(
        basicHeading = "基本用法",
        basicHint = "密码默认以掩码显示，眼睛按钮可以临时切换为明文；passwordVisible 只读反映当前状态。",
        password = "密码",
        atLeastEight = "至少 8 位",
        visible = { visible, length -> "passwordVisible = $visible · 长度 $length" },
        revealCaption = "明文切换与读值",
        revealHeading = "明文切换按钮",
        revealHint = "眼睛按钮让用户在输入后确认内容，移动端尤其有用；revealButtonHidden 可以去掉这个" +
            "按钮，适合一次性口令等不希望暴露内容的场景。",
        revealShown = "显示切换按钮",
        revealHidden = "隐藏切换按钮",
        featuresHeading = "基本特性",
        featuresHint = "标签、辅助文本、占位符、提示、清空按钮与前后缀插槽与 Text Field 完全一致。",
        label = "标签",
        helperText = "辅助文本",
        placeholder = "占位符",
        tooltipText = "提示文本",
        validationHeading = "校验",
        validationHint = "required、minlength / maxlength 与 pattern 一起约束密码；示例在 validated " +
            "事件里把每种失败映射成具体提示。",
        validationHelper = "6 到 12 位，只支持字母 A-Z 和数字",
        notValidated = "尚未校验",
        required = "这一项必填",
        tooShort = { "最短 $it 个字符" },
        tooLong = { "最长 $it 个字符" },
        pattern = "只允许字母 A-Z 和数字",
        checkPrefix = "校验：",
        passed = "通过",
        failed = "未通过",
        statesHeading = "只读与禁用",
        statesHint = "readonly 可以聚焦和复制但不能修改；disabled 完全移除交互，两者都保留掩码显示。",
        readonly = "只读",
        disabled = "禁用",
        stylesHeading = "样式变体",
        stylesHint = "theme 变体与 Text Field 相同：align-right、small 与 helper-above-field 可以组合使用。",
        practicesHeading = "最佳实践",
        practicesHint = "在辅助文本里直接说明密码要求，用户不必猜；密码强度也可以显示在辅助文本和后缀" +
            "图标里。示例只按长度分为弱 / 中 / 强，真实强度评估应在应用侧完成。",
        requirementsHelper = "密码至少 8 位，且必须包含至少一个字母和一个数字。",
        invalidPassword = "密码不符合要求",
        requirementsCaption = "在辅助文本中说明要求",
        strengthCaption = "显示密码强度",
        strengthPrefix = "密码强度：",
        weak = "弱",
        moderate = "中",
        strong = "强",
    )

internal class PasswordFieldPage : DemoPage() {
    private val texts = Binder(passwordFieldTexts)

    private val frame = PageFrame(passwordFieldEntry)

    private val passwordField = createPasswordField()

    private val readout = Readout("")

    private val password = createPasswordField()

    private val passwordResult = Readout("")

    /** The kind of the last validation, so a language switch can write it again. */
    private var lastCheck: String? = null

    private val requirements = createPasswordField()

    private val advanced = createPasswordField()

    private val strengthText = HtmlTagName.span()

    private val strengthCheck = createIcon { icon = "vaadin:check" }

    private var modes: ValueChangeSection? = null

    init {
        frame.mount(this)
        registerVaadinIcons()

        fun field(copy: (PasswordFieldTexts) -> String): PasswordFieldElement =
            createPasswordField().also { node -> texts.bind(node, copy) { node.label = it } }

        // --- Basic usage (password-field-basic.ts) --------------------------------------------------
        val basic = SectionBlock.NAME { show() }
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        val docsField = field { it.password }
        docsField.value = "Ex@mplePassw0rd"
        texts.bind(passwordField, { it.password }) { passwordField.label = it }
        texts.bind(passwordField, { it.atLeastEight }) { passwordField.helperText = it }
        passwordField.clearButtonVisible = false
        passwordField.dataset["test"] = "password-field"
        passwordField.listen(PasswordFieldEvents.input) { showVisibility() }
        // `passwordVisible` is read-only and has no change event; the reveal button's click is composed,
        // so a click listener on the host is enough to refresh the read-out.
        passwordField.clickEvent.addHandler { showVisibility() }
        basic.body.append(
            exampleBox(null, docsField),
            exampleBox(
                null,
                newCaption { it.revealCaption },
                kitCol(passwordField, readout.root),
            ),
        )
        body.append(basic.root)

        // --- Reveal button (password-field-reveal-button-hidden.ts) --------------------------------
        val reveal = SectionBlock.NAME { show() }
        texts.bind(reveal.heading) { it.revealHeading }
        texts.bind(reveal.hint) { it.revealHint }
        val shown = field { it.revealShown }
        shown.value = "Ex@mplePassw0rd"
        val hidden = field { it.revealHidden }
        hidden.value = "Ex@mplePassw0rd"
        hidden.revealButtonHidden = true
        reveal.body.append(exampleBox(null, kitRow(shown, hidden)))
        body.append(reveal.root)

        // --- Basic features (password-field-basic-features.ts) --------------------------------------
        val features = SectionBlock.NAME { show() }
        texts.bind(features.heading) { it.featuresHeading }
        texts.bind(features.hint) { it.featuresHint }
        val featureField = createPasswordField()
        texts.bind(featureField, { it.label }) { featureField.label = it }
        texts.bind(featureField, { it.helperText }) { featureField.helperText = it }
        texts.bind(featureField, { it.placeholder }) { featureField.placeholder = it }
        featureField.clearButtonVisible = true
        val tooltip = createTooltip().apply { slot = "tooltip" }
        texts.bind(tooltip, { it.tooltipText }) { tooltip.text = it }
        featureField.append(tooltip, createIcon { icon = "vaadin:lock" }.apply { slot = "prefix" })
        features.body.append(exampleBox(null, featureField))
        body.append(features.root)

        // --- Validation (password-field-validation.ts) ----------------------------------------------
        val validation = SectionBlock.NAME { show() }
        texts.bind(validation.heading) { it.validationHeading }
        texts.bind(validation.hint) { it.validationHint }
        texts.bind(password, { it.password }) { password.label = it }
        texts.bind(password, { it.validationHelper }) { password.helperText = it }
        password.required = true
        password.minlength = 6.0
        password.maxlength = 12.0
        password.pattern = PASSWORD_LETTERS_AND_DIGITS
        password.listen(PasswordFieldEvents.validated) {
            val validity = validityOf(password)
            lastCheck =
                when {
                    validity.valueMissing -> "required"
                    validity.tooShort -> "tooShort"
                    validity.tooLong -> "tooLong"
                    validity.patternMismatch -> "pattern"
                    else -> "ok"
                }
            showValidation()
        }
        validation.body.append(exampleBox(null, kitCol(password, passwordResult.root)))
        body.append(validation.root)

        // --- Read-only and disabled (password-field-readonly-and-disabled.ts) -----------------------
        val states = SectionBlock.NAME { show() }
        texts.bind(states.heading) { it.statesHeading }
        texts.bind(states.hint) { it.statesHint }
        val readonlyField = field { it.readonly }
        readonlyField.value = "Ex@mplePassw0rd"
        readonlyField.readonly = true
        val disabledField = field { it.disabled }
        states.body.append(exampleBox(null, kitRow(readonlyField, disabledField)))
        body.append(states.root)

        // --- Style variants (password-field-styles.ts) ----------------------------------------------
        val styles = SectionBlock.NAME { show() }
        texts.bind(styles.heading) { it.stylesHeading }
        texts.bind(styles.hint) { it.stylesHint }
        val styledField = field { it.label }
        texts.bind(styledField, { it.helperText }) { styledField.helperText = it }
        styledField.value = "Ex@mplePassw0rd"
        styledField.addTheme("align-right", "small", "helper-above-field")
        styles.body.append(exampleBox(null, styledField))
        body.append(styles.root)

        // --- Best practices (password-field-helper.ts, password-field-advanced-helper.ts) -----------
        val practices = SectionBlock.NAME { show() }
        texts.bind(practices.heading) { it.practicesHeading }
        texts.bind(practices.hint) { it.practicesHint }
        texts.bind(requirements, { it.password }) { requirements.label = it }
        texts.bind(requirements, { it.requirementsHelper }) { requirements.helperText = it }
        requirements.pattern = PASSWORD_HELPER_PATTERN
        texts.bind(requirements, { it.invalidPassword }) { requirements.errorMessage = it }
        texts.bind(advanced, { it.password }) { advanced.label = it }
        advanced.pattern = PASSWORD_HELPER_PATTERN
        texts.bind(advanced, { it.invalidPassword }) { advanced.errorMessage = it }
        advanced.style.setProperty("width", "14em")
        strengthCheck.slot = "suffix"
        strengthCheck.toggleAttribute("hidden", true)
        val helperSlot = HtmlTagName.div()
        helperSlot.slot = "helper"
        val strengthPrefix = HtmlTagName.span()
        texts.bind(strengthPrefix, { it.strengthPrefix }) { strengthPrefix.textContent = it }
        helperSlot.append(strengthPrefix, strengthText)
        advanced.append(strengthCheck, helperSlot)
        advanced.listen(PasswordFieldEvents.valueChanged) { showStrength() }
        practices.body.append(
            exampleBox(null, newCaption { it.requirementsCaption }, requirements),
            exampleBox(null, newCaption { it.strengthCaption }, advanced),
        )
        body.append(practices.root)

        // --- Value change modes (password-field-value-change-mode.ts, Flow-only example) ------------
        val modeField = field { it.password }
        modes =
            valueChangeModesSection(
                modeField,
                PasswordFieldEvents.valueChanged,
                PasswordFieldEvents.change,
            ) { modeField.value }
        body.append(modes!!.root)

        applyTranslation(Langs.EN)
    }

    /** A paragraph above an example, used where the caption is copy rather than a CSS snippet. */
    private fun newCaption(copy: (PasswordFieldTexts) -> String): HTMLElement =
        HtmlTagName.p().apply {
            style.setProperty("margin", "0 0 0.75rem")
            style.setProperty("font-size", "0.8125rem")
            style.setProperty("color", "var(--lumo-secondary-text-color)")
            texts.bind(this, copy) { textContent = it }
        }

    private fun showVisibility() {
        readout.set(texts.current.visible(passwordField.passwordVisible, passwordField.value.length))
    }

    private fun showValidation() {
        val copy = texts.current
        val message =
            when (lastCheck) {
                "required" -> copy.required
                "tooShort" -> copy.tooShort(password.minlength.toInt())
                "tooLong" -> copy.tooLong(password.maxlength.toInt())
                "pattern" -> copy.pattern
                else -> ""
            }
        password.errorMessage = message
        passwordResult.set(
            copy.checkPrefix,
            if (lastCheck == null) copy.notValidated else if (message.isEmpty()) copy.passed else copy.failed,
        )
    }

    private fun showStrength() {
        val copy = texts.current
        val length = advanced.value.length
        val strong = length > 9
        val moderate = length in 6..9
        strengthText.textContent =
            when {
                strong -> copy.strong
                moderate -> copy.moderate
                else -> copy.weak
            }
        strengthText.style.setProperty(
            "color",
            when {
                strong -> "var(--lumo-success-text-color)"
                moderate -> "var(--lumo-warning-text-color)"
                else -> "var(--lumo-error-text-color)"
            },
        )
        strengthCheck.toggleAttribute("hidden", !strong)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = passwordFieldEntry.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        showVisibility()
        showValidation()
        showStrength()
        modes?.show(lang)
    }

    companion object : CustomElementReferenceAdapter<PasswordFieldPage>(PasswordFieldPage::class.js, "vaadin-demo-password-field-page") {
        /** Pattern of `password-field-helper.ts`: at least eight characters, one letter and one digit. */
        private const val PASSWORD_HELPER_PATTERN = "^(?=.*[0-9])(?=.*[a-zA-Z]).{8}.*$"
        /** Pattern of `password-field-validation.ts`: letters A-Z and digits only. */
        private const val PASSWORD_LETTERS_AND_DIGITS = "^[A-Za-z0-9]+$"
        private val passwordFieldTexts: Translations<PasswordFieldTexts> = texts(
            Langs.EN to english,
            Langs.ZH_HANS to chinese,
        )
    }
}
