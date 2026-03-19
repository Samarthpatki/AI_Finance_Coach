package com.samarth.aifinancecoach.presentation.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samarth.aifinancecoach.domain.usecase.auth.SignInWithGoogleUseCase
import com.samarth.aifinancecoach.domain.usecase.auth.SignUpWithEmailUseCase
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
class SignUpViewModel @Inject constructor(
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val signUpWithEmailUseCase: SignUpWithEmailUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SignUpState())
    val state: StateFlow<SignUpState> = _state.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun onNameChanged(name: String) {
        _state.update { it.copy(name = name) }
    }

    fun onEmailChanged(email: String) {
        _state.update { it.copy(email = email) }
    }

    fun onPasswordChanged(password: String) {
        _state.update { it.copy(password = password) }
    }

    fun onConfirmPasswordChanged(password: String) {
        _state.update { it.copy(confirmPassword = password) }
    }

    fun signUp() {
        val name = _state.value.name
        val email = _state.value.email
        val password = _state.value.password
        val confirmPassword = _state.value.confirmPassword

        if (name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            _state.update { it.copy(error = "Please fill in all fields") }
            return
        }

        if (password != confirmPassword) {
            _state.update { it.copy(error = "Passwords do not match") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            signUpWithEmailUseCase(name, email, password).onSuccess {
                _state.update { it.copy(isLoading = false, isSuccess = true) }
                _navigationEvent.emit(Screen.ProfileSetup.route)
            }.onFailure { exception ->
                _state.update { it.copy(isLoading = false, error = exception.message ?: "Sign up failed") }
            }
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
                _state.update { it.copy(isLoading = false, error = exception.message ?: "Sign up failed") }
            }
        }
    }
    
    fun onLoginClick() {
        viewModelScope.launch {
            _navigationEvent.emit(Screen.Login.route)
        }
    }
}
