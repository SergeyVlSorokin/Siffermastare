package com.siffermastare.domain.evaluation

import com.siffermastare.domain.validation.strategies.StandardNumberEvaluationStrategy

/**
 * Shared utility for phone number hybrid atom decomposition.
 * Used by both PhoneNumberGenerator (to populate question.atoms)
 * and PhoneNumberEvaluationStrategy (to decompose user input).
 *
 * Phone numbers use position-dependent decomposition:
 * - Prefix (chars 0-2) + Group1 (chars 3-5): individual digit atoms
 * - Pair1 (chars 6-7) + Pair2 (chars 8-9): standard number decomposition
 *   - Exception: pairs < 10 → individual digit atoms ["0", "X"]
 */
object PhoneNumberDecomposer {

    /**
     * Decomposes a 10-digit phone number string into hybrid atoms.
     *
     * @param input Must be exactly 10 digits.
     * @return List of atom strings using hybrid decomposition rules.
     * @throws IllegalArgumentException if input length is not 10.
     */
    fun decompose(input: String): List<String> {
        require(input.length == 10) { "Phone number must be exactly 10 digits, got ${input.length}" }

        val atoms = mutableListOf<String>()

        // Prefix (chars 0-2): individual digit atoms
        for (i in 0..2) {
            atoms.add(input[i].toString())
        }

        // Group1 (chars 3-5): individual digit atoms
        for (i in 3..5) {
            atoms.add(input[i].toString())
        }

        // Pair1 (chars 6-7): standard number decomposition
        val pair1 = input.substring(6, 8).toInt()
        atoms.addAll(decomposePair(pair1))

        // Pair2 (chars 8-10): standard number decomposition
        val pair2 = input.substring(8, 10).toInt()
        atoms.addAll(decomposePair(pair2))

        return atoms
    }

    /**
     * Decomposes a phone number pair (2-digit segment) into atoms.
     * Pairs < 10 (with leading zero) → individual digit atoms ["0", "X"]
     * Pairs >= 10 → standard number decomposition
     */
    fun decomposePair(n: Int): List<String> {
        return if (n < 10) {
            listOf("0", n.toString())
        } else {
            StandardNumberEvaluationStrategy.decompose(n)
        }
    }
}
