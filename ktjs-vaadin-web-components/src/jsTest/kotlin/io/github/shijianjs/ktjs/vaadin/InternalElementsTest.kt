package io.github.shijianjs.ktjs.vaadin

import io.github.shijianjs.ktjs.vaadin.avatargroup.createAvatarGroupMenu
import io.github.shijianjs.ktjs.vaadin.avatargroup.createAvatarGroupMenuItem
import io.github.shijianjs.ktjs.vaadin.combobox.createComboBoxItem
import io.github.shijianjs.ktjs.vaadin.menubar.createMenuBarButton
import io.github.shijianjs.ktjs.vaadin.menubar.createMenuBarListBox
import io.github.shijianjs.ktjs.vaadin.menubar.createMenuBarSubmenu
import io.github.shijianjs.ktjs.vaadin.messageinput.createMessageInputButton
import io.github.shijianjs.ktjs.vaadin.multiselectcombobox.createMultiSelectComboBoxItem
import io.github.shijianjs.ktjs.vaadin.select.createSelectValueButton
import io.github.shijianjs.ktjs.vaadin.timepicker.createTimePickerItem
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import web.html.HTMLElement

/**
 * The `@protected` elements that upstream documents as having a public API. They are absent from the
 * manifests, so their wrappers are hand-written and listed in the generator's `MANUAL_TAGS`; these
 * cases keep them pointing at the real element and at the API the docs promise.
 */
class InternalElementsTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T =
        io.github.shijianjs.ktjs.vaadin.attach(element).also { attached += it }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    @Test
    fun menuBarInternalsAreRegistered() {
        val button = attach(createMenuBarButton { disabled = true })
        val listBox = attach(createMenuBarListBox())
        // Not attached: the submenu's tooltip controller expects the slot that only exists once the
        // menu bar projects it, so connecting a bare one throws.
        val submenu = createMenuBarSubmenu()

        assertEquals("vaadin-menu-bar-button", button.tagName.lowercase())
        assertEquals("vaadin-menu-bar-list-box", listBox.tagName.lowercase())
        assertEquals("vaadin-menu-bar-submenu", submenu.tagName.lowercase())
        // The button *is* a `<vaadin-button>` subclass, so the button API is the real one.
        assertTrue(button.disabled)
    }

    @Test
    fun avatarGroupInternalsAreItemAndListBoxElements() {
        val menu = attach(createAvatarGroupMenu())
        val item = attach(createAvatarGroupMenuItem { selected = true })

        assertEquals("vaadin-avatar-group-menu", menu.tagName.lowercase())
        assertEquals("vaadin-avatar-group-menu-item", item.tagName.lowercase())
        assertTrue(item.selected)
    }

    @Test
    fun comboBoxRowsAreTypedItems() {
        val item = attach(createComboBoxItem { label = "one" })
        val multiItem = attach(createMultiSelectComboBoxItem { label = "two" })
        val timeItem = attach(createTimePickerItem { label = "three" })

        assertEquals("one", item.label)
        assertEquals("two", multiItem.label)
        assertEquals("three", timeItem.label)
        assertEquals("vaadin-multi-select-combo-box-item", multiItem.tagName.lowercase())
        assertEquals("vaadin-time-picker-item", timeItem.tagName.lowercase())
    }

    @Test
    fun buttonInternalsExposeTheButtonApi() {
        val messageButton = attach(createMessageInputButton { disabled = true })
        val selectButton = attach(createSelectValueButton { disabled = true })

        assertEquals("vaadin-message-input-button", messageButton.tagName.lowercase())
        assertEquals("vaadin-select-value-button", selectButton.tagName.lowercase())
        assertTrue(messageButton.disabled)
        assertTrue(selectButton.disabled)
    }
}
