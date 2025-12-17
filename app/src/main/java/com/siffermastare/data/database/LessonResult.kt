package com.siffermastare.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lesson_results")
data class LessonResult(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val accuracy: Float,
    val averageSpeed: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val lessonType: String
)
