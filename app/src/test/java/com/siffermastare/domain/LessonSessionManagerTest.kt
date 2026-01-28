package com.siffermastare.domain

import com.siffermastare.domain.generators.NumberGenerator
import com.siffermastare.domain.models.Question
import com.siffermastare.domain.evaluation.ExactMatchEvaluationStrategy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LessonSessionManagerTest {

    private val fakeGenerator = object : NumberGenerator {
        override val evaluationStrategy = ExactMatchEvaluationStrategy()

        override fun generateLesson(count: Int): List<Question> {
            return List(count) { 
                Question(targetValue = "5", spokenText = "5", visualHint = null) 
            }
        }
    }

    @Test
    fun `startLesson initializes state correctly`() = runTest {
        val manager = LessonSessionManager()
        manager.startLesson(fakeGenerator)
        
        val state = manager.lessonState.first()
        assertEquals(1, state.questionCount)
        assertEquals(10, state.totalQuestions)
        assertEquals("5", state.currentQuestion?.targetValue)
        assertEquals(0, state.mistakeCount)
    }

    @Test
    fun `submitAnswer handles correct answer`() = runTest {
        val manager = LessonSessionManager()
        manager.startLesson(fakeGenerator)
        
        val result = manager.submitAnswer("5")
        assertTrue(result)
        // Note: In real app, we wait for nextQuestion() call, but checking immediate result here
    }

    @Test
    fun `submitAnswer handles incorrect answer`() = runTest {
        val manager = LessonSessionManager()
        manager.startLesson(fakeGenerator)
        
        val result = manager.submitAnswer("9")
        assertEquals(false, result)
        
        val state = manager.lessonState.first()
        assertEquals(1, state.mistakeCount)
    }
    @Test
    fun `submitAnswer uses custom validator when provided`() = runTest {
        val manager = LessonSessionManager()
        // Override generator to produce known target "5"
        manager.startLesson(fakeGenerator)
        
        // Validator that always returns true
        val alwaysTrueValidator: (String, String) -> Boolean = { _, _ -> true }
        
        // Submit wrong answer "9", but with alwaysTrueValidator
        val result = manager.submitAnswer("9", alwaysTrueValidator)
        
        assertTrue("Validator should override equality check", result)
    }
}
