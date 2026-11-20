package demo.pages.display

import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.page.Blocks.row as kitRow
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleCaption
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import io.github.shijianjs.ktjs.vaadin.avatar.AvatarElement
import io.github.shijianjs.ktjs.vaadin.avatar.createAvatar
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.ui.Images
import demo.ui.PageFrame
import demo.ui.PageMeta

val avatarEntry: CatalogEntry =
    CatalogEntry(
        id = "avatar",
        group = Groups.DISPLAY,
        meta =
            PageMeta(
                title = bi("Avatar", "Avatar"),
                summary = bi("用户头像：缩写、图片、颜色索引与悬停提示。", "User avatars: initials, images, colour index and hover tooltip."),
                docsUrl = "$DOCS_BASE/avatar",
                sourcePath = "pages/display/AvatarPage.kt",
            ),
        kitPage = { AvatarPage.NAME() },
    )

private val english: AvatarTexts = AvatarTexts(
    basicHeading = "Basic usage",
    basicHint = "Shows the image when img is set, otherwise the abbreviation; name is the accessible name and " +
        "the tooltip text. The official example loads a person with getPeople(); the demo uses fixed local " +
        "people with the same portraits as the docs.",
    namingHeading = "Name and abbreviation",
    namingHint = "With only name the abbreviation is derived from it; abbr overrides it explicitly. " +
        "withTooltip shows name on hover or focus (i18n.anonymous when there is no name).",
    namingCaption = "Derived initials / explicit abbreviation",
    imageHeading = "Image",
    imageHint = "img accepts any image source; the official example shows a person photo and a company logo, " +
        "and the demo uses the same pictures copied from the official documentation.",
    sizesHeading = "Sizes",
    sizesHint = "The theme offers xlarge / large / small / xsmall; without a theme the default size is used.",
    coloursHeading = "Colour index",
    coloursHint = "colorIndex selects one of the seven Lumo user colours; the avatar group examples use the " +
        "same property.",
    user = { index -> "User $index" },
)

private val chinese: AvatarTexts = AvatarTexts(
    basicHeading = "基本用法",
    basicHint = "有 img 时显示图片，否则显示缩写；name 用作无障碍名称与提示文本。官方示例从后端的 " +
        "getPeople() 取人像，演示站使用固定的本地人物，照片与文档示例相同。",
    namingHeading = "姓名与缩写",
    namingHint = "只给 name 时缩写由姓名推导；abbr 显式覆盖。withTooltip 打开后，悬停或聚焦会显示 name " +
        "（没有名字时用 i18n.anonymous）。",
    namingCaption = "姓名推导缩写 / 显式缩写",
    imageHeading = "图片",
    imageHint = "img 可以是任意图片地址；官方示例展示人物照片与公司标志，demo 直接使用从官方文档复制来的同一组图片。",
    sizesHeading = "尺寸",
    sizesHint = "theme 提供 xlarge / large / small / xsmall 四个尺寸，未设置时使用默认尺寸。",
    coloursHeading = "颜色索引",
    coloursHint = "colorIndex 选择 Lumo 的用户色板，共 7 个颜色；avatar-group 的例子用同样的属性。",
    user = { index -> "用户 $index" },
)

internal class AvatarPage : DemoPage() {
    private val texts = Binder(avatarTexts)

    private val frame = PageFrame(avatarEntry)

    /** The colour row, kept so a language switch rewrites the names the tooltips read. */
    private val colourAvatars: List<AvatarElement> =
        (0..6).map { index ->
            createAvatar {
                name = english.user(index)
                abbr = "U$index"
                colorIndex = index.toDouble()
            }
        }

    init {
        frame.mount(this)

        // --- Basic usage (avatar/avatar-basic.ts) ---------------------------------------------------
        val basic = SectionBlock.NAME { show() }
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        val anonymous = createAvatar()
        anonymous.dataset["test"] = "avatar-anonymous"
        basic.body.append(
            exampleBox(
                null,
                kitRow(
                    anonymous,
                    createAvatar { name = "Ada Lovelace" },
                    createAvatar {
                        name = "Grace Hopper"
                        img = Images.portrait(3)
                    },
                ),
            ),
        )
        body.append(basic.root)

        // --- Name and abbreviation (avatar/avatar-name.ts, avatar/avatar-abbreviation.ts) -----------
        val naming = SectionBlock.NAME { show() }
        texts.bind(naming.heading) { it.namingHeading }
        texts.bind(naming.hint) { it.namingHint }
        val namingCaption = texts.bind(exampleCaption(), { it.namingCaption })
        naming.body.append(
            exampleBox(
                null,
                namingCaption,
                kitRow(
                    createAvatar {
                        name = "Augusta Ada King"
                        withTooltip = true
                    },
                    createAvatar {
                        name = "Augusta Ada King"
                        abbr = "AK"
                        withTooltip = true
                    },
                ),
            ),
        )
        body.append(naming.root)

        // --- Image (avatar/avatar-image.ts) ---------------------------------------------------------
        val image = SectionBlock.NAME { show() }
        texts.bind(image.heading) { it.imageHeading }
        texts.bind(image.hint) { it.imageHint }
        image.body.append(
            exampleBox(
                null,
                kitRow(
                    createAvatar {
                        name = "Ada Lovelace"
                        img = Images.portrait(1)
                    },
                    createAvatar {
                        name = "Company Inc."
                        img = Images.companyLogo
                    },
                ),
            ),
        )
        body.append(image.root)

        // --- Sizes (avatar/avatar-sizes.ts) ---------------------------------------------------------
        val sizes = SectionBlock.NAME { show() }
        texts.bind(sizes.heading) { it.sizesHeading }
        texts.bind(sizes.hint) { it.sizesHint }
        sizes.body.append(
            exampleBox(
                null,
                kitRow(
                    createAvatar { name = "Ada Lovelace" }.addTheme("xlarge"),
                    createAvatar { name = "Ada Lovelace" }.addTheme("large"),
                    createAvatar { name = "Ada Lovelace" },
                    createAvatar { name = "Ada Lovelace" }.addTheme("small"),
                    createAvatar { name = "Ada Lovelace" }.addTheme("xsmall"),
                ),
            ),
        )
        body.append(sizes.root)

        // --- Background colour (wrapper extra, see avatar-group-bg-color.ts) ------------------------
        val colours = SectionBlock.NAME { show() }
        texts.bind(colours.heading) { it.coloursHeading }
        texts.bind(colours.hint) { it.coloursHint }
        colours.body.append(exampleBox(null, kitRow(*colourAvatars.toTypedArray())))
        body.append(colours.root)

        applyTranslation(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = avatarEntry.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        val copy = texts.current
        colourAvatars.forEachIndexed { index, avatar -> avatar.name = copy.user(index) }
    }

    companion object : CustomElementReferenceAdapter<AvatarPage>(AvatarPage::class.js, "vaadin-demo-avatar-page") {
        private val avatarTexts: Translations<AvatarTexts> = texts(
            Langs.EN to english,
            Langs.ZH_HANS to chinese,
        )
    }
}
