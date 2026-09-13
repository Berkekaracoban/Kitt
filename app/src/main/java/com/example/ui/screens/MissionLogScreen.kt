package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.ui.theme.KittRed
import com.example.ui.theme.KittSurfaceVariant
import com.example.ui.theme.KittTextPrimary
import com.example.ui.theme.KittTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MissionLogScreen(
  viewModel: KittViewModel,
  modifier: Modifier = Modifier
) {
  val messages by viewModel.messages.collectAsState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(KittBlack)
      .padding(horizontal = 14.dp)
  ) {
    Spacer(modifier = Modifier.height(4.dp))
    KittScanner(isScanning = true)
    Spacer(modifier = Modifier.height(10.dp))

    // Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(KittDarkSurface)
        .border(1.dp, Color(0x33FF0D34), RoundedCornerShape(8.dp))
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Archive, contentDescription = null, tint = KittCyan, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text(
            text = "FLAG GÖREV VE TELSİZ KAYITLARI",
            color = KittCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
          Text(
            text = "${messages.size} Aktif Telsiz Kaydı",
            color = KittTextSecondary,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace
          )
        }
      }

      if (messages.isNotEmpty()) {
        Button(
          onClick = { viewModel.clearCommsHistory() },
          colors = ButtonDefaults.buttonColors(containerColor = KittSurfaceVariant),
          shape = RoundedCornerShape(6.dp),
          modifier = Modifier.testTag("clear_logs_btn")
        ) {
          Icon(Icons.Default.ClearAll, contentDescription = null, tint = KittRed, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = "Temizle", color = KittRed, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    if (messages.isEmpty()) {
      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth(),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "Kayıtlı telsiz iletimi bulunmuyor Michael.",
          color = KittTextSecondary,
          fontSize = 12.sp,
          fontFamily = FontFamily.Monospace
        )
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(messages.reversed(), key = { it.id }) { msg ->
          val timeStr = SimpleDateFormat("dd/MM HH:mm:ss", Locale.getDefault()).format(Date(msg.timestamp))
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(KittDarkSurface)
              .border(1.dp, if (msg.isUser) Color(0x3300E5FF) else Color(0x33FF0D34), RoundedCornerShape(8.dp))
              .padding(10.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = if (msg.isUser) ">> SÜRÜCÜ İLETİMİ" else "<< K.I.T.T. YANITI",
                color = if (msg.isUser) KittCyan else KittRed,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              )
              Text(
                text = timeStr,
                color = KittTextSecondary,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
              )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = msg.text,
              color = KittTextPrimary,
              fontSize = 12.sp,
              lineHeight = 16.sp
            )

            if (!msg.isUser) {
              Spacer(modifier = Modifier.height(2.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
              ) {
                IconButton(
                  onClick = { viewModel.speakText(msg.text) },
                  modifier = Modifier.size(24.dp)
                ) {
                  Icon(
                    Icons.Default.VolumeUp,
                    contentDescription = "Seslendir",
                    tint = KittAmber,
                    modifier = Modifier.size(14.dp)
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
