package com.siffermastare.domain.engine

import com.siffermastare.data.database.AtomState
import com.siffermastare.domain.evaluation.EvaluationResult
import com.siffermastare.testdoubles.FakeKnowledgeRepository
import com.siffermastare.testdoubles.FakeTimeProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class KnowledgeEngineTest {

    private lateinit var repository: FakeKnowledgeRepository
    private lateinit var timeProvider: FakeTimeProvider
    private lateinit var engine: KnowledgeEngine

    @Before
    fun setUp() {
        repository = FakeKnowledgeRepository()
        timeProvider = FakeTimeProvider()
        engine = KnowledgeEngine(repository, timeProvider)
    }

    // AC1: Success update with W=1.0
    @Test
    fun `success update with standard speed produces W of 1_0`() = runBlocking {
        timeProvider.currentTime = 1000L
        repository.seedAtomState(AtomState("5", 2.0f, 3.0f, 500L))

        val result = EvaluationResult(
            isCorrect = true,
            atomUpdates = mapOf("5" to listOf(true))
        )
        engine.processEvaluation(result, elapsedMs = 1600L, targetLength = 2)

        val state = repository.getAtomState("5")
        // W = clamp(800 / (1600/2), 0.2, 1.3) = clamp(800/800, ...) = 1.0
        // alpha_new = 0.9 * 2.0 + 1.0 = 2.8
        // beta_new  = 0.9 * 3.0       = 2.7
        assertEquals(2.8f, state.alpha, 0.001f)
        assertEquals(2.7f, state.beta, 0.001f)
    }

    // AC2: Failure update (standard weight 1.0)
    @Test
    fun `failure update applies weight 1_0 regardless of speed`() = runBlocking {
        timeProvider.currentTime = 1000L
        repository.seedAtomState(AtomState("20", 2.0f, 3.0f, 500L))

        val result = EvaluationResult(
            isCorrect = false,
            atomUpdates = mapOf("20" to listOf(false))
        )
        engine.processEvaluation(result, elapsedMs = 1600L, targetLength = 2)

        val state = repository.getAtomState("20")
        // alpha_new = 0.9 * 2.0       = 1.8
        // beta_new  = 0.9 * 3.0 + 1.0 = 3.7
        assertEquals(1.8f, state.alpha, 0.001f)
        assertEquals(3.7f, state.beta, 0.001f)
    }

    // AC3: Fast answer bonus W=1.3
    @Test
    fun `fast answer produces capped W of 1_3`() = runBlocking {
        timeProvider.currentTime = 1000L
        repository.seedAtomState(AtomState("5", 2.0f, 3.0f, 500L))

        val result = EvaluationResult(
            isCorrect = true,
            atomUpdates = mapOf("5" to listOf(true))
        )
        engine.processEvaluation(result, elapsedMs = 600L, targetLength = 2)

        val state = repository.getAtomState("5")
        // MPE = 600/2 = 300, W = clamp(800/300, 0.2, 1.3) = 1.3
        // alpha_new = 0.9 * 2.0 + 1.3 = 3.1
        // beta_new  = 0.9 * 3.0       = 2.7
        assertEquals(3.1f, state.alpha, 0.001f)
        assertEquals(2.7f, state.beta, 0.001f)
    }

    // AC4: Slow answer penalty W=0.2
    @Test
    fun `slow answer produces floored W of 0_2`() = runBlocking {
        timeProvider.currentTime = 1000L
        repository.seedAtomState(AtomState("5", 2.0f, 3.0f, 500L))

        val result = EvaluationResult(
            isCorrect = true,
            atomUpdates = mapOf("5" to listOf(true))
        )
        engine.processEvaluation(result, elapsedMs = 8000L, targetLength = 2)

        val state = repository.getAtomState("5")
        // MPE = 8000/2 = 4000, W = clamp(800/4000, 0.2, 1.3) = 0.2
        // alpha_new = 0.9 * 2.0 + 0.2 = 2.0
        // beta_new  = 0.9 * 3.0       = 2.7
        assertEquals(2.0f, state.alpha, 0.001f)
        assertEquals(2.7f, state.beta, 0.001f)
    }

    // AC5: Multiple atoms in single evaluation
    @Test
    fun `multiple atoms updated with correct success and failure`() = runBlocking {
        timeProvider.currentTime = 1000L
        repository.seedAtomState(AtomState("20", 2.0f, 3.0f, 500L))
        repository.seedAtomState(AtomState("5", 4.0f, 5.0f, 500L))

        val result = EvaluationResult(
            isCorrect = true,
            atomUpdates = mapOf(
                "20" to listOf(true),
                "5" to listOf(false)
            )
        )
        engine.processEvaluation(result, elapsedMs = 1600L, targetLength = 2)

        // W = clamp(800/(1600/2), 0.2, 1.3) = 1.0

        val state20 = repository.getAtomState("20")
        // Success: alpha = 0.9*2.0 + 1.0 = 2.8, beta = 0.9*3.0 = 2.7
        assertEquals(2.8f, state20.alpha, 0.001f)
        assertEquals(2.7f, state20.beta, 0.001f)

        val state5 = repository.getAtomState("5")
        // Failure: alpha = 0.9*4.0 = 3.6, beta = 0.9*5.0 + 1.0 = 5.5
        assertEquals(3.6f, state5.alpha, 0.001f)
        assertEquals(5.5f, state5.beta, 0.001f)
    }

    // AC6: Duplicate atom entries — sequential updates
    @Test
    fun `duplicate atom entries apply sequential updates`() = runBlocking {
        timeProvider.currentTime = 1000L
        repository.seedAtomState(AtomState("5", 2.0f, 3.0f, 500L))

        val result = EvaluationResult(
            isCorrect = true,
            atomUpdates = mapOf("5" to listOf(true, false))
        )
        engine.processEvaluation(result, elapsedMs = 1600L, targetLength = 2)

        val state = repository.getAtomState("5")
        // W = 1.0
        // First update (success): alpha = 0.9*2.0 + 1.0 = 2.8, beta = 0.9*3.0 = 2.7
        // Second update (failure): alpha = 0.9*2.8 = 2.52, beta = 0.9*2.7 + 1.0 = 3.43
        assertEquals(2.52f, state.alpha, 0.001f)
        assertEquals(3.43f, state.beta, 0.001f)
    }

    // AC7: Default prior for new atoms
    @Test
    fun `new atom uses default prior from repository`() = runBlocking {
        timeProvider.currentTime = 1000L
        // No pre-existing state for "X"

        val result = EvaluationResult(
            isCorrect = true,
            atomUpdates = mapOf("X" to listOf(true))
        )
        engine.processEvaluation(result, elapsedMs = 1600L, targetLength = 2)

        val state = repository.getAtomState("X")
        // W = 1.0, default prior alpha=1.0, beta=1.0
        // alpha_new = 0.9*1.0 + 1.0 = 1.9
        // beta_new  = 0.9*1.0       = 0.9
        assertEquals(1.9f, state.alpha, 0.001f)
        assertEquals(0.9f, state.beta, 0.001f)
    }

    // AC8: Absent atom receives no update
    @Test
    fun `absent atom in atomUpdates receives no update`() = runBlocking {
        timeProvider.currentTime = 1000L
        val original = AtomState("Z", 5.0f, 6.0f, 500L)
        repository.seedAtomState(original)

        val result = EvaluationResult(
            isCorrect = true,
            atomUpdates = mapOf("5" to listOf(true))
        )
        engine.processEvaluation(result, elapsedMs = 1600L, targetLength = 2)

        val stateZ = repository.getAtomState("Z")
        assertEquals(5.0f, stateZ.alpha, 0.001f)
        assertEquals(6.0f, stateZ.beta, 0.001f)
    }

    // Edge: targetLength = 0 → guard division by zero → W = 0.2
    @Test
    fun `target length zero guards division by zero with W of 0_2`() = runBlocking {
        timeProvider.currentTime = 1000L
        repository.seedAtomState(AtomState("5", 2.0f, 3.0f, 500L))

        val result = EvaluationResult(
            isCorrect = true,
            atomUpdates = mapOf("5" to listOf(true))
        )
        engine.processEvaluation(result, elapsedMs = 1600L, targetLength = 0)

        val state = repository.getAtomState("5")
        // targetLength=0 → MPE treated as very large → W = 0.2
        // alpha_new = 0.9*2.0 + 0.2 = 2.0
        // beta_new  = 0.9*3.0       = 2.7
        assertEquals(2.0f, state.alpha, 0.001f)
        assertEquals(2.7f, state.beta, 0.001f)
    }

    // Edge: elapsedMs = 0 → fastest possible → W = 1.3
    @Test
    fun `elapsed zero gives maximum W of 1_3`() = runBlocking {
        timeProvider.currentTime = 1000L
        repository.seedAtomState(AtomState("5", 2.0f, 3.0f, 500L))

        val result = EvaluationResult(
            isCorrect = true,
            atomUpdates = mapOf("5" to listOf(true))
        )
        engine.processEvaluation(result, elapsedMs = 0L, targetLength = 2)

        val state = repository.getAtomState("5")
        // MPE = 0/2 = 0 → 800/0 = Inf → clamped to 1.3
        // alpha_new = 0.9*2.0 + 1.3 = 3.1
        // beta_new  = 0.9*3.0       = 2.7
        assertEquals(3.1f, state.alpha, 0.001f)
        assertEquals(2.7f, state.beta, 0.001f)
    }
}
