package com.siffermastare.domain.generators

import com.siffermastare.domain.models.Question
import com.siffermastare.domain.evaluation.ExactMatchEvaluationStrategy

class TrickyPairsGenerator : NumberGenerator {

    override val evaluationStrategy = ExactMatchEvaluationStrategy()
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
             Question(
                 targetValue = numberStr,
                 spokenText = numberStr,
                 visualHint = null
             )
        }
    }
}
