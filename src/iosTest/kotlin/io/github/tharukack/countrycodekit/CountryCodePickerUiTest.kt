package io.github.tharukack.countrycodekit

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class CountryCodePickerUiTest {
    @Test
    fun triggerOpensSearchAndSelectsCountry() = runComposeUiTest {
        val state = CountryCodePickerState(CountryCodeCatalog.findByIsoCode("AU")!!)
        setContent { MaterialTheme { CountryCodePicker(state, config = CountryCodePickerConfig(style = CountryCodePickerStyle.Dialog)) } }

        onNodeWithTag(CountryCodePickerTestTags.Trigger).performClick()
        onNodeWithTag(CountryCodePickerTestTags.Container).assertExists()
        onNodeWithTag(CountryCodePickerTestTags.Search).performTextInput("Canada")
        onNodeWithTag(CountryCodePickerTestTags.country("CA")).performClick()
        waitForIdle()

        assertEquals("CA", state.selectedCountry.isoCode)
        onNodeWithTag(CountryCodePickerTestTags.Container).assertDoesNotExist()
    }

    @Test
    fun impossibleSearchShowsEmptyState() = runComposeUiTest {
        val state = CountryCodePickerState(CountryCodeCatalog.findByIsoCode("AU")!!).also { it.open() }
        setContent { MaterialTheme { CountryCodePicker(state, config = CountryCodePickerConfig(style = CountryCodePickerStyle.Dialog)) } }
        onNodeWithTag(CountryCodePickerTestTags.Search).performTextInput("not-a-country")
        onNodeWithTag(CountryCodePickerTestTags.NoResults).assertExists()
    }

    @Test
    fun everyStyleRendersItsContainer() {
        CountryCodePickerStyle.entries.forEach { style ->
            runComposeUiTest {
                val state = CountryCodePickerState(CountryCodeCatalog.findByIsoCode("AU")!!).also { it.open() }
                setContent { MaterialTheme { CountryCodePicker(state, config = CountryCodePickerConfig(style = style)) } }
                onNodeWithTag(CountryCodePickerTestTags.Container).assertExists()
            }
        }
    }

    @Test
    fun customCountryWithoutAssetUsesIsoFallback() = runComposeUiTest {
        val custom = CountryCode("ZZ", "Custom region", 999)
        setContent { MaterialTheme { CountryCodeFlag(custom) } }
        onNodeWithText("ZZ").assertExists()
    }

    @Test
    fun recentSelectionsAppearWithoutIsoLabels() = runComposeUiTest {
        val australia = CountryCodeCatalog.findByIsoCode("AU")!!
        val canada = CountryCodeCatalog.findByIsoCode("CA")!!
        val state = CountryCodePickerState(australia)
        state.select(canada)
        state.open()

        setContent {
            MaterialTheme {
                CountryCodePicker(
                    state,
                    config = CountryCodePickerConfig(style = CountryCodePickerStyle.Dialog),
                )
            }
        }

        onNodeWithTag(CountryCodePickerTestTags.RecentSection).assertExists()
        onNodeWithTag(CountryCodePickerTestTags.recentCountry("CA")).assertExists()
        onNodeWithText("CA").assertDoesNotExist()
    }

    @Test
    fun letterSeparationCanBeDisabled() {
        listOf(true, false).forEach { separate ->
            runComposeUiTest {
                val state = CountryCodePickerState(CountryCodeCatalog.findByIsoCode("AU")!!).also { it.open() }
                setContent {
                    MaterialTheme {
                        CountryCodePicker(
                            state,
                            config = CountryCodePickerConfig(
                                style = CountryCodePickerStyle.Dialog,
                                countryList = CountryCodePickerCountryListConfig(
                                    separateCountriesByLetter = separate,
                                ),
                            ),
                        )
                    }
                }

                if (separate) {
                    onNodeWithTag(CountryCodePickerTestTags.letter("A")).assertExists()
                } else {
                    onNodeWithTag(CountryCodePickerTestTags.letter("A")).assertDoesNotExist()
                }
            }
        }
    }

    @Test
    fun supportedCountryFilterUsesStringIsoCodes() = runComposeUiTest {
        val state = CountryCodePickerState(CountryCodeCatalog.findByIsoCode("AU")!!).also { it.open() }
        setContent {
            MaterialTheme {
                CountryCodePicker(
                    state = state,
                    config = CountryCodePickerConfig(
                        style = CountryCodePickerStyle.Dialog,
                        countryFilter = CountryCodePickerCountryFilter.Supported(
                            isoCodes = listOf("au", "NZ"),
                        ),
                    ),
                )
            }
        }

        onNodeWithTag(CountryCodePickerTestTags.country("AU")).assertExists()
        onNodeWithTag(CountryCodePickerTestTags.country("US")).assertDoesNotExist()
    }

    @Test
    fun unsupportedCountryFilterRemovesOnlyListedIsoCodes() = runComposeUiTest {
        val state = CountryCodePickerState(CountryCodeCatalog.findByIsoCode("AU")!!).also { it.open() }
        setContent {
            MaterialTheme {
                CountryCodePicker(
                    state = state,
                    config = CountryCodePickerConfig(
                        style = CountryCodePickerStyle.Dialog,
                        countryFilter = CountryCodePickerCountryFilter.Unsupported(
                            isoCodes = listOf("AU"),
                        ),
                    ),
                )
            }
        }

        onNodeWithTag(CountryCodePickerTestTags.country("AU")).assertDoesNotExist()
        onNodeWithTag(CountryCodePickerTestTags.country("AF")).assertExists()
    }
}
