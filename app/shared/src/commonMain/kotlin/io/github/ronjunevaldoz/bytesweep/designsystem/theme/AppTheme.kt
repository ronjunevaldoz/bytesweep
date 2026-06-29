package io.github.ronjunevaldoz.bytesweep.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import io.github.ronjunevaldoz.bytesweep.designsystem.tokens.AppColors
import io.github.ronjunevaldoz.bytesweep.designsystem.tokens.AppShapes
import io.github.ronjunevaldoz.bytesweep.designsystem.tokens.AppSpacing
import io.github.ronjunevaldoz.bytesweep.designsystem.tokens.AppTypography
import io.github.ronjunevaldoz.bytesweep.designsystem.tokens.DarkColors
import io.github.ronjunevaldoz.bytesweep.designsystem.tokens.LightColors

/** The full token set exposed to composables via [LocalAppTheme]. */
@Immutable
data class AppThemeTokens(
    val colors: AppColors,
    val typography: AppTypography,
    val shapes: AppShapes,
    val spacing: AppSpacing,
) {
    companion object {
        fun dark(
            colors: AppColors = DarkColors,
            typography: AppTypography = AppTypography(),
            shapes: AppShapes = AppShapes(),
            spacing: AppSpacing = AppSpacing(),
        ) = AppThemeTokens(colors, typography, shapes, spacing)

        fun light(
            colors: AppColors = LightColors,
            typography: AppTypography = AppTypography(),
            shapes: AppShapes = AppShapes(),
            spacing: AppSpacing = AppSpacing(),
        ) = AppThemeTokens(colors, typography, shapes, spacing)
    }
}

val LocalAppTheme: ProvidableCompositionLocal<AppThemeTokens> =
    staticCompositionLocalOf { AppThemeTokens.dark() }

/**
 * Preferred foreground color for the current container. Components like [AppButton]
 * provide this so [AppText] children inherit the right `onX` color without plumbing.
 */
val LocalContentColor: ProvidableCompositionLocal<Color> =
    staticCompositionLocalOf { Color.Unspecified }

/** Convenience accessor for the active tokens inside a composable. */
val appTheme: AppThemeTokens
    @Composable get() = LocalAppTheme.current

/**
 * Bytesweep theme root. Dark-leaning: defaults to the dark palette. Pass
 * `darkTheme = isSystemInDarkTheme()` at the entry point to follow the system setting.
 */
@Composable
fun AppTheme(
    darkTheme: Boolean = true,
    tokens: AppThemeTokens = if (darkTheme) AppThemeTokens.dark() else AppThemeTokens.light(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalAppTheme provides tokens,
        LocalContentColor provides tokens.colors.onSurface,
        content = content,
    )
}

/** Follows the OS dark/light setting; use when you don't want the dark-leaning default. */
@Composable
fun AppThemeSystem(content: @Composable () -> Unit) =
    AppTheme(darkTheme = isSystemInDarkTheme(), content = content)
