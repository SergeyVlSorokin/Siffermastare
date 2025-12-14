package com.siffermastare.ui.lesson

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.siffermastare.ui.theme.SiffermästareTheme

@Composable
fun LessonScreen(
    modifier: Modifier = Modifier
) {
    SiffermästareTheme {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Lesson Screen - Placeholder")
        }
    }
}

