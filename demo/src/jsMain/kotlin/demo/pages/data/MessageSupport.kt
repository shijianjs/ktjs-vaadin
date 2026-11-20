package demo.pages.data

import demo.ui.Images
import js.objects.unsafeJso
import kotlin.js.js
import io.github.shijianjs.ktjs.vaadin.messagelist.MessageAttachment
import io.github.shijianjs.ktjs.vaadin.messagelist.MessageListItem
import web.events.Event

// ---------------------------------------------------------------------------------------------
// Virtual List
//
// docs: virtuallist/virtual-list-basic.ts (a card per person with an expandable contact block)
//       plus a scrollToIndex example that the docs describe in prose only.
// ---------------------------------------------------------------------------------------------

/** One person of the virtual list; [photoIndex] selects the official demo portrait. */
internal class PersonCard(
    val firstName: String,
    val lastName: String,
    val profession: String,
    val email: String,
    val phone: String,
    val photoIndex: Int,
) {
    val fullName: String
        get() = "$firstName $lastName"

    val photo: String
        get() = Images.portrait(photoIndex)
}

// ---------------------------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------------------------

/** Builds a message item; [userColorIndex] selects the avatar color of the initials. */
internal fun message(
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
internal fun attachment(name: String, url: String, type: String): MessageAttachment =
    unsafeJso {
        this.name = name
        this.url = url
        this.type = type
    }

/** Reads `event.detail.attachment.name` of an `attachment-click` event. */
internal fun attachmentName(event: Event): String =
    js("(event) => (event.detail && event.detail.attachment && event.detail.attachment.name) || ''")(event) as String

/** Appends [item] to a message list `items` array and returns the new array. */
internal fun appendMessage(current: Array<MessageListItem>?, item: MessageListItem): Array<MessageListItem> {
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
internal fun streamReply(
    reply: MessageListItem,
    text: String,
    joiner: String,
    onChunk: (Boolean) -> Unit,
) {
    val words = if (joiner.isEmpty()) text.map { "$it" } else text.split(joiner)
    var sent = 0
    var handle = 0
    handle =
        js("(fn, ms) => window.setInterval(fn, ms)")(
            {
                sent += 1
                reply.text = words.take(sent).joinToString(joiner)
                val done = sent >= words.size
                onChunk(done)
                if (done) js("(handle) => window.clearInterval(handle)")(handle)
            },
            120,
        ) as Int
}
