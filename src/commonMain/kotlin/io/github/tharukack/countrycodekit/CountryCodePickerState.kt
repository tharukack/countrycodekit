package io.github.tharukack.countrycodekit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import io.github.tharukack.countrycodekit.internal.CountryCodeRecentsRepository
import io.github.tharukack.countrycodekit.internal.rememberCountryCodeRecentsRepository

@Stable
class CountryCodePickerState internal constructor(
    initialCountry: CountryCode,
    initialRecentSelections: List<CountryCode> = emptyList(),
    private val recentsRepository: CountryCodeRecentsRepository? = null,
) {
    var selectedCountry by mutableStateOf(initialCountry)
        private set
    var isOpen by mutableStateOf(false)
        private set
    var query by mutableStateOf("")
        private set
    private var isolatedRecentSelections by mutableStateOf(normalizeRecents(initialRecentSelections))

    var recentSelections: List<CountryCode>
        get() = recentsRepository?.recentSelections ?: isolatedRecentSelections
        private set(value) {
            isolatedRecentSelections = normalizeRecents(value)
        }

    init {
        recentsRepository?.seedIfEmpty(initialRecentSelections)
    }

    fun open() { isOpen = true }
    fun dismiss() {
        isOpen = false
        query = ""
    }
    fun updateQuery(value: String) { query = value }
    fun select(country: CountryCode) {
        if (recentsRepository != null) {
            recentsRepository.select(country)
        } else {
            recentSelections = listOf(country) + isolatedRecentSelections
        }
        selectedCountry = country
        dismiss()
    }

    internal fun selectDetectedCountry(country: CountryCode) {
        selectedCountry = country
    }

    private fun normalizeRecents(countries: List<CountryCode>): List<CountryCode> = countries
        .distinctBy { it.isoCode.uppercase() }
        .take(MAX_REMEMBERED_RECENTS)

    private companion object {
        const val MAX_REMEMBERED_RECENTS = 3
    }
}

/**
 * Creates saveable picker state connected to the library's shared persistent recents.
 * [initialRecentSelections] seeds an empty stored list using ISO 3166-1 alpha-2 codes;
 * matching is case-insensitive and unknown codes are ignored.
 */
@Composable
fun rememberCountryCodePickerState(
    initialCountry: CountryCode = CountryCodeCatalog.findByIsoCode("US")
        ?: CountryCodeCatalog.countries.first(),
    initialRecentSelections: List<String> = emptyList(),
): CountryCodePickerState {
    val recentsRepository = rememberCountryCodeRecentsRepository()
    val isoCode = initialCountry.isoCode
    val initialRecentsKey = initialRecentSelections.joinToString(",") { it.trim().uppercase() }
    val initialRecentCountries = remember(initialRecentsKey) {
        initialRecentSelections.mapNotNull { CountryCodeCatalog.findByIsoCode(it.trim()) }
    }
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
            val restoredRecents = values.getOrElse(3) { "" }
                .split(',')
                .filter(String::isNotBlank)
                .mapNotNull(CountryCodeCatalog::findByIsoCode)
            CountryCodePickerState(restoredCountry, restoredRecents, recentsRepository).also { state ->
                if (values[1].toBoolean()) state.open()
                state.updateQuery(values[2])
            }
        },
    )) { CountryCodePickerState(initialCountry, initialRecentCountries, recentsRepository) }
}
