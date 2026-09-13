package com.example.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class KittNavTab(val title: String, val icon: ImageVector) {
  COCKPIT("Kokpit", Icons.Default.Dashboard),
  DIAGNOSTICS("Teşhis", Icons.Default.Memory),
  LOGS("Kayıtlar", Icons.Default.Archive),
  SETTINGS("Ayarlar", Icons.Default.Settings)
}
