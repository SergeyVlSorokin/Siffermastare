package com.siffermastare.ui.navigation

/**
 * Sealed class representing the screens in the application.
 *
 * @property route The string route used by Navigation Compose.
 */
sealed class Screen(val route: String) {
    /** The home screen destination. */
    object Home : Screen("home")
    
    /** The lesson screen destination. */
    object Lesson : Screen("lesson/{lessonId}") {
        fun createRoute(lessonId: String) = "lesson/$lessonId"
    }

    /** The summary screen destination. */
    object Summary : Screen("summary/{accuracy}/{avgSpeed}") {
        fun createRoute(accuracy: Float, avgSpeed: Long) = "summary/$accuracy/$avgSpeed"
    }
}

