package com.siffermastare.domain.generators

import com.siffermastare.domain.models.Question
import kotlin.random.Random

class InformalTimeGenerator : NumberGenerator {
    
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
            // e.g. if 14:30, alt is 02:30. If 02:30, alt is 14:30.
            val altHour = (hour + 12) % 24
            val altHourStr = altHour.toString().padStart(2, '0')
            
            // Allow both answers (e.g. "1430|0230")
            val targetValue = "$hourStr$minuteStr|$altHourStr$minuteStr"
            
            val spokenText = formatInformalTime(hour, minute)
            
            Question(
                targetValue = targetValue,
                spokenText = spokenText,
                visualHint = null
            )
        }
    }

    fun formatInformalTime(hour: Int, minute: Int): String {
        // Convert to 12-hour basis for the base hour
        // If hour is 0 or 12 -> 12. 13 -> 1.
        val currentHour12 = if (hour % 12 == 0) 12 else hour % 12
        
        // Define next hour (also 12-hour basis)
        // If hour is 12 (noon), next is 1. If 23, next is 12 (midnight).
        // (hour + 1) logic:
        // hour 14 -> next 15 -> 3
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
}
