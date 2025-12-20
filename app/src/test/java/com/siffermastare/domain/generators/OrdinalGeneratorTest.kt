package com.siffermastare.domain.generators

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OrdinalGeneratorTest {

    @Test
    fun `generateLesson returns correct ordinal formats`() {
        // We'll test specific numbers to verify the suffix logic
        // 1 -> 1:a
        // 2 -> 2:a
        // 3 -> 3:e
        // 11 -> 11:e
        // 12 -> 12:e
        // 21 -> 21:a
        // 22 -> 22:a
        
        // Since the generator uses random, we might need a way to test specific logic 
        // or we just instantiate it and inspect what it produces if we mock/spy, 
        // but it's simpler to just expose the formatting logic or verify a larger sample.
        // For now, let's just create a generator that produces deterministic output or specific range?
        // The generator interface is random. 
        // However, we can test the `min/max` and ensure format is *valid* (ends in :a or :e).
        
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
        
        // Target value is what user types, so it should be just digits "1", "2" etc.
        // Spoken text is "1:a"
        
        assertTrue(q.targetValue.all { it.isDigit() })
        assertTrue(q.spokenText.contains(":"))
    }
}
