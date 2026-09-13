package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val KittColorScheme = darkColorScheme(
  primary = KittRed,
  onPrimary = Color.White,
  primaryContainer = KittRedDark,
  onPrimaryContainer = Color.White,
  secondary = KittAmber,
  onSecondary = Color.Black,
  secondaryContainer = KittSurfaceVariant,
  onSecondaryContainer = KittTextPrimary,
  tertiary = KittCyan,
  onTertiary = Color.Black,
  background = KittBlack,
  onBackground = KittTextPrimary,
  surface = KittDarkSurface,
  onSurface = KittTextPrimary,
  surfaceVariant = KittSurfaceVariant,
  onSurfaceVariant = KittTextSecondary,
  outline = KittBorder,
  outlineVariant = Color(0x22FF0D34)
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = KittColorScheme,
    typography = Typography,
    content = content
  )
}

