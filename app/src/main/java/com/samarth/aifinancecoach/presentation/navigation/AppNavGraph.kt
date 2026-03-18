package com.samarth.aifinancecoach.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.samarth.aifinancecoach.presentation.auth.login.LoginScreen
import com.samarth.aifinancecoach.presentation.auth.onboarding.OnboardingScreen
import com.samarth.aifinancecoach.presentation.auth.profile.ProfileSetupScreen
import com.samarth.aifinancecoach.presentation.auth.signup.SignUpScreen
import com.samarth.aifinancecoach.presentation.dashboard.DashboardScreen
import com.samarth.aifinancecoach.presentation.transaction.add.AddTransactionScreen
import com.samarth.aifinancecoach.presentation.transaction.detail.TransactionDetailScreen
import com.samarth.aifinancecoach.presentation.transaction.list.TransactionListScreen
import com.samarth.aifinancecoach.presentation.transaction.recurring.RecurringScreen
import com.samarth.aifinancecoach.presentation.main.MainScreen
import com.samarth.aifinancecoach.presentation.budget.tracking.BudgetTrackingScreen
import com.samarth.aifinancecoach.presentation.budget.setup.BudgetSetupScreen
import com.samarth.aifinancecoach.presentation.ai.AiCoachScreen
import com.samarth.aifinancecoach.presentation.ai.insights.AiInsightsScreen
import com.samarth.aifinancecoach.presentation.ai.report.AiReportScreen

@Composable
fun AppNavGraph(
    startDestination: String = Screen.Onboarding.route,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // ─── Auth ─────────────────────────────────────────────────
        composable(Screen.Onboarding.route) {
            OnboardingScreen(navController = navController)
        }

        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(navController = navController)
        }

        composable(Screen.ProfileSetup.route) {
            ProfileSetupScreen(navController = navController)
        }

        // ─── Main / Dashboard (Container for Pager) ───────────────
        composable(Screen.Dashboard.route) {
            MainScreen(navController = navController)
        }

        // ─── Transactions ─────────────────────────────────────────
        composable(Screen.TransactionList.route) {
            TransactionListScreen(navController = navController)
        }

        composable(
            route = Screen.AddTransaction.route,
            arguments = listOf(
                navArgument(Screen.AddTransaction.ARG_TRANSACTION_ID) {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getLong(Screen.AddTransaction.ARG_TRANSACTION_ID)
            AddTransactionScreen(navController = navController, transactionId = transactionId)
        }

        composable(
            route = Screen.TransactionDetail.route,
            arguments = listOf(
                navArgument(Screen.TransactionDetail.ARG_TRANSACTION_ID) {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getLong(Screen.TransactionDetail.ARG_TRANSACTION_ID) ?: return@composable
            TransactionDetailScreen(navController = navController, transactionId = transactionId)
        }

        composable(Screen.RecurringTransactions.route) {
            RecurringScreen(navController = navController)
        }

        // ─── Budget ───────────────────────────────────────────────────
        composable(
            route = Screen.BudgetSetup.route,
            arguments = listOf(
                navArgument(Screen.BudgetSetup.ARG_BUDGET_ID) {
                    type = NavType.LongType
                    defaultValue = -1L
                },
                navArgument(Screen.BudgetSetup.ARG_CATEGORY) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            BudgetSetupScreen(navController = navController)
        }

        composable(Screen.BudgetTracking.route) {
            BudgetTrackingScreen(navController = navController)
        }

        // ─── AI ───────────────────────────────────────────────────
        composable(Screen.AiChat.route) {
            AiCoachScreen()
        }

        composable(Screen.AiInsights.route) {
            AiInsightsScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.AiReport.route) {
            AiReportScreen()
        }

        // ─── Analytics ────────────────────────────────────────────
        composable(Screen.Analytics.route) {
            // AnalyticsScreen(navController = navController)
        }

        // ─── Settings / Profile ────────────────────────────────────────
        composable(Screen.Profile.route) {
             // ProfileScreen(navController = navController)
        }

        composable(Screen.Settings.route) {
            // SettingsScreen(navController = navController)
        }
    }
}
