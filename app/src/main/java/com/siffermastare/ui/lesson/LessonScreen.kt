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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import com.siffermastare.R
import com.siffermastare.data.tts.TTSManager
import com.siffermastare.ui.components.Numpad
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Lesson screen composable.
 *
 * Displays a number to listen to, an input field for the answer, and a custom Numpad.
 * Implements the basic game loop via LessonViewModel.
 *
 * @param onLessonComplete Callback when lesson is finished.
 * @param modifier Modifier to be applied to the layout.
 * @param viewModel ViewModel for business logic.
 */
@Composable
fun LessonScreen(
    onLessonComplete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LessonViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    
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
    // We observe the state from ViewModel now
    LaunchedEffect(uiState.targetNumber) {
        val swedishText = digitToSwedish(uiState.targetNumber)
        ttsManager.speak(swedishText)
    }

    // Handle Feedback Messages
    LaunchedEffect(uiState.feedbackMessage) {
        uiState.feedbackMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
            }
            viewModel.messageShown()
        }
    }

    // Handle Lesson Completion
    LaunchedEffect(uiState.isLessonComplete) {
        if (uiState.isLessonComplete) {
            onLessonComplete()
        }
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
                // Progress Indicator
                Text(
                    text = stringResource(R.string.lesson_progress_format, uiState.questionCount, uiState.totalQuestions),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                
                Spacer(modifier = Modifier.height(8.dp))

                // Instructions / Status
                Text(
                    text = stringResource(R.string.lesson_instructions),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Replay Button (Large and Central)
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

                // User Input Display
                Text(
                    text = if (uiState.currentInput.isEmpty()) "_" else uiState.currentInput,
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary
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

// Keeping this helper private here or could move to a util class if more widely used.
// Since logic is mainly in VM now, the conversion to text is still UI concern for TTS.
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
