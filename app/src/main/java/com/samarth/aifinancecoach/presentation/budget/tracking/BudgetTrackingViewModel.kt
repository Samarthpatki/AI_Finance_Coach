package com.samarth.aifinancecoach.presentation.budget.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samarth.aifinancecoach.domain.model.Budget
import com.samarth.aifinancecoach.domain.usecase.budget.DeleteBudgetUseCase
import com.samarth.aifinancecoach.domain.usecase.budget.GetBudgetHealthScoreUseCase
import com.samarth.aifinancecoach.domain.usecase.budget.GetBudgetsForMonthUseCase
import com.samarth.aifinancecoach.domain.usecase.budget.SetBudgetUseCase
import com.samarth.aifinancecoach.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BudgetTrackingViewModel @Inject constructor(
    private val getBudgetsForMonthUseCase: GetBudgetsForMonthUseCase,
    private val deleteBudgetUseCase: DeleteBudgetUseCase,
    private val setBudgetUseCase: SetBudgetUseCase,
    private val getBudgetHealthScoreUseCase: GetBudgetHealthScoreUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(BudgetTrackingState())
    val state: StateFlow<BudgetTrackingState> = _state.asStateFlow()

    private val _snackbarEvent = MutableSharedFlow<BudgetSnackbarData>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()

    private var budgetsJob: Job? = null
    private var deletedBudget: Budget? = null

    init {
        loadBudgets()
    }

    fun loadBudgets() {
        budgetsJob?.cancel()
        budgetsJob = getBudgetsForMonthUseCase(state.value.selectedMonth, state.value.selectedYear)
            .onEach { budgets ->
                val totalBudgeted = budgets.sumOf { it.limitAmount }
                val totalSpent = budgets.sumOf { it.spentAmount }
                val healthScore = getBudgetHealthScoreUseCase(budgets)

                _state.update { it.copy(
                    budgets = budgets,
                    totalBudgeted = totalBudgeted,
                    totalSpent = totalSpent,
                    healthScore = healthScore,
                    isLoading = false
                ) }
            }
            .launchIn(viewModelScope)
    }

    fun onMonthChanged(month: Int, year: Int) {
        _state.update { it.copy(selectedMonth = month, selectedYear = year, isLoading = true) }
        loadBudgets()
    }

    fun deleteBudget(budget: Budget) {
        viewModelScope.launch {
            deletedBudget = budget
            deleteBudgetUseCase(budget.id)
            _snackbarEvent.emit(
                BudgetSnackbarData(
                    message = "Budget deleted",
                    actionLabel = "Undo",
                    onAction = { undoDelete() }
                )
            )
        }
    }

    private fun undoDelete() {
        viewModelScope.launch {
            deletedBudget?.let {
                setBudgetUseCase(it)
                deletedBudget = null
            }
        }
    }
}

data class BudgetSnackbarData(
    val message: String,
    val actionLabel: String? = null,
    val onAction: (() -> Unit)? = null
)
