package com.siffermastare.domain.generators

import org.junit.Assert.assertEquals
import org.junit.Test

class InformalTimeGeneratorTest {

    private val generator = InformalTimeGenerator()

    // ===== formatInformalTime tests =====

    @Test
    fun testExactHours() {
        assertEquals("klockan två", generator.formatInformalTime(2, 0))
        assertEquals("klockan två", generator.formatInformalTime(14, 0))
    }

    @Test
    fun testPastHour() {
        assertEquals("fem över två", generator.formatInformalTime(14, 5))
        assertEquals("tio över två", generator.formatInformalTime(14, 10))
        assertEquals("tretton över två", generator.formatInformalTime(14, 13))
    }

    @Test
    fun testQuarterPast() {
        assertEquals("kvart över två", generator.formatInformalTime(14, 15))
    }

    @Test
    fun testToHalf() {
        // 14:20 -> "tjugo över två" (Refactored to natural Swedish)
        assertEquals("tjugo över två", generator.formatInformalTime(14, 20))
        // 14:25 -> "fem i halv tre"
        assertEquals("fem i halv tre", generator.formatInformalTime(14, 25))
    }

    @Test
    fun testHalf() {
        assertEquals("halv tre", generator.formatInformalTime(14, 30))
    }

    @Test
    fun testPastHalf() {
        // 14:35 -> "fem över halv tre"
        assertEquals("fem över halv tre", generator.formatInformalTime(14, 35))
        // 14:36 -> "sex över halv tre" (Refactored to natural Swedish, previously 'tjugofyra i tre')
        assertEquals("sex över halv tre", generator.formatInformalTime(14, 36))
    }

    @Test
    fun testToNextHour() {
        // 14:40 -> "tjugo i tre"
        assertEquals("tjugo i tre", generator.formatInformalTime(14, 40))
        // 14:50 -> "tio i tre"
        assertEquals("tio i tre", generator.formatInformalTime(14, 50))
        // 14:55 -> "fem i tre"
        assertEquals("fem i tre", generator.formatInformalTime(14, 55))
    }

    @Test
    fun testQuarterTo() {
        assertEquals("kvart i tre", generator.formatInformalTime(14, 45))
    }

    @Test
    fun testGenerateLesson_createsPipeDelimitedTargets() {
        val questions = generator.generateLesson(5)
        assertEquals(5, questions.size)
        
        questions.forEach { q ->
            // Target should contain pipe
            assert(q.targetValue.contains("|")) { "Target value ${q.targetValue} should contain pipe" }
            
            val parts = q.targetValue.split("|")
            assertEquals(2, parts.size)
            
            // Check that parts are 4 digits
            assert(parts[0].length == 4)
            assert(parts[1].length == 4)
        }
    }

    // ===== buildAtoms tests =====

    @Test
    fun testAtoms_exactHour() {
        // "klockan två" (hour=2, minute=0) → [2]
        assertEquals(listOf("2"), generator.buildAtoms(2, 0))
        // 24h equivalent
        assertEquals(listOf("2"), generator.buildAtoms(14, 0))
    }

    @Test
    fun testAtoms_exactHour_tolv() {
        // "klockan tolv" (hour=0, minute=0) → [12]
        assertEquals(listOf("12"), generator.buildAtoms(0, 0))
        assertEquals(listOf("12"), generator.buildAtoms(12, 0))
    }

    @Test
    fun testAtoms_kvartOver() {
        // "kvart över fyra" (hour=4, minute=15) → [#kvart, #over, 4]
        assertEquals(listOf("#kvart", "#over", "4"), generator.buildAtoms(4, 15))
        assertEquals(listOf("#kvart", "#over", "4"), generator.buildAtoms(16, 15))
    }

    @Test
    fun testAtoms_kvartI() {
        // "kvart i fem" (hour=4, minute=45) → [#kvart, #i, 5]
        assertEquals(listOf("#kvart", "#i", "5"), generator.buildAtoms(4, 45))
    }

    @Test
    fun testAtoms_halv() {
        // "halv tre" (hour=2, minute=30) → [#halv, 3]
        assertEquals(listOf("#halv", "3"), generator.buildAtoms(2, 30))
        assertEquals(listOf("#halv", "3"), generator.buildAtoms(14, 30))
    }

    @Test
    fun testAtoms_minutesOver() {
        // "fem över två" (hour=2, minute=5) → [5, #over, 2]
        assertEquals(listOf("5", "#over", "2"), generator.buildAtoms(2, 5))
        // "tio över åtta" (hour=8, minute=10) → [10, #over, 8]
        assertEquals(listOf("10", "#over", "8"), generator.buildAtoms(8, 10))
        // "tjugo över två" (hour=14, minute=20) → [20, #over, 2]
        assertEquals(listOf("20", "#over", "2"), generator.buildAtoms(14, 20))
    }

    @Test
    fun testAtoms_minutesI() {
        // "fem i tre" (hour=2, minute=55) → [5, #i, 3]
        assertEquals(listOf("5", "#i", "3"), generator.buildAtoms(2, 55))
        // "tjugo i tre" (hour=2, minute=40) → [20, #i, 3]
        assertEquals(listOf("20", "#i", "3"), generator.buildAtoms(2, 40))
    }

    @Test
    fun testAtoms_minutesIHalv() {
        // "fem i halv tre" (hour=2, minute=25) → [5, #i, #halv, 3]
        assertEquals(listOf("5", "#i", "#halv", "3"), generator.buildAtoms(2, 25))
        assertEquals(listOf("5", "#i", "#halv", "3"), generator.buildAtoms(14, 25))
    }

    @Test
    fun testAtoms_minutesOverHalv() {
        // "fem över halv tre" (hour=2, minute=35) → [5, #over, #halv, 3]
        assertEquals(listOf("5", "#over", "#halv", "3"), generator.buildAtoms(2, 35))
        assertEquals(listOf("5", "#over", "#halv", "3"), generator.buildAtoms(14, 35))
    }

    @Test
    fun testAtoms_duplicateAtom() {
        // "tio över tio" (hour=10, minute=10) → [10, #over, 10]
        assertEquals(listOf("10", "#over", "10"), generator.buildAtoms(10, 10))
    }

    @Test
    fun testGenerateLesson_populatesAtoms() {
        val questions = generator.generateLesson(10)
        questions.forEach { q ->
            assert(q.atoms.isNotEmpty()) { "Atoms should not be empty for ${q.spokenText}" }
        }
    }
}
