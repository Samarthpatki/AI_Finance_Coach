package com.samarth.aifinancecoach.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.samarth.aifinancecoach.R
import com.samarth.aifinancecoach.domain.model.AiInsight
import com.samarth.aifinancecoach.domain.model.Transaction
import com.samarth.aifinancecoach.presentation.components.*
import com.samarth.aifinancecoach.presentation.navigation.Screen
import com.samarth.aifinancecoach.presentation.theme.AIFinanceCoachTheme
import com.samarth.aifinancecoach.presentation.theme.SoraFontFamily
import com.samarth.aifinancecoach.utils.DateUtils
import kotlinx.coroutines.flow.collectLatest

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.navigationEvent) {
        viewModel.navigationEvent.collectLatest { route ->
            navController.navigate(route)
        }
    }

    DashboardContent(
        state = state,
        onAddTransactionClick = viewModel::onAddTransactionClick,
        onBudgetClick = viewModel::onBudgetClick,
        onSeeAllTransactionsClick = viewModel::onSeeAllTransactionsClick,
        onInsightClick = viewModel::onInsightClick,
        onProfileClick = viewModel::onProfileClick,
        onTransactionClick = { transaction ->
            navController.navigate(Screen.TransactionDetail.createRoute(transaction.id))
        },
        onDeleteTransaction = { /* Dashboard usually doesn't delete directly */ },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardContent(
    state: DashboardState,
    onAddTransactionClick: () -> Unit,
    onBudgetClick: () -> Unit,
    onSeeAllTransactionsClick: () -> Unit,
    onInsightClick: (AiInsight) -> Unit,
    onProfileClick: () -> Unit,
    onTransactionClick: (Transaction) -> Unit,
    onDeleteTransaction: (Transaction) -> Unit,

) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = DateUtils.getGreeting(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${state.userName} 👋",
                            style = MaterialTheme.typography.headlineMedium.copy(fontFamily = SoraFontFamily),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            imageVector = Icons.Rounded.Person,
                            contentDescription = "Profile",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTransactionClick,
                shape = MaterialTheme.shapes.small,
                containerColor = Color.Transparent,
                elevation = FloatingActionButtonDefaults.elevation(5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF00C896), Color(0xFF0096FF))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = stringResource(R.string.dashboard_add_transaction),
                        tint = Color.White
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Hero Summary Card
            item {
                SummaryCard(
                    totalIncome = state.totalIncome,
                    totalExpense = state.totalExpense,
                    monthName = DateUtils.getMonthName(state.currentMonth),
                    year = state.currentYear
                )
            }

            // Budget Health Section
            item {
                SectionHeader(
                    title = stringResource(R.string.dashboard_budget_health),
                    actionText = stringResource(R.string.dashboard_see_all),
                    onActionClick = onBudgetClick
                )
                if (state.budgets.isEmpty()) {
                    EmptyState(
                        message = stringResource(R.string.dashboard_no_budgets),
                        actionText = "Set up budgets",
                        onActionClick = onBudgetClick
                    )
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(state.budgets) { budget ->
                            BudgetProgressCard(
                                budget = budget,
                                onClick = onBudgetClick
                            )
                        }
                    }
                }
            }

            // Recent Transactions Section
            item {
                SectionHeader(
                    title = stringResource(R.string.dashboard_recent_transactions),
                    actionText = stringResource(R.string.dashboard_see_all),
                    onActionClick = onSeeAllTransactionsClick
                )
                if (state.recentTransactions.isEmpty()) {
                    EmptyState(
                        message = stringResource(R.string.dashboard_no_transactions),
                        actionText = "Add Transaction",
                        onActionClick = onAddTransactionClick
                    )
                }
            }

            items(state.recentTransactions) { transaction ->
                TransactionCard(
                    transaction = transaction,
                    onClick = { onTransactionClick(transaction) },
                    onDelete = { onDeleteTransaction(transaction) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            }

            // AI Insights Section
            item {
                SectionHeader(
                    title = stringResource(R.string.dashboard_ai_insights)
                )
                if (state.aiInsights.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Rounded.Face,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = stringResource(R.string.dashboard_no_insights),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(state.aiInsights) { insight ->
                            InsightCard(
                                insight = insight,
                                onClick = { onInsightClick(insight) }
                            )
                        }
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    AIFinanceCoachTheme {
        DashboardContent(
            state = DashboardState(
                userName = "Samarth",
                totalIncome = 50000.0,
                totalExpense = 20000.0
            ),
            onAddTransactionClick = {},
            onBudgetClick = {},
            onSeeAllTransactionsClick = {},
            onInsightClick = {},
            onProfileClick = {},
            onTransactionClick = {},
            onDeleteTransaction = {},

         )
    }
}
