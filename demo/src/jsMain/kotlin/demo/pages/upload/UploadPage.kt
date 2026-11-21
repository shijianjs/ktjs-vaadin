package demo.pages.upload

import io.github.shijianjs.ktjs.demo.page.Blocks.col as kitCol
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import io.github.shijianjs.ktjs.demo.page.Blocks.anchor as kitAnchor
import io.github.shijianjs.ktjs.demo.page.Blocks.heading
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import io.github.shijianjs.ktjs.demo.page.Grid
import demo.CatalogEntry
import demo.Catalog
import demo.Groups
import demo.bi
import demo.ui.EventLog
import demo.ui.PageFrame
import demo.ui.PageMeta
import demo.ui.Readout
import demo.ui.jsObject
import demo.ui.listen
import io.github.shijianjs.ktjs.dom.element.inlineStyle
import web.html.HTMLElement
import web.cssom.AlignItems
import web.cssom.Display
import web.cssom.JustifyContent
import web.cssom.px
import js.objects.unsafeJso
import kotlin.js.unsafeCast
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.vaadin.notification.Notification
import io.github.shijianjs.ktjs.vaadin.notification.NotificationShowOptions
import io.github.shijianjs.ktjs.vaadin.upload.UploadElement
import io.github.shijianjs.ktjs.vaadin.upload.UploadEvents
import io.github.shijianjs.ktjs.vaadin.upload.UploadFile
import io.github.shijianjs.ktjs.vaadin.upload.UploadI18n
import io.github.shijianjs.ktjs.vaadin.upload.UploadManager
import io.github.shijianjs.ktjs.vaadin.upload.UploadManagerOptions
import io.github.shijianjs.ktjs.vaadin.upload.createUploadButton
import io.github.shijianjs.ktjs.vaadin.upload.createUploadDropZone
import io.github.shijianjs.ktjs.vaadin.upload.createUploadFileList
import io.github.shijianjs.ktjs.vaadin.upload.createUploadManager
import web.dom.clickEvent
import web.events.Event
import web.events.addHandler
import web.html.HtmlTagName
import demo.ui.MUTED

internal class UploadPage : DemoPage() {
    private val texts = Binder(uploadTexts)

    private val frame = PageFrame(ENTRY)

    /** Uploads whose `i18n` object has to be rebuilt when the language changes. */
    private val i18nFills: MutableList<Pair<UploadElement, (UploadTexts) -> UploadI18n>> = mutableListOf()

    /** Ways to hand a fake file list back to an upload or a manager, keyed by how it is set. */
    private val fileFills: MutableList<Pair<(Array<UploadFile>) -> Unit, (UploadTexts) -> Array<UploadFile>>> =
        mutableListOf()

    private fun i18n(upload: UploadElement, fill: (UploadTexts) -> UploadI18n) {
        i18nFills += upload to fill
    }

    private fun files(upload: UploadElement, fill: (UploadTexts) -> Array<UploadFile>) {
        fileFills += ({ list: Array<UploadFile> -> upload.files = list }) to fill
    }

    private fun files(manager: UploadManager, fill: (UploadTexts) -> Array<UploadFile>) {
        fileFills += ({ list: Array<UploadFile> -> manager.files = list }) to fill
    }

    private fun reportRejection(event: Event) {
        demo.pages.upload.showRejection(event, texts.current.fileNotUploaded)
    }

    init {
        frame.mount(this)
        appendBasic()
        appendDragAndDrop()
        appendAutoUpload()
        appendRestrictions()
        appendFileActions()
        appendInternationalization()
        appendCustomization()
        appendListeners()
        appendBestPractices()
        appendErrorMessages()
        appendModular()
        appendDisabled()
        applyTranslation(Langs.EN)
    }

    private fun appendBasic() {
        val basic = SectionBlock.NAME { show() }
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        val upload = mockedUpload { target = "/api/fileupload" }
        upload.dataset["test"] = "upload-basic"
        files(upload) { copy ->
            uploadFiles(
                """
                [
                  {"name": "Annual Report.docx", "complete": true},
                  {"name": "Workflow.pdf", "progress": 60,
                   "status": "19.7 MB: 60% (remaining time: 00:12:34)"},
                  {"name": "Financials.xlsx", "error": "${copy.uploadFailed}"}
                ]
                """.trimIndent(),
            )
        }
        basic.body.append(exampleBox(null, upload))
        body.append(basic.root)
    }

    private fun appendDragAndDrop() {
        val dragAndDrop = SectionBlock.NAME { show() }
        texts.bind(dragAndDrop.heading) { it.dragHeading }
        texts.bind(dragAndDrop.hint) { it.dragHint }
        val dropEnabled = mockedUpload { nodrop = false }
        dropEnabled.dataset["test"] = "upload-drop-enabled"
        val dropDisabled = mockedUpload { nodrop = true }
        dropDisabled.dataset["test"] = "upload-drop-disabled"
        val enabledLabel = HtmlTagName.strong()
        texts.bind(enabledLabel) { it.dragEnabled }
        val disabledLabel = HtmlTagName.strong()
        texts.bind(disabledLabel) { it.dragDisabled }
        dragAndDrop.body.append(
            exampleBox(
                null,
                Grid.NAME { show(260); add(kitCol(enabledLabel, dropEnabled), kitCol(disabledLabel, dropDisabled)) },
            ),
        )
        body.append(dragAndDrop.root)
    }

    private fun appendAutoUpload() {
        val autoUpload = SectionBlock.NAME { show() }
        texts.bind(autoUpload.heading) { it.autoHeading }
        texts.bind(autoUpload.hint) { it.autoHint }

        val manual = mockedUpload {
            noAuto = true
            target = "/api/fileupload"
        }
        manual.dataset["test"] = "upload-no-auto"
        i18n(manual) { copy ->
            unsafeJso<UploadI18n> {
                addFiles = unsafeJso { many = copy.selectFilesMany }
            }
        }
        files(manual) { copy -> heldFiles(copy.queued, "Workflow.pdf") }
        autoUpload.body.append(exampleBox(null, manual))

        val uploadAll = mockedUpload {
            noAuto = true
            target = "/api/fileupload"
        }
        uploadAll.dataset["test"] = "upload-all"
        i18n(uploadAll) { copy ->
            unsafeJso<UploadI18n> {
                addFiles = unsafeJso { many = copy.selectFilesMany }
            }
        }
        files(uploadAll) { copy -> heldFiles(copy.queued, "Workflow.pdf", "Financials.xlsx") }
        mockSuccessfulRequests(uploadAll)
        val readout = Readout("")
        fun refreshReadout() {
            readout.set(texts.current.uploadStatus, fileSummary(uploadAll))
        }
        uploadAll.listen(UploadEvents.uploadStart) { refreshReadout() }
        uploadAll.listen(UploadEvents.uploadProgress) { refreshReadout() }
        uploadAll.listen(UploadEvents.uploadSuccess) { refreshReadout() }
        val startAll = createButton { textContent = "" }
        texts.bind(startAll) { it.uploadAllFiles }
        startAll.addTheme("primary")
        startAll.clickEvent.addHandler { uploadAll.uploadFiles() }
        val caption = HtmlTagName.p()
        texts.bind(caption, { it.uploadAllCaption }) { caption.textContent = it }
        val autoCaption = HtmlTagName.p()
        texts.bind(autoCaption, { it.autoDisabledCaption }) { autoCaption.textContent = it }
        autoUpload.body.append(
            exampleBox(null, kitCol(autoCaption, manual)),
            exampleBox(null, kitCol(caption, uploadAll, startAll, readout.root)),
        )
        readout.set(texts.current.notUploading)
        body.append(autoUpload.root)
        notUploadingReadout = readout
    }

    private var notUploadingReadout: Readout? = null

    private fun appendRestrictions() {
        val restrictions = SectionBlock.NAME { show() }
        texts.bind(restrictions.heading) { it.restrictionsHeading }
        texts.bind(restrictions.hint) { it.restrictionsHint }

        val report = mockedUpload {
            accept = "application/pdf,.pdf"
            maxFiles = 1.0
        }
        report.dataset["test"] = "upload-format"
        i18n(report) { copy ->
            unsafeJso<UploadI18n> {
                addFiles = unsafeJso { one = copy.uploadReportOne }
                dropFiles = unsafeJso { one = copy.dropReportOne }
                error = unsafeJso { incorrectFileType = copy.incorrectPdf }
            }
        }
        report.listen(UploadEvents.fileReject) { event -> reportRejection(event) }
        val reportHeading = heading(4, "")
        texts.bind(reportHeading, { it.uploadReport }) { reportHeading.textContent = it }
        val accepted = HtmlTagName.p { className = MUTED }
        texts.bind(accepted, { it.acceptedPdf }) { accepted.textContent = it }
        restrictions.body.append(
            exampleBox(null, kitCol(reportHeading, accepted, report)),
        )

        val count = mockedUpload { maxFiles = 3.0 }
        count.dataset["test"] = "upload-count"
        i18n(count) { copy ->
            unsafeJso<UploadI18n> {
                error = unsafeJso { tooManyFiles = copy.tooManyFiles }
            }
        }
        count.listen(UploadEvents.fileReject) { event -> reportRejection(event) }
        val countHeading = heading(4, "")
        texts.bind(countHeading, { it.uploadFilesHeading }) { countHeading.textContent = it }
        val countHint = HtmlTagName.p { className = MUTED }
        texts.bind(countHint, { it.maxThreeFiles }) { countHint.textContent = it }
        restrictions.body.append(
            exampleBox(null, kitCol(countHeading, countHint, count)),
        )

        val size = mockedUpload {
            maxFiles = 1.0
            maxFileSize = 10.0 * 1024 * 1024
        }
        size.dataset["test"] = "upload-size"
        i18n(size) { copy ->
            unsafeJso<UploadI18n> {
                error = unsafeJso { fileIsTooBig = copy.fileTooBig }
            }
        }
        size.listen(UploadEvents.fileReject) { event -> reportRejection(event) }
        val sizeHeading = heading(4, "")
        texts.bind(sizeHeading, { it.uploadFileHeading }) { sizeHeading.textContent = it }
        val sizeHint = HtmlTagName.p { className = MUTED }
        texts.bind(sizeHint, { it.maxSize10 }) { sizeHint.textContent = it }
        restrictions.body.append(
            exampleBox(null, kitCol(sizeHeading, sizeHint, size)),
        )
        body.append(restrictions.root)
    }

    private fun appendFileActions() {
        val actions = SectionBlock.NAME { show() }
        texts.bind(actions.heading) { it.actionsHeading }
        texts.bind(actions.hint) { it.actionsHint }
        val inProgress = mockedUpload()
        inProgress.dataset["test"] = "upload-clear"
        inProgress.files =
            uploadFiles(
                """
                [
                  {"name": "Workflow.pdf", "progress": 60,
                   "status": "19.7 MB: 60% (remaining time: 00:12:34)"},
                  {"name": "Financials.xlsx", "complete": true}
                ]
                """.trimIndent(),
            )
        val clearCaption = HtmlTagName.p()
        texts.bind(clearCaption, { it.clearCaption }) { clearCaption.textContent = it }
        actions.body.append(exampleBox(null, kitCol(clearCaption, inProgress)))

        val failed = mockedUpload()
        failed.dataset["test"] = "upload-retry"
        files(failed) { copy ->
            uploadFiles("""[{"name": "Financials.xlsx", "error": "${copy.somethingWrong}"}]""")
        }
        val retryCaption = HtmlTagName.p()
        texts.bind(retryCaption, { it.retryCaption }) { retryCaption.textContent = it }
        actions.body.append(exampleBox(null, kitCol(retryCaption, failed)))

        val queued = mockedUpload()
        queued.dataset["test"] = "upload-start"
        files(queued) { copy -> heldFiles(copy.queued, "Workflow.pdf") }
        val startCaption = HtmlTagName.p()
        texts.bind(startCaption, { it.startCaption }) { startCaption.textContent = it }
        actions.body.append(exampleBox(null, kitCol(startCaption, queued)))
        body.append(actions.root)
    }

    private fun appendInternationalization() {
        val i18nSection = SectionBlock.NAME { show() }
        texts.bind(i18nSection.heading) { it.i18nHeading }
        texts.bind(i18nSection.hint) { it.i18nHint }
        val upload = mockedUpload()
        upload.dataset["test"] = "upload-i18n"
        // The Finnish copy is the point of the example, so it does not follow the page language.
        upload.i18nProperty = jsObject(FINNISH_I18N.trimIndent()).unsafeCast<UploadI18n>()
        i18nSection.body.append(exampleBox(null, upload))
        body.append(i18nSection.root)
    }

    private fun appendCustomization() {
        val customization = SectionBlock.NAME { show() }
        texts.bind(customization.heading) { it.customHeading }
        texts.bind(customization.hint) { it.customHint }

        val primaryButton = createButton { textContent = "" }
        texts.bind(primaryButton) { it.uploadPdf }
        primaryButton.addTheme("primary")
        primaryButton.slot = "add-button"
        val buttonUpload = mockedUpload {
            maxFiles = 1.0
            accept = "application/pdf,.pdf"
        }
        buttonUpload.dataset["test"] = "upload-button-variant"
        i18n(buttonUpload) { copy ->
            unsafeJso<UploadI18n> {
                dropFiles = unsafeJso { one = copy.dropPdf }
            }
        }
        buttonUpload.append(primaryButton)
        buttonUpload.listen(UploadEvents.maxFilesReachedChanged) {
            primaryButton.disabled = buttonUpload.maxFilesReached
        }
        buttonUpload.listen(UploadEvents.fileReject) { event ->
            Notification.show(
                texts.current.errorPrefix + rejectText(event) + " '" + rejectedFileName(event) + "'",
                unsafeJso<NotificationShowOptions> { theme = "error" },
            )
        }
        val customHint = HtmlTagName.p { className = MUTED }
        texts.bind(customHint, { it.buttonLimitHint }) { customHint.textContent = it }
        customization.body.append(
            exampleBox(null, kitCol(customHint, buttonUpload)),
        )

        val dropLabel = mockedUpload()
        dropLabel.dataset["test"] = "upload-drop-label"
        val dropLabelText = HtmlTagName.span()
        texts.bind(dropLabelText, { it.cloudText }) { dropLabelText.textContent = it }
        val privacy = kitAnchor("", "https://vaadin.com/privacy-policy")
        texts.bind(privacy, { it.privacyPolicy }) { privacy.textContent = it }
        val dropLabelHolder = HtmlTagName.span().apply {
            slot = "drop-label"
            append(dropLabelText, privacy)
        }
        dropLabel.append(
            createIcon { icon = "vaadin:cloud-upload" }.apply { slot = "drop-label-icon" },
            dropLabelHolder,
        )
        val dropCaption = HtmlTagName.p()
        texts.bind(dropCaption, { it.dropLabelCaption }) { dropCaption.textContent = it }
        customization.body.append(exampleBox(null, kitCol(dropCaption, dropLabel)))
        body.append(customization.root)
    }

    private fun appendListeners() {
        val listeners = SectionBlock.NAME { show() }
        texts.bind(listeners.heading) { it.listenersHeading }
        texts.bind(listeners.hint) { it.listenersHint }
        val upload = mockedUpload { target = "/api/fileupload" }
        upload.dataset["test"] = "upload-listeners"
        upload.files = heldFiles(texts.current.queued, "Workflow.pdf")
        val log = EventLog(6)
        upload.listen(UploadEvents.uploadStart) { log.add("upload-start") }
        upload.listen(UploadEvents.uploadProgress) {
            log.add("upload-progress · ${fileProgress(upload.files.firstOrNull())}")
        }
        upload.listen(UploadEvents.uploadSuccess) { log.add("upload-success") }
        upload.listen(UploadEvents.uploadError) { log.add("upload-error") }
        val start = createButton { textContent = "" }
        texts.bind(start) { it.startUpload }
        start.addTheme("primary")
        start.clickEvent.addHandler { upload.uploadFiles() }
        listeners.body.append(exampleBox(null, kitCol(upload, start, log.root)))
        body.append(listeners.root)
    }

    private fun appendBestPractices() {
        val bestPractices = SectionBlock.NAME { show() }
        texts.bind(bestPractices.heading) { it.bestHeading }
        texts.bind(bestPractices.hint) { it.bestHint }

        val labelled = mockedUpload {
            maxFiles = 1.0
            accept = "application/pdf,.pdf"
        }
        labelled.dataset["test"] = "upload-labelling"
        i18n(labelled) { copy ->
            unsafeJso<UploadI18n> {
                addFiles = unsafeJso { one = copy.uploadReportOne }
                dropFiles = unsafeJso { one = copy.dropReportOne }
                error = unsafeJso { incorrectFileType = copy.incorrectPdf }
            }
        }
        labelled.listen(UploadEvents.fileReject) { event -> reportRejection(event) }
        val labellingCaption = HtmlTagName.p()
        texts.bind(labellingCaption, { it.buttonLabellingCaption }) { labellingCaption.textContent = it }
        bestPractices.body.append(exampleBox(null, kitCol(labellingCaption, labelled)))

        val spreadsheet = mockedUpload {
            maxFiles = 1.0
            maxFileSize = 1.0 * 1024 * 1024
            accept =
                "application/vnd.ms-excel,.xls," +
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,.xlsx,text/csv,.csv"
        }
        spreadsheet.dataset["test"] = "upload-helper"
        i18n(spreadsheet) { copy ->
            unsafeJso<UploadI18n> {
                addFiles = unsafeJso { one = copy.uploadSpreadsheetOne }
                dropFiles = unsafeJso { one = copy.dropSpreadsheetOne }
                error = unsafeJso { incorrectFileType = copy.supportedFormats }
            }
        }
        spreadsheet.listen(UploadEvents.fileReject) { event -> reportRejection(event) }
        val helperCaption = HtmlTagName.p()
        texts.bind(helperCaption, { it.helperCaption }) { helperCaption.textContent = it }
        val spreadsheetHeading = heading(4, "")
        texts.bind(spreadsheetHeading, { it.uploadSpreadsheet }) { spreadsheetHeading.textContent = it }
        val sizeHint = HtmlTagName.p { className = MUTED }
        texts.bind(sizeHint, { it.tooBigOneMb }) { sizeHint.textContent = it }
        val formatHint = HtmlTagName.p { className = MUTED }
        texts.bind(formatHint, { it.spreadsheetOnly }) { formatHint.textContent = it }
        bestPractices.body.append(
            exampleBox(null, kitCol(helperCaption, spreadsheetHeading, sizeHint, formatHint, spreadsheet)),
        )
        body.append(bestPractices.root)
    }

    private fun appendErrorMessages() {
        val errorMessages = SectionBlock.NAME { show() }
        texts.bind(errorMessages.heading) { it.errorsHeading }
        texts.bind(errorMessages.hint) { it.errorsHint }
        val caution = mockedUpload { nodrop = true }
        caution.dataset["test"] = "upload-caution"
        i18n(caution) { copy ->
            unsafeJso<UploadI18n> {
                uploading = unsafeJso { error = unsafeJso { unexpectedServerError = copy.unexpectedServerError } }
            }
        }
        files(caution) { copy ->
            uploadFiles("""[{"name": "Financials.xlsx", "error": "${copy.unexpectedServerError}"}]""")
        }

        val recommended = mockedUpload { nodrop = true }
        recommended.dataset["test"] = "upload-recommended"
        i18n(recommended) { copy ->
            unsafeJso<UploadI18n> {
                uploading = unsafeJso {
                    error = unsafeJso { unexpectedServerError = copy.fileNotUploaded }
                }
            }
        }
        files(recommended) { copy ->
            uploadFiles("""[{"name": "Financials.xlsx", "error": "${copy.fileNotUploaded}"}]""")
        }
        val cautionLabel = HtmlTagName.strong()
        texts.bind(cautionLabel, { it.cautionCaption }) { cautionLabel.textContent = it }
        val recommendedLabel = HtmlTagName.strong()
        texts.bind(recommendedLabel, { it.recommendedCaption }) { recommendedLabel.textContent = it }
        errorMessages.body.append(
            exampleBox(
                null,
                Grid.NAME { show(260); add(kitCol(cautionLabel, caution), kitCol(recommendedLabel, recommended)) },
            ),
        )
        body.append(errorMessages.root)
    }

    private fun appendModular() {
        val modular = SectionBlock.NAME { show() }
        texts.bind(modular.heading) { it.modularHeading }
        texts.bind(modular.hint) { it.modularHint }

        val basicManager = createUploadManager(
            unsafeJso<UploadManagerOptions> {
                target = "/api/fileupload"
                maxFiles = 5.0
                maxFileSize = 10.0 * 1024 * 1024
                accept = "image/*,application/pdf"
            },
        )
        mockSuccessfulRequests(basicManager)
        basicManager.files =
            uploadFiles(
                """
                [
                  {"name": "Annual Report.pdf", "type": "application/pdf", "complete": true},
                  {"name": "Workflow.pdf", "type": "application/pdf", "progress": 60,
                   "status": "19.7 MB: 60% (remaining time: 00:12:34)"}
                ]
                """.trimIndent(),
            )
        val basicButton = createUploadButton { textContent = "" }
        texts.bind(basicButton) { it.selectFilesOne }
        basicButton.manager = basicManager
        val basicList = createUploadFileList()
        basicList.manager = basicManager
        fullWidth(basicList)
        val basicCaption = HtmlTagName.p()
        texts.bind(basicCaption, { it.modularBasicCaption }) { basicCaption.textContent = it }
        modular.body.append(exampleBox(null, kitCol(basicCaption, basicButton, basicList)))

        val zoneManager = createUploadManager(unsafeJso<UploadManagerOptions> { target = "/api/fileupload"; maxFiles = 10.0 })
        mockSuccessfulRequests(zoneManager)
        files(zoneManager) { copy -> heldFiles(copy.queued, "Quarterly budget.pdf") }
        val browseButton = createUploadButton { textContent = "" }
        texts.bind(browseButton) { it.browse }
        browseButton.manager = zoneManager
        val dropZone = createUploadDropZone()
        dropZone.manager = zoneManager
        dropZone.style.setProperty("box-sizing", "border-box")
        dropZone.style.setProperty("width", "100%")
        dropZone.style.setProperty("padding", "var(--lumo-space-l)")
        dropZone.style.setProperty("border", "1px dashed var(--lumo-contrast-30pct)")
        dropZone.style.setProperty("border-radius", "var(--lumo-border-radius-l)")
        val dropHint = HtmlTagName.span()
        texts.bind(dropHint, { it.dropHereOr }) { dropHint.textContent = it }
        dropZone.append(
            HtmlTagName.div().apply {
                inlineStyle.apply {
                    display = Display.flex
                    alignItems = AlignItems.center
                    justifyContent = JustifyContent.center
                    columnGap = 8.px
                }
                append(
                    createIcon { icon = "vaadin:upload" },
                    dropHint,
                    browseButton,
                )
            },
        )
        val zoneList = createUploadFileList()
        zoneList.manager = zoneManager
        fullWidth(zoneList)
        val zoneCaption = HtmlTagName.p()
        texts.bind(zoneCaption, { it.dropZoneCaption }) { zoneCaption.textContent = it }
        modular.body.append(exampleBox(null, kitCol(zoneCaption, dropZone, zoneList)))

        val thumbnailsManager = createUploadManager(unsafeJso<UploadManagerOptions> { target = "/api/fileupload" })
        mockSuccessfulRequests(thumbnailsManager)
        files(thumbnailsManager) { copy ->
            uploadFiles(
                """
                [
                  {"name": "Annual Report.docx", "complete": true},
                  {"name": "Financials.xlsx", "error": "${copy.uploadFailed}"}
                ]
                """.trimIndent(),
            )
        }
        val thumbnailsButton = createUploadButton { textContent = "" }
        texts.bind(thumbnailsButton) { it.selectFilesOne }
        thumbnailsButton.manager = thumbnailsManager
        val thumbnailsList = createUploadFileList()
        thumbnailsList.manager = thumbnailsManager
        thumbnailsList.addTheme("thumbnails")
        fullWidth(thumbnailsList)
        val thumbnailsCaption = HtmlTagName.p()
        texts.bind(thumbnailsCaption, { it.thumbnailsCaption }) { thumbnailsCaption.textContent = it }
        modular.body.append(exampleBox(null, kitCol(thumbnailsCaption, thumbnailsButton, thumbnailsList)))
        body.append(modular.root)
    }

    private fun appendDisabled() {
        val disabled = SectionBlock.NAME { show() }
        texts.bind(disabled.heading) { it.disabledHeading }
        texts.bind(disabled.hint) { it.disabledHint }
        val upload = mockedUpload {
            noAuto = true
            this.disabled = true
        }
        upload.dataset["test"] = "upload-disabled"
        disabled.body.append(exampleBox(null, upload))

        val lockedManager = createUploadManager(unsafeJso<UploadManagerOptions> { target = "/api/fileupload" })
        lockedManager.disabled = true
        val lockedButton = createUploadButton { textContent = "" }
        texts.bind(lockedButton) { it.uploadDisabled }
        lockedButton.manager = lockedManager
        val lockedList = createUploadFileList()
        lockedList.manager = lockedManager
        lockedList.disabled = true
        fullWidth(lockedList)
        val lockedCaption = HtmlTagName.p()
        texts.bind(lockedCaption, { it.disabledManagerCaption }) { lockedCaption.textContent = it }
        disabled.body.append(exampleBox(null, kitCol(lockedCaption, lockedButton, lockedList)))
        body.append(disabled.root)
    }

    /** Gives [node] the full width of the example box: an upload list is meant to fill it. */
    private fun <T : HTMLElement> fullWidth(node: T): T =
        node.apply { style.setProperty("width", "100%") }

    override fun applyTranslation(lang: Lang) {
        val meta = ENTRY.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        val copy = texts.current
        i18nFills.forEach { (upload, fill) -> upload.i18nProperty = fill(copy) }
        fileFills.forEach { (apply, fill) -> apply(fill(copy)) }
        notUploadingReadout?.set(copy.notUploading)
    }

    companion object : UploadPageDefaultReference() {
        val ENTRY: CatalogEntry =
            CatalogEntry(
                id = "upload",
                group = Groups.DATA,
                meta =
                    PageMeta(
                        title = bi("Upload", "Upload"),
                        summary =
                            bi(
                                "文件选择、上传进度与状态列表；本页没有后端，示例用模拟请求走完整个上传流程。",
                                "File selection, upload progress and a status list; this page has no backend, so the " +
                                    "examples finish the whole upload flow against a simulated request.",
                            ),
                        docsUrl = "${Catalog.DOCS_BASE}/upload",
                        sourcePath = "pages/upload/UploadPage.kt",
                    ),
                kitPage = { UploadPage.NAME() },
            )

        private val english: UploadTexts =
            UploadTexts(
                basicHeading = "Basic usage",
                basicHint = "target points at the URL that handles the upload; each entry in files describes its " +
                    "state through status / progress / error. The example fills in fake files showing a completed, " +
                    "an in-flight and a failed upload, and sends no request.",
                uploadFailed = "An error occurred",
                dragHeading = "Drag & drop",
                dragHint = "Drag and drop is enabled on desktop and disabled on touch devices by default; setting " +
                    "nodrop explicitly to false or true applies to both.",
                dragEnabled = "Drag and drop enabled",
                dragDisabled = "Drag and drop disabled",
                autoHeading = "Auto-upload",
                autoHint = "Files are queued for upload as soon as they are selected; with noAuto they stay in the " +
                    "list until the start button of a file or uploadFiles() is used.",
                selectFilesMany = "Select Files...",
                autoDisabledCaption = "Auto-upload disabled",
                uploadAllCaption = "Starting all uploads at once",
                notUploading = "Not uploading yet",
                uploadStatus = "Upload status: ",
                uploadAllFiles = "Upload All Files",
                restrictionsHeading = "Upload restrictions",
                restrictionsHint = "Format, count and size are validated on the client first and rejected files " +
                    "never enter the list; the file-reject event carries the reason, shown here through a Notification.",
                uploadReport = "Upload report",
                acceptedPdf = "Accepted file formats: PDF (.pdf)",
                uploadReportOne = "Upload Report...",
                dropReportOne = "Drop report here",
                incorrectPdf = "The provided file does not have the correct format (PDF document).",
                uploadFilesHeading = "Upload files",
                maxThreeFiles = "Maximum of 3 files allowed",
                tooManyFiles = "You may only upload a maximum of three files at once.",
                uploadFileHeading = "Upload file",
                maxSize10 = "Maximum file size: 10 MB",
                fileTooBig = "The file exceeds the maximum allowed size of 10MB.",
                actionsHeading = "File actions",
                actionsHint = "Every file always has a clear button; a failed upload shows a retry button and a queued " +
                    "file shows a start button. The examples build files in those states directly.",
                clearCaption = "Clear / Remove",
                retryCaption = "Retry",
                startCaption = "Start",
                somethingWrong = "Something went wrong, please try again",
                i18nHeading = "Internationalization",
                i18nHint = "Every button, status and error text can be replaced through i18n; the example switches to " +
                    "Finnish and covers the drop label, the button, the errors, the remaining time and the size units.",
                customHeading = "Customization",
                customHint = "The default upload button can be replaced by a slotted custom button (for example a " +
                    "stronger primary variant), and the drop area icon and text come from slots as well.",
                uploadPdf = "Upload PDF...",
                dropPdf = "Drop PDF here",
                buttonLimitHint = "The button is disabled once the file limit is reached; a rejected file is reported " +
                    "through a Notification.",
                errorPrefix = "Error: ",
                customButtonCaption = "Custom upload button",
                cloudText = "Files will be uploaded to our cloud. See our ",
                privacyPolicy = "privacy policy",
                dropLabelCaption = "Custom drop label and icon",
                listenersHeading = "Listeners",
                listenersHint = "An upload fires upload-start, upload-progress and upload-success in order, and " +
                    "upload-error on failure; files-changed and file-reject cover the list and validation. The example " +
                    "runs one successful upload against a simulated request.",
                startUpload = "Start upload",
                bestHeading = "Best practices",
                bestHint = "The button and helper text should say what to upload and which limits apply, so users " +
                    "pick the right file the first time.",
                buttonLabellingCaption = "Button labelling",
                helperCaption = "Helper text",
                uploadSpreadsheet = "Upload spreadsheet",
                tooBigOneMb = "File size must be less than or equal to 1 MB.",
                spreadsheetOnly = "Only Excel and CSV files are accepted.",
                uploadSpreadsheetOne = "Upload Spreadsheet...",
                dropSpreadsheetOne = "Drop spreadsheet here",
                supportedFormats = "Provide the file in one of the supported formats (.xls, .xlsx, .csv).",
                errorsHeading = "Error messages",
                errorsHint = "The same failure reads very differently depending on the message: \"Unexpected Server " +
                    "Error\" says little, while telling users what to do next helps. The example replaces the default " +
                    "unexpectedServerError text through i18n.",
                cautionCaption = "Caution",
                recommendedCaption = "Recommended",
                unexpectedServerError = "Unexpected Server Error",
                fileNotUploaded = "File couldn't be uploaded, try again later",
                modularHeading = "Modular upload",
                modularHint = "UploadManager separates validation, the queue and requests from the UI; the upload " +
                    "button, the drop zone and the file list connect to the same instance through the manager property " +
                    "and can be placed independently.",
                modularBasicCaption = "Basic usage",
                selectFilesOne = "Select Files",
                browse = "Browse",
                dropHereOr = "Drop files here or",
                dropZoneCaption = "Drop zone",
                thumbnailsCaption = "Thumbnails",
                disabledHeading = "Disabled",
                disabledHint = "Use disabled to lock the whole upload UI; in the modular setup the manager must be " +
                    "disabled as well, since disabling the button alone does not stop requests sent directly.",
                uploadDisabled = "Upload disabled",
                disabledManagerCaption = "Disabled manager",
                queued = "Queued",
            )
        private val chinese: UploadTexts =
            UploadTexts(
                basicHeading = "基本用法",
                basicHint = "target 指向处理上传的 URL；files 里每个条目用 status / progress / error 描述上传状态。" +
                    "示例直接填入假文件，展示完成、上传中与失败三种状态，不会发出请求。",
                uploadFailed = "上传失败，请重试",
                dragHeading = "拖放",
                dragHint = "拖放在桌面端默认启用、触摸设备默认禁用；把 nodrop 显式设成 false 或 true 会同时作用于桌面与移动端。",
                dragEnabled = "拖放已启用",
                dragDisabled = "拖放已禁用",
                autoHeading = "自动上传",
                autoHint = "默认选中文件后立即排队上传；noAuto 打开后文件停留在列表里，需要点每个文件的启动按钮、" +
                    "或者调用 uploadFiles() 才开始。",
                selectFilesMany = "选择文件…",
                autoDisabledCaption = "关闭自动上传",
                uploadAllCaption = "一次性开始全部上传",
                notUploading = "尚未开始上传",
                uploadStatus = "上传状态：",
                uploadAllFiles = "开始全部上传",
                restrictionsHeading = "上传限制",
                restrictionsHint = "格式、数量与大小都在客户端先校验，被拒绝的文件不会进入列表；file-reject 事件带着原因，" +
                    "示例用 Notification 提示。",
                uploadReport = "上传报告",
                acceptedPdf = "可接受的文件格式：PDF（.pdf）",
                uploadReportOne = "上传报告…",
                dropReportOne = "把报告拖到这里",
                incorrectPdf = "文件格式不正确（需要 PDF 文档）。",
                uploadFilesHeading = "上传文件",
                maxThreeFiles = "最多允许 3 个文件",
                tooManyFiles = "一次最多只能上传 3 个文件。",
                uploadFileHeading = "上传文件",
                maxSize10 = "最大文件大小：10 MB",
                fileTooBig = "文件超过 10 MB 的大小上限。",
                actionsHeading = "文件操作",
                actionsHint = "每个文件始终带清除按钮；失败时出现重试按钮，队列中的文件出现启动按钮。示例直接构造对应状态的文件。",
                clearCaption = "清除 / 移除",
                retryCaption = "重试",
                startCaption = "启动",
                somethingWrong = "出了点问题，请重试",
                i18nHeading = "国际化",
                i18nHint = "所有按钮、状态与错误文案都能通过 i18n 替换；示例换成芬兰语，覆盖拖放区、按钮、错误、" +
                    "剩余时间与文件大小单位。",
                customHeading = "自定义",
                customHint = "默认上传按钮可以换成插槽里的自定义按钮（例如更强的 primary 主题），拖放区的图标与文案也由插槽提供。",
                uploadPdf = "上传 PDF…",
                dropPdf = "把 PDF 拖到这里",
                buttonLimitHint = "达到数量上限后按钮会被禁用；选错文件时用 Notification 给出原因。",
                errorPrefix = "错误：",
                customButtonCaption = "自定义上传按钮",
                cloudText = "文件会上传到我们的云端。参见",
                privacyPolicy = "隐私政策",
                dropLabelCaption = "自定义拖放区文案与图标",
                listenersHeading = "监听器",
                listenersHint = "一次上传依次触发 upload-start、upload-progress、upload-success，失败时触发 upload-error；" +
                    "列表与校验另有 files-changed、file-reject。示例用模拟请求走完一次成功上传。",
                startUpload = "开始上传",
                bestHeading = "最佳实践",
                bestHint = "按钮与提示文案写清楚要传什么、格式与大小限制是什么，用户才能一次选对文件。",
                buttonLabellingCaption = "按钮文案",
                helperCaption = "辅助说明",
                uploadSpreadsheet = "上传表格",
                tooBigOneMb = "文件大小不超过 1 MB。",
                spreadsheetOnly = "仅接受 Excel 与 CSV 文件。",
                uploadSpreadsheetOne = "上传表格…",
                dropSpreadsheetOne = "把表格拖到这里",
                supportedFormats = "请提供受支持格式的文件（.xls、.xlsx、.csv）。",
                errorsHeading = "错误消息",
                errorsHint = "同样的失败状态，文案不同效果也不同：只写「服务器错误」不够有用，告诉用户接下来能做什么才好。" +
                    "示例用 i18n 替换默认的 unexpectedServerError 文案。",
                cautionCaption = "不推荐",
                recommendedCaption = "推荐",
                unexpectedServerError = "服务器错误",
                fileNotUploaded = "文件未能上传，请稍后重试",
                modularHeading = "模块化上传",
                modularHint = "UploadManager 把校验、队列与请求从界面里拆出来；上传按钮、拖放区与文件列表通过 manager 属性" +
                    "连接到同一个实例，可以分别摆在不同位置。",
                modularBasicCaption = "基本用法",
                selectFilesOne = "选择文件",
                browse = "浏览…",
                dropHereOr = "把文件拖到这里，或",
                dropZoneCaption = "拖放区",
                thumbnailsCaption = "缩略图",
                disabledHeading = "禁用",
                disabledHint = "禁用整份上传界面用 disabled；模块化上传里应同时禁用 manager，只禁用按钮挡不住直接发来的请求。",
                uploadDisabled = "上传已禁用",
                disabledManagerCaption = "禁用的管理器",
                queued = "排队中",
            )
        private val uploadTexts: Translations<UploadTexts> = texts(
            Langs.EN to english,
            Langs.ZH_HANS to chinese,
        )
        /** The Finnish copy the internationalization example shows; it never follows the page language. */
        private const val FINNISH_I18N =
            """
            {
              "dropFiles": {"one": "Raahaa tiedosto tähän", "many": "Raahaa tiedostot tähän"},
              "addFiles": {"one": "Valitse tiedosto...", "many": "Valitse tiedostot..."},
              "error": {
                "tooManyFiles": "Liian monta tiedostoa.",
                "fileIsTooBig": "Tiedosto on liian suuri.",
                "incorrectFileType": "Väärä tiedostomuoto."
              },
              "uploading": {
                "status": {
                  "connecting": "Yhdistetään...",
                  "stalled": "Pysäytetty",
                  "processing": "Käsitellään tiedostoa...",
                  "held": "Jonossa"
                },
                "remainingTime": {
                  "prefix": "aikaa jäljellä: ",
                  "unknown": "jäljellä olevaa aikaa ei saatavilla"
                },
                "error": {
                  "serverUnavailable": "Palvelin ei vastaa",
                  "unexpectedServerError": "Palvelinvirhe",
                  "forbidden": "Kielletty"
                }
              },
              "units": {
                "size": ["t", "kt", "Mt", "Gt", "Tt", "Pt", "Et", "ZB", "YB"],
                "sizeBase": 1000
              }
            }
            """
    }
}
