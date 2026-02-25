package com.siffermastare.domain.generators

import com.siffermastare.domain.models.Question
import com.siffermastare.domain.validation.strategies.StandardNumberEvaluationStrategy
import kotlin.random.Random

/**
 * Generates cardinal numbers within a specified range.
 * Populates Question.atoms using standard number decomposition.
 *
 * @property min The minimum value (inclusive).
 * @property max The maximum value (inclusive).
 */
class CardinalGenerator(
    private val min: Int,
    private val max: Int
) : NumberGenerator {

    override val evaluationStrategy = StandardNumberEvaluationStrategy()

    override fun generateLesson(count: Int): List<Question> {
        return List(count) {
            val number = Random.nextInt(min, max + 1)
            val numberString = number.toString()
            val atoms = StandardNumberEvaluationStrategy.decompose(number)
            
            Question(
                targetValue = numberString,
                spokenText = numberString,
                visualHint = null,
                atoms = atoms
            )
        }
    }
}
