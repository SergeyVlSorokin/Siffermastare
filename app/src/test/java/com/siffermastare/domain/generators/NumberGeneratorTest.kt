package com.siffermastare.domain.generators

import com.siffermastare.domain.validation.strategies.StandardNumberEvaluationStrategy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NumberGeneratorTest {

    @Test
    fun `generateLesson returns correct number of questions`() {
        val generator = CardinalGenerator(0, 10)
        val questions = generator.generateLesson(count = 5)
        assertEquals(5, questions.size)
    }
    
    @Test
    fun `generateLesson respects min and max bounds`() {
        val min = 10
        val max = 20
        val generator = CardinalGenerator(min, max)
        val questions = generator.generateLesson(count = 100)
        
        questions.forEach { question ->
            val value = question.targetValue.toInt()
            assertTrue("Value $value should be >= $min", value >= min)
            assertTrue("Value $value should be <= $max", value <= max)
        }
    }

    @Test
    fun `generateLesson sets spokenText equal to digits`() {
        val generator = CardinalGenerator(5, 5)
        val questions = generator.generateLesson(count = 1)
        val question = questions.first()
        
        assertEquals("5", question.targetValue)
        assertEquals("5", question.spokenText)
    }

    @Test
    fun `CardinalGenerator populates atoms for 25`() {
        val generator = CardinalGenerator(25, 25)
        val question = generator.generateLesson(1).first()
        assertEquals(listOf("20", "5"), question.atoms)
    }

    @Test
    fun `CardinalGenerator populates atoms for 0`() {
        val generator = CardinalGenerator(0, 0)
        val question = generator.generateLesson(1).first()
        assertEquals(listOf("0"), question.atoms)
    }

    @Test
    fun `CardinalGenerator populates atoms for 505`() {
        val generator = CardinalGenerator(505, 505)
        val question = generator.generateLesson(1).first()
        assertEquals(listOf("5", "5"), question.atoms)
    }

    @Test
    fun `CardinalGenerator uses StandardNumberEvaluationStrategy`() {
        val generator = CardinalGenerator(0, 10)
        assertTrue(generator.evaluationStrategy is StandardNumberEvaluationStrategy)
    }

    @Test
    fun `CardinalGenerator populates atoms for all generated questions`() {
        val generator = CardinalGenerator(0, 1000)
        val questions = generator.generateLesson(count = 50)
        questions.forEach { question ->
            assertTrue("Question ${question.targetValue} should have non-empty atoms", question.atoms.isNotEmpty())
        }
    }
}
