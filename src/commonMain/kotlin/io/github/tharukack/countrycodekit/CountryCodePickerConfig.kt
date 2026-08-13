package io.github.tharukack.countrycodekit

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class CountryCodePickerStyle { BottomSheet, Dialog, FullScreen }

enum class CountryCodePickerTriggerElement { Flag, CountryCode, Chevron }

enum class CountryCodeFlagStyle { Rounded, Circle }

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
data class CountryCodePickerCountryListColors(
    val accent: Color = Color(0xFF46BD99),
    val accentStrong: Color = Color(0xFF2F987A),
    val sheetContainer: Color = Color(0xFFFFFFFF),
    val searchContainer: Color = Color(0xFFF4F6F6),
    val selectedContainer: Color = Color(0xFFEEF8F4),
    val selectedContent: Color = Color.Unspecified,
    val content: Color = Color.Unspecified,
    val secondaryContent: Color = Color.Unspecified,
    val divider: Color = Color(0xFFE3E9E7),
    val containerBorder: Color = Color.Transparent,
    val scrim: Color = Color(0x52000000),
)

@Immutable
data class CountryCodePickerTriggerColors(
    val container: Color = Color.Transparent,
    val content: Color = Color.Unspecified,
    val chevron: Color = Color.Unspecified,
    val border: Color = Color.Transparent,
)

@Immutable
data class CountryCodePickerTriggerConfig(
    val triggerElements: Set<CountryCodePickerTriggerElement> = CountryCodePickerTriggerElement.entries.toSet(),
    val flagStyle: CountryCodeFlagStyle = CountryCodeFlagStyle.Rounded,
    val shape: Shape = RoundedCornerShape(14.dp),
    val borderWidth: Dp = 0.dp,
    val countryCodeTextStyle: TextStyle = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
    ),
    val chevronSize: Dp = 12.dp,
    val startPadding: Dp = 9.dp,
    val topPadding: Dp = 9.dp,
    val endPadding: Dp = 9.dp,
    val bottomPadding: Dp = 9.dp,
    val elementSpacing: Dp = 6.dp,
    val colors: CountryCodePickerTriggerColors = CountryCodePickerTriggerColors(),
) {
    init {
        require(triggerElements.isNotEmpty()) { "At least one picker trigger element is required." }
        require(borderWidth >= 0.dp) { "Trigger border width cannot be negative." }
        require(chevronSize > 0.dp) { "Chevron size must be greater than zero." }
        require(
            startPadding >= 0.dp &&
                topPadding >= 0.dp &&
                endPadding >= 0.dp &&
                bottomPadding >= 0.dp
        ) {
            "Trigger padding cannot be negative."
        }
        require(elementSpacing >= 0.dp) { "Trigger element spacing cannot be negative." }
    }
}

@Immutable
data class CountryCodePickerCountryListTextStyles(
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
data class CountryCodePickerCountryListConfig(
    val showSearch: Boolean = true,
    val showCallingCode: Boolean = true,
    val showRecentSelections: Boolean = true,
    val recentSelectionLimit: Int = 3,
    val separateCountriesByLetter: Boolean = true,
    val autoFocusSearch: Boolean = false,
    val flagStyle: CountryCodeFlagStyle = CountryCodeFlagStyle.Rounded,
    val search: CountryCodePickerSearchConfig = CountryCodePickerSearchConfig(),
    val selection: CountryCodePickerSelectionConfig = CountryCodePickerSelectionConfig(),
    val strings: CountryCodePickerStrings = CountryCodePickerStrings(),
    val textStyles: CountryCodePickerCountryListTextStyles = CountryCodePickerCountryListTextStyles(),
    val colors: CountryCodePickerCountryListColors = CountryCodePickerCountryListColors(),
)

@Immutable
data class CountryCodePickerBottomSheetConfig(
    val heightFraction: Float = 0.65f,
    val gesturesEnabled: Boolean = false,
    val shape: Shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    val showDragHandle: Boolean = true,
    val dragHandleWidth: Dp = 40.dp,
    val dragHandleHeight: Dp = 4.dp,
    val dragHandleTopPadding: Dp = 10.dp,
    val dragHandleShape: Shape = RoundedCornerShape(100.dp),
    val borderWidth: Dp = 0.dp,
    val tonalElevation: Dp = 0.dp,
    val minWidth: Dp? = null,
    val maxWidth: Dp? = null,
) {
    init {
        require(heightFraction in 0.5f..1f) { "Bottom-sheet height fraction must be between 0.5 and 1." }
        require(dragHandleWidth > 0.dp && dragHandleHeight > 0.dp) {
            "Bottom-sheet drag-handle dimensions must be greater than zero."
        }
        require(dragHandleTopPadding >= 0.dp && borderWidth >= 0.dp && tonalElevation >= 0.dp) {
            "Bottom-sheet padding, border width, and elevation cannot be negative."
        }
        validateWidthRange(minWidth, maxWidth, "Bottom-sheet")
    }
}

@Immutable
data class CountryCodePickerDialogConfig(
    val height: Dp = 620.dp,
    val shape: Shape = RoundedCornerShape(28.dp),
    val borderWidth: Dp = 0.dp,
    val tonalElevation: Dp = 0.dp,
    val shadowElevation: Dp = 0.dp,
    val minWidth: Dp? = null,
    val maxWidth: Dp? = null,
    val dismissOnBackPress: Boolean = true,
    val dismissOnClickOutside: Boolean = true,
) {
    init {
        require(height > 0.dp) { "Dialog height must be greater than zero." }
        require(borderWidth >= 0.dp && tonalElevation >= 0.dp && shadowElevation >= 0.dp) {
            "Dialog border width and elevations cannot be negative."
        }
        validateWidthRange(minWidth, maxWidth, "Dialog")
    }
}

@Immutable
data class CountryCodePickerFullScreenConfig(
    val contentMinWidth: Dp? = null,
    val contentMaxWidth: Dp? = null,
    val useStatusBarPadding: Boolean = true,
    val useNavigationBarPadding: Boolean = false,
    val dismissOnBackPress: Boolean = true,
) {
    init {
        validateWidthRange(contentMinWidth, contentMaxWidth, "Full-screen content")
    }
}

@Immutable
data class CountryCodePickerConfig(
    val style: CountryCodePickerStyle = CountryCodePickerStyle.BottomSheet,
    val countryFilter: CountryCodePickerCountryFilter = CountryCodePickerCountryFilter.All,
    val trigger: CountryCodePickerTriggerConfig = CountryCodePickerTriggerConfig(),
    val countryList: CountryCodePickerCountryListConfig = CountryCodePickerCountryListConfig(),
    val bottomSheet: CountryCodePickerBottomSheetConfig = CountryCodePickerBottomSheetConfig(),
    val dialog: CountryCodePickerDialogConfig = CountryCodePickerDialogConfig(),
    val fullScreen: CountryCodePickerFullScreenConfig = CountryCodePickerFullScreenConfig(),
)

private fun validateWidthRange(minWidth: Dp?, maxWidth: Dp?, label: String) {
    require(minWidth == null || minWidth > 0.dp) { "$label minimum width must be greater than zero." }
    require(maxWidth == null || maxWidth > 0.dp) { "$label maximum width must be greater than zero." }
    require(minWidth == null || maxWidth == null || minWidth <= maxWidth) {
        "$label minimum width cannot exceed its maximum width."
    }
}

internal fun CountryCodePickerCountryFilter.includes(country: CountryCode): Boolean {
    val isoCode = country.isoCode.uppercase()
    return when (this) {
        CountryCodePickerCountryFilter.All -> true
        is CountryCodePickerCountryFilter.Supported -> isoCodes.any { it.uppercase() == isoCode }
        is CountryCodePickerCountryFilter.Unsupported -> isoCodes.none { it.uppercase() == isoCode }
    }
}
