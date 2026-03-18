package com.samarth.aifinancecoach.presentation.auth.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samarth.aifinancecoach.domain.model.UserProfile
import com.samarth.aifinancecoach.domain.usecase.auth.GetCurrentUserUseCase
import com.samarth.aifinancecoach.domain.usecase.user.SaveUserProfileUseCase
import com.samarth.aifinancecoach.presentation.navigation.Screen
import com.samarth.aifinancecoach.utils.isValidIncome
import com.samarth.aifinancecoach.utils.isValidName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileSetupViewModel @Inject constructor(
    private val saveUserProfileUseCase: SaveUserProfileUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileSetupState())
    val state: StateFlow<ProfileSetupState> = _state.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun onNameChanged(name: String) {
        _state.update { it.copy(name = name, nameError = null) }
    }

    fun onIncomeChanged(income: String) {
        _state.update { it.copy(monthlyIncome = income, incomeError = null) }
    }

    fun onCurrencySelected(currency: String, symbol: String) {
        _state.update { it.copy(selectedCurrency = currency, selectedCurrencySymbol = symbol) }
    }

    fun onContinue() {
        val currentState = _state.value
        val isNameValid = currentState.name.isValidName()
        val isIncomeValid = currentState.monthlyIncome.isValidIncome()

        if (!isNameValid) {
            _state.update { it.copy(nameError = "Please enter your name") }
        }
        if (!isIncomeValid) {
            _state.update { it.copy(incomeError = "Please enter a valid income amount") }
        }

        if (isNameValid && isIncomeValid) {
            if (currentState.currentStep == 1) {
                _state.update { it.copy(currentStep = 2) }
            } else {
                saveProfile()
            }
        }
    }

    fun onBack() {
        if (_state.value.currentStep > 1) {
            _state.update { it.copy(currentStep = 1) }
        }
    }

    private fun saveProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val currentUser = getCurrentUserUseCase()
            if (currentUser != null) {
                val profile = currentUser.copy(
                    name = _state.value.name,
                    monthlyIncome = _state.value.monthlyIncome.toDoubleOrNull() ?: 0.0,
                    currency = _state.value.selectedCurrency,
                    currencySymbol = _state.value.selectedCurrencySymbol,
                    profileSetupComplete = true
                )
                saveUserProfileUseCase(profile)
                _navigationEvent.emit(Screen.Dashboard.route)
            } else {
                _state.update { it.copy(isLoading = false) }
                // Handle error
            }
        }
    }
}
