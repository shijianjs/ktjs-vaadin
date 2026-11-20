package demo.pages.data

import io.github.shijianjs.ktjs.demo.page.col as kitCol
import io.github.shijianjs.ktjs.demo.page.exampleBox
import io.github.shijianjs.ktjs.demo.page.sectionBlock


import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import web.components.CustomElementReference
import web.dom.TagName
import web.html.HTMLElement
import web.html.HtmlTagName
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.ui.Images
import demo.ui.PageFrame
import demo.ui.PageMeta
import demo.ui.Readout
import demo.ui.listen
import io.github.shijianjs.ktjs.dom.css.cssSheet
import web.cssom.CSSStyleSheet
import web.cssom.FontWeight
import io.github.shijianjs.ktjs.vaadin.messageinput.MessageInputEvents
import io.github.shijianjs.ktjs.vaadin.messageinput.createMessageInput
import io.github.shijianjs.ktjs.vaadin.messagelist.MessageListElement
import io.github.shijianjs.ktjs.vaadin.messagelist.MessageListEvents
import io.github.shijianjs.ktjs.vaadin.messagelist.MessageListItem
import io.github.shijianjs.ktjs.vaadin.messagelist.createMessageList
import io.github.shijianjs.ktjs.dom.css.dsl.className
import io.github.shijianjs.ktjs.dom.css.dsl.compound
import io.github.shijianjs.ktjs.dom.css.dsl.invoke
import io.github.shijianjs.ktjs.dom.css.dsl.pseudo
import io.github.shijianjs.ktjs.dom.css.dsl.pseudoElement
import io.github.shijianjs.ktjs.dom.css.dsl.tag
import io.github.shijianjs.ktjs.vaadin.VaadinTagName
import web.cssom.ClassName
import web.cssom.ident

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
        kitPage = { newMessageListPage() },
    )

// ---------------------------------------------------------------------------------------------
// Message List
//
// docs: messages/message-list-component.ts, message-list-markdown.ts, message-list-attachments.ts,
//       styling.adoc (message-list-bubble.ts, message-list-one-to-one.ts,
//       message-list-class-name.ts) and message-list-ai-chat.ts.
// The typing indicator is Flow-only and is described instead of rendered.
// ---------------------------------------------------------------------------------------------

/** The class that marks the message sent by the reader. */
private val CURRENT_USER: ClassName = ClassName("current-user")

/**
 * Marks the message the page sends as its own: the docs example puts the author's name in bold for
 * the signed-in user. `className` lands on the light-DOM message element, and the name is a part
 * inside its shadow tree, so it joins the host compound as `::part(name)`.
 */
private val MESSAGE_SHEET: CSSStyleSheet = cssSheet {
    compound(
        tag(VaadinTagName.message),
        className(CURRENT_USER),
        pseudo(pseudoElement("part"), ident("name")),
    ).invoke { fontWeight = FontWeight.bold }
}

/** Every string this page shows, in one language. */
private class MessageTexts(
    val basicHeading: String,
    val basicHint: String,
    val quote: String,
    val yesterday: String,
    val quoteTwo: String,
    val justNow: String,
    val markdownHeading: String,
    val markdownHint: String,
    val markdownHello: String,
    val markdownReport: String,
    val attachmentsHeading: String,
    val attachmentsHint: String,
    val attachmentIntro: String,
    val attachmentThanks: String,
    val fiftyMinutesAgo: String,
    val clickHint: String,
    val clickedPrefix: String,
    val bubbleHeading: String,
    val bubbleHint: String,
    val bubbleAsk: String,
    val bubbleReply: String,
    val bubbleThanks: String,
    val fortyMinutesAgo: String,
    val oneToOneHeading: String,
    val oneToOneHint: String,
    val whichOrders: String,
    val fiveMinutesAgo: String,
    val orderReport: String,
    val fourMinutesAgo: String,
    val assistant: String,
    val classNameHeading: String,
    val classNameHint: String,
    val aiHeading: String,
    val aiHint: String,
    val greeting: String,
    val me: String,
    val processing: String,
    val newMessage: String,
    val plan: String,
    val joiner: String,
    val typingHeading: String,
    val typingHint: String,
)

private val english: MessageTexts =
    MessageTexts(
        basicHeading = "Basic usage",
        basicHint = "Each item provides text / userName / time / userImg / userColorIndex; the avatar " +
            "falls back to the user's initials.",
        quote = "Nature does not hurry, yet everything gets accomplished.",
        yesterday = "yesterday",
        quoteTwo = "Using your talent, hobby or profession in a way that makes you contribute with " +
            "something good to this world is truly the way to go.",
        justNow = "right now",
        markdownHeading = "Markdown",
        markdownHint = "With markdown on, the message text is rendered as Markdown; the content is " +
            "sanitized automatically and HTML tags are allowed too.",
        markdownHello = "**Hello team!** Did everyone review the *design document* for the new project?",
        markdownReport = """
            ## Project Update
            I've completed the initial research phase and documented my findings.

            * UI mockups ✅
            * Market analysis ✅
            * [See detailed report](https://vaadin.com)

            Let me know your thoughts!
        """.trimIndent(),
        attachmentsHeading = "Attachments",
        attachmentsHint = "An attachment has name / url / type; image/* is shown as a thumbnail and " +
            "everything else as a file icon. Clicking one fires attachment-click with the whole attachment.",
        attachmentIntro = "Here are the documents for the project.",
        attachmentThanks = "Thanks! Here's a photo from the offsite.",
        fiftyMinutesAgo = "50 minutes ago",
        clickHint = "Click an attachment to see its name here.",
        clickedPrefix = "Clicked: ",
        bubbleHeading = "Chat bubbles",
        bubbleHint = "The bubble theme on the list renders messages as bubbles; the self theme on an " +
            "item marks it as the current user's, aligned to the end without avatar and name.",
        bubbleAsk = "Linsey, could you check if the details with the order are okay?",
        bubbleReply = "All good. Ship it.",
        bubbleThanks = "Great, the customer will be glad to hear that.",
        fortyMinutesAgo = "40 minutes ago",
        oneToOneHeading = "One-to-one conversations",
        oneToOneHint = "bubble + one-to-one hides every avatar and name; long assistant replies use " +
            "full-width to drop the bubble and width limit, together with markdown for rich text.",
        whichOrders = "Which orders are still waiting for shipment?",
        fiveMinutesAgo = "5 minutes ago",
        orderReport = """
            These orders are waiting for shipment:

            - **#1042**, paid yesterday
            - **#1043**, paid today
            - **#1045**, waiting for stock

            The first two can ship today. Order #1045 ships when the missing items arrive.
        """.trimIndent(),
        fourMinutesAgo = "4 minutes ago",
        assistant = "Assistant",
        classNameHeading = "Styling individual messages",
        classNameHint = "Give an item a className and style vaadin-message with plain CSS; messages live " +
            "in the light DOM of the list, so page styles apply directly.",
        aiHeading = "Usage for AI chats",
        aiHint = "The docs combine Message List and Message Input into an AI chat interface and append " +
            "the assistant reply token by token. There is no backend here, so a local timer streams the " +
            "words and the items array is replaced the same way; announcements go to an aria-live region.",
        greeting = "Hi! Shall I help you organize your to-do list?",
        me = "User",
        processing = "AI is processing the prompt",
        newMessage = "New message available",
        plan = "Here is a plan: first sort today's **to-dos** by priority, then block out an " +
            "uninterrupted stretch of time.",
        joiner = " ",
        typingHeading = "Typing indicator",
        typingHint = "The typing indicator (setTypingUsers / setTypingIndicatorType) is currently " +
            "Flow-only and the web component has no matching API, so it is not shown here. With Message " +
            "List alone, appending a \"typing…\" message at the end achieves a similar effect.",
    )

private val chinese: MessageTexts =
    MessageTexts(
        basicHeading = "基本用法",
        basicHint = "items 里每条消息提供 text / userName / time / userImg / userColorIndex；" +
            "头像缺省时用姓名缩写。",
        quote = "大自然从不匆忙，却万事皆成。",
        yesterday = "昨天",
        quoteTwo = "用你的天赋、爱好或职业做点对世界有益的事，就是最值得走的那条路。",
        justNow = "刚刚",
        markdownHeading = "Markdown",
        markdownHint = "markdown 打开后消息文本按 Markdown 渲染；组件会自动净化内容，HTML 标签也允许写。",
        markdownHello = "**大家好！** 有人看过新项目的*设计文档*了吗？",
        markdownReport = """
            ## 项目进展
            我已经完成初步调研，并把结论整理成文档。

            * UI 稿 ✅
            * 市场分析 ✅
            * [查看详细报告](https://vaadin.com)

            欢迎提出想法！
        """.trimIndent(),
        attachmentsHeading = "附件",
        attachmentsHint = "附件有 name / url / type；image/* 显示缩略图，其余显示文件图标。点击附件触发 " +
            "attachment-click，事件里带整个附件对象。",
        attachmentIntro = "这是项目的相关文档。",
        attachmentThanks = "谢谢！这是团建时的照片。",
        fiftyMinutesAgo = "50 分钟前",
        clickHint = "点击附件查看文件名。",
        clickedPrefix = "已点击：",
        bubbleHeading = "聊天气泡",
        bubbleHint = "列表主题 bubble 把消息渲染成气泡；单条消息的 self 主题表示当前用户，" +
            "靠右对齐并去掉头像和姓名。",
        bubbleAsk = "Linsey，你能确认一下订单细节吗？",
        bubbleReply = "都没问题，可以发货。",
        bubbleThanks = "太好了，客户听到会很高兴。",
        fortyMinutesAgo = "40 分钟前",
        oneToOneHeading = "一对一对话",
        oneToOneHint = "bubble + one-to-one 隐藏所有头像和姓名；助手这类长回复用 full-width 去掉气泡和" +
            "宽度限制，配合 markdown 展示富文本。",
        whichOrders = "哪些订单还在等待发货？",
        fiveMinutesAgo = "5 分钟前",
        orderReport = """
            这些订单还在等待发货：

            - **#1042**，昨天已付款
            - **#1043**，今天已付款
            - **#1045**，等待补货

            前两单今天可以发出；#1045 要等缺货到货后再发。
        """.trimIndent(),
        fourMinutesAgo = "4 分钟前",
        assistant = "助手",
        classNameHeading = "单条消息样式",
        classNameHint = "给单条消息加 className，再用普通 CSS 选中 vaadin-message 即可改样式；" +
            "消息挂在列表的 light DOM 里，页面样式能直接命中。",
        aiHeading = "AI 对话",
        aiHint = "文档示例把 Message List 与 Message Input 组合成 AI 聊天界面，并按 token 追加助手回复。" +
            "这里没有后端，用本地定时器逐词「流式」输出，同样通过替换 items 触发重绘；" +
            "屏幕阅读器公告放在 aria-live 区域。",
        greeting = "你好！需要我帮你整理待办事项吗？",
        me = "我",
        processing = "AI 正在处理",
        newMessage = "有新消息",
        plan = "我来帮你安排：先把今天的 **待办** 按优先级排好，再留出一段不被打扰的时间。",
        joiner = "",
        typingHeading = "打字指示器",
        typingHint = "打字指示器（setTypingUsers / setTypingIndicatorType）目前只在 Flow 中提供，" +
            "Web Component 没有对应 API，因此这里不演示。用 Message List 实现时，可以在列表末尾追加一条" +
            "「正在输入…」消息达到类似效果。",
    )

private val messageTexts: Translations<MessageTexts> = texts(
    Langs.EN to english,
    Langs.ZH_HANS to chinese,
)

internal class MessageListPage : DemoPage() {
    private val texts = Binder(messageTexts)

    private val frame = PageFrame(messageListEntry)

    /** Each list plus how to fill its `items` in any language. */
    private val lists: MutableList<Pair<MessageListElement, (MessageTexts) -> Array<MessageListItem>>> =
        mutableListOf()

    private val attachmentReadout = Readout("")

    private var lastAttachment: String? = null

    private val chat = createMessageList().apply { markdown = true }

    private val chatInput = createMessageInput()

    private val announcement = HtmlTagName.div()

    /** A line of the AI chat; [copy] is set for lines the page writes, [literal] for user input. */
    private class ChatLine(val role: Int, val copy: ((MessageTexts) -> String)? = null, val literal: String? = null)

    private val chatLines: MutableList<ChatLine> = mutableListOf()

    init {
        frame.mount(this)
        addSheet(MESSAGE_SHEET)

        fun list(
            configure: MessageListElement.() -> Unit = {},
            fill: (MessageTexts) -> Array<MessageListItem>,
        ): MessageListElement = createMessageList(configure).also { lists += it to fill }

        val basic = sectionBlock()
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        val messages = list { copy ->
            arrayOf(
                message(
                    text = copy.quote,
                    time = copy.yesterday,
                    userName = "Matt Mambo",
                    userColorIndex = 1,
                ),
                message(
                    text = copy.quoteTwo,
                    time = copy.justNow,
                    userName = "Linsey Listy",
                    userImg = Images.portrait(8),
                    userColorIndex = 2,
                ),
            )
        }
        messages.dataset["test"] = "message-list"
        basic.body.append(exampleBox(null, messages))
        body.append(basic.root)

        val markdownSection = sectionBlock()
        texts.bind(markdownSection.heading) { it.markdownHeading }
        texts.bind(markdownSection.hint) { it.markdownHint }
        val markdownList =
            list(configure = { markdown = true }) { copy ->
                arrayOf(
                    message(text = copy.markdownHello, userName = "Alex Johnson"),
                    message(text = copy.markdownReport, userName = "Sam Rivera"),
                )
            }
        markdownSection.body.append(exampleBox(null, markdownList))
        body.append(markdownSection.root)

        val attachmentsSection = sectionBlock()
        texts.bind(attachmentsSection.heading) { it.attachmentsHeading }
        texts.bind(attachmentsSection.hint) { it.attachmentsHint }
        val attachmentList =
            list { copy ->
                arrayOf(
                    message(
                        text = copy.attachmentIntro,
                        time = copy.yesterday,
                        userName = "Matt Mambo",
                        userColorIndex = 1,
                        attachments =
                            arrayOf(
                                attachment(
                                    "project-proposal.pdf",
                                    "https://example.com/files/proposal.pdf",
                                    "application/pdf",
                                ),
                                attachment(
                                    "budget-overview.xlsx",
                                    "https://example.com/files/budget.xlsx",
                                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                                ),
                            ),
                    ),
                    message(
                        text = copy.attachmentThanks,
                        time = copy.fiftyMinutesAgo,
                        userName = "Linsey Listy",
                        userColorIndex = 2,
                        attachments = arrayOf(attachment("landscape.jpg", Images.reindeer, "image/jpeg")),
                    ),
                )
            }
        attachmentList.listen(MessageListEvents.attachmentClick) {
            lastAttachment = attachmentName(it)
            showAttachment()
        }
        attachmentsSection.body.append(exampleBox(null, kitCol(attachmentList, attachmentReadout.root)))
        body.append(attachmentsSection.root)

        val bubbleSection = sectionBlock()
        texts.bind(bubbleSection.heading) { it.bubbleHeading }
        texts.bind(bubbleSection.hint) { it.bubbleHint }
        val bubbles =
            list(configure = { addTheme("bubble") }) { copy ->
                arrayOf(
                    message(
                        text = copy.bubbleAsk,
                        time = copy.yesterday,
                        userName = "Matt Mambo",
                        userColorIndex = 1,
                    ),
                    message(
                        text = copy.bubbleReply,
                        time = copy.fiftyMinutesAgo,
                        userName = "Linsey Listy",
                        userColorIndex = 2,
                        theme = "self",
                    ),
                    message(
                        text = copy.bubbleThanks,
                        time = copy.fortyMinutesAgo,
                        userName = "Sam Swanson",
                        userColorIndex = 3,
                    ),
                )
            }
        bubbleSection.body.append(exampleBox(null, bubbles))
        body.append(bubbleSection.root)

        val oneToOneSection = sectionBlock()
        texts.bind(oneToOneSection.heading) { it.oneToOneHeading }
        texts.bind(oneToOneSection.hint) { it.oneToOneHint }
        val oneToOne =
            list(configure = {
                addTheme("bubble", "one-to-one")
                markdown = true
            }) { copy ->
                arrayOf(
                    message(
                        text = copy.whichOrders,
                        time = copy.fiveMinutesAgo,
                        userName = "Linsey Listy",
                        theme = "self",
                    ),
                    message(
                        text = copy.orderReport,
                        time = copy.fourMinutesAgo,
                        userName = copy.assistant,
                        theme = "full-width",
                    ),
                )
            }
        oneToOneSection.body.append(exampleBox(null, oneToOne))
        body.append(oneToOneSection.root)

        val classNameSection = sectionBlock()
        texts.bind(classNameSection.heading) { it.classNameHeading }
        texts.bind(classNameSection.hint) { it.classNameHint }
        val classNameList =
            list { copy ->
                arrayOf(
                    message(
                        text = copy.bubbleAsk,
                        time = copy.yesterday,
                        userName = "Matt Mambo",
                        userColorIndex = 1,
                    ),
                    message(
                        text = copy.bubbleReply,
                        time = copy.fiftyMinutesAgo,
                        userName = "Linsey Listy",
                        userColorIndex = 2,
                        className = "$CURRENT_USER",
                    ),
                )
            }
        classNameSection.body.append(exampleBox(null, classNameList))
        body.append(classNameSection.root)

        val aiSection = sectionBlock()
        texts.bind(aiSection.heading) { it.aiHeading }
        texts.bind(aiSection.hint) { it.aiHint }
        announcement.setAttribute("aria-live", "polite")
        // Announced to assistive technology only: a one-pixel box clipped out of the layout.
        announcement.style.setProperty("position", "absolute")
        announcement.style.setProperty("clip", "rect(0, 0, 0, 0)")
        announcement.style.setProperty("width", "1px")
        announcement.style.setProperty("height", "1px")
        announcement.style.setProperty("overflow", "hidden")
        announcement.style.setProperty("white-space", "nowrap")
        chatLines += ChatLine(role = 0, copy = { it.greeting })
        chatInput.listen(MessageInputEvents.submit) {
            val prompt = chatInput.value
            if (prompt.isNotEmpty()) {
                chatInput.value = ""
                chatLines += ChatLine(role = 1, literal = prompt)
                val reply = ChatLine(role = 2)
                chatLines += reply
                showChat()
                announcement.textContent = texts.current.processing
                streamReply(reply.message(), texts.current.plan, texts.current.joiner) {
                    showChat()
                    if (it) announcement.textContent = texts.current.newMessage
                }
            }
        }
        aiSection.body.append(exampleBox(null, kitCol(announcement, chat, chatInput)))
        body.append(aiSection.root)

        val typingSection = sectionBlock()
        texts.bind(typingSection.heading) { it.typingHeading }
        texts.bind(typingSection.hint) { it.typingHint }
        body.append(typingSection.root)

        applyTranslation(Langs.EN)
    }

    private val chatReplies = mutableMapOf<ChatLine, MessageListItem>()

    private fun ChatLine.message(): MessageListItem {
        val copy = texts.current
        val name = if (role == 1) copy.me else copy.assistant
        return chatReplies.getOrPut(this) {
            message(text = "", userName = name, userColorIndex = if (role == 1) 1 else 2)
        }
    }

    private fun showChat() {
        val copy = texts.current
        chat.items =
            chatLines
                .map { line ->
                    val existing = chatReplies[line]
                    if (existing == null) {
                        message(
                            text = line.copy?.invoke(copy) ?: line.literal.orEmpty(),
                            userName = if (line.role == 1) copy.me else copy.assistant,
                            userColorIndex = if (line.role == 1) 1 else 2,
                        )
                    } else {
                        existing.userName = if (line.role == 1) copy.me else copy.assistant
                        existing
                    }
                }
                .toTypedArray()
    }

    private fun showAttachment() {
        val copy = texts.current
        attachmentReadout.set(lastAttachment?.let { copy.clickedPrefix + it } ?: copy.clickHint)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = messageListEntry.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        lists.forEach { (list, fill) -> list.items = fill(texts.current) }
        showAttachment()
        showChat()
    }

    companion object : CustomElementReference<MessageListPage>(MessageListPage::class.js) {
        val NAME: TagName<MessageListPage> = TagName("vaadin-demo-message-list-page")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<vaadin-demo-message-list-page>`. */
internal fun newMessageListPage(): MessageListPage {
    MessageListPage.define()
    return MessageListPage.NAME()
}
