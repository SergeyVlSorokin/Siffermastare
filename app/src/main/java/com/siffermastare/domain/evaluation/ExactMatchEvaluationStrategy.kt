package com.siffermastare.domain.evaluation

import com.siffermastare.domain.models.Question

/**
 * Basic strategy that requires the input to exactly match the target value.
 * Currently uses standard string equality.
 */
class ExactMatchEvaluationStrategy : EvaluationStrategy {

    override fun evaluate(input: String, question: Question): EvaluationResult {
        // Simple exact match against the target value
        val isCorrect = input == question.targetValue
        
        return EvaluationResult(
            isCorrect = isCorrect
            // No specific atom updates for now, could be added later if needed
        )
    }
}
