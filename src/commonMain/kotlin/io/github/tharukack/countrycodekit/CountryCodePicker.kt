package io.github.tharukack.countrycodekit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

private val TightChevronDown = ImageVector.Builder(
    name = "CountryCodeKitChevronDown",
    defaultWidth = 18.dp,
    defaultHeight = 18.dp,
    viewportWidth = 18f,
    viewportHeight = 18f,
).apply {
    path(fill = SolidColor(Color.Black)) {
        moveTo(1f, 4.5f)
        lineTo(9f, 12.5f)
        lineTo(17f, 4.5f)
        lineTo(14.8f, 2.5f)
        lineTo(9f, 8.3f)
        lineTo(3.2f, 2.5f)
        close()
    }
}.build()

object CountryCodePickerTestTags {
    const val Trigger = "country-code-trigger"
    const val Container = "country-code-container"
    const val Search = "country-code-search"
    const val NoResults = "country-code-no-results"
    const val RecentSection = "country-code-recent-section"
    const val AllCountriesSection = "country-code-all-countries-section"
    fun country(isoCode: String): String = "country-code-country-${isoCode.uppercase()}"
    fun recentCountry(isoCode: String): String = "country-code-recent-country-${isoCode.uppercase()}"
    fun letter(letter: String): String = "country-code-letter-${letter.uppercase()}"
}

/**
 * A complete country calling-code picker. The trigger and selected country are managed by [state].
 * Use [CountryCodePickerConfig.countryFilter] to restrict the catalog and [flagContent] to replace bundled flags.
 */
@Composable
fun CountryCodePicker(
    state: CountryCodePickerState,
    modifier: Modifier = Modifier,
    config: CountryCodePickerConfig = CountryCodePickerConfig(),
    enabled: Boolean = true,
    flagContent: (@Composable (CountryCode) -> Unit)? = null,
) {
    val triggerFlagContent: @Composable (CountryCode) -> Unit = flagContent ?: {
        CountryCodeFlag(it, style = config.trigger.flagStyle)
    }
    val listFlagContent: @Composable (CountryCode) -> Unit = flagContent ?: {
        CountryCodeFlag(it, style = config.list.flagStyle)
    }
    val countries = remember(config.countryFilter) {
        val requestedIsoCodes = when (val filter = config.countryFilter) {
            CountryCodePickerCountryFilter.All -> emptySet()
            is CountryCodePickerCountryFilter.Supported -> filter.isoCodes
            is CountryCodePickerCountryFilter.Unsupported -> filter.isoCodes
        }.map { it.trim().uppercase() }.toSet()

        when (config.countryFilter) {
            CountryCodePickerCountryFilter.All -> CountryCodeCatalog.countries
            is CountryCodePickerCountryFilter.Supported -> CountryCodeCatalog.countries.filter {
                it.isoCode.uppercase() in requestedIsoCodes
            }
            is CountryCodePickerCountryFilter.Unsupported -> CountryCodeCatalog.countries.filterNot {
                it.isoCode.uppercase() in requestedIsoCodes
            }
        }
    }
    Surface(
        modifier = modifier
            .testTag(CountryCodePickerTestTags.Trigger)
            .clickable(enabled = enabled, role = Role.Button, onClick = state::open),
        shape = config.trigger.shape,
        color = config.trigger.colors.container,
        border = config.trigger.borderWidth.takeIf { it > 0.dp }?.let {
            BorderStroke(it, config.trigger.colors.border)
        },
        tonalElevation = 1.dp,
        shadowElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier.padding(
                start = config.trigger.startPadding,
                top = config.trigger.topPadding,
                end = config.trigger.endPadding,
                bottom = config.trigger.bottomPadding,
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(config.trigger.elementSpacing),
        ) {
            if (CountryCodePickerTriggerElement.Flag in config.trigger.triggerElements) {
                triggerFlagContent(state.selectedCountry)
            }
            if (CountryCodePickerTriggerElement.CountryCode in config.trigger.triggerElements) {
                Text(
                    text = state.selectedCountry.formattedCallingCode,
                    style = MaterialTheme.typography.bodyMedium.merge(
                        config.trigger.countryCodeTextStyle,
                    ),
                    color = config.trigger.colors.content,
                )
            }
            if (CountryCodePickerTriggerElement.Chevron in config.trigger.triggerElements) {
                Icon(
                    TightChevronDown,
                    contentDescription = config.list.strings.title,
                    modifier = Modifier
                        .size(config.trigger.chevronSize)
                        .offset(y = 2.dp),
                    tint = config.trigger.colors.chevron,
                )
            }
        }
    }

    if (state.isOpen) {
        CountryCodePickerStyledContainer(
            state = state,
            config = config,
            countries = countries,
            flagContent = listFlagContent,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CountryCodePickerStyledContainer(
    state: CountryCodePickerState,
    config: CountryCodePickerConfig,
    countries: List<CountryCode>,
    flagContent: @Composable (CountryCode) -> Unit,
) {
    when (config.style) {
        CountryCodePickerStyle.BottomSheet -> ModalBottomSheet(
            onDismissRequest = state::dismiss,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            sheetGesturesEnabled = config.sheetGesturesEnabled,
            containerColor = config.list.colors.sheetContainer,
            scrimColor = config.list.colors.scrim,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            tonalElevation = 0.dp,
            dragHandle = {
                Surface(
                    modifier = Modifier.padding(top = 10.dp).width(40.dp).height(4.dp),
                    shape = RoundedCornerShape(100.dp),
                    color = config.list.colors.divider,
                ) {}
            },
        ) {
            PickerContent(
                state = state,
                config = config,
                countries = countries,
                flagContent = flagContent,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(config.sheetHeightFraction.coerceIn(0.5f, 1f))
                    .imePadding(),
            )
        }

        CountryCodePickerStyle.Dialog -> Dialog(onDismissRequest = state::dismiss) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = config.list.colors.sheetContainer,
                tonalElevation = 0.dp,
            ) {
                PickerContent(
                    state = state,
                    config = config,
                    countries = countries,
                    flagContent = flagContent,
                    modifier = Modifier.fillMaxWidth().height(620.dp),
                )
            }
        }

        CountryCodePickerStyle.FullScreen -> Dialog(
            onDismissRequest = state::dismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = config.list.colors.sheetContainer,
            ) {
                PickerContent(
                    state = state,
                    config = config,
                    countries = countries,
                    flagContent = flagContent,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(WindowInsets.statusBars.asPaddingValues()),
                )
            }
        }
    }
}

@Composable
private fun PickerContent(
    state: CountryCodePickerState,
    config: CountryCodePickerConfig,
    countries: List<CountryCode>,
    flagContent: @Composable (CountryCode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val results = CountryCodeCatalog.search(state.query, countries)
    val availableIsoCodes = remember(countries) { countries.mapTo(mutableSetOf()) { it.isoCode.uppercase() } }
    val recentCountries = state.recentSelections
        .filter { it.isoCode.uppercase() in availableIsoCodes }
        .take(config.list.recentSelectionLimit.coerceIn(0, 3))
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(state.isOpen, config.list.showSearch, config.list.autoFocusSearch) {
        if (state.isOpen && config.list.showSearch && config.list.autoFocusSearch) focusRequester.requestFocus()
    }

    Column(
        modifier = modifier
            .background(config.list.colors.sheetContainer)
            .testTag(CountryCodePickerTestTags.Container),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 10.dp, top = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = config.list.strings.title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleLarge.merge(config.list.textStyles.title),
                color = config.list.colors.content,
            )
            IconButton(onClick = state::dismiss, modifier = Modifier.size(40.dp)) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = config.list.strings.close,
                    modifier = Modifier.size(20.dp),
                    tint = config.list.colors.content,
                )
            }
        }

        if (config.list.showSearch) {
            BasicTextField(
                value = state.query,
                onValueChange = state::updateQuery,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 8.dp)
                    .height(config.list.search.height)
                    .background(config.list.colors.searchContainer, config.list.search.shape)
                    .focusRequester(focusRequester)
                    .testTag(CountryCodePickerTestTags.Search),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium
                    .merge(config.list.textStyles.search)
                    .copy(color = config.list.colors.content),
                cursorBrush = SolidColor(config.list.colors.accent),
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier.fillMaxSize().padding(
                            start = config.list.search.horizontalPadding,
                            end = config.list.search.horizontalPadding,
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(config.list.search.iconSize),
                            tint = config.list.colors.secondaryContent,
                        )
                        Spacer(Modifier.width(config.list.search.iconSpacing))
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            if (state.query.isEmpty()) {
                                Text(
                                    text = config.list.strings.searchPlaceholder,
                                    style = MaterialTheme.typography.bodyMedium.merge(config.list.textStyles.search),
                                    color = config.list.colors.secondaryContent,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            innerTextField()
                        }
                        if (state.query.isNotEmpty()) {
                            IconButton(
                                onClick = { state.updateQuery("") },
                                modifier = Modifier.size(config.list.search.clearButtonSize),
                            ) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = config.list.strings.clearSearch,
                                    modifier = Modifier.size(config.list.search.clearIconSize),
                                    tint = config.list.colors.secondaryContent,
                                )
                            }
                        }
                    }
                },
            )
        } else {
            Spacer(Modifier.height(12.dp))
        }

        if (results.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().testTag(CountryCodePickerTestTags.NoResults),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    config.list.strings.noResults,
                    color = config.list.colors.secondaryContent,
                    style = MaterialTheme.typography.bodyLarge.merge(config.list.textStyles.emptyState),
                )
            }
        } else {
            CountryList(
                state = state,
                config = config,
                results = results,
                recentCountries = recentCountries,
                flagContent = flagContent,
            )
        }
    }
}

@Composable
private fun CountryList(
    state: CountryCodePickerState,
    config: CountryCodePickerConfig,
    results: List<CountryCode>,
    recentCountries: List<CountryCode>,
    flagContent: @Composable (CountryCode) -> Unit,
) {
    val browsing = state.query.isBlank()
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        if (browsing && config.list.showRecentSelections && recentCountries.isNotEmpty()) {
            item(key = "recent-header") {
                SectionLabel(
                    text = config.list.strings.recent,
                    tag = CountryCodePickerTestTags.RecentSection,
                    config = config,
                )
            }
            item(key = "recent-cards") {
                RecentCountryCards(
                    countries = recentCountries,
                    selectedIsoCode = state.selectedCountry.isoCode,
                    showCallingCode = config.list.showCallingCode,
                    config = config,
                    flagContent = flagContent,
                    onSelect = state::select,
                )
            }
        }

        item(key = if (browsing) "all-header" else "results-header") {
            SectionLabel(
                text = if (browsing) config.list.strings.allCountries else config.list.strings.searchResults,
                tag = CountryCodePickerTestTags.AllCountriesSection,
                config = config,
            )
        }

        if (browsing && config.list.separateCountriesByLetter) {
            results.groupBy(::countrySection).forEach { (letter, sectionCountries) ->
                item(key = "letter-$letter") {
                    LetterLabel(letter = letter, config = config)
                }
                itemsIndexed(
                    items = sectionCountries,
                    key = { _, country -> "all-${country.isoCode.uppercase()}" },
                ) { index, country ->
                    CountryRow(
                        country = country,
                        selected = country.isoCode.equals(state.selectedCountry.isoCode, ignoreCase = true),
                        showDivider = !sectionCountries
                            .getOrNull(index + 1)
                            ?.isoCode
                            .equals(state.selectedCountry.isoCode, ignoreCase = true),
                        showCallingCode = config.list.showCallingCode,
                        config = config,
                        flagContent = flagContent,
                        testTag = CountryCodePickerTestTags.country(country.isoCode),
                        onClick = { state.select(country) },
                    )
                }
            }
        } else {
            itemsIndexed(
                items = results,
                key = { _, country -> "result-${country.isoCode.uppercase()}" },
            ) { index, country ->
                CountryRow(
                    country = country,
                    selected = country.isoCode.equals(state.selectedCountry.isoCode, ignoreCase = true),
                    showDivider = !results
                        .getOrNull(index + 1)
                        ?.isoCode
                        .equals(state.selectedCountry.isoCode, ignoreCase = true),
                    showCallingCode = config.list.showCallingCode,
                    config = config,
                    flagContent = flagContent,
                    testTag = CountryCodePickerTestTags.country(country.isoCode),
                    onClick = { state.select(country) },
                )
            }
        }
        item(key = "list-bottom-space") { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun SectionLabel(
    text: String,
    tag: String,
    config: CountryCodePickerConfig,
) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(tag)
            .padding(horizontal = 20.dp, vertical = 7.dp),
        style = MaterialTheme.typography.titleSmall.merge(config.list.textStyles.sectionTitle),
        color = config.list.colors.content,
    )
}

@Composable
private fun RecentCountryCards(
    countries: List<CountryCode>,
    selectedIsoCode: String,
    showCallingCode: Boolean,
    config: CountryCodePickerConfig,
    flagContent: @Composable (CountryCode) -> Unit,
    onSelect: (CountryCode) -> Unit,
) {
    val useCompactCards = countries.size > 1
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, bottom = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(config.list.selection.recentCardSpacing),
    ) {
        countries.forEach { country ->
            val selected = country.isoCode.equals(selectedIsoCode, ignoreCase = true)
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(if (useCompactCards) 56.dp else 44.dp)
                    .selectable(selected = selected, role = Role.RadioButton) { onSelect(country) }
                    .testTag(CountryCodePickerTestTags.recentCountry(country.isoCode)),
                shape = config.list.selection.recentCardShape,
                color = if (selected) config.list.colors.selectedContainer else config.list.colors.sheetContainer,
                border = config.list.selection.recentCardBorderWidth.takeIf { it > 0.dp }?.let {
                    BorderStroke(
                        it,
                        if (selected) config.list.colors.accentStrong.copy(alpha = 0.35f) else config.list.colors.divider,
                    )
                },
                tonalElevation = 0.dp,
                shadowElevation = 0.dp,
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(
                                start = config.list.selection.recentContentPadding,
                                end = config.list.selection.recentContentPadding,
                                top = 4.dp,
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        flagContent(country)
                        Spacer(Modifier.width(6.dp))
                        if (useCompactCards) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = country.name,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.bodySmall
                                        .copy(fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium)
                                        .merge(config.list.textStyles.countryName),
                                    color = if (selected) config.list.colors.selectedContent else config.list.colors.content,
                                )
                                if (showCallingCode) {
                                    Text(
                                        text = country.formattedCallingCode,
                                        style = MaterialTheme.typography.labelSmall.merge(config.list.textStyles.callingCode),
                                        color = config.list.colors.accentStrong,
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = country.name,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodySmall
                                    .copy(fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium)
                                    .merge(config.list.textStyles.countryName),
                                color = if (selected) config.list.colors.selectedContent else config.list.colors.content,
                            )
                            if (showCallingCode) {
                                Text(
                                    text = country.formattedCallingCode,
                                    style = MaterialTheme.typography.labelMedium.merge(config.list.textStyles.callingCode),
                                    color = config.list.colors.accentStrong,
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(config.list.selection.recentIndicatorHeight)
                            .background(if (selected) config.list.colors.accentStrong else Color.Transparent),
                    )
                }
            }
        }
    }
}

@Composable
private fun LetterLabel(
    letter: String,
    config: CountryCodePickerConfig,
) {
    Text(
        text = letter,
        modifier = Modifier
            .fillMaxWidth()
            .background(config.list.colors.sheetContainer)
            .testTag(CountryCodePickerTestTags.letter(letter))
            .padding(start = 20.dp, end = 20.dp, top = 6.dp, bottom = 5.dp),
        style = MaterialTheme.typography.titleMedium.merge(config.list.textStyles.letterHeader),
        color = config.list.colors.content,
    )
}

@Composable
private fun CountryRow(
    country: CountryCode,
    selected: Boolean,
    showDivider: Boolean,
    showCallingCode: Boolean,
    config: CountryCodePickerConfig,
    flagContent: @Composable (CountryCode) -> Unit,
    testTag: String,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = if (selected) config.list.selection.rowHorizontalInset else 0.dp,
                    vertical = if (selected) config.list.selection.rowVerticalInset else 1.dp,
                )
                .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
                .testTag(testTag),
            shape = if (selected) config.list.selection.rowShape else RectangleShape,
            color = if (selected) config.list.colors.selectedContainer else config.list.colors.sheetContainer,
            border = if (selected && config.list.selection.rowBorderWidth > 0.dp) {
                BorderStroke(config.list.selection.rowBorderWidth, config.list.colors.accentStrong)
            } else {
                null
            },
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 44.dp)
                    .padding(
                        start = if (selected) config.list.selection.rowContentStartPadding else 20.dp,
                        end = if (selected) config.list.selection.rowContentEndPadding else 16.dp,
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                flagContent(country)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = country.name,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                        .copy(fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium)
                        .merge(config.list.textStyles.countryName),
                    color = if (selected) config.list.colors.selectedContent else config.list.colors.content,
                )
                if (showCallingCode) {
                    Text(
                        text = country.formattedCallingCode,
                        style = MaterialTheme.typography.bodyMedium.merge(config.list.textStyles.callingCode),
                        color = config.list.colors.accentStrong,
                    )
                }
                Box(
                    modifier = Modifier.width(26.dp),
                    contentAlignment = Alignment.CenterEnd,
                ) {
                    if (selected) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(config.list.selection.indicatorSize),
                            tint = config.list.colors.accentStrong,
                        )
                    }
                }
            }
        }
        if (!selected && showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 0.5.dp,
                color = config.list.colors.divider,
            )
        }
    }
}

private fun countrySection(country: CountryCode): String = country.name
    .trimStart()
    .firstOrNull()
    ?.uppercaseChar()
    ?.takeIf(Char::isLetter)
    ?.toString()
    ?: "#"
