package com.siffermastare.domain.generators

import com.siffermastare.domain.validation.strategies.OrdinalNumberEvaluationStrategy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OrdinalGeneratorTest {

    @Test
    fun `generateLesson returns correct ordinal formats`() {
        val generator = OrdinalGenerator(1, 25)
        val questions = generator.generateLesson(count = 50) 
        
        questions.forEach { q ->
            val num = q.targetValue.toInt()
            val spoken = q.spokenText
            
            // Basic format check
            assertTrue("Spoken text $spoken should start with $num", spoken.startsWith("$num"))
            assertTrue("Spoken text $spoken should end with :a or :e", spoken.endsWith(":a") || spoken.endsWith(":e"))
            
            // Specific rule check
            if (num % 10 == 1 && num % 100 != 11) {
                assertEquals("1s should be :a (except 11)", "$num:a", spoken)
            } else if (num % 10 == 2 && num % 100 != 12) {
                assertEquals("2s should be :a (except 12)", "$num:a", spoken)
            } else {
                 assertEquals("Others should be :e", "$num:e", spoken)
            }
        }
    }
    
    @Test
    fun `generateLesson targetValue is digit string`() {
        val generator = OrdinalGenerator(1, 5)
        val questions = generator.generateLesson(count = 1)
        val q = questions.first()
        
        assertTrue(q.targetValue.all { it.isDigit() })
        assertTrue(q.spokenText.contains(":"))
    }

    @Test
    fun `OrdinalGenerator populates ordinal atoms for 25`() {
        val generator = OrdinalGenerator(25, 25)
        val question = generator.generateLesson(1).first()
        assertEquals(listOf("ord:20", "ord:5"), question.atoms)
    }

    @Test
    fun `OrdinalGenerator populates ordinal atoms for 13`() {
        val generator = OrdinalGenerator(13, 13)
        val question = generator.generateLesson(1).first()
        assertEquals(listOf("ord:13"), question.atoms)
    }

    @Test
    fun `OrdinalGenerator populates ordinal atoms for 7`() {
        val generator = OrdinalGenerator(7, 7)
        val question = generator.generateLesson(1).first()
        assertEquals(listOf("ord:7"), question.atoms)
    }

    @Test
    fun `OrdinalGenerator uses OrdinalNumberEvaluationStrategy`() {
        val generator = OrdinalGenerator(1, 10)
        assertTrue(generator.evaluationStrategy is OrdinalNumberEvaluationStrategy)
    }
}
