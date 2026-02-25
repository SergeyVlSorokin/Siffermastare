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

    @Test
    fun `TimeGenerator populates atoms for generated questions`() {
        val generator = TimeGenerator()
        val questions = generator.generateLesson(count = 50)
        questions.forEach { q ->
            assertTrue(
                "Question ${q.targetValue} should have non-empty atoms",
                q.atoms.isNotEmpty()
            )
        }
    }

    @Test
    fun `decomposeTime for 0515 returns 0 5 15`() {
        val atoms = TimeGenerator.decomposeTime("05", "15")
        assertEquals(listOf("0", "5", "15"), atoms)
    }

    @Test
    fun `decomposeTime for 1430 returns 14 30`() {
        val atoms = TimeGenerator.decomposeTime("14", "30")
        assertEquals(listOf("14", "30"), atoms)
    }

    @Test
    fun `decomposeTime for 0030 returns 0 0 30`() {
        val atoms = TimeGenerator.decomposeTime("00", "30")
        assertEquals(listOf("0", "0", "30"), atoms)
    }

    @Test
    fun `decomposeTime for 0003 returns 0 0 0 3`() {
        val atoms = TimeGenerator.decomposeTime("00", "03")
        assertEquals(listOf("0", "0", "0", "3"), atoms)
    }

    @Test
    fun `decomposeTime for 1019 returns 10 19`() {
        val atoms = TimeGenerator.decomposeTime("10", "19")
        assertEquals(listOf("10", "19"), atoms)
    }

    @Test
    fun `decomposeTime for 2359 returns 20 3 50 9`() {
        val atoms = TimeGenerator.decomposeTime("23", "59")
        assertEquals(listOf("20", "3", "50", "9"), atoms)
    }
}
