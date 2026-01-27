package com.siffermastare.data.repository

import com.siffermastare.data.database.AtomState
import com.siffermastare.data.database.AtomStateDao
import com.siffermastare.util.TimeProvider

class RoomKnowledgeRepository(
    private val atomStateDao: AtomStateDao,
    private val timeProvider: TimeProvider
) : KnowledgeRepository {

    override suspend fun getAtomState(atomId: String): AtomState {
        return atomStateDao.getAtomState(atomId) ?: AtomState(
            atomId = atomId,
            alpha = 1.0f,
            beta = 1.0f,
            lastUpdated = timeProvider.currentTimeMillis()
        )
    }

    override suspend fun updateAtomState(atomState: AtomState) {
        atomStateDao.insertOrUpdate(atomState)
    }
}
