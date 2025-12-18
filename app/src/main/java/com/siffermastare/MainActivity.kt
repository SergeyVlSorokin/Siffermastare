package com.siffermastare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.siffermastare.ui.home.HomeScreen
import com.siffermastare.ui.lesson.LessonScreen
import com.siffermastare.ui.summary.SummaryScreen
import com.siffermastare.ui.navigation.Screen
import com.siffermastare.ui.theme.SiffermästareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SiffermästareTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route
                    ) {
                        composable(Screen.Home.route) {
                            HomeScreen(navController = navController)
                        }
                        composable(Screen.Lesson.route) {
                            LessonScreen(
                                onLessonComplete = { accuracy, avgSpeed ->
                                    navController.navigate(Screen.Summary.createRoute(accuracy, avgSpeed)) {
                                        // Pop up to Home so back button from Summary goes to Home, 
                                        // and Lesson state is cleared (ViewModel cleared)
                                        popUpTo(Screen.Home.route) { inclusive = false }
                                    }
                                }
                            )
                        }
                        composable(
                            route = Screen.Summary.route,
                            arguments = listOf(
                                androidx.navigation.navArgument("accuracy") { type = androidx.navigation.NavType.FloatType },
                                androidx.navigation.navArgument("avgSpeed") { type = androidx.navigation.NavType.LongType }
                            )
                        ) { backStackEntry ->
                            val accuracy = backStackEntry.arguments?.getFloat("accuracy") ?: 0f
                            val avgSpeed = backStackEntry.arguments?.getLong("avgSpeed") ?: 0L
                            
                            SummaryScreen(
                                accuracy = accuracy,
                                avgSpeed = avgSpeed,
                                onNavigateHome = {
                                    // Pop back to Home
                                    navController.popBackStack(Screen.Home.route, false)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}