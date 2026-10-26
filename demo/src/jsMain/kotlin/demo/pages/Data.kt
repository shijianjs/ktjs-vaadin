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
import demo.ui.div
import demo.ui.example
import demo.ui.joinValues
import demo.ui.jsArray
import demo.ui.listen
import demo.ui.page
import demo.ui.paragraph
import demo.ui.row
import demo.ui.section
import demo.ui.withTheme
import kotlin.js.JsAny
import vaadin.button.createButton
import vaadin.grid.GridEvents
import vaadin.grid.GridSorterDirections
import vaadin.grid.createGrid
import vaadin.grid.createGridColumn
import vaadin.grid.createGridFilterColumn
import vaadin.grid.createGridSelectionColumn
import vaadin.grid.createGridSortColumn
import vaadin.markdown.createMarkdown
import vaadin.messageinput.MessageInputEvents
import vaadin.messageinput.createMessageInput
import vaadin.messagelist.MessageListEvents
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
            id = "grid",
            group = Groups.DATA,
            meta =
                PageMeta(
                    title = bi("Grid", "Grid"),
                    summary =
                        bi(
                            "数据表格：列、排序、筛选、选择、行详情与主题变体。",
                            "Data table: columns, sorting, filtering, selection, row details and theme variants.",
                        ),
                    docsUrl = "$DOCS_BASE/grid",
                    sourcePath = "pages/Data.kt",
                ),
            builder = ::buildGridPage,
        ),
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

private const val PEOPLE_JSON =
    """
    [
      {"name": "Ada Lovelace", "role": "Mathematician", "score": 98, "team": "Research"},
      {"name": "Grace Hopper", "role": "Computer scientist", "score": 95, "team": "Compiler"},
      {"name": "Margaret Hamilton", "role": "Software engineer", "score": 99, "team": "Apollo"},
      {"name": "Katherine Johnson", "role": "Mathematician", "score": 97, "team": "Orbital"},
      {"name": "Barbara Liskov", "role": "Programmer", "score": 96, "team": "Research"}
    ]
    """

private fun buildGridPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "列用 path 绑定 items 里的字段；选择列把选中项写进 selectedItems。",
                "Columns bind fields of items through path; the selection column writes selected rows " +
                    "into selectedItems.",
            ),
        )
    val log = EventLog(6)
    val grid = createGrid()
    grid.dataset["test"] = "grid-basic"
    // Grid defaults to `height: 100%`; inside an auto-height container it needs an explicit height.
    grid.style.setProperty("height", "18rem")
    grid.items = jsArray<JsAny>(PEOPLE_JSON)
    grid.append(
        createGridSelectionColumn(),
        createGridColumn {
            header = tr("姓名", "Name")
            path = "name"
        },
        createGridColumn {
            header = tr("职务", "Role")
            path = "role"
        },
        createGridColumn {
            header = tr("得分", "Score")
            path = "score"
            width = "6rem"
        },
    )
    val selection = Readout(tr("已选 0 行", "0 rows selected"))
    grid.listen(GridEvents.selectedItemsChanged) {
        selection.set(tr("已选 ${grid.selectedItems.size} 行", "${grid.selectedItems.size} rows selected"))
    }
    // `cell-activate` only fires for user interaction (double click / Enter), so the log starts
    // empty instead of recording the events the grid fires while it initialises.
    grid.listen(GridEvents.cellActivate) { log.add("cell-activate") }
    basic.body.append(example(null, column(grid, row(selection.root), log.root)))
    view.body.append(basic.root)

    val sorting =
        section(
            tr("排序与筛选", "Sorting and filtering"),
            tr(
                "sort-column 的 direction 由表头点击更新，filter-column 提供筛选输入框。",
                "Clicking a header updates the direction of sort-column, and filter-column adds a filter input.",
            ),
        )
    val sorted = createGrid()
    sorted.dataset["test"] = "grid-sorted"
    sorted.style.setProperty("height", "18rem")
    sorted.items = jsArray<JsAny>(PEOPLE_JSON)
    sorted.append(
        createGridSortColumn {
            header = tr("姓名", "Name")
            path = "name"
            direction = GridSorterDirections.asc
        },
        createGridSortColumn {
            header = tr("得分", "Score")
            path = "score"
            width = "6rem"
        },
        createGridFilterColumn {
            header = tr("团队", "Team")
            path = "team"
        },
        createGridColumn {
            header = tr("职务", "Role")
            path = "role"
        },
    )
    sorting.body.append(example(null, sorted))
    view.body.append(sorting.root)

    val variants =
        section(
            tr("行详情与主题", "Row details and themes"),
            tr(
                "rowDetailsRenderer 渲染展开区；theme 控制行条纹、边框与密度。",
                "rowDetailsRenderer renders the expanded area; theme controls row stripes, borders and density.",
            ),
        )
    val detailed = createGrid()
    detailed.dataset["test"] = "grid-details"
    detailed.items = jsArray<JsAny>(PEOPLE_JSON)
    detailed.allRowsVisible = true
    detailed.withTheme("row-stripes", "column-borders", "compact")
    detailed.append(
        createGridColumn {
            header = tr("姓名", "Name")
            path = "name"
        },
        createGridColumn {
            header = tr("团队", "Team")
            path = "team"
        },
    )
    detailed.rowDetailsRenderer = { root, _, model ->
        root.append(
            paragraph(
                tr(
                    "第 ${model.index + 1} 行的详情内容，由 rowDetailsRenderer 生成。",
                    "Details of row ${model.index + 1}, generated by rowDetailsRenderer.",
                ),
            ),
        )
    }
    val openDetails =
        createButton { textContent = tr("展开第一行详情", "Toggle first row details") }.withTheme("tertiary").apply {
            clickEvent.addHandler {
                gridDetailsToggle(detailed)
            }
        }
    variants.body.append(example(null, column(detailed, openDetails)))
    view.body.append(variants.root)

    return view
}

/** Opens or closes the details of the first row through the public API. */
private fun gridDetailsToggle(grid: vaadin.grid.GridElement) {
    val items = grid.items
    val first = items?.firstOrNull() ?: return
    if (grid.detailsOpenedItems.isNotEmpty()) {
        grid.closeItemDetails(first)
    } else {
        grid.openItemDetails(first)
    }
}

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
        )
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
        )
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
private fun appendMessage(current: Array<JsAny?>?, text: String): Array<JsAny?> {
    // `Object.assign` keeps the object literal inside a call: the Kotlin/JS `js()` emitter drops the
    // parentheses of `() => ({ ... })`, which would turn the body into a block instead of an object.
    val message: JsAny =
        kotlin.js.js("(text) => Object.assign({}, { text: text, userName: 'You', userAbbr: 'ME', time: 'now' })")(text)
    val existing: List<JsAny?> = current?.toList() ?: emptyList()
    return (existing + message).toTypedArray()
}
