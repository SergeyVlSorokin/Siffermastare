package com.siffermastare.util

import android.util.Log

/**
 * Production logger that delegates to Android's [Log].
 */
class AndroidLogger : Logger {
    override fun d(tag: String, message: String) {
        Log.d(tag, message)
    }
}
