package demo.pages.data

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
import demo.ui.listen
import demo.ui.page
import demo.ui.section
import js.objects.unsafeJso
import vaadin.messageinput.MessageInputEvents
import vaadin.messageinput.createMessageInput
import vaadin.messagelist.createMessageList
import vaadin.notification.Notification
import vaadin.notification.NotificationShowOptions

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
        builder = ::buildMessageInputPage,
    )

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
