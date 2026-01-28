package com.siffermastare.domain.validation.strategies

import com.siffermastare.domain.evaluation.EvaluationResult
import com.siffermastare.domain.evaluation.EvaluationStrategy
import com.siffermastare.domain.models.Question
import kotlin.math.min

/**
 * Strategy for standard integer numbers (0-1000).
 * Decomposes numbers into "speaking parts" (Atoms) to provide granular feedback.
 */
class StandardNumberEvaluationStrategy : EvaluationStrategy {

    override fun evaluate(input: String, question: Question): EvaluationResult {
        val targetStr = question.targetValue
        val target = targetStr.toIntOrNull() ?: return EvaluationResult(false) // Should be valid integer
        val inputInt = input.trim().toIntOrNull()

        val targetAtoms = decompose(target)

        // If input is non-numeric, fail all target atoms
        if (inputInt == null) {
            val updates = targetAtoms.distinct().associateWith { atom ->
                List(targetAtoms.count { it == atom }) { false }
            }
            return EvaluationResult(false, updates)
        }

        val inputAtoms = decompose(inputInt)

        // Bag Logic comparison
        val updates = mutableMapOf<String, List<Boolean>>()
        val targetCounts = targetAtoms.groupingBy { it }.eachCount()
        val inputCounts = inputAtoms.groupingBy { it }.eachCount()

        targetCounts.forEach { (atom, required) ->
            val provided = inputCounts[atom] ?: 0
            val matches = min(required, provided)
            val misses = required - matches
            
            val atomResults = mutableListOf<Boolean>()
            repeat(matches) { atomResults.add(true) }
            repeat(misses) { atomResults.add(false) }
            updates[atom] = atomResults
        }

        // Correct only if numeric values are equal
        val isCorrect = inputInt == target
        
        return EvaluationResult(isCorrect, updates)
    }

    /**
     * Decomposes a number into its atomic "speaking parts".
     * Ranges:
     * - 0-19: Atomic
     * - 20-99: Tens + Digit
     * - 100-999: HundredsDigit + Remainder (Tens/Digits)
     * - 1000: "1" (ett tusen)
     */
    private fun decompose(number: Int): List<String> {
        if (number !in 0..1000) return emptyList()

        val atoms = mutableListOf<String>()

        if (number == 1000) {
            atoms.add("1")
            return atoms
        }
        
        if (number == 0) {
            atoms.add("0")
            return atoms
        }

        var remaining = number
        val hundreds = remaining / 100
        if (hundreds > 0) {
            atoms.add(hundreds.toString())
            remaining %= 100
        }

        if (remaining > 0) {
            if (remaining < 20) {
                atoms.add(remaining.toString())
            } else {
                val tens = (remaining / 10) * 10
                atoms.add(tens.toString())
                
                val ones = remaining % 10
                if (ones > 0) {
                    atoms.add(ones.toString())
                }
            }
        }
        
        return atoms
    }
}
