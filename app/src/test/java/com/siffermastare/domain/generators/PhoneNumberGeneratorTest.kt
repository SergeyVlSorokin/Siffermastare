package com.siffermastare.domain.generators

import com.siffermastare.domain.evaluation.PhoneNumberDecomposer
import com.siffermastare.domain.evaluation.PhoneNumberEvaluationStrategy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PhoneNumberGeneratorTest {

    private val generator = PhoneNumberGenerator()

    @Test
    fun testGenerateQuestion_returnsValidFormat() {
        val lesson = generator.generateLesson(5)
        lesson.forEach { question ->
            // Target should be raw digits: e.g. "0701234567"
            assertTrue("Target should be 10 digits starting with 07", question.targetValue.matches(Regex("07\\d{8}")))
            
            // Spoken text should be hybrid format: "0 7 0, 1 2 3, 45, 67"
            // Matches: digit space digit space digit comma space digit space digit space digit comma space two_digits comma space two_digits
            // Regex approximation: 
            // Regex approximation: 
            // 0 7 \d, \d \d \d, WORDS, WORDS
            // allowing anything after the second comma group basically, as long as it's comma separated
            val spokenRegex = Regex("0 7 \\d, \\d \\d \\d, .+, .+")
            assertTrue(
                "Spoken text '${question.spokenText}' should match hybrid format '0 7 x, x x x, xx, xx'", 
                question.spokenText.matches(spokenRegex)
            )
        }
    }

    @Test
    fun testLeadingZeroPairs_spokenCorrectly() {
        // Generate enough numbers to hit 00-09 in pairs
        val lesson = generator.generateLesson(500)
        
        var foundLeadingZero = false
        var foundDoubleZero = false
        
        lesson.forEach { question ->
            // Parse target: 07x-xxx AA BB
            // extracting AA and BB (indices 6,7 and 8,9 from "07xxxxxxxxx")
            val target = question.targetValue
            val pair1 = target.substring(6, 8)
            val pair2 = target.substring(8, 10)
            
            // Check Pair 1
            checkPairSpokenText(pair1, question.spokenText, 2)
            // Check Pair 2
            checkPairSpokenText(pair2, question.spokenText, 3)
            
            if (pair1.startsWith("0")) foundLeadingZero = true
            if (pair1 == "00") foundDoubleZero = true
        }
        
        // Ensure we actually tested the edge cases
        assertTrue("Should have generated at least one pair starting with 0", foundLeadingZero)
    }

    private fun checkPairSpokenText(pairDigits: String, fullSpokenText: String, groupIndex: Int) {
        // fullSpokenText format: "0 7 x, 1 2 3, XX, YY"
        // Split by comma
        val parts = fullSpokenText.split(",")
        if (parts.size != 4) return // Should be 4
        
        val spokenPair = parts[groupIndex].trim()
        
        if (pairDigits.startsWith("0")) {
             // Case 05 -> Should speak "noll fem" or "0 5" (explicit zero)
             // Case 00 -> Should speak "noll noll" or "0 0"
             
             // We accept "noll" text or "0". 
             // Currently the bug is it sends "05", which might pass if we check for digit "0".
             // But we want to ensure it is NOT just "5". 
             
             // The most strict check for the fix validation:
             // It must contain "noll" OR valid digit "0" followed by space?
             // Actually, if we send "05" and test checks regex `\d\d` it passes.
             // We want to force it to be "noll X".
             
             assertTrue("Pair '$pairDigits' should be spoken as 'noll ...' but was '$spokenPair'", 
                 spokenPair.lowercase().startsWith("noll"))
        }
    }

    // ===== AC1: Atoms verification =====

    @Test
    fun `atoms are populated with hybrid decomposition for known target`() {
        // Generate lessons and verify atoms for each
        val lesson = generator.generateLesson(20)
        lesson.forEach { question ->
            val expected = PhoneNumberDecomposer.decompose(question.targetValue)
            assertEquals(
                "Atoms for target '${question.targetValue}' should match hybrid decomposition",
                expected,
                question.atoms
            )
        }
    }

    @Test
    fun `atoms for target with standard pairs`() {
        // Generate and find a question with pair >= 10
        val lesson = generator.generateLesson(200)
        val withStandardPair = lesson.find { q ->
            val pair1 = q.targetValue.substring(6, 8).toInt()
            pair1 >= 10
        }
        assertTrue("Should find question with pair >= 10", withStandardPair != null)

        val atoms = withStandardPair!!.atoms
        // Should have atoms (not empty)
        assertTrue("Atoms should not be empty", atoms.isNotEmpty())
        // Verify they match the decompose function
        assertEquals(
            PhoneNumberDecomposer.decompose(withStandardPair.targetValue),
            atoms
        )
    }

    // ===== AC1: Strategy type verification =====

    @Test
    fun `evaluationStrategy is PhoneNumberEvaluationStrategy`() {
        assertTrue(
            "Strategy should be PhoneNumberEvaluationStrategy",
            generator.evaluationStrategy is PhoneNumberEvaluationStrategy
        )
    }
}
