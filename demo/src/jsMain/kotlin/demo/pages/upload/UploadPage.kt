package demo.pages.upload

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.tr
import demo.ui.EventLog
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.Readout
import demo.ui.anchor
import demo.ui.column
import demo.ui.div
import demo.ui.element
import demo.ui.example
import demo.ui.grid
import demo.ui.heading
import demo.ui.jsObject
import demo.ui.listen
import demo.ui.page
import demo.ui.paragraph
import demo.ui.section
import demo.ui.span
import demo.ui.strong
import demo.ui.withClass
import demo.ui.withTheme
import js.objects.unsafeJso
import kotlin.js.unsafeCast
import vaadin.button.createButton
import vaadin.icon.createIcon
import vaadin.notification.Notification
import vaadin.notification.NotificationShowOptions
import vaadin.upload.UploadEvents
import vaadin.upload.UploadI18n
import vaadin.upload.UploadManagerOptions
import vaadin.upload.createUploadButton
import vaadin.upload.createUploadDropZone
import vaadin.upload.createUploadFileList
import vaadin.upload.createUploadManager
import web.dom.clickEvent
import web.events.addHandler
import web.html.HtmlTagName

val uploadEntry: CatalogEntry =
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
                docsUrl = "$DOCS_BASE/upload",
                sourcePath = "pages/upload/UploadPage.kt",
            ),
        builder = ::buildUploadPage,
    )

private fun buildUploadPage(meta: PageMeta): PageView {
    val view = page(meta)
    appendBasic(view)
    appendDragAndDrop(view)
    appendAutoUpload(view)
    appendRestrictions(view)
    appendFileActions(view)
    appendInternationalization(view)
    appendCustomization(view)
    appendListeners(view)
    appendBestPractices(view)
    appendErrorMessages(view)
    appendModular(view)
    appendDisabled(view)
    return view
}

private fun appendBasic(view: PageView) {
    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "target 指向处理上传的 URL；files 里每个条目用 status / progress / error 描述上传状态。" +
                    "示例直接填入假文件，展示完成、上传中与失败三种状态，不会发出请求。",
                "target points at the URL that handles the upload; each entry in files describes its state " +
                    "through status / progress / error. The example fills in fake files showing a completed, " +
                    "an in-flight and a failed upload, and sends no request.",
            ),
        )
    val upload = mockedUpload { target = "/api/fileupload" }
    upload.dataset["test"] = "upload-basic"
    upload.files =
        uploadFiles(
            """
            [
              {"name": "Annual Report.docx", "complete": true},
              {"name": "Workflow.pdf", "progress": 60,
               "status": "19.7 MB: 60% (remaining time: 00:12:34)"},
              {"name": "Financials.xlsx", "error": "${tr("上传失败，请重试", "An error occurred")}"}
            ]
            """.trimIndent(),
        )
    basic.body.append(example(null, upload))
    view.body.append(basic.root)
}

private fun appendDragAndDrop(view: PageView) {
    val dragAndDrop =
        section(
            tr("拖放", "Drag & drop"),
            tr(
                "拖放在桌面端默认启用、触摸设备默认禁用；把 nodrop 显式设成 false 或 true 会同时作用于桌面与移动端。",
                "Drag and drop is enabled on desktop and disabled on touch devices by default; setting nodrop " +
                    "explicitly to false or true applies to both.",
            ),
        )
    val dropEnabled = mockedUpload { nodrop = false }
    dropEnabled.dataset["test"] = "upload-drop-enabled"
    val dropDisabled = mockedUpload { nodrop = true }
    dropDisabled.dataset["test"] = "upload-drop-disabled"
    dragAndDrop.body.append(
        example(
            null,
            grid(
                column(strong(tr("拖放已启用", "Drag and drop enabled")), dropEnabled),
                column(strong(tr("拖放已禁用", "Drag and drop disabled")), dropDisabled),
            ),
        ),
    )
    view.body.append(dragAndDrop.root)
}

private fun appendAutoUpload(view: PageView) {
    val autoUpload =
        section(
            tr("自动上传", "Auto-upload"),
            tr(
                "默认选中文件后立即排队上传；noAuto 打开后文件停留在列表里，需要点每个文件的启动按钮、" +
                    "或者调用 uploadFiles() 才开始。",
                "Files are queued for upload as soon as they are selected; with noAuto they stay in the list " +
                    "until the start button of a file or uploadFiles() is used.",
            ),
        )

    val manual = mockedUpload {
        noAuto = true
        target = "/api/fileupload"
    }
    manual.dataset["test"] = "upload-no-auto"
    manual.i18nProperty =
        unsafeJso<UploadI18n> {
            addFiles = unsafeJso { many = tr("选择文件…", "Select Files...") }
        }
    manual.files = heldFiles("Workflow.pdf")
    autoUpload.body.append(
        example(
            tr("关闭自动上传", "Auto-upload disabled"),
            manual,
        ),
    )

    val uploadAll = mockedUpload {
        noAuto = true
        target = "/api/fileupload"
    }
    uploadAll.dataset["test"] = "upload-all"
    uploadAll.i18nProperty =
        unsafeJso<UploadI18n> {
            addFiles = unsafeJso { many = tr("选择文件…", "Select Files...") }
        }
    uploadAll.files =
        heldFiles(
            tr("Workflow.pdf", "Workflow.pdf"),
            tr("Financials.xlsx", "Financials.xlsx"),
        )
    mockSuccessfulRequests(uploadAll)
    val readout = Readout(tr("尚未开始上传", "Not uploading yet"))
    val refreshReadout = { readout.set(tr("上传状态：", "Upload status: "), fileSummary(uploadAll)) }
    uploadAll.listen(UploadEvents.uploadStart) { refreshReadout() }
    uploadAll.listen(UploadEvents.uploadProgress) { refreshReadout() }
    uploadAll.listen(UploadEvents.uploadSuccess) { refreshReadout() }
    val startAll = createButton { textContent = tr("开始全部上传", "Upload All Files") }
    startAll.withTheme("primary")
    startAll.clickEvent.addHandler { uploadAll.uploadFiles() }
    autoUpload.body.append(
        example(
            tr("一次性开始全部上传", "Starting all uploads at once"),
            column(uploadAll, startAll, readout.root),
        ),
    )
    view.body.append(autoUpload.root)
}

private fun appendRestrictions(view: PageView) {
    val restrictions =
        section(
            tr("上传限制", "Upload restrictions"),
            tr(
                "格式、数量与大小都在客户端先校验，被拒绝的文件不会进入列表；file-reject 事件带着原因，" +
                    "示例用 Notification 提示。",
                "Format, count and size are validated on the client first and rejected files never enter the " +
                    "list; the file-reject event carries the reason, shown here through a Notification.",
            ),
        )

    val report = mockedUpload {
        accept = "application/pdf,.pdf"
        maxFiles = 1.0
    }
    report.dataset["test"] = "upload-format"
    report.i18nProperty =
        unsafeJso<UploadI18n> {
            addFiles = unsafeJso { one = tr("上传报告…", "Upload Report...") }
            dropFiles = unsafeJso { one = tr("把报告拖到这里", "Drop report here") }
            error =
                unsafeJso {
                    incorrectFileType =
                        tr("文件格式不正确（需要 PDF 文档）。", "The provided file does not have the correct format (PDF document).")
                }
        }
    report.listen(UploadEvents.fileReject) { event -> showRejection(event) }
    restrictions.body.append(
        example(
            null,
            column(
                heading(4, tr("上传报告", "Upload report")),
                paragraph(tr("可接受的文件格式：PDF（.pdf）", "Accepted file formats: PDF (.pdf)"), "muted"),
                report,
            ),
        ),
    )

    val count = mockedUpload { maxFiles = 3.0 }
    count.dataset["test"] = "upload-count"
    count.i18nProperty =
        unsafeJso<UploadI18n> {
            error = unsafeJso { tooManyFiles = tr("一次最多只能上传 3 个文件。", "You may only upload a maximum of three files at once.") }
        }
    count.listen(UploadEvents.fileReject) { event -> showRejection(event) }
    restrictions.body.append(
        example(
            null,
            column(
                heading(4, tr("上传文件", "Upload files")),
                paragraph(tr("最多允许 3 个文件", "Maximum of 3 files allowed"), "muted"),
                count,
            ),
        ),
    )

    val size = mockedUpload {
        maxFiles = 1.0
        maxFileSize = 10.0 * 1024 * 1024
    }
    size.dataset["test"] = "upload-size"
    size.i18nProperty =
        unsafeJso<UploadI18n> {
            error = unsafeJso { fileIsTooBig = tr("文件超过 10 MB 的大小上限。", "The file exceeds the maximum allowed size of 10MB.") }
        }
    size.listen(UploadEvents.fileReject) { event -> showRejection(event) }
    restrictions.body.append(
        example(
            null,
            column(
                heading(4, tr("上传文件", "Upload file")),
                paragraph(tr("最大文件大小：10 MB", "Maximum file size: 10 MB"), "muted"),
                size,
            ),
        ),
    )
    view.body.append(restrictions.root)
}

private fun appendFileActions(view: PageView) {
    val actions =
        section(
            tr("文件操作", "File actions"),
            tr(
                "每个文件始终带清除按钮；失败时出现重试按钮，队列中的文件出现启动按钮。示例直接构造对应状态的文件。",
                "Every file always has a clear button; a failed upload shows a retry button and a queued file " +
                    "shows a start button. The examples build files in those states directly.",
            ),
        )
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
    actions.body.append(example(tr("清除 / 移除", "Clear / Remove"), inProgress))

    val failed = mockedUpload()
    failed.dataset["test"] = "upload-retry"
    failed.files =
        uploadFiles(
            """
            [{"name": "Financials.xlsx", "error": "${tr("出了点问题，请重试", "Something went wrong, please try again")}"}]
            """.trimIndent(),
        )
    actions.body.append(example(tr("重试", "Retry"), failed))

    val queued = mockedUpload()
    queued.dataset["test"] = "upload-start"
    queued.files = heldFiles("Workflow.pdf")
    actions.body.append(example(tr("启动", "Start"), queued))
    view.body.append(actions.root)
}

private fun appendInternationalization(view: PageView) {
    val i18n =
        section(
            tr("国际化", "Internationalization"),
            tr(
                "所有按钮、状态与错误文案都能通过 i18n 替换；示例换成芬兰语，覆盖拖放区、按钮、错误、" +
                    "剩余时间与文件大小单位。",
                "Every button, status and error text can be replaced through i18n; the example switches to " +
                    "Finnish and covers the drop label, the button, the errors, the remaining time and the size units.",
            ),
        )
    val upload = mockedUpload()
    upload.dataset["test"] = "upload-i18n"
    upload.i18nProperty =
        jsObject(
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
            """.trimIndent(),
        ).unsafeCast<UploadI18n>()
    i18n.body.append(example(null, upload))
    view.body.append(i18n.root)
}

private fun appendCustomization(view: PageView) {
    val customization =
        section(
            tr("自定义", "Customization"),
            tr(
                "默认上传按钮可以换成插槽里的自定义按钮（例如更强的 primary 主题），拖放区的图标与文案也由插槽提供。",
                "The default upload button can be replaced by a slotted custom button (for example a stronger " +
                    "primary variant), and the drop area icon and text come from slots as well.",
            ),
        )

    val primaryButton = createButton { textContent = tr("上传 PDF…", "Upload PDF...") }
    primaryButton.withTheme("primary")
    primaryButton.slot = "add-button"
    val buttonUpload = mockedUpload {
        maxFiles = 1.0
        accept = "application/pdf,.pdf"
    }
    buttonUpload.dataset["test"] = "upload-button-variant"
    buttonUpload.i18nProperty =
        unsafeJso<UploadI18n> {
            dropFiles = unsafeJso { one = tr("把 PDF 拖到这里", "Drop PDF here") }
        }
    buttonUpload.append(primaryButton)
    buttonUpload.listen(UploadEvents.maxFilesReachedChanged) {
        primaryButton.disabled = buttonUpload.maxFilesReached
    }
    buttonUpload.listen(UploadEvents.fileReject) { event ->
        Notification.show(
            tr("错误：", "Error: ") + rejectText(event) + " '" + rejectedFileName(event) + "'",
            unsafeJso<NotificationShowOptions> { theme = "error" },
        )
    }
    customization.body.append(
        example(
            tr("自定义上传按钮", "Custom upload button"),
            column(
                paragraph(
                    tr("达到数量上限后按钮会被禁用；选错文件时用 Notification 给出原因。", "The button is disabled once the file limit is reached; a rejected file is reported through a Notification."),
                    "muted",
                ),
                buttonUpload,
            ),
        ),
    )

    val dropLabel = mockedUpload()
    dropLabel.dataset["test"] = "upload-drop-label"
    dropLabel.append(
        createIcon { icon = "vaadin:cloud-upload" }.apply { slot = "drop-label-icon" },
        element(HtmlTagName.span).apply {
            slot = "drop-label"
            append(
                span(tr("文件会上传到我们的云端。参见", "Files will be uploaded to our cloud. See our ")),
                anchor(tr("隐私政策", "privacy policy"), "https://vaadin.com/privacy-policy", external = true),
            )
        },
    )
    customization.body.append(example(tr("自定义拖放区文案与图标", "Custom drop label and icon"), dropLabel))
    view.body.append(customization.root)
}

private fun appendListeners(view: PageView) {
    val listeners =
        section(
            tr("监听器", "Listeners"),
            tr(
                "一次上传依次触发 upload-start、upload-progress、upload-success，失败时触发 upload-error；" +
                    "列表与校验另有 files-changed、file-reject。示例用模拟请求走完一次成功上传。",
                "An upload fires upload-start, upload-progress and upload-success in order, and upload-error " +
                    "on failure; files-changed and file-reject cover the list and validation. The example runs one " +
                    "successful upload against a simulated request.",
            ),
        )
    val upload = mockedUpload { target = "/api/fileupload" }
    upload.dataset["test"] = "upload-listeners"
    upload.files = heldFiles("Workflow.pdf")
    val log = EventLog(6)
    upload.listen(UploadEvents.uploadStart) { log.add("upload-start") }
    upload.listen(UploadEvents.uploadProgress) {
        log.add("upload-progress · ${fileProgress(upload.files.firstOrNull())}")
    }
    upload.listen(UploadEvents.uploadSuccess) { log.add("upload-success") }
    upload.listen(UploadEvents.uploadError) { log.add("upload-error") }
    val start = createButton { textContent = tr("开始上传", "Start upload") }
    start.withTheme("primary")
    start.clickEvent.addHandler { upload.uploadFiles() }
    listeners.body.append(example(null, column(upload, start, log.root)))
    view.body.append(listeners.root)
}

private fun appendBestPractices(view: PageView) {
    val bestPractices =
        section(
            tr("最佳实践", "Best practices"),
            tr(
                "按钮与提示文案写清楚要传什么、格式与大小限制是什么，用户才能一次选对文件。",
                "The button and helper text should say what to upload and which limits apply, so users pick the " +
                    "right file the first time.",
            ),
        )

    val labelled = mockedUpload {
        maxFiles = 1.0
        accept = "application/pdf,.pdf"
    }
    labelled.dataset["test"] = "upload-labelling"
    labelled.i18nProperty =
        unsafeJso<UploadI18n> {
            addFiles = unsafeJso { one = tr("上传报告…", "Upload Report...") }
            dropFiles = unsafeJso { one = tr("把报告拖到这里", "Drop report here") }
            error =
                unsafeJso {
                    incorrectFileType =
                        tr("文件格式不正确（需要 PDF 文档）。", "The provided file does not have the correct format (PDF document).")
                }
        }
    labelled.listen(UploadEvents.fileReject) { event -> showRejection(event) }
    bestPractices.body.append(example(tr("按钮文案", "Button labelling"), labelled))

    val spreadsheet = mockedUpload {
        maxFiles = 1.0
        maxFileSize = 1.0 * 1024 * 1024
        accept =
            "application/vnd.ms-excel,.xls," +
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,.xlsx,text/csv,.csv"
    }
    spreadsheet.dataset["test"] = "upload-helper"
    spreadsheet.i18nProperty =
        unsafeJso<UploadI18n> {
            addFiles = unsafeJso { one = tr("上传表格…", "Upload Spreadsheet...") }
            dropFiles = unsafeJso { one = tr("把表格拖到这里", "Drop spreadsheet here") }
            error =
                unsafeJso {
                    incorrectFileType =
                        tr(
                            "请提供受支持格式的文件（.xls、.xlsx、.csv）。",
                            "Provide the file in one of the supported formats (.xls, .xlsx, .csv).",
                        )
                }
        }
    spreadsheet.listen(UploadEvents.fileReject) { event -> showRejection(event) }
    bestPractices.body.append(
        example(
            tr("辅助说明", "Helper text"),
            column(
                heading(4, tr("上传表格", "Upload spreadsheet")),
                paragraph(tr("文件大小不超过 1 MB。", "File size must be less than or equal to 1 MB."), "muted"),
                paragraph(tr("仅接受 Excel 与 CSV 文件。", "Only Excel and CSV files are accepted."), "muted"),
                spreadsheet,
            ),
        ),
    )
    view.body.append(bestPractices.root)
}

private fun appendErrorMessages(view: PageView) {
    val errorMessages =
        section(
            tr("错误消息", "Error messages"),
            tr(
                "同样的失败状态，文案不同效果也不同：只写「服务器错误」不够有用，告诉用户接下来能做什么才好。" +
                    "示例用 i18n 替换默认的 unexpectedServerError 文案。",
                "The same failure reads very differently depending on the message: \"Unexpected Server Error\" " +
                    "says little, while telling users what to do next helps. The example replaces the default " +
                    "unexpectedServerError text through i18n.",
            ),
        )
    val caution = mockedUpload { nodrop = true }
    caution.dataset["test"] = "upload-caution"
    caution.i18nProperty =
        unsafeJso<UploadI18n> {
            uploading = unsafeJso { error = unsafeJso { unexpectedServerError = tr("服务器错误", "Unexpected Server Error") } }
        }
    caution.files =
        uploadFiles("""[{"name": "Financials.xlsx", "error": "${tr("服务器错误", "Unexpected Server Error")}"}]""")

    val recommended = mockedUpload { nodrop = true }
    recommended.dataset["test"] = "upload-recommended"
    recommended.i18nProperty =
        unsafeJso<UploadI18n> {
            uploading =
                unsafeJso {
                    error =
                        unsafeJso {
                            unexpectedServerError = tr("文件未能上传，请稍后重试", "File couldn't be uploaded, try again later")
                        }
                }
        }
    recommended.files =
        uploadFiles(
            """[{"name": "Financials.xlsx", "error": "${tr("文件未能上传，请稍后重试", "File couldn't be uploaded, try again later")}"}]""",
        )
    errorMessages.body.append(
        example(
            null,
            grid(
                column(strong(tr("不推荐", "Caution")), caution),
                column(strong(tr("推荐", "Recommended")), recommended),
            ),
        ),
    )
    view.body.append(errorMessages.root)
}

private fun appendModular(view: PageView) {
    val modular =
        section(
            tr("模块化上传", "Modular upload"),
            tr(
                "UploadManager 把校验、队列与请求从界面里拆出来；上传按钮、拖放区与文件列表通过 manager 属性" +
                    "连接到同一个实例，可以分别摆在不同位置。",
                "UploadManager separates validation, the queue and requests from the UI; the upload button, the " +
                    "drop zone and the file list connect to the same instance through the manager property and can " +
                    "be placed independently.",
            ),
        )

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
    val basicButton = createUploadButton { textContent = tr("选择文件", "Select Files") }
    basicButton.manager = basicManager
    val basicList = createUploadFileList()
    basicList.manager = basicManager
    basicList.withClass("full-width")
    modular.body.append(
        example(
            tr("基本用法", "Basic usage"),
            column(basicButton, basicList),
        ),
    )

    val zoneManager = createUploadManager(unsafeJso<UploadManagerOptions> { target = "/api/fileupload"; maxFiles = 10.0 })
    mockSuccessfulRequests(zoneManager)
    zoneManager.files = heldFiles("Quarterly budget.pdf")
    val browseButton = createUploadButton { textContent = tr("浏览…", "Browse") }
    browseButton.manager = zoneManager
    val dropZone = createUploadDropZone()
    dropZone.manager = zoneManager
    dropZone.withClass("upload-drop-zone")
    dropZone.append(
        div("upload-drop-zone__content").apply {
            append(
                createIcon { icon = "vaadin:upload" },
                span(tr("把文件拖到这里，或", "Drop files here or")),
                browseButton,
            )
        },
    )
    val zoneList = createUploadFileList()
    zoneList.manager = zoneManager
    zoneList.withClass("full-width")
    modular.body.append(
        example(
            tr("拖放区", "Drop zone"),
            column(dropZone, zoneList),
        ),
    )

    val thumbnailsManager = createUploadManager(unsafeJso<UploadManagerOptions> { target = "/api/fileupload" })
    mockSuccessfulRequests(thumbnailsManager)
    thumbnailsManager.files =
        uploadFiles(
            """
            [
              {"name": "Annual Report.docx", "complete": true},
              {"name": "Financials.xlsx", "error": "${tr("上传失败，请重试", "An error occurred")}"}
            ]
            """.trimIndent(),
        )
    val thumbnailsButton = createUploadButton { textContent = tr("选择文件", "Select Files") }
    thumbnailsButton.manager = thumbnailsManager
    val thumbnailsList = createUploadFileList()
    thumbnailsList.manager = thumbnailsManager
    thumbnailsList.withTheme("thumbnails")
    thumbnailsList.withClass("full-width")
    modular.body.append(
        example(
            tr("缩略图", "Thumbnails"),
            column(thumbnailsButton, thumbnailsList),
        ),
    )
    view.body.append(modular.root)
}

private fun appendDisabled(view: PageView) {
    val disabled =
        section(
            tr("禁用", "Disabled"),
            tr(
                "禁用整份上传界面用 disabled；模块化上传里应同时禁用 manager，只禁用按钮挡不住直接发来的请求。",
                "Use disabled to lock the whole upload UI; in the modular setup the manager must be disabled as " +
                    "well, since disabling the button alone does not stop requests sent directly.",
            ),
        )
    val upload = mockedUpload {
        noAuto = true
        this.disabled = true
    }
    upload.dataset["test"] = "upload-disabled"
    disabled.body.append(example(null, upload))

    val lockedManager = createUploadManager(unsafeJso<UploadManagerOptions> { target = "/api/fileupload" })
    lockedManager.disabled = true
    val lockedButton = createUploadButton { textContent = tr("上传已禁用", "Upload disabled") }
    lockedButton.manager = lockedManager
    val lockedList = createUploadFileList()
    lockedList.manager = lockedManager
    lockedList.disabled = true
    lockedList.withClass("full-width")
    disabled.body.append(example(tr("禁用的管理器", "Disabled manager"), column(lockedButton, lockedList)))
    view.body.append(disabled.root)
}
