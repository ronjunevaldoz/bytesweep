package io.github.ronjunevaldoz.bytesweep

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.ronjunevaldoz.bytesweep.di.initKoin

fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Bytesweep",
        ) {
            App()
        }
    }
}