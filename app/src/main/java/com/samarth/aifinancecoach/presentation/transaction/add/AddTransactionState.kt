package com.samarth.aifinancecoach.presentation.transaction.add

import com.samarth.aifinancecoach.domain.model.AccountType
import com.samarth.aifinancecoach.domain.model.Category
import com.samarth.aifinancecoach.domain.model.TransactionType

data class AddTransactionState(
    val transactionId: Long? = null,
    val selectedType: TransactionType = TransactionType.EXPENSE,
    val amount: String = "",
    val selectedCategory: Category? = null,
    val selectedAccount: AccountType = AccountType.UPI,
    val note: String = "",
    val selectedDateMillis: Long = System.currentTimeMillis(),
    val isRecurring: Boolean = false,
    val recurringIntervalDays: Int = 30,
    val amountError: String? = null,
    val categoryError: String? = null,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val isEditMode: Boolean = false
)
