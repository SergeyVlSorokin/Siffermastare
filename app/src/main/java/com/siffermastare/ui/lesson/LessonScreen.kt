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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.siffermastare.SiffermastareApplication
import com.siffermastare.ui.lesson.LessonViewModelFactory
import com.siffermastare.ui.theme.CorrectGreen

/**
 * Lesson screen composable.
 *
 * Displays a number to listen to, an input field for the answer, and a custom Numpad.
 * Implements the game loop with visual feedback (Shake, Colors).
 */
@Composable
fun LessonScreen(
    lessonId: String,
    onLessonComplete: (Float, Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val application = context.applicationContext as SiffermastareApplication
    val repository = application.lessonRepository
    
    val viewModel: LessonViewModel = viewModel(
        factory = LessonViewModelFactory(repository)
    )

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
    
    // Load Lesson ID
    LaunchedEffect(lessonId) {
        viewModel.loadLesson(lessonId)
    }

    // Speak on initial load (or when target changes and is ready)
    // We observe the state to determine when to speak
    // Speak on initial load/change of targetNumber
    // We observe the state to determine when to speak
    LaunchedEffect(uiState.targetNumber, uiState.questionCount) {
        // Trigger speak if we are in a fresh question state (Neutral)
        if (uiState.currentInput.isEmpty() && uiState.answerState == AnswerState.NEUTRAL) {
             val textToSpeak = uiState.spokenText.ifEmpty { uiState.targetNumber.toString() }
             android.util.Log.d("LessonScreen", "Requesting TTS for: '$textToSpeak'")
             
             // Check to ensure we don't speak 0 on initial empty state if it defaults to 0
             // But valid numbers include 0. 
             // We can check if questionCount > 0 to imply started.
             // Manager initializes QuestionCount to 1 on start.
             if (uiState.questionCount > 0) {
                 ttsManager.speak(textToSpeak)
             }
        }
    }
    
    // Handle Replay Trigger
    LaunchedEffect(uiState.replayTrigger) {
        if (uiState.replayTrigger > 0) {
            val textToSpeak = uiState.spokenText.ifEmpty { uiState.targetNumber.toString() }
            android.util.Log.d("LessonScreen", "Replay TTS for: '$textToSpeak'")
            ttsManager.speak(textToSpeak)
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
            val (accuracy, avgSpeed) = viewModel.getFinalStats()
            onLessonComplete(accuracy, avgSpeed)
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
                    color = MaterialTheme.colorScheme.onBackground // Fix: Was secondary(yellow)
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
                        val textToSpeak = uiState.spokenText.ifEmpty { uiState.targetNumber.toString() }
                        android.util.Log.d("LessonScreen", "Refresher TTS for: '$textToSpeak'")
                        ttsManager.speak(textToSpeak)
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
                    AnswerState.CORRECT -> CorrectGreen // Fix: Was Color.Green
                    AnswerState.INCORRECT -> MaterialTheme.colorScheme.error
                    AnswerState.NEUTRAL -> MaterialTheme.colorScheme.onBackground
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
