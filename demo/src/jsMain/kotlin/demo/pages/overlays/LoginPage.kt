package demo.pages.overlays

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.pages.data.message
import demo.tr
import demo.ui.EventLog
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.Readout
import demo.ui.column
import demo.ui.div
import demo.ui.example
import demo.ui.listen
import demo.ui.page
import demo.ui.paragraph
import demo.ui.row
import demo.ui.section
import demo.ui.withTheme
import js.objects.unsafeJso
import kotlin.js.js
import vaadin.button.createButton
import vaadin.integerfield.createIntegerField
import vaadin.login.LoginFormEvents
import vaadin.login.LoginI18n
import vaadin.login.LoginI18nErrorMessage
import vaadin.login.LoginI18nForm
import vaadin.login.LoginI18nHeader
import vaadin.login.createLoginForm
import vaadin.login.createLoginOverlay
import vaadin.switch.SwitchEvents
import vaadin.switch.createSwitch
import web.dom.clickEvent
import web.events.addHandler

val loginEntry: CatalogEntry =
    CatalogEntry(
        id = "login",
        group = Groups.OVERLAYS,
        meta =
            PageMeta(
                title = bi("Login", "Login"),
                summary =
                    bi(
                        "登录表单与登录浮层：用户名 / 密码校验、错误状态与 forgot-password 事件。",
                        "Login form and login overlay: username / password validation, the error state and " +
                            "the forgot-password event.",
                    ),
                docsUrl = "$DOCS_BASE/login",
                sourcePath = "pages/overlays/LoginPage.kt",
            ),
        builder = ::buildLoginPage,
    )

/**
 * A trigger for a login overlay; the overlays stay closed so they do not cover the page, and a
 * successful submit closes the overlay again because `LoginOverlay` has no close control of its own.
 */
private fun openOverlayButton(label: String, overlay: vaadin.login.LoginOverlayElement): vaadin.button.ButtonElement =
    createButton { textContent = label }.withTheme("tertiary").apply {
        overlay.listen(LoginFormEvents.login) { overlay.opened = false }
        clickEvent.addHandler { overlay.opened = true }
    }

private fun buildLoginPage(meta: PageMeta): PageView {
    val view = page(meta)

    val formSection =
        section(
            tr("登录表单", "Login form"),
            tr(
                "点击提交后字段会先校验，然后触发 login 事件（本页不连后端）。",
                "Submitting validates the fields first and then fires login (this page has no backend).",
            ),
        )
    val log = EventLog(5)
    val loginForm = createLoginForm()
    loginForm.dataset["test"] = "login-form"
    loginForm.noAutofocus = true
    loginForm.listen(LoginFormEvents.login) { log.add("login") }
    loginForm.listen(LoginFormEvents.forgotPassword) { log.add("forgot-password") }
    // `error` starts as `false`, so only the transitions to the error state are interesting.
    loginForm.listen(LoginFormEvents.errorChanged) { if (loginForm.error) log.add("error-changed · true") }

    val errorSwitch = createSwitch { label = tr("显示错误状态", "Show error state") }
    errorSwitch.listen(SwitchEvents.checkedChanged) { loginForm.error = errorSwitch.checked }
    val disabledSwitch = createSwitch { label = tr("禁用表单", "Disable form") }
    disabledSwitch.listen(SwitchEvents.checkedChanged) { loginForm.disabled = disabledSwitch.checked }

    formSection.body.append(example(null, row(loginForm, column(errorSwitch, disabledSwitch, log.root))))
    view.body.append(formSection.root)

    val eventSection =
        section(
            tr("禁用与重新启用", "Disabling during submit"),
            tr(
                "disabled 会锁住提交按钮；真实应用可以在 login 事件里禁用，等请求返回后再放开。",
                "disabled locks the submit button; a real application can disable it on login and re-enable " +
                    "it after the request returns.",
            ),
        )
    val slowForm = createLoginForm()
    slowForm.noAutofocus = true
    val slowStatus = Readout(tr("状态：可提交", "Status: ready"))
    slowForm.listen(LoginFormEvents.login) {
        slowForm.disabled = true
        slowStatus.set(tr("状态：提交中…", "Status: submitting…"))
        js("(callback, delay) => setTimeout(callback, delay)")(
            {
                slowForm.disabled = false
                slowStatus.set(tr("状态：可提交", "Status: ready"))
            },
            1000.0,
        )
    }
    eventSection.body.append(example(null, row(slowForm, column(slowStatus.root))))
    view.body.append(eventSection.root)

    val i18nSection =
        section(
            tr("国际化", "Localization"),
            tr(
                "i18n 覆盖表单里的全部文案；只提供需要的字段时会与默认值合并。",
                "i18n replaces the texts inside the form; the object is merged into the default bundle, so " +
                    "only the changed fields are required.",
            ),
        )
    val finnishForm = createLoginForm()
    finnishForm.noAutofocus = true
    finnishForm.i18nProperty =
        unsafeJso<LoginI18n> {
            form =
                unsafeJso<LoginI18nForm> {
                    title = "Kirjaudu sisään"
                    username = "Käyttäjänimi"
                    password = "Salasana"
                    submit = "Kirjaudu sisään"
                    forgotPassword = "Unohtuiko salasana?"
                }
            errorMessage =
                unsafeJso<LoginI18nErrorMessage> {
                    title = "Väärä käyttäjätunnus tai salasana"
                    message = "Tarkista että käyttäjätunnus ja salasana ovat oikein ja yritä uudestaan."
                    username = "Käyttäjätunnus vaaditaan"
                    password = "Salasana vaaditaan"
                }
        }
    i18nSection.body.append(example(null, finnishForm))
    view.body.append(i18nSection.root)

    val overlay =
        section(
            tr("登录浮层", "Login overlay"),
            tr(
                "LoginOverlay 把同一个表单放进全屏浮层，适合应用入口。",
                "LoginOverlay puts the same form into a full-screen overlay, which suits an app entry point.",
            ),
        )
    val loginOverlay = createLoginOverlay { opened = false }
    loginOverlay.dataset["test"] = "login-overlay"
    loginOverlay.description = tr("使用任意用户名和密码体验交互", "Use any username and password to try it")
    loginOverlay.noAutofocus = true
    loginOverlay.listen(LoginFormEvents.login) { loginOverlay.opened = false }
    overlay.body.append(
        example(
            null,
            row(
                createButton { textContent = tr("打开登录浮层", "Open login overlay") }.withTheme("primary").apply {
                    clickEvent.addHandler { loginOverlay.opened = true }
                },
                loginOverlay,
            ),
        ),
    )
    view.body.append(overlay.root)

    val overlayParts =
        section(
            tr("浮层插槽", "Overlay slots"),
            tr(
                "custom-form-area 把额外字段放进表单区域，footer 在按钮下方追加内容。",
                "custom-form-area adds fields to the form area and footer appends content below the buttons.",
            ),
        )
    val codeOverlay = createLoginOverlay { opened = false }
    codeOverlay.noAutofocus = true
    codeOverlay.append(
        createIntegerField {
            label = tr("一次性验证码", "One-time code")
            slot = "custom-form-area"
        },
    )
    val footerOverlay = createLoginOverlay { opened = false }
    footerOverlay.noAutofocus = true
    footerOverlay.append(
        paragraph(tr("永远不要把密码告诉任何人", "Never tell your password to anyone")).apply {
            slot = "footer"
            style.setProperty("text-align", "center")
        },
    )
    overlayParts.body.append(
        example(
            null,
            row(
                openOverlayButton(tr("打开验证码浮层", "Open code overlay"), codeOverlay),
                openOverlayButton(tr("打开底部插槽浮层", "Open footer overlay"), footerOverlay),
                codeOverlay,
                footerOverlay,
            ),
        ),
    )
    view.body.append(overlayParts.root)

    val overlayHeader =
        section(
            tr("浮层标题与本地化", "Overlay header and localization"),
            tr(
                "title / description 覆盖头部文案；i18n.header 与 additionalInformation 一并生效。",
                "title / description override the header; i18n.header and additionalInformation join in.",
            ),
        )
    val brandedOverlay = createLoginOverlay { opened = false }
    brandedOverlay.noAutofocus = true
    brandedOverlay.title = "TaskMob"
    brandedOverlay.description = tr("用 ♥ 构建", "Built with ♥ by Vaadin")

    val localizedOverlay = createLoginOverlay { opened = false }
    localizedOverlay.noAutofocus = true
    localizedOverlay.i18nProperty =
        unsafeJso<LoginI18n> {
            header =
                unsafeJso<LoginI18nHeader> {
                    title = "Sovelluksen nimi"
                    description = "Sovelluksen kuvaus"
                }
            form =
                unsafeJso<LoginI18nForm> {
                    title = "Kirjaudu sisään"
                    submit = "Kirjaudu sisään"
                }
            additionalInformation = "Jos tarvitset lisätietoja käyttäjälle."
        }
    overlayHeader.body.append(
        example(
            null,
            row(
                openOverlayButton(tr("打开品牌浮层", "Open branded overlay"), brandedOverlay),
                openOverlayButton(tr("打开本地化浮层", "Open localized overlay"), localizedOverlay),
                brandedOverlay,
                localizedOverlay,
            ),
        ),
    )
    view.body.append(overlayHeader.root)

    val states =
        section(
            tr("错误状态与附加信息", "Error state and extra information"),
            tr(
                "error 直接打开失败横幅；additionalInformation 在表单下方追加说明。",
                "error shows the failure banner; additionalInformation adds a note below the form.",
            ),
        )
    val errorOverlay = createLoginOverlay { opened = false }
    errorOverlay.noAutofocus = true
    errorOverlay.error = true
    val infoOverlay = createLoginOverlay { opened = false }
    infoOverlay.noAutofocus = true
    infoOverlay.i18nProperty =
        unsafeJso<LoginI18n> {
            additionalInformation =
                tr(
                    "登录遇到问题时请联系 admin@company.com",
                    "Contact admin@company.com if you're experiencing issues logging into your account",
                )
        }
    states.body.append(
        example(
            null,
            row(
                openOverlayButton(tr("打开错误状态浮层", "Open error overlay"), errorOverlay),
                openOverlayButton(tr("打开附加信息浮层", "Open extra information overlay"), infoOverlay),
                errorOverlay,
                infoOverlay,
            ),
        ),
    )
    view.body.append(states.root)

    val rich =
        section(
            tr("富内容背景", "Rich background"),
            tr(
                "登录表单可以放在任意背景上；深色背景配合 theme=\"dark\" 保持对比度。",
                "The login form sits on any background; a dark surface pairs with theme=\"dark\" to keep contrast.",
            ),
        )
    val richForm = createLoginForm()
    richForm.noAutofocus = true
    richForm.withTheme("dark")
    val richHost = div("login-rich-content", richForm)
    rich.body.append(example(null, richHost))
    view.body.append(rich.root)

    return view
}
