package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.KittBrain
import com.example.audio.KittSynthesizer
import com.example.data.ChatMessageEntity
import com.example.data.KittDatabase
import com.example.speech.KittVoiceEngine
import com.example.ui.components.KittDriveMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

class KittViewModel(application: Application) : AndroidViewModel(application) {
  private val database = KittDatabase.getDatabase(application)
  private val dao = database.kittDao()

  val voiceEngine = KittVoiceEngine(application)

  val messages: StateFlow<List<ChatMessageEntity>> = dao.getAllMessages()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val _driveMode = MutableStateFlow(KittDriveMode.NORMAL_CRUISE)
  val driveMode: StateFlow<KittDriveMode> = _driveMode.asStateFlow()

  private val _speedKmh = MutableStateFlow(88)
  val speedKmh: StateFlow<Int> = _speedKmh.asStateFlow()

  private val _turboPsi = MutableStateFlow(14.7f)
  val turboPsi: StateFlow<Float> = _turboPsi.asStateFlow()

  private val _isScanning = MutableStateFlow(true)
  val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

  private val _driverName = MutableStateFlow("Michael")
  val driverName: StateFlow<String> = _driverName.asStateFlow()

  private val _isListening = MutableStateFlow(false)
  val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

  private val _customApiKey = MutableStateFlow("")
  val customApiKey: StateFlow<String> = _customApiKey.asStateFlow()

  private val _soundEffectsEnabled = MutableStateFlow(true)
  val soundEffectsEnabled: StateFlow<Boolean> = _soundEffectsEnabled.asStateFlow()

  private var speedSimulatorJob: Job? = null

  init {
    startSpeedSimulation()
    viewModelScope.launch {
      delay(800)
      // Check if messages empty, add introductory salute
      if (messages.value.isEmpty()) {
        val welcome = "Merhaba Michael. Knight Industries Two Thousand tüm sistemleriyle emrinde. " +
            "Tamamen ücretsiz ve açık asistanın olarak seni dinliyorum. " +
            "Bana 'Durum raporu', 'Turbo Boost!', 'Çevreyi tara' diyebilir veya dilediğin soruyu sorabilirsin."
        val id = dao.insertMessage(ChatMessageEntity(text = welcome, isUser = false))
        if (id > 0) {
          KittSynthesizer.playCommsChirp()
          voiceEngine.speak(welcome)
        }
      }
    }
  }

  fun setDriveMode(mode: KittDriveMode) {
    _driveMode.value = mode
    if (_soundEffectsEnabled.value) {
      KittSynthesizer.playCommsChirp()
    }
    when (mode) {
      KittDriveMode.NORMAL_CRUISE -> _speedKmh.value = 90
      KittDriveMode.AUTO_CRUISE -> _speedKmh.value = 110
      KittDriveMode.PURSUIT -> _speedKmh.value = 240
      KittDriveMode.SUPER_PURSUIT -> _speedKmh.value = 450
    }
  }

  fun triggerTurboBoost() {
    viewModelScope.launch {
      if (_soundEffectsEnabled.value) {
        KittSynthesizer.playTurboBoost()
      }
      _turboPsi.value = 45.0f
      _speedKmh.value = 280
      _driveMode.value = KittDriveMode.PURSUIT

      val reply = "Turbo Boost devrede ${_driverName.value}! Hidrolik iticiler ateşlendi, engel aşıldı!"
      dao.insertMessage(ChatMessageEntity(text = "TURBO BOOST!", isUser = true))
      dao.insertMessage(ChatMessageEntity(text = reply, isUser = false, actionTag = "TURBO_BOOST"))
      voiceEngine.speak(reply)

      delay(3000)
      _turboPsi.value = 14.7f
    }
  }

  fun triggerRadarScan() {
    viewModelScope.launch {
      if (_soundEffectsEnabled.value) {
        KittSynthesizer.playScannerSweep(true)
      }
      val reply = "Sensör ve radar taraması tamamlandı ${_driverName.value}. 360 derecelik alanda hiçbir tehlike tespit edilmedi."
      dao.insertMessage(ChatMessageEntity(text = "Çevreyi tara.", isUser = true))
      dao.insertMessage(ChatMessageEntity(text = reply, isUser = false, actionTag = "SCAN"))
      voiceEngine.speak(reply)
    }
  }

  fun sendMessage(query: String) {
    val clean = query.trim()
    if (clean.isEmpty()) return

    viewModelScope.launch {
      if (_soundEffectsEnabled.value) {
        KittSynthesizer.playCommsChirp()
      }
      dao.insertMessage(ChatMessageEntity(text = clean, isUser = true))

      val response = KittBrain.processQuery(
        query = clean,
        driverName = _driverName.value,
        customApiKey = _customApiKey.value
      )

      if (response.triggerAction == "TURBO_BOOST") {
        if (_soundEffectsEnabled.value) KittSynthesizer.playTurboBoost()
        _turboPsi.value = 45.0f
        delay(2500)
        _turboPsi.value = 14.7f
      } else if (response.triggerAction == "SCAN") {
        if (_soundEffectsEnabled.value) KittSynthesizer.playScannerSweep(true)
      }

      dao.insertMessage(
        ChatMessageEntity(
          text = response.replyText,
          isUser = false,
          actionTag = response.triggerAction
        )
      )

      voiceEngine.speak(response.replyText)
    }
  }

  fun speakText(text: String) {
    voiceEngine.speak(text)
  }

  fun stopSpeech() {
    voiceEngine.stop()
  }

  fun toggleMute(): Boolean {
    return voiceEngine.toggleMute()
  }

  fun toggleScannerSound() {
    _soundEffectsEnabled.value = !_soundEffectsEnabled.value
  }

  fun setDriverName(name: String) {
    if (name.isNotBlank()) {
      _driverName.value = name.trim()
    }
  }

  fun setCustomApiKey(key: String) {
    _customApiKey.value = key.trim()
  }

  fun clearCommsHistory() {
    viewModelScope.launch {
      dao.clearAllMessages()
    }
  }

  private fun startSpeedSimulation() {
    speedSimulatorJob = viewModelScope.launch {
      while (true) {
        delay(2000)
        val jitter = Random.nextInt(-2, 3)
        val target = when (_driveMode.value) {
          KittDriveMode.NORMAL_CRUISE -> 90
          KittDriveMode.AUTO_CRUISE -> 110
          KittDriveMode.PURSUIT -> 240
          KittDriveMode.SUPER_PURSUIT -> 450
        }
        _speedKmh.value = (target + jitter).coerceAtLeast(0)
      }
    }
  }

  override fun onCleared() {
    super.onCleared()
    speedSimulatorJob?.cancel()
    voiceEngine.shutdown()
  }
}
