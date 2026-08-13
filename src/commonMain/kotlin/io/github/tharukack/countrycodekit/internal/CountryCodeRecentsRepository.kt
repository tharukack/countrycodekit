package io.github.tharukack.countrycodekit.internal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.tharukack.countrycodekit.CountryCode
import io.github.tharukack.countrycodekit.CountryCodeCatalog

@Stable
internal class CountryCodeRecentsRepository(
    initialIsoCodes: List<String>,
    private val persist: (List<String>) -> Unit,
) {
    var recentSelections by mutableStateOf(resolve(initialIsoCodes))
        private set

    fun seedIfEmpty(countries: List<CountryCode>) {
        if (recentSelections.isEmpty() && countries.isNotEmpty()) {
            update(countries)
        }
    }

    fun select(country: CountryCode) {
        update(listOf(country) + recentSelections)
    }

    private fun update(countries: List<CountryCode>) {
        recentSelections = countries
            .distinctBy { it.isoCode.uppercase() }
            .take(MAX_RECENTS)
        persist(recentSelections.map(CountryCode::isoCode))
    }

    private fun resolve(isoCodes: List<String>): List<CountryCode> = isoCodes
        .mapNotNull { CountryCodeCatalog.findByIsoCode(it.trim()) }
        .distinctBy { it.isoCode.uppercase() }
        .take(MAX_RECENTS)

    private companion object {
        const val MAX_RECENTS = 3
    }
}

@Composable
internal expect fun rememberCountryCodeRecentsRepository(): CountryCodeRecentsRepository
