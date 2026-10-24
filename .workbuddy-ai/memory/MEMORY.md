# ktjs-vaadin 项目笔记

- 测试一律放在 `ktjs-vaadin-web-components/src/jsTest/kotlin/vaadin/<包>/`，包名去连字符（`a11y-base` →
  `vaadin.a11ybase`）。用例优先翻译上游 `S:\workspace\github\vaadin\web-components\packages/<包>/test/`。
- 上游源码树版本常比 `package.json` 锁定的 npm 版本新：断言以 `node_modules` 的 `custom-elements.json`
  为准，不要照抄源码树的行为。
- `scripts/dump-api.py <包名>` 输出该包生成的 Kotlin API 摘要，写测试前用它核对成员名与类型。
- 测试辅助在 `vaadin.TestSupport`（`fixture`、`nextRender`、`nextResize`、`on`、`typeInto`、`shadowQuery`、
  `computedStyle` 等），不要在用例里重复实现。
- 上游需要真实键鼠序列（`sendKeys`、拖拽）或真实文件上传的用例属于明确放弃项，在用例 KDoc 里注明。
