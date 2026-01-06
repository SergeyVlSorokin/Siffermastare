package com.siffermastare.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.MaterialTheme
import com.siffermastare.R

@Composable
fun ExitConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Avsluta lektionen?") },
        text = { Text(text = "Dina framsteg sparas inte.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Avsluta", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Avbryt")
            }
        }
    )
}
