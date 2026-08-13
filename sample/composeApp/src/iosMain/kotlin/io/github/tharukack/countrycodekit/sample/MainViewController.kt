package io.github.tharukack.countrycodekit.sample

import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController(autoDemo: Boolean = false) = ComposeUIViewController {
    App(autoDemo = autoDemo)
}
