package io.github.tharukack.countrycodekit

import androidx.compose.ui.text.AnnotatedString
import kotlin.test.Test
import kotlin.test.assertEquals

class CountryCodePhoneFormatterTest {
    private val australia = CountryCodeCatalog.findByIsoCode("AU")!!

    @Test
    fun formatsEverySupportedOutputStyle() {
        assertEquals(
            "0412 345 678",
            CountryCodePhoneFormatter.format("0412345678", australia, CountryCodePhoneFormat.National),
        )
        assertEquals(
            "+61 412 345 678",
            CountryCodePhoneFormatter.format("0412345678", australia, CountryCodePhoneFormat.International),
        )
        assertEquals(
            "+61412345678",
            CountryCodePhoneFormatter.format("0412345678", australia, CountryCodePhoneFormat.E164),
        )
        assertEquals(
            "tel:+61-412-345-678",
            CountryCodePhoneFormatter.format("0412345678", australia, CountryCodePhoneFormat.Rfc3966),
        )
    }

    @Test
    fun formatsAsYouTypeAndHandlesAlreadyFormattedInput() {
        assertEquals("0412 345 678", CountryCodePhoneFormatter.formatAsYouType("0412345678", australia))
        assertEquals("0412 345 678", CountryCodePhoneFormatter.formatAsYouType("0412 345 678", australia))
        assertEquals("0412 345", CountryCodePhoneFormatter.formatAsYouType("0412345", australia))
    }

    @Test
    fun visualTransformationFormatsWithoutChangingRawOffsets() {
        val transformed = CountryCodePhoneVisualTransformation(australia)
            .filter(AnnotatedString("0412345678"))

        assertEquals("0412 345 678", transformed.text.text)
        assertEquals(5, transformed.offsetMapping.originalToTransformed(4))
        assertEquals(4, transformed.offsetMapping.transformedToOriginal(5))
        assertEquals(12, transformed.offsetMapping.originalToTransformed(10))
    }

    @Test
    fun normalizesPastedPhoneInputForFieldState() {
        assertEquals("+61412345678", CountryCodePhoneFormatter.normalizeInput(" +61 412-345-678"))
    }

    @Test
    fun returnsOriginalInputWhenRegionOrNumberCannotBeFormatted() {
        assertEquals("hello", CountryCodePhoneFormatter.format("hello", "AU"))
        assertEquals("123", CountryCodePhoneFormatter.format("123", "ZZ"))
        assertEquals("123", CountryCodePhoneFormatter.formatAsYouType("123", "ZZ"))
    }
}
