package com.siffermastare.ui.lesson

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNotEquals
import org.junit.Test
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class LessonViewModelTest {

    @Test
    fun initialState_isQuestionOne() = runBlocking {
        val viewModel = LessonViewModel()
        val state = viewModel.uiState.first()

        assertEquals(1, state.questionCount)
        assertEquals(10, state.totalQuestions)
        assertFalse(state.isLessonComplete)
        assertEquals("", state.currentInput)
    }

    @Test
    fun correctAnswer_incrementsQuestionCount() = runBlocking {
        val viewModel = LessonViewModel()
        val initialState = viewModel.uiState.first()
        val target = initialState.targetNumber

        // Simulate typing correct number
        viewModel.onDigitClick(target)
        viewModel.onCheckClick()

        val newState = viewModel.uiState.first()
        assertEquals(2, newState.questionCount)
        assertEquals("Correct!", newState.feedbackMessage)
        // Previous input should be cleared
        assertEquals("", newState.currentInput)
    }

    @Test
    fun incorrectAnswer_doesNotIncrementCount() = runBlocking {
        val viewModel = LessonViewModel()
        val initialState = viewModel.uiState.first()
        val target = initialState.targetNumber
        
        // Simulate typing incorrect number (ensure it's different)
        val wrongInput = if (target == 0) 1 else 0
        viewModel.onDigitClick(wrongInput)
        viewModel.onCheckClick()

        val newState = viewModel.uiState.first()
        assertEquals(1, newState.questionCount)
        assertEquals("Try Again", newState.feedbackMessage)
        assertEquals(wrongInput.toString(), newState.currentInput)
    }

    @Test
    fun completing10Questions_setsLessonComplete() = runBlocking {
        val viewModel = LessonViewModel()
        
        // Loop through 10 questions
        for (i in 1..10) {
            val state = viewModel.uiState.first()
            val target = state.targetNumber
            
            viewModel.onDigitClick(target)
            viewModel.onCheckClick()
        }

        val finalState = viewModel.uiState.first()
        assertTrue(finalState.isLessonComplete)
        assertEquals(10, finalState.questionCount) // It stays at 10 or increments to 11? Logic says if count >= total, complete.
        // Let's check logic:
        // if count (e.g. 10) >= total (10) -> complete.
        // So it stays at 10.
    }
}
