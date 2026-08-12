package io.github.tharukack.countrycodekit

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Displays country-aware phone-number spacing without changing the field's raw value.
 *
 * Store [CountryCodePhoneFormatter.normalizeInput] in the host application's field state and
 * recreate this transformation when the selected country changes.
 */
class CountryCodePhoneVisualTransformation(
    private val defaultRegion: String,
) : VisualTransformation {
    constructor(country: CountryCode) : this(country.isoCode)

    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        val formatted = CountryCodePhoneFormatter.formatAsYouType(raw, defaultRegion)
        if (formatted == raw) return TransformedText(text, OffsetMapping.Identity)

        val characterPositions = IntArray(raw.length)
        var formattedSearchStart = 0
        raw.forEachIndexed { rawIndex, character ->
            val formattedIndex = formatted.indexOf(character, formattedSearchStart)
            if (formattedIndex < 0) {
                return TransformedText(text, OffsetMapping.Identity)
            }
            characterPositions[rawIndex] = formattedIndex
            formattedSearchStart = formattedIndex + 1
        }

        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int = when {
                offset <= 0 -> 0
                offset >= raw.length -> formatted.length
                else -> characterPositions[offset]
            }

            override fun transformedToOriginal(offset: Int): Int =
                characterPositions.count { it < offset }.coerceAtMost(raw.length)
        }

        return TransformedText(AnnotatedString(formatted), mapping)
    }
}
