package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.KittViewModel
import com.example.ui.components.KittScanner
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

data class SubsystemInfo(
  val name: String,
  val status: String,
  val healthPercent: Float,
  val description: String,
  val icon: ImageVector,
  val accentColor: Color
)

@Composable
fun DiagnosticsScreen(
  viewModel: KittViewModel,
  modifier: Modifier = Modifier
) {
  val driverName by viewModel.driverName.collectAsState()
  var diagnosticActive by remember { mutableStateOf(false) }

  val subsystems = listOf(
    SubsystemInfo(
      name = "Moleküler Bağlı Zırh",
      status = "NOMİNAL (%100)",
      healthPercent = 1.0f,
      description = "Wilton Knight patentli moleküler zırh. Tüm mermilere, patlayıcılara ve kimyasal silahlara karşı dayanıklı.",
      icon = Icons.Default.Security,
      accentColor = KittGreen
    ),
    SubsystemInfo(
      name = "Knight 2000 Yapay Zeka Çekirdeği",
      status = "OPTİMAL (%99.8)",
      healthPercent = 0.998f,
      description = "Sıvı soğutmalı galyum arsenit mikroişlemciler. 100 terabayt nöral matriks, bağımsız karar motoru.",
      icon = Icons.Default.Memory,
      accentColor = KittCyan
    ),
    SubsystemInfo(
      name = "Hidrojen Türbin Güç Ünitesi",
      status = "88°C / HAZIR",
      healthPercent = 0.98f,
      description = "Çift hidrojen türbinli turbo motor. 0-100 km/s hızlanma 0.2 saniye (Turbo Boost ile).",
      icon = Icons.Default.Bolt,
      accentColor = KittAmber
    ),
    SubsystemInfo(
      name = "Kızılötesi & Radar Sensör Dizisi",
      status = "360° AKTİF",
      healthPercent = 1.0f,
      description = "Ön ızgara arkasındaki tarayıcı göz, röntgen görüşü, ultrasonik dinleme ve termal izleme.",
      icon = Icons.Default.Radar,
      accentColor = KittRed
    ),
    SubsystemInfo(
      name = "Akustik Sentezleyici & Ses Kutusu",
      status = "ÇALIŞIYOR",
      healthPercent = 1.0f,
      description = "İnsan sesini taklit edebilen gelişmiş harmonik ses sentezleyicisi ve 3 kanallı görselleştirici.",
      icon = Icons.Default.Settings,
      accentColor = KittRedGlow
    )
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(KittBlack)
      .padding(horizontal = 14.dp)
  ) {
    Spacer(modifier = Modifier.height(4.dp))
    KittScanner(isScanning = true)
    Spacer(modifier = Modifier.height(10.dp))

    // Header Card
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .background(KittDarkSurface)
        .border(1.dp, Color(0xFF330910), RoundedCornerShape(10.dp))
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column {
        Text(
          text = "KNIGHT 2000 SİSTEM TEŞHİSİ",
          color = KittRed,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )
        Text(
          text = "Sürücü: $driverName | FLAG Protokolü",
          color = KittTextSecondary,
          fontSize = 10.sp,
          fontFamily = FontFamily.Monospace
        )
      }

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(6.dp))
          .background(KittRedDark)
          .border(1.dp, KittRedGlow, RoundedCornerShape(6.dp))
          .clickable {
            diagnosticActive = true
            viewModel.sendMessage("Tüm sistemlerin detaylı teşhisini yap ve durum raporu ver.")
          }
          .padding(horizontal = 10.dp, vertical = 6.dp)
          .testTag("run_diagnostics_btn"),
        contentAlignment = Alignment.Center
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "TEST BAŞLAT",
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Subsystems List
    LazyColumn(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(subsystems.size) { index ->
        val sub = subsystems[index]
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(KittDarkSurface)
            .border(1.dp, sub.accentColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                sub.icon,
                contentDescription = null,
                tint = sub.accentColor,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = sub.name,
                color = KittTextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = sub.accentColor,
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = sub.status,
                color = sub.accentColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          LinearProgressIndicator(
            progress = { sub.healthPercent },
            modifier = Modifier
              .fillMaxWidth()
              .height(4.dp)
              .clip(RoundedCornerShape(2.dp)),
            color = sub.accentColor,
            trackColor = KittSurfaceVariant,
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = sub.description,
            color = KittTextSecondary,
            fontSize = 10.sp,
            lineHeight = 14.sp
          )
        }
      }
    }
  }
}
