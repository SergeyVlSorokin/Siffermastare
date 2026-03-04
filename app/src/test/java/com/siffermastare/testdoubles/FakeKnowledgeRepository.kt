package com.siffermastare.testdoubles

import com.siffermastare.data.database.AtomState
import com.siffermastare.data.repository.KnowledgeRepository

class FakeKnowledgeRepository : KnowledgeRepository {
    private val states = mutableMapOf<String, AtomState>()
    var defaultPriorAlpha = 1.0f
    var defaultPriorBeta = 1.0f
    var defaultPriorLastUpdated = 0L

    override suspend fun getAtomState(atomId: String): AtomState {
        return states[atomId] ?: AtomState(
            atomId = atomId,
            alpha = defaultPriorAlpha,
            beta = defaultPriorBeta,
            lastUpdated = defaultPriorLastUpdated
        )
    }

    override suspend fun updateAtomState(atomState: AtomState) {
        states[atomState.atomId] = atomState
    }
    
    // Test helper to initially seed state
    fun seedAtomState(atomState: AtomState) {
        states[atomState.atomId] = atomState
    }
}
