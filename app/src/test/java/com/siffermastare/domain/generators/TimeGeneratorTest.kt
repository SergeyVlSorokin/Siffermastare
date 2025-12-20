package com.siffermastare.domain.generators

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TimeGeneratorTest {

    @Test
    fun `generateLesson produces correct format`() {
        val generator = TimeGenerator()
        val questions = generator.generateLesson(count = 50)
        
        questions.forEach { q ->
            val target = q.targetValue
            val spoken = q.spokenText
            
            // Target should be 4 digits "0900", "1430"
            assertTrue("Target $target should be 4 digits", target.length == 4 && target.all { it.isDigit() })
            
            // Spoken should be "HH:MM" e.g., "09:00", "14:30"
            assertTrue("Spoken $spoken should format as HH:MM", spoken.matches(Regex("\\d{2}:\\d{2}")))
            
            val hour = target.substring(0, 2).toInt()
            val minute = target.substring(2, 4).toInt()
            
            assertTrue("Hour $hour must be 0-23", hour in 0..23)
            assertTrue("Minute $minute must be 0-59", minute in 0..59)
            
            assertEquals("$spoken should match $target with colon", "${target.substring(0, 2)}:${target.substring(2, 4)}", spoken)
        }
    }
}
