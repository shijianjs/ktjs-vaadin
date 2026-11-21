package demo.pages.data

import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.page.Blocks.col as kitCol
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import io.github.shijianjs.ktjs.dom.element.invoke
import demo.CatalogEntry
import demo.Catalog
import demo.Groups
import demo.bi
import demo.ui.EventLog
import demo.ui.PageMeta
import demo.ui.PageFrame
import demo.ui.Readout
import demo.ui.listen
import js.objects.unsafeJso
import io.github.shijianjs.ktjs.vaadin.messageinput.MessageInputEvents
import io.github.shijianjs.ktjs.vaadin.messageinput.createMessageInput
import io.github.shijianjs.ktjs.vaadin.messagelist.createMessageList
import io.github.shijianjs.ktjs.vaadin.notification.Notification
import io.github.shijianjs.ktjs.vaadin.notification.NotificationShowOptions

// ---------------------------------------------------------------------------------------------
// Message Input
//
// docs: messages/message-input-component.ts (Notification on submit) and message-basic.ts
// (Message List + Message Input).
// ---------------------------------------------------------------------------------------------

internal class MessageInputPage : DemoPage() {
    private val texts = Binder(messageInputTexts)

    private val frame = PageFrame(ENTRY)

    /** The single example's state lines; kept so a language switch can rewrite what is still untouched. */
    private val readout = Readout(english.notSentYet)

    private val log = EventLog(4, english.noEvents)

    private val liveList = createMessageList()

    private val liveLog = EventLog(4, english.noEvents)

    private var sent = false

    init {
        frame.mount(this)

        val basic = SectionBlock.NAME { show() }
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }

        val input = createMessageInput()
        input.dataset["test"] = "message-input"
        input.listen(MessageInputEvents.submit) {
            val text = input.value
            val copy = texts.current
            sent = text.isNotEmpty()
            if (sent) readout.set(copy.sentPrefix, text) else readout.set(copy.notSentYet)
            log.add("submit · $text")
            Notification.show(
                copy.receivedPrefix + text,
                unsafeJso<NotificationShowOptions> { position = "middle" },
            )
            input.value = ""
        }
        basic.body.append(exampleBox(null, kitCol(input, readout.root, log.root)))
        body.append(basic.root)

        val combined = SectionBlock.NAME { show() }
        texts.bind(combined.heading) { it.combinedHeading }
        texts.bind(combined.hint) { it.combinedHint }

        liveList.items = arrayOf(seed(english))
        val liveInput = createMessageInput()
        liveInput.listen(MessageInputEvents.submit) {
            val text = liveInput.value
            if (text.isNotEmpty()) {
                val copy = texts.current
                liveList.items =
                    appendMessage(
                        liveList.items,
                        message(
                            text = text,
                            time = copy.rightNow,
                            userName = copy.milla,
                            userAbbr = "MS",
                            userColorIndex = 3,
                        ),
                    )
                liveLog.add("submit · $text")
                liveInput.value = ""
            }
        }
        combined.body.append(exampleBox(null, kitCol(liveList, liveInput, liveLog.root)))
        body.append(combined.root)

        val disabled = SectionBlock.NAME { show() }
        texts.bind(disabled.heading) { it.disabledHeading }
        texts.bind(disabled.hint) { it.disabledHint }
        val disabledInput = createMessageInput()
        disabledInput.disabled = true
        disabled.body.append(exampleBox(null, disabledInput))
        body.append(disabled.root)

        applyTranslation(Langs.EN)
    }

    /** The one message the live list starts with; rewritten while nothing has been submitted. */
    private fun seed(copy: MessageInputTexts) =
        message(
            text = copy.saySomething,
            time = "09:00",
            userName = "System",
            userAbbr = "SY",
        )

    override fun applyTranslation(lang: Lang) {
        val meta = ENTRY.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        val copy = texts.current
        if (!sent) readout.set(copy.notSentYet)
        log.placeholder(copy.noEvents)
        liveLog.placeholder(copy.noEvents)
        // A list the reader has not typed into holds this page's own seed line, not their content.
        if ((liveList.items?.size ?: 0) <= 1) liveList.items = arrayOf(seed(copy))
    }

    companion object : MessageInputPageDefaultReference() {
        val ENTRY: CatalogEntry =
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
                        docsUrl = "${Catalog.DOCS_BASE}/message-input",
                        sourcePath = "pages/data/MessageInputPage.kt",
                    ),
                kitPage = { MessageInputPage.NAME() },
            )

        private val english: MessageInputTexts = MessageInputTexts(
            basicHeading = "Basic usage",
            basicHint = "Enter or the send button fires submit; value is not cleared automatically and the caller " +
                "decides. The docs show the received value in a Notification.",
            notSentYet = "Not sent yet",
            sentPrefix = "Sent: ",
            receivedPrefix = "Message received: ",
            combinedHeading = "With a message list",
            combinedHint = "items is a plain array and replacing it updates the list in place; this appends what the " +
                "input submits, which is the basic chat interface of the docs' Message List + Message Input example.",
            saySomething = "Say something.",
            rightNow = "seconds ago",
            milla = "Milla Sting",
            disabledHeading = "Disabled",
            disabledHint = "disabled locks both the text area and the send button.",
            noEvents = "No events yet",
        )
        private val chinese: MessageInputTexts = MessageInputTexts(
            basicHeading = "基本用法",
            basicHint = "回车或点击发送触发 submit；value 不会自动清空，由调用方决定。文档示例把收到的内容用 " +
                "Notification 弹出来。",
            notSentYet = "尚未发送",
            sentPrefix = "已发送：",
            receivedPrefix = "收到消息：",
            combinedHeading = "与消息列表组合",
            combinedHint = "items 是普通数组，替换后列表局部更新；这里把输入框提交的内容追加进列表，" +
                "就是文档里 Message List + Message Input 的基本聊天界面。",
            saySomething = "说点什么吧。",
            rightNow = "刚刚",
            milla = "米拉",
            disabledHeading = "禁用",
            disabledHint = "disabled 同时锁住文本域和发送按钮。",
            noEvents = "尚未触发事件",
        )
        private val messageInputTexts: Translations<MessageInputTexts> = texts(
            Langs.EN to english,
            Langs.ZH_HANS to chinese,
        )
    }
}
