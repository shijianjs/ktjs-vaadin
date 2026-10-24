# ktjs-vaadin 项目笔记

- 测试一律放在 `ktjs-vaadin-web-components/src/jsTest/kotlin/vaadin/<包>/`，包名去连字符（`a11y-base` →
  `vaadin.a11ybase`）。用例优先翻译上游 `S:\workspace\github\vaadin\web-components\packages/<包>/test/`。
- **上游源码树必须先切到锁定版本的 tag**：`S:\workspace\github\vaadin\web-components` 默认停在 `main`，
  比 `package.json` 锁定的 npm 版本新（`main` 是 `25.4.0-alpha4`，锁的是 `25.3.4`）。翻译前
  `git checkout v<锁定版本>`（如 `git checkout v25.3.4`），否则会照抄到新版本的属性名与默认值
  （`headingLevel` vs `titleHeadingLevel`、是否反射、默认是否 `undefined`）。核对以 `node_modules` 的
  `custom-elements.json` 为准。
- `scripts/dump-api.py <包名>` 输出该包生成的 Kotlin API 摘要，写测试前用它核对成员名与类型。
- 测试辅助在 `vaadin.TestSupport`（`fixture`、`nextRender`、`nextResize`、`on`、`typeInto`、`shadowQuery`、
  `computedStyle` 等），不要在用例里重复实现。
- 上游需要真实键鼠序列（`sendKeys`、拖拽）或真实文件上传的用例属于明确放弃项，在用例 KDoc 里注明。
- 测试目标是「全部补齐」：大包（`grid`、`upload`、`combo-box`、`date-picker` 等）也要逐条翻译上游用例，
  不接受只覆盖公开 API 面的取舍。
