package com.siffermastare.ui.util

object TimeFormatter {
    fun format(input: String): String {
        if (input.length <= 2) return input
        // Insert colon before the last 2 digits
        return StringBuilder(input).insert(input.length - 2, ":").toString()
    }
}
