package com.samarth.aifinancecoach.presentation.splash


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samarth.aifinancecoach.data.datastore.UserPreferencesDataStore
import com.samarth.aifinancecoach.domain.usecase.auth.IsUserLoggedInUseCase
import com.samarth.aifinancecoach.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val isUserLoggedInUseCase: IsUserLoggedInUseCase,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _navigationRoute = MutableStateFlow<String?>(null)
    val navigationRoute: StateFlow<String?> = _navigationRoute

    init {
        decideNavigation()
    }

    private fun decideNavigation() {
        viewModelScope.launch {
            // Minimum splash display time — branding visibility
//            delay(1500L)

            val isLoggedIn      = isUserLoggedInUseCase()
            val onboardingSeen  = userPreferencesDataStore.isOnboardingComplete()
            val profileComplete = userPreferencesDataStore.isProfileSetupComplete()

            _navigationRoute.value = when {
                isLoggedIn && profileComplete  -> Screen.Dashboard.route
                isLoggedIn && !profileComplete -> Screen.ProfileSetup.route
                onboardingSeen                 -> Screen.Login.route
                else                           -> Screen.Onboarding.route
            }
        }
    }
}
