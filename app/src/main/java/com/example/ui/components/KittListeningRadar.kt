package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.KittBlack
import com.example.ui.theme.KittCyan
import com.example.ui.theme.KittRed
import com.example.ui.theme.KittRedDark
import com.example.ui.theme.KittRedGlow
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * A circular pulsing waveform animation component indicating when K.I.T.T.
 * is actively listening to the driver's voice input.
 *
 * Features:
 * - Expanding sonar ripples with alpha fade
 * - Orbiting circular waveform perimeter (radial audio wave spikes)
 * - Glowing center core with microphone icon and haptic touch feedback
 */
@Composable
fun KittListeningRadar(
  isListening: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  size: Dp = 68.dp,
  primaryColor: Color = KittRed,
  glowColor: Color = KittRedGlow
) {
  val infiniteTransition = rememberInfiniteTransition(label = "KittListeningTransitions")

  // Pulse 1: Inner to outer ripple expansion (0.0 to 1.0)
  val pulseProgress1 by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1400, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "PulseProgress1"
  )

  // Pulse 2: Staggered secondary outer ripple
  val pulseProgress2 by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1400, delayMillis = 450, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "PulseProgress2"
  )

  // Waveform rotation angle (0 to 360 deg)
  val waveRotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = (2 * PI).toFloat(),
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 3500, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "WaveRotation"
  )

  // Breaths the core glow size
  val corePulse by infiniteTransition.animateFloat(
    initialValue = 0.92f,
    targetValue = 1.08f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "CorePulse"
  )

  Box(
    modifier = modifier
      .size(size * 1.55f)
      .testTag("kitt_listening_radar"),
    contentAlignment = Alignment.Center
  ) {
    // Canvas for pulsing ripples and circular waveform
    if (isListening) {
      Canvas(modifier = Modifier.matchParentSize()) {
        val center = Offset(this.size.width / 2f, this.size.height / 2f)
        val baseRadius = (size.toPx() / 2f)
        val maxRippleRadius = this.size.width / 2f

        // Ripple 1
        val r1 = baseRadius + (maxRippleRadius - baseRadius) * pulseProgress1
        val alpha1 = (1f - pulseProgress1).coerceIn(0f, 1f) * 0.7f
        drawCircle(
          color = glowColor.copy(alpha = alpha1),
          radius = r1,
          center = center,
          style = Stroke(width = 2.dp.toPx())
        )

        // Ripple 2 (staggered)
        val r2 = baseRadius + (maxRippleRadius - baseRadius) * pulseProgress2
        val alpha2 = (1f - pulseProgress2).coerceIn(0f, 1f) * 0.6f
        drawCircle(
          color = primaryColor.copy(alpha = alpha2),
          radius = r2,
          center = center,
          style = Stroke(width = 1.5.dp.toPx())
        )

        // Circular waveform teeth / spikes along the perimeter
        val spikeCount = 36
        for (i in 0 until spikeCount) {
          val angle = (i.toFloat() / spikeCount) * (2 * PI).toFloat() + waveRotation
          // Modulate spike height with harmonic sine waves to look like audio frequencies
          val harmonic = sin(angle * 4f) * cos(angle * 2f + waveRotation)
          val spikeLength = (6.dp.toPx() + (harmonic * 8.dp.toPx()).coerceAtLeast(0f))

          val startX = center.x + cos(angle) * (baseRadius + 2.dp.toPx())
          val startY = center.y + sin(angle) * (baseRadius + 2.dp.toPx())
          val endX = center.x + cos(angle) * (baseRadius + 2.dp.toPx() + spikeLength)
          val endY = center.y + sin(angle) * (baseRadius + 2.dp.toPx() + spikeLength)

          drawLine(
            brush = Brush.linearGradient(
              listOf(glowColor, Color(0xFFFF9EAA)),
              start = Offset(startX, startY),
              end = Offset(endX, endY)
            ),
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = 2.dp.toPx()
          )
        }
      }
    }

    // Interactive Center Button
    val centerSize = if (isListening) size * corePulse else size

    Box(
      modifier = Modifier
        .size(centerSize)
        .shadow(
          elevation = if (isListening) 16.dp else 4.dp,
          shape = CircleShape,
          ambientColor = if (isListening) glowColor else Color.Transparent,
          spotColor = if (isListening) primaryColor else Color.Transparent
        )
        .clip(CircleShape)
        .background(
          brush = Brush.radialGradient(
            colors = if (isListening) {
              listOf(
                glowColor,
                primaryColor,
                KittRedDark,
                KittBlack
              )
            } else {
              listOf(
                Color(0xFF2E0910),
                Color(0xFF190407),
                KittBlack
              )
            }
          )
        )
        .border(
          width = if (isListening) 2.dp else 1.5.dp,
          color = if (isListening) Color(0xFFFFD1D8) else primaryColor,
          shape = CircleShape
        )
        .clickable(
          interactionSource = remember { MutableInteractionSource() },
          indication = ripple(bounded = true, color = Color.White)
        ) { onClick() }
        .testTag("listening_center_button"),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = if (isListening) Icons.Default.Mic else Icons.Default.MicOff,
        contentDescription = if (isListening) "K.I.T.T. Dinliyor - Durdurmak için dokunun" else "Mikrofonu Aç",
        tint = if (isListening) Color.White else primaryColor,
        modifier = Modifier.size(size * 0.44f)
      )
    }
  }
}
