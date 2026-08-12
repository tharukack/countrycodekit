package io.github.tharukack.countrycodekit

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CountryCodePhoneStateTest {
    private val australia = CountryCodeCatalog.findByIsoCode("AU")!!
    private val canada = CountryCodeCatalog.findByIsoCode("CA")!!

    @Test
    fun unifiedStateNormalizesValidatesAndDetectsCountry() {
        var persistedRawNumber = ""
        val state = CountryCodePhoneState(
            pickerState = CountryCodePickerState(canada),
            pickerConfig = CountryCodePickerConfig(),
            initialNumber = "",
            initialFormatAsYouType = true,
            processing = CountryCodePhoneProcessing.ValidateAndDetectCountry,
            validationPreset = CountryCodePhoneValidationPreset.PhoneNumber,
            onRawNumberChanged = { persistedRawNumber = it },
        )

        state.updateNumber("+61 412-345-678")

        assertEquals("+61412345678", state.rawNumber)
        assertEquals(state.rawNumber, persistedRawNumber)
        assertEquals(australia, state.selectedCountry)
        assertTrue(state.isValid)
        assertEquals("+61412345678", state.e164)
    }

    @Test
    fun detectionOnlyDoesNotCreateAValidationResult() {
        val state = CountryCodePhoneState(
            pickerState = CountryCodePickerState(canada),
            pickerConfig = CountryCodePickerConfig(),
            initialNumber = "",
            initialFormatAsYouType = true,
            processing = CountryCodePhoneProcessing.DetectCountry,
            validationPreset = CountryCodePhoneValidationPreset.PhoneNumber,
            onRawNumberChanged = {},
        )

        state.updateNumber("+61 412 345 678")

        assertEquals(australia, state.selectedCountry)
        assertNull(state.validation)
    }
}
