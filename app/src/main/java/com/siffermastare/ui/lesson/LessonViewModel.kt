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

    init {
        generateNewNumber()
    }

    private fun generateNewNumber() {
        _uiState.update { it.copy(targetNumber = Random.nextInt(0, 10)) }
    }

    fun onDigitClick(digit: Int) {
        // Only allow typing if we are in NEUTRAL state (not currently showing feedback animation)
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
        // Prevent multiple checks
        if (_uiState.value.answerState != AnswerState.NEUTRAL) return

        val currentState = _uiState.value
        if (currentState.currentInput.isEmpty()) {
            return
        }

        val userNumber = currentState.currentInput.toIntOrNull()
        
        viewModelScope.launch {
            if (userNumber == currentState.targetNumber) {
                // Correct Flow
                _uiState.update { it.copy(answerState = AnswerState.CORRECT) }
                
                delay(FEEDBACK_DELAY) // Wait for visual cue
                
                if (currentState.questionCount >= currentState.totalQuestions) {
                    _uiState.update {
                        it.copy(
                            isLessonComplete = true,
                            answerState = AnswerState.NEUTRAL // Reset for next time if any
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            answerState = AnswerState.NEUTRAL,
                            currentInput = "",
                            questionCount = it.questionCount + 1,
                            targetNumber = Random.nextInt(0, 10)
                        )
                    }
                }
            } else {
                // Incorrect Flow
                _uiState.update { it.copy(answerState = AnswerState.INCORRECT) }
                
                delay(FEEDBACK_DELAY) // Wait for shake
                
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

    companion object {
        const val FEEDBACK_DELAY = 500L
    }
}
