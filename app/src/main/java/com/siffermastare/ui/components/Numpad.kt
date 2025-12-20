package com.siffermastare.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * A custom numeric keypad composable.
 *
 * Displays digits 0-9, a Backspace button, and a Check (Submit) button.
 * Designed for quick data entry in the Siffermastare app.
 *
 * @param onDigitClick Callback when a digit button (0-9) is clicked.
 * @param onBackspaceClick Callback when the backspace button is clicked.
 * @param onCheckClick Callback when the check (submit) button is clicked.
 * @param modifier Modifier to be applied to the layout.
 */
@Composable
fun Numpad(
    onDigitClick: (Int) -> Unit,
    onBackspaceClick: () -> Unit,
    onCheckClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Row 1: 1, 2, 3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NumpadButton(text = "1", onClick = { onDigitClick(1) })
            NumpadButton(text = "2", onClick = { onDigitClick(2) })
            NumpadButton(text = "3", onClick = { onDigitClick(3) })
        }

        // Row 2: 4, 5, 6
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NumpadButton(text = "4", onClick = { onDigitClick(4) })
            NumpadButton(text = "5", onClick = { onDigitClick(5) })
            NumpadButton(text = "6", onClick = { onDigitClick(6) })
        }

        // Row 3: 7, 8, 9
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NumpadButton(text = "7", onClick = { onDigitClick(7) })
            NumpadButton(text = "8", onClick = { onDigitClick(8) })
            NumpadButton(text = "9", onClick = { onDigitClick(9) })
        }

        // Row 4: Backspace, 0, Check
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Backspace Button
            Button(
                onClick = onBackspaceClick,
                modifier = Modifier.size(80.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Backspace"
                )
            }

            // 0 Button
            NumpadButton(text = "0", onClick = { onDigitClick(0) })

            // Check Button
            Button(
                onClick = onCheckClick,
                modifier = Modifier.size(80.dp), // Check button prominent
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Check"
                )
            }
        }
    }
}

@Composable
private fun NumpadButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.size(80.dp) // Large circular buttons
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}
