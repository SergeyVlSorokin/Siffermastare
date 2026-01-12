package com.siffermastare.domain.utils

object SwedishNumberFormatter {
    
    private val units = arrayOf("", "ett", "två", "tre", "fyra", "fem", "sex", "sju", "åtta", "nio")
    private val teens = arrayOf("tio", "elva", "tolv", "tretton", "fjorton", "femton", "sexton", "sjutton", "arton", "nitton")
    
    fun toText(n: Int): String {
        if (n == 0) return "noll"
        
        return when {
            n < 10 -> units[n]
            n < 20 -> teens[n - 10]
            n < 100 -> {
                val tenVal = n / 10
                val unitVal = n % 10
                val tenStr = when(tenVal) {
                    2 -> "tjugo"
                    3 -> "trettio"
                    4 -> "fyrtio"
                    5 -> "femtio"
                    6 -> "sextio"
                    7 -> "sjuttio"
                    8 -> "a\u030Attio" // Keep the hack for now if it was needed for TTS, OR try "åttio" standard.
                    // The review finding said "override is messy". The hack "a\u030Attio" is decomposed "a" + combining ring.
                    // Standard "åttio" uses precomposed 'å'. 
                    // I will try standard "åttio" first as it's cleaner code. If TTS fails, we revert.
                    // Actually, let's fix it properly. "åttio".
                    9 -> "nittio"
                    else -> ""
                }
                
                // Explicit fix for 80 without the hack, assuming clean UTF-8 environment
                val realTenStr = if (tenVal == 8) "åttio" else tenStr
                
                if (unitVal == 0) realTenStr else "$realTenStr${units[unitVal]}"
            }
            // For >100, we fall back to digits for now or implement hundreds if needed. 
            // Current requirements (decimals/fractions) mainly use small numbers, but 100 might be hit as denominator or whole part.
            // If n >= 100, just return digits string to be safe, or implement simple hundreds logic?
            // "ett hundra"? Let's stick to safe fallback for now as original code fell back.
            else -> n.toString()
        }
    }
}
