# ktjs-vaadin-web-components

免费版 [Vaadin Web Components](https://github.com/vaadin/web-components) 的 Kotlin/JS wrapper：
覆盖 `@vaadin/*` 作用域下 65 个 npm 包，用带类型的 `external class` 声明暴露 92 个自定义元素。

模块根据每个包的 `custom-elements.json` 生成，元素属性、方法、mixin 与静态入口都与上游 API 对应；
没有 manifest 的六个包（`aura`、`field-highlighter`、`icons`、`lit-renderer`、
`vaadin-lumo-styles`、`vaadin-themable-mixin`）由手写 wrapper 覆盖。收费组件（Board、Charts、CRUD、
Dashboard、Grid Pro、Map、Rich Text Editor）、React 绑定以及 `vaadin` / `vaadin-core` 元包不在范围内。

## 安装

把版本换成需要的那一个（`{上游版本}-kt.{修订号}`）：

```kotlin
dependencies {
    implementation("io.github.shijianjs:ktjs-vaadin-web-components:25.3.4-kt.1")
}
```

65 个包都作为模块的 `npm(...)` 依赖声明，并固定到同一个上游版本。Kotlin/JS 只链接应用实际 import 的
包，未使用的组件会被摇树掉。Vaadin 各包只提供 ESM，消费方的 `js` 目标需要使用 `useEsModules()`。

## 目录结构

每个 npm 包对应一个 Kotlin 包，import 与上游结构保持一致：

| npm 包 | Kotlin 包 |
| --- | --- |
| `@vaadin/button` | `vaadin.button` |
| `@vaadin/combo-box` | `vaadin.combobox` |
| `@vaadin/field-base` | `vaadin.fieldbase` |
| `@vaadin/vaadin-lumo-styles` | `vaadin.lumostyles` |
| `@vaadin/vaadin-themable-mixin` | `vaadin.themablemixin` |

每个元素包都提供元素类型（`ButtonElement`）、带类型的标签名（`ButtonTagName.button`）、注册函数
（`registerButton()`）、工厂函数（`createButton {}`）、属性名常量（`ButtonAttributes`），以及上游
manifest 声明了事件时的带类型事件名（`NotificationEvents.closed`）。

## 用法

工厂函数会在首次使用时注册元素：

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

通过工厂创建是常规路径。如果自己创建元素（例如页面由服务端渲染），先调用对应的 `registerXxx()` 注册：

```kotlin
import vaadin.dialog.DialogTagName
import vaadin.dialog.registerDialog
import web.dom.document

registerDialog()
val dialog = document.createElement(DialogTagName.dialog)
dialog.headerTitle = "Details"
```

组件就是普通的 DOM 元素，事件监听等交互使用 `kotlin-browser` 标准的 `web.events` API：

```kotlin
import web.dom.clickEvent
import web.events.addHandler

button.clickEvent.addHandler { Notification.show("Clicked") }
```

上游类的静态成员通过同名对象暴露：`Notification.show(...)`、`Grid.setDefaultMultiSortPriority(...)`、
`Popover.setDefaultHoverDelay(...)`、`Tooltip.setDefaultFocusDelay(...)`、
`AppLayout.dispatchCloseOverlayDrawerEvent()`、`Iconset.getIconset(...)`。

## Mixin

上游的 mixin 都声明为 `external interface`，元素类实现它实际应用的所有 mixin。因此组件实例可以按它真正
具备的能力来使用，与 Kotlin 类实现多个接口的用法一致：

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

mixin 的继承关系与上游一致（`TextFieldMixin : InputFieldMixin`），把字段赋给父 mixin 类型同样可用。

少数上游属性的 JavaScript 名字与 `HTMLElement` 上同名但类型不同的成员冲突，例如 `hidden`（上游
`boolean`，DOM 为 `Hidden`）、`autocapitalize`（上游 `string`，DOM 为 `AutoCapitalize`）。这些属性
带 `Property` 后缀暴露，并映射回原始名字，无需 cast 就能读写上游值：

```kotlin
overlayMixin.hiddenProperty = true        // 写入 element.hidden
input.autocapitalizeProperty = "words"    // 写入 element.autocapitalize
```

## 主题

Lumo 与 Aura 都是 CSS 优先的设计系统，推荐在应用样式管线里引入对应样式表：

```kotlin
import vaadin.aura.Aura
import vaadin.lumostyles.LumoThemes

// 把路径交给打包器或样式表 @import：
val aura = Aura.CSS                                   // @vaadin/aura/aura.css
val lumo = LumoThemes.CSS                             // @vaadin/vaadin-lumo-styles/lumo.css
val compact = LumoThemes.COMPACT_CSS                  // @vaadin/vaadin-lumo-styles/presets/compact.css
```

需要时也可以注册 Lumo 图标集与旧版 Lumo 样式 mixin：

```kotlin
import vaadin.lumostyles.LumoStyles
import vaadin.lumostyles.registerLumoIcons
import vaadin.lumostyles.registerLumoStyles

registerLumoIcons()
registerLumoStyles()
val overlayStyles = LumoStyles.overlay
```

自定义样式通过 `registerStyles` 注册；`unsafeCSS` 把 CSS 字符串包装成 Lit 的 `CSSResult`：

```kotlin
import vaadin.themablemixin.registerStyles
import vaadin.themablemixin.unsafeCSS

registerStyles(
    "vaadin-button",
    unsafeCSS(":host { font-weight: 600; }"),
)
```

`@vaadin/icons` 只负责注册图标集；使用 `vaadin:` 前缀的图标前要先加载：

```kotlin
import vaadin.icon.createIcon
import vaadin.icons.registerVaadinIcons

registerVaadinIcons()
val icon = createIcon { this.icon = "vaadin:home" }
```

## 包列表

`a11y-base`、`accordion`、`app-layout`、`aura`、`avatar`、`avatar-group`、`badge`、`breadcrumbs`、
`button`、`card`、`checkbox`、`checkbox-group`、`combo-box`、`component-base`、`confirm-dialog`、
`context-menu`、`custom-field`、`date-picker`、`date-time-picker`、`details`、`dialog`、
`email-field`、`field-base`、`field-highlighter`、`form-layout`、`grid`、`horizontal-layout`、
`icon`、`icons`、`input-container`、`integer-field`、`item`、`list-box`、`lit-renderer`、`login`、
`lumo-styles`、`markdown`、`master-detail-layout`、`menu-bar`、`message-input`、`message-list`、
`multi-select-combo-box`、`notification`、`number-field`、`overlay`、`password-field`、`popover`、
`progress-bar`、`radio-group`、`scroller`、`select`、`side-nav`、`slider`、`split-layout`、`switch`、
`tabs`、`tabsheet`、`text-area`、`text-field`、`themable-mixin`、`time-picker`、`tooltip`、`upload`、
`vertical-layout`、`virtual-list`。

## 重新生成

源码由 [`scripts/generate-wrappers.mjs`](../scripts/generate-wrappers.mjs) 根据已安装 npm 包里的
manifest 生成。新增或升级依赖后执行：

```bash
./gradlew kotlinNpmInstall
node scripts/generate-wrappers.mjs
```

生成文件带标记注释，会被覆盖；手写 wrapper 在脚本里单独列出，不受影响。

## 构建与测试

```bash
./gradlew :ktjs-vaadin-web-components:jsBrowserTest
```

浏览器测试会注册并创建每个元素，检查文档中声明的成员在实例或类上确实存在，验证 mixin 类型与 DOM 同名
属性映射，并覆盖典型行为：属性往返、grid 数据、dialog 状态与 `Notification.show()` 等。

新增或升级 npm 依赖后，刷新共享锁文件：

```bash
./gradlew kotlinUpgradePackageLock
```
