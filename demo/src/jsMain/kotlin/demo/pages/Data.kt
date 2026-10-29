package demo.pages

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.tr
import demo.ui.EventLog
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.Readout
import demo.ui.column
import demo.ui.example
import demo.ui.jsArray
import demo.ui.listen
import demo.ui.page
import demo.ui.row
import demo.ui.section
import demo.ui.withTheme
import kotlin.js.JsAny
import kotlin.js.unsafeCast
import vaadin.button.createButton
import vaadin.markdown.createMarkdown
import vaadin.messageinput.MessageInputEvents
import vaadin.messageinput.createMessageInput
import vaadin.messagelist.MessageListEvents
import vaadin.messagelist.MessageListItem
import vaadin.messagelist.createMessageList
import vaadin.upload.UploadEvents
import vaadin.upload.createUpload
import vaadin.virtuallist.createVirtualList
import web.dom.clickEvent
import web.events.addHandler
import web.html.HTMLElement

val dataEntries: List<CatalogEntry> =
    listOf(
        CatalogEntry(
            id = "virtual-list",
            group = Groups.DATA,
            meta =
                PageMeta(
                    title = bi("Virtual List", "Virtual List"),
                    summary =
                        bi(
                            "只用 renderer 渲染可见行的长列表，适合成千上万条数据。",
                            "Long lists that render only visible rows through a renderer, built for " +
                                "thousands of items.",
                        ),
                    docsUrl = "$DOCS_BASE/virtual-list",
                    sourcePath = "pages/Data.kt",
                ),
            builder = ::buildVirtualListPage,
        ),
        CatalogEntry(
            id = "message-list",
            group = Groups.DATA,
            meta =
                PageMeta(
                    title = bi("Message List", "Message List"),
                    summary =
                        bi(
                            "聊天式消息列表：items 描述每条消息的文本、作者与时间。",
                            "Chat-style message list: items describe the text, author and time of each message.",
                        ),
                    docsUrl = "$DOCS_BASE/message-list",
                    sourcePath = "pages/Data.kt",
                ),
            builder = ::buildMessageListPage,
        ),
        CatalogEntry(
            id = "message-input",
            group = Groups.DATA,
            meta =
                PageMeta(
                    title = bi("Message Input", "Message Input"),
                    summary =
                        bi(
                            "消息输入框：value 与 submit 事件，适合与 Message List 组合。",
                            "Message input field: value and the submit event, designed to pair with Message List.",
                        ),
                    docsUrl = "$DOCS_BASE/message-input",
                    sourcePath = "pages/Data.kt",
                ),
            builder = ::buildMessageInputPage,
        ),
        CatalogEntry(
            id = "markdown",
            group = Groups.DATA,
            meta =
                PageMeta(
                    title = bi("Markdown", "Markdown"),
                    summary =
                        bi(
                            "把 Markdown 字符串渲染成富文本，可选择是否保留换行。",
                            "Renders a Markdown string as rich text, optionally keeping single line breaks.",
                        ),
                    docsUrl = "$DOCS_BASE/markdown",
                    sourcePath = "pages/Data.kt",
                ),
            builder = ::buildMarkdownPage,
        ),
        CatalogEntry(
            id = "upload",
            group = Groups.DATA,
            meta =
                PageMeta(
                    title = bi("Upload", "Upload"),
                    summary =
                        bi(
                            "文件选择与上传列表；本页不连后端，只演示选择、数量限制与移除。",
                            "File picker and upload list; this page has no backend and only shows selection, " +
                                "the file limit and removal.",
                        ),
                    docsUrl = "$DOCS_BASE/upload",
                    sourcePath = "pages/Data.kt",
                ),
            builder = ::buildUploadPage,
        ),
    )

private fun buildVirtualListPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "renderer 只对可见行调用；滚动时复用容器节点，因此看起来像无限长的列表。",
                "The renderer is called only for visible rows and container nodes are reused while scrolling, " +
                    "so the list looks endless.",
            ),
        )
    val list = createVirtualList()
    list.dataset["test"] = "virtual-list"
    list.items = jsArray<JsAny>((1..1000).joinToString(",", "[", "]"))
    list.renderer = { container, _, model ->
        container.textContent = tr("第 ${model.index + 1} 行", "Row ${model.index + 1}")
    }
    list.style.setProperty("height", "16rem")
    basic.body.append(example(null, list))
    view.body.append(basic.root)

    val jump =
        section(
            tr("定位", "Scrolling to an index"),
            tr("scrollToIndex 直接跳到指定行。", "scrollToIndex jumps straight to the given row."),
        )
    val jumpList = createVirtualList()
    jumpList.items = jsArray<JsAny>((1..500).joinToString(",", "[", "]"))
    jumpList.renderer = { container, _, model ->
        container.textContent = tr("行 ${model.index + 1}", "Row ${model.index + 1}")
    }
    jumpList.style.setProperty("height", "12rem")
    jump.body.append(
        example(
            null,
            column(
                jumpList,
                row(
                    createButton { textContent = tr("跳到第 1 行", "Go to row 1") }.withTheme("tertiary").apply {
                        clickEvent.addHandler { jumpList.scrollToIndex(0.0) }
                    },
                    createButton { textContent = tr("跳到第 300 行", "Go to row 300") }.withTheme("tertiary").apply {
                        clickEvent.addHandler { jumpList.scrollToIndex(299.0) }
                    },
                ),
            ),
        ),
    )
    view.body.append(jump.root)

    return view
}

private fun buildMessageListPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "items 里每条消息提供 text / userName / userAbbr / time；markdown 打开后按 Markdown 渲染。",
                "Each item provides text / userName / userAbbr / time; with markdown on, the text is " +
                    "rendered as Markdown.",
            ),
        )
    val messages =
        createMessageList()
    messages.dataset["test"] = "message-list"
    messages.items =
        jsArray<JsAny?>(
            """
            [
              {"text": "${tr("今天的构建通过了。", "Today's build passed.")}",
               "userName": "Ada Lovelace", "userAbbr": "AL", "time": "09:12", "userColorIndex": 0},
              {"text": "${tr("我把 `Grid` 的示例补完了。", "I finished the `Grid` examples.")}",
               "userName": "Grace Hopper", "userAbbr": "GH", "time": "09:15", "userColorIndex": 1},
              {"text": "${tr("谢谢！**合并**吧。", "Thanks! Let's **merge** it.")}",
               "userName": "You", "userAbbr": "ME", "time": "09:16", "userColorIndex": 2}
            ]
            """.trimIndent(),
        ).unsafeCast<Array<MessageListItem>>()
    messages.markdown = true
    basic.body.append(example(null, messages))
    view.body.append(basic.root)

    val append =
        section(
            tr("追加消息", "Appending messages"),
            tr(
                "items 是普通数组，替换后列表局部更新；这里演示把 Message Input 的内容追加进去。",
                "items is a plain array and replacing it updates the list in place; this example appends " +
                    "what Message Input submits.",
            ),
        )
    val liveList = createMessageList()
    liveList.items =
        jsArray<JsAny?>(
            """
            [{"text": "${tr("说点什么吧。", "Say something.")}",
              "userName": "System", "userAbbr": "SY", "time": "09:00"}]
            """.trimIndent(),
        ).unsafeCast<Array<MessageListItem>>()
    val input = createMessageInput()
    val log = EventLog(4)
    input.listen(MessageInputEvents.submit) {
        val text = input.value
        if (text.isNotEmpty()) {
            liveList.items = appendMessage(liveList.items, text)
            log.add("submit · $text")
            input.value = ""
        }
    }
    append.body.append(example(null, column(liveList, input, log.root)))
    view.body.append(append.root)

    return view
}

private fun buildMessageInputPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "回车或点击发送触发 submit；value 不会自动清空，由调用方决定。",
                "Enter or the send button fires submit; value is not cleared automatically and the caller decides.",
            ),
        )
    val input = createMessageInput()
    input.dataset["test"] = "message-input"
    val readout = Readout(tr("尚未发送", "Not sent yet"))
    val log = EventLog(4)
    input.listen(MessageInputEvents.submit) {
        readout.set(tr("已发送：", "Sent: "), input.value)
        log.add("submit · ${input.value}")
    }
    basic.body.append(example(null, column(input, readout.root, log.root)))
    view.body.append(basic.root)

    val disabled = section(tr("禁用", "Disabled"), "")
    val disabledInput = createMessageInput()
    disabledInput.disabled = true
    disabled.body.append(example(null, disabledInput))
    view.body.append(disabled.root)

    return view
}

private fun buildMarkdownPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "content 是 Markdown 源码，组件把它渲染成 HTML。",
                "content holds Markdown source, which the component renders as HTML.",
            ),
        )
    val markdown = createMarkdown()
    markdown.dataset["test"] = "markdown"
    val features =
        tr(
            "Markdown 组件支持 **粗体**、*斜体*、`行内代码` 与列表：",
            "The Markdown component supports **bold**, *italic*, `inline code` and lists:",
        )
    markdown.content =
        """
        # ${tr("标题", "Title")}

        $features

        - ${tr("第一项", "First item")}
        - ${tr("第二项", "Second item")}

        > ${tr("引用也可以。", "Blockquotes work too.")}
        """.trimIndent()
    basic.body.append(example(null, markdown))
    view.body.append(basic.root)

    val lineBreakSection =
        section(
            "lineBreaks",
            tr(
                "打开后单个换行也会变成 <br>，否则按标准 Markdown 合并成一段。",
                "When on, a single line break becomes <br>; otherwise standard Markdown joins the " +
                    "lines into one paragraph.",
            ),
        )
    val withBreaks = createMarkdown { lineBreaks = true }
    withBreaks.content = tr("第一行\n第二行\n第三行", "First line\nSecond line\nThird line")
    val withoutBreaks = createMarkdown()
    withoutBreaks.content = tr("第一行\n第二行\n第三行", "First line\nSecond line\nThird line")
    lineBreakSection.body.append(example(null, row(withBreaks, withoutBreaks)))
    view.body.append(lineBreakSection.root)

    return view
}

private fun buildUploadPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("文件选择", "File selection"),
            tr(
                "noAuto 打开后文件只进入列表、不会真的上传，因此没有后端也能完整演示选择、移除与数量限制。",
                "With noAuto on, files only enter the list and are never uploaded, so selection, removal and " +
                    "the file limit work without a backend.",
            ),
        )
    val upload = createUpload()
    upload.dataset["test"] = "upload"
    upload.noAuto = true
    upload.maxFiles = 3.0
    upload.accept = "image/*,application/pdf"
    val readout = Readout(tr("已选择 0 / 3 个文件", "0 of 3 files selected"))
    val log = EventLog(4)
    upload.listen(UploadEvents.filesChanged) {
        readout.set(
            tr("已选择 ${upload.files.size} / 3 个文件：", "${upload.files.size} of 3 files selected: "),
            upload.files.joinToString(", ") { it.name },
        )
        log.add("files-changed · ${upload.files.size}")
    }
    val rejectMessage =
        tr("file-reject（超出数量或类型不符）", "file-reject (too many files or unsupported type)")
    upload.listen(UploadEvents.fileReject) { log.add(rejectMessage) }
    basic.body.append(example(null, column(upload, readout.root, log.root)))
    view.body.append(basic.root)

    val disabled = section(tr("禁用", "Disabled"), "")
    val disabledUpload = createUpload { noAuto = true }
    disabledUpload.disabled = true
    disabled.body.append(example(null, disabledUpload))
    view.body.append(disabled.root)

    return view
}

/** Appends [text] to a message list `items` array and returns the new array. */
private fun appendMessage(current: Array<MessageListItem>?, text: String): Array<MessageListItem> {
    // `Object.assign` keeps the object literal inside a call: the Kotlin/JS `js()` emitter drops the
    // parentheses of `() => ({ ... })`, which would turn the body into a block instead of an object.
    val message =
        kotlin.js.js("(text) => Object.assign({}, { text: text, userName: 'You', userAbbr: 'ME', time: 'now' })")(text)
            .unsafeCast<MessageListItem>()
    val existing: List<MessageListItem> = current?.toList() ?: emptyList()
    return (existing + message).toTypedArray()
}
