package demo.pages.overlays

import io.github.shijianjs.ktjs.demo.page.Blocks.row as kitRow
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.page.Blocks.col as kitCol
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import io.github.shijianjs.ktjs.demo.page.Blocks.paragraph
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import demo.CatalogEntry
import demo.Catalog
import demo.Groups
import demo.bi
import demo.ui.EventLog
import demo.ui.PageFrame
import demo.ui.PageMeta
import demo.ui.Readout
import demo.ui.RecordedLog
import demo.ui.listen
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.confirmdialog.ConfirmDialogEvents
import io.github.shijianjs.ktjs.vaadin.confirmdialog.createConfirmDialog
import web.dom.clickEvent
import web.events.addHandler

internal class ConfirmDialogPage : DemoPage() {
    private val texts = Binder(confirmDialogTexts)

    private val frame = PageFrame(ENTRY)

    private val status = Readout("")

    private val deleteStatus = Readout("")

    private val exportStatus = Readout("")

    private var lastStatus: ((ConfirmDialogTexts) -> String)? = null

    private var lastDeleteStatus: ((ConfirmDialogTexts) -> String)? = null

    private var lastExportStatus: ((ConfirmDialogTexts) -> String)? = null

    /** The three-outcome dialog's history: every line is copy, so it is replayed on a switch. */
    private val threeLog = RecordedLog<ConfirmDialogTexts>(4)

    init {
        frame.mount(this)

        val basic = SectionBlock.NAME { show() }
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        val log = EventLog(6)
        val confirm = createConfirmDialog()
        confirm.dataset["test"] = "confirm-dialog-basic"
        texts.bind(confirm, { it.deleteHeader }) { confirm.header = it }
        texts.bind(confirm, { it.deleteMessage }) { confirm.message = it }
        texts.bind(confirm, { it.delete }) { confirm.confirmText = it }
        texts.bind(confirm, { it.keep }) { confirm.cancelText = it }
        texts.bind(confirm, { it.rejectButton }) { confirm.rejectText = it }
        confirm.cancelButtonVisible = true
        confirm.listen(ConfirmDialogEvents.confirm) {
            log.add("confirm")
            lastStatus = { copy -> copy.deleted }
            showStatus()
        }
        confirm.listen(ConfirmDialogEvents.cancel) {
            log.add("cancel")
            lastStatus = { copy -> copy.canceled }
            showStatus()
        }
        confirm.listen(ConfirmDialogEvents.closed) { log.add("closed") }

        val openButton = createButton().addTheme("error").apply {
            clickEvent.addHandler {
                confirm.opened = true
                lastStatus = null
                showStatus()
            }
        }
        texts.bind(openButton) { it.deleteRecord }
        basic.body.append(exampleBox(null, kitCol(kitRow(openButton, status.root), confirm, log.root)))
        body.append(basic.root)

        val threeWay = SectionBlock.NAME { show() }
        texts.bind(threeWay.heading) { it.threeHeading }
        texts.bind(threeWay.hint) { it.threeHint }
        val threeWayDialog = createConfirmDialog()
        texts.bind(threeWayDialog, { it.saveHeader }) { threeWayDialog.header = it }
        texts.bind(threeWayDialog, { it.saveMessage }) { threeWayDialog.message = it }
        texts.bind(threeWayDialog, { it.save }) { threeWayDialog.confirmText = it }
        texts.bind(threeWayDialog, { it.discard }) { threeWayDialog.rejectText = it }
        texts.bind(threeWayDialog, { it.back }) { threeWayDialog.cancelText = it }
        threeWayDialog.cancelButtonVisible = true
        threeWayDialog.rejectButtonVisible = true
        threeWayDialog.rejectTheme = "error tertiary"
        threeWayDialog.listen(ConfirmDialogEvents.confirm) {
            threeLog.add(texts.current, { copy -> copy.logConfirmSave })
        }
        threeWayDialog.listen(ConfirmDialogEvents.reject) {
            threeLog.add(texts.current, { copy -> copy.logRejectDiscard })
        }
        threeWayDialog.listen(ConfirmDialogEvents.cancel) {
            threeLog.add(texts.current, { copy -> copy.logCancelBack })
        }

        val openThree = createButton().addTheme("tertiary").apply {
            clickEvent.addHandler { threeWayDialog.opened = true }
        }
        texts.bind(openThree) { it.openThree }
        threeWay.body.append(
            exampleBox(null, kitCol(openThree, threeWayDialog, threeLog.root)),
        )
        body.append(threeWay.root)

        val cancelSection = SectionBlock.NAME { show() }
        texts.bind(cancelSection.heading) { it.cancelHeading }
        texts.bind(cancelSection.hint) { it.cancelHint }
        val deleteDialog = createConfirmDialog()
        texts.bind(deleteDialog, { it.deleteReportHeader }) { deleteDialog.header = it }
        texts.bind(deleteDialog, { it.deleteReportMessage }) { deleteDialog.message = it }
        texts.bind(deleteDialog, { it.delete }) { deleteDialog.confirmText = it }
        texts.bind(deleteDialog, { it.cancelButton }) { deleteDialog.cancelText = it }
        texts.bind(deleteDialog, { it.rejectButton }) { deleteDialog.rejectText = it }
        deleteDialog.confirmTheme = "error primary"
        deleteDialog.cancelButtonVisible = true
        deleteDialog.listen(ConfirmDialogEvents.confirm) {
            lastDeleteStatus = { copy -> copy.deleted }
            showDeleteStatus()
        }
        deleteDialog.listen(ConfirmDialogEvents.cancel) {
            lastDeleteStatus = { copy -> copy.canceled }
            showDeleteStatus()
        }
        val deleteReport = createButton().addTheme("error").apply {
            clickEvent.addHandler { deleteDialog.opened = true }
        }
        texts.bind(deleteReport) { it.deleteReport }
        cancelSection.body.append(
            exampleBox(null, kitCol(kitRow(deleteReport, deleteStatus.root), deleteDialog)),
        )
        body.append(cancelSection.root)

        val confirmOnlySection = SectionBlock.NAME { show() }
        texts.bind(confirmOnlySection.heading) { it.confirmOnlyHeading }
        texts.bind(confirmOnlySection.hint) { it.confirmOnlyHint }
        val exportDialog = createConfirmDialog()
        texts.bind(exportDialog, { it.exportHeader }) { exportDialog.header = it }
        texts.bind(exportDialog, { it.ok }) { exportDialog.confirmText = it }
        texts.bind(exportDialog, { it.cancelButton }) { exportDialog.cancelText = it }
        texts.bind(exportDialog, { it.rejectButton }) { exportDialog.rejectText = it }
        exportDialog.cancelButtonVisible = false
        exportDialog.listen(ConfirmDialogEvents.confirm) {
            lastExportStatus = { copy -> copy.acknowledged }
            showExportStatus()
        }
        val exportMessage = paragraph()
        texts.bind(exportMessage) { it.exportMessage }
        exportDialog.append(exportMessage)
        val exportReport = createButton().addTheme("tertiary").apply {
            clickEvent.addHandler { exportDialog.opened = true }
        }
        texts.bind(exportReport) { it.exportReport }
        confirmOnlySection.body.append(
            exampleBox(null, kitCol(kitRow(exportReport, exportStatus.root), exportDialog)),
        )
        body.append(confirmOnlySection.root)

        applyTranslation(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = ENTRY.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        showStatus()
        showDeleteStatus()
        showExportStatus()
        threeLog.replay(texts.current)
    }

    private fun showStatus() {
        val copy = texts.current
        status.set(copy.statusPrefix, lastStatus?.invoke(copy) ?: "")
    }

    private fun showDeleteStatus() {
        val copy = texts.current
        deleteStatus.set(copy.statusPrefix, lastDeleteStatus?.invoke(copy) ?: "")
    }

    private fun showExportStatus() {
        val copy = texts.current
        exportStatus.set(copy.statusPrefix, lastExportStatus?.invoke(copy) ?: "")
    }

    companion object : CustomElementReferenceAdapter<ConfirmDialogPage>(ConfirmDialogPage::class.js, "vaadin-demo-confirm-dialog-page") {
        val ENTRY: CatalogEntry =
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
                        docsUrl = "${Catalog.DOCS_BASE}/confirm-dialog",
                        sourcePath = "pages/overlays/ConfirmDialogPage.kt",
                    ),
                kitPage = { ConfirmDialogPage.NAME() },
            )

        private val english: ConfirmDialogTexts =
            ConfirmDialogTexts(
                basicHeading = "Basic usage",
                basicHint = "Confirm / cancel fire confirm and cancel respectively, followed by closed.",
                statusPrefix = "Status: ",
                deleted = "Deleted",
                canceled = "Canceled",
                deleteRecord = "Delete record",
                deleteHeader = "Delete this record?",
                deleteMessage = "This cannot be undone.",
                delete = "Delete",
                keep = "Keep",
                cancelButton = "Cancel",
                rejectButton = "Reject",
                threeHeading = "Three outcomes",
                threeHint = "rejectButtonVisible adds a third button, which fires reject.",
                saveHeader = "Save changes?",
                saveMessage = "You can save, discard or go back to keep editing.",
                save = "Save",
                discard = "Discard",
                back = "Back",
                logConfirmSave = "confirm → Save",
                logRejectDiscard = "reject → Discard",
                logCancelBack = "cancel → Back",
                openThree = "Open three-button dialog",
                cancelHeading = "Cancel button",
                cancelHint = "cancelButtonVisible shows the cancel button; confirmTheme marks the confirm action " +
                    "as destructive.",
                deleteReportHeader = "Delete \"Report Q4\"?",
                deleteReportMessage = "Are you sure you want to permanently delete this item?",
                deleteReport = "Delete report",
                confirmOnlyHeading = "Confirm only",
                confirmOnlyHint = "With only a confirm button left it acknowledges a result; the message takes " +
                    "any DOM, including bold text and links.",
                exportHeader = "Export failed",
                ok = "OK",
                exportMessage = "An error occurred while exporting Report Q4. Please try again. If the problem " +
                    "persists, contact support@company.com.",
                exportReport = "Export report",
                acknowledged = "Acknowledged",
            )
        private val chinese: ConfirmDialogTexts =
            ConfirmDialogTexts(
                basicHeading = "基本用法",
                basicHint = "确认 / 取消会分别触发 confirm 与 cancel，之后触发 closed。",
                statusPrefix = "状态：",
                deleted = "已删除",
                canceled = "已取消",
                deleteRecord = "删除记录",
                deleteHeader = "删除这条记录？",
                deleteMessage = "删除后无法恢复。",
                delete = "删除",
                keep = "保留",
                cancelButton = "取消",
                rejectButton = "拒绝",
                threeHeading = "三种结果",
                threeHint = "rejectButtonVisible 打开第三个按钮，事件为 reject。",
                saveHeader = "保存修改？",
                saveMessage = "可以保存、放弃或返回继续编辑。",
                save = "保存",
                discard = "放弃",
                back = "返回",
                logConfirmSave = "confirm → 保存",
                logRejectDiscard = "reject → 放弃",
                logCancelBack = "cancel → 返回",
                openThree = "打开三按钮对话框",
                cancelHeading = "取消按钮",
                cancelHint = "cancelButtonVisible 显示取消按钮，confirmTheme 让确认按钮表达危险操作。",
                deleteReportHeader = "删除「Q4 报告」？",
                deleteReportMessage = "确定要永久删除这一项吗？",
                deleteReport = "删除报告",
                confirmOnlyHeading = "仅确认按钮",
                confirmOnlyHint = "只保留一个确认按钮时，它用于告知结果；消息支持任意 DOM，包括粗体与链接。",
                exportHeader = "导出失败",
                ok = "好",
                exportMessage = "导出「Q4 报告」时发生错误，请重试。若问题仍然存在，请联系 support@company.com。",
                exportReport = "导出报告",
                acknowledged = "已确认",
            )
        private val confirmDialogTexts: Translations<ConfirmDialogTexts> = texts(
            Langs.EN to english,
            Langs.ZH_HANS to chinese,
        )
    }
}
