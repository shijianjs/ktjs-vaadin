package demo.pages.data

import io.github.shijianjs.ktjs.demo.page.col as kitCol
import io.github.shijianjs.ktjs.demo.page.exampleBox
import io.github.shijianjs.ktjs.demo.page.sectionBlock


import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import web.components.CustomElementReference
import web.dom.TagName
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.tr
import demo.ui.EventLog
import demo.ui.PageMeta
import demo.ui.configureFrame
import demo.ui.Readout
import demo.ui.listen
import js.objects.unsafeJso
import io.github.shijianjs.ktjs.vaadin.messageinput.MessageInputEvents
import io.github.shijianjs.ktjs.vaadin.messageinput.createMessageInput
import io.github.shijianjs.ktjs.vaadin.messagelist.createMessageList
import io.github.shijianjs.ktjs.vaadin.notification.Notification
import io.github.shijianjs.ktjs.vaadin.notification.NotificationShowOptions

val messageInputEntry: CatalogEntry =
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
                sourcePath = "pages/data/MessageInputPage.kt",
            ),
        kitPage = { newMessageInputPage() },
        rebuildsOnLanguage = true,
    )

// ---------------------------------------------------------------------------------------------
// Message Input
//
// docs: messages/message-input-component.ts (Notification on submit) and message-basic.ts
// (Message List + Message Input).
// ---------------------------------------------------------------------------------------------

internal class MessageInputPage : DemoPage() {

    init {
        configureFrame(messageInputEntry)

        val basic =
            sectionBlock(
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
        basic.body.append(exampleBox(null, kitCol(input, readout.root, log.root)))
        body.append(basic.root)

        val combined =
            sectionBlock(
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
        combined.body.append(exampleBox(null, kitCol(liveList, liveInput, liveLog.root)))
        body.append(combined.root)

        val disabled =
            sectionBlock(
                tr("禁用", "Disabled"),
                tr("disabled 同时锁住文本域和发送按钮。", "disabled locks both the text area and the send button."),
            )
        val disabledInput = createMessageInput()
        disabledInput.disabled = true
        disabled.body.append(exampleBox(null, disabledInput))
        body.append(disabled.root)

    }

    companion object : CustomElementReference<MessageInputPage>(MessageInputPage::class.js) {
        val NAME: TagName<MessageInputPage> = TagName("vaadin-demo-message-input-page")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<vaadin-demo-message-input-page>`. */
internal fun newMessageInputPage(): MessageInputPage {
    MessageInputPage.define()
    return MessageInputPage.NAME()
}

