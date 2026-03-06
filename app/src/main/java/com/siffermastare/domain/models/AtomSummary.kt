package com.siffermastare.domain.models

import com.siffermastare.domain.evaluation.EvaluationResult

/**
 * Summary of an atom's performance within a single lesson session.
 *
 * @param atomId The atom identifier (e.g. "5", "ord:20", "#kvart").
 * @param successes Number of successful evaluations for this atom in the session.
 * @param failures Number of failed evaluations for this atom in the session.
 */
data class AtomSummary(
    val atomId: String,
    val successes: Int,
    val failures: Int
)

/**
 * Builds atom summary lists from a session's evaluation results.
 *
 * Merges all atomUpdates across all results by atomId, counting successes and failures.
 * An atom is "improved" if it has zero failures, "needs practice" if it has any failures.
 *
 * @return A pair of (improved, needsPractice) lists. Both lists are empty if no atom updates exist.
 */
fun buildAtomSummaries(results: List<EvaluationResult>): Pair<List<AtomSummary>, List<AtomSummary>> {
    val successCounts = mutableMapOf<String, Int>()
    val failureCounts = mutableMapOf<String, Int>()

    for (result in results) {
        for ((atomId, outcomes) in result.atomUpdates) {
            for (success in outcomes) {
                if (success) {
                    successCounts[atomId] = (successCounts[atomId] ?: 0) + 1
                } else {
                    failureCounts[atomId] = (failureCounts[atomId] ?: 0) + 1
                }
            }
        }
    }

    val allAtomIds = (successCounts.keys + failureCounts.keys)
    val improved = mutableListOf<AtomSummary>()
    val needsPractice = mutableListOf<AtomSummary>()

    for (atomId in allAtomIds) {
        val s = successCounts[atomId] ?: 0
        val f = failureCounts[atomId] ?: 0
        val summary = AtomSummary(atomId, s, f)
        if (f == 0) {
            improved.add(summary)
        } else {
            needsPractice.add(summary)
        }
    }

    // Smart sorting logic
    val idComparator = Comparator<AtomSummary> { a, b ->
        val idA = a.atomId
        val idB = b.atomId
        
        // Extract numeric value safely, stripping prefixes
        val numA = idA.replace(Regex("[^0-9]"), "").toIntOrNull()
        val numB = idB.replace(Regex("[^0-9]"), "").toIntOrNull()
        
        // If both have numbers, sort numerically
        if (numA != null && numB != null) {
            // If numbers are equal, then fallback to string comparison (e.g., "5" vs "ord:5")
            if (numA != numB) {
                return@Comparator numA.compareTo(numB)
            }
        }
        
        // If one has number and the other doesn't, numbers come first
        if (numA != null && numB == null) return@Comparator -1
        if (numA == null && numB != null) return@Comparator 1
        
        // Fallback to alphabetical for non-numeric (kvart, halv, i)
        idA.compareTo(idB)
    }

    improved.sortWith(idComparator)
    needsPractice.sortWith(idComparator)

    return Pair(improved, needsPractice)
}
