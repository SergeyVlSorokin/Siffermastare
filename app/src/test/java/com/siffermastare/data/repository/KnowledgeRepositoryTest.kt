package com.siffermastare.data.repository

import com.siffermastare.data.database.AtomState
import com.siffermastare.data.database.AtomStateDao
import com.siffermastare.util.TimeProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class KnowledgeRepositoryTest {

    private lateinit var atomStateDao: FakeAtomStateDao
    private lateinit var timeProvider: FakeTimeProvider
    private lateinit var repository: KnowledgeRepository

    @Before
    fun setUp() {
        atomStateDao = FakeAtomStateDao()
        timeProvider = FakeTimeProvider()
        repository = RoomKnowledgeRepository(atomStateDao, timeProvider)
    }

    @Test
    fun `getAtomState returns existing state when present`() = runBlocking {
        val existingState = AtomState("1", 2.0f, 3.0f, 1000L)
        atomStateDao.insertOrUpdate(existingState)

        val result = repository.getAtomState("1")

        assertEquals(existingState, result)
    }

    @Test
    fun `getAtomState returns default state when missing`() = runBlocking {
        // Setup fake time
        timeProvider.currentTime = 5555L
        
        // No state inserted for "2"

        val result = repository.getAtomState("2")

        assertEquals("2", result.atomId)
        assertEquals(1.0f, result.alpha)
        assertEquals(1.0f, result.beta)
        assertEquals(5555L, result.lastUpdated)
    }

    @Test
    fun `updateAtomState calls dao insertOrUpdate`() = runBlocking {
        val state = AtomState("1", 2.0f, 2.0f, 2000L)
        
        repository.updateAtomState(state)

        val stored = atomStateDao.getAtomState("1")
        assertEquals(state, stored)
    }
}

class FakeAtomStateDao : AtomStateDao {
    private val states = mutableMapOf<String, AtomState>()

    override suspend fun getAtomState(atomId: String): AtomState? {
        return states[atomId]
    }

    override suspend fun insertOrUpdate(atomState: AtomState) {
        states[atomState.atomId] = atomState
    }
}

class FakeTimeProvider : TimeProvider {
    var currentTime: Long = 0L
    override fun currentTimeMillis(): Long = currentTime
}
