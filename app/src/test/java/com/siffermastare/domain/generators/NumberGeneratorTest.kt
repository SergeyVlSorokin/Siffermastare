package com.siffermastare.domain.generators

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
}
