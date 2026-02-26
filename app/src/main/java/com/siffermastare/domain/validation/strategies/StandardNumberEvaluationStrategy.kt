package com.siffermastare.domain.validation.strategies

import com.siffermastare.domain.evaluation.BagLogicHelper
import com.siffermastare.domain.evaluation.EvaluationResult
import com.siffermastare.domain.evaluation.EvaluationStrategy
import com.siffermastare.domain.models.Question

/**
 * Strategy for standard integer numbers (0-1000).
 * Uses question.atoms as the target atom list (Generator Owns Atoms rule).
 * Decomposes user INPUT into atoms for bag-logic comparison.
 */
class StandardNumberEvaluationStrategy : EvaluationStrategy {

    override fun evaluate(input: String, question: Question): EvaluationResult {
        val targetStr = question.targetValue
        val target = targetStr.toIntOrNull() ?: return EvaluationResult(false) // Should be valid integer
        val inputInt = input.trim().toIntOrNull()

        val targetAtoms = question.atoms

        // If input is non-numeric, fail all target atoms
        if (inputInt == null) {
            return EvaluationResult(false, BagLogicHelper.failAll(targetAtoms))
        }

        val inputAtoms = decompose(inputInt)

        // Bag Logic comparison
        val updates = BagLogicHelper.compare(targetAtoms, inputAtoms)

        // Correct only if numeric values are equal
        val isCorrect = inputInt == target
        
        return EvaluationResult(isCorrect, updates)
    }

    companion object {
        /**
         * Decomposes a number into its atomic "speaking parts".
         * Ranges:
         * - 0-19: Atomic
         * - 20-99: Tens + Digit
         * - 100-999: HundredsDigit + Remainder (Tens/Digits)
         * - 1000: "1" (ett tusen)
         *
         * @param number The integer to decompose (0-1000).
         * @param prefix Optional prefix for atom IDs (e.g., "ord:" for ordinals).
         * @return List of atom ID strings.
         */
        fun decompose(number: Int, prefix: String = ""): List<String> {
            if (number !in 0..1000) return emptyList()

            val atoms = mutableListOf<String>()

            if (number == 1000) {
                atoms.add("${prefix}1")
                return atoms
            }
            
            if (number == 0) {
                atoms.add("${prefix}0")
                return atoms
            }

            var remaining = number
            val hundreds = remaining / 100
            if (hundreds > 0) {
                atoms.add("${prefix}$hundreds")
                remaining %= 100
            }

            if (remaining > 0) {
                if (remaining < 20) {
                    atoms.add("${prefix}$remaining")
                } else {
                    val tens = (remaining / 10) * 10
                    atoms.add("${prefix}$tens")
                    
                    val ones = remaining % 10
                    if (ones > 0) {
                        atoms.add("${prefix}$ones")
                    }
                }
            }
            
            return atoms
        }
    }
}
