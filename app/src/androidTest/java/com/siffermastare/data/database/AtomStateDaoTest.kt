package com.siffermastare.data.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AtomStateDaoTest {
    private lateinit var atomStateDao: AtomStateDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        atomStateDao = db.atomStateDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetAtomState() = runBlocking {
        val atomState = AtomState("1", 2.0f, 3.0f, 123456789L)
        atomStateDao.insertOrUpdate(atomState)

        val retrievedState = atomStateDao.getAtomState("1")
        assertNotNull(retrievedState)
        assertEquals(atomState.alpha, retrievedState?.alpha)
        assertEquals(atomState.beta, retrievedState?.beta)
        assertEquals(atomState.lastUpdated, retrievedState?.lastUpdated)
    }

    @Test
    @Throws(Exception::class)
    fun updateAtomState() = runBlocking {
        val atomState = AtomState("1", 2.0f, 3.0f, 123456789L)
        atomStateDao.insertOrUpdate(atomState)

        val updatedState = AtomState("1", 4.0f, 5.0f, 987654321L)
        atomStateDao.insertOrUpdate(updatedState)

        val retrievedState = atomStateDao.getAtomState("1")
        assertNotNull(retrievedState)
        assertEquals(4.0f, retrievedState?.alpha)
        assertEquals(5.0f, retrievedState?.beta)
        assertEquals(987654321L, retrievedState?.lastUpdated)
    }
}
