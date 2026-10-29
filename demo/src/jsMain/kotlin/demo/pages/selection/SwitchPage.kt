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
import demo.ui.listen
import demo.ui.page
import demo.ui.row
import demo.ui.section
import vaadin.button.createButton
import vaadin.switch.SwitchEvents
import vaadin.switch.createSwitch
import vaadin.tooltip.createTooltip
import web.dom.clickEvent
import web.events.addHandler

val switchEntry: CatalogEntry =
    CatalogEntry(
        id = "switch",
        group = Groups.SELECTION,
        meta =
            PageMeta(
                title = bi("Switch", "Switch"),
                summary =
                    bi(
                        "二态开关，语义与 Checkbox 相同，视觉上表达即时生效的设置。",
                        "Two-state switch with the same semantics as Checkbox, used for settings that " +
                            "apply immediately.",
                    ),
                docsUrl = "$DOCS_BASE/switch",
                sourcePath = "pages/selection/SwitchPage.kt",
            ),
        builder = ::buildSwitchPage,
    )

private fun buildSwitchPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "switch 与 checkbox 共享状态模型，用 checked 读写。",
                "switch and checkbox share the same state model, read and written through checked.",
            ),
        )
    val notifications = createSwitch { label = tr("接收发布通知", "Receive release notifications") }
    notifications.checked = true
    notifications.dataset["test"] = "switch-basic"
    val readout = Readout(tr("已开启", "On"))
    notifications.listen(SwitchEvents.checkedChanged) {
        readout.set(tr("状态：", "State: "), if (notifications.checked) tr("已开启", "on") else tr("已关闭", "off"))
    }
    basic.body.append(example(null, column(notifications, readout.root)))
    view.body.append(basic.root)

    val features =
        section(
            tr("辅助信息与提示", "Helper text and tooltip"),
            tr(
                "helper-text 说明开关的作用；slot=\"tooltip\" 的 tooltip 补充细节。",
                "helper-text explains what the switch does; a slotted tooltip adds the details.",
            ),
        )
    val autosave =
        createSwitch {
            label = tr("自动保存", "Autosave")
            helperText = tr("工作时自动保存改动", "Automatically save changes as you work")
        }
    autosave.append(
        createTooltip { text = tr("上次保存于 5 分钟前", "Last saved 5 minutes ago") }.apply {
            setAttribute("slot", "tooltip")
        },
    )
    features.body.append(example(null, autosave))
    view.body.append(features.root)

    val validation =
        section(
            tr("校验", "Validation"),
            tr(
                "开关同样支持 required：未满足时 validate() 失败并显示 error-message。",
                "The switch supports required as well: validate() fails while it is off and shows the " +
                    "error-message.",
            ),
        )
    val twoFactor =
        createSwitch {
            label = tr("双因素认证", "Two-factor authentication")
            this.required = true
            checked = true
            errorMessage = tr("公司安全策略要求开启", "Required by your workplace security policy")
        }
    val result = Readout(tr("尚未校验", "Not validated yet"))
    val validateButton =
        createButton { textContent = tr("校验", "Validate") }.apply {
            clickEvent.addHandler {
                result.set(
                    tr("校验：", "Validation: "),
                    if (twoFactor.validate()) tr("通过", "passed") else tr("失败", "failed"),
                )
            }
        }
    validation.body.append(example(null, column(twoFactor, row(validateButton, result.root))))
    view.body.append(validation.root)

    val states =
        section(
            tr("只读与禁用", "Read-only and disabled"),
            tr(
                "readonly 只展示当前状态；disabled 让整行不可交互。",
                "readonly only displays the current state; disabled makes the whole row inert.",
            ),
        )
    states.body.append(
        example(
            null,
            column(
                createSwitch {
                    label = tr("审计日志保留（90 天）", "Audit log retention (90 days)")
                    helperText = tr("商业版套餐已包含", "Included on the Business plan")
                    readonly = true
                    checked = true
                },
                createSwitch {
                    label = tr("每日摘要", "Daily digest")
                    disabled = true
                },
            ),
        ),
    )
    view.body.append(states.root)

    return view
}
