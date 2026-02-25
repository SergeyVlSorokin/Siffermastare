package com.siffermastare.domain.generators

import com.siffermastare.domain.models.Question
import com.siffermastare.domain.validation.strategies.StandardNumberEvaluationStrategy

/**
 * Generates questions from a curated list of Swedish tricky number pairs.
 * Populates Question.atoms using standard number decomposition.
 */
class TrickyPairsGenerator : NumberGenerator {

    override val evaluationStrategy = StandardNumberEvaluationStrategy()
    private val trickyNumbers = listOf(
        "7", "20", "70",
        "6", "60",
        "13", "30",
        "14", "40",
        "15", "50",
        "16", "17", "18", "80", "19", "90"
    )

    override fun generateLesson(count: Int): List<Question> {
        return List(count) {
             val numberStr = trickyNumbers.random()
             val number = numberStr.toInt()
             val atoms = StandardNumberEvaluationStrategy.decompose(number)
             Question(
                 targetValue = numberStr,
                 spokenText = numberStr,
                 visualHint = null,
                 atoms = atoms
             )
        }
    }
}
