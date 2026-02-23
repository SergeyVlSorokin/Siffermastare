package com.siffermastare.domain.evaluation

import com.siffermastare.domain.generators.InformalTimeGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Integration tests verifying that Questions produced by InformalTimeGenerator
 * evaluate correctly through InformalTimeEvaluationStrategy.
 *
 * These tests close the gap between the unit-tested generator (atoms, targetValue)
 * and the unit-tested strategy (grading rules) by asserting they work together.
 */
class InformalTimeIntegrationTest {

    private val generator = InformalTimeGenerator()
    private val strategy = InformalTimeEvaluationStrategy()

    // ===== Deterministic: one pattern per time zone =====

    @Test
    fun `exact hour - klockan tva`() {
        val q = generateQuestion(hour = 2, minute = 0)
        val result = strategy.evaluate("0200", q)
        assertTrue("isCorrect", result.isCorrect)
        assertAllAtomsTrue(result)
    }

    @Test
    fun `kvart over - kvart over fyra`() {
        val q = generateQuestion(hour = 4, minute = 15)
        val result = strategy.evaluate("0415", q)
        assertTrue("isCorrect", result.isCorrect)
        assertAllAtomsTrue(result)
    }

    @Test
    fun `minutes over - fem over tva`() {
        val q = generateQuestion(hour = 2, minute = 5)
        val result = strategy.evaluate("0205", q)
        assertTrue("isCorrect", result.isCorrect)
        assertAllAtomsTrue(result)
    }

    @Test
    fun `minutes i halv - fem i halv tre`() {
        val q = generateQuestion(hour = 2, minute = 25)
        val result = strategy.evaluate("0225", q)
        assertTrue("isCorrect", result.isCorrect)
        assertAllAtomsTrue(result)
    }

    @Test
    fun `halv - halv tre`() {
        val q = generateQuestion(hour = 2, minute = 30)
        val result = strategy.evaluate("0230", q)
        assertTrue("isCorrect", result.isCorrect)
        assertAllAtomsTrue(result)
    }

    @Test
    fun `minutes over halv - fem over halv tre`() {
        val q = generateQuestion(hour = 2, minute = 35)
        val result = strategy.evaluate("0235", q)
        assertTrue("isCorrect", result.isCorrect)
        assertAllAtomsTrue(result)
    }

    @Test
    fun `minutes i - tjugo i tre`() {
        val q = generateQuestion(hour = 2, minute = 40)
        val result = strategy.evaluate("0240", q)
        assertTrue("isCorrect", result.isCorrect)
        assertAllAtomsTrue(result)
    }

    @Test
    fun `kvart i - kvart i tre`() {
        val q = generateQuestion(hour = 2, minute = 45)
        val result = strategy.evaluate("0245", q)
        assertTrue("isCorrect", result.isCorrect)
        assertAllAtomsTrue(result)
    }

    // ===== 24h equivalence: generator uses 24h, user submits 12h =====

    @Test
    fun `24h generator input evaluates with 12h user answer`() {
        val q = generateQuestion(hour = 14, minute = 25) // "fem i halv tre" at 14:25
        val result = strategy.evaluate("0225", q)
        assertTrue("12h equivalent should be correct", result.isCorrect)
        assertAllAtomsTrue(result)
    }

    @Test
    fun `24h generator input evaluates with 24h user answer`() {
        val q = generateQuestion(hour = 14, minute = 25)
        val result = strategy.evaluate("1425", q)
        assertTrue("24h should also be correct", result.isCorrect)
        assertAllAtomsTrue(result)
    }

    // ===== Edge cases: midnight/noon =====

    @Test
    fun `midnight - klockan tolv`() {
        val q = generateQuestion(hour = 0, minute = 0)
        val result = strategy.evaluate("0000", q)
        assertTrue("isCorrect", result.isCorrect)
        assertAllAtomsTrue(result)
        assertEquals(listOf("12"), q.atoms) // hour 0 → atom "12"
    }

    @Test
    fun `noon - klockan tolv`() {
        val q = generateQuestion(hour = 12, minute = 0)
        val result = strategy.evaluate("1200", q)
        assertTrue("isCorrect", result.isCorrect)
        assertAllAtomsTrue(result)
        assertEquals(listOf("12"), q.atoms)
    }

    // ===== Duplicate atom: tio over tio =====

    @Test
    fun `duplicate atom - tio over tio`() {
        val q = generateQuestion(hour = 10, minute = 10)
        val result = strategy.evaluate("1010", q)
        assertTrue("isCorrect", result.isCorrect)
        assertEquals(listOf("10", "#over", "10"), q.atoms)
        assertEquals(mapOf("10" to listOf(true, true), "#over" to listOf(true)), result.atomUpdates)
    }

    // ===== Wrong answer through integration =====

    @Test
    fun `wrong answer detected through full pipeline`() {
        val q = generateQuestion(hour = 4, minute = 15) // kvart over fyra
        val result = strategy.evaluate("0515", q)        // wrong hour
        assertTrue("should be incorrect", !result.isCorrect)
        // Kvart and over still correct, but hour atom should fail
        assertEquals(false, result.atomUpdates[q.atoms.last()]?.last())
    }

    // ===== Bulk: generate N random questions, verify each self-evaluates =====

    @Test
    fun `bulk - generated questions self-evaluate correctly`() {
        val questions = generator.generateLesson(50)
        questions.forEach { q ->
            val correctAnswer = q.targetValue.split("|").first()
            val result = strategy.evaluate(correctAnswer, q)
            assertTrue(
                "Question '${q.spokenText}' (target=$correctAnswer, atoms=${q.atoms}) " +
                    "should self-evaluate as correct but got isCorrect=${result.isCorrect}",
                result.isCorrect
            )
            assertAllAtomsTrue(result, "Question '${q.spokenText}' (target=$correctAnswer)")
        }
    }

    @Test
    fun `bulk - second alternative also evaluates correctly`() {
        val questions = generator.generateLesson(50)
        questions.forEach { q ->
            val altAnswer = q.targetValue.split("|").last()
            val result = strategy.evaluate(altAnswer, q)
            assertTrue(
                "Question '${q.spokenText}' alt=$altAnswer should be correct",
                result.isCorrect
            )
            assertAllAtomsTrue(result, "Question '${q.spokenText}' alt=$altAnswer")
        }
    }

    // ===== Helpers =====

    private fun generateQuestion(hour: Int, minute: Int) =
        com.siffermastare.domain.models.Question(
            targetValue = buildTargetValue(hour, minute),
            spokenText = generator.formatInformalTime(hour, minute),
            atoms = generator.buildAtoms(hour, minute)
        )

    private fun buildTargetValue(hour: Int, minute: Int): String {
        val h = hour.toString().padStart(2, '0')
        val m = minute.toString().padStart(2, '0')
        val altH = ((hour + 12) % 24).toString().padStart(2, '0')
        return "$h$m|$altH$m"
    }

    private fun assertAllAtomsTrue(result: EvaluationResult, context: String = "") {
        result.atomUpdates.forEach { (atom, grades) ->
            grades.forEachIndexed { i, grade ->
                assertTrue("$context Atom '$atom'[$i] expected true but was false", grade)
            }
        }
    }
}
