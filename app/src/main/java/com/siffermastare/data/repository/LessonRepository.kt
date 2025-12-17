package com.siffermastare.data.repository

import com.siffermastare.data.database.LessonResult
import kotlinx.coroutines.flow.Flow

interface LessonRepository {
    suspend fun insertLessonResult(result: LessonResult)
    fun getAllLessonResults(): Flow<List<LessonResult>>
}
