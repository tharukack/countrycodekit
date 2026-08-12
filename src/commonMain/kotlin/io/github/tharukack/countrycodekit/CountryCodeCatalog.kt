package io.github.tharukack.countrycodekit

import io.github.tharukack.countrycodekit.internal.PhoneEngine
import io.github.tharukack.countrycodekit.internal.countryNames
import io.github.tharukack.countrycodekit.internal.flagResourceFor

/** The catalog generated from the bundled Google libphonenumber metadata. */
object CountryCodeCatalog {
    val countries: List<CountryCode> by lazy {
        PhoneEngine.util.getSupportedRegions()
            .mapNotNull { isoCode ->
                val callingCode = PhoneEngine.util.getCountryCodeForRegion(isoCode)
                val name = countryNames[isoCode]
                if (callingCode > 0 && name != null) {
                    CountryCode(isoCode, name, callingCode)
                } else {
                    null
                }
            }
            .sortedBy(CountryCode::name)
    }

    fun findByIsoCode(isoCode: String): CountryCode? =
        countries.firstOrNull { it.isoCode.equals(isoCode, ignoreCase = true) }

    fun search(query: String, source: List<CountryCode> = countries): List<CountryCode> {
        val term = query.trim()
        if (term.isEmpty()) return source
        val normalizedCallingCode = term.removePrefix("+")
        return source.filter { country ->
            country.name.contains(term, ignoreCase = true) ||
                country.isoCode.contains(term, ignoreCase = true) ||
                country.callingCode.toString().startsWith(normalizedCallingCode)
        }
    }

    /** Useful when validating custom country lists before rendering them. */
    fun hasBundledFlag(isoCode: String): Boolean = flagResourceFor(isoCode) != null
}
