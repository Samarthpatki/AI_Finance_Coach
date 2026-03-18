package com.samarth.aifinancecoach.presentation.transaction.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.samarth.aifinancecoach.R
import com.samarth.aifinancecoach.domain.model.Transaction
import com.samarth.aifinancecoach.presentation.components.MonthSelector
import com.samarth.aifinancecoach.presentation.components.TransactionCard
import com.samarth.aifinancecoach.presentation.navigation.Screen
import com.samarth.aifinancecoach.presentation.theme.DmSansFontFamily
import com.samarth.aifinancecoach.presentation.theme.ErrorRed
import com.samarth.aifinancecoach.presentation.theme.SoraFontFamily
import com.samarth.aifinancecoach.presentation.theme.SuccessGreen
import com.samarth.aifinancecoach.utils.CurrencyFormatter
import com.samarth.aifinancecoach.utils.DateUtils
import kotlinx.coroutines.flow.collectLatest

@Composable
fun TransactionListScreen(
    navController: NavController,
    viewModel: TransactionListViewModel = hiltViewModel()
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

    TransactionListContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onFilterChanged = viewModel::onFilterChanged,
        onMonthChanged = viewModel::onMonthChanged,
        onDeleteTransaction = viewModel::deleteTransaction,
        onAddTransactionClick = { navController.navigate(Screen.AddTransaction.createRoute()) },
        onTransactionClick = { transaction ->
            navController.navigate(Screen.TransactionDetail.createRoute(transaction.id))
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListContent(
    state: TransactionListState,
    snackbarHostState: SnackbarHostState,
    onSearchQueryChanged: (String) -> Unit,
    onFilterChanged: (TransactionFilter) -> Unit,
    onMonthChanged: (Int, Int) -> Unit,
    onDeleteTransaction: (Transaction) -> Unit,
    onAddTransactionClick: () -> Unit,
    onTransactionClick: (Transaction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.transactions_title),
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
                onClick = onAddTransactionClick,
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
                        contentDescription = stringResource(R.string.dashboard_add_transaction),
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
            // Search Bar
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = onSearchQueryChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(52.dp),
                placeholder = { Text(stringResource(R.string.transactions_search_hint)) },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                shape = RoundedCornerShape(26.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                singleLine = true
            )

            // Summary Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SummaryMiniCard(
                    label = stringResource(R.string.transactions_total_income),
                    amount = state.totalIncome,
                    color = SuccessGreen,
                    modifier = Modifier.weight(1f)
                )
                SummaryMiniCard(
                    label = stringResource(R.string.transactions_total_expense),
                    amount = state.totalExpense,
                    color = ErrorRed,
                    modifier = Modifier.weight(1f)
                )
            }

            // Filter Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(TransactionFilter.values()) { filter ->
                    FilterChip(
                        selected = state.selectedFilter == filter,
                        onClick = { onFilterChanged(filter) },
                        label = {
                            Text(
                                text = when (filter) {
                                    TransactionFilter.ALL -> stringResource(R.string.transactions_filter_all)
                                    TransactionFilter.INCOME -> stringResource(R.string.transactions_filter_income)
                                    TransactionFilter.EXPENSE -> stringResource(R.string.transactions_filter_expense)
                                },
                                style = MaterialTheme.typography.labelMedium.copy(fontFamily = DmSansFontFamily)
                            )
                        },
                        shape = RoundedCornerShape(17.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = MaterialTheme.colorScheme.outline,
                            selectedBorderColor = Color.Transparent,
                            enabled = true,
                            selected = false
                        )
                    )
                }
            }

            // Month Selector
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
                },
                canGoNext = state.selectedYear < DateUtils.getCurrentYear() ||
                            (state.selectedYear == DateUtils.getCurrentYear() && state.selectedMonth < DateUtils.getCurrentMonth())
            )

            // Transaction List
            if (state.groupedTransactions.isEmpty() && !state.isLoading) {
                EmptyTransactionsState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    state.groupedTransactions.forEach { (section, transactions) ->
                        item(key = section) {
                            SectionHeader(section)
                        }
                        items(transactions, key = { it.id }) { transaction ->
                            TransactionCard(
                                transaction = transaction,
                                onClick = { onTransactionClick(transaction) },
                                onDelete = { onDeleteTransaction(transaction) }
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryMiniCard(
    label: String,
    amount: Double,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(color, RoundedCornerShape(3.dp))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = CurrencyFormatter.format(amount),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = SoraFontFamily,
                    color = color
                )
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EmptyTransactionsState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "💸", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.transactions_empty_title),
            style = MaterialTheme.typography.titleMedium.copy(fontFamily = SoraFontFamily)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.transactions_empty_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
