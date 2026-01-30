package com.siffermastare.domain.evaluation

import com.siffermastare.domain.models.Question
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DigitalTimeEvaluationStrategyTest {

    private val strategy = DigitalTimeEvaluationStrategy()

    // Helper to create a question with just the target value (others ignored by strategy)
    private fun createQuestion(target: String): Question {
        return Question(targetValue = target, spokenText = "", visualHint = null)
    }

    // 1. Exact Match
    // Target: "1430", Input: "1430"
    // Atoms 14 (Teen) and 30 (Ten) -> Success
    @Test
    fun testExactMatch() {
        val question = createQuestion("1430")
        val result = strategy.evaluate("1430", question)

        assertTrue("Should be correct", result.isCorrect)
        val updates = result.atomUpdates
        // 1430 -> Hours=14 (Atom "14"), Minutes=30 (Atom "30")
        assertTrue(updates["14"]?.all { it } == true)
        assertTrue(updates["30"]?.all { it } == true)
        assertEquals(2, updates.size)
    }

    // 2. 24h Mismatch
    // Target: "0230", Input: "1430"
    // Target Atoms: 0, 2, 30. Input Atoms: 14, 30.
    // Result: 30 OK, 0 Fail, 2 Fail. isCorrect=False
    @Test
    fun test24hMismatch() {
        val question = createQuestion("0230")
        val result = strategy.evaluate("1430", question)

        assertFalse("Should be incorrect", result.isCorrect)
        val updates = result.atomUpdates
        
        // Target: 0230 -> H=02 (0, 2), M=30 (30)
        // Check 30 is success
        assertTrue("Atom 30 should be success", updates["30"]?.all { it } == true)
        
        // Check 0 failed
        assertTrue("Atom 0 should be failure", updates["0"]?.all { !it } == true)
        
        // Check 2 failed
        assertTrue("Atom 2 should be failure", updates["2"]?.all { !it } == true)
        
        // Target: "0230" -> Atoms: "0", "2", "30". Size: 3.
        assertEquals(3, updates.size)
    }

    // 3. Minute Mismatch
    // Target: "1415", Input: "1430"
    // Target Atoms: 14, 15. Input Atoms: 14, 30.
    // Result: 14 OK, 15 Fail. isCorrect=False
    @Test
    fun testMinuteMismatch() {
        val question = createQuestion("1415")
        val result = strategy.evaluate("1430", question)

        assertFalse("Should be incorrect", result.isCorrect)
        val updates = result.atomUpdates

        // Target: 1415 -> H=14 (14), M=15 (15)
        // Input: 1430 -> H=14 (14), M=30 (30)
        assertTrue("Atom 14 OK", updates["14"]?.all { it } == true)
        assertTrue("Atom 15 Fail", updates["15"]?.all { !it } == true) // 15 missing
        assertEquals(2, updates.size)
    }

    // 4. Complex Minute
    // Target: "0515", Input: "0515"
    // H=05 (0, 5), M=15 (15).
    // Result: 0, 5, 15 Success.
    @Test
    fun testComplexMinute() {
        val question = createQuestion("0515")
        val result = strategy.evaluate("0515", question)

        assertTrue("Should be correct", result.isCorrect)
        val updates = result.atomUpdates
        // 05 -> 0, 5
        // 15 -> 15
        assertTrue(updates["0"]?.all { it } == true)
        assertTrue(updates["5"]?.all { it } == true)
        assertTrue(updates["15"]?.all { it } == true)
        assertEquals(3, updates.size)
    }

    // 5. Midnight (Double Zero)
    // Target: "0030", Input: "0030"
    // H=00 (0, 0), M=30 (30).
    // Result: 0, 0, 30 Success.
    @Test
    fun testMidnight() {
        val question = createQuestion("0030")
        val result = strategy.evaluate("0030", question)

        assertTrue("Should be correct", result.isCorrect)
        val updates = result.atomUpdates
        
        // 00 -> 0, 0
        // 30 -> 30
        val zeroUpdates = updates["0"] ?: emptyList()
        assertEquals(2, zeroUpdates.size)
        assertTrue(zeroUpdates.all { it })
        
        assertTrue(updates["30"]?.all { it } == true)
        assertEquals(2, updates.size)
    }

    // 6. Triple Zero
    // Target: "0003", Input: "0003"
    // H=00 (0,0), M=03 (0,3).
    // Result: 0,0,0,3 Success.
    @Test
    fun testTripleZero() {
        val question = createQuestion("0003")
        val result = strategy.evaluate("0003", question)

        assertTrue("Should be correct", result.isCorrect)
        val updates = result.atomUpdates
        
        // 00 -> 0, 0
        // 03 -> 0, 3
        // Total three 0s
        val zeroUpdates = updates["0"] ?: emptyList()
        assertEquals(3, zeroUpdates.size)
        assertTrue(zeroUpdates.all { it })
        
        assertTrue(updates["3"]?.all { it } == true)
        assertEquals(2, updates.size)
    }

    // 7. Triple Zero Error
    // Target: "0003", Input: "0103"
    // Matches: 3 OK. 0 matches: Target has 3 zeros. Input has 2 zeros ("0103" -> 0,1,0,3).
    // Result: Two 0s OK, One 0 Fail. 1 Extra?
    // Wait, target 0003 -> 0,0,0,3.
    // Input 0103 -> H=01 (0,1), M=03 (0,3) -> 0,1,0,3.
    // Atom 0: Target count 3. Input count 2.
    // Matches = min(3, 2) = 2.
    // Misses = 3 - 2 = 1.
    // Updates for 0: [true, true, false].
    @Test
    fun testTripleZeroError() {
        val question = createQuestion("0003")
        val result = strategy.evaluate("0103", question)

        assertFalse("Should be incorrect", result.isCorrect)
        val updates = result.atomUpdates
        
        // Check zeros
        val zeroUpdates = updates["0"]
        assertEquals(3, zeroUpdates?.size)
        // 2 Success, 1 Failure
        val successes = zeroUpdates?.count { it }
        assertEquals(2, successes)
        
        // Check 3
        assertTrue(updates["3"]?.all { it } == true)
        
        // Input had extra "1".
        // Extra input doesn't show in updates (only target atoms track updates),
        // but it causes isCorrect=False due to "Answer is Incorrect" rule for extra atoms.
        assertEquals(2, updates.size)
    }

    // 8. Leading Zero Optional
    // Target: "0513", Input: "513"
    // Target: 0, 5, 13.
    // Input "513" -> normalized to "0513" -> 0, 5, 13
    // Result: Correct.
    @Test
    fun testLeadingZeroOptional() {
        val question = createQuestion("0513")
        val result = strategy.evaluate("513", question) // Strategy should handle "513" as "0513"

        assertTrue("Should be correct", result.isCorrect)
        val updates = result.atomUpdates
        
        assertTrue(updates["0"]?.all { it } == true)
        assertTrue(updates["5"]?.all { it } == true)
        assertTrue(updates["13"]?.all { it } == true)
        assertEquals(3, updates.size)
    }
    
    // 9. Swapped Digits
    // Target: "1415", Input: "1514"
    // H=14, M=15 vs H=15, M=14.
    // Result: Incorrect due to order mismatch. Both atoms fail.
    @Test
    fun testSwappedDigits() {
        val question = createQuestion("1415")
        // User Input: "1514"
        val result = strategy.evaluate("1514", question)

        assertFalse("Should be incorrect due to swap", result.isCorrect)
        val updates = result.atomUpdates
        
        // Position 0: Target 14. Input 15. -> 14 Missing (Fail).
        // Position 1: Target 15. Input 14. -> 15 Missing (Fail).
        
        assertTrue("Atom 14 Fail", updates["14"]?.all { !it } == true)
        assertTrue("Atom 15 Fail", updates["15"]?.all { !it } == true)
        assertEquals(2, updates.size)
    }
    // 10. Teens Boundary
    // Target: "1019", Input: "1019"
    // H=10 (Teen), M=19 (Teen).
    // Result: 10 OK, 19 OK.
    @Test
    fun testTeensBoundary() {
        val question = createQuestion("1019")
        val result = strategy.evaluate("1019", question)
        
        assertTrue("Should be correct", result.isCorrect)
        val updates = result.atomUpdates
        
        assertTrue("Atom 10 OK", updates["10"]?.all { it } == true)
        assertTrue("Atom 19 OK", updates["19"]?.all { it } == true)
        assertEquals(2, updates.size)
    }
}
