package com.siffermastare.domain.validation

object AnswerValidator {
    
    fun isTimeLesson(lessonId: String): Boolean {
        return lessonId == "time_digital"
    }

    fun validateTime(input: String, target: String): Boolean {
        if (input.isEmpty()) return false
        
        val normalizedInput = normalizeTimeInput(input) ?: return false
        val normalizedTarget = normalizeTimeInput(target) ?: return false
        
        return normalizedInput == normalizedTarget
    }
    
    private fun normalizeTimeInput(input: String): String? {
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
