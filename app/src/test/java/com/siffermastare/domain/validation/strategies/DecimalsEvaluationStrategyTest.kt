package com.siffermastare.domain.validation.strategies

import com.siffermastare.domain.models.Question
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DecimalsEvaluationStrategyTest {

    private val strategy = DecimalsEvaluationStrategy()

    @Test
    fun `Exact Match`() {
        val question = Question(
            targetValue = "2,5",
            spokenText = "två komma fem",
            atoms = listOf("2", "5")
        )
        val result = strategy.evaluate("2,5", question)
        assertTrue(result.isCorrect)
        assertEquals(listOf(true), result.atomUpdates["2"])
        assertEquals(listOf(true), result.atomUpdates["5"])
    }

    @Test
    fun `Partial Match - Wrong decimal part`() {
        val question = Question(
            targetValue = "2,5",
            spokenText = "två komma fem",
            atoms = listOf("2", "5")
        )
        val result = strategy.evaluate("2,4", question)
        assertFalse(result.isCorrect)
        assertEquals(listOf(true), result.atomUpdates["2"])
        assertEquals(listOf(false), result.atomUpdates["5"])
    }

    @Test
    fun `Partial Match - Wrong length in decimal part`() {
        val question = Question(
            targetValue = "2,25",
            spokenText = "två komma tjugofem",
            atoms = listOf("2", "20", "5")
        )
        val result = strategy.evaluate("2,5", question)
        assertFalse(result.isCorrect)
        assertEquals(listOf(true), result.atomUpdates["2"])
        assertEquals(listOf(true), result.atomUpdates["5"])
        assertEquals(listOf(false), result.atomUpdates["20"])
    }

    @Test
    fun `Wrong Length - Missing trailing decimals`() {
        val question = Question(
            targetValue = "3,14",
            spokenText = "tre komma fjorton",
            atoms = listOf("3", "14")
        )
        val result = strategy.evaluate("3,1", question)
        assertFalse(result.isCorrect)
        assertEquals(listOf(true), result.atomUpdates["3"])
        assertEquals(listOf(false), result.atomUpdates["14"])
    }

    @Test
    fun `Multiple Commas - Invalid format`() {
        val question = Question(
            targetValue = "2,5",
            spokenText = "två komma fem",
            atoms = listOf("2", "5")
        )
        val result = strategy.evaluate("2,,5", question)
        assertFalse(result.isCorrect)
        assertEquals(listOf(true), result.atomUpdates["2"])
        assertEquals(listOf(false), result.atomUpdates["5"])
    }

    @Test
    fun `Missing Comma - Integer fallback`() {
        val question = Question(
            targetValue = "2,5",
            spokenText = "två komma fem",
            atoms = listOf("2", "5")
        )
        val result = strategy.evaluate("25", question)
        assertFalse(result.isCorrect)
        // Without comma, the input is just "25". This doesn't match the integer part "2" or decimal part "5".
        assertEquals(listOf(false), result.atomUpdates["2"])
        assertEquals(listOf(false), result.atomUpdates["5"])
    }
    
    @Test
    fun `Partial Match - Integer part wrong`() {
        val question = Question(
            targetValue = "2,5",
            spokenText = "två komma fem",
            atoms = listOf("2", "5")
        )
        val result = strategy.evaluate("3,5", question)
        assertFalse(result.isCorrect)
        assertEquals(listOf(false), result.atomUpdates["2"])
        assertEquals(listOf(true), result.atomUpdates["5"])
    }
}
