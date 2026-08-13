<h1 align="center">CountryCodeKit</h1>

<p align="center">
  <strong>A polished country calling-code picker for Compose Multiplatform</strong>
</p>

<p align="center">
  Search countries, show recent selections, browse optional A–Z sections, render bundled flags, and validate phone numbers offline on Android and iOS.
</p>

<p align="center">
  <img alt="Android" src="https://img.shields.io/badge/Android-supported-16A34A?style=for-the-badge&logo=android&logoColor=white" />
  <img alt="iOS" src="https://img.shields.io/badge/iOS-supported-2563EB?style=for-the-badge&logo=apple&logoColor=white" />
  <img alt="Kotlin Multiplatform" src="https://img.shields.io/badge/Kotlin%20Multiplatform-ready-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" />
  <img alt="MIT License" src="https://img.shields.io/badge/License-MIT-111827?style=for-the-badge" />
</p>

<p align="center">
  <a href="#installation">Installation</a> ·
  <a href="#quick-start">Quick Start</a> ·
  <a href="#bottom-sheet-experience">Bottom Sheet</a> ·
  <a href="#customization">Customization</a> ·
  <a href="#phone-number-validation">Validation</a> ·
  <a href="#platform-support">Platform Support</a>
</p>

---

## Features

- Search-first country picker with the search field fixed at the top.
- Responsive recent-selection cards instead of hard-coded popular countries: one fills the row, while two or three divide it evenly.
- Optional alphabetical country sections, enabled by default.
- Clean rows with country flag, full country name, and calling code—no ISO abbreviations.
- Selected-country highlighting and an accessible selection indicator.
- 271 bundled, transparent PNG flags rendered from reviewed `flag-icons` sources.
- Country names, calling codes, parsing, formatting, and validation backed by Google libphonenumber metadata.
- Offline possible/valid checks, E.164 formatting, and international formatting.
- Bottom-sheet, dialog, and full-screen styles.
- Android and iOS targets only.
- No verification service, network request, or external runtime phone-number wrapper.

CountryCodeKit inherits `MaterialTheme.typography` from the host application. It does not bundle or force a font family.

---

## Installation

Add CountryCodeKit to the Compose Multiplatform source set where the picker is used:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.tharukack:countrycodekit:0.1.0")
        }
    }
}
```

CountryCodeKit is published to Maven Central. Use the same dependency from shared code for both Android and iOS Compose Multiplatform targets.

---

## Quick Start

Create a saveable picker state, then place `CountryCodePicker` beside your phone-number field.

```kotlin
val australia = CountryCodeCatalog.findByIsoCode("AU")!!
val pickerState = rememberCountryCodePickerState(initialCountry = australia)

CountryCodePicker(
    state = pickerState,
)
```

The default style is the bottom sheet, so no style configuration is required. Read the selected country from state:

```kotlin
val selected = pickerState.selectedCountry

Text(selected.name)
Text(selected.formattedCallingCode)
```

---

## Bottom Sheet Experience

The default sheet is designed for fast one-handed country selection:

1. The title and search field stay at the top.
2. Search matches full country names, ISO codes, and calling codes.
3. Recent user selections appear only when there are real selections to show.
4. The complete country list follows under **All countries**.
5. Countries are separated into A–Z sections by default.
6. Selecting a country updates state, moves it to the front of **Recent**, clears search, and closes the sheet.

Recent selections belong to `CountryCodePickerState` and survive saveable-state restoration. They are not uploaded or written to storage by the library. Applications that persist recents can provide them when creating state:

```kotlin
val pickerState = rememberCountryCodePickerState(
    initialCountry = australia,
    initialRecentSelections = listOf(
        CountryCodeCatalog.findByIsoCode("AU")!!,
        CountryCodeCatalog.findByIsoCode("US")!!,
    ),
)
```

---

## Customization

### Customization catalogue

| Area | What you can configure |
| --- | --- |
| [Picker behavior](#picker-behavior) | Style, search, recents, calling codes, alphabetical sections, focus, and sheet height. |
| [Strings](#strings) | Title, search placeholder, section labels, empty state, and accessibility labels. |
| [Colors](#colors) | Accent, sheet, search, selection, content, divider, and scrim colors. |
| [Countries](#restrict-countries) | Provide either supported or unsupported ISO country codes. |
| [Flags](#custom-flags) | Replace bundled artwork with application-owned flag content. |
| [Trigger](#custom-trigger-shape) | Set the trigger shape or compose your own trigger around state. |

### Picker behavior

```kotlin
CountryCodePicker(
    state = pickerState,
    config = CountryCodePickerConfig(
        style = CountryCodePickerStyle.BottomSheet,
        trigger = CountryCodePickerTriggerConfig(
            triggerElements = setOf(
                CountryCodePickerTriggerElement.Flag,
                CountryCodePickerTriggerElement.CountryCode,
                CountryCodePickerTriggerElement.Chevron,
            ),
            flagStyle = CountryCodeFlagStyle.Rounded,
            shape = RoundedCornerShape(14.dp),
            borderWidth = 0.dp,
            countryCodeTextStyle = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            ),
            chevronSize = 12.dp,
            startPadding = 9.dp,
            topPadding = 9.dp,
            endPadding = 9.dp,
            bottomPadding = 9.dp,
            elementSpacing = 6.dp,
        ),
        list = CountryCodePickerListConfig(
            showSearch = true,
            showCallingCode = true,
            showRecentSelections = true,
            recentSelectionLimit = 3,
            separateCountriesByLetter = true,
            autoFocusSearch = false,
            flagStyle = CountryCodeFlagStyle.Rounded,
            search = CountryCodePickerSearchConfig(),
            selection = CountryCodePickerSelectionConfig(),
            textStyles = CountryCodePickerListTextStyles(),
        ),
        sheetHeightFraction = 0.65f,
        sheetGesturesEnabled = false,
    ),
)
```

| Option | Default | Purpose |
| --- | --- | --- |
| `style` | `BottomSheet` | Uses a bottom sheet, dialog, or full-screen style. Omit it to use the default. |
| `countryFilter` | `All` | Accepts one supported or unsupported list of string ISO codes. |
| `trigger` | Default trigger config | Controls trigger elements, shape, border, and colors. |
| `list` | Default list config | Controls search, calling codes, recents, sections, strings, and list colors. |
| `sheetHeightFraction` | `0.65f` | Provides a balanced default sheet height; values are constrained to `0.5f..1f`. |
| `sheetGesturesEnabled` | `false` | Prevents list scrolls from moving or expanding the sheet. Scrim, back, and close dismissal remain available. |

Set `separateCountriesByLetter = false` for one continuous country list:

```kotlin
val config = CountryCodePickerConfig(
    list = CountryCodePickerListConfig(
        separateCountriesByLetter = false,
    ),
)
```

### Strings

```kotlin
val strings = CountryCodePickerStrings(
    title = "Select your country",
    searchPlaceholder = "Search country or code",
    recent = "Recent",
    allCountries = "All countries",
    searchResults = "Search results",
    noResults = "No countries found",
    close = "Close",
    clearSearch = "Clear search",
)

CountryCodePicker(
    state = pickerState,
    config = CountryCodePickerConfig(
        list = CountryCodePickerListConfig(strings = strings),
    ),
)
```

### List text styles

List typography inherits the corresponding host `MaterialTheme` styles by default. Override only the properties your app needs:

```kotlin
val config = CountryCodePickerConfig(
    list = CountryCodePickerListConfig(
        textStyles = CountryCodePickerListTextStyles(
            title = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold),
            search = TextStyle(fontSize = 14.sp),
            sectionTitle = TextStyle(fontWeight = FontWeight.SemiBold),
            letterHeader = TextStyle(fontSize = 18.sp),
            countryName = TextStyle(fontSize = 15.sp),
            callingCode = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
            emptyState = TextStyle(fontSize = 16.sp),
        ),
    ),
)
```

Every field uses `TextStyle.Default` or a small default weight override, then merges with the host typography. Primary and selected text default to `MaterialTheme.colorScheme.onSurface`, while secondary text defaults to `onSurfaceVariant`. Explicit values in `CountryCodePickerListColors` override those Material colors.

### Search box

```kotlin
val search = CountryCodePickerSearchConfig(
    shape = RoundedCornerShape(14.dp),
    height = 48.dp,
    horizontalPadding = 12.dp,
    iconSize = 19.dp,
    iconSpacing = 8.dp,
    clearButtonSize = 32.dp,
    clearIconSize = 18.dp,
)

val config = CountryCodePickerConfig(
    list = CountryCodePickerListConfig(search = search),
)
```

### Selection boxes

```kotlin
val selection = CountryCodePickerSelectionConfig(
    rowShape = RoundedCornerShape(10.dp),
    rowBorderWidth = 1.dp,
    rowHorizontalInset = 8.dp,
    rowVerticalInset = 2.dp,
    rowContentStartPadding = 12.dp,
    rowContentEndPadding = 8.dp,
    indicatorSize = 20.dp,
    recentCardShape = RoundedCornerShape(10.dp),
    recentCardBorderWidth = 0.5.dp,
    recentCardSpacing = 6.dp,
    recentContentPadding = 8.dp,
    recentIndicatorHeight = 3.dp,
)

val config = CountryCodePickerConfig(
    list = CountryCodePickerListConfig(selection = selection),
)
```

Selection colors remain in `CountryCodePickerListColors`, including `selectedContainer`, `selectedContent`, `accentStrong`, and `divider`.

### List colors

CountryCodeKit ships with a white list surface, soft-neutral search field, and accessible mint selection defaults. List colors belong inside `CountryCodePickerListConfig`:

```kotlin
val config = CountryCodePickerConfig(
    list = CountryCodePickerListConfig(
        colors = CountryCodePickerListColors(
            accent = Color(0xFF46BD99),
            accentStrong = Color(0xFF2F987A),
            sheetContainer = Color.White,
            searchContainer = Color(0xFFF4F6F6),
            selectedContainer = Color(0xFFEEF8F4),
            selectedContent = Color.Unspecified,
            content = Color.Unspecified,
            secondaryContent = Color.Unspecified,
            divider = Color(0xFFE3E9E7),
            scrim = Color(0x52000000),
        ),
    ),
)
```

| Color | Default | Purpose |
| --- | --- | --- |
| `accent` | `#46BD99` | Primary visual accent. |
| `accentStrong` | `#2F987A` | Calling codes and the selection indicator. |
| `sheetContainer` | `#FFFFFF` | Sheet, dialog, full-screen, and ordinary row background. |
| `searchContainer` | `#F4F6F6` | Filled search field background. |
| `selectedContainer` | `#EEF8F4` | Selected-country row background. |
| `selectedContent` | Material `onSurface` | Selected-country name. |
| `content` | Material `onSurface` | Primary text, icons, and letter labels. |
| `secondaryContent` | Material `onSurfaceVariant` | Search icons, placeholders, and empty-state text. |
| `divider` | `#E3E9E7` | Inset row dividers and the sheet drag handle. |
| `scrim` | 32% black | Background dimming behind the sheet. |

### Restrict countries

Provide one list of string ISO codes as either supported or unsupported countries.

```kotlin
CountryCodePicker(
    state = pickerState,
    config = CountryCodePickerConfig(
        countryFilter = CountryCodePickerCountryFilter.Supported(
            isoCodes = listOf("AU", "NZ", "US"),
        ),
        list = CountryCodePickerListConfig(separateCountriesByLetter = false),
    ),
)
```

Alternatively, hide a list of countries while keeping every other catalog entry:

```kotlin
val config = CountryCodePickerConfig(
    countryFilter = CountryCodePickerCountryFilter.Unsupported(
        isoCodes = listOf("AQ", "BV"),
    ),
)
```

Provide either `Supported` or `Unsupported`—never both. ISO codes are case-insensitive, and unknown codes are ignored.

### Custom flags

Rounded flags remain the default. Circular flags can be enabled independently for the trigger and list:

```kotlin
CountryCodePicker(
    state = pickerState,
    config = CountryCodePickerConfig(
        trigger = CountryCodePickerTriggerConfig(
            flagStyle = CountryCodeFlagStyle.Circle,
        ),
        list = CountryCodePickerListConfig(
            flagStyle = CountryCodeFlagStyle.Circle,
        ),
    ),
)
```

You can also replace the bundled PNG flag slot without changing picker behavior:

```kotlin
CountryCodePicker(
    state = pickerState,
    flagContent = { country ->
        MyFlag(country.isoCode)
    },
)
```

`CountryCodeFlag(country, style = CountryCodeFlagStyle.Circle)` is also available as a standalone composable. Custom countries without a bundled asset receive a compact ISO fallback inside the flag area; ISO abbreviations are never shown beside normal country names.

### Custom trigger shape

```kotlin
CountryCodePicker(
    state = pickerState,
    config = CountryCodePickerConfig(
        trigger = CountryCodePickerTriggerConfig(
            shape = RoundedCornerShape(20.dp),
        ),
    ),
)
```

### Trigger colors and formatting

Trigger background, text, chevron, and optional border colors belong inside `CountryCodePickerTriggerConfig`:

```kotlin
CountryCodePicker(
    state = pickerState,
    config = CountryCodePickerConfig(
        trigger = CountryCodePickerTriggerConfig(
            triggerElements = setOf(
                CountryCodePickerTriggerElement.Flag,
                CountryCodePickerTriggerElement.Chevron,
            ),
            shape = RoundedCornerShape(14.dp),
            borderWidth = 1.dp,
            countryCodeTextStyle = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            ),
            chevronSize = 18.dp,
            startPadding = 9.dp,
            topPadding = 9.dp,
            endPadding = 9.dp,
            bottomPadding = 9.dp,
            elementSpacing = 8.dp,
            colors = CountryCodePickerTriggerColors(
                container = Color.White,
                content = Color(0xFF17243A),
                chevron = Color(0xFF17243A),
                border = Color(0xFFDDE7E3),
            ),
        ),
    ),
)
```

`countryCodeTextStyle` is merged with the host application's `MaterialTheme.typography.bodyMedium`. The trigger text defaults to `MaterialTheme.colorScheme.onSurface`, and the chevron follows that resolved text color. Explicit `content` and `chevron` values in `CountryCodePickerTriggerColors` override those defaults independently.

---

## Phone Number Formatting

CountryCodeKit provides standalone formatters that work with any app-owned text field.

```kotlin
val national = CountryCodePhoneFormatter.format(
    number = "0412345678",
    country = pickerState.selectedCountry,
    format = CountryCodePhoneFormat.National,
)

val international = CountryCodePhoneFormatter.format(
    number = "0412345678",
    country = pickerState.selectedCountry,
    format = CountryCodePhoneFormat.International,
)
```

Available final formats are `National`, `International`, `E164`, and `Rfc3966`. Invalid input or an unsupported region is returned unchanged.

The simplest integration uses one optional `CountryCodePhoneState`. It owns the raw phone value and coordinates formatting, validation, country detection, and picker state while the host application continues to own and render the text field:

```kotlin
val phoneState = rememberCountryCodePhoneState(
    initialCountry = CountryCodeCatalog.findByIsoCode("AU")!!,
    pickerConfig = CountryCodePickerConfig(),
)

CountryCodePicker(
    state = phoneState.pickerState,
    config = phoneState.pickerConfig,
)

OutlinedTextField(
    value = phoneState.rawNumber,
    onValueChange = phoneState::updateNumber,
    visualTransformation = phoneState.visualTransformation,
)

val validation = phoneState.validation
val e164 = phoneState.e164
```

By default, this state enables cursor-safe as-you-type formatting, full phone validation, and country detection. Detection updates the picker only for a complete, valid international number beginning with `+`; national numbers retain the selected country. Automatic matches respect the configured country filter and are not added to Recent selections.

Choose lighter processing when needed:

```kotlin
val phoneState = rememberCountryCodePhoneState(
    processing = CountryCodePhoneProcessing.Validate, // or None, DetectCountry
    validationPreset = CountryCodePhoneValidationPreset.PhoneNumber,
    pickerConfig = CountryCodePickerConfig(
        countryFilter = CountryCodePickerCountryFilter.Supported(
            listOf("AU", "NZ", "US"),
        ),
    ),
    formatAsYouType = true,
)
```

`pickerConfig` is the single source of truth for both the visible picker list and automatic detection. Pass `phoneState.pickerConfig` to `CountryCodePicker`; a detected country outside its filter will never replace the selection.

Do not call `formatAsYouType()` and save its formatted result into raw field state. Use `phoneState.visualTransformation` for immediate formatting without delays or cursor jumps. It keeps inserted spaces display-only and preserves cursor mapping.

For custom state management, bind the lower-level validator once and pass each changing number to the required operation:

```kotlin
val phone = CountryCodePhoneValidator(
    pickerState = pickerState,
    preset = CountryCodePhoneValidationPreset.PhoneNumber,
    countryFilter = config.countryFilter,
)

val validationOnly = phone.validate(rawInput)
val detectedCountryOnly = phone.detectCountry(rawInput)
val validationAndDetection = phone.validateAndDetectCountry(rawInput)
```

`validate()` never changes the picker. The two detection operations update it only for a complete, valid international number beginning with `+`, respect supported or unsupported country filters, and do not add automatic matches to recent user selections. National numbers retain the existing country because they cannot be identified reliably.

---

## Phone Number Validation

CountryCodeKit validates locally with its maintained Kotlin Multiplatform port of Google libphonenumber.

```kotlin
val result = CountryCodePhoneValidator.validate(
    number = phoneNumber,
    country = pickerState.selectedCountry,
    preset = CountryCodePhoneValidationPreset.PhoneNumber,
)

when (result.status) {
    CountryCodePhoneStatus.VALID -> println(result.e164)
    CountryCodePhoneStatus.IMPOSSIBLE -> println("Impossible number length")
    CountryCodePhoneStatus.INVALID -> println("Invalid phone number")
    else -> Unit
}
```

Choose only the validation level your app needs:

| Preset | Behavior |
| --- | --- |
| `PhoneNumber` | Full country-aware libphonenumber validation; this remains the default. |
| `PossibleLength` | Accepts numbers with a possible length for the selected country, without requiring an assigned number pattern. |
| `DigitsOnly` | Accepts ASCII digits only and does not require a valid country. |
| `DigitsAndPossibleLength` | Requires digits only plus a possible length for the selected country. |
| `CustomLength(range, digitsOnly)` | Checks your own digit-count range and can optionally reject formatting characters. |

```kotlin
val result = CountryCodePhoneValidator.validate(
    number = phoneNumber,
    country = pickerState.selectedCountry,
    preset = CountryCodePhoneValidationPreset.CustomLength(
        range = 7..12,
        digitsOnly = true,
    ),
)
```

`CountryCodePhoneResult` can provide:

- `status`
- `e164`
- `international`
- `normalizedDigits`
- `isValid`

This is structural phone-number validation, not ownership verification. CountryCodeKit does not send SMS messages, place calls, or contact a verification service.

---

## Platform Support

| Platform | Status | Target |
| --- | --- | --- |
| Android | Supported | Kotlin/Android library and sample APK |
| iOS devices | Supported | `iosArm64` static framework |
| iOS simulator | Supported | `iosSimulatorArm64` static framework |

CountryCodeKit does not publish desktop, standalone JVM, web, watchOS, tvOS, Linux, or Windows targets.

---

## Run the Sample and Tests

Build the Android sample and run the shared tests/framework checks:

```shell
./gradlew :sample:composeApp:assembleDebug
./gradlew iosSimulatorArm64Test
./gradlew linkDebugFrameworkIosSimulatorArm64
```

For iOS, open `sample/iosApp/CountryCodeKitSample.xcodeproj` in Xcode and run the `CountryCodeKitSample` scheme on an iPhone simulator or device. Xcode builds and embeds the shared Compose framework automatically.

The sample demonstrates every picker style and live validation. The test suite checks the complete country metadata set, bundled flag coverage, shared calling codes, search, recents, optional alphabetical sections, picker state, phone validation, and Compose interactions.

---

## Data Sources and Updates

[`UPSTREAMS.properties`](UPSTREAMS.properties) records the exact upstream revisions used by each release:

- [Google libphonenumber](https://github.com/google/libphonenumber) for calling codes, parsing, formatting, and validation metadata under Apache License 2.0.
- [flag-icons](https://github.com/lipis/flag-icons) for SVG flag artwork under the MIT License. CountryCodeKit converts reviewed source artwork into Android/iOS-compatible PNG resources.
- [libphonenumber-kotlin](https://github.com/luca992/libphonenumber-kotlin) as the original Kotlin port baseline under Apache License 2.0.

Run the sync scripts against reviewed local upstream checkouts, update the pins, run the complete suite, and publish a new CountryCodeKit version. A weekly workflow checks whether pinned sources have advanced. Vendored-source changes must never be shipped silently in an existing artifact version.

See [`third_party/`](third_party/) for complete notices and provenance.

---

## License

CountryCodeKit is available under the [MIT License](LICENSE). Bundled third-party data, source ports, and artwork retain their respective licenses described in the third-party notice directories.
