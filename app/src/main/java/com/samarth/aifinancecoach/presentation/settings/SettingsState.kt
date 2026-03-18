package com.samarth.aifinancecoach.presentation.settings

data class SettingsState(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val monthlyIncome: String = "",
    val selectedCurrency: String = "INR",
    val isDarkMode: Boolean = true,
    val nameError: String? = null,
    val incomeError: String? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isUploadingPhoto: Boolean = false,
    val showSignOutDialog: Boolean = false,
    val successMessage: String? = null
)
