package io.github.ronjunevaldoz.bytesweep.designsystem.previews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.ronjunevaldoz.bytesweep.designsystem.components.AppButton
import io.github.ronjunevaldoz.bytesweep.designsystem.components.AppCard
import io.github.ronjunevaldoz.bytesweep.designsystem.components.AppText
import io.github.ronjunevaldoz.bytesweep.designsystem.components.AppTextStyle
import io.github.ronjunevaldoz.bytesweep.designsystem.components.ButtonVariant
import io.github.ronjunevaldoz.bytesweep.designsystem.components.CardVariant
import io.github.ronjunevaldoz.bytesweep.designsystem.theme.AppTheme
import io.github.ronjunevaldoz.bytesweep.designsystem.theme.appTheme

@Composable
private fun Showcase() {
    Column(
        modifier = Modifier
            .background(appTheme.colors.background)
            .padding(appTheme.spacing.lg)
            .fillMaxWidth(),
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

@Preview
@Composable
fun DesignSystemDarkPreview() {
    AppTheme(darkTheme = true) { Showcase() }
}

@Preview
@Composable
fun DesignSystemLightPreview() {
    AppTheme(darkTheme = false) { Showcase() }
}
