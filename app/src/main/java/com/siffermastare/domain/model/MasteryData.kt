package com.siffermastare.domain.model

data class MasteryData(
    val globalState: AtomMastery,
    val atomStates: List<AtomMastery>,
    val hasDatabaseData: Boolean = false
)
