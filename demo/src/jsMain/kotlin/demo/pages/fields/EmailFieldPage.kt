package demo.pages.fields

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.pages.data.message
import demo.pages.layout.emailField
import demo.tr
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.Readout
import demo.ui.column
import demo.ui.example
import demo.ui.listen
import demo.ui.page
import demo.ui.row
import demo.ui.section
import demo.ui.withTheme
import vaadin.emailfield.EmailFieldEvents
import vaadin.emailfield.createEmailField
import vaadin.icon.createIcon
import vaadin.icons.registerVaadinIcons
import vaadin.tooltip.createTooltip

val emailFieldEntry: CatalogEntry =
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
                docsUrl = "$DOCS_BASE/email-field",
                sourcePath = "pages/fields/EmailFieldPage.kt",
            ),
        builder = ::buildEmailFieldPage,
    )

/** Pattern of `email-field-validation.ts`: only addresses in the `example.com` domain. */
private const val EMAIL_EXAMPLE_PATTERN = "^[a-zA-Z0-9_\\-+]+(?:\\.[a-zA-Z0-9_\\-+]+)*@example\\.com$"

private fun buildEmailFieldPage(meta: PageMeta): PageView {
    val view = page(meta)
    registerVaadinIcons()

    // --- Basic usage (email-field-basic.ts) -----------------------------------------------------
    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "Email Field 继承 Text Field 的全部功能，并按 RFC 5322 校验邮箱格式；值无效时字段高亮并" +
                    "显示错误信息。",
                "Email Field inherits every Text Field feature and validates the address against RFC 5322; " +
                    "an invalid value highlights the field and shows the error message.",
            ),
        )
    val validEmail =
        createEmailField {
            label = tr("邮箱地址", "Email address")
            name = "email"
            value = "julia.scheider@email.com"
            errorMessage = tr("请输入合法的邮箱地址", "Enter a valid email address")
            clearButtonVisible = true
        }
    val invalidEmail =
        createEmailField {
            label = tr("邮箱地址", "Email address")
            name = "email"
            value = "This is not an email"
            errorMessage = tr("请输入合法的邮箱地址", "Enter a valid email address")
            clearButtonVisible = true
            invalid = true
        }
    val emailField =
        createEmailField {
            label = tr("邮箱地址", "Email address")
            placeholder = "ada@example.com"
            required = true
            clearButtonVisible = true
            helperText = tr("用于接收通知", "Used for notifications")
            errorMessage = tr("请输入合法的邮箱地址", "Enter a valid email address")
        }
    emailField.dataset["test"] = "email-field"
    emailField.append(createIcon { icon = "lumo:user" }.apply { slot = "prefix" })
    val readout = Readout("valid = false")
    fun syncValidity() {
        readout.set(
            tr(
                "valid = ${emailField.checkValidity()} · 值：${emailField.value.ifEmpty { "（空）" }}",
                "valid = ${emailField.checkValidity()} · value: ${emailField.value.ifEmpty { "(empty)" }}",
            ),
        )
    }
    emailField.listen(EmailFieldEvents.input) { syncValidity() }
    emailField.listen(EmailFieldEvents.change) { syncValidity() }
    syncValidity()
    basic.body.append(
        example(tr("合法与非法邮箱", "Valid and invalid addresses"), row(validEmail, invalidEmail)),
        example(tr("实时校验结果", "Validity while typing"), column(emailField, readout.root)),
    )
    view.body.append(basic.root)

    // --- Basic features (email-field-basic-features.ts) -----------------------------------------
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
        createEmailField {
            label = tr("标签", "Label")
            helperText = tr("辅助文本", "Helper text")
            placeholder = tr("占位符", "Placeholder")
            clearButtonVisible = true
        }
    featureField.append(
        createTooltip { text = tr("提示文本", "Tooltip text") }.apply { slot = "tooltip" },
        createIcon { icon = "vaadin:envelope" }.apply { slot = "prefix" },
    )
    features.body.append(example(null, featureField))
    view.body.append(features.root)

    // --- Validation (email-field-validation.ts) -------------------------------------------------
    val validation =
        section(
            tr("校验", "Validation"),
            tr(
                "内置的邮箱格式校验之外，pattern 可以进一步限制域名。示例只接受 example.com 地址，并在" +
                    "validated 事件里区分“必填”与“格式不符”。",
                "Beyond the built-in email check, pattern can restrict the domain further. The example only " +
                    "accepts example.com addresses and distinguishes missing and malformed values in the " +
                    "validated event.",
            ),
        )
    val restricted =
        createEmailField {
            label = tr("邮箱地址", "Email address")
            helperText = tr("只接受 example.com 域名", "Only example.com addresses allowed")
            required = true
            pattern = EMAIL_EXAMPLE_PATTERN
        }
    val restrictedResult = Readout(tr("尚未校验", "Not checked yet"))
    restricted.listen(EmailFieldEvents.validated) {
        val validity = validityOf(restricted)
        val message =
            when {
                validity.valueMissing -> tr("这一项必填", "Field is required")
                validity.patternMismatch ->
                    tr("请输入 example.com 域名下的合法邮箱地址", "Enter a valid example.com email address")
                else -> ""
            }
        restricted.errorMessage = message
        restrictedResult.set(
            tr("校验：", "Check: "),
            if (message.isEmpty()) tr("通过", "passed") else tr("未通过", "failed"),
        )
    }
    validation.body.append(example(null, column(restricted, restrictedResult.root)))
    view.body.append(validation.root)

    // --- Read-only and disabled (email-field-readonly-and-disabled.ts) --------------------------
    val states =
        section(
            tr("只读与禁用", "Read-only and disabled"),
            tr(
                "readonly 可以聚焦、选择和复制但不能修改；disabled 完全移除交互。",
                "readonly allows focus, selection and copying but no edits; disabled removes interaction " +
                    "entirely.",
            ),
        )
    states.body.append(
        example(
            null,
            row(
                createEmailField {
                    label = tr("只读", "Read-only")
                    value = "example@example.com"
                    readonly = true
                },
                createEmailField {
                    label = tr("禁用", "Disabled")
                },
            ),
        ),
    )
    view.body.append(states.root)

    // --- Style variants (email-field-styles.ts) -------------------------------------------------
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
        createEmailField {
            label = tr("标签", "Label")
            helperText = tr("辅助文本", "Helper text")
            value = "foo@bar.com"
        }.withTheme("align-right", "small", "helper-above-field")
    styles.body.append(example(null, styledField))
    view.body.append(styles.root)

    // --- Value change modes (email-field-value-change-mode.ts, Flow-only example) ---------------
    val modeField =
        createEmailField {
            label = tr("邮箱地址", "Email address")
            placeholder = "ada@example.com"
        }
    view.body.append(
        valueChangeModesSection(modeField, EmailFieldEvents.valueChanged, EmailFieldEvents.change) { modeField.value }.root,
    )

    return view
}
