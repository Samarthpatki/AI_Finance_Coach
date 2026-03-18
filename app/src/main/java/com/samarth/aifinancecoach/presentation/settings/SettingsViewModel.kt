package com.samarth.aifinancecoach.presentation.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samarth.aifinancecoach.data.datastore.UserPreferencesDataStore
import com.samarth.aifinancecoach.domain.model.UserProfile
import com.samarth.aifinancecoach.domain.usecase.auth.GetCurrentUserUseCase
import com.samarth.aifinancecoach.domain.usecase.auth.SignOutUseCase
import com.samarth.aifinancecoach.domain.usecase.user.UpdateUserProfileUseCase
import com.samarth.aifinancecoach.domain.usecase.user.UploadProfilePhotoUseCase
import com.samarth.aifinancecoach.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val uploadProfilePhotoUseCase: UploadProfilePhotoUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        loadUser()
        loadPreferences()
    }

    private fun loadUser() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val user = getCurrentUserUseCase()
            user?.let { profile ->
                _state.update {
                    it.copy(
                        userId = profile.id,
                        name = profile.name,
                        email = profile.email,
                        photoUrl = profile.photoUrl,
                        monthlyIncome = profile.monthlyIncome.toString(),
                        selectedCurrency = profile.currency,
                        isLoading = false
                    )
                }
            } ?: _state.update { it.copy(isLoading = false) }
        }
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            userPreferencesDataStore.isDarkModeEnabled().collect { isEnabled ->
                _state.update { it.copy(isDarkMode = isEnabled) }
            }
        }
    }

    fun onNameChanged(name: String) {
        _state.update { it.copy(name = name, nameError = null) }
    }

    fun onIncomeChanged(income: String) {
        if (income.isEmpty() || income.matches(Regex("^\\d*\\.?\\d*$"))) {
            _state.update { it.copy(monthlyIncome = income, incomeError = null) }
        }
    }

    fun onCurrencySelected(currency: String) {
        _state.update { it.copy(selectedCurrency = currency) }
    }

    fun onDarkModeToggled() {
        val newValue = !_state.value.isDarkMode
        viewModelScope.launch {
            userPreferencesDataStore.setDarkMode(newValue)
        }
    }

    fun onProfilePhotoSelected(uri: Uri?) {
        val userId = _state.value.userId
        if (uri != null && userId.isNotEmpty()) {
            _state.update { it.copy(isUploadingPhoto = true) }
            viewModelScope.launch {
                try {
                    val downloadUrl = uploadProfilePhotoUseCase(userId, uri)
                    _state.update { it.copy(photoUrl = downloadUrl, isUploadingPhoto = false) }
                } catch (e: Exception) {
                    _state.update { it.copy(isUploadingPhoto = false) }
                }
            }
        }
    }

    fun onSaveClicked() {
        val currentState = _state.value
        if (currentState.name.isBlank()) {
            _state.update { it.copy(nameError = "Name cannot be empty") }
            return
        }
        val income = currentState.monthlyIncome.toDoubleOrNull()
        if (income == null) {
            _state.update { it.copy(incomeError = "Invalid income amount") }
            return
        }

        _state.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            try {
                val updatedProfile = UserProfile(
                    id = currentState.userId,
                    name = currentState.name,
                    email = currentState.email,
                    photoUrl = currentState.photoUrl,
                    monthlyIncome = income,
                    currency = currentState.selectedCurrency
                )
                updateUserProfileUseCase(updatedProfile)
                _state.update { it.copy(isSaving = false, successMessage = "Profile updated successfully") }
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false) }
            }
        }
    }

    fun onSignOutClicked() {
        _state.update { it.copy(showSignOutDialog = true) }
    }

    fun onConfirmSignOut() {
        viewModelScope.launch {
            signOutUseCase()
            userPreferencesDataStore.setUserLoggedIn(false)
            userPreferencesDataStore.setOnboardingComplete(false)
            userPreferencesDataStore.setProfileSetupComplete(false)
            _navigationEvent.emit(Screen.Login.route)
        }
    }

    fun onDismissSignOut() {
        _state.update { it.copy(showSignOutDialog = false) }
    }

    fun clearSuccessMessage() {
        _state.update { it.copy(successMessage = null) }
    }
}
