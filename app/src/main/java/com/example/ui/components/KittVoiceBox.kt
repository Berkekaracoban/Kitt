package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.KittBlack
import com.example.ui.theme.KittRed
import com.example.ui.theme.KittRedDark
import com.example.ui.theme.KittRedGlow
import kotlin.math.abs

@Composable
fun KittVoiceBox(
  modifier: Modifier = Modifier,
  isSpeaking: Boolean = false,
  voiceLevel: Float = 0f
) {
  // Animate the audio level smoothly
  val animatedLevel by animateFloatAsState(
    targetValue = if (isSpeaking) voiceLevel.coerceIn(0.2f, 1.0f) else 0.08f,
    animationSpec = spring(stiffness = 600f),
    label = "VoiceBoxLevel"
  )

  Box(
    modifier = modifier
      .width(130.dp)
      .height(68.dp)
      .clip(RoundedCornerShape(8.dp))
      .background(KittBlack)
      .border(1.5.dp, Color(0xFF380008), RoundedCornerShape(8.dp))
      .padding(horizontal = 10.dp, vertical = 6.dp)
      .testTag("kitt_voice_box"),
    contentAlignment = Alignment.Center
  ) {
    Row(
      modifier = Modifier.fillMaxHeight(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Left Column
      VoiceBarColumn(
        modifier = Modifier.weight(1f),
        level = (animatedLevel * 0.82f).coerceIn(0.05f, 1f)
      )
      Spacer(modifier = Modifier.width(6.dp))
      // Center Column (tallest)
      VoiceBarColumn(
        modifier = Modifier.weight(1f),
        level = animatedLevel.coerceIn(0.08f, 1f)
      )
      Spacer(modifier = Modifier.width(6.dp))
      // Right Column
      VoiceBarColumn(
        modifier = Modifier.weight(1f),
        level = (animatedLevel * 0.82f).coerceIn(0.05f, 1f)
      )
    }
  }
}

@Composable
private fun VoiceBarColumn(
  modifier: Modifier = Modifier,
  level: Float
) {
  val barCount = 10
  Canvas(modifier = modifier.fillMaxHeight()) {
    val barHeight = (size.height - (barCount - 1) * 2.5.dp.toPx()) / barCount
    val barWidth = size.width
    val centerIndex = barCount / 2f

    val activeBars = (level * barCount).toInt().coerceAtLeast(1)

    for (i in 0 until barCount) {
      val y = i * (barHeight + 2.5.dp.toPx())
      val distFromCenter = abs(i + 0.5f - centerIndex)
      val maxDist = centerIndex

      // Activate outward from center
      val isLit = (1f - (distFromCenter / maxDist)) <= level || (distFromCenter <= (activeBars / 2f))

      val color = if (isLit) {
        if (distFromCenter < 1.5f) KittRedGlow else KittRed
      } else {
        Color(0xFF220005)
      }

      drawRoundRect(
        color = color,
        topLeft = Offset(0f, y),
        size = Size(barWidth, barHeight),
        cornerRadius = CornerRadius(1.5.dp.toPx(), 1.5.dp.toPx())
      )
    }
  }
}
