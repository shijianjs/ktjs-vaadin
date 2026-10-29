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
import demo.ui.element
import demo.ui.example
import demo.ui.listen
import demo.ui.page
import demo.ui.row
import demo.ui.section
import demo.ui.span
import demo.ui.withTheme
import js.objects.unsafeJso
import kotlin.js.JsAny
import kotlin.js.js
import vaadin.button.createButton
import vaadin.customfield.CustomFieldEvents
import vaadin.customfield.createCustomField
import vaadin.datepicker.createDatePicker
import vaadin.select.SelectItemData
import vaadin.select.createSelect
import vaadin.textfield.createTextField
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
        builder = ::buildCustomFieldPage,
    )

private fun buildCustomFieldPage(meta: PageMeta): PageView {
    val view = page(meta)

    // --- Basic: enrollment period ---------------------------------------------------------------
    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "custom-field 按顺序收集插槽里的输入组件，formatValue 把它们的值数组拼成一个字段值，" +
                    "parseValue 再拆回去。下面的报名周期由两个 DatePicker 组成，页面复现文档里 Binder 的两条校验器：" +
                    "跨度不超过 30 天、开始日期早于结束日期。",
                "custom-field collects the slotted inputs in order; formatValue joins the array of input values " +
                    "into the field value and parseValue splits it back. The enrollment period below combines two " +
                    "date pickers and reproduces the two validators the documentation adds through Binder: the " +
                    "span cannot exceed 30 days and the start date must be earlier than the end date.",
            ),
        )
    val period =
        createCustomField {
            label = tr("报名周期", "Enrollment period")
            helperText = tr("跨度不能超过 30 天", "Cannot be longer than 30 days")
            required = true
            formatValue = { values -> joinWithTab(values) }
            parseValue = { text -> splitByTab(text) }
        }
    period.dataset["test"] = "custom-field"
    val startDate =
        createDatePicker {
            accessibleName = tr("开始日期", "Start date")
            placeholder = tr("开始日期", "Start date")
        }
    val endDate =
        createDatePicker {
            accessibleName = tr("结束日期", "End date")
            placeholder = tr("结束日期", "End date")
        }
    period.append(startDate, span("–"), endDate)

    val periodReadout = Readout(tr("字段值：（空）", "Field value: (empty)"))

    fun checkPeriod() {
        val (from, to) = tabParts(period.value)
        val message =
            when {
                from.isEmpty() || to.isEmpty() -> ""
                else -> {
                    val fromDay = isoDay(from)
                    val toDay = isoDay(to)
                    when {
                        fromDay.isNaN() || toDay.isNaN() -> ""
                        toDay <= fromDay ->
                            tr("开始日期必须早于结束日期", "Start date must be earlier than end date")
                        toDay - fromDay > 30 * DAY_MILLIS ->
                            tr("日期跨度不能超过 30 天", "Dates cannot be more than 30 days apart")
                        else -> ""
                    }
                }
            }
        period.errorMessage = message
        period.invalid = message.isNotEmpty()
        periodReadout.set(tr("字段值：", "Field value: "), if (period.value.isEmpty()) tr("（空）", "(empty)") else period.value.replace('\t', ' '))
    }
    period.listen(CustomFieldEvents.valueChanged) { checkPeriod() }
    val periodValidate =
        createButton { textContent = tr("校验", "Validate") }.apply {
            clickEvent.addHandler { checkPeriod() }
        }
    basic.body.append(example(null, column(period, row(periodValidate, periodReadout.root))))
    view.body.append(basic.root)

    // --- Formatting the combined value ----------------------------------------------------------
    val formatting =
        section(
            tr("组合值与格式化", "Combined value and formatting"),
            tr(
                "formatValue 决定 value 的拼法，parseValue 负责把已有值拆回各个输入；" +
                    "两者都收 / 发字符串数组，字段本身始终只暴露一个 value。",
                "formatValue decides how value is assembled and parseValue splits an existing value back into " +
                    "the inputs; both take or return the array of strings, while the field still exposes a " +
                    "single value.",
            ),
        )
    val firstName =
        createTextField {
            label = tr("名", "First name")
            placeholder = "Ada"
        }
    val lastName =
        createTextField {
            label = tr("姓", "Last name")
            placeholder = "Lovelace"
        }
    val fullName =
        createCustomField {
            label = tr("全名", "Full name")
            helperText = tr("由两个文本输入组合而成", "Combined from two text inputs")
            formatValue = { values -> joinInputs(values) }
            parseValue = { text -> splitInputs(text) }
        }
    fullName.dataset["test"] = "custom-field-format"
    fullName.append(firstName, lastName)
    val nameReadout = Readout(tr("字段值：（空）", "Field value: (empty)"))
    val inputCount = Readout("inputs = 0")
    fullName.listen(CustomFieldEvents.valueChanged) {
        nameReadout.set(tr("字段值：", "Field value: "), fullName.value.ifEmpty { tr("（空）", "(empty)") })
        inputCount.set("inputs = ${fullName.inputs?.size ?: 0}")
    }
    formatting.body.append(example(null, column(fullName, row(nameReadout.root, inputCount.root))))
    view.body.append(formatting.root)

    // --- Native input fields --------------------------------------------------------------------
    val native =
        section(
            tr("原生输入字段", "Native input fields"),
            tr(
                "插槽里可以放任意原生输入：组件用 value 把它们按顺序拼起来，pattern / required 仍由浏览器校验。" +
                    "下面的三个 input 不需要任何 Vaadin 字段包装。",
                "Any native input can be slotted: the field joins their values in order and the browser still " +
                    "enforces pattern / required. The three inputs below need no Vaadin field wrapper.",
            ),
        )
    val payment =
        createCustomField {
            label = tr("付款信息", "Payment information")
        }
    payment.dataset["test"] = "custom-field-native"
    val cardholder = nativeInput(tr("持卡人姓名", "Cardholder name"), """[\p{L} \-]+""")
    val cardNumber = nativeInput(tr("卡号", "Card number"), """[\d ]{12,23}""")
    val securityCode = nativeInput(tr("安全码", "Security code"), """[0-9]{3,4}""")
    payment.append(row(cardholder, cardNumber, securityCode))
    val paymentReadout = Readout(tr("付款信息：（空）", "Payment information: (empty)"))
    payment.listen(CustomFieldEvents.change) {
        paymentReadout.set(
            tr("付款信息：", "Payment information: "),
            payment.value.replace("\t", " · ").ifEmpty { tr("（空）", "(empty)") },
        )
    }
    native.body.append(example(null, column(payment, paymentReadout.root)))
    view.body.append(native.root)

    // --- Styles ---------------------------------------------------------------------------------
    val styles =
        section(
            tr("样式变体", "Styles"),
            tr(
                "small 与 helper-above-field 作用在整个组合字段上；内部字段各自也要 small，尺寸才一致。",
                "small and helper-above-field apply to the whole combined field; the inner fields need small " +
                    "as well so the sizes match.",
            ),
        )
    val price =
        createCustomField {
            label = tr("价格", "Price")
            helperText = tr("说明文字", "Helper text")
        }.withTheme("small", "helper-above-field")
    price.dataset["test"] = "custom-field-styles"
    val amount =
        createTextField {
            accessibleName = tr("金额", "Amount")
        }.withTheme("small")
    val currency =
        createSelect {
            accessibleName = tr("币种", "Currency")
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
        }.withTheme("small")
    currency.style.setProperty("width", "6em")
    price.append(row(amount, currency))
    styles.body.append(example(null, price))
    view.body.append(styles.root)

    return view
}

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

/** Creates a bare `<input>` with the attributes the native-input example relies on. */
private fun nativeInput(placeholder: String, pattern: String): HTMLInputElement =
    element(HtmlTagName.input).apply {
        setAttribute("type", "text")
        setAttribute("aria-label", placeholder)
        setAttribute("placeholder", placeholder)
        setAttribute("pattern", pattern)
        required = true
        style.setProperty("width", "9rem")
    }
