package com.siffermastare.domain.generators

import com.siffermastare.domain.models.Question
import com.siffermastare.domain.evaluation.ExactMatchEvaluationStrategy

class PhoneNumberGenerator : NumberGenerator {

    override val evaluationStrategy = ExactMatchEvaluationStrategy()

    override fun generateLesson(count: Int): List<Question> {
        return List(count) {
            val prefixDigit = (0..9).random() // 07x
            val group1Val = (0..999).random()
            val group1 = group1Val.toString().padStart(3, '0')
            
            val group2Val = (0..99).random()
            val group2 = group2Val.toString().padStart(2, '0')
            
            val group3Val = (0..99).random()
            val group3 = group3Val.toString().padStart(2, '0')

            val targetValue = "07$prefixDigit$group1$group2$group3"

            // Spoken: "0 7 x, 1 2 3, XX, YY"
            val spokenPrefix = "0 7 $prefixDigit"
            
            // Group 1: 3 digits separate (e.g. 1 2 3)
            val spokenGroup1 = group1.map { it }.joinToString(" ")
            
            // Pairs: Use logic similar to decimals (explicit words)
            val spokenGroup2 = formatPair(group2Val)
            val spokenGroup3 = formatPair(group3Val)
            
            val spokenText = "$spokenPrefix, $spokenGroup1, $spokenGroup2, $spokenGroup3"

            Question(
                targetValue = targetValue,
                spokenText = spokenText,
                visualHint = "07$prefixDigit-$group1 $group2 $group3"
            )
        }
    }
    
    private fun formatPair(n: Int): String {
        // Explicitly handle leading zero or double zero logic
        // If < 10, say "noll X" (e.g. "noll fem", "noll noll")
        if (n < 10) {
            return "noll ${com.siffermastare.domain.utils.SwedishNumberFormatter.toText(n)}"
        }
        // Else just normal number (e.g. 10 -> "tio", 45 -> "fyrtiofem")
        // We use text to be consistent and avoid TTS ambiguity
        return com.siffermastare.domain.utils.SwedishNumberFormatter.toText(n)
    }
}
