package demo.pages.layout

import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import io.github.shijianjs.ktjs.dom.element.invoke
import demo.CatalogEntry
import demo.Catalog
import demo.Groups
import demo.bi
import demo.ui.PageFrame
import demo.ui.PageMeta
import js.objects.unsafeJso
import io.github.shijianjs.ktjs.vaadin.emailfield.EmailFieldElement
import io.github.shijianjs.ktjs.vaadin.emailfield.createEmailField
import io.github.shijianjs.ktjs.vaadin.formlayout.FormLayoutLabelsPositions
import io.github.shijianjs.ktjs.vaadin.formlayout.FormLayoutResponsiveStep
import io.github.shijianjs.ktjs.vaadin.formlayout.StringOrDouble
import io.github.shijianjs.ktjs.vaadin.formlayout.createFormItem
import io.github.shijianjs.ktjs.vaadin.formlayout.createFormLayout
import io.github.shijianjs.ktjs.vaadin.formlayout.createFormRow
import io.github.shijianjs.ktjs.vaadin.passwordfield.PasswordFieldElement
import io.github.shijianjs.ktjs.vaadin.passwordfield.createPasswordField
import io.github.shijianjs.ktjs.vaadin.textfield.TextFieldElement
import io.github.shijianjs.ktjs.vaadin.textfield.createTextField
import web.html.HTMLElement
import web.html.HTMLParagraphElement
import web.html.HtmlTagName

internal class FormLayoutPage : DemoPage() {
    private val texts = Binder(formLayoutTexts)

    private val frame = PageFrame(ENTRY)

    init {
        frame.mount(this)

        fun field(copy: (FormLayoutTexts) -> String): TextFieldElement =
            createTextField().also { node -> texts.bind(node, copy) { node.label = it } }

        fun mail(copy: (FormLayoutTexts) -> String): EmailFieldElement =
            createEmailField().also { node -> texts.bind(node, copy) { node.label = it } }

        fun secret(copy: (FormLayoutTexts) -> String): PasswordFieldElement =
            createPasswordField().also { node -> texts.bind(node, copy) { node.label = it } }

        fun item(copy: (FormLayoutTexts) -> String, field: HTMLElement): HTMLElement {
            val formItem = createFormItem()
            val labelNode = HtmlTagName.label().apply { slot = "label" }
            texts.bind(labelNode, copy) { labelNode.textContent = it }
            formItem.append(labelNode, field)
            return formItem
        }

        val autoSection = SectionBlock.NAME { show() }
        texts.bind(autoSection.heading) { it.autoHeading }
        texts.bind(autoSection.hint) { it.autoHint }
        val basicForm = createFormLayout { autoResponsive = true }
        basicForm.dataset["test"] = "form-layout"
        basicForm.append(
            formRow(field { it.firstName }, field { it.lastName }),
            formRow(mail { it.emailAddress }),
            formRow(secret { it.password }, secret { it.confirmPassword }),
        )
        autoSection.body.append(exampleBox(null, FormFrame.NAME { show(basicForm) }))
        body.append(autoSection.root)

        val labelsSection = SectionBlock.NAME { show() }
        texts.bind(labelsSection.heading) { it.labelsHeading }
        texts.bind(labelsSection.hint) { it.labelsHint }
        val asideForm = createFormLayout {
            autoResponsive = true
            labelsAside = true
        }
        asideForm.dataset["test"] = "form-labels-aside"
        asideForm.append(
            item({ it.firstName }, createTextField()),
            item({ it.lastName }, createTextField()),
            item({ it.emailAddress }, createEmailField()),
        )
        labelsSection.body.append(exampleBox(null, FormFrame.NAME { show(asideForm) }))
        body.append(labelsSection.root)

        val columnsSection = SectionBlock.NAME { show() }
        texts.bind(columnsSection.heading) { it.columnsHeading }
        texts.bind(columnsSection.hint) { it.columnsHint }
        val expandColumnsForm = createFormLayout {
            autoResponsive = true
            columnWidth = "8em"
            expandColumns = true
        }
        expandColumnsForm.append(
            formRow(field { it.firstName }, field { it.lastName }),
            formRow(mail { it.emailAddress }),
            formRow(secret { it.password }, secret { it.confirmPassword }),
        )
        val expandFieldsForm = createFormLayout {
            autoResponsive = true
            columnWidth = "8em"
            expandColumns = true
            expandFields = true
        }
        expandFieldsForm.append(
            formRow(field { it.firstName }, field { it.lastName }),
            formRow(mail { it.emailAddress }),
            formRow(secret { it.password }, secret { it.confirmPassword }),
        )
        columnsSection.body.append(
            exampleBox("expand-columns", FormFrame.NAME { show(expandColumnsForm) }),
            exampleBox("expand-columns expand-fields", FormFrame.NAME { show(expandFieldsForm) }),
        )
        body.append(columnsSection.root)

        val colspan = SectionBlock.NAME { show() }
        texts.bind(colspan.heading) { it.colspanHeading }
        texts.bind(colspan.hint) { it.colspanHint }
        val spanForm = createFormLayout {
            autoResponsive = true
            columnWidth = "8em"
            expandFields = true
        }
        spanForm.dataset["test"] = "form-colspan"
        spanForm.append(
            formRow(field { it.street }.apply { setAttribute("data-colspan", "3") }),
            formRow(
                field { it.postal },
                field { it.city }.apply { setAttribute("data-colspan", "2") },
            ),
            formRow(field { it.country }.apply { setAttribute("data-colspan", "2") }),
        )
        colspan.body.append(exampleBox(null, FormFrame.NAME { show(spanForm) }))
        body.append(colspan.root)

        val stepsSection = SectionBlock.NAME { show() }
        texts.bind(stepsSection.heading) { it.stepsHeading }
        texts.bind(stepsSection.hint) { it.stepsHint }
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
            field { it.firstName },
            field { it.lastName },
            mail { it.emailAddress },
        )

        val stepsAsideForm = createFormLayout()
        stepsAsideForm.style.setProperty("--vaadin-form-layout-label-width", "92px")
        stepsAsideForm.append(
            item({ it.firstName }, createTextField()),
            item({ it.lastName }, createTextField()),
            item({ it.emailAddress }, createEmailField()),
        )
        val asideCard = exampleBox("", FormFrame.NAME { show(stepsAsideForm) })
        texts.bind(asideCard.firstElementChild as HTMLParagraphElement) { it.asideCaption }
        stepsSection.body.append(
            exampleBox("responsiveSteps", FormFrame.NAME { show(stepsForm) }),
            asideCard,
        )
        body.append(stepsSection.root)

        applyTranslation(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = ENTRY.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
    }

    companion object : FormLayoutPageDefaultReference() {
        val ENTRY: CatalogEntry =
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
                        docsUrl = "${Catalog.DOCS_BASE}/form-layout",
                        sourcePath = "pages/layout/FormLayoutPage.kt",
                    ),
                kitPage = { FormLayoutPage.NAME() },
            )

        private val english: FormLayoutTexts =
            FormLayoutTexts(
                autoHeading = "Auto-responsive mode",
                autoHint = "autoResponsive creates columns from the available width, each as wide as " +
                    "columnWidth (12em by default). Every field starts a new row unless fields are grouped " +
                    "with form-row.",
                firstName = "First name",
                lastName = "Last name",
                emailAddress = "Email address",
                password = "Password",
                confirmPassword = "Confirm password",
                labelsHeading = "Label position",
                labelsHint = "labelsAside makes form-item prefer placing its label beside the field and fall " +
                    "back above it when there is not enough room.",
                columnsHeading = "Column width and count",
                columnsHint = "columnWidth sets the width of a column, expandColumns lets columns share the " +
                    "remaining width and expandFields stretches the fields, including those inside form-item.",
                colspanHeading = "Column span",
                colspanHint = "data-colspan on a field makes it span several columns; it only applies inside " +
                    "form-row with autoResponsive enabled.",
                street = "Street address",
                postal = "Postal code",
                city = "City/Town",
                country = "Country",
                stepsHeading = "Responsive steps",
                stepsHint = "Without autoResponsive, responsiveSteps describes the breakpoints and column " +
                    "counts; the first step usually starts at 0.",
                asideCaption = "form-item puts the label aside by default",
            )
        private val chinese: FormLayoutTexts =
            FormLayoutTexts(
                autoHeading = "自动响应式模式",
                autoHint = "autoResponsive 按可用宽度自动创建列，列宽由 columnWidth 指定（默认 12em）。每个字段默认独占一行，" +
                    "用 form-row 可以把字段并到同一行。",
                firstName = "名",
                lastName = "姓",
                emailAddress = "邮箱地址",
                password = "密码",
                confirmPassword = "确认密码",
                labelsHeading = "标签位置",
                labelsHint = "labelsAside 让 form-item 优先把标签放在字段左侧；宽度不够时自动改回字段上方。",
                columnsHeading = "列宽与列数",
                columnsHint = "columnWidth 决定一列多宽，expandColumns 让列平分剩余宽度，expandFields 让字段撑满所在列" +
                    "（包括 form-item 里的字段）。",
                colspanHeading = "跨列",
                colspanHint = "字段上的 data-colspan 让它占据多列；只能用在 form-row 里，并且需要 autoResponsive。",
                street = "街道地址",
                postal = "邮政编码",
                city = "城市/镇",
                country = "国家",
                stepsHeading = "响应式断点",
                stepsHint = "不用 autoResponsive 时用 responsiveSteps 描述断点与列数；首项通常从 0 开始。",
                asideCaption = "form-item 默认把标签放在左侧",
            )
        private val formLayoutTexts: Translations<FormLayoutTexts> = texts(
            Langs.EN to english,
            Langs.ZH_HANS to chinese,
        )
        /**
         * `<vaadin-demo-form-frame>`: puts a form layout next to an empty pane in a split layout, as the docs
         * examples do — dragging the splitter changes the width of the form and the responsive column count
         * follows it.
         */

        /** A `<vaadin-form-row>` grouping fields into one row of an auto-responsive form layout. */
        private fun formRow(vararg fields: HTMLElement): HTMLElement {
            val row = createFormRow()
            row.append(*fields)
            return row
        }
    }
}

internal fun textField(label: String): HTMLElement = createTextField { this.label = label }

internal fun passwordField(label: String): HTMLElement = createPasswordField { this.label = label }
