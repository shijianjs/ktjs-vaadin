package demo.pages.display

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.tr
import demo.ui.Images
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.example
import demo.ui.page
import demo.ui.row
import demo.ui.section
import demo.ui.withTheme
import vaadin.avatar.createAvatar

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
        builder = ::buildAvatarPage,
    )

private fun buildAvatarPage(meta: PageMeta): PageView {
    val view = page(meta)

    // --- Basic usage (avatar/avatar-basic.ts) ---------------------------------------------------
    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "有 img 时显示图片，否则显示缩写；name 用作无障碍名称与提示文本。官方示例从后端的 " +
                    "getPeople() 取人像，演示站使用固定的本地人物，照片与文档示例相同。",
                "Shows the image when img is set, otherwise the abbreviation; name is the accessible name and " +
                    "the tooltip text. The official example loads a person with getPeople(); the demo uses " +
                    "fixed local people with the same portraits as the docs.",
            ),
        )
    val anonymous = createAvatar()
    anonymous.dataset["test"] = "avatar-anonymous"
    basic.body.append(
        example(
            null,
            row(
                anonymous,
                createAvatar { name = "Ada Lovelace" },
                createAvatar {
                    name = "Grace Hopper"
                    img = Images.portrait(3)
                },
            ),
        ),
    )
    view.body.append(basic.root)

    // --- Name and abbreviation (avatar/avatar-name.ts, avatar/avatar-abbreviation.ts) -----------
    val naming =
        section(
            tr("姓名与缩写", "Name and abbreviation"),
            tr(
                "只给 name 时缩写由姓名推导；abbr 显式覆盖。withTooltip 打开后，悬停或聚焦会显示 name " +
                    "（没有名字时用 i18n.anonymous）。",
                "With only name the abbreviation is derived from it; abbr overrides it explicitly. " +
                    "withTooltip shows name on hover or focus (i18n.anonymous when there is no name).",
            ),
        )
    naming.body.append(
        example(
            tr("姓名推导缩写 / 显式缩写", "Derived initials / explicit abbreviation"),
            row(
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
    view.body.append(naming.root)

    // --- Image (avatar/avatar-image.ts) ---------------------------------------------------------
    val image =
        section(
            tr("图片", "Image"),
            tr(
                "img 可以是任意图片地址；官方示例展示人物照片与公司标志，demo 直接使用从官方文档复制来的同一组图片。",
                "img accepts any image source; the official example shows a person photo and a company logo, " +
                    "and the demo uses the same pictures copied from the official documentation.",
            ),
        )
    image.body.append(
        example(
            null,
            row(
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
    view.body.append(image.root)

    // --- Sizes (avatar/avatar-sizes.ts) ---------------------------------------------------------
    val sizes =
        section(
            tr("尺寸", "Sizes"),
            tr(
                "theme 提供 xlarge / large / small / xsmall 四个尺寸，未设置时使用默认尺寸。",
                "The theme offers xlarge / large / small / xsmall; without a theme the default size is used.",
            ),
        )
    sizes.body.append(
        example(
            null,
            row(
                createAvatar { name = "Ada Lovelace" }.withTheme("xlarge"),
                createAvatar { name = "Ada Lovelace" }.withTheme("large"),
                createAvatar { name = "Ada Lovelace" },
                createAvatar { name = "Ada Lovelace" }.withTheme("small"),
                createAvatar { name = "Ada Lovelace" }.withTheme("xsmall"),
            ),
        ),
    )
    view.body.append(sizes.root)

    // --- Background colour (wrapper extra, see avatar-group-bg-color.ts) ------------------------
    val colours =
        section(
            tr("颜色索引", "Colour index"),
            tr(
                "colorIndex 选择 Lumo 的用户色板，共 7 个颜色；avatar-group 的例子用同样的属性。",
                "colorIndex selects one of the seven Lumo user colours; the avatar group examples use the " +
                    "same property.",
            ),
        )
    colours.body.append(
        example(
            null,
            row(*(0..6).map { index ->
                createAvatar {
                    name = tr("用户 $index", "User $index")
                    abbr = "U$index"
                    colorIndex = index.toDouble()
                }
            }.toTypedArray()),
        ),
    )
    view.body.append(colours.root)

    return view
}
