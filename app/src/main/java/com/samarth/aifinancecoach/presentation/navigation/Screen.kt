package com.samarth.aifinancecoach.presentation.navigation


sealed class Screen(val route: String) {

    // ─── Auth Flow ────────────────────────────────────────────────
    object Onboarding       : Screen("onboarding")
    object Login            : Screen("login")
    object SignUp           : Screen("signup")
    object ProfileSetup     : Screen("profile_setup")

    // ─── Main / Dashboard ─────────────────────────────────────────
    object Dashboard        : Screen("dashboard")

    // ─── Transactions ─────────────────────────────────────────────
    object TransactionList  : Screen("transaction_list")

    object TransactionDetail : Screen("transaction_detail/{transactionId}") {
        fun createRoute(transactionId: Long) = "transaction_detail/$transactionId"
        const val ARG_TRANSACTION_ID = "transactionId"
    }

    object AddTransaction : Screen("add_transaction?transactionId={transactionId}") {
        fun createRoute(transactionId: Long? = null) =
            if (transactionId != null) "add_transaction?transactionId=$transactionId"
            else "add_transaction"
        const val ARG_TRANSACTION_ID = "transactionId"
    }

    object RecurringTransactions : Screen("recurring_transactions")

    // ─── Budget ───────────────────────────────────────────────────
    object BudgetSetup      : Screen("budget_setup")
    object BudgetTracking   : Screen("budget_tracking")

    // ─── AI ───────────────────────────────────────────────────────
    object AiChat           : Screen("ai_chat")
    object AiInsights       : Screen("ai_insights")
    object AiReport         : Screen("ai_report")

    // ─── Analytics ────────────────────────────────────────────────
    object Analytics        : Screen("analytics")

    // ─── Settings / Profile ────────────────────────────────────────
    object Profile          : Screen("profile")
    object Settings         : Screen("settings")
}
