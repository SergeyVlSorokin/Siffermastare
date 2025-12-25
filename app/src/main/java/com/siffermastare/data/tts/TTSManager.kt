package com.siffermastare.data.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

/**
 * Manages Text-to-Speech (TTS) operations.
 *
 * Handles initialization with Swedish locale and provides a safe interface for speaking text.
 * Wraps the native Android [TextToSpeech] API.
 *
 * @param context The application context.
 */
class TTSManager(context: Context) {

    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false
    private var pendingText: String? = null
    private var pendingRate: Float = 1.0f

    init {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale("sv", "SE"))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTSManager", "Swedish language not supported or missing data")
                } else {
                    isInitialized = true
                    // Check for pending text
                    pendingText?.let {
                        speak(it, pendingRate)
                        pendingText = null
                    }
                }
            } else {
                Log.e("TTSManager", "TTS Initialization failed")
            }
        }
    }

    /**
     * Speaks the given text using the initialized TTS engine.
     *
     * @param text The text to speak.
     * @param rate The speech rate (1.0 is normal, lower is slower). Default 1.0f.
     */
    fun speak(text: String, rate: Float = 1.0f) {
        if (isInitialized) {
            Log.d("TTSManager", "Speaking: '$text' at rate $rate")
            textToSpeech?.setSpeechRate(rate)
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            Log.w("TTSManager", "TTS not initialized, queuing text: $text")
            pendingText = text
            pendingRate = rate
        }
    }

    /**
     * Shuts down the TTS engine and releases resources.
     * Should be called when the manager is no longer needed.
     */
    fun shutdown() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        isInitialized = false
    }
}
