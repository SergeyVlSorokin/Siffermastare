package com.siffermastare.domain.evaluation

import com.siffermastare.domain.models.Question
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PhoneNumberEvaluationStrategyTest {

    private val strategy = PhoneNumberEvaluationStrategy()

    /**
     * Helper: creates a Question with hybrid atoms for a 10-digit phone target.
     */
    private fun createQuestion(target: String): Question {
        val atoms = PhoneNumberDecomposer.decompose(target)
        return Question(targetValue = target, spokenText = "", atoms = atoms)
    }

    // ===== AC2: Correct input marks all atoms Success =====

    @Test
    fun `correct input 0701234567 marks all atoms Success`() {
        // Target atoms: ["0","7","0","1","2","3","40","5","60","7"]
        val q = createQuestion("0701234567")
        val result = strategy.evaluate("0701234567", q)
        assertTrue(result.isCorrect)
        // All atoms should be success
        result.atomUpdates.forEach { (atom, results) ->
            results.forEach { assertTrue("Atom '$atom' should be Success", it) }
        }
    }

    // ===== AC3: Partial digit errors provide granular feedback =====

    @Test
    fun `partial error last digit 9 instead of 7 provides granular feedback`() {
        // Target: "0701234567" → atoms include "7" x2 (prefix index 1, pair2 decomposition of 67→60,7)
        val q = createQuestion("0701234567")
        val result = strategy.evaluate("0701234569", q)
        assertFalse(result.isCorrect)
        // "7" appears 2x in target, 1x in input → 1 Success, 1 Failure
        assertEquals(listOf(true, false), result.atomUpdates["7"])
        // "9" not in target → ignored (No Extra Atoms rule)
        assertFalse(result.atomUpdates.containsKey("9"))
    }

    // ===== AC4: Pair with leading zero uses individual digit atoms =====

    @Test
    fun `pair 05 decomposes to individual digit atoms 0 and 5`() {
        // Target: "0701230567" → pair1=05 → atoms ["0","5"], pair2=67 → atoms ["60","7"]
        val q = createQuestion("0701230567")
        // Verify atoms include individual digits for pair 05
        assertTrue("Atoms should contain '5' from pair 05", q.atoms.contains("5"))
        // Count of "0" should include: prefix(0,0) + pair1(0) = at least 3
        val zeroCount = q.atoms.count { it == "0" }
        assertTrue("Should have at least 3 zeros (prefix + pair)", zeroCount >= 3)

        val result = strategy.evaluate("0701230567", q)
        assertTrue(result.isCorrect)
    }

    @Test
    fun `pair 05 evaluation grades digit atoms correctly`() {
        // Target with pair=05: "0700000567"
        // prefix: "0","7","0", group1: "0","0","0", pair1: "0","5", pair2: "60","7"
        val q = createQuestion("0700000567")
        val result = strategy.evaluate("0700000567", q)
        assertTrue(result.isCorrect)
        // All should be success
        result.atomUpdates.values.forEach { results ->
            results.forEach { assertTrue(it) }
        }
    }

    // ===== AC5: Pair with teen uses standard decomposition =====

    @Test
    fun `pair 12 decomposes to single teen atom`() {
        // Target: "0701231267" → pair1=12 → atom ["12"]
        val q = createQuestion("0701231267")
        assertTrue("Atoms should contain teen '12'", q.atoms.contains("12"))
        val result = strategy.evaluate("0701231267", q)
        assertTrue(result.isCorrect)
        assertEquals(listOf(true), result.atomUpdates["12"])
    }

    @Test
    fun `pair 10 decomposes to single atom`() {
        // Target: "0701231067" → pair1=10 → atom ["10"]
        val q = createQuestion("0701231067")
        assertTrue("Atoms should contain '10'", q.atoms.contains("10"))
        val result = strategy.evaluate("0701231067", q)
        assertTrue(result.isCorrect)
        assertEquals(listOf(true), result.atomUpdates["10"])
    }

    @Test
    fun `pair 20 decomposes to tens-only atom`() {
        // Target: "0701232067" → pair1=20 → atom ["20"]
        val q = createQuestion("0701232067")
        assertTrue("Atoms should contain '20'", q.atoms.contains("20"))
        val result = strategy.evaluate("0701232067", q)
        assertTrue(result.isCorrect)
        assertEquals(listOf(true), result.atomUpdates["20"])
    }

    // ===== AC6: Pair with double zero uses individual digit atoms =====

    @Test
    fun `pair 00 decomposes to two zero atoms`() {
        // Target: "0701230067" → pair1=00 → atoms ["0","0"]
        val q = createQuestion("0701230067")
        // Zeros from: prefix(0,0), group1 could have none, pair1(0,0), pair2 67→60,7
        // Total "0" count from prefix = 2 (positions 0,2), pair1 = 2 => at least 4
        val zeroCount = q.atoms.count { it == "0" }
        assertTrue("Should have zeros from prefix and pair 00", zeroCount >= 4)

        val result = strategy.evaluate("0701230067", q)
        assertTrue(result.isCorrect)
    }

    @Test
    fun `pair 00 zeros are bag-counted with prefix zeros`() {
        // Target: "0701230067"
        // prefix: "0","7","0" → two "0"s
        // group1: "1","2","3" → zero "0"s
        // pair1: "00" → two "0"s (individual digits)
        // pair2: "67" → "60","7"
        // Total "0" atoms = 4
        val q = createQuestion("0701230067")
        val result = strategy.evaluate("0701230067", q)
        assertTrue(result.isCorrect)

        // All 4 zeros should be success via bag logic
        assertEquals(listOf(true, true, true, true), result.atomUpdates["0"])
    }

    // ===== AC7: Non-numeric and length-mismatch inputs =====

    @Test
    fun `non-numeric input fails all target atoms`() {
        val q = createQuestion("0701234567")
        val result = strategy.evaluate("abc", q)
        assertFalse(result.isCorrect)
        // All atoms should be Failure
        result.atomUpdates.values.forEach { results ->
            results.forEach { assertFalse(it) }
        }
    }

    @Test
    fun `too short input returns incorrect with no atom grading`() {
        val q = createQuestion("0701234567")
        val result = strategy.evaluate("070123456", q)
        assertFalse(result.isCorrect)
        // Length mismatch → atoms are NOT graded (empty updates)
        assertTrue("Atom updates should be empty for length mismatch", result.atomUpdates.isEmpty())
    }

    @Test
    fun `too long input returns incorrect with no atom grading`() {
        val q = createQuestion("0701234567")
        val result = strategy.evaluate("07012345678", q)
        assertFalse(result.isCorrect)
        // Length mismatch → atoms are NOT graded (empty updates)
        assertTrue("Atom updates should be empty for length mismatch", result.atomUpdates.isEmpty())
    }

    @Test
    fun `completely wrong input fails all target atoms`() {
        val q = createQuestion("0701234567")
        val result = strategy.evaluate("9999999999", q)
        assertFalse(result.isCorrect)
        // Atoms from input: "9","9","9","9","9","9", decompose(99)=["90","9"], decompose(99)=["90","9"]
        // None of the target atoms "0","7","0","1","2","3","40","5","60","7" get matched
        result.atomUpdates.forEach { (atom, results) ->
            results.forEach { assertFalse("Atom '$atom' should be Failure for completely wrong input", it) }
        }
    }

    @Test
    fun `empty string input returns incorrect with no atom grading`() {
        val q = createQuestion("0701234567")
        val result = strategy.evaluate("", q)
        assertFalse(result.isCorrect)
        // Empty string is all-digits (vacuously true), but length 0 ≠ 10 → not graded
        assertTrue("Atom updates should be empty for empty input", result.atomUpdates.isEmpty())
    }

    // ===== Decomposer unit tests =====

    @Test
    fun `decompose standard phone number`() {
        val atoms = PhoneNumberDecomposer.decompose("0701234567")
        assertEquals(
            listOf("0", "7", "0", "1", "2", "3", "40", "5", "60", "7"),
            atoms
        )
    }

    @Test
    fun `decompose pair leading zero 05`() {
        // "0701230567" → pair1=05 → ["0","5"]
        val atoms = PhoneNumberDecomposer.decompose("0701230567")
        // prefix: 0,7,0; group1: 1,2,3; pair1: 0,5; pair2: 60,7
        assertEquals(
            listOf("0", "7", "0", "1", "2", "3", "0", "5", "60", "7"),
            atoms
        )
    }

    @Test
    fun `decompose pair double zero 00`() {
        val atoms = PhoneNumberDecomposer.decompose("0701230067")
        // pair1=00→["0","0"], pair2=67→["60","7"]
        assertEquals(
            listOf("0", "7", "0", "1", "2", "3", "0", "0", "60", "7"),
            atoms
        )
    }

    @Test
    fun `decompose pair teen 12`() {
        val atoms = PhoneNumberDecomposer.decompose("0701231267")
        // pair1=12→["12"], pair2=67→["60","7"]
        assertEquals(
            listOf("0", "7", "0", "1", "2", "3", "12", "60", "7"),
            atoms
        )
    }

    @Test
    fun `decompose pair tens only 20`() {
        val atoms = PhoneNumberDecomposer.decompose("0701232067")
        // pair1=20→["20"], pair2=67→["60","7"]
        assertEquals(
            listOf("0", "7", "0", "1", "2", "3", "20", "60", "7"),
            atoms
        )
    }

    @Test
    fun `decomposePair less than 10 returns individual digits`() {
        assertEquals(listOf("0", "5"), PhoneNumberDecomposer.decomposePair(5))
        assertEquals(listOf("0", "0"), PhoneNumberDecomposer.decomposePair(0))
        assertEquals(listOf("0", "9"), PhoneNumberDecomposer.decomposePair(9))
    }

    @Test
    fun `decomposePair 10 or more uses standard decomposition`() {
        assertEquals(listOf("12"), PhoneNumberDecomposer.decomposePair(12))
        assertEquals(listOf("10"), PhoneNumberDecomposer.decomposePair(10))
        assertEquals(listOf("20"), PhoneNumberDecomposer.decomposePair(20))
        assertEquals(listOf("40", "5"), PhoneNumberDecomposer.decomposePair(45))
        assertEquals(listOf("90", "9"), PhoneNumberDecomposer.decomposePair(99))
    }
}
