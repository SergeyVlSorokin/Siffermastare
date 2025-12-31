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
    INCORRECT,
    REVEALED
}

data class LessonUiState(
    val targetNumber: String = "",
    val spokenText: String = "",
    val currentInput: String = "",
    val questionCount: Int = 1,
    val totalQuestions: Int = 10,
    val isLessonComplete: Boolean = false,
    val answerState: AnswerState = AnswerState.NEUTRAL,
    val replayTrigger: Int = 0, // Increments to trigger replay
    val ttsRate: Float = 1.0f, // New: Speech Rate
    val incorrectAttempts: Int = 0,
    val lessonId: String = ""
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
    private var correctAnswers: Int = 0
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
        correctAnswers = 0
        totalTimeMs = 0L
        finalAccuracy = 0f
        finalAvgSpeed = 0L
    }

    private fun updateUiState(sessionState: LessonState) {
        val wasComplete = _uiState.value.isLessonComplete

        _uiState.update {
            it.copy(
                targetNumber = sessionState.currentQuestion?.targetValue ?: "",
                spokenText = sessionState.currentQuestion?.spokenText ?: "",
                // If question changed, reset input? 
                // Manager handles nextQuestion, so we just reflect state
                questionCount = sessionState.questionCount,
                totalQuestions = sessionState.totalQuestions,
                isLessonComplete = sessionState.isLessonComplete,
                // New question? Reset rate to 1.0f if neutral?
                // Logic: If transitioning to neutral/new question, reset rate.
                // We'll trust onCheckClick to do it, OR do it here if checking.
                // Safest: When questionCount changes, reset rate.
                lessonId = currentLessonId
            )
        }
        
        // Handle Lesson Completion Trigger
        if (sessionState.isLessonComplete && !wasComplete) {
             viewModelScope.launch { calculateAndSaveResults() }
        }
    }

    fun onDigitClick(digit: Int) {
        if (_uiState.value.answerState != AnswerState.NEUTRAL) return
        if (_uiState.value.currentInput.length >= 8) return 
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

    fun onGiveUp() {
        val target = _uiState.value.targetNumber
        _uiState.update {
            it.copy(
                answerState = AnswerState.REVEALED,
                currentInput = target
            )
        }
    }

    fun onSlowReplay() {
        // Trigger replay with slow rate
        _uiState.update { 
            it.copy(
                ttsRate = 0.7f,
                replayTrigger = it.replayTrigger + 1
            ) 
        }
    }

    fun onCheckClick() {
        // Handle Revealed State (behaves as Next)
        if (_uiState.value.answerState == AnswerState.REVEALED) {
            _uiState.update { 
                it.copy(
                    answerState = AnswerState.NEUTRAL, 
                    currentInput = "", 
                    ttsRate = 1.0f, 
                    incorrectAttempts = 0
                ) 
            }
            sessionManager.nextQuestion()
            startTime = System.currentTimeMillis()
            return
        }

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
                 correctAnswers++
                 
                _uiState.update { it.copy(answerState = AnswerState.CORRECT) }
                delay(FEEDBACK_DELAY)
                // RESET RATE HERE
                _uiState.update { 
                    it.copy(
                        answerState = AnswerState.NEUTRAL, 
                        currentInput = "", 
                        ttsRate = 1.0f,
                        incorrectAttempts = 0 
                    ) 
                }
                
                sessionManager.nextQuestion()
                startTime = System.currentTimeMillis()
            } else {
                _uiState.update { it.copy(answerState = AnswerState.INCORRECT) }
                delay(FEEDBACK_DELAY)
                // Incorrect answer replay - keep rate? Or reset?
                // Standard replay usually normal speed. 
                // Story doesn't specify. Assuming normal speed on auto-replay.
                _uiState.update { 
                    it.copy(
                        answerState = AnswerState.NEUTRAL, 
                        currentInput = "", 
                        replayTrigger = it.replayTrigger + 1, 
                        ttsRate = 1.0f,
                        incorrectAttempts = it.incorrectAttempts + 1
                    ) 
                }
            }
        }
    }

    private suspend fun calculateAndSaveResults() {
       val totalQuestions = sessionManager.lessonState.value.totalQuestions
        
        finalAccuracy = if (totalQuestions > 0) {
            (correctAnswers.toFloat() / totalQuestions.toFloat()) * 100f
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
