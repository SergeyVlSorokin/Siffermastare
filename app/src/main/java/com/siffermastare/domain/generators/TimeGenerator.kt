package com.siffermastare.domain.generators

import com.siffermastare.domain.models.Question
import com.siffermastare.domain.evaluation.DigitalTimeEvaluationStrategy
import kotlin.random.Random

/**
 * Generates random time strings in digital format (HH:MM).
 * Target value (input) is 4 digits (HHMM).
 */
class TimeGenerator : NumberGenerator {

    override val evaluationStrategy = com.siffermastare.domain.evaluation.DigitalTimeEvaluationStrategy()

    override fun generateLesson(count: Int): List<Question> {
        return List(count) {
            val hour = Random.nextInt(0, 24)
            val minute = Random.nextInt(0, 60)
            
            // Format with leading zeros
            val hourStr = hour.toString().padStart(2, '0')
            val minuteStr = minute.toString().padStart(2, '0')
            
            val spokenText = "$hourStr:$minuteStr"
            val targetValue = "$hourStr$minuteStr"
            
            Question(
                targetValue = targetValue,
                spokenText = spokenText,
                visualHint = null
            )
        }
    }
}
