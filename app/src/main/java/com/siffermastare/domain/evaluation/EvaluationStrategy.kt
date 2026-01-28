package com.siffermastare.domain.evaluation

import com.siffermastare.domain.models.Question

/**
 * Strategy interface for evaluating user input against a Question.
 */
interface EvaluationStrategy {
    /**
     * Evaluates the user's input.
     *
     * @param input The user's raw input string.
     * @param question The current Question context.
     * @return EvaluationResult containing correctness and atom updates.
     */
    fun evaluate(input: String, question: Question): EvaluationResult
}
