# CountryCodeKit iOS Sample

This SwiftUI host runs the shared Compose Multiplatform sample on iOS.

The Compose UI entry point is exported from `sample/composeApp` as the `CountryCodeKitSample` framework through `MainViewController()`.

Open `CountryCodeKitSample.xcodeproj` in Xcode, select an iPhone simulator or connected iPhone, and run the `CountryCodeKitSample` scheme. Its build phase creates and embeds the Gradle-generated framework automatically.

Requirements: Xcode 16 or newer, iOS 15 or newer, and Java 17 available to Gradle.
