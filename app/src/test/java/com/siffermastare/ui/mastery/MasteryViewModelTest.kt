package com.siffermastare.ui.mastery

import com.siffermastare.data.database.AtomState
import com.siffermastare.data.repository.KnowledgeRepository
import com.siffermastare.domain.usecases.GetMasteryDataUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FakeKnowledgeRepository(private val states: List<AtomState>) : KnowledgeRepository {
    override suspend fun getAtomState(atomId: String): AtomState = states.firstOrNull { it.atomId == atomId } ?: AtomState(atomId, 1f, 1f, 0)
    override suspend fun getAllAtomStates(): List<AtomState> = states
    override suspend fun updateAtomState(atomState: AtomState) {}
}

@OptIn(ExperimentalCoroutinesApi::class)
class MasteryViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `NATURAL_ORDER sorts numbers correctly across categories`() = runTest {
        val states = listOf(
            AtomState("ord:20", 1f, 1f, 0),
            AtomState("2", 1f, 1f, 0),
            AtomState("time_informal|halv", 1f, 1f, 0),
            AtomState("13:00", 1f, 1f, 0)
        )
        val useCase = GetMasteryDataUseCase(FakeKnowledgeRepository(states))
        val viewModel = MasteryViewModel(useCase)
        
        viewModel.setSortOption(MasterySortOption.NATURAL_ORDER)
        
        val state = viewModel.uiState.first { it is MasteryUiState.Success } as MasteryUiState.Success
        val sortedIds = state.masteryData.atomStates.map { it.id }
        
        // "2" -> 2
        // "ord:20" -> 20
        // "13:00" -> 1300
        // "time_informal|halv" -> null (falls back to string compare of display name "halv")
        
        // Expected order: 2, 20, 1300, halv
        
        assertEquals("2", sortedIds[0])
        assertEquals("ord:20", sortedIds[1])
        assertEquals("13:00", sortedIds[2])
        assertEquals("time_informal|halv", sortedIds[3])
    }

    @Test
    fun `showUntested false filters out baseline atoms`() = runTest {
        val states = listOf(
            AtomState("1", 5f, 2f, 0),    // tested
            AtomState("2", 1f, 1f, 0)     // untested (exactly 1.0f)
        )
        val useCase = GetMasteryDataUseCase(FakeKnowledgeRepository(states))
        val viewModel = MasteryViewModel(useCase)
        
        viewModel.setShowUntested(false)
        
        val state = viewModel.uiState.first { it is MasteryUiState.Success } as MasteryUiState.Success
        assertEquals(1, state.masteryData.atomStates.size)
        assertEquals("1", state.masteryData.atomStates[0].id)
    }
}
