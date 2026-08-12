package io.github.tharukack.countrycodekit

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CountryCodePhoneValidatorTest {
    @Test
    fun validatesAndFormatsRepresentativeNumbers() {
        val cases = listOf(
            Triple("AU", "0412 345 678", "+61412345678"),
            Triple("US", "202-555-0123", "+12025550123"),
            Triple("GB", "020 7946 0018", "+442079460018"),
        )
        cases.forEach { (region, input, e164) ->
            val result = CountryCodePhoneValidator.validate(input, region)
            assertTrue(result.isValid, "$region: $result")
            assertEquals(e164, result.e164)
            assertTrue(result.international!!.startsWith("+"))
        }
    }

    @Test
    fun internationalInputOverridesDefaultRegion() {
        val result = CountryCodePhoneValidator.validate("+44 20 7946 0018", "AU")
        assertEquals(CountryCodePhoneStatus.VALID, result.status)
        assertEquals("+442079460018", result.e164)
    }

    @Test
    fun distinguishesCommonInvalidInputs() {
        assertEquals(CountryCodePhoneStatus.EMPTY, CountryCodePhoneValidator.validate("  ", "AU").status)
        assertEquals(CountryCodePhoneStatus.INVALID_REGION, CountryCodePhoneValidator.validate("123", "ZZ").status)
        assertEquals(CountryCodePhoneStatus.NOT_A_NUMBER, CountryCodePhoneValidator.validate("hello", "AU").status)
        assertEquals(CountryCodePhoneStatus.IMPOSSIBLE, CountryCodePhoneValidator.validate("12", "AU").status)
        val invalid = CountryCodePhoneValidator.validate("1234567890", "US")
        assertEquals(CountryCodePhoneStatus.INVALID, invalid.status)
        assertFalse(invalid.isValid)
    }

    @Test
    fun digitsOnlyRejectsFormattingCharactersWithoutRequiringACountry() {
        val valid = CountryCodePhoneValidator.validate(
            number = "0412345678",
            defaultRegion = "ZZ",
            preset = CountryCodePhoneValidationPreset.DigitsOnly,
        )
        val invalid = CountryCodePhoneValidator.validate(
            number = "0412 345 678",
            defaultRegion = "AU",
            preset = CountryCodePhoneValidationPreset.DigitsOnly,
        )

        assertTrue(valid.isValid)
        assertEquals("0412345678", valid.normalizedDigits)
        assertEquals(CountryCodePhoneStatus.NON_DIGIT_CHARACTERS, invalid.status)
        assertEquals("0412345678", invalid.normalizedDigits)
    }

    @Test
    fun possibleLengthDoesNotRequireTheNumberToBeAssigned() {
        val result = CountryCodePhoneValidator.validate(
            number = "1234567890",
            defaultRegion = "US",
            preset = CountryCodePhoneValidationPreset.PossibleLength,
        )

        assertTrue(result.isValid)
        assertEquals("+11234567890", result.e164)
    }

    @Test
    fun digitsAndPossibleLengthAppliesBothChecks() {
        val formatted = CountryCodePhoneValidator.validate(
            number = "202 555 0123",
            defaultRegion = "US",
            preset = CountryCodePhoneValidationPreset.DigitsAndPossibleLength,
        )
        val tooShort = CountryCodePhoneValidator.validate(
            number = "12",
            defaultRegion = "US",
            preset = CountryCodePhoneValidationPreset.DigitsAndPossibleLength,
        )

        assertEquals(CountryCodePhoneStatus.NON_DIGIT_CHARACTERS, formatted.status)
        assertEquals(CountryCodePhoneStatus.IMPOSSIBLE, tooShort.status)
    }

    @Test
    fun customLengthCountsNormalizedDigitsAndCanRequireDigitsOnly() {
        val flexible = CountryCodePhoneValidator.validate(
            number = "123 456",
            defaultRegion = "ZZ",
            preset = CountryCodePhoneValidationPreset.CustomLength(6..8),
        )
        val strict = CountryCodePhoneValidator.validate(
            number = "123 456",
            defaultRegion = "AU",
            preset = CountryCodePhoneValidationPreset.CustomLength(6..8, digitsOnly = true),
        )
        val short = CountryCodePhoneValidator.validate(
            number = "12345",
            defaultRegion = "AU",
            preset = CountryCodePhoneValidationPreset.CustomLength(6..8),
        )
        val long = CountryCodePhoneValidator.validate(
            number = "123456789",
            defaultRegion = "AU",
            preset = CountryCodePhoneValidationPreset.CustomLength(6..8),
        )

        assertTrue(flexible.isValid)
        assertEquals(CountryCodePhoneStatus.NON_DIGIT_CHARACTERS, strict.status)
        assertEquals(CountryCodePhoneStatus.TOO_SHORT, short.status)
        assertEquals(CountryCodePhoneStatus.TOO_LONG, long.status)
    }
}
