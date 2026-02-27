package com.siffermastare.domain.generators

import com.siffermastare.domain.models.Question
import com.siffermastare.domain.validation.strategies.DecimalsEvaluationStrategy
import com.siffermastare.domain.validation.strategies.StandardNumberEvaluationStrategy
import com.siffermastare.domain.utils.SwedishNumberFormatter
import kotlin.random.Random

class DecimalsGenerator : NumberGenerator {

    override val evaluationStrategy = DecimalsEvaluationStrategy()

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
                decimalValue = Random.nextInt(100)
                decimalPartString = String.format("%02d", decimalValue)
            } else {
                // 0 to 9.
                decimalValue = Random.nextInt(10)
                decimalPartString = String.format("%d", decimalValue)
            }
            
            val targetValue = "$integerPart,$decimalPartString"
            val spokenText = formatSpokenText(integerPart, decimalPartString, decimalValue)
            
            // Build atoms list
            val atoms = mutableListOf<String>()
            atoms.addAll(StandardNumberEvaluationStrategy.decompose(integerPart))
            
            var i = 0
            while (i < decimalPartString.length && decimalPartString[i] == '0') {
                atoms.add("0")
                i++
            }
            if (decimalValue > 0) {
                atoms.addAll(StandardNumberEvaluationStrategy.decompose(decimalValue))
            }
            
            Question(
                targetValue = targetValue,
                spokenText = spokenText,
                visualHint = targetValue,
                atoms = atoms
            )
        }
    }

    private fun formatSpokenText(integerPart: Int, decimalPartStr: String, decimalValue: Int): String {
        val intText = SwedishNumberFormatter.toText(integerPart)
        
        val decText = if (decimalPartStr.length == 2 && decimalPartStr.startsWith("0")) {
             "noll ${SwedishNumberFormatter.toText(decimalValue)}"
        } else {
            SwedishNumberFormatter.toText(decimalValue)
        }
        
        return "$intText komma $decText"
    }
}
