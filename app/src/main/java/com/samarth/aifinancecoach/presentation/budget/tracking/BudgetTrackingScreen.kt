package com.samarth.aifinancecoach.presentation.budget.tracking

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.samarth.aifinancecoach.R
import com.samarth.aifinancecoach.domain.model.BudgetHealthLabel
import com.samarth.aifinancecoach.presentation.components.BudgetCard
import com.samarth.aifinancecoach.presentation.components.MonthSelector
import com.samarth.aifinancecoach.presentation.navigation.Screen
import com.samarth.aifinancecoach.presentation.theme.ErrorRed
import com.samarth.aifinancecoach.presentation.theme.SoraFontFamily
import com.samarth.aifinancecoach.presentation.theme.SuccessGreen
import com.samarth.aifinancecoach.utils.CurrencyFormatter
import com.samarth.aifinancecoach.utils.DateUtils
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetTrackingScreen(
    navController: NavController,
    viewModel: BudgetTrackingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.snackbarEvent.collectLatest { event ->
            val result = snackbarHostState.showSnackbar(
                message = event.message,
                actionLabel = event.actionLabel,
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                event.onAction?.invoke()
            }
        }
    }

    BudgetTrackingContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onMonthChanged = viewModel::onMonthChanged,
        onAddBudgetClick = { navController.navigate(Screen.BudgetSetup.createRoute()) },
        onEditBudgetClick = { budget -> 
            navController.navigate(Screen.BudgetSetup.createRoute(budgetId = budget.id)) 
        },
        onDeleteBudget = viewModel::deleteBudget
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetTrackingContent(
    state: BudgetTrackingState,
    snackbarHostState: SnackbarHostState,
    onMonthChanged: (Int, Int) -> Unit,
    onAddBudgetClick: () -> Unit,
    onEditBudgetClick: (com.samarth.aifinancecoach.domain.model.Budget) -> Unit,
    onDeleteBudget: (com.samarth.aifinancecoach.domain.model.Budget) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.budget_title),
                        style = MaterialTheme.typography.headlineMedium.copy(fontFamily = SoraFontFamily)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddBudgetClick,
                shape = MaterialTheme.shapes.small,
                containerColor = Color.Transparent,
                elevation = FloatingActionButtonDefaults.elevation(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF00C896), Color(0xFF0096FF))
                            ),
                            shape = MaterialTheme.shapes.small
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = stringResource(R.string.budget_add_new),
                        tint = Color.White
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            MonthSelector(
                month = state.selectedMonth,
                year = state.selectedYear,
                onPreviousClick = {
                    val newMonth = if (state.selectedMonth == 0) 11 else state.selectedMonth - 1
                    val newYear = if (state.selectedMonth == 0) state.selectedYear - 1 else state.selectedYear
                    onMonthChanged(newMonth, newYear)
                },
                onNextClick = {
                    val newMonth = if (state.selectedMonth == 11) 0 else state.selectedMonth + 1
                    val newYear = if (state.selectedMonth == 11) state.selectedYear + 1 else state.selectedYear
                    onMonthChanged(newMonth, newYear)
                }
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                item {
                    state.healthScore?.let { HealthScoreCard(it) }
                }

                item {
                    OverviewStrip(state)
                }

                item {
                    OverallProgressBar(state)
                }

                item {
                    SectionHeader(
                        title = stringResource(R.string.budget_categories),
                        onAddClick = onAddBudgetClick
                    )
                }

                if (state.budgets.isEmpty() && !state.isLoading) {
                    item {
                        EmptyBudgetsState()
                    }
                } else {
                    items(state.budgets, key = { it.id }) { budget ->
                        BudgetCard(
                            budget = budget,
                            onClick = { onEditBudgetClick(budget) },
                            onDelete = { onDeleteBudget(budget) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HealthScoreCard(healthScore: com.samarth.aifinancecoach.domain.model.BudgetHealthScore) {
    val scoreColor by animateColorAsState(
        targetValue = when (healthScore.label) {
            BudgetHealthLabel.EXCELLENT -> SuccessGreen
            BudgetHealthLabel.GOOD -> MaterialTheme.colorScheme.primary
            BudgetHealthLabel.FAIR -> MaterialTheme.colorScheme.tertiary
            BudgetHealthLabel.POOR -> ErrorRed
        },
        label = "HealthScoreColor"
    )

    val labelText = when (healthScore.label) {
        BudgetHealthLabel.EXCELLENT -> stringResource(R.string.budget_health_excellent)
        BudgetHealthLabel.GOOD -> stringResource(R.string.budget_health_good)
        BudgetHealthLabel.FAIR -> stringResource(R.string.budget_health_fair)
        BudgetHealthLabel.POOR -> stringResource(R.string.budget_health_poor)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = scoreColor.copy(alpha = 0.1f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, scoreColor.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(R.string.budget_health_score),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = healthScore.score.toString(),
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontFamily = SoraFontFamily,
                        fontWeight = FontWeight.Bold
                    ),
                    color = scoreColor
                )
                Text(
                    text = labelText,
                    style = MaterialTheme.typography.labelMedium,
                    color = scoreColor,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { healthScore.score / 100f },
                    modifier = Modifier.size(72.dp),
                    color = scoreColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeWidth = 8.dp,
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                Text(
                    text = healthScore.score.toString(),
                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = SoraFontFamily),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun OverviewStrip(state: BudgetTrackingState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OverviewCard(
            label = stringResource(R.string.budget_total_budgeted),
            amount = state.totalBudgeted,
            modifier = Modifier.weight(1f)
        )
        OverviewCard(
            label = stringResource(R.string.budget_total_spent),
            amount = state.totalSpent,
            amountColor = if (state.overallProgress > 1f) ErrorRed else MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )
        OverviewCard(
            label = stringResource(R.string.budget_remaining),
            amount = state.totalRemaining,
            amountColor = if (state.totalRemaining < 0) ErrorRed else SuccessGreen,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun OverviewCard(
    label: String,
    amount: Double,
    modifier: Modifier = Modifier,
    amountColor: Color = MaterialTheme.colorScheme.onBackground
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = CurrencyFormatter.format(amount),
                style = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = SoraFontFamily,
                    fontSize = 13.sp
                ),
                color = amountColor,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun OverallProgressBar(state: BudgetTrackingState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.budget_overall_progress),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${(state.overallProgress * 100).toInt()}%",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        
        val progressColor = when {
            state.overallProgress > 1f -> ErrorRed
            state.overallProgress > 0.8f -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.primary
        }

        val animatedProgress by animateFloatAsState(
            targetValue = state.overallProgress.coerceIn(0f, 1f),
            animationSpec = tween(800),
            label = "OverallProgress"
        )

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp)),
            color = progressColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }
}

@Composable
private fun SectionHeader(title: String, onAddClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontFamily = SoraFontFamily)
        )
        TextButton(onClick = onAddClick) {
            Text(
                text = stringResource(R.string.budget_add_new),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun EmptyBudgetsState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "🎯", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.budget_empty_title),
            style = MaterialTheme.typography.titleMedium.copy(fontFamily = SoraFontFamily)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.budget_empty_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
