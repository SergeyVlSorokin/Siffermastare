package com.siffermastare.ui.home

import com.siffermastare.data.database.LessonResult
import com.siffermastare.data.repository.LessonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var fakeRepository: FakeLessonRepository
    private lateinit var viewModel: HomeViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeLessonRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is empty`() = runTest {
        // Default fake has 0 lessons, empty timestamps
        viewModel = HomeViewModel(fakeRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(0, viewModel.uiState.value.totalLessons)
        assertEquals(0, viewModel.uiState.value.currentStreak)
    }

    @Test
    fun `calculates streak correctly`() = runTest {
        fakeRepository.setCount(5)
        
        val now = System.currentTimeMillis()
        val oneDayMillis = 24 * 60 * 60 * 1000L
        
        val today = now
        val yesterday = now - oneDayMillis
        val threeDaysAgo = now - (3 * oneDayMillis)

        val timestamps = listOf(today, yesterday, threeDaysAgo)
        fakeRepository.setTimestamps(timestamps)

        viewModel = HomeViewModel(fakeRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(5, viewModel.uiState.value.totalLessons)
        assertEquals(2, viewModel.uiState.value.currentStreak)
    }
    
    @Test
    fun `calculates streak with gap resets`() = runTest {
        fakeRepository.setCount(5)
        
        // Mock Timestamps: 2 days ago, 3 days ago (Gap of today/yesterday)
        val oneDayMillis = 24 * 60 * 60 * 1000L
        val now = System.currentTimeMillis()
        val twoDaysAgo = now - (2 * oneDayMillis)
        val threeDaysAgo = now - (3 * oneDayMillis)

        val timestamps = listOf(twoDaysAgo, threeDaysAgo)
        fakeRepository.setTimestamps(timestamps)

        viewModel = HomeViewModel(fakeRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(0, viewModel.uiState.value.currentStreak)
    }
}

class FakeLessonRepository : LessonRepository {
    private val _count = MutableStateFlow(0)
    private val _timestamps = MutableStateFlow<List<Long>>(emptyList())

    fun setCount(count: Int) {
        _count.value = count
    }

    fun setTimestamps(timestamps: List<Long>) {
        _timestamps.value = timestamps
    }

    override suspend fun insertLessonResult(result: LessonResult) {
        // No-op for this test
    }

    override fun getAllLessonResults(): Flow<List<LessonResult>> {
        return flowOf(emptyList()) // Not used
    }

    override fun getLessonCount(): Flow<Int> {
        return _count.asStateFlow()
    }

    override fun getAllTimestamps(): Flow<List<Long>> {
        return _timestamps.asStateFlow()
    }
}
