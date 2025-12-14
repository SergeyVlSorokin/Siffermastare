package com.siffermastare.ui.lesson

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.siffermastare.data.tts.TTSManager
import com.siffermastare.ui.components.Numpad
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Lesson screen composable.
 *
 * Displays a number to listen to, an input field for the answer, and a custom Numpad.
 * Implements the basic game loop: Listen -> Type -> Check -> Feedback.
 *
 * @param modifier Modifier to be applied to the layout.
 */
@Composable
fun LessonScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var targetNumber by remember { mutableIntStateOf(generateRandomDigit()) }
    var currentInput by remember { mutableStateOf("") }
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // Initialize TTSManager
    val ttsManager = remember { TTSManager(context) }

    // Lifecycle management for TTS
    DisposableEffect(Unit) {
        onDispose {
            ttsManager.shutdown()
        }
    }

    // Speak on initial load/change of targetNumber
    LaunchedEffect(targetNumber) {
        val swedishText = digitToSwedish(targetNumber)
        ttsManager.speak(swedishText)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                // Instructions / Status
                Text(
                    text = "Listen and type the number",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Replay Button (Large and Central)
                IconButton(
                    onClick = {
                        val swedishText = digitToSwedish(targetNumber)
                        ttsManager.speak(swedishText)
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Replay",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Text("Replay", style = MaterialTheme.typography.labelSmall)

                Spacer(modifier = Modifier.height(32.dp))

                // User Input Display
                Text(
                    text = if (currentInput.isEmpty()) "_" else currentInput,
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Custom Numpad
                Numpad(
                    onDigitClick = { digit ->
                        currentInput += digit.toString()
                    },
                    onBackspaceClick = {
                        if (currentInput.isNotEmpty()) {
                            currentInput = currentInput.dropLast(1)
                        }
                    },
                    onCheckClick = {
                        if (currentInput.isEmpty()) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Please enter a number")
                            }
                            return@Numpad
                        }

                        if (compareAnswer(currentInput, targetNumber)) {
                            // Correct Answer Flow
                            scope.launch {
                                snackbarHostState.showSnackbar("Correct!")
                            }
                            currentInput = ""
                            targetNumber = generateRandomDigit() // Triggers LaunchedEffect to speak
                        } else {
                            // Incorrect Answer Flow
                            scope.launch {
                                snackbarHostState.showSnackbar("Try Again")
                            }
                            // Keep input and targetNumber state
                        }
                    }
                )
            }
        }
    }
}

private fun generateRandomDigit(): Int {
    return Random.nextInt(0, 10)
}

private fun digitToSwedish(digit: Int): String {
    return when (digit) {
        0 -> "noll"
        1 -> "ett"
        2 -> "två"
        3 -> "tre"
        4 -> "fyra"
        5 -> "fem"
        6 -> "sex"
        7 -> "sju"
        8 -> "åtta"
        9 -> "nio"
        else -> "fel"
    }
}

private fun compareAnswer(userInput: String, targetNumber: Int): Boolean {
    val userNumber = userInput.toIntOrNull()
    return userNumber == targetNumber
}
