package com.siffermastare

import android.app.Application
import com.siffermastare.data.database.AppDatabase
import com.siffermastare.data.repository.LessonRepository
import com.siffermastare.data.repository.LessonRepositoryImpl
import com.siffermastare.data.repository.RoomKnowledgeRepository
import com.siffermastare.domain.engine.KnowledgeEngine
import com.siffermastare.util.SystemTimeProvider
import com.siffermastare.util.TimeProvider

class SiffermastareApplication : Application() {
    // Manual Dependency Injection Container
    
    // Database instance (lazy)
    val database by lazy { AppDatabase.getDatabase(this) }
    
    // Repository instance (lazy)
    val lessonRepository: LessonRepository by lazy { 
        LessonRepositoryImpl(database.lessonDao()) 
    }
    
    // TimeProvider instance (lazy)
    val timeProvider: TimeProvider by lazy { SystemTimeProvider() }
    
    // Knowledge Repository backed by Room
    val knowledgeRepository by lazy {
        RoomKnowledgeRepository(database.atomStateDao(), timeProvider)
    }
    
    // Knowledge Engine instance (lazy)
    val knowledgeEngine: KnowledgeEngine by lazy {
        KnowledgeEngine(knowledgeRepository, timeProvider)
    }
}
