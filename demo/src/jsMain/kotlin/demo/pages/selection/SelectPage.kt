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
import web.html.HtmlTagName
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import demo.CatalogEntry
import demo.Catalog
import demo.Groups
import demo.bi
import demo.pages.fixtures.CITIES_JSON
import demo.pages.fixtures.SORT_ITEMS_JSON
import demo.pages.fixtures.people
import demo.ui.PageFrame
import demo.ui.PageMeta
import demo.ui.Readout
import demo.ui.jsArray
import demo.ui.listen
import kotlin.js.JsAny
import kotlin.js.unsafeCast
import io.github.shijianjs.ktjs.vaadin.avatar.createAvatar
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.vaadin.select.SelectElement
import io.github.shijianjs.ktjs.vaadin.select.SelectEvents
import io.github.shijianjs.ktjs.vaadin.select.SelectItemData
import io.github.shijianjs.ktjs.vaadin.select.createSelect
import io.github.shijianjs.ktjs.vaadin.select.createSelectItem
import io.github.shijianjs.ktjs.vaadin.select.createSelectListBox
import io.github.shijianjs.ktjs.vaadin.tooltip.createTooltip
import web.dom.clickEvent
import web.events.addHandler

internal class SelectPage : DemoPage() {
    private val texts = Binder(selectTexts)

    private val frame = PageFrame(ENTRY)

    private val cities: SelectElement = createSelect()

    private val readout = Readout("value = helsinki")

    private val team: SelectElement = createSelect()

    private val teamReadout = Readout("")

    init {
        frame.mount(this)

        fun field(copy: (SelectTexts) -> String): SelectElement =
            createSelect().also { node -> texts.bind(node, copy) { node.label = it } }

        val basic = SectionBlock.NAME { show() }
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        cities.value = "helsinki"
        cities.items = selectItems(CITIES_JSON)
        texts.bind(cities, { it.city }) { cities.label = it }
        texts.bind(cities, { it.chooseOne }) { cities.placeholder = it }
        cities.dataset["test"] = "select-basic"
        cities.listen(SelectEvents.valueChanged) { showValue() }
        basic.body.append(exampleBox(null, kitCol(cities, readout.root)))
        body.append(basic.root)

        val features = SectionBlock.NAME { show() }
        texts.bind(features.heading) { it.featuresHeading }
        texts.bind(features.hint) { it.featuresHint }
        val sortBy = field { it.sortBy }
        sortBy.items = selectItems(SORT_ITEMS_JSON)
        texts.bind(sortBy, { it.helper }) { sortBy.helperText = it }
        texts.bind(sortBy, { it.placeholder }) { sortBy.placeholder = it }
        val tooltip = createTooltip().apply { slot = "tooltip" }
        texts.bind(tooltip, { it.tooltipText }) { tooltip.text = it }
        sortBy.append(tooltip, createIcon { icon = "vaadin:vaadin-h" }.apply { slot = "prefix" })
        features.body.append(exampleBox(null, sortBy))
        body.append(features.root)

        val dividersAndDisabled = SectionBlock.NAME { show() }
        texts.bind(dividersAndDisabled.heading) { it.dividersHeading }
        texts.bind(dividersAndDisabled.hint) { it.dividersHint }
        val divided = field { it.sortBy }
        divided.value = "recent"
        divided.items = selectItems(DIVIDED_SORT_ITEMS_JSON)
        val sizes = field { it.size }
        sizes.value = "xl"
        sizes.items = selectItems(SIZES_JSON)
        dividersAndDisabled.body.append(exampleBox(null, kitRow(divided, sizes)))
        body.append(dividersAndDisabled.root)

        val overlay = SectionBlock.NAME { show() }
        texts.bind(overlay.heading) { it.overlayHeading }
        texts.bind(overlay.hint) { it.overlayHint }
        val noOverlap = field { it.sortBy }
        noOverlap.noVerticalOverlap = true
        noOverlap.items = selectItems(SORT_ITEMS_JSON)
        val employees = people(5)
        val wide = field { it.employee }
        wide.items =
            selectItems(
                employees
                    .map { person ->
                        "{\"label\": \"${person.profession} - ${person.displayName}\", " +
                            "\"value\": \"${person.displayName}\"}"
                    }
                    .joinToString(",", "[", "]"),
            )
        wide.style.setProperty("--vaadin-select-overlay-width", "350px")
        overlay.body.append(exampleBox(null, kitRow(noOverlap, wide)))
        body.append(overlay.root)

        val placeholderSection = SectionBlock.NAME { show() }
        texts.bind(placeholderSection.heading) { it.placeholderHeading }
        texts.bind(placeholderSection.hint) { it.placeholderHint }
        team.items =
            selectItems(
                """
                [
                  {"label": "Engineering", "value": "eng"},
                  {"label": "Design", "value": "design"}
                ]
                """,
            )
        texts.bind(team, { it.team }) { team.label = it }
        texts.bind(team, { it.nothingSelected }) { team.placeholder = it }
        team.listen(SelectEvents.valueChanged) { showTeam() }
        val clear = createButton().apply { clickEvent.addHandler { team.value = "" } }
        texts.bind(clear) { it.clear }
        placeholderSection.body.append(exampleBox(null, kitCol(team, kitRow(clear, teamReadout.root))))
        body.append(placeholderSection.root)

        val emptySelection = SectionBlock.NAME { show() }
        texts.bind(emptySelection.heading) { it.emptySelectionHeading }
        texts.bind(emptySelection.hint) { it.emptySelectionHint }
        body.append(emptySelection.root)

        val customLabel = SectionBlock.NAME { show() }
        texts.bind(customLabel.heading) { it.customLabelHeading }
        texts.bind(customLabel.hint) { it.customLabelHint }
        val assignee = field { it.assignee }
        val assigneeBox = createSelectListBox().apply { slot = "overlay" }
        employees.forEach { person ->
            assigneeBox.append(
                createSelectItem {
                    value = "${person.id}"
                    label = person.displayName
                }.apply {
                    append(
                        kitCol(
                            HtmlTagName.span { textContent = person.displayName },
                            HtmlTagName.span { textContent = person.profession },
                        ),
                    )
                },
            )
        }
        assignee.append(assigneeBox)
        assignee.value = "${employees.first().id}"
        customLabel.body.append(exampleBox(null, assignee))
        body.append(customLabel.root)

        val presentation = SectionBlock.NAME { show() }
        texts.bind(presentation.heading) { it.presentationHeading }
        texts.bind(presentation.hint) { it.presentationHint }
        val doctors = field { it.chooseDoctor }
        doctors.style.width = "15em"
        val doctorBox = createSelectListBox().apply { slot = "overlay" }
        employees.forEach { person ->
            doctorBox.append(
                createSelectItem { value = "${person.id}" }.apply {
                    append(
                        kitRow(
                            createAvatar {
                                name = person.displayName
                                img = person.pictureUrl
                            },
                            kitCol(
                                HtmlTagName.span { textContent = person.displayName },
                                HtmlTagName.span { textContent = person.profession },
                            ),
                        ),
                    )
                },
            )
        }
        doctors.append(doctorBox)
        doctors.value = "${employees.first().id}"
        presentation.body.append(exampleBox(null, doctors))
        body.append(presentation.root)

        val states = SectionBlock.NAME { show() }
        texts.bind(states.heading) { it.statesHeading }
        val readonlySelect =
            createSelect {
                value = "bravo"
                readonly = true
                items =
                    selectItems(
                        """
                        [
                          {"label": "Alpha", "value": "alpha"},
                          {"label": "Bravo", "value": "bravo"},
                          {"label": "Charlie", "value": "charlie"}
                        ]
                        """,
                    )
            }
        texts.bind(readonlySelect, { it.readonly }) { readonlySelect.label = it }
        val disabledSelect =
            createSelect {
                value = "alpha"
                disabled = true
                items =
                    selectItems(
                        """
                        [
                          {"label": "Alpha", "value": "alpha"},
                          {"label": "Bravo", "value": "bravo"}
                        ]
                        """,
                    )
            }
        texts.bind(disabledSelect, { it.disabled }) { disabledSelect.label = it }
        states.body.append(exampleBox(null, kitRow(readonlySelect, disabledSelect)))
        body.append(states.root)

        val styles = SectionBlock.NAME { show() }
        texts.bind(styles.heading) { it.stylesHeading }
        texts.bind(styles.hint) { it.stylesHint }
        val styled = createSelect { value = "recent" }.addTheme("align-right", "small", "helper-above-field")
        texts.bind(styled, { it.label }) { styled.label = it }
        texts.bind(styled, { it.helperText }) { styled.helperText = it }
        styled.items = selectItems(SORT_ITEMS_JSON)
        styles.body.append(exampleBox(null, styled))
        body.append(styles.root)

        applyTranslation(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = ENTRY.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        showValue()
        showTeam()
    }

    private fun showValue() {
        readout.set("value = ", cities.value.ifEmpty { texts.current.none })
    }

    private fun showTeam() {
        val copy = texts.current
        teamReadout.set(copy.currentPrefix, team.value.ifEmpty { copy.empty })
    }

    companion object : SelectPageDefaultReference() {
        val ENTRY: CatalogEntry =
            CatalogEntry(
                id = "select",
                group = Groups.SELECTION,
                meta =
                    PageMeta(
                        title = bi("Select", "Select"),
                        summary =
                            bi(
                                "下拉单选：items 提供选项，value 是选中的字符串。",
                                "Dropdown single select: items provides the options and value is the selected " +
                                    "string.",
                            ),
                        docsUrl = "${Catalog.DOCS_BASE}/select",
                        sourcePath = "pages/selection/SelectPage.kt",
                    ),
                kitPage = { SelectPage.NAME() },
            )

        private val english: SelectTexts =
            SelectTexts(
                basicHeading = "Basic usage",
                basicHint = "items takes an array of { label, value }; the dropdown renders in an overlay and " +
                    "value is a string.",
                city = "City",
                chooseOne = "Choose one",
                none = "(none)",
                featuresHeading = "Basic features",
                featuresHint = "label, helper-text, placeholder and a prefix icon make up the complete field; " +
                    "the tooltip is attached through its slot.",
                sortBy = "Sort by",
                helper = "Helper text",
                placeholder = "Placeholder",
                tooltipText = "Tooltip text",
                dividersHeading = "Dividers and disabled items",
                dividersHint = "Use { component: \"hr\" } in items for a divider; individual items can be marked " +
                    "disabled.",
                size = "Size",
                overlayHeading = "Overlay position and width",
                overlayHint = "no-vertical-overlap keeps the overlay from covering the field itself; " +
                    "--vaadin-select-overlay-width widens the dropdown independently.",
                employee = "Employee",
                placeholderHeading = "Placeholder and clearing",
                placeholderHint = "placeholder only shows while nothing is selected; setting value to an empty " +
                    "string returns the field to it.",
                team = "Team",
                nothingSelected = "Nothing selected yet",
                currentPrefix = "Current value: ",
                empty = "(empty)",
                clear = "Clear",
                emptySelectionHeading = "Empty selection item",
                emptySelectionHint = "Adding an item that represents \"no selection\" with a custom caption " +
                    "requires the Flow setEmptySelectionCaption API; the web component has no equivalent property, " +
                    "so it is not shown here.",
                customLabelHeading = "Custom item label",
                customLabelHint = "Build the dropdown content in the default slot; the label attribute of each " +
                    "item is what the field shows once selected.",
                assignee = "Assignee",
                presentationHeading = "Custom item presentation",
                presentationHint = "Items can hold an avatar and secondary information; the selected value still " +
                    "comes from label.",
                chooseDoctor = "Choose doctor",
                statesHeading = "Read-only and disabled",
                readonly = "Read-only",
                disabled = "Disabled",
                stylesHeading = "Style variants",
                stylesHint = "align-right aligns the text to the end, small compresses the height and " +
                    "helper-above-field puts the helper text below the label.",
                label = "Label",
                helperText = "Helper text",
            )
        private val chinese: SelectTexts =
            SelectTexts(
                basicHeading = "基本用法",
                basicHint = "items 接受 { label, value } 数组；下拉在浮层中渲染，value 是字符串。",
                city = "所在城市",
                chooseOne = "请选择",
                none = "（未选择）",
                featuresHeading = "基本特性",
                featuresHint = "label、helper-text、placeholder 与 prefix 图标组合成完整字段；tooltip 通过 slot 挂载。",
                sortBy = "排序方式",
                helper = "辅助信息",
                placeholder = "占位提示",
                tooltipText = "提示文本",
                dividersHeading = "分隔线与禁用条目",
                dividersHint = "items 里用 { component: \"hr\" } 插入分隔线；单个条目可以用 disabled 标为不可选。",
                size = "尺码",
                overlayHeading = "浮层位置与宽度",
                overlayHint = "no-vertical-overlap 禁止浮层覆盖输入框本身；--vaadin-select-overlay-width 单独设置下拉宽度。",
                employee = "员工",
                placeholderHeading = "占位符与清空",
                placeholderHint = "placeholder 只在没有选中值时显示；把 value 置空即可回到占位状态。",
                team = "团队",
                nothingSelected = "尚未选择",
                currentPrefix = "当前选择：",
                empty = "（空）",
                clear = "清空",
                emptySelectionHeading = "空选择项",
                emptySelectionHint = "在下拉列表里放一个表示「不选择」的条目、并自定义它的文案，需要 Flow 侧的 " +
                    "setEmptySelectionCaption API，Web Component 没有对应属性，这里不演示。",
                customLabelHeading = "自定义条目标签",
                customLabelHint = "用默认插槽自行组装下拉内容；条目上的 label 属性决定选中后显示在输入框里的文本。",
                assignee = "负责人",
                presentationHeading = "自定义条目呈现",
                presentationHint = "条目内部可以放头像与次要信息；选中值仍由 label 决定。",
                chooseDoctor = "选择医生",
                statesHeading = "只读与禁用",
                readonly = "只读",
                disabled = "禁用",
                stylesHeading = "样式变体",
                stylesHint = "align-right 让文本右对齐，small 压缩高度，helper-above-field 把辅助信息放到标签下。",
                label = "标签",
                helperText = "辅助信息",
            )
        private const val SIZES_JSON =
            """
            [
              {"label": "XS (out of stock)", "value": "xs", "disabled": true},
              {"label": "S", "value": "s"},
              {"label": "M", "value": "m"},
              {"label": "L", "value": "l"},
              {"label": "XL", "value": "xl"}
            ]
            """
        private const val DIVIDED_SORT_ITEMS_JSON =
            """
            [
              {"label": "Most recent first", "value": "recent"},
              {"component": "hr"},
              {"label": "Rating: high to low", "value": "rating-desc"},
              {"label": "Rating: low to high", "value": "rating-asc"},
              {"component": "hr"},
              {"label": "Price: high to low", "value": "price-desc"},
              {"label": "Price: low to high", "value": "price-asc"}
            ]
            """
        private val selectTexts: Translations<SelectTexts> = texts(
            Langs.EN to english,
            Langs.ZH_HANS to chinese,
        )
        /** Parses a JSON array of `{label, value, ...}` entries into the type `<vaadin-select>` takes. */
        private fun selectItems(json: String): Array<SelectItemData> =
            jsArray<JsAny>(json).unsafeCast<Array<SelectItemData>>()
    }
}
