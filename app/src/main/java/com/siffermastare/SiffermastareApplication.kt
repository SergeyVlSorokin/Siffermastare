package com.siffermastare

import android.app.Application
import com.siffermastare.data.database.AppDatabase
import com.siffermastare.data.repository.LessonRepository
import com.siffermastare.data.repository.LessonRepositoryImpl

class SiffermastareApplication : Application() {
    // Manual Dependency Injection Container
    
    // Database instance (lazy)
    val database by lazy { AppDatabase.getDatabase(this) }
    
    // Repository instance (lazy)
    val lessonRepository: LessonRepository by lazy { 
        LessonRepositoryImpl(database.lessonDao()) 
    }
}
