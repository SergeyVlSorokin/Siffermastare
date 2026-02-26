package com.siffermastare.domain.generators

import com.siffermastare.domain.evaluation.FractionsEvaluationStrategy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FractionsGeneratorTest {

    private val generator = FractionsGenerator()

    @Test
    fun `generateLesson returns correct number of questions`() {
        val questions = generator.generateLesson(5)
        assertEquals(5, questions.size)
    }

    @Test
    fun `evaluationStrategy is FractionsEvaluationStrategy`() {
        assertTrue(generator.evaluationStrategy is FractionsEvaluationStrategy)
    }

    @Test
    fun `generated questions have correct atoms - cardinal numerator and ordinal denominator`() {
        val questions = generator.generateLesson(20)
        questions.forEach { q ->
            val parts = q.targetValue.split("/")
            val expectedAtoms = listOf(parts[0], "ord:${parts[1]}")
            assertEquals("Atoms for ${q.targetValue}", expectedAtoms, q.atoms)
        }
    }

    @Test
    fun `generateLesson produces valid fraction format`() {
        val questions = generator.generateLesson(10)
        questions.forEach { question ->
            // Target value should be "numerator/denominator"
            assertTrue("Target value should contain /", question.targetValue.contains("/"))
            val parts = question.targetValue.split("/")
            assertEquals(2, parts.size)
            assertTrue("Numerator should be integer", parts[0].all { it.isDigit() })
            assertTrue("Denominator should be integer", parts[1].all { it.isDigit() })
        }
    }

    @Test
    fun `formatSpokenText produces correct text for various fractions`() {
        assertEquals("en halv", generator.formatSpokenText(1, 2))
        assertEquals("en tredjedel", generator.formatSpokenText(1, 3))
        assertEquals("två tredjedelar", generator.formatSpokenText(2, 3))
        assertEquals("en fjärdedel", generator.formatSpokenText(1, 4))
        assertEquals("tre fjärdedelar", generator.formatSpokenText(3, 4))
        assertEquals("en tiondel", generator.formatSpokenText(1, 10))
        assertEquals("nio tiondelar", generator.formatSpokenText(9, 10))
    }
}
