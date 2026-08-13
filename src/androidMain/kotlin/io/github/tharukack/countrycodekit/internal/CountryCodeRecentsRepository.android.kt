package io.github.tharukack.countrycodekit.internal

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

private const val PREFERENCES_NAME = "countrycodekit_compose"
private const val RECENT_ISO_CODES_KEY = "recent_country_iso_codes"

private var sharedRepository: CountryCodeRecentsRepository? = null

@Composable
internal actual fun rememberCountryCodeRecentsRepository(): CountryCodeRecentsRepository {
    val applicationContext = LocalContext.current.applicationContext
    return remember(applicationContext) {
        sharedRepository ?: createRepository(applicationContext).also { sharedRepository = it }
    }
}

private fun createRepository(context: Context): CountryCodeRecentsRepository {
    val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    val initialIsoCodes = preferences.getString(RECENT_ISO_CODES_KEY, null)
        ?.split(',')
        .orEmpty()
        .filter(String::isNotBlank)
    return CountryCodeRecentsRepository(initialIsoCodes) { isoCodes ->
        preferences.edit()
            .putString(RECENT_ISO_CODES_KEY, isoCodes.joinToString(","))
            .apply()
    }
}
