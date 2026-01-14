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
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.siffermastare.R
import com.siffermastare.SiffermastareApplication
import com.siffermastare.ui.navigation.Screen
import com.siffermastare.ui.theme.SiffermästareTheme
import com.siffermastare.domain.generators.NumberGeneratorFactory


import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing

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
    val context = LocalContext.current
    val application = context.applicationContext as SiffermastareApplication
    val repository = application.lessonRepository
    
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(repository)
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing) // Handle system bars (status + nav)
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Header
        Text(
            text = stringResource(R.string.home_title),
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Dashboard Stats
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DashboardStatCard(
                label = stringResource(R.string.dashboard_lessons_label),
                value = uiState.totalLessons.toString(),
                modifier = Modifier.weight(1f)
            )
            DashboardStatCard(
                label = stringResource(R.string.dashboard_streak_label),
                value = "${uiState.currentStreak} ${stringResource(R.string.dashboard_streak_days_suffix)}", // e.g., "5 Days"
                modifier = Modifier.weight(1f)
            )
        }

        Button(
            onClick = { navController.navigate(Screen.Lesson.createRoute("cardinal_0_20")) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 8.dp)
        ) {
            Text(stringResource(R.string.menu_numbers_0_20))
        }

        Button(
            onClick = { navController.navigate(Screen.Lesson.createRoute("tricky_pairs")) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 8.dp)
        ) {
            Text(stringResource(R.string.menu_tricky_pairs))
        }

        Button(
            onClick = { navController.navigate(Screen.Lesson.createRoute("cardinal_20_100")) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 8.dp)
        ) {
            Text(stringResource(R.string.menu_numbers_20_100))
        }

        Button(
            onClick = { navController.navigate(Screen.Lesson.createRoute("cardinal_100_1000")) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 8.dp)
        ) {
            Text(stringResource(R.string.menu_numbers_100_1000))
        }

        Button(
            onClick = { navController.navigate(Screen.Lesson.createRoute("ordinal_1_20")) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 8.dp)
        ) {
            Text(stringResource(R.string.menu_ordinals))
        }

        Button(
            onClick = { navController.navigate(Screen.Lesson.createRoute("time_digital")) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 8.dp)
        ) {
            Text(stringResource(R.string.menu_time))
        }

        Button(
            onClick = { navController.navigate(Screen.Lesson.createRoute("time_informal")) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 8.dp)
        ) {
            Text(stringResource(R.string.menu_time_informal))
        }

        Button(
            onClick = { navController.navigate(Screen.Lesson.createRoute(com.siffermastare.domain.generators.NumberGeneratorFactory.ID_PHONE_NUMBER)) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 8.dp)
        ) {
            Text(stringResource(R.string.menu_phone_numbers))
        }

        Button(
            onClick = { navController.navigate(Screen.Lesson.createRoute(com.siffermastare.domain.generators.NumberGeneratorFactory.ID_FRACTIONS)) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 8.dp)
        ) {
            Text(stringResource(R.string.menu_fractions))
        }

        Button(
            onClick = { navController.navigate(Screen.Lesson.createRoute(com.siffermastare.domain.generators.NumberGeneratorFactory.ID_DECIMALS)) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 8.dp)
        ) {
            Text(stringResource(R.string.menu_decimals))
        }


    }
}

@Composable
fun DashboardStatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Card(
        modifier = modifier,
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.displaySmall, // Big number
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

