package com.samarth.aifinancecoach.presentation.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import com.samarth.aifinancecoach.presentation.theme.Amber
import com.samarth.aifinancecoach.presentation.theme.AmberDark
import com.samarth.aifinancecoach.presentation.theme.AmberLight
import com.samarth.aifinancecoach.presentation.theme.AppShapes
import com.samarth.aifinancecoach.presentation.theme.AppTypography
import com.samarth.aifinancecoach.presentation.theme.DarkBackground
import com.samarth.aifinancecoach.presentation.theme.DarkBorder
import com.samarth.aifinancecoach.presentation.theme.DarkBorderFocus
import com.samarth.aifinancecoach.presentation.theme.DarkSurface
import com.samarth.aifinancecoach.presentation.theme.DarkSurface2
import com.samarth.aifinancecoach.presentation.theme.DarkTextPrimary
import com.samarth.aifinancecoach.presentation.theme.DarkTextSecondary
import com.samarth.aifinancecoach.presentation.theme.ErrorRed
import com.samarth.aifinancecoach.presentation.theme.ErrorRedDark
import com.samarth.aifinancecoach.presentation.theme.ErrorRedLight
import com.samarth.aifinancecoach.presentation.theme.LightBackground
import com.samarth.aifinancecoach.presentation.theme.LightBorder
import com.samarth.aifinancecoach.presentation.theme.LightBorderFocus
import com.samarth.aifinancecoach.presentation.theme.LightSurface
import com.samarth.aifinancecoach.presentation.theme.LightSurface2
import com.samarth.aifinancecoach.presentation.theme.LightTextPrimary
import com.samarth.aifinancecoach.presentation.theme.LightTextSecondary
import com.samarth.aifinancecoach.presentation.theme.MintGreen
import com.samarth.aifinancecoach.presentation.theme.MintGreenDark
import com.samarth.aifinancecoach.presentation.theme.MintGreenLight
import com.samarth.aifinancecoach.presentation.theme.OceanBlue
import com.samarth.aifinancecoach.presentation.theme.OceanBlueDark
import com.samarth.aifinancecoach.presentation.theme.OceanBlueLight

// ─────────────────────────────────────────────
// Dark Color Scheme
// ─────────────────────────────────────────────

val DarkColorScheme = darkColorScheme(
    primary           = MintGreen,
    onPrimary         = Color(0xFF003828),
    primaryContainer  = Color(0xFF004D38),
    onPrimaryContainer= MintGreenLight,

    secondary         = OceanBlue,
    onSecondary       = Color(0xFF003060),
    secondaryContainer= Color(0xFF003F80),
    onSecondaryContainer = OceanBlueLight,

    tertiary          = Amber,
    onTertiary        = Color(0xFF3D2800),
    tertiaryContainer = Color(0xFF563C00),
    onTertiaryContainer = AmberLight,

    error             = ErrorRed,
    onError           = Color(0xFF4D0012),
    errorContainer    = Color(0xFF7A001F),
    onErrorContainer  = ErrorRedLight,

    background        = DarkBackground,
    onBackground      = DarkTextPrimary,

    surface           = DarkSurface,
    onSurface         = DarkTextPrimary,
    surfaceVariant    = DarkSurface2,
    onSurfaceVariant  = DarkTextSecondary,

    outline           = DarkBorder,
    outlineVariant    = DarkBorderFocus,

    scrim             = Color(0x99000000),
    inverseSurface    = LightSurface,
    inverseOnSurface  = LightTextPrimary,
    inversePrimary    = MintGreenDark
)

// ─────────────────────────────────────────────
// Light Color Scheme
// ─────────────────────────────────────────────

val LightColorScheme = lightColorScheme(
    primary           = MintGreenDark,
    onPrimary         = Color(0xFFFFFFFF),
    primaryContainer  = LightSurface2,
    onPrimaryContainer= MintGreenDark,

    secondary         = OceanBlueDark,
    onSecondary       = Color(0xFFFFFFFF),
    secondaryContainer= Color(0xFFD6EEFF),
    onSecondaryContainer = OceanBlueDark,

    tertiary          = AmberDark,
    onTertiary        = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFEDD0),
    onTertiaryContainer = AmberDark,

    error             = ErrorRedDark,
    onError           = Color(0xFFFFFFFF),
    errorContainer    = Color(0xFFFFDADE),
    onErrorContainer  = ErrorRedDark,

    background        = LightBackground,
    onBackground      = LightTextPrimary,

    surface           = LightSurface,
    onSurface         = LightTextPrimary,
    surfaceVariant    = LightSurface2,
    onSurfaceVariant  = LightTextSecondary,

    outline           = LightBorder,
    outlineVariant    = LightBorderFocus,

    scrim             = Color(0x99000000),
    inverseSurface    = DarkSurface,
    inverseOnSurface  = DarkTextPrimary,
    inversePrimary    = MintGreen
)

// ─────────────────────────────────────────────
// App Theme
// ─────────────────────────────────────────────
fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}
@Composable
fun AIFinanceCoachTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val context = LocalContext.current
    val activity = context.findActivity()

    // Make status bar and nav bar match the theme
     SideEffect {
        activity?.window?.let { window ->
            window.statusBarColor =
                if (darkTheme) DarkBackground.toArgb()
                else LightBackground.toArgb()

            window.navigationBarColor =
                if (darkTheme) DarkSurface.toArgb()
                else LightSurface.toArgb()

            val controller = WindowCompat.getInsetsController(window, window.decorView)

            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = AppTypography,
        shapes      = AppShapes,
        content     = content
    )
}
