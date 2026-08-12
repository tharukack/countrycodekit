package io.github.tharukack.countrycodekit

import io.github.tharukack.countrycodekit.internal.PhoneEngine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CountryCodeCatalogTest {
    @Test
    fun catalogHasUniqueCompleteCountriesAndFlags() {
        val countries = CountryCodeCatalog.countries
        assertTrue(countries.size >= 240, "Expected the complete libphonenumber region catalog")
        assertEquals(countries.size, countries.map { it.isoCode }.distinct().size)
        countries.forEach { country ->
            assertTrue(country.isoCode.length == 2, country.isoCode)
            assertTrue(country.name.isNotBlank(), country.isoCode)
            assertTrue(country.callingCode > 0, country.isoCode)
            assertTrue(CountryCodeCatalog.hasBundledFlag(country.isoCode), "Missing ${country.isoCode} flag")
        }
    }

    @Test
    fun sharedCallingCodesRemainSeparateCountries() {
        assertEquals(1, CountryCodeCatalog.findByIsoCode("US")?.callingCode)
        assertEquals(1, CountryCodeCatalog.findByIsoCode("CA")?.callingCode)
        assertEquals(7, CountryCodeCatalog.findByIsoCode("RU")?.callingCode)
        assertEquals(7, CountryCodeCatalog.findByIsoCode("KZ")?.callingCode)
    }

    @Test
    fun searchMatchesNameIsoAndCountryCode() {
        assertEquals("AU", CountryCodeCatalog.search("australia").single().isoCode)
        assertEquals("GB", CountryCodeCatalog.search("GB").first().isoCode)
        assertTrue(CountryCodeCatalog.search("+61").any { it.isoCode == "AU" })
        assertTrue(CountryCodeCatalog.search("definitely nowhere").isEmpty())
    }

    @Test
    fun exceptionalTerritoryFlagsAreBundled() {
        assertTrue(CountryCodeCatalog.hasBundledFlag("AC"))
        assertTrue(CountryCodeCatalog.hasBundledFlag("TA"))
        assertTrue(CountryCodeCatalog.hasBundledFlag("XK"))
        assertTrue(!CountryCodeCatalog.hasBundledFlag("ZZ"))
        assertNotNull(CountryCodeCatalog.findByIsoCode("AC"))
    }

    @Test
    fun everyRegionMetadataFileLoadsAndItsExampleValidates() {
        CountryCodeCatalog.countries.forEach { country ->
            val example = assertNotNull(
                PhoneEngine.util.getExampleNumber(country.isoCode),
                "No example phone number for ${country.isoCode}",
            )
            val metadata = PhoneEngine.util.getMetadataForRegion(country.isoCode)
            val national = PhoneEngine.util.getNationalSignificantNumber(example)
            assertTrue(
                PhoneEngine.util.isValidNumberForRegion(example, country.isoCode),
                "Invalid upstream example for ${country.isoCode}: $example; " +
                    "global=${PhoneEngine.util.isValidNumber(example)}; " +
                    "resolved=${PhoneEngine.util.getRegionCodeForNumber(example)}; " +
                    "national=$national; " +
                    "general=${metadata?.generalDesc?.nationalNumberPattern}; " +
                    "generalDirect=${metadata?.generalDesc?.nationalNumberPattern?.let { Regex(it).matches(national) }}; " +
                    "fixed=${metadata?.fixedLine?.nationalNumberPattern}; " +
                    "fixedDirect=${metadata?.fixedLine?.nationalNumberPattern?.let { Regex(it).matches(national) }}; " +
                    "lengths=${metadata?.fixedLine?.possibleLengthList}",
            )
        }
    }
}
