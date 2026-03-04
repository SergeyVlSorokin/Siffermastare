package com.siffermastare.domain.engine

import com.siffermastare.data.repository.KnowledgeRepository
import com.siffermastare.domain.evaluation.EvaluationResult
import com.siffermastare.util.TimeProvider

open class KnowledgeEngine(
    private val repository: KnowledgeRepository,
    private val timeProvider: TimeProvider
) {
    companion object {
        const val DECAY_FACTOR = 0.9f
        const val REFERENCE_MPE = 800f
        const val MIN_WEIGHT = 0.2f
        const val MAX_WEIGHT = 1.3f
    }

    open suspend fun processEvaluation(result: EvaluationResult, elapsedMs: Long, targetLength: Int) {
        val mpe = if (targetLength > 0) {
            elapsedMs.toFloat() / targetLength.toFloat()
        } else {
            Float.MAX_VALUE
        }

        val weight = (REFERENCE_MPE / mpe).coerceIn(MIN_WEIGHT, MAX_WEIGHT)

        for ((atomId, outcomes) in result.atomUpdates) {
            var current = repository.getAtomState(atomId)

            for (success in outcomes) {
                val newAlpha: Float
                val newBeta: Float

                if (success) {
                    newAlpha = DECAY_FACTOR * current.alpha + weight
                    newBeta = DECAY_FACTOR * current.beta
                } else {
                    newAlpha = DECAY_FACTOR * current.alpha
                    newBeta = DECAY_FACTOR * current.beta + 1.0f
                }

                current = current.copy(
                    alpha = newAlpha,
                    beta = newBeta,
                    lastUpdated = timeProvider.currentTimeMillis()
                )
            }

            repository.updateAtomState(current)
        }
    }
}
