package com.siffermastare.domain

import com.siffermastare.domain.generators.NumberGenerator
import com.siffermastare.domain.models.Question
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LessonSessionManagerTest {

    private val fakeGenerator = object : NumberGenerator {
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
}
