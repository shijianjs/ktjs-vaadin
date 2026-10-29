package demo.pages.overlays

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.tr
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.column
import demo.ui.example
import demo.ui.page
import demo.ui.paragraph
import demo.ui.row
import demo.ui.section
import demo.ui.withTheme
import js.objects.unsafeJso
import vaadin.button.createButton
import vaadin.overlay.createOverlay
import web.dom.clickEvent
import web.events.addHandler

val overlayEntry: CatalogEntry =
    CatalogEntry(
        id = "overlay",
        group = Groups.OVERLAYS,
        meta =
            PageMeta(
                title = bi("Overlay", "Overlay"),
                summary =
                    bi(
                        "底层浮层元素：Dialog / Select / ComboBox 都构建在它之上。",
                        "Low-level overlay element: Dialog / Select / ComboBox are all built on it.",
                    ),
                docsUrl = "$DOCS_BASE/dialog",
                docsNote =
                    bi(
                        "vaadin-overlay 是内部基础元素，官方文档见 Dialog。",
                        "vaadin-overlay is an internal building block; the official documentation is " +
                            "under Dialog.",
                    ),
                sourcePath = "pages/overlays/OverlayPage.kt",
            ),
        builder = ::buildOverlayPage,
    )

private fun buildOverlayPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "renderer(root, owner, model) 负责内容；withBackdrop 打开遮罩。",
                "renderer(root, owner, model) supplies the content; withBackdrop shows the backdrop.",
            ),
        )
    val overlay = createOverlay()
    overlay.dataset["test"] = "overlay-basic"
    overlay.withBackdrop = true
    val overlayContent =
        column(
            paragraph(
                tr(
                    "这是一个裸 overlay，没有 dialog 的头部与底部。",
                    "This is a bare overlay, without the dialog header and footer.",
                ),
            ),
        )
    val overlayClose = createButton { textContent = tr("关闭", "Close") }
    overlayClose.clickEvent.addHandler { overlay.opened = false }
    overlayContent.append(overlayClose)
    overlay.renderer = { root, _, _ -> root.append(overlayContent) }

    val openButton =
        createButton { textContent = tr("打开 overlay", "Open overlay") }.apply {
            clickEvent.addHandler { overlay.opened = true }
        }
    basic.body.append(example(null, row(openButton, overlay)))
    view.body.append(basic.root)

    val modeless =
        section(
            tr("无遮罩 / 非模态", "No backdrop / modeless"),
            tr(
                "modeless 让浮层不抢焦点；withBackdrop=false 去掉遮罩。",
                "modeless keeps the overlay from taking focus; withBackdrop=false removes the backdrop.",
            ),
        )
    val modelessOverlay = createOverlay()
    modelessOverlay.withBackdrop = false
    modelessOverlay.modeless = true
    val modelessContent =
        column(
            paragraph(tr("非模态浮层，页面其他部分仍可交互。", "Modeless overlay: the rest of the page stays interactive.")),
        )
    val modelessClose = createButton { textContent = tr("关闭", "Close") }
    modelessClose.clickEvent.addHandler { modelessOverlay.opened = false }
    modelessContent.append(modelessClose)
    modelessOverlay.renderer = { root, _, _ -> root.append(modelessContent) }
    modeless.body.append(
        example(
            null,
            row(
                createButton { textContent = tr("打开非模态 overlay", "Open modeless overlay") }
                    .withTheme("tertiary").apply {
                        clickEvent.addHandler { modelessOverlay.opened = true }
                    },
                modelessOverlay,
            ),
        ),
    )
    view.body.append(modeless.root)

    val sized =
        section(
            tr("尺寸与滚动", "Size and scrolling"),
            tr(
                "setBounds 写入浮层的宽度与高度，内容超出时由内容区滚动。",
                "setBounds sets the width and height of the overlay; overflowing content scrolls inside " +
                    "the content area.",
            ),
        )
    val sizedOverlay = createOverlay()
    val longContent = column(paragraph(tr("可滚动的内容：", "Scrollable content:")))
    repeat(12) { index ->
        longContent.append(paragraph(tr("第 ${index + 1} 行内容", "Line ${index + 1}")))
    }
    val sizedClose = createButton { textContent = tr("关闭", "Close") }
    sizedClose.clickEvent.addHandler { sizedOverlay.opened = false }
    longContent.append(sizedClose)
    sizedOverlay.renderer = { root, _, _ -> root.append(longContent) }
    sized.body.append(
        example(
            null,
            row(
                createButton { textContent = tr("打开固定尺寸 overlay", "Open sized overlay") }
                    .withTheme("tertiary").apply {
                        clickEvent.addHandler {
                            // `setBounds` writes to the overlay's internal parts, which exist only once
                            // the element has been upgraded and opened.
                            sizedOverlay.setBounds(
                                unsafeJso<vaadin.overlay.OverlayBounds> {
                                    width = vaadin.overlay.DoubleOrString("22rem")
                                    height = vaadin.overlay.DoubleOrString("14rem")
                                },
                            )
                            sizedOverlay.opened = true
                        }
                    },
                sizedOverlay,
            ),
        ),
    )
    view.body.append(sized.root)

    return view
}
