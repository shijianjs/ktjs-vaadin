package demo.pages

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
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
import demo.ui.withTheme
import demo.tr
import vaadin.button.createButton
import vaadin.emailfield.EmailFieldEvents
import vaadin.emailfield.createEmailField
import vaadin.icon.createIcon
import vaadin.passwordfield.PasswordFieldEvents
import vaadin.passwordfield.createPasswordField
import vaadin.textarea.TextAreaEvents
import vaadin.textarea.createTextArea
import vaadin.textfield.TextFieldEvents
import vaadin.textfield.createTextField
import web.dom.clickEvent
import web.events.addHandler
import web.html.HTMLElement

val textFieldEntries: List<CatalogEntry> =
    listOf(
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
                    sourcePath = "pages/TextFields.kt",
                ),
            builder = ::buildTextFieldPage,
        ),
        CatalogEntry(
            id = "text-area",
            group = Groups.FIELDS,
            meta =
                PageMeta(
                    title = bi("Text Area", "Text Area"),
                    summary =
                        bi(
                            "多行文本输入：行数范围、字数上限与约束校验。",
                            "Multi-line text input: row range, character limit and constraint validation.",
                        ),
                    docsUrl = "$DOCS_BASE/text-area",
                    sourcePath = "pages/TextFields.kt",
                ),
            builder = ::buildTextAreaPage,
        ),
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
                    sourcePath = "pages/TextFields.kt",
                ),
            builder = ::buildEmailFieldPage,
        ),
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
                    sourcePath = "pages/TextFields.kt",
                ),
            builder = ::buildPasswordFieldPage,
        ),
    )

private fun buildTextFieldPage(meta: PageMeta): PageView {
    val view = page(meta)

    // --- Basic usage ----------------------------------------------------------------------------
    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "input 在每次按键后触发，change 在提交（回车或失焦）后触发。",
                "input fires after every keystroke; change fires on commit (Enter or blur).",
            ),
        )
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
        example(null, column(nameField, readout.root)),
        example(tr("事件流", "Event log"), log.root),
    )
    view.body.append(basic.root)

    // --- Prefix, suffix and clear ---------------------------------------------------------------
    val decoration =
        section(
            tr("前缀 / 后缀与清空", "Prefix, suffix and clear"),
            tr(
                "任意节点都可以放进 prefix / suffix 插槽；clearButtonVisible 提供内置的清空按钮。",
                "Any node can go into the prefix / suffix slots; clearButtonVisible adds a built-in clear button.",
            ),
        )
    val searchField =
        createTextField {
            label = tr("搜索", "Search")
            placeholder = tr("输入关键字", "Type to search")
            clearButtonVisible = true
        }
    searchField.append(createIcon { icon = "lumo:search" }.apply { slot = "prefix" })
    val unitField =
        createTextField {
            label = tr("金额", "Amount")
            value = "128"
        }
    unitField.append(createIcon { icon = "lumo:checkmark" }.apply { slot = "suffix" })
    val cleared = Readout(tr("清空按钮会把值置为空字符串", "The clear button sets the value to an empty string"))
    searchField.listen(TextFieldEvents.valueChanged) {
        cleared.set(tr("搜索值：", "Search value: "), searchField.value.ifEmpty { tr("（空）", "(empty)") })
    }
    decoration.body.append(example(null, column(searchField, unitField, cleared.root)))
    view.body.append(decoration.root)

    // --- Validation -----------------------------------------------------------------------------
    val validation =
        section(
            tr("校验", "Validation"),
            tr(
                "required 与 pattern 由组件的 validate() 检查；失败时同时设置 invalid 与 errorMessage。",
                "required and pattern are checked by the component's validate(); a failure sets invalid and " +
                    "errorMessage together.",
            ),
        )
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
    validation.body.append(example(null, column(codeField, row(validateButton, resetButton), result.root)))
    view.body.append(validation.root)

    // --- Read-only and disabled -----------------------------------------------------------------
    val states =
        section(
            tr("只读与禁用", "Read-only and disabled"),
            tr(
                "readonly 可以聚焦和复制但不能修改；disabled 完全移除交互。",
                "readonly allows focus and copying but no edits; disabled removes interaction entirely.",
            ),
        )
    states.body.append(
        example(
            null,
            row(
                createTextField {
                    label = tr("只读", "Read-only")
                    value = "2026-10-25"
                    readonly = true
                },
                createTextField {
                    label = tr("禁用", "Disabled")
                    value = tr("不可编辑", "Not editable")
                    disabled = true
                },
            ),
        ),
    )
    view.body.append(states.root)

    return view
}

private fun buildTextAreaPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "多行输入默认 2 行，可通过 minRows / maxRows 控制高度增长区间。",
                "Multi-line input starts at 2 rows; minRows / maxRows bound how far it can grow.",
            ),
        )
    val lengthReadout = Readout(tr("字数：0 / 200", "Characters: 0 / 200"))
    val messageField =
        createTextArea {
            label = tr("留言", "Message")
            placeholder = tr("最多 200 字", "Up to 200 characters")
            maxlength = 200.0
            helperText = tr("支持换行，超出上限会被截断", "Line breaks are kept; text beyond the limit is truncated")
        }
    messageField.dataset["test"] = "text-area-basic"
    messageField.listen(TextAreaEvents.input) {
        lengthReadout.set(
            tr(
                "字数：${messageField.value.length} / 200",
                "Characters: ${messageField.value.length} / 200",
            ),
        )
    }
    basic.body.append(example(null, column(messageField, lengthReadout.root)))
    view.body.append(basic.root)

    val rows =
        section(
            tr("行数范围", "Row range"),
            tr(
                "minRows / maxRows 决定初始高度与自动增长的边界。",
                "minRows / maxRows set the initial height and the limit of automatic growth.",
            ),
        )
    rows.body.append(
        example(
            null,
            row(
                createTextArea {
                    label = tr("3 行起，最多 5 行", "3 to 5 rows")
                    minRows = 3.0
                    maxRows = 5.0
                    value = tr("第一行\n第二行\n第三行", "First line\nSecond line\nThird line")
                },
                createTextArea {
                    label = tr("单行高度的多行输入", "Multi-line input at a single row")
                    minRows = 1.0
                    maxRows = 1.0
                },
            ),
        ),
    )
    view.body.append(rows.root)

    val validation =
        section(
            tr("校验", "Validation"),
            tr("required 与 maxlength 一起构成输入约束。", "required and maxlength together form the input constraint."),
        )
    val requiredArea =
        createTextArea {
            label = tr("必填说明", "Required note")
            required = true
            errorMessage = tr("这一项不能为空", "This field is required")
        }
    requiredArea.dataset["test"] = "text-area-required"
    val result = Readout(tr("尚未校验", "Not validated yet"))
    val validateButton =
        createButton { textContent = tr("校验", "Validate") }.apply {
            clickEvent.addHandler {
                val status = if (requiredArea.validate()) tr("通过", "Valid") else tr("失败", "Failed")
                result.set(tr("校验结果：", "Result: "), status)
            }
        }
    validation.body.append(example(null, column(requiredArea, row(validateButton, result.root))))
    view.body.append(validation.root)

    return view
}

private fun buildEmailFieldPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "值不是合法邮箱时 checkValidity() 返回 false；输入过程中会实时反映 invalid。",
                "checkValidity() returns false for an invalid address; invalid updates while you type.",
            ),
        )
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
    val sync = {
        readout.set(
            tr(
                "valid = ${emailField.checkValidity()} · 值：${emailField.value.ifEmpty { "（空）" }}",
                "valid = ${emailField.checkValidity()} · value: ${emailField.value.ifEmpty { "(empty)" }}",
            ),
        )
    }
    emailField.listen(EmailFieldEvents.input) { sync() }
    emailField.listen(EmailFieldEvents.change) { sync() }
    sync()

    basic.body.append(example(null, column(emailField, readout.root)))
    view.body.append(basic.root)

    val states = section(tr("只读与禁用", "Read-only and disabled"), "")
    states.body.append(
        example(
            null,
            row(
                createEmailField {
                    label = tr("只读", "Read-only")
                    value = "ada@example.com"
                    readonly = true
                },
                createEmailField {
                    label = tr("禁用", "Disabled")
                    value = "grace@example.com"
                    disabled = true
                },
            ),
        ),
    )
    view.body.append(states.root)

    return view
}

private fun buildPasswordFieldPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "点击眼睛按钮切换明文显示，passwordVisible 只读反映当前状态。",
                "The eye button toggles plain text; passwordVisible reflects the current state read-only.",
            ),
        )
    val passwordField =
        createPasswordField {
            label = tr("密码", "Password")
            helperText = tr("至少 8 位", "At least 8 characters")
            clearButtonVisible = false
        }
    passwordField.dataset["test"] = "password-field"
    val readout = Readout("passwordVisible = false")
    val sync = {
        readout.set(
            tr(
                "passwordVisible = ${passwordField.passwordVisible} · 长度 ${passwordField.value.length}",
                "passwordVisible = ${passwordField.passwordVisible} · length ${passwordField.value.length}",
            ),
        )
    }
    passwordField.listen(PasswordFieldEvents.input) { sync() }
    // `passwordVisible` is read-only and has no change event; the reveal button's click is composed,
    // so a click listener on the host is enough to refresh the read-out.
    passwordField.clickEvent.addHandler { sync() }
    basic.body.append(example(null, column(passwordField, readout.root)))
    view.body.append(basic.root)

    val variants =
        section(
            tr("变体", "Variants"),
            tr(
                "revealButtonHidden 去掉明文切换按钮，适合一次性口令等场景。",
                "revealButtonHidden removes the reveal button, which suits one-time codes and similar cases.",
            ),
        )
    variants.body.append(
        example(
            null,
            row(
                createPasswordField {
                    label = tr("保留明文按钮", "Reveal button shown")
                    value = "secret-123"
                },
                createPasswordField {
                    label = tr("隐藏明文按钮", "Reveal button hidden")
                    value = "secret-123"
                    revealButtonHidden = true
                },
                createPasswordField {
                    label = tr("禁用", "Disabled")
                    value = "secret-123"
                    disabled = true
                },
            ),
        ),
    )
    view.body.append(variants.root)

    return view
}
