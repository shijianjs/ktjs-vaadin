package demo.pages.buttons


import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import web.components.CustomElementReference
import web.dom.TagName
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import demo.LanguageState
import demo.componentApiUrl
import demo.ui.REPO_SOURCE_ROOT
import demo.ui.MUTED_SMALL
import demo.ui.SMALL
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.page.anchor as kitAnchor
import io.github.shijianjs.ktjs.demo.page.exampleBox
import io.github.shijianjs.ktjs.demo.page.row as kitRow
import io.github.shijianjs.ktjs.demo.page.col as kitCol
import io.github.shijianjs.ktjs.demo.page.sectionBlock
import web.html.HTMLAnchorElement
import io.github.shijianjs.ktjs.demo.page.exampleCaption
import io.github.shijianjs.ktjs.vaadin.verticallayout.VerticalLayoutElement
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.pages.layout.emailField
import demo.pages.overlays.ariaLabel
import demo.tr
import demo.ui.EventLog
import demo.ui.Images
import demo.ui.PageMeta
import demo.ui.Readout
import demo.ui.jsArray
import demo.ui.listen
import js.objects.unsafeJso
import kotlin.js.JsAny
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.emailfield.createEmailField
import io.github.shijianjs.ktjs.vaadin.formlayout.FormLayoutLabelsPositions
import io.github.shijianjs.ktjs.vaadin.formlayout.FormLayoutResponsiveStep
import io.github.shijianjs.ktjs.vaadin.formlayout.StringOrDouble
import io.github.shijianjs.ktjs.vaadin.formlayout.createFormLayout
import io.github.shijianjs.ktjs.vaadin.grid.GridEvents
import io.github.shijianjs.ktjs.vaadin.grid.createGrid
import io.github.shijianjs.ktjs.vaadin.grid.createGridColumn
import io.github.shijianjs.ktjs.vaadin.grid.createGridSelectionColumn
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.vaadin.icons.registerVaadinIcons
import io.github.shijianjs.ktjs.vaadin.progressbar.createProgressBar
import io.github.shijianjs.ktjs.vaadin.textfield.createTextField
import io.github.shijianjs.ktjs.vaadin.tooltip.createTooltip
import web.dom.clickEvent
import web.dom.document
import web.events.addHandler
import web.html.HTMLElement
import web.html.HtmlTagName

val buttonEntry: CatalogEntry =
    CatalogEntry(
        id = "button",
        group = Groups.BUTTONS,
        meta =
            PageMeta(
                title = bi("Button", "Button"),
                summary =
                    bi(
                        "可点击的按钮：主题变体、尺寸、图标插槽、禁用状态与点击事件。",
                        "Clickable buttons: theme variants, sizes, icon slots, disabled state and click events.",
                    ),
                docsUrl = "$DOCS_BASE/button",
                sourcePath = "pages/buttons/ButtonPage.kt",
            ),
        kitPage = { newButtonPage() },
    )

/** Every string this page shows, in one language. */
private class ButtonTexts(
    val docsLink: String,
    val sourceLink: String,
    val apiLink: String,
    val basicTitle: String,
    val basicHint: String,
    val clicksPrefix: String,
    val saveChanges: String,
    val eventLog: String,
    val clickLogPrefix: String,
    val clickLogSuffix: String,
    val iconsTitle: String,
    val iconsHint: String,
    val addItem: String,
    val close: String,
    val closeDialog: String,
    val left: String,
    val right: String,
    val imagesTitle: String,
    val imagesHint: String,
    val vaadinLogo: String,
    val disabledTitle: String,
    val disabledHint: String,
    val enabledButton: String,
    val enable: String,
    val disable: String,
    val statePrefix: String,
    val stateEnabled: String,
    val stateEnabledClicked: String,
    val stateDisabled: String,
    val focusTitle: String,
    val focusHint: String,
    val keyboardFocus: String,
    val plainButton: String,
    val formsTitle: String,
    val formsHint: String,
    val emailAddress: String,
    val firstName: String,
    val lastName: String,
    val createAccount: String,
    val cancel: String,
    val delete: String,
    val formActions: String,
    val dialogFooter: String,
    val labelsTitle: String,
    val labelsHint: String,
    val primaryEmail: String,
    val secondaryEmail: String,
    val remove: String,
    val sizesTitle: String,
    val sizesHint: String,
    val stylesTitle: String,
    val stylesHint: String,
    val variantsHintCaption: String,
    val defaultVariant: String,
    val coloursTitle: String,
    val coloursHint: String,
    val toolbarTitle: String,
    val toolbarHint: String,
    val email: String,
    val editProfile: String,
    val managePermissions: String,
    val resetPassword: String,
    val rowsPrefix: String,
    val rowsSuffix: String,
    val users: String,
    val addUser: String,
    val longTitle: String,
    val longHint: String,
    val perform: String,
)

private val buttonEnglish: ButtonTexts = ButtonTexts(
    docsLink = "Vaadin documentation",
    sourceLink = "Kotlin source",
    apiLink = "Kotlin API reference",
    basicTitle = "Basic usage",
    basicHint = "Clicking a button fires the click event; the count and the event log are shown below.",
    clicksPrefix = "Clicks: ",
    saveChanges = "Save changes",
    eventLog = "Event log",
    clickLogPrefix = "click · attempt ",
    clickLogSuffix = "",
    iconsTitle = "Buttons with icons",
    iconsHint = "Icon-only buttons need an aria-label as a textual alternative; the tooltip slot adds a " +
        "description; the prefix / suffix slots place icons next to the label.",
    addItem = "Add item",
    close = "Close",
    closeDialog = "Close the dialog",
    left = "Left",
    right = "Right",
    imagesTitle = "Buttons with images",
    imagesHint = "The icon slots accept plain images as well; describe them with alt text.",
    vaadinLogo = "Vaadin logo",
    disabledTitle = "Disabled",
    disabledHint = "A disabled button ignores clicks and does not submit forms; toggle it live below.",
    enabledButton = "Enabled button",
    enable = "Enable",
    disable = "Disable",
    statePrefix = "State: ",
    stateEnabled = "enabled",
    stateEnabledClicked = "enabled, just clicked",
    stateDisabled = "disabled",
    focusTitle = "Keyboard focus",
    focusHint = "focus-ring appears only when the button is focused with the keyboard, not on mouse click; " +
        "the example sets the state attribute directly.",
    keyboardFocus = "Keyboard focus",
    plainButton = "Plain button (try Tab)",
    formsTitle = "Buttons in forms and dialogs",
    formsHint = "The form actions follow the primary / secondary order; a dialog puts cancel / confirm in " +
        "slot=\"end\" so they align to the trailing edge.",
    emailAddress = "Email address",
    firstName = "First name",
    lastName = "Last name",
    createAccount = "Create account",
    cancel = "Cancel",
    delete = "Delete",
    formActions = "Form actions",
    dialogFooter = "Dialog footer",
    labelsTitle = "Button labels",
    labelsHint = "A field and its button sit in one row aligned on the baseline, and the button label names " +
        "the field it acts on.",
    primaryEmail = "Primary email address",
    secondaryEmail = "Secondary email address",
    remove = "Remove",
    sizesTitle = "Sizes",
    sizesHint = "large / normal / small change the padding and font size; they can be mixed in one row.",
    stylesTitle = "Style variants",
    stylesHint = "The theme property accepts several space-separated variants; primary / secondary / " +
        "tertiary express the action hierarchy and tertiary-inline is for inline actions.",
    variantsHintCaption = "Click any variant to record an event",
    defaultVariant = "Default",
    coloursTitle = "Semantic colours",
    coloursHint = "success / warning / error combine with the action hierarchy to convey the outcome or " +
        "the risk of the action.",
    toolbarTitle = "Buttons in a grid toolbar",
    toolbarHint = "Toolbar buttons depend on the selection: edit-style actions need exactly one row, " +
        "delete is enabled as soon as something is selected.",
    email = "Email",
    editProfile = "Edit profile",
    managePermissions = "Manage permissions",
    resetPassword = "Reset password",
    rowsPrefix = "",
    rowsSuffix = " rows selected",
    users = "Users",
    addUser = "Add user",
    longTitle = "Long-running action",
    longHint = "The button is disabled while the action runs so it cannot be submitted twice; the progress " +
        "bar is simulated locally, there is no backend request.",
    perform = "Perform action",
)

private val buttonChinese: ButtonTexts = ButtonTexts(
    docsLink = "Vaadin 官方文档",
    sourceLink = "查看 Kotlin 源码",
    apiLink = "Kotlin API 文档",
    basicTitle = "基本用法",
    basicHint = "点击按钮触发 click 事件，页面把次数和事件流写在下面。",
    clicksPrefix = "点击次数：",
    saveChanges = "保存更改",
    eventLog = "事件流",
    clickLogPrefix = "click · 第 ",
    clickLogSuffix = " 次",
    iconsTitle = "图标按钮",
    iconsHint = "只有图标的按钮要用 aria-label 提供文本替代；tooltip 插槽可以补充说明；prefix / suffix " +
        "插槽把图标放在文字两侧。",
    addItem = "添加项目",
    close = "关闭",
    closeDialog = "关闭对话框",
    left = "向左",
    right = "向右",
    imagesTitle = "图片按钮",
    imagesHint = "图标位置也可以放普通图片，用 alt 文本描述图片内容。",
    vaadinLogo = "Vaadin 标志",
    disabledTitle = "禁用状态",
    disabledHint = "禁用按钮不响应点击，也不会提交表单；下面可以实时切换。",
    enabledButton = "启用中的按钮",
    enable = "切换到启用",
    disable = "切换到禁用",
    statePrefix = "当前状态：",
    stateEnabled = "启用",
    stateEnabledClicked = "启用，刚刚被点击",
    stateDisabled = "禁用",
    focusTitle = "键盘焦点",
    focusHint = "focus-ring 只在使用键盘聚焦时出现，鼠标点击不会显示；下面用属性直接展示该状态。",
    keyboardFocus = "键盘焦点",
    plainButton = "普通按钮（按 Tab 试试）",
    formsTitle = "表单与对话框中的按钮",
    formsHint = "表单底部的按钮按主次排列；对话框把取消 / 确认放在 slot=\"end\"，让它们靠右对齐。",
    emailAddress = "邮箱地址",
    firstName = "名",
    lastName = "姓",
    createAccount = "创建账户",
    cancel = "取消",
    delete = "删除",
    formActions = "表单操作按钮",
    dialogFooter = "对话框页脚",
    labelsTitle = "按钮标签",
    labelsHint = "并排的字段和按钮用行内布局对齐基线，按钮文字直接说明作用于哪个字段。",
    primaryEmail = "首选邮箱",
    secondaryEmail = "备用邮箱",
    remove = "移除",
    sizesTitle = "尺寸",
    sizesHint = "large / normal / small 改变内边距与字号，三者可以并排使用。",
    stylesTitle = "样式变体",
    stylesHint = "theme 属性接受空格分隔的多个变体；primary / secondary / tertiary 表达操作层级，" +
        "tertiary-inline 用于正文中的行内操作。",
    variantsHintCaption = "点击任意变体记录事件",
    defaultVariant = "默认",
    coloursTitle = "语义颜色",
    coloursHint = "success / warning / error 与操作层级组合使用，传达操作的结果或风险。",
    toolbarTitle = "表格中的按钮",
    toolbarHint = "工具栏按钮按选中行数启停：编辑类操作要求恰好选中一行，删除在有选中项时可用。",
    email = "邮箱",
    editProfile = "编辑资料",
    managePermissions = "管理权限",
    resetPassword = "重置密码",
    rowsPrefix = "已选 ",
    rowsSuffix = " 行",
    users = "用户",
    addUser = "添加用户",
    longTitle = "长时操作",
    longHint = "操作进行中禁用按钮，避免重复提交；进度条只在本地模拟，没有后端请求。",
    perform = "执行操作",
)

private val buttonTexts: Translations<ButtonTexts> = texts(
    Langs.EN to buttonEnglish,
    Langs.ZH_HANS to buttonChinese,
)

/**
 * The same page on kit blocks.
 *
 * The most stateful page of the demo: a click counter, a live enable / disable switch and a grid
 * selection that decides which toolbar buttons are usable. All of it lives in page fields, so a
 * language switch only rewrites the text and every one of those states survives.
 */

internal class ButtonPage : DemoPage() {
    private val meta: PageMeta = buttonEntry.meta


    private val texts = Binder(buttonTexts)

    // --- one element per example that owns state ---------------------------------------------------

    private val clickCounter = newClickCounter()

    private val enabledToggle = newEnabledToggle()

    private val variantLog = newVariantLog()

    private val gridToolbar = newGridToolbar()

    private val longAction = newLongAction()

    private val primaryEmailRow = newEmailRow()

    private val secondaryEmailRow = newEmailRow()

    // --- single components whose text is translated, in examples without state ----------------------

    private val addItemButton = createButton()

    private val closeButton = createButton()

    private val closeTooltip = createTooltip()

    private val leftButton = createButton()

    private val rightButton = createButton()

    private val logoButton = createButton()

    private val logo = HtmlTagName.img { src = Images.vaadinLogo; width = 100 }

    private val focusButton = createButton().apply { setAttribute("focus-ring", "") }

    private val plainFocusButton = createButton()

    private val emailField = createEmailField { value = "john.smith@example.com" }.apply {
        setAttribute("colspan", "2")
    }

    private val firstNameField = createTextField { value = "John" }

    private val lastNameField = createTextField { value = "Smith" }

    private val createAccountButton = createButton().addTheme("primary")

    private val cancelButton = createButton().addTheme("secondary")

    private val dialogDeleteButton = createButton().addTheme("error")

    private val dialogCancelButton = createButton().apply { slot = "end" }

    private val dialogCreateButton = createButton().addTheme("primary").apply { slot = "end" }

    private val variantCaption = texts.bind(exampleCaption(), { it.variantsHintCaption })

    private val links = mutableListOf<HTMLAnchorElement>()


    init {
        registerVaadinIcons()
        val docs = kitAnchor("", meta.docsUrlFor(Langs.EN))
        val source = kitAnchor("", "$REPO_SOURCE_ROOT/${meta.sourcePath}")
        val api = kitAnchor("", componentApiUrl(buttonEntry.id))
        links += docs
        links += source
        links += api
        addLink(texts.bind(docs, { it.docsLink }))
        addLink(texts.bind(source, { it.sourceLink }))
        addLink(texts.bind(api, { it.apiLink }))

        body.append(
            basicSection(),
            iconsSection(),
            imagesSection(),
            disabledSection(),
            focusSection(),
            formsSection(),
            labelsSection(),
            sizesSection(),
            stylesSection(),
            coloursSection(),
            toolbarSection(),
            longSection(),
        )

        applyTranslation(Langs.EN)
    }

    private fun basicSection(): HTMLElement {
        val section = sectionBlock()
        texts.bind(section.heading, { it.basicTitle })
        texts.bind(section.hint, { it.basicHint })
        section.body.append(exampleBox(null, clickCounter))
        return section.root
    }

    private fun iconsSection(): HTMLElement {
        val section = sectionBlock()
        texts.bind(section.heading, { it.iconsTitle })
        texts.bind(section.hint, { it.iconsHint })
        if (addItemButton.childElementCount == 0) {
            addItemButton.append(createIcon { icon = "vaadin:plus" })
            closeButton.append(createIcon { icon = "vaadin:close" })
            closeTooltip.slot = "tooltip"
            closeButton.append(closeTooltip)
            leftButton.append(createIcon { icon = "vaadin:arrow-left" }.apply { slot = "prefix" })
            rightButton.append(createIcon { icon = "vaadin:arrow-right" }.apply { slot = "suffix" })
        }
        section.body.append(exampleBox(null, kitRow(addItemButton, closeButton, leftButton, rightButton)))
        return section.root
    }

    private fun imagesSection(): HTMLElement {
        val section = sectionBlock()
        texts.bind(section.heading, { it.imagesTitle })
        texts.bind(section.hint, { it.imagesHint })
        texts.bind(logo, { it.vaadinLogo }) { text -> logo.alt = text }
        logoButton.append(logo)
        section.body.append(exampleBox(null, logoButton))
        return section.root
    }

    private fun disabledSection(): HTMLElement {
        val section = sectionBlock()
        texts.bind(section.heading, { it.disabledTitle })
        texts.bind(section.hint, { it.disabledHint })
        // The three disabled variants carry the variant names themselves — those are the values, not copy.
        val disabledVariants = kitRow(
            createButton { textContent = "primary" }.addTheme("primary").apply { disabled = true },
            createButton { textContent = "secondary" }.addTheme("secondary").apply { disabled = true },
            createButton { textContent = "tertiary" }.addTheme("tertiary").apply { disabled = true },
        )
        section.body.append(exampleBox(null, kitCol(disabledVariants, enabledToggle)))
        return section.root
    }

    private fun focusSection(): HTMLElement {
        val section = sectionBlock()
        texts.bind(section.heading, { it.focusTitle })
        texts.bind(section.hint, { it.focusHint })
        section.body.append(exampleBox(null, kitRow(focusButton, plainFocusButton)))
        return section.root
    }

    private fun formsSection(): HTMLElement {
        val section = sectionBlock()
        texts.bind(section.heading, { it.formsTitle })
        texts.bind(section.hint, { it.formsHint })
        val form = createFormLayout().apply {
            responsiveSteps =
                arrayOf(
                    unsafeJso<FormLayoutResponsiveStep> {
                        minWidth = StringOrDouble("0px")
                        columns = 2.0
                        labelsPosition = FormLayoutLabelsPositions.top
                    },
                )
        }
        form.append(firstNameField, lastNameField, emailField)
        section.body.append(
            exampleBox(
                texts.current.formActions,
                kitCol(form, kitRow(createAccountButton, cancelButton)),
            ),
            exampleBox(
                texts.current.dialogFooter,
                kitRow(dialogDeleteButton, dialogCancelButton, dialogCreateButton),
            ),
        )
        return section.root
    }

    private fun labelsSection(): HTMLElement {
        val section = sectionBlock()
        texts.bind(section.heading, { it.labelsTitle })
        texts.bind(section.hint, { it.labelsHint })
        section.body.append(exampleBox(null, kitCol(primaryEmailRow, secondaryEmailRow)))
        return section.root
    }

    private fun sizesSection(): HTMLElement {
        val section = sectionBlock()
        texts.bind(section.heading, { it.sizesTitle })
        texts.bind(section.hint, { it.sizesHint })
        section.body.append(
            exampleBox(
                null,
                kitRow(
                    createButton { textContent = "Large" }.addTheme("large"),
                    createButton { textContent = "Normal" }.addTheme("normal"),
                    createButton { textContent = "Small" }.addTheme("small"),
                ),
            ),
        )
        return section.root
    }

    private fun stylesSection(): HTMLElement {
        val section = sectionBlock()
        texts.bind(section.heading, { it.stylesTitle })
        texts.bind(section.hint, { it.stylesHint })
        val variants = kitRow(
            createButton { textContent = "Primary" }.addTheme("primary"),
            createButton { textContent = "Secondary" }.addTheme("secondary"),
            createButton { textContent = "Tertiary" }.addTheme("tertiary"),
            createButton { textContent = "Tertiary inline" }.addTheme("tertiary-inline"),
            createButton { textContent = "Contrast" }.addTheme("contrast"),
        )
        section.body.append(
            exampleBox(null, variants),
            exampleBox(null, variantCaption, variantLog),
        )
        return section.root
    }

    private fun coloursSection(): HTMLElement {
        val section = sectionBlock()
        texts.bind(section.heading, { it.coloursTitle })
        texts.bind(section.hint, { it.coloursHint })
        // The variant names are the captions here; they are the same word in both languages.
        for (variant in listOf("success", "warning", "error")) {
            section.body.append(
                exampleBox(
                    variant,
                    kitRow(
                        createButton { textContent = "Primary" }.addTheme("primary", variant),
                        createButton { textContent = "Secondary" }.addTheme("secondary", variant),
                        createButton { textContent = "Tertiary" }.addTheme("tertiary", variant),
                    ),
                ),
            )
        }
        return section.root
    }

    private fun toolbarSection(): HTMLElement {
        val section = sectionBlock()
        texts.bind(section.heading, { it.toolbarTitle })
        texts.bind(section.hint, { it.toolbarHint })
        section.body.append(exampleBox(null, gridToolbar))
        return section.root
    }

    private fun longSection(): HTMLElement {
        val section = sectionBlock()
        texts.bind(section.heading, { it.longTitle })
        texts.bind(section.hint, { it.longHint })
        section.body.append(exampleBox(null, longAction))
        return section.root
    }

    override fun applyTranslation(lang: Lang) {
        texts.apply(lang)
        val copy = texts.current
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        links.first().href = meta.docsUrlFor(lang)

        clickCounter.buttonLabel = copy.saveChanges
        clickCounter.clicksLabel = copy.clicksPrefix
        clickCounter.logPrefix = copy.clickLogPrefix
        clickCounter.logSuffix = copy.clickLogSuffix
        clickCounter.logCaption = copy.eventLog

        enabledToggle.targetLabel = copy.enabledButton
        enabledToggle.enableLabel = copy.enable
        enabledToggle.disableLabel = copy.disable
        enabledToggle.statePrefix = copy.statePrefix
        enabledToggle.stateEnabled = copy.stateEnabled
        enabledToggle.stateClicked = copy.stateEnabledClicked
        enabledToggle.stateDisabled = copy.stateDisabled

        variantLog.defaultLabel = copy.defaultVariant

        gridToolbar.gridTitle = copy.users
        gridToolbar.addLabel = copy.addUser
        gridToolbar.editLabel = copy.editProfile
        gridToolbar.manageLabel = copy.managePermissions
        gridToolbar.resetLabel = copy.resetPassword
        gridToolbar.deleteLabel = copy.delete
        gridToolbar.rowsPrefix = copy.rowsPrefix
        gridToolbar.rowsSuffix = copy.rowsSuffix
        gridToolbar.setHeader("firstName", copy.firstName)
        gridToolbar.setHeader("lastName", copy.lastName)
        gridToolbar.setHeader("email", copy.email)

        longAction.performLabel = copy.perform

        primaryEmailRow.removeLabel = copy.remove
        primaryEmailRow.subject = copy.primaryEmail
        primaryEmailRow.setField(copy.primaryEmail, "foo@example.com")
        secondaryEmailRow.removeLabel = copy.remove
        secondaryEmailRow.subject = copy.secondaryEmail
        secondaryEmailRow.setField(copy.secondaryEmail, "bar@example.com")

        addItemButton.textContent = copy.addItem
        addItemButton.ariaLabel = copy.addItem
        closeButton.ariaLabel = copy.close
        closeTooltip.text = copy.closeDialog
        leftButton.textContent = copy.left
        rightButton.textContent = copy.right
        focusButton.textContent = copy.keyboardFocus
        plainFocusButton.textContent = copy.plainButton
        emailField.label = copy.emailAddress
        firstNameField.label = copy.firstName
        lastNameField.label = copy.lastName
        createAccountButton.textContent = copy.createAccount
        cancelButton.textContent = copy.cancel
        dialogDeleteButton.textContent = copy.delete
        dialogCancelButton.textContent = copy.cancel
        dialogCreateButton.textContent = copy.createAccount
    }

    companion object : CustomElementReference<ButtonPage>(ButtonPage::class.js) {
        val NAME: TagName<ButtonPage> = TagName("vaadin-demo-button-page")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<vaadin-demo-button-page>`. */
internal fun newButtonPage(): ButtonPage {
    ButtonPage.define()
    return ButtonPage.NAME()
}


