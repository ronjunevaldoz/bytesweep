package io.github.ronjunevaldoz.bytesweep.designsystem.components

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import io.github.ronjunevaldoz.bytesweep.designsystem.theme.LocalContentColor
import io.github.ronjunevaldoz.bytesweep.designsystem.theme.appTheme

/** Named entries into the typography scale (avoids a name clash with Compose `TextStyle`). */
enum class AppTextStyle {
    DisplayLarge, DisplayMedium,
    TitleLarge, TitleMedium, TitleSmall,
    BodyLarge, BodyMedium, BodySmall,
    LabelLarge, LabelSmall,
}

/**
 * Text primitive built on [BasicText] — no Material dependency.
 *
 * Color resolution: explicit [color] > [muted] > [LocalContentColor] (provided by
 * containers like [AppButton]) > theme `onSurface`.
 */
@Composable
fun AppText(
    text: String,
    modifier: Modifier = Modifier,
    style: AppTextStyle = AppTextStyle.BodyMedium,
    muted: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    color: Color = Color.Unspecified,
) {
    val theme = appTheme
    val resolved = when (style) {
        AppTextStyle.DisplayLarge -> theme.typography.displayLarge
        AppTextStyle.DisplayMedium -> theme.typography.displayMedium
        AppTextStyle.TitleLarge -> theme.typography.titleLarge
        AppTextStyle.TitleMedium -> theme.typography.titleMedium
        AppTextStyle.TitleSmall -> theme.typography.titleSmall
        AppTextStyle.BodyLarge -> theme.typography.bodyLarge
        AppTextStyle.BodyMedium -> theme.typography.bodyMedium
        AppTextStyle.BodySmall -> theme.typography.bodySmall
        AppTextStyle.LabelLarge -> theme.typography.labelLarge
        AppTextStyle.LabelSmall -> theme.typography.labelSmall
    }

    val inherited = LocalContentColor.current
    val textColor = when {
        color != Color.Unspecified -> color
        muted -> theme.colors.onSurfaceVariant
        inherited != Color.Unspecified -> inherited
        else -> theme.colors.onSurface
    }

    BasicText(
        text = text,
        modifier = modifier,
        style = resolved.copy(color = textColor),
        maxLines = maxLines,
        overflow = overflow,
    )
}
