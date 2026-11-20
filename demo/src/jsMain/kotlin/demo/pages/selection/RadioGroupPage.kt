package demo.pages.selection

import io.github.shijianjs.ktjs.demo.page.col as kitCol
import io.github.shijianjs.ktjs.demo.page.exampleBox
import io.github.shijianjs.ktjs.demo.page.row as kitRow
import io.github.shijianjs.ktjs.demo.page.sectionBlock


import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import web.components.CustomElementReference
import web.dom.TagName
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.pages.fixtures.people
import demo.ui.PageFrame
import demo.ui.PageMeta
import demo.ui.Readout
import demo.ui.listen
import io.github.shijianjs.ktjs.dom.css.cssSheet
import io.github.shijianjs.ktjs.dom.css.dsl.className
import io.github.shijianjs.ktjs.dom.css.dsl.descendant
import io.github.shijianjs.ktjs.dom.css.dsl.nextSibling
import io.github.shijianjs.ktjs.dom.css.dsl.invoke
import io.github.shijianjs.ktjs.dom.css.dsl.tag
import io.github.shijianjs.ktjs.demo.style.Palette
import web.cssom.CSSStyleSheet
import web.cssom.ClassName
import web.html.HtmlTagName
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.checkbox.createCheckbox
import io.github.shijianjs.ktjs.vaadin.radiogroup.RadioGroupEvents
import io.github.shijianjs.ktjs.vaadin.radiogroup.createRadioButton
import io.github.shijianjs.ktjs.vaadin.radiogroup.createRadioGroup
import io.github.shijianjs.ktjs.vaadin.textfield.createTextField
import io.github.shijianjs.ktjs.vaadin.tooltip.createTooltip
import web.dom.clickEvent
import web.events.addHandler

val radioGroupEntry: CatalogEntry =
    CatalogEntry(
        id = "radio-group",
        group = Groups.SELECTION,
        meta =
            PageMeta(
                title = bi("Radio Group", "Radio Group"),
                summary =
                    bi(
                        "单选组：value 是字符串，子项由 vaadin-radio-button 提供。",
                        "Single-select group: value is a string and the items are vaadin-radio-button " +
                            "elements.",
                    ),
                docsUrl = "$DOCS_BASE/radio-button",
                docsNote =
                    bi(
                        "官方文档的 Radio Button 页面同时覆盖 Radio Group。",
                        "The Radio Button documentation page also covers Radio Group.",
                    ),
                sourcePath = "pages/selection/RadioGroupPage.kt",
            ),
        kitPage = { newRadioGroupPage() },
    )

/** The option box of the custom-label example; the sheet and the three options share it. */
private val RADIO_OPTION: ClassName = ClassName("radio-option")

/**
 * The card-style options of the "custom label" example: two lines of text per option, the second
 * one muted. The boxes themselves are `kitCol(...)`, so only the muted second line needs a rule.
 */
private val RADIO_SHEET: CSSStyleSheet = cssSheet {
    // The boxes themselves are `kitCol(...)` (a vertical layout), so the sheet only says that the
    // second line of an option is the muted one.
    ((className(RADIO_OPTION) descendant tag(HtmlTagName.span)) nextSibling tag(HtmlTagName.span)).invoke {
        color = Palette.muted
    }
}

/** Every string this page shows, in one language. */
private class RadioGroupTexts(
    val basicHeading: String,
    val basicHint: String,
    val plan: String,
    val basicOption: String,
    val standardOption: String,
    val proOption: String,
    val noneSelected: String,
    val defaultsHeading: String,
    val defaultsHint: String,
    val repeat: String,
    val never: String,
    val daily: String,
    val weekly: String,
    val monthly: String,
    val orientationHeading: String,
    val orientationHint: String,
    val status: String,
    val pending: String,
    val submitted: String,
    val confirmed: String,
    val travelClass: String,
    val economy: String,
    val business: String,
    val firstClass: String,
    val featuresHeading: String,
    val featuresHint: String,
    val label: String,
    val helperText: String,
    val tooltipText: String,
    val item1: String,
    val item2: String,
    val item3: String,
    val requiredHeading: String,
    val requiredHint: String,
    val paymentMethod: String,
    val choosePayment: String,
    val card: String,
    val bankTransfer: String,
    val readOnly: String,
    val yes: String,
    val no: String,
    val disabled: String,
    val inProgress: String,
    val done: String,
    val notValidated: String,
    val validate: String,
    val validationPrefix: String,
    val passed: String,
    val failed: String,
    val customOptionHeading: String,
    val customOptionHint: String,
    val visaLine: String,
    val expires: String,
    val other: String,
    val enterCardNumber: String,
    val cardNumber: String,
    val alternativeHeading: String,
    val alternativeHint: String,
    val replyBehavior: String,
    val reply: String,
    val replyAll: String,
    val checkboxLabel: String,
    val presentationHeading: String,
    val presentationHint: String,
    val account: (Int) -> String,
    val holder: (String) -> String,
    val stylesHeading: String,
    val stylesHint: String,
)

private val english: RadioGroupTexts =
    RadioGroupTexts(
        basicHeading = "Basic usage",
        basicHint = "radio-buttons inside one radio-group are mutually exclusive; value is the selected " +
            "string.",
        plan = "Plan",
        basicOption = "Basic",
        standardOption = "Standard",
        proOption = "Pro",
        noneSelected = "(none)",
        defaultsHeading = "Default value and group labels",
        defaultsHint = "Assigning an initial value selects a default option; several groups on one page " +
            "stay independent and their labels name different dimensions.",
        repeat = "Repeat",
        never = "None",
        daily = "Daily",
        weekly = "Weekly",
        monthly = "Monthly",
        orientationHeading = "Orientation",
        orientationHint = "theme=\"horizontal\" lays the options out in a row, vertical stacks them.",
        status = "Status",
        pending = "Pending",
        submitted = "Submitted",
        confirmed = "Confirmed",
        travelClass = "Travel class",
        economy = "Economy",
        business = "Business",
        firstClass = "First Class",
        featuresHeading = "Label, helper text and tooltip",
        featuresHint = "helper-text on the group and a slotted tooltip cover the field-level explanation.",
        label = "Label",
        helperText = "Helper text",
        tooltipText = "Tooltip text",
        item1 = "Item 1",
        item2 = "Item 2",
        item3 = "Item 3",
        requiredHeading = "Validation and states",
        requiredHint = "required rejects an empty value; readonly keeps the selection but blocks changes, " +
            "disabled turns off all interaction.",
        paymentMethod = "Payment method",
        choosePayment = "Choose a payment method",
        card = "Card",
        bankTransfer = "Bank transfer",
        readOnly = "Read-only",
        yes = "Yes",
        no = "No",
        disabled = "Disabled",
        inProgress = "In progress",
        done = "Done",
        notValidated = "Not validated yet",
        validate = "Validate",
        validationPrefix = "Validation: ",
        passed = "passed",
        failed = "failed",
        customOptionHeading = "Custom option",
        customOptionHint = "The label slot of a radio-button accepts arbitrary content; choosing \"Other\" " +
            "reveals an extra field for the details.",
        visaLine = "Visa · account 1234",
        expires = "Expires 12/28",
        other = "Other",
        enterCardNumber = "Enter the card number manually",
        cardNumber = "Card number",
        alternativeHeading = "Alternative to a checkbox",
        alternativeHint = "A single either-or decision reads better as a radio-group: here only one of " +
            "\"Reply\" and \"Reply to all\" is ever active.",
        replyBehavior = "Default reply behavior",
        reply = "Reply",
        replyAll = "Reply to all",
        checkboxLabel = "Reply All by default (unchecked state not clear)",
        presentationHeading = "Custom item presentation",
        presentationHint = "A multi-line label adds context to every option, which helps when the details " +
            "matter.",
        account = { "Account $it" },
        holder = { "Holder $it" },
        stylesHeading = "Style variants",
        stylesHint = "helper-above-field moves the helper text between the label and the options.",
    )

private val chinese: RadioGroupTexts =
    RadioGroupTexts(
        basicHeading = "基本用法",
        basicHint = "同一 radio-group 内的 radio-button 互斥；value 是选中的字符串。",
        plan = "套餐",
        basicOption = "基础版",
        standardOption = "标准版",
        proOption = "专业版",
        noneSelected = "（未选择）",
        defaultsHeading = "默认值与分组标签",
        defaultsHint = "给组赋初始 value 就有默认选中项；同一页面上的多组互不影响，各自的 label 说明不同维度。",
        repeat = "重复",
        never = "从不",
        daily = "每日",
        weekly = "每周",
        monthly = "每月",
        orientationHeading = "方向",
        orientationHint = "theme=\"horizontal\" 让选项横排，vertical 让选项竖排。",
        status = "状态",
        pending = "待处理",
        submitted = "已提交",
        confirmed = "已确认",
        travelClass = "旅行舱位",
        economy = "经济舱",
        business = "商务舱",
        firstClass = "头等舱",
        featuresHeading = "标签、辅助信息与提示",
        featuresHint = "group 上的 helper-text 与 slot=\"tooltip\" 的 tooltip 覆盖字段级说明。",
        label = "标签",
        helperText = "辅助信息",
        tooltipText = "提示文本",
        item1 = "选项 1",
        item2 = "选项 2",
        item3 = "选项 3",
        requiredHeading = "校验与状态",
        requiredHint = "required 让空值无法通过；readonly 保留选择但不能更改，disabled 完全不可交互。",
        paymentMethod = "付款方式",
        choosePayment = "请选择一种付款方式",
        card = "银行卡",
        bankTransfer = "转账",
        readOnly = "只读",
        yes = "是",
        no = "否",
        disabled = "禁用",
        inProgress = "进行中",
        done = "已完成",
        notValidated = "尚未校验",
        validate = "校验",
        validationPrefix = "校验：",
        passed = "通过",
        failed = "失败",
        customOptionHeading = "自定义选项",
        customOptionHint = "radio-button 的 label 插槽可以放任意内容；选中「其他」时再补一个输入字段收集详情。",
        visaLine = "Visa · 账户 1234",
        expires = "有效期 12/28",
        other = "其他",
        enterCardNumber = "手动输入卡号",
        cardNumber = "卡号",
        alternativeHeading = "替代勾选框",
        alternativeHint = "单个二选一的决定用 radio-group 更明确：这里的「回复」与「回复全部」始终只有一个生效。",
        replyBehavior = "默认回复行为",
        reply = "回复",
        replyAll = "回复全部",
        checkboxLabel = "默认回复全部（未勾选状态不明确）",
        presentationHeading = "自定义条目呈现",
        presentationHint = "多行 label 让每个选项带上补充信息，适合需要对比细节的场景。",
        account = { "账户 $it" },
        holder = { "持卡人 $it" },
        stylesHeading = "样式变体",
        stylesHint = "helper-above-field 把辅助信息放到标签与选项之间。",
    )

private val radioGroupTexts: Translations<RadioGroupTexts> = texts(
    Langs.EN to english,
    Langs.ZH_HANS to chinese,
)

internal class RadioGroupPage : DemoPage() {
    private val texts = Binder(radioGroupTexts)

    private val frame = PageFrame(radioGroupEntry)

    private val valueReadout = Readout("")

    private val validationReadout = Readout("")

    /** The value last selected in the basic group; `null` means nothing was chosen since load. */
    private var selectedValue: String? = null

    /** `null` until the validate button has run once. */
    private var validationResult: Boolean? = null

    private fun showValue() {
        valueReadout.set("value = ", selectedValue?.ifEmpty { texts.current.noneSelected } ?: "standard")
    }

    private fun showValidation() {
        val result = validationResult
        validationReadout.set(
            if (result == null) {
                texts.current.notValidated
            } else {
                texts.current.validationPrefix + if (result) texts.current.passed else texts.current.failed
            },
        )
    }

    init {
        frame.mount(this)
        addSheet(RADIO_SHEET)

        val basic = sectionBlock()
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        val group = createRadioGroup().addTheme("vertical")
        texts.bind(group, { it.plan }) { group.label = it }
        group.value = "standard"
        group.dataset["test"] = "radio-group"
        val basicButton = createRadioButton { value = "basic" }
        texts.bind(basicButton) { it.basicOption }
        val standardButton = createRadioButton { value = "standard" }
        texts.bind(standardButton) { it.standardOption }
        val proButton = createRadioButton { value = "pro" }
        texts.bind(proButton) { it.proOption }
        group.append(basicButton, standardButton, proButton)
        group.listen(RadioGroupEvents.valueChanged) {
            selectedValue = group.value
            showValue()
        }
        basic.body.append(exampleBox(null, kitCol(group, valueReadout.root)))
        body.append(basic.root)

        val defaults = sectionBlock()
        texts.bind(defaults.heading) { it.defaultsHeading }
        texts.bind(defaults.hint) { it.defaultsHint }
        val repeat = createRadioGroup().addTheme("vertical")
        texts.bind(repeat, { it.repeat }) { repeat.label = it }
        fun repeatOption(value: String, copy: (RadioGroupTexts) -> String, checked: Boolean = false) {
            val node = createRadioButton { this.value = value }
            node.checked = checked
            texts.bind(node, copy) { node.textContent = it }
            repeat.append(node)
        }
        repeatOption("none", { it.never }, checked = true)
        repeatOption("daily", { it.daily })
        repeatOption("weekly", { it.weekly })
        repeatOption("monthly", { it.monthly })
        defaults.body.append(exampleBox(null, repeat))
        body.append(defaults.root)

        val orientation = sectionBlock()
        texts.bind(orientation.heading) { it.orientationHeading }
        texts.bind(orientation.hint) { it.orientationHint }
        val horizontal = createRadioGroup().addTheme("horizontal")
        texts.bind(horizontal, { it.status }) { horizontal.label = it }
        horizontal.value = "pending"
        fun option(parent: io.github.shijianjs.ktjs.vaadin.radiogroup.RadioGroupElement, value: String, copy: (RadioGroupTexts) -> String) {
            val node = createRadioButton { this.value = value }
            texts.bind(node, copy) { node.textContent = it }
            parent.append(node)
        }
        option(horizontal, "pending") { it.pending }
        option(horizontal, "submitted") { it.submitted }
        option(horizontal, "confirmed") { it.confirmed }
        val vertical = createRadioGroup().addTheme("vertical")
        texts.bind(vertical, { it.travelClass }) { vertical.label = it }
        option(vertical, "economy") { it.economy }
        option(vertical, "business") { it.business }
        option(vertical, "firstClass") { it.firstClass }
        orientation.body.append(exampleBox(null, horizontal), exampleBox(null, vertical))
        body.append(orientation.root)

        val features = sectionBlock()
        texts.bind(features.heading) { it.featuresHeading }
        texts.bind(features.hint) { it.featuresHint }
        val labeled = createRadioGroup().addTheme("vertical")
        texts.bind(labeled, { it.label }) { labeled.label = it }
        texts.bind(labeled, { it.helperText }) { labeled.helperText = it }
        val tooltip = createTooltip().apply { setAttribute("slot", "tooltip") }
        texts.bind(tooltip, { it.tooltipText }) { tooltip.text = it }
        labeled.append(tooltip)
        option(labeled, "1") { it.item1 }
        option(labeled, "2") { it.item2 }
        option(labeled, "3") { it.item3 }
        features.body.append(exampleBox(null, labeled))
        body.append(features.root)

        val required = sectionBlock()
        texts.bind(required.heading) { it.requiredHeading }
        texts.bind(required.hint) { it.requiredHint }
        val payment = createRadioGroup()
        texts.bind(payment, { it.paymentMethod }) { payment.label = it }
        texts.bind(payment, { it.choosePayment }) { payment.errorMessage = it }
        payment.required = true
        option(payment, "card") { it.card }
        option(payment, "transfer") { it.bankTransfer }
        val readOnly = createRadioGroup().addTheme("vertical")
        texts.bind(readOnly, { it.readOnly }) { readOnly.label = it }
        readOnly.value = "yes"
        readOnly.readonly = true
        option(readOnly, "yes") { it.yes }
        option(readOnly, "no") { it.no }
        val disabled = createRadioGroup().addTheme("vertical")
        texts.bind(disabled, { it.disabled }) { disabled.label = it }
        disabled.value = "inProgress"
        disabled.disabled = true
        option(disabled, "inProgress") { it.inProgress }
        option(disabled, "done") { it.done }
        val validateButton = createButton()
        texts.bind(validateButton) { it.validate }
        validateButton.clickEvent.addHandler {
            validationResult = payment.validate()
            showValidation()
        }
        required.body.append(
            exampleBox(null, kitCol(payment, kitRow(validateButton, validationReadout.root))),
            exampleBox(null, kitRow(readOnly, disabled)),
        )
        body.append(required.root)

        val customOption = sectionBlock()
        texts.bind(customOption.heading) { it.customOptionHeading }
        texts.bind(customOption.hint) { it.customOptionHint }
        val cardGroup = createRadioGroup().addTheme("vertical")
        texts.bind(cardGroup, { it.paymentMethod }) { cardGroup.label = it }
        val visa = createRadioButton { value = "visa" }
        val visaBox = kitCol()
        visaBox.className = RADIO_OPTION
        val visaLine = HtmlTagName.span()
        texts.bind(visaLine, { it.visaLine }) { visaLine.textContent = it }
        val visaExpires = HtmlTagName.span()
        texts.bind(visaExpires, { it.expires }) { visaExpires.textContent = it }
        visaBox.append(visaLine, visaExpires)
        visaBox.setAttribute("slot", "label")
        visa.append(visaBox)
        val other = createRadioButton { value = "-1" }
        val otherBox = kitCol()
        otherBox.className = RADIO_OPTION
        val otherLine = HtmlTagName.span()
        texts.bind(otherLine, { it.other }) { otherLine.textContent = it }
        val otherHint = HtmlTagName.span()
        texts.bind(otherHint, { it.enterCardNumber }) { otherHint.textContent = it }
        otherBox.append(otherLine, otherHint)
        otherBox.setAttribute("slot", "label")
        other.append(otherBox)
        cardGroup.append(visa, other)
        val cardNumber = createTextField()
        texts.bind(cardNumber, { it.cardNumber }) { cardNumber.label = it }
        cardNumber.toggleAttribute("hidden", true)
        cardGroup.listen(RadioGroupEvents.valueChanged) {
            cardNumber.toggleAttribute("hidden", cardGroup.value != "-1")
        }
        customOption.body.append(exampleBox(null, kitCol(cardGroup, cardNumber)))
        body.append(customOption.root)

        val alternative = sectionBlock()
        texts.bind(alternative.heading) { it.alternativeHeading }
        texts.bind(alternative.hint) { it.alternativeHint }
        val replyGroup = createRadioGroup().addTheme("horizontal")
        texts.bind(replyGroup, { it.replyBehavior }) { replyGroup.label = it }
        option(replyGroup, "reply") { it.reply }
        option(replyGroup, "replyAll") { it.replyAll }
        replyGroup.value = "reply"
        val plainCheckbox = createCheckbox()
        texts.bind(plainCheckbox) { it.checkboxLabel }
        alternative.body.append(exampleBox(null, kitCol(plainCheckbox, replyGroup)))
        body.append(alternative.root)

        val presentation = sectionBlock()
        texts.bind(presentation.heading) { it.presentationHeading }
        texts.bind(presentation.hint) { it.presentationHint }
        val accounts = createRadioGroup().addTheme("vertical")
        texts.bind(accounts, { it.paymentMethod }) { accounts.label = it }
        accounts.value = "1"
        people(3).forEachIndexed { index, person ->
            val node = createRadioButton { value = "${person.id}" }
            val box = kitCol()
            box.className = RADIO_OPTION
            box.setAttribute("slot", "label")
            val accountLine = HtmlTagName.span()
            texts.bind(accountLine, { copy -> copy.account(person.id) }) { accountLine.textContent = it }
            val holderLine = HtmlTagName.span()
            texts.bind(holderLine, { copy -> copy.holder(person.displayName) }) { holderLine.textContent = it }
            val expiresLine = HtmlTagName.span()
            texts.bind(expiresLine, { it.expires }) { expiresLine.textContent = it }
            box.append(accountLine, holderLine, expiresLine)
            node.append(box)
            if (index == 0) node.checked = true
            accounts.append(node)
        }
        presentation.body.append(exampleBox(null, accounts))
        body.append(presentation.root)

        val styles = sectionBlock()
        texts.bind(styles.heading) { it.stylesHeading }
        texts.bind(styles.hint) { it.stylesHint }
        val styled = createRadioGroup().addTheme("helper-above-field", "vertical")
        texts.bind(styled, { it.label }) { styled.label = it }
        texts.bind(styled, { it.helperText }) { styled.helperText = it }
        listOf("Item 1", "Item 2", "Item 3").forEach { label ->
            styled.append(createRadioButton { value = label; textContent = label })
        }
        styles.body.append(exampleBox(null, styled))
        body.append(styles.root)

        applyTranslation(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = radioGroupEntry.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        showValue()
        showValidation()
    }

    companion object : CustomElementReference<RadioGroupPage>(RadioGroupPage::class.js) {
        val NAME: TagName<RadioGroupPage> = TagName("vaadin-demo-radio-group-page")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<vaadin-demo-radio-group-page>`. */
internal fun newRadioGroupPage(): RadioGroupPage {
    RadioGroupPage.define()
    return RadioGroupPage.NAME()
}
