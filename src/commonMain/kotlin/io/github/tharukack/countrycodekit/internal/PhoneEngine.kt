package io.github.tharukack.countrycodekit.internal

import io.michaelrocks.libphonenumber.kotlin.PhoneNumberUtil
import io.michaelrocks.libphonenumber.kotlin.metadata.defaultMetadataLoader

internal object PhoneEngine {
    val util: PhoneNumberUtil by lazy {
        PhoneNumberUtil.createInstance(defaultMetadataLoader())
    }
}
