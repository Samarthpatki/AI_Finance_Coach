package com.samarth.aifinancecoach.presentation.budget.setup

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samarth.aifinancecoach.domain.model.Budget
import com.samarth.aifinancecoach.domain.model.Category
import com.samarth.aifinancecoach.domain.repository.BudgetRepository
import com.samarth.aifinancecoach.domain.usecase.budget.GetBudgetSuggestionUseCase
import com.samarth.aifinancecoach.domain.usecase.budget.SetBudgetUseCase
import com.samarth.aifinancecoach.presentation.navigation.Screen
import com.samarth.aifinancecoach.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class BudgetSetupViewModel @Inject constructor(
    private val setBudgetUseCase: SetBudgetUseCase,
    private val getBudgetSuggestionUseCase: GetBudgetSuggestionUseCase,
    private val budgetRepository: BudgetRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(BudgetSetupState())
    val state: StateFlow<BudgetSetupState> = _state.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        val budgetId = savedStateHandle.get<Long>(Screen.BudgetSetup.ARG_BUDGET_ID)
        val categoryArg = savedStateHandle.get<String>(Screen.BudgetSetup.ARG_CATEGORY)

        if (budgetId != null && budgetId != -1L) {
            loadExistingBudget(budgetId)
        } else if (categoryArg != null) {
            val category = try { Category.valueOf(categoryArg) } catch (e: Exception) { null }
            category?.let { onCategorySelected(it) }
        }
    }

    private fun loadExistingBudget(id: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val budget = budgetRepository.getBudgetById(id)
            budget?.let { b ->
                _state.update { it.copy(
                    budgetId = b.id,
                    selectedCategory = b.category,
                    limitAmount = b.limitAmount.toString(),
                    isEditMode = true,
                    isLoading = false
                ) }
                fetchAiSuggestion(b.category)
            }
        }
    }

    fun onCategorySelected(category: Category) {
        if (state.value.isEditMode) return
        
        _state.update { it.copy(
            selectedCategory = category,
            categoryError = null
        ) }
        checkExistingBudget(category)
        fetchAiSuggestion(category)
    }

    private fun checkExistingBudget(category: Category) {
        viewModelScope.launch {
            val existing = budgetRepository.getBudgetForCategory(
                category,
                state.value.currentMonth,
                state.value.currentYear
            )
            _state.update { it.copy(existingBudget = existing) }
        }
    }

    private fun fetchAiSuggestion(category: Category) {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingAiSuggestion = true) }
            val suggestion = getBudgetSuggestionUseCase(
                category,
                state.value.currentMonth,
                state.value.currentYear
            )
            _state.update { it.copy(
                aiSuggestion = suggestion,
                isLoadingAiSuggestion = false
            ) }
        }
    }

    fun onAmountChanged(amount: String) {
        if (amount.isEmpty() || amount.matches(Regex("^\\d*\\.?\\d*\$"))) {
            _state.update { it.copy(limitAmount = amount, amountError = null) }
        }
    }

    fun onApplyAiSuggestion() {
        state.value.aiSuggestion?.let { suggestion ->
            _state.update { it.copy(limitAmount = String.format(Locale.getDefault(), "%.2f", suggestion)) }
        }
    }

    fun onSaveClicked() {
        val category = state.value.selectedCategory
        val amount = state.value.limitAmount.toDoubleOrNull()

        if (category == null) {
            _state.update { it.copy(categoryError = "Please select a category") }
            return
        }
        if (amount == null || amount <= 0) {
            _state.update { it.copy(amountError = "Please enter a valid amount") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            val budget = Budget(
                id = state.value.budgetId ?: 0,
                category = category,
                limitAmount = amount,
                spentAmount = state.value.existingBudget?.spentAmount ?: 0.0,
                month = state.value.currentMonth,
                year = state.value.currentYear
            )
            
            setBudgetUseCase(budget)
            _state.update { it.copy(isSaved = true, isLoading = false) }
            _navigationEvent.emit("pop")
        }
    }
}
