package demo.pages.data

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.tr
import demo.ui.Images
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.Readout
import demo.ui.column
import demo.ui.div
import demo.ui.example
import demo.ui.listen
import demo.ui.page
import demo.ui.section
import demo.ui.withTheme
import vaadin.messageinput.MessageInputEvents
import vaadin.messageinput.createMessageInput
import vaadin.messagelist.MessageListEvents
import vaadin.messagelist.createMessageList

val messageListEntry: CatalogEntry =
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
                sourcePath = "pages/data/MessageListPage.kt",
            ),
        builder = ::buildMessageListPage,
    )

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
                userImg = Images.portrait(8),
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
                attachments = arrayOf(attachment("landscape.jpg", Images.reindeer, "image/jpeg")),
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
