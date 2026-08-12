package io.github.tharukack.countrycodekit.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.tharukack.countrycodekit.CountryCodeCatalog
import io.github.tharukack.countrycodekit.CountryCodePhoneResult
import io.github.tharukack.countrycodekit.CountryCodePhoneProcessing
import io.github.tharukack.countrycodekit.CountryCodePhoneStatus
import io.github.tharukack.countrycodekit.CountryCodePicker
import io.github.tharukack.countrycodekit.CountryCodePickerConfig
import io.github.tharukack.countrycodekit.CountryCodePickerListConfig
import io.github.tharukack.countrycodekit.CountryCodePickerSearchConfig
import io.github.tharukack.countrycodekit.CountryCodePickerSelectionConfig
import io.github.tharukack.countrycodekit.CountryCodePickerStyle
import io.github.tharukack.countrycodekit.CountryCodePickerTriggerColors
import io.github.tharukack.countrycodekit.CountryCodePickerTriggerConfig
import io.github.tharukack.countrycodekit.CountryCodePickerTriggerElement
import io.github.tharukack.countrycodekit.rememberCountryCodePickerState
import io.github.tharukack.countrycodekit.rememberCountryCodePhoneState

private val Accent = Color(0xFF2F987A)
private val AccentBright = Color(0xFF46BD99)
private val Ink = Color(0xFF17243A)
private val MutedInk = Color(0xFF6E7C81)
private val Canvas = Color(0xFFF6FAF8)
private val SoftMint = Color(0xFFEAF7F2)
private val Line = Color(0xFFDDE7E3)

private val AppColors = lightColorScheme(
    primary = Accent,
    onPrimary = Color.White,
    primaryContainer = SoftMint,
    onPrimaryContainer = Color(0xFF102D25),
    secondary = AccentBright,
    onSecondary = Color(0xFF092820),
    background = Canvas,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = Color(0xFFF0F5F3),
    onSurfaceVariant = MutedInk,
    outline = Color(0xFF96A7A1),
    outlineVariant = Line,
    error = Color(0xFFB3261E),
)

@Composable
fun App() {
    MaterialTheme(colorScheme = AppColors) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            CountryCodeKitSample()
        }
    }
}

@Composable
private fun CountryCodeKitSample() {
    var page by rememberSaveable { mutableStateOf(SamplePage.Home) }

    Box(Modifier.fillMaxSize()) {
        when (page) {
            SamplePage.Home -> CountryCodeKitHome()
            SamplePage.PickerStyle -> PickerStylePage()
        }
        SampleBottomBar(
            selected = page,
            onSelected = { page = it },
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun CountryCodeKitHome() {
    val focusManager = LocalFocusManager.current
    val backgroundInteractionSource = remember { MutableInteractionSource() }
    val initialCountry = remember { CountryCodeCatalog.findByIsoCode("AU")!! }
    val pickerOnlyState = rememberCountryCodePickerState(initialCountry)
    var pickerStyle by remember { mutableStateOf(CountryCodePickerStyle.BottomSheet) }
    var showRecents by rememberSaveable { mutableStateOf(true) }
    var separateByLetter by rememberSaveable { mutableStateOf(true) }
    val pickerConfig = remember(pickerStyle, showRecents, separateByLetter) {
        CountryCodePickerConfig(
            style = pickerStyle,
            list = CountryCodePickerListConfig(
                showRecentSelections = showRecents,
                separateCountriesByLetter = separateByLetter,
            ),
        )
    }
    val detachedPhoneState = rememberCountryCodePhoneState(
        initialCountry = initialCountry,
        processing = CountryCodePhoneProcessing.DetectCountry,
        pickerConfig = pickerConfig,
    )
    val attachedPhoneState = rememberCountryCodePhoneState(
        initialCountry = initialCountry,
        pickerConfig = pickerConfig,
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFEAF8F2), Canvas, Canvas),
                ),
            )
            .clickable(
                interactionSource = backgroundInteractionSource,
                indication = null,
                onClick = { focusManager.clearFocus() },
            ),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .widthIn(max = 620.dp)
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .imePadding()
                .padding(start = 20.dp, top = 18.dp, end = 20.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            SampleHeader()

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Line, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    SectionHeading(
                        eyebrow = "INTEGRATION",
                        title = "Attach it your way",
                        supporting = "CountryCodeKit provides the picker. The phone fields shown here belong to the host app.",
                    )

                    IntegrationExample(title = "1  Picker only") {
                        CountryCodePicker(
                            state = pickerOnlyState,
                            config = pickerConfig,
                        )
                    }

                    IntegrationExample(title = "2  Picker + app-owned detached field") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            CountryCodePicker(
                                state = detachedPhoneState.pickerState,
                                config = detachedPhoneState.pickerConfig,
                                modifier = Modifier.height(56.dp),
                            )
                            OutlinedTextField(
                                value = detachedPhoneState.rawNumber,
                                onValueChange = detachedPhoneState::updateNumber,
                                placeholder = { Text("Phone number") },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                visualTransformation = detachedPhoneState.visualTransformation,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone,
                                    imeAction = ImeAction.Done,
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = { focusManager.clearFocus() },
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Accent,
                                    unfocusedBorderColor = Line,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                ),
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }

                    IntegrationExample(title = "3  Picker + app-owned attached field") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            CountryCodePicker(
                                state = attachedPhoneState.pickerState,
                                config = attachedPhoneState.pickerConfig.copy(
                                    trigger = attachedPhoneState.pickerConfig.trigger.copy(
                                        shape = RoundedCornerShape(
                                            topStart = 14.dp,
                                            bottomStart = 14.dp,
                                        ),
                                    ),
                                ),
                                modifier = Modifier.height(56.dp),
                            )
                            OutlinedTextField(
                                value = attachedPhoneState.rawNumber,
                                onValueChange = attachedPhoneState::updateNumber,
                                placeholder = { Text("Phone number") },
                                singleLine = true,
                                shape = RoundedCornerShape(
                                    topEnd = 14.dp,
                                    bottomEnd = 14.dp,
                                ),
                                visualTransformation = attachedPhoneState.visualTransformation,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone,
                                    imeAction = ImeAction.Done,
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = { focusManager.clearFocus() },
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Accent,
                                    unfocusedBorderColor = Line,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                ),
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }

                    HorizontalDivider(color = Line)
                    SettingRow(
                        title = "Format as you type",
                        supporting = "Displays country-aware spacing without changing the raw value.",
                        checked = attachedPhoneState.formatAsYouType,
                        onCheckedChange = {
                            attachedPhoneState.formatAsYouType = it
                            detachedPhoneState.formatAsYouType = it
                        },
                    )
                    HorizontalDivider(color = Line)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Built-in validation",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Use the built-in validation when your app requires it.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    attachedPhoneState.validation?.let { ValidationPanel(it) }
                }
            }

            FeatureSummary()

            Text(
                text = "CountryCodeKit · Android + iOS · Offline metadata",
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private enum class SamplePage { Home, PickerStyle }

private enum class TriggerPalette { Default, Mint, Dark }

private enum class TriggerTextSize { Small, Default, Large }

private enum class TriggerColorChoice { Palette, Ink, Accent, White }

private enum class TriggerChevronSize { Small, Default, Large }

@Composable
private fun PickerStylePage() {
    val initialCountry = remember { CountryCodeCatalog.findByIsoCode("AU")!! }
    val pickerState = rememberCountryCodePickerState(initialCountry)
    var pickerStyle by rememberSaveable { mutableStateOf(CountryCodePickerStyle.BottomSheet) }
    var elements by remember {
        mutableStateOf(CountryCodePickerTriggerElement.entries.toSet())
    }
    var cornerRadius by rememberSaveable { mutableStateOf(14) }
    var usePillShape by rememberSaveable { mutableStateOf(false) }
    var palette by rememberSaveable { mutableStateOf(TriggerPalette.Default) }
    var showBorder by rememberSaveable { mutableStateOf(false) }
    var textSize by rememberSaveable { mutableStateOf(TriggerTextSize.Default) }
    var boldCountryCode by rememberSaveable { mutableStateOf(false) }
    var textColor by rememberSaveable { mutableStateOf(TriggerColorChoice.Palette) }
    var chevronColor by rememberSaveable { mutableStateOf(TriggerColorChoice.Palette) }
    var chevronSize by rememberSaveable { mutableStateOf(TriggerChevronSize.Default) }
    var searchCornerRadius by rememberSaveable { mutableStateOf(14) }
    var selectionCornerRadius by rememberSaveable { mutableStateOf(10) }
    var selectionBorderWidth by rememberSaveable { mutableStateOf(1) }

    val triggerShape: Shape = if (usePillShape) CircleShape else when (cornerRadius) {
        0 -> RectangleShape
        else -> RoundedCornerShape(cornerRadius.dp)
    }
    val paletteColors = when (palette) {
        TriggerPalette.Default -> CountryCodePickerTriggerColors(
            border = Line,
        )
        TriggerPalette.Mint -> CountryCodePickerTriggerColors(
            container = SoftMint,
            content = Accent,
            chevron = Accent,
            border = Accent,
        )
        TriggerPalette.Dark -> CountryCodePickerTriggerColors(
            container = Ink,
            content = Color.White,
            chevron = Color.White.copy(alpha = 0.72f),
            border = Ink,
        )
    }
    val triggerColors = paletteColors.copy(
        content = textColor.resolve(paletteColors.content),
        chevron = chevronColor.resolve(paletteColors.chevron),
    )
    val config = CountryCodePickerConfig(
        style = pickerStyle,
        trigger = CountryCodePickerTriggerConfig(
            triggerElements = elements,
            shape = triggerShape,
            borderWidth = if (showBorder) 1.dp else 0.dp,
            countryCodeTextStyle = TextStyle(
                fontSize = when (textSize) {
                    TriggerTextSize.Small -> 12.sp
                    TriggerTextSize.Default -> 14.sp
                    TriggerTextSize.Large -> 18.sp
                },
                fontWeight = if (boldCountryCode) FontWeight.Bold else FontWeight.SemiBold,
            ),
            chevronSize = when (chevronSize) {
                TriggerChevronSize.Small -> 12.dp
                TriggerChevronSize.Default -> 16.dp
                TriggerChevronSize.Large -> 20.dp
            },
            colors = triggerColors,
        ),
        list = CountryCodePickerListConfig(
            search = CountryCodePickerSearchConfig(
                shape = pickerShape(searchCornerRadius),
            ),
            selection = CountryCodePickerSelectionConfig(
                rowShape = pickerShape(selectionCornerRadius),
                rowBorderWidth = selectionBorderWidth.dp,
                recentCardShape = pickerShape(selectionCornerRadius),
            ),
        ),
    )

    Box(
        modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFFEAF8F2), Canvas, Canvas)),
        ),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .widthIn(max = 620.dp)
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(start = 20.dp, top = 18.dp, end = 20.dp, bottom = 104.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            SectionHeading(
                eyebrow = "PICKER STYLE",
                title = "Build your trigger",
                supporting = "Choose only the content and styling your app needs. The picker opens as a bottom sheet by default.",
            )

            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, Line, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp),
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Text("Live preview", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    CountryCodePicker(
                        state = pickerState,
                        config = config,
                    )
                    Text(
                        text = "Tap the preview to open the configured picker.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedInk,
                    )
                }
            }

            StyleControlCard(title = "Content", supporting = "Select the elements to show in the trigger.") {
                CountryCodePickerTriggerElement.entries.forEach { element ->
                    val selected = element in elements
                    FilterChip(
                        selected = selected,
                        onClick = {
                            if (!selected || elements.size > 1) {
                                elements = if (selected) elements - element else elements + element
                            }
                        },
                        label = { Text(element.label()) },
                        colors = sampleChipColors(),
                    )
                }
            }

            StyleControlCard(title = "Shape", supporting = "Use a pill or choose the corner radius.") {
                listOf(-1 to "Pill", 0 to "Square", 8 to "8 dp", 14 to "14 dp", 24 to "24 dp").forEach { (radius, label) ->
                    val selected = if (radius == -1) usePillShape else !usePillShape && cornerRadius == radius
                    FilterChip(
                        selected = selected,
                        onClick = {
                            usePillShape = radius == -1
                            if (radius >= 0) cornerRadius = radius
                        },
                        label = { Text(label) },
                        colors = sampleChipColors(),
                    )
                }
            }

            StyleControlCard(title = "Colors", supporting = "Apps can provide any background, content, chevron, and border colors.") {
                TriggerPalette.entries.forEach { option ->
                    FilterChip(
                        selected = palette == option,
                        onClick = { palette = option },
                        label = { Text(option.label()) },
                        colors = sampleChipColors(),
                    )
                }
            }

            StyleControlCard(title = "Text size", supporting = "The country code inherits app typography and can be overridden when needed.") {
                TriggerTextSize.entries.forEach { option ->
                    FilterChip(
                        selected = textSize == option,
                        onClick = { textSize = option },
                        label = { Text(option.label()) },
                        colors = sampleChipColors(),
                    )
                }
            }

            StyleControlCard(title = "Text color", supporting = "Set the country-code text color independently from the background.") {
                TriggerColorChoice.entries.forEach { option ->
                    FilterChip(
                        selected = textColor == option,
                        onClick = { textColor = option },
                        label = { Text(option.label()) },
                        colors = sampleChipColors(),
                    )
                }
            }

            StyleControlCard(title = "Chevron size", supporting = "Size the dropdown indicator independently from the text.") {
                TriggerChevronSize.entries.forEach { option ->
                    FilterChip(
                        selected = chevronSize == option,
                        onClick = { chevronSize = option },
                        label = { Text(option.label()) },
                        colors = sampleChipColors(),
                    )
                }
            }

            StyleControlCard(title = "Chevron color", supporting = "Set the dropdown indicator color independently from the country code.") {
                TriggerColorChoice.entries.forEach { option ->
                    FilterChip(
                        selected = chevronColor == option,
                        onClick = { chevronColor = option },
                        label = { Text(option.label()) },
                        colors = sampleChipColors(),
                    )
                }
            }

            StyleControlCard(title = "Search shape", supporting = "Control the search-box corner radius inside the country list.") {
                listOf(0, 8, 14, 24).forEach { radius ->
                    FilterChip(
                        selected = searchCornerRadius == radius,
                        onClick = { searchCornerRadius = radius },
                        label = { Text(if (radius == 0) "Square" else "$radius dp") },
                        colors = sampleChipColors(),
                    )
                }
            }

            StyleControlCard(title = "Selection shape", supporting = "Apply the selected-row and recent-card corner radius.") {
                listOf(0, 8, 10, 18).forEach { radius ->
                    FilterChip(
                        selected = selectionCornerRadius == radius,
                        onClick = { selectionCornerRadius = radius },
                        label = { Text(if (radius == 0) "Square" else "$radius dp") },
                        colors = sampleChipColors(),
                    )
                }
            }

            StyleControlCard(title = "Selection border", supporting = "Choose no border or a visible selected-row border width.") {
                listOf(0, 1, 2).forEach { width ->
                    FilterChip(
                        selected = selectionBorderWidth == width,
                        onClick = { selectionBorderWidth = width },
                        label = { Text(if (width == 0) "None" else "$width dp") },
                        colors = sampleChipColors(),
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, Line, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp),
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SettingRow(
                        title = "Trigger border",
                        supporting = "Add an optional border using the configured color.",
                        checked = showBorder,
                        onCheckedChange = { showBorder = it },
                    )
                    HorizontalDivider(color = Line)
                    SettingRow(
                        title = "Bold country code",
                        supporting = "Override the inherited text weight for the trigger.",
                        checked = boldCountryCode,
                        onCheckedChange = { boldCountryCode = it },
                    )
                    HorizontalDivider(color = Line)
                    Text("Opening style", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        CountryCodePickerStyle.entries.forEach { option ->
                            FilterChip(
                                selected = pickerStyle == option,
                                onClick = { pickerStyle = option },
                                label = { Text(option.label()) },
                                colors = sampleChipColors(),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StyleControlCard(
    title: String,
    supporting: String,
    content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth().border(1.dp, Line, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text(supporting, style = MaterialTheme.typography.bodySmall, color = MutedInk)
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                content = content,
            )
        }
    }
}

@Composable
private fun sampleChipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = SoftMint,
    selectedLabelColor = Accent,
)

@Composable
private fun SampleBottomBar(
    selected: SamplePage,
    onSelected: (SamplePage) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 16.dp),
        ) {
            SamplePage.entries.forEach { page ->
                val active = selected == page
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSelected(page) }
                        .padding(vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(1.dp),
                ) {
                    Box(
                        Modifier.width(24.dp).height(2.dp).background(
                            if (active) Accent else Color.Transparent,
                            CircleShape,
                        ),
                    )
                    Text(
                        text = if (page == SamplePage.Home) "Home" else "Picker style",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (active) Accent else MutedInk,
                        fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                    )
                }
            }
        }
    }
}

@Composable
private fun SampleHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        Brush.linearGradient(listOf(AccentBright, Accent)),
                        RoundedCornerShape(14.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "CC",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                )
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = "CountryCodeKit",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Compose Multiplatform sample",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(Modifier.height(2.dp))
        Text(
            text = "Country selection that feels native.",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Ink,
        )
    }
}

@Composable
private fun SectionHeading(
    eyebrow: String,
    title: String,
    supporting: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = eyebrow,
            style = MaterialTheme.typography.labelSmall,
            color = Accent,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = supporting,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun IntegrationExample(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
        )
        content()
    }
}

@Composable
private fun ValidationPanel(result: CountryCodePhoneResult) {
    val valid = result.status == CountryCodePhoneStatus.VALID
    val empty = result.status == CountryCodePhoneStatus.EMPTY
    val container = when {
        valid -> Color(0xFFE9F7F0)
        empty -> MaterialTheme.colorScheme.surfaceVariant
        else -> Color(0xFFFFEFEE)
    }
    val content = when {
        valid -> Accent
        empty -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.error
    }
    val title = when (result.status) {
        CountryCodePhoneStatus.EMPTY -> "Ready when you are"
        CountryCodePhoneStatus.VALID -> "Valid phone number"
        CountryCodePhoneStatus.IMPOSSIBLE -> "Check the number length"
        CountryCodePhoneStatus.TOO_SHORT -> "The number is too short"
        CountryCodePhoneStatus.TOO_LONG -> "The number is too long"
        CountryCodePhoneStatus.INVALID_REGION -> "Unsupported country region"
        CountryCodePhoneStatus.NOT_A_NUMBER -> "Use a phone-number format"
        CountryCodePhoneStatus.NON_DIGIT_CHARACTERS -> "Use numbers only"
        CountryCodePhoneStatus.INVALID -> "This number is not valid"
    }
    val supporting = when {
        valid -> result.international.orEmpty()
        empty -> "Validation runs locally as you type."
        else -> "Update the number or choose a different country."
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = container,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(9.dp).background(content, CircleShape),
            )
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = content,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = supporting,
                    style = MaterialTheme.typography.bodySmall,
                    color = content.copy(alpha = 0.82f),
                )
            }
        }
    }
}

@Composable
private fun SettingRow(
    title: String,
    supporting: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = supporting,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.width(16.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Accent,
                uncheckedBorderColor = Line,
            ),
        )
    }
}

@Composable
private fun FeatureSummary() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Ink,
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Included by default",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FeaturePill("271 PNG flags", Modifier.weight(1f))
                FeaturePill("Offline data", Modifier.weight(1f))
                FeaturePill("A–Z search", Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun FeaturePill(text: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.09f),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 9.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

private fun CountryCodePickerStyle.label(): String = when (this) {
    CountryCodePickerStyle.BottomSheet -> "Bottom sheet"
    CountryCodePickerStyle.Dialog -> "Dialog"
    CountryCodePickerStyle.FullScreen -> "Full screen"
}

private fun CountryCodePickerTriggerElement.label(): String = when (this) {
    CountryCodePickerTriggerElement.Flag -> "Flag"
    CountryCodePickerTriggerElement.CountryCode -> "Country code"
    CountryCodePickerTriggerElement.Chevron -> "Chevron"
}

private fun TriggerPalette.label(): String = when (this) {
    TriggerPalette.Default -> "Default"
    TriggerPalette.Mint -> "Mint"
    TriggerPalette.Dark -> "Dark"
}

private fun TriggerTextSize.label(): String = when (this) {
    TriggerTextSize.Small -> "Small"
    TriggerTextSize.Default -> "Default"
    TriggerTextSize.Large -> "Large"
}

private fun TriggerColorChoice.label(): String = when (this) {
    TriggerColorChoice.Palette -> "Palette"
    TriggerColorChoice.Ink -> "Ink"
    TriggerColorChoice.Accent -> "Accent"
    TriggerColorChoice.White -> "White"
}

private fun TriggerColorChoice.resolve(paletteColor: Color): Color = when (this) {
    TriggerColorChoice.Palette -> paletteColor
    TriggerColorChoice.Ink -> Ink
    TriggerColorChoice.Accent -> Accent
    TriggerColorChoice.White -> Color.White
}

private fun TriggerChevronSize.label(): String = when (this) {
    TriggerChevronSize.Small -> "12 dp"
    TriggerChevronSize.Default -> "16 dp"
    TriggerChevronSize.Large -> "20 dp"
}

private fun pickerShape(cornerRadius: Int): Shape = if (cornerRadius == 0) {
    RectangleShape
} else {
    RoundedCornerShape(cornerRadius.dp)
}
