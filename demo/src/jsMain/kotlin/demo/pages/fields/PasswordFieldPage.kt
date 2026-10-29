package demo.pages.fields

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.pages.data.message
import demo.pages.layout.passwordField
import demo.tr
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.Readout
import demo.ui.column
import demo.ui.div
import demo.ui.example
import demo.ui.listen
import demo.ui.page
import demo.ui.row
import demo.ui.section
import demo.ui.setHidden
import demo.ui.span
import demo.ui.withTheme
import vaadin.icon.createIcon
import vaadin.icons.registerVaadinIcons
import vaadin.passwordfield.PasswordFieldEvents
import vaadin.passwordfield.createPasswordField
import vaadin.tooltip.createTooltip
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
        builder = ::buildPasswordFieldPage,
    )

/** Pattern of `password-field-helper.ts`: at least eight characters, one letter and one digit. */
private const val PASSWORD_HELPER_PATTERN = "^(?=.*[0-9])(?=.*[a-zA-Z]).{8}.*$"

/** Pattern of `password-field-validation.ts`: letters A-Z and digits only. */
private const val PASSWORD_LETTERS_AND_DIGITS = "^[A-Za-z0-9]+$"

private fun buildPasswordFieldPage(meta: PageMeta): PageView {
    val view = page(meta)
    registerVaadinIcons()

    // --- Basic usage (password-field-basic.ts) --------------------------------------------------
    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "密码默认以掩码显示，眼睛按钮可以临时切换为明文；passwordVisible 只读反映当前状态。",
                "The value is masked by default and the eye button temporarily reveals it; the read-only " +
                    "passwordVisible property reflects the current state.",
            ),
        )
    val docsField =
        createPasswordField {
            label = tr("密码", "Password")
            value = "Ex@mplePassw0rd"
        }
    val passwordField =
        createPasswordField {
            label = tr("密码", "Password")
            helperText = tr("至少 8 位", "At least 8 characters")
            clearButtonVisible = false
        }
    passwordField.dataset["test"] = "password-field"
    val readout = Readout("passwordVisible = false")
    fun syncVisibility() {
        readout.set(
            tr(
                "passwordVisible = ${passwordField.passwordVisible} · 长度 ${passwordField.value.length}",
                "passwordVisible = ${passwordField.passwordVisible} · length ${passwordField.value.length}",
            ),
        )
    }
    passwordField.listen(PasswordFieldEvents.input) { syncVisibility() }
    // `passwordVisible` is read-only and has no change event; the reveal button's click is composed,
    // so a click listener on the host is enough to refresh the read-out.
    passwordField.clickEvent.addHandler { syncVisibility() }
    basic.body.append(
        example(null, docsField),
        example(tr("明文切换与读值", "Reveal toggle and read-out"), column(passwordField, readout.root)),
    )
    view.body.append(basic.root)

    // --- Reveal button (password-field-reveal-button-hidden.ts) --------------------------------
    val reveal =
        section(
            tr("明文切换按钮", "Reveal button"),
            tr(
                "眼睛按钮让用户在输入后确认内容，移动端尤其有用；revealButtonHidden 可以去掉这个按钮，" +
                    "适合一次性口令等不希望暴露内容的场景。",
                "The eye button lets users confirm what they typed, which helps on mobile; " +
                    "revealButtonHidden removes it for one-time codes and other values that should not be " +
                    "revealed.",
            ),
        )
    reveal.body.append(
        example(
            null,
            row(
                createPasswordField {
                    label = tr("显示切换按钮", "Reveal button shown")
                    value = "Ex@mplePassw0rd"
                },
                createPasswordField {
                    label = tr("隐藏切换按钮", "Reveal button hidden")
                    value = "Ex@mplePassw0rd"
                    revealButtonHidden = true
                },
            ),
        ),
    )
    view.body.append(reveal.root)

    // --- Basic features (password-field-basic-features.ts) --------------------------------------
    val features =
        section(
            tr("基本特性", "Basic features"),
            tr(
                "标签、辅助文本、占位符、提示、清空按钮与前后缀插槽与 Text Field 完全一致。",
                "The label, helper text, placeholder, tooltip, clear button and prefix / suffix slots work " +
                    "exactly as in Text Field.",
            ),
        )
    val featureField =
        createPasswordField {
            label = tr("标签", "Label")
            helperText = tr("辅助文本", "Helper text")
            placeholder = tr("占位符", "Placeholder")
            clearButtonVisible = true
        }
    featureField.append(
        createTooltip { text = tr("提示文本", "Tooltip text") }.apply { slot = "tooltip" },
        createIcon { icon = "vaadin:lock" }.apply { slot = "prefix" },
    )
    features.body.append(example(null, featureField))
    view.body.append(features.root)

    // --- Validation (password-field-validation.ts) ----------------------------------------------
    val validation =
        section(
            tr("校验", "Validation"),
            tr(
                "required、minlength / maxlength 与 pattern 一起约束密码；示例在 validated 事件里把每种" +
                    "失败映射成具体提示。",
                "required, minlength / maxlength and pattern constrain the password together; the example " +
                    "maps each failure to its own message in the validated event.",
            ),
        )
    val password =
        createPasswordField {
            label = tr("密码", "Password")
            helperText =
                tr(
                    "6 到 12 位，只支持字母 A-Z 和数字",
                    "6 to 12 characters. Only letters A-Z and numbers supported.",
                )
            required = true
            minlength = 6.0
            maxlength = 12.0
            pattern = PASSWORD_LETTERS_AND_DIGITS
        }
    val passwordResult = Readout(tr("尚未校验", "Not validated yet"))
    password.listen(PasswordFieldEvents.validated) {
        val validity = validityOf(password)
        val message =
            when {
                validity.valueMissing -> tr("这一项必填", "Field is required")
                validity.tooShort ->
                    tr(
                        "最短 ${password.minlength.toInt()} 个字符",
                        "Minimum length is ${password.minlength.toInt()} characters",
                    )
                validity.tooLong ->
                    tr(
                        "最长 ${password.maxlength.toInt()} 个字符",
                        "Maximum length is ${password.maxlength.toInt()} characters",
                    )
                validity.patternMismatch -> tr("只允许字母 A-Z 和数字", "Only letters A-Z and numbers are allowed")
                else -> ""
            }
        password.errorMessage = message
        passwordResult.set(
            tr("校验：", "Check: "),
            if (message.isEmpty()) tr("通过", "passed") else tr("未通过", "failed"),
        )
    }
    validation.body.append(example(null, column(password, passwordResult.root)))
    view.body.append(validation.root)

    // --- Read-only and disabled (password-field-readonly-and-disabled.ts) -----------------------
    val states =
        section(
            tr("只读与禁用", "Read-only and disabled"),
            tr(
                "readonly 可以聚焦和复制但不能修改；disabled 完全移除交互，两者都保留掩码显示。",
                "readonly allows focus and copying but no edits; disabled removes interaction entirely. " +
                    "Both keep the masked rendering.",
            ),
        )
    states.body.append(
        example(
            null,
            row(
                createPasswordField {
                    label = tr("只读", "Read-only")
                    value = "Ex@mplePassw0rd"
                    readonly = true
                },
                createPasswordField {
                    label = tr("禁用", "Disabled")
                },
            ),
        ),
    )
    view.body.append(states.root)

    // --- Style variants (password-field-styles.ts) ----------------------------------------------
    val styles =
        section(
            tr("样式变体", "Style variants"),
            tr(
                "theme 变体与 Text Field 相同：align-right、small 与 helper-above-field 可以组合使用。",
                "The theme variants match Text Field: align-right, small and helper-above-field can be " +
                    "combined.",
            ),
        )
    val styledField =
        createPasswordField {
            label = tr("标签", "Label")
            helperText = tr("辅助文本", "Helper text")
            value = "Ex@mplePassw0rd"
        }.withTheme("align-right", "small", "helper-above-field")
    styles.body.append(example(null, styledField))
    view.body.append(styles.root)

    // --- Best practices (password-field-helper.ts, password-field-advanced-helper.ts) -----------
    val practices =
        section(
            tr("最佳实践", "Best practices"),
            tr(
                "在辅助文本里直接说明密码要求，用户不必猜；密码强度也可以显示在辅助文本和后缀图标里。" +
                    "示例只按长度分为弱 / 中 / 强，真实强度评估应在应用侧完成。",
                "State the password requirements in the helper text so users do not have to guess; the " +
                    "strength can go into the helper text and a suffix icon as well. The example derives " +
                    "weak / moderate / strong from the length only; real strength estimation belongs in the " +
                    "application.",
            ),
        )
    val requirements =
        createPasswordField {
            label = tr("密码", "Password")
            helperText =
                tr(
                    "密码至少 8 位，且必须包含至少一个字母和一个数字。",
                    "A password must be at least 8 characters. It has to have at least one letter and one " +
                        "digit.",
                )
            pattern = PASSWORD_HELPER_PATTERN
            errorMessage = tr("密码不符合要求", "Not a valid password")
        }
    val advanced =
        createPasswordField {
            label = tr("密码", "Password")
            pattern = PASSWORD_HELPER_PATTERN
            errorMessage = tr("密码不符合要求", "Not a valid password")
        }
    advanced.style.setProperty("width", "14em")
    val strengthCheck = createIcon { icon = "vaadin:check" }.apply { slot = "suffix" }
    strengthCheck.setHidden(true)
    val strengthText = span(tr("弱", "weak"))
    val helperSlot = div()
    helperSlot.slot = "helper"
    helperSlot.append(span(tr("密码强度：", "Password strength: ")), strengthText)
    advanced.append(strengthCheck, helperSlot)

    fun updateStrength() {
        val length = advanced.value.length
        val strong = length > 9
        val label =
            when {
                strong -> tr("强", "strong")
                length > 5 -> tr("中", "moderate")
                else -> tr("弱", "weak")
            }
        val color =
            when {
                strong -> "var(--lumo-success-text-color)"
                length > 5 -> "var(--lumo-warning-text-color)"
                else -> "var(--lumo-error-text-color)"
            }
        strengthText.textContent = label
        strengthText.style.setProperty("color", color)
        strengthCheck.setHidden(!strong)
    }
    advanced.listen(PasswordFieldEvents.valueChanged) { updateStrength() }
    updateStrength()

    practices.body.append(
        example(tr("在辅助文本中说明要求", "Requirements in the helper text"), requirements),
        example(tr("显示密码强度", "Showing password strength"), advanced),
    )
    view.body.append(practices.root)

    // --- Value change modes (password-field-value-change-mode.ts, Flow-only example) ------------
    val modeField = createPasswordField { label = tr("密码", "Password") }
    view.body.append(
        valueChangeModesSection(
            modeField,
            PasswordFieldEvents.valueChanged,
            PasswordFieldEvents.change,
        ) { modeField.value }.root,
    )

    return view
}
