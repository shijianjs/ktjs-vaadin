package demo.pages.layout

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.tr
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.div
import demo.ui.element
import demo.ui.example
import demo.ui.page
import demo.ui.row
import demo.ui.section
import js.objects.unsafeJso
import vaadin.emailfield.createEmailField
import vaadin.formlayout.FormLayoutLabelsPositions
import vaadin.formlayout.FormLayoutResponsiveStep
import vaadin.formlayout.StringOrDouble
import vaadin.formlayout.createFormItem
import vaadin.formlayout.createFormLayout
import vaadin.formlayout.createFormRow
import vaadin.passwordfield.createPasswordField
import vaadin.splitlayout.createSplitLayout
import vaadin.textfield.createTextField
import web.html.HTMLElement
import web.html.HtmlTagName

val formLayoutEntry: CatalogEntry =
    CatalogEntry(
        id = "form-layout",
        group = Groups.LAYOUT,
        meta =
            PageMeta(
                title = bi("Form Layout", "Form Layout"),
                summary =
                    bi(
                        "响应式表单栅格：按可用宽度自动调整列数，标签可以放在字段旁边。",
                        "Responsive form grid: the column count follows the available width and labels can " +
                            "sit beside their fields.",
                    ),
                docsUrl = "$DOCS_BASE/form-layout",
                sourcePath = "pages/layout/FormLayoutPage.kt",
            ),
        builder = ::buildFormLayoutPage,
    )

private fun buildFormLayoutPage(meta: PageMeta): PageView {
    val view = page(meta)

    val autoSection =
        section(
            tr("自动响应式模式", "Auto-responsive mode"),
            tr(
                "autoResponsive 按可用宽度自动创建列，列宽由 columnWidth 指定（默认 12em）。每个字段默认独占" +
                    "一行，用 form-row 可以把字段并到同一行。",
                "autoResponsive creates columns from the available width, each as wide as columnWidth (12em by " +
                    "default). Every field starts a new row unless fields are grouped with form-row.",
            ),
        )
    val basicForm = createFormLayout { autoResponsive = true }
    basicForm.dataset["test"] = "form-layout"
    basicForm.append(
        formRow(textField(tr("名", "First name")), textField(tr("姓", "Last name"))),
        formRow(emailField(tr("邮箱地址", "Email address"))),
        formRow(passwordField(tr("密码", "Password")), passwordField(tr("确认密码", "Confirm password"))),
    )
    autoSection.body.append(example(null, formFrame(basicForm)))
    view.body.append(autoSection.root)

    val labelsSection =
        section(
            tr("标签位置", "Label position"),
            tr(
                "labelsAside 让 form-item 优先把标签放在字段左侧；宽度不够时自动改回字段上方。",
                "labelsAside makes form-item prefer placing its label beside the field and fall back above it " +
                    "when there is not enough room.",
            ),
        )
    val asideForm = createFormLayout {
        autoResponsive = true
        labelsAside = true
    }
    asideForm.dataset["test"] = "form-labels-aside"
    asideForm.append(
        formItem(tr("名", "First name"), createTextField()),
        formItem(tr("姓", "Last name"), createTextField()),
        formItem(tr("邮箱地址", "Email address"), createEmailField()),
    )
    labelsSection.body.append(example(null, formFrame(asideForm)))
    view.body.append(labelsSection.root)

    val columnsSection =
        section(
            tr("列宽与列数", "Column width and count"),
            tr(
                "columnWidth 决定一列多宽，expandColumns 让列平分剩余宽度，expandFields 让字段撑满所在列" +
                    "（包括 form-item 里的字段）。",
                "columnWidth sets the width of a column, expandColumns lets columns share the remaining width " +
                    "and expandFields stretches the fields, including those inside form-item.",
            ),
        )
    val expandColumnsForm = createFormLayout {
        autoResponsive = true
        columnWidth = "8em"
        expandColumns = true
    }
    expandColumnsForm.append(
        formRow(textField(tr("名", "First name")), textField(tr("姓", "Last name"))),
        formRow(emailField(tr("邮箱地址", "Email address"))),
        formRow(passwordField(tr("密码", "Password")), passwordField(tr("确认密码", "Confirm password"))),
    )
    val expandFieldsForm = createFormLayout {
        autoResponsive = true
        columnWidth = "8em"
        expandColumns = true
        expandFields = true
    }
    expandFieldsForm.append(
        formRow(textField(tr("名", "First name")), textField(tr("姓", "Last name"))),
        formRow(emailField(tr("邮箱地址", "Email address"))),
        formRow(passwordField(tr("密码", "Password")), passwordField(tr("确认密码", "Confirm password"))),
    )
    columnsSection.body.append(
        example("expand-columns", formFrame(expandColumnsForm)),
        example("expand-columns expand-fields", formFrame(expandFieldsForm)),
    )
    view.body.append(columnsSection.root)

    val colspan =
        section(
            tr("跨列", "Column span"),
            tr(
                "字段上的 data-colspan 让它占据多列；只能用在 form-row 里，并且需要 autoResponsive。",
                "data-colspan on a field makes it span several columns; it only applies inside form-row with " +
                    "autoResponsive enabled.",
            ),
        )
    val spanForm = createFormLayout {
        autoResponsive = true
        columnWidth = "8em"
        expandFields = true
    }
    spanForm.dataset["test"] = "form-colspan"
    spanForm.append(
        formRow(textField(tr("街道地址", "Street address")).apply { setAttribute("data-colspan", "3") }),
        formRow(
            textField(tr("邮政编码", "Postal code")),
            textField(tr("城市", "City/Town")).apply { setAttribute("data-colspan", "2") },
        ),
        formRow(textField(tr("国家", "Country")).apply { setAttribute("data-colspan", "2") }),
    )
    colspan.body.append(example(null, formFrame(spanForm)))
    view.body.append(colspan.root)

    val stepsSection =
        section(
            tr("响应式断点", "Responsive steps"),
            tr(
                "不用 autoResponsive 时用 responsiveSteps 描述断点与列数；首项通常从 0 开始。",
                "Without autoResponsive, responsiveSteps describes the breakpoints and column counts; the " +
                    "first step usually starts at 0.",
            ),
        )
    val stepsForm = createFormLayout()
    stepsForm.responsiveSteps =
        arrayOf(
            unsafeJso<FormLayoutResponsiveStep> {
                minWidth = StringOrDouble(0.0)
                columns = 1.0
                labelsPosition = FormLayoutLabelsPositions.top
            },
            unsafeJso<FormLayoutResponsiveStep> {
                minWidth = StringOrDouble("320px")
                columns = 2.0
            },
            unsafeJso<FormLayoutResponsiveStep> {
                minWidth = StringOrDouble("500px")
                columns = 3.0
            },
        )
    stepsForm.append(
        textField(tr("名", "First name")),
        textField(tr("姓", "Last name")),
        emailField(tr("邮箱地址", "Email address")),
    )

    val stepsAsideForm = createFormLayout()
    stepsAsideForm.style.setProperty("--vaadin-form-layout-label-width", "92px")
    stepsAsideForm.append(
        formItem(tr("名", "First name"), createTextField()),
        formItem(tr("姓", "Last name"), createTextField()),
        formItem(tr("邮箱地址", "Email address"), createEmailField()),
    )
    stepsSection.body.append(
        example("responsiveSteps", formFrame(stepsForm)),
        example(tr("form-item 默认把标签放在左侧", "form-item puts the label aside by default"), formFrame(stepsAsideForm)),
    )
    view.body.append(stepsSection.root)

    return view
}

/**
 * Puts a form layout next to an empty pane in a split layout, as the docs examples do: dragging the
 * splitter changes the width of the form and the responsive column count follows it.
 */
private fun formFrame(form: HTMLElement): HTMLElement {
    form.style.setProperty("width", "100%")
    val split = createSplitLayout()
    split.style.setProperty("height", "18rem")
    split.append(form, div().apply { style.setProperty("background", "var(--lumo-contrast-5pct)") })
    return split
}

/** A `<vaadin-form-row>` grouping fields into one row of an auto-responsive form layout. */
private fun formRow(vararg fields: HTMLElement): HTMLElement {
    val row = createFormRow()
    row.append(*fields)
    return row
}

internal fun textField(label: String): HTMLElement = createTextField { this.label = label }

internal fun passwordField(label: String): HTMLElement = createPasswordField { this.label = label }

/** A `vaadin-form-item` whose label goes into the `label` slot, as upstream documents it. */
private fun formItem(label: String, field: HTMLElement): HTMLElement {
    val item = createFormItem()
    val labelNode = element(HtmlTagName.label).apply {
        slot = "label"
        textContent = label
    }
    item.append(labelNode, field)
    return item
}
