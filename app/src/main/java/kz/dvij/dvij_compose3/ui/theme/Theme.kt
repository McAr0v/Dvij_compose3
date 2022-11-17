package kz.dvij.dvij_compose3.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat



private val NightDvijThemeColors = darkColorScheme(
    primary = PrimaryColor,
    secondary = Primary100,
    tertiary = Primary100,

    background = Grey100,
    surface = Grey95,
    onPrimary = Grey00,
    onSecondary = Grey00,
    onTertiary = Grey00,
    onBackground = Grey10,
    onSurface = Grey10
)

private val LightDvijThemeColors = lightColorScheme(
    primary = PrimaryColor,
    secondary = Primary100,
    tertiary = Primary100,

    background = Grey100,
    surface = Grey95,
    onPrimary = Grey00,
    onSecondary = Grey00,
    onTertiary = Grey00,
    onBackground = Grey10,
    onSurface = Grey10
)

@Composable
fun CustomDvijTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    // условие - если темная тема, то используй цвета из темного набора, если светлая - из светлого
    val colors = if (darkTheme) {
        // как дойду до темы смены цветов, прописать ниже NightDvijThemeColors. Пока одна тема и там и там
        LightDvijThemeColors
    } else {
        LightDvijThemeColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}


/*

Настройки темы, созданной изначально

@Composable
fun Dvij_compose3Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            (view.context as Activity).window.statusBarColor = colorScheme.primary.toArgb()
            ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
*/