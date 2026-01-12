package com.siffermastare.domain.generators

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
    fun `spoken text for 1 over 2 is correct`() {
        // We can't deterministicly force 1/2, so we might need to test specific logic 
        // OR we can generate enough to hopefully find one, 
        // OR (better) we test a helper function if we make it public, or assume implementation
        // For now, let's rely on checking specific known mappings if they appear, or specific logic testing.
        // Actually, best practice is to test the internal formatting logic if it's complex.
        // But since this is a black-box test of the generator, let's check that IF we get 1/2, it says "en halv"
        
        // Let's create a specific testable method or just check behaviors on a large sample
        // Or better: Let's refactor the generator to allow testing specific numbers or expose the formatter.
        // But following TDD, I should write the test against the interface.
        
        // Use a loop to find specific cases or mock the random (if possible). 
        // Since I can't mock random easily without dependency injection, I will check 
        // that generated values satisfy the rules.
        
        val questions = generator.generateLesson(100)
        
        questions.forEach { q ->
            val parts = q.targetValue.split("/")
            val num = parts[0].toInt()
            val den = parts[1].toInt()
            
            val expectedSuffix = if (num > 1) "ar" else "" // Plural rule often ends in ar (tredjedelar)
            
            // Check specific denominators
            when (den) {
                2 -> {
                    if (num == 1) assertEquals("en halv", q.spokenText)
                    // unique case for plural halves? "tre halva"? Story doesn't specify >1 halves, likely >1 numerator means mixed numbers? 
                    // No, 3/2 is three halves. "tre halvor". 
                    // Story says: "Focus on denominators 2-10". 
                    // Usually we practice proper fractions (num < den)? 
                    // Story doesn't strictly say proper fractions only. 
                    // Examples: 1/2, 1/3, 2/3. Implicitly proper fractions.
                }
                3 -> {
                    val root = "tredjedel"
                    val spoken = if (num == 1) "en $root" else "${numberName(num)} ${root}ar"
                    assertEquals(spoken, q.spokenText)
                }
                4 -> {
                    val root = "fjärdedel"
                    val spoken = if (num == 1) "en $root" else "${numberName(num)} ${root}ar"
                    assertEquals(spoken, q.spokenText)
                }
                5 -> {
                    val root = "femtedel"
                    val spoken = if (num == 1) "en $root" else "${numberName(num)} ${root}ar"
                    assertEquals(spoken, q.spokenText)
                }
            }
        }
    }
    
    // Helper to match simple number names for the test
    private fun numberName(n: Int): String {
        return when(n) {
            1 -> "en" // special for fractions? usually 'ett' but 'en' for counting nouns?
            // "en tredjedel" (one third). "två tredjedelar".
            // Story example: 1/3 -> "en tredjedel". 
            // So 1 is "en".
            2 -> "två"
            3 -> "tre"
            4 -> "fyra"
            5 -> "fem"
            6 -> "sex"
            7 -> "sju"
            8 -> "åtta"
            9 -> "nio"
            10 -> "tio"
            else -> n.toString()
        }
    }
}
