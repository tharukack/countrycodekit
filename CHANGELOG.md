# Changelog

All notable changes to CountryCodeKit will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project follows semantic versioning.

## [Unreleased]

## [1.0.0] - 2026-08-13

### Library changes

- Increased the default trigger country-code size, tightened the chevron bounds and element spacing, and made its background transparent.
- Made each trigger padding edge independently configurable, with a `9.dp` default on every side.
- Added independently configurable circular flag styles for the trigger and country list while retaining rounded flags by default.
- Matched the default trigger chevron color to the country-code text color.
- Made primary, selected, secondary, and trigger text colors inherit semantic Material theme colors unless explicitly overridden.
- Simplified initial recent selections to accept ISO-code strings directly.
- Added style-specific bottom-sheet, dialog, and full-screen configuration, including sheet shape, drag handle, border, elevation, width constraints, dialog sizing/dismissal, and full-screen inset/content-width controls.
- Renamed the generic `list` API to the clearer `countryList` configuration, including its colors and text-style types.
- Reorganized the README around a fully customized integration followed by a complete API and customization reference.
- Added focused, copyable examples before each API customization explanation.

### Sample changes

- Simplified the sample home header and clarified default versus app-styled picker integrations.

## [0.1.0] - 2026-08-12

### Library changes

- Renamed the project, Maven artifact, frameworks, packages, sample identifiers, and public APIs to CountryCodeKit.
- Added the search-first picker design with polished color, spacing, shape, selection, and row defaults.
- Added state-backed recent selections with configurable visibility and row limit instead of hard-coded popular countries.
- Added configurable alphabetical country sections, enabled by default.
- Removed ISO abbreviations from ordinary country rows while retaining ISO search and missing-flag fallback behavior.
- Kept typography fully inherited from the host application's `MaterialTheme`.
- Added bottom-sheet, dialog, and full-screen styles, with bottom sheet as the optional configuration's default.
- Added configurable trigger content, container/content/chevron/border colors, optional border width, and host-defined trigger shapes.
- Grouped public options into clear `trigger` and `list` configurations.
- Added mutually exclusive supported/unsupported country filtering using string ISO-code lists.
- Exposed national, international, E.164, RFC3966, and as-you-type phone-number formatters.
- Added normalized raw phone input and a cursor-aware visual transformation for app-owned fields.
- Added a reusable picker-bound validator factory with validation-only, detection-only, and combined operations for valid international phone numbers.
- Added a unified optional phone state that coordinates raw input, cursor-safe formatting, validation, country detection, and picker state.
- Made the unified phone state's picker configuration the shared source of truth for visible countries and detection filtering.
- Added trigger country-code text styling, chevron sizing, padding, and element-spacing options.
- Kept colors scoped to their respective `trigger` and `list` configurations, with explicitly named color types.
- Added optional list text-style overrides that merge with the host application's Material typography.
- Added complete search-box and selected-row shape, dimension, padding, border, and indicator configuration.
- Bundled flag-icons 7.5.0 artwork as Android/iOS-compatible lossless PNG resources.
- Added the project-maintained libphonenumber KMP port with Google 9.0.36 metadata.
- Compiled the maintained phone engine and metadata directly into CountryCodeKit as a single published module.
- Added full phone-number, possible-length, digits-only, digits-and-length, and custom-length validation presets.
- Restricted supported targets to Android and iOS; removed the standalone JVM/desktop target.

### Project and documentation

- Added an Android and iOS sample application.
- Added exhaustive region metadata, flag coverage, validation, state, alphabetical-section, recents, and Compose UI tests.
- Rebuilt the README using the same installation, quick-start, customization-table, API guidance, platform-support, and maintenance structure used by GuideKit.

[Unreleased]: https://github.com/tharukack/countrycodekit/compare/v1.0.0...HEAD
[1.0.0]: https://github.com/tharukack/countrycodekit/compare/v0.1.0...v1.0.0
[0.1.0]: https://github.com/tharukack/countrycodekit/releases/tag/v0.1.0
