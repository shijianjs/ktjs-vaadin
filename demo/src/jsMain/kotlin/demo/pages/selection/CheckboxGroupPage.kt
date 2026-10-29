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
import demo.ui.withTheme
import vaadin.button.createButton
import vaadin.checkbox.createCheckbox
import vaadin.checkboxgroup.CheckboxGroupEvents
import vaadin.checkboxgroup.createCheckboxGroup
import vaadin.tooltip.createTooltip
import web.dom.clickEvent
import web.events.addHandler

val checkboxGroupEntry: CatalogEntry =
    CatalogEntry(
        id = "checkbox-group",
        group = Groups.SELECTION,
        meta =
            PageMeta(
                title = bi("Checkbox Group", "Checkbox Group"),
                summary =
                    bi(
                        "多选组：value 是数组，支持必填校验与整体只读。",
                        "Multi-select group: value is an array, with required validation and a " +
                            "group-wide read-only state.",
                    ),
                docsUrl = "$DOCS_BASE/checkbox",
                docsNote =
                    bi(
                        "Checkbox Group 与 Checkbox 共用一页官方文档。",
                        "Checkbox Group shares the Checkbox documentation page.",
                    ),
                sourcePath = "pages/selection/CheckboxGroupPage.kt",
            ),
        builder = ::buildCheckboxGroupPage,
    )

private fun buildCheckboxGroupPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "checkbox-group 的 value 是数组，子项通过 value 属性区分。",
                "The value of a checkbox-group is an array; the items are told apart by their value " +
                    "property.",
            ),
        )
    val group =
        createCheckboxGroup {
            label = tr("使用的语言", "Languages used")
            helperText = tr("可以多选", "Multiple answers are allowed")
        }.withTheme("vertical")
    group.dataset["test"] = "checkbox-group"
    group.append(
        createCheckbox {
            value = "kotlin"
            textContent = "Kotlin"
        },
        createCheckbox {
            value = "java"
            textContent = "Java"
        },
        createCheckbox {
            value = "js"
            textContent = "JavaScript"
        },
    )
    val readout = Readout("value = []")
    group.listen(CheckboxGroupEvents.valueChanged) {
        readout.set("value = ", joinValues(group.value).ifEmpty { "[]" })
    }
    basic.body.append(example(null, column(group, readout.root)))
    view.body.append(basic.root)

    val preset =
        section(
            tr("预设值与校验", "Preset values and validation"),
            tr(
                "value 可以预先赋值；required 在空选择时让 validate() 失败。",
                "value can be preset; required makes validate() fail on an empty selection.",
            ),
        )
    val skills =
        createCheckboxGroup {
            label = tr("技能（必填）", "Skills (required)")
            required = true
            errorMessage = tr("至少选择一项", "Select at least one")
        }
    skills.value = arrayOf("css", "html")
    skills.append(
        createCheckbox {
            value = "css"
            textContent = "CSS"
        },
        createCheckbox {
            value = "html"
            textContent = "HTML"
        },
        createCheckbox {
            value = "kotlin"
            textContent = "Kotlin"
        },
    )
    val result = Readout("value = css, html")
    val validateButton =
        createButton { textContent = tr("校验", "Validate") }.apply {
            clickEvent.addHandler {
                result.set(tr("校验：", "Validation: "), if (skills.validate()) tr("通过", "passed") else tr("失败", "failed"))
            }
        }
    skills.listen(CheckboxGroupEvents.valueChanged) {
        result.set(tr("已选：", "Selected: "), joinValues(skills.value).ifEmpty { "[]" })
    }
    preset.body.append(example(null, column(skills, row(validateButton, result.root))))
    view.body.append(preset.root)

    val features =
        section(
            tr("标签、辅助信息与提示", "Label, helper text and tooltip"),
            tr(
                "label 与 helper-text 直接写在组上；tooltip 通过 slot=\"tooltip\" 挂到字段上。",
                "label and helper-text live on the group; a tooltip is attached to the field through " +
                    "slot=\"tooltip\".",
            ),
        )
    val languages =
        createCheckboxGroup {
            label = tr("标签", "Label")
            helperText = tr("辅助信息", "Helper text")
        }.withTheme("vertical")
    languages.append(
        createTooltip { text = tr("提示文本", "Tooltip text") }.apply { setAttribute("slot", "tooltip") },
        createCheckbox {
            value = "1"
            textContent = tr("选项 1", "Item 1")
        },
        createCheckbox {
            value = "2"
            textContent = tr("选项 2", "Item 2")
        },
        createCheckbox {
            value = "3"
            textContent = tr("选项 3", "Item 3")
        },
    )
    features.body.append(
        example(
            null,
            column(
                createCheckbox {
                    label = tr("标签", "Label")
                    helperText = tr("辅助信息", "Helper text")
                },
                languages,
            ),
        ),
    )
    view.body.append(features.root)

    val states = section(tr("只读与禁用", "Read-only and disabled"))
    val readonlyGroup =
        createCheckboxGroup { label = tr("只读", "Read-only") }.withTheme("vertical")
    readonlyGroup.readonly = true
    readonlyGroup.value = arrayOf("a")
    readonlyGroup.append(
        createCheckbox {
            value = "a"
            textContent = tr("选项 A", "Option A")
        },
        createCheckbox {
            value = "b"
            textContent = tr("选项 B", "Option B")
        },
    )
    val disabledGroup =
        createCheckboxGroup { label = tr("禁用", "Disabled") }.withTheme("vertical")
    disabledGroup.disabled = true
    disabledGroup.append(
        *listOf(
            tr("工程", "Engineering"),
            tr("人力资源", "Human Resources"),
            tr("市场", "Marketing"),
        ).map { name ->
            createCheckbox {
                value = name
                textContent = name
            }
        }.toTypedArray(),
    )
    states.body.append(example(null, row(readonlyGroup, disabledGroup)))
    view.body.append(states.root)

    return view
}
