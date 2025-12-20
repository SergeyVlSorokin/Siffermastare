package com.siffermastare.ui.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.siffermastare.data.repository.LessonRepository
import com.siffermastare.domain.LessonSessionManager
import com.siffermastare.domain.LessonState
import com.siffermastare.domain.generators.NumberGeneratorFactory

enum class AnswerState {
    NEUTRAL,
    CORRECT,
    INCORRECT
}

data class LessonUiState(
    val targetNumber: Int = 0,
    val spokenText: String = "",
    val currentInput: String = "",
    val questionCount: Int = 1,
    val totalQuestions: Int = 10,
    val isLessonComplete: Boolean = false,
    val answerState: AnswerState = AnswerState.NEUTRAL,
    val replayTrigger: Int = 0 // Increments to trigger replay
)



class LessonViewModel(
    private val repository: LessonRepository,
    private val sessionManager: LessonSessionManager = LessonSessionManager()
) : ViewModel() {

    // Helper mapping until we merge LessonUiState with LessonState
    private val _uiState = MutableStateFlow(LessonUiState())
    val uiState: StateFlow<LessonUiState> = _uiState.asStateFlow()

    // Metric Tracking
    private var startTime: Long = 0L
    private var totalTimeMs: Long = 0L
    private var totalAttempts: Int = 0
    private var currentLessonId: String = "0-10" // Default

    // Final Stats
    private var finalAccuracy: Float = 0f
    private var finalAvgSpeed: Long = 0L

    init {
        // Observe Manager State
        viewModelScope.launch {
            sessionManager.lessonState.collect { sessionState ->
                updateUiState(sessionState)
            }
        }
    }

    // Called from UI/Navigation to start specific lesson
    fun loadLesson(lessonId: String) {
        currentLessonId = lessonId
        val generator = com.siffermastare.domain.generators.NumberGeneratorFactory.create(lessonId)
        sessionManager.startLesson(generator)
        startTime = System.currentTimeMillis()
        totalAttempts = 0
        totalTimeMs = 0L
        finalAccuracy = 0f
        finalAvgSpeed = 0L
    }

    private fun updateUiState(sessionState: LessonState) {
        val wasComplete = _uiState.value.isLessonComplete

        _uiState.update {
            it.copy(
                targetNumber = sessionState.currentQuestion?.targetValue?.toIntOrNull() ?: 0,
                spokenText = sessionState.currentQuestion?.spokenText ?: "",
                // If question changed, reset input? 
                // Manager handles nextQuestion, so we just reflect state
                questionCount = sessionState.questionCount,
                totalQuestions = sessionState.totalQuestions,
                isLessonComplete = sessionState.isLessonComplete,
                // answerState: We still manage UI feedback delay here or in Manager?
                // For MVP 3.1b Refactor, we'll keep AnswerState local but driven by Manager checks.
            )
        }
        
        // Handle Lesson Completion Trigger
        if (sessionState.isLessonComplete && !wasComplete) {
             viewModelScope.launch { calculateAndSaveResults() }
        }
    }

    fun onDigitClick(digit: Int) {
        if (_uiState.value.answerState != AnswerState.NEUTRAL) return
        _uiState.update { it.copy(currentInput = it.currentInput + digit) }
    }

    fun onBackspaceClick() {
        if (_uiState.value.answerState != AnswerState.NEUTRAL) return
        _uiState.update {
            if (it.currentInput.isNotEmpty()) {
                it.copy(currentInput = it.currentInput.dropLast(1))
            } else { it }
        }
    }

    fun onCheckClick() {
        if (_uiState.value.answerState != AnswerState.NEUTRAL) return
        val currentInput = _uiState.value.currentInput
        if (currentInput.isEmpty()) return

        totalAttempts++
        
        // Delegate verification to Manager
        val isCorrect = sessionManager.submitAnswer(currentInput)
        
        viewModelScope.launch {
            if (isCorrect) {
                 val endTime = System.currentTimeMillis()
                 totalTimeMs += (endTime - startTime)
                 
                _uiState.update { it.copy(answerState = AnswerState.CORRECT) }
                delay(FEEDBACK_DELAY)
                _uiState.update { it.copy(answerState = AnswerState.NEUTRAL, currentInput = "") }
                
                sessionManager.nextQuestion()
                startTime = System.currentTimeMillis()
            } else {
                _uiState.update { it.copy(answerState = AnswerState.INCORRECT) }
                delay(FEEDBACK_DELAY)
                _uiState.update { it.copy(answerState = AnswerState.NEUTRAL, currentInput = "", replayTrigger = it.replayTrigger + 1) }
            }
        }
    }

    private suspend fun calculateAndSaveResults() {
       val totalQuestions = sessionManager.lessonState.value.totalQuestions
        
        finalAccuracy = if (totalAttempts > 0) {
            (totalQuestions.toFloat() / totalAttempts.toFloat()) * 100f
        } else { 0f }
        
        finalAvgSpeed = if (totalQuestions > 0) {
            totalTimeMs / totalQuestions
        } else { 0L }

        val result = com.siffermastare.data.database.LessonResult(
            accuracy = finalAccuracy,
            averageSpeed = finalAvgSpeed,
            lessonType = currentLessonId
        )
        repository.insertLessonResult(result)
    }

    // Expose generated stats for the View
    fun getFinalStats(): Pair<Float, Long> = Pair(finalAccuracy, finalAvgSpeed)

    companion object {
        const val FEEDBACK_DELAY = 500L
    }
}
