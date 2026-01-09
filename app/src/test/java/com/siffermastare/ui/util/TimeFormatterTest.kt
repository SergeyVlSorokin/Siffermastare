package com.siffermastare.ui.util

import org.junit.Assert.assertEquals
import org.junit.Test

class TimeFormatterTest {

    @Test
    fun format_emptyString_returnsEmpty() {
        assertEquals("", TimeFormatter.format(""))
    }

    @Test
    fun format_shortString_returnsOriginal() {
        assertEquals("1", TimeFormatter.format("1"))
        assertEquals("12", TimeFormatter.format("12"))
    }

    @Test
    fun format_threeDigits_insertsColonAfterFirst() {
        // "123" -> "1:23" (User Request)
        assertEquals("1:23", TimeFormatter.format("123"))
        assertEquals("9:30", TimeFormatter.format("930"))
    }

    @Test
    fun format_fourDigits_insertsColonAfterSecond() {
        assertEquals("09:30", TimeFormatter.format("0930"))
        assertEquals("14:45", TimeFormatter.format("1445"))
    }

    @Test
    fun format_handlesPipeDelimitedValues() {
        // "1846|0646" -> "18:46|06:46"
        assertEquals("18:46|06:46", TimeFormatter.format("1846|0646"))
        
        // Mixed lengths
        // "930|2130" -> "9:30|21:30"
        assertEquals("9:30|21:30", TimeFormatter.format("930|2130"))
    }
    

}
