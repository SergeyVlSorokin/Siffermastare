package com.siffermastare.domain.validation

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AnswerValidatorTest {

    @Test
    fun isTimeLesson_returnsTrueForTimeDigital() {
        assertTrue(AnswerValidator.isTimeLesson("time_digital"))
    }

    @Test
    fun isTimeLesson_returnsFalseForOthers() {
        assertFalse(AnswerValidator.isTimeLesson("cardinal_0_20"))
    }

    @Test
    fun validateTime_normalizesMissingLeadingZero() {
        // "930" -> "09:30" == "09:30"
        assertTrue(AnswerValidator.validateTime("930", "09:30"))
    }

    @Test
    fun validateTime_normalizesMissingColon() {
        // "0930" -> "09:30"
        assertTrue(AnswerValidator.validateTime("0930", "09:30"))
    }

    @Test
    fun validateTime_validatesExactMatch() {
        assertTrue(AnswerValidator.validateTime("10:00", "10:00"))
    }

    @Test
    fun validateTime_handlesShortInput() {
        // "1" != "10:00"
        assertFalse(AnswerValidator.validateTime("1", "10:00"))
    }
    
    @Test
    fun validateTime_handlesHandlesRawTarget() {
        // Generator produces "0930" (no colon)
        // User inputs "0930" or "930" or "09:30"
        
        // This is what is currently failing in main app
        assertTrue(AnswerValidator.validateTime("0930", "0930"))
        assertTrue(AnswerValidator.validateTime("09:30", "0930"))
    }

    @Test
    fun validateTime_handlesMultipleTargets() {
        // "1430|0230" should accept "1430" OR "0230"
        val target = "1430|0230"
        assertTrue(AnswerValidator.validateTime("1430", target))
        assertTrue(AnswerValidator.validateTime("0230", target))
        assertTrue(AnswerValidator.validateTime("14:30", target))
        assertTrue(AnswerValidator.validateTime("2:30", target))
        
        // Should rely on "0230" -> "02:30" == "02:30"
        
        assertFalse(AnswerValidator.validateTime("1230", target))
    }
}
