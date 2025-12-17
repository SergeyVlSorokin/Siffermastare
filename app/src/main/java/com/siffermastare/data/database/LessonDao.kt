package com.siffermastare.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(result: LessonResult)

    @Query("SELECT * FROM lesson_results ORDER BY timestamp DESC")
    fun getAll(): Flow<List<LessonResult>>
}
