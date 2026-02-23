package com.siffermastare.domain.evaluation

import com.siffermastare.domain.models.Question

/**
 * Evaluation strategy for informal Swedish time expressions.
 *
 * Grades user input at the atom level: hour atoms, minute number atoms,
 * structural concepts (#kvart, #halv), and directional concepts (#over, #i).
 * Uses the atoms list from the Question as the target ground truth.
 */
class InformalTimeEvaluationStrategy : EvaluationStrategy {

    override fun evaluate(input: String, question: Question): EvaluationResult {
        // Rule 1: Input Parsing
        val parsed = parseInput(input) ?: return EvaluationResult(isCorrect = false)
        val (inputHour, inputMinute) = parsed

        // Parse stimulus from first alternative in targetValue
        val stimulusStr = question.targetValue.split("|").first()
        val stimulusParsed = parseInput(stimulusStr) ?: return EvaluationResult(isCorrect = false)
        val (stimulusHour, stimulusMinute) = stimulusParsed

        val targetAtoms = question.atoms

        // Rule 2: isCorrect
        val isCorrect = (inputHour % 12 == stimulusHour % 12) && (inputMinute == stimulusMinute)

        // Grade each atom by index (position-aware for duplicates)
        val atomUpdates = mutableMapOf<String, MutableList<Boolean>>()

        // Pre-compute: find the index of the last number atom (= hour atom position)
        val lastNumberIndex = targetAtoms.indexOfLast { !it.startsWith("#") }

        for (i in targetAtoms.indices) {
            val atom = targetAtoms[i]
            val isHourPosition = (i == lastNumberIndex)

            val result = gradeAtomByIndex(atom, isHourPosition, targetAtoms, inputHour, inputMinute)
            if (result != null) {
                atomUpdates.getOrPut(atom) { mutableListOf() }.add(result)
            }
            // null = SKIP → omit from map
        }

        return EvaluationResult(isCorrect = isCorrect, atomUpdates = atomUpdates)
    }

    /**
     * Grades a single atom occurrence at a given index.
     * @param isHourPosition true if this is the hour atom (last number in the list)
     */
    private fun gradeAtomByIndex(
        atom: String,
        isHourPosition: Boolean,
        targetAtoms: List<String>,
        inputHour: Int,
        inputMinute: Int
    ): Boolean? {
        return when {
            atom == "#kvart" -> gradeKvart(inputMinute)
            atom == "#halv" -> gradeHalv(inputMinute)
            atom == "#over" -> gradeDirectional(atom, targetAtoms, inputHour, inputMinute)
            atom == "#i" -> gradeDirectional(atom, targetAtoms, inputHour, inputMinute)
            isHourPosition -> gradeHourAtom(atom, inputHour)
            else -> gradeMinuteNumberAtom(atom, inputMinute)
        }
    }

    // Rule 3: Hour Atom Grading
    private fun gradeHourAtom(atom: String, inputHour: Int): Boolean {
        val atomValue = atom.toInt()
        val inputMod = inputHour % 12
        val directMatch = atomValue % 12
        val offsetMatch = (atomValue - 1 + 12) % 12
        return inputMod == directMatch || inputMod == offsetMatch
    }

    // Rule 4: Minute Number Atom Grading
    private fun gradeMinuteNumberAtom(atom: String, inputMinute: Int): Boolean {
        val atomValue = atom.toInt()

        // Semantic + cross-direction match: both derive from the same minute→number mapping
        val derivedMinuteNumber = computeSemanticMinuteValue(inputMinute)

        return derivedMinuteNumber == atomValue
    }

    /**
     * Computes the semantic minute value for the input minute.
     */
    private fun computeSemanticMinuteValue(inputMinute: Int): Int? {
        return when (inputMinute) {
            0 -> null
            15, 45 -> null
            30 -> null
            in 1..14, in 16..20 -> inputMinute
            in 21..29 -> 30 - inputMinute
            in 31..39 -> inputMinute - 30
            in 40..44, in 46..59 -> 60 - inputMinute
            else -> null
        }
    }



    // Rule 5: Structural Concept Atoms
    private fun gradeKvart(inputMinute: Int): Boolean {
        return inputMinute == 15 || inputMinute == 45
    }

    private fun gradeHalv(inputMinute: Int): Boolean {
        return inputMinute in 21..39
    }

    // Rule 6: Directional Concept Atoms
    private fun gradeDirectional(
        atom: String,
        targetAtoms: List<String>,
        inputHour: Int,
        inputMinute: Int
    ): Boolean? {
        // Precondition: the corresponding minute-side atom must be ✅
        if (!minuteSideAtomIsSuccess(targetAtoms, inputMinute)) {
            return null // SKIP
        }

        // Derive direction from InputMinute
        val derivedDirection = deriveDirection(inputMinute) ?: return null // SKIP for 0 or 30

        return derivedDirection == atom
    }

    /**
     * Checks if the minute-side atom (minute number atom, or #kvart if no number atom) is successful.
     * Uses positional index: the first number atom before any directional atom is the minute atom.
     * Falls back to #kvart if no minute number atom precedes the directional atom.
     */
    private fun minuteSideAtomIsSuccess(targetAtoms: List<String>, inputMinute: Int): Boolean {
        // Find first directional atom index to determine what comes before it
        val dirIndex = targetAtoms.indexOfFirst { it == "#over" || it == "#i" }
        if (dirIndex < 0) return false

        // Look for a number atom before the directional atom (minute-side)
        val minuteNumberAtom = targetAtoms.take(dirIndex).firstOrNull { !it.startsWith("#") }
        if (minuteNumberAtom != null) {
            return gradeMinuteNumberAtom(minuteNumberAtom, inputMinute)
        }

        // No number atom before direction → check #kvart as minute-side
        if (targetAtoms.contains("#kvart")) {
            return gradeKvart(inputMinute)
        }

        return false
    }

    /**
     * Derives direction from InputMinute per Rule 6.
     */
    private fun deriveDirection(inputMinute: Int): String? {
        return when (inputMinute) {
            0, 30 -> null
            in 1..20 -> "#over"
            in 21..29 -> "#i"
            in 31..39 -> "#over"
            in 40..59 -> "#i"
            else -> null
        }
    }

    /**
     * Parses a digit string (3 or 4 chars) into (hour, minute).
     */
    private fun parseInput(input: String): Pair<Int, Int>? {
        if (!input.all { it.isDigit() }) return null
        if (input.length !in 3..4) return null

        val minuteStr = input.takeLast(2)
        val hourStr = input.dropLast(2)

        val hour = hourStr.toIntOrNull() ?: return null
        val minute = minuteStr.toIntOrNull() ?: return null

        if (hour > 24) return null
        if (minute > 59) return null

        return Pair(hour, minute)
    }
}
