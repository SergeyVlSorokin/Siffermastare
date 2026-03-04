package com.siffermastare.domain

import com.siffermastare.domain.engine.KnowledgeEngine
import com.siffermastare.domain.evaluation.EvaluationResult
import com.siffermastare.domain.generators.NumberGenerator
import com.siffermastare.domain.models.Question
import com.siffermastare.util.TimeProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LessonState(
    val currentQuestion: Question? = null,
    val questionCount: Int = 0,
    val totalQuestions: Int = 10,
    val mistakeCount: Int = 0,
    val isLessonComplete: Boolean = false,
    val questionStartTime: Long = 0L
)

class LessonSessionManager(
    private val knowledgeEngine: KnowledgeEngine? = null,
    private val timeProvider: TimeProvider
) {

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
                isLessonComplete = false,
                questionStartTime = timeProvider.currentTimeMillis()
            )
        }
    }

    fun submitAnswer(input: String, validator: ((String, String) -> Boolean)? = null, scope: CoroutineScope? = null): Boolean {
        val state = _lessonState.value
        val currentQ = state.currentQuestion ?: return false
        val startTime = state.questionStartTime
        val elapsedMs = if (startTime > 0) timeProvider.currentTimeMillis() - startTime else 0L

        fun processAsync(isCorrect: Boolean, evaluationResult: EvaluationResult? = null) {
            if (!isCorrect) incrementMistake()
            
            if (scope != null && knowledgeEngine != null && evaluationResult != null) {
                scope.launch {
                    try {
                        knowledgeEngine.processEvaluation(
                            result = evaluationResult,
                            elapsedMs = elapsedMs,
                            targetLength = currentQ.targetValue.length
                        )
                    } catch (_: Exception) {
                        // Silently suppress DB persistence failures without blocking user flow
                    }
                }
            }
        }

        // Priority 1: Legacy Validator (if provided)
        if (validator != null) {
            val isCorrect = validator(input, currentQ.targetValue)
            val fakeResult = EvaluationResult(isCorrect, emptyMap())
            processAsync(isCorrect, fakeResult)
            return isCorrect
        }

        // Priority 2: Generator Strategy
        val strategy = currentGenerator?.evaluationStrategy
        if (strategy != null) {
            val result = strategy.evaluate(input, currentQ)
            processAsync(result.isCorrect, result)
            return result.isCorrect
        }

        // Priority 3: Fallback (Strict Equality)
        val isCorrect = input == currentQ.targetValue
        val fakeResult = EvaluationResult(isCorrect, emptyMap())
        processAsync(isCorrect, fakeResult)
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
                    questionStartTime = timeProvider.currentTimeMillis()
                )
            }
        } else {
            _lessonState.update { it.copy(isLessonComplete = true) }
        }
    }
}
