package io.github.tharukack.countrycodekit

/** A country or territory that can receive international telephone calls. */
data class CountryCode(
    val isoCode: String,
    val name: String,
    val callingCode: Int,
) {
    val formattedCallingCode: String get() = "+$callingCode"
}
