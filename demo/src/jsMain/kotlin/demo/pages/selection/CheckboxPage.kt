package demo.pages.selection

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
import demo.Catalog
import demo.Groups
import demo.bi
import demo.pages.fixtures.people
import demo.ui.PageFrame
import demo.ui.PageMeta
import demo.ui.RecordedLog
import demo.ui.Readout
import demo.ui.joinValues
import demo.ui.listen
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.checkbox.CheckboxEvents
import io.github.shijianjs.ktjs.vaadin.checkbox.createCheckbox
import io.github.shijianjs.ktjs.vaadin.checkboxgroup.CheckboxGroupEvents
import io.github.shijianjs.ktjs.vaadin.checkboxgroup.createCheckboxGroup
import web.dom.clickEvent
import web.events.addHandler

internal class CheckboxPage : DemoPage() {
    private val texts = Binder(checkboxTexts)

    private val frame = PageFrame(ENTRY)

    private val log = RecordedLog<Pair<Boolean, Boolean>>(6)

    private val terms = createCheckbox()

    private val stateReadout = Readout("")

    private var lastValidation: Boolean? = null

    private val requiredResult = Readout("")

    private val exportGroup = createCheckboxGroup().addTheme("vertical")

    private val exportReadout = Readout("")

    init {
        frame.mount(this)

        fun check(label: (CheckboxTexts) -> String): io.github.shijianjs.ktjs.vaadin.checkbox.CheckboxElement =
            createCheckbox().also { node -> texts.bind(node, label) { node.label = it } }

        val basic = SectionBlock.NAME { show() }
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        texts.bind(terms, { it.acceptTerms }) { terms.label = it }
        terms.dataset["test"] = "checkbox-basic"
        // `change` only fires for user interaction, so the event stream stays readable; the read-out
        // shows `checked-changed` working for programmatic updates as well.
        terms.listen(CheckboxEvents.change) {
            log.add(false to terms.checked) { (fromCode, value) ->
                if (fromCode) texts.current.codeLine(value) else texts.current.changeLine(value)
            }
        }
        terms.listen(CheckboxEvents.checkedChanged) { showChecked() }
        val toggleFromCode = createButton()
        texts.bind(toggleFromCode) { it.toggleFromCode }
        toggleFromCode.clickEvent.addHandler {
            terms.checked = !terms.checked
            log.add(true to terms.checked) { (fromCode, value) ->
                if (fromCode) texts.current.codeLine(value) else texts.current.changeLine(value)
            }
        }
        basic.body.append(exampleBox(null, kitCol(terms, kitRow(toggleFromCode, stateReadout.root), log.root)))
        body.append(basic.root)

        val labeling = SectionBlock.NAME { show() }
        texts.bind(labeling.heading) { it.labelingHeading }
        texts.bind(labeling.hint) { it.labelingHint }
        val agree = check { it.agree }
        val releases = check { it.releases }
        texts.bind(releases, { it.oneEmail }) { releases.helperText = it }
        labeling.body.append(exampleBox(null, kitCol(agree, releases)))
        body.append(labeling.root)

        val required = SectionBlock.NAME { show() }
        texts.bind(required.heading) { it.requiredHeading }
        texts.bind(required.hint) { it.requiredHint }
        val grant = check { it.grantView }
        grant.required = true
        texts.bind(grant, { it.fieldRequired }) { grant.errorMessage = it }
        val submit = createButton()
        texts.bind(submit) { it.validate }
        submit.clickEvent.addHandler {
            lastValidation = grant.validate()
            showValidation()
        }
        required.body.append(exampleBox(null, kitCol(grant, kitRow(submit, requiredResult.root))))
        body.append(required.root)

        val indeterminate = SectionBlock.NAME { show() }
        texts.bind(indeterminate.heading) { it.indeterminateHeading }
        texts.bind(indeterminate.hint) { it.indeterminateHint }
        val users = people(3)
        val selectAll = check { it.notifyUsers }
        val userGroup = createCheckboxGroup().addTheme("vertical")
        texts.bind(userGroup, { it.usersToNotify }) { userGroup.label = it }
        userGroup.append(
            *users.map { person ->
                createCheckbox {
                    value = person.displayName
                    textContent = person.displayName
                }
            }.toTypedArray(),
        )
        fun syncSelectAll() {
            val count = userGroup.value.size
            selectAll.checked = count == users.size
            selectAll.indeterminate = count in 1 until users.size
        }
        userGroup.value = arrayOf(users[0].displayName, users[2].displayName)
        syncSelectAll()
        selectAll.listen(CheckboxEvents.change) {
            userGroup.value = if (selectAll.checked) users.map { it.displayName }.toTypedArray() else arrayOf()
            syncSelectAll()
        }
        userGroup.listen(CheckboxGroupEvents.valueChanged) { syncSelectAll() }
        indeterminate.body.append(exampleBox(null, kitCol(selectAll, userGroup)))
        body.append(indeterminate.root)

        val orientation = SectionBlock.NAME { show() }
        texts.bind(orientation.heading) { it.orientationHeading }
        texts.bind(orientation.hint) { it.orientationHint }
        val permissions = createCheckboxGroup().addTheme("horizontal")
        texts.bind(permissions, { it.permissions }) { permissions.label = it }
        permissions.append(
            option("read") { it.read },
            option("edit") { it.edit },
            option("delete") { it.delete },
        )
        val days = createCheckboxGroup().addTheme("vertical")
        texts.bind(days, { it.workingDays }) { days.label = it }
        listOf(
            "monday" to { copy: CheckboxTexts -> copy.monday },
            "tuesday" to { copy: CheckboxTexts -> copy.tuesday },
            "wednesday" to { copy: CheckboxTexts -> copy.wednesday },
            "thursday" to { copy: CheckboxTexts -> copy.thursday },
            "friday" to { copy: CheckboxTexts -> copy.friday },
            "saturday" to { copy: CheckboxTexts -> copy.saturday },
            "sunday" to { copy: CheckboxTexts -> copy.sunday },
        ).forEach { (value, copy) ->
            val node = createCheckbox { this.value = value }
            texts.bind(node, copy) { node.textContent = it }
            days.append(node)
        }
        val manufacturers = createCheckboxGroup().addTheme("vertical")
        texts.bind(manufacturers, { it.manufacturer }) { manufacturers.label = it }
        manufacturers.append(
            *listOf("Akuchi", "Broek", "Wulf").mapIndexed { index, name ->
                createCheckbox {
                    value = "$index"
                    textContent = name
                }
            }.toTypedArray(),
        )
        val statuses = createCheckboxGroup().addTheme("vertical")
        texts.bind(statuses, { it.status }) { statuses.label = it }
        statuses.append(
            option("0") { it.inProgress },
            option("1") { it.done },
            option("2") { it.cancelled },
        )
        val adjacent = kitCol(manufacturers, statuses)
        orientation.body.append(
            exampleBox(null, permissions),
            exampleBox(null, days),
            exampleBox(null, adjacent),
        )
        body.append(orientation.root)

        val preset = SectionBlock.NAME { show() }
        texts.bind(preset.heading) { it.presetHeading }
        texts.bind(preset.hint) { it.presetHint }
        texts.bind(exportGroup, { it.exportData }) { exportGroup.label = it }
        exportGroup.value = arrayOf("0", "2")
        exportGroup.append(
            option("0") { it.orderId },
            option("1") { it.productName },
            option("2") { it.customer },
            option("3") { it.statusColumn },
        )
        exportGroup.listen(CheckboxGroupEvents.valueChanged) { showExport() }
        preset.body.append(exampleBox(null, kitCol(exportGroup, exportReadout.root)))
        body.append(preset.root)

        val styles = SectionBlock.NAME { show() }
        texts.bind(styles.heading) { it.stylesHeading }
        texts.bind(styles.hint) { it.stylesHint }
        val styled = createCheckboxGroup().addTheme("helper-above-field", "vertical")
        texts.bind(styled, { it.label }) { styled.label = it }
        texts.bind(styled, { it.helperText }) { styled.helperText = it }
        styled.append(
            *listOf("Item 1", "Item 2", "Item 3").map { label ->
                createCheckbox {
                    value = label
                    textContent = label
                }
            }.toTypedArray(),
        )
        styles.body.append(exampleBox(null, styled))
        body.append(styles.root)

        applyTranslation(Langs.EN)
    }

    private fun option(
        value: String,
        copy: (CheckboxTexts) -> String,
    ) = createCheckbox { this.value = value }.also { node ->
        texts.bind(node, copy) { node.textContent = it }
    }

    override fun applyTranslation(lang: Lang) {
        val meta = ENTRY.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        showChecked()
        showValidation()
        showExport()
        log.replay(false to false)
    }

    private fun showChecked() {
        stateReadout.set(texts.current.checkedPrefix + terms.checked)
    }

    private fun showValidation() {
        val copy = texts.current
        val result = lastValidation
        requiredResult.set(
            if (result == null) copy.notValidated
            else copy.validationPrefix + (if (result) copy.passed else copy.failed),
        )
    }

    private fun showExport() {
        exportReadout.set("value = ", joinValues(exportGroup.value).ifEmpty { texts.current.emptyValue })
    }

    companion object : CustomElementReferenceAdapter<CheckboxPage>(CheckboxPage::class.js, "vaadin-demo-checkbox-page") {
        val ENTRY: CatalogEntry =
            CatalogEntry(
                id = "checkbox",
                group = Groups.SELECTION,
                meta =
                    PageMeta(
                        title = bi("Checkbox", "Checkbox"),
                        summary =
                            bi(
                                "单个勾选框：checked / indeterminate 三态、只读、禁用与 change 事件。",
                                "Single checkbox: checked / indeterminate tri-state, read-only, disabled and " +
                                    "change events.",
                            ),
                        docsUrl = "${Catalog.DOCS_BASE}/checkbox",
                        sourcePath = "pages/selection/CheckboxPage.kt",
                    ),
                kitPage = { CheckboxPage.NAME() },
            )

        private val english: CheckboxTexts =
            CheckboxTexts(
                basicHeading = "Basic usage",
                basicHint = "checked reflects the tick state; change only fires for user interaction, while " +
                    "checked-changed also fires when code sets the property.",
                acceptTerms = "I accept the terms and conditions",
                toggleFromCode = "Toggle from code",
                checkedPrefix = "checked = ",
                changeLine = { "change · $it" },
                codeLine = { "checked-changed (from code) · $it" },
                labelingHeading = "Label and helper text",
                labelingHint = "label is the visible caption; helper-text explains the expected input. Both are " +
                    "set through properties.",
                agree = "Yes, I agree",
                releases = "Receive release notifications",
                oneEmail = "At most one email per day",
                requiredHeading = "Required",
                requiredHint = "required makes an empty value invalid; error-message is shown below the field " +
                    "when validation fails.",
                grantView = "Grant view permissions",
                fieldRequired = "This field is required",
                notValidated = "Not validated yet",
                validate = "Validate",
                validationPrefix = "Validation: ",
                passed = "passed",
                failed = "failed",
                indeterminateHeading = "Indeterminate",
                indeterminateHint = "The \"select all\" checkbox uses indeterminate for a partial selection; it " +
                    "is derived from the number of selected items together with checked.",
                notifyUsers = "Notify users",
                usersToNotify = "Users to notify",
                orientationHeading = "Orientation",
                orientationHint = "theme=\"horizontal\" lays the options out in a row, vertical stacks them; " +
                    "adjacent groups read better with a gap between them.",
                permissions = "Permissions",
                read = "Read",
                edit = "Edit",
                delete = "Delete",
                workingDays = "Working days",
                monday = "Monday",
                tuesday = "Tuesday",
                wednesday = "Wednesday",
                thursday = "Thursday",
                friday = "Friday",
                saturday = "Saturday",
                sunday = "Sunday",
                manufacturer = "Manufacturer",
                status = "Status",
                inProgress = "In progress",
                done = "Done",
                cancelled = "Cancelled",
                presetHeading = "Preset value",
                presetHint = "value can be assigned directly; value-changed reports the new array after user edits.",
                exportData = "Export data",
                orderId = "Order ID",
                productName = "Product name",
                customer = "Customer",
                statusColumn = "Status",
                emptyValue = "[]",
                stylesHeading = "Style variants",
                stylesHint = "helper-above-field moves the helper text between the label and the options.",
                label = "Label",
                helperText = "Helper text",
            )
        private val chinese: CheckboxTexts =
            CheckboxTexts(
                basicHeading = "基本用法",
                basicHint = "checked 反映勾选状态；change 只在用户操作时触发，checked-changed 在属性被代码设置时也会触发。",
                acceptTerms = "我接受条款与条件",
                toggleFromCode = "程序化切换",
                checkedPrefix = "checked = ",
                changeLine = { "change · $it" },
                codeLine = { "checked-changed（程序设置）· $it" },
                labelingHeading = "标签与辅助信息",
                labelingHint = "label 是可见标签；helper-text 提示填写方式，两者都通过 property 设置。",
                agree = "我同意",
                releases = "接收发布通知",
                oneEmail = "每天最多一封邮件",
                requiredHeading = "必填",
                requiredHint = "required 让空值无法通过校验；error-message 是失败时显示在字段下方的文案。",
                grantView = "授予查看权限",
                fieldRequired = "此项为必填",
                notValidated = "尚未校验",
                validate = "校验",
                validationPrefix = "校验：",
                passed = "通过",
                failed = "失败",
                indeterminateHeading = "不确定态",
                indeterminateHint = "「全选」勾选框用 indeterminate 表示部分选中；它与组件的 checked 一起由选中数量派生。",
                notifyUsers = "通知所有用户",
                usersToNotify = "接收通知的用户",
                orientationHeading = "方向",
                orientationHint = "theme=\"horizontal\" 让选项横排，vertical 让选项竖排；相邻的两个组之间留出间距更易读。",
                permissions = "权限",
                read = "读取",
                edit = "编辑",
                delete = "删除",
                workingDays = "工作日",
                monday = "周一",
                tuesday = "周二",
                wednesday = "周三",
                thursday = "周四",
                friday = "周五",
                saturday = "周六",
                sunday = "周日",
                manufacturer = "制造商",
                status = "状态",
                inProgress = "进行中",
                done = "已完成",
                cancelled = "已取消",
                presetHeading = "预设值",
                presetHint = "value 可以直接赋值；value-changed 报告用户改动后的数组。",
                exportData = "导出数据",
                orderId = "订单号",
                productName = "产品名称",
                customer = "客户",
                statusColumn = "状态",
                emptyValue = "[]",
                stylesHeading = "样式变体",
                stylesHint = "helper-above-field 把辅助信息放到标签与选项之间。",
                label = "标签",
                helperText = "辅助信息",
            )
        private val checkboxTexts: Translations<CheckboxTexts> = texts(
            Langs.EN to english,
            Langs.ZH_HANS to chinese,
        )
    }
}
