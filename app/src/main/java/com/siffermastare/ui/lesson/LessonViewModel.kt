package com.siffermastare.ui.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random
import com.siffermastare.data.repository.LessonRepository

enum class AnswerState {
    NEUTRAL,
    CORRECT,
    INCORRECT
}

data class LessonUiState(
    val targetNumber: Int = 0,
    val currentInput: String = "",
    val questionCount: Int = 1,
    val totalQuestions: Int = 10,
    val isLessonComplete: Boolean = false,
    val answerState: AnswerState = AnswerState.NEUTRAL,
    val replayTrigger: Int = 0 // Increments to trigger replay
)



class LessonViewModel(private val repository: LessonRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LessonUiState())
    val uiState: StateFlow<LessonUiState> = _uiState.asStateFlow()

    // Metric Tracking
    private var startTime: Long = 0L
    private var totalTimeMs: Long = 0L
    private var totalAttempts: Int = 0

    // Final Stats
    private var finalAccuracy: Float = 0f
    private var finalAvgSpeed: Long = 0L

    init {
        generateNewNumber()
    }

    private fun generateNewNumber() {
        _uiState.update { it.copy(targetNumber = Random.nextInt(0, 10)) }
        startTime = System.currentTimeMillis() // Start timer for this question
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
            } else {
                it
            }
        }
    }

    fun onCheckClick() {
        if (_uiState.value.answerState != AnswerState.NEUTRAL) return

        val currentState = _uiState.value
        if (currentState.currentInput.isEmpty()) return

        val userNumber = currentState.currentInput.toIntOrNull()
        
        // Count attempt
        totalAttempts++
        
        viewModelScope.launch {
            if (userNumber == currentState.targetNumber) {
                // Correct Logic
                val endTime = System.currentTimeMillis()
                totalTimeMs += (endTime - startTime)
                
                _uiState.update { it.copy(answerState = AnswerState.CORRECT) }
                delay(FEEDBACK_DELAY)

                if (currentState.questionCount >= currentState.totalQuestions) {
                    calculateAndSaveResults()
                } else {
                    _uiState.update {
                        it.copy(
                            answerState = AnswerState.NEUTRAL,
                            currentInput = "",
                            questionCount = it.questionCount + 1,
                            targetNumber = Random.nextInt(0, 10)
                        )
                    }
                    startTime = System.currentTimeMillis() // Reset timer for next question
                }
            } else {
                // Incorrect Logic
                _uiState.update { it.copy(answerState = AnswerState.INCORRECT) }
                delay(FEEDBACK_DELAY)
                
                _uiState.update {
                    it.copy(
                        answerState = AnswerState.NEUTRAL,
                        currentInput = "",
                        replayTrigger = it.replayTrigger + 1
                    )
                }
            }
        }
    }

    private suspend fun calculateAndSaveResults() {
        val totalQuestions = _uiState.value.totalQuestions
        
        // Calculate Metrics
        finalAccuracy = if (totalAttempts > 0) {
            (totalQuestions.toFloat() / totalAttempts.toFloat()) * 100f
        } else {
            0f
        }
        
        finalAvgSpeed = if (totalQuestions > 0) {
            totalTimeMs / totalQuestions
        } else {
            0L
        }

        // Save to DB
        // TODO: Import LessonResult properly. Assuming it's available or need import.
        val result = com.siffermastare.data.database.LessonResult(
            accuracy = finalAccuracy,
            averageSpeed = finalAvgSpeed,
            lessonType = "0-10"
        )
        repository.insertLessonResult(result)

        // Complete Lesson
        _uiState.update {
            it.copy(
                isLessonComplete = true,
                answerState = AnswerState.NEUTRAL
            )
        }
    }
    
    // Expose generated stats for the View
    fun getFinalStats(): Pair<Float, Long> = Pair(finalAccuracy, finalAvgSpeed)

    companion object {
        const val FEEDBACK_DELAY = 500L
    }
}
