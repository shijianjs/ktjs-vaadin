package demo.pages.selection

import io.github.shijianjs.ktjs.demo.page.Blocks.col as kitCol
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.page.Blocks.row as kitRow
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.ui.PageFrame
import demo.ui.PageMeta
import demo.ui.Readout
import demo.ui.joinValues
import demo.ui.listen
import io.github.shijianjs.ktjs.vaadin.slider.RangeSliderElement
import io.github.shijianjs.ktjs.vaadin.slider.RangeSliderEvents
import io.github.shijianjs.ktjs.vaadin.slider.SliderElement
import io.github.shijianjs.ktjs.vaadin.slider.SliderEvents
import io.github.shijianjs.ktjs.vaadin.slider.createRangeSlider
import io.github.shijianjs.ktjs.vaadin.slider.createSlider

val sliderEntry: CatalogEntry =
    CatalogEntry(
        id = "slider",
        group = Groups.SELECTION,
        meta =
            PageMeta(
                title = bi("Slider", "Slider"),
                summary =
                    bi(
                        "拖动选择数值，支持区间滑块、步进与只读状态。",
                        "Pick a value by dragging, with range slider, step and read-only state.",
                    ),
                docsUrl = "$DOCS_BASE/slider",
                sourcePath = "pages/selection/SliderPage.kt",
            ),
        kitPage = { SliderPage.NAME() },
    )

private val english: SliderTexts =
    SliderTexts(
        basicHeading = "Basic usage",
        basicHint = "Dragging fires input and releasing fires change; value is a Double.",
        volume = "Volume",
        valuePrefix = "Value = ",
        committedPrefix = "Committed value = ",
        helperHeading = "Helper text",
        helperHint = "helper-text explains the unit or the meaning of the slider.",
        label = "Label",
        helperText = "Helper text",
        minMaxHeading = "Min & max labels",
        minMaxHint = "min-max-visible shows the range at both ends of the track.",
        temperature = "Temperature",
        visibleHeading = "Persistent value display",
        visibleHint = "value-always-visible keeps the value bubble on screen instead of only while dragging.",
        brightness = "Brightness",
        stepHeading = "Step",
        stepHint = "step sets the increment of both keyboard and pointer input; fractions are allowed.",
        rangeHeading = "Range slider",
        rangeHint = "range-slider takes a two-element value; the handles control the lower and the " +
            "upper bound, and accessible-name-start / accessible-name-end name them for screen readers.",
        priceRange = "Price range",
        minPrice = "Minimum price",
        maxPrice = "Maximum price",
        rangePrefix = "Range = ",
        rangeValidationHeading = "Range validation",
        rangeValidationHint = "When the span breaks the business rule, set invalid and provide an error-message.",
        priceRangeLabel = "Price Range",
        priceError = "Price range must span at least \$200",
        modesHeading = "Value change modes",
        modesHint = "The web component only has input (while dragging) and change (committed); the Flow " +
            "eager / lazy modes belong to the server-side wrapper and are out of scope here.",
        rating = "Rating",
        livePrefix = "Live: ",
        committedValuePrefix = "Committed: ",
        statesHeading = "Read-only and disabled",
        readonly = "Read-only",
        disabled = "Disabled",
    )

private val chinese: SliderTexts =
    SliderTexts(
        basicHeading = "基本用法",
        basicHint = "拖动时触发 input，松开后触发 change；value 是 Double。",
        volume = "音量",
        valuePrefix = "数值 = ",
        committedPrefix = "已提交数值 = ",
        helperHeading = "辅助信息",
        helperHint = "helper-text 用来解释滑块的单位或含义。",
        label = "标签",
        helperText = "辅助信息",
        minMaxHeading = "最小值与最大值标签",
        minMaxHint = "min-max-visible 在轨道两端显示取值范围。",
        temperature = "温度",
        visibleHeading = "常显数值",
        visibleHint = "value-always-visible 让数值气泡常驻，不必等用户拖动才显示。",
        brightness = "亮度",
        stepHeading = "步进",
        stepHint = "step 决定键盘与拖动的增量，也可以是非整数。",
        rangeHeading = "区间滑块",
        rangeHint = "range-slider 的 value 是两元素数组，两个手柄分别控制下限与上限；" +
            "accessible-name-start / accessible-name-end 给两个手柄各自的读屏名称。",
        priceRange = "价格区间",
        minPrice = "最低价格",
        maxPrice = "最高价格",
        rangePrefix = "区间 = ",
        rangeValidationHeading = "区间校验",
        rangeValidationHint = "区间跨度不满足业务规则时，把 invalid 置真并给出 error-message。",
        priceRangeLabel = "价格区间",
        priceError = "价格区间至少 200",
        modesHeading = "值变更模式",
        modesHint = "Web Component 只有 input（拖动中）与 change（提交）两个时机；" +
            "Flow 的 eager / lazy 模式属于服务端包装，不在本页范围。",
        rating = "评分",
        livePrefix = "拖动中：",
        committedValuePrefix = "已提交：",
        statesHeading = "只读与禁用",
        readonly = "只读",
        disabled = "禁用",
    )

internal class SliderPage : DemoPage() {
    private val texts = Binder(sliderTexts)

    private val frame = PageFrame(sliderEntry)

    private val slider: SliderElement = createSlider { min = 0.0; max = 100.0; step = 5.0; value = 40.0 }

    private val readout = Readout("value = 40")

    /** Which line the basic readout last showed, so a switch can rewrite it in place. */
    private var basicLine: ((SliderTexts, String) -> Pair<String, String>)? = null

    private val stepped: SliderElement = createSlider { min = 0.0; max = 10.0; value = 5.0; step = 0.5; valueAlwaysVisible = true }

    private val stepReadout = Readout("value = 5.0")

    private val rangeSlider: RangeSliderElement =
        createRangeSlider {
            min = 0.0
            max = 1000.0
            step = 50.0
            value = arrayOf(200.0, 800.0)
        }

    private val rangeReadout = Readout("value = 200 - 800")

    private val priceRange: RangeSliderElement =
        createRangeSlider {
            min = 0.0
            max = 1000.0
            step = 50.0
            value = arrayOf(200.0, 800.0)
        }

    private var priceInvalid = false

    private val modeSlider: SliderElement = createSlider { min = 0.0; max = 10.0; step = 1.0; value = 7.0 }

    private val liveValue = Readout("")

    private val committedValue = Readout("")

    private var committedText: String? = null

    init {
        frame.mount(this)

        val basic = SectionBlock.NAME { show() }
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        texts.bind(slider, { it.volume }) { slider.label = it }
        slider.dataset["test"] = "slider-basic"
        slider.listen(SliderEvents.input) {
            basicLine = { copy, value -> copy.valuePrefix to value }
            showBasic()
        }
        slider.listen(SliderEvents.change) {
            basicLine = { copy, value -> copy.committedPrefix to value }
            showBasic()
        }
        basic.body.append(exampleBox(null, kitCol(slider, readout.root)))
        body.append(basic.root)

        val features = SectionBlock.NAME { show() }
        texts.bind(features.heading) { it.helperHeading }
        texts.bind(features.hint) { it.helperHint }
        val helperSlider = createSlider()
        texts.bind(helperSlider, { it.label }) { helperSlider.label = it }
        texts.bind(helperSlider, { it.helperText }) { helperSlider.helperText = it }
        features.body.append(exampleBox(null, helperSlider))
        body.append(features.root)

        val minMax = SectionBlock.NAME { show() }
        texts.bind(minMax.heading) { it.minMaxHeading }
        texts.bind(minMax.hint) { it.minMaxHint }
        val temperature =
            createSlider {
                min = 0.0
                max = 100.0
                value = 50.0
                minMaxVisible = true
            }
        texts.bind(temperature, { it.temperature }) { temperature.label = it }
        minMax.body.append(exampleBox(null, temperature))
        body.append(minMax.root)

        val alwaysVisible = SectionBlock.NAME { show() }
        texts.bind(alwaysVisible.heading) { it.visibleHeading }
        texts.bind(alwaysVisible.hint) { it.visibleHint }
        val brightness =
            createSlider {
                min = 0.0
                max = 100.0
                value = 75.0
                valueAlwaysVisible = true
            }
        texts.bind(brightness, { it.brightness }) { brightness.label = it }
        alwaysVisible.body.append(exampleBox(null, brightness))
        body.append(alwaysVisible.root)

        val stepSection = SectionBlock.NAME { show() }
        texts.bind(stepSection.heading) { it.stepHeading }
        texts.bind(stepSection.hint) { it.stepHint }
        texts.bind(stepped, { it.volume }) { stepped.label = it }
        stepped.listen(SliderEvents.valueChanged) { showStep() }
        stepSection.body.append(exampleBox(null, kitCol(stepped, stepReadout.root)))
        body.append(stepSection.root)

        val range = SectionBlock.NAME { show() }
        texts.bind(range.heading) { it.rangeHeading }
        texts.bind(range.hint) { it.rangeHint }
        texts.bind(rangeSlider, { it.priceRange }) { rangeSlider.label = it }
        texts.bind(rangeSlider, { it.minPrice }) { rangeSlider.accessibleNameStart = it }
        texts.bind(rangeSlider, { it.maxPrice }) { rangeSlider.accessibleNameEnd = it }
        rangeSlider.listen(RangeSliderEvents.valueChanged) { showRange() }
        range.body.append(exampleBox(null, kitCol(rangeSlider, rangeReadout.root)))
        body.append(range.root)

        val rangeValidation = SectionBlock.NAME { show() }
        texts.bind(rangeValidation.heading) { it.rangeValidationHeading }
        texts.bind(rangeValidation.hint) { it.rangeValidationHint }
        texts.bind(priceRange, { it.priceRangeLabel }) { priceRange.label = it }
        priceRange.listen(RangeSliderEvents.change) {
            val start = priceRange.value[0]
            val end = priceRange.value[1]
            priceInvalid = end - start < 200.0
            priceRange.invalid = priceInvalid
            priceRange.errorMessage = if (priceInvalid) texts.current.priceError else ""
        }
        rangeValidation.body.append(exampleBox(null, priceRange))
        body.append(rangeValidation.root)

        val valueModes = SectionBlock.NAME { show() }
        texts.bind(valueModes.heading) { it.modesHeading }
        texts.bind(valueModes.hint) { it.modesHint }
        texts.bind(modeSlider, { it.rating }) { modeSlider.label = it }
        modeSlider.listen(SliderEvents.input) {
            liveValue.set(texts.current.livePrefix, modeSlider.value.toString())
        }
        modeSlider.listen(SliderEvents.change) {
            committedText = modeSlider.value.toString()
            showModes()
        }

        valueModes.body.append(exampleBox(null, kitCol(modeSlider, kitRow(liveValue.root, committedValue.root))))
        body.append(valueModes.root)

        val states = SectionBlock.NAME { show() }
        texts.bind(states.heading) { it.statesHeading }
        val readonlySlider =
            createSlider {
                value = 60.0
                readonly = true
            }
        texts.bind(readonlySlider, { it.readonly }) { readonlySlider.label = it }
        val disabledSlider =
            createSlider {
                value = 60.0
                disabled = true
            }
        texts.bind(disabledSlider, { it.disabled }) { disabledSlider.label = it }
        states.body.append(exampleBox(null, kitRow(readonlySlider, disabledSlider)))
        body.append(states.root)

        applyTranslation(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = sliderEntry.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        showBasic()
        showStep()
        showRange()
        showModes()
        val copy = texts.current
        priceRange.errorMessage = if (priceInvalid) copy.priceError else ""
    }

    private fun showBasic() {
        val line = basicLine ?: return
        val (prefix, value) = line(texts.current, slider.value.toString())
        readout.set(prefix, value)
    }

    private fun showStep() {
        stepReadout.set("value = ", stepped.value.toString())
    }

    private fun showRange() {
        rangeReadout.set(texts.current.rangePrefix, joinValues(rangeSlider.value))
    }

    private fun showModes() {
        val copy = texts.current
        liveValue.set(copy.livePrefix, modeSlider.value.toString())
        val committed = committedText
        if (committed == null) {
            committedValue.set(copy.committedValuePrefix, modeSlider.value.toString())
        } else {
            committedValue.set(copy.committedValuePrefix, committed)
        }
    }

    companion object : CustomElementReferenceAdapter<SliderPage>(SliderPage::class.js, "vaadin-demo-slider-page") {
        private val sliderTexts: Translations<SliderTexts> = texts(
            Langs.EN to english,
            Langs.ZH_HANS to chinese,
        )
    }
}
