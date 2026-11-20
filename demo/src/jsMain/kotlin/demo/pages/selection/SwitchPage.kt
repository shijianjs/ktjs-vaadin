package demo.pages.selection

import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.Blocks.col as kitCol
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.page.Blocks.row as kitRow
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.ui.PageFrame
import demo.ui.PageMeta
import demo.ui.Readout
import demo.ui.listen
import io.github.shijianjs.ktjs.vaadin.button.ButtonElement
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.switch.SwitchElement
import io.github.shijianjs.ktjs.vaadin.switch.SwitchEvents
import io.github.shijianjs.ktjs.vaadin.switch.createSwitch
import io.github.shijianjs.ktjs.vaadin.tooltip.TooltipElement
import io.github.shijianjs.ktjs.vaadin.tooltip.createTooltip
import web.dom.clickEvent
import web.events.addHandler

val switchEntry: CatalogEntry =
    CatalogEntry(
        id = "switch",
        group = Groups.SELECTION,
        meta =
            PageMeta(
                title = bi("Switch", "Switch"),
                summary =
                    bi(
                        "二态开关，语义与 Checkbox 相同，视觉上表达即时生效的设置。",
                        "Two-state switch with the same semantics as Checkbox, used for settings that " +
                            "apply immediately.",
                    ),
                docsUrl = "$DOCS_BASE/switch",
                sourcePath = "pages/selection/SwitchPage.kt",
            ),
        kitPage = { SwitchPage.NAME() },
    )

internal class SwitchPage : DemoPage() {
    private val texts = Binder(switchTexts)

    private val frame = PageFrame(switchEntry)

    private val notifications: SwitchElement = createSwitch()

    private val autosave: SwitchElement = createSwitch()

    private val autosaveTooltip: TooltipElement = createTooltip()

    private val twoFactor: SwitchElement = createSwitch()

    private val auditLog: SwitchElement = createSwitch()

    private val dailyDigest: SwitchElement = createSwitch()

    private val validateButton: ButtonElement = createButton()

    private val readout = Readout(english.on)

    private val result = Readout(english.notValidated)

    /** Whether the required switch passed its last validation; null before the first click. */
    private var lastValidation: Boolean? = null

    init {
        frame.mount(this)

        val basic = SectionBlock.NAME { show() }
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        notifications.checked = true
        notifications.dataset["test"] = "switch-basic"
        notifications.listen(SwitchEvents.checkedChanged) { renderState() }
        basic.body.append(exampleBox(null, kitCol(notifications, readout.root)))
        body.append(basic.root)

        val features = SectionBlock.NAME { show() }
        texts.bind(features.heading) { it.featuresHeading }
        texts.bind(features.hint) { it.featuresHint }
        autosaveTooltip.setAttribute("slot", "tooltip")
        autosave.append(autosaveTooltip)
        features.body.append(exampleBox(null, autosave))
        body.append(features.root)

        val validation = SectionBlock.NAME { show() }
        texts.bind(validation.heading) { it.validationHeading }
        texts.bind(validation.hint) { it.validationHint }
        twoFactor.required = true
        twoFactor.checked = true
        validateButton.clickEvent.addHandler {
            lastValidation = twoFactor.validate()
            renderResult()
        }
        validation.body.append(exampleBox(null, kitCol(twoFactor, kitRow(validateButton, result.root))))
        body.append(validation.root)

        val states = SectionBlock.NAME { show() }
        texts.bind(states.heading) { it.statesHeading }
        texts.bind(states.hint) { it.statesHint }
        auditLog.readonly = true
        auditLog.checked = true
        dailyDigest.disabled = true
        states.body.append(exampleBox(null, kitCol(auditLog, dailyDigest)))
        body.append(states.root)

        applyTranslation(Langs.EN)
    }

    private fun renderState() {
        val copy = texts.current
        readout.set(copy.statePrefix, if (notifications.checked) copy.on else copy.off)
    }

    private fun renderResult() {
        val copy = texts.current
        val outcome = lastValidation ?: return result.set(copy.notValidated)
        result.set(copy.validationPrefix, if (outcome) copy.passed else copy.failed)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = switchEntry.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        val copy = texts.current
        notifications.label = copy.notifications
        autosave.label = copy.autosave
        autosave.helperText = copy.autosaveHelper
        autosaveTooltip.text = copy.lastSaved
        twoFactor.label = copy.twoFactor
        twoFactor.errorMessage = copy.twoFactorError
        auditLog.label = copy.auditLog
        auditLog.helperText = copy.auditLogHelper
        dailyDigest.label = copy.dailyDigest
        validateButton.textContent = copy.validate
        renderState()
        renderResult()
    }

    companion object : CustomElementReferenceAdapter<SwitchPage>(SwitchPage::class.js, "vaadin-demo-switch-page") {
        private val english: SwitchTexts = SwitchTexts(
            basicHeading = "Basic usage",
            basicHint = "switch and checkbox share the same state model, read and written through checked.",
            notifications = "Receive release notifications",
            on = "on",
            off = "off",
            statePrefix = "State: ",
            featuresHeading = "Helper text and tooltip",
            featuresHint = "helper-text explains what the switch does; a slotted tooltip adds the details.",
            autosave = "Autosave",
            autosaveHelper = "Automatically save changes as you work",
            lastSaved = "Last saved 5 minutes ago",
            validationHeading = "Validation",
            validationHint = "The switch supports required as well: validate() fails while it is off and shows the " +
                "error-message.",
            twoFactor = "Two-factor authentication",
            twoFactorError = "Required by your workplace security policy",
            notValidated = "Not validated yet",
            validate = "Validate",
            validationPrefix = "Validation: ",
            passed = "passed",
            failed = "failed",
            statesHeading = "Read-only and disabled",
            statesHint = "readonly only displays the current state; disabled makes the whole row inert.",
            auditLog = "Audit log retention (90 days)",
            auditLogHelper = "Included on the Business plan",
            dailyDigest = "Daily digest",
        )
        private val chinese: SwitchTexts = SwitchTexts(
            basicHeading = "基本用法",
            basicHint = "switch 与 checkbox 共享状态模型，用 checked 读写。",
            notifications = "接收发布通知",
            on = "已开启",
            off = "已关闭",
            statePrefix = "状态：",
            featuresHeading = "辅助信息与提示",
            featuresHint = "helper-text 说明开关的作用；slot=\"tooltip\" 的 tooltip 补充细节。",
            autosave = "自动保存",
            autosaveHelper = "工作时自动保存改动",
            lastSaved = "上次保存于 5 分钟前",
            validationHeading = "校验",
            validationHint = "开关同样支持 required：未满足时 validate() 失败并显示 error-message。",
            twoFactor = "双因素认证",
            twoFactorError = "公司安全策略要求开启",
            notValidated = "尚未校验",
            validate = "校验",
            validationPrefix = "校验：",
            passed = "通过",
            failed = "失败",
            statesHeading = "只读与禁用",
            statesHint = "readonly 只展示当前状态；disabled 让整行不可交互。",
            auditLog = "审计日志保留（90 天）",
            auditLogHelper = "商业版套餐已包含",
            dailyDigest = "每日摘要",
        )
        private val switchTexts: Translations<SwitchTexts> = texts(
            Langs.EN to english,
            Langs.ZH_HANS to chinese,
        )
    }
}
