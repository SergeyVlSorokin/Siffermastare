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
    @Test
    fun lessonComplete_savesStatsToRepository() = runTest {
        // Play through 10 questions
        // Scenario: 9 correct on first try, 1 correct on second try (1 error)
        // Total attempts = 11.
        // Accuracy = 10/11 = ~90.9%
        
        repeat(9) {
            val state = viewModel.uiState.first()
            val target = state.targetNumber
            
            // Correct Answer
            viewModel.onDigitClick(target)
            viewModel.onCheckClick()
            advanceTimeBy(LessonViewModel.FEEDBACK_DELAY + 100)
        }
        
        // 10th Question: Make a mistake first
        val state10 = viewModel.uiState.first()
        val target10 = state10.targetNumber
        val wrong = if (target10 == 0) 1 else 0
        
        // Wrong attempt
        viewModel.onDigitClick(wrong)
        viewModel.onCheckClick()
        advanceTimeBy(LessonViewModel.FEEDBACK_DELAY + 100)
        
        // Correct attempt
        val state10Retry = viewModel.uiState.first()
        // Wait, input is cleared, state is neutral.
        viewModel.onDigitClick(target10)
        viewModel.onCheckClick()
        advanceTimeBy(LessonViewModel.FEEDBACK_DELAY + 100)
        
        // Check Saved Result
        val result = fakeRepository.lastInsertedResult
        
        // Assertions
        val finalState = viewModel.uiState.first()
        assertEquals(true, finalState.isLessonComplete)
        
        // Verify stats in Repository
        // Accuracy: 10 questions / 11 attempts = 0.909090... -> 90.9%
        val expectedAcc = (10f / 11f) * 100f
        assertEquals(expectedAcc, result?.accuracy ?: 0f, 0.1f)
        
        // Speed: Hard to assert exact time with runTest and System.currentTimeMillis() dependency.
        // But we can assert it's sane (>= 0).
        assertTrue((result?.averageSpeed ?: -1L) >= 0)
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
}
