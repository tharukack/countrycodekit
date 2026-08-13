package io.github.tharukack.countrycodekit.internal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSUserDefaults

private const val RECENT_ISO_CODES_KEY = "io.github.tharukack.countrycodekit.recentCountryIsoCodes"

private var sharedRepository: CountryCodeRecentsRepository? = null

@Composable
internal actual fun rememberCountryCodeRecentsRepository(): CountryCodeRecentsRepository = remember {
    sharedRepository ?: createRepository().also { sharedRepository = it }
}

private fun createRepository(): CountryCodeRecentsRepository {
    val defaults = NSUserDefaults.standardUserDefaults
    val initialIsoCodes = defaults.stringArrayForKey(RECENT_ISO_CODES_KEY)
        ?.filterIsInstance<String>()
        .orEmpty()
    return CountryCodeRecentsRepository(initialIsoCodes) { isoCodes ->
        defaults.setObject(isoCodes, forKey = RECENT_ISO_CODES_KEY)
    }
}
