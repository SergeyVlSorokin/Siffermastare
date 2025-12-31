package com.siffermastare.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.siffermastare.ui.util.TimeFormatter

@Composable
fun AnswerDisplay(
    text: String,
    textColor: Color,
    isTimeFormat: Boolean = false,
    modifier: Modifier = Modifier
) {
    val formatted = if (isTimeFormat) TimeFormatter.format(text) else text
    val displayText = formatted.ifEmpty { "_" }

    Text(
        text = displayText,
        style = MaterialTheme.typography.displayLarge,
        color = textColor,
        modifier = modifier
    )
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun AnswerDisplayPreview() {
    MaterialTheme {
        AnswerDisplay(text = "0930", textColor = Color.Black, isTimeFormat = true)
    }
}
