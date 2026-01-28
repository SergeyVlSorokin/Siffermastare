package com.siffermastare.domain.generators

import com.siffermastare.domain.models.Question
import com.siffermastare.domain.evaluation.EvaluationStrategy

/**
 * Interface for generating numbers/questions for a lesson.
 */
interface NumberGenerator {
    val evaluationStrategy: EvaluationStrategy
    
    /**
     * Generates a list of questions for a single lesson session.
     * @param count The number of questions to generate (default 10).
     */
    fun generateLesson(count: Int = 10): List<Question>
}
