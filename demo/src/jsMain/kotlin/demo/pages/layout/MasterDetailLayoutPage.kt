package demo.pages.layout

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.pages.fixtures.Person
import demo.pages.fixtures.people
import demo.tr
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.Readout
import demo.ui.column
import demo.ui.div
import demo.ui.example
import demo.ui.heading
import demo.ui.listen
import demo.ui.page
import demo.ui.row
import demo.ui.section
import demo.ui.span
import demo.ui.withTheme
import js.objects.unsafeJso
import kotlin.js.unsafeCast
import vaadin.avatar.createAvatar
import vaadin.button.createButton
import vaadin.emailfield.createEmailField
import vaadin.formlayout.createFormLayout
import vaadin.masterdetaillayout.MasterDetailLayoutOrientations
import vaadin.masterdetaillayout.MasterDetailLayoutOverlayContainments
import vaadin.masterdetaillayout.createMasterDetailLayout
import vaadin.select.SelectEvents
import vaadin.select.SelectItemData
import vaadin.select.createSelect
import vaadin.textfield.createTextField
import web.dom.clickEvent
import web.events.addHandler
import web.html.HTMLElement

val masterDetailLayoutEntry: CatalogEntry =
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
                docsUrl = "$DOCS_BASE/master-detail-layout",
                sourcePath = "pages/layout/MasterDetailLayoutPage.kt",
            ),
        builder = ::buildMasterDetailPage,
    )

private fun buildMasterDetailPage(meta: PageMeta): PageView {
    val view = page(meta)
    val crew = people(6)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "主区放在默认插槽，详情放进 slot=\"detail\"。点击左侧人物会更新右侧表单；空间不足时详情" +
                    "自动变成浮层。",
                "The master goes into the default slot and the detail into slot=\"detail\". Selecting a person " +
                    "updates the form on the right, and the detail turns into an overlay when space runs out.",
            ),
        )
    val layout = createMasterDetailLayout {
        masterSize = "15rem"
        style.setProperty("height", "24rem")
    }
    layout.dataset["test"] = "master-detail-layout"

    val detailName = heading(3, crew.first().displayName)
    val firstName = createTextField { label = tr("名", "First name") }
    val lastName = createTextField { label = tr("姓", "Last name") }
    val emailField = createEmailField { label = tr("邮箱地址", "Email address") }
    val phoneField = createTextField { label = tr("电话号码", "Phone number") }
    val detailForm = createFormLayout { autoResponsive = true }
    detailForm.append(firstName, lastName, emailField, phoneField)
    val detail = div().apply {
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
        rows.forEach { candidate -> candidate.setAttribute("aria-current", if (candidate === row) "true" else "false") }
        show(person)
    }

    val master = div("master-list")
    crew.forEach { person ->
        val row = div("master-list__item")
        row.append(
            createAvatar {
                name = person.displayName
                img = person.pictureUrl
            }.withTheme("small"),
            column(span(person.displayName, "master-list__name"), span(person.profession, "muted small")),
        )
        row.setAttribute("aria-current", "false")
        row.clickEvent.addHandler { select(person, row) }
        rows.add(row)
        master.append(row)
    }
    select(crew.first(), rows.first())
    layout.append(master, detail)
    basic.body.append(example(null, layout))
    view.body.append(basic.root)

    val sizes =
        section(
            tr("尺寸与扩展", "Sizes and expansion"),
            tr(
                "masterSize 默认 30rem；detailSize 未设置时按详情内容测量并缓存。expandMaster / expandDetail " +
                    "让对应区域吃掉剩余宽度。",
                "masterSize defaults to 30rem, while an unset detailSize is measured from the detail content " +
                    "and cached. expandMaster / expandDetail let an area take the remaining width.",
            ),
        )
    val sized = createMasterDetailLayout {
        masterSize = "12rem"
        detailSize = "16rem"
        style.setProperty("height", "12rem")
    }
    sized.dataset["test"] = "master-detail-sizes"
    sized.append(
        box(tr("master · 12rem", "master · 12rem")),
        div().apply {
            slot = "detail"
            style.setProperty("padding", "var(--lumo-space-m)")
            textContent = tr("detail · 16rem", "detail · 16rem")
        },
    )
    val expandMasterSwitch = vaadin.switch.createSwitch { label = "expandMaster" }
    val expandDetailSwitch = vaadin.switch.createSwitch { label = "expandDetail" }
    val sizeReadout = Readout("expandMaster = false · expandDetail = false")
    fun describeSizes() {
        sizeReadout.set("expandMaster = ${sized.expandMaster} · expandDetail = ${sized.expandDetail}")
    }
    expandMasterSwitch.listen(vaadin.switch.SwitchEvents.checkedChanged) {
        sized.expandMaster = expandMasterSwitch.checked
        describeSizes()
    }
    expandDetailSwitch.listen(vaadin.switch.SwitchEvents.checkedChanged) {
        sized.expandDetail = expandDetailSwitch.checked
        describeSizes()
    }
    sizes.body.append(example(null, column(sized, row(expandMasterSwitch, expandDetailSwitch, sizeReadout.root))))
    view.body.append(sizes.root)

    val orientationSection =
        section(
            tr("方向", "Orientation"),
            tr(
                "orientation 决定主区与详情是左右还是上下排列；纵向排列时需要足够的可用高度。",
                "orientation decides whether master and detail sit side by side or stacked; the vertical " +
                    "variant needs enough available height.",
            ),
        )
    val oriented = createMasterDetailLayout {
        masterSize = "10rem"
        detailSize = "8rem"
        style.setProperty("height", "16rem")
    }
    oriented.append(
        box(tr("master", "master")),
        div().apply {
            slot = "detail"
            style.setProperty("padding", "var(--lumo-space-m)")
            textContent = tr("detail", "detail")
        },
    )
    val orientationReadout = Readout("orientation = horizontal")
    val orientationButton =
        createButton { textContent = tr("切换方向", "Toggle orientation") }.withTheme("tertiary").apply {
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
    orientationSection.body.append(example(null, column(oriented, row(orientationButton, orientationReadout.root))))
    view.body.append(orientationSection.root)

    val overlaySection =
        section(
            tr("浮层", "Overlay"),
            tr(
                "forceOverlay 强制详情以浮层显示，overlaySize 单独设置浮层尺寸，overlayContainment 决定浮层" +
                    "覆盖布局还是整个页面。",
                "forceOverlay always shows the detail as an overlay, overlaySize sets its size separately and " +
                    "overlayContainment decides whether it covers the layout or the whole page.",
            ),
        )
    val overlayLayout = createMasterDetailLayout {
        masterSize = "14rem"
        detailSize = "12rem"
        style.setProperty("height", "12rem")
    }
    overlayLayout.dataset["test"] = "master-detail-overlay"
    overlayLayout.append(
        box(tr("master", "master")),
        div().apply {
            slot = "detail"
            style.setProperty("padding", "var(--lumo-space-m)")
            textContent = tr("detail", "detail")
        },
    )
    val forceOverlaySwitch = vaadin.switch.createSwitch { label = "forceOverlay" }
    val fullOverlaySwitch = vaadin.switch.createSwitch { label = "overlaySize = 100%" }
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
    forceOverlaySwitch.listen(vaadin.switch.SwitchEvents.checkedChanged) {
        overlayLayout.forceOverlay = forceOverlaySwitch.checked
        overlayLayout.recalculateLayout()
    }
    fullOverlaySwitch.listen(vaadin.switch.SwitchEvents.checkedChanged) {
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
    overlaySection.body.append(
        example(
            tr("浮层只覆盖布局本身；切到 page 后会遮住整个页面。", "The overlay covers the layout; page containment covers the viewport."),
            column(overlayLayout, row(forceOverlaySwitch, fullOverlaySwitch, containmentSelect)),
        ),
    )
    view.body.append(overlaySection.root)

    val placeholderSection =
        section(
            tr("详情占位", "Detail placeholder"),
            tr(
                "没有选中项时，slot=\"detail-placeholder\" 的内容显示在详情区；详情出现后占位自动让位。",
                "While nothing is selected, the slot=\"detail-placeholder\" content fills the detail area and " +
                    "makes way once a detail is provided.",
            ),
        )
    val placeholderLayout = createMasterDetailLayout {
        masterSize = "14rem"
        style.setProperty("height", "12rem")
    }
    placeholderLayout.dataset["test"] = "master-detail-placeholder"
    val placeholderDetail = div().apply {
        slot = "detail"
        style.setProperty("padding", "var(--lumo-space-m)")
        textContent = tr("Ada Lovelace 的详细资料", "The details of Ada Lovelace")
    }
    var detailShown = false
    val detailToggle = createButton { textContent = tr("显示详情", "Show detail") }.withTheme("tertiary")
    detailToggle.clickEvent.addHandler {
        detailShown = !detailShown
        if (detailShown) placeholderLayout.append(placeholderDetail) else placeholderDetail.remove()
        detailToggle.textContent = if (detailShown) tr("隐藏详情", "Hide detail") else tr("显示详情", "Show detail")
        placeholderLayout.recalculateLayout()
    }
    placeholderLayout.append(
        div("master-list").apply {
            repeat(3) { index ->
                append(div("master-list__item").apply { textContent = tr("成员 ${index + 1}", "Member ${index + 1}") })
            }
        },
        div().apply {
            slot = "detail-placeholder"
            style.setProperty("padding", "var(--lumo-space-m)")
            textContent = tr("选择左侧成员查看详情", "Select a member to see the details")
        },
    )
    placeholderSection.body.append(example(null, column(placeholderLayout, detailToggle)))
    view.body.append(placeholderSection.root)

    val nestingSection =
        section(
            tr("嵌套", "Nesting"),
            tr(
                "详情区里可以再放一个主从布局，外层决定了内层何时退化成浮层。",
                "The detail area can host another master-detail layout; the outer one decides when the inner " +
                    "one falls back to an overlay.",
            ),
        )
    val inner = createMasterDetailLayout {
        masterSize = "8rem"
        style.setProperty("height", "100%")
    }
    inner.append(
        box(tr("二级主区", "Inner master")),
        div().apply {
            slot = "detail"
            style.setProperty("padding", "var(--lumo-space-s)")
            textContent = tr("二级详情", "Inner detail")
        },
    )
    val outer = createMasterDetailLayout {
        masterSize = "10rem"
        detailSize = "1fr"
        style.setProperty("height", "16rem")
    }
    outer.append(
        box(tr("一级主区", "Outer master")),
        div().apply { slot = "detail"; append(inner) },
    )
    nestingSection.body.append(example(null, outer))
    view.body.append(nestingSection.root)

    return view
}
