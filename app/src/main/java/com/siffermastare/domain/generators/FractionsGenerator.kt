package com.siffermastare.domain.generators

import com.siffermastare.domain.models.Question

class FractionsGenerator : NumberGenerator {
    
    companion object {
        private const val MIN_DENOMINATOR = 2
        private const val MAX_DENOMINATOR = 10
        
        private val DENOMINATOR_NAMES = mapOf(
            2 to "halv",
            3 to "tredjedel",
            4 to "fjärdedel",
            5 to "femtedel",
            6 to "sjättedel",
            7 to "sjundedel",
            8 to "åttondel",
            9 to "niondel",
            10 to "tiondel"
        )
        
        private fun numberToText(n: Int): String {
            return when (n) {
                1 -> "ett" 
                2 -> "två"
                3 -> "tre"
                4 -> "fyra"
                5 -> "fem"
                6 -> "sex"
                7 -> "sju"
                8 -> "åtta"
                9 -> "nio"
                10 -> "tio"
                else -> n.toString()
            }
        }
    }
    
    override fun generateLesson(count: Int): List<Question> {
        return List(count) {
            val denominator = (MIN_DENOMINATOR..MAX_DENOMINATOR).random()
            val numerator = (1 until denominator).random()
            
            val targetValue = "$numerator/$denominator"
            val spokenText = formatSpokenText(numerator, denominator)
            
            Question(
                targetValue = targetValue,
                spokenText = spokenText,
                visualHint = targetValue
            )
        }
    }

    private fun formatSpokenText(numerator: Int, denominator: Int): String {
        val numStr = if (numerator == 1) "en" else numberToText(numerator)
        
        val baseDenom = DENOMINATOR_NAMES[denominator] ?: "${denominator}:del"
        val denStr = if (denominator == 2 && numerator > 1) "halvor" else baseDenom
        val suffix = if (numerator > 1 && denominator != 2) "ar" else ""
        
        return "$numStr $denStr$suffix"
    }
}
