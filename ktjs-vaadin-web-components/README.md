# ktjs-vaadin-web-components

Kotlin/JS wrappers for the free [Vaadin Web Components](https://github.com/vaadin/web-components):
55 npm packages from the `@vaadin/*` scope, exposing 85 custom elements through typed
`external class` declarations.

The module is generated from the `custom-elements.json` manifest of every package, so element
properties, methods and static entry points match the upstream API. Commercial Vaadin components
(Board, Charts, CRUD, Dashboard, Grid Pro, Map, Rich Text Editor and the React Pro components) are
not included.

## Installation

Replace the version with the one you need (`{upstream version}-kt.{revision}`):

```kotlin
dependencies {
    implementation("io.github.shijianjs:ktjs-vaadin-web-components:25.3.4-kt.1")
}
```

All 55 packages are declared as `npm(...)` dependencies of the module and pinned to the same
upstream version. Kotlin/JS links the ones your application actually imports; unused components are
tree-shaken away. Vaadin packages are ESM-only, so the consuming project must use
`useEsModules()` in its `js` target.

## Usage

Each custom element has a Kotlin element type (`ButtonElement`), a typed tag name
(`VaadinTagName.button`), a registration function (`registerButton()`) and a factory
(`createButton {}`). Factories register the element on first use:

```kotlin
import vaadin.Notification
import vaadin.createButton
import vaadin.createTextField
import web.dom.document

fun main() {
    val name = createTextField {
        label = "Name"
        placeholder = "Ada Lovelace"
    }

    val button = createButton { textContent = "Say hello" }

    document.body.append(name, button)
    Notification.show("Hello from ktjs-vaadin!")
}
```

Components are plain DOM elements, so listeners and other interactions use the standard
`web.events` API from `kotlin-browser`. The [demo](https://shijianjs.github.io/ktjs-vaadin/) wires a
button click to `Notification.show()` and renders a grid.

Creating the element through the factory is the usual path. If you create elements yourself (for
example from a server-rendered page), call the matching `registerXxx()` function first so the custom
element is defined:

```kotlin
import vaadin.VaadinTagName
import vaadin.registerDialog

registerDialog()
val dialog = document.createElement(VaadinTagName.dialog)
dialog.headerTitle = "Details"
```

Static class members are exposed through an object named after the upstream class:
`Notification.show(...)`, `Grid.setDefaultMultiSortPriority(...)`,
`Popover.setDefaultHoverDelay(...)`, `Tooltip.setDefaultFocusDelay(...)`,
`AppLayout.dispatchCloseOverlayDrawerEvent()` and `Iconset.getIconset(...)`.

The `@vaadin/icons` package only registers the icon set; load it before using `vaadin:` icon names:

```kotlin
import vaadin.registerVaadinIcons

registerVaadinIcons()
val icon = createIcon { this.icon = "vaadin:home" }
```

## Packages

`accordion`, `app-layout`, `avatar`, `avatar-group`, `badge`, `breadcrumbs`, `button`, `card`,
`checkbox`, `checkbox-group`, `combo-box`, `confirm-dialog`, `context-menu`, `custom-field`,
`date-picker`, `date-time-picker`, `details`, `dialog`, `email-field`, `form-layout`, `grid`,
`horizontal-layout`, `icon`, `icons`, `integer-field`, `item`, `list-box`, `login`, `markdown`,
`master-detail-layout`, `menu-bar`, `message-input`, `message-list`, `multi-select-combo-box`,
`notification`, `number-field`, `password-field`, `popover`, `progress-bar`, `radio-group`,
`scroller`, `select`, `side-nav`, `slider`, `split-layout`, `switch`, `tabs`, `tabsheet`,
`text-area`, `text-field`, `time-picker`, `tooltip`, `upload`, `vertical-layout`, `virtual-list`.

## Build and test

```bash
./gradlew :ktjs-vaadin-web-components:jsBrowserTest
```

The browser suite registers and creates every element, checks that every documented member exists
on the instance or class, and exercises representative behavior such as value round-trips, grid
items, dialog state and `Notification.show()`.

After adding or upgrading an npm dependency, refresh the shared lock file:

```bash
./gradlew kotlinUpgradePackageLock
```
