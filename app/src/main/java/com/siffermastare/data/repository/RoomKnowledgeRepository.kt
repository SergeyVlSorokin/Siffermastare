package com.siffermastare.data.repository

import android.util.Log
import com.siffermastare.data.database.AtomState
import com.siffermastare.data.database.AtomStateDao
import com.siffermastare.util.TimeProvider

class RoomKnowledgeRepository(
    private val atomStateDao: AtomStateDao,
    private val timeProvider: TimeProvider
) : KnowledgeRepository {

    override suspend fun getAtomState(atomId: String): AtomState {
        val existing = atomStateDao.getAtomState(atomId)
        if (existing != null) {
            Log.d(TAG, "GET atom=$atomId α=${existing.alpha} β=${existing.beta}")
        } else {
            Log.d(TAG, "GET atom=$atomId → new prior (α=1.0, β=1.0)")
        }
        return existing ?: AtomState(
            atomId = atomId,
            alpha = 1.0f,
            beta = 1.0f,
            lastUpdated = timeProvider.currentTimeMillis()
        )
    }

    override suspend fun updateAtomState(atomState: AtomState) {
        val mean = atomState.alpha / (atomState.alpha + atomState.beta)
        Log.d(TAG, "UPDATE atom=${atomState.atomId} α=${atomState.alpha} β=${atomState.beta} mean=%.3f".format(mean))
        atomStateDao.insertOrUpdate(atomState)
    }

    companion object {
        private const val TAG = "KnowledgeDB"
    }
}
