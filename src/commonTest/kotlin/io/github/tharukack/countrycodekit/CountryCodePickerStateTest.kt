package io.github.tharukack.countrycodekit

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
}
