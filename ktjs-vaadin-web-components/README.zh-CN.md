# ktjs-vaadin-web-components

免费版 [Vaadin Web Components](https://github.com/vaadin/web-components) 的 Kotlin/JS wrapper：
覆盖 `@vaadin/*` 作用域下 55 个 npm 包，用带类型的 `external class` 声明暴露 85 个自定义元素。

模块根据每个包的 `custom-elements.json` 生成，元素属性、方法与静态入口都与上游 API 对应。收费组件
（Board、Charts、CRUD、Dashboard、Grid Pro、Map、Rich Text Editor 与 React Pro 组件）不在范围内。

## 安装

把版本换成需要的那一个（`{上游版本}-kt.{修订号}`）：

```kotlin
dependencies {
    implementation("io.github.shijianjs:ktjs-vaadin-web-components:25.3.4-kt.1")
}
```

55 个包都作为模块的 `npm(...)` 依赖声明，并固定到同一个上游版本。Kotlin/JS 只链接应用实际 import 的
包，未使用的组件会被摇树掉。Vaadin 各包只提供 ESM，消费方的 `js` 目标需要使用 `useEsModules()`。

## 用法

每个自定义元素都有对应的元素类型（`ButtonElement`）、带类型的标签名（`VaadinTagName.button`）、
注册函数（`registerButton()`）和工厂函数（`createButton {}`）。工厂函数会在首次使用时注册元素：

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

组件就是普通的 DOM 元素，事件监听等交互使用 `kotlin-browser` 标准的 `web.events` API。
[演示页](https://shijianjs.github.io/ktjs-vaadin/) 把按钮点击接到 `Notification.show()`，并渲染了一个
网格。

通过工厂创建是常规路径。如果自己创建元素（例如页面由服务端渲染），先调用对应的 `registerXxx()` 注册：

```kotlin
import vaadin.VaadinTagName
import vaadin.registerDialog

registerDialog()
val dialog = document.createElement(VaadinTagName.dialog)
dialog.headerTitle = "Details"
```

上游类的静态成员通过同名对象暴露：`Notification.show(...)`、
`Grid.setDefaultMultiSortPriority(...)`、`Popover.setDefaultHoverDelay(...)`、
`Tooltip.setDefaultFocusDelay(...)`、`AppLayout.dispatchCloseOverlayDrawerEvent()`、
`Iconset.getIconset(...)`。

`@vaadin/icons` 只负责注册图标集；使用 `vaadin:` 前缀的图标前要先加载：

```kotlin
import vaadin.registerVaadinIcons

registerVaadinIcons()
val icon = createIcon { this.icon = "vaadin:home" }
```

## 包列表

`accordion`、`app-layout`、`avatar`、`avatar-group`、`badge`、`breadcrumbs`、`button`、`card`、
`checkbox`、`checkbox-group`、`combo-box`、`confirm-dialog`、`context-menu`、`custom-field`、
`date-picker`、`date-time-picker`、`details`、`dialog`、`email-field`、`form-layout`、`grid`、
`horizontal-layout`、`icon`、`icons`、`integer-field`、`item`、`list-box`、`login`、`markdown`、
`master-detail-layout`、`menu-bar`、`message-input`、`message-list`、`multi-select-combo-box`、
`notification`、`number-field`、`password-field`、`popover`、`progress-bar`、`radio-group`、
`scroller`、`select`、`side-nav`、`slider`、`split-layout`、`switch`、`tabs`、`tabsheet`、
`text-area`、`text-field`、`time-picker`、`tooltip`、`upload`、`vertical-layout`、`virtual-list`。

## 构建与测试

```bash
./gradlew :ktjs-vaadin-web-components:jsBrowserTest
```

浏览器测试会注册并创建每个元素，检查文档中声明的成员在实例或类上确实存在，并覆盖典型行为：属性往返、
grid 数据、dialog 状态与 `Notification.show()` 等。

新增或升级 npm 依赖后，刷新共享锁文件：

```bash
./gradlew kotlinUpgradePackageLock
```
