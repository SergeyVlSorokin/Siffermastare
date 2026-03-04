package com.siffermastare.ui.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.siffermastare.data.repository.LessonRepository
import com.siffermastare.domain.engine.KnowledgeEngine
import com.siffermastare.util.TimeProvider

class LessonViewModelFactory(
    private val repository: LessonRepository,
    private val knowledgeEngine: KnowledgeEngine? = null,
    private val timeProvider: TimeProvider
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LessonViewModel::class.java)) {
            return LessonViewModel(repository, knowledgeEngine, timeProvider) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
