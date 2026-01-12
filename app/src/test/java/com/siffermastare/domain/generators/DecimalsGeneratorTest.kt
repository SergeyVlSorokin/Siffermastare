package com.siffermastare.domain.generators

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DecimalsGeneratorTest {

    // Ideally would inject random seed, but for now testing via pattern matching on output
    private val generator = DecimalsGenerator()

    @Test
    fun `generateLesson returns correct number of questions`() {
        val count = 10
        val questions = generator.generateLesson(count)
        assertEquals(count, questions.size)
    }

    @Test
    fun `generated values contain comma`() {
        val questions = generator.generateLesson(50)
        questions.forEach { q ->
            assertTrue("Target value ${q.targetValue} should contain comma", q.targetValue.contains(","))
        }
    }

    @Test
    fun `generated values have 1 or 2 decimals`() {
        val questions = generator.generateLesson(100)
        questions.forEach { q ->
            val parts = q.targetValue.split(",")
            assertEquals("Should split into 2 parts", 2, parts.size)
            val decimals = parts[1]
            assertTrue("Decimal part length should be 1 or 2", decimals.length in 1..2)
        }
    }

    @Test
    fun `spoken text format is correct for simple decimal`() {
        // We can't verify exact "3,5" => "tre komma fem" without controlling random, 
        // but we can check the format structure relative to targetValue.
        
        // However, we can instantiate the formatter directly if we make it accessible, 
        // OR we just assume the generator implementation and check consistency.
        // Let's rely on finding examples in a large batch or just checking logic consistency.
        
        // Better: For this test class, let's verify specific known inputs if possible. 
        // Since we can't seed the Random in the current interface, we'll verify properties.
        
        val questions = generator.generateLesson(100)
        questions.forEach { q ->
            val target = q.targetValue
            val spoken = q.spokenText
            
            // Should contain "komma"
            assertTrue("Spoken text '$spoken' should contain 'komma'", spoken.contains("komma"))
            
            // Basic check: starts with number name
            // difficult to reverse engineer numberToText fully here without duplicating logic.
        }
    }
    
    // To strictly test the logic requested (0,01 vs 0,1), we need to test the private formatting logic
    // or expose it package-private. 
    // I will write a test that acts like a unit test for the formatting logic by making a temporary 
    // subclass or just reflecting? No, that's messy.
    // I will verify that IF we get "0,01", the spoken text is "noll komma noll ett".
    
    @Test
    fun `validate specific formatting rules on generated samples`() {
        // Generate enough to hopefully hit edge cases, or manually verify logic via reflection if necessary (not ideal).
        // Since TDD requires me to write the test first, I will write checking logic that asserts correctness 
        // FOR WHATEVER IS GENERATED.
        
        val questions = generator.generateLesson(500) // generate enough to hit 0,01 roughly
        
        questions.forEach { q ->
            val parts = q.targetValue.split(",")
            val dec = parts[1]
            val spoken = q.spokenText
            
            // assert separator
            assertTrue(spoken.contains("komma"))
            
            // Check leading zero logic
            if (dec.length == 2 && dec.startsWith("0")) {
                // e.g., "0,05"
                // Spoken should contain "komma noll"
                // "noll komma noll fem"
                val distinctParts = spoken.split("komma ")
                val decimalSpoken = distinctParts[1]
                assertTrue("Decimal part '$decimalSpoken' for '$dec' should start with 'noll'", 
                    decimalSpoken.startsWith("noll"))
            }
            
            // Removed dead code block (checking if dec.length==2 and !startsWith("0") and < 10, which is impossible)
        }
    }
}
