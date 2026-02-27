package com.siffermastare.domain.validation.strategies

import com.siffermastare.domain.evaluation.BagLogicHelper
import com.siffermastare.domain.evaluation.EvaluationResult
import com.siffermastare.domain.evaluation.EvaluationStrategy
import com.siffermastare.domain.models.Question

class DecimalsEvaluationStrategy : EvaluationStrategy {

    override fun evaluate(input: String, question: Question): EvaluationResult {
        val targetParts = question.targetValue.split(",")
        // If target is not properly formatted, fail gracefully
        if (targetParts.size != 2) return EvaluationResult(false, BagLogicHelper.failAll(question.atoms))

        val targetIntStr = targetParts[0]
        val targetDecStr = targetParts[1]

        val targetIntAtoms = StandardNumberEvaluationStrategy.decompose(targetIntStr.toIntOrNull() ?: 0)
        val targetDecAtoms = decomposeDecimal(targetDecStr)

        val inputParts = input.split(",")
        
        val inputIntStr = if (inputParts.isNotEmpty()) inputParts[0] else ""
        // If there's no comma, inputParts.size is 1, so inputDecStr = ""
        // If there are multiple commas, we just take the first part as int, the second as dec, 
        // which helps failing cleanly on "2,,5" (dec part is "")
        val inputDecStr = if (inputParts.size > 1) inputParts[1] else ""

        val inputIntNumber = inputIntStr.toIntOrNull()
        val inputIntAtoms = if (inputIntNumber != null) StandardNumberEvaluationStrategy.decompose(inputIntNumber) else emptyList()
        val inputDecAtoms = decomposeDecimal(inputDecStr)

        val intUpdates = BagLogicHelper.compare(targetIntAtoms, inputIntAtoms)
        val decUpdates = BagLogicHelper.compare(targetDecAtoms, inputDecAtoms)

        // Combine maps
        val combinedUpdates = mutableMapOf<String, MutableList<Boolean>>()
        
        intUpdates.forEach { (atom, list) ->
            combinedUpdates.getOrPut(atom) { mutableListOf() }.addAll(list)
        }
        decUpdates.forEach { (atom, list) ->
            combinedUpdates.getOrPut(atom) { mutableListOf() }.addAll(list)
        }

        val isCorrect = input.trim() == question.targetValue

        return EvaluationResult(isCorrect, combinedUpdates)
    }

    private fun decomposeDecimal(str: String): List<String> {
        val trimmed = str.trim()
        if (trimmed.isEmpty()) return emptyList()
        
        // Ensure it's purely decimal digits
        if (!trimmed.all { it.isDigit() }) return emptyList()

        val number = trimmed.toIntOrNull() ?: return emptyList()
        val atoms = mutableListOf<String>()

        var i = 0
        while (i < trimmed.length && trimmed[i] == '0') {
            atoms.addAll(StandardNumberEvaluationStrategy.decompose(0))
            i++
        }

        if (number > 0) {
            atoms.addAll(StandardNumberEvaluationStrategy.decompose(number))
        }

        return atoms
    }
}
