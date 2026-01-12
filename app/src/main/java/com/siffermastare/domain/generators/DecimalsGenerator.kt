package com.siffermastare.domain.generators

import com.siffermastare.domain.models.Question
import com.siffermastare.domain.utils.SwedishNumberFormatter
import kotlin.random.Random

class DecimalsGenerator : NumberGenerator {

    override fun generateLesson(count: Int): List<Question> {
        return List(count) {
            // Determine if 1 or 2 decimal places (50/50 chance)
            val isTwoDecimals = Random.nextBoolean()
            
            // Integer part: 0 to 99
            val integerPart = Random.nextInt(100)
            
            val decimalPartString: String
            val decimalValue: Int
            
            if (isTwoDecimals) {
                // 00 to 99. 
                // Random.nextInt(100) -> 0..99
                decimalValue = Random.nextInt(100)
                decimalPartString = String.format("%02d", decimalValue)
            } else {
                // 0 to 9.
                decimalValue = Random.nextInt(10)
                decimalPartString = String.format("%d", decimalValue)
            }
            
            val targetValue = "$integerPart,$decimalPartString"
            val spokenText = formatSpokenText(integerPart, decimalPartString, decimalValue)
            
            Question(
                targetValue = targetValue,
                spokenText = spokenText,
                visualHint = targetValue
            )
        }
    }

    private fun formatSpokenText(integerPart: Int, decimalPartStr: String, decimalValue: Int): String {
        val intText = SwedishNumberFormatter.toText(integerPart)
        
        // Decimal part logic
        // Rule: "komma" separator
        // Rule: 0,01 -> " ... komma noll ett"
        // Rule: 3,14 -> "... komma fjorton"
        
        val decText = if (decimalPartStr.length == 2 && decimalPartStr.startsWith("0")) {
            // Case 0,05 -> "noll fem"
            // Case 0,00 is unlikely/invalid for this drill usually but if generated: "noll noll"
             "noll ${SwedishNumberFormatter.toText(decimalValue)}"
        } else {
            // Case 0,5 -> "fem"
            // Case 0,14 -> "fjorton"
            // Case 0,10 -> "tio"
            SwedishNumberFormatter.toText(decimalValue)
        }
        
        return "$intText komma $decText"
    }
}
