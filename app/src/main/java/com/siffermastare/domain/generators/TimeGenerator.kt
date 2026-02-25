package com.siffermastare.domain.generators

import com.siffermastare.domain.models.Question
import com.siffermastare.domain.evaluation.DigitalTimeEvaluationStrategy
import kotlin.random.Random

/**
 * Generates random time strings in digital format (HH:MM).
 * Target value (input) is 4 digits (HHMM).
 * Populates Question.atoms using digital time decomposition.
 */
class TimeGenerator : NumberGenerator {

    override val evaluationStrategy = DigitalTimeEvaluationStrategy()

    override fun generateLesson(count: Int): List<Question> {
        return List(count) {
            val hour = Random.nextInt(0, 24)
            val minute = Random.nextInt(0, 60)
            
            // Format with leading zeros
            val hourStr = hour.toString().padStart(2, '0')
            val minuteStr = minute.toString().padStart(2, '0')
            
            val spokenText = "$hourStr:$minuteStr"
            val targetValue = "$hourStr$minuteStr"
            val atoms = decomposeTime(hourStr, minuteStr)
            
            Question(
                targetValue = targetValue,
                spokenText = spokenText,
                visualHint = null,
                atoms = atoms
            )
        }
    }

    companion object {
        /**
         * Decomposes a digital time into its concept atoms.
         * Hours and minutes are decomposed independently as two-digit parts.
         *
         * @param hourStr Two-digit hour string (e.g., "05", "14").
         * @param minuteStr Two-digit minute string (e.g., "00", "30").
         * @return List of atom ID strings.
         */
        fun decomposeTime(hourStr: String, minuteStr: String): List<String> {
            return decomposeTwoDigitPart(hourStr) + decomposeTwoDigitPart(minuteStr)
        }

        /**
         * Decomposes a two-digit part (HH or MM) into atoms.
         * - 00 → ["0", "0"] (noll noll)
         * - 01-09 → ["0", "X"] (noll X)
         * - 10-19 → ["N"] (teen, atomic)
         * - 20-99 → tens + optional ones
         */
        internal fun decomposeTwoDigitPart(partStr: String): List<String> {
            val valInt = partStr.toInt()
            val d1 = partStr[0].digitToInt()
            val d2 = partStr[1].digitToInt()

            val atoms = mutableListOf<String>()
            if (valInt in 1..9) { // 01-09 -> "noll X"
                if (d1 == 0) atoms.add("0")
                atoms.add(d2.toString())
            } else if (valInt == 0) { // 00 -> "noll noll"
                atoms.add("0")
                atoms.add("0")
            } else { // 10-99
                atoms.addAll(decomposeStandard(valInt))
            }
            return atoms
        }

        private fun decomposeStandard(number: Int): List<String> {
            val list = mutableListOf<String>()
            if (number == 0) {
                list.add("0")
                return list
            }

            if (number in 10..19) {
                // Teens are atomic (including 10)
                list.add(number.toString())
            } else {
                // 20-99
                val tens = (number / 10) * 10
                val ones = number % 10
                if (tens > 0) list.add(tens.toString())
                if (ones > 0) list.add(ones.toString())
            }
            return list
        }
    }
}
