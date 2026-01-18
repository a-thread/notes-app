package com.example.notes.ui.auth

import android.util.Patterns
import java.util.UUID

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null,
    val userId: UUID? = null,
    val isAuthenticated: Boolean = false
) {
    val isEmailValid: Boolean
        get() = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    val isPasswordValid: Boolean
        get() = password.length >= 6

    val canSubmit: Boolean
        get() = isEmailValid && isPasswordValid && !isLoading
}
