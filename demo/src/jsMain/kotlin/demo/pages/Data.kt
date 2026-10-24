package demo.pages

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.ui.EventLog
import demo.ui.PageMeta
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
                    title = "Grid",
                    summary = "数据表格：列、排序、筛选、选择、行详情与主题变体。",
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
                    title = "Virtual List",
                    summary = "只用 renderer 渲染可见行的长列表，适合成千上万条数据。",
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
                    title = "Message List",
                    summary = "聊天式消息列表：items 描述每条消息的文本、作者与时间。",
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
                    title = "Message Input",
                    summary = "消息输入框：value 与 submit 事件，适合与 Message List 组合。",
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
                    title = "Markdown",
                    summary = "把 Markdown 字符串渲染成富文本，可选择是否保留换行。",
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
                    title = "Upload",
                    summary = "文件选择与上传列表；本页不连后端，只演示选择、数量限制与移除。",
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

private fun buildGridPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("基本用法", "列用 path 绑定 items 里的字段；选择列把选中项写进 selectedItems。")
    val log = EventLog(6)
    val grid = createGrid()
    grid.setAttribute("data-test", "grid-basic")
    // Grid defaults to `height: 100%`; inside an auto-height container it needs an explicit height.
    grid.style.setProperty("height", "18rem")
    grid.items = jsArray<JsAny>(PEOPLE_JSON)
    grid.append(
        createGridSelectionColumn(),
        createGridColumn {
            header = "姓名"
            path = "name"
        },
        createGridColumn {
            header = "职务"
            path = "role"
        },
        createGridColumn {
            header = "得分"
            path = "score"
            width = "6rem"
        },
    )
    val selection = Readout("已选 0 行")
    grid.listen(GridEvents.selectedItemsChanged) {
        selection.set("已选 ${grid.selectedItems.size} 行")
    }
    // `cell-activate` only fires for user interaction (double click / Enter), so the log starts
    // empty instead of recording the events the grid fires while it initialises.
    grid.listen(GridEvents.cellActivate) { log.add("cell-activate") }
    basic.body.append(example(null, column(grid, row(selection.root), log.root)))
    view.body.append(basic.root)

    val sorting = section("排序与筛选", "sort-column 的 direction 由表头点击更新，filter-column 提供筛选输入框。")
    val sorted = createGrid()
    sorted.setAttribute("data-test", "grid-sorted")
    sorted.style.setProperty("height", "18rem")
    sorted.items = jsArray<JsAny>(PEOPLE_JSON)
    sorted.append(
        createGridSortColumn {
            header = "姓名"
            path = "name"
            direction = "asc"
        },
        createGridSortColumn {
            header = "得分"
            path = "score"
            width = "6rem"
        },
        createGridFilterColumn {
            header = "团队"
            path = "team"
        },
        createGridColumn {
            header = "职务"
            path = "role"
        },
    )
    sorting.body.append(example(null, sorted))
    view.body.append(sorting.root)

    val variants = section("行详情与主题", "rowDetailsRenderer 渲染展开区；theme 控制行条纹、边框与密度。")
    val detailed = createGrid()
    detailed.setAttribute("data-test", "grid-details")
    detailed.items = jsArray<JsAny>(PEOPLE_JSON)
    detailed.allRowsVisible = true
    detailed.setAttribute("theme", "row-stripes column-borders compact")
    detailed.append(
        createGridColumn {
            header = "姓名"
            path = "name"
        },
        createGridColumn {
            header = "团队"
            path = "team"
        },
    )
    detailed.rowDetailsRenderer = { root, _, model ->
        root.append(paragraph("第 ${model.index + 1} 行的详情内容，由 rowDetailsRenderer 生成。"))
    }
    val openDetails =
        createButton { textContent = "展开第一行详情" }.withTheme("tertiary").apply {
            clickEvent.addHandler {
                gridDetailsToggle(detailed)
            }
        }
    variants.body.append(example(null, column(detailed, openDetails)))
    view.body.append(variants.root)

    return view.root
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

private fun buildVirtualListPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("基本用法", "renderer 只对可见行调用；滚动时复用容器节点，因此看起来像无限长的列表。")
    val list = createVirtualList()
    list.setAttribute("data-test", "virtual-list")
    list.items = jsArray<JsAny>((1..1000).joinToString(",", "[", "]"))
    list.renderer = { container, _, model ->
        container.textContent = "第 ${model.index + 1} 行"
    }
    list.style.setProperty("height", "16rem")
    basic.body.append(example(null, list))
    view.body.append(basic.root)

    val jump = section("定位", "scrollToIndex 直接跳到指定行。")
    val jumpList = createVirtualList()
    jumpList.items = jsArray<JsAny>((1..500).joinToString(",", "[", "]"))
    jumpList.renderer = { container, _, model -> container.textContent = "行 ${model.index + 1}" }
    jumpList.style.setProperty("height", "12rem")
    jump.body.append(
        example(
            null,
            column(
                jumpList,
                row(
                    createButton { textContent = "跳到第 1 行" }.withTheme("tertiary").apply {
                        clickEvent.addHandler { jumpList.scrollToIndex(0.0) }
                    },
                    createButton { textContent = "跳到第 300 行" }.withTheme("tertiary").apply {
                        clickEvent.addHandler { jumpList.scrollToIndex(299.0) }
                    },
                ),
            ),
        ),
    )
    view.body.append(jump.root)

    return view.root
}

private fun buildMessageListPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("基本用法", "items 里每条消息提供 text / userName / userAbbr / time；markdown 打开后按 Markdown 渲染。")
    val messages =
        createMessageList()
    messages.setAttribute("data-test", "message-list")
    messages.items =
        jsArray<JsAny?>(
            """
            [
              {"text": "今天的构建通过了。", "userName": "Ada Lovelace", "userAbbr": "AL", "time": "09:12", "userColorIndex": 0},
              {"text": "我把 `Grid` 的示例补完了。", "userName": "Grace Hopper", "userAbbr": "GH", "time": "09:15", "userColorIndex": 1},
              {"text": "谢谢！**合并**吧。", "userName": "You", "userAbbr": "ME", "time": "09:16", "userColorIndex": 2}
            ]
            """.trimIndent(),
        )
    messages.markdown = true
    basic.body.append(example(null, messages))
    view.body.append(basic.root)

    val append = section("追加消息", "items 是普通数组，替换后列表局部更新；这里演示把 Message Input 的内容追加进去。")
    val liveList = createMessageList()
    liveList.items =
        jsArray<JsAny?>("""[{"text": "说点什么吧。", "userName": "System", "userAbbr": "SY", "time": "09:00"}]""")
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

    return view.root
}

private fun buildMessageInputPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("基本用法", "回车或点击发送触发 submit；value 不会自动清空，由调用方决定。")
    val input = createMessageInput()
    input.setAttribute("data-test", "message-input")
    val readout = Readout("尚未发送")
    val log = EventLog(4)
    input.listen(MessageInputEvents.submit) {
        readout.set("已发送：", input.value)
        log.add("submit · ${input.value}")
    }
    basic.body.append(example(null, column(input, readout.root, log.root)))
    view.body.append(basic.root)

    val disabled = section("禁用", "")
    val disabledInput = createMessageInput()
    disabledInput.disabled = true
    disabled.body.append(example(null, disabledInput))
    view.body.append(disabled.root)

    return view.root
}

private fun buildMarkdownPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("基本用法", "content 是 Markdown 源码，组件把它渲染成 HTML。")
    val markdown = createMarkdown()
    markdown.setAttribute("data-test", "markdown")
    markdown.content =
        """
        # 标题

        Markdown 组件支持 **粗体**、*斜体*、`行内代码` 与列表：

        - 第一项
        - 第二项

        > 引用也可以。
        """.trimIndent()
    basic.body.append(example(null, markdown))
    view.body.append(basic.root)

    val lineBreakSection = section("lineBreaks", "打开后单个换行也会变成 <br>，否则按标准 Markdown 合并成一段。")
    val withBreaks = createMarkdown { lineBreaks = true }
    withBreaks.content = "第一行\n第二行\n第三行"
    val withoutBreaks = createMarkdown()
    withoutBreaks.content = "第一行\n第二行\n第三行"
    lineBreakSection.body.append(example(null, row(withBreaks, withoutBreaks)))
    view.body.append(lineBreakSection.root)

    return view.root
}

private fun buildUploadPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic =
        section(
            "文件选择",
            "noAuto 打开后文件只进入列表、不会真的上传，因此没有后端也能完整演示选择、移除与数量限制。",
        )
    val upload = createUpload()
    upload.setAttribute("data-test", "upload")
    upload.noAuto = true
    upload.maxFiles = 3.0
    upload.accept = "image/*,application/pdf"
    val readout = Readout("已选择 0 / 3 个文件")
    val log = EventLog(4)
    upload.listen(UploadEvents.filesChanged) {
        readout.set("已选择 ${upload.files.size} / 3 个文件：", upload.files.joinToString(", ") { it.name })
        log.add("files-changed · ${upload.files.size}")
    }
    upload.listen(UploadEvents.fileReject) { log.add("file-reject（超出数量或类型不符）") }
    basic.body.append(example(null, column(upload, readout.root, log.root)))
    view.body.append(basic.root)

    val disabled = section("禁用", "")
    val disabledUpload = createUpload { noAuto = true }
    disabledUpload.disabled = true
    disabled.body.append(example(null, disabledUpload))
    view.body.append(disabled.root)

    return view.root
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
