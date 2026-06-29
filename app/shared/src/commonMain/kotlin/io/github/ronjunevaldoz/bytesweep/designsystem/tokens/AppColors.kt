package io.github.ronjunevaldoz.bytesweep.designsystem.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Color tokens for the Bytesweep design system. No Material dependency — these are the
 * single source of truth for every surface, text, border, and accent color.
 *
 * Bytesweep is a dark-leaning utility app; [DarkColors] is the primary palette and
 * [LightColors] is the complement. Brand accent is teal.
 */
@Immutable
data class AppColors(
    // Brand / primary action
    val primary: Color,
    val primaryHover: Color,
    val primaryPressed: Color,
    val onPrimary: Color,

    // Secondary / neutral fill
    val secondary: Color,
    val onSecondary: Color,

    // Destructive
    val destructive: Color,
    val onDestructive: Color,

    // Surfaces
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,

    // Borders
    val border: Color,
    val borderFocus: Color,

    // Muted / ghost
    val muted: Color,
    val onMuted: Color,

    // Status
    val success: Color,
    val warning: Color,
    val error: Color,

    val isLight: Boolean,
)

private val BrandTeal = Color(0xFF0FB5AE)

val DarkColors = AppColors(
    primary          = BrandTeal,
    primaryHover     = Color(0xFF15C7BF),
    primaryPressed   = Color(0xFF0C9A94),
    onPrimary        = Color(0xFF00201E),

    secondary        = Color(0xFF1E2426),
    onSecondary      = Color(0xFFE6EAEB),

    destructive      = Color(0xFFE5484D),
    onDestructive    = Color(0xFFFFFFFF),

    background       = Color(0xFF0B0E0F),
    surface          = Color(0xFF14181A),
    surfaceVariant   = Color(0xFF1E2426),
    onSurface        = Color(0xFFE6EAEB),
    onSurfaceVariant = Color(0xFF9AA4A6),

    border           = Color(0xFF2A3133),
    borderFocus      = BrandTeal,

    muted            = Color(0xFF1E2426),
    onMuted          = Color(0xFF9AA4A6),

    success          = Color(0xFF30A46C),
    warning          = Color(0xFFE2A336),
    error            = Color(0xFFE5484D),

    isLight          = false,
)

val LightColors = AppColors(
    primary          = BrandTeal,
    primaryHover     = Color(0xFF0DA39C),
    primaryPressed   = Color(0xFF0A8C87),
    onPrimary        = Color(0xFFFFFFFF),

    secondary        = Color(0xFFF1F4F4),
    onSecondary      = Color(0xFF0B0E0F),

    destructive      = Color(0xFFDC2626),
    onDestructive    = Color(0xFFFFFFFF),

    background       = Color(0xFFFFFFFF),
    surface          = Color(0xFFFFFFFF),
    surfaceVariant   = Color(0xFFF1F4F4),
    onSurface        = Color(0xFF0B0E0F),
    onSurfaceVariant = Color(0xFF5A6466),

    border           = Color(0xFFE0E5E5),
    borderFocus      = BrandTeal,

    muted            = Color(0xFFF1F4F4),
    onMuted          = Color(0xFF5A6466),

    success          = Color(0xFF16A34A),
    warning          = Color(0xFFD97706),
    error            = Color(0xFFDC2626),

    isLight          = true,
)
