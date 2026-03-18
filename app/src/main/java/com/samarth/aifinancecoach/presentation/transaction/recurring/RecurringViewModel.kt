package com.samarth.aifinancecoach.presentation.transaction.recurring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samarth.aifinancecoach.domain.model.Transaction
import com.samarth.aifinancecoach.domain.usecase.transaction.DeleteTransactionUseCase
import com.samarth.aifinancecoach.domain.usecase.transaction.GetRecurringTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecurringViewModel @Inject constructor(
    private val getRecurringTransactionsUseCase: GetRecurringTransactionsUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase
) : ViewModel() {

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        getRecurringTransactionsUseCase()
            .onEach { _transactions.value = it }
            .launchIn(viewModelScope)
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            deleteTransactionUseCase(id)
        }
    }
}
