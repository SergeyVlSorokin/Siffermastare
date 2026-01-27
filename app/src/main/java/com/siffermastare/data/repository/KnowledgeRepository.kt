package com.siffermastare.data.repository

import com.siffermastare.data.database.AtomState

interface KnowledgeRepository {
    suspend fun getAtomState(atomId: String): AtomState
    suspend fun updateAtomState(atomState: AtomState)
}
