package demo.pages.fields

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.pages.data.message
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
import demo.ui.span
import demo.ui.withTheme
import vaadin.button.createButton
import vaadin.icon.createIcon
import vaadin.icons.registerVaadinIcons
import vaadin.textarea.TextAreaEvents
import vaadin.textarea.createTextArea
import vaadin.tooltip.createTooltip
import web.dom.clickEvent
import web.events.addHandler

val textAreaEntry: CatalogEntry =
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
                sourcePath = "pages/fields/TextAreaPage.kt",
            ),
        builder = ::buildTextAreaPage,
    )

/** Sentence pattern of `text-area-validation.ts`: one sentence, capitalised, ending with a period. */
private const val SENTENCE_PATTERN = "^[A-Z]([A-Za-z0-9,-\\s])*\\.$"

/** The `templates.json` lorem ipsum used by the text-area height and character-counter examples. */
private const val LOREM_IPSUM =
    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec vel semper libero. Vestibulum ante " +
        "ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae.\n\n" +
        "Proin volutpat, sapien ut facilisis ultricies, eros purus blandit velit, at ultrices mi libero " +
        "quis ante. Curabitur scelerisque metus et libero convallis consequat. Pellentesque feugiat " +
        "pulvinar nisl sed pellentesque."

/**
 * `TextArea` has no native `pattern` support; upstream checks it with a regular expression, and the
 * error mapping of the docs example does the same. Compile errors count as a match like upstream.
 */
private fun hasPatternMismatch(pattern: String, value: String): Boolean {
    if (pattern.isEmpty() || value.isEmpty()) return false
    return runCatching { !Regex(pattern).matches(value) }.getOrDefault(false)
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
