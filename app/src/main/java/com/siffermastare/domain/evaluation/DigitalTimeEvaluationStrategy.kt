package com.siffermastare.domain.evaluation

import com.siffermastare.domain.generators.TimeGenerator
import com.siffermastare.domain.models.Question

/**
 * Strictly evaluates Digital Time questions (e.g. "1400").
 * Uses question.atoms as the target atom list (Generator Owns Atoms rule).
 * Decomposes user INPUT into atoms and compares against target atoms.
 * Enforces ORDER of atoms within each part (Hours, Minutes).
 */
class DigitalTimeEvaluationStrategy : EvaluationStrategy {

    override fun evaluate(input: String, question: Question): EvaluationResult {
        // 1. Strict Validation: Input must be digits only
        if (!input.all { it.isDigit() }) {
            return EvaluationResult(isCorrect = false)
        }

        // 2. Parse Target
        val target = question.targetValue // e.g., "1430"
        val targetAtoms = question.atoms
        
        // 3. Normalize Input (handle 3-digit case "513" -> "0513")
        val normalizedInput = if (input.length == 3) "0$input" else input
        
        // If input is not 4 digits after normalization, fails structure check.
        if (normalizedInput.length != 4) {
             // Fallback: mark all target atoms as failed.
             val updates = targetAtoms.distinct().associateWith { atom ->
                 List(targetAtoms.count { it == atom }) { false }.toMutableList()
             }.toMutableMap()
             return EvaluationResult(isCorrect = false, atomUpdates = updates)
        }

        // 4. Split question.atoms into H and M parts using target string structure
        val targetH = target.substring(0, 2)
        val targetM = target.substring(2, 4)
        val inputH = normalizedInput.substring(0, 2)
        val inputM = normalizedInput.substring(2, 4)
        
        // Split question.atoms: first N atoms belong to hours, rest to minutes
        // N is determined by the hour part's decomposition structure
        val hourAtomCount = TimeGenerator.decomposeTwoDigitPart(targetH).size
        val targetHAtoms = targetAtoms.take(hourAtomCount)
        val targetMAtoms = targetAtoms.drop(hourAtomCount)
        val inputHAtoms = TimeGenerator.decomposeTwoDigitPart(inputH)
        val inputMAtoms = TimeGenerator.decomposeTwoDigitPart(inputM)
        
        // 5. Evaluate Parts
        val updates = mutableMapOf<String, MutableList<Boolean>>()
        
        val hResult = evaluatePart(targetHAtoms, inputHAtoms, updates)
        val mResult = evaluatePart(targetMAtoms, inputMAtoms, updates)
        
        // 6. Overall Correctness
        // Strict: All Target Atoms matched, AND No Extra Atoms in Input
        val noExtraH = inputHAtoms.size <= targetHAtoms.size
        val noExtraM = inputMAtoms.size <= targetMAtoms.size
        
        val isCorrect = hResult && mResult && noExtraH && noExtraM
        
        return EvaluationResult(
            isCorrect = isCorrect,
            atomUpdates = updates
        )
    }
    
    // Evaluate a list of atoms against input atoms (Ordered)
    // Returns true if all target atoms matched
    private fun evaluatePart(
        targetAtoms: List<String>, 
        inputAtoms: List<String>, 
        updates: MutableMap<String, MutableList<Boolean>>
    ): Boolean {
        var allMatched = true
        
        for (i in targetAtoms.indices) {
            val atomId = targetAtoms[i]
            val inputAtom = inputAtoms.getOrNull(i)
            
            val isMatch = (inputAtom == atomId)
            if (!isMatch) allMatched = false
            
            if (!updates.containsKey(atomId)) {
                updates[atomId] = mutableListOf()
            }
            updates[atomId]?.add(isMatch)
        }
        
        return allMatched
    }
}
