package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.KittViewModel
import com.example.ui.components.KittDashboardGauges
import com.example.ui.components.KittListeningRadar
import com.example.ui.components.KittMessageItem
import com.example.ui.components.KittScanner
import com.example.ui.components.KittVoiceBox
import com.example.ui.theme.KittAmber
import com.example.ui.theme.KittBlack
import com.example.ui.theme.KittCyan
import com.example.ui.theme.KittDarkSurface
import com.example.ui.theme.KittGreen
import com.example.ui.theme.KittRed
import com.example.ui.theme.KittRedGlow
import com.example.ui.theme.KittSurfaceVariant
import com.example.ui.theme.KittTextPrimary
import com.example.ui.theme.KittTextSecondary
import java.util.Locale

@Composable
fun CockpitScreen(
  viewModel: KittViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val messages by viewModel.messages.collectAsState()
  val driveMode by viewModel.driveMode.collectAsState()
  val speedKmh by viewModel.speedKmh.collectAsState()
  val turboPsi by viewModel.turboPsi.collectAsState()
  val isScanning by viewModel.isScanning.collectAsState()
  val isSpeaking by viewModel.voiceEngine.isSpeaking.collectAsState()
  val voiceLevel by viewModel.voiceEngine.voiceLevel.collectAsState()
  val isMuted by viewModel.voiceEngine.speechMuted.collectAsState()

  var inputText by remember { mutableStateOf("") }
  var isListeningVoice by remember { mutableStateOf(false) }

  val listState = rememberLazyListState()

  // Auto scroll to bottom on new message
  LaunchedEffect(messages.size) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  // Setup SpeechRecognizer
  val speechRecognizer = remember {
    if (SpeechRecognizer.isRecognitionAvailable(context)) {
      SpeechRecognizer.createSpeechRecognizer(context)
    } else null
  }

  DisposableEffect(Unit) {
    onDispose {
      speechRecognizer?.destroy()
    }
  }

  val startListening: () -> Unit = {
    if (speechRecognizer != null) {
      val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale("tr", "TR").toString())
        putExtra(RecognizerIntent.EXTRA_PROMPT, "K.I.T.T. sizi dinliyor Michael...")
      }

      speechRecognizer.setRecognitionListener(object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) { isListeningVoice = true }
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() { isListeningVoice = false }
        override fun onError(error: Int) { isListeningVoice = false }
        override fun onResults(results: Bundle?) {
          isListeningVoice = false
          val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
          val spokenText = matches?.firstOrNull()
          if (!spokenText.isNullOrBlank()) {
            viewModel.sendMessage(spokenText)
          }
        }
        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
      })

      speechRecognizer.startListening(intent)
    }
  }

  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      startListening()
    }
  }

  val onMicClick: () -> Unit = {
    val hasPermission = ContextCompat.checkSelfPermission(
      context,
      Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED

    if (hasPermission) {
      if (isListeningVoice) {
        speechRecognizer?.stopListening()
        isListeningVoice = false
      } else {
        startListening()
      }
    } else {
      permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(KittBlack)
      .padding(horizontal = 12.dp)
  ) {
    // 1. Sweeping Red Scanner Bar
    Spacer(modifier = Modifier.height(4.dp))
    KittScanner(isScanning = isScanning)

    Spacer(modifier = Modifier.height(8.dp))

    // 2. Center Console: Voice Box Equalizer & Telemetry Indicators
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .background(KittDarkSurface)
        .border(1.dp, Color(0xFF330910), RoundedCornerShape(10.dp))
        .padding(horizontal = 10.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Voice status label
      Column {
        Text(
          text = if (isSpeaking) "K.I.T.T. KONUŞUYOR" else "DİNLİYOR / HAZIR",
          color = if (isSpeaking) KittRedGlow else KittTextSecondary,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )
        Text(
          text = "%100 Ücretsiz Sistem",
          color = KittGreen,
          fontSize = 8.sp,
          fontWeight = FontWeight.Medium,
          fontFamily = FontFamily.Monospace
        )
      }

      // Iconic 3-column Voice Box
      KittVoiceBox(
        isSpeaking = isSpeaking,
        voiceLevel = voiceLevel
      )

      // Audio Mute toggle
      IconButton(
        onClick = { viewModel.toggleMute() },
        modifier = Modifier
          .size(36.dp)
          .testTag("mute_toggle_btn")
      ) {
        Icon(
          if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
          contentDescription = "Ses Aç/Kapat",
          tint = if (isMuted) KittTextSecondary else KittAmber,
          modifier = Modifier.size(20.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // 3. Dashboard Gauges (Modes, Speed, Turbo, Scan)
    KittDashboardGauges(
      currentMode = driveMode,
      onModeSelect = { viewModel.setDriveMode(it) },
      speedKmh = speedKmh,
      turboPsi = turboPsi,
      onTurboBoostClick = { viewModel.triggerTurboBoost() },
      onScanClick = { viewModel.triggerRadarScan() }
    )

    Spacer(modifier = Modifier.height(6.dp))

    // 4. Quick Command Chips
    val quickPrompts = listOf(
      "Durum raporu ver",
      "Turbo Boost!",
      "Çevreyi tara",
      "Michael kimdir?",
      "Espri yap",
      "Süper Takip Modu",
      "Ücretsiz mi?"
    )

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      quickPrompts.forEach { prompt ->
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(KittSurfaceVariant)
            .border(1.dp, Color(0x33FF0D34), RoundedCornerShape(14.dp))
            .clickable { viewModel.sendMessage(prompt) }
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .testTag("chip_${prompt.replace(" ", "_").lowercase()}"),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = prompt,
            color = KittTextPrimary,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // 5. Conversation Stream
    LazyColumn(
      state = listState,
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(KittBlack)
        .testTag("chat_messages_list")
    ) {
      items(messages, key = { it.id }) { message ->
        KittMessageItem(
          message = message,
          onSpeakAgain = { viewModel.speakText(it) }
        )
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // 6. Bottom Input Row: Mic & Text
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Pulsing Circular Waveform Listening Radar
      KittListeningRadar(
        isListening = isListeningVoice,
        onClick = onMicClick,
        size = 46.dp
      )

      Spacer(modifier = Modifier.width(4.dp))

      // Input TextField
      OutlinedTextField(
        value = inputText,
        onValueChange = { inputText = it },
        placeholder = {
          Text(
            text = if (isListeningVoice) "Dinleniyor..." else "K.I.T.T.'e bir şey sor...",
            color = KittTextSecondary,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace
          )
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = KittDarkSurface,
          unfocusedContainerColor = KittDarkSurface,
          focusedBorderColor = KittRed,
          unfocusedBorderColor = Color(0x33FF0D34),
          focusedTextColor = KittTextPrimary,
          unfocusedTextColor = KittTextPrimary,
          cursorColor = KittRed
        ),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
          .weight(1f)
          .height(50.dp)
          .testTag("chat_input_field")
      )

      Spacer(modifier = Modifier.width(8.dp))

      // Send Button
      Box(
        modifier = Modifier
          .size(48.dp)
          .clip(CircleShape)
          .background(if (inputText.isNotBlank()) KittRed else KittSurfaceVariant)
          .border(1.dp, if (inputText.isNotBlank()) KittRedGlow else Color.Transparent, CircleShape)
          .clickable(enabled = inputText.isNotBlank()) {
            val textToSend = inputText
            inputText = ""
            viewModel.sendMessage(textToSend)
          }
          .testTag("send_message_btn"),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Send,
          contentDescription = "Gönder",
          tint = if (inputText.isNotBlank()) Color.White else KittTextSecondary,
          modifier = Modifier.size(20.dp)
        )
      }
    }
  }
}
