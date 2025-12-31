package com.siffermastare.ui.lesson

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material3.ButtonDefaults // For button colors if needed
import androidx.compose.material3.OutlinedButton

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
import com.siffermastare.ui.components.AnswerDisplay
import com.siffermastare.ui.util.TimeFormatter
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
            val rate = uiState.ttsRate
            android.util.Log.d("LessonScreen", "Replay TTS for: '$textToSpeak' at rate $rate")
            ttsManager.speak(textToSpeak, rate)
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

                // Replay Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Reveal (Give Up) - Order: Reveal -> Normal -> Slow
                    val isRevealEnabled = uiState.incorrectAttempts >= 3 && uiState.answerState != AnswerState.REVEALED && uiState.answerState != AnswerState.CORRECT
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = {
                                viewModel.onGiveUp()
                            },
                            enabled = isRevealEnabled,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Outlined.Visibility,
                                contentDescription = "Give Up",
                                modifier = Modifier.fillMaxSize(),
                                tint = if (isRevealEnabled) Color.Black else Color.LightGray
                            )
                        }
                        Text(stringResource(R.string.lesson_give_up), style = MaterialTheme.typography.labelSmall)
                    }

                    Spacer(modifier = Modifier.width(32.dp))

                    // Normal Replay
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = {
                                val textToSpeak = uiState.spokenText.ifEmpty { uiState.targetNumber.toString() }
                                android.util.Log.d("LessonScreen", "Refresher TTS for: '$textToSpeak'")
                                ttsManager.speak(textToSpeak) // Default rate 1.0
                            },
                            modifier = Modifier.size(64.dp) // Large
                        ) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Outlined.PlayCircle,
                                contentDescription = "Replay",
                                modifier = Modifier.fillMaxSize(),
                                tint = Color.Black
                            )
                        }
                        Text(stringResource(R.string.lesson_replay), style = MaterialTheme.typography.labelSmall)
                    }

                    Spacer(modifier = Modifier.width(32.dp))

                    // Slow Replay (Turtle/Sakta)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = {
                                viewModel.onSlowReplay()
                            },
                            modifier = Modifier.size(64.dp) // Same size as Normal
                        ) {
                            // Slow Replay (Turtle/Sakta)
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Filled.SlowMotionVideo,
                                contentDescription = "Slow Replay",
                                modifier = Modifier.fillMaxSize(),
                                tint = Color.Black
                            )
                        }
                        Text(stringResource(R.string.lesson_slow_replay), style = MaterialTheme.typography.labelSmall)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // User Input Display with Color and Shake
                val textColor = when (uiState.answerState) {
                    AnswerState.CORRECT -> CorrectGreen // Fix: Was Color.Green
                    AnswerState.INCORRECT -> MaterialTheme.colorScheme.error
                    AnswerState.REVEALED -> Color(0xFFFFA000) // Amber
                    AnswerState.NEUTRAL -> MaterialTheme.colorScheme.onBackground
                }
                
                AnswerDisplay(
                    text = uiState.currentInput,
                    textColor = textColor,
                    isTimeFormat = uiState.lessonId.contains("time", ignoreCase = true),
                    modifier = Modifier.offset(x = shakeOffset.value.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))



                Spacer(modifier = Modifier.height(16.dp))

                // Custom Numpad
                val isInputLocked = uiState.answerState == AnswerState.REVEALED
                Numpad(
                    onDigitClick = { digit ->
                        viewModel.onDigitClick(digit)
                    },
                    onBackspaceClick = {
                        viewModel.onBackspaceClick()
                    },
                    onCheckClick = {
                        viewModel.onCheckClick()
                    },
                    enabled = !isInputLocked,
                    checkIcon = if (isInputLocked) androidx.compose.material.icons.Icons.Default.PlayArrow else androidx.compose.material.icons.Icons.Default.Check
                )
            }
        }
    }
}
