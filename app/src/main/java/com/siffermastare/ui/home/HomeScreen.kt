package com.siffermastare.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.MaterialTheme
import com.siffermastare.R
import com.siffermastare.ui.navigation.Screen
import com.siffermastare.ui.theme.SiffermästareTheme

/**
 * Home screen composable.
 *
 * Displays the main landing page with navigation options to lesson modes.
 *
 * @param navController The [NavController] used for navigating between screens.
 * @param modifier Modifier to be applied to the layout.
 */
@Composable
fun HomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Header
        Text(
            text = "Siffermästare",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = { navController.navigate(Screen.Lesson.createRoute("cardinal_0_20")) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 8.dp)
        ) {
            Text("Numbers 0-20")
        }

        Button(
            onClick = { navController.navigate(Screen.Lesson.createRoute("cardinal_20_100")) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 8.dp)
        ) {
            Text("Numbers 20-100")
        }

        Button(
            onClick = { navController.navigate(Screen.Lesson.createRoute("cardinal_100_1000")) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 8.dp)
        ) {
            Text("Numbers 100-1000")
        }

        Button(
            onClick = { navController.navigate(Screen.Lesson.createRoute("ordinal_1_20")) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 8.dp)
        ) {
            Text("Ordinals (1:a - 20:e)")
        }

        Button(
            onClick = { navController.navigate(Screen.Lesson.createRoute("time_digital")) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 8.dp)
        ) {
            Text("Time (Digital)")
        }
    }
}

