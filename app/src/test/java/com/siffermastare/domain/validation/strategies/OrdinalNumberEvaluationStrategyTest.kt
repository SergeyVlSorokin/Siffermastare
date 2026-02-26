package com.siffermastare.domain.validation.strategies

import com.siffermastare.domain.evaluation.EvaluationResult
import com.siffermastare.domain.models.Question
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OrdinalNumberEvaluationStrategyTest {

    private val strategy = OrdinalNumberEvaluationStrategy()

    private fun createQuestion(target: String, atoms: List<String>): Question {
        return Question(targetValue = target, spokenText = "dummy", visualHint = null, atoms = atoms)
    }

    @Test
    fun `exact match 25 vs 25 returns correct and success updates`() {
        val result = strategy.evaluate("25", createQuestion("25", listOf("ord:20", "ord:5")))
        assertTrue(result.isCorrect)
        assertEquals(2, result.atomUpdates.size)
        assertEquals(listOf(true), result.atomUpdates["ord:20"])
        assertEquals(listOf(true), result.atomUpdates["ord:5"])
    }

    @Test
    fun `partial match 25 vs 24 returns incorrect and mixed updates`() {
        val result = strategy.evaluate("24", createQuestion("25", listOf("ord:20", "ord:5")))
        assertFalse(result.isCorrect)
        assertEquals(2, result.atomUpdates.size)
        assertEquals(listOf(true), result.atomUpdates["ord:20"])
        assertEquals(listOf(false), result.atomUpdates["ord:5"])
    }

    @Test
    fun `complete mismatch 25 vs 99 returns incorrect and failure updates`() {
        val result = strategy.evaluate("99", createQuestion("25", listOf("ord:20", "ord:5")))
        assertFalse(result.isCorrect)
        assertEquals(2, result.atomUpdates.size)
        assertEquals(listOf(false), result.atomUpdates["ord:20"])
        assertEquals(listOf(false), result.atomUpdates["ord:5"])
    }

    @Test
    fun `3-digit exact match 123 vs 123`() {
        val result = strategy.evaluate("123", createQuestion("123", listOf("ord:1", "ord:20", "ord:3")))
        assertTrue(result.isCorrect)
        assertEquals(3, result.atomUpdates.size)
        assertEquals(listOf(true), result.atomUpdates["ord:1"])  // Hundred
        assertEquals(listOf(true), result.atomUpdates["ord:20"]) // Ten
        assertEquals(listOf(true), result.atomUpdates["ord:3"])  // One
    }

    @Test
    fun `3-digit partial match 123 vs 120`() {
        val result = strategy.evaluate("120", createQuestion("123", listOf("ord:1", "ord:20", "ord:3")))
        assertFalse(result.isCorrect)
        assertEquals(3, result.atomUpdates.size)
        assertEquals(listOf(true), result.atomUpdates["ord:1"])
        assertEquals(listOf(true), result.atomUpdates["ord:20"])
        assertEquals(listOf(false), result.atomUpdates["ord:3"])
    }

    @Test
    fun `structural mismatch 25 vs 205`() {
        val result = strategy.evaluate("205", createQuestion("25", listOf("ord:20", "ord:5")))
        assertFalse(result.isCorrect)
        assertEquals(2, result.atomUpdates.size)
        // Target: ord:20, ord:5. Input: ord:2, ord:5.
        // ord:5 is in both -> Success
        // ord:20 is missing -> Failure
        assertEquals(listOf(false), result.atomUpdates["ord:20"])
        assertEquals(listOf(true), result.atomUpdates["ord:5"])
    }
    
    @Test
    fun `edge case 0`() {
        val result = strategy.evaluate("0", createQuestion("0", listOf("ord:0")))
        assertTrue(result.isCorrect)
        assertEquals(1, result.atomUpdates.size)
        assertEquals(listOf(true), result.atomUpdates["ord:0"])
    }

    @Test
    fun `edge case 1000`() {
        val result = strategy.evaluate("1000", createQuestion("1000", listOf("ord:1")))
        assertTrue(result.isCorrect)
        assertEquals(1, result.atomUpdates.size)
        assertEquals(listOf(true), result.atomUpdates["ord:1"])
    }
    
    @Test
    fun `repeated atoms 555 vs 554`() {
        // Target 555 -> {ord:5 (hundreds), ord:50, ord:5 (ones)}.
        // Input 554 -> {ord:5 (hundreds), ord:50, ord:4}.
        val result = strategy.evaluate("554", createQuestion("555", listOf("ord:5", "ord:50", "ord:5")))
        assertFalse(result.isCorrect)
        // Expected atoms in target: ord:5, ord:50
        assertEquals(2, result.atomUpdates.size) 
        assertEquals(listOf(true, false), result.atomUpdates["ord:5"])
        assertEquals(listOf(true), result.atomUpdates["ord:50"])
    }

    @Test
    fun `invalid input returns all failures`() {
        val result = strategy.evaluate("abc", createQuestion("25", listOf("ord:20", "ord:5")))
        assertFalse(result.isCorrect)
        assertEquals(2, result.atomUpdates.size)
        assertEquals(listOf(false), result.atomUpdates["ord:20"])
        assertEquals(listOf(false), result.atomUpdates["ord:5"])
    }
}
