package com.siffermastare.domain.generators

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import com.siffermastare.domain.validation.strategies.StandardNumberEvaluationStrategy

class DecimalsGeneratorTest {

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
    fun `question owns atoms representing the integer and decimal parts`() {
        val questions = generator.generateLesson(500)
        
        // Find a few specific examples from the generated list to assert against
        val qZero = questions.find { it.targetValue == "0,0" || it.targetValue == "0,00" }
        if (qZero != null) {
            val decimals = qZero.targetValue.split(",")[1]
            if (decimals == "0") {
                assertEquals(listOf("0", "0"), qZero.atoms)
            } else {
                assertEquals(listOf("0", "0", "0"), qZero.atoms)
            }
        }
        
        val qLeadingZero = questions.find { it.targetValue.endsWith(",05") }
        if (qLeadingZero != null) {
            val intPart = qLeadingZero.targetValue.split(",")[0].toInt()
            val expectedIntAtoms = StandardNumberEvaluationStrategy.decompose(intPart)
            val expectedAtoms = expectedIntAtoms + listOf("0", "5")
            assertEquals(expectedAtoms, qLeadingZero.atoms)
        }

        val qNoDecimal = questions.find { it.targetValue.endsWith(",0") && it.targetValue != "0,0" }
        if (qNoDecimal != null) {
            val intPart = qNoDecimal.targetValue.split(",")[0].toInt()
            val expectedIntAtoms = StandardNumberEvaluationStrategy.decompose(intPart)
            val expectedAtoms = expectedIntAtoms + listOf("0")
            assertEquals(expectedAtoms, qNoDecimal.atoms)
        }
    }

    @Test
    fun `validate specific formatting rules on generated samples`() {
        val questions = generator.generateLesson(500) 
        
        questions.forEach { q ->
            val parts = q.targetValue.split(",")
            val dec = parts[1]
            val spoken = q.spokenText
            
            // assert separator
            assertTrue("Spoken text should contain 'komma'", spoken.contains("komma"))
            
            val distinctParts = spoken.split("komma ")
            assertEquals("Spoken text should have exactly one 'komma'", 2, distinctParts.size)
            val decimalSpoken = distinctParts[1]
            
            // Check leading zero logic
            if (dec.length == 2 && dec.startsWith("0")) {
                assertTrue("Decimal part '$decimalSpoken' for '$dec' should start with 'noll'", 
                    decimalSpoken.startsWith("noll"))
                
                if (dec == "00") {
                    assertEquals("Decimal '00' should be spoken as 'noll noll'", "noll noll", decimalSpoken)
                }
            } else if (dec == "0") {
                assertEquals("Decimal '0' should be spoken as 'noll'", "noll", decimalSpoken)
            }
        }
    }
}
