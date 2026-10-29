package demo.pages.fields

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.tr
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.Readout
import demo.ui.column
import demo.ui.example
import demo.ui.page
import demo.ui.row
import demo.ui.section
import demo.ui.withTheme
import vaadin.button.createButton
import vaadin.fieldhighlighter.AiFieldMarkerConfidences
import vaadin.fieldhighlighter.FieldHighlighter
import vaadin.fieldhighlighter.createAiFieldMarker
import vaadin.fieldhighlighter.fieldHighlighterUser
import vaadin.textfield.createTextField
import web.dom.clickEvent
import web.events.addHandler

val fieldHighlighterEntry: CatalogEntry =
    CatalogEntry(
        id = "field-highlighter",
        group = Groups.FIELDS,
        meta =
            PageMeta(
                title = bi("Field Highlighter", "Field Highlighter"),
                summary =
                    bi(
                        "协作编辑指示：给字段加上其他用户正在编辑的彩色轮廓与用户标签。",
                        "Collaboration indicator: adds a coloured outline and a user tag for other people " +
                            "editing the field.",
                    ),
                docsUrl = DOCS_BASE,
                docsNote =
                    bi(
                        "Field Highlighter 是内部协作组件，官方文档没有独立页面。",
                        "Field Highlighter is an internal collaboration component without a dedicated " +
                            "documentation page.",
                    ),
                sourcePath = "pages/fields/FieldHighlighterPage.kt",
            ),
        builder = ::buildFieldHighlighterPage,
    )

private fun buildFieldHighlighterPage(meta: PageMeta): PageView {
    val view = page(meta)

    val collab =
        section(
            tr("协作用户指示", "Collaboration indicators"),
            tr(
                "FieldHighlighter.addUser 让字段显示其他用户正在编辑；焦点进入字段时还会出现用户标签。",
                "FieldHighlighter.addUser marks the field as edited by another user; focusing the field also " +
                    "shows a user tag.",
            ),
        )
    val field =
        createTextField {
            label = tr("共享标题", "Shared title")
            value = tr("季度报告草稿", "Quarterly report draft")
            helperText = tr("下面的按钮模拟其他用户开始 / 结束编辑", "The buttons below simulate other users joining and leaving")
        }
    field.dataset["test"] = "highlighted-field"
    val alice = fieldHighlighterUser(1.0, "Alice", 0.0)
    val bob = fieldHighlighterUser(2.0, "Bob", 1.0)
    val readout = Readout(tr("当前没有其他用户", "No other users right now"))

    val addButton =
        createButton { textContent = tr("Alice 开始编辑", "Alice starts editing") }.apply {
            clickEvent.addHandler {
                FieldHighlighter.addUser(field, alice)
                readout.set(tr("正在编辑：", "Editing: "), "Alice")
            }
        }
    val addBobButton =
        createButton { textContent = tr("Bob 开始编辑", "Bob starts editing") }.withTheme("tertiary").apply {
            clickEvent.addHandler {
                FieldHighlighter.addUser(field, bob)
                readout.set(tr("正在编辑：", "Editing: "), tr("Alice、Bob", "Alice, Bob"))
            }
        }
    val removeButton =
        createButton { textContent = tr("全部离开", "Everyone leaves") }.withTheme("tertiary").apply {
            clickEvent.addHandler {
                FieldHighlighter.removeUser(field, alice)
                FieldHighlighter.removeUser(field, bob)
                readout.set(tr("正在编辑：", "Editing: "), tr("无", "Nobody"))
            }
        }

    collab.body.append(example(null, column(field, row(addButton, addBobButton, removeButton), readout.root)))
    view.body.append(collab.root)

    val ai =
        section(
            tr("AI 标记", "AI marker"),
            tr(
                "ai-field-marker 是字段的直接子节点：working 时显示进度，confidence 显示置信度。",
                "ai-field-marker is a direct child of the field: working shows progress and confidence shows " +
                    "the confidence level.",
            ),
        )
    val aiField =
        createTextField {
            label = tr("AI 填充的字段", "AI-filled field")
            value = tr("自动摘要……", "Automatic summary…")
        }
    val marker = createAiFieldMarker { working = true }
    marker.confidence = AiFieldMarkerConfidences.high
    aiField.append(marker)

    val toggleButton =
        createButton { textContent = tr("结束 AI 生成", "Finish AI generation") }.apply {
            clickEvent.addHandler {
                marker.working = !marker.working
                textContent =
                    if (marker.working) {
                        tr("结束 AI 生成", "Finish AI generation")
                    } else {
                        tr("重新开始生成", "Restart generation")
                    }
            }
        }
    ai.body.append(example(null, column(aiField, toggleButton)))
    view.body.append(ai.root)

    return view
}
