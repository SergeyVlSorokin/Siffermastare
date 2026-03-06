package com.siffermastare.domain.models

import com.siffermastare.domain.evaluation.EvaluationResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AtomSummaryTest {

    @Test
    fun `buildAtomSummaries partitions atoms correctly`() {
        val results = listOf(
            EvaluationResult(true, mapOf("5" to listOf(true), "7" to listOf(true))),
            EvaluationResult(false, mapOf("5" to listOf(false), "3" to listOf(true)))
        )

        val (improved, needsPractice) = buildAtomSummaries(results)

        val improvedIds = improved.map { it.atomId }
        val needsPracticeIds = needsPractice.map { it.atomId }

        assertEquals(listOf("3", "7"), improvedIds)
        assertEquals(listOf("5"), needsPracticeIds)

        val atom5 = needsPractice.first { it.atomId == "5" }
        assertEquals(1, atom5.successes)
        assertEquals(1, atom5.failures)
    }

    @Test
    fun `buildAtomSummaries returns empty lists for empty results`() {
        val (improved, needsPractice) = buildAtomSummaries(emptyList())

        assertTrue(improved.isEmpty())
        assertTrue(needsPractice.isEmpty())
    }

    @Test
    fun `buildAtomSummaries handles results with empty atomUpdates`() {
        val results = listOf(
            EvaluationResult(true, emptyMap()),
            EvaluationResult(false, emptyMap())
        )

        val (improved, needsPractice) = buildAtomSummaries(results)

        assertTrue(improved.isEmpty())
        assertTrue(needsPractice.isEmpty())
    }

    @Test
    fun `buildAtomSummaries aggregates atom across multiple results correctly`() {
        val results = listOf(
            EvaluationResult(true, mapOf("5" to listOf(true))),
            EvaluationResult(true, mapOf("5" to listOf(true))),
            EvaluationResult(false, mapOf("5" to listOf(false)))
        )

        val (improved, needsPractice) = buildAtomSummaries(results)

        assertTrue(improved.isEmpty())
        assertEquals(1, needsPractice.size)

        val atom5 = needsPractice.first()
        assertEquals("5", atom5.atomId)
        assertEquals(2, atom5.successes)
        assertEquals(1, atom5.failures)
    }

    @Test
    fun `buildAtomSummaries all successes go to improved`() {
        val results = listOf(
            EvaluationResult(true, mapOf("10" to listOf(true), "20" to listOf(true))),
            EvaluationResult(true, mapOf("10" to listOf(true)))
        )

        val (improved, needsPractice) = buildAtomSummaries(results)

        assertEquals(2, improved.size)
        assertTrue(needsPractice.isEmpty())

        val atom10 = improved.first { it.atomId == "10" }
        assertEquals(2, atom10.successes)
        assertEquals(0, atom10.failures)
    }

    @Test
    fun `buildAtomSummaries handles multiple booleans per atom in single result`() {
        val results = listOf(
            EvaluationResult(true, mapOf("ord:5" to listOf(true, false, true)))
        )

        val (improved, needsPractice) = buildAtomSummaries(results)

        assertTrue(improved.isEmpty())
        assertEquals(1, needsPractice.size)

        val atom = needsPractice.first()
        assertEquals("ord:5", atom.atomId)
        assertEquals(2, atom.successes)
        assertEquals(1, atom.failures)
    }

    @Test
    fun `buildAtomSummaries sorts atoms smartly`() {
        val results = listOf(
            EvaluationResult(true, mapOf(
                "10" to listOf(true),
                "2" to listOf(true),
                "ord:20" to listOf(true),
                "1" to listOf(true),
                "#kvart" to listOf(true),
                "ord:5" to listOf(true),
                "#halv" to listOf(true)
            ))
        )

        val (improved, _) = buildAtomSummaries(results)

        val improvedIds = improved.map { it.atomId }
        // Order: Numbers first (1, 2, 5, 10, 20), then non-numeric alphabetical
        assertEquals(listOf("1", "2", "ord:5", "10", "ord:20", "#halv", "#kvart"), improvedIds)
    }
}
