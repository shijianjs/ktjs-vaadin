package demo.pages.buttons

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
import demo.ui.PageView
import demo.ui.Readout
import demo.ui.column
import demo.ui.element
import demo.ui.example
import demo.ui.jsArray
import demo.ui.listen
import demo.ui.page
import demo.ui.row
import demo.ui.section
import demo.ui.withTheme
import js.objects.unsafeJso
import kotlin.js.JsAny
import vaadin.button.createButton
import vaadin.emailfield.createEmailField
import vaadin.formlayout.FormLayoutLabelsPositions
import vaadin.formlayout.FormLayoutResponsiveStep
import vaadin.formlayout.StringOrDouble
import vaadin.formlayout.createFormLayout
import vaadin.grid.GridEvents
import vaadin.grid.createGrid
import vaadin.grid.createGridColumn
import vaadin.grid.createGridSelectionColumn
import vaadin.icon.createIcon
import vaadin.icons.registerVaadinIcons
import vaadin.progressbar.createProgressBar
import vaadin.textfield.createTextField
import vaadin.tooltip.createTooltip
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
        builder = ::buildButtonPage,
    )

private fun startInterval(millis: Int, tick: () -> Unit) {
    stopInterval()
    intervalHandle = js("(fn, ms) => window.setInterval(fn, ms)")(tick, millis) as Int
}

private fun stopInterval() {
    val handle = intervalHandle ?: return
    js("(handle) => window.clearInterval(handle)")(handle)
    intervalHandle = null
}

private const val BUTTON_GRID_USERS =
    """
    [
      {"firstName": "Ada", "lastName": "Lovelace", "email": "ada@example.com"},
      {"firstName": "Grace", "lastName": "Hopper", "email": "grace@example.com"},
      {"firstName": "Margaret", "lastName": "Hamilton", "email": "margaret@example.com"},
      {"firstName": "Katherine", "lastName": "Johnson", "email": "katherine@example.com"}
    ]
    """

private fun buildButtonPage(meta: PageMeta): PageView {
    val view = page(meta)
    registerVaadinIcons()

    // --- Basic usage ----------------------------------------------------------------------------
    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "点击按钮触发 click 事件，页面把次数和事件流写在下面。",
                "Clicking a button fires the click event; the count and the event log are shown below.",
            ),
        )
    val clicks = Readout(tr("点击次数：0", "Clicks: 0"))
    val log = EventLog()
    var count = 0
    val saveButton = createButton { textContent = tr("保存更改", "Save changes") }
    saveButton.dataset["test"] = "basic-button"
    saveButton.clickEvent.addHandler {
        count++
        clicks.set(tr("点击次数：$count", "Clicks: $count"))
        log.add(tr("click · 第 $count 次", "click · attempt $count"))
    }
    basic.body.append(
        example(null, row(saveButton, clicks.root)),
        example(tr("事件流", "Event log"), log.root),
    )
    view.body.append(basic.root)

    // --- Buttons with icons ---------------------------------------------------------------------
    val iconSection =
        section(
            tr("图标按钮", "Buttons with icons"),
            tr(
                "只有图标的按钮要用 aria-label 提供文本替代；tooltip 插槽可以补充说明；prefix / suffix " +
                    "插槽把图标放在文字两侧。",
                "Icon-only buttons need an aria-label as a textual alternative; the tooltip slot adds a " +
                    "description; the prefix / suffix slots place icons next to the label.",
            ),
        )
    val addItem = createButton()
    addItem.ariaLabel = tr("添加项目", "Add item")
    addItem.append(createIcon { icon = "vaadin:plus" })
    val closeButton = createButton()
    closeButton.ariaLabel = tr("关闭", "Close")
    closeButton.append(createIcon { icon = "vaadin:close" })
    closeButton.append(createTooltip { text = tr("关闭对话框", "Close the dialog") }.apply { slot = "tooltip" })
    val moveLeft = createButton { textContent = tr("向左", "Left") }
    moveLeft.append(createIcon { icon = "vaadin:arrow-left" }.apply { slot = "prefix" })
    val moveRight = createButton { textContent = tr("向右", "Right") }
    moveRight.append(createIcon { icon = "vaadin:arrow-right" }.apply { slot = "suffix" })
    iconSection.body.append(example(null, row(addItem, closeButton, moveLeft, moveRight)))
    view.body.append(iconSection.root)

    // --- Buttons with images --------------------------------------------------------------------
    val imageSection =
        section(
            tr("图片按钮", "Buttons with images"),
            tr(
                "图标位置也可以放普通图片，用 alt 文本描述图片内容。",
                "The icon slots accept plain images as well; describe them with alt text.",
            ),
        )
    val logoButton = createButton()
    val logo = document.createElement(HtmlTagName.img)
    logo.src = Images.vaadinLogo
    logo.width = 100
    logo.alt = tr("Vaadin 标志", "Vaadin logo")
    logoButton.append(logo)
    imageSection.body.append(example(null, logoButton))
    view.body.append(imageSection.root)

    // --- Disabled -------------------------------------------------------------------------------
    val disabledSection =
        section(
            tr("禁用状态", "Disabled"),
            tr(
                "禁用按钮不响应点击，也不会提交表单；下面可以实时切换。",
                "A disabled button ignores clicks and does not submit forms; toggle it live below.",
            ),
        )
    val target = createButton { textContent = tr("启用中的按钮", "Enabled button") }.withTheme("primary")
    val stateReadout = Readout(tr("当前状态：启用", "State: enabled"))
    target.clickEvent.addHandler {
        stateReadout.set(tr("当前状态：启用，刚刚被点击", "State: enabled, just clicked"))
    }
    val toggleButton =
        createButton { textContent = tr("切换到禁用", "Disable") }.apply {
            clickEvent.addHandler {
                target.disabled = !target.disabled
                textContent = if (target.disabled) tr("切换到启用", "Enable") else tr("切换到禁用", "Disable")
                stateReadout.set(
                    tr(
                        if (target.disabled) "当前状态：禁用" else "当前状态：启用",
                        if (target.disabled) "State: disabled" else "State: enabled",
                    ),
                )
            }
        }

    disabledSection.body.append(
        example(
            null,
            column(
                row(
                    createButton { textContent = "primary" }.withTheme("primary").apply { this.disabled = true },
                    createButton { textContent = "secondary" }.withTheme("secondary").apply { this.disabled = true },
                    createButton { textContent = "tertiary" }.withTheme("tertiary").apply { this.disabled = true },
                ),
                row(target, toggleButton, stateReadout.root),
            ),
        ),
    )
    view.body.append(disabledSection.root)

    // --- Keyboard focus -------------------------------------------------------------------------
    val focus =
        section(
            tr("键盘焦点", "Keyboard focus"),
            tr(
                "focus-ring 只在使用键盘聚焦时出现，鼠标点击不会显示；下面用属性直接展示该状态。",
                "focus-ring appears only when the button is focused with the keyboard, not on mouse click; " +
                    "the example sets the state attribute directly.",
            ),
        )
    val focusButton = createButton { textContent = tr("键盘焦点", "Keyboard focus") }
    focusButton.setAttribute("focus-ring", "")
    focus.body.append(
        example(
            null,
            column(
                row(focusButton, createButton { textContent = tr("普通按钮（按 Tab 试试）", "Plain button (try Tab)") }),
            ),
        ),
    )
    view.body.append(focus.root)

    // --- Buttons in forms -----------------------------------------------------------------------
    val forms =
        section(
            tr("表单与对话框中的按钮", "Buttons in forms and dialogs"),
            tr(
                "表单底部的按钮按主次排列；对话框把取消 / 确认放在 slot=\"end\"，让它们靠右对齐。",
                "The form actions follow the primary / secondary order; a dialog puts cancel / confirm in " +
                    "slot=\"end\" so they align to the trailing edge.",
            ),
        )
    val form = createFormLayout()
    form.responsiveSteps =
        arrayOf(
            unsafeJso<FormLayoutResponsiveStep> {
                minWidth = StringOrDouble("0px")
                columns = 2.0
                labelsPosition = FormLayoutLabelsPositions.top
            },
        )
    val emailField =
        createEmailField {
            label = tr("邮箱地址", "Email address")
            value = "john.smith@example.com"
        }.apply { setAttribute("colspan", "2") }
    form.append(
        createTextField {
            label = tr("名", "First name")
            value = "John"
        },
        createTextField {
            label = tr("姓", "Last name")
            value = "Smith"
        },
        emailField,
    )
    val formActions =
        row(
            createButton { textContent = tr("创建账户", "Create account") }.withTheme("primary"),
            createButton { textContent = tr("取消", "Cancel") }.withTheme("secondary"),
        )
    val dialogActions =
        row(
            createButton { textContent = tr("删除", "Delete") }.withTheme("error"),
            createButton { textContent = tr("取消", "Cancel") }.apply { slot = "end" },
            createButton { textContent = tr("创建账户", "Create account") }.withTheme("primary").apply { slot = "end" },
        )
    forms.body.append(
        example(tr("表单操作按钮", "Form actions"), column(form, formActions)),
        example(tr("对话框页脚", "Dialog footer"), dialogActions),
    )
    view.body.append(forms.root)

    // --- Button labels --------------------------------------------------------------------------
    val labels =
        section(
            tr("按钮标签", "Button labels"),
            tr(
                "并排的字段和按钮用行内布局对齐基线，按钮文字直接说明作用于哪个字段。",
                "A field and its button sit in one row aligned on the baseline, and the button label names " +
                    "the field it acts on.",
            ),
        )
    fun emailRow(label: String, initial: String): HTMLElement {
        val field =
            createEmailField {
                this.label = label
                value = initial
            }
        val remove =
            createButton { textContent = tr("移除", "Remove") }.apply {
                ariaLabel = tr("移除 $label", "Remove $label")
                clickEvent.addHandler { field.value = "" }
            }
        return row(field, remove)
    }
    labels.body.append(
        example(
            null,
            column(
                emailRow(tr("首选邮箱", "Primary email address"), "foo@example.com"),
                emailRow(tr("备用邮箱", "Secondary email address"), "bar@example.com"),
            ),
        ),
    )
    view.body.append(labels.root)

    // --- Sizes ----------------------------------------------------------------------------------
    val sizing =
        section(
            tr("尺寸", "Sizes"),
            tr(
                "large / normal / small 改变内边距与字号，三者可以并排使用。",
                "large / normal / small change the padding and font size; they can be mixed in one row.",
            ),
        )
    sizing.body.append(
        example(
            null,
            row(
                createButton { textContent = "Large" }.withTheme("large"),
                createButton { textContent = "Normal" }.withTheme("normal"),
                createButton { textContent = "Small" }.withTheme("small"),
            ),
        ),
    )
    view.body.append(sizing.root)

    // --- Style variants -------------------------------------------------------------------------
    val styles =
        section(
            tr("样式变体", "Style variants"),
            tr(
                "theme 属性接受空格分隔的多个变体；primary / secondary / tertiary 表达操作层级，" +
                    "tertiary-inline 用于正文中的行内操作。",
                "The theme property accepts several space-separated variants; primary / secondary / " +
                    "tertiary express the action hierarchy and tertiary-inline is for inline actions.",
            ),
        )
    val variantLog = EventLog(4)
    styles.body.append(
        example(
            null,
            row(
                createButton { textContent = "Primary" }.withTheme("primary"),
                createButton { textContent = "Secondary" }.withTheme("secondary"),
                createButton { textContent = "Tertiary" }.withTheme("tertiary"),
                createButton { textContent = "Tertiary inline" }.withTheme("tertiary-inline"),
                createButton { textContent = "Contrast" }.withTheme("contrast"),
            ),
        ),
        example(
            tr("点击任意变体记录事件", "Click any variant to record an event"),
            row(
                *listOf(
                    tr("默认", "Default") to listOf<String>(),
                    "primary" to listOf("primary"),
                    "tertiary" to listOf("tertiary"),
                    "tertiary-inline" to listOf("tertiary-inline"),
                    "contrast" to listOf("contrast"),
                ).map { (label, themes) ->
                    createButton { textContent = label }.withTheme(*themes.toTypedArray()).apply {
                        clickEvent.addHandler { variantLog.add("click · $label") }
                    }
                }.toTypedArray(),
            ),
            variantLog.root,
        ),
    )
    view.body.append(styles.root)

    // --- Semantic colours -----------------------------------------------------------------------
    val colours =
        section(
            tr("语义颜色", "Semantic colours"),
            tr(
                "success / warning / error 与操作层级组合使用，传达操作的结果或风险。",
                "success / warning / error combine with the action hierarchy to convey the outcome or the " +
                    "risk of the action.",
            ),
        )
    fun colourRow(variant: String): HTMLElement =
        row(
            createButton { textContent = "Primary" }.withTheme("primary", variant),
            createButton { textContent = "Secondary" }.withTheme("secondary", variant),
            createButton { textContent = "Tertiary" }.withTheme("tertiary", variant),
        )
    colours.body.append(
        example(tr("success", "success"), colourRow("success")),
        example(tr("warning", "warning"), colourRow("warning")),
        example(tr("error", "error"), colourRow("error")),
    )
    view.body.append(colours.root)

    // --- Buttons in a grid toolbar --------------------------------------------------------------
    val toolbar =
        section(
            tr("表格中的按钮", "Buttons in a grid toolbar"),
            tr(
                "工具栏按钮按选中行数启停：编辑类操作要求恰好选中一行，删除在有选中项时可用。",
                "Toolbar buttons depend on the selection: edit-style actions need exactly one row, delete " +
                    "is enabled as soon as something is selected.",
            ),
        )
    val users = createGrid()
    users.dataset["test"] = "button-grid"
    users.style.setProperty("height", "14rem")
    users.items = jsArray<JsAny>(BUTTON_GRID_USERS)
    users.append(
        createGridSelectionColumn { autoSelect = true },
        createGridColumn {
            header = tr("名", "First name")
            path = "firstName"
        },
        createGridColumn {
            header = tr("姓", "Last name")
            path = "lastName"
        },
        createGridColumn {
            header = tr("邮箱", "Email")
            path = "email"
        },
    )
    val editProfile = createButton { textContent = tr("编辑资料", "Edit profile") }
    val managePermissions = createButton { textContent = tr("管理权限", "Manage permissions") }
    val resetPassword = createButton { textContent = tr("重置密码", "Reset password") }
    val deleteUser = createButton { textContent = tr("删除", "Delete") }.withTheme("error")
    val singleSelection = listOf(editProfile, managePermissions, resetPassword)
    val selectionReadout = Readout(tr("已选 0 行", "0 rows selected"))
    fun syncSelection() {
        val size = users.selectedItems.size
        singleSelection.forEach { it.disabled = size != 1 }
        deleteUser.disabled = size == 0
        selectionReadout.set(tr("已选 $size 行", "$size rows selected"))
    }
    users.listen(GridEvents.selectedItemsChanged) { syncSelection() }
    syncSelection()
    val usersTitle = element(HtmlTagName.h3).apply { textContent = tr("用户", "Users") }
    val addUser = createButton { textContent = tr("添加用户", "Add user") }
    toolbar.body.append(
        example(
            null,
            column(
                row(usersTitle, addUser),
                users,
                row(*singleSelection.toTypedArray(), deleteUser),
                selectionReadout.root,
            ),
        ),
    )
    view.body.append(toolbar.root)

    // --- Long-running action --------------------------------------------------------------------
    val longRunning =
        section(
            tr("长时操作", "Long-running action"),
            tr(
                "操作进行中禁用按钮，避免重复提交；进度条只在本地模拟，没有后端请求。",
                "The button is disabled while the action runs so it cannot be submitted twice; the progress " +
                    "bar is simulated locally, there is no backend request.",
            ),
        )
    val progress = createProgressBar { value = 0.0 }
    progress.style.setProperty("flex", "1")
    val perform = createButton { textContent = tr("执行操作", "Perform action") }.withTheme("primary")
    perform.clickEvent.addHandler {
        perform.disabled = true
        progress.value = 0.0
        startInterval(40) {
            progress.value = (progress.value + 0.04).coerceAtMost(1.0)
            if (progress.value >= 1.0) {
                stopInterval()
                perform.disabled = false
            }
        }
    }
    longRunning.body.append(example(null, row(perform, progress)))
    view.body.append(longRunning.root)

    return view
}
