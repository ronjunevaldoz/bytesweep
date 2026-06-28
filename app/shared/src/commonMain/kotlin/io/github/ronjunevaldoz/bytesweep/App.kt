package io.github.ronjunevaldoz.bytesweep

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.github.ronjunevaldoz.bytesweep.ui.ScannerScreen
import io.github.ronjunevaldoz.bytesweep.ui.theme.BytesweepTheme

@Composable
@Preview
fun App() {
    BytesweepTheme {
        ScannerScreen()
    }
}
