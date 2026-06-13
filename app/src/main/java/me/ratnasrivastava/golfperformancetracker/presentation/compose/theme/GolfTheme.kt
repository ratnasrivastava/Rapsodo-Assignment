package me.ratnasrivastava.golfperformancetracker.presentation.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GreenPrimary = Color(0xFF2E7D32)
private val GreenPrimaryDark = Color(0xFF1B5E20)
private val GreenLight = Color(0xFFA5D6A7)
private val GreenContainerLight = Color(0xFFC8E6C9)
private val GreenContainerDark = Color(0xFF0F3D14)

private val SurfaceLight = Color(0xFFFCFDF7)
private val SurfaceDark = Color(0xFF1A1C18)
private val OnSurfaceLight = Color(0xFF1A1C18)
private val OnSurfaceDark = Color(0xFFE2E3DC)
private val SurfaceVariantLight = Color(0xFFDEE5D8)
private val SurfaceVariantDark = Color(0xFF424940)
private val OutlineLight = Color(0xFF72796F)
private val OutlineDark = Color(0xFF8C9388)

private val LightColors = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = Color.White,
    primaryContainer = GreenContainerLight,
    onPrimaryContainer = GreenPrimaryDark,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OutlineLight,
    outline = OutlineLight,
    background = SurfaceLight,
    onBackground = OnSurfaceLight
)

private val DarkColors = darkColorScheme(
    primary = GreenLight,
    onPrimary = GreenPrimaryDark,
    primaryContainer = GreenContainerDark,
    onPrimaryContainer = GreenLight,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OutlineDark,
    outline = OutlineDark,
    background = SurfaceDark,
    onBackground = OnSurfaceDark
)

@Composable
fun GolfTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}