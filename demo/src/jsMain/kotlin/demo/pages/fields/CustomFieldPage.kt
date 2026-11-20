package demo.pages.fields

import io.github.shijianjs.ktjs.demo.page.Blocks.row as kitRow
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.page.Blocks.col as kitCol
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.page.SectionBlock
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
import js.objects.unsafeJso
import kotlin.js.JsAny
import kotlin.js.js
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.customfield.CustomFieldElement
import io.github.shijianjs.ktjs.vaadin.customfield.CustomFieldEvents
import io.github.shijianjs.ktjs.vaadin.customfield.createCustomField
import io.github.shijianjs.ktjs.vaadin.datepicker.createDatePicker
import io.github.shijianjs.ktjs.vaadin.select.SelectItemData
import io.github.shijianjs.ktjs.vaadin.select.createSelect
import io.github.shijianjs.ktjs.vaadin.textfield.createTextField
import web.dom.clickEvent
import web.events.addHandler
import web.html.HTMLInputElement
import web.html.HtmlTagName

val customFieldEntry: CatalogEntry =
    CatalogEntry(
        id = "custom-field",
        group = Groups.FIELDS,
        meta =
            PageMeta(
                title = bi("Custom Field", "Custom Field"),
                summary =
                    bi(
                        "把多个输入组合成单个字段，用 formatValue / parseValue 在组合值与字符串之间转换。",
                        "Combines several inputs into one field, converting between the combined value and a " +
                            "string with formatValue / parseValue.",
                    ),
                docsUrl = "$DOCS_BASE/custom-field",
                sourcePath = "pages/fields/CustomFieldPage.kt",
            ),
        kitPage = { CustomFieldPage.NAME() },
    )

private val english: CustomFieldTexts =
    CustomFieldTexts(
        basicHeading = "Basic usage",
        basicHint = "custom-field collects the slotted inputs in order; formatValue joins the array of " +
            "input values into the field value and parseValue splits it back. The enrollment period " +
            "below combines two date pickers and reproduces the two validators the documentation adds " +
            "through Binder: the span cannot exceed 30 days and the start date must be earlier than the " +
            "end date.",
        periodLabel = "Enrollment period",
        periodHelper = "Cannot be longer than 30 days",
        startDate = "Start date",
        endDate = "End date",
        empty = "(empty)",
        valuePrefix = "Field value: ",
        valueEmpty = "Field value: (empty)",
        startBeforeEnd = "Start date must be earlier than end date",
        spanTooLong = "Dates cannot be more than 30 days apart",
        validate = "Validate",
        formatHeading = "Combined value and formatting",
        formatHint = "formatValue decides how value is assembled and parseValue splits an existing " +
            "value back into the inputs; both take or return the array of strings, while the field still " +
            "exposes a single value.",
        firstName = "First name",
        lastName = "Last name",
        fullName = "Full name",
        fullNameHelper = "Combined from two text inputs",
        nativeHeading = "Native input fields",
        nativeHint = "Any native input can be slotted: the field joins their values in order and the " +
            "browser still enforces pattern / required. The three inputs below need no Vaadin field " +
            "wrapper.",
        payment = "Payment information",
        cardholder = "Cardholder name",
        cardNumber = "Card number",
        securityCode = "Security code",
        paymentEmpty = "Payment information: (empty)",
        paymentPrefix = "Payment information: ",
        stylesHeading = "Styles",
        stylesHint = "small and helper-above-field apply to the whole combined field; the inner fields " +
            "need small as well so the sizes match.",
        price = "Price",
        priceHelper = "Helper text",
        amount = "Amount",
        currency = "Currency",
    )

private val chinese: CustomFieldTexts =
    CustomFieldTexts(
        basicHeading = "基本用法",
        basicHint = "custom-field 按顺序收集插槽里的输入组件，formatValue 把它们的值数组拼成一个字段值，" +
            "parseValue 再拆回去。下面的报名周期由两个 DatePicker 组成，页面复现文档里 Binder 的两条校验器：" +
            "跨度不超过 30 天、开始日期早于结束日期。",
        periodLabel = "报名周期",
        periodHelper = "跨度不能超过 30 天",
        startDate = "开始日期",
        endDate = "结束日期",
        empty = "（空）",
        valuePrefix = "字段值：",
        valueEmpty = "字段值：（空）",
        startBeforeEnd = "开始日期必须早于结束日期",
        spanTooLong = "日期跨度不能超过 30 天",
        validate = "校验",
        formatHeading = "组合值与格式化",
        formatHint = "formatValue 决定 value 的拼法，parseValue 负责把已有值拆回各个输入；两者都收 / 发字符串数组，" +
            "字段本身始终只暴露一个 value。",
        firstName = "名",
        lastName = "姓",
        fullName = "全名",
        fullNameHelper = "由两个文本输入组合而成",
        nativeHeading = "原生输入字段",
        nativeHint = "插槽里可以放任意原生输入：组件用 value 把它们按顺序拼起来，pattern / required 仍由浏览器校验。" +
            "下面的三个 input 不需要任何 Vaadin 字段包装。",
        payment = "付款信息",
        cardholder = "持卡人姓名",
        cardNumber = "卡号",
        securityCode = "安全码",
        paymentEmpty = "付款信息：（空）",
        paymentPrefix = "付款信息：",
        stylesHeading = "样式变体",
        stylesHint = "small 与 helper-above-field 作用在整个组合字段上；内部字段各自也要 small，尺寸才一致。",
        price = "价格",
        priceHelper = "说明文字",
        amount = "金额",
        currency = "币种",
    )

internal class CustomFieldPage : DemoPage() {
    private val texts = Binder(customFieldTexts)

    private val frame = PageFrame(customFieldEntry)

    private val period: CustomFieldElement = createCustomField()

    private val periodReadout = Readout("")

    /** The last verdict of the period's two validators; `null` while the value is incomplete. */
    private var periodError: ((CustomFieldTexts) -> String)? = null

    private val fullName: CustomFieldElement = createCustomField()

    private val nameReadout = Readout("")

    private val inputCount = Readout("inputs = 0")

    private val payment: CustomFieldElement = createCustomField()

    private val paymentReadout = Readout("")

    init {
        frame.mount(this)

        // --- Basic: enrollment period ---------------------------------------------------------------
        val basic = SectionBlock.NAME { show() }
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        period.required = true
        period.formatValue = { values -> joinWithTab(values) }
        period.parseValue = { text -> splitByTab(text) }
        texts.bind(period, { it.periodLabel }) { period.label = it }
        texts.bind(period, { it.periodHelper }) { period.helperText = it }
        period.dataset["test"] = "custom-field"
        val startDate = createDatePicker()
        texts.bind(startDate, { it.startDate }) { text ->
            startDate.accessibleName = text
            startDate.placeholder = text
        }
        val endDate = createDatePicker()
        texts.bind(endDate, { it.endDate }) { text ->
            endDate.accessibleName = text
            endDate.placeholder = text
        }
        period.append(startDate, HtmlTagName.span { textContent = "–" }, endDate)
        period.listen(CustomFieldEvents.valueChanged) { checkPeriod() }
        val periodValidate = createButton()
        texts.bind(periodValidate) { it.validate }
        periodValidate.clickEvent.addHandler { checkPeriod() }
        basic.body.append(exampleBox(null, kitCol(period, kitRow(periodValidate, periodReadout.root))))
        body.append(basic.root)

        // --- Formatting the combined value ----------------------------------------------------------
        val formatting = SectionBlock.NAME { show() }
        texts.bind(formatting.heading) { it.formatHeading }
        texts.bind(formatting.hint) { it.formatHint }
        val firstName = createTextField { placeholder = "Ada" }
        texts.bind(firstName, { it.firstName }) { firstName.label = it }
        val lastName = createTextField { placeholder = "Lovelace" }
        texts.bind(lastName, { it.lastName }) { lastName.label = it }
        fullName.formatValue = { values -> joinInputs(values) }
        fullName.parseValue = { text -> splitInputs(text) }
        texts.bind(fullName, { it.fullName }) { fullName.label = it }
        texts.bind(fullName, { it.fullNameHelper }) { fullName.helperText = it }
        fullName.dataset["test"] = "custom-field-format"
        fullName.append(firstName, lastName)
        fullName.listen(CustomFieldEvents.valueChanged) { showName() }
        formatting.body.append(exampleBox(null, kitCol(fullName, kitRow(nameReadout.root, inputCount.root))))
        body.append(formatting.root)

        // --- Native input fields --------------------------------------------------------------------
        val native = SectionBlock.NAME { show() }
        texts.bind(native.heading) { it.nativeHeading }
        texts.bind(native.hint) { it.nativeHint }
        texts.bind(payment, { it.payment }) { payment.label = it }
        payment.dataset["test"] = "custom-field-native"
        val cardholder = nativeInput("""[\p{L} \-]+""")
        texts.bind(cardholder, { it.cardholder }) { text -> cardholder.nativeText(text) }
        val cardNumber = nativeInput("""[\d ]{12,23}""")
        texts.bind(cardNumber, { it.cardNumber }) { text -> cardNumber.nativeText(text) }
        val securityCode = nativeInput("""[0-9]{3,4}""")
        texts.bind(securityCode, { it.securityCode }) { text -> securityCode.nativeText(text) }
        payment.append(kitRow(cardholder, cardNumber, securityCode))
        payment.listen(CustomFieldEvents.change) { showPayment() }
        native.body.append(exampleBox(null, kitCol(payment, paymentReadout.root)))
        body.append(native.root)

        // --- Styles ---------------------------------------------------------------------------------
        val styles = SectionBlock.NAME { show() }
        texts.bind(styles.heading) { it.stylesHeading }
        texts.bind(styles.hint) { it.stylesHint }
        val price = createCustomField().addTheme("small", "helper-above-field")
        texts.bind(price, { it.price }) { price.label = it }
        texts.bind(price, { it.priceHelper }) { price.helperText = it }
        price.dataset["test"] = "custom-field-styles"
        val amount = createTextField().addTheme("small")
        texts.bind(amount, { it.amount }) { amount.accessibleName = it }
        val currency =
            createSelect {
                items =
                    arrayOf(
                        unsafeJso<SelectItemData> { label = "AUD"; value = "aud" },
                        unsafeJso<SelectItemData> { label = "CAD"; value = "cad" },
                        unsafeJso<SelectItemData> { label = "CHF"; value = "chf" },
                        unsafeJso<SelectItemData> { label = "EUR"; value = "eur" },
                        unsafeJso<SelectItemData> { label = "GBP"; value = "gbp" },
                        unsafeJso<SelectItemData> { label = "JPY"; value = "jpy" },
                        unsafeJso<SelectItemData> { label = "USD"; value = "usd" },
                    )
                value = "usd"
            }.addTheme("small")
        texts.bind(currency, { it.currency }) { currency.accessibleName = it }
        currency.style.setProperty("width", "6em")
        price.append(kitRow(amount, currency))
        styles.body.append(exampleBox(null, price))
        body.append(styles.root)

        applyTranslation(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = customFieldEntry.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        checkPeriod()
        showName()
        showPayment()
    }

    /** Re-runs the period's validators for the current copy and rewrites its readout. */
    private fun checkPeriod() {
        // A custom field starts with an undefined value, so it is read through a guard.
        val value = period.value ?: ""
        val (from, to) = tabParts(value)
        val error: ((CustomFieldTexts) -> String)? =
            if (from.isEmpty() || to.isEmpty()) {
                null
            } else {
                val fromDay = isoDay(from)
                val toDay = isoDay(to)
                when {
                    fromDay.isNaN() || toDay.isNaN() -> null
                    toDay <= fromDay -> { copy -> copy.startBeforeEnd }
                    toDay - fromDay > 30 * DAY_MILLIS -> { copy -> copy.spanTooLong }
                    else -> null
                }
            }
        periodError = error
        val copy = texts.current
        period.errorMessage = error?.invoke(copy) ?: ""
        period.invalid = error != null
        periodReadout.set(
            copy.valuePrefix,
            value.ifEmpty { copy.empty }.replace('\t', ' '),
        )
    }

    private fun showName() {
        val copy = texts.current
        nameReadout.set(copy.valuePrefix, (fullName.value ?: "").ifEmpty { copy.empty })
        inputCount.set("inputs = ${fullName.inputs?.size ?: 0}")
    }

    private fun showPayment() {
        val copy = texts.current
        paymentReadout.set(copy.paymentPrefix, (payment.value ?: "").replace("\t", " · ").ifEmpty { copy.empty })
    }

    companion object : CustomElementReferenceAdapter<CustomFieldPage>(CustomFieldPage::class.js, "vaadin-demo-custom-field-page") {
        private val customFieldTexts: Translations<CustomFieldTexts> = texts(
            Langs.EN to english,
            Langs.ZH_HANS to chinese,
        )
        private const val DAY_MILLIS: Double = 86_400_000.0
        /** Epoch milliseconds of an ISO `yyyy-MM-dd` string; `NaN` when the text is not a date. */
        private fun isoDay(text: String): Double =
            js("(text) => Date.parse(text + 'T00:00:00Z')")(text) as Double
        /** Splits a custom-field value into its first two tab-separated parts. */
        private fun tabParts(value: String): Pair<String, String> {
            val index = value.indexOf('\t')
            return if (index < 0) value to "" else value.substring(0, index) to value.substring(index + 1)
        }
        /** Joins the input value array with tabs, the separator the date range format uses. */
        private fun joinWithTab(values: Array<JsAny?>): String =
            js("(values) => values.join('\\t')")(values) as String
        /** Splits a tab-separated field value back into per-input values. */
        private fun splitByTab(text: String): Array<JsAny?> =
            (js("(text) => String(text || '').split('\\t')")(text) as Array<JsAny?>)
        /** Joins the input value array the way the custom-field demo expects. */
        private fun joinInputs(values: Array<JsAny?>): String =
            js("(values) => values.filter((value) => value != null && value !== '').join(' ')")(values) as String
        /** Splits a full name into the per-input values. */
        private fun splitInputs(text: String): Array<JsAny?> =
            js("(text) => text.trim().split(/\\s+/)").let { split ->
                @Suppress("UNCHECKED_CAST")
                (split(text) as Array<JsAny?>)
            }
        /** Creates a bare `<input>` with the pattern the native-input example relies on. */
        private fun nativeInput(pattern: String): HTMLInputElement =
            HtmlTagName.input().apply {
                setAttribute("type", "text")
                setAttribute("pattern", pattern)
                required = true
                style.setProperty("width", "9rem")
            }
    }
}

/** A bare input announces itself twice: the placeholder and the label screen readers hear. */
private fun HTMLInputElement.nativeText(text: String) {
    setAttribute("placeholder", text)
    setAttribute("aria-label", text)
}
