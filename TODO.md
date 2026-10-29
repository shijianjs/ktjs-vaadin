# Demo 示例对齐官方文档 · 进度跟踪

目标：让 `demo` 的每个组件页覆盖 Vaadin 官方文档（`S:\workspace\github\vaadin\docs`，已切到
`25.3.4` tag，与锁定的 npm 版本一致）中该组件的全部示例；过程中发现 wrapper 的类型/API 问题就地修掉，
并把修复写进本文件底部的「wrapper 问题清单」。

## 工作方式

- 以 `article/components/<组件>/*.adoc` 的小节标题为页面 `section()`，每个 `frontend/demo/component/<组件>/*.ts`
  对应一个 `example()`；标题和说明都走 `tr(zh, en)`，页面元信息走 `bi(zh, en)`。
- 跳过：`*-helper.ts` 之类的数据辅助、`react/` 目录、收费组件（Board/Charts/CRUD/Dashboard/Grid Pro/Map/
  Rich Text Editor/Spreadsheet）与 Auto*（云商业组件）。需要真实后端或真实键鼠序列的交互降级为
  「演示同一 API 的本地版本」，并在 `example()` 标题或说明里注明。
- 每轮：读 docs 示例 → 改写页面 → `./gradlew :demo:compileKotlinJs` → 相关 e2e（`./gradlew :demo:e2eTest`）
  → 修 wrapper → 提交（一次提交覆盖一个或几个相邻组件）。
- docs 示例里的 Lit 写法（`@state`、`repeat()`、`unsafeHTML`）在 demo 中一律换成保留模式 DOM 操作，
  不引入任何 UI runtime。

## 页面清单

状态：`TODO` = 未对齐；`WIP` = 正在改写；`DONE` = 已覆盖文档全部可移植示例并编译/e2e 通过。

### 概览

| 页面 | docs 目录 | 示例数 | 状态 | 备注 |
| --- | --- | --- | --- | --- |
| overview | — | — | DONE | 索引页由 catalog 自动生成 |

### 按钮与操作（`Buttons.kt`）

| 页面 | docs 目录 | 示例数 | 状态 | 备注 |
| --- | --- | --- | --- | --- |
| button | button | 16 | DONE | 全部示例已覆盖；图片用内联 SVG data URI |
| menu-bar | menubar | 17 | DONE | 全部示例已覆盖；openOnHover 并入「行为开关」 |

### 输入字段（`TextFields.kt`、`NumberFields.kt`）

| 页面 | docs 目录 | 示例数 | 状态 | 备注 |
| --- | --- | --- | --- | --- |
| text-field | textfield | 6 | DONE | 30/30 个 text-field / text-area / email-field / password-field 示例；值变更模式在浏览器端模拟 |
| text-area | textarea | 9 | DONE | 同上 |
| email-field | emailfield | 6 | DONE | 同上 |
| password-field | passwordfield | 9 | DONE | 同上 |
| number-field | numberfield | 10 | DONE | 8 个可渲染示例全部覆盖；BigDecimal / 值变更模式为 Flow 专属，按 API 语义改写；校验读 `inputElement.validity` |
| integer-field | numberfield | — | DONE | 文档并入 number-field 页，补基本用法 / 取值 / 范围 / 样式 |
| custom-field | custom-field | 3 | DONE | 全部覆盖（报名周期、原生输入、样式）＋ 组合值格式化 |
| field-highlighter | — | — | DONE | 官方文档无示例，按手写包 API 覆盖用户指示与 AI 标记 |

### 选择控件（`Selection.kt`）

| 页面 | docs 目录 | 示例数 | 状态 | 备注 |
| --- | --- | --- | --- | --- |
| checkbox | checkbox | 12 | DONE | 12 个示例全部覆盖（含 indeterminate 全选、相邻分组） |
| checkbox-group | checkbox | 6 | DONE | 组 API、预设值、校验、方向、只读/禁用 |
| radio-group | radiobutton | 12 | DONE | 含自定义选项、多行条目呈现、checkbox 替代方案 |
| switch | switch | 4 | DONE | |
| select | select | 14 | DONE | 分隔线 / 禁用项 / 浮层宽度 / 自定义条目（Flow 专属的空选择项除外） |
| combo-box | combobox | 15 | DONE | 含自定义筛选、部分匹配、懒加载（本地 dataProvider）、renderer |
| multi-select-combo-box | multi-select-combo-box | 9 | DONE | 含自动展开、置顶、折叠 chip、i18n |
| list-box | listbox | 6 | DONE | 分隔线、禁用条目、单/多选、自定义条目呈现 |
| item | listbox | — | DONE | 文档并入 list-box / select |
| slider | slider | 10 | DONE | 含区间滑块、区间校验（Flow 的值变更模式改为 input/change 对照） |

### 日期与时间（`Pickers.kt`）

| 页面 | docs 目录 | 示例数 | 状态 | 备注 |
| --- | --- | --- | --- | --- |
| date-picker | datepicker | 17 | DONE | 42 个 section；custom-format / locale 为 Flow 专属示例，用浏览器端 `i18n` 复现并在文案注明 |
| time-picker | timepicker | 10 | DONE | 自定义解析器用 `i18n.parseTime` / `formatTime` 复现 |
| date-time-picker | datetimepicker | 15 | DONE | 校验示例区分语法错误 / 缺失 / 超范围 |

### 浮层与反馈（`Overlays.kt`、`OverlayPopups.kt`）

| 页面 | docs 目录 | 示例数 | 状态 | 备注 |
| --- | --- | --- | --- | --- |
| dialog | dialog | 7 | DONE | 基本 / 表单内容 / 尺寸与关闭行为 / 可拖动 / 自定义头部 / 底部按钮布局 / 无内边距 / 可调整大小 |
| confirm-dialog | confirmdialog | 4 | DONE | 基本 / 三种结果 / 取消按钮样式 / 仅确认按钮（插槽消息） |
| notification | notification | 15 | DONE | 位置与时长 / 全部位置 / 主题变体 / 常驻 / 富内容 / 带操作 / 键盘快捷键 / 内容长度 |
| popover | popover | 8 | DONE | 基本 / 位置 / 箭头 / 模态 / 交互式提示 / 下拉字段（预设日期区间）/ 通知面板 / 用户菜单 / 浮层里的表单 |
| tooltip | tooltip | 4 | DONE | 插槽 / `for` 关联 / 手动模式 / 延迟与 Markdown / 动态文本 / 位置 |
| context-menu | contextmenu | 11 | DONE | 右键 / 表格行 / 勾选条目 / className / 自定义条目数据 / 分隔线与层级 / 左键 / 自定义呈现 / 条目提示 / 表格操作 |
| overlay | — | — | DONE | 内部元素，文档并入 popover/notification；基本 / 无遮罩非模态 / `setBounds` 尺寸 |
| login | login | 11 | DONE | 表单校验 / 禁用与重新启用 / 国际化 / 登录浮层 / 浮层插槽 / 标题与本地化 / 错误状态 / 富内容背景 |

说明：浮层页面拆成两个文件（`Overlays.kt` 放 dialog / confirm-dialog / overlay / login，
`OverlayPopups.kt` 放 notification / popover / tooltip / context-menu），条目的 `sourcePath` 分别指向对应文件。
登录浮层默认全部关闭，由各自的按钮打开（提交成功后自动关闭），避免多个浮层在页面加载时互相遮挡。

### 数据展示（`Grid.kt`、`Data.kt`）

| 页面 | docs 目录 | 示例数 | 状态 | 备注 |
| --- | --- | --- | --- | --- |
| grid | grid | 39 | DONE | 拆成 `Grid.kt`（夹具、基本、列、数据绑定、排序）与 `GridFeatures.kt`（行详情、空状态、菜单与 tooltip、焦点与滚动、拖放、选择、样式、懒列）；inline-editing 的 buffered / unbuffered 只有 Flow 示例，改在数据绑定一节说明 `dataProvider` 的写法；tree-grid 的拖放过滤示例用普通 grid 的 `dragFilter` / `dropFilter` 等价演示 |
| virtual-list | virtuallist | 1 | TODO | |
| message-list | messages | 8 | TODO | |
| message-input | messages | 1 | TODO | |
| markdown | markdown | 2 | TODO | |
| upload | upload | 17 | TODO | |

### 布局与导航（`Layouts.kt`）

| 页面 | docs 目录 | 示例数 | 状态 | 备注 |
| --- | --- | --- | --- | --- |
| app-layout | app-layout | 9 | TODO | |
| horizontal-layout | horizontal-layout | 11 | TODO | |
| vertical-layout | vertical-layout | 8 | TODO | |
| form-layout | formlayout | 7 | TODO | |
| split-layout | splitlayout | 7 | TODO | |
| scroller | scroller | 3 | TODO | |
| master-detail-layout | master-detail-layout | 1 | TODO | |
| card | card | 13 | TODO | |
| accordion | accordion | 8 | TODO | |
| details | details | 7 | TODO | |
| tabs | tabs | 19 | TODO | 含 tabsheet |
| tabsheet | tabs | — | TODO | |
| side-nav | side-nav | 6 | TODO | |
| breadcrumbs | breadcrumbs | 3 | TODO | |

### 基础展示（`Display.kt`）

| 页面 | docs 目录 | 示例数 | 状态 | 备注 |
| --- | --- | --- | --- | --- |
| avatar | avatar | 9 | DONE | 基本 / 姓名缩写 / 图片 / 尺寸 / 颜色索引 |
| avatar-group | avatar | — | DONE | 基本 / 最大数量 / 背景色 / 本地化 |
| badge | badge | 11 | DONE | 全部示例（标签、颜色、图标、数字、圆点、动态更新等） |
| icon | icons | 12 | DONE | 基本 / 尺寸 / 颜色 / 字体图标 / SVG 字面量 |
| icons | icons | — | DONE | 两套图标集、无障碍、独立 SVG、sprite、生成器 |
| progress-bar | progressbar | 6 | DONE | 含标签与估计完成时间 |
| input-container | — | — | DONE | 内部元素，文档并入 text-field |

### 主题与样式（`Theming.kt`）

| 页面 | docs 目录 | 示例数 | 状态 | 备注 |
| --- | --- | --- | --- | --- |
| theming | styling/* | — | DONE | theme 属性、设计令牌、样式属性、Shadow Part、工具类、i18n、样式表入口 |

## 未包装的文档目录

`auto-crud`、`auto-form`、`auto-grid`、`charts`、`crud`、`dashboard`、`grid-pro`、`map`、`richtexteditor`、
`spreadsheet`（收费或云端组件，不包装）；`tree-grid`（V25 已不再是独立元素，由 grid 的 tree column +
数据提供者实现，后续并入 grid 页）。

## wrapper 问题清单

发现即修复，修复后重新生成/编译，并在此登记。

| 日期 | 组件 | 问题 | 处理 |
| --- | --- | --- | --- |
| 2026-10-29 | menu-bar | manifest 只把 `MenuBarMixin.i18n` 记为 `object`，生成结果退化为 `JsAny` | 手写 `MenuBarI18n`（对应上游 `.d.ts`），demo 用 `unsafeJso<MenuBarI18n>` 赋值 |
| 2026-10-29 | field-base | manifest 只把 `InputMixin.inputElement` 记为受保护内部成员，公开 API 缺 `inputElement`，读原生 `validity` / 原始文本要绕 `js()` | 生成器新增 `EXTRA_MEMBERS`，按 `input-mixin.d.ts` 补 `val inputElement: web.html.HTMLElement`；`FieldBaseTest` 补用例；TextFields / Pickers / NumberFields 改用类型化访问 |
| 2026-10-29 | date-picker | `parseDate` 被生成为非空 `Date`，上游是 `Date \| undefined`；`dateEquals` 也不接受 `null` | 两个模块级函数写进 `MODULE_SIGNATURES`（可空参数 / 返回值）；测试改为直接用 `null` 断言 |
| 2026-10-29 | date-time-picker | `i18n` 继承自 `I18nMixin`（`JsAny`），上游 `.d.ts` 声明为 `DateTimePickerI18n`（含 `dateLabel` / `timeLabel`） | 手写 `DateTimePickerI18n`，生成器 `EXTRA_MEMBERS` 为元素补 `@JsName("i18n") i18nProperty`；demo 与测试改用类型化访问器 |
| 2026-10-29 | 生成器 | `resolveName` 用未更新的 `name` 做循环条件，遇到「继承成员同名但类型不同」会死循环；重命名分支只在 DOM 改名时写 `@JsName` | 循环改用 `kotlin`；`@JsName` 按最终 Kotlin 名与 JS 名是否一致决定 |
| 2026-10-29 | login | `LoginMixin.i18n` 在 manifest 里是 `object`（生成成 `JsAny`），上游 `.d.ts` 声明 `interface LoginI18n`；`LoginMixin.action` 上游是 `string \| null`，manifest 只写 `string` | 手写 `vaadin/login/LoginTypes.kt`（`LoginI18n` / `LoginI18nForm` / `LoginI18nErrorMessage` / `LoginI18nHeader`），生成器 `EXTRA_MEMBERS` 给 `LoginForm` / `LoginOverlay` 补 `@JsName("i18n") i18nProperty`，`MEMBER_TYPES` 把 `action` 改成 `String?`；`LoginFormTest` 补类型化 i18n 与 `action = null` 用例；demo 改用 `i18nProperty` |
| 2026-10-29 | 选择控件 | 复核查过一遍：`TooltipMixin.text`、`MultiSelectComboBox.i18n`、`ListBoxEdge` 等成员 manifest 与 `.d.ts` 一致，本轮无新增缺口；`multi-select-combo-box` 的 `i18n` 仍是 `JsAny`（见已知限制） | 未改生成器 |

### 已知限制（不修）

- 所有事件的类型都是 `web.events.EventType<web.events.Event>`：manifest 不记录 `detail` 结构，逐个事件手写 detail 类型
  代价过大（上游 `validated` 的 `{ valid }` 等）。demo 改为读取组件的状态属性 / 原生 `validity` 判断结果。
- `date-time-picker` 内部的两个 picker 只在 light DOM 里（`slot="date-picker"` / `slot="time-picker"`），上游没有暴露
  属性，demo 用一次 `js()` 读回它们的值。
- `field-highlighter`、`input-container` 等内部元素没有独立文档页，页面文案已注明。
- `context-menu` 的 `ContextMenuItemData<TItemData>` 是「封闭字段 + 交叉泛型」：Kotlin 表达不了 `& TItemData`，
  wrapper 只建模声明过的字段。需要附加数据时声明一个继承 `ContextMenuItemData` 的 `external interface`（demo 的
  `CopyMenuItem` 就是这种写法），再 `unsafeCast` 成 `Array<ContextMenuItemData>` 赋给 `items`。
- `multi-select-combo-box` 的 `i18n` 仍是 `JsAny`：上游 `MultiSelectComboBoxI18n` 没有结构化 manifest 信息，demo 用 `jsObject` 传对象；
  后续可按 `DateTimePickerI18n` 的做法手写具名接口。
