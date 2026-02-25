package com.siffermastare.domain.generators

import com.siffermastare.domain.models.Question
import com.siffermastare.domain.validation.strategies.StandardNumberEvaluationStrategy
import kotlin.random.Random

/**
 * Generates ordinal numbers within a specified range.
 * Produces spoken text in Swedish ordinal format (e.g. 1:a, 2:a, 3:e).
 * Populates Question.atoms with ordinal-prefixed atoms (e.g., "ord:20", "ord:5").
 *
 * @property min The minimum value (inclusive).
 * @property max The maximum value (inclusive).
 */
class OrdinalGenerator(
    private val min: Int,
    private val max: Int
) : NumberGenerator {

    override val evaluationStrategy = StandardNumberEvaluationStrategy()

    override fun generateLesson(count: Int): List<Question> {
        return List(count) {
            val number = Random.nextInt(min, max + 1)
            val suffix = getOrdinalSuffix(number)
            val atoms = StandardNumberEvaluationStrategy.decompose(number, "ord:")
            
            Question(
                targetValue = number.toString(),
                spokenText = "$number$suffix",
                visualHint = null,
                atoms = atoms
            )
        }
    }

    private fun getOrdinalSuffix(number: Int): String {
        val lastDigit = number % 10
        val lastTwoDigits = number % 100

        return when {
            lastTwoDigits == 11 || lastTwoDigits == 12 -> ":e"
            lastDigit == 1 || lastDigit == 2 -> ":a"
            else -> ":e"
        }
    }
}
