package com.siffermastare.domain.generators

/**
 * Factory for creating NumberGenerators based on ID.
 */
object NumberGeneratorFactory {
    
    // Constants for Lesson IDs
    const val ID_CARDINAL_0_20 = "cardinal_0_20"
    const val ID_CARDINAL_20_100 = "cardinal_20_100"
    const val ID_CARDINAL_100_1000 = "cardinal_100_1000"
    const val ID_ORDINAL_1_20 = "ordinal_1_20"
    const val ID_TIME_DIGITAL = "time_digital"
    const val ID_TIME_INFORMAL = "time_informal"
    const val ID_TRICKY_PAIRS = "tricky_pairs"
    const val ID_PHONE_NUMBER = "phone_number"
    const val ID_FRACTIONS = "fractions"

    fun create(id: String): NumberGenerator {
        return when (id) {
            ID_CARDINAL_0_20 -> CardinalGenerator(0, 20)
            ID_CARDINAL_20_100 -> CardinalGenerator(20, 100)
            ID_CARDINAL_100_1000 -> CardinalGenerator(100, 1000)
            ID_ORDINAL_1_20 -> OrdinalGenerator(1, 20)
            ID_TIME_DIGITAL -> TimeGenerator()
            ID_TIME_INFORMAL -> InformalTimeGenerator()
            ID_TRICKY_PAIRS -> TrickyPairsGenerator()
            ID_PHONE_NUMBER -> PhoneNumberGenerator()
            ID_FRACTIONS -> FractionsGenerator()
            else -> CardinalGenerator(0, 10) // Fallback/Default
        }
    }
}
