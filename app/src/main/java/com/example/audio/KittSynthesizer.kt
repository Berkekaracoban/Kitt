package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

object KittSynthesizer {
  private val scope = CoroutineScope(Dispatchers.Default)

  fun playScannerSweep(enabled: Boolean = true) {
    if (!enabled) return
    scope.launch {
      val sampleRate = 22050
      val durationMs = 280
      val numSamples = (sampleRate * durationMs) / 1000
      val buffer = ShortArray(numSamples)

      for (i in 0 until numSamples) {
        val progress = i.toDouble() / numSamples
        // Modulated frequency between 400Hz and 750Hz
        val currentFreq = 400.0 + 350.0 * sin(progress * PI)
        val envelope = sin(progress * PI) // fade in and out
        val angle = 2.0 * PI * i / (sampleRate / currentFreq)
        buffer[i] = (sin(angle) * envelope * Short.MAX_VALUE * 0.45).toInt().toShort()
      }

      playPcmBuffer(buffer, sampleRate)
    }
  }

  fun playTurboBoost() {
    scope.launch {
      val sampleRate = 22050
      val durationMs = 700
      val numSamples = (sampleRate * durationMs) / 1000
      val buffer = ShortArray(numSamples)

      for (i in 0 until numSamples) {
        val progress = i.toDouble() / numSamples
        val currentFreq = 120.0 + 550.0 * progress
        val envelope = if (progress < 0.2) progress / 0.2 else (1.0 - progress) / 0.8
        val angle = 2.0 * PI * i / (sampleRate / currentFreq)
        // Add subharmonic rumble
        val subAngle = 2.0 * PI * i / (sampleRate / (currentFreq * 0.5))
        val sample = (sin(angle) * 0.7 + sin(subAngle) * 0.3) * envelope * Short.MAX_VALUE * 0.7
        buffer[i] = sample.toInt().toShort()
      }

      playPcmBuffer(buffer, sampleRate)
    }
  }

  fun playCommsChirp() {
    scope.launch {
      val sampleRate = 22050
      val durationMs = 180
      val numSamples = (sampleRate * durationMs) / 1000
      val buffer = ShortArray(numSamples)

      for (i in 0 until numSamples) {
        val progress = i.toDouble() / numSamples
        val freq = if (progress < 0.45) 1200.0 else if (progress < 0.55) 0.0 else 1800.0
        val envelope = if (freq == 0.0) 0.0 else sin((progress % 0.5) / 0.5 * PI)
        val angle = if (freq == 0.0) 0.0 else 2.0 * PI * i / (sampleRate / freq)
        buffer[i] = (sin(angle) * envelope * Short.MAX_VALUE * 0.5).toInt().toShort()
      }

      playPcmBuffer(buffer, sampleRate)
    }
  }

  fun playScannerBlip() {
    scope.launch {
      val sampleRate = 22050
      val durationMs = 90
      val numSamples = (sampleRate * durationMs) / 1000
      val buffer = ShortArray(numSamples)

      for (i in 0 until numSamples) {
        val progress = i.toDouble() / numSamples
        val freq = 900.0 - 300.0 * progress
        val envelope = 1.0 - progress
        val angle = 2.0 * PI * i / (sampleRate / freq)
        buffer[i] = (sin(angle) * envelope * Short.MAX_VALUE * 0.4).toInt().toShort()
      }

      playPcmBuffer(buffer, sampleRate)
    }
  }

  private fun playPcmBuffer(buffer: ShortArray, sampleRate: Int) {
    try {
      val track = AudioTrack.Builder()
        .setAudioAttributes(
          AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        )
        .setAudioFormat(
          AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setSampleRate(sampleRate)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()
        )
        .setBufferSizeInBytes(buffer.size * 2)
        .setTransferMode(AudioTrack.MODE_STATIC)
        .build()

      track.write(buffer, 0, buffer.size)
      track.play()
      track.setNotificationMarkerPosition(buffer.size)
      track.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
        override fun onPeriodicNotification(t: AudioTrack?) {}
        override fun onMarkerReached(t: AudioTrack?) {
          track.release()
        }
      })
    } catch (_: Exception) {
      // Audio playback fallback
    }
  }
}
