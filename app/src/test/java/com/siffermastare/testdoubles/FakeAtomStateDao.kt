package com.siffermastare.testdoubles

import com.siffermastare.data.database.AtomState
import com.siffermastare.data.database.AtomStateDao

class FakeAtomStateDao : AtomStateDao {
    private val states = mutableMapOf<String, AtomState>()

    override suspend fun getAtomState(atomId: String): AtomState? {
        return states[atomId]
    }

    override suspend fun getAllAtomStates(): List<AtomState> {
        return states.values.toList()
    }

    override suspend fun insertOrUpdate(atomState: AtomState) {
        states[atomState.atomId] = atomState
    }
}
