package com.samarth.aifinancecoach.presentation.budget.setup

import com.samarth.aifinancecoach.domain.model.Budget
import com.samarth.aifinancecoach.domain.model.Category
import com.samarth.aifinancecoach.utils.DateUtils

data class BudgetSetupState(
    val budgetId: Long? = null,
    val selectedCategory: Category? = null,
    val limitAmount: String = "",
    val currentMonth: Int = DateUtils.getCurrentMonth(),
    val currentYear: Int = DateUtils.getCurrentYear(),
    val existingBudget: Budget? = null,
    val aiSuggestion: Double? = null,
    val isLoadingAiSuggestion: Boolean = false,
    val categoryError: String? = null,
    val amountError: String? = null,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val isEditMode: Boolean = false
)
