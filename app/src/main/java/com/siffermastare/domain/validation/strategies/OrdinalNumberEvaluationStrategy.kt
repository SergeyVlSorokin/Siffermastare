package com.siffermastare.domain.validation.strategies

import com.siffermastare.domain.evaluation.BagLogicHelper
import com.siffermastare.domain.evaluation.EvaluationResult
import com.siffermastare.domain.evaluation.EvaluationStrategy
import com.siffermastare.domain.models.Question

/**
 * Strategy for ordinal numbers.
 * Uses question.atoms as the target atom list (Generator Owns Atoms rule).
 * Decomposes user INPUT into ord: prefixed atoms for bag-logic comparison.
 */
class OrdinalNumberEvaluationStrategy : EvaluationStrategy {

    override fun evaluate(input: String, question: Question): EvaluationResult {
        val targetStr = question.targetValue
        val target = targetStr.toIntOrNull() ?: return EvaluationResult(false) // Should be valid integer
        val inputInt = input.trim().toIntOrNull()

        val targetAtoms = question.atoms

        // If input is non-numeric, fail all target atoms
        if (inputInt == null) {
            return EvaluationResult(false, BagLogicHelper.failAll(targetAtoms))
        }

        // Decompose input using the "ord:" prefix
        val inputAtoms = StandardNumberEvaluationStrategy.decompose(inputInt, "ord:")

        // Bag Logic comparison
        val updates = BagLogicHelper.compare(targetAtoms, inputAtoms)

        // Correct only if numeric values are equal
        val isCorrect = inputInt == target
        
        return EvaluationResult(isCorrect, updates)
    }
}
