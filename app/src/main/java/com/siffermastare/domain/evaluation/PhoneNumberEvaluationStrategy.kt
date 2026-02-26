package com.siffermastare.domain.evaluation

import com.siffermastare.domain.models.Question

/**
 * Strategy for evaluating phone number input with hybrid atom decomposition.
 *
 * Phone numbers use position-dependent decomposition:
 * - Prefix (chars 0-2) + Group1 (chars 3-5): individual digit atoms
 * - Pair1 (chars 6-7) + Pair2 (chars 8-9): standard number decomposition
 *   - Exception: pairs < 10 → individual digit atoms ["0", "X"]
 *
 * Uses question.atoms as ground truth (Generator Owns Atoms rule).
 */
class PhoneNumberEvaluationStrategy : EvaluationStrategy {

    override fun evaluate(input: String, question: Question): EvaluationResult {
        val targetAtoms = question.atoms
        val trimmed = input.trim()

        // Non-numeric input → fail all target atoms
        if (!trimmed.all { it.isDigit() }) {
            return EvaluationResult(false, BagLogicHelper.failAll(targetAtoms))
        }

        // Length mismatch → incorrect, not graded (no atom updates)
        if (trimmed.length != 10) {
            return EvaluationResult(false)
        }

        // Decompose input using same hybrid rules as generator
        val inputAtoms = PhoneNumberDecomposer.decompose(trimmed)

        // Bag-logic comparison
        val updates = BagLogicHelper.compare(targetAtoms, inputAtoms)

        // Correct only if raw string matches exactly
        val isCorrect = trimmed == question.targetValue

        return EvaluationResult(isCorrect, updates)
    }
}
