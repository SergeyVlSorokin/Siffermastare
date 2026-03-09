package com.siffermastare

import android.app.Application
import com.siffermastare.data.database.AppDatabase
import com.siffermastare.data.repository.LessonRepository
import com.siffermastare.data.repository.LessonRepositoryImpl
import com.siffermastare.data.repository.RoomKnowledgeRepository
import com.siffermastare.domain.engine.KnowledgeEngine
import com.siffermastare.domain.generators.NumberGeneratorFactory
import com.siffermastare.domain.usecases.GetMasteryDataUseCase
import com.siffermastare.util.AndroidLogger
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
        RoomKnowledgeRepository(database.atomStateDao(), timeProvider, AndroidLogger())
    }
    
    // Knowledge Engine instance (lazy)
    val knowledgeEngine: KnowledgeEngine by lazy {
        KnowledgeEngine(knowledgeRepository, timeProvider)
    }
    
    // All available generators for atom enumeration in Mastery Dashboard
    val allGenerators by lazy {
        listOf(
            NumberGeneratorFactory.create(NumberGeneratorFactory.ID_CARDINAL_0_20),
            NumberGeneratorFactory.create(NumberGeneratorFactory.ID_CARDINAL_20_100),
            NumberGeneratorFactory.create(NumberGeneratorFactory.ID_CARDINAL_100_1000),
            NumberGeneratorFactory.create(NumberGeneratorFactory.ID_ORDINAL_1_20),
            NumberGeneratorFactory.create(NumberGeneratorFactory.ID_TIME_DIGITAL),
            NumberGeneratorFactory.create(NumberGeneratorFactory.ID_TIME_INFORMAL),
            NumberGeneratorFactory.create(NumberGeneratorFactory.ID_TRICKY_PAIRS),
            NumberGeneratorFactory.create(NumberGeneratorFactory.ID_PHONE_NUMBER),
            NumberGeneratorFactory.create(NumberGeneratorFactory.ID_FRACTIONS),
            NumberGeneratorFactory.create(NumberGeneratorFactory.ID_DECIMALS)
        )
    }
    
    val getMasteryDataUseCase: GetMasteryDataUseCase by lazy {
        GetMasteryDataUseCase(knowledgeRepository, allGenerators)
    }
}
