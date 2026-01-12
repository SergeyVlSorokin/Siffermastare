package com.siffermastare.domain.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class SwedishNumberFormatterTest {

    @Test
    fun `toText formats 0-9 correctly`() {
        assertEquals("noll", SwedishNumberFormatter.toText(0))
        assertEquals("ett", SwedishNumberFormatter.toText(1))
        assertEquals("nio", SwedishNumberFormatter.toText(9))
    }

    @Test
    fun `toText formats 10-19 correctly`() {
        assertEquals("tio", SwedishNumberFormatter.toText(10))
        assertEquals("elva", SwedishNumberFormatter.toText(11))
        assertEquals("fjorton", SwedishNumberFormatter.toText(14))
        assertEquals("nitton", SwedishNumberFormatter.toText(19))
    }

    @Test
    fun `toText formats tens correctly`() {
        assertEquals("tjugo", SwedishNumberFormatter.toText(20))
        assertEquals("trettio", SwedishNumberFormatter.toText(30))
        assertEquals("fyrtio", SwedishNumberFormatter.toText(40))
        assertEquals("femtio", SwedishNumberFormatter.toText(50))
        assertEquals("sextio", SwedishNumberFormatter.toText(60))
        assertEquals("sjuttio", SwedishNumberFormatter.toText(70))
        assertEquals("åttio", SwedishNumberFormatter.toText(80)) // Critical check for previous bug
        assertEquals("nittio", SwedishNumberFormatter.toText(90))
    }

    @Test
    fun `toText formats compound numbers correctly`() {
        assertEquals("tjugoett", SwedishNumberFormatter.toText(21))
        assertEquals("trettiofem", SwedishNumberFormatter.toText(35))
        assertEquals("åttioåtta", SwedishNumberFormatter.toText(88))
        assertEquals("nittionio", SwedishNumberFormatter.toText(99))
    }
}
