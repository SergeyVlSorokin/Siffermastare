package com.siffermastare.ui.lesson

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import com.siffermastare.R
import com.siffermastare.data.tts.TTSManager
import com.siffermastare.ui.components.Numpad
import kotlinx.coroutines.launch

/**
 * Lesson screen composable.
 *
 * Displays a number to listen to, an input field for the answer, and a custom Numpad.
 * Implements the game loop with visual feedback (Shake, Colors).
 */
@Composable
fun LessonScreen(
    onLessonComplete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LessonViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    
    // Animation State
    val shakeOffset = remember { Animatable(0f) }

    // Initialize TTSManager
    val ttsManager = remember { TTSManager(context) }

    // Lifecycle management for TTS
    DisposableEffect(Unit) {
        onDispose {
            ttsManager.shutdown()
        }
    }

    // Speak on initial load/change of targetNumber
    LaunchedEffect(uiState.targetNumber) {
        // Only speak if we are NOT in the middle of a replay loop from incorrect answer
        // Actually, logic says speak whenever targetNumber changes (new question).
        // Replay handled separately.
        if (uiState.currentInput.isEmpty() && uiState.answerState == AnswerState.NEUTRAL) {
             val swedishText = digitToSwedish(uiState.targetNumber)
             ttsManager.speak(swedishText)
        }
    }
    
    // Handle Replay Trigger
    LaunchedEffect(uiState.replayTrigger) {
        if (uiState.replayTrigger > 0) {
            val swedishText = digitToSwedish(uiState.targetNumber)
            ttsManager.speak(swedishText)
        }
    }

    // Handle Shake Animation for Incorrect Answer
    LaunchedEffect(uiState.answerState) {
        if (uiState.answerState == AnswerState.INCORRECT) {
            shakeOffset.animateTo(
                targetValue = 0f,
                animationSpec = androidx.compose.animation.core.keyframes {
                    durationMillis = (LessonViewModel.FEEDBACK_DELAY - 100).toInt() // Slightly faster than VM delay to ensure completion
                    0f at 0
                    20f at 50
                    -20f at 100
                    20f at 150
                    -20f at 200
                    10f at 250
                    -10f at 300
                    0f at (LessonViewModel.FEEDBACK_DELAY - 100).toInt()
                }
            )
        } else {
             // Ensure reset if state changes abruptly
             shakeOffset.snapTo(0f)
        }
    }

    // Handle Lesson Completion
    LaunchedEffect(uiState.isLessonComplete) {
        if (uiState.isLessonComplete) {
            onLessonComplete()
        }
    }

    Scaffold(
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
                // Progress Indicator
                Text(
                    text = stringResource(R.string.lesson_progress_format, uiState.questionCount, uiState.totalQuestions),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                
                Spacer(modifier = Modifier.height(8.dp))

                // Instructions
                Text(
                    text = stringResource(R.string.lesson_instructions),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Replay Button
                IconButton(
                    onClick = {
                        val swedishText = digitToSwedish(uiState.targetNumber)
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
                Text(stringResource(R.string.lesson_replay), style = MaterialTheme.typography.labelSmall)

                Spacer(modifier = Modifier.height(32.dp))

                // User Input Display with Color and Shake
                val textColor = when (uiState.answerState) {
                    AnswerState.CORRECT -> Color.Green // Or use a defined safe green
                    AnswerState.INCORRECT -> Color.Red
                    AnswerState.NEUTRAL -> MaterialTheme.colorScheme.primary
                }
                
                Text(
                    text = if (uiState.currentInput.isEmpty()) "_" else uiState.currentInput,
                    style = MaterialTheme.typography.displayLarge,
                    color = textColor,
                    modifier = Modifier.offset(x = shakeOffset.value.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Custom Numpad
                Numpad(
                    onDigitClick = { digit ->
                        viewModel.onDigitClick(digit)
                    },
                    onBackspaceClick = {
                        viewModel.onBackspaceClick()
                    },
                    onCheckClick = {
                        viewModel.onCheckClick()
                    }
                )
            }
        }
    }
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
