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

import com.siffermastare.data.database.LessonResult
import com.siffermastare.data.repository.LessonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalCoroutinesApi::class)
class LessonViewModelTest {

    private lateinit var viewModel: LessonViewModel
    private val fakeRepository = FakeLessonRepository()

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = LessonViewModel(fakeRepository)
        viewModel.loadLesson("cardinal_0_20")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_isQuestionOne_Neutral() = runTest {
        val state = viewModel.uiState.first()

        assertEquals(1, state.questionCount)
        assertEquals(AnswerState.NEUTRAL, state.answerState)
        assertEquals("", state.currentInput)
    }

    @Test
    fun correctAnswer_setsCorrectState_thenNeutral() = runTest {
        val initialState = viewModel.uiState.first()
        val target = initialState.targetNumber

        viewModel.onDigitClick(target.toInt())
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
        val initialState = viewModel.uiState.first()
        val target = initialState.targetNumber
        
        val wrongInput = if (target == "0") 1 else 0
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
    @Test
    fun lessonComplete_savesStatsToRepository() = runTest {
        // Play through 10 questions
        
        repeat(9) {
            val state = viewModel.uiState.first()
            val target = state.targetNumber
            
            // Correct Answer
            viewModel.onDigitClick(target.toInt())
            viewModel.onCheckClick()
            advanceTimeBy(LessonViewModel.FEEDBACK_DELAY + 100)
        }
        
        // 10th Question
        val state10 = viewModel.uiState.first()
        val target10 = state10.targetNumber
        val wrong = if (target10 == "0") 1 else 0
        
        // Wrong attempt
        viewModel.onDigitClick(wrong)
        viewModel.onCheckClick()
        advanceTimeBy(LessonViewModel.FEEDBACK_DELAY + 100)
        
        // Correct attempt
        viewModel.onDigitClick(target10.toInt())
        viewModel.onCheckClick()
        advanceTimeBy(LessonViewModel.FEEDBACK_DELAY + 100)
        
        // Check Saved Result
        val result = fakeRepository.lastInsertedResult
        
        // Assertions
        val finalState = viewModel.uiState.first()
        assertEquals("Lesson should be complete", true, finalState.isLessonComplete)
        
        val expectedAcc = (10f / 10f) * 100f
        assertEquals(expectedAcc, result?.accuracy ?: 0f, 0.1f)
        
        assertTrue((result?.averageSpeed ?: -1L) >= 0)
    }
    @Test
    fun slowReplay_setsSlowRate_andTriggersReplay() = runTest {
        val initialState = viewModel.uiState.first()
        
        viewModel.onSlowReplay()
        
        val state = viewModel.uiState.value
        assertEquals(0.7f, state.ttsRate, 0.0f)
        assertEquals(1, state.replayTrigger)
    }

    @Test
    fun nextQuestion_resetsRate_toNormal() = runTest {
        viewModel.onSlowReplay()
        // Simulate correct answer to advance
        viewModel.onDigitClick(1) // Target is 1
        viewModel.onCheckClick()
        advanceTimeBy(LessonViewModel.FEEDBACK_DELAY + 100)
        
        val state = viewModel.uiState.value
        assertEquals(1.0f, state.ttsRate, 0.0f)
    }

    @Test
    fun incorrectAnswer_incrementsAttempts() = runTest {
        // Attempt 1
        viewModel.onDigitClick(9) // Wrong
        viewModel.onCheckClick()
        advanceTimeBy(LessonViewModel.FEEDBACK_DELAY + 100)
        assertEquals(1, viewModel.uiState.value.incorrectAttempts)

        // Attempt 2
        viewModel.onDigitClick(9)
        viewModel.onCheckClick()
        advanceTimeBy(LessonViewModel.FEEDBACK_DELAY + 100)
        assertEquals(2, viewModel.uiState.value.incorrectAttempts)
    }

    @Test
    fun correctAnswer_resetsAttempts() = runTest {
        // Fail once
        val target = viewModel.uiState.value.targetNumber
        val wrong = if (target == "0") 1 else 0
        
        viewModel.onDigitClick(wrong)
        viewModel.onCheckClick()
        advanceTimeBy(LessonViewModel.FEEDBACK_DELAY + 100)
        assertEquals(1, viewModel.uiState.value.incorrectAttempts)

        // Succeed
        val targetStr = target.toString()
        targetStr.forEach { 
             viewModel.onDigitClick(it.digitToInt())
        }
        viewModel.onCheckClick()
        advanceTimeBy(LessonViewModel.FEEDBACK_DELAY + 100)
        
        // Assert reset (on next question generally, but state should update)
        assertEquals(0, viewModel.uiState.value.incorrectAttempts)
    }

    @Test
    fun onGiveUp_setsRevealedState_andFillsInput() = runTest {
        val target = viewModel.uiState.value.targetNumber
        viewModel.onGiveUp()
        
        val state = viewModel.uiState.value
        assertEquals(AnswerState.REVEALED, state.answerState)
        assertEquals(target.toString(), state.currentInput)
    }
    
    @Test
    fun onCheck_inRevealedState_advancesQuestion() = runTest {
        viewModel.onGiveUp()
        assertEquals(AnswerState.REVEALED, viewModel.uiState.value.answerState)
        
        // Click Check (which acts as Next)
        viewModel.onCheckClick()
        advanceTimeBy(LessonViewModel.FEEDBACK_DELAY + 100)
        
        // Should be new question (Question 2)
        assertEquals(2, viewModel.uiState.value.questionCount)
        assertEquals(AnswerState.NEUTRAL, viewModel.uiState.value.answerState)
        assertEquals(0, viewModel.uiState.value.incorrectAttempts) // Reset
    }
}

class FakeLessonRepository : LessonRepository {
    var lastInsertedResult: LessonResult? = null

    override suspend fun insertLessonResult(result: LessonResult) {
        lastInsertedResult = result
    }

    override fun getAllLessonResults(): Flow<List<LessonResult>> {
        return flowOf(emptyList())
    }

    override fun getLessonCount(): Flow<Int> {
        return flowOf(0)
    }

    override fun getAllTimestamps(): Flow<List<Long>> {
        return flowOf(emptyList())
    }
}
