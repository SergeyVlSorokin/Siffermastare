package com.siffermastare.ui.mastery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.siffermastare.domain.model.MasteryData
import com.siffermastare.domain.usecases.GetMasteryDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MasterySortOption {
    NATURAL_ORDER,
    KNOWLEDGE_INCREASING,
    KNOWLEDGE_DECREASING
}

sealed class MasteryUiState {
    object Loading : MasteryUiState()
    data class Success(
        val masteryData: MasteryData,
        val sortOption: MasterySortOption,
        val showUntested: Boolean
    ) : MasteryUiState()
}

class MasteryViewModel(
    private val getMasteryDataUseCase: GetMasteryDataUseCase
) : ViewModel() {

    private val _sortOption = MutableStateFlow(MasterySortOption.NATURAL_ORDER)
    private val _showUntested = MutableStateFlow(true)
    private val _masteryData = MutableStateFlow<MasteryData?>(null)

    val uiState: StateFlow<MasteryUiState> = combine(_masteryData, _sortOption, _showUntested) { data, sort, showUntested ->
        if (data == null) return@combine MasteryUiState.Loading
        
        var atoms = data.atomStates
        if (!showUntested) {
            // Check for baseline untested state instead of exact float matching
            atoms = atoms.filter { !(it.alpha == 1f && it.beta == 1f) }
        }
        
        val sortedAtoms = atoms.sortedWith(getComparator(sort))
        MasteryUiState.Success(data.copy(atomStates = sortedAtoms), sort, showUntested)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MasteryUiState.Loading)

    init {
        loadMasteryData()
    }

    private fun loadMasteryData() {
        viewModelScope.launch {
            _masteryData.value = getMasteryDataUseCase()
        }
    }
    
    fun setSortOption(option: MasterySortOption) {
        _sortOption.value = option
    }
    
    fun setShowUntested(show: Boolean) {
        _showUntested.value = show
    }

    fun refresh() {
        loadMasteryData()
    }
    
    private fun getComparator(sortOption: MasterySortOption): Comparator<com.siffermastare.domain.model.AtomMastery> {
        return when (sortOption) {
            MasterySortOption.KNOWLEDGE_INCREASING -> compareBy<com.siffermastare.domain.model.AtomMastery> { it.mu }.thenBy { it.displayName }
            MasterySortOption.KNOWLEDGE_DECREASING -> compareByDescending<com.siffermastare.domain.model.AtomMastery> { it.mu }.thenBy { it.displayName }
            MasterySortOption.NATURAL_ORDER -> Comparator { a, b ->
                val idA = a.id
                val idB = b.id
                
                val numA = idA.replace(Regex("[^0-9]"), "").toIntOrNull()
                val numB = idB.replace(Regex("[^0-9]"), "").toIntOrNull()
                
                if (numA != null && numB != null) {
                    if (numA != numB) return@Comparator numA.compareTo(numB)
                }
                
                if (numA != null && numB == null) return@Comparator -1
                if (numA == null && numB != null) return@Comparator 1
                
                a.displayName.compareTo(b.displayName)
            }
        }
    }
}

class MasteryViewModelFactory(
    private val getMasteryDataUseCase: GetMasteryDataUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MasteryViewModel::class.java)) {
            return MasteryViewModel(getMasteryDataUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
