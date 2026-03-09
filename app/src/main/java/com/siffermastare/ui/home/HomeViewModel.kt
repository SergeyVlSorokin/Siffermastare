package com.siffermastare.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.siffermastare.data.repository.LessonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

import com.siffermastare.domain.model.AtomMastery
import com.siffermastare.domain.usecases.GetMasteryDataUseCase

data class HomeUiState(
    val totalLessons: Int = 0,
    val currentStreak: Int = 0,
    val globalMastery: AtomMastery? = null
)

class HomeViewModel(
    private val lessonRepository: LessonRepository,
    private val getMasteryDataUseCase: GetMasteryDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                lessonRepository.getLessonCount(),
                lessonRepository.getAllTimestamps()
            ) { count, timestamps ->
                Pair(count, timestamps)
            }
            .distinctUntilChanged()
            .collectLatest { (count, timestamps) ->
                val streak = calculateStreak(timestamps)
                val masteryData = getMasteryDataUseCase()
                _uiState.value = HomeUiState(
                    totalLessons = count,
                    currentStreak = streak,
                    globalMastery = masteryData.globalState
                )
            }
        }
    }

    private fun calculateStreak(timestamps: List<Long>): Int {
        if (timestamps.isEmpty()) return 0

        // Use system default zone for simplicity (or pass timezone if needed)
        val zoneId = ZoneId.systemDefault()
        val today = LocalDate.now(zoneId)
        val yesterday = today.minusDays(1)

        val uniqueDates = timestamps
            .map { Instant.ofEpochMilli(it).atZone(zoneId).toLocalDate() }
            .distinct()
            .sortedDescending() // Newest first

        if (uniqueDates.isEmpty()) return 0

        val firstDate = uniqueDates[0]
        
        // Check if streak is alive (Today or Yesterday present)
        // If the last played date is before yesterday, streak is broken.
        if (!firstDate.isEqual(today) && !firstDate.isEqual(yesterday)) {
            return 0
        }

        var streak = 0
        var checkDate = if (uniqueDates.contains(today)) today else yesterday

        for (date in uniqueDates) {
            if (date.isEqual(checkDate)) {
                streak++
                checkDate = checkDate.minusDays(1)
            } else if (date.isBefore(checkDate)) {
                // Gap found
                break
            }
            // If date is "after" checkDate (e.g. we have today AND yesterday, but we started check from today), 
            // logic handles it because we iterate sorted dates. "today" matches checkDate (today).
            // checkDate becomes yesterday. Next iter "yesterday" matches.
        }
        
        return streak
    }
}

class HomeViewModelFactory(
    private val repository: LessonRepository,
    private val getMasteryDataUseCase: GetMasteryDataUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repository, getMasteryDataUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
