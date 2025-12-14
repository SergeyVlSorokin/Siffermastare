package com.siffermastare.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Lesson : Screen("lesson")
}

