package io.github.ronjunevaldoz.bytesweep

import androidx.compose.foundation.layout.size
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import io.github.takahirom.roborazzi.captureRoboImage
import io.github.ronjunevaldoz.bytesweep.analysis.Recommendation
import io.github.ronjunevaldoz.bytesweep.analysis.Safety
import io.github.ronjunevaldoz.bytesweep.model.JunkCategory
import io.github.ronjunevaldoz.bytesweep.model.JunkItem
import io.github.ronjunevaldoz.bytesweep.presenter.ScannerContract
import io.github.ronjunevaldoz.bytesweep.ui.ScannerContent
import io.github.ronjunevaldoz.bytesweep.ui.theme.BytesweepTheme
import org.junit.Rule
import kotlin.test.Test

/** Records golden images for the main scanner screen across its key states. */
class ScannerContentScreenshotTest {

    @get:Rule
    val rule = createComposeRule()

    private val sample = listOf(
        JunkItem("1", "Glide image cache", "/cache/glide", 48_300_000, JunkCategory.CACHE),
        JunkItem("2", "Temp downloads", "/tmp/dl", 12_500_000, JunkCategory.TEMP, selected = false),
        JunkItem("3", "app.log", "/logs/app.log", 2_100_000, JunkCategory.LOGS),
        JunkItem("4", "old-backup.zip", "/tmp/old-backup.zip", 256_000_000, JunkCategory.LARGE_FILE),
    )

    private fun capture(name: String, darkTheme: Boolean = false, content: @Composable () -> Unit) {
        rule.setContent {
            BytesweepTheme(darkTheme = darkTheme) {
                androidx.compose.foundation.layout.Box(Modifier.size(420.dp, 840.dp)) { content() }
            }
        }
        rule.onRoot().captureRoboImage("src/jvmTest/snapshots/$name.png")
    }

    @Test
    fun scanner_idle() = capture("scanner_idle") {
        ScannerContent(ScannerContract.State(canPickFolder = true), SnackbarHostState(), onIntent = {})
    }

    @Test
    fun scanner_results() = capture("scanner_results") {
        ScannerContent(
            ScannerContract.State(
                hasScanned = true,
                canOpenLocations = true,
                canPickFolder = true,
                items = sample,
            ),
            SnackbarHostState(),
            onIntent = {},
        )
    }

    @Test
    fun scanner_empty() = capture("scanner_empty") {
        ScannerContent(
            ScannerContract.State(hasScanned = true, canPickFolder = true, items = emptyList()),
            SnackbarHostState(),
            onIntent = {},
        )
    }

    @Test
    fun scanner_analyzed_dark() = capture("scanner_analyzed_dark", darkTheme = true) {
        ScannerContent(
            ScannerContract.State(
                hasScanned = true,
                canOpenLocations = true,
                canPickFolder = true,
                items = sample,
                analysisSummary = "Most items are regenerable caches; review the 256 MB backup before deleting.",
                recommendations = mapOf(
                    "1" to Recommendation("1", Safety.SAFE, "Image cache, regenerated on demand"),
                    "3" to Recommendation("3", Safety.SAFE, "Log file, safe to remove"),
                    "4" to Recommendation("4", Safety.KEEP, "Looks like a user backup archive"),
                ),
            ),
            SnackbarHostState(),
            onIntent = {},
        )
    }
}
