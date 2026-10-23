# ktjs-vaadin

免费版 [Vaadin Web Components](https://github.com/vaadin/web-components) 的 Kotlin/JS wrapper。

[English](README.md) | [简体中文](README.zh-CN.md)

**[在线演示](https://shijianjs.github.io/ktjs-vaadin/)** ·
**[API 文档](https://shijianjs.github.io/ktjs-vaadin/api/)**

所有免费的 `@vaadin/*` 包同步发版、共享同一个上游版本，因此合并为单个
`ktjs-vaadin-web-components` 模块：55 个 npm 包、85 个自定义元素。终端应用打包时本来就会摇树，
未使用的声明不会进入最终产物；只发布一个模块也能减少 Maven Central 的 deployment 次数与版本噪音。
收费组件（Board、Charts、CRUD、Dashboard、Grid Pro、Map、Rich Text Editor 与 React Pro 组件）
不在范围内。

## 模块

| 模块 | npm 包 | wrapper 版本 | Maven Central |
| --- | --- | --- | --- |
| [ktjs-vaadin-web-components](ktjs-vaadin-web-components/README.zh-CN.md) | 55 个免费 `@vaadin/*` 包 | `25.3.4-kt.1` | [![Maven Central](https://img.shields.io/maven-central/v/io.github.shijianjs/ktjs-vaadin-web-components?label=Maven%20Central&logo=apachemaven&logoColor=white)](https://central.sonatype.com/artifact/io.github.shijianjs/ktjs-vaadin-web-components) |

## 版本约定

每个模块的版本为 `{上游 npm 版本}-kt.{修订号}`。上游 npm 版本只在根 [`package.json`](package.json)
中声明；每个模块的修订号在 [`wrapper-revisions.properties`](wrapper-revisions.properties) 中维护。
Gradle 从这两份文件推导基础版本和 `npm(...)` 依赖：

- 只发布带 `-kt.N` 后缀的版本，不存在不带后缀的版本。
- 上游升级：改 `package.json` 里的版本，基础版本自动跟随，修订号保持不变。
- 仅 wrapper 修复：只把对应模块的修订号加一（`kt.2`、`kt.3`……）。
- 修订号按模块维护，只增不减，永不重置为 `kt.1`。发布时只发修订号变化的模块。

## 环境要求

- JDK 17 或更高（构建使用 Gradle 9.4.1）。
- Node.js 由 Kotlin Gradle 插件自动下载；`gradle.properties` 关闭了 Yarn，统一用 npm。
- 首次跑浏览器测试时由 Playwright 下载 Chromium。
- demo 的端到端测试是独立的 Node 脚本，因此 `PATH` 上还要有 `node`。

## 构建与测试

```bash
./gradlew build

# 单个模块的测试
./gradlew :ktjs-vaadin-web-components:jsBrowserTest

# demo 打包页面的端到端检查（Playwright，需要 PATH 上有 node）
./gradlew :demo:e2eTest

# GitHub Pages 站点（根目录是 demo，API 文档在 /api/）
./gradlew pagesSite
```

新增或升级模块的 npm 依赖后，刷新共享锁文件：

```bash
./gradlew kotlinUpgradePackageLock
```

## 安装

发布到 Maven Central，group 为 `io.github.shijianjs`。把版本换成需要的那一个
（`{上游版本}-kt.{修订号}`）：

```kotlin
dependencies {
    implementation("io.github.shijianjs:ktjs-vaadin-web-components:25.3.4-kt.1")
}
```

POM 的 `url` 与 `scm.url` 指向本仓库，每个模块都是 Kotlin Multiplatform 项目并发布
`kotlin-tooling-metadata.json`。这是 [klibs.io](https://klibs.io/) 自动收录的条件。

## 发布

发布配置统一在根 [`build.gradle.kts`](build.gradle.kts) 中。发布由
`.github/workflows/release.yml` 执行：签名构件、为每个 Maven 坐标打一个归档，并注册到共享的
Maven Central hub；hub 之后把待发布坐标合并成一次 Central deployment。一次性配置见
[AGENTS.md](AGENTS.md) 的发布章节。

## 许可证

以 [MIT License](LICENSE) 发布。
