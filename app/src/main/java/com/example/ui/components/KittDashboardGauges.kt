package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.KittAmber
import com.example.ui.theme.KittBlack
import com.example.ui.theme.KittCyan
import com.example.ui.theme.KittDarkSurface
import com.example.ui.theme.KittGreen
import com.example.ui.theme.KittRed
import com.example.ui.theme.KittRedDark
import com.example.ui.theme.KittRedGlow
import com.example.ui.theme.KittSurfaceVariant
import com.example.ui.theme.KittTextPrimary
import com.example.ui.theme.KittTextSecondary

enum class KittDriveMode(val label: String, val color: Color) {
  NORMAL_CRUISE("NORMAL", KittAmber),
  AUTO_CRUISE("AUTO CRUISE", KittCyan),
  PURSUIT("PURSUIT", KittRed),
  SUPER_PURSUIT("SPM 450", KittRedGlow)
}

@Composable
fun KittDashboardGauges(
  modifier: Modifier = Modifier,
  currentMode: KittDriveMode,
  onModeSelect: (KittDriveMode) -> Unit,
  speedKmh: Int,
  turboPsi: Float,
  onTurboBoostClick: () -> Unit,
  onScanClick: () -> Unit
) {
  val animatedPsi by animateFloatAsState(
    targetValue = turboPsi,
    animationSpec = tween(500),
    label = "TurboPsiAnim"
  )

  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(KittDarkSurface)
      .border(1.dp, Color(0xFF2E0910), RoundedCornerShape(12.dp))
      .padding(12.dp)
      .testTag("kitt_dashboard_gauges")
  ) {
    // Top Bar: Drive Modes Selector
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      KittDriveMode.values().forEach { mode ->
        val isSelected = mode == currentMode
        val bgColor by animateColorAsState(
          targetValue = if (isSelected) mode.color.copy(alpha = 0.25f) else KittSurfaceVariant,
          label = "ModeBg"
        )
        val borderColor by animateColorAsState(
          targetValue = if (isSelected) mode.color else Color.Transparent,
          label = "ModeBorder"
        )

        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(6.dp))
            .clickable { onModeSelect(mode) }
            .padding(vertical = 6.dp)
            .testTag("mode_${mode.name.lowercase()}"),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = mode.label,
            color = if (isSelected) mode.color else KittTextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Middle Telemetry Row
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Speed Indicator
      Column(
        modifier = Modifier
          .weight(1f)
          .clip(RoundedCornerShape(8.dp))
          .background(KittBlack)
          .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Speed, contentDescription = null, tint = KittAmber, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = "SPEED", color = KittTextSecondary, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        }
        Text(
          text = "$speedKmh",
          color = KittAmber,
          fontSize = 20.sp,
          fontWeight = FontWeight.Black,
          fontFamily = FontFamily.Monospace
        )
        Text(text = "KM/H", color = KittTextSecondary, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
      }

      Spacer(modifier = Modifier.width(8.dp))

      // Turbo PSI
      Column(
        modifier = Modifier
          .weight(1f)
          .clip(RoundedCornerShape(8.dp))
          .background(KittBlack)
          .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Bolt, contentDescription = null, tint = KittRed, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = "TURBO", color = KittTextSecondary, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        }
        Text(
          text = "%.1f".format(animatedPsi),
          color = if (animatedPsi > 25f) KittRedGlow else KittRed,
          fontSize = 20.sp,
          fontWeight = FontWeight.Black,
          fontFamily = FontFamily.Monospace
        )
        Text(text = "PSI", color = KittTextSecondary, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
      }

      Spacer(modifier = Modifier.width(8.dp))

      // Turbo Boost Action Button
      Box(
        modifier = Modifier
          .weight(1.2f)
          .height(58.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(
            Brush.verticalGradient(
              listOf(KittRed, KittRedDark)
            )
          )
          .border(1.5.dp, KittRedGlow, RoundedCornerShape(8.dp))
          .clickable { onTurboBoostClick() }
          .padding(4.dp)
          .testTag("turbo_boost_btn"),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "TURBO BOOST",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace
          )
          Text(
            text = "AKTİF ET",
            color = Color(0xFFFFD1D8),
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Bottom Bar: Molecular Shell (100%) & Scanner Quick Action
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          Icons.Default.Security,
          contentDescription = null,
          tint = KittGreen,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column(modifier = Modifier.weight(1f)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "MOLEKÜLER ZIRH",
              color = KittTextSecondary,
              fontSize = 9.sp,
              fontFamily = FontFamily.Monospace
            )
            Text(
              text = "%100",
              color = KittGreen,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace
            )
          }
          Spacer(modifier = Modifier.height(2.dp))
          LinearProgressIndicator(
            progress = { 1f },
            modifier = Modifier
              .fillMaxWidth()
              .height(4.dp)
              .clip(RoundedCornerShape(2.dp)),
            color = KittGreen,
            trackColor = Color(0xFF0F2618),
          )
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      // Scan Button
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(6.dp))
          .background(KittSurfaceVariant)
          .border(1.dp, KittCyan.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
          .clickable { onScanClick() }
          .padding(horizontal = 10.dp, vertical = 6.dp)
          .testTag("radar_scan_btn"),
        contentAlignment = Alignment.Center
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Radar, contentDescription = null, tint = KittCyan, modifier = Modifier.size(12.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "TARA",
            color = KittCyan,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
        }
      }
    }
  }
}
