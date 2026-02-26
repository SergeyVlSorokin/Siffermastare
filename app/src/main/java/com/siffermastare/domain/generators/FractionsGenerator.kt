package com.siffermastare.domain.generators

import com.siffermastare.domain.models.Question
import com.siffermastare.domain.evaluation.FractionsEvaluationStrategy
import com.siffermastare.domain.utils.SwedishNumberFormatter

class FractionsGenerator : NumberGenerator {
    
    override val evaluationStrategy = FractionsEvaluationStrategy()
    
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
                visualHint = targetValue,
                atoms = listOf(numerator.toString(), "ord:$denominator")
            )
        }
    }

    internal fun formatSpokenText(numerator: Int, denominator: Int): String {
        val numStr = if (numerator == 1) "en" else SwedishNumberFormatter.toText(numerator)
        
        val denStr = DENOMINATOR_NAMES[denominator] ?: "${denominator}:del"
        val suffix = if (numerator > 1) "ar" else ""
        
        return "$numStr $denStr$suffix"
    }
}
