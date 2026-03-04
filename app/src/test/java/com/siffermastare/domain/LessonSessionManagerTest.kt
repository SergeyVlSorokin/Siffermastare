package com.siffermastare.domain

import com.siffermastare.domain.engine.KnowledgeEngine
import com.siffermastare.domain.generators.NumberGenerator
import com.siffermastare.domain.models.Question
import com.siffermastare.domain.evaluation.ExactMatchEvaluationStrategy
import com.siffermastare.domain.evaluation.EvaluationResult
import com.siffermastare.testdoubles.FakeKnowledgeRepository
import com.siffermastare.testdoubles.FakeTimeProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LessonSessionManagerTest {

    private val fakeTimeProvider = FakeTimeProvider()
    
    private class StubKnowledgeEngine : KnowledgeEngine(
        repository = FakeKnowledgeRepository(), 
        timeProvider = FakeTimeProvider()
    ) {
        var lastElapsedMs: Long = -1L
        var lastTargetLength: Int = -1
        var callCount: Int = 0
        var lastResult: EvaluationResult? = null
        
        override suspend fun processEvaluation(result: EvaluationResult, elapsedMs: Long, targetLength: Int) {
            lastElapsedMs = elapsedMs
            lastTargetLength = targetLength
            lastResult = result
            callCount++
        }
    }

    private val fakeGenerator = object : NumberGenerator {
        override val evaluationStrategy = ExactMatchEvaluationStrategy()

        override fun generateLesson(count: Int): List<Question> {
            return List(count) { 
                Question(targetValue = "5", spokenText = "5", visualHint = null) 
            }
        }
    }

    private val multiCharGenerator = object : NumberGenerator {
        override val evaluationStrategy = ExactMatchEvaluationStrategy()

        override fun generateLesson(count: Int): List<Question> {
            return List(count) {
                Question(targetValue = "125", spokenText = "etthundratjugofem", visualHint = null)
            }
        }
    }

    @Test
    fun `startLesson initializes state correctly`() = runTest {
        val manager = LessonSessionManager(null, fakeTimeProvider)
        manager.startLesson(fakeGenerator)
        
        val state = manager.lessonState.first()
        assertEquals(1, state.questionCount)
        assertEquals(10, state.totalQuestions)
        assertEquals("5", state.currentQuestion?.targetValue)
        assertEquals(0, state.mistakeCount)
        assertEquals(0L, state.questionStartTime)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `submitAnswer handles correct answer and calculates elapsed time`() = runTest {
        val engine = StubKnowledgeEngine()
        val manager = LessonSessionManager(engine, fakeTimeProvider)
        
        fakeTimeProvider.currentTime = 1000L
        manager.startLesson(fakeGenerator)
        
        fakeTimeProvider.currentTime = 2500L
        
        val result = manager.submitAnswer("5", scope = this)
        assertTrue(result)
        
        runCurrent()
        
        assertEquals(1500L, engine.lastElapsedMs)
    }

    @Test
    fun `submitAnswer handles incorrect answer without async error`() = runTest {
        val engine = StubKnowledgeEngine()
        val manager = LessonSessionManager(engine, fakeTimeProvider)
        manager.startLesson(fakeGenerator)
        
        val result = manager.submitAnswer("9", scope = this)
        assertEquals(false, result)
        
        val state = manager.lessonState.first()
        assertEquals(1, state.mistakeCount)
    }
    
    @Test
    fun `submitAnswer uses custom validator when provided`() = runTest {
        val manager = LessonSessionManager(null, fakeTimeProvider)
        manager.startLesson(fakeGenerator)
        
        val alwaysTrueValidator: (String, String) -> Boolean = { _, _ -> true }
        
        val result = manager.submitAnswer("9", alwaysTrueValidator, scope = this)
        
        assertTrue("Validator should override equality check", result)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `nextQuestion resets questionStartTime`() = runTest {
        val manager = LessonSessionManager(null, fakeTimeProvider)
        
        fakeTimeProvider.currentTime = 1000L
        manager.startLesson(fakeGenerator)
        
        val stateAfterStart = manager.lessonState.first()
        assertEquals(1000L, stateAfterStart.questionStartTime)
        
        fakeTimeProvider.currentTime = 5000L
        manager.nextQuestion()
        
        val stateAfterNext = manager.lessonState.first()
        assertEquals(5000L, stateAfterNext.questionStartTime)
        assertEquals(2, stateAfterNext.questionCount)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `submitAnswer passes correct targetLength to engine`() = runTest {
        val engine = StubKnowledgeEngine()
        val manager = LessonSessionManager(engine, fakeTimeProvider)
        
        fakeTimeProvider.currentTime = 1000L
        manager.startLesson(multiCharGenerator)
        
        fakeTimeProvider.currentTime = 2000L
        manager.submitAnswer("125", scope = this)
        runCurrent()
        
        assertEquals(3, engine.lastTargetLength)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `submitAnswer calls engine via legacy validator path`() = runTest {
        val engine = StubKnowledgeEngine()
        val manager = LessonSessionManager(engine, fakeTimeProvider)
        
        fakeTimeProvider.currentTime = 1000L
        manager.startLesson(fakeGenerator)
        
        fakeTimeProvider.currentTime = 3000L
        
        // Validator that always returns true
        val alwaysTrueValidator: (String, String) -> Boolean = { _, _ -> true }
        
        val result = manager.submitAnswer("9", alwaysTrueValidator, scope = this)
        assertTrue("Validator should override equality check", result)
        
        runCurrent()
        
        // Engine should be called with correct elapsed time even through validator path
        assertEquals(1, engine.callCount)
        assertEquals(2000L, engine.lastElapsedMs)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `submitAnswer does not call processEvaluation when scope is null`() = runTest {
        val engine = StubKnowledgeEngine()
        val manager = LessonSessionManager(engine, fakeTimeProvider)
        manager.startLesson(fakeGenerator)
        
        manager.submitAnswer("5", scope = null)
        runCurrent()
        
        assertEquals("processEvaluation should not be called without scope", 0, engine.callCount)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `submitAnswer does not crash when processEvaluation throws`() = runTest {
        val throwingEngine = object : KnowledgeEngine(
            repository = FakeKnowledgeRepository(),
            timeProvider = FakeTimeProvider()
        ) {
            override suspend fun processEvaluation(result: EvaluationResult, elapsedMs: Long, targetLength: Int) {
                throw RuntimeException("Simulated DB failure")
            }
        }
        
        val manager = LessonSessionManager(throwingEngine, fakeTimeProvider)
        manager.startLesson(fakeGenerator)
        
        val result = manager.submitAnswer("5", scope = this)
        runCurrent()
        
        assertTrue("Answer should still be graded correctly despite DB failure", result)
    }
}
