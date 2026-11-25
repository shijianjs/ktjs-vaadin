package demo.pages.layout

import io.github.shijianjs.ktjs.demo.page.Blocks.col as kitCol
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.page.Blocks.row as kitRow
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import web.aria.AriaCurrent
import web.aria.`false`
import web.aria.`true`
import web.html.HtmlTagName
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import io.github.shijianjs.ktjs.demo.page.Blocks.heading
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import demo.CatalogEntry
import demo.Catalog
import demo.Groups
import demo.bi
import demo.pages.fixtures.Person
import demo.pages.fixtures.people
import demo.ui.PageFrame
import demo.ui.PageMeta
import demo.ui.Readout
import demo.ui.listen
import js.objects.unsafeJso
import kotlin.js.unsafeCast
import io.github.shijianjs.ktjs.vaadin.avatar.createAvatar
import io.github.shijianjs.ktjs.vaadin.button.ButtonElement
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.emailfield.createEmailField
import io.github.shijianjs.ktjs.vaadin.formlayout.createFormLayout
import io.github.shijianjs.ktjs.vaadin.masterdetaillayout.MasterDetailLayoutOrientations
import io.github.shijianjs.ktjs.vaadin.masterdetaillayout.MasterDetailLayoutOverlayContainments
import io.github.shijianjs.ktjs.vaadin.masterdetaillayout.createMasterDetailLayout
import io.github.shijianjs.ktjs.vaadin.select.SelectEvents
import io.github.shijianjs.ktjs.vaadin.select.SelectItemData
import io.github.shijianjs.ktjs.vaadin.select.createSelect
import io.github.shijianjs.ktjs.vaadin.switch.SwitchEvents
import io.github.shijianjs.ktjs.vaadin.switch.createSwitch
import io.github.shijianjs.ktjs.vaadin.textfield.createTextField
import web.dom.clickEvent
import web.events.addHandler
import web.html.HTMLElement
import web.html.HTMLParagraphElement
import web.cssom.ClassName
import demo.ui.MUTED_SMALL

internal class MasterDetailLayoutPage : DemoPage() {
    private val texts = Binder(masterDetailLayoutTexts)

    private val frame = PageFrame(ENTRY)

    /** The placeholder example's toggle, whose label follows the state as well as the language. */
    private val detailToggle: ButtonElement = createButton()

    private var detailShown = false

    private var detailToggleCopy: (MasterDetailLayoutTexts) -> String = { it.showDetail }

    init {
        frame.mount(this)
        val crew = people(6)

        val basic = SectionBlock.NAME { show() }
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        val layout = createMasterDetailLayout {
            masterSize = "15rem"
            style.setProperty("height", "24rem")
        }
        layout.dataset["test"] = "master-detail-layout"

        val detailName = heading(3, crew.first().displayName)
        val firstName = createTextField()
        texts.bind(firstName, { it.firstName }) { firstName.label = it }
        val lastName = createTextField()
        texts.bind(lastName, { it.lastName }) { lastName.label = it }
        val emailField = createEmailField()
        texts.bind(emailField, { it.email }) { emailField.label = it }
        val phoneField = createTextField()
        texts.bind(phoneField, { it.phone }) { phoneField.label = it }
        val detailForm = createFormLayout { autoResponsive = true }
        detailForm.append(firstName, lastName, emailField, phoneField)
        val detail = HtmlTagName.div().apply {
            slot = "detail"
            style.setProperty("padding", "var(--lumo-space-m)")
            append(detailName, detailForm)
        }

        val rows = mutableListOf<HTMLElement>()
        fun show(person: Person) {
            detailName.textContent = person.displayName
            firstName.value = person.firstName
            lastName.value = person.lastName
            emailField.value = person.email
            phoneField.value = person.phone
        }
        fun select(person: Person, row: HTMLElement) {
            rows.forEach { candidate ->
                candidate.ariaCurrent = if (candidate === row) AriaCurrent.`true` else AriaCurrent.`false`
            }
            show(person)
        }

        val master = HtmlTagName.div { className = MASTER_LIST }
        crew.forEach { person ->
            val row = HtmlTagName.div { className = MASTER_LIST_ITEM }
            row.append(
                createAvatar {
                    name = person.displayName
                    img = person.pictureUrl
                }.addTheme("small"),
                kitCol(HtmlTagName.span {
                           className = MASTER_LIST_NAME
                           textContent = person.displayName
                       }, HtmlTagName.span {
                              className = MUTED_SMALL
                              textContent = person.profession
                          }),
            )
            row.ariaCurrent = AriaCurrent.`false`
            row.clickEvent.addHandler { select(person, row) }
            rows.add(row)
            master.append(row)
        }
        select(crew.first(), rows.first())
        layout.append(master, detail)
        basic.body.append(exampleBox(null, layout))
        body.append(basic.root)

        val sizes = SectionBlock.NAME { show() }
        texts.bind(sizes.heading) { it.sizesHeading }
        texts.bind(sizes.hint) { it.sizesHint }
        val sized = createMasterDetailLayout {
            masterSize = "12rem"
            detailSize = "16rem"
            style.setProperty("height", "12rem")
        }
        sized.dataset["test"] = "master-detail-sizes"
        val sizedMaster = HtmlTagName.div()
        texts.bind(sizedMaster) { it.master12 }
        val sizedDetail = HtmlTagName.div().apply {
            slot = "detail"
            style.setProperty("padding", "var(--lumo-space-m)")
        }
        texts.bind(sizedDetail) { it.detail16 }
        sized.append(sizedMaster, sizedDetail)
        val expandMasterSwitch = createSwitch { label = "expandMaster" }
        val expandDetailSwitch = createSwitch { label = "expandDetail" }
        val sizeReadout = Readout("expandMaster = false · expandDetail = false")
        fun describeSizes() {
            sizeReadout.set("expandMaster = ${sized.expandMaster} · expandDetail = ${sized.expandDetail}")
        }
        expandMasterSwitch.listen(SwitchEvents.checkedChanged) {
            sized.expandMaster = expandMasterSwitch.checked
            describeSizes()
        }
        expandDetailSwitch.listen(SwitchEvents.checkedChanged) {
            sized.expandDetail = expandDetailSwitch.checked
            describeSizes()
        }
        sizes.body.append(exampleBox(null, kitCol(sized, kitRow(expandMasterSwitch, expandDetailSwitch, sizeReadout.root))))
        body.append(sizes.root)

        val orientationSection = SectionBlock.NAME { show() }
        texts.bind(orientationSection.heading) { it.orientationHeading }
        texts.bind(orientationSection.hint) { it.orientationHint }
        val oriented = createMasterDetailLayout {
            masterSize = "10rem"
            detailSize = "8rem"
            style.setProperty("height", "16rem")
        }
        val orientedMaster = HtmlTagName.div()
        texts.bind(orientedMaster) { it.master }
        val orientedDetail = HtmlTagName.div().apply {
            slot = "detail"
            style.setProperty("padding", "var(--lumo-space-m)")
        }
        texts.bind(orientedDetail) { it.detail }
        oriented.append(orientedMaster, orientedDetail)
        val orientationReadout = Readout("orientation = horizontal")
        val orientationButton = createButton().addTheme("tertiary").apply {
            clickEvent.addHandler {
                oriented.orientation =
                    if (oriented.orientation == MasterDetailLayoutOrientations.horizontal) {
                        MasterDetailLayoutOrientations.vertical
                    } else {
                        MasterDetailLayoutOrientations.horizontal
                    }
                orientationReadout.set("orientation = ${oriented.orientation.unsafeCast<String>()}")
                oriented.recalculateLayout()
            }
        }
        texts.bind(orientationButton) { it.toggleOrientation }
        orientationSection.body.append(exampleBox(null, kitCol(oriented, kitRow(orientationButton, orientationReadout.root))))
        body.append(orientationSection.root)

        val overlaySection = SectionBlock.NAME { show() }
        texts.bind(overlaySection.heading) { it.overlayHeading }
        texts.bind(overlaySection.hint) { it.overlayHint }
        val overlayLayout = createMasterDetailLayout {
            masterSize = "14rem"
            detailSize = "12rem"
            style.setProperty("height", "12rem")
        }
        overlayLayout.dataset["test"] = "master-detail-overlay"
        val overlayMaster = HtmlTagName.div()
        texts.bind(overlayMaster) { it.master }
        val overlayDetail = HtmlTagName.div().apply {
            slot = "detail"
            style.setProperty("padding", "var(--lumo-space-m)")
        }
        texts.bind(overlayDetail) { it.detail }
        overlayLayout.append(overlayMaster, overlayDetail)
        val forceOverlaySwitch = createSwitch { label = "forceOverlay" }
        val fullOverlaySwitch = createSwitch { label = "overlaySize = 100%" }
        val containmentSelect =
            createSelect {
                label = "overlayContainment"
                items =
                    arrayOf(
                        unsafeJso<SelectItemData> {
                            this.label = "layout"
                            value = "layout"
                        },
                        unsafeJso<SelectItemData> {
                            this.label = "page"
                            value = "page"
                        },
                    )
                value = "layout"
            }
        forceOverlaySwitch.listen(SwitchEvents.checkedChanged) {
            overlayLayout.forceOverlay = forceOverlaySwitch.checked
            overlayLayout.recalculateLayout()
        }
        fullOverlaySwitch.listen(SwitchEvents.checkedChanged) {
            overlayLayout.overlaySize = if (fullOverlaySwitch.checked) "100%" else ""
            overlayLayout.recalculateLayout()
        }
        containmentSelect.listen(SelectEvents.valueChanged) {
            overlayLayout.overlayContainment =
                if (containmentSelect.value == "page") {
                    MasterDetailLayoutOverlayContainments.page
                } else {
                    MasterDetailLayoutOverlayContainments.layout
                }
        }
        val overlayCard =
            exampleBox(
                "",
                kitCol(overlayLayout, kitRow(forceOverlaySwitch, fullOverlaySwitch, containmentSelect)),
            )
        texts.bind(overlayCard.firstElementChild as HTMLParagraphElement) { it.overlayCaption }
        overlaySection.body.append(overlayCard)
        body.append(overlaySection.root)

        val placeholderSection = SectionBlock.NAME { show() }
        texts.bind(placeholderSection.heading) { it.placeholderHeading }
        texts.bind(placeholderSection.hint) { it.placeholderHint }
        val placeholderLayout = createMasterDetailLayout {
            masterSize = "14rem"
            style.setProperty("height", "12rem")
        }
        placeholderLayout.dataset["test"] = "master-detail-placeholder"
        val placeholderDetail = HtmlTagName.div().apply {
            slot = "detail"
            style.setProperty("padding", "var(--lumo-space-m)")
        }
        texts.bind(placeholderDetail) { it.adaDetails }
        detailToggle.addTheme("tertiary")
        texts.bind(detailToggle) { detailToggleCopy(it) }
        detailToggle.clickEvent.addHandler {
            detailShown = !detailShown
            detailToggleCopy = if (detailShown) { { copy -> copy.hideDetail } } else { { copy -> copy.showDetail } }
            if (detailShown) placeholderLayout.append(placeholderDetail) else placeholderDetail.remove()
            showToggleCopy()
            placeholderLayout.recalculateLayout()
        }
        val members = HtmlTagName.div { className = MASTER_LIST }
        repeat(3) { index ->
            val row = HtmlTagName.div { className = MASTER_LIST_ITEM }
            texts.bind(row, { copy -> copy.member(index) }) { row.textContent = it }
            members.append(row)
        }
        val placeholder = HtmlTagName.div().apply {
            slot = "detail-placeholder"
            style.setProperty("padding", "var(--lumo-space-m)")
        }
        texts.bind(placeholder) { it.selectMember }
        placeholderLayout.append(members, placeholder)
        placeholderSection.body.append(exampleBox(null, kitCol(placeholderLayout, detailToggle)))
        body.append(placeholderSection.root)

        val nestingSection = SectionBlock.NAME { show() }
        texts.bind(nestingSection.heading) { it.nestingHeading }
        texts.bind(nestingSection.hint) { it.nestingHint }
        val inner = createMasterDetailLayout {
            masterSize = "8rem"
            style.setProperty("height", "100%")
        }
        val innerMaster = HtmlTagName.div()
        texts.bind(innerMaster) { it.innerMaster }
        val innerDetail = HtmlTagName.div().apply {
            slot = "detail"
            style.setProperty("padding", "var(--lumo-space-s)")
        }
        texts.bind(innerDetail) { it.innerDetail }
        inner.append(innerMaster, innerDetail)
        val outer = createMasterDetailLayout {
            masterSize = "10rem"
            detailSize = "1fr"
            style.setProperty("height", "16rem")
        }
        val outerMaster = HtmlTagName.div()
        texts.bind(outerMaster) { it.outerMaster }
        outer.append(
            outerMaster,
            HtmlTagName.div().apply { slot = "detail"; append(inner) },
        )
        nestingSection.body.append(exampleBox(null, outer))
        body.append(nestingSection.root)

        applyTranslation(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = ENTRY.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        showToggleCopy()
    }

    /** Rewrites the toggle's label from the state and the language it is currently in. */
    private fun showToggleCopy() {
        detailToggle.textContent = detailToggleCopy(texts.current)
    }

    companion object : MasterDetailLayoutPageDefaultReference() {
        val ENTRY: CatalogEntry =
            CatalogEntry(
                id = "master-detail-layout",
                group = Groups.LAYOUT,
                meta =
                    PageMeta(
                        title = bi("Master Detail Layout", "Master Detail Layout"),
                        summary =
                            bi(
                                "主从布局：主区在默认插槽，详情在 detail 插槽，放不下时自动切换成浮层。",
                                "Master-detail layout: the master goes into the default slot and the detail into the " +
                                    "detail slot; the detail switches to an overlay when it does not fit.",
                            ),
                        docsUrl = "${Catalog.DOCS_BASE}/master-detail-layout",
                        sourcePath = "pages/layout/MasterDetailLayoutPage.kt",
                    ),
                kitPage = { MasterDetailLayoutPage.NAME() },
            )

        private val english: MasterDetailLayoutTexts =
            MasterDetailLayoutTexts(
                basicHeading = "Basic usage",
                basicHint = "The master goes into the default slot and the detail into slot=\"detail\". " +
                    "Selecting a person updates the form on the right, and the detail turns into an overlay " +
                    "when space runs out.",
                firstName = "First name",
                lastName = "Last name",
                email = "Email address",
                phone = "Phone number",
                sizesHeading = "Sizes and expansion",
                sizesHint = "masterSize defaults to 30rem, while an unset detailSize is measured from the " +
                    "detail content and cached. expandMaster / expandDetail let an area take the remaining width.",
                master12 = "master · 12rem",
                detail16 = "detail · 16rem",
                orientationHeading = "Orientation",
                orientationHint = "orientation decides whether master and detail sit side by side or stacked; " +
                    "the vertical variant needs enough available height.",
                master = "master",
                detail = "detail",
                toggleOrientation = "Toggle orientation",
                overlayHeading = "Overlay",
                overlayHint = "forceOverlay always shows the detail as an overlay, overlaySize sets its size " +
                    "separately and overlayContainment decides whether it covers the layout or the whole page.",
                overlayCaption = "The overlay covers the layout; page containment covers the viewport.",
                placeholderHeading = "Detail placeholder",
                placeholderHint = "While nothing is selected, the slot=\"detail-placeholder\" content fills the " +
                    "detail area and makes way once a detail is provided.",
                adaDetails = "The details of Ada Lovelace",
                showDetail = "Show detail",
                hideDetail = "Hide detail",
                member = { index -> "Member ${index + 1}" },
                selectMember = "Select a member to see the details",
                nestingHeading = "Nesting",
                nestingHint = "The detail area can host another master-detail layout; the outer one decides " +
                    "when the inner one falls back to an overlay.",
                innerMaster = "Inner master",
                innerDetail = "Inner detail",
                outerMaster = "Outer master",
            )
        private val chinese: MasterDetailLayoutTexts =
            MasterDetailLayoutTexts(
                basicHeading = "基本用法",
                basicHint = "主区放在默认插槽，详情放进 slot=\"detail\"。点击左侧人物会更新右侧表单；空间不足时详情自动变成浮层。",
                firstName = "名",
                lastName = "姓",
                email = "邮箱地址",
                phone = "电话号码",
                sizesHeading = "尺寸与扩展",
                sizesHint = "masterSize 默认 30rem；detailSize 未设置时按详情内容测量并缓存。expandMaster / " +
                    "expandDetail 让对应区域吃掉剩余宽度。",
                master12 = "master · 12rem",
                detail16 = "detail · 16rem",
                orientationHeading = "方向",
                orientationHint = "orientation 决定主区与详情是左右还是上下排列；纵向排列时需要足够的可用高度。",
                master = "master",
                detail = "detail",
                toggleOrientation = "切换方向",
                overlayHeading = "浮层",
                overlayHint = "forceOverlay 强制详情以浮层显示，overlaySize 单独设置浮层尺寸，overlayContainment " +
                    "决定浮层覆盖布局还是整个页面。",
                overlayCaption = "浮层只覆盖布局本身；切到 page 后会遮住整个页面。",
                placeholderHeading = "详情占位",
                placeholderHint = "没有选中项时，slot=\"detail-placeholder\" 的内容显示在详情区；详情出现后占位自动让位。",
                adaDetails = "Ada Lovelace 的详细资料",
                showDetail = "显示详情",
                hideDetail = "隐藏详情",
                member = { index -> "成员 ${index + 1}" },
                selectMember = "选择左侧成员查看详情",
                nestingHeading = "嵌套",
                nestingHint = "详情区里可以再放一个主从布局，外层决定了内层何时退化成浮层。",
                innerMaster = "二级主区",
                innerDetail = "二级详情",
                outerMaster = "一级主区",
            )
        private val MASTER_LIST: ClassName = ClassName("master-list")
        private val MASTER_LIST_ITEM: ClassName = ClassName("master-list__item")
        private val MASTER_LIST_NAME: ClassName = ClassName("master-list__name")
        private val masterDetailLayoutTexts: Translations<MasterDetailLayoutTexts> = texts(
            Langs.EN to english,
            Langs.ZH_HANS to chinese,
        )
    }
}
