package com.samarth.aifinancecoach.presentation.main

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samarth.aifinancecoach.data.datastore.UserPreferencesDataStore
import com.samarth.aifinancecoach.domain.usecase.auth.IsUserLoggedInUseCase
import com.samarth.aifinancecoach.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val isUserLoggedInUseCase: IsUserLoggedInUseCase,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

    // Keeps splash screen visible while true
    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    // Decides where the app navigates after splash
    private val _startDestination = mutableStateOf(Screen.Onboarding.route)
    val startDestination: State<String> = _startDestination

    val isDarkMode: StateFlow<Boolean> = userPreferencesDataStore
        .isDarkModeEnabled()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    init {
        decideStartDestination()
    }

    private fun decideStartDestination() {
        viewModelScope.launch {
            try {
                val isLoggedIn = isUserLoggedInUseCase()
                val profileComplete = userPreferencesDataStore.isProfileSetupComplete()

                _startDestination.value = when {
                    // Returning user, fully set up → go straight to Dashboard
                    isLoggedIn && profileComplete -> Screen.Dashboard.route

                    // Logged in but profile not set up yet
                    isLoggedIn && !profileComplete -> Screen.ProfileSetup.route

                    // Not logged in → Always show Onboarding first
                    else -> Screen.Onboarding.route
                }
            } catch (e: Exception) {
                // Default to Onboarding on error
                _startDestination.value = Screen.Onboarding.route
            } finally {
                // Auth check done — dismiss splash
                _isLoading.value = false
            }
        }
    }
}
