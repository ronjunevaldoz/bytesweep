package io.github.ronjunevaldoz.bytesweep

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import io.github.takahirom.roborazzi.captureRoboImage
import io.github.ronjunevaldoz.bytesweep.designsystem.components.AppButton
import io.github.ronjunevaldoz.bytesweep.designsystem.components.AppCard
import io.github.ronjunevaldoz.bytesweep.designsystem.components.AppText
import io.github.ronjunevaldoz.bytesweep.designsystem.components.AppTextStyle
import io.github.ronjunevaldoz.bytesweep.designsystem.components.ButtonVariant
import io.github.ronjunevaldoz.bytesweep.designsystem.components.CardVariant
import io.github.ronjunevaldoz.bytesweep.designsystem.theme.AppTheme
import io.github.ronjunevaldoz.bytesweep.designsystem.theme.appTheme
import org.junit.Rule
import kotlin.test.Test

/** Records golden images for the custom design system in light and dark. */
class DesignSystemScreenshotTest {

    @get:Rule
    val rule = createComposeRule()

    @Composable
    private fun Showcase() {
        Column(
            modifier = Modifier
                .background(appTheme.colors.background)
                .padding(appTheme.spacing.lg)
                .width(360.dp),
            verticalArrangement = Arrangement.spacedBy(appTheme.spacing.md),
        ) {
            AppText("Bytesweep", style = AppTextStyle.TitleLarge)
            AppText("Reclaim space across all your devices", style = AppTextStyle.BodySmall, muted = true)
            AppButton(onClick = {}, variant = ButtonVariant.Primary) { AppText("Scan storage") }
            AppButton(onClick = {}, variant = ButtonVariant.Secondary) { AppText("Secondary") }
            AppButton(onClick = {}, variant = ButtonVariant.Outline) { AppText("Outline") }
            AppButton(onClick = {}, variant = ButtonVariant.Ghost) { AppText("Ghost") }
            AppButton(onClick = {}, variant = ButtonVariant.Destructive) { AppText("Clean all") }
            AppButton(onClick = {}, enabled = false) { AppText("Disabled") }
            AppCard(modifier = Modifier.fillMaxWidth(), variant = CardVariant.Default) {
                AppText("Default card", style = AppTextStyle.TitleSmall)
                AppText("48.3 MB reclaimable", style = AppTextStyle.BodySmall, muted = true)
            }
            AppCard(modifier = Modifier.fillMaxWidth(), variant = CardVariant.Filled) {
                AppText("Filled card", style = AppTextStyle.TitleSmall)
            }
        }
    }

    @Test
    fun designSystem_light() {
        rule.setContent { AppTheme(darkTheme = false) { Showcase() } }
        rule.onRoot().captureRoboImage("$SNAPSHOTS/design_system_light.png")
    }

    @Test
    fun designSystem_dark() {
        rule.setContent { AppTheme(darkTheme = true) { Showcase() } }
        rule.onRoot().captureRoboImage("$SNAPSHOTS/design_system_dark.png")
    }

    private companion object {
        const val SNAPSHOTS = "src/jvmTest/snapshots"
    }
}
