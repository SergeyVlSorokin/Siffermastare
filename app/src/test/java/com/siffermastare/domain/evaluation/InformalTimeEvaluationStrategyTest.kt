package com.siffermastare.domain.evaluation

import com.siffermastare.domain.models.Question
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InformalTimeEvaluationStrategyTest {

    private val strategy = InformalTimeEvaluationStrategy()

    private fun q(target: String, atoms: List<String>): Question =
        Question(targetValue = target, spokenText = "", atoms = atoms)

    // ===== Section 1: Exact Hour — "Klockan X" =====

    // "Klockan två" (0200), Atoms: [2]

    @Test
    fun `1_1 Klockan tva - exact match 0200`() {
        val result = strategy.evaluate("0200", q("0200|1400", listOf("2")))
        assertTrue(result.isCorrect)
        assertEquals(mapOf("2" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `1_2 Klockan tva - 24h equivalent 1400`() {
        val result = strategy.evaluate("1400", q("0200|1400", listOf("2")))
        assertTrue(result.isCorrect)
        assertEquals(mapOf("2" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `1_3 Klockan tva - leading zero skipped 200`() {
        val result = strategy.evaluate("200", q("0200|1400", listOf("2")))
        assertTrue(result.isCorrect)
        assertEquals(mapOf("2" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `1_4 Klockan tva - wrong hour 0300`() {
        val result = strategy.evaluate("0300", q("0200|1400", listOf("2")))
        assertFalse(result.isCorrect)
        assertEquals(mapOf("2" to listOf(false)), result.atomUpdates)
    }

    @Test
    fun `1_5 Klockan tva - extra minutes 0205`() {
        val result = strategy.evaluate("0205", q("0200|1400", listOf("2")))
        assertFalse(result.isCorrect)
        assertEquals(mapOf("2" to listOf(true)), result.atomUpdates)
    }

    // "Klockan tolv" (1200), Atoms: [12]

    @Test
    fun `1_6 Klockan tolv - exact 1200`() {
        val result = strategy.evaluate("1200", q("1200|0000", listOf("12")))
        assertTrue(result.isCorrect)
        assertEquals(mapOf("12" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `1_7 Klockan tolv - midnight 0000`() {
        val result = strategy.evaluate("0000", q("1200|0000", listOf("12")))
        assertTrue(result.isCorrect)
        assertEquals(mapOf("12" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `1_8 Klockan tolv - leading zero 000`() {
        val result = strategy.evaluate("000", q("1200|0000", listOf("12")))
        assertTrue(result.isCorrect)
        assertEquals(mapOf("12" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `1_9 Klockan tolv - 2400`() {
        val result = strategy.evaluate("2400", q("1200|0000", listOf("12")))
        assertTrue(result.isCorrect)
        assertEquals(mapOf("12" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `1_10 Klockan tolv - wrong hour 0100`() {
        val result = strategy.evaluate("0100", q("1200|0000", listOf("12")))
        assertFalse(result.isCorrect)
        assertEquals(mapOf("12" to listOf(false)), result.atomUpdates)
    }

    // ===== Section 2: Quarter Past — "Kvart över X" =====

    // "Kvart över fyra" (0415), Atoms: [#kvart, #over, 4]

    @Test
    fun `2_1 Kvart over fyra - exact 0415`() {
        val result = strategy.evaluate("0415", q("0415|1615", listOf("#kvart", "#over", "4")))
        assertTrue(result.isCorrect)
        assertEquals(mapOf("#kvart" to listOf(true), "#over" to listOf(true), "4" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `2_2 Kvart over fyra - 24h 1615`() {
        val result = strategy.evaluate("1615", q("0415|1615", listOf("#kvart", "#over", "4")))
        assertTrue(result.isCorrect)
        assertEquals(mapOf("#kvart" to listOf(true), "#over" to listOf(true), "4" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `2_3 Kvart over fyra - wrong minute 0430`() {
        val result = strategy.evaluate("0430", q("0415|1615", listOf("#kvart", "#over", "4")))
        assertFalse(result.isCorrect)
        // #kvart fails → #over is SKIP
        assertEquals(mapOf("#kvart" to listOf(false), "4" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `2_4 Kvart over fyra - opposite direction 0445`() {
        val result = strategy.evaluate("0445", q("0415|1615", listOf("#kvart", "#over", "4")))
        assertFalse(result.isCorrect)
        // #kvart=true, direction=i≠over → false
        assertEquals(mapOf("#kvart" to listOf(true), "#over" to listOf(false), "4" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `2_5 Kvart over fyra - wrong hour 0515`() {
        val result = strategy.evaluate("0515", q("0415|1615", listOf("#kvart", "#over", "4")))
        assertFalse(result.isCorrect)
        // #kvart=true, #over=true, hour: 5≠4 and 5≠3 → false
        assertEquals(mapOf("#kvart" to listOf(true), "#over" to listOf(true), "4" to listOf(false)), result.atomUpdates)
    }

    // ===== Section 3: Half Past — "Halv X" =====

    // "Halv tre" (0230), Atoms: [#halv, 3]

    @Test
    fun `3_1 Halv tre - exact 0230`() {
        val result = strategy.evaluate("0230", q("0230|1430", listOf("#halv", "3")))
        assertTrue(result.isCorrect)
        assertEquals(mapOf("#halv" to listOf(true), "3" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `3_2 Halv tre - 24h 1430`() {
        val result = strategy.evaluate("1430", q("0230|1430", listOf("#halv", "3")))
        assertTrue(result.isCorrect)
        // 14%12=2=(3-1) → true
        assertEquals(mapOf("#halv" to listOf(true), "3" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `3_3 Halv tre - wrong hour but literal match 0330`() {
        val result = strategy.evaluate("0330", q("0230|1430", listOf("#halv", "3")))
        assertFalse(result.isCorrect)
        // 3=literal match → true, 30∈[21..39] → halv true
        assertEquals(mapOf("#halv" to listOf(true), "3" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `3_4 Halv tre - wrong hour 0430`() {
        val result = strategy.evaluate("0430", q("0230|1430", listOf("#halv", "3")))
        assertFalse(result.isCorrect)
        // 4≠3 and 4≠2 → false
        assertEquals(mapOf("#halv" to listOf(true), "3" to listOf(false)), result.atomUpdates)
    }

    @Test
    fun `3_5 Halv tre - not in halv zone 0215`() {
        val result = strategy.evaluate("0215", q("0230|1430", listOf("#halv", "3")))
        assertFalse(result.isCorrect)
        // 15∉[21..39] → halv false, 2=(3-1) → hour true
        assertEquals(mapOf("#halv" to listOf(false), "3" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `3_6 Halv tre - exact hour 0200`() {
        val result = strategy.evaluate("0200", q("0230|1430", listOf("#halv", "3")))
        assertFalse(result.isCorrect)
        // 0∉[21..39] → halv false, 2=(3-1) → hour true
        assertEquals(mapOf("#halv" to listOf(false), "3" to listOf(true)), result.atomUpdates)
    }

    // ===== Section 4: Quarter To — "Kvart i X" =====

    // "Kvart i fem" (0445), Atoms: [#kvart, #i, 5]

    @Test
    fun `4_1 Kvart i fem - exact 0445`() {
        val result = strategy.evaluate("0445", q("0445|1645", listOf("#kvart", "#i", "5")))
        assertTrue(result.isCorrect)
        assertEquals(mapOf("#kvart" to listOf(true), "#i" to listOf(true), "5" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `4_2 Kvart i fem - opposite direction 0515`() {
        val result = strategy.evaluate("0515", q("0445|1645", listOf("#kvart", "#i", "5")))
        assertFalse(result.isCorrect)
        // #kvart true, dir over≠i → false, 5=literal → true
        assertEquals(mapOf("#kvart" to listOf(true), "#i" to listOf(false), "5" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `4_3 Kvart i fem - halv zone 0430`() {
        val result = strategy.evaluate("0430", q("0445|1645", listOf("#kvart", "#i", "5")))
        assertFalse(result.isCorrect)
        // #kvart false → SKIP #i, 4=(5-1) → hour true
        assertEquals(mapOf("#kvart" to listOf(false), "5" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `4_4 Kvart i fem - wrong all 0350`() {
        val result = strategy.evaluate("0350", q("0445|1645", listOf("#kvart", "#i", "5")))
        assertFalse(result.isCorrect)
        // 50→#kvart false, SKIP #i, 3≠5 and 3≠4 → false
        assertEquals(mapOf("#kvart" to listOf(false), "5" to listOf(false)), result.atomUpdates)
    }

    // ===== Section 5: Minutes Past — "X över Y" =====

    // "Fem över två" (0205), Atoms: [5, #over, 2]

    @Test
    fun `5_1 Fem over tva - exact 0205`() {
        val result = strategy.evaluate("0205", q("0205|1405", listOf("5", "#over", "2")))
        assertTrue(result.isCorrect)
        assertEquals(mapOf("5" to listOf(true), "#over" to listOf(true), "2" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `5_2 Fem over tva - 24h 1405`() {
        val result = strategy.evaluate("1405", q("0205|1405", listOf("5", "#over", "2")))
        assertTrue(result.isCorrect)
        assertEquals(mapOf("5" to listOf(true), "#over" to listOf(true), "2" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `5_3 Fem over tva - cross direction 0155`() {
        val result = strategy.evaluate("0155", q("0205|1405", listOf("5", "#over", "2")))
        assertFalse(result.isCorrect)
        // derive(55)=5 → minute true, dir i≠over → false, 1=(2-1) → hour true
        assertEquals(mapOf("5" to listOf(true), "#over" to listOf(false), "2" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `5_4 Fem over tva - wrong minute 0217`() {
        val result = strategy.evaluate("0217", q("0205|1405", listOf("5", "#over", "2")))
        assertFalse(result.isCorrect)
        // derive(17)=17≠5 → minute false, SKIP #over, 2=literal → hour true
        assertEquals(mapOf("5" to listOf(false), "2" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `5_5 Fem over tva - cross dir wrong hour 0455`() {
        val result = strategy.evaluate("0455", q("0205|1405", listOf("5", "#over", "2")))
        assertFalse(result.isCorrect)
        // derive(55)=5 → minute true, dir i≠over → false, 4≠2 and 4≠1 → false
        assertEquals(mapOf("5" to listOf(true), "#over" to listOf(false), "2" to listOf(false)), result.atomUpdates)
    }

    @Test
    fun `5_6 Fem over tva - wrong hour same dir 0305`() {
        val result = strategy.evaluate("0305", q("0205|1405", listOf("5", "#over", "2")))
        assertFalse(result.isCorrect)
        // 05=semantic → minute true, dir over → true, 3≠2 and 3≠1 → false
        assertEquals(mapOf("5" to listOf(true), "#over" to listOf(true), "2" to listOf(false)), result.atomUpdates)
    }

    // ===== Section 6: Minutes To — "X i Y" =====

    // "Fem i tre" (0255), Atoms: [5, #i, 3]

    @Test
    fun `6_1 Fem i tre - exact 0255`() {
        val result = strategy.evaluate("0255", q("0255|1455", listOf("5", "#i", "3")))
        assertTrue(result.isCorrect)
        assertEquals(mapOf("5" to listOf(true), "#i" to listOf(true), "3" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `6_2 Fem i tre - 24h 1455`() {
        val result = strategy.evaluate("1455", q("0255|1455", listOf("5", "#i", "3")))
        assertTrue(result.isCorrect)
        assertEquals(mapOf("5" to listOf(true), "#i" to listOf(true), "3" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `6_3 Fem i tre - cross direction 0305`() {
        val result = strategy.evaluate("0305", q("0255|1455", listOf("5", "#i", "3")))
        assertFalse(result.isCorrect)
        // derive(05)=5 → minute true, dir over≠i → false, 3=literal → true
        assertEquals(mapOf("5" to listOf(true), "#i" to listOf(false), "3" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `6_4 Fem i tre - wrong minute 0250`() {
        val result = strategy.evaluate("0250", q("0255|1455", listOf("5", "#i", "3")))
        assertFalse(result.isCorrect)
        // derive(50)=10≠5 → minute false, SKIP #i, 2=(3-1) → true
        assertEquals(mapOf("5" to listOf(false), "3" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `6_5 Fem i tre - wrong hour 0455`() {
        val result = strategy.evaluate("0455", q("0255|1455", listOf("5", "#i", "3")))
        assertFalse(result.isCorrect)
        // derive(55)=5 → true, dir i → true, 4≠3 and 4≠2 → false
        assertEquals(mapOf("5" to listOf(true), "#i" to listOf(true), "3" to listOf(false)), result.atomUpdates)
    }

    // ===== Section 7: Minutes to Half — "X i halv Y" =====

    // "Fem i halv tre" (0225), Atoms: [5, #i, #halv, 3]

    @Test
    fun `7_1 Fem i halv tre - exact 0225`() {
        val result = strategy.evaluate("0225", q("0225|1425", listOf("5", "#i", "#halv", "3")))
        assertTrue(result.isCorrect)
        assertEquals(mapOf("5" to listOf(true), "#i" to listOf(true), "#halv" to listOf(true), "3" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `7_2 Fem i halv tre - 24h 1425`() {
        val result = strategy.evaluate("1425", q("0225|1425", listOf("5", "#i", "#halv", "3")))
        assertTrue(result.isCorrect)
        assertEquals(mapOf("5" to listOf(true), "#i" to listOf(true), "#halv" to listOf(true), "3" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `7_3 Fem i halv tre - opposite direction 0235`() {
        val result = strategy.evaluate("0235", q("0225|1425", listOf("5", "#i", "#halv", "3")))
        assertFalse(result.isCorrect)
        // derive(35)=5 → minute true, dir over≠i → false, 35∈halv → true, 2=(3-1) → true
        assertEquals(mapOf("5" to listOf(true), "#i" to listOf(false), "#halv" to listOf(true), "3" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `7_4 Fem i halv tre - outside halv 0205`() {
        val result = strategy.evaluate("0205", q("0225|1425", listOf("5", "#i", "#halv", "3")))
        assertFalse(result.isCorrect)
        // derive(05)=5 → true, dir over≠i → false, 05∉halv → false, 2=(3-1) → true
        assertEquals(mapOf("5" to listOf(true), "#i" to listOf(false), "#halv" to listOf(false), "3" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `7_5 Fem i halv tre - wrong hour in halv 0325`() {
        val result = strategy.evaluate("0325", q("0225|1425", listOf("5", "#i", "#halv", "3")))
        assertFalse(result.isCorrect)
        // derive(25)=5 → true, dir i → true, 25∈halv → true, 3=literal → true
        assertEquals(mapOf("5" to listOf(true), "#i" to listOf(true), "#halv" to listOf(true), "3" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `7_6 Fem i halv tre - wrong minute 0217`() {
        val result = strategy.evaluate("0217", q("0225|1425", listOf("5", "#i", "#halv", "3")))
        assertFalse(result.isCorrect)
        // derive(17)=17≠5 → false, SKIP #i, 17∉halv → false, 2=(3-1) → true
        assertEquals(mapOf("5" to listOf(false), "#halv" to listOf(false), "3" to listOf(true)), result.atomUpdates)
    }

    // ===== Section 8: Minutes Past Half — "X över halv Y" =====

    // "Fem över halv tre" (0235), Atoms: [5, #over, #halv, 3]

    @Test
    fun `8_1 Fem over halv tre - exact 0235`() {
        val result = strategy.evaluate("0235", q("0235|1435", listOf("5", "#over", "#halv", "3")))
        assertTrue(result.isCorrect)
        assertEquals(mapOf("5" to listOf(true), "#over" to listOf(true), "#halv" to listOf(true), "3" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `8_2 Fem over halv tre - opposite dir 0225`() {
        val result = strategy.evaluate("0225", q("0235|1435", listOf("5", "#over", "#halv", "3")))
        assertFalse(result.isCorrect)
        // derive(25)=5 → true, dir i≠over → false, 25∈halv → true, 2=(3-1) → true
        assertEquals(mapOf("5" to listOf(true), "#over" to listOf(false), "#halv" to listOf(true), "3" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `8_3 Fem over halv tre - outside halv 0205`() {
        val result = strategy.evaluate("0205", q("0235|1435", listOf("5", "#over", "#halv", "3")))
        assertFalse(result.isCorrect)
        // derive(05)=5 → true, dir over → true, 05∉halv → false, 2=(3-1) → true
        assertEquals(mapOf("5" to listOf(true), "#over" to listOf(true), "#halv" to listOf(false), "3" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `8_4 Fem over halv tre - wrong hour 0535`() {
        val result = strategy.evaluate("0535", q("0235|1435", listOf("5", "#over", "#halv", "3")))
        assertFalse(result.isCorrect)
        // derive(35)=5 → true, dir over → true, 35∈halv → true, 5≠3 and 5≠2 → false
        assertEquals(mapOf("5" to listOf(true), "#over" to listOf(true), "#halv" to listOf(true), "3" to listOf(false)), result.atomUpdates)
    }

    // ===== Section 9: Larger Minutes — "Tio över X" =====

    // "Tio över åtta" (0810), Atoms: [10, #over, 8]

    @Test
    fun `9_1 Tio over atta - exact 0810`() {
        val result = strategy.evaluate("0810", q("0810|2010", listOf("10", "#over", "8")))
        assertTrue(result.isCorrect)
        assertEquals(mapOf("10" to listOf(true), "#over" to listOf(true), "8" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `9_2 Tio over atta - wrong minute 0805`() {
        val result = strategy.evaluate("0805", q("0810|2010", listOf("10", "#over", "8")))
        assertFalse(result.isCorrect)
        // derive(05)=5≠10 → false, SKIP #over, 8=literal → true
        assertEquals(mapOf("10" to listOf(false), "8" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `9_3 Tio over atta - cross direction 0850`() {
        val result = strategy.evaluate("0850", q("0810|2010", listOf("10", "#over", "8")))
        assertFalse(result.isCorrect)
        // derive(50)=10 → true, dir i≠over → false, 8=literal → true
        assertEquals(mapOf("10" to listOf(true), "#over" to listOf(false), "8" to listOf(true)), result.atomUpdates)
    }

    // ===== Section 10: Larger Minutes — "Tjugo i X" =====

    // "Tjugo i tre" (0240), Atoms: [20, #i, 3]

    @Test
    fun `10_1 Tjugo i tre - exact 0240`() {
        val result = strategy.evaluate("0240", q("0240|1440", listOf("20", "#i", "3")))
        assertTrue(result.isCorrect)
        assertEquals(mapOf("20" to listOf(true), "#i" to listOf(true), "3" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `10_2 Tjugo i tre - opposite dir 0220`() {
        val result = strategy.evaluate("0220", q("0240|1440", listOf("20", "#i", "3")))
        assertFalse(result.isCorrect)
        // derive(20)=20 → true, dir over≠i → false, 2=(3-1) → true
        assertEquals(mapOf("20" to listOf(true), "#i" to listOf(false), "3" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `10_3 Tjugo i tre - wrong minute 0250`() {
        val result = strategy.evaluate("0250", q("0240|1440", listOf("20", "#i", "3")))
        assertFalse(result.isCorrect)
        // derive(50)=10≠20 → false, SKIP #i, 2=(3-1) → true
        assertEquals(mapOf("20" to listOf(false), "3" to listOf(true)), result.atomUpdates)
    }

    // ===== Section 11: Duplicate Atoms — "Tio över tio" =====

    // "Tio över tio" (1010), Atoms: [10(min), #over, 10(hr)]

    @Test
    fun `11_1 Tio over tio - exact 1010`() {
        val result = strategy.evaluate("1010", q("1010|2210", listOf("10", "#over", "10")))
        assertTrue(result.isCorrect)
        // Both 10s match: [true, true]
        assertEquals(mapOf("10" to listOf(true, true), "#over" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `11_2 Tio over tio - minute ok hour wrong 0110`() {
        val result = strategy.evaluate("0110", q("1010|2210", listOf("10", "#over", "10")))
        assertFalse(result.isCorrect)
        // min 10 → true, dir over → true, hr: 1≠10 and 1≠9 → false
        assertEquals(mapOf("10" to listOf(true, false), "#over" to listOf(true)), result.atomUpdates)
    }

    @Test
    fun `11_3 Tio over tio - minute wrong hour ok 1005`() {
        val result = strategy.evaluate("1005", q("1010|2210", listOf("10", "#over", "10")))
        assertFalse(result.isCorrect)
        // derive(05)=5≠10 → minute false, SKIP #over, hr: 10=literal → true
        assertEquals(mapOf("10" to listOf(false, true)), result.atomUpdates)
    }
}
