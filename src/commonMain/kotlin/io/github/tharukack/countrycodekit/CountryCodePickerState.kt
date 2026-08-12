package io.github.tharukack.countrycodekit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@Stable
class CountryCodePickerState internal constructor(
    initialCountry: CountryCode,
    initialRecentSelections: List<CountryCode> = emptyList(),
) {
    var selectedCountry by mutableStateOf(initialCountry)
        private set
    var isOpen by mutableStateOf(false)
        private set
    var query by mutableStateOf("")
        private set
    var recentSelections by mutableStateOf(normalizeRecents(initialRecentSelections))
        private set

    fun open() { isOpen = true }
    fun dismiss() {
        isOpen = false
        query = ""
    }
    fun updateQuery(value: String) { query = value }
    fun select(country: CountryCode) {
        recentSelections = normalizeRecents(listOf(country) + recentSelections)
        selectedCountry = country
        dismiss()
    }

    private fun normalizeRecents(countries: List<CountryCode>): List<CountryCode> = countries
        .distinctBy { it.isoCode.uppercase() }
        .take(MAX_REMEMBERED_RECENTS)

    private companion object {
        const val MAX_REMEMBERED_RECENTS = 12
    }
}

@Composable
fun rememberCountryCodePickerState(
    initialCountry: CountryCode = CountryCodeCatalog.findByIsoCode("US")
        ?: CountryCodeCatalog.countries.first(),
    initialRecentSelections: List<CountryCode> = emptyList(),
): CountryCodePickerState {
    val isoCode = initialCountry.isoCode
    val initialRecentsKey = initialRecentSelections.joinToString(",") { it.isoCode.uppercase() }
    return rememberSaveable(isoCode, initialRecentsKey, saver = androidx.compose.runtime.saveable.Saver(
        save = {
            listOf(
                it.selectedCountry.isoCode,
                it.isOpen.toString(),
                it.query,
                it.recentSelections.joinToString(",") { country -> country.isoCode },
            )
        },
        restore = { values ->
            val restoredCountry = CountryCodeCatalog.findByIsoCode(values[0]) ?: initialCountry
            val initialCountriesByIso = initialRecentSelections.associateBy { it.isoCode.uppercase() }
            val restoredRecents = values.getOrElse(3) { "" }
                .split(',')
                .filter(String::isNotBlank)
                .mapNotNull { code ->
                    initialCountriesByIso[code.uppercase()] ?: CountryCodeCatalog.findByIsoCode(code)
                }
            CountryCodePickerState(restoredCountry, restoredRecents).also { state ->
                if (values[1].toBoolean()) state.open()
                state.updateQuery(values[2])
            }
        },
    )) { CountryCodePickerState(initialCountry, initialRecentSelections) }
}
