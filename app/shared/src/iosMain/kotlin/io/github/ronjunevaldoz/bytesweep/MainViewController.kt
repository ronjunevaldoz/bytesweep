package io.github.ronjunevaldoz.bytesweep

import androidx.compose.ui.window.ComposeUIViewController
import io.github.ronjunevaldoz.bytesweep.di.initKoin

private var koinStarted = false

fun MainViewController() = ComposeUIViewController {
    if (!koinStarted) {
        initKoin()
        koinStarted = true
    }
    App()
}