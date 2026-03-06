package com.siffermastare.util

/**
 * Abstraction over platform logging, enabling unit tests without Android dependencies.
 */
interface Logger {
    fun d(tag: String, message: String)
}
