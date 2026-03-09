package com.siffermastare.ui.mastery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.siffermastare.R
import com.siffermastare.SiffermastareApplication
import com.siffermastare.domain.model.AtomMastery
import com.siffermastare.domain.usecases.GetMasteryDataUseCase
import com.siffermastare.ui.components.BetaHeatmapBar
import com.siffermastare.ui.components.getMasteryColor
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MasteryScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val application = context.applicationContext as SiffermastareApplication
    val masteryUseCase = application.getMasteryDataUseCase

    val viewModel: MasteryViewModel = viewModel(
        factory = MasteryViewModelFactory(masteryUseCase)
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showSortMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.mastery_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_navigate_back)
                        )
                    }
                }
            )
        },
        modifier = modifier.windowInsetsPadding(WindowInsets.safeDrawing)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is MasteryUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is MasteryUiState.Success -> {
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        val globalState = state.masteryData.globalState
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.mastery_overall),
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "${(globalState.mu * 100).roundToInt()}%",
                                style = MaterialTheme.typography.titleLarge,
                                color = getMasteryColor(globalState.mu, globalState.alpha, globalState.beta)
                            )
                        }
                        
                        // Global aggregate bar
                        BetaHeatmapBar(
                            alpha = globalState.alpha,
                            beta = globalState.beta,
                            mu = globalState.mu,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // User friendly legend
                        MasteryLegend()
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.mastery_detailed_breakdown),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(0.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.mastery_show_untested),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Checkbox(
                                    checked = state.showUntested,
                                    onCheckedChange = { viewModel.setShowUntested(it) },
                                    modifier = Modifier.scale(0.75f)
                                )
                                Box {
                                    IconButton(
                                        onClick = { showSortMenu = true },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Sort,
                                            contentDescription = "Sort Options",
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    DropdownMenu(
                                        expanded = showSortMenu,
                                        onDismissRequest = { showSortMenu = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text(stringResource(R.string.mastery_sort_natural)) },
                                            onClick = {
                                                viewModel.setSortOption(MasterySortOption.NATURAL_ORDER)
                                                showSortMenu = false
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text(stringResource(R.string.mastery_sort_increasing)) },
                                            onClick = {
                                                viewModel.setSortOption(MasterySortOption.KNOWLEDGE_INCREASING)
                                                showSortMenu = false
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text(stringResource(R.string.mastery_sort_decreasing)) },
                                            onClick = {
                                                viewModel.setSortOption(MasterySortOption.KNOWLEDGE_DECREASING)
                                                showSortMenu = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // If no real atoms, show empty state
                        if (state.masteryData.atomStates.isEmpty()) {
                            val emptyMessage = if (!state.masteryData.hasDatabaseData) {
                                stringResource(R.string.mastery_empty_state)
                            } else {
                                stringResource(R.string.mastery_empty_state_filtered)
                            }
                            
                            Text(
                                text = emptyMessage,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(state.masteryData.atomStates, key = { it.id }) { atom ->
                                    MasteryItem(atom = atom)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MasteryItem(atom: AtomMastery, modifier: Modifier = Modifier) {
    val color = getMasteryColor(atom.mu, atom.alpha, atom.beta)
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = atom.displayName,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
            // Show percentage proficiency based on average (mu)
            Text(
                text = "${(atom.mu * 100).roundToInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        BetaHeatmapBar(
            alpha = atom.alpha,
            beta = atom.beta,
            mu = atom.mu,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun MasteryLegend() {
    Column {
        Text(
            text = stringResource(R.string.mastery_legend_title),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LegendItem(color = getMasteryColor(0.9f, 20f, 1f), label = stringResource(R.string.mastery_legend_mastered))
            LegendItem(color = getMasteryColor(0.7f, 7f, 3f), label = stringResource(R.string.mastery_legend_learning))
            LegendItem(color = getMasteryColor(0.3f, 3f, 7f), label = stringResource(R.string.mastery_legend_needs_work))
            LegendItem(color = getMasteryColor(0.5f, 1f, 1f), label = stringResource(R.string.mastery_legend_untested))
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .width(12.dp)
                .height(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}
