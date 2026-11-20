package demo.pages.layout

import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts

/** Every string the card page and its card elements show, in one language. */
internal class CardTexts(
    val contentHeading: String,
    val contentHint: String,
    val titlesHeading: String,
    val titlesHint: String,
    val headerHeading: String,
    val headerHint: String,
    val mediaHeading: String,
    val mediaHint: String,
    val footerHeading: String,
    val footerHint: String,
    val variantsHeading: String,
    val variantsHint: String,
    val combinedHeading: String,
    val combinedHint: String,
    val playgroundHeading: String,
    val playgroundHint: String,
    val lapland: String,
    val exoticNorth: String,
    val arctic: String,
    val bookVacation: String,
    val learnMore: String,
    val defaultCardText: String,
    val outlinedCardText: String,
    val elevatedCardText: String,
    val shortText: String,
    val longText: String,
    val samiText: String,
    val slotsLabel: String,
    val mediaLabel: String,
    val contentLabel: String,
    val themeLabel: String,
    val mediaSlot: String,
    val contentSlot: String,
)

private val english: CardTexts =
    CardTexts(
        contentHeading = "Content",
        contentHint = "Nodes in the default slot form the card content; any DOM fragment is allowed.",
        titlesHeading = "Title and subtitle",
        titlesHint = "The title / subtitle slots take custom heading nodes, while the cardTitle property " +
            "generates the heading itself with titleHeadingLevel as its level.",
        headerHeading = "Header area",
        headerHint = "The header slot replaces the default title row; header-prefix / header-suffix place " +
            "one node on each side, typically an avatar and a badge.",
        mediaHeading = "Media",
        mediaHint = "The media slot takes an image, an icon or an avatar; the card sizes it according to the " +
            "active variants.",
        footerHeading = "Footer",
        footerHint = "Buttons in the footer slot stay at the bottom of the card, which pays off with a " +
            "fixed height.",
        variantsHeading = "Style variants",
        variantsHint = "Beyond the default style, outlined adds a border and elevated adds a shadow that " +
            "works best on a shaded background.",
        combinedHeading = "Combining variants",
        combinedHint = "horizontal puts media next to the content, stretch-media stretches the media into " +
            "the remaining space and cover-media makes it cover the top of the card. Themes can be combined.",
        playgroundHeading = "Interactive configuration",
        playgroundHint = "Ticking slots and style variants, or switching the media and content, updates the " +
            "preview in place: nodes are only detached and reattached, the card is never rebuilt.",
        lapland = "Lapland",
        exoticNorth = "The Exotic North",
        arctic = "Arctic",
        bookVacation = "Book Vacation",
        learnMore = "Learn More",
        defaultCardText = "This is the default card style.",
        outlinedCardText = "Adds a solid outline around the card.",
        elevatedCardText = "This variant works better on a shaded background.",
        shortText = "Lapland is the northern-most region of Finland and an active outdoor destination.",
        longText = "Lapland is the northern-most region of Finland and an active outdoor destination that's " +
            "known for its incredible, year-round light phenomena, vast arctic nature, and Santa Claus.",
        samiText = "The land of the indigenous Sámi people, known as Sámi homeland or Sápmi, also crosses " +
            "the northern part of the region. Explore Finnish Lapland, Europe's last great wilderness!",
        slotsLabel = "Slots",
        mediaLabel = "Media",
        contentLabel = "Content",
        themeLabel = "Style variants",
        mediaSlot = "Media",
        contentSlot = "Content",
    )

private val chinese: CardTexts =
    CardTexts(
        contentHeading = "内容",
        contentHint = "默认插槽里的节点组成卡片正文，可以是任意 DOM 片段。",
        titlesHeading = "标题与副标题",
        titlesHint = "title / subtitle 插槽放自定义的标题节点；cardTitle 属性则直接生成标题元素，标题层级由 " +
            "titleHeadingLevel 决定。",
        headerHeading = "头部区域",
        headerHint = "header 插槽替换默认的标题行，header-prefix / header-suffix 在标题两侧各放一个节点，" +
            "常见的是头像与徽标。",
        mediaHeading = "媒体",
        mediaHint = "media 插槽放图片、图标或头像；宽高由卡片根据当前变体决定。",
        footerHeading = "底部操作",
        footerHint = "footer 插槽里的按钮固定在卡片底部，配合固定高度的卡片使用。",
        variantsHeading = "样式变体",
        variantsHint = "默认样式之外，outlined 加边框，elevated 加阴影（更适合有底色的背景）。",
        combinedHeading = "组合变体",
        combinedHint = "horizontal 让媒体与内容并排，stretch-media 拉伸媒体填满剩余空间，cover-media 让媒体铺满卡片" +
            "上方。多个 theme 可以叠加。",
        playgroundHeading = "交互式配置",
        playgroundHint = "勾选插槽与样式变体、切换媒体与正文内容，预览会就地更新：节点只被摘下来或重新挂回去，" +
            "不会重建整张卡片。",
        lapland = "拉普兰",
        exoticNorth = "异域北方",
        arctic = "北极圈",
        bookVacation = "预订假期",
        learnMore = "了解更多",
        defaultCardText = "默认卡片样式。",
        outlinedCardText = "在卡片四周加一圈实线边框。",
        elevatedCardText = "适合有底色背景的阴影样式。",
        shortText = "拉普兰是芬兰最北端的地区，也是活跃的户外目的地。",
        longText = "拉普兰是芬兰最北端的地区，也是活跃的户外目的地：这里一年四季都有神奇的光现象、" +
            "广袤的北极自然与圣诞老人。",
        samiText = "萨米人的家园——萨米或萨普米——也横跨该地区的北部。来探索芬兰拉普兰这片欧洲最后的荒野吧！",
        slotsLabel = "插槽",
        mediaLabel = "媒体内容",
        contentLabel = "正文内容",
        themeLabel = "样式变体",
        mediaSlot = "媒体",
        contentSlot = "内容",
    )

private val cardTranslations: Translations<CardTexts> = texts(
    Langs.EN to english,
    Langs.ZH_HANS to chinese,
)

/** The text table of the card page; every card element shares this one table. */
internal val cardTexts: Binder<CardTexts> = Binder(cardTranslations)
