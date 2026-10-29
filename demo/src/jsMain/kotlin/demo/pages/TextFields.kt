package demo.pages

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.tr
import demo.ui.EventLog
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.Readout
import demo.ui.Section
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
import js.objects.unsafeJso
import kotlin.js.js
import kotlin.js.unsafeCast
import vaadin.button.createButton
import vaadin.emailfield.EmailFieldEvents
import vaadin.emailfield.createEmailField
import vaadin.fieldbase.InputMixin
import vaadin.icon.createIcon
import vaadin.icons.registerVaadinIcons
import vaadin.passwordfield.PasswordFieldEvents
import vaadin.passwordfield.createPasswordField
import vaadin.select.SelectEvents
import vaadin.select.SelectItemData
import vaadin.select.createSelect
import vaadin.switch.SwitchEvents
import vaadin.switch.createSwitch
import vaadin.textarea.TextAreaEvents
import vaadin.textarea.createTextArea
import vaadin.textfield.TextFieldEvents
import vaadin.textfield.createTextField
import vaadin.tooltip.createTooltip
import web.dom.clickEvent
import web.events.Event
import web.events.EventType
import web.events.addHandler
import web.html.HTMLInputElement
import web.html.HTMLElement
import web.validation.ValidityState

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

/** Phone pattern of `text-field-validation.ts` (documents grouped digits with optional separators). */
private const val PHONE_PATTERN = "^[+]?\\(?[0-9]{3}\\)?-?[0-9]{3}-?[0-9]{4,6}$"

/** Sentence pattern of `text-area-validation.ts`: one sentence, capitalised, ending with a period. */
private const val SENTENCE_PATTERN = "^[A-Z]([A-Za-z0-9,-\\s])*\\.$"

/** Pattern of `email-field-validation.ts`: only addresses in the `example.com` domain. */
private const val EMAIL_EXAMPLE_PATTERN = "^[a-zA-Z0-9_\\-+]+(?:\\.[a-zA-Z0-9_\\-+]+)*@example\\.com$"

/** Pattern of `password-field-helper.ts`: at least eight characters, one letter and one digit. */
private const val PASSWORD_HELPER_PATTERN = "^(?=.*[0-9])(?=.*[a-zA-Z]).{8}.*$"

/** Pattern of `password-field-validation.ts`: letters A-Z and digits only. */
private const val PASSWORD_LETTERS_AND_DIGITS = "^[A-Za-z0-9]+$"

/** The `templates.json` lorem ipsum used by the text-area height and character-counter examples. */
private const val LOREM_IPSUM =
    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec vel semper libero. Vestibulum ante " +
        "ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae.\n\n" +
        "Proin volutpat, sapien ut facilisis ultricies, eros purus blandit velit, at ultrices mi libero " +
        "quis ante. Curabitur scelerisque metus et libero convallis consequat. Pellentesque feugiat " +
        "pulvinar nisl sed pellentesque."

private const val VALUE_CHANGE_ON_CHANGE = "on-change"
private const val VALUE_CHANGE_EAGER = "eager"
private const val VALUE_CHANGE_LAZY = "lazy"
private const val VALUE_CHANGE_TIMEOUT = "timeout"

/**
 * Reads the native validity state behind a field.
 *
 * The validation examples of the documentation map the individual `validity` flags to their own
 * error messages. `inputElement` is part of `vaadin.fieldbase.InputMixin`; the upstream type is
 * `HTMLElement`, so it is narrowed to the `<input>` that actually backs a text field.
 */
private fun validityOf(field: InputMixin): ValidityState =
    field.inputElement.unsafeCast<HTMLInputElement>().validity

/**
 * `TextArea` has no native `pattern` support; upstream checks it with a regular expression, and the
 * error mapping of the docs example does the same. Compile errors count as a match like upstream.
 */
private fun hasPatternMismatch(pattern: String, value: String): Boolean {
    if (pattern.isEmpty() || value.isEmpty()) return false
    return runCatching { !Regex(pattern).matches(value) }.getOrDefault(false)
}

/** Timer helpers for the local value-change-mode simulation; kotlin-browser has no timer wrapper. */
private fun startTimeout(millis: Int, tick: () -> Unit): Int =
    js("(fn, ms) => window.setTimeout(fn, ms)")(tick, millis) as Int

private fun clearTimeout(handle: Int) {
    js("(handle) => window.clearTimeout(handle)")(handle)
}

/**
 * Simulates Flow's `ValueChangeMode`, which is a server-side API without a web-component
 * counterpart. The select switches when the client value is copied to the "server" read-out.
 */
private fun valueChangeModes(
    field: HTMLElement,
    valueChanged: EventType<Event>,
    change: EventType<Event>,
    readValue: () -> String,
): HTMLElement {
    val typed = Readout(tr("输入框内容：（空）", "Typed here: (empty)"))
    val synced = Readout(tr("服务端收到：（空）", "Server received: (empty)"))
    var mode = VALUE_CHANGE_ON_CHANGE
    var timer: Int? = null

    fun cancelTimer() {
        timer?.let { clearTimeout(it) }
        timer = null
    }

    fun showSynced() {
        cancelTimer()
        synced.set(
            tr("服务端收到：", "Server received: "),
            readValue().ifEmpty { tr("（空）", "(empty)") },
        )
    }

    val modes =
        createSelect {
            label = tr("同步模式", "Sync mode")
            items =
                arrayOf(
                    unsafeJso<SelectItemData> {
                        label = tr("on change（默认）", "on change (default)")
                        value = VALUE_CHANGE_ON_CHANGE
                    },
                    unsafeJso<SelectItemData> {
                        label = "eager"
                        value = VALUE_CHANGE_EAGER
                    },
                    unsafeJso<SelectItemData> {
                        label = tr("lazy（400 ms 防抖）", "lazy (400 ms debounce)")
                        value = VALUE_CHANGE_LAZY
                    },
                    unsafeJso<SelectItemData> {
                        label = tr("timeout（每 400 ms）", "timeout (every 400 ms)")
                        value = VALUE_CHANGE_TIMEOUT
                    },
                )
            value = VALUE_CHANGE_ON_CHANGE
        }

    modes.listen(SelectEvents.valueChanged) {
        mode = modes.value
        showSynced()
    }

    field.listen(valueChanged) {
        typed.set(
            tr("输入框内容：", "Typed here: "),
            readValue().ifEmpty { tr("（空）", "(empty)") },
        )
        when (mode) {
            VALUE_CHANGE_EAGER -> showSynced()
            VALUE_CHANGE_LAZY -> {
                cancelTimer()
                timer = startTimeout(400) { showSynced() }
            }
            VALUE_CHANGE_TIMEOUT -> if (timer == null) timer = startTimeout(400) { showSynced() }
        }
    }
    field.listen(change) { showSynced() }

    return column(modes, field, typed.root, synced.root)
}

/** Section wrapper around [valueChangeModes], shared by the four field pages. */
private fun valueChangeModesSection(
    field: HTMLElement,
    valueChanged: EventType<Event>,
    change: EventType<Event>,
    readValue: () -> String,
): Section {
    val modes =
        section(
            tr("值变化模式", "Value change modes"),
            tr(
                "ValueChangeMode 是 Flow 的服务端 API，Web Component 没有对应属性；这里用本地定时器模拟" +
                    "四种模式，“服务端收到”一行就是服务端最终会拿到的值。",
                "ValueChangeMode is a Flow server-side API without a web-component counterpart; this " +
                    "example simulates the four modes with local timers, and the server received line " +
                    "shows the value the server would end up with.",
            ),
        )
    modes.body.append(example(null, valueChangeModes(field, valueChanged, change, readValue)))
    return modes
}

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

private fun buildTextAreaPage(meta: PageMeta): PageView {
    val view = page(meta)
    registerVaadinIcons()

    // --- Basic usage (text-area-basic.ts) -------------------------------------------------------
    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "多行输入默认两行高，并随内容自动增长；maxlength 限制字符数，辅助文本实时显示已输入的" +
                    "长度与上限。",
                "A text area starts at two rows and grows with its content; maxlength caps the number of " +
                    "characters and the helper text shows the current length against that cap.",
            ),
        )
    val initialComment = "Great job. This is excellent!"
    val messageField =
        createTextArea {
            label = tr("评论", "Comment")
            value = initialComment
            maxlength = 200.0
            helperText = "${initialComment.length}/200"
        }
    messageField.dataset["test"] = "text-area-basic"
    val counter =
        Readout(
            tr(
                "字数：${initialComment.length} / 200",
                "Characters: ${initialComment.length} / 200",
            ),
        )
    messageField.listen(TextAreaEvents.valueChanged) {
        val length = messageField.value.length
        messageField.helperText = "$length/200"
        counter.set(tr("字数：$length / 200", "Characters: $length / 200"))
    }
    basic.body.append(example(null, column(messageField, counter.root)))
    view.body.append(basic.root)

    // --- Automatic height (text-area-auto-height.ts, text-area-height.ts) -----------------------
    val height =
        section(
            tr("高度自动调整", "Automatic height adjustment"),
            tr(
                "没有固定高度时，Text Area 随内容增长，默认最小高度是两行；也可以用 min-height / " +
                    "max-height 或 minRows / maxRows 约束增长范围。",
                "Without a fixed height the text area grows with its content and defaults to a two-row " +
                    "minimum; min-height / max-height or minRows / maxRows bound how far it may grow.",
            ),
        )
    val autoHeight =
        createTextArea {
            label = tr("描述", "Description")
            value = LOREM_IPSUM
        }
    autoHeight.style.setProperty("width", "100%")
    val boundedHeight =
        createTextArea {
            label = tr("描述（100 - 150 px）", "Description (100 - 150 px)")
            value = LOREM_IPSUM
        }
    boundedHeight.style.setProperty("width", "100%")
    boundedHeight.style.setProperty("min-height", "100px")
    boundedHeight.style.setProperty("max-height", "150px")
    val rowsHeight =
        createTextArea {
            label = tr("3 到 5 行", "3 to 5 rows")
            minRows = 3.0
            maxRows = 5.0
            value = tr("第一行\n第二行\n第三行", "First line\nSecond line\nThird line")
        }
    val singleRow =
        createTextArea {
            label = tr("单行高度的多行输入", "Multi-line input at a single row")
            minRows = 1.0
            maxRows = 1.0
        }
    height.body.append(
        example(tr("自动增长", "Growing automatically"), autoHeight),
        example(tr("限制高度", "Bounded height"), boundedHeight),
        example(tr("限制行数", "Bounded row count"), row(rowsHeight, singleRow)),
    )
    view.body.append(height.root)

    // --- Basic features (text-area-basic-features.ts) -------------------------------------------
    val features =
        section(
            tr("基本特性", "Basic features"),
            tr(
                "除多行输入外，Text Area 与 Text Field 共享标签、辅助文本、占位符、提示与前后缀插槽。",
                "Besides the multi-line input, Text Area shares the label, helper text, placeholder, " +
                    "tooltip and prefix / suffix slots with Text Field.",
            ),
        )
    val featureField =
        createTextArea {
            label = tr("标签", "Label")
            helperText = tr("辅助文本", "Helper text")
            placeholder = tr("占位符", "Placeholder")
            clearButtonVisible = true
        }
    featureField.style.setProperty("width", "100%")
    featureField.append(
        createTooltip { text = tr("提示文本", "Tooltip text") }.apply { slot = "tooltip" },
        createIcon { icon = "vaadin:vaadin-h" }.apply { slot = "prefix" },
        span(":)").apply { slot = "suffix" },
    )
    features.body.append(example(null, featureField))
    view.body.append(features.root)

    // --- Validation (text-area-validation.ts) ---------------------------------------------------
    val validation =
        section(
            tr("校验", "Validation"),
            tr(
                "required、minlength / maxlength、pattern 与 allowedCharPattern 组合使用。textarea 不支持" +
                    "原生 pattern，组件的 checkValidity() 用正则自行检查，示例在 validated 事件里按同样" +
                    "的规则给出错误文案。",
                "required, minlength / maxlength, pattern and allowedCharPattern combine. A textarea has no " +
                    "native pattern support, so the component checks it with a regular expression; the " +
                    "example applies the same rule when mapping the validated event to an error message.",
            ),
        )
    val sentence =
        createTextArea {
            label = tr("句子", "Sentence")
            helperText =
                tr(
                    "必须是以句号结尾的完整句子，长度 5 到 50 个字符",
                    "Must be one complete sentence ending in a period, between 5 and 50 characters long",
                )
            required = true
            minlength = 5.0
            maxlength = 50.0
            pattern = SENTENCE_PATTERN
            allowedCharPattern = "[A-Za-z0-9,.\\-\\s]"
        }
    sentence.style.setProperty("width", "100%")
    val sentenceResult = Readout(tr("尚未校验", "Not validated yet"))
    sentence.listen(TextAreaEvents.validated) {
        val validity = validityOf(sentence)
        val message =
            when {
                validity.valueMissing -> tr("这一项必填", "Field is required")
                validity.tooShort ->
                    tr(
                        "最短 ${sentence.minlength.toInt()} 个字符",
                        "Minimum length is ${sentence.minlength.toInt()} characters",
                    )
                validity.tooLong ->
                    tr(
                        "最长 ${sentence.maxlength.toInt()} 个字符",
                        "Maximum length is ${sentence.maxlength.toInt()} characters",
                    )
                hasPatternMismatch(sentence.pattern, sentence.value) ->
                    tr("必须是以句号结尾的完整句子", "Must be one complete sentence ending in a period")
                else -> ""
            }
        sentence.errorMessage = message
        sentenceResult.set(
            tr("校验：", "checked · "),
            if (message.isEmpty()) tr("通过", "valid") else tr("未通过", "invalid"),
        )
    }
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
    validation.body.append(
        example(tr("约束与错误文案", "Constraints and error messages"), column(sentence, sentenceResult.root)),
        example(tr("手动校验（validate）", "Manual validation (validate())"), column(requiredArea, row(validateButton, result.root))),
    )
    view.body.append(validation.root)

    // --- Read-only and disabled (text-area-readonly-and-disabled.ts) ----------------------------
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
                createTextArea {
                    label = tr("只读", "Read-only")
                    value = tr("值", "Value")
                    readonly = true
                },
                createTextArea {
                    label = tr("禁用", "Disabled")
                },
            ),
        ),
    )
    view.body.append(states.root)

    // --- Character counter (text-area-helper.ts) ------------------------------------------------
    val counterSection =
        section(
            tr("字数统计", "Character counter"),
            tr(
                "较长的自由文本常用 maxlength 限制上限，并借助辅助文本显示当前长度与上限。",
                "Longer free-form inputs are often capped with maxlength, and the helper text shows the " +
                    "current length against that cap.",
            ),
        )
    val longArea =
        createTextArea {
            label = tr("描述", "Description")
            value = LOREM_IPSUM
            maxlength = 600.0
            helperText = "${LOREM_IPSUM.length}/600"
        }
    longArea.style.setProperty("width", "100%")
    longArea.listen(TextAreaEvents.valueChanged) { longArea.helperText = "${longArea.value.length}/600" }
    counterSection.body.append(example(null, longArea))
    view.body.append(counterSection.root)

    // --- Style variants (text-area-styles.ts) ---------------------------------------------------
    val styles =
        section(
            tr("样式变体", "Style variants"),
            tr(
                "theme 属性同样接受 align-right、small 与 helper-above-field；多行输入通常占满容器宽度。",
                "The same align-right, small and helper-above-field variants apply; a text area usually " +
                    "fills the width of its container.",
            ),
        )
    val styledArea =
        createTextArea {
            label = tr("标签", "Label")
            helperText = tr("辅助文本", "Helper text")
            value = tr("值", "Value")
        }.withTheme("align-right", "small", "helper-above-field")
    styledArea.style.setProperty("width", "100%")
    styles.body.append(example(null, styledArea))
    view.body.append(styles.root)

    // --- Value change modes (text-area-value-change-mode.ts, Flow-only example) -----------------
    val modeField = createTextArea { label = tr("输入内容", "Type here") }
    view.body.append(
        valueChangeModesSection(modeField, TextAreaEvents.valueChanged, TextAreaEvents.change) { modeField.value }.root,
    )

    return view
}

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
