package com.siffermastare.domain.evaluation

import com.siffermastare.domain.models.Question
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExactMatchEvaluationStrategyTest {

    private val strategy = ExactMatchEvaluationStrategy()

    @Test
    fun `evaluate returns true when input matches target exactly`() {
        val question = Question(
            targetValue = "42",
            spokenText = "fortytwo",
            visualHint = null
        )
        val result = strategy.evaluate("42", question)
        assertTrue(result.isCorrect)
    }

    @Test
    fun `evaluate returns false when input does not match target`() {
        val question = Question(
            targetValue = "42",
            spokenText = "fortytwo",
            visualHint = null
        )
        val result = strategy.evaluate("43", question)
        assertFalse(result.isCorrect)
    }

    @Test
    fun `evaluate returns false for matching validation with extra spaces (strict match)`() {
        val question = Question(
            targetValue = "42",
            spokenText = "fortytwo",
            visualHint = null
        )
        // ExactMatch implies strict equality, so " 42" should fail unless stripped before.
        // Based on implementation valid == question.targetValue
        val result = strategy.evaluate(" 42", question)
        assertFalse(result.isCorrect)
    }
}
