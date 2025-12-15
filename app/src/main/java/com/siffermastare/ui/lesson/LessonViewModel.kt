package com.siffermastare.ui.lesson

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

data class LessonUiState(
    val targetNumber: Int = 0,
    val currentInput: String = "",
    val questionCount: Int = 1,
    val totalQuestions: Int = 10,
    val isLessonComplete: Boolean = false,
    val feedbackMessage: String? = null
)

class LessonViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LessonUiState())
    val uiState: StateFlow<LessonUiState> = _uiState.asStateFlow()

    init {
        generateNewNumber()
    }

    private fun generateNewNumber() {
        _uiState.update { it.copy(targetNumber = Random.nextInt(0, 10)) }
    }

    fun onDigitClick(digit: Int) {
        _uiState.update { it.copy(currentInput = it.currentInput + digit) }
    }

    fun onBackspaceClick() {
        _uiState.update {
            if (it.currentInput.isNotEmpty()) {
                it.copy(currentInput = it.currentInput.dropLast(1))
            } else {
                it
            }
        }
    }

    fun onCheckClick() {
        val currentState = _uiState.value
        if (currentState.currentInput.isEmpty()) {
            _uiState.update { it.copy(feedbackMessage = "Please enter a number") }
            return
        }

        val userNumber = currentState.currentInput.toIntOrNull()
        if (userNumber == currentState.targetNumber) {
            // Correct
            if (currentState.questionCount >= currentState.totalQuestions) {
                // Lesson Complete
                _uiState.update {
                    it.copy(
                        feedbackMessage = "Correct!",
                        isLessonComplete = true
                    )
                }
            } else {
                // Next Question
                _uiState.update {
                    it.copy(
                        feedbackMessage = "Correct!",
                        currentInput = "",
                        questionCount = it.questionCount + 1,
                        targetNumber = Random.nextInt(0, 10) // Directly generating here to avoid double update if I reused generateNewNumber logic blindly
                    )
                }
            }
        } else {
            // Incorrect
            _uiState.update { it.copy(feedbackMessage = "Try Again") }
        }
    }

    fun messageShown() {
        _uiState.update { it.copy(feedbackMessage = null) }
    }
}
