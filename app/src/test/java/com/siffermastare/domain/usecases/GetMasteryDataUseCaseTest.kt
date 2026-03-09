package com.siffermastare.domain.usecases

import com.siffermastare.data.database.AtomState
import com.siffermastare.data.repository.KnowledgeRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class FakeKnowledgeRepository(private val states: List<AtomState>) : KnowledgeRepository {
    override suspend fun getAtomState(atomId: String): AtomState = states.firstOrNull { it.atomId == atomId } ?: AtomState(atomId, 1f, 1f, 0)
    override suspend fun getAllAtomStates(): List<AtomState> = states
    override suspend fun updateAtomState(atomState: AtomState) {}
}

class GetMasteryDataUseCaseTest {
    @Test
    fun `empty database returns baseline mastery`() = runBlocking {
        val repo = FakeKnowledgeRepository(emptyList())
        val useCase = GetMasteryDataUseCase(repo)
        
        val data = useCase()
        
        assertEquals("global", data.globalState.id)
        assertEquals(1.0f, data.globalState.alpha)
        assertEquals(1.0f, data.globalState.beta)
        assertEquals(0.5f, data.globalState.mu)
        assertEquals(0, data.atomStates.size)
    }

    @Test
    fun `aggregates multiple atoms correctly`() = runBlocking {
        val states = listOf(
            AtomState("time_informal|kvart", 10f, 2f, 0),
            AtomState("time_informal|halv", 5f, 5f, 0)
        )
        val repo = FakeKnowledgeRepository(states)
        val useCase = GetMasteryDataUseCase(repo)
        
        val data = useCase()
        
        // Global (Mean of alpha and beta)
        // kvart: (10f, 2f), halv: (5f, 5f)
        // Average: alpha = 15/2 = 7.5f, beta = 7/2 = 3.5f
        assertEquals(7.5f, data.globalState.alpha)
        assertEquals(3.5f, data.globalState.beta)
        assertEquals(7.5f / 11.0f, data.globalState.mu, 0.001f)
        
        // Individual formatting
        assertEquals(2, data.atomStates.size)
        
        // They should be returned in the original order because sorting was moved to ViewModel
        val kvart = data.atomStates[0]
        val halv = data.atomStates[1]
        
        assertEquals("time_informal|kvart", kvart.id)
        assertEquals("kvart", kvart.displayName)
        assertEquals(10f, kvart.alpha)
        
        assertEquals("time_informal|halv", halv.id)
        assertEquals("halv", halv.displayName)
        assertEquals(5f, halv.alpha)
    }

    @Test
    fun `formats atom display names correctly`() = runBlocking {
        val states = listOf(
            AtomState("ord:20", 10f, 2f, 0),
            AtomState("#kvart", 5f, 5f, 0),
            AtomState("15", 3f, 1f, 0)
        )
        val repo = FakeKnowledgeRepository(states)
        val useCase = GetMasteryDataUseCase(repo)
        
        val data = useCase()
        
        assertEquals(3, data.atomStates.size)
        assertEquals("20 (ordningstal)", data.atomStates[0].displayName)
        assertEquals("kvart", data.atomStates[1].displayName)
        assertEquals("15", data.atomStates[2].displayName)
    }
}
