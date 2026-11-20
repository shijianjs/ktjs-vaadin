package demo.pages.pickers

import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.componentApiUrl
import demo.ui.PageMeta
import demo.ui.REPO_SOURCE_ROOT
import demo.ui.listen
import js.objects.unsafeJso
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.page.Blocks.anchor as kitAnchor
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.page.Blocks.row as kitRow
import io.github.shijianjs.ktjs.demo.page.Blocks.col as kitCol
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import io.github.shijianjs.ktjs.demo.page.Readout
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.vaadin.icons.registerVaadinIcons
import io.github.shijianjs.ktjs.vaadin.timepicker.TimePickerEvents
import io.github.shijianjs.ktjs.vaadin.timepicker.TimePickerI18n
import io.github.shijianjs.ktjs.vaadin.timepicker.TimePickerTime
import io.github.shijianjs.ktjs.vaadin.timepicker.createTimePicker
import io.github.shijianjs.ktjs.vaadin.timepicker.formatISOTime
import io.github.shijianjs.ktjs.vaadin.timepicker.parseISOTime
import io.github.shijianjs.ktjs.vaadin.tooltip.createTooltip
import web.dom.clickEvent
import web.events.addHandler
import web.html.HTMLAnchorElement

private val timePickerEnglish: TimePickerTexts = TimePickerTexts(
    docsLink = "Vaadin documentation",
    sourceLink = "Kotlin source",
    apiLink = "Kotlin API reference",
    basicTitle = "Basic usage",
    basicHint = "value is an HH:mm string; typing or picking from the dropdown snaps the value to the step.",
    alarm = "Alarm",
    currentTime = "Current time: ",
    empty = "(empty)",
    minutesTitle = "Minutes step",
    minutesHint = "step is given in seconds (1800 = 30 minutes) and also decides whether the value keeps " +
        "minutes or seconds.",
    meetingTime = "Meeting time",
    halfHour = "30-minute increments",
    secondsTitle = "Seconds step",
    secondsHint = "With step = 1 the value is HH:mm:ss and the dropdown lists every second.",
    received = "Message received",
    autoTitle = "Auto open",
    autoHint = "autoOpenDisabled keeps the dropdown from opening on focus; open() / close() still control it " +
        "from outside.",
    open = "Open",
    close = "Close",
    validationTitle = "Validation",
    validationHint = "required, min and max are checked by validate(); the validated event sets errorMessage " +
        "from the value.",
    appointment = "Appointment time",
    openHours = "Open 8:00-16:00",
    invalidFormat = "Invalid time format",
    required = "Field is required",
    tooEarly = "Too early, choose another time",
    tooLate = "Too late, choose another time",
    validate = "Validate",
    errorPrefix = "Error message: ",
    none = "(none)",
    customTitle = "Custom validation",
    customHint = "The docs example uses a Binder validator to allow two business-hour ranges; this page " +
        "performs the same check in validated, so the 12:00-13:00 lunch break is rejected.",
    customHours = "Open 8:00-12:00, 13:00-16:00",
    unavailable = "The selected time is not available",
    featuresTitle = "Basic features",
    featuresHint = "helperText, placeholder and clearButtonVisible are the usual input hints; the prefix slot " +
        "holds an icon and the tooltip slot a hover description.",
    label = "Label",
    helper = "Helper text",
    placeholder = "Placeholder",
    tooltip = "Tooltip text",
    statesTitle = "Read-only and disabled",
    statesHint = "readonly allows focus and copying but no edits; disabled turns off all interaction.",
    readonly = "Read-only",
    disabled = "Disabled",
    stylesTitle = "Style variants",
    stylesHint = "align-right, small and helper-above-field combine the same way as on the date picker.",
    parserTitle = "Custom parser",
    parserHint = "time-picker-custom-parser in the docs is a plain picker (the parser is configured in Flow). " +
        "In the browser the same effect comes from i18n.parseTime / i18n.formatTime: the field below also " +
        "accepts a value such as 7.30.",
    defaultParser = "Default parser",
    looseAlarm = "Alarm (also accepts 7.30)",
    looseHelper = "Type either 7.30 or 07:30",
    committed = "Committed value: ",
    parseTimeCaption = "i18n.parseTime",
)

private val timePickerChinese: TimePickerTexts = TimePickerTexts(
    docsLink = "Vaadin 官方文档",
    sourceLink = "查看 Kotlin 源码",
    apiLink = "Kotlin API 文档",
    basicTitle = "基本用法",
    basicHint = "value 是 HH:mm 字符串；输入或从下拉列表选择都会把值规整到 step 的精度。",
    alarm = "闹钟",
    currentTime = "当前时间：",
    empty = "（空）",
    minutesTitle = "分钟步进",
    minutesHint = "step 以秒为单位（1800 = 30 分钟），同时决定值保留到分钟还是秒。",
    meetingTime = "会议时间",
    halfHour = "每 30 分钟一档",
    secondsTitle = "秒级步进",
    secondsHint = "step = 1 时值是 HH:mm:ss，下拉列表列出每一秒。",
    received = "消息接收时间",
    autoTitle = "自动展开",
    autoHint = "autoOpenDisabled 阻止聚焦时弹出下拉，仍可用 open() / close() 从外部控制。",
    open = "打开",
    close = "关闭",
    validationTitle = "校验",
    validationHint = "required、min 与 max 由 validate() 检查；validated 事件里按值设置 errorMessage。",
    appointment = "预约时间",
    openHours = "开放时间 8:00-16:00",
    invalidFormat = "时间格式不正确",
    required = "此项为必填项",
    tooEarly = "时间过早，请另选一个时间",
    tooLate = "时间过晚，请另选一个时间",
    validate = "校验",
    errorPrefix = "错误信息：",
    none = "（无）",
    customTitle = "自定义校验",
    customHint = "文档示例用 Binder 的验证器限制两段营业时间；这里在 validated 里做同样的判断，" +
        "12:00-13:00 的午休被拒绝。",
    customHours = "开放时间 8:00-12:00、13:00-16:00",
    unavailable = "所选时间不可用",
    featuresTitle = "基本特性",
    featuresHint = "helperText、placeholder 与 clearButtonVisible 是三个最常用的输入提示；prefix 放图标，" +
        "tooltip 插槽提供悬停说明。",
    label = "标签",
    helper = "辅助文本",
    placeholder = "占位符",
    tooltip = "提示文本",
    statesTitle = "只读与禁用",
    statesHint = "readonly 可以聚焦和复制但不能修改；disabled 关闭全部交互。",
    readonly = "只读",
    disabled = "禁用",
    stylesTitle = "样式变体",
    stylesHint = "align-right、small 与 helper-above-field 可以组合使用，和日期选择器完全一致。",
    parserTitle = "自定义解析",
    parserHint = "文档里的 time-picker-custom-parser 只是一个默认选择器（解析器在 Flow 侧配置）。浏览器端可以" +
        "用 i18n.parseTime / i18n.formatTime 达到同样效果：下面的字段也接受 7.30 这样的写法。",
    defaultParser = "默认解析器",
    looseAlarm = "闹钟（也接受 7.30）",
    looseHelper = "输入 7.30 或 07:30 都可以",
    committed = "提交的值：",
    parseTimeCaption = "i18n.parseTime",
)

private val timePickerTexts: Translations<TimePickerTexts> = texts(
    Langs.EN to timePickerEnglish,
    Langs.ZH_HANS to timePickerChinese,
)

val timePickerEntry: CatalogEntry =
    CatalogEntry(
        id = "time-picker",
        group = Groups.PICKERS,
        meta =
            PageMeta(
                title = bi("Time Picker", "Time Picker"),
                summary =
                    bi(
                        "时间选择：值格式为 HH:mm，可限制范围与步进精度。",
                        "Time selection: the value is HH:mm, with optional bounds and step precision.",
                    ),
                docsUrl = "$DOCS_BASE/time-picker",
                sourcePath = "pages/pickers/TimePickerPage.kt",
            ),
        kitPage = { TimePickerPage.NAME() },
    )

// ---------------------------------------------------------------------------------------------
// Time picker
// ---------------------------------------------------------------------------------------------

/**
 * The legacy builder, kept until the whole catalog has moved to kit blocks.
 *
 * Every migrated page keeps its old builder for one reason: reverting a page that turns out to
 * render wrong is then deleting one line from the catalog instead of rewriting the page again.
 */
/**
 * The same page on kit blocks: the nodes are built once and a language switch only rewrites their
 * text, so a value someone typed survives the switch.
 */
internal class TimePickerPage : DemoPage() {
    private val meta: PageMeta = timePickerEntry.meta

    private val texts = Binder(timePickerTexts)

    private val basicPicker = createTimePicker { value = "07:00" }.apply { dataset["test"] = "time-picker-basic" }

    private val basicReadout = Readout.NAME { show("") }

    private val minutesPicker = createTimePicker {
        value = "12:30"
        step = 1800.0
    }

    private val secondsPicker = createTimePicker {
        value = "15:45:08"
        step = 1.0
    }

    private val autoPicker = createTimePicker {
        value = "05:30"
        step = 1800.0
        autoOpenDisabled = true
    }

    private val openedReadout = Readout.NAME { show("opened = false") }

    private val openButton = createButton().addTheme("tertiary")

    private val closeButton = createButton().addTheme("tertiary")

    private val validatedPicker = createTimePicker {
        value = "08:30"
        required = true
        min = "08:00"
        max = "16:00"
        step = 1800.0
    }

    private val validatedError = Readout.NAME { show("") }

    private val validateButton = createButton()

    private val customPicker = createTimePicker {
        min = "08:00"
        max = "16:00"
        step = 1800.0
    }

    private val customError = Readout.NAME { show("") }

    private val customValidateButton = createButton()

    private val featuredPicker = createTimePicker { clearButtonVisible = true }

    private val featuredTooltip = createTooltip()

    private val readonlyPicker = createTimePicker {
        value = "07:00"
        readonly = true
    }

    private val disabledPicker = createTimePicker().apply { disabled = true }

    private val styledPicker = createTimePicker { value = "07:00" }.apply {
        addTheme("align-right", "small", "helper-above-field")
    }

    private val plainPicker = createTimePicker()

    private val loosePicker = createTimePicker {
        value = "07:30"
    }.apply {
        i18n =
            unsafeJso<TimePickerI18n> {
                parseTime = { text -> parseLooseTime(text) }
                formatTime = { time -> formatISOTime(time) }
            }
    }

    private val looseReadout = Readout.NAME { show("") }

    private val links = mutableListOf<HTMLAnchorElement>()

    /**
     * The custom-validation message as the page last set it. It is kept here rather than read back
     * from the picker: an `errorMessage` the user never triggered reads as `undefined` from the
     * component, and the page needs `""` on a language switch before any validation ran.
     */
    private var customErrorText: String = ""

    /** Which validation message the user is currently looking at, re-rendered on a language switch. */
    private var validationReason: ValidationReason = ValidationReason.NONE

    private enum class ValidationReason { NONE, FORMAT, REQUIRED, EARLY, LATE }

    init {
        registerVaadinIcons()
        val docs = kitAnchor("", meta.docsUrlFor(Langs.EN))
        val source = kitAnchor("", "$REPO_SOURCE_ROOT/${meta.sourcePath}")
        val api = kitAnchor("", componentApiUrl(timePickerEntry.id))
        links += docs
        links += source
        links += api
        addLink(texts.bind(docs, { it.docsLink }))
        addLink(texts.bind(source, { it.sourceLink }))
        addLink(texts.bind(api, { it.apiLink }))

        buildBasic()
        buildMinutes()
        buildSeconds()
        buildAutoOpen()
        buildValidation()
        buildCustomValidation()
        buildFeatures()
        buildStates()
        buildStyles()
        buildParser()

        applyTranslation(Langs.EN)
    }

    private fun buildBasic() {
        val basic = SectionBlock.NAME { show() }
        texts.bind(basic.heading, { it.basicTitle })
        texts.bind(basic.hint, { it.basicHint })
        texts.bind(basicPicker, { it.alarm })
        basic.body.append(exampleBox(null, kitCol(basicPicker, basicReadout.root)))
        body.append(basic.root)
        basicPicker.listen(TimePickerEvents.valueChanged) {
            renderBasic()
        }
    }

    private fun buildMinutes() {
        val section = SectionBlock.NAME { show() }
        texts.bind(section.heading, { it.minutesTitle })
        texts.bind(section.hint, { it.minutesHint })
        texts.bind(minutesPicker, { it.meetingTime })
        texts.bind(minutesPicker, { it.halfHour }) { text -> minutesPicker.helperText = text }
        section.body.append(exampleBox(null, minutesPicker))
        body.append(section.root)
    }

    private fun buildSeconds() {
        val section = SectionBlock.NAME { show() }
        texts.bind(section.heading, { it.secondsTitle })
        texts.bind(section.hint, { it.secondsHint })
        texts.bind(secondsPicker, { it.received })
        section.body.append(exampleBox(null, secondsPicker))
        body.append(section.root)
    }

    private fun buildAutoOpen() {
        val section = SectionBlock.NAME { show() }
        texts.bind(section.heading, { it.autoTitle })
        texts.bind(section.hint, { it.autoHint })
        texts.bind(autoPicker, { it.alarm })
        texts.bind(openButton, { it.open })
        texts.bind(closeButton, { it.close })
        openButton.clickEvent.addHandler { autoPicker.open() }
        closeButton.clickEvent.addHandler { autoPicker.close() }
        autoPicker.listen(TimePickerEvents.openedChanged) {
            openedReadout.set("opened = ${autoPicker.opened}")
        }
        section.body.append(exampleBox(null, kitCol(autoPicker, kitRow(openButton, closeButton, openedReadout.root)))
        )
        body.append(section.root)
    }

    private fun buildValidation() {
        val section = SectionBlock.NAME { show() }
        texts.bind(section.heading, { it.validationTitle })
        texts.bind(section.hint, { it.validationHint })
        texts.bind(validatedPicker, { it.appointment })
        texts.bind(validatedPicker, { it.openHours }) { text -> validatedPicker.helperText = text }
        texts.bind(validateButton, { it.validate })
        validateButton.clickEvent.addHandler { validatedPicker.validate() }
        validatedPicker.listen(TimePickerEvents.validated) {
            val value = validatedPicker.value
            validationReason =
                when {
                    value.isEmpty() && rawInputText(validatedPicker).isNotEmpty() -> ValidationReason.FORMAT
                    value.isEmpty() -> ValidationReason.REQUIRED
                    value < validatedPicker.min -> ValidationReason.EARLY
                    value > validatedPicker.max -> ValidationReason.LATE
                    else -> ValidationReason.NONE
                }
            validatedPicker.errorMessage = validationMessage(validationReason)
            validatedPicker.invalid = validatedPicker.errorMessage.isNotEmpty()
            renderValidatedReadout()
        }
        section.body.append(exampleBox(null, kitCol(validatedPicker, kitRow(validateButton, validatedError.root)))
        )
        body.append(section.root)
    }

    private fun buildCustomValidation() {
        val section = SectionBlock.NAME { show() }
        texts.bind(section.heading, { it.customTitle })
        texts.bind(section.hint, { it.customHint })
        texts.bind(customPicker, { it.appointment })
        texts.bind(customPicker, { it.customHours }) { text -> customPicker.helperText = text }
        texts.bind(customValidateButton, { it.validate })
        customValidateButton.clickEvent.addHandler { customPicker.validate() }
        customPicker.listen(TimePickerEvents.validated) {
            val value = customPicker.value
            val available = (value >= "08:00" && value <= "12:00") || (value >= "13:00" && value <= "16:00")
            customErrorText = if (available) "" else texts.current.unavailable
            customPicker.errorMessage = customErrorText
            customPicker.invalid = customErrorText.isNotEmpty()
            renderCustomReadout()
        }
        section.body.append(exampleBox(null, kitCol(customPicker, kitRow(customValidateButton, customError.root)))
        )
        body.append(section.root)
    }

    private fun buildFeatures() {
        val section = SectionBlock.NAME { show() }
        texts.bind(section.heading, { it.featuresTitle })
        texts.bind(section.hint, { it.featuresHint })
        texts.bind(featuredPicker, { it.label })
        texts.bind(featuredPicker, { it.helper }) { text -> featuredPicker.helperText = text }
        texts.bind(featuredPicker, { it.placeholder }) { text -> featuredPicker.placeholder = text }
        texts.bind(featuredTooltip, { it.tooltip }) { text -> featuredTooltip.text = text }
        featuredTooltip.slot = "tooltip"
        featuredPicker.append(featuredTooltip)
        featuredPicker.append(createIcon { icon = "vaadin:vaadin-h" }.apply { slot = "prefix" })
        section.body.append(exampleBox(null, featuredPicker))
        body.append(section.root)
    }

    private fun buildStates() {
        val section = SectionBlock.NAME { show() }
        texts.bind(section.heading, { it.statesTitle })
        texts.bind(section.hint, { it.statesHint })
        texts.bind(readonlyPicker, { it.readonly })
        texts.bind(disabledPicker, { it.disabled })
        section.body.append(exampleBox(null, kitRow(readonlyPicker, disabledPicker)))
        body.append(section.root)
    }

    private fun buildStyles() {
        val section = SectionBlock.NAME { show() }
        texts.bind(section.heading, { it.stylesTitle })
        texts.bind(section.hint, { it.stylesHint })
        texts.bind(styledPicker, { it.label })
        texts.bind(styledPicker, { it.helper }) { text -> styledPicker.helperText = text }
        section.body.append(exampleBox(null, styledPicker))
        body.append(section.root)
    }

    private fun buildParser() {
        val section = SectionBlock.NAME { show() }
        texts.bind(section.heading, { it.parserTitle })
        texts.bind(section.hint, { it.parserHint })
        texts.bind(plainPicker, { it.alarm })
        texts.bind(loosePicker, { it.looseAlarm })
        texts.bind(loosePicker, { it.looseHelper }) { text -> loosePicker.helperText = text }
        section.body.append(
            exampleBox(texts.current.defaultParser, plainPicker),
            exampleBox(texts.current.parseTimeCaption, kitCol(loosePicker, looseReadout.root)),
        )
        body.append(section.root)
        loosePicker.listen(TimePickerEvents.valueChanged) { renderLooseReadout() }
    }

    override fun applyTranslation(lang: Lang) {
        texts.apply(lang)
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        links.first().href = meta.docsUrlFor(lang)
        renderBasic()
        renderValidatedReadout()
        renderCustomReadout()
        renderLooseReadout()
    }

    private fun renderBasic() {
        val copy = texts.current
        basicReadout.set(copy.currentTime + basicPicker.value.ifEmpty { copy.empty })
    }

    private fun renderValidatedReadout() {
        val copy = texts.current
        validatedError.set(copy.errorPrefix + validationMessage(validationReason).ifEmpty { copy.none })
    }

    private fun renderCustomReadout() {
        val copy = texts.current
        customError.set(copy.errorPrefix + customErrorText.ifEmpty { copy.none })
    }

    private fun renderLooseReadout() {
        val copy = texts.current
        looseReadout.set(copy.committed + loosePicker.value.ifEmpty { copy.empty })
    }

    private fun validationMessage(reason: ValidationReason): String {
        val copy = texts.current
        return when (reason) {
            ValidationReason.NONE -> ""
            ValidationReason.FORMAT -> copy.invalidFormat
            ValidationReason.REQUIRED -> copy.required
            ValidationReason.EARLY -> copy.tooEarly
            ValidationReason.LATE -> copy.tooLate
        }
    }

    companion object : CustomElementReferenceAdapter<TimePickerPage>(TimePickerPage::class.js, "vaadin-demo-time-picker-page")
}

/**
 * Time picker parser that accepts `7.30` as well as `07:30`, the custom-parser example. Text with
 * seconds (`7.30.15`, `07:30:15`) is normalized and handed to the standard ISO parser.
 */
private fun parseLooseTime(text: String): TimePickerTime? {
    val pieces = text.trim().replace('.', ':').split(":")
    if (pieces.size == 3) {
        val hours = pieces[0].toIntOrNull() ?: return null
        val minutes = pieces[1].toIntOrNull() ?: return null
        val seconds = pieces[2].toIntOrNull() ?: return null
        if (hours !in 0..23 || minutes !in 0..59 || seconds !in 0..59) return null
        return parseISOTime("${pad2(hours)}:${pad2(minutes)}:${pad2(seconds)}")
    }
    if (pieces.size != 2) return null
    val hours = pieces[0].toIntOrNull() ?: return null
    val minutes = pieces[1].toIntOrNull() ?: return null
    if (hours !in 0..23 || minutes !in 0..59) return null
    return parseISOTime("${pad2(hours)}:${pad2(minutes)}")
}
