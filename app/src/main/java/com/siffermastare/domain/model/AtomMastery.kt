package com.siffermastare.domain.model

data class AtomMastery(
    val id: String,
    val alpha: Float,
    val beta: Float,
    val mu: Float = alpha / (alpha + beta),
    val displayName: String = id
)
