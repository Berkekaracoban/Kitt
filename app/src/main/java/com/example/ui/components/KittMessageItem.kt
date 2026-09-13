package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChatMessageEntity
import com.example.ui.theme.KittAmber
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
fun KittMessageItem(
  message: ChatMessageEntity,
  onSpeakAgain: (String) -> Unit
) {
  val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp))

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp, horizontal = 8.dp),
    horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
  ) {
    if (!message.isUser) {
      // KITT Avatar
      Box(
        modifier = Modifier
          .size(32.dp)
          .clip(CircleShape)
          .background(Color.Black)
          .border(1.5.dp, KittRed, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "K",
          color = KittRed,
          fontWeight = FontWeight.Black,
          fontFamily = FontFamily.Monospace,
          fontSize = 14.sp
        )
      }
      Spacer(modifier = Modifier.width(8.dp))
    }

    Column(
      modifier = Modifier
        .weight(1f, fill = false)
        .clip(
          RoundedCornerShape(
            topStart = 12.dp,
            topEnd = 12.dp,
            bottomStart = if (message.isUser) 12.dp else 2.dp,
            bottomEnd = if (message.isUser) 2.dp else 12.dp
          )
        )
        .background(if (message.isUser) KittSurfaceVariant else KittDarkSurface)
        .border(
          width = 1.dp,
          color = if (message.isUser) Color(0x3300E5FF) else Color(0x44FF0D34),
          shape = RoundedCornerShape(
            topStart = 12.dp,
            topEnd = 12.dp,
            bottomStart = if (message.isUser) 12.dp else 2.dp,
            bottomEnd = if (message.isUser) 2.dp else 12.dp
          )
        )
        .padding(10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (message.isUser) "SÜRÜCÜ" else "K.I.T.T. 2000",
          color = if (message.isUser) KittCyan else KittRed,
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
        text = message.text,
        color = KittTextPrimary,
        fontSize = 13.sp,
        lineHeight = 18.sp
      )

      if (!message.isUser) {
        Spacer(modifier = Modifier.height(2.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = { onSpeakAgain(message.text) },
            modifier = Modifier
              .size(26.dp)
              .testTag("speak_again_btn")
          ) {
            Icon(
              Icons.Default.VolumeUp,
              contentDescription = "Tekrar Dinle",
              tint = KittAmber,
              modifier = Modifier.size(14.dp)
            )
          }
        }
      }
    }
  }
}
