package com.siffermastare.domain.validation.strategies

import com.siffermastare.domain.evaluation.EvaluationResult
import com.siffermastare.domain.models.Question
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StandardNumberEvaluationStrategyTest {

    private val strategy = StandardNumberEvaluationStrategy()

    private fun createQuestion(target: String): Question {
        return Question(targetValue = target, spokenText = "dummy", visualHint = null)
    }

    @Test
    fun `exact match 25 vs 25 returns correct and success updates`() {
        val result = strategy.evaluate("25", createQuestion("25"))
        assertTrue(result.isCorrect)
        assertEquals(2, result.atomUpdates.size)
        assertEquals(listOf(true), result.atomUpdates["20"])
        assertEquals(listOf(true), result.atomUpdates["5"])
    }

    @Test
    fun `partial match 25 vs 24 returns incorrect and mixed updates`() {
        val result = strategy.evaluate("24", createQuestion("25"))
        assertFalse(result.isCorrect)
        assertEquals(2, result.atomUpdates.size)
        assertEquals(listOf(true), result.atomUpdates["20"])
        assertEquals(listOf(false), result.atomUpdates["5"])
    }

    @Test
    fun `complete mismatch 25 vs 99 returns incorrect and failure updates`() {
        val result = strategy.evaluate("99", createQuestion("25"))
        assertFalse(result.isCorrect)
        assertEquals(2, result.atomUpdates.size)
        assertEquals(listOf(false), result.atomUpdates["20"])
        assertEquals(listOf(false), result.atomUpdates["5"])
    }

    @Test
    fun `3-digit exact match 123 vs 123`() {
        val result = strategy.evaluate("123", createQuestion("123"))
        assertTrue(result.isCorrect)
        assertEquals(3, result.atomUpdates.size)
        assertEquals(listOf(true), result.atomUpdates["1"])  // Hundred
        assertEquals(listOf(true), result.atomUpdates["20"]) // Ten
        assertEquals(listOf(true), result.atomUpdates["3"])  // One
    }

    @Test
    fun `3-digit partial match 123 vs 120`() {
        val result = strategy.evaluate("120", createQuestion("123"))
        assertFalse(result.isCorrect)
        assertEquals(3, result.atomUpdates.size)
        assertEquals(listOf(true), result.atomUpdates["1"])
        assertEquals(listOf(true), result.atomUpdates["20"])
        assertEquals(listOf(false), result.atomUpdates["3"])
    }

    @Test
    fun `3-digit hundreds only 500 vs 500`() {
        val result = strategy.evaluate("500", createQuestion("500"))
        assertTrue(result.isCorrect)
        assertEquals(1, result.atomUpdates.size)
        assertEquals(listOf(true), result.atomUpdates["5"])
        assertTrue(result.atomUpdates["50"]?.isEmpty() ?: true) // No tens
        assertTrue(result.atomUpdates["0"]?.isEmpty() ?: true) // No zero implied
    }

    @Test
    fun `structural mismatch 25 vs 205`() {
        val result = strategy.evaluate("205", createQuestion("25"))
        assertFalse(result.isCorrect)
        assertEquals(2, result.atomUpdates.size)
        // Target: 20, 5. Input: 2, 5.
        // 5 is in both -> Success
        // 20 is missing -> Failure
        assertEquals(listOf(false), result.atomUpdates["20"])
        assertEquals(listOf(true), result.atomUpdates["5"])
    }
    
    @Test
    fun `edge case 0`() {
        val result = strategy.evaluate("0", createQuestion("0"))
        assertTrue(result.isCorrect)
        assertEquals(1, result.atomUpdates.size)
        assertEquals(listOf(true), result.atomUpdates["0"])
    }

    @Test
    fun `edge case 1000`() {
        val result = strategy.evaluate("1000", createQuestion("1000"))
        assertTrue(result.isCorrect)
        assertEquals(1, result.atomUpdates.size)
        assertEquals(listOf(true), result.atomUpdates["1"])
    }
    
    @Test
    fun `repeated atoms 505 vs 505`() {
        // Target 505 -> {5, 5}. Input 505 -> {5, 5}
        val result = strategy.evaluate("505", createQuestion("505"))
        assertTrue(result.isCorrect)
        assertEquals(1, result.atomUpdates.size) // Key "5" only
        assertEquals(listOf(true, true), result.atomUpdates["5"])
    }

    @Test
    fun `repeated atoms 505 vs 500`() {
        // Target 505 -> {5 (hundreds), 5 (ones)}. Input 500 -> {5 (hundreds)}
        // Match 1. Miss 1.
        val result = strategy.evaluate("500", createQuestion("505"))
        assertFalse(result.isCorrect)
        assertEquals(1, result.atomUpdates.size)
        assertEquals(listOf(true, false), result.atomUpdates["5"])
    }
    
    @Test
    fun `invalid input returns all failures`() {
        val result = strategy.evaluate("abc", createQuestion("25"))
        assertFalse(result.isCorrect)
        assertEquals(2, result.atomUpdates.size)
        assertEquals(listOf(false), result.atomUpdates["20"])
        assertEquals(listOf(false), result.atomUpdates["5"])
    }
}
