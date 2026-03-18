package com.samarth.aifinancecoach.presentation.transaction.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samarth.aifinancecoach.domain.model.Transaction
import com.samarth.aifinancecoach.domain.usecase.transaction.DeleteTransactionUseCase
import com.samarth.aifinancecoach.domain.usecase.transaction.GetTransactionByIdUseCase
import com.samarth.aifinancecoach.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionDetailViewModel @Inject constructor(
    private val getTransactionByIdUseCase: GetTransactionByIdUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _transaction = MutableStateFlow<Transaction?>(null)
    val transaction: StateFlow<Transaction?> = _transaction.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        val transactionId = savedStateHandle.get<Long>(Screen.TransactionDetail.ARG_TRANSACTION_ID)
        if (transactionId != null) {
            loadTransaction(transactionId)
        }
    }

    private fun loadTransaction(id: Long) {
        viewModelScope.launch {
            _transaction.value = getTransactionByIdUseCase(id)
        }
    }

    fun onDeleteClick() {
        _transaction.value?.let { transaction ->
            viewModelScope.launch {
                deleteTransactionUseCase(transaction.id)
                _navigationEvent.emit("popBackStack")
            }
        }
    }
}
