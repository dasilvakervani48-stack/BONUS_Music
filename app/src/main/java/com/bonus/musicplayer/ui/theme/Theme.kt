package com.bonus.musicplayer.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.material3.darkColorScheme

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF007AFF),
    onPrimary = Color.White,

    background = Color.White,
    onBackground = Color.Black,

    surface = Color.White,
    onSurface = Color.Black,

    surfaceVariant = Color.White,
    onSurfaceVariant = Color.Black
)
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4DA3FF),
    onPrimary = Color.Black,

    background = Color.Black,
    onBackground = Color.White,

    surface = Color.Black,
    onSurface = Color.White,

    surfaceVariant = Color(0xFF1C1C1E),
    onSurfaceVariant = Color.White
)

@Composable
fun BONUSMusicTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val view = LocalView.current

    SideEffect {
        val context = view.context

        if (context is Activity) {
            val window = context.window

            WindowCompat.setDecorFitsSystemWindows(
                window,
                false
            )

            window.statusBarColor =
                android.graphics.Color.TRANSPARENT

            window.navigationBarColor =
                android.graphics.Color.TRANSPARENT

            val controller =
                WindowCompat.getInsetsController(
                    window,
                    view
                )

            controller.isAppearanceLightStatusBars =
                !darkTheme

            controller.isAppearanceLightNavigationBars =
                !darkTheme
        }
    }

    MaterialTheme(
        colorScheme =
            if (darkTheme) {
                DarkColorScheme
            } else {
                LightColorScheme
            },
        shapes = Shapes,
        typography = Typography,
        content = content
    )
}
