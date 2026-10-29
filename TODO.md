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
| text-field | textfield | 6 | TODO | |
| text-area | textarea | 9 | TODO | |
| email-field | emailfield | 6 | TODO | |
| password-field | passwordfield | 9 | TODO | |
| number-field | numberfield | 10 | TODO | |
| integer-field | numberfield | — | TODO | 文档并入 number-field 页 |
| custom-field | custom-field | 3 | TODO | |
| field-highlighter | — | — | TODO | 文档并入 field 公共章节 |

### 选择控件（`Selection.kt`）

| 页面 | docs 目录 | 示例数 | 状态 | 备注 |
| --- | --- | --- | --- | --- |
| checkbox | checkbox | 12 | TODO | |
| checkbox-group | checkbox | 6 | TODO | |
| radio-group | radiobutton | 12 | TODO | |
| switch | switch | 4 | TODO | |
| select | select | 14 | TODO | |
| combo-box | combobox | 15 | TODO | |
| multi-select-combo-box | multi-select-combo-box | 9 | TODO | |
| list-box | listbox | 6 | TODO | |
| item | listbox | — | TODO | 文档并入 list-box / select |
| slider | slider | 10 | TODO | |

### 日期与时间（`Pickers.kt`）

| 页面 | docs 目录 | 示例数 | 状态 | 备注 |
| --- | --- | --- | --- | --- |
| date-picker | datepicker | 17 | TODO | |
| time-picker | timepicker | 10 | TODO | |
| date-time-picker | datetimepicker | 15 | TODO | |

### 浮层与反馈（`Overlays.kt`）

| 页面 | docs 目录 | 示例数 | 状态 | 备注 |
| --- | --- | --- | --- | --- |
| dialog | dialog | 7 | TODO | |
| confirm-dialog | confirmdialog | 4 | TODO | |
| notification | notification | 15 | TODO | |
| popover | popover | 8 | TODO | |
| tooltip | tooltip | 4 | TODO | |
| context-menu | contextmenu | 11 | TODO | |
| overlay | — | — | TODO | 内部元素，文档并入 popover/notification |
| login | login | 11 | TODO | |

### 数据展示（`Data.kt`）

| 页面 | docs 目录 | 示例数 | 状态 | 备注 |
| --- | --- | --- | --- | --- |
| grid | grid | 39 | TODO | |
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
| avatar | avatar | 9 | TODO | 含 avatar-group |
| avatar-group | avatar | — | TODO | |
| badge | badge | 11 | TODO | |
| icon | icons | 12 | TODO | |
| icons | icons | — | TODO | |
| progress-bar | progressbar | 6 | TODO | |

### 主题与样式（`Theming.kt`）

| 页面 | docs 目录 | 示例数 | 状态 | 备注 |
| --- | --- | --- | --- | --- |
| theming | styling/* | — | WIP | 覆盖 theme 属性、样式属性、utility class |

## 未包装的文档目录

`auto-crud`、`auto-form`、`auto-grid`、`charts`、`crud`、`dashboard`、`grid-pro`、`map`、`richtexteditor`、
`spreadsheet`（收费或云端组件，不包装）；`tree-grid`（V25 已不再是独立元素，由 grid 的 tree column +
数据提供者实现，后续并入 grid 页）。

## wrapper 问题清单

发现即修复，修复后重新生成/编译，并在此登记。

| 日期 | 组件 | 问题 | 处理 |
| --- | --- | --- | --- |
| 2026-10-29 | menu-bar | manifest 只把 `MenuBarMixin.i18n` 记为 `object`，生成结果退化为 `JsAny` | 手写 `MenuBarI18n`（对应上游 `.d.ts`），demo 用 `unsafeJso<MenuBarI18n>` 赋值 |
