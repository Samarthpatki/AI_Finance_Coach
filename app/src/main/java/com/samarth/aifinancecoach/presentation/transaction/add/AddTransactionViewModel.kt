package com.samarth.aifinancecoach.presentation.transaction.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samarth.aifinancecoach.domain.model.AccountType
import com.samarth.aifinancecoach.domain.model.Category
import com.samarth.aifinancecoach.domain.model.Transaction
import com.samarth.aifinancecoach.domain.model.TransactionType
import com.samarth.aifinancecoach.domain.usecase.transaction.AddTransactionUseCase
import com.samarth.aifinancecoach.domain.usecase.transaction.GetTransactionByIdUseCase
import com.samarth.aifinancecoach.domain.usecase.transaction.UpdateTransactionUseCase
import com.samarth.aifinancecoach.presentation.navigation.Screen
import com.samarth.aifinancecoach.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val addTransactionUseCase: AddTransactionUseCase,
    private val updateTransactionUseCase: UpdateTransactionUseCase,
    private val getTransactionByIdUseCase: GetTransactionByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(AddTransactionState())
    val state: StateFlow<AddTransactionState> = _state.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        val transactionId = savedStateHandle.get<Long>(Screen.AddTransaction.ARG_TRANSACTION_ID)
        if (transactionId != null && transactionId != -1L) {
            loadTransaction(transactionId)
        }
    }

    private fun loadTransaction(id: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, isEditMode = true, transactionId = id) }
            getTransactionByIdUseCase(id)?.let { transaction ->
                _state.update { it.copy(
                    selectedType = transaction.type,
                    amount = transaction.amount.toString(),
                    selectedCategory = transaction.category,
                    selectedAccount = transaction.account,
                    note = transaction.note,
                    selectedDateMillis = transaction.dateMillis,
                    isRecurring = transaction.isRecurring,
                    recurringIntervalDays = transaction.recurringIntervalDays,
                    isLoading = false
                ) }
            }
        }
    }

    fun onTypeSelected(type: TransactionType) {
        _state.update { it.copy(selectedType = type, selectedCategory = null) }
    }

    fun onAmountChanged(amount: String) {
        if (amount.isEmpty() || amount.matches(Regex("^\\d*\\.?\\d*$"))) {
            _state.update { it.copy(amount = amount, amountError = null) }
        }
    }

    fun onCategorySelected(category: Category) {
        _state.update { it.copy(selectedCategory = category, categoryError = null) }
    }

    fun onAccountSelected(account: AccountType) {
        _state.update { it.copy(selectedAccount = account) }
    }

    fun onNoteChanged(note: String) {
        _state.update { it.copy(note = note) }
    }

    fun onDateSelected(millis: Long) {
        _state.update { it.copy(selectedDateMillis = millis) }
    }

    fun onRecurringToggled(isRecurring: Boolean) {
        _state.update { it.copy(isRecurring = isRecurring) }
    }

    fun onRecurringIntervalChanged(days: Int) {
        _state.update { it.copy(recurringIntervalDays = days) }
    }

    fun onSaveClicked() {
        val amountValue = state.value.amount.toDoubleOrNull()
        if (amountValue == null || amountValue <= 0) {
            _state.update { it.copy(amountError = "Please enter a valid amount") }
            return
        }
        if (state.value.selectedCategory == null) {
            _state.update { it.copy(categoryError = "Please select a category") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val dateMillis = state.value.selectedDateMillis
            val transaction = Transaction(
                id = state.value.transactionId ?: 0,
                amount = amountValue,
                type = state.value.selectedType,
                category = state.value.selectedCategory!!,
                account = state.value.selectedAccount,
                note = state.value.note,
                dateMillis = dateMillis,
                month = DateUtils.getMonthFromMillis(dateMillis),
                year = DateUtils.getYearFromMillis(dateMillis),
                isRecurring = state.value.isRecurring,
                recurringIntervalDays = state.value.recurringIntervalDays ?: 30
            )

            if (state.value.isEditMode) {
                updateTransactionUseCase(transaction)
            } else {
                addTransactionUseCase(transaction)
            }
            _state.update { it.copy(isLoading = false, isSaved = true) }
            _navigationEvent.emit("popBackStack")
        }
    }
}
