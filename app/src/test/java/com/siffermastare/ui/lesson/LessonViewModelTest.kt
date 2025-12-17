package com.siffermastare.ui.lesson

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before

@OptIn(ExperimentalCoroutinesApi::class)
class LessonViewModelTest {

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_isQuestionOne_Neutral() = runTest {
        val viewModel = LessonViewModel()
        val state = viewModel.uiState.first()

        assertEquals(1, state.questionCount)
        assertEquals(AnswerState.NEUTRAL, state.answerState)
        assertEquals("", state.currentInput)
    }

    @Test
    fun correctAnswer_setsCorrectState_thenNeutral() = runTest {
        val viewModel = LessonViewModel()
        val initialState = viewModel.uiState.first()
        val target = initialState.targetNumber

        viewModel.onDigitClick(target)
        val inputState = viewModel.uiState.first()
        assertEquals(target.toString(), inputState.currentInput)

        viewModel.onCheckClick()
        
        // Advance time past the feedback delay
        advanceTimeBy(LessonViewModel.FEEDBACK_DELAY + 100)
        
        val finalState = viewModel.uiState.first()
        assertEquals(AnswerState.NEUTRAL, finalState.answerState)
        assertEquals(2, finalState.questionCount)
        assertEquals("", finalState.currentInput)
    }

    @Test
    fun incorrectAnswer_setsIncorrectState_thenReplays() = runTest {
        val viewModel = LessonViewModel()
        val initialState = viewModel.uiState.first()
        val target = initialState.targetNumber
        
        val wrongInput = if (target == 0) 1 else 0
        viewModel.onDigitClick(wrongInput)
        viewModel.onCheckClick()

        // Advance time past the feedback delay
        advanceTimeBy(LessonViewModel.FEEDBACK_DELAY + 100)

        val finalState = viewModel.uiState.first()
        assertEquals(AnswerState.NEUTRAL, finalState.answerState)
        assertEquals(1, finalState.questionCount) // Count stays same
        assertEquals("", finalState.currentInput) // Input cleared
        assertTrue(finalState.replayTrigger > 0) // Replay triggered
    }
}
