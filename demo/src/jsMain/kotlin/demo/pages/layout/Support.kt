package demo.pages.layout

import io.github.shijianjs.ktjs.dom.element.invoke
import js.objects.unsafeJso
import demo.tr
import io.github.shijianjs.ktjs.vaadin.accordion.createAccordionPanel
import io.github.shijianjs.ktjs.vaadin.checkbox.createCheckbox
import io.github.shijianjs.ktjs.vaadin.emailfield.createEmailField
import io.github.shijianjs.ktjs.vaadin.select.SelectItemData
import web.html.HTMLElement


internal fun playgroundCheckbox(value: String, label: String) = createCheckbox { this.value = value; this.label = label }

internal fun selectOptions(vararg entries: String): Array<SelectItemData> =
    entries
        .mapIndexed { index, label ->
            unsafeJso<SelectItemData> {
                this.label = label
                value = index.toString()
            }
        }.toTypedArray()



internal fun emailField(label: String): HTMLElement = createEmailField { this.label = label }

/** An accordion panel with a summary and one content child. */
internal fun accordionPanel(summary: String): io.github.shijianjs.ktjs.vaadin.accordion.AccordionPanelElement =
    createAccordionPanel { this.summary = summary }

internal fun laplandShortText(): String =
    tr("拉普兰是芬兰最北端的地区，也是活跃的户外目的地。", "Lapland is the northern-most region of Finland and an active outdoor destination.")

internal fun laplandLongText(): String =
    tr(
        "拉普兰是芬兰最北端的地区，也是活跃的户外目的地：这里一年四季都有神奇的光现象、广袤的北极自然" +
            "与圣诞老人。",
        "Lapland is the northern-most region of Finland and an active outdoor destination that's known for its " +
            "incredible, year-round light phenomena, vast arctic nature, and Santa Claus.",
    )

internal fun laplandSamiText(): String =
    tr(
        "萨米人的家园——萨米或萨普米——也横跨该地区的北部。来探索芬兰拉普兰这片欧洲最后的荒野吧！",
        "The land of the indigenous Sámi people, known as Sámi homeland or Sápmi, also crosses the northern part " +
            "of the region. Explore Finnish Lapland, Europe's last great wilderness!",
    )
