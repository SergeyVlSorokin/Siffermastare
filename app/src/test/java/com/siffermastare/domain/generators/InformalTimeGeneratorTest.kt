package com.siffermastare.domain.generators

import org.junit.Assert.assertEquals
import org.junit.Test

class InformalTimeGeneratorTest {

    private val generator = InformalTimeGenerator()

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
}
