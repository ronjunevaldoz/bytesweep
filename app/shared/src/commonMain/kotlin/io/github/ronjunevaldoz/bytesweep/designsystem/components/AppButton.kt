package io.github.ronjunevaldoz.bytesweep.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ronjunevaldoz.bytesweep.designsystem.theme.LocalContentColor
import io.github.ronjunevaldoz.bytesweep.designsystem.theme.appTheme

/** shadcn-inspired button variants. Resolved against theme tokens at composition. */
enum class ButtonVariant { Primary, Secondary, Outline, Ghost, Destructive }

enum class ButtonSize(val height: Dp, val padding: PaddingValues) {
    Sm(32.dp, PaddingValues(horizontal = 12.dp, vertical = 4.dp)),
    Md(40.dp, PaddingValues(horizontal = 16.dp, vertical = 8.dp)),
    Lg(48.dp, PaddingValues(horizontal = 20.dp, vertical = 12.dp)),
}

/**
 * Button primitive built on Foundation `clickable` — no Material dependency, no ripple.
 * Provides [LocalContentColor] so [AppText] children inherit the correct foreground color.
 */
@Composable
fun AppButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: ButtonVariant = ButtonVariant.Primary,
    size: ButtonSize = ButtonSize.Md,
    content: @Composable () -> Unit,
) {
    val colors = appTheme.colors
    val shape = RoundedCornerShape(appTheme.shapes.md)
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val background: Color = when (variant) {
        ButtonVariant.Primary -> if (pressed) colors.primaryPressed else colors.primary
        ButtonVariant.Secondary -> colors.secondary
        ButtonVariant.Destructive -> colors.destructive
        ButtonVariant.Outline -> if (pressed) colors.secondary else Color.Transparent
        ButtonVariant.Ghost -> if (pressed) colors.secondary else Color.Transparent
    }
    val contentColor: Color = when (variant) {
        ButtonVariant.Primary -> colors.onPrimary
        ButtonVariant.Secondary -> colors.onSecondary
        ButtonVariant.Destructive -> colors.onDestructive
        ButtonVariant.Outline, ButtonVariant.Ghost -> colors.onSurface
    }
    val border: BorderStroke? =
        if (variant == ButtonVariant.Outline) BorderStroke(1.dp, colors.border) else null

    Row(
        modifier = modifier
            .alpha(if (enabled) 1f else 0.38f)
            .clip(shape)
            .background(background, shape)
            .then(if (border != null) Modifier.border(border, shape) else Modifier)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .defaultMinSize(minHeight = size.height)
            .padding(size.padding),
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor, content = content)
    }
}
