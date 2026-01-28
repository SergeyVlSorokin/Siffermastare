package com.siffermastare.domain

import com.siffermastare.domain.generators.NumberGenerator
import com.siffermastare.domain.models.Question
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LessonState(
    val currentQuestion: Question? = null,
    val questionCount: Int = 0,
    val totalQuestions: Int = 10,
    val mistakeCount: Int = 0,
    val isLessonComplete: Boolean = false
)

class LessonSessionManager {

    private val _lessonState = MutableStateFlow(LessonState())
    val lessonState: StateFlow<LessonState> = _lessonState.asStateFlow()

    private var questions: List<Question> = emptyList()
    private var currentIndex = 0
    private var currentGenerator: NumberGenerator? = null

    fun startLesson(generator: NumberGenerator) {
        currentGenerator = generator
        questions = generator.generateLesson()
        currentIndex = 0
        
        _lessonState.update {
            it.copy(
                currentQuestion = questions.getOrNull(currentIndex),
                questionCount = 1,
                totalQuestions = questions.size,
                mistakeCount = 0,
                isLessonComplete = false
            )
        }
    }

    fun submitAnswer(input: String, validator: ((String, String) -> Boolean)? = null): Boolean {
        val currentQ = _lessonState.value.currentQuestion ?: return false
        
        // Priority 1: Legacy Validator (if provided)
        if (validator != null) {
            val isCorrect = validator(input, currentQ.targetValue)
            if (!isCorrect) incrementMistake()
            return isCorrect
        }

        // Priority 2: Generator Strategy
        val strategy = currentGenerator?.evaluationStrategy
        if (strategy != null) {
            val result = strategy.evaluate(input, currentQ)
            if (!result.isCorrect) incrementMistake()
            return result.isCorrect
        }

        // Priority 3: Fallback (Strict Equality)
        val isCorrect = input == currentQ.targetValue
        if (!isCorrect) incrementMistake()
        return isCorrect
    }

    private fun incrementMistake() {
        _lessonState.update { it.copy(mistakeCount = it.mistakeCount + 1) }
    }

    fun nextQuestion() {
        currentIndex++
        if (currentIndex < questions.size) {
            _lessonState.update {
                it.copy(
                    currentQuestion = questions[currentIndex],
                    questionCount = currentIndex + 1,
                    // Mistake count persists for the session, or reset per question?
                    // PRD doesn't specify strictly, but keeping it cumulative is safer for now, 
                    // or we reset if we track per-question. 
                    // Based on Story 2.4, we track session accuracy.
                )
            }
        } else {
            _lessonState.update { it.copy(isLessonComplete = true) }
        }
    }
}
