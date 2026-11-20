package demo.pages.pickers

import io.github.shijianjs.ktjs.demo.page.Blocks.col as kitCol
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.page.Blocks.row as kitRow
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.ui.EventLog
import demo.ui.PageFrame
import demo.ui.PageMeta
import demo.ui.Readout
import demo.ui.joinValues
import demo.ui.jsArray
import demo.ui.listen
import js.objects.unsafeJso
import kotlin.js.Date
import kotlin.js.JsAny
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.combobox.ComboBoxEvents
import io.github.shijianjs.ktjs.vaadin.combobox.createComboBox
import io.github.shijianjs.ktjs.vaadin.datepicker.DatePickerDate
import io.github.shijianjs.ktjs.vaadin.datepicker.DatePickerDateMetadata
import io.github.shijianjs.ktjs.vaadin.datepicker.DatePickerDateMetadataArrayOrPromise
import io.github.shijianjs.ktjs.vaadin.datepicker.DatePickerDateMetadataProvider
import io.github.shijianjs.ktjs.vaadin.datepicker.DatePickerEvents
import io.github.shijianjs.ktjs.vaadin.datepicker.DatePickerI18n
import io.github.shijianjs.ktjs.vaadin.datepicker.createDate
import io.github.shijianjs.ktjs.vaadin.datepicker.createDatePicker
import io.github.shijianjs.ktjs.vaadin.datepicker.extractDateParts
import io.github.shijianjs.ktjs.vaadin.datepicker.formatISODate
import io.github.shijianjs.ktjs.vaadin.datepicker.parseDate
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.vaadin.icons.registerVaadinIcons
import io.github.shijianjs.ktjs.vaadin.tooltip.createTooltip
import web.dom.clickEvent
import web.events.addHandler
import web.html.HtmlTagName

val datePickerEntry: CatalogEntry =
    CatalogEntry(
        id = "date-picker",
        group = Groups.PICKERS,
        meta =
            PageMeta(
                title = bi("Date Picker", "Date Picker"),
                summary =
                    bi(
                        "日期选择：值格式为 ISO 日期字符串，支持范围、禁用日期、周数与手动输入。",
                        "Date selection: the value is an ISO date string, with ranges, disabled dates, " +
                            "week numbers and manual input.",
                    ),
                docsUrl = "$DOCS_BASE/date-picker",
                sourcePath = "pages/pickers/DatePickerPage.kt",
            ),
        kitPage = { DatePickerPage.NAME() },
    )

internal class DatePickerPage : DemoPage() {
    private val texts = Binder(datePickerTexts)

    private val frame = PageFrame(datePickerEntry)

    private val basicReadout = Readout("value = 2026-10-25")

    private val validatedError = Readout("")

    private val weekdayError = Readout("")

    private val metadataReadout = Readout("")

    private val individualReadout = Readout("")

    private val rangeValue = Readout("")

    private val customFunctionsText = Readout("")

    private val validatedPicker = createDatePicker()

    private val weekdayPicker = createDatePicker()

    private val departurePicker = createDatePicker()

    private val returnPicker = createDatePicker()

    private val customFunctionsPicker = createDatePicker()

    private val metadataPicker = createDatePicker()

    private val currentYear = Date().getFullYear()

    private val monthLabels =
        listOf(
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December",
        )

    private val yearCombo = createComboBox()
    private val monthCombo = createComboBox()
    private val dayCombo = createComboBox()

    private var basicValue: String? = null
    private var validatedMessage: DateMessage? = null
    private var weekdayMessage: DateMessage? = null
    private var metadataValue: String? = null
    private var selectedYear: Int? = null
    private var selectedMonth: Int? = null
    private var selectedDay: Int? = null
    private var departureValue: String = ""
    private var returnValue: String = ""
    private var inputTextValue: String? = null

    private fun message(copy: DatePickerTexts, which: DateMessage?): String =
        when (which) {
            null -> copy.none
            DateMessage.NONE -> copy.none
            DateMessage.BAD_FORMAT -> copy.badFormat
            DateMessage.REQUIRED -> copy.required
            DateMessage.TOO_EARLY -> copy.tooEarly
            DateMessage.TOO_LATE -> copy.tooLate
            DateMessage.CLOSED -> copy.closed
            DateMessage.WEEKDAY -> copy.selectWeekday
        }

    private fun showReadouts() {
        val copy = texts.current
        basicReadout.set("value = ", (basicValue ?: "2026-10-25").ifEmpty { copy.empty })
        validatedError.set(copy.errorPrefix, message(copy, validatedMessage))
        weekdayError.set(copy.errorPrefix, message(copy, weekdayMessage))
        metadataReadout.set(
            if (metadataValue == null) copy.providerReady else copy.chosenDate + metadataValue,
        )
        individualReadout.set(
            copy.selectedDate,
            if (selectedYear != null && selectedMonth != null && selectedDay != null) {
                formatISODate(
                    createDate(selectedYear!!.toDouble(), selectedMonth!!.toDouble(), selectedDay!!.toDouble()),
                )
            } else {
                copy.empty
            },
        )
        rangeValue.set(
            copy.rangeReadout(
                departureValue.ifEmpty { copy.empty },
                returnValue.ifEmpty { copy.empty },
            ),
        )
        customFunctionsText.set(copy.inputText, inputTextValue ?: customFunctionsPicker.value)
    }

    init {
        frame.mount(this)
        registerVaadinIcons()
        val todayDate = todayAtMidnight()

        // --- 1. Basic usage (date-picker-basic.ts) -------------------------------------------------
        val basic = SectionBlock.NAME { show() }
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        val basicLog = EventLog(6)
        val basicPicker = createDatePicker()
        texts.bind(basicPicker, { it.startDate }) { basicPicker.label = it }
        texts.bind(basicPicker, { it.basicHelper }) { basicPicker.helperText = it }
        basicPicker.value = "2026-10-25"
        basicPicker.clearButtonVisible = true
        basicPicker.dataset["test"] = "date-picker-basic"
        basicPicker.listen(DatePickerEvents.valueChanged) {
            basicValue = basicPicker.value
            showReadouts()
            basicLog.add("value-changed · ${basicPicker.value}")
        }
        basicPicker.listen(DatePickerEvents.unparsableChange) { basicLog.add("unparsable-change") }
        val eventCaption = HtmlTagName.p()
        texts.bind(eventCaption, { it.eventStream }) { eventCaption.textContent = it }
        basic.body.append(
            exampleBox(null, kitCol(basicPicker, basicReadout.root)),
            exampleBox(null, kitCol(eventCaption, basicLog.root)),
        )
        body.append(basic.root)

        // --- 2. Validation (date-picker-validation.ts) ---------------------------------------------
        val validation = SectionBlock.NAME { show() }
        texts.bind(validation.heading) { it.validationHeading }
        texts.bind(validation.hint) { it.validationHint }
        val closedDates = listOf(formatISODate(addDays(todayDate, 3)), formatISODate(addDays(todayDate, 4)))
        fun officeClosed(iso: String): Boolean {
            val date = parseDate(iso) ?: return false
            return isWeekend(date) || iso == closedDates[0] || iso == closedDates[1]
        }
        texts.bind(validatedPicker, { it.appointmentDate }) { validatedPicker.label = it }
        texts.bind(validatedPicker, { it.businessDayHelper }) { validatedPicker.helperText = it }
        validatedPicker.required = true
        validatedPicker.min = formatISODate(todayDate)
        validatedPicker.max = formatISODate(addDays(todayDate, 60))
        validatedPicker.isDateDisabled = { parts -> officeClosed(isoOf(parts)) }
        validatedPicker.listen(DatePickerEvents.validated) {
            val value = validatedPicker.value
            val which =
                when {
                    value.isEmpty() && rawInputText(validatedPicker).isNotEmpty() -> DateMessage.BAD_FORMAT
                    value.isEmpty() -> DateMessage.REQUIRED
                    value < validatedPicker.min -> DateMessage.TOO_EARLY
                    value > validatedPicker.max -> DateMessage.TOO_LATE
                    officeClosed(value) -> DateMessage.CLOSED
                    else -> DateMessage.NONE
                }
            validatedMessage = which
            validatedPicker.errorMessage = message(texts.current, which).takeIf { which != DateMessage.NONE } ?: ""
            showReadouts()
        }
        val validateDate = createButton()
        texts.bind(validateDate) { it.validate }
        validateDate.clickEvent.addHandler { validatedPicker.validate() }
        validation.body.append(
            exampleBox(null, kitCol(validatedPicker, kitRow(validateDate, validatedError.root))),
        )
        body.append(validation.root)

        // --- 3. Custom validation (date-picker-custom-validation.ts) --------------------------------
        val weekday = SectionBlock.NAME { show() }
        texts.bind(weekday.heading) { it.customHeading }
        texts.bind(weekday.hint) { it.customHint }
        texts.bind(weekdayPicker, { it.meetingDate }) { weekdayPicker.label = it }
        texts.bind(weekdayPicker, { it.weekdaysOnly }) { weekdayPicker.helperText = it }
        weekdayPicker.listen(DatePickerEvents.validated) {
            val date = parseDate(weekdayPicker.value)
            val which =
                when {
                    weekdayPicker.value.isEmpty() && rawInputText(weekdayPicker).isNotEmpty() -> DateMessage.BAD_FORMAT
                    weekdayPicker.value.isEmpty() -> DateMessage.REQUIRED
                    date == null || isWeekend(date) -> DateMessage.WEEKDAY
                    else -> DateMessage.NONE
                }
            weekdayMessage = which
            weekdayPicker.errorMessage = message(texts.current, which).takeIf { which != DateMessage.NONE } ?: ""
            // The component only renders errorMessage while `invalid` is set, like Binder does.
            weekdayPicker.invalid = weekdayPicker.errorMessage.isNotEmpty()
            showReadouts()
        }
        val validateWeekday = createButton()
        texts.bind(validateWeekday) { it.validate }
        validateWeekday.clickEvent.addHandler { weekdayPicker.validate() }
        weekday.body.append(
            exampleBox(null, kitCol(weekdayPicker, kitRow(validateWeekday, weekdayError.root))),
        )
        body.append(weekday.root)

        // --- 4. Date metadata (date-picker-date-metadata.ts) ----------------------------------------
        val metadata = SectionBlock.NAME { show() }
        texts.bind(metadata.heading) { it.metadataHeading }
        texts.bind(metadata.hint) { it.metadataHint }
        val metadataProvider: DatePickerDateMetadataProvider = { range ->
            val start = parseDate(range.start)
            val end = parseDate(range.end)
            if (start == null || end == null) {
                null
            } else {
                val entries = mutableListOf<DatePickerDateMetadata>()
                var day: Date = start
                var guard = 0
                while (day.getTime() <= end.getTime() && guard < 400) {
                    val iso = formatISODate(day)
                    val dayOfMonth = day.getDate()
                    when {
                        // Fully booked: every 7th day, offset so the pattern is visible in any month.
                        dayOfMonth % 7 == 3 ->
                            entries +=
                                unsafeJso<DatePickerDateMetadata> {
                                    date = iso
                                    disabled = true
                                }
                        // Almost full: gets the `limited` part name instead of being disabled.
                        dayOfMonth % 5 == 0 ->
                            entries +=
                                unsafeJso<DatePickerDateMetadata> {
                                    date = iso
                                    part = "limited"
                                }
                    }
                    day = addDays(day, 1)
                    guard++
                }
                DatePickerDateMetadataArrayOrPromise(entries.toTypedArray())
            }
        }
        texts.bind(metadataPicker, { it.appointmentDate }) { metadataPicker.label = it }
        texts.bind(metadataPicker, { it.almostFullHelper }) { metadataPicker.helperText = it }
        metadataPicker.dateMetadataProvider = metadataProvider
        metadataPicker.listen(DatePickerEvents.valueChanged) {
            metadataValue = metadataPicker.value
            showReadouts()
        }
        metadata.body.append(exampleBox(null, kitCol(metadataPicker, metadataReadout.root)))
        body.append(metadata.root)

        // --- 5. Week numbers (date-picker-week-numbers.ts) ------------------------------------------
        val weekNumbers = SectionBlock.NAME { show() }
        texts.bind(weekNumbers.heading) { it.weekNumbersHeading }
        texts.bind(weekNumbers.hint) { it.weekNumbersHint }
        val weekPicker = createDatePicker()
        texts.bind(weekPicker, { it.vacationStart }) { weekPicker.label = it }
        weekPicker.showWeekNumbers = true
        weekPicker.i18n = unsafeJso<DatePickerI18n> { firstDayOfWeek = 1.0 }
        weekNumbers.body.append(exampleBox(null, weekPicker))
        body.append(weekNumbers.root)

        // --- 6. Initial position (date-picker-initial-position.ts) ----------------------------------
        val initial = SectionBlock.NAME { show() }
        texts.bind(initial.heading) { it.initialHeading }
        texts.bind(initial.hint) { it.initialHint }
        val initialIso = formatISODate(lastDayOfYear(todayDate))
        val initialPicker = createDatePicker()
        texts.bind(initialPicker, { it.q4Deadline }) { initialPicker.label = it }
        initialPicker.initialPosition = initialIso
        initial.body.append(exampleBox(null, kitCol(initialPicker, Readout("initialPosition = $initialIso").root)))
        body.append(initial.root)

        // --- 7. Auto open (date-picker-auto-open.ts) ------------------------------------------------
        val autoOpen = SectionBlock.NAME { show() }
        texts.bind(autoOpen.heading) { it.autoOpenHeading }
        texts.bind(autoOpen.hint) { it.autoOpenHint }
        val autoPicker = createDatePicker()
        texts.bind(autoPicker, { it.startDate }) { autoPicker.label = it }
        autoPicker.autoOpenDisabled = true
        autoPicker.value = "2026-01-15"
        autoPicker.dataset["test"] = "date-picker-manual"
        val autoOpened = Readout("opened = false")
        autoPicker.listen(DatePickerEvents.openedChanged) {
            autoOpened.set("opened = ${autoPicker.opened}")
        }
        val openButton = createButton().addTheme("tertiary")
        texts.bind(openButton) { it.open }
        openButton.clickEvent.addHandler { autoPicker.open() }
        val closeButton = createButton().addTheme("tertiary")
        texts.bind(closeButton) { it.close }
        closeButton.clickEvent.addHandler { autoPicker.close() }
        autoOpen.body.append(
            exampleBox(null, kitCol(autoPicker, kitRow(openButton, closeButton, autoOpened.root))),
        )
        body.append(autoOpen.root)

        // --- 8. Internationalization (date-picker-internationalization.ts) --------------------------
        val localization = SectionBlock.NAME { show() }
        texts.bind(localization.heading) { it.localizationHeading }
        texts.bind(localization.hint) { it.localizationHint }
        val germanPicker =
            createDatePicker {
                label = "Sitzungsdatum"
                i18n =
                    unsafeJso<DatePickerI18n> {
                        monthNames =
                            arrayOf(
                                "Januar",
                                "Februar",
                                "März",
                                "April",
                                "Mai",
                                "Juni",
                                "Juli",
                                "August",
                                "September",
                                "Oktober",
                                "November",
                                "Dezember",
                            )
                        weekdays =
                            arrayOf("Sonntag", "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag")
                        weekdaysShort = arrayOf("So", "Mo", "Di", "Mi", "Do", "Fr", "Sa")
                        today = "Heute"
                        cancel = "Abbrechen"
                    }
            }
        val germanCaption = HtmlTagName.p()
        texts.bind(germanCaption, { it.germanCaption }) { germanCaption.textContent = it }
        localization.body.append(exampleBox(null, kitCol(germanCaption, germanPicker)))
        body.append(localization.root)

        // --- 9. Basic features (date-picker-basic-features.ts) --------------------------------------
        val basics = SectionBlock.NAME { show() }
        texts.bind(basics.heading) { it.basicsHeading }
        texts.bind(basics.hint) { it.basicsHint }
        val featuredPicker = createDatePicker()
        texts.bind(featuredPicker, { it.label }) { featuredPicker.label = it }
        texts.bind(featuredPicker, { it.helperText }) { featuredPicker.helperText = it }
        texts.bind(featuredPicker, { it.placeholder }) { featuredPicker.placeholder = it }
        featuredPicker.clearButtonVisible = true
        val tooltip = createTooltip { slot = "tooltip" }
        texts.bind(tooltip, { it.tooltipText }) { tooltip.text = it }
        featuredPicker.append(tooltip)
        featuredPicker.append(createIcon { icon = "vaadin:vaadin-h" }.apply { slot = "prefix" })
        basics.body.append(exampleBox(null, featuredPicker))
        body.append(basics.root)

        // --- 10. Read-only and disabled (date-picker-readonly-and-disabled.ts) ----------------------
        val states = SectionBlock.NAME { show() }
        texts.bind(states.heading) { it.statesHeading }
        texts.bind(states.hint) { it.statesHint }
        val readOnlyPicker = createDatePicker()
        texts.bind(readOnlyPicker, { it.readOnly }) { readOnlyPicker.label = it }
        readOnlyPicker.value = "2020-06-12"
        readOnlyPicker.readonly = true
        val disabledPicker = createDatePicker()
        texts.bind(disabledPicker, { it.disabled }) { disabledPicker.label = it }
        disabledPicker.disabled = true
        states.body.append(exampleBox(null, kitRow(readOnlyPicker, disabledPicker)))
        body.append(states.root)

        // --- 11. Date range (date-picker-date-range.ts) ---------------------------------------------
        val range = SectionBlock.NAME { show() }
        texts.bind(range.heading) { it.rangeHeading }
        texts.bind(range.hint) { it.rangeHint }
        texts.bind(departurePicker, { it.departure }) { departurePicker.label = it }
        texts.bind(returnPicker, { it.returnDate }) { returnPicker.label = it }
        departureValue = departurePicker.value
        returnValue = returnPicker.value
        fun syncRange() {
            departurePicker.max = returnPicker.value
            returnPicker.min = departurePicker.value
            departureValue = departurePicker.value
            returnValue = returnPicker.value
            showReadouts()
        }
        departurePicker.listen(DatePickerEvents.valueChanged) { syncRange() }
        returnPicker.listen(DatePickerEvents.valueChanged) { syncRange() }
        range.body.append(exampleBox(null, kitCol(kitRow(departurePicker, returnPicker), rangeValue.root)))
        body.append(range.root)

        // --- 12. Individual input fields (date-picker-individual-input-fields.ts) -------------------
        val individual = SectionBlock.NAME { show() }
        texts.bind(individual.heading) { it.individualHeading }
        texts.bind(individual.hint) { it.individualHint }
        texts.bind(yearCombo, { it.year }) { yearCombo.label = it }
        yearCombo.items = numberItems((currentYear - 99..currentYear).toList())
        yearCombo.style.setProperty("width", "7em")
        texts.bind(monthCombo, { it.month }) { monthCombo.label = it }
        monthCombo.items = jsArray<JsAny>(monthLabels.joinToString(",", "[", "]") { "\"$it\"" })
        monthCombo.disabled = true
        monthCombo.style.setProperty("width", "10em")
        texts.bind(dayCombo, { it.day }) { dayCombo.label = it }
        dayCombo.items = numberItems(emptyList())
        dayCombo.disabled = true
        dayCombo.style.setProperty("width", "6em")
        fun refreshDays() {
            val year = selectedYear
            val month = selectedMonth
            val days = if (year != null && month != null) createDate(year.toDouble(), (month + 1).toDouble(), 0.0).getDate() else 0
            dayCombo.items = numberItems((1..days).toList())
            dayCombo.disabled = days == 0
            dayCombo.selectedItem = null
            selectedDay = null
        }
        yearCombo.listen(ComboBoxEvents.selectedItemChanged) {
            selectedYear = joinValues(yearCombo.selectedItem).toIntOrNull()
            monthCombo.selectedItem = null
            monthCombo.disabled = selectedYear == null
            selectedMonth = null
            refreshDays()
            showReadouts()
        }
        monthCombo.listen(ComboBoxEvents.selectedItemChanged) {
            selectedMonth = monthLabels.indexOf(joinValues(monthCombo.selectedItem)).takeIf { it >= 0 }
            refreshDays()
            showReadouts()
        }
        dayCombo.listen(ComboBoxEvents.selectedItemChanged) {
            selectedDay = joinValues(dayCombo.selectedItem).toIntOrNull()
            showReadouts()
        }
        individual.body.append(exampleBox(null, kitCol(kitRow(yearCombo, monthCombo, dayCombo), individualReadout.root)))
        body.append(individual.root)

        // --- 13. Show the date format (date-picker-date-format-indicator.ts) -------------------------
        val formatHint = SectionBlock.NAME { show() }
        texts.bind(formatHint.heading) { it.formatHeading }
        texts.bind(formatHint.hint) { it.formatHint }
        val formatPicker = createDatePicker()
        texts.bind(formatPicker, { it.startDate }) { formatPicker.label = it }
        texts.bind(formatPicker, { it.formatHelper }) { formatPicker.helperText = it }
        formatPicker.placeholder = "DD/MM/YYYY"
        formatHint.body.append(exampleBox(null, formatPicker))
        body.append(formatHint.root)

        // --- 14. Styles (date-picker-styles.ts) -----------------------------------------------------
        val styles = SectionBlock.NAME { show() }
        texts.bind(styles.heading) { it.stylesHeading }
        texts.bind(styles.hint) { it.stylesHint }
        val styledPicker = createDatePicker()
        texts.bind(styledPicker, { it.label }) { styledPicker.label = it }
        texts.bind(styledPicker, { it.helperText }) { styledPicker.helperText = it }
        styledPicker.value = "2020-06-12"
        styledPicker.addTheme("align-right", "small", "helper-above-field")
        styles.body.append(exampleBox(null, styledPicker))
        body.append(styles.root)

        // --- 15. Custom format functions (date-formats.adoc, custom-functions.ts) -------------------
        val customFunctions = SectionBlock.NAME { show() }
        texts.bind(customFunctions.heading) { it.customFunctionsHeading }
        texts.bind(customFunctions.hint) { it.customFunctionsHint }
        texts.bind(customFunctionsPicker, { it.selectDate }) { customFunctionsPicker.label = it }
        texts.bind(customFunctionsPicker, { it.isoHelper }) { customFunctionsPicker.helperText = it }
        customFunctionsPicker.value = formatISODate(todayDate)
        customFunctionsPicker.i18n =
            unsafeJso<DatePickerI18n> {
                formatDate = { parts -> isoOf(parts) }
                // Fully qualified: `parseDate` also names the property being assigned here.
                parseDate = { text -> io.github.shijianjs.ktjs.vaadin.datepicker.parseDate(text)?.let(::extractDateParts) }
            }
        customFunctionsPicker.listen(DatePickerEvents.valueChanged) {
            inputTextValue = customFunctionsPicker.value
            showReadouts()
        }
        customFunctions.body.append(exampleBox(null, kitCol(customFunctionsPicker, customFunctionsText.root)))
        body.append(customFunctions.root)

        // --- 16. Custom parse formats (date-formats.adoc, custom-format.ts, Flow only) -------------
        val customFormats = SectionBlock.NAME { show() }
        texts.bind(customFormats.heading) { it.customFormatsHeading }
        texts.bind(customFormats.hint) { it.customFormatsHint }
        val customFormatsPicker = createDatePicker()
        texts.bind(customFormatsPicker, { it.selectDate }) { customFormatsPicker.label = it }
        texts.bind(customFormatsPicker, { it.flexibleHelper }) { customFormatsPicker.helperText = it }
        customFormatsPicker.value = formatISODate(todayDate)
        customFormatsPicker.i18n =
            unsafeJso<DatePickerI18n> {
                formatDate = { parts -> isoOf(parts) }
                parseDate = { text -> parseFlexibleDate(text) }
            }
        customFormats.body.append(exampleBox(null, customFormatsPicker))
        body.append(customFormats.root)

        // --- 17. Locale (date-formats.adoc, locale.ts, Flow only) -----------------------------------
        val locale = SectionBlock.NAME { show() }
        texts.bind(locale.heading) { it.localeHeading }
        texts.bind(locale.hint) { it.localeHint }
        val localePicker = createDatePicker()
        texts.bind(localePicker, { it.selectDate }) { localePicker.label = it }
        texts.bind(localePicker, { it.finnishHelper }) { localePicker.helperText = it }
        localePicker.value = formatISODate(todayDate)
        localePicker.i18n =
            unsafeJso<DatePickerI18n> {
                monthNames = FINNISH_MONTHS
                weekdays = FINNISH_WEEKDAYS
                weekdaysShort = FINNISH_WEEKDAYS_SHORT
                firstDayOfWeek = 1.0
                today = "Tänään"
                cancel = "Peruuta"
                formatDate = { parts -> formatFinnishDate(parts) }
                parseDate = { text -> parseFinnishDate(text) }
            }
        locale.body.append(exampleBox(null, localePicker))
        body.append(locale.root)

        applyTranslation(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = datePickerEntry.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        showReadouts()
        validatedPicker.errorMessage =
            message(texts.current, validatedMessage).takeIf { validatedMessage != DateMessage.NONE } ?: ""
        if (weekdayPicker.invalid) {
            weekdayPicker.errorMessage =
                message(texts.current, weekdayMessage).takeIf { weekdayMessage != DateMessage.NONE } ?: ""
        }
    }

    companion object : CustomElementReferenceAdapter<DatePickerPage>(DatePickerPage::class.js, "vaadin-demo-date-picker-page") {
        // ---------------------------------------------------------------------------------------------
        // Date picker
        // ---------------------------------------------------------------------------------------------

        private val english: DatePickerTexts =
            DatePickerTexts(
                basicHeading = "Basic usage",
                basicHint = "value is a YYYY-MM-DD string; the clear button empties it and an unparsable date fires " +
                    "unparsable-change.",
                startDate = "Start date",
                basicHelper = "Type a date or pick one from the calendar",
                empty = "(empty)",
                eventStream = "Event stream",
                validationHeading = "Validation",
                validationHint = "min / max and isDateDisabled decide which dates can be picked; the validated event " +
                    "sets errorMessage from the value. Weekends and the next two days (office closed) are blocked.",
                appointmentDate = "Appointment date",
                businessDayHelper = "Must be a business day within 60 days from today",
                errorPrefix = "Error message: ",
                none = "(none)",
                badFormat = "Invalid date format",
                required = "Field is required",
                tooEarly = "Too early, choose another date",
                tooLate = "Too late, choose another date",
                closed = "The office is closed, choose another date",
                validate = "Validate",
                customHeading = "Custom validation",
                customHint = "The docs example uses a Binder validator to allow weekdays only; this page performs the " +
                    "same check on validate() and writes the result back to errorMessage.",
                meetingDate = "Meeting date",
                weekdaysOnly = "Mondays – Fridays only",
                selectWeekday = "Select a weekday",
                metadataHeading = "Configuring individual dates",
                metadataHint = "dateMetadataProvider answers whole month ranges at once: disabled blocks a date and " +
                    "part adds a custom part name (here limited for almost-full days, styled with ::part(limited) from " +
                    "the component theme). The data is simulated locally, there is no backend call.",
                almostFullHelper = "Highlighted dates are almost full",
                providerReady = "Provider ready (answers whole months)",
                chosenDate = "Chosen date: ",
                weekNumbersHeading = "Week numbers",
                weekNumbersHint = "showWeekNumbers shows ISO week numbers; it only works when i18n.firstDayOfWeek is " +
                    "1 (Monday).",
                vacationStart = "Vacation start date",
                initialHeading = "Initial position",
                initialHint = "initialPosition decides which day the calendar opens on when there is no value; here " +
                    "it points at the last day of the current year.",
                q4Deadline = "Q4 deadline",
                autoOpenHeading = "Auto open",
                autoOpenHint = "autoOpenDisabled stops the calendar from opening on focus; the dropdown can still be " +
                    "opened and closed from outside.",
                open = "Open",
                close = "Close",
                localizationHeading = "Internationalization",
                localizationHint = "i18n overrides the month names, weekdays and the today / cancel buttons; only the " +
                    "changed fields are given, the rest is merged with the defaults.",
                germanCaption = "German (Deutsch)",
                basicsHeading = "Basic features",
                basicsHint = "helperText and placeholder guide the input, clearButtonVisible adds the clear button; " +
                    "the prefix slot holds an icon and the tooltip slot a hover description.",
                label = "Label",
                helperText = "Helper text",
                placeholder = "Placeholder",
                tooltipText = "Tooltip text",
                statesHeading = "Read-only and disabled",
                statesHint = "readonly allows focus and copying but no edits; disabled turns off all interaction.",
                readOnly = "Read-only",
                disabled = "Disabled",
                rangeHeading = "Date range",
                rangeHint = "The two pickers constrain each other: the return date cannot precede the departure date " +
                    "and the departure date cannot follow the return date.",
                departure = "Departure date",
                returnDate = "Return date",
                rangeReadout = { departure, ret -> "Departure: $departure · Return: $ret" },
                individualHeading = "Individual input fields",
                individualHint = "The docs example replaces the calendar with three combo boxes: the year and month " +
                    "decide how many days exist and the day options are rebuilt accordingly.",
                year = "Year",
                month = "Month",
                day = "Day",
                selectedDate = "Selected date: ",
                formatHeading = "Show the date format",
                formatHint = "Put the expected format in the placeholder and the helper text so the user does not " +
                    "have to guess how to type.",
                formatHelper = "Format: DD/MM/YYYY",
                stylesHeading = "Style variants",
                stylesHint = "align-right aligns the text to the trailing edge, small shrinks the field and " +
                    "helper-above-field moves the helper text above the input.",
                customFunctionsHeading = "Custom format functions",
                customFunctionsHint = "i18n.formatDate / i18n.parseDate own the text-to-date conversion; this page " +
                    "re-implements ISO 8601 in Kotlin, equivalent to the date-fns version in the docs.",
                selectDate = "Select a date:",
                isoHelper = "Date picker configured to use ISO 8601 format",
                inputText = "Input text: ",
                customFormatsHeading = "Multiple input formats",
                customFormatsHint = "The Flow example (date-picker-custom-format) configures one display format and " +
                    "several parse formats; the web component has a single parseDate, so this function tries " +
                    "yyyy-MM-dd, MM/dd/yyyy and dd.MM.yyyy in turn while the display stays yyyy-MM-dd.",
                flexibleHelper = "Type 2026-10-25, 10/25/2026 or 25.10.2026",
                localeHeading = "Locale",
                localeHint = "The Flow example (date-picker-locale) switches to the Finnish locale; the web component " +
                    "has no Locale property, so this page reproduces the effect with Finnish month and weekday names " +
                    "plus the d.M.yyyy format.",
                finnishHelper = "Date picker configured to use Finnish date format",
            )
        private val chinese: DatePickerTexts =
            DatePickerTexts(
                basicHeading = "基本用法",
                basicHint = "value 是 YYYY-MM-DD 字符串；清空按钮把值置为空，输入非法日期触发 unparsable-change。",
                startDate = "开始日期",
                basicHelper = "可以直接输入，也可以从日历选择",
                empty = "（空）",
                eventStream = "事件流",
                validationHeading = "校验",
                validationHint = "min / max 与 isDateDisabled 共同决定可选日期；validated 事件里按值设置 errorMessage，" +
                    "周末和最近两天（停业）都不可选。",
                appointmentDate = "预约日期",
                businessDayHelper = "必须是今天起 60 天内的工作日",
                errorPrefix = "错误信息：",
                none = "（无）",
                badFormat = "日期格式不正确",
                required = "此项为必填项",
                tooEarly = "日期过早，请另选一天",
                tooLate = "日期过晚，请另选一天",
                closed = "办公室不开放，请另选一天",
                validate = "校验",
                customHeading = "自定义校验",
                customHint = "文档示例用 Binder 的验证器限制工作日；这里在同一次 validate() 里做同样的判断，结果写回 errorMessage。",
                meetingDate = "会议日期",
                weekdaysOnly = "仅限周一至周五",
                selectWeekday = "请选择工作日",
                metadataHeading = "单个日期的配置",
                metadataHint = "dateMetadataProvider 按整个月的范围批量返回元数据：disabled 关闭日期，part 给日期加上自定义 " +
                    "part 名（示例里满额日期是 limited，需要用 ::part(limited) 在组件内部主题里着色）。数据在本地模拟，没有后端请求。",
                almostFullHelper = "高亮的日期接近满额",
                providerReady = "provider 已就绪（按月批量返回）",
                chosenDate = "所选日期：",
                weekNumbersHeading = "周数",
                weekNumbersHint = "showWeekNumbers 显示 ISO 周数，i18n.firstDayOfWeek 必须是 1（周一）才会生效。",
                vacationStart = "休假开始日期",
                initialHeading = "初始月份",
                initialHint = "initialPosition 决定没有值时日历打开到哪一天：这里定位到本年的最后一天。",
                q4Deadline = "第四季度截止日期",
                autoOpenHeading = "自动展开",
                autoOpenHint = "autoOpenDisabled 关闭「聚焦即弹出日历」，下拉仍可以用按钮从外部打开和关闭。",
                open = "打开",
                close = "关闭",
                localizationHeading = "本地化",
                localizationHint = "i18n 覆盖月份、星期、今天 / 取消按钮等文案；只给出需要改的字段，其余与默认值合并。",
                germanCaption = "德语（Deutsch）",
                basicsHeading = "基本特性",
                basicsHint = "helperText 与 placeholder 提供输入提示，clearButtonVisible 加清空按钮；prefix 插槽放图标，" +
                    "tooltip 插槽提供悬停说明。",
                label = "标签",
                helperText = "辅助文本",
                placeholder = "占位符",
                tooltipText = "提示文本",
                statesHeading = "只读与禁用",
                statesHint = "readonly 可以聚焦和复制但不能修改；disabled 关闭全部交互。",
                readOnly = "只读",
                disabled = "禁用",
                rangeHeading = "日期区间",
                rangeHint = "两个选择器互相约束：返回日期不早于出发日期，出发日期不晚于返回日期。",
                departure = "出发日期",
                returnDate = "返回日期",
                rangeReadout = { departure, ret -> "出发：$departure · 返回：$ret" },
                individualHeading = "分开的年月日字段",
                individualHint = "文档示例用三个 combo-box 取代日历：年、月决定天数，日选项随之重建。浏览器没有日期选择能力时" +
                    "这是一种可访问的替代方案。",
                year = "年",
                month = "月",
                day = "日",
                selectedDate = "已选日期：",
                formatHeading = "显示日期格式",
                formatHint = "把期望的格式写进 placeholder 与 helperText，用户不必猜输入方式。",
                formatHelper = "格式：DD/MM/YYYY",
                stylesHeading = "样式变体",
                stylesHint = "align-right 右对齐文本，small 缩小尺寸，helper-above-field 把辅助文本放到字段上方。",
                customFunctionsHeading = "自定义格式化函数",
                customFunctionsHint = "i18n.formatDate / i18n.parseDate 接管文本与日期的互转；这里用 Kotlin 重新实现了 ISO 8601，" +
                    "与文档中的 date-fns 版本等价。",
                selectDate = "选择日期：",
                isoHelper = "日期选择器使用 ISO 8601 格式",
                inputText = "输入框文本：",
                customFormatsHeading = "多种输入格式",
                customFormatsHint = "Flow 示例（date-picker-custom-format）配置一个显示格式和多个解析格式；Web Component 只有一个" +
                    " parseDate，所以这里在同一个函数里依次尝试 yyyy-MM-dd、MM/dd/yyyy 和 dd.MM.yyyy，显示始终是 yyyy-MM-dd。",
                flexibleHelper = "可输入 2026-10-25、10/25/2026 或 25.10.2026",
                localeHeading = "区域设置",
                localeHint = "Flow 示例（date-picker-locale）切换到芬兰语区域；浏览器端没有 Locale 属性，这里用 i18n 的" +
                    "芬兰语月份、星期与 d.M.yyyy 格式复现同样的效果。",
                finnishHelper = "日期选择器使用芬兰语格式",
            )
        private val datePickerTexts: Translations<DatePickerTexts> = texts(
            Langs.EN to english,
            Langs.ZH_HANS to chinese,
        )
        private fun lastDayOfYear(date: Date): Date = createDate(date.getFullYear().toDouble(), 11.0, 31.0)
        /** Formats date parts as ISO 8601, the display format of the web component by default. */
        private fun isoOf(parts: DatePickerDate): String = formatISODate(createDate(parts.year, parts.month, parts.day))
        /** The display format of the Flow custom-format example is ISO, the parse formats are three. */
        private fun parseFlexibleDate(text: String): DatePickerDate? =
            parseDate(text)?.let(::extractDateParts) ?: parseMonthDayYear(text) ?: parseDottedDayMonthYear(text)
        private fun formatFinnishDate(parts: DatePickerDate): String =
            "${parts.day.toInt()}.${parts.month.toInt() + 1}.${parts.year.toInt()}"
        private fun parseFinnishDate(text: String): DatePickerDate? {
            val pieces = text.trim().split(".")
            if (pieces.size != 3) return null
            val day = pieces[0].toIntOrNull() ?: return null
            val month = pieces[1].toIntOrNull() ?: return null
            val year = pieces[2].toIntOrNull() ?: return null
            return numericDate(year, month, day)
        }
        /** Builds the `items` array of a combo box from numbers. */
        private fun numberItems(values: List<Int>): Array<JsAny> = jsArray<JsAny>(values.joinToString(",", "[", "]"))
        private val FINNISH_MONTHS: Array<String> =
            arrayOf(
                "tammikuu",
                "helmikuu",
                "maaliskuu",
                "huhtikuu",
                "toukokuu",
                "kesäkuu",
                "heinäkuu",
                "elokuu",
                "syyskuu",
                "lokakuu",
                "marraskuu",
                "joulukuu",
            )
        private val FINNISH_WEEKDAYS: Array<String> =
            arrayOf("sunnuntai", "maanantai", "tiistai", "keskiviikko", "torstai", "perjantai", "lauantai")
        private val FINNISH_WEEKDAYS_SHORT: Array<String> = arrayOf("su", "ma", "ti", "ke", "to", "pe", "la")
    }
}
