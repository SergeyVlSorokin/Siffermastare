package com.siffermastare.testdoubles

import com.siffermastare.util.TimeProvider

class FakeTimeProvider : TimeProvider {
    var currentTime: Long = 0L
    override fun currentTimeMillis(): Long = currentTime
}
