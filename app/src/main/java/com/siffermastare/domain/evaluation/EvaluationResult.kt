package com.siffermastare.domain.evaluation

/**
 * Result of an evaluation attempt.
 *
 * @param isCorrect Whether the answer was considered correct overall.
 * @param atomUpdates A map of atom IDs to their success status (true/false).
 *                    Used for granular knowledge tracking. Default is empty.
 */
data class EvaluationResult(
    val isCorrect: Boolean,
    val atomUpdates: Map<String, Boolean> = emptyMap()
)
