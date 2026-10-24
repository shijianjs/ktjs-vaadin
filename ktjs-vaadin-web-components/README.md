# ktjs-vaadin-web-components

Kotlin/JS wrappers for the free [Vaadin Web Components](https://github.com/vaadin/web-components):
65 npm packages from the `@vaadin/*` scope, exposing 92 custom elements through typed
`external class` declarations.

The module is generated from the `custom-elements.json` manifest of every package, so element
properties, methods, mixins and static entry points match the upstream API. The six packages
without a manifest (`aura`, `field-highlighter`, `icons`, `lit-renderer`, `vaadin-lumo-styles`,
`vaadin-themable-mixin`) are wrapped by hand. Commercial Vaadin components (Board, Charts, CRUD,
Dashboard, Grid Pro, Map, Rich Text Editor), the React bindings and the `vaadin` / `vaadin-core`
meta packages are not included.

## Installation

Replace the version with the one you need (`{upstream version}-kt.{revision}`):

```kotlin
dependencies {
    implementation("io.github.shijianjs:ktjs-vaadin-web-components:25.3.4-kt.1")
}
```

All 65 packages are declared as `npm(...)` dependencies of the module and pinned to the same
upstream version. Kotlin/JS links the ones your application actually imports; unused components are
tree-shaken away. Vaadin packages are ESM-only, so the consuming project must use
`useEsModules()` in its `js` target.

## Layout

Every npm package maps to one Kotlin package, so imports stay close to the upstream structure:

| npm package | Kotlin package |
| --- | --- |
| `@vaadin/button` | `vaadin.button` |
| `@vaadin/combo-box` | `vaadin.combobox` |
| `@vaadin/field-base` | `vaadin.fieldbase` |
| `@vaadin/vaadin-lumo-styles` | `vaadin.lumostyles` |
| `@vaadin/vaadin-themable-mixin` | `vaadin.themablemixin` |

Each element package provides an element type (`ButtonElement`), a registration function
(`registerButton()`), a factory (`createButton {}`), attribute names (`ButtonAttributes`) and, when
the upstream manifest declares events, typed event names (`NotificationEvents.closed`). Every tag
name is collected in one place, `vaadin.VaadinTagName`.

## Usage

Factories register the element on first use:

```kotlin
import vaadin.button.createButton
import vaadin.notification.Notification
import vaadin.textfield.createTextField
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

Creating the element through the factory is the usual path. If you create elements yourself (for
example from a server-rendered page), call the matching `registerXxx()` function first so the custom
element is defined:

```kotlin
import vaadin.VaadinTagName
import vaadin.dialog.registerDialog
import web.dom.document

registerDialog()
val dialog = document.createElement(VaadinTagName.dialog)
dialog.headerTitle = "Details"
```

Components are plain DOM elements, so listeners and other interactions use the standard
`web.events` API from `kotlin-browser`:

```kotlin
import web.dom.clickEvent
import web.events.addHandler

button.clickEvent.addHandler { Notification.show("Clicked") }
```

Static class members are exposed through an object named after the upstream class:
`Notification.show(...)`, `Grid.setDefaultMultiSortPriority(...)`,
`Popover.setDefaultHoverDelay(...)`, `Tooltip.setDefaultFocusDelay(...)`,
`AppLayout.dispatchCloseOverlayDrawerEvent()` and `Iconset.getIconset(...)`.

## Mixins

Upstream mixins are `external interface` declarations, and element classes implement every mixin
they apply. A component instance can therefore be used through the capability it actually
provides, the same way a Kotlin class implements several interfaces:

```kotlin
import vaadin.fieldbase.DisabledMixin
import vaadin.fieldbase.InputFieldMixin
import vaadin.textfield.createTextField

val field = createTextField { label = "Name" }

val disabled: DisabledMixin = field
disabled.disabled = true

val input: InputFieldMixin = field
input.maxlength = 20.0
```

Mixins form the same hierarchy as upstream (`TextFieldMixin : InputFieldMixin`), so assigning a
field to a parent mixin type works as well.

A few upstream properties have the same JavaScript name as an `HTMLElement` member with a different
type, for example `hidden` (upstream `boolean`, DOM `Hidden`) or `autocapitalize` (upstream
`string`, DOM `AutoCapitalize`). Those are exposed with a `Property` suffix and map back to the
original name, so the upstream value is reachable without casting:

```kotlin
overlayMixin.hiddenProperty = true        // sets element.hidden
input.autocapitalizeProperty = "words"    // sets element.autocapitalize
```

## Renderers and Lit

Components that accept rich content expose a `renderer` property. It is a plain callback that
receives the container element to fill, so content is built with DOM APIs and the nodes are reused
across calls:

```kotlin
import vaadin.grid.createGrid
import vaadin.grid.createGridColumn
import web.dom.document

val grid = createGrid()
val nameColumn = createGridColumn { path = "name" }
nameColumn.renderer = { root, _, _ ->
    val label = root.firstElementChild ?: document.createElement("span").also(root::append)
    label.textContent = "Cell content"
}
grid.appendChild(nameColumn)
grid.requestContentUpdate() // re-runs the renderer after the data changed
```

Vaadin also publishes a Lit entry point for these packages (`@vaadin/combo-box/lit.js`,
`@vaadin/grid/lit.js`, ...) with directives such as `comboBoxRenderer()` and
`columnBodyRenderer()`. A directive only works inside a Lit template: it assigns the same
`renderer` property and re-runs it when its dependencies change. Kotlin/JS builds the DOM directly,
so this module does not wrap them; the generated `renderer` properties are already typed, and the
dependency check is replaced by an explicit `requestContentUpdate()` call.

The only Lit value left in the public API is the SVG literal accepted by `<vaadin-icon>`. It is
modelled as the opaque `IconSvgLiteral`, and `unsafeSvgLiteral()` turns SVG markup into one:

```kotlin
import vaadin.icon.Iconset
import vaadin.icon.createIcon
import vaadin.icon.unsafeSvgLiteral

val icon = createIcon()
icon.svg = unsafeSvgLiteral("""<path d="M12 2 2 22h20z"></path>""")

// Icons resolved from a registered iconset have the same type.
// The second argument is the iconset name; null derives it from the "vaadin:" prefix.
val check = Iconset.getIconSvg("vaadin:check", null)
icon.svg = check.svg
```

## Themes

Lumo and Aura are CSS-first design systems; the supported path is importing their stylesheet in the
application stylesheet pipeline:

```kotlin
import vaadin.aura.Aura
import vaadin.lumostyles.LumoThemes

// Pass these paths to your bundler / stylesheet @import:
val aura = Aura.CSS                                   // @vaadin/aura/aura.css
val lumo = LumoThemes.CSS                             // @vaadin/vaadin-lumo-styles/lumo.css
val compact = LumoThemes.COMPACT_CSS                  // @vaadin/vaadin-lumo-styles/presets/compact.css
```

Registering the Lumo icon set and the legacy Lumo style mixins is available for components that need
them:

```kotlin
import vaadin.lumostyles.LumoStyles
import vaadin.lumostyles.registerLumoIcons
import vaadin.lumostyles.registerLumoStyles

registerLumoIcons()
registerLumoStyles()
val overlayStyles = LumoStyles.overlay
```

Custom styles are registered with `registerStyles`; `unsafeCSS` wraps a CSS string in a Lit
`CSSResult`:

```kotlin
import vaadin.themablemixin.registerStyles
import vaadin.themablemixin.unsafeCSS

registerStyles(
    "vaadin-button",
    unsafeCSS(":host { font-weight: 600; }"),
)
```

The `@vaadin/icons` package only registers the icon set; load it before using `vaadin:` icon names:

```kotlin
import vaadin.icon.createIcon
import vaadin.icons.registerVaadinIcons

registerVaadinIcons()
val icon = createIcon { this.icon = "vaadin:home" }
```

## Packages

`a11y-base`, `accordion`, `app-layout`, `aura`, `avatar`, `avatar-group`, `badge`, `breadcrumbs`,
`button`, `card`, `checkbox`, `checkbox-group`, `combo-box`, `component-base`, `confirm-dialog`,
`context-menu`, `custom-field`, `date-picker`, `date-time-picker`, `details`, `dialog`,
`email-field`, `field-base`, `field-highlighter`, `form-layout`, `grid`, `horizontal-layout`,
`icon`, `icons`, `input-container`, `integer-field`, `item`, `list-box`, `lit-renderer`, `login`,
`lumo-styles`, `markdown`, `master-detail-layout`, `menu-bar`, `message-input`, `message-list`,
`multi-select-combo-box`, `notification`, `number-field`, `overlay`, `password-field`, `popover`,
`progress-bar`, `radio-group`, `scroller`, `select`, `side-nav`, `slider`, `split-layout`, `switch`,
`tabs`, `tabsheet`, `text-area`, `text-field`, `themable-mixin`, `time-picker`, `tooltip`, `upload`,
`vertical-layout`, `virtual-list`.

## Regenerating

Sources are generated by [`scripts/generate-wrappers.mjs`](../scripts/generate-wrappers.mjs) from
the manifests in the installed npm packages. After adding or upgrading a dependency:

```bash
./gradlew kotlinNpmInstall
node scripts/generate-wrappers.mjs
```

Generated files start with a marker comment and are overwritten; hand-written wrappers are listed in
the script and left untouched.

## Build and test

```bash
./gradlew :ktjs-vaadin-web-components:jsBrowserTest
```

The browser suite registers and creates every element, checks that every documented member exists
on the instance or class, verifies mixin typing and the DOM name mappings, and exercises
representative behavior such as value round-trips, grid items, dialog state and
`Notification.show()`.

After adding or upgrading an npm dependency, refresh the shared lock file:

```bash
./gradlew kotlinUpgradePackageLock
```
