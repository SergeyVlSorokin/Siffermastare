package com.siffermastare.domain.generators

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
            // 0 7 \d, \d \d \d, \d\d, \d\d
            val spokenRegex = Regex("0 7 \\d, \\d \\d \\d, \\d\\d, \\d\\d")
            assertTrue(
                "Spoken text '${question.spokenText}' should match hybrid format '0 7 x, x x x, xx, xx'", 
                question.spokenText.matches(spokenRegex)
            )
        }
    }
}
