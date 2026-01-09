package com.siffermastare.domain.generators

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
        // Generate a larger sample to ensure coverage isn't just luck
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
}
