@file:OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    id("com.android.library")
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.vanniktech.maven.publish)
}

group = "io.github.tharukack"
version = "0.1.0"

kotlin {
    androidTarget {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
    }

    listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "CountryCodeKit"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.compose.runtime)
            api(libs.compose.ui)
            api(libs.compose.foundation)
            implementation(compose.components.resources)
            implementation(libs.compose.material3)
            implementation(libs.compose.material.icons.core)
            implementation(libs.okio)
            implementation(libs.kermit)
        }
        androidMain.dependencies {
            implementation(libs.androidx.startup.runtime)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(compose.uiTest)
        }
    }
}

android {
    namespace = "io.github.tharukack.countrycodekit"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "io.github.tharukack.countrycodekit.generated.resources"
    generateResClass = always
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    coordinates(
        groupId = "io.github.tharukack",
        artifactId = "countrycodekit",
        version = version.toString(),
    )

    pom {
        name.set("CountryCodeKit")
        description.set(
            "A modern Compose Multiplatform country calling-code picker with bundled flags and libphonenumber validation."
        )
        inceptionYear.set("2026")
        url.set("https://github.com/tharukack/countrycodekit")

        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
                distribution.set("repo")
            }
        }
        developers {
            developer {
                id.set("tharukack")
                name.set("Tharuka Chathura")
                url.set("https://github.com/tharukack")
            }
        }
        scm {
            url.set("https://github.com/tharukack/countrycodekit")
            connection.set("scm:git:git://github.com/tharukack/countrycodekit.git")
            developerConnection.set("scm:git:ssh://git@github.com/tharukack/countrycodekit.git")
        }
    }
}
