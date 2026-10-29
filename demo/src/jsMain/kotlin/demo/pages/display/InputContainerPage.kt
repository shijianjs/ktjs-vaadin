package demo.pages.display

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.pages.overlays.ariaLabel
import demo.tr
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.div
import demo.ui.element
import demo.ui.example
import demo.ui.page
import demo.ui.paragraph
import demo.ui.row
import demo.ui.section
import vaadin.icon.createIcon
import vaadin.inputcontainer.createInputContainer
import web.html.HTMLElement
import web.html.HtmlTagName

val inputContainerEntry: CatalogEntry =
    CatalogEntry(
        id = "input-container",
        group = Groups.DISPLAY,
        meta =
            PageMeta(
                title = bi("Input Container", "Input Container"),
                summary =
                    bi(
                        "输入类字段的底层容器：前缀 / 后缀插槽、invalid 与 readonly 状态。",
                        "Low-level container for input fields: prefix/suffix slots plus invalid and readonly states.",
                    ),
                docsUrl = "$DOCS_BASE/text-field",
                docsNote =
                    bi(
                        "vaadin-input-container 是内部基础元素，官方文档见 Text Field。",
                        "vaadin-input-container is an internal building block; the official documentation is under Text Field.",
                    ),
                sourcePath = "pages/display/InputContainerPage.kt",
            ),
        builder = ::buildInputContainerPage,
    )

// ---------------------------------------------------------------------------------------------
// Input container (internal element, documented under Text Field)
// ---------------------------------------------------------------------------------------------

private fun buildInputContainerPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "容器只负责布局与状态，输入元素由使用方提供；prefix / suffix 插槽放图标或按钮。",
                "The container only handles layout and state; the input element is supplied by the caller. " +
                    "The prefix/suffix slots hold icons or buttons.",
            ),
        )
    val container = createInputContainer()
    container.dataset["test"] = "input-container"
    val input = element(HtmlTagName.input)
    input.style.setProperty("border", "none")
    input.style.setProperty("outline", "none")
    input.style.setProperty("background", "transparent")
    input.style.setProperty("font", "inherit")
    input.style.setProperty("color", "inherit")
    input.style.setProperty("width", "100%")
    container.append(createIcon { icon = "lumo:search" }.apply { slot = "prefix" }, input)
    container.append(createIcon { icon = "lumo:checkmark" }.apply { slot = "suffix" })
    basic.body.append(example(null, container))
    view.body.append(basic.root)

    val states =
        section(
            tr("状态", "States"),
            tr(
                "readonly / invalid / disabled 三个属性直接对应视觉状态。",
                "The readonly / invalid / disabled properties map directly to visual states.",
            ),
        )
    states.body.append(
        example(
            null,
            row(
                plainContainer(tr("只读", "Read-only"), readonly = true),
                plainContainer(tr("无效", "Invalid"), invalid = true),
                plainContainer(tr("禁用", "Disabled"), disabled = true),
            ),
        ),
    )
    view.body.append(states.root)

    return view
}

/** Builds an input container with a plain text input inside, for the state examples. */
private fun plainContainer(label: String, readonly: Boolean = false, invalid: Boolean = false, disabled: Boolean = false): HTMLElement {
    val container = createInputContainer()
    container.readonly = readonly
    container.invalid = invalid
    container.disabled = disabled
    val input = element(HtmlTagName.input)
    input.value = label
    input.ariaLabel = label
    input.style.setProperty("border", "none")
    input.style.setProperty("outline", "none")
    input.style.setProperty("background", "transparent")
    input.style.setProperty("font", "inherit")
    input.style.setProperty("color", "inherit")
    input.style.setProperty("width", "100%")
    container.append(input)
    return div().apply {
        style.setProperty("width", "12rem")
        append(container, paragraph(label, "small muted"))
    }
}
