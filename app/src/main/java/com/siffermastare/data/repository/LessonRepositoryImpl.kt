package com.siffermastare.data.repository

import com.siffermastare.data.database.LessonDao
import com.siffermastare.data.database.LessonResult
import kotlinx.coroutines.flow.Flow

class LessonRepositoryImpl(private val lessonDao: LessonDao) : LessonRepository {
    override suspend fun insertLessonResult(result: LessonResult) {
        lessonDao.insert(result)
    }

    override fun getAllLessonResults(): Flow<List<LessonResult>> {
        return lessonDao.getAll()
    }

    override fun getLessonCount(): Flow<Int> {
        return lessonDao.getLessonCount()
    }

    override fun getAllTimestamps(): Flow<List<Long>> {
        return lessonDao.getAllTimestamps()
    }
}
