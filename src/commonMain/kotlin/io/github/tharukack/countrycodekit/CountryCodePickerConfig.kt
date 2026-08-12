package io.github.tharukack.countrycodekit

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class CountryCodePickerStyle { BottomSheet, Dialog, FullScreen }

enum class CountryCodePickerTriggerElement { Flag, CountryCode, Chevron }

/** Restricts the picker with one mutually exclusive list of ISO 3166-1 alpha-2 codes. */
sealed interface CountryCodePickerCountryFilter {
    /** Shows every country in the bundled catalog. */
    data object All : CountryCodePickerCountryFilter

    /** Shows only countries whose ISO codes are in [isoCodes]. */
    data class Supported(val isoCodes: List<String>) : CountryCodePickerCountryFilter {
        init {
            require(isoCodes.isNotEmpty()) { "Supported country ISO codes cannot be empty." }
        }
    }

    /** Hides countries whose ISO codes are in [isoCodes]. */
    data class Unsupported(val isoCodes: List<String>) : CountryCodePickerCountryFilter
}

@Immutable
data class CountryCodePickerStrings(
    val title: String = "Select your country",
    val searchPlaceholder: String = "Search country or code",
    val recent: String = "Recent",
    val allCountries: String = "All countries",
    val searchResults: String = "Search results",
    val noResults: String = "No countries found",
    val close: String = "Close",
    val clearSearch: String = "Clear search",
)

@Immutable
data class CountryCodePickerListColors(
    val accent: Color = Color(0xFF46BD99),
    val accentStrong: Color = Color(0xFF2F987A),
    val sheetContainer: Color = Color(0xFFFFFFFF),
    val searchContainer: Color = Color(0xFFF4F6F6),
    val selectedContainer: Color = Color(0xFFEEF8F4),
    val selectedContent: Color = Color(0xFF132E27),
    val content: Color = Color(0xFF17243A),
    val secondaryContent: Color = Color(0xFF7D898D),
    val divider: Color = Color(0xFFE3E9E7),
    val scrim: Color = Color(0x52000000),
)

@Immutable
data class CountryCodePickerTriggerColors(
    val container: Color = Color(0xFFFFFFFF),
    val content: Color = Color(0xFF17243A),
    val chevron: Color = Color(0xFF7D898D),
    val border: Color = Color.Transparent,
)

@Immutable
data class CountryCodePickerTriggerConfig(
    val triggerElements: Set<CountryCodePickerTriggerElement> = CountryCodePickerTriggerElement.entries.toSet(),
    val shape: Shape = RoundedCornerShape(14.dp),
    val borderWidth: Dp = 0.dp,
    val countryCodeTextStyle: TextStyle = TextStyle(fontWeight = FontWeight.SemiBold),
    val chevronSize: Dp = 16.dp,
    val horizontalPadding: Dp = 12.dp,
    val verticalPadding: Dp = 9.dp,
    val elementSpacing: Dp = 8.dp,
    val colors: CountryCodePickerTriggerColors = CountryCodePickerTriggerColors(),
) {
    init {
        require(triggerElements.isNotEmpty()) { "At least one picker trigger element is required." }
        require(borderWidth >= 0.dp) { "Trigger border width cannot be negative." }
        require(chevronSize > 0.dp) { "Chevron size must be greater than zero." }
        require(horizontalPadding >= 0.dp && verticalPadding >= 0.dp) {
            "Trigger padding cannot be negative."
        }
        require(elementSpacing >= 0.dp) { "Trigger element spacing cannot be negative." }
    }
}

@Immutable
data class CountryCodePickerListTextStyles(
    val title: TextStyle = TextStyle(fontWeight = FontWeight.Bold),
    val search: TextStyle = TextStyle.Default,
    val sectionTitle: TextStyle = TextStyle(fontWeight = FontWeight.Bold),
    val letterHeader: TextStyle = TextStyle(fontWeight = FontWeight.Bold),
    val countryName: TextStyle = TextStyle.Default,
    val callingCode: TextStyle = TextStyle(fontWeight = FontWeight.SemiBold),
    val emptyState: TextStyle = TextStyle.Default,
)

@Immutable
data class CountryCodePickerSearchConfig(
    val shape: Shape = RoundedCornerShape(14.dp),
    val height: Dp = 48.dp,
    val horizontalPadding: Dp = 12.dp,
    val iconSize: Dp = 19.dp,
    val iconSpacing: Dp = 8.dp,
    val clearButtonSize: Dp = 32.dp,
    val clearIconSize: Dp = 18.dp,
) {
    init {
        require(height > 0.dp) { "Search height must be greater than zero." }
        require(horizontalPadding >= 0.dp && iconSpacing >= 0.dp) {
            "Search padding and spacing cannot be negative."
        }
        require(iconSize > 0.dp && clearButtonSize > 0.dp && clearIconSize > 0.dp) {
            "Search icon sizes must be greater than zero."
        }
    }
}

@Immutable
data class CountryCodePickerSelectionConfig(
    val rowShape: Shape = RoundedCornerShape(10.dp),
    val rowBorderWidth: Dp = 1.dp,
    val rowHorizontalInset: Dp = 8.dp,
    val rowVerticalInset: Dp = 2.dp,
    val rowContentStartPadding: Dp = 12.dp,
    val rowContentEndPadding: Dp = 8.dp,
    val indicatorSize: Dp = 20.dp,
    val recentCardShape: Shape = RoundedCornerShape(10.dp),
    val recentCardBorderWidth: Dp = 0.5.dp,
    val recentCardSpacing: Dp = 6.dp,
    val recentContentPadding: Dp = 8.dp,
    val recentIndicatorHeight: Dp = 3.dp,
) {
    init {
        require(rowBorderWidth >= 0.dp && recentCardBorderWidth >= 0.dp) {
            "Selection border widths cannot be negative."
        }
        require(
            rowHorizontalInset >= 0.dp && rowVerticalInset >= 0.dp &&
                rowContentStartPadding >= 0.dp && rowContentEndPadding >= 0.dp
        ) {
            "Selection insets and padding cannot be negative."
        }
        require(recentCardSpacing >= 0.dp && recentContentPadding >= 0.dp) {
            "Recent-card spacing and padding cannot be negative."
        }
        require(indicatorSize > 0.dp && recentIndicatorHeight >= 0.dp) {
            "Selection indicator sizes must be valid positive values."
        }
    }
}

@Immutable
data class CountryCodePickerListConfig(
    val showSearch: Boolean = true,
    val showCallingCode: Boolean = true,
    val showRecentSelections: Boolean = true,
    val recentSelectionLimit: Int = 3,
    val separateCountriesByLetter: Boolean = true,
    val autoFocusSearch: Boolean = false,
    val search: CountryCodePickerSearchConfig = CountryCodePickerSearchConfig(),
    val selection: CountryCodePickerSelectionConfig = CountryCodePickerSelectionConfig(),
    val strings: CountryCodePickerStrings = CountryCodePickerStrings(),
    val textStyles: CountryCodePickerListTextStyles = CountryCodePickerListTextStyles(),
    val colors: CountryCodePickerListColors = CountryCodePickerListColors(),
)

@Immutable
data class CountryCodePickerConfig(
    val style: CountryCodePickerStyle = CountryCodePickerStyle.BottomSheet,
    val countryFilter: CountryCodePickerCountryFilter = CountryCodePickerCountryFilter.All,
    val trigger: CountryCodePickerTriggerConfig = CountryCodePickerTriggerConfig(),
    val list: CountryCodePickerListConfig = CountryCodePickerListConfig(),
    val sheetHeightFraction: Float = 0.65f,
    val sheetGesturesEnabled: Boolean = false,
)
