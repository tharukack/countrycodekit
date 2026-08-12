package io.github.tharukack.countrycodekit

import io.github.tharukack.countrycodekit.internal.PhoneEngine
import io.michaelrocks.libphonenumber.kotlin.NumberParseException
import io.michaelrocks.libphonenumber.kotlin.PhoneNumberUtil.PhoneNumberFormat

enum class CountryCodePhoneFormat {
    National,
    International,
    E164,
    Rfc3966,
}

/** Country-aware phone formatting backed by the bundled Google metadata. */
object CountryCodePhoneFormatter {
    fun format(
        number: String,
        country: CountryCode,
        format: CountryCodePhoneFormat = CountryCodePhoneFormat.National,
    ): String = format(number, country.isoCode, format)

    fun format(
        number: String,
        defaultRegion: String,
        format: CountryCodePhoneFormat = CountryCodePhoneFormat.National,
    ): String {
        if (number.isBlank()) return number
        val region = defaultRegion.trim().uppercase()
        if (region !in PhoneEngine.util.getSupportedRegions()) return number

        return try {
            val parsed = PhoneEngine.util.parse(number, region)
            PhoneEngine.util.format(parsed, format.toLibPhoneNumberFormat())
        } catch (_: NumberParseException) {
            number
        }
    }

    /**
     * Rebuilds country-aware formatting from the digits in [number]. This works well from a text
     * field's `onValueChange`, including paste and backspace operations.
     */
    fun formatAsYouType(number: String, country: CountryCode): String =
        formatAsYouType(number, country.isoCode)

    fun formatAsYouType(number: String, defaultRegion: String): String {
        if (number.isEmpty()) return number
        val region = defaultRegion.trim().uppercase()
        if (region !in PhoneEngine.util.getSupportedRegions()) return number

        val normalizedInput = buildString {
            if (number.trimStart().startsWith('+')) append('+')
            number.forEach { character ->
                if (character in '0'..'9') append(character)
            }
        }
        if (normalizedInput.isEmpty() || normalizedInput == "+") return normalizedInput

        val formatter = PhoneEngine.util.getAsYouTypeFormatter(region)
        var formatted = ""
        normalizedInput.forEach { character ->
            formatted = formatter.inputDigit(character)
        }
        return formatted
    }

    private fun CountryCodePhoneFormat.toLibPhoneNumberFormat(): PhoneNumberFormat = when (this) {
        CountryCodePhoneFormat.National -> PhoneNumberFormat.NATIONAL
        CountryCodePhoneFormat.International -> PhoneNumberFormat.INTERNATIONAL
        CountryCodePhoneFormat.E164 -> PhoneNumberFormat.E164
        CountryCodePhoneFormat.Rfc3966 -> PhoneNumberFormat.RFC3966
    }
}
