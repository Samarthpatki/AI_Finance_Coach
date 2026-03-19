package com.samarth.aifinancecoach.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samarth.aifinancecoach.data.datastore.UserPreferencesDataStore
import com.samarth.aifinancecoach.domain.model.AiInsight
import com.samarth.aifinancecoach.domain.usecase.ai.GetAiInsightsUseCase
import com.samarth.aifinancecoach.domain.usecase.analytics.GetMonthlyAnalyticsUseCase
import com.samarth.aifinancecoach.domain.usecase.auth.GetCurrentUserUseCase
import com.samarth.aifinancecoach.domain.usecase.budget.GetBudgetsForMonthUseCase
import com.samarth.aifinancecoach.domain.usecase.transaction.GetRecentTransactionsUseCase
import com.samarth.aifinancecoach.presentation.navigation.Screen
import com.samarth.aifinancecoach.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getRecentTransactionsUseCase: GetRecentTransactionsUseCase,
    private val getMonthlyAnalyticsUseCase: GetMonthlyAnalyticsUseCase,
    private val getBudgetsForMonthUseCase: GetBudgetsForMonthUseCase,
    private val getAiInsightsUseCase: GetAiInsightsUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val dataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            val month = DateUtils.getCurrentMonth()
            val year = DateUtils.getCurrentYear()

            // Observe User Name
            dataStore.getUserName().onEach { name ->
                _state.update { it.copy(userName = name) }
            }.launchIn(viewModelScope)

            // Observe Recent Transactions
            getRecentTransactionsUseCase(5).onEach { transactions ->
                _state.update { it.copy(recentTransactions = transactions) }
            }.launchIn(viewModelScope)

            // Observe Analytics
            getMonthlyAnalyticsUseCase(month, year).onEach { analytics ->
                _state.update { it.copy(
                    totalIncome = analytics.totalIncome,
                    totalExpense = analytics.totalExpense
                ) }
            }.launchIn(viewModelScope)

            // Observe Budgets
            getBudgetsForMonthUseCase(month, year).onEach { budgets ->
                _state.update { it.copy(budgets = budgets) }
            }.launchIn(viewModelScope)

            // Observe AI Insights
            getAiInsightsUseCase().onEach { insights ->
                _state.update { it.copy(aiInsights = insights) }
            }.launchIn(viewModelScope)

            _state.update { it.copy(isLoading = false) }
        }
    }

    fun onAddTransactionClick() {
        viewModelScope.launch {
            _navigationEvent.emit(Screen.AddTransaction.route)
        }
    }

    fun onSeeAllTransactionsClick() {
        viewModelScope.launch {
            _navigationEvent.emit(Screen.TransactionList.route)
        }
    }

    fun onBudgetClick() {
        viewModelScope.launch {
            _navigationEvent.emit(Screen.BudgetTracking.route)
        }
    }

    fun onInsightClick(insight: AiInsight) {
        viewModelScope.launch {
            _navigationEvent.emit(Screen.AiInsights.route)
        }
    }

    fun onProfileClick() {
        viewModelScope.launch {
            _navigationEvent.emit(Screen.Settings.route)
        }
    }
}
