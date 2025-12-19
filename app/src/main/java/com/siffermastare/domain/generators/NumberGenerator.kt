package com.siffermastare.domain.generators

import com.siffermastare.domain.models.Question

/**
 * Interface for generating numbers/questions for a lesson.
 */
interface NumberGenerator {
    /**
     * Generates a list of questions for a single lesson session.
     * @param count The number of questions to generate (default 10).
     */
    fun generateLesson(count: Int = 10): List<Question>
}
