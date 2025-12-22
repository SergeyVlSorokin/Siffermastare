package com.siffermastare.data.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class LessonDaoTest {
    private lateinit var lessonDao: LessonDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        lessonDao = db.lessonDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndReadInList() = runBlocking {
        val result = LessonResult(
            accuracy = 0.9f,
            averageSpeed = 1500L,
            lessonType = "0-10"
        )
        lessonDao.insert(result)

        val allResults = lessonDao.getAll().first()
        assertEquals(allResults[0].accuracy, 0.9f)
        assertEquals(allResults[0].averageSpeed, 1500L)
        assertEquals(allResults[0].lessonType, "0-10")
    }

    @Test
    fun getLessonCount() = runBlocking {
        val result1 = LessonResult(accuracy = 1.0f, averageSpeed = 1000L, lessonType = "test")
        val result2 = LessonResult(accuracy = 0.5f, averageSpeed = 2000L, lessonType = "test")
        lessonDao.insert(result1)
        lessonDao.insert(result2)

        val count = lessonDao.getLessonCount().first()
        assertEquals(2, count)
    }

    @Test
    fun getAllTimestamps() = runBlocking {
        // Timestamps: 1000, 2000
        val result1 = LessonResult(accuracy = 1.0f, averageSpeed = 1000L, timestamp = 1000L, lessonType = "test")
        val result2 = LessonResult(accuracy = 0.9f, averageSpeed = 1500L, timestamp = 2000L, lessonType = "test")
        
        lessonDao.insert(result1)
        lessonDao.insert(result2)

        val timestamps = lessonDao.getAllTimestamps().first()
        assertEquals(2, timestamps.size)
        assertEquals(2000L, timestamps[0]) // DESC order
        assertEquals(1000L, timestamps[1])
    }
}
