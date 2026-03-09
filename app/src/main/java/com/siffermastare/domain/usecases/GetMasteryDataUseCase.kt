package com.siffermastare.domain.usecases

import com.siffermastare.data.repository.KnowledgeRepository
import com.siffermastare.domain.generators.NumberGenerator
import com.siffermastare.domain.model.AtomMastery
import com.siffermastare.domain.model.MasteryData

class GetMasteryDataUseCase(
    private val repository: KnowledgeRepository,
    private val generators: List<NumberGenerator> = emptyList()
) {
    suspend operator fun invoke(): MasteryData {
        val dbStates = repository.getAllAtomStates()
        
        // Build a map from atomId -> DB state for fast lookup
        val dbStateMap = dbStates.associateBy { it.atomId }
        
        // Collect ALL possible atoms from generators
        val allAtomIds = generators.flatMap { it.getAllAtomIds() }.toSet()
        
        // If no generators and no DB data, return baseline
        if (allAtomIds.isEmpty() && dbStates.isEmpty()) {
            val baselineGlobal = AtomMastery("global", 1.0f, 1.0f, displayName = "Global Mastery")
            return MasteryData(baselineGlobal, emptyList())
        }
        
        // Merge: use DB state if present, otherwise baseline prior (1,1)
        val atomIdsToShow = if (allAtomIds.isNotEmpty()) {
            // Union: all generator atoms + any extra DB atoms
            allAtomIds + dbStateMap.keys
        } else {
            // No generators configured - fall back to just what's in DB
            dbStateMap.keys
        }
        
        var globalAlphaSum = 0.0f
        var globalBetaSum = 0.0f
        val masteryList = mutableListOf<AtomMastery>()

        for (atomId in atomIdsToShow) {
            val state = dbStateMap[atomId]
            val alpha = state?.alpha ?: 1.0f
            val beta = state?.beta ?: 1.0f
            
            // Sum all alphas and betas to compute the exact mean later
            globalAlphaSum += alpha
            globalBetaSum += beta
            
            // Extract bare id (strip category pipe if present)
            val rawId = atomId.substringAfter("|")
            
            // Format display name
            val displayName = when {
                rawId.startsWith("ord:") -> "${rawId.removePrefix("ord:")} (ordningstal)"
                rawId.startsWith("#") -> rawId.removePrefix("#")
                else -> rawId
            }
            
            masteryList.add(
                AtomMastery(
                    id = atomId,
                    alpha = alpha,
                    beta = beta,
                    displayName = displayName
                )
            )
        }

        val numAtoms = atomIdsToShow.size
        val globalMastery = AtomMastery(
            id = "global",
            alpha = if (numAtoms > 0) globalAlphaSum / numAtoms else 1.0f,
            beta = if (numAtoms > 0) globalBetaSum / numAtoms else 1.0f,
            displayName = "Global Mastery"
        )
        
        return MasteryData(
            globalState = globalMastery,
            atomStates = masteryList,
            hasDatabaseData = dbStates.isNotEmpty()
        )
    }
}
