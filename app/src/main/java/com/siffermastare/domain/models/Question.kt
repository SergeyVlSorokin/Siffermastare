package com.siffermastare.domain.models

/**
 * Represents a single question in a lesson.
 *
 * @property targetValue The correct answer (e.g., "1415").
 * @property spokenText The text to be spoken by the TTS engine.
 * @property visualHint Optional hint text to display.
 */
data class Question(
    val targetValue: String,
    val spokenText: String,
    val visualHint: String? = null
)
