package com.samarth.aifinancecoach.presentation.auth.profile

data class ProfileSetupState(
    val name: String = "",
    val monthlyIncome: String = "",
    val selectedCurrency: String = "INR",
    val selectedCurrencySymbol: String = "₹",
    val nameError: String? = null,
    val incomeError: String? = null,
    val isLoading: Boolean = false,
    val currentStep: Int = 1
)
