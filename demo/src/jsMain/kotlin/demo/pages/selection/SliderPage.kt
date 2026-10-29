package demo.pages.selection

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.tr
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.Readout
import demo.ui.column
import demo.ui.example
import demo.ui.joinValues
import demo.ui.listen
import demo.ui.page
import demo.ui.row
import demo.ui.section
import vaadin.slider.RangeSliderEvents
import vaadin.slider.SliderEvents
import vaadin.slider.createRangeSlider
import vaadin.slider.createSlider

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
        builder = ::buildSliderPage,
    )

private fun buildSliderPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "拖动时触发 input，松开后触发 change；value 是 Double。",
                "Dragging fires input and releasing fires change; value is a Double.",
            ),
        )
    val slider =
        createSlider {
            label = tr("音量", "Volume")
            min = 0.0
            max = 100.0
            step = 5.0
            value = 40.0
        }
    slider.dataset["test"] = "slider-basic"
    val readout = Readout("value = 40")
    slider.listen(SliderEvents.input) { readout.set(tr("数值 = ", "Value = "), slider.value.toString()) }
    slider.listen(SliderEvents.change) {
        readout.set(tr("已提交数值 = ", "Committed value = "), slider.value.toString())
    }
    basic.body.append(example(null, column(slider, readout.root)))
    view.body.append(basic.root)

    val features =
        section(
            tr("辅助信息", "Helper text"),
            tr(
                "helper-text 用来解释滑块的单位或含义。",
                "helper-text explains the unit or the meaning of the slider.",
            ),
        )
    features.body.append(
        example(
            null,
            createSlider {
                label = tr("标签", "Label")
                helperText = tr("辅助信息", "Helper text")
            },
        ),
    )
    view.body.append(features.root)

    val minMax =
        section(
            tr("最小值与最大值标签", "Min & max labels"),
            tr(
                "min-max-visible 在轨道两端显示取值范围。",
                "min-max-visible shows the range at both ends of the track.",
            ),
        )
    minMax.body.append(
        example(
            null,
            createSlider {
                label = tr("温度", "Temperature")
                min = 0.0
                max = 100.0
                value = 50.0
                minMaxVisible = true
            },
        ),
    )
    view.body.append(minMax.root)

    val alwaysVisible =
        section(
            tr("常显数值", "Persistent value display"),
            tr(
                "value-always-visible 让数值气泡常驻，不必等用户拖动才显示。",
                "value-always-visible keeps the value bubble on screen instead of only while dragging.",
            ),
        )
    alwaysVisible.body.append(
        example(
            null,
            createSlider {
                label = tr("亮度", "Brightness")
                min = 0.0
                max = 100.0
                value = 75.0
                valueAlwaysVisible = true
            },
        ),
    )
    view.body.append(alwaysVisible.root)

    val stepSection =
        section(
            tr("步进", "Step"),
            tr(
                "step 决定键盘与拖动的增量，也可以是非整数。",
                "step sets the increment of both keyboard and pointer input; fractions are allowed.",
            ),
        )
    val stepped =
        createSlider {
            label = tr("音量", "Volume")
            min = 0.0
            max = 10.0
            value = 5.0
            step = 0.5
            valueAlwaysVisible = true
        }
    val stepReadout = Readout("value = 5.0")
    stepped.listen(SliderEvents.valueChanged) { stepReadout.set("value = ", stepped.value.toString()) }
    stepSection.body.append(example(null, column(stepped, stepReadout.root)))
    view.body.append(stepSection.root)

    val range =
        section(
            tr("区间滑块", "Range slider"),
            tr(
                "range-slider 的 value 是两元素数组，两个手柄分别控制下限与上限；" +
                    "accessible-name-start / accessible-name-end 给两个手柄各自的读屏名称。",
                "range-slider takes a two-element value; the handles control the lower and the upper bound, " +
                    "and accessible-name-start / accessible-name-end name them for screen readers.",
            ),
        )
    val rangeSlider =
        createRangeSlider {
            label = tr("价格区间", "Price range")
            min = 0.0
            max = 1000.0
            step = 50.0
            value = arrayOf(200.0, 800.0)
            accessibleNameStart = tr("最低价格", "Minimum price")
            accessibleNameEnd = tr("最高价格", "Maximum price")
        }
    val rangeReadout = Readout("value = 200 - 800")
    rangeSlider.listen(RangeSliderEvents.valueChanged) {
        rangeReadout.set(tr("区间 = ", "Range = "), joinValues(rangeSlider.value))
    }
    range.body.append(example(null, column(rangeSlider, rangeReadout.root)))
    view.body.append(range.root)

    val rangeValidation =
        section(
            tr("区间校验", "Range validation"),
            tr(
                "区间跨度不满足业务规则时，把 invalid 置真并给出 error-message。",
                "When the span breaks the business rule, set invalid and provide an error-message.",
            ),
        )
    val priceRange =
        createRangeSlider {
            label = tr("价格区间", "Price Range")
            min = 0.0
            max = 1000.0
            step = 50.0
            value = arrayOf(200.0, 800.0)
        }
    priceRange.listen(RangeSliderEvents.change) {
        val start = priceRange.value[0]
        val end = priceRange.value[1]
        if (end - start < 200.0) {
            priceRange.invalid = true
            priceRange.errorMessage = tr("价格区间至少 200", "Price range must span at least $200")
        } else {
            priceRange.invalid = false
        }
    }
    rangeValidation.body.append(example(null, priceRange))
    view.body.append(rangeValidation.root)

    val valueModes =
        section(
            tr("值变更模式", "Value change modes"),
            tr(
                "Web Component 只有 input（拖动中）与 change（提交）两个时机；" +
                    "Flow 的 eager / lazy 模式属于服务端包装，不在本页范围。",
                "The web component only has input (while dragging) and change (committed); the Flow " +
                    "eager / lazy modes belong to the server-side wrapper and are out of scope here.",
            ),
        )
    val modeSlider =
        createSlider {
            label = tr("评分", "Rating")
            min = 0.0
            max = 10.0
            step = 1.0
            value = 7.0
        }
    val liveValue = Readout(tr("拖动中：7", "Live: 7"))
    val committedValue = Readout(tr("已提交：7", "Committed: 7"))
    modeSlider.listen(SliderEvents.input) { liveValue.set(tr("拖动中：", "Live: "), modeSlider.value.toString()) }
    modeSlider.listen(SliderEvents.change) {
        committedValue.set(tr("已提交：", "Committed: "), modeSlider.value.toString())
    }
    valueModes.body.append(example(null, column(modeSlider, row(liveValue.root, committedValue.root))))
    view.body.append(valueModes.root)

    val states = section(tr("只读与禁用", "Read-only and disabled"))
    states.body.append(
        example(
            null,
            row(
                createSlider {
                    label = tr("只读", "Read-only")
                    value = 60.0
                    readonly = true
                },
                createSlider {
                    label = tr("禁用", "Disabled")
                    value = 60.0
                    disabled = true
                },
            ),
        ),
    )
    view.body.append(states.root)

    return view
}
