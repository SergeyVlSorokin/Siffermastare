package com.siffermastare.domain.generators

import com.siffermastare.domain.validation.strategies.StandardNumberEvaluationStrategy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TrickyPairsGeneratorTest {

    private val generator = TrickyPairsGenerator()
    
    // The set of tricky numbers defined in Story 6.2
    private val expectedTrickyNumbers = setOf(
        "7", "20", "70",
        "6", "60",
        "13", "30",
        "14", "40",
        "15", "50",
        "16", 
        "17", 
        "18", "80",
        "19", "90"
    )

    @Test
    fun testGenerateQuestion_returnsTrickyNumber() {
        val lesson = generator.generateLesson(100)
        
        lesson.forEach { question ->
            assertTrue(
                "Generated number ${question.targetValue} should be in tricky list",
                expectedTrickyNumbers.contains(question.targetValue)
            )
        }
    }
    
    @Test
    fun testGenerateQuestion_spokenTextMatchesTarget() {
         val lesson = generator.generateLesson(10)
         lesson.forEach { question ->
             assertTrue(question.targetValue == question.spokenText)
         }
    }

    @Test
    fun `TrickyPairsGenerator populates atoms for single-atom number`() {
        // All tricky numbers are 0-99, so they decompose to at most 2 atoms
        val lesson = generator.generateLesson(100)
        lesson.forEach { question ->
            assertTrue(
                "Question ${question.targetValue} should have non-empty atoms",
                question.atoms.isNotEmpty()
            )
        }
    }

    @Test
    fun `TrickyPairsGenerator atoms for 30 is single atom`() {
        // Verify via decompose that 30 -> ["30"] (single atom for tens)
        val lesson = generator.generateLesson(200)
        val q30 = lesson.firstOrNull { it.targetValue == "30" }
        if (q30 != null) {
            assertEquals(listOf("30"), q30.atoms)
        }
        // Also verify decompose directly as a regression check
        assertEquals(listOf("30"), StandardNumberEvaluationStrategy.decompose(30))
    }

    @Test
    fun `TrickyPairsGenerator uses StandardNumberEvaluationStrategy`() {
        assertTrue(generator.evaluationStrategy is StandardNumberEvaluationStrategy)
    }
}
