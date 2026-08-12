package io.github.tharukack.countrycodekit

import io.github.tharukack.countrycodekit.internal.PhoneEngine
import io.michaelrocks.libphonenumber.kotlin.NumberParseException
import io.michaelrocks.libphonenumber.kotlin.PhoneNumberUtil.PhoneNumberFormat

enum class CountryCodePhoneStatus {
    EMPTY,
    INVALID_REGION,
    NOT_A_NUMBER,
    NON_DIGIT_CHARACTERS,
    TOO_SHORT,
    TOO_LONG,
    IMPOSSIBLE,
    INVALID,
    VALID,
}

/** Ready-to-use validation levels, from lightweight input checks to full phone validation. */
sealed interface CountryCodePhoneValidationPreset {
    /** Uses libphonenumber to require a possible and valid number for the selected country. */
    data object PhoneNumber : CountryCodePhoneValidationPreset

    /** Uses country metadata to check whether the number has a possible length. */
    data object PossibleLength : CountryCodePhoneValidationPreset

    /** Accepts ASCII digits only. No country-specific length check is performed. */
    data object DigitsOnly : CountryCodePhoneValidationPreset

    /** Requires ASCII digits and a possible country-specific number length. */
    data object DigitsAndPossibleLength : CountryCodePhoneValidationPreset

    /** Checks the digit count against [range], optionally rejecting every non-digit character. */
    data class CustomLength(
        val range: IntRange,
        val digitsOnly: Boolean = false,
    ) : CountryCodePhoneValidationPreset {
        init {
            require(!range.isEmpty() && range.first >= 1) {
                "Custom length range must be non-empty and start at one or greater."
            }
        }
    }
}

data class CountryCodePhoneResult(
    val status: CountryCodePhoneStatus,
    val e164: String? = null,
    val international: String? = null,
    val normalizedDigits: String = "",
) {
    val isValid: Boolean get() = status == CountryCodePhoneStatus.VALID
}

/** Parsing, formatting and validation backed by the bundled Google metadata. */
object CountryCodePhoneValidator {
    fun validate(number: String, country: CountryCode): CountryCodePhoneResult =
        validate(number, country.isoCode, CountryCodePhoneValidationPreset.PhoneNumber)

    fun validate(
        number: String,
        country: CountryCode,
        preset: CountryCodePhoneValidationPreset,
    ): CountryCodePhoneResult = validate(number, country.isoCode, preset)

    fun validate(number: String, defaultRegion: String): CountryCodePhoneResult =
        validate(number, defaultRegion, CountryCodePhoneValidationPreset.PhoneNumber)

    fun validate(
        number: String,
        defaultRegion: String,
        preset: CountryCodePhoneValidationPreset,
    ): CountryCodePhoneResult {
        val normalizedDigits = number.filter { it in '0'..'9' }
        if (number.isBlank()) {
            return CountryCodePhoneResult(
                status = CountryCodePhoneStatus.EMPTY,
                normalizedDigits = normalizedDigits,
            )
        }

        when (preset) {
            CountryCodePhoneValidationPreset.DigitsOnly -> {
                return digitsOnlyResult(number, normalizedDigits)
            }

            is CountryCodePhoneValidationPreset.CustomLength -> {
                if (preset.digitsOnly && !number.isAsciiDigitsOnly()) {
                    return CountryCodePhoneResult(
                        status = CountryCodePhoneStatus.NON_DIGIT_CHARACTERS,
                        normalizedDigits = normalizedDigits,
                    )
                }
                return lengthResult(normalizedDigits, preset.range)
            }

            CountryCodePhoneValidationPreset.PhoneNumber,
            CountryCodePhoneValidationPreset.PossibleLength,
            CountryCodePhoneValidationPreset.DigitsAndPossibleLength,
            -> Unit
        }

        if (
            preset == CountryCodePhoneValidationPreset.DigitsAndPossibleLength &&
            !number.isAsciiDigitsOnly()
        ) {
            return CountryCodePhoneResult(
                status = CountryCodePhoneStatus.NON_DIGIT_CHARACTERS,
                normalizedDigits = normalizedDigits,
            )
        }

        val region = defaultRegion.uppercase()
        if (!PhoneEngine.util.getSupportedRegions().contains(region)) {
            return CountryCodePhoneResult(
                status = CountryCodePhoneStatus.INVALID_REGION,
                normalizedDigits = normalizedDigits,
            )
        }

        return try {
            val parsed = PhoneEngine.util.parse(number, region)
            val possible = PhoneEngine.util.isPossibleNumber(parsed)
            when {
                !possible -> CountryCodePhoneResult(
                    status = CountryCodePhoneStatus.IMPOSSIBLE,
                    normalizedDigits = normalizedDigits,
                )
                preset != CountryCodePhoneValidationPreset.PhoneNumber -> formattedResult(
                    parsed = parsed,
                    normalizedDigits = normalizedDigits,
                )
                !PhoneEngine.util.isValidNumber(parsed) -> CountryCodePhoneResult(
                    status = CountryCodePhoneStatus.INVALID,
                    normalizedDigits = normalizedDigits,
                )
                else -> CountryCodePhoneResult(
                    status = CountryCodePhoneStatus.VALID,
                    e164 = PhoneEngine.util.format(parsed, PhoneNumberFormat.E164),
                    international = PhoneEngine.util.format(parsed, PhoneNumberFormat.INTERNATIONAL),
                    normalizedDigits = normalizedDigits,
                )
            }
        } catch (_: NumberParseException) {
            CountryCodePhoneResult(
                status = CountryCodePhoneStatus.NOT_A_NUMBER,
                normalizedDigits = normalizedDigits,
            )
        }
    }

    private fun digitsOnlyResult(number: String, normalizedDigits: String): CountryCodePhoneResult =
        CountryCodePhoneResult(
            status = if (number.isAsciiDigitsOnly()) {
                CountryCodePhoneStatus.VALID
            } else {
                CountryCodePhoneStatus.NON_DIGIT_CHARACTERS
            },
            normalizedDigits = normalizedDigits,
        )

    private fun lengthResult(digits: String, range: IntRange): CountryCodePhoneResult =
        CountryCodePhoneResult(
            status = when {
                digits.length < range.first -> CountryCodePhoneStatus.TOO_SHORT
                digits.length > range.last -> CountryCodePhoneStatus.TOO_LONG
                else -> CountryCodePhoneStatus.VALID
            },
            normalizedDigits = digits,
        )

    private fun formattedResult(
        parsed: io.michaelrocks.libphonenumber.kotlin.Phonenumber.PhoneNumber,
        normalizedDigits: String,
    ) = CountryCodePhoneResult(
        status = CountryCodePhoneStatus.VALID,
        e164 = PhoneEngine.util.format(parsed, PhoneNumberFormat.E164),
        international = PhoneEngine.util.format(parsed, PhoneNumberFormat.INTERNATIONAL),
        normalizedDigits = normalizedDigits,
    )

    private fun String.isAsciiDigitsOnly(): Boolean = isNotEmpty() && all { it in '0'..'9' }
}
