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
import demo.ui.jsArray
import demo.ui.listen
import demo.ui.page
import demo.ui.row
import demo.ui.section
import demo.ui.span
import demo.ui.strong
import demo.ui.withTheme
import js.objects.unsafeJso
import kotlin.js.JsAny
import kotlin.js.js
import vaadin.avatar.createAvatar
import vaadin.button.createButton
import vaadin.details.DetailsEvents
import vaadin.details.createDetails
import vaadin.horizontallayout.createHorizontalLayout
import vaadin.markdown.createMarkdown
import vaadin.messageinput.MessageInputEvents
import vaadin.messageinput.createMessageInput
import vaadin.messagelist.MessageAttachment
import vaadin.messagelist.MessageListEvents
import vaadin.messagelist.MessageListItem
import vaadin.messagelist.createMessageList
import vaadin.notification.Notification
import vaadin.notification.NotificationShowOptions
import vaadin.verticallayout.createVerticalLayout
import vaadin.virtuallist.createVirtualList
import web.dom.clickEvent
import web.events.Event
import web.events.addHandler

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
    )

// ---------------------------------------------------------------------------------------------
// Virtual List
//
// docs: virtuallist/virtual-list-basic.ts (a card per person with an expandable contact block)
//       plus a scrollToIndex example that the docs describe in prose only.
// ---------------------------------------------------------------------------------------------

/** One person of the virtual list; [fill] selects the illustrated portrait generated below. */
private class PersonCard(
    val firstName: String,
    val lastName: String,
    val profession: String,
    val email: String,
    val phone: String,
    val fill: String,
) {
    val fullName: String
        get() = "$firstName $lastName"

    val photo: String
        get() = portrait(fill)
}

private fun buildVirtualListPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "renderer 只对可见行调用，滚动时复用容器节点；每一行是一张人员卡片，展开联系方式后" +
                    "滚动离开再回来仍保持展开。",
                "The renderer is called only for visible rows and container nodes are reused while scrolling. " +
                    "Each row is a person card, and an expanded contact block stays open across scrolling.",
            ),
        )
    val base =
        listOf(
            PersonCard(
                "Ada",
                "Lovelace",
                tr("数学家", "Mathematician"),
                "ada.lovelace@example.com",
                "+44 20 7946 0101",
                "7c9cbf",
            ),
            PersonCard(
                "Grace",
                "Hopper",
                tr("计算机科学家", "Computer scientist"),
                "grace.hopper@example.com",
                "+1 202 555 0142",
                "8f7ab8",
            ),
            PersonCard(
                "Alan",
                "Turing",
                tr("密码学家", "Cryptanalyst"),
                "alan.turing@example.com",
                "+44 20 7946 0193",
                "6aa38d",
            ),
            PersonCard(
                "Katherine",
                "Johnson",
                tr("航空航天工程师", "Aerospace engineer"),
                "katherine.johnson@example.com",
                "+1 202 555 0177",
                "b08a6d",
            ),
            PersonCard(
                "Edsger",
                "Dijkstra",
                tr("计算机科学家", "Computer scientist"),
                "edsger.dijkstra@example.com",
                "+31 20 555 0124",
                "9d7f92",
            ),
        )
    // The docs load a hundred people; repeating the fixed set is enough to show row reuse.
    val people = List(200) { index -> base[index % base.size] }
    val expanded = mutableSetOf<Int>()

    val list = createVirtualList()
    list.dataset["test"] = "virtual-list"
    list.items = jsArray<JsAny>((0 until people.size).joinToString(",", "[", "]"))
    list.renderer = { container, _, model ->
        val index = model.index.toInt()
        val person = people[index]

        val details =
            createDetails {
                summary = tr("联系方式", "Contact information")
                opened = expanded.contains(index)
            }
        details.listen(DetailsEvents.openedChanged) {
            if (details.opened) expanded.add(index) else expanded.remove(index)
        }
        details.append(
            createVerticalLayout().apply {
                append(span(person.email), span(person.phone))
                withTheme("spacing")
            },
        )

        val text =
            createVerticalLayout().apply {
                append(strong(person.fullName), span(person.profession), details)
                withTheme("spacing")
            }
        val card =
            createHorizontalLayout().apply {
                append(
                    createAvatar {
                        name = person.fullName
                        img = person.photo
                    }.withTheme("xlarge"),
                    text,
                )
                withTheme("spacing", "margin")
            }
        container.replaceChildren(card)
    }
    list.style.setProperty("height", "20rem")
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
        val row = span(tr("行 ${model.index + 1}", "Row ${model.index + 1}"))
        container.replaceChildren(row)
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

// ---------------------------------------------------------------------------------------------
// Message List
//
// docs: messages/message-list-component.ts, message-list-markdown.ts, message-list-attachments.ts,
//       styling.adoc (message-list-bubble.ts, message-list-one-to-one.ts,
//       message-list-class-name.ts) and message-list-ai-chat.ts.
// The typing indicator is Flow-only and is described instead of rendered.
// ---------------------------------------------------------------------------------------------

private fun buildMessageListPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "items 里每条消息提供 text / userName / time / userImg / userColorIndex；" +
                    "头像缺省时用姓名缩写。",
                "Each item provides text / userName / time / userImg / userColorIndex; the avatar falls " +
                    "back to the user's initials.",
            ),
        )
    val messages = createMessageList()
    messages.dataset["test"] = "message-list"
    messages.items =
        arrayOf(
            message(
                text = tr("大自然从不匆忙，却万事皆成。", "Nature does not hurry, yet everything gets accomplished."),
                time = tr("昨天", "yesterday"),
                userName = "Matt Mambo",
                userColorIndex = 1,
            ),
            message(
                text =
                    tr(
                        "用你的天赋、爱好或职业做点对世界有益的事，就是最值得走的那条路。",
                        "Using your talent, hobby or profession in a way that makes you contribute with " +
                            "something good to this world is truly the way to go.",
                    ),
                time = tr("刚刚", "right now"),
                userName = "Linsey Listy",
                userImg = portrait("c98f7c"),
                userColorIndex = 2,
            ),
        )
    basic.body.append(example(null, messages))
    view.body.append(basic.root)

    val markdownSection =
        section(
            tr("Markdown", "Markdown"),
            tr(
                "markdown 打开后消息文本按 Markdown 渲染；组件会自动净化内容，HTML 标签也允许写。",
                "With markdown on, the message text is rendered as Markdown; the content is sanitized " +
                    "automatically and HTML tags are allowed too.",
            ),
        )
    val markdownList = createMessageList()
    markdownList.markdown = true
    markdownList.items =
        arrayOf(
            message(
                text =
                    tr(
                        "**大家好！** 有人看过新项目的*设计文档*了吗？",
                        "**Hello team!** Did everyone review the *design document* for the new project?",
                    ),
                userName = "Alex Johnson",
            ),
            message(
                text =
                    tr(
                        """
                        ## 项目进展
                        我已经完成初步调研，并把结论整理成文档。

                        * UI 稿 ✅
                        * 市场分析 ✅
                        * [查看详细报告](https://vaadin.com)

                        欢迎提出想法！
                        """.trimIndent(),
                        """
                        ## Project Update
                        I've completed the initial research phase and documented my findings.

                        * UI mockups ✅
                        * Market analysis ✅
                        * [See detailed report](https://vaadin.com)

                        Let me know your thoughts!
                        """.trimIndent(),
                    ),
                userName = "Sam Rivera",
            ),
        )
    markdownSection.body.append(example(null, markdownList))
    view.body.append(markdownSection.root)

    val attachmentsSection =
        section(
            tr("附件", "Attachments"),
            tr(
                "附件有 name / url / type；image/* 显示缩略图，其余显示文件图标。点击附件触发 " +
                    "attachment-click，事件里带整个附件对象。",
                "An attachment has name / url / type; image/* is shown as a thumbnail and everything else " +
                    "as a file icon. Clicking one fires attachment-click with the whole attachment.",
            ),
        )
    val attachmentList = createMessageList()
    attachmentList.items =
        arrayOf(
            message(
                text = tr("这是项目的相关文档。", "Here are the documents for the project."),
                time = tr("昨天", "yesterday"),
                userName = "Matt Mambo",
                userColorIndex = 1,
                attachments =
                    arrayOf(
                        attachment("project-proposal.pdf", "https://example.com/files/proposal.pdf", "application/pdf"),
                        attachment(
                            "budget-overview.xlsx",
                            "https://example.com/files/budget.xlsx",
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                        ),
                    ),
            ),
            message(
                text = tr("谢谢！这是团建时的照片。", "Thanks! Here's a photo from the offsite."),
                time = tr("50 分钟前", "50 minutes ago"),
                userName = "Linsey Listy",
                userColorIndex = 2,
                attachments = arrayOf(attachment("landscape.jpg", landscapeDataUri(), "image/jpeg")),
            ),
        )
    val attachmentReadout = Readout(tr("点击附件查看文件名。", "Click an attachment to see its name here."))
    attachmentList.listen(MessageListEvents.attachmentClick) {
        attachmentReadout.set(tr("已点击：", "Clicked: "), attachmentName(it))
    }
    attachmentsSection.body.append(example(null, column(attachmentList, attachmentReadout.root)))
    view.body.append(attachmentsSection.root)

    val bubbleSection =
        section(
            tr("聊天气泡", "Chat bubbles"),
            tr(
                "列表主题 bubble 把消息渲染成气泡；单条消息的 self 主题表示当前用户，靠右对齐并去掉头像和姓名。",
                "The bubble theme on the list renders messages as bubbles; the self theme on an item marks " +
                    "it as the current user's, aligned to the end without avatar and name.",
            ),
        )
    val bubbles = createMessageList().withTheme("bubble")
    bubbles.items =
        arrayOf(
            message(
                text = tr("Linsey，你能确认一下订单细节吗？", "Linsey, could you check if the details with the order are okay?"),
                time = tr("昨天", "yesterday"),
                userName = "Matt Mambo",
                userColorIndex = 1,
            ),
            message(
                text = tr("都没问题，可以发货。", "All good. Ship it."),
                time = tr("50 分钟前", "50 minutes ago"),
                userName = "Linsey Listy",
                userColorIndex = 2,
                theme = "self",
            ),
            message(
                text = tr("太好了，客户听到会很高兴。", "Great, the customer will be glad to hear that."),
                time = tr("40 分钟前", "40 minutes ago"),
                userName = "Sam Swanson",
                userColorIndex = 3,
            ),
        )
    bubbleSection.body.append(example(null, bubbles))
    view.body.append(bubbleSection.root)

    val oneToOneSection =
        section(
            tr("一对一对话", "One-to-one conversations"),
            tr(
                "bubble + one-to-one 隐藏所有头像和姓名；助手这类长回复用 full-width 去掉气泡和宽度限制，" +
                    "配合 markdown 展示富文本。",
                "bubble + one-to-one hides every avatar and name; long assistant replies use full-width to " +
                    "drop the bubble and width limit, together with markdown for rich text.",
            ),
        )
    val oneToOne =
        createMessageList()
            .withTheme("bubble", "one-to-one")
            .apply { markdown = true }
    oneToOne.items =
        arrayOf(
            message(
                text = tr("哪些订单还在等待发货？", "Which orders are still waiting for shipment?"),
                time = tr("5 分钟前", "5 minutes ago"),
                userName = "Linsey Listy",
                theme = "self",
            ),
            message(
                text =
                    tr(
                        """
                        这些订单还在等待发货：

                        - **#1042**，昨天已付款
                        - **#1043**，今天已付款
                        - **#1045**，等待补货

                        前两单今天可以发出；#1045 要等缺货到货后再发。
                        """.trimIndent(),
                        """
                        These orders are waiting for shipment:

                        - **#1042**, paid yesterday
                        - **#1043**, paid today
                        - **#1045**, waiting for stock

                        The first two can ship today. Order #1045 ships when the missing items arrive.
                        """.trimIndent(),
                    ),
                time = tr("4 分钟前", "4 minutes ago"),
                userName = tr("助手", "Assistant"),
                theme = "full-width",
            ),
        )
    oneToOneSection.body.append(example(null, oneToOne))
    view.body.append(oneToOneSection.root)

    val classNameSection =
        section(
            tr("单条消息样式", "Styling individual messages"),
            tr(
                "给单条消息加 className，再用普通 CSS 选中 vaadin-message 即可改样式；消息挂在列表的 light DOM 里，" +
                    "页面样式能直接命中。",
                "Give an item a className and style vaadin-message with plain CSS; messages live in the light " +
                    "DOM of the list, so page styles apply directly.",
            ),
        )
    val classNameList = createMessageList()
    classNameList.items =
        arrayOf(
            message(
                text = tr("Linsey，你能确认一下订单细节吗？", "Linsey, could you check if the details with the order are okay?"),
                time = tr("昨天", "yesterday"),
                userName = "Matt Mambo",
                userColorIndex = 1,
            ),
            message(
                text = tr("都没问题，可以发货。", "All good. Ship it."),
                time = tr("50 分钟前", "50 minutes ago"),
                userName = "Linsey Listy",
                userColorIndex = 2,
                className = "current-user",
            ),
        )
    classNameSection.body.append(example(null, classNameList))
    view.body.append(classNameSection.root)

    val aiSection =
        section(
            tr("AI 对话", "Usage for AI chats"),
            tr(
                "文档示例把 Message List 与 Message Input 组合成 AI 聊天界面，并按 token 追加助手回复。" +
                    "这里没有后端，用本地定时器逐词“流式”输出，同样通过替换 items 触发重绘；" +
                    "屏幕阅读器公告放在 aria-live 区域。",
                "The docs combine Message List and Message Input into an AI chat interface and append the " +
                    "assistant reply token by token. There is no backend here, so a local timer streams the " +
                    "words and the items array is replaced the same way; announcements go to an aria-live region.",
            ),
        )
    val chat = createMessageList()
    chat.markdown = true
    val announcement = div("screen-reader-only").apply { setAttribute("aria-live", "polite") }
    val chatItems =
        mutableListOf(
            message(
                text = tr("你好！需要我帮你整理待办事项吗？", "Hi! Shall I help you organize your to-do list?"),
                userName = tr("助手", "Assistant"),
                userColorIndex = 2,
            ),
        )
    fun renderChat() {
        chat.items = chatItems.toTypedArray()
    }
    renderChat()
    val chatInput = createMessageInput()
    chatInput.listen(MessageInputEvents.submit) {
        val prompt = chatInput.value
        if (prompt.isNotEmpty()) {
            chatInput.value = ""
            chatItems += message(text = prompt, userName = tr("我", "User"), userColorIndex = 1)
            val reply = message(text = "", userName = tr("助手", "Assistant"), userColorIndex = 2)
            chatItems += reply
            renderChat()
            announcement.textContent = tr("AI 正在处理", "AI is processing the prompt")
            streamReply(reply) {
                renderChat()
                if (it) announcement.textContent = tr("有新消息", "New message available")
            }
        }
    }
    aiSection.body.append(example(null, column(announcement, chat, chatInput)))
    view.body.append(aiSection.root)

    val typingSection =
        section(
            tr("打字指示器", "Typing indicator"),
            tr(
                "打字指示器（setTypingUsers / setTypingIndicatorType）目前只在 Flow 中提供，Web Component 没有对应 API，" +
                    "因此这里不演示。用 Message List 实现时，可以在列表末尾追加一条“正在输入…”消息达到类似效果。",
                "The typing indicator (setTypingUsers / setTypingIndicatorType) is currently Flow-only and the " +
                    "web component has no matching API, so it is not shown here. With Message List alone, " +
                    "appending a \"typing…\" message at the end achieves a similar effect.",
            ),
        )
    view.body.append(typingSection.root)

    return view
}

// ---------------------------------------------------------------------------------------------
// Message Input
//
// docs: messages/message-input-component.ts (Notification on submit) and message-basic.ts
// (Message List + Message Input).
// ---------------------------------------------------------------------------------------------

private fun buildMessageInputPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "回车或点击发送触发 submit；value 不会自动清空，由调用方决定。文档示例把收到的内容用 " +
                    "Notification 弹出来。",
                "Enter or the send button fires submit; value is not cleared automatically and the caller " +
                    "decides. The docs show the received value in a Notification.",
            ),
        )
    val input = createMessageInput()
    input.dataset["test"] = "message-input"
    val readout = Readout(tr("尚未发送", "Not sent yet"))
    val log = EventLog(4)
    input.listen(MessageInputEvents.submit) {
        val text = input.value
        readout.set(tr("已发送：", "Sent: "), text)
        log.add("submit · $text")
        Notification.show(
            tr("收到消息：", "Message received: ") + text,
            unsafeJso<NotificationShowOptions> { position = "middle" },
        )
        input.value = ""
    }
    basic.body.append(example(null, column(input, readout.root, log.root)))
    view.body.append(basic.root)

    val combined =
        section(
            tr("与消息列表组合", "With a message list"),
            tr(
                "items 是普通数组，替换后列表局部更新；这里把输入框提交的内容追加进列表，" +
                    "就是文档里 Message List + Message Input 的基本聊天界面。",
                "items is a plain array and replacing it updates the list in place; this appends what the " +
                    "input submits, which is the basic chat interface of the docs' Message List + " +
                    "Message Input example.",
            ),
        )
    val liveList = createMessageList()
    liveList.items =
        arrayOf(
            message(
                text = tr("说点什么吧。", "Say something."),
                time = tr("09:00", "09:00"),
                userName = "System",
                userAbbr = "SY",
            ),
        )
    val liveInput = createMessageInput()
    val liveLog = EventLog(4)
    liveInput.listen(MessageInputEvents.submit) {
        val text = liveInput.value
        if (text.isNotEmpty()) {
            liveList.items =
                appendMessage(
                    liveList.items,
                    message(
                        text = text,
                        time = tr("刚刚", "seconds ago"),
                        userName = tr("米拉", "Milla Sting"),
                        userAbbr = "MS",
                        userColorIndex = 3,
                    ),
                )
            liveLog.add("submit · $text")
            liveInput.value = ""
        }
    }
    combined.body.append(example(null, column(liveList, liveInput, liveLog.root)))
    view.body.append(combined.root)

    val disabled =
        section(
            tr("禁用", "Disabled"),
            tr("disabled 同时锁住文本域和发送按钮。", "disabled locks both the text area and the send button."),
        )
    val disabledInput = createMessageInput()
    disabledInput.disabled = true
    disabled.body.append(example(null, disabledInput))
    view.body.append(disabled.root)

    return view
}

// ---------------------------------------------------------------------------------------------
// Markdown
//
// docs: markdown/markdown-basic.ts and markdown/markdown-line-breaks.ts.
// ---------------------------------------------------------------------------------------------

private fun buildMarkdownPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "content 是 Markdown 源码，组件把它渲染成 HTML 并做净化：标题、粗体、斜体、行内代码、删除线、" +
                    "有序 / 无序列表、引用与链接都能直接用。",
                "content holds Markdown source, which the component renders as sanitized HTML: headings, " +
                    "bold, italic, inline code, strikethrough, ordered and unordered lists, quotes and links " +
                    "all work out of the box.",
            ),
        )
    val markdown = createMarkdown()
    markdown.dataset["test"] = "markdown"
    markdown.content =
        tr(
            """
            ## 富文本格式

            用简单的 Markdown 语法就能写出 **粗体**、*斜体* 和 `行内代码`。
            需要时还可以 ~~删除线~~。

            ## 列表

            ### 有序列表：
            1. 第一项
            2. 第二项
            3. 带 **粗体** 的第三项

            ### 无序列表：
            - 水果
                - 苹果 🍎
                - 香蕉 🍌
                - 橙子 🍊
            - 蔬菜
                - 胡萝卜
                - 西兰花

            ## 链接与引用

            > Markdown 是一种轻量标记语言，可以为纯文本添加格式。

            [访问 Vaadin 官网](https://vaadin.com) | [了解 Markdown](https://www.markdownguide.org/)
            """.trimIndent(),
            """
            ## Rich Text Formatting

            You can create **bold text**, *italicized text*, and `inline code` with simple Markdown syntax.
            You can also ~~strike through~~ text when needed.

            ## Lists

            ### Ordered List:
            1. First item
            2. Second item
            3. Third item with **bold text**

            ### Unordered List:
            - Fruits
                - Apples 🍎
                - Bananas 🍌
                - Oranges 🍊
            - Vegetables
                - Carrots
                - Broccoli

            ## Links & Quotes

            > Markdown is a lightweight markup language that you can use to add formatting elements to plaintext documents.

            [Visit Vaadin website](https://vaadin.com) | [Learn more about Markdown](https://www.markdownguide.org/)
            """.trimIndent(),
        )
    basic.body.append(example(null, markdown))
    view.body.append(basic.root)

    val lineBreakSection =
        section(
            tr("换行", "Line breaks"),
            tr(
                "默认遵循原始 Markdown 规范：段落里的单个换行会合并成空格，换段需要空行。" +
                    "打开 lineBreaks 后单个换行（软换行）也会渲染成真正的换行，像聊天消息那样；空行仍然换段。",
                "By default the original Markdown spec applies: a single line break inside a paragraph " +
                    "collapses into a space and a blank line starts a new paragraph. With lineBreaks on, " +
                    "single (soft) breaks render as real line breaks like chat messages, while blank lines " +
                    "still start a new paragraph.",
            ),
        )
    val letter =
        tr(
            """
            你好，Maria：
            订单今早已发出。
            预计周四送达。

            此致
            Tom
            """.trimIndent(),
            """
            Hi Maria,
            Your order shipped this morning.
            It should arrive by Thursday.

            Best regards,
            Tom
            """.trimIndent(),
        )
    val withBreaks = createMarkdown { lineBreaks = true }
    withBreaks.content = letter
    val withoutBreaks = createMarkdown()
    withoutBreaks.content = letter
    lineBreakSection.body.append(
        example(tr("左：lineBreaks 打开　右：默认", "Left: lineBreaks on. Right: default."), row(withBreaks, withoutBreaks)),
    )
    view.body.append(lineBreakSection.root)

    return view
}

// ---------------------------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------------------------

/** Builds a message item; [userColorIndex] selects the avatar color of the initials. */
private fun message(
    text: String,
    userName: String,
    time: String? = null,
    userAbbr: String? = null,
    userImg: String? = null,
    userColorIndex: Int? = null,
    theme: String? = null,
    className: String? = null,
    attachments: Array<MessageAttachment>? = null,
): MessageListItem =
    unsafeJso {
        this.text = text
        this.userName = userName
        this.time = time
        this.userAbbr = userAbbr
        this.userImg = userImg
        this.userColorIndex = userColorIndex?.toDouble()
        this.theme = theme
        this.className = className
        this.attachments = attachments
    }

/** Builds one attachment entry of a message. */
private fun attachment(name: String, url: String, type: String): MessageAttachment =
    unsafeJso {
        this.name = name
        this.url = url
        this.type = type
    }

/** Reads `event.detail.attachment.name` of an `attachment-click` event. */
private fun attachmentName(event: Event): String =
    js("(event) => (event.detail && event.detail.attachment && event.detail.attachment.name) || ''")(event) as String

/** Appends [item] to a message list `items` array and returns the new array. */
private fun appendMessage(current: Array<MessageListItem>?, item: MessageListItem): Array<MessageListItem> {
    val existing: List<MessageListItem> = current?.toList() ?: emptyList()
    return (existing + item).toTypedArray()
}

/**
 * Streams [reply] word by word into the assistant message.
 *
 * The docs call a backend streaming service; the demo has none, so the same incremental update is
 * driven by a timer to show how tokens are appended to a single message. [onChunk] receives `true`
 * once the reply is complete.
 */
private fun streamReply(reply: MessageListItem, onChunk: (Boolean) -> Unit) {
    val words =
        tr(
            "我来帮你安排：先把今天的 **待办** 按优先级排好，再留出一段不被打扰的时间。",
            "Here is a plan: first sort today's **to-dos** by priority, then block out an uninterrupted " +
                "stretch of time.",
        ).split(" ")
    var sent = 0
    var handle = 0
    handle =
        js("(fn, ms) => window.setInterval(fn, ms)")(
            {
                sent += 1
                reply.text = words.take(sent).joinToString(" ")
                val done = sent >= words.size
                onChunk(done)
                if (done) js("(handle) => window.clearInterval(handle)")(handle)
            },
            120,
        ) as Int
}

// ---------------------------------------------------------------------------------------------
// Inline artwork
//
// The docs examples load photos from their own resources. The demo ships no binary assets and must
// not request anything from the network, so both pictures below are data URIs built from a snippet
// of SVG at page-build time. Strings are only used as image sources, never as DOM markup.
// ---------------------------------------------------------------------------------------------

/** Turns an SVG snippet into a `data:` URI that `<img src>` and `<vaadin-avatar img>` accept. */
private fun svgDataUri(markup: String): String =
    "data:image/svg+xml," +
        markup
            .replace("\"", "'")
            .replace("%", "%25")
            .replace("<", "%3C")
            .replace(">", "%3E")
            .replace("#", "%23")
            .replace(" ", "%20")

/** A landscape picture for the image attachment of the message list. */
private fun landscapeDataUri(): String =
    svgDataUri(
        "<svg xmlns='http://www.w3.org/2000/svg' width='160' height='100'>" +
            "<rect width='160' height='100' fill='#cfe3f3'/>" +
            "<path d='M0 74l38-30 26 20 30-26 66 48H0z' fill='#6f9b71'/>" +
            "<circle cx='128' cy='24' r='12' fill='#f6d365'/>" +
            "</svg>",
    )
