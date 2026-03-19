package com.samarth.aifinancecoach.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samarth.aifinancecoach.data.datastore.UserPreferencesDataStore
import com.samarth.aifinancecoach.domain.usecase.auth.SignInWithEmailUseCase
import com.samarth.aifinancecoach.domain.usecase.auth.SignInWithGoogleUseCase
import com.samarth.aifinancecoach.presentation.navigation.Screen
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
class LoginViewModel @Inject constructor(
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val signInWithEmailUseCase: SignInWithEmailUseCase,
    private val dataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun onEmailChanged(email: String) {
        _state.update { it.copy(email = email) }
    }

    fun onPasswordChanged(password: String) {
        _state.update { it.copy(password = password) }
    }

    fun login() {
        val email = _state.value.email
        val password = _state.value.password

        if (email.isBlank() || password.isBlank()) {
            _state.update { it.copy(error = "Please fill in all fields") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            signInWithEmailUseCase(email, password).onSuccess { userProfile ->
                _state.update { it.copy(isLoading = false, isSuccess = true) }
                if (userProfile.profileSetupComplete) {
                    _navigationEvent.emit(Screen.Dashboard.route)
                } else {
                    _navigationEvent.emit(Screen.ProfileSetup.route)
                }
            }.onFailure { exception ->
                _state.update { it.copy(isLoading = false, error = exception.message ?: "Login failed") }
            }
        }
    }

    fun onForgotPasswordClick() {
        // Handle forgot password
    }

    fun onSignUpClick() {
        viewModelScope.launch {
            _navigationEvent.emit(Screen.SignUp.route)
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            signInWithGoogleUseCase(idToken).onSuccess { userProfile ->
                _state.update { it.copy(isLoading = false, isSuccess = true) }
                
                if (userProfile.profileSetupComplete) {
                    _navigationEvent.emit(Screen.Dashboard.route)
                } else {
                    _navigationEvent.emit(Screen.ProfileSetup.route)
                }
            }.onFailure { exception ->
                _state.update { it.copy(isLoading = false, error = exception.message ?: "Sign in failed") }
            }
        }
    }
}
