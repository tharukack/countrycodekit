package io.github.tharukack.countrycodekit

import io.github.tharukack.countrycodekit.internal.CountryCodeRecentsRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CountryCodePickerStateTest {
    private val australia = CountryCodeCatalog.findByIsoCode("AU")!!
    private val canada = CountryCodeCatalog.findByIsoCode("CA")!!

    @Test
    fun selectingCountryClosesAndClearsSearch() {
        val state = CountryCodePickerState(australia)
        state.open()
        state.updateQuery("can")
        state.select(canada)
        assertEquals(canada, state.selectedCountry)
        assertFalse(state.isOpen)
        assertEquals("", state.query)
    }

    @Test
    fun dismissKeepsSelection() {
        val state = CountryCodePickerState(australia)
        state.open()
        assertTrue(state.isOpen)
        state.dismiss()
        assertEquals(australia, state.selectedCountry)
        assertFalse(state.isOpen)
    }

    @Test
    fun selectionsAreStoredMostRecentFirstWithoutDuplicates() {
        val state = CountryCodePickerState(australia)

        state.select(canada)
        state.select(australia)
        state.select(canada)

        assertEquals(listOf("CA", "AU"), state.recentSelections.map(CountryCode::isoCode))
    }

    @Test
    fun recentSelectionsAreSharedPersistedAndLimitedToThree() {
        var persistedIsoCodes = emptyList<String>()
        val repository = CountryCodeRecentsRepository(emptyList()) { persistedIsoCodes = it }
        val firstState = CountryCodePickerState(australia, recentsRepository = repository)
        val secondState = CountryCodePickerState(canada, recentsRepository = repository)
        val unitedStates = CountryCodeCatalog.findByIsoCode("US")!!
        val newZealand = CountryCodeCatalog.findByIsoCode("NZ")!!

        firstState.select(canada)
        firstState.select(australia)
        firstState.select(unitedStates)
        firstState.select(newZealand)

        assertEquals(listOf("NZ", "US", "AU"), persistedIsoCodes)
        assertEquals(persistedIsoCodes, secondState.recentSelections.map(CountryCode::isoCode))

        val restoredRepository = CountryCodeRecentsRepository(persistedIsoCodes) { }
        val recreatedState = CountryCodePickerState(canada, recentsRepository = restoredRepository)
        assertEquals(persistedIsoCodes, recreatedState.recentSelections.map(CountryCode::isoCode))
    }

    @Test
    fun internationalNumberUpdatesCountryWithoutAddingARecentSelection() {
        val state = CountryCodePickerState(canada)

        val detected = CountryCodePhoneValidator(
            pickerState = state,
        ).detectCountry("+61 412 345 678")

        assertEquals(australia, detected)
        assertEquals(australia, state.selectedCountry)
        assertTrue(state.recentSelections.isEmpty())
    }

    @Test
    fun countryDetectionRequiresAnInternationalNumberAndRespectsTheCountryFilter() {
        val state = CountryCodePickerState(canada)

        assertEquals(
            null,
            CountryCodePhoneValidator(state).detectCountry("0412 345 678"),
        )
        assertEquals(canada, state.selectedCountry)

        assertEquals(
            null,
            CountryCodePhoneValidator(
                pickerState = state,
                countryFilter = CountryCodePickerCountryFilter.Supported(listOf("CA", "US")),
            ).detectCountry("+61 412 345 678"),
        )
        assertEquals(canada, state.selectedCountry)
    }

    @Test
    fun factoryCanValidateAndDetectCountryInOneOperation() {
        val state = CountryCodePickerState(canada)

        val result = CountryCodePhoneValidator(
            pickerState = state,
        ).validateAndDetectCountry("+61 412 345 678")

        assertTrue(result.isValid)
        assertEquals(australia, result.detectedCountry)
        assertEquals(australia, state.selectedCountry)
    }
}
