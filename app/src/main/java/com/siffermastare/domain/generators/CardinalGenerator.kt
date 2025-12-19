package com.siffermastare.domain.generators

import com.siffermastare.domain.models.Question
import kotlin.random.Random

/**
 * Generates cardinal numbers within a specified range.
 * Relies on Android's TTS engine to read digits correctly as numbers.
 *
 * @property min The minimum value (inclusive).
 * @property max The maximum value (inclusive).
 */
class CardinalGenerator(
    private val min: Int,
    private val max: Int
) : NumberGenerator {

    override fun generateLesson(count: Int): List<Question> {
        return List(count) {
            val number = Random.nextInt(min, max + 1)
            val numberString = number.toString()
            
            Question(
                targetValue = numberString,
                spokenText = numberString, // Using digits for TTS per user request
                visualHint = null
            )
        }
    }
}
