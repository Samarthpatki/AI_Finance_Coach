package com.samarth.aifinancecoach.presentation.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.AccountBox
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.samarth.aifinancecoach.presentation.dashboard.DashboardContent
import com.samarth.aifinancecoach.presentation.dashboard.DashboardViewModel
import com.samarth.aifinancecoach.presentation.navigation.Screen
import com.samarth.aifinancecoach.presentation.theme.SoraFontFamily
import com.samarth.aifinancecoach.presentation.transaction.list.TransactionListContent
import com.samarth.aifinancecoach.presentation.transaction.list.TransactionListViewModel
import com.samarth.aifinancecoach.presentation.budget.tracking.BudgetTrackingContent
import com.samarth.aifinancecoach.presentation.budget.tracking.BudgetTrackingViewModel
import com.samarth.aifinancecoach.presentation.ai.AiCoachScreen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun MainScreen(navController: NavController) {
    val pagerState = rememberPagerState(pageCount = { 4 })
    val scope = rememberCoroutineScope()
    
    // ViewModels for the tabs
    val dashboardViewModel: DashboardViewModel = hiltViewModel()
    val transactionListViewModel: TransactionListViewModel = hiltViewModel()
    val budgetTrackingViewModel: BudgetTrackingViewModel = hiltViewModel()
    
    val dashboardState by dashboardViewModel.state.collectAsStateWithLifecycle()
    val transactionListState by transactionListViewModel.state.collectAsStateWithLifecycle()
    val budgetTrackingState by budgetTrackingViewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle Dashboard navigation events (ones that are NOT tab switches)
    LaunchedEffect(Unit) {
        dashboardViewModel.navigationEvent.collectLatest { route ->
            // Only navigate if it's NOT one of the pager tabs
            if (route != Screen.TransactionList.route && 
                route != Screen.BudgetTracking.route && 
                route != Screen.AiChat.route) {
                navController.navigate(route)
            }
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                val navItemStyle = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = SoraFontFamily,
                    fontSize = 9.sp,
                    letterSpacing = 0.sp
                )

                NavigationBarItem(
                    selected = pagerState.currentPage == 0,
                    onClick = { scope.launch { pagerState.animateScrollToPage(0) } },
                    icon = { Icon(Icons.Rounded.Home, contentDescription = "Home") },
                    label = { 
                        Text(
                            text = "Home", 
                            style = navItemStyle,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Visible
                        ) 
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = pagerState.currentPage == 1,
                    onClick = { scope.launch { pagerState.animateScrollToPage(1) } },
                    icon = { Icon(Icons.AutoMirrored.Rounded.List, contentDescription = "Transactions") },
                    label = { 
                        Text(
                            text = "Transactions", 
                            style = navItemStyle,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Visible
                        ) 
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = pagerState.currentPage == 2,
                    onClick = { scope.launch { pagerState.animateScrollToPage(2) } },
                    icon = { Icon(Icons.Rounded.AccountBox, contentDescription = "Budget") },
                    label = { 
                        Text(
                            text = "Budget", 
                            style = navItemStyle,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Visible
                        ) 
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = pagerState.currentPage == 3,
                    onClick = { scope.launch { pagerState.animateScrollToPage(3) } },
                    icon = { Icon(Icons.Rounded.AutoAwesome, contentDescription = "AI Coach") },
                    label = { 
                        Text(
                            text = "AI Coach", 
                            style = navItemStyle,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Visible
                        ) 
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) { page ->
            when (page) {
                0 -> DashboardContent(
                    state = dashboardState,
                    onAddTransactionClick = { navController.navigate(Screen.AddTransaction.createRoute()) },
                    onBudgetClick = { scope.launch { pagerState.animateScrollToPage(2) } },
                    onSeeAllTransactionsClick = { scope.launch { pagerState.animateScrollToPage(1) } },
                    onInsightClick = { insight -> navController.navigate(Screen.AiInsights.route) },
                    onProfileClick = { dashboardViewModel.onProfileClick() },
//                    onProfileClick = { navController.navigate(Screen.Profile.route) },
                    onTransactionClick = { transaction ->
                        navController.navigate(Screen.TransactionDetail.createRoute(transaction.id)) 
                    },
                    onDeleteTransaction = { transaction -> 
                        // Typically handled in Transaction List
                    },
                 )
                1 -> TransactionListContent(
                    state = transactionListState,
                    snackbarHostState = snackbarHostState,
                    onSearchQueryChanged = transactionListViewModel::onSearchQueryChanged,
                    onFilterChanged = transactionListViewModel::onFilterChanged,
                    onMonthChanged = transactionListViewModel::onMonthChanged,
                    onDeleteTransaction = transactionListViewModel::deleteTransaction,
                    onAddTransactionClick = { navController.navigate(Screen.AddTransaction.createRoute()) },
                    onTransactionClick = { transaction ->
                        navController.navigate(Screen.TransactionDetail.createRoute(transaction.id))
                    }
                )
                2 -> BudgetTrackingContent(
                    state = budgetTrackingState,
                    snackbarHostState = snackbarHostState,
                    onMonthChanged = budgetTrackingViewModel::onMonthChanged,
                    onAddBudgetClick = { navController.navigate(Screen.BudgetSetup.createRoute()) },
                    onEditBudgetClick = { budget -> 
                        navController.navigate(Screen.BudgetSetup.createRoute(budgetId = budget.id)) 
                    },
                    onDeleteBudget = budgetTrackingViewModel::deleteBudget
                )
                3 -> AiCoachScreen()
            }
        }
    }
}
