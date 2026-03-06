package com.siffermastare.ui.summary

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.siffermastare.R
import com.siffermastare.domain.models.AtomSummary

/**
 * Formats an atom ID for user-readable display.
 *
 * - `ord:20` → "20 (ordningstal)"
 * - `#kvart` → "kvart"
 * - plain `5` → "5"
 */
internal fun formatAtomId(atomId: String): String {
    return when {
        atomId.startsWith("ord:") -> "${atomId.removePrefix("ord:")} (ordningstal)"
        atomId.startsWith("#") -> atomId.removePrefix("#")
        else -> atomId
    }
}

@Composable
fun SummaryScreen(
    accuracy: Float,
    avgSpeed: Long,
    onNavigateHome: () -> Unit,
    improvedAtoms: List<AtomSummary> = emptyList(),
    needsPracticeAtoms: List<AtomSummary> = emptyList(),
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.lesson_complete_title),
                style = MaterialTheme.typography.displayMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.lesson_complete_message),
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.lesson_accuracy_format, accuracy),
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.lesson_speed_format, avgSpeed / 1000.0),
                style = MaterialTheme.typography.headlineSmall
            )

            // Atom Summary Sections — only shown when data exists
            if (improvedAtoms.isNotEmpty() || needsPracticeAtoms.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(modifier = Modifier.fillMaxWidth(0.8f))
            }

            if (improvedAtoms.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.summary_improved_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                improvedAtoms.forEach { atom ->
                    Text(
                        text = "✅ ${formatAtomId(atom.atomId)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (needsPracticeAtoms.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.summary_needs_practice_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
                needsPracticeAtoms.forEach { atom ->
                    Text(
                        text = "🔄 ${formatAtomId(atom.atomId)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(onClick = onNavigateHome) {
                Text(text = stringResource(R.string.back_to_home))
            }
        }
    }
}
