package com.synapse.social.studioasinc.web.presentation.auth

import com.synapse.social.studioasinc.shared.domain.usecase.auth.SignInUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AuthViewModel(
    private val signInUseCase: SignInUseCase
) {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    suspend fun signIn(): Boolean {
        val currentState = _uiState.value
        if (currentState.email.isBlank() || currentState.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter email and password") }
            return false
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        val result = signInUseCase(currentState.email, currentState.password)

        _uiState.update { it.copy(isLoading = false) }

        return if (result.isSuccess) {
            true
        } else {
            _uiState.update { it.copy(errorMessage = result.exceptionOrNull()?.message ?: "Login failed") }
            false
        }
    }
}

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
