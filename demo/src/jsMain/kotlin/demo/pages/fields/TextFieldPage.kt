package demo.pages.fields

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.pages.data.message
import demo.tr
import demo.ui.EventLog
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.Readout
import demo.ui.column
import demo.ui.example
import demo.ui.listen
import demo.ui.page
import demo.ui.row
import demo.ui.section
import demo.ui.span
import demo.ui.withTheme
import vaadin.button.createButton
import vaadin.icon.createIcon
import vaadin.icons.registerVaadinIcons
import vaadin.switch.SwitchEvents
import vaadin.switch.createSwitch
import vaadin.textfield.TextFieldEvents
import vaadin.textfield.createTextField
import vaadin.tooltip.createTooltip
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
        builder = ::buildTextFieldPage,
    )

/** Phone pattern of `text-field-validation.ts` (documents grouped digits with optional separators). */
private const val PHONE_PATTERN = "^[+]?\\(?[0-9]{3}\\)?-?[0-9]{3}-?[0-9]{4,6}$"

private fun buildTextFieldPage(meta: PageMeta): PageView {
    val view = page(meta)
    registerVaadinIcons()

    // --- Basic usage (text-field-basic.ts) ------------------------------------------------------
    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "文本输入支持标签、前缀 / 后缀插槽和内置清空按钮；input 在每次按键后触发，change 在提交" +
                    "（回车或失焦）后触发。",
                "A text field supports a label, prefix / suffix slots and a built-in clear button; input " +
                    "fires after every keystroke and change on commit (Enter or blur).",
            ),
        )
    val address =
        createTextField {
            label = tr("街道地址", "Street address")
            value = "Ruukinkatu 2"
            clearButtonVisible = true
        }
    address.append(createIcon { icon = "vaadin:map-marker" }.apply { slot = "prefix" })
    val readout = Readout(tr("当前值：（空）", "Value: (empty)"))
    val log = EventLog(6)
    val nameField =
        createTextField {
            label = tr("姓名", "Name")
            placeholder = tr("例如 Ada Lovelace", "e.g. Ada Lovelace")
            helperText = tr("显示在输入框下方", "Shown below the input")
        }
    nameField.dataset["test"] = "text-field-basic"
    nameField.listen(TextFieldEvents.input) {
        readout.set(tr("当前值：", "Value: "), nameField.value.ifEmpty { tr("（空）", "(empty)") })
    }
    nameField.listen(TextFieldEvents.change) { log.add("change · ${nameField.value}") }
    nameField.listen(TextFieldEvents.valueChanged) { log.add("value-changed · ${nameField.value}") }
    basic.body.append(
        example(tr("街道地址：前缀图标与清空按钮", "Street address: prefix icon and clear button"), address),
        example(tr("读值", "Read-out"), column(nameField, readout.root)),
        example(tr("事件流", "Event log"), log.root),
    )
    view.body.append(basic.root)

    // --- Basic features (text-field-basic-features.ts) ------------------------------------------
    val features =
        section(
            tr("基本特性", "Basic features"),
            tr(
                "标签说明字段用途，辅助文本补充说明，占位符给出输入示例；tooltip 插槽在悬停与键盘聚焦时" +
                    "显示提示，前后缀插槽放置图标或单位。",
                "The label names the field, the helper text adds guidance and the placeholder shows an " +
                    "example; the tooltip slot explains the field, and the prefix / suffix slots hold icons " +
                    "or units.",
            ),
        )
    val featureField =
        createTextField {
            label = tr("标签", "Label")
            helperText = tr("辅助文本", "Helper text")
            placeholder = tr("占位符", "Placeholder")
            clearButtonVisible = true
        }
    featureField.append(
        createTooltip { text = tr("提示文本", "Tooltip text") }.apply { slot = "tooltip" },
        createIcon { icon = "vaadin:vaadin-h" }.apply { slot = "prefix" },
        span(":)").apply { slot = "suffix" },
    )
    features.body.append(example(null, featureField))
    view.body.append(features.root)

    // --- Validation (text-field-validation.ts) --------------------------------------------------
    val validation =
        section(
            tr("校验", "Validation"),
            tr(
                "required、minlength / maxlength、pattern 与 allowedCharPattern 一起约束输入。示例在" +
                    "validated 事件里读取原生 validity，为每种失败给出对应的错误文案。",
                "required, minlength / maxlength, pattern and allowedCharPattern constrain the input together. " +
                    "The example reads the native validity in the validated event and maps each failure to " +
                    "its own error message.",
            ),
        )
    val phone =
        createTextField {
            label = tr("电话号码", "Phone number")
            helperText = tr("格式：+(123)456-7890", "Format: +(123)456-7890")
            required = true
            minlength = 5.0
            maxlength = 18.0
            pattern = PHONE_PATTERN
            allowedCharPattern = "[0-9()+-]"
        }
    val phoneResult = Readout(tr("尚未校验", "Not validated yet"))
    phone.listen(TextFieldEvents.validated) {
        val validity = validityOf(phone)
        val message =
            when {
                validity.valueMissing -> tr("这一项必填", "Field is required")
                validity.tooShort ->
                    tr(
                        "最短 ${phone.minlength.toInt()} 个字符",
                        "Minimum length is ${phone.minlength.toInt()} characters",
                    )
                validity.tooLong ->
                    tr(
                        "最长 ${phone.maxlength.toInt()} 个字符",
                        "Maximum length is ${phone.maxlength.toInt()} characters",
                    )
                validity.patternMismatch -> tr("电话号码格式不正确", "Invalid phone number format")
                else -> ""
            }
        phone.errorMessage = message
        phoneResult.set(
            tr("校验：", "validated · valid = "),
            if (message.isEmpty()) tr("通过", "true") else tr("未通过", "false"),
        )
    }
    val codeField =
        createTextField {
            label = tr("邀请码", "Invitation code")
            helperText = tr("3-6 位小写字母", "3-6 lowercase letters")
            required = true
            pattern = "[a-z]{3,6}"
            errorMessage = tr("请输入 3-6 位小写字母", "Enter 3-6 lowercase letters")
        }
    codeField.dataset["test"] = "text-field-validated"
    val result = Readout(tr("尚未校验", "Not validated yet"))
    val validateButton =
        createButton { textContent = tr("校验", "Validate") }.apply {
            clickEvent.addHandler {
                val valid = codeField.validate()
                val status =
                    if (valid) {
                        tr("通过", "Valid")
                    } else {
                        tr(
                            "失败（invalid=${codeField.invalid}）",
                            "Failed (invalid=${codeField.invalid})",
                        )
                    }
                result.set(tr("校验结果：", "Result: "), status)
            }
        }
    val resetButton =
        createButton { textContent = tr("重置", "Reset") }.withTheme("tertiary").apply {
            clickEvent.addHandler {
                codeField.clear()
                codeField.invalid = false
                result.set(tr("已重置，值：", "Reset, value: "), tr("（空）", "(empty)"))
            }
        }
    validation.body.append(
        example(tr("约束与错误文案", "Constraints and error messages"), column(phone, phoneResult.root)),
        example(
            tr("手动校验（validate）", "Manual validation (validate())"),
            column(codeField, row(validateButton, resetButton), result.root),
        ),
    )
    view.body.append(validation.root)

    // --- Read-only and disabled (text-field-readonly-and-disabled.ts) ---------------------------
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
                createTextField {
                    label = tr("只读", "Read-only")
                    value = tr("值", "Value")
                    readonly = true
                },
                createTextField {
                    label = tr("禁用", "Disabled")
                },
            ),
        ),
    )
    view.body.append(states.root)

    // --- Autoselect -----------------------------------------------------------------------------
    val autoselectSection =
        section(
            tr("自动全选", "Autoselect"),
            tr(
                "autoselect 打开时，用鼠标或键盘聚焦都会选中全部内容，便于直接替换；关闭时鼠标点击会把" +
                    "光标放在点击处。",
                "With autoselect enabled, focusing the field with either the pointer or the keyboard " +
                    "selects the whole value so it can be replaced; with it off, a click places the caret " +
                    "where you clicked.",
            ),
        )
    val autoselectField =
        createTextField {
            label = tr("值", "Value")
            value = "Ruukinkatu 2"
            autoselect = true
        }
    val autoselectSwitch =
        createSwitch {
            label = tr("autoselect", "autoselect")
            checked = true
        }
    autoselectSwitch.listen(SwitchEvents.checkedChanged) { autoselectField.autoselect = autoselectSwitch.checked }
    autoselectSection.body.append(example(null, column(autoselectSwitch, autoselectField)))
    view.body.append(autoselectSection.root)

    // --- Input mode -----------------------------------------------------------------------------
    val inputMode =
        section(
            tr("输入模式", "Input mode"),
            tr(
                "inputmode 是给移动端浏览器的提示，决定聚焦时弹出哪种虚拟键盘；桌面浏览器没有可见效果。",
                "inputmode hints the browser which virtual keyboard to show on mobile devices; desktop " +
                    "browsers show no visible difference.",
            ),
        )
    inputMode.body.append(
        example(
            null,
            row(
                createTextField {
                    label = tr("网址", "URL")
                    inputModeProperty = "url"
                    helperText = "inputmode=\"url\""
                },
                createTextField {
                    label = tr("邮箱", "Email")
                    inputModeProperty = "email"
                    helperText = "inputmode=\"email\""
                },
                createTextField {
                    label = tr("搜索", "Search")
                    inputModeProperty = "search"
                    helperText = "inputmode=\"search\""
                },
            ),
        ),
    )
    view.body.append(inputMode.root)

    // --- Style variants (text-field-styles.ts) --------------------------------------------------
    val styles =
        section(
            tr("样式变体", "Style variants"),
            tr(
                "theme 属性接受空格分隔的变体：align-right 右对齐文本（适合纵向排列的数值），small 更紧凑，" +
                    "helper-above-field 把辅助文本放在字段上方。",
                "The theme property takes space-separated variants: align-right right-aligns the text (useful " +
                    "for numbers in vertical groups), small renders a compact field and helper-above-field " +
                    "moves the helper text above the field.",
            ),
        )
    val styledField =
        createTextField {
            label = tr("标签", "Label")
            helperText = tr("辅助文本", "Helper text")
            value = tr("值", "Value")
        }.withTheme("align-right", "small", "helper-above-field")
    styles.body.append(
        example(
            tr("align-right + small + helper-above-field", "align-right + small + helper-above-field"),
            styledField,
        ),
        example(
            tr("文本对齐", "Text alignment"),
            row(
                createTextField {
                    label = "align-left"
                    value = "1234"
                }.withTheme("align-left"),
                createTextField {
                    label = "align-center"
                    value = "1234"
                }.withTheme("align-center"),
                createTextField {
                    label = "align-right"
                    value = "1234"
                }.withTheme("align-right"),
            ),
        ),
    )
    view.body.append(styles.root)

    // --- Value change modes (text-field-value-change-mode.ts, Flow-only example) ----------------
    val modeField =
        createTextField {
            label = tr("输入内容", "Type here")
            placeholder = tr("边输入边观察同步结果", "Type to watch the sync result")
        }
    view.body.append(
        valueChangeModesSection(modeField, TextFieldEvents.valueChanged, TextFieldEvents.change) { modeField.value }.root,
    )

    return view
}
