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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.ui.KittViewModel
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

@Composable
fun SettingsScreen(
  viewModel: KittViewModel,
  modifier: Modifier = Modifier
) {
  val driverName by viewModel.driverName.collectAsState()
  val soundEffectsEnabled by viewModel.soundEffectsEnabled.collectAsState()
  val customApiKey by viewModel.customApiKey.collectAsState()

  var nameInput by remember(driverName) { mutableStateOf(driverName) }
  var apiKeyInput by remember(customApiKey) { mutableStateOf(customApiKey) }

  var pitchVal by remember { mutableFloatStateOf(viewModel.voiceEngine.pitch) }
  var rateVal by remember { mutableFloatStateOf(viewModel.voiceEngine.speechRate) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(KittBlack)
      .verticalScroll(rememberScrollState())
      .padding(16.dp)
  ) {
    // 1. Prominent Free Badge & Guarantee
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(
          Brush.linearGradient(
            listOf(Color(0xFF071F12), Color(0xFF0F3822), Color(0xFF071F12))
          )
        )
        .border(1.dp, KittGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
        .padding(14.dp)
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          Icons.Default.Security,
          contentDescription = null,
          tint = KittGreen,
          modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = "TAMAMEN ÜCRETSİZ & AÇIK SİSTEM",
            color = KittGreen,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "K.I.T.T. 2000 asistanı hiçbir abonelik, ücretli paket veya gizli bedel gerektirmez. Tüm sesli ve yapay zeka özellikleri ücretsizdir.",
            color = KittTextPrimary,
            fontSize = 11.sp,
            lineHeight = 15.sp
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 2. Driver Name Setting
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .background(KittDarkSurface)
        .border(1.dp, Color(0x33FF0D34), RoundedCornerShape(10.dp))
        .padding(12.dp)
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Person, contentDescription = null, tint = KittCyan, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "SÜRÜCÜ KİMLİĞİ (HİTAP)",
          color = KittCyan,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )
      }
      Spacer(modifier = Modifier.height(8.dp))
      Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
          value = nameInput,
          onValueChange = { nameInput = it },
          placeholder = { Text("Varsayılan: Michael", color = KittTextSecondary, fontSize = 12.sp) },
          singleLine = true,
          modifier = Modifier
            .weight(1f)
            .height(50.dp)
            .testTag("driver_name_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = KittBlack,
            unfocusedContainerColor = KittBlack,
            focusedBorderColor = KittCyan,
            unfocusedBorderColor = Color(0x3300E5FF),
            focusedTextColor = KittTextPrimary,
            unfocusedTextColor = KittTextPrimary
          )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(KittCyan)
            .clickable {
              viewModel.setDriverName(nameInput)
              viewModel.speakText("Hitap ayarlandı $nameInput.")
            }
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .testTag("save_driver_name_btn"),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.Check, contentDescription = "Kaydet", tint = Color.Black, modifier = Modifier.size(18.dp))
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 3. Voice Tuner (Profiles, Pitch & Rate)
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .background(KittDarkSurface)
        .border(1.dp, Color(0x33FF0D34), RoundedCornerShape(10.dp))
        .padding(12.dp)
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.VolumeUp, contentDescription = null, tint = KittAmber, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "K.I.T.T. SES PROFİLİ VE AKUSTİK AYARLAR",
          color = KittAmber,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )
      }
      Spacer(modifier = Modifier.height(10.dp))

      // Voice Profile Selector Chips
      val activeProfile by viewModel.voiceEngine.currentProfile.collectAsState()
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        com.example.speech.KittVoiceProfile.values().forEach { profile ->
          val isSelected = profile == activeProfile
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(6.dp))
              .background(if (isSelected) KittAmber.copy(alpha = 0.2f) else KittBlack)
              .border(
                width = 1.dp,
                color = if (isSelected) KittAmber else Color(0x22FFAA00),
                shape = RoundedCornerShape(6.dp)
              )
              .clickable {
                viewModel.voiceEngine.setProfile(profile)
                pitchVal = profile.defaultPitch
                rateVal = profile.defaultRate
              }
              .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = profile.label,
                color = if (isSelected) KittAmber else KittTextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              )
              Text(
                text = profile.description,
                color = KittTextSecondary,
                fontSize = 9.sp
              )
            }
            if (isSelected) {
              Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = KittAmber,
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = "Ses Kalınlığı (Pitch): %.2f".format(pitchVal),
        color = KittTextSecondary,
        fontSize = 11.sp,
        fontFamily = FontFamily.Monospace
      )
      Slider(
        value = pitchVal,
        onValueChange = {
          pitchVal = it
          viewModel.voiceEngine.pitch = it
        },
        valueRange = 0.7f..1.3f,
        colors = SliderDefaults.colors(thumbColor = KittAmber, activeTrackColor = KittAmber),
        modifier = Modifier.testTag("pitch_slider")
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "Konuşma Hızı: %.2f".format(rateVal),
        color = KittTextSecondary,
        fontSize = 11.sp,
        fontFamily = FontFamily.Monospace
      )
      Slider(
        value = rateVal,
        onValueChange = {
          rateVal = it
          viewModel.voiceEngine.speechRate = it
        },
        valueRange = 0.8f..1.3f,
        colors = SliderDefaults.colors(thumbColor = KittAmber, activeTrackColor = KittAmber),
        modifier = Modifier.testTag("speed_slider")
      )

      Button(
        onClick = {
          viewModel.speakText("Akustik ses ayarları test ediliyor ${viewModel.driverName.value}. Sesim nasıl geliyor?")
        },
        colors = ButtonDefaults.buttonColors(containerColor = KittSurfaceVariant),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier.fillMaxWidth().testTag("test_voice_btn")
      ) {
        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = KittAmber, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "Sesi Test Et", color = KittAmber, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 4. Sound Effects & Chime
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .background(KittDarkSurface)
        .border(1.dp, Color(0x33FF0D34), RoundedCornerShape(10.dp))
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = "80'ler Synth Ses Efektleri",
          color = KittTextPrimary,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )
        Text(
          text = "Turbo Boost ve tarayıcı efektleri",
          color = KittTextSecondary,
          fontSize = 10.sp
        )
      }
      Switch(
        checked = soundEffectsEnabled,
        onCheckedChange = { viewModel.toggleScannerSound() },
        colors = SwitchDefaults.colors(
          checkedThumbColor = KittRed,
          checkedTrackColor = KittRedDark,
          uncheckedThumbColor = KittTextSecondary,
          uncheckedTrackColor = KittSurfaceVariant
        ),
        modifier = Modifier.testTag("sound_effects_switch")
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 5. Optional Gemini API Key
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .background(KittDarkSurface)
        .border(1.dp, Color(0x33FF0D34), RoundedCornerShape(10.dp))
        .padding(12.dp)
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Lock, contentDescription = null, tint = KittRedGlow, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "İSTEĞE BAĞLI: GEMINI API (ÜCRETSİZ KOTA)",
          color = KittRedGlow,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )
      }
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "K.I.T.T. dahili zekasıyla internetsiz ve tamamen ücretsiz çalışır. İsterseniz Google AI Studio'dan aldığınız ücretsiz Gemini API anahtarını buraya ekleyebilirsiniz:",
        color = KittTextSecondary,
        fontSize = 10.sp,
        lineHeight = 14.sp
      )
      Spacer(modifier = Modifier.height(8.dp))
      Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
          value = apiKeyInput,
          onValueChange = { apiKeyInput = it },
          placeholder = { Text("AIzaSy...", color = KittTextSecondary, fontSize = 11.sp) },
          singleLine = true,
          modifier = Modifier
            .weight(1f)
            .height(50.dp)
            .testTag("gemini_key_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = KittBlack,
            unfocusedContainerColor = KittBlack,
            focusedBorderColor = KittRed,
            unfocusedBorderColor = Color(0x33FF0D34),
            focusedTextColor = KittTextPrimary,
            unfocusedTextColor = KittTextPrimary
          )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(KittRed)
            .clickable {
              viewModel.setCustomApiKey(apiKeyInput)
              viewModel.speakText("API anahtarı kaydedildi ${viewModel.driverName.value}.")
            }
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .testTag("save_api_key_btn"),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.Check, contentDescription = "Kaydet", tint = Color.White, modifier = Modifier.size(18.dp))
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 6. Clear Comms History
    Button(
      onClick = { viewModel.clearCommsHistory() },
      colors = ButtonDefaults.buttonColors(containerColor = KittSurfaceVariant),
      shape = RoundedCornerShape(8.dp),
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, Color(0x33FF0D34), RoundedCornerShape(8.dp))
        .testTag("clear_history_btn")
    ) {
      Icon(Icons.Default.Delete, contentDescription = null, tint = KittRed, modifier = Modifier.size(16.dp))
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = "Tüm Sohbet Geçmişini Sıfırla",
        color = KittRed,
        fontSize = 11.sp,
        fontFamily = FontFamily.Monospace
      )
    }

    Spacer(modifier = Modifier.height(24.dp))
  }
}
