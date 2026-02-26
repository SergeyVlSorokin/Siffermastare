package com.siffermastare.domain.evaluation

import com.siffermastare.domain.models.Question
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FractionsEvaluationStrategyTest {

    private val strategy = FractionsEvaluationStrategy()

    /**
     * Helper: creates a Question with fraction atoms.
     * Atoms = [numerator (cardinal), "ord:" + denominator (ordinal)]
     */
    private fun createQuestion(target: String): Question {
        val parts = target.split("/")
        val atoms = listOf(parts[0], "ord:${parts[1]}")
        return Question(targetValue = target, spokenText = "", atoms = atoms)
    }

    // ===== AC2: Correct input marks all atoms Success =====

    @Test
    fun `correct input 3 over 4 marks all atoms Success`() {
        val q = createQuestion("3/4")
        val result = strategy.evaluate("3/4", q)
        assertTrue(result.isCorrect)
        assertEquals(listOf(true), result.atomUpdates["3"])
        assertEquals(listOf(true), result.atomUpdates["ord:4"])
    }

    @Test
    fun `correct input 1 over 2 marks all atoms Success`() {
        val q = createQuestion("1/2")
        val result = strategy.evaluate("1/2", q)
        assertTrue(result.isCorrect)
        assertEquals(listOf(true), result.atomUpdates["1"])
        assertEquals(listOf(true), result.atomUpdates["ord:2"])
    }

    // ===== AC3: Wrong denominator provides granular feedback =====

    @Test
    fun `wrong denominator 1 over 4 instead of 1 over 2`() {
        val q = createQuestion("1/2")
        val result = strategy.evaluate("1/4", q)
        assertFalse(result.isCorrect)
        assertEquals(listOf(true), result.atomUpdates["1"])
        assertEquals(listOf(false), result.atomUpdates["ord:2"])
    }

    // ===== AC4: Wrong numerator provides granular feedback =====

    @Test
    fun `wrong numerator 5 over 4 instead of 3 over 4`() {
        val q = createQuestion("3/4")
        val result = strategy.evaluate("5/4", q)
        assertFalse(result.isCorrect)
        assertEquals(listOf(false), result.atomUpdates["3"])
        assertEquals(listOf(true), result.atomUpdates["ord:4"])
    }

    // ===== AC5: Completely wrong input fails all atoms =====

    @Test
    fun `completely wrong input fails all atoms`() {
        val q = createQuestion("1/2")
        val result = strategy.evaluate("7/9", q)
        assertFalse(result.isCorrect)
        assertEquals(listOf(false), result.atomUpdates["1"])
        assertEquals(listOf(false), result.atomUpdates["ord:2"])
    }

    // ===== AC6: Invalid format handled gracefully =====

    @Test
    fun `input with no slash fails all atoms`() {
        val q = createQuestion("3/4")
        val result = strategy.evaluate("12", q)
        assertFalse(result.isCorrect)
        result.atomUpdates.values.forEach { results ->
            results.forEach { assertFalse(it) }
        }
    }

    @Test
    fun `non-numeric input fails all atoms`() {
        val q = createQuestion("3/4")
        val result = strategy.evaluate("abc", q)
        assertFalse(result.isCorrect)
        result.atomUpdates.values.forEach { results ->
            results.forEach { assertFalse(it) }
        }
    }

    @Test
    fun `empty input fails all atoms`() {
        val q = createQuestion("3/4")
        val result = strategy.evaluate("", q)
        assertFalse(result.isCorrect)
        result.atomUpdates.values.forEach { results ->
            results.forEach { assertFalse(it) }
        }
    }

    @Test
    fun `input with multiple slashes fails all atoms`() {
        val q = createQuestion("3/4")
        val result = strategy.evaluate("3/4/5", q)
        assertFalse(result.isCorrect)
        result.atomUpdates.values.forEach { results ->
            results.forEach { assertFalse(it) }
        }
    }

    @Test
    fun `slash with non-numeric denominator fails all atoms`() {
        val q = createQuestion("3/4")
        val result = strategy.evaluate("3/abc", q)
        assertFalse(result.isCorrect)
        result.atomUpdates.values.forEach { results ->
            results.forEach { assertFalse(it) }
        }
    }

    // ===== AC7: Denominator 10 uses single atom =====

    @Test
    fun `denominator 10 correct input`() {
        val q = createQuestion("5/10")
        val result = strategy.evaluate("5/10", q)
        assertTrue(result.isCorrect)
        assertEquals(listOf(true), result.atomUpdates["5"])
        assertEquals(listOf(true), result.atomUpdates["ord:10"])
    }

    @Test
    fun `denominator 10 wrong numerator`() {
        val q = createQuestion("5/10")
        val result = strategy.evaluate("3/10", q)
        assertFalse(result.isCorrect)
        assertEquals(listOf(false), result.atomUpdates["5"])
        assertEquals(listOf(true), result.atomUpdates["ord:10"])
    }

    // ===== Whitespace handling =====

    @Test
    fun `input with extra whitespace is trimmed and graded correctly`() {
        val q = createQuestion("3/4")
        val result = strategy.evaluate("  3/4  ", q)
        assertTrue(result.isCorrect)
        assertEquals(listOf(true), result.atomUpdates["3"])
        assertEquals(listOf(true), result.atomUpdates["ord:4"])
    }
}
