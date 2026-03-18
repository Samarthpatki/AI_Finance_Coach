package com.samarth.aifinancecoach.presentation.transaction.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samarth.aifinancecoach.domain.model.Transaction
import com.samarth.aifinancecoach.domain.model.TransactionType
import com.samarth.aifinancecoach.domain.usecase.transaction.*
import com.samarth.aifinancecoach.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val getTransactionsByMonthUseCase: GetTransactionsByMonthUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val searchTransactionsUseCase: SearchTransactionsUseCase,
    private val addTransactionUseCase: AddTransactionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TransactionListState())
    val state: StateFlow<TransactionListState> = _state.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _snackbarEvent = MutableSharedFlow<SnackbarData>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()

    private var deletedTransaction: Transaction? = null
    private var transactionsJob: Job? = null

    init {
        loadTransactions()
    }

    fun loadTransactions() {
        transactionsJob?.cancel()
        transactionsJob = getTransactionsByMonthUseCase(state.value.selectedMonth, state.value.selectedYear)
            .onEach { transactions ->
                _state.update { it.copy(
                    allTransactions = transactions,
                    isLoading = false
                ) }
                applyFiltersAndGrouping()
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChanged(query: String) {
        _state.update { it.copy(searchQuery = query) }
        if (query.isBlank()) {
            loadTransactions()
        } else {
            transactionsJob?.cancel()
            transactionsJob = searchTransactionsUseCase(query)
                .onEach { transactions ->
                    _state.update { it.copy(allTransactions = transactions) }
                    applyFiltersAndGrouping()
                }
                .launchIn(viewModelScope)
        }
    }

    fun onFilterChanged(filter: TransactionFilter) {
        _state.update { it.copy(selectedFilter = filter) }
        applyFiltersAndGrouping()
    }

    fun onMonthChanged(month: Int, year: Int) {
        _state.update { it.copy(selectedMonth = month, selectedYear = year) }
        loadTransactions()
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            deletedTransaction = transaction
            deleteTransactionUseCase(transaction.id)
            _snackbarEvent.emit(
                SnackbarData(
                    message = "Transaction deleted",
                    actionLabel = "Undo",
                    onAction = { undoDelete() }
                )
            )
        }
    }

    private fun undoDelete() {
        viewModelScope.launch {
            deletedTransaction?.let {
                addTransactionUseCase(it)
                deletedTransaction = null
            }
        }
    }

    private fun applyFiltersAndGrouping() {
        val filtered = when (state.value.selectedFilter) {
            TransactionFilter.ALL -> state.value.allTransactions
            TransactionFilter.INCOME -> state.value.allTransactions.filter { it.type == TransactionType.INCOME }
            TransactionFilter.EXPENSE -> state.value.allTransactions.filter { it.type == TransactionType.EXPENSE }
        }

        val grouped = groupTransactionsByDate(filtered)
        val income = filtered.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val expense = filtered.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

        _state.update { it.copy(
            filteredTransactions = filtered,
            groupedTransactions = grouped,
            totalIncome = income,
            totalExpense = expense
        ) }
    }

    private fun groupTransactionsByDate(transactions: List<Transaction>): Map<String, List<Transaction>> {
        return transactions.groupBy { DateUtils.getDateSection(it.dateMillis) }
            .toMutableMap()
            .apply {
                // Ensure order
                val sortedMap = LinkedHashMap<String, List<Transaction>>()
                listOf("Today", "Yesterday", "This Week", "Older").forEach { section ->
                    this[section]?.let { sortedMap[section] = it }
                }
            }
    }
}

data class SnackbarData(
    val message: String,
    val actionLabel: String? = null,
    val onAction: (() -> Unit)? = null
)
