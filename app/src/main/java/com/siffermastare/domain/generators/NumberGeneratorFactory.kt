package com.siffermastare.domain.generators

/**
 * Factory for creating NumberGenerators based on ID.
 */
object NumberGeneratorFactory {

    fun create(id: String): NumberGenerator {
        return when (id) {
            "cardinal_0_20" -> CardinalGenerator(0, 20)
            "cardinal_20_100" -> CardinalGenerator(20, 100)
            "cardinal_100_1000" -> CardinalGenerator(100, 1000)
            "ordinal_1_20" -> OrdinalGenerator(1, 20)
            "time_digital" -> TimeGenerator()
            else -> CardinalGenerator(0, 10) // Fallback/Default
        }
    }
}
