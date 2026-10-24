package demo.pages

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.ui.EventLog
import demo.ui.PageMeta
import demo.ui.Readout
import demo.ui.column
import demo.ui.example
import demo.ui.listen
import demo.ui.page
import demo.ui.row
import demo.ui.section
import demo.ui.withTheme
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
                    title = "Text Field",
                    summary = "单行文本输入：标签、占位符、前缀/后缀插槽、清空按钮、只读、禁用与校验。",
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
                    title = "Text Area",
                    summary = "多行文本输入：行数范围、字数上限与约束校验。",
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
                    title = "Email Field",
                    summary = "带邮箱格式语义的文本输入，继承 Text Field 的全部属性。",
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
                    title = "Password Field",
                    summary = "密码输入：自带明文切换按钮，可隐藏该按钮。",
                    docsUrl = "$DOCS_BASE/password-field",
                    sourcePath = "pages/TextFields.kt",
                ),
            builder = ::buildPasswordFieldPage,
        ),
    )

private fun buildTextFieldPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    // --- 基本用法 -------------------------------------------------------------------------------
    val basic = section("基本用法", "input 在每次按键后触发，change 在提交（回车或失焦）后触发。")
    val readout = Readout("当前值：（空）")
    val log = EventLog(6)
    val nameField =
        createTextField {
            label = "姓名"
            placeholder = "例如 Ada Lovelace"
            helperText = "显示在输入框下方"
        }
    nameField.setAttribute("data-test", "text-field-basic")
    nameField.listen(TextFieldEvents.input) {
        readout.set("当前值：", nameField.value.ifEmpty { "（空）" })
    }
    nameField.listen(TextFieldEvents.change) { log.add("change · ${nameField.value}") }
    nameField.listen(TextFieldEvents.valueChanged) { log.add("value-changed · ${nameField.value}") }

    basic.body.append(example(null, column(nameField, readout.root)), example("事件流", log.root))
    view.body.append(basic.root)

    // --- 前缀、后缀与清空 ------------------------------------------------------------------------
    val decoration = section("前缀 / 后缀与清空", "任意节点都可以放进 prefix / suffix 插槽；clearButtonVisible 提供内置的清空按钮。")
    val searchField =
        createTextField {
            label = "搜索"
            placeholder = "输入关键字"
            clearButtonVisible = true
        }
    searchField.append(createIcon { icon = "lumo:search" }.apply { setAttribute("slot", "prefix") })
    val unitField =
        createTextField {
            label = "金额"
            value = "128"
        }
    unitField.append(createIcon { icon = "lumo:checkmark" }.apply { setAttribute("slot", "suffix") })
    val cleared = Readout("清空按钮会把值置为空字符串")
    searchField.listen(TextFieldEvents.valueChanged) {
        cleared.set("搜索值：", searchField.value.ifEmpty { "（空）" })
    }
    decoration.body.append(example(null, column(searchField, unitField, cleared.root)))
    view.body.append(decoration.root)

    // --- 校验 -----------------------------------------------------------------------------------
    val validation = section("校验", "required 与 pattern 由组件的 validate() 检查；失败时同时设置 invalid 与 errorMessage。")
    val codeField =
        createTextField {
            label = "邀请码"
            helperText = "3-6 位小写字母"
            required = true
            pattern = "[a-z]{3,6}"
            errorMessage = "请输入 3-6 位小写字母"
        }
    codeField.setAttribute("data-test", "text-field-validated")
    val result = Readout("尚未校验")
    val validateButton =
        createButton { textContent = "校验" }.apply {
            clickEvent.addHandler {
                val valid = codeField.validate()
                result.set("校验结果：", if (valid) "通过" else "失败（invalid=${codeField.invalid}）")
            }
        }
    val resetButton =
        createButton { textContent = "重置" }.withTheme("tertiary").apply {
            clickEvent.addHandler {
                codeField.clear()
                codeField.invalid = false
                result.set("已重置，值：", "（空）")
            }
        }
    validation.body.append(example(null, column(codeField, row(validateButton, resetButton), result.root)))
    view.body.append(validation.root)

    // --- 只读与禁用 -----------------------------------------------------------------------------
    val states = section("只读与禁用", "readonly 可以聚焦和复制但不能修改；disabled 完全移除交互。")
    states.body.append(
        example(
            null,
            row(
                createTextField {
                    label = "只读"
                    value = "2026-10-25"
                    readonly = true
                },
                createTextField {
                    label = "禁用"
                    value = "不可编辑"
                    disabled = true
                },
            ),
        ),
    )
    view.body.append(states.root)

    return view.root
}

private fun buildTextAreaPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("基本用法", "多行输入默认 2 行，可通过 minRows / maxRows 控制高度增长区间。")
    val lengthReadout = Readout("字数：0 / 200")
    val messageField =
        createTextArea {
            label = "留言"
            placeholder = "最多 200 字"
            maxlength = 200.0
            helperText = "支持换行，超出上限会被截断"
        }
    messageField.setAttribute("data-test", "text-area-basic")
    messageField.listen(TextAreaEvents.input) {
        lengthReadout.set("字数：${messageField.value.length} / 200")
    }
    basic.body.append(example(null, column(messageField, lengthReadout.root)))
    view.body.append(basic.root)

    val rows = section("行数范围", "minRows / maxRows 决定初始高度与自动增长的边界。")
    rows.body.append(
        example(
            null,
            row(
                createTextArea {
                    label = "3 行起，最多 5 行"
                    minRows = 3.0
                    maxRows = 5.0
                    value = "第一行\n第二行\n第三行"
                },
                createTextArea {
                    label = "单行高度的多行输入"
                    minRows = 1.0
                    maxRows = 1.0
                },
            ),
        ),
    )
    view.body.append(rows.root)

    val validation = section("校验", "required 与 maxlength 一起构成输入约束。")
    val requiredArea =
        createTextArea {
            label = "必填说明"
            required = true
            errorMessage = "这一项不能为空"
        }
    requiredArea.setAttribute("data-test", "text-area-required")
    val result = Readout("尚未校验")
    val validateButton =
        createButton { textContent = "校验" }.apply {
            clickEvent.addHandler {
                result.set("校验结果：", if (requiredArea.validate()) "通过" else "失败")
            }
        }
    validation.body.append(example(null, column(requiredArea, row(validateButton, result.root))))
    view.body.append(validation.root)

    return view.root
}

private fun buildEmailFieldPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("基本用法", "值不是合法邮箱时 checkValidity() 返回 false；输入过程中会实时反映 invalid。")
    val emailField =
        createEmailField {
            label = "邮箱地址"
            placeholder = "ada@example.com"
            required = true
            clearButtonVisible = true
            helperText = "用于接收通知"
            errorMessage = "请输入合法的邮箱地址"
        }
    emailField.setAttribute("data-test", "email-field")
    emailField.append(createIcon { icon = "lumo:user" }.apply { setAttribute("slot", "prefix") })
    val readout = Readout("valid = false")
    val sync = {
        readout.set("valid = ${emailField.checkValidity()} · 值：${emailField.value.ifEmpty { "（空）" }}")
    }
    emailField.listen(EmailFieldEvents.input) { sync() }
    emailField.listen(EmailFieldEvents.change) { sync() }
    sync()

    basic.body.append(example(null, column(emailField, readout.root)))
    view.body.append(basic.root)

    val states = section("只读与禁用", "")
    states.body.append(
        example(
            null,
            row(
                createEmailField {
                    label = "只读"
                    value = "ada@example.com"
                    readonly = true
                },
                createEmailField {
                    label = "禁用"
                    value = "grace@example.com"
                    disabled = true
                },
            ),
        ),
    )
    view.body.append(states.root)

    return view.root
}

private fun buildPasswordFieldPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("基本用法", "点击眼睛按钮切换明文显示，passwordVisible 只读反映当前状态。")
    val passwordField =
        createPasswordField {
            label = "密码"
            helperText = "至少 8 位"
            clearButtonVisible = false
        }
    passwordField.setAttribute("data-test", "password-field")
    val readout = Readout("passwordVisible = false")
    val sync = {
        readout.set("passwordVisible = ${passwordField.passwordVisible} · 长度 ${passwordField.value.length}")
    }
    passwordField.listen(PasswordFieldEvents.input) { sync() }
    // `passwordVisible` is read-only and has no change event; the reveal button's click is composed,
    // so a click listener on the host is enough to refresh the read-out.
    passwordField.clickEvent.addHandler { sync() }
    basic.body.append(example(null, column(passwordField, readout.root)))
    view.body.append(basic.root)

    val variants = section("变体", "revealButtonHidden 去掉明文切换按钮，适合一次性口令等场景。")
    variants.body.append(
        example(
            null,
            row(
                createPasswordField {
                    label = "保留明文按钮"
                    value = "secret-123"
                },
                createPasswordField {
                    label = "隐藏明文按钮"
                    value = "secret-123"
                    revealButtonHidden = true
                },
                createPasswordField {
                    label = "禁用"
                    value = "secret-123"
                    disabled = true
                },
            ),
        ),
    )
    view.body.append(variants.root)

    return view.root
}
