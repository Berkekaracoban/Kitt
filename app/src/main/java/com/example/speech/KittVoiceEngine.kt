package com.example.speech

import android.content.Context
import android.media.AudioAttributes
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.random.Random

/**
 * Voice profile presets that emulate K.I.T.T.'s classic cadence and tonal delivery.
 */
enum class KittVoiceProfile(
  val label: String,
  val defaultPitch: Float,
  val defaultRate: Float,
  val description: String
) {
  CLASSIC_AUTHORITATIVE(
    label = "Knight 2000 (Otoriter Bariton)",
    defaultPitch = 0.86f,
    defaultRate = 0.98f,
    description = "William Daniels'ın efsanevi, sakin, tok ve otoriter K.I.T.T. tonu."
  ),
  TACTICAL_PURSUIT(
    label = "Taktik Takip Modu",
    defaultPitch = 0.90f,
    defaultRate = 1.12f,
    description = "Yüksek hızlı takip durumlarında seri ve net durum analizi."
  ),
  CALM_CRUISE(
    label = "Sakin Seyir",
    defaultPitch = 0.82f,
    defaultRate = 0.94f,
    description = "Daha derin, rahatlatıcı ve saygılı asistan tonu."
  )
}

/**
 * Text-to-Speech integration wrapping Android TextToSpeech engine
 * configured with synthesized voice profiles that emulate K.I.T.T.'s
 * classic dignified, authoritative tone.
 */
class KittVoiceEngine(context: Context) : TextToSpeech.OnInitListener {
  private val appContext = context.applicationContext
  private var tts: TextToSpeech? = null
  private var isInitialized = false

  private val _isSpeaking = MutableStateFlow(false)
  val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

  private val _voiceLevel = MutableStateFlow(0f)
  val voiceLevel: StateFlow<Float> = _voiceLevel.asStateFlow()

  private val _speechMuted = MutableStateFlow(false)
  val speechMuted: StateFlow<Boolean> = _speechMuted.asStateFlow()

  private val _currentProfile = MutableStateFlow(KittVoiceProfile.CLASSIC_AUTHORITATIVE)
  val currentProfile: StateFlow<KittVoiceProfile> = _currentProfile.asStateFlow()

  var pitch: Float = KittVoiceProfile.CLASSIC_AUTHORITATIVE.defaultPitch
  var speechRate: Float = KittVoiceProfile.CLASSIC_AUTHORITATIVE.defaultRate

  private val scope = CoroutineScope(Dispatchers.Default)
  private var animationJob: Job? = null

  init {
    tts = TextToSpeech(appContext, this)
  }

  override fun onInit(status: Int) {
    if (status == TextToSpeech.SUCCESS) {
      applyVoiceConfiguration()
      isInitialized = true
    }
  }

  private fun applyVoiceConfiguration() {
    val targetEngine = tts ?: return

    // 1. Set AudioAttributes for Assistant / Speech stream
    val audioAttributes = AudioAttributes.Builder()
      .setUsage(AudioAttributes.USAGE_ASSISTANT)
      .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
      .build()
    targetEngine.setAudioAttributes(audioAttributes)

    // 2. Select Language & Search for Deep/Male/Authoritative Voice
    val localeTR = Locale("tr", "TR")
    val langResult = targetEngine.setLanguage(localeTR)
    if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
      targetEngine.setLanguage(Locale.getDefault())
    }

    // Attempt to locate a high-quality or male-timbred synthesized voice on device
    try {
      val availableVoices = targetEngine.voices
      if (!availableVoices.isNullOrEmpty()) {
        val selectedVoice = availableVoices.firstOrNull { v ->
          v.locale.language == localeTR.language &&
              (v.name.contains("male", ignoreCase = true) ||
                  v.name.contains("erkek", ignoreCase = true) ||
                  v.features?.contains(TextToSpeech.Engine.KEY_FEATURE_NOT_INSTALLED) == false)
        } ?: availableVoices.firstOrNull { v ->
          v.locale.language == localeTR.language
        }

        if (selectedVoice != null) {
          targetEngine.voice = selectedVoice
        }
      }
    } catch (_: Exception) {
      // Fall back gracefully to default voice
    }

    // 3. Configure Authoritative Baritone Tone Parameters
    targetEngine.setPitch(pitch)
    targetEngine.setSpeechRate(speechRate)

    // 4. Register Utterance Progress Listener for Real-time Voice Box animation
    targetEngine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
      override fun onStart(utteranceId: String?) {
        _isSpeaking.value = true
        startVoiceAnimation()
      }

      override fun onDone(utteranceId: String?) {
        _isSpeaking.value = false
        stopVoiceAnimation()
      }

      @Deprecated("Deprecated in Java")
      override fun onError(utteranceId: String?) {
        _isSpeaking.value = false
        stopVoiceAnimation()
      }
    })
  }

  fun setProfile(profile: KittVoiceProfile) {
    _currentProfile.value = profile
    pitch = profile.defaultPitch
    speechRate = profile.defaultRate
    tts?.setPitch(pitch)
    tts?.setSpeechRate(speechRate)
  }

  /**
   * Pre-processes speech text for natural K.I.T.T. diction,
   * converting abbreviations and inserting rhythmic pauses.
   */
  private fun formatForKittDiction(text: String): String {
    return text
      .replace("K.I.T.T.", "Kit")
      .replace("KITT", "Kit")
      .replace("FLAG", "F-L-A-G")
      .replace("PSI", "P-S-I")
      .replace("KM/H", "kilometre bölü saat")
      .replace("km/s", "kilometre bölü saat")
      .replace("SPM", "S-P-M")
      .replace(Regex("[*#_`\\[\\]]"), "")
  }

  fun speak(text: String) {
    if (_speechMuted.value || !isInitialized) return

    tts?.setPitch(pitch)
    tts?.setSpeechRate(speechRate)

    val cleanText = formatForKittDiction(text)
    val utteranceId = "KITT_UTTERANCE_${System.currentTimeMillis()}"
    tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
  }

  fun stop() {
    tts?.stop()
    _isSpeaking.value = false
    stopVoiceAnimation()
  }

  fun toggleMute(): Boolean {
    _speechMuted.value = !_speechMuted.value
    if (_speechMuted.value) {
      stop()
    }
    return _speechMuted.value
  }

  private fun startVoiceAnimation() {
    animationJob?.cancel()
    animationJob = scope.launch {
      while (isActive && _isSpeaking.value) {
        // Modulate voice levels between 0.35 and 1.0 reflecting speech amplitude
        val base = Random.nextFloat() * 0.65f + 0.35f
        _voiceLevel.value = base
        delay(65)
      }
      _voiceLevel.value = 0f
    }
  }

  private fun stopVoiceAnimation() {
    animationJob?.cancel()
    _voiceLevel.value = 0f
  }

  fun shutdown() {
    tts?.stop()
    tts?.shutdown()
  }
}

