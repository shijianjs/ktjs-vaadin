package demo.pages.overlays

import io.github.shijianjs.ktjs.demo.page.Blocks.row as kitRow
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.page.Blocks.col as kitCol
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import web.html.HtmlTagName
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import io.github.shijianjs.ktjs.demo.page.Blocks.paragraph
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.ui.EventLog
import demo.ui.PageFrame
import demo.ui.PageMeta
import demo.ui.Readout
import demo.ui.listen
import js.objects.unsafeJso
import kotlin.js.js
import io.github.shijianjs.ktjs.vaadin.button.ButtonElement
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.integerfield.createIntegerField
import io.github.shijianjs.ktjs.vaadin.login.LoginFormEvents
import io.github.shijianjs.ktjs.vaadin.login.LoginI18n
import io.github.shijianjs.ktjs.vaadin.login.LoginI18nErrorMessage
import io.github.shijianjs.ktjs.vaadin.login.LoginI18nForm
import io.github.shijianjs.ktjs.vaadin.login.LoginI18nHeader
import io.github.shijianjs.ktjs.vaadin.login.LoginOverlayElement
import io.github.shijianjs.ktjs.vaadin.login.createLoginForm
import io.github.shijianjs.ktjs.vaadin.login.createLoginOverlay
import io.github.shijianjs.ktjs.vaadin.switch.SwitchEvents
import io.github.shijianjs.ktjs.vaadin.switch.createSwitch
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
        kitPage = { LoginPage.NAME() },
    )

private val english: LoginTexts =
    LoginTexts(
        formHeading = "Login form",
        formHint = "Submitting validates the fields first and then fires login (this page has no backend).",
        showError = "Show error state",
        disableForm = "Disable form",
        disableHeading = "Disabling during submit",
        disableHint = "disabled locks the submit button; a real application can disable it on login and " +
            "re-enable it after the request returns.",
        ready = "Status: ready",
        submitting = "Status: submitting…",
        i18nHeading = "Localization",
        i18nHint = "i18n replaces the texts inside the form; the object is merged into the default " +
            "bundle, so only the changed fields are required.",
        overlayHeading = "Login overlay",
        overlayHint = "LoginOverlay puts the same form into a full-screen overlay, which suits an app " +
            "entry point.",
        overlayDescription = "Use any username and password to try it",
        openOverlay = "Open login overlay",
        partsHeading = "Overlay slots",
        partsHint = "custom-form-area adds fields to the form area and footer appends content below the " +
            "buttons.",
        oneTimeCode = "One-time code",
        privacyNote = "Never tell your password to anyone",
        openCode = "Open code overlay",
        openFooter = "Open footer overlay",
        headerHeading = "Overlay header and localization",
        headerHint = "title / description override the header; i18n.header and additionalInformation join in.",
        brandedDescription = "Built with ♥ by Vaadin",
        openBranded = "Open branded overlay",
        openLocalized = "Open localized overlay",
        statesHeading = "Error state and extra information",
        statesHint = "error shows the failure banner; additionalInformation adds a note below the form.",
        contact = "Contact admin@company.com if you're experiencing issues logging into your account",
        openError = "Open error overlay",
        openExtra = "Open extra information overlay",
        richHeading = "Rich background",
        richHint = "The login form sits on any background; a dark surface pairs with theme=\"dark\" to " +
            "keep contrast.",
    )

private val chinese: LoginTexts =
    LoginTexts(
        formHeading = "登录表单",
        formHint = "点击提交后字段会先校验，然后触发 login 事件（本页不连后端）。",
        showError = "显示错误状态",
        disableForm = "禁用表单",
        disableHeading = "禁用与重新启用",
        disableHint = "disabled 会锁住提交按钮；真实应用可以在 login 事件里禁用，等请求返回后再放开。",
        ready = "状态：可提交",
        submitting = "状态：提交中…",
        i18nHeading = "国际化",
        i18nHint = "i18n 覆盖表单里的全部文案；只提供需要的字段时会与默认值合并。",
        overlayHeading = "登录浮层",
        overlayHint = "LoginOverlay 把同一个表单放进全屏浮层，适合应用入口。",
        overlayDescription = "使用任意用户名和密码体验交互",
        openOverlay = "打开登录浮层",
        partsHeading = "浮层插槽",
        partsHint = "custom-form-area 把额外字段放进表单区域，footer 在按钮下方追加内容。",
        oneTimeCode = "一次性验证码",
        privacyNote = "永远不要把密码告诉任何人",
        openCode = "打开验证码浮层",
        openFooter = "打开底部插槽浮层",
        headerHeading = "浮层标题与本地化",
        headerHint = "title / description 覆盖头部文案；i18n.header 与 additionalInformation 一并生效。",
        brandedDescription = "用 ♥ 构建",
        openBranded = "打开品牌浮层",
        openLocalized = "打开本地化浮层",
        statesHeading = "错误状态与附加信息",
        statesHint = "error 直接打开失败横幅；additionalInformation 在表单下方追加说明。",
        contact = "登录遇到问题时请联系 admin@company.com",
        openError = "打开错误状态浮层",
        openExtra = "打开附加信息浮层",
        richHeading = "富内容背景",
        richHint = "登录表单可以放在任意背景上；深色背景配合 theme=\"dark\" 保持对比度。",
    )

private val loginTexts: Translations<LoginTexts> = texts(
    Langs.EN to english,
    Langs.ZH_HANS to chinese,
)

internal class LoginPage : DemoPage() {
    private val texts = Binder(loginTexts)

    private val frame = PageFrame(loginEntry)

    /** The field status line, and which of the two states it is showing. */
    private val slowStatus = Readout("")

    private var slowState: (LoginTexts) -> String = { it.ready }

    /** Carries a localizable `additionalInformation`, so a switch has to rebuild its bundle. */
    private val infoOverlay: LoginOverlayElement = createLoginOverlay { opened = false }

    init {
        frame.mount(this)

        val formSection = SectionBlock.NAME { show() }
        texts.bind(formSection.heading) { it.formHeading }
        texts.bind(formSection.hint) { it.formHint }
        val log = EventLog(5)
        val loginForm = createLoginForm()
        loginForm.dataset["test"] = "login-form"
        loginForm.noAutofocus = true
        loginForm.listen(LoginFormEvents.login) { log.add("login") }
        loginForm.listen(LoginFormEvents.forgotPassword) { log.add("forgot-password") }
        // `error` starts as `false`, so only the transitions to the error state are interesting.
        loginForm.listen(LoginFormEvents.errorChanged) { if (loginForm.error) log.add("error-changed · true") }

        val errorSwitch = createSwitch()
        texts.bind(errorSwitch, { it.showError }) { errorSwitch.label = it }
        errorSwitch.listen(SwitchEvents.checkedChanged) { loginForm.error = errorSwitch.checked }
        val disabledSwitch = createSwitch()
        texts.bind(disabledSwitch, { it.disableForm }) { disabledSwitch.label = it }
        disabledSwitch.listen(SwitchEvents.checkedChanged) { loginForm.disabled = disabledSwitch.checked }

        formSection.body.append(exampleBox(null, kitRow(loginForm, kitCol(errorSwitch, disabledSwitch, log.root))))
        body.append(formSection.root)

        val eventSection = SectionBlock.NAME { show() }
        texts.bind(eventSection.heading) { it.disableHeading }
        texts.bind(eventSection.hint) { it.disableHint }
        val slowForm = createLoginForm()
        slowForm.noAutofocus = true
        slowForm.listen(LoginFormEvents.login) {
            slowForm.disabled = true
            slowState = { copy -> copy.submitting }
            showStatus()
            js("(callback, delay) => setTimeout(callback, delay)")(
                {
                    slowForm.disabled = false
                    slowState = { copy -> copy.ready }
                    showStatus()
                },
                1000.0,
            )
        }
        eventSection.body.append(exampleBox(null, kitRow(slowForm, kitCol(slowStatus.root))))
        body.append(eventSection.root)

        val i18nSection = SectionBlock.NAME { show() }
        texts.bind(i18nSection.heading) { it.i18nHeading }
        texts.bind(i18nSection.hint) { it.i18nHint }
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
        i18nSection.body.append(exampleBox(null, finnishForm))
        body.append(i18nSection.root)

        val overlay = SectionBlock.NAME { show() }
        texts.bind(overlay.heading) { it.overlayHeading }
        texts.bind(overlay.hint) { it.overlayHint }
        val loginOverlay = createLoginOverlay { opened = false }
        loginOverlay.dataset["test"] = "login-overlay"
        texts.bind(loginOverlay, { it.overlayDescription }) { loginOverlay.description = it }
        loginOverlay.noAutofocus = true
        loginOverlay.listen(LoginFormEvents.login) { loginOverlay.opened = false }
        val openLogin = createButton { }.addTheme("primary").apply {
            clickEvent.addHandler { loginOverlay.opened = true }
        }
        texts.bind(openLogin) { it.openOverlay }
        overlay.body.append(exampleBox(null, kitRow(openLogin, loginOverlay)))
        body.append(overlay.root)

        val overlayParts = SectionBlock.NAME { show() }
        texts.bind(overlayParts.heading) { it.partsHeading }
        texts.bind(overlayParts.hint) { it.partsHint }
        val codeOverlay = createLoginOverlay { opened = false }
        codeOverlay.noAutofocus = true
        val codeField = createIntegerField { slot = "custom-form-area" }
        texts.bind(codeField, { it.oneTimeCode }) { codeField.label = it }
        codeOverlay.append(codeField)
        val footerOverlay = createLoginOverlay { opened = false }
        footerOverlay.noAutofocus = true
        val footerNote = paragraph()
        texts.bind(footerNote) { it.privacyNote }
        footerNote.slot = "footer"
        footerNote.style.setProperty("text-align", "center")
        footerOverlay.append(footerNote)
        val openCode = openOverlay(codeOverlay)
        texts.bind(openCode) { it.openCode }
        val openFooter = openOverlay(footerOverlay)
        texts.bind(openFooter) { it.openFooter }
        overlayParts.body.append(
            exampleBox(null, kitRow(openCode, openFooter, codeOverlay, footerOverlay)),
        )
        body.append(overlayParts.root)

        val overlayHeader = SectionBlock.NAME { show() }
        texts.bind(overlayHeader.heading) { it.headerHeading }
        texts.bind(overlayHeader.hint) { it.headerHint }
        val brandedOverlay = createLoginOverlay { opened = false }
        brandedOverlay.noAutofocus = true
        brandedOverlay.title = "TaskMob"
        texts.bind(brandedOverlay, { it.brandedDescription }) { brandedOverlay.description = it }

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
        val openBranded = openOverlay(brandedOverlay)
        texts.bind(openBranded) { it.openBranded }
        val openLocalized = openOverlay(localizedOverlay)
        texts.bind(openLocalized) { it.openLocalized }
        overlayHeader.body.append(
            exampleBox(null, kitRow(openBranded, openLocalized, brandedOverlay, localizedOverlay)),
        )
        body.append(overlayHeader.root)

        val states = SectionBlock.NAME { show() }
        texts.bind(states.heading) { it.statesHeading }
        texts.bind(states.hint) { it.statesHint }
        val errorOverlay = createLoginOverlay { opened = false }
        errorOverlay.noAutofocus = true
        errorOverlay.error = true
        infoOverlay.noAutofocus = true
        val openError = openOverlay(errorOverlay)
        texts.bind(openError) { it.openError }
        val openExtra = openOverlay(infoOverlay)
        texts.bind(openExtra) { it.openExtra }
        states.body.append(
            exampleBox(null, kitRow(openError, openExtra, errorOverlay, infoOverlay)),
        )
        body.append(states.root)

        val rich = SectionBlock.NAME { show() }
        texts.bind(rich.heading) { it.richHeading }
        texts.bind(rich.hint) { it.richHint }
        val richForm = createLoginForm()
        richForm.noAutofocus = true
        richForm.addTheme("dark")
        // A full-bleed gradient behind the form: the login page's own backdrop, nothing else uses it.
        val richHost =
            HtmlTagName.div().apply {
                style.setProperty("padding", "var(--lumo-space-l)")
                style.setProperty("border-radius", "var(--lumo-border-radius-l)")
                style.setProperty("background", "linear-gradient(135deg, #1f4e9c, #7a3f8f)")
                append(richForm)
            }
        rich.body.append(exampleBox(null, richHost))
        body.append(rich.root)

        applyTranslation(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = loginEntry.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        showStatus()
        val copy = texts.current
        infoOverlay.i18nProperty =
            unsafeJso<LoginI18n> {
                additionalInformation = copy.contact
            }
    }

    /** Writes the status line from the state the last submit left it in. */
    private fun showStatus() {
        slowStatus.set(slowState(texts.current))
    }

    /**
     * A trigger for a login overlay; the overlay itself stays closed so it does not cover the page, and
     * a successful submit closes it again because `LoginOverlay` has no close control of its own.
     */
    private fun openOverlay(overlay: LoginOverlayElement): ButtonElement =
        createButton { }.addTheme("tertiary").apply {
            overlay.listen(LoginFormEvents.login) { overlay.opened = false }
            clickEvent.addHandler { overlay.opened = true }
        }

    companion object : CustomElementReferenceAdapter<LoginPage>(LoginPage::class.js, "vaadin-demo-login-page")
}
