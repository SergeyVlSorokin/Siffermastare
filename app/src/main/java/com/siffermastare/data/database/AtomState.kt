package com.siffermastare.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "atom_states")
data class AtomState(
    @PrimaryKey val atomId: String,
    val alpha: Float,
    val beta: Float,
    val lastUpdated: Long
)
