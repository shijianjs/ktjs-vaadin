package demo.pages.overlays

import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.Blocks.col as kitCol
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.page.Blocks.row as kitRow
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import io.github.shijianjs.ktjs.demo.page.Blocks.paragraph
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import demo.CatalogEntry
import demo.Catalog
import demo.Groups
import demo.bi
import demo.ui.PageFrame
import demo.ui.PageMeta
import js.objects.unsafeJso
import io.github.shijianjs.ktjs.vaadin.button.ButtonElement
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.overlay.createOverlay
import web.dom.clickEvent
import web.events.addHandler

internal class OverlayPage : DemoPage() {
    private val texts = Binder(overlayTexts)

    private val frame = PageFrame(ENTRY)

    /** The buttons of the three examples; only their labels depend on the language. */
    private val openButton: ButtonElement = createButton()
    private val overlayClose: ButtonElement = createButton()
    private val modelessClose: ButtonElement = createButton()
    private val openModeless: ButtonElement = createButton()
    private val sizedClose: ButtonElement = createButton()
    private val openSized: ButtonElement = createButton()

    init {
        frame.mount(this)

        val basic = SectionBlock.NAME { show() }
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        val overlay = createOverlay()
        overlay.dataset["test"] = "overlay-basic"
        overlay.withBackdrop = true
        val bareOverlay = paragraph()
        texts.bind(bareOverlay) { it.bareOverlay }
        val overlayContent = kitCol(bareOverlay)
        overlayClose.clickEvent.addHandler { overlay.opened = false }
        overlayContent.append(overlayClose)
        overlay.renderer = { root, _, _ -> root.append(overlayContent) }

        openButton.clickEvent.addHandler { overlay.opened = true }
        basic.body.append(exampleBox(null, kitRow(openButton, overlay)))
        body.append(basic.root)

        val modeless = SectionBlock.NAME { show() }
        texts.bind(modeless.heading) { it.modelessHeading }
        texts.bind(modeless.hint) { it.modelessHint }
        val modelessOverlay = createOverlay()
        modelessOverlay.withBackdrop = false
        modelessOverlay.modeless = true
        val modelessBody = paragraph()
        texts.bind(modelessBody) { it.modelessBody }
        val modelessContent = kitCol(modelessBody)
        modelessClose.clickEvent.addHandler { modelessOverlay.opened = false }
        modelessContent.append(modelessClose)
        modelessOverlay.renderer = { root, _, _ -> root.append(modelessContent) }
        openModeless.addTheme("tertiary")
        openModeless.clickEvent.addHandler { modelessOverlay.opened = true }
        modeless.body.append(exampleBox(null, kitRow(openModeless, modelessOverlay)))
        body.append(modeless.root)

        val sized = SectionBlock.NAME { show() }
        texts.bind(sized.heading) { it.sizedHeading }
        texts.bind(sized.hint) { it.sizedHint }
        val sizedOverlay = createOverlay()
        val longContent = kitCol()
        val scrollableContent = paragraph()
        texts.bind(scrollableContent) { it.scrollableContent }
        longContent.append(scrollableContent)
        repeat(12) { index ->
            val line = paragraph()
            texts.bind(line) { copy -> copy.line(index + 1) }
            longContent.append(line)
        }
        sizedClose.clickEvent.addHandler { sizedOverlay.opened = false }
        longContent.append(sizedClose)
        sizedOverlay.renderer = { root, _, _ -> root.append(longContent) }
        openSized.addTheme("tertiary")
        openSized.clickEvent.addHandler {
            // `setBounds` writes to the overlay's internal parts, which exist only once the element
            // has been upgraded and opened.
            sizedOverlay.setBounds(
                unsafeJso<io.github.shijianjs.ktjs.vaadin.overlay.OverlayBounds> {
                    width = io.github.shijianjs.ktjs.vaadin.overlay.DoubleOrString("22rem")
                    height = io.github.shijianjs.ktjs.vaadin.overlay.DoubleOrString("14rem")
                },
            )
            sizedOverlay.opened = true
        }
        sized.body.append(exampleBox(null, kitRow(openSized, sizedOverlay)))
        body.append(sized.root)

        applyTranslation(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = ENTRY.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        val copy = texts.current
        openButton.textContent = copy.openOverlay
        overlayClose.textContent = copy.close
        modelessClose.textContent = copy.close
        openModeless.textContent = copy.openModeless
        sizedClose.textContent = copy.close
        openSized.textContent = copy.openSized
    }

    companion object : OverlayPageDefaultReference() {
        val ENTRY: CatalogEntry =
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
                        docsUrl = "${Catalog.DOCS_BASE}/dialog",
                        docsNote =
                            bi(
                                "vaadin-overlay 是内部基础元素，官方文档见 Dialog。",
                                "vaadin-overlay is an internal building block; the official documentation is " +
                                    "under Dialog.",
                            ),
                        sourcePath = "pages/overlays/OverlayPage.kt",
                    ),
                kitPage = { OverlayPage.NAME() },
            )

        private val english: OverlayTexts = OverlayTexts(
            basicHeading = "Basic usage",
            basicHint = "renderer(root, owner, model) supplies the content; withBackdrop shows the backdrop.",
            bareOverlay = "This is a bare overlay, without the dialog header and footer.",
            close = "Close",
            openOverlay = "Open overlay",
            modelessHeading = "No backdrop / modeless",
            modelessHint = "modeless keeps the overlay from taking focus; withBackdrop=false removes the backdrop.",
            modelessBody = "Modeless overlay: the rest of the page stays interactive.",
            openModeless = "Open modeless overlay",
            sizedHeading = "Size and scrolling",
            sizedHint = "setBounds sets the width and height of the overlay; overflowing content scrolls inside the " +
                "content area.",
            scrollableContent = "Scrollable content:",
            line = { index -> "Line $index" },
            openSized = "Open sized overlay",
        )
        private val chinese: OverlayTexts = OverlayTexts(
            basicHeading = "基本用法",
            basicHint = "renderer(root, owner, model) 负责内容；withBackdrop 打开遮罩。",
            bareOverlay = "这是一个裸 overlay，没有 dialog 的头部与底部。",
            close = "关闭",
            openOverlay = "打开 overlay",
            modelessHeading = "无遮罩 / 非模态",
            modelessHint = "modeless 让浮层不抢焦点；withBackdrop=false 去掉遮罩。",
            modelessBody = "非模态浮层，页面其他部分仍可交互。",
            openModeless = "打开非模态 overlay",
            sizedHeading = "尺寸与滚动",
            sizedHint = "setBounds 写入浮层的宽度与高度，内容超出时由内容区滚动。",
            scrollableContent = "可滚动的内容：",
            line = { index -> "第 $index 行内容" },
            openSized = "打开固定尺寸 overlay",
        )
        private val overlayTexts: Translations<OverlayTexts> = texts(
            Langs.EN to english,
            Langs.ZH_HANS to chinese,
        )
    }
}
