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
import androidx.compose.material.icons.automirrored.filled.Backspace
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
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    checkIcon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Default.Check
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
            NumpadButton(text = "1", onClick = { onDigitClick(1) }, enabled = enabled)
            NumpadButton(text = "2", onClick = { onDigitClick(2) }, enabled = enabled)
            NumpadButton(text = "3", onClick = { onDigitClick(3) }, enabled = enabled)
        }

        // Row 2: 4, 5, 6
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NumpadButton(text = "4", onClick = { onDigitClick(4) }, enabled = enabled)
            NumpadButton(text = "5", onClick = { onDigitClick(5) }, enabled = enabled)
            NumpadButton(text = "6", onClick = { onDigitClick(6) }, enabled = enabled)
        }

        // Row 3: 7, 8, 9
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NumpadButton(text = "7", onClick = { onDigitClick(7) }, enabled = enabled)
            NumpadButton(text = "8", onClick = { onDigitClick(8) }, enabled = enabled)
            NumpadButton(text = "9", onClick = { onDigitClick(9) }, enabled = enabled)
        }

        // Row 4: Backspace, 0, Check
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Backspace Button
            Button(
                onClick = onBackspaceClick,
                enabled = enabled,
                modifier = Modifier.size(80.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = "Backspace"
                )
            }

            // 0 Button
            NumpadButton(text = "0", onClick = { onDigitClick(0) }, enabled = enabled)

            // Check Button (Always enabled if Next functionality? Or enabled param controls it?)
            // If we are in REVEALED state, Numpad is disabled (digits).
            // But CHECK button becomes NEXT button and MUST BE ENABLED.
            // So 'enabled' param should apply to digits/backspace.
            // Check button should probably remain enabled or have its own control?
            // "Disable Numpad grid" implies digits.
            // I will update NumpadButton to take enabled.
            // For Check Button, I will keep it passed through enabled? No, logic requires Next to work.
            // Solution: Add checkEnabled logic? Or assume Check is always active unless app logic blocks it.
            // For now, I'll allow Check button to stay enabled even if 'enabled' (digits) is false.
            // Wait, if I pass enabled=false, typically user expects WHOLE component disabled.
            // But here I want Input Locked. Meaning Digits/Backspace disabled. Check/Next enabled.
            // I'll call the param 'inputEnabled' or just apply 'enabled' to digits only.
            // Let's refine 'enabled' behavior: Applies to DIGITS and BACKSPACE. Check is separate.
            Button(
                onClick = onCheckClick,
                modifier = Modifier.size(80.dp), // Check button prominent
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = checkIcon,
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
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.size(80.dp) // Large circular buttons
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}
