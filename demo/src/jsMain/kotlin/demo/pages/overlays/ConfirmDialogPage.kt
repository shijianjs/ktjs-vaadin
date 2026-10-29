package demo.pages.overlays

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.pages.data.message
import demo.pages.upload.rejectText
import demo.tr
import demo.ui.EventLog
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.Readout
import demo.ui.column
import demo.ui.example
import demo.ui.listen
import demo.ui.page
import demo.ui.paragraph
import demo.ui.row
import demo.ui.section
import demo.ui.withTheme
import vaadin.button.createButton
import vaadin.confirmdialog.ConfirmDialogEvents
import vaadin.confirmdialog.createConfirmDialog
import web.dom.clickEvent
import web.events.addHandler

val confirmDialogEntry: CatalogEntry =
    CatalogEntry(
        id = "confirm-dialog",
        group = Groups.OVERLAYS,
        meta =
            PageMeta(
                title = bi("Confirm Dialog", "Confirm Dialog"),
                summary =
                    bi(
                        "确认对话框：确认 / 取消 / 拒绝三种结果与对应事件。",
                        "Confirmation dialog: the confirm / cancel / reject outcomes and their events.",
                    ),
                docsUrl = "$DOCS_BASE/confirm-dialog",
                sourcePath = "pages/overlays/ConfirmDialogPage.kt",
            ),
        builder = ::buildConfirmDialogPage,
    )

private fun buildConfirmDialogPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "确认 / 取消会分别触发 confirm 与 cancel，之后触发 closed。",
                "Confirm / cancel fire confirm and cancel respectively, followed by closed.",
            ),
        )
    val log = EventLog(6)
    val status = Readout(tr("状态：", "Status: "))
    val confirm = createConfirmDialog()
    confirm.dataset["test"] = "confirm-dialog-basic"
    confirm.header = tr("删除这条记录？", "Delete this record?")
    confirm.message = tr("删除后无法恢复。", "This cannot be undone.")
    confirm.confirmText = tr("删除", "Delete")
    confirm.cancelText = tr("保留", "Keep")
    confirm.cancelButtonVisible = true
    confirm.listen(ConfirmDialogEvents.confirm) {
        log.add("confirm")
        status.set(tr("状态：", "Status: "), tr("已删除", "Deleted"))
    }
    confirm.listen(ConfirmDialogEvents.cancel) {
        log.add("cancel")
        status.set(tr("状态：", "Status: "), tr("已取消", "Canceled"))
    }
    confirm.listen(ConfirmDialogEvents.closed) { log.add("closed") }

    val openButton =
        createButton { textContent = tr("删除记录", "Delete record") }.withTheme("error").apply {
            clickEvent.addHandler {
                confirm.opened = true
                status.set(tr("状态：", "Status: "))
            }
        }
    basic.body.append(example(null, column(row(openButton, status.root), confirm, log.root)))
    view.body.append(basic.root)

    val threeWay =
        section(
            tr("三种结果", "Three outcomes"),
            tr(
                "rejectButtonVisible 打开第三个按钮，事件为 reject。",
                "rejectButtonVisible adds a third button, which fires reject.",
            ),
        )
    val threeLog = EventLog(4)
    val threeWayDialog = createConfirmDialog()
    threeWayDialog.header = tr("保存修改？", "Save changes?")
    threeWayDialog.message =
        tr("可以保存、放弃或返回继续编辑。", "You can save, discard or go back to keep editing.")
    threeWayDialog.confirmText = tr("保存", "Save")
    threeWayDialog.rejectText = tr("放弃", "Discard")
    threeWayDialog.cancelText = tr("返回", "Back")
    threeWayDialog.cancelButtonVisible = true
    threeWayDialog.rejectButtonVisible = true
    threeWayDialog.rejectTheme = "error tertiary"
    threeWayDialog.listen(ConfirmDialogEvents.confirm) { threeLog.add(tr("confirm → 保存", "confirm → Save")) }
    threeWayDialog.listen(ConfirmDialogEvents.reject) { threeLog.add(tr("reject → 放弃", "reject → Discard")) }
    threeWayDialog.listen(ConfirmDialogEvents.cancel) { threeLog.add(tr("cancel → 返回", "cancel → Back")) }

    threeWay.body.append(
        example(
            null,
            column(
                createButton { textContent = tr("打开三按钮对话框", "Open three-button dialog") }
                    .withTheme("tertiary").apply {
                        clickEvent.addHandler { threeWayDialog.opened = true }
                    },
                threeWayDialog,
                threeLog.root,
            ),
        ),
    )
    view.body.append(threeWay.root)

    val cancelSection =
        section(
            tr("取消按钮", "Cancel button"),
            tr(
                "cancelButtonVisible 显示取消按钮，confirmTheme 让确认按钮表达危险操作。",
                "cancelButtonVisible shows the cancel button; confirmTheme marks the confirm action as destructive.",
            ),
        )
    val deleteStatus = Readout(tr("状态：", "Status: "))
    val deleteDialog = createConfirmDialog()
    deleteDialog.header = tr("删除「Q4 报告」？", "Delete \"Report Q4\"?")
    deleteDialog.message = tr("确定要永久删除这一项吗？", "Are you sure you want to permanently delete this item?")
    deleteDialog.confirmText = tr("删除", "Delete")
    deleteDialog.confirmTheme = "error primary"
    deleteDialog.cancelButtonVisible = true
    deleteDialog.listen(ConfirmDialogEvents.confirm) {
        deleteStatus.set(tr("状态：", "Status: "), tr("已删除", "Deleted"))
    }
    deleteDialog.listen(ConfirmDialogEvents.cancel) {
        deleteStatus.set(tr("状态：", "Status: "), tr("已取消", "Canceled"))
    }
    cancelSection.body.append(
        example(
            null,
            column(
                row(
                    createButton { textContent = tr("删除报告", "Delete report") }.withTheme("error").apply {
                        clickEvent.addHandler { deleteDialog.opened = true }
                    },
                    deleteStatus.root,
                ),
                deleteDialog,
            ),
        ),
    )
    view.body.append(cancelSection.root)

    val confirmOnlySection =
        section(
            tr("仅确认按钮", "Confirm only"),
            tr(
                "只保留一个确认按钮时，它用于告知结果；消息支持任意 DOM，包括粗体与链接。",
                "With only a confirm button left it acknowledges a result; the message takes any DOM, " +
                    "including bold text and links.",
            ),
        )
    val exportStatus = Readout(tr("状态：", "Status: "))
    val exportDialog = createConfirmDialog()
    exportDialog.header = tr("导出失败", "Export failed")
    exportDialog.confirmText = tr("好", "OK")
    exportDialog.cancelButtonVisible = false
    exportDialog.listen(ConfirmDialogEvents.confirm) {
        exportStatus.set(tr("状态：", "Status: "), tr("已确认", "Acknowledged"))
    }
    val exportMessage =
        paragraph(
            tr(
                "导出「Q4 报告」时发生错误，请重试。若问题仍然存在，请联系 support@company.com。",
                "An error occurred while exporting Report Q4. Please try again. If the problem persists, " +
                    "contact support@company.com.",
            ),
        )
    exportDialog.append(exportMessage)
    confirmOnlySection.body.append(
        example(
            null,
            column(
                row(
                    createButton { textContent = tr("导出报告", "Export report") }.withTheme("tertiary").apply {
                        clickEvent.addHandler { exportDialog.opened = true }
                    },
                    exportStatus.root,
                ),
                exportDialog,
            ),
        ),
    )
    view.body.append(confirmOnlySection.root)

    return view
}
