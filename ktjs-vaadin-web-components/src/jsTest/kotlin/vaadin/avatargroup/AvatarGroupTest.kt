package vaadin.avatargroup

import kotlin.js.JsAny
import kotlin.js.Promise
import kotlin.js.js
import kotlin.js.unsafeCast
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.click
import vaadin.detachAll
import vaadin.fixture
import vaadin.keyDown
import vaadin.nextFrame
import vaadin.nextRender
import vaadin.nextResize
import vaadin.nextUpdate
import vaadin.on
import vaadin.timeout
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/avatar-group/test/avatar-group.test.js`.
 *
 * The upstream suite reads the private `_avatars`, `_overflow`, `_menuElement` and `$.overlay`
 * members; the same nodes are reachable through the public DOM (`vaadin-avatar` children, the
 * `slot="overflow"` avatar, the slotted `vaadin-avatar-group-menu` and the overlay in the shadow
 * root), so no private state is touched here. The announcement cases use real timers instead of
 * sinon fake timers.
 */
class AvatarGroupTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerAvatarGroup()
        vaadin.avatar.registerAvatar()
        vaadin.tooltip.registerTooltip()
        vaadin.overlay.registerOverlay()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newGroup(): AvatarGroupElement = attach(createAvatarGroup())

    private fun items(json: String): Array<AvatarGroupItem> =
        js("(json) => JSON.parse(json)")(json).unsafeCast<Array<AvatarGroupItem>>()

    private fun avatars(group: AvatarGroupElement): Array<HTMLElement> =
        js("(group) => Array.from(group.children).filter((node) => node.localName === 'vaadin-avatar')")(group)
            as Array<HTMLElement>

    private fun overflow(group: AvatarGroupElement): HTMLElement =
        assertNotNull(js("(group) => group.querySelector('vaadin-avatar[slot=overflow]')")(group) as HTMLElement?)

    private fun menu(group: AvatarGroupElement): HTMLElement =
        assertNotNull(
            js("(group) => group.querySelector('vaadin-avatar-group-menu')")(group) as HTMLElement?,
        )

    private fun overlay(group: AvatarGroupElement): HTMLElement =
        assertNotNull(js("(group) => group.shadowRoot.querySelector('vaadin-overlay')")(group) as HTMLElement?)

    private fun abbrOf(element: HTMLElement): String = js("(element) => element.abbr")(element) as String

    private fun isHidden(element: HTMLElement): Boolean = js("(element) => element.hasAttribute('hidden')")(element) as Boolean

    private fun isOverlayOpened(group: AvatarGroupElement): Boolean =
        js("(group) => group.shadowRoot.querySelector('vaadin-overlay').opened")(group) as Boolean

    private val fourItems = "[{ abbr: 'PM' }, { name: 'Yuriy Yevstihnyeyev' }, { abbr: 'SK' }, { name: 'Jens Jansson' }]"

    // custom element definition

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerAvatarGroup()
        val group = newGroup()

        assertEquals("vaadin-avatar-group", group.tagName.lowercase())
        assertEquals("vaadin-avatar-group", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun hasAValidStaticIsGetter() {
        val ctor = registerAvatarGroup()

        assertEquals("vaadin-avatar-group", js("(ctor) => ctor.is")(ctor) as String)
    }

    // items property

    @Test
    fun rendersAnAvatarPerItemPlusOverflow(): Promise<Unit> {
        val group = newGroup()

        return nextRender(group)
            .then {
                group.items = items(fourItems)
                nextRender(group)
            }
            .then { assertEquals(assertNotNull(group.items).size + 1, avatars(group).size) }
    }

    @Test
    fun propagatesTheThemeToAllAvatars(): Promise<Unit> {
        val group = newGroup()

        return nextRender(group)
            .then {
                group.items = items(fourItems)
                group.setAttribute("theme", "small")
                nextFrame()
            }
            .then {
                avatars(group).forEach { avatar -> assertEquals("small", avatar.getAttribute("theme")) }
            }
    }

    @Test
    fun appendsAvatarsForItemsAddedAtTheEnd(): Promise<Unit> {
        val group = newGroup()

        return nextRender(group)
            .then {
                group.items = items(fourItems)
                nextRender(group)
            }
            .then {
                group.items = items("[{ name: 'Yuriy Yevstihnyeyev' }, { abbr: 'SK' }, { abbr: 'GG' }]")
                nextRender(group)
            }
            .then { assertEquals("GG", abbrOf(avatars(group)[2])) }
    }

    @Test
    fun insertsAvatarsForItemsAddedInTheMiddle(): Promise<Unit> {
        val group = newGroup()

        return nextRender(group)
            .then {
                group.items = items(fourItems)
                nextRender(group)
            }
            .then {
                group.items = items("[{ name: 'Yuriy Yevstihnyeyev' }, { abbr: 'GG' }, { abbr: 'SK' }]")
                nextRender(group)
            }
            .then { assertEquals("GG", abbrOf(avatars(group)[1])) }
    }

    @Test
    fun reusesTheExistingAvatarWhenUpdatingItems(): Promise<Unit> {
        val group = newGroup()

        return nextRender(group)
            .then {
                group.items = items(fourItems)
                nextRender(group)
            }
            .then {
                val first = avatars(group)[0]
                group.items = items("[{ name: 'Yuriy Yevstihnyeyev' }, { abbr: 'SK' }]")
                nextRender(group).then { first }
            }
            .then { first ->
                val rendered = avatars(group)
                assertEquals(3, rendered.size)
                assertEquals(true, js("(a, b) => a === b")(rendered[0], first) as Boolean)
                assertEquals(false, js("(a, b) => a === b")(rendered[1], first) as Boolean)
            }
    }

    // maxItemsVisible

    private fun groupWithLimit(limit: Double): Promise<AvatarGroupElement> {
        val group = newGroup()

        return nextRender(group)
            .then {
                group.items = items(
                    "[{ abbr: 'PM' }, { name: 'Yuriy Yevstihnyeyev' }, { name: 'Serhii Kulykov' }, { abbr: 'JJ' }, {}]",
                )
                group.maxItemsVisible = limit
                nextRender(group)
            }
            .then { group }
    }

    @Test
    fun rendersAvatarsBasedOnMaxItemsVisibleIncludingOverflow(): Promise<Unit> =
        groupWithLimit(3.0).then { group -> assertEquals(3, avatars(group).size) }

    @Test
    fun setsTheAbbreviationOfTheOverflowAvatar(): Promise<Unit> =
        groupWithLimit(3.0).then { group -> assertEquals("+3", abbrOf(overflow(group))) }

    @Test
    fun setsTheTooltipGeneratorOfTheOverflowAvatar(): Promise<Unit> = groupWithLimit(3.0).then { group ->
        val tooltip = assertNotNull(js("(overflow) => overflow.querySelector('vaadin-tooltip')")(overflow(group)) as HTMLElement?)
        assertEquals("Serhii Kulykov\nJJ\nanonymous", js("(tooltip) => tooltip.textContent")(tooltip) as String)
    }

    @Test
    fun showsTheOverflowAvatar(): Promise<Unit> =
        groupWithLimit(3.0).then { group -> assertFalse(isHidden(overflow(group))) }

    @Test
    fun showsAtLeastTwoAvatarsWhenTheLimitIsBelowTwo(): Promise<Unit> = groupWithLimit(3.0)
        .then { group ->
            group.maxItemsVisible = 1.0
            nextRender(group).then { group }
        }
        .then { group -> assertEquals(2, avatars(group).size) }

    @Test
    fun setsTheOverflowAbbreviationWhenTheLimitIsBelowTwo(): Promise<Unit> = groupWithLimit(3.0)
        .then { group ->
            group.maxItemsVisible = 1.0
            nextRender(group).then { group }
        }
        .then { group -> assertEquals("+4", abbrOf(overflow(group))) }

    @Test
    fun showsTwoAvatarsWhenTheLimitIsBelowTwo(): Promise<Unit> = groupWithLimit(3.0)
        .then { group ->
            group.items = items("[{ abbr: 'PM' }, { name: 'Yuriy Yevstihnyeyev' }]")
            group.maxItemsVisible = 1.0
            nextRender(group).then { group }
        }
        .then { group ->
            val visible = avatars(group).filter { !isHidden(it) }
            assertEquals(2, visible.size)
        }

    @Test
    fun hidesTheOverflowAvatarWhenTheLimitIsNull(): Promise<Unit> = groupWithLimit(3.0)
        .then { group ->
            js("(group) => { group.maxItemsVisible = null; }")(group)
            nextRender(group).then { group }
        }
        .then { group -> assertTrue(isHidden(overflow(group))) }

    @Test
    fun hidesTheOverflowAvatarWhenTheLimitIsUndefined(): Promise<Unit> = groupWithLimit(3.0)
        .then { group ->
            js("(group) => { group.maxItemsVisible = undefined; }")(group)
            nextRender(group).then { group }
        }
        .then { group -> assertTrue(isHidden(overflow(group))) }

    @Test
    fun hidesTheOverflowAvatarWhenTheItemsChange(): Promise<Unit> = groupWithLimit(3.0)
        .then { group ->
            group.maxItemsVisible = 2.0
            nextRender(group).then { group }
        }
        .then { group ->
            group.items = items("[{ abbr: 'PM' }, { name: 'Yuriy Yevstihnyeyev' }]")
            nextRender(group).then { group }
        }
        .then { group -> assertTrue(isHidden(overflow(group))) }

    @Test
    fun showsOnlyMaxItemsVisibleWhenSettingItemsAfterBeingEmpty(): Promise<Unit> = groupWithLimit(3.0)
        .then { group ->
            val items = assertNotNull(group.items)
            group.items = items("[]")
            group.items = items
            nextRender(group).then { group }
        }
        .then { group -> assertEquals(3, avatars(group).size) }

    // width is set

    @Test
    fun considersTheElementWidthWhenTheLimitIsSet(): Promise<Unit> = groupWithLimit(3.0)
        .then { group ->
            group.maxItemsVisible = 4.0
            nextRender(group).then { group }
        }
        .then { group ->
            group.style.setProperty("width", "100px")
            nextResize(group).then { group }
        }
        .then { group -> assertEquals(3, avatars(group).size) }

    @Test
    fun setsTheOverflowAbbreviationWhenTheLimitAndWidthAreSet(): Promise<Unit> = groupWithLimit(3.0)
        .then { group ->
            group.maxItemsVisible = 4.0
            nextRender(group).then { group }
        }
        .then { group ->
            group.style.setProperty("width", "100px")
            nextResize(group).then { group }
        }
        .then { group -> assertEquals("+3", abbrOf(overflow(group))) }

    // auto overflow

    private fun autoOverflowGroup(): Promise<AvatarGroupElement> {
        val group = newGroup()

        return nextRender(group)
            .then {
                group.items = items("[{ abbr: '01' }, { abbr: '02' }, { abbr: '03' }, { abbr: '04' }, { abbr: '05' }]")
                nextRender(group)
            }
            .then { group }
    }

    @Test
    fun rendersAvatarsToFitTheWidthOnResize(): Promise<Unit> = autoOverflowGroup()
        .then { group ->
            group.style.setProperty("width", "110px")
            nextResize(group).then { group }
        }
        .then { group ->
            assertEquals(3, avatars(group).size)
            assertEquals("+3", abbrOf(overflow(group)))
        }

    @Test
    fun alwaysShowsAtLeastTwoAvatars(): Promise<Unit> = autoOverflowGroup()
        .then { group ->
            group.items = items("[{ abbr: '01' }, { abbr: '02' }]")
            group.style.setProperty("width", "50px")
            nextResize(group).then { group }
        }
        .then { group -> assertEquals(2, avatars(group).filter { !isHidden(it) }.size) }

    @Test
    fun doesNotShowTheOverflowWithOnlyOneAvatar(): Promise<Unit> = autoOverflowGroup()
        .then { group ->
            group.style.setProperty("width", "170px")
            nextResize(group).then { group }
        }
        .then { group -> assertTrue(isHidden(overflow(group))) }

    @Test
    fun reRendersTheAvatarsOnItemsChange(): Promise<Unit> = autoOverflowGroup()
        .then { group ->
            group.style.setProperty("width", "110px")
            group.items = items("[{ abbr: '01' }, { abbr: '02' }]")
            nextResize(group).then { group }
        }
        .then { group -> assertTrue(isHidden(overflow(group))) }

    // overlay

    private fun overlayGroup(): Promise<AvatarGroupElement> {
        val group = newGroup()

        return nextRender(group)
            .then {
                group.items = items(fourItems)
                group.maxItemsVisible = 2.0
                nextRender(group).then { group }
            }
            .then { group }
    }

    @Test
    fun setsTheOwnerOfTheOverlay(): Promise<Unit> = overlayGroup().then { group ->
        assertEquals(true, js("(group) => group.shadowRoot.querySelector('vaadin-overlay').owner === group")(group) as Boolean)
    }

    @Test
    fun opensTheOverlayOnOverflowClick(): Promise<Unit> = overlayGroup().then { group ->
        click(overflow(group))
        assertTrue(isOverlayOpened(group))
    }

    @Test
    fun opensTheOverlayOnOverflowEnter(): Promise<Unit> = overlayGroup().then { group ->
        keyDown(overflow(group), "Enter")
        assertTrue(isOverlayOpened(group))
    }

    @Test
    fun opensTheOverlayOnOverflowSpace(): Promise<Unit> = overlayGroup().then { group ->
        keyDown(overflow(group), " ")
        assertTrue(isOverlayOpened(group))
    }

    @Test
    fun closesTheOverlayOnAvatarGroupDetach(): Promise<Unit> = overlayGroup()
        .then { group ->
            click(overflow(group))
            nextFrame().then { group }
        }
        .then { group ->
            assertTrue(isOverlayOpened(group))
            group.remove()
            assertFalse(isOverlayOpened(group))
        }

    @Test
    fun closesTheOverlayOnSubsequentOverflowClick(): Promise<Unit> = overlayGroup()
        .then { group ->
            click(overflow(group))
            nextFrame().then { group }
        }
        .then { group ->
            click(overflow(group))
            nextRender(group).then { group }
        }
        .then { group -> assertFalse(isOverlayOpened(group)) }

    @Test
    fun closesTheOverlayOnMenuEsc(): Promise<Unit> = overlayGroup()
        .then { group ->
            click(overflow(group))
            nextFrame().then { group }
        }
        .then { group ->
            keyDown(menu(group), "Escape")
            nextRender(group).then { group }
        }
        .then { group -> assertFalse(isOverlayOpened(group)) }

    @Test
    fun closesTheOverlayOnMenuTab(): Promise<Unit> = overlayGroup()
        .then { group ->
            click(overflow(group))
            nextFrame().then { group }
        }
        .then { group ->
            keyDown(menu(group), "Tab")
            nextRender(group).then { group }
        }
        .then { group -> assertFalse(isOverlayOpened(group)) }

    @Test
    fun togglesAriaExpandedOnTheOverflowAvatar(): Promise<Unit> = overlayGroup()
        .then { group ->
            click(overflow(group))
            nextFrame().then { group }
        }
        .then { group ->
            assertEquals("true", overflow(group).getAttribute("aria-expanded"))
            click(overflow(group))
            nextRender(group).then { group }
        }
        .then { group -> assertEquals("false", overflow(group).getAttribute("aria-expanded")) }

    @Test
    fun doesNotCreateTooltipsForTheOverlayAvatars(): Promise<Unit> = overlayGroup()
        .then { group ->
            click(overflow(group))
            nextFrame().then { group }
        }
        .then { group ->
            val overlayAvatars = js("(group) => Array.from(group.shadowRoot.querySelector('vaadin-overlay').querySelectorAll('vaadin-avatar'))")(
                group,
            ) as Array<HTMLElement>
            overlayAvatars.forEach { avatar ->
                assertEquals(false, js("(avatar) => avatar.withTooltip")(avatar) as Boolean)
                assertEquals(
                    null,
                    js("(avatar) => avatar.querySelector('vaadin-tooltip')")(avatar),
                )
            }
        }

    // color index

    @Test
    fun passesTheColorIndexToTheAvatars(): Promise<Unit> {
        val group = newGroup()

        return nextRender(group)
            .then {
                group.items = items(
                    "[{ abbr: 'PM', colorIndex: 0 }, { name: 'Yuriy Yevstihnyeyev', colorIndex: 1 }," +
                        " { name: 'Serhii Kulykov', colorIndex: 2 }]",
                )
                nextRender(group).then { group }
            }
            .then {
                assertEquals(0.0, js("(avatar) => avatar.colorIndex")(avatars(group)[0]) as Double)
                assertEquals(1.0, js("(avatar) => avatar.colorIndex")(avatars(group)[1]) as Double)
                assertEquals(2.0, js("(avatar) => avatar.colorIndex")(avatars(group)[2]) as Double)
            }
    }

    // className

    @Test
    fun passesTheClassNameToTheAvatars(): Promise<Unit> {
        val group = newGroup()

        return nextRender(group)
            .then {
                group.items = items(
                    "[{ abbr: 'PM', className: 'foo' }, { name: 'Yuriy Yevstihnyeyev', className: 'bar' }," +
                        " { name: 'Serhii Kulykov', className: 'baz' }]",
                )
                nextRender(group)
            }
            .then {
                assertEquals("foo", avatars(group)[0].getAttribute("class"))
                assertEquals("bar", avatars(group)[1].getAttribute("class"))
                assertEquals("baz", avatars(group)[2].getAttribute("class"))
            }
    }

    // i18n

    private val customI18nJson =
        """{"anonymous": "someone", "activeUsers": {"one": "One active user", "many": "{count} active users"}}"""

    private fun i18nGroup(): Promise<AvatarGroupElement> {
        val group = newGroup()

        return nextRender(group)
            .then {
                group.items = items("[{ abbr: 'PM' }, { name: 'Yuriy Yevstihnyeyev' }, { name: 'Serhii Kulykov' }]")
                group.i18n = js("(json) => JSON.parse(json)")(customI18nJson)
                nextRender(group)
            }
            .then { group }
    }

    @Test
    fun setsTheAriaLabelFromTheManyTemplate(): Promise<Unit> = i18nGroup().then { group ->
        assertEquals("3 active users", group.getAttribute("aria-label"))
    }

    @Test
    fun setsTheAriaLabelFromTheOneTemplate(): Promise<Unit> = i18nGroup()
        .then { group ->
            group.items = items("[{ abbr: 'PM' }]")
            nextRender(group).then { group }
        }
        .then { group -> assertEquals("One active user", group.getAttribute("aria-label")) }

    @Test
    fun setsTheAriaLabelForAnEmptyItemArray(): Promise<Unit> = i18nGroup()
        .then { group ->
            group.items = items("[]")
            nextRender(group).then { group }
        }
        .then { group -> assertEquals("0 active users", group.getAttribute("aria-label")) }

    @Test
    fun setsTheAriaLabelForANullItemArray(): Promise<Unit> = i18nGroup()
        .then { group ->
            js("(group) => { group.items = null; }")(group)
            nextRender(group).then { group }
        }
        .then { group -> assertEquals("0 active users", group.getAttribute("aria-label")) }

    @Test
    fun passesTheI18nToTheAvatars(): Promise<Unit> = i18nGroup().then { group ->
        avatars(group).forEach { avatar ->
            assertEquals("someone", js("(avatar) => avatar.i18n.anonymous")(avatar) as String)
        }
    }

    // announcements

    private fun announcementGroup(): Promise<AvatarGroupElement> {
        val group = newGroup()

        return nextRender(group)
            .then {
                group.items = items("[{ name: 'AA' }, { name: 'BB' }, { name: 'CC' }]")
                nextRender(group).then { group }
            }
            .then { group }
    }

    private fun liveRegionText(): String =
        js("() => { const region = document.querySelector('[aria-live]'); return region ? region.textContent : ''; }")() as String

    @Test
    fun announcesAnAddedItem(): Promise<Unit> = announcementGroup()
        .then { group ->
            group.items = items("[{ name: 'AA' }, { name: 'BB' }, { name: 'CC' }, { name: 'DD' }]")
            timeout(200)
        }
        .then { assertEquals("DD joined", liveRegionText()) }

    @Test
    fun announcesARemovedItem(): Promise<Unit> = announcementGroup()
        .then { group ->
            group.items = items("[{ name: 'AA' }, { name: 'BB' }]")
            timeout(200)
        }
        .then { assertEquals("CC left", liveRegionText()) }

    @Test
    fun announcesMultipleAddedItems(): Promise<Unit> = announcementGroup()
        .then { group ->
            group.items = items("[{ name: 'AA' }, { name: 'BB' }, { name: 'CC' }, { name: 'DD' }, { name: 'EE' }]")
            timeout(200)
        }
        .then { assertEquals("DD joined, EE joined", liveRegionText()) }

    @Test
    fun announcesMultipleRemovedItems(): Promise<Unit> = announcementGroup()
        .then { group ->
            group.items = items("[{ name: 'AA' }]")
            timeout(200)
        }
        .then { assertEquals("BB left, CC left", liveRegionText()) }

    @Test
    fun announcesAnAddedAndARemovedItem(): Promise<Unit> = announcementGroup()
        .then { group ->
            group.items = items("[{ name: 'AA' }, { name: 'BB' }, { name: 'DD' }]")
            timeout(200)
        }
        .then { assertEquals("CC left, DD joined", liveRegionText()) }

    @Test
    fun announcesMultipleAddedAndRemovedItems(): Promise<Unit> = announcementGroup()
        .then { group ->
            group.items = items("[{ name: 'AA' }, { name: 'DD' }, { name: 'EE' }]")
            timeout(200)
        }
        .then { assertEquals("BB left, CC left, DD joined, EE joined", liveRegionText()) }

    // avatar group in column flex

    @Test
    fun hasTheWidthOfTheParentInAColumnFlex(): Promise<Unit> {
        val layout = attach(
            fixture(
                "<div style=\"display: flex; width: 200px; flex-direction: column; align-items: flex-start;\">" +
                    "<vaadin-avatar-group></vaadin-avatar-group></div>",
            ),
        )
        val group = js("(layout) => layout.firstElementChild")(layout) as AvatarGroupElement

        return nextRender(group)
            .then {
                group.items = items("[{ abbr: 'PM' }, { name: 'Yuriy Yevstihnyeyev' }, { abbr: 'SK' }]")
                nextRender(group).then { group }
            }
            .then {
                assertEquals(
                    js("(layout) => layout.offsetWidth")(layout) as Int,
                    js("(group) => group.offsetWidth")(group) as Int,
                )
            }
    }

    @Test
    fun doesNotShowTheOverflowAvatarInAColumnFlex(): Promise<Unit> {
        val layout = attach(
            fixture(
                "<div style=\"display: flex; width: 200px; flex-direction: column; align-items: flex-start;\">" +
                    "<vaadin-avatar-group></vaadin-avatar-group></div>",
            ),
        )
        val group = js("(layout) => layout.firstElementChild")(layout) as AvatarGroupElement

        return nextRender(group)
            .then {
                group.items = items("[{ abbr: 'PM' }, { name: 'Yuriy Yevstihnyeyev' }, { abbr: 'SK' }]")
                nextRender(group)
            }
            .then { assertTrue(isHidden(overflow(group))) }
    }

    // Kotlin-only coverage

    @Test
    fun createAvatarGroupAppliesTheConfigureBlock() {
        val group = attach(createAvatarGroup { maxItemsVisible = 3.0 })

        assertEquals(3.0, group.maxItemsVisible)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals(
            "vaadin-avatar-group",
            document.createElement(vaadin.VaadinTagName.avatarGroup).tagName.lowercase(),
        )
        assertEquals("max-items-visible", AvatarGroupAttributes.maxItemsVisible)
        assertEquals("i18n", AvatarGroupAttributes.i18n)
        assertEquals("theme", AvatarGroupAttributes.theme)
    }

    @Test
    fun i18nObjectIsExposed() {
        val group = newGroup()

        assertNotNull(js("(group) => group.i18n")(group) as JsAny?)
    }

    @Test
    fun itemDataIsAPlainJsObject() {
        val item = js("({ abbr: 'PM', name: 'Pauli', colorIndex: 1, className: 'foo' })").unsafeCast<AvatarGroupItem>()

        assertEquals("PM", item.abbr)
        assertEquals("Pauli", item.name)
        assertEquals(1.0, item.colorIndex)
        assertEquals("foo", item.className)
    }

    @Test
    fun groupWithoutItemsRendersOnlyTheOverflowAvatar(): Promise<Unit> {
        val group = newGroup()

        return nextRender(group)
            .then { nextFrame() }
            .then { assertEquals(1, avatars(group).size) }
    }

    @Test
    fun updatesTheRenderedAvatars(): Promise<Unit> {
        val group = newGroup()

        return nextRender(group)
            .then {
                group.items = items(fourItems)
                nextFrame().then { group }
            }
            .then {
                group.items = items("[{ abbr: 'GG' }]")
                nextUpdate(group)
            }
            .then { nextFrame() }
            .then { assertEquals("GG", abbrOf(avatars(group)[0])) }
    }
}
