package com.siffermastare.domain.evaluation

import kotlin.math.min

/**
 * Shared utility for bag-logic comparison of atom lists.
 * Used by evaluation strategies to compare target atoms against input atoms.
 */
object BagLogicHelper {

    /**
     * Bag-logic comparison: for each target atom, count matches in input.
     * Extra input atoms are ignored (No Extra Atoms rule).
     *
     * @param targetAtoms The expected atoms from question.atoms.
     * @param inputAtoms The atoms decomposed from user input.
     * @return Map of atom → list of booleans (true=matched, false=unmatched).
     */
    fun compare(
        targetAtoms: List<String>,
        inputAtoms: List<String>
    ): Map<String, List<Boolean>> {
        val targetCounts = targetAtoms.groupingBy { it }.eachCount()
        val inputCounts = inputAtoms.groupingBy { it }.eachCount()

        return targetCounts.map { (atom, required) ->
            val provided = inputCounts[atom] ?: 0
            val matches = min(required, provided)
            val misses = required - matches

            val atomResults = mutableListOf<Boolean>()
            repeat(matches) { atomResults.add(true) }
            repeat(misses) { atomResults.add(false) }
            atom to atomResults
        }.toMap()
    }

    /**
     * Fail all target atoms — every occurrence marked false.
     *
     * @param targetAtoms The expected atoms from question.atoms.
     * @return Map of atom → list of false booleans.
     */
    fun failAll(targetAtoms: List<String>): Map<String, List<Boolean>> {
        return targetAtoms.distinct().associateWith { atom ->
            List(targetAtoms.count { it == atom }) { false }
        }
    }
}
