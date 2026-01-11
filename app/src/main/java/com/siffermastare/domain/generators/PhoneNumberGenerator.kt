package com.siffermastare.domain.generators

import com.siffermastare.domain.models.Question

class PhoneNumberGenerator : NumberGenerator {
    override fun generateLesson(count: Int): List<Question> {
        return List(count) {
            val prefixDigit = (0..9).random() // 07x
            val group1 = (0..999).random().toString().padStart(3, '0')
            val group2 = (0..99).random().toString().padStart(2, '0')
            val group3 = (0..99).random().toString().padStart(2, '0')

            val targetValue = "07$prefixDigit$group1$group2$group3"

            // Spoken: "0 7 x, 1 2 3, 45, 67"
            val spokenPrefix = "0 7 $prefixDigit"
            val spokenGroup1 = group1.map { it }.joinToString(" ")
            val spokenText = "$spokenPrefix, $spokenGroup1, $group2, $group3"

            Question(
                targetValue = targetValue,
                spokenText = spokenText,
                visualHint = "07$prefixDigit-$group1 $group2 $group3"
            )
        }
    }
}
