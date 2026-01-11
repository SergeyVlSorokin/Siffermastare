package com.siffermastare.domain.validation

object AnswerValidator {
    
    fun isTimeLesson(lessonId: String): Boolean {
        return lessonId == "time_digital" || lessonId == "time_informal" || lessonId == "phone_number"
    }

    fun validateTime(input: String, target: String): Boolean {
        if (input.isEmpty()) return false
        
        // Handle multiple correct answers separated by pipe "|"
        val targets = target.split("|")
        
        return targets.any { t ->
            val normalizedTarget = normalizeTimeInput(t)
            
            if (normalizedTarget != null) {
                // Target is a valid time -> Input must be valid time
                val normalizedInput = normalizeTimeInput(input)
                normalizedInput != null && normalizedInput == normalizedTarget
            } else {
                // Target is NOT a time (e.g. Phone Number) -> Raw strip match
                val rawTarget = t.replace(" ", "").replace("-", "")
                val rawInput = input.replace(" ", "").replace("-", "")
                rawInput == rawTarget
            }
        }
    }
    
    private fun normalizeTimeInput(input: String): String? {
        // Only normalize if it looks like time (short length or contains colon)
        if (input.length > 5 && !input.contains(":")) return null // Skip logic for phone numbers
        
        // Remove known separators just in case (though input might be raw)
        val clean = input.replace(":", "")
        
        // Check length
        if (clean.length < 3 || clean.length > 4) return null
        
        // Pad with leading zero if length is 3 (e.g. "930" -> "0930")
        val padded = if (clean.length == 3) "0$clean" else clean
        
        // Insert colon
        return "${padded.substring(0, 2)}:${padded.substring(2)}"
    }
}
