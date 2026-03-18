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

        // ─── Dashboard ────────────────────────────────────────────
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController = navController)
        }

        // ─── Transactions ─────────────────────────────────────────
        composable(Screen.TransactionList.route) {
            // TransactionListScreen(navController = navController)
        }

        composable(
            route = Screen.AddTransaction.route,
            arguments = listOf(
                navArgument(Screen.AddTransaction.ARG_TRANSACTION_ID) {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) {
            // AddTransactionScreen(navController = navController)
        }

        composable(
            route = Screen.TransactionDetail.route,
            arguments = listOf(
                navArgument(Screen.TransactionDetail.ARG_TRANSACTION_ID) {
                    type = NavType.LongType
                }
            )
        ) {
            // TransactionDetailScreen(navController = navController)
        }

        composable(Screen.RecurringTransactions.route) {
            // RecurringTransactionsScreen(navController = navController)
        }

        // ─── Budget ───────────────────────────────────────────────────
        composable(Screen.BudgetSetup.route) {
            // BudgetSetupScreen(navController = navController)
        }

        composable(Screen.BudgetTracking.route) {
            // BudgetTrackingScreen(navController = navController)
        }

        // ─── AI ───────────────────────────────────────────────────
        composable(Screen.AiChat.route) {
            // AiChatScreen(navController = navController)
        }

        composable(Screen.AiInsights.route) {
            // AiInsightsScreen(navController = navController)
        }

        composable(Screen.AiReport.route) {
            // AiReportScreen(navController = navController)
        }

        // ─── Analytics ────────────────────────────────────────────
        composable(Screen.Analytics.route) {
            // AnalyticsScreen(navController = navController)
        }

        // ─── Settings ─────────────────────────────────────────────
        composable(Screen.Settings.route) {
            // SettingsScreen(navController = navController)
        }
    }
}
