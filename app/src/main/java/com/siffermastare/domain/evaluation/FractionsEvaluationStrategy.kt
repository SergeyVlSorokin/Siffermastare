package com.siffermastare.domain.evaluation

import com.siffermastare.domain.models.Question

/**
 * Strategy for evaluating fraction input (e.g., "3/4").
 *
 * Fractions use two-atom decomposition with mixed atom types:
 * - Numerator → cardinal atom (e.g., "3")
 * - Denominator → ordinal atom with "ord:" prefix (e.g., "ord:4")
 *
 * Uses question.atoms as ground truth (Generator Owns Atoms rule).
 */
class FractionsEvaluationStrategy : EvaluationStrategy {

    override fun evaluate(input: String, question: Question): EvaluationResult {
        val targetAtoms = question.atoms
        val trimmed = input.trim()

        // Split on "/" — must produce exactly 2 parts
        val parts = trimmed.split("/")
        if (parts.size != 2) {
            return EvaluationResult(false, BagLogicHelper.failAll(targetAtoms))
        }

        val numStr = parts[0]
        val denStr = parts[1]

        // Both parts must be valid integers
        if (numStr.toIntOrNull() == null || denStr.toIntOrNull() == null) {
            return EvaluationResult(false, BagLogicHelper.failAll(targetAtoms))
        }

        // Decompose input into atoms: [cardinal numerator, ordinal denominator]
        val inputAtoms = listOf(numStr, "ord:$denStr")

        // Bag-logic comparison
        val updates = BagLogicHelper.compare(targetAtoms, inputAtoms)

        // Correct only if exact string match
        val isCorrect = trimmed == question.targetValue

        return EvaluationResult(isCorrect, updates)
    }
}
