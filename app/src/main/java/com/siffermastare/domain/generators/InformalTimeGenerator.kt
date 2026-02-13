package com.siffermastare.domain.generators

import com.siffermastare.domain.models.Question
import com.siffermastare.domain.evaluation.InformalTimeEvaluationStrategy
import kotlin.random.Random

class InformalTimeGenerator : NumberGenerator {

    override val evaluationStrategy = InformalTimeEvaluationStrategy()
    
    private val numbers = arrayOf(
        "noll", "ett", "två", "tre", "fyra", "fem", "sex", "sju", "åtta", "nio",
        "tio", "elva", "tolv", "tretton", "fjorton", "femton", "sexton", "sjutton", "arton", "nitton",
        "tjugo", "tjugoett", "tjugotvå", "tjugotre", "tjugofyra", "tjugofem", "tjugosex", "tjugosju", "tjugoåtta", "tjugonio"
    )

    override fun generateLesson(count: Int): List<Question> {
        return List(count) {
            val hour = Random.nextInt(0, 24)
            val minute = Random.nextInt(0, 60)

            val hourStr = hour.toString().padStart(2, '0')
            val minuteStr = minute.toString().padStart(2, '0')
            
            // Calculate alternative 12h/24h counterpart
            val altHour = (hour + 12) % 24
            val altHourStr = altHour.toString().padStart(2, '0')
            
            // Allow both answers (e.g. "1430|0230")
            val targetValue = "$hourStr$minuteStr|$altHourStr$minuteStr"
            
            val spokenText = formatInformalTime(hour, minute)
            val atoms = buildAtoms(hour, minute)
            
            Question(
                targetValue = targetValue,
                spokenText = spokenText,
                visualHint = null,
                atoms = atoms
            )
        }
    }

    fun formatInformalTime(hour: Int, minute: Int): String {
        val currentHour12 = if (hour % 12 == 0) 12 else hour % 12
        val nextHourVal = (hour + 1)
        val nextHour12 = if (nextHourVal % 12 == 0) 12 else nextHourVal % 12

        val currentHourName = numbers[currentHour12]
        val nextHourName = numbers[nextHour12]

        return when (minute) {
            0 -> "klockan $currentHourName"
            in 1..20 -> {
                if (minute == 15) "kvart över $currentHourName"
                else "${numbers[minute]} över $currentHourName"
            }
            in 21..29 -> {
                val diff = 30 - minute
                "${numbers[diff]} i halv $nextHourName"
            }
            30 -> "halv $nextHourName"
            in 31..39 -> {
                val diff = minute - 30
                "${numbers[diff]} över halv $nextHourName"
            }
            else -> { // 40..59
                if (minute == 45) "kvart i $nextHourName"
                else {
                    val diff = 60 - minute
                    "${numbers[diff]} i $nextHourName"
                }
            }
        }
    }

    /**
     * Builds the list of concept atoms for the given informal time.
     * Atom ordering matches the spoken Swedish phrase.
     */
    fun buildAtoms(hour: Int, minute: Int): List<String> {
        val currentHour12 = if (hour % 12 == 0) 12 else hour % 12
        val nextHourVal = (hour + 1)
        val nextHour12 = if (nextHourVal % 12 == 0) 12 else nextHourVal % 12

        return when (minute) {
            0 -> listOf(currentHour12.toString())
            15 -> listOf("#kvart", "#over", currentHour12.toString())
            45 -> listOf("#kvart", "#i", nextHour12.toString())
            30 -> listOf("#halv", nextHour12.toString())
            in 1..14, in 16..20 -> {
                // "X över CurrentHour"
                listOf(minute.toString(), "#over", currentHour12.toString())
            }
            in 21..29 -> {
                // "X i halv NextHour"
                val diff = 30 - minute
                listOf(diff.toString(), "#i", "#halv", nextHour12.toString())
            }
            in 31..39 -> {
                // "X över halv NextHour"
                val diff = minute - 30
                listOf(diff.toString(), "#over", "#halv", nextHour12.toString())
            }
            else -> { // 40..44, 46..59
                // "X i NextHour"
                val diff = 60 - minute
                listOf(diff.toString(), "#i", nextHour12.toString())
            }
        }
    }
}
