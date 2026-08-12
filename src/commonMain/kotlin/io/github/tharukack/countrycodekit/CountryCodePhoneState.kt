package io.github.tharukack.countrycodekit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.VisualTransformation

enum class CountryCodePhoneProcessing {
    None,
    Validate,
    DetectCountry,
    ValidateAndDetectCountry,
}

/**
 * Optional state holder that coordinates an app-owned phone field with a country picker.
 * The field itself remains owned and rendered by the host application.
 */
@Stable
class CountryCodePhoneState internal constructor(
    val pickerState: CountryCodePickerState,
    val pickerConfig: CountryCodePickerConfig,
    initialNumber: String,
    initialFormatAsYouType: Boolean,
    val processing: CountryCodePhoneProcessing,
    val validationPreset: CountryCodePhoneValidationPreset,
    private val onRawNumberChanged: (String) -> Unit,
) {
    var rawNumber by mutableStateOf(CountryCodePhoneFormatter.normalizeInput(initialNumber))
        private set

    var formatAsYouType by mutableStateOf(initialFormatAsYouType)

    var validation by mutableStateOf<CountryCodePhoneResult?>(
        if (processing.validates) {
            CountryCodePhoneResult(status = CountryCodePhoneStatus.EMPTY)
        } else {
            null
        },
    )
        private set

    val selectedCountry: CountryCode get() = pickerState.selectedCountry
    val isValid: Boolean get() = validation?.isValid == true
    val e164: String? get() = validation?.e164
    val international: String? get() = validation?.international
    val visualTransformation: VisualTransformation
        get() = if (formatAsYouType) {
            CountryCodePhoneVisualTransformation(pickerState.selectedCountry)
        } else {
            VisualTransformation.None
        }

    private val processor = CountryCodePhoneValidator(
        pickerState = pickerState,
        preset = validationPreset,
        countryFilter = pickerConfig.countryFilter,
    )
    private var lastProcessedCountryIsoCode = pickerState.selectedCountry.isoCode

    fun updateNumber(input: String) {
        rawNumber = CountryCodePhoneFormatter.normalizeInput(input)
        onRawNumberChanged(rawNumber)
        processNumber()
    }

    internal fun processInitialNumber() {
        processNumber()
    }

    internal fun onPickerCountryChanged() {
        val selectedIsoCode = pickerState.selectedCountry.isoCode
        if (selectedIsoCode == lastProcessedCountryIsoCode) return
        if (processing.validates) {
            validation = processor.validate(rawNumber)
        }
        lastProcessedCountryIsoCode = selectedIsoCode
    }

    private fun processNumber() {
        when (processing) {
            CountryCodePhoneProcessing.None -> validation = null
            CountryCodePhoneProcessing.Validate -> validation = processor.validate(rawNumber)
            CountryCodePhoneProcessing.DetectCountry -> {
                processor.detectCountry(rawNumber)
                validation = null
            }
            CountryCodePhoneProcessing.ValidateAndDetectCountry -> {
                validation = processor.validateAndDetectCountry(rawNumber)
            }
        }
        lastProcessedCountryIsoCode = pickerState.selectedCountry.isoCode
    }
}

@Composable
fun rememberCountryCodePhoneState(
    initialCountry: CountryCode = CountryCodeCatalog.findByIsoCode("US")
        ?: CountryCodeCatalog.countries.first(),
    initialNumber: String = "",
    formatAsYouType: Boolean = true,
    processing: CountryCodePhoneProcessing = CountryCodePhoneProcessing.ValidateAndDetectCountry,
    validationPreset: CountryCodePhoneValidationPreset = CountryCodePhoneValidationPreset.PhoneNumber,
    pickerConfig: CountryCodePickerConfig = CountryCodePickerConfig(),
    initialRecentSelections: List<CountryCode> = emptyList(),
): CountryCodePhoneState {
    val pickerState = rememberCountryCodePickerState(initialCountry, initialRecentSelections)
    var savedRawNumber by rememberSaveable {
        mutableStateOf(CountryCodePhoneFormatter.normalizeInput(initialNumber))
    }
    val state = remember(pickerState, processing, validationPreset, pickerConfig) {
        CountryCodePhoneState(
            pickerState = pickerState,
            pickerConfig = pickerConfig,
            initialNumber = savedRawNumber,
            initialFormatAsYouType = formatAsYouType,
            processing = processing,
            validationPreset = validationPreset,
            onRawNumberChanged = { savedRawNumber = it },
        )
    }

    LaunchedEffect(state) {
        state.processInitialNumber()
    }
    LaunchedEffect(state, pickerState.selectedCountry.isoCode) {
        state.onPickerCountryChanged()
    }
    return state
}

private val CountryCodePhoneProcessing.validates: Boolean
    get() = this == CountryCodePhoneProcessing.Validate ||
        this == CountryCodePhoneProcessing.ValidateAndDetectCountry
