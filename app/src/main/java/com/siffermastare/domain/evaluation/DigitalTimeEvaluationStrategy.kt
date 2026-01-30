package com.siffermastare.domain.evaluation

import com.siffermastare.domain.models.Question

/**
 * Strictly evaluates Digital Time questions (e.g. "1400").
 * Decomposes time into High-Level Parts (HH, MM) and then atoms.
 * Enforces ORDER of atoms within each part.
 */
class DigitalTimeEvaluationStrategy : EvaluationStrategy {

    override fun evaluate(input: String, question: Question): EvaluationResult {
        // 1. Strict Validation: Input must be digits only
        if (!input.all { it.isDigit() }) {
            return EvaluationResult(isCorrect = false)
        }

        // 2. Parse Target
        val target = question.targetValue // e.g., "1430"
        
        // 3. Normalize Input (handle 3-digit case "513" -> "0513")
        val normalizedInput = if (input.length == 3) "0$input" else input
        
        // If input is not 4 digits after normalization (e.g. too short/long), fails structure check.
        if (normalizedInput.length != 4) {
             // Fallback: If structure is wrong, we can't align HH/MM reliably. 
             // Mark all target atoms as failed.
             val updates = decomposeTimeFull(target).associateWith { listOf(false).toMutableList() }.toMutableMap()
             return EvaluationResult(isCorrect = false, atomUpdates = updates)
        }

        // 4. Split and Decompose Parts
        val targetH = target.substring(0, 2)
        val targetM = target.substring(2, 4)
        val inputH = normalizedInput.substring(0, 2)
        val inputM = normalizedInput.substring(2, 4)
        
        val targetHAtoms = decomposeHours(targetH)
        val targetMAtoms = decomposeMinutes(targetM)
        val inputHAtoms = decomposeHours(inputH)
        val inputMAtoms = decomposeMinutes(inputM)
        
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

    // Helper to decompose complete time (for fallback)
    private fun decomposeTimeFull(timeStr: String): List<String> {
        if (timeStr.length != 4) return emptyList()
        val h = decomposeHours(timeStr.substring(0, 2))
        val m = decomposeMinutes(timeStr.substring(2, 4))
        return h + m
    }

    private fun decomposeHours(hh: String): List<String> {
        return decomposeTwoDigitPart(hh)
    }

    private fun decomposeMinutes(mm: String): List<String> {
        return decomposeTwoDigitPart(mm)
    }

    private fun decomposeTwoDigitPart(partStr: String): List<String> {
        val valInt = partStr.toInt()
        val d1 = partStr[0].digitToInt()
        val d2 = partStr[1].digitToInt()
        
        val atoms = mutableListOf<String>()
        if (valInt in 1..9) { // 01-09 -> "noll X"
            if (d1 == 0) atoms.add("0")
            atoms.add(d2.toString())
        } else if (valInt == 0) { // 00 -> "noll noll"
            atoms.add("0")
            atoms.add("0")
        } else { // 10-99
            atoms.addAll(decomposeStandard(valInt))
        }
        return atoms
    }

    private fun decomposeStandard(number: Int): List<String> {
        val list = mutableListOf<String>()
        if (number == 0) {
            list.add("0")
            return list
        }
        
        val tens = (number / 10) * 10
        val ones = number % 10
        
        if (number in 10..19) {
            // Teens are atomic (including 10)
            list.add(number.toString())
        } else {
            // 20-99
            if (tens > 0) list.add(tens.toString())
            if (ones > 0) list.add(ones.toString())
        }
        return list
    }
}
