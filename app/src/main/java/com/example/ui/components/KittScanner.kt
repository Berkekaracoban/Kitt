package com.example.ui.components

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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.KittBlack
import com.example.ui.theme.KittRed
import com.example.ui.theme.KittRedDark
import com.example.ui.theme.KittRedGlow
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.pow

/**
 * Custom Jetpack Compose component that renders K.I.T.T.'s iconic
 * horizontal sweeping red light scanner bar with authentic incandescent
 * halogen decay trail, optical lens diffusion, and recessed bumper housing.
 */
@Composable
fun KittScanner(
  modifier: Modifier = Modifier,
  isScanning: Boolean = true,
  segmentCount: Int = 12,
  speedMs: Int = 1050,
  barHeight: Dp = 32.dp,
  primaryColor: Color = KittRed,
  glowColor: Color = KittRedGlow,
  showHousing: Boolean = true,
  onScannerClick: (() -> Unit)? = null
) {
  // Infinite ping-pong oscillation: 0.0 -> 1.0 -> 0.0
  val infiniteTransition = rememberInfiniteTransition(label = "KittScannerOscillator")
  val scanPhase by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = speedMs, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "ScannerPhase"
  )

  // Track sweep direction to model authentic incandescent filament cooldown/afterglow
  var previousPhase by remember { mutableFloatStateOf(0f) }
  val movingRight = scanPhase >= previousPhase
  previousPhase = scanPhase

  val activePos = if (isScanning) scanPhase else 0.5f

  val containerModifier = modifier
    .fillMaxWidth()
    .height(barHeight)
    .then(
      if (showHousing) {
        Modifier
          .padding(horizontal = 14.dp, vertical = 2.dp)
          .shadow(
            elevation = 12.dp,
            shape = RoundedCornerShape(10.dp),
            ambientColor = glowColor.copy(alpha = 0.35f),
            spotColor = primaryColor
          )
          .clip(RoundedCornerShape(10.dp))
          .background(KittBlack)
          .border(
            width = 1.5.dp,
            brush = Brush.horizontalGradient(
              listOf(
                Color(0xFF1E0306),
                Color(0xFF6B0F1C),
                Color(0xFF1E0306)
              )
            ),
            shape = RoundedCornerShape(10.dp)
          )
      } else {
        Modifier
      }
    )
    .then(
      if (onScannerClick != null) {
        Modifier.clickable { onScannerClick() }
      } else Modifier
    )
    .testTag("kitt_scanner_bar")

  Box(modifier = containerModifier) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      drawScannerGraphics(
        segmentCount = segmentCount,
        activePosition = activePos,
        isMovingRight = movingRight,
        isScanning = isScanning,
        primaryColor = primaryColor,
        glowColor = glowColor,
        showHousing = showHousing
      )
    }
  }
}

/**
 * Draws the optical segments, halogen phosphor afterglow trails,
 * diffuse light bloom, and lens micro-ridges.
 */
private fun DrawScope.drawScannerGraphics(
  segmentCount: Int,
  activePosition: Float,
  isMovingRight: Boolean,
  isScanning: Boolean,
  primaryColor: Color,
  glowColor: Color,
  showHousing: Boolean
) {
  val width = size.width
  val height = size.height

  // 1. Draw subtle horizontal optical lens grid / scanline texture in housing
  if (showHousing) {
    val lensLineCount = 4
    val lineSpacing = height / (lensLineCount + 1)
    for (l in 1..lensLineCount) {
      drawLine(
        color = Color(0x223A0008),
        start = Offset(0f, l * lineSpacing),
        end = Offset(width, l * lineSpacing),
        strokeWidth = 1f
      )
    }
  }

  // 2. Compute segment geometry
  val sidePadding = 10.dp.toPx()
  val segmentSpacing = 3.5.dp.toPx()
  val usableWidth = width - (sidePadding * 2) - ((segmentCount - 1) * segmentSpacing)
  val segmentWidth = usableWidth / segmentCount
  val segmentHeight = height - (10.dp.toPx())
  val cornerRadius = CornerRadius(3.5.dp.toPx(), 3.5.dp.toPx())

  // 3. Wide diffuse halogen glow backdrop behind active beam
  if (isScanning) {
    val glowCenterX = sidePadding + activePosition * (width - sidePadding * 2)
    drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(
          glowColor.copy(alpha = 0.45f),
          primaryColor.copy(alpha = 0.20f),
          Color.Transparent
        ),
        center = Offset(glowCenterX, height / 2f),
        radius = segmentWidth * 3.5f
      ),
      radius = segmentWidth * 3.5f,
      center = Offset(glowCenterX, height / 2f)
    )
  }

  // 4. Draw individual halogen segments with directional afterglow trail
  for (i in 0 until segmentCount) {
    val segNormalized = i.toFloat() / (segmentCount - 1)
    val distance = segNormalized - activePosition
    val absDist = abs(distance)

    // Directional incandescent decay: segments behind the sweep direction stay lit longer!
    val isTrailing = if (isMovingRight) distance < 0 else distance > 0
    val effectiveDecay = if (isTrailing && isScanning) {
      absDist * 0.70f // slower decay for the trail behind beam
    } else {
      absDist * 1.15f // faster falloff ahead of the beam
    }

    val intensity = when {
      !isScanning -> 0.08f
      effectiveDecay < 0.08f -> 1.0f
      effectiveDecay < 0.16f -> 0.85f
      effectiveDecay < 0.28f -> 0.55f
      effectiveDecay < 0.42f -> 0.28f
      effectiveDecay < 0.60f -> 0.12f
      else -> 0.04f
    }

    val segX = sidePadding + i * (segmentWidth + segmentSpacing)
    val segY = (height - segmentHeight) / 2f

    // Dynamic color gradient based on halogen thermal excitation
    val baseSegColor = when {
      intensity > 0.82f -> glowColor
      intensity > 0.50f -> primaryColor
      intensity > 0.25f -> KittRedDark
      intensity > 0.10f -> Color(0xFF4A000C)
      else -> Color(0xFF1E0206)
    }

    // Outer segment lamp housing
    drawRoundRect(
      color = baseSegColor,
      topLeft = Offset(segX, segY),
      size = Size(segmentWidth, segmentHeight),
      cornerRadius = cornerRadius
    )

    // Dark inset border around each lamp segment for authentic lens look
    drawRoundRect(
      color = Color(0x33000000),
      topLeft = Offset(segX, segY),
      size = Size(segmentWidth, segmentHeight),
      cornerRadius = cornerRadius,
      style = Stroke(width = 1.dp.toPx())
    )

    // Inner bright hot laser filament for peak intensity segments
    if (intensity > 0.70f) {
      val coreWidth = segmentWidth * 0.55f
      val coreHeight = segmentHeight * 0.45f
      val coreX = segX + (segmentWidth - coreWidth) / 2f
      val coreY = segY + (segmentHeight - coreHeight) / 2f

      // Incandescent core hot-spot (white/pink highlight)
      drawRoundRect(
        color = Color(0xFFFFD5DC),
        topLeft = Offset(coreX, coreY),
        size = Size(coreWidth, coreHeight),
        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
      )

      // Horizontal slit flare
      drawLine(
        color = Color.White.copy(alpha = 0.9f),
        start = Offset(coreX, segY + segmentHeight / 2f),
        end = Offset(coreX + coreWidth, segY + segmentHeight / 2f),
        strokeWidth = 1.5.dp.toPx()
      )
    }
  }

  // 5. Exterior glass lens reflection (glossy highlight)
  if (showHousing) {
    drawRoundRect(
      brush = Brush.verticalGradient(
        listOf(
          Color.White.copy(alpha = 0.18f),
          Color.Transparent
        ),
        startY = 0f,
        endY = height * 0.45f
      ),
      topLeft = Offset(0f, 0f),
      size = Size(width, height * 0.45f),
      cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
    )
  }
}

