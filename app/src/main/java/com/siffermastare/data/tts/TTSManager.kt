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

    init {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale("sv", "SE"))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTSManager", "Swedish language not supported or missing data")
                    // In a real app we might want to callback to UI here,
                    // but for this MVP spike we just log it.
                    // The speak function will handle the case where language is not active.
                } else {
                    isInitialized = true
                    // Check for pending text
                    pendingText?.let {
                        speak(it)
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
     */
    fun speak(text: String) {
        if (isInitialized) {
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            Log.w("TTSManager", "TTS not initialized, queuing text: $text")
            pendingText = text
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
