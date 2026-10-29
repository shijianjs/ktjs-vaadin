package demo.pages.overlays

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.tr
import demo.ui.EventLog
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.anchor
import demo.ui.column
import demo.ui.example
import demo.ui.listen
import demo.ui.page
import demo.ui.paragraph
import demo.ui.row
import demo.ui.section
import demo.ui.span
import demo.ui.withTheme
import js.objects.unsafeJso
import vaadin.avatar.createAvatar
import vaadin.button.createButton
import vaadin.horizontallayout.createHorizontalLayout
import vaadin.icon.createIcon
import vaadin.notification.Notification
import vaadin.notification.NotificationShowOptions
import vaadin.notification.createNotification
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
        builder = ::buildNotificationPage,
    )

// region Notification

private fun buildNotificationPage(meta: PageMeta): PageView {
    val view = page(meta)

    val positions =
        section(
            tr("位置与时长", "Position and duration"),
            tr(
                "Notification.show 是一次性提示；options 可以指定 position、duration 与 theme。",
                "Notification.show shows a one-off message; options can set position, duration and theme.",
            ),
        )
    positions.body.append(
        example(
            null,
            row(
                createButton { textContent = tr("右上角", "Top end") }.withTheme("tertiary").apply {
                    clickEvent.addHandler {
                        Notification.show(
                            tr("右上角的提示", "Message at the top end"),
                            unsafeJso<NotificationShowOptions> {
                                position = "top-end"
                                duration = 3000.0
                            },
                        )
                    }
                },
                createButton { textContent = tr("顶部居中", "Top center") }.withTheme("tertiary").apply {
                    clickEvent.addHandler {
                        Notification.show(
                            tr("顶部居中的提示", "Message at the top center"),
                            unsafeJso<NotificationShowOptions> {
                                position = "top-center"
                                duration = 3000.0
                            },
                        )
                    }
                },
                createButton { textContent = tr("底部居中（默认）", "Bottom center (default)") }
                    .withTheme("tertiary").apply {
                        clickEvent.addHandler {
                            Notification.show(tr("底部居中的提示", "Message at the bottom center"))
                        }
                    },
            ),
        ),
    )
    view.body.append(positions.root)

    val allPositions =
        section(
            tr("全部位置", "Every position"),
            tr(
                "position 接受九种取值：顶部 / 中间 / 底部，各三种水平对齐，stretch 占满整行。",
                "position takes nine values: top / middle / bottom, each with three horizontal alignments; " +
                    "stretch spans the whole row.",
            ),
        )
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
        example(
            null,
            row(
                *positionNames
                    .map { name ->
                        createButton { textContent = name }.withTheme("tertiary small").apply {
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
    view.body.append(allPositions.root)

    val themes =
        section(
            tr("主题变体", "Theme variants"),
            tr("theme 会传播到内部的 notification-card。", "theme propagates to the inner notification-card."),
        )
    themes.body.append(
        example(
            null,
            row(
                createButton { textContent = "success" }.withTheme("success").apply {
                    clickEvent.addHandler {
                        Notification.show(
                            tr("保存成功", "Saved successfully"),
                            unsafeJso<NotificationShowOptions> {
                                theme = "success"
                                position = "bottom-end"
                            },
                        )
                    }
                },
                createButton { textContent = "error" }.withTheme("error").apply {
                    clickEvent.addHandler {
                        Notification.show(
                            tr("保存失败", "Save failed"),
                            unsafeJso<NotificationShowOptions> {
                                theme = "error"
                                position = "bottom-end"
                            },
                        )
                    }
                },
                createButton { textContent = "contrast" }.withTheme("contrast").apply {
                    clickEvent.addHandler {
                        Notification.show(
                            tr("对比色提示", "Contrast message"),
                            unsafeJso<NotificationShowOptions> {
                                theme = "contrast"
                                position = "bottom-end"
                            },
                        )
                    }
                },
                createButton { textContent = "info" }.withTheme("tertiary").apply {
                    clickEvent.addHandler {
                        Notification.show(
                            tr("新项目计划已就绪", "New project plan available"),
                            unsafeJso<NotificationShowOptions> {
                                theme = "info"
                                position = "bottom-end"
                            },
                        )
                    }
                },
                createButton { textContent = "warning" }.withTheme("tertiary").apply {
                    clickEvent.addHandler {
                        Notification.show(
                            tr("会话即将过期", "Your session is about to expire"),
                            unsafeJso<NotificationShowOptions> {
                                theme = "warning"
                                position = "bottom-end"
                            },
                        )
                    }
                },
            ),
        ),
    )
    view.body.append(themes.root)

    val persistent =
        section(
            tr("常驻通知", "Persistent notification"),
            tr(
                "duration 设为 0 时不自动关闭，需要在 renderer 里放一个关闭按钮。",
                "With duration set to 0 the notification stays open; put a close button in the renderer.",
            ),
        )
    val persistentNote = createNotification()
    persistentNote.duration = 0.0
    persistentNote.position = "middle"
    val noteContent =
        row(
            paragraph(tr("这条通知会一直显示，直到点击关闭。", "This notification stays visible until it is closed.")),
            createButton { textContent = tr("关闭", "Close") }.withTheme("tertiary small").apply {
                clickEvent.addHandler { persistentNote.opened = false }
            },
        )
    persistentNote.renderer = { root, _ -> root.append(noteContent) }

    persistent.body.append(
        example(
            null,
            row(
                createButton { textContent = tr("显示常驻通知", "Show persistent notification") }.apply {
                    clickEvent.addHandler { persistentNote.opened = true }
                },
                createButton { textContent = tr("关闭", "Close") }.withTheme("tertiary").apply {
                    clickEvent.addHandler { persistentNote.opened = false }
                },
                persistentNote,
            ),
        ),
    )
    view.body.append(persistent.root)

    val rich =
        section(
            tr("富内容通知", "Rich notifications"),
            tr(
                "renderer 里可以放图标、头像、链接与操作按钮；通知卡片在 renderer 与组件之间共享。",
                "The renderer can hold icons, avatars, links and actions; the card is shared between the " +
                    "renderer and the component.",
            ),
        )
    val richHost = column()

    val successNote = createNotification()
    successNote.duration = 0.0
    successNote.position = "middle"
    successNote.withTheme("success")
    successNote.renderer = { root, _ ->
        val bar = horizontalBar()
        bar.append(createIcon { icon = "vaadin:check-circle" })
        bar.append(span(tr("申请已提交！", "Application submitted!")))
        bar.append(slotted("end", createButton { textContent = tr("查看", "View") }))
        bar.append(closeIcon(tr("关闭", "Close")) { successNote.opened = false }.also { it.slot = "end" })
        root.append(bar)
    }

    val failedNote = createNotification()
    failedNote.duration = 0.0
    failedNote.position = "middle"
    failedNote.withTheme("error")
    failedNote.renderer = { root, _ ->
        val bar = horizontalBar()
        bar.append(createIcon { icon = "vaadin:warning" })
        bar.append(span(tr("报表生成失败", "Failed to generate report")))
        bar.append(slotted("end", createButton { textContent = tr("重试", "Retry") }))
        bar.append(closeIcon(tr("关闭", "Close")) { failedNote.opened = false }.also { it.slot = "end" })
        root.append(bar)
    }

    val mentionNote = createNotification()
    mentionNote.duration = 0.0
    mentionNote.position = "middle"
    mentionNote.renderer = { root, _ ->
        val bar = horizontalBar()
        bar.append(createAvatar { name = "Jason Bailey" })
        bar.append(
            span(tr("Jason Bailey 在", "Jason Bailey mentioned you in")).apply {
                append(anchor(tr("项目 Q4", "Project Q4"), "#"))
            },
        )
        bar.append(closeIcon(tr("关闭", "Close")) { mentionNote.opened = false }.also { it.slot = "end" })
        root.append(bar)
    }
    richHost.append(
        row(
            createButton { textContent = tr("成功通知", "Show rich notification") }.withTheme("success").apply {
                clickEvent.addHandler { successNote.opened = true }
            },
            createButton { textContent = tr("错误通知", "error") }.withTheme("error").apply {
                clickEvent.addHandler { failedNote.opened = true }
            },
            createButton { textContent = tr("提及通知", "mention") }.withTheme("tertiary").apply {
                clickEvent.addHandler { mentionNote.opened = true }
            },
        ),
        successNote,
        failedNote,
        mentionNote,
    )
    rich.body.append(example(null, richHost))
    view.body.append(rich.root)

    val actions =
        section(
            tr("带操作的通知", "Notifications with actions"),
            tr(
                "撤销、重试与链接操作都放在 renderer 里；关闭按钮用图标按钮表达。",
                "Undo, retry and link actions all live in the renderer; a close button is an icon button.",
            ),
        )
    val actionLog = EventLog(5)
    val undoNote = createNotification()
    undoNote.duration = 10000.0
    undoNote.position = "middle"
    undoNote.renderer = { root, _ ->
        val bar = horizontalBar()
        bar.append(span(tr("已删除 5 个任务", "5 tasks deleted")))
        bar.append(
            slotted("end", createButton { textContent = tr("撤销", "Undo") }.apply {
                clickEvent.addHandler {
                    actionLog.add("undo")
                    undoNote.opened = false
                }
            }),
        )
        bar.append(closeIcon(tr("关闭", "Close")) { undoNote.opened = false }.also { it.slot = "end" })
        root.append(bar)
    }

    val linkNote = createNotification()
    linkNote.duration = 0.0
    linkNote.position = "middle"
    linkNote.renderer = { root, _ ->
        val bar = horizontalBar()
        bar.append(
            span(tr("Jason Bailey 提到了你的", "Jason Bailey mentioned you in")).apply {
                append(anchor(tr("项目 Q4", "Project Q4"), "#"))
            },
        )
        bar.append(closeIcon(tr("关闭", "Close")) { linkNote.opened = false }.also { it.slot = "end" })
        root.append(bar)
    }

    val retryNote = createNotification()
    retryNote.duration = 0.0
    retryNote.position = "middle"
    retryNote.withTheme("error")
    retryNote.renderer = { root, _ ->
        val bar = horizontalBar()
        bar.append(span(tr("报表生成失败", "Failed to generate report")))
        bar.append(
            slotted("end", createButton { textContent = tr("重试", "Retry") }.apply {
                clickEvent.addHandler {
                    actionLog.add("retry")
                    retryNote.opened = false
                }
            }),
        )
        bar.append(closeIcon(tr("关闭", "Close")) { retryNote.opened = false }.also { it.slot = "end" })
        root.append(bar)
    }

    actions.body.append(
        example(
            null,
            column(
                row(
                    createButton { textContent = tr("撤销通知", "Undo notification") }.withTheme("tertiary").apply {
                        clickEvent.addHandler { undoNote.opened = true }
                    },
                    createButton { textContent = tr("链接通知", "Link notification") }.withTheme("tertiary").apply {
                        clickEvent.addHandler { linkNote.opened = true }
                    },
                    createButton { textContent = tr("重试通知", "Retry notification") }.withTheme("error").apply {
                        clickEvent.addHandler { retryNote.opened = true }
                    },
                ),
                undoNote,
                linkNote,
                retryNote,
                actionLog.root,
            ),
        ),
    )
    view.body.append(actions.root)

    val keyboard =
        section(
            tr("键盘快捷键", "Keyboard shortcut"),
            tr(
                "通知打开时按 Ctrl-Z（macOS 上是 ⌘-Z）也能撤销；快捷键只在通知可见时生效。",
                "Ctrl-Z (⌘-Z on macOS) undoes while the notification is open; the shortcut only works " +
                    "while it is visible.",
            ),
        )
    val keyboardNote = createNotification()
    keyboardNote.duration = 10000.0
    keyboardNote.position = "middle"
    keyboardNote.renderer = { root, _ ->
        val bar = horizontalBar()
        bar.append(span(tr("已删除 5 个任务", "5 tasks deleted")))
        bar.append(
            slotted("end", createButton { textContent = tr("撤销 Ctrl-Z", "Undo Ctrl-Z") }.apply {
                clickEvent.addHandler { keyboardNote.opened = false }
            }),
        )
        root.append(bar)
    }
    // The listener lives on `document`, so it is registered once per page build and stops reacting
    // as soon as the notification is closed.
    document.listen(KEY_DOWN) { event: KeyboardEvent ->
        if (keyboardNote.opened && (event.metaKey || event.ctrlKey) && event.key == "z") {
            event.preventDefault()
            keyboardNote.opened = false
        }
    }
    keyboard.body.append(
        example(
            null,
            column(
                createButton { textContent = tr("显示通知", "Show notification") }.apply {
                    clickEvent.addHandler { keyboardNote.opened = true }
                },
                keyboardNote,
            ),
        ),
    )
    view.body.append(keyboard.root)

    val lengths =
        section(
            tr("内容长度", "Content length"),
            tr(
                "通知只放结论与一个主要操作；细节留在页面里，避免出现长段落。",
                "Keep a notification to the outcome and one primary action; details belong on the page, " +
                    "not in a long paragraph.",
            ),
        )
    val goodNote = createNotification()
    goodNote.duration = 0.0
    goodNote.position = "middle"
    goodNote.renderer = { root, _ ->
        val bar = horizontalBar()
        bar.append(
            column(
                span(tr("Aria Bailey", "Aria Bailey")),
                span(tr("好的，不过你能帮我……", "Yeah, I know. But could you help me with...")).apply {
                    style.setProperty("font-size", "0.875rem")
                    style.setProperty("color", "var(--vaadin-text-color-secondary)")
                },
            ),
        )
        bar.append(slotted("end", createButton { textContent = tr("查看", "View") }))
        bar.append(closeIcon(tr("关闭", "Close")) { goodNote.opened = false }.also { it.slot = "end" })
        root.append(bar)
    }
    val longText =
        tr(
            "我知道，不过你能帮我看看吗？我不确定我的 CSS 里 bug 在哪里：勾选标记没有显示成正确的颜色，我用了设计系统的 CSS 自定义属性，但好像没有生效。",
            "Yeah, I know. But could you help me with this. I'm not sure where the bug is in my CSS? " +
                "The checkmark doesn't get the right color. I'm trying to use the CSS custom properties " +
                "from our design system, but for some reason it's not working.",
        )
    val badNote = createNotification()
    badNote.duration = 0.0
    badNote.position = "middle"
    badNote.renderer = { root, _ ->
        val bar = horizontalBar()
        bar.append(
            column(
                span(tr("来自 Aria Bailey 的新消息", "New message from Aria Bailey")),
                span(longText).apply {
                    style.setProperty("font-size", "0.875rem")
                    style.setProperty("color", "var(--vaadin-text-color-secondary)")
                },
            ),
        )
        bar.append(closeIcon(tr("关闭", "Close")) { badNote.opened = false }.also { it.slot = "end" })
        root.append(bar)
    }
    lengths.body.append(
        example(
            tr("推荐：一行结论 + 一个操作", "Do: outcome plus one action"),
            column(
                createButton { textContent = tr("显示推荐示例", "Show recommended") }.withTheme("tertiary").apply {
                    clickEvent.addHandler { goodNote.opened = true }
                },
                goodNote,
            ),
        ),
        example(
            tr("不推荐：整段对话塞进通知", "Don't: a whole paragraph in a notification"),
            column(
                createButton { textContent = tr("显示不推荐示例", "Show discouraged") }.withTheme("tertiary").apply {
                    clickEvent.addHandler { badNote.opened = true }
                },
                badNote,
            ),
        ),
    )
    view.body.append(lengths.root)

    return view
}

private fun horizontalBar(): HTMLElement {
    val bar = createHorizontalLayout { withTheme("spacing") }
    bar.style.setProperty("align-items", "center")
    bar.style.setProperty("min-width", "300px")
    return bar
}

private fun closeIcon(name: String, onClick: () -> Unit): HTMLElement =
    createButton { textContent = "" }.withTheme("icon").apply {
        setAttribute("aria-label", name)
        append(createIcon { icon = "lumo:cross" })
        clickEvent.addHandler { onClick() }
    }
