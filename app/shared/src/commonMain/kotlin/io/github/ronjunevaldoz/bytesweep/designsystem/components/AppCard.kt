package io.github.ronjunevaldoz.bytesweep.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import io.github.ronjunevaldoz.bytesweep.designsystem.theme.LocalContentColor
import io.github.ronjunevaldoz.bytesweep.designsystem.theme.appTheme

/** Card surface variants. */
enum class CardVariant { Default, Elevated, Filled }

/**
 * Card primitive built on Foundation — no Material dependency. Provides
 * [LocalContentColor] so text inside inherits `onSurface`.
 */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    variant: CardVariant = CardVariant.Default,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = appTheme.colors
    val shape = RoundedCornerShape(appTheme.shapes.xxl)

    val base = when (variant) {
        CardVariant.Elevated -> Modifier.shadow(elevation = 4.dp, shape = shape)
        else -> Modifier
    }
    val surface = when (variant) {
        CardVariant.Filled -> colors.surfaceVariant
        else -> colors.surface
    }
    val withBorder =
        if (variant == CardVariant.Default) {
            Modifier.border(1.dp, colors.border, shape)
        } else {
            Modifier
        }

    Column(
        modifier = modifier
            .then(base)
            .clip(shape)
            .background(surface, shape)
            .then(withBorder)
            .padding(appTheme.spacing.lg),
    ) {
        CompositionLocalProvider(LocalContentColor provides colors.onSurface) {
            content()
        }
    }
}
