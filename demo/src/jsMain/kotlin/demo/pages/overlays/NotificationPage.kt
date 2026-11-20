package demo.pages.overlays

import io.github.shijianjs.ktjs.demo.page.Blocks.row as kitRow
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.page.Blocks.col as kitCol
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import web.html.HtmlTagName
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import io.github.shijianjs.ktjs.demo.page.Blocks.anchor as kitAnchor
import io.github.shijianjs.ktjs.demo.page.Blocks.paragraph
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.ui.PageFrame
import demo.ui.PageMeta
import demo.ui.RecordedLog
import demo.ui.listen
import js.objects.unsafeJso
import io.github.shijianjs.ktjs.vaadin.avatar.createAvatar
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.horizontallayout.createHorizontalLayout
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.vaadin.notification.Notification
import io.github.shijianjs.ktjs.vaadin.notification.NotificationShowOptions
import io.github.shijianjs.ktjs.vaadin.notification.createNotification
import web.dom.clickEvent
import web.dom.document
import web.events.addHandler
import web.html.HTMLElement
import web.keyboard.KeyboardEvent

val notificationEntry: CatalogEntry =
    CatalogEntry(
        id = "notification",
        group = Groups.OVERLAYS,
        meta =
            PageMeta(
                title = bi("Notification", "Notification"),
                summary =
                    bi(
                        "轻量提示：位置、时长、主题变体，以及需要手动关闭的常驻通知。",
                        "Short messages: position, duration, theme variants and persistent notices " +
                            "that are closed by hand.",
                    ),
                docsUrl = "$DOCS_BASE/notification",
                sourcePath = "pages/overlays/NotificationPage.kt",
            ),
        kitPage = { NotificationPage.NAME() },
    )

// region Notification

private val english: NotificationTexts =
    NotificationTexts(
        positionsHeading = "Position and duration",
        positionsHint = "Notification.show shows a one-off message; options can set position, duration " +
            "and theme.",
        topEnd = "Top end",
        topEndMessage = "Message at the top end",
        topCenter = "Top center",
        topCenterMessage = "Message at the top center",
        bottomCenterDefault = "Bottom center (default)",
        bottomCenterMessage = "Message at the bottom center",
        allPositionsHeading = "Every position",
        allPositionsHint = "position takes nine values: top / middle / bottom, each with three horizontal " +
            "alignments; stretch spans the whole row.",
        themesHeading = "Theme variants",
        themesHint = "theme propagates to the inner notification-card.",
        saved = "Saved successfully",
        saveFailed = "Save failed",
        contrast = "Contrast message",
        info = "New project plan available",
        warning = "Your session is about to expire",
        persistentHeading = "Persistent notification",
        persistentHint = "With duration set to 0 the notification stays open; put a close button in the " +
            "renderer.",
        persistentText = "This notification stays visible until it is closed.",
        close = "Close",
        showPersistent = "Show persistent notification",
        richHeading = "Rich notifications",
        richHint = "The renderer can hold icons, avatars, links and actions; the card is shared between " +
            "the renderer and the component.",
        submitted = "Application submitted!",
        view = "View",
        reportFailed = "Failed to generate report",
        retry = "Retry",
        mentioned = "Jason Bailey mentioned you in",
        projectQ4 = "Project Q4",
        showRich = "Show rich notification",
        errorShort = "error",
        mentionShort = "mention",
        actionsHeading = "Notifications with actions",
        actionsHint = "Undo, retry and link actions all live in the renderer; a close button is an icon " +
            "button.",
        deletedTasks = "5 tasks deleted",
        undo = "Undo",
        undoLine = "undo",
        retryLine = "retry",
        undoNotification = "Undo notification",
        linkNotification = "Link notification",
        retryNotification = "Retry notification",
        keyboardHeading = "Keyboard shortcut",
        keyboardHint = "Ctrl-Z (⌘-Z on macOS) undoes while the notification is open; the shortcut only works " +
            "while it is visible.",
        undoCtrlZ = "Undo Ctrl-Z",
        showNotification = "Show notification",
        lengthsHeading = "Content length",
        lengthsHint = "Keep a notification to the outcome and one primary action; details belong on the " +
            "page, not in a long paragraph.",
        ariaName = "Aria Bailey",
        ariaText = "Yeah, I know. But could you help me with...",
        newMessageFrom = "New message from Aria Bailey",
        longMessage = "Yeah, I know. But could you help me with this. I'm not sure where the bug is in my " +
            "CSS? The checkmark doesn't get the right color. I'm trying to use the CSS custom properties " +
            "from our design system, but for some reason it's not working.",
        recommendedCaption = "Do: outcome plus one action",
        discouragedCaption = "Don't: a whole paragraph in a notification",
        showRecommended = "Show recommended",
        showDiscouraged = "Show discouraged",
    )

private val chinese: NotificationTexts =
    NotificationTexts(
        positionsHeading = "位置与时长",
        positionsHint = "Notification.show 是一次性提示；options 可以指定 position、duration 与 theme。",
        topEnd = "右上角",
        topEndMessage = "右上角的提示",
        topCenter = "顶部居中",
        topCenterMessage = "顶部居中的提示",
        bottomCenterDefault = "底部居中（默认）",
        bottomCenterMessage = "底部居中的提示",
        allPositionsHeading = "全部位置",
        allPositionsHint = "position 接受九种取值：顶部 / 中间 / 底部，各三种水平对齐，stretch 占满整行。",
        themesHeading = "主题变体",
        themesHint = "theme 会传播到内部的 notification-card。",
        saved = "保存成功",
        saveFailed = "保存失败",
        contrast = "对比色提示",
        info = "新项目计划已就绪",
        warning = "会话即将过期",
        persistentHeading = "常驻通知",
        persistentHint = "duration 设为 0 时不自动关闭，需要在 renderer 里放一个关闭按钮。",
        persistentText = "这条通知会一直显示，直到点击关闭。",
        close = "关闭",
        showPersistent = "显示常驻通知",
        richHeading = "富内容通知",
        richHint = "renderer 里可以放图标、头像、链接与操作按钮；通知卡片在 renderer 与组件之间共享。",
        submitted = "申请已提交！",
        view = "查看",
        reportFailed = "报表生成失败",
        retry = "重试",
        mentioned = "Jason Bailey 在",
        projectQ4 = "项目 Q4",
        showRich = "成功通知",
        errorShort = "错误通知",
        mentionShort = "提及通知",
        actionsHeading = "带操作的通知",
        actionsHint = "撤销、重试与链接操作都放在 renderer 里；关闭按钮用图标按钮表达。",
        deletedTasks = "已删除 5 个任务",
        undo = "撤销",
        undoLine = "撤销",
        retryLine = "重试",
        undoNotification = "撤销通知",
        linkNotification = "链接通知",
        retryNotification = "重试通知",
        keyboardHeading = "键盘快捷键",
        keyboardHint = "通知打开时按 Ctrl-Z（macOS 上是 ⌘-Z）也能撤销；快捷键只在通知可见时生效。",
        undoCtrlZ = "撤销 Ctrl-Z",
        showNotification = "显示通知",
        lengthsHeading = "内容长度",
        lengthsHint = "通知只放结论与一个主要操作；细节留在页面里，避免出现长段落。",
        ariaName = "Aria Bailey",
        ariaText = "好的，不过你能帮我……",
        newMessageFrom = "来自 Aria Bailey 的新消息",
        longMessage = "我知道，不过你能帮我看看吗？我不确定我的 CSS 里 bug 在哪里：勾选标记没有显示成正确的颜色，" +
            "我用了设计系统的 CSS 自定义属性，但好像没有生效。",
        recommendedCaption = "推荐：一行结论 + 一个操作",
        discouragedCaption = "不推荐：整段对话塞进通知",
        showRecommended = "显示推荐示例",
        showDiscouraged = "显示不推荐示例",
    )

private val notificationTexts: Translations<NotificationTexts> = texts(
    Langs.EN to english,
    Langs.ZH_HANS to chinese,
)

internal class NotificationPage : DemoPage() {
    private val texts = Binder(notificationTexts)

    private val frame = PageFrame(notificationEntry)

    private val actionLog = RecordedLog<String>(5)

    private val persistentBar = bar()

    private val successBar = bar()

    private val failedBar = bar()

    private val mentionBar = bar()

    private val undoBar = bar()

    private val linkBar = bar()

    private val retryBar = bar()

    private val keyboardBar = bar()

    private val goodBar = bar()

    private val badBar = bar()

    /** Bars whose content is rebuilt on every language switch. */
    private val bars: MutableList<Pair<HTMLElement, (NotificationTexts) -> Unit>> = mutableListOf()

    private val persistentNote = createNotification()

    private val successNote = createNotification()

    private val failedNote = createNotification()

    private val mentionNote = createNotification()

    private val undoNote = createNotification()

    private val linkNote = createNotification()

    private val retryNote = createNotification()

    private val keyboardNote = createNotification()

    private val goodNote = createNotification()

    private val badNote = createNotification()

    private val undoRow = HtmlTagName.div()

    private val retryRow = HtmlTagName.div()

    private val recommendedCaption = HtmlTagName.p()
    private val discouragedCaption = HtmlTagName.p()

    /** Adds a filled row to [bar] and registers it for rebuilds. */
    private fun row(bar: HTMLElement, fill: (NotificationTexts, HTMLElement) -> Unit) {
        bars += bar to { copy -> bar.replaceChildren(); fill(copy, bar) }
    }

    private fun closeButton(bar: HTMLElement, copy: NotificationTexts, onClick: () -> Unit) {
        bar.append(newCloseIcon(copy.close, onClick).also { it.slot = "end" })
    }

    private fun actionButton(
        bar: HTMLElement,
        label: String,
        slotEnd: Boolean = true,
        onClick: () -> Unit,
    ) {
        val button = createButton { textContent = label }
        if (slotEnd) button.slot = "end"
        button.clickEvent.addHandler { onClick() }
        bar.append(button)
    }

    private fun mutedSpan(text: String): HTMLElement =
        HtmlTagName.span().apply {
            textContent = text
            style.setProperty("font-size", "0.875rem")
            style.setProperty("color", "var(--vaadin-text-color-secondary)")
        }

    init {
        frame.mount(this)

        val positions = SectionBlock.NAME { show() }
        texts.bind(positions.heading) { it.positionsHeading }
        texts.bind(positions.hint) { it.positionsHint }
        val topEndButton = createButton().addTheme("tertiary")
        texts.bind(topEndButton) { it.topEnd }
        topEndButton.clickEvent.addHandler {
            Notification.show(
                texts.current.topEndMessage,
                unsafeJso<NotificationShowOptions> {
                    position = "top-end"
                    duration = 3000.0
                },
            )
        }
        val topCenterButton = createButton().addTheme("tertiary")
        texts.bind(topCenterButton) { it.topCenter }
        topCenterButton.clickEvent.addHandler {
            Notification.show(
                texts.current.topCenterMessage,
                unsafeJso<NotificationShowOptions> {
                    position = "top-center"
                    duration = 3000.0
                },
            )
        }
        val bottomCenterButton = createButton().addTheme("tertiary")
        texts.bind(bottomCenterButton) { it.bottomCenterDefault }
        bottomCenterButton.clickEvent.addHandler {
            Notification.show(texts.current.bottomCenterMessage)
        }
        positions.body.append(exampleBox(null, kitRow(topEndButton, topCenterButton, bottomCenterButton)))
        body.append(positions.root)

        val allPositions = SectionBlock.NAME { show() }
        texts.bind(allPositions.heading) { it.allPositionsHeading }
        texts.bind(allPositions.hint) { it.allPositionsHint }
        val positionNames =
            listOf(
                "top-stretch",
                "top-start",
                "top-center",
                "top-end",
                "middle",
                "bottom-start",
                "bottom-center",
                "bottom-end",
                "bottom-stretch",
            )
        allPositions.body.append(
            exampleBox(
                null,
                kitRow(
                    *positionNames
                        .map { name ->
                            createButton { textContent = name }.addTheme("tertiary small").apply {
                                clickEvent.addHandler {
                                    Notification.show(
                                        name,
                                        unsafeJso<NotificationShowOptions> { position = name },
                                    )
                                }
                            }
                        }
                        .toTypedArray(),
                ),
            ),
        )
        body.append(allPositions.root)

        val themes = SectionBlock.NAME { show() }
        texts.bind(themes.heading) { it.themesHeading }
        texts.bind(themes.hint) { it.themesHint }

        // The buttons are collected into one row below.
        val themeRow = kitRow()
        themes.body.append(themeRow)
        fun addThemeButton(theme: String, message: (NotificationTexts) -> String) {
            val button = createButton { textContent = theme }.addTheme(theme)
            button.clickEvent.addHandler {
                Notification.show(
                    message(texts.current),
                    unsafeJso<NotificationShowOptions> {
                        this.theme = theme
                        position = "bottom-end"
                    },
                )
            }
            themeRow.append(button)
        }
        addThemeButton("success") { it.saved }
        addThemeButton("error") { it.saveFailed }
        addThemeButton("contrast") { it.contrast }
        addThemeButton("info") { it.info }
        addThemeButton("warning") { it.warning }
        body.append(themes.root)

        val persistent = SectionBlock.NAME { show() }
        texts.bind(persistent.heading) { it.persistentHeading }
        texts.bind(persistent.hint) { it.persistentHint }
        persistentNote.duration = 0.0
        persistentNote.position = "middle"
        row(persistentBar) { copy, bar ->
            bar.append(paragraph(copy.persistentText))
            actionButton(bar, copy.close, slotEnd = false) { persistentNote.opened = false }
        }
        persistentNote.renderer = { root, _ -> root.append(persistentBar) }
        val showPersistent = createButton()
        texts.bind(showPersistent) { it.showPersistent }
        showPersistent.clickEvent.addHandler { persistentNote.opened = true }
        val hidePersistent = createButton().addTheme("tertiary")
        texts.bind(hidePersistent) { it.close }
        hidePersistent.clickEvent.addHandler { persistentNote.opened = false }
        persistent.body.append(exampleBox(null, kitRow(showPersistent, hidePersistent, persistentNote)))
        body.append(persistent.root)

        val rich = SectionBlock.NAME { show() }
        texts.bind(rich.heading) { it.richHeading }
        texts.bind(rich.hint) { it.richHint }
        successNote.duration = 0.0
        successNote.position = "middle"
        successNote.addTheme("success")
        row(successBar) { copy, bar ->
            bar.append(createIcon { icon = "vaadin:check-circle" })
            bar.append(HtmlTagName.span { textContent = copy.submitted })
            actionButton(bar, copy.view, onClick = {})
            closeButton(bar, copy) { successNote.opened = false }
        }
        successNote.renderer = { root, _ -> root.append(successBar) }

        failedNote.duration = 0.0
        failedNote.position = "middle"
        failedNote.addTheme("error")
        row(failedBar) { copy, bar ->
            bar.append(createIcon { icon = "vaadin:warning" })
            bar.append(HtmlTagName.span { textContent = copy.reportFailed })
            actionButton(bar, copy.retry, onClick = {})
            closeButton(bar, copy) { failedNote.opened = false }
        }
        failedNote.renderer = { root, _ -> root.append(failedBar) }

        mentionNote.duration = 0.0
        mentionNote.position = "middle"
        row(mentionBar) { copy, bar ->
            bar.append(createAvatar { name = "Jason Bailey" })
            bar.append(HtmlTagName.span { textContent = copy.mentioned }.apply { append(kitAnchor(copy.projectQ4, "#")) })
            closeButton(bar, copy) { mentionNote.opened = false }
        }
        mentionNote.renderer = { root, _ -> root.append(mentionBar) }

        val showRich = createButton().addTheme("success")
        texts.bind(showRich) { it.showRich }
        showRich.clickEvent.addHandler { successNote.opened = true }
        val showFailed = createButton().addTheme("error")
        texts.bind(showFailed) { it.errorShort }
        showFailed.clickEvent.addHandler { failedNote.opened = true }
        val showMention = createButton().addTheme("tertiary")
        texts.bind(showMention) { it.mentionShort }
        showMention.clickEvent.addHandler { mentionNote.opened = true }
        rich.body.append(
            exampleBox(
                null,
                kitCol(kitRow(showRich, showFailed, showMention), successNote, failedNote, mentionNote),
            ),
        )
        body.append(rich.root)

        val actions = SectionBlock.NAME { show() }
        texts.bind(actions.heading) { it.actionsHeading }
        texts.bind(actions.hint) { it.actionsHint }
        undoNote.duration = 10000.0
        undoNote.position = "middle"
        row(undoBar) { copy, bar ->
            bar.append(HtmlTagName.span { textContent = copy.deletedTasks })
            actionButton(bar, copy.undo) {
                actionLog.add("undo") { _ -> texts.current.undoLine }
                undoNote.opened = false
            }
            closeButton(bar, copy) { undoNote.opened = false }
        }
        undoNote.renderer = { root, _ -> root.append(undoBar) }

        linkNote.duration = 0.0
        linkNote.position = "middle"
        row(linkBar) { copy, bar ->
            bar.append(
                HtmlTagName.span { textContent = copy.mentioned }.apply { append(kitAnchor(copy.projectQ4, "#")) },
            )
            closeButton(bar, copy) { linkNote.opened = false }
        }
        linkNote.renderer = { root, _ -> root.append(linkBar) }

        retryNote.duration = 0.0
        retryNote.position = "middle"
        retryNote.addTheme("error")
        row(retryBar) { copy, bar ->
            bar.append(HtmlTagName.span { textContent = copy.reportFailed })
            actionButton(bar, copy.retry) {
                actionLog.add("retry") { _ -> texts.current.retryLine }
                retryNote.opened = false
            }
            closeButton(bar, copy) { retryNote.opened = false }
        }
        retryNote.renderer = { root, _ -> root.append(retryBar) }

        val showUndo = createButton().addTheme("tertiary")
        texts.bind(showUndo) { it.undoNotification }
        showUndo.clickEvent.addHandler { undoNote.opened = true }
        val showLink = createButton().addTheme("tertiary")
        texts.bind(showLink) { it.linkNotification }
        showLink.clickEvent.addHandler { linkNote.opened = true }
        val showRetry = createButton().addTheme("error")
        texts.bind(showRetry) { it.retryNotification }
        showRetry.clickEvent.addHandler { retryNote.opened = true }
        actions.body.append(
            exampleBox(
                null,
                kitCol(
                    kitRow(showUndo, showLink, showRetry),
                    undoNote,
                    linkNote,
                    retryNote,
                    actionLog.root,
                ),
            ),
        )
        body.append(actions.root)

        val keyboard = SectionBlock.NAME { show() }
        texts.bind(keyboard.heading) { it.keyboardHeading }
        texts.bind(keyboard.hint) { it.keyboardHint }
        keyboardNote.duration = 10000.0
        keyboardNote.position = "middle"
        row(keyboardBar) { copy, bar ->
            bar.append(HtmlTagName.span { textContent = copy.deletedTasks })
            actionButton(bar, copy.undoCtrlZ) { keyboardNote.opened = false }
        }
        keyboardNote.renderer = { root, _ -> root.append(keyboardBar) }
        // The listener lives on `document`, so it is registered once per page build and stops reacting
        // as soon as the notification is closed.
        document.listen(KEY_DOWN) { event: KeyboardEvent ->
            if (keyboardNote.opened && (event.metaKey || event.ctrlKey) && event.key == "z") {
                event.preventDefault()
                keyboardNote.opened = false
            }
        }
        val showKeyboard = createButton()
        texts.bind(showKeyboard) { it.showNotification }
        showKeyboard.clickEvent.addHandler { keyboardNote.opened = true }
        keyboard.body.append(exampleBox(null, kitCol(showKeyboard, keyboardNote)))
        body.append(keyboard.root)

        val lengths = SectionBlock.NAME { show() }
        texts.bind(lengths.heading) { it.lengthsHeading }
        texts.bind(lengths.hint) { it.lengthsHint }
        texts.bind(recommendedCaption, { it.recommendedCaption }) { recommendedCaption.textContent = it }
        texts.bind(discouragedCaption, { it.discouragedCaption }) { discouragedCaption.textContent = it }
        goodNote.duration = 0.0
        goodNote.position = "middle"
        row(goodBar) { copy, bar ->
            bar.append(
                kitCol(
                    HtmlTagName.span { textContent = copy.ariaName },
                    mutedSpan(copy.ariaText),
                ),
            )
            actionButton(bar, copy.view, onClick = {})
            closeButton(bar, copy) { goodNote.opened = false }
        }
        goodNote.renderer = { root, _ -> root.append(goodBar) }

        badNote.duration = 0.0
        badNote.position = "middle"
        row(badBar) { copy, bar ->
            bar.append(kitCol(HtmlTagName.span { textContent = copy.newMessageFrom }, mutedSpan(copy.longMessage)))
            closeButton(bar, copy) { badNote.opened = false }
        }
        badNote.renderer = { root, _ -> root.append(badBar) }

        val showGood = createButton().addTheme("tertiary")
        texts.bind(showGood) { it.showRecommended }
        showGood.clickEvent.addHandler { goodNote.opened = true }
        val showBad = createButton().addTheme("tertiary")
        texts.bind(showBad) { it.showDiscouraged }
        showBad.clickEvent.addHandler { badNote.opened = true }
        lengths.body.append(
            exampleBox(null, kitCol(recommendedCaption, showGood, goodNote)),
            exampleBox(null, kitCol(discouragedCaption, showBad, badNote)),
        )
        body.append(lengths.root)

        applyTranslation(Langs.EN)
    }

    /**
     * The horizontal bar the notification examples put into a notification's content slot — spaced,
     * centred, with a minimum width so the text does not jitter.
     *
     * The bar itself stays a `vaadin-horizontal-layout`, because the notification reads the nodes the
     * examples append to it as its own children; it is a page method so the DOM is never assembled by
     * a function standing outside the page component.
     */
    private fun bar(): HTMLElement {
        val bar = createHorizontalLayout { addTheme("spacing") }
        bar.style.setProperty("align-items", "center")
        bar.style.setProperty("min-width", "300px")
        return bar
    }

    override fun applyTranslation(lang: Lang) {
        val meta = notificationEntry.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        bars.forEach { (bar, fill) -> fill(texts.current) }
        actionLog.replay("undo")
    }

    companion object : CustomElementReferenceAdapter<NotificationPage>(NotificationPage::class.js, "vaadin-demo-notification-page")
}

/**
 * `<vaadin-demo-close-icon>`: the cross button the notification examples put into their `end` slot.
 *
 * The host is what the notification slots, so `slot="end"` stays on it; the button and its icon sit
 * in the shadow root and the action is handed in as [show]'s callback.
 */

/** Creates a defined `<vaadin-demo-close-icon>`. */
internal fun newCloseIcon(name: String, onClick: () -> Unit): CloseIcon {
    // The companion registers the tag in its `init`; reading `NAME` upgrades the element the page already spells.
    CloseIcon.NAME
    return CloseIcon.NAME().also { it.show(name, onClick) }
}
