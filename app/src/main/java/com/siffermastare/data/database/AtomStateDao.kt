package com.siffermastare.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AtomStateDao {
    @Query("SELECT * FROM atom_states WHERE atomId = :atomId")
    suspend fun getAtomState(atomId: String): AtomState?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(atomState: AtomState)
}
