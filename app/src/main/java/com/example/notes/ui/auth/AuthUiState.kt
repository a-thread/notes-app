package com.example.notes.ui.auth

import android.util.Patterns
import java.util.UUID

enum class AuthScreen {
    Login,
    ForgotPassword,
    ResetPassword,
    CreateAccount,
    About
}

data class AuthUiState(
    val screen: AuthScreen = AuthScreen.Login,
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
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

    val canSubmitReset: Boolean
        get() = email.isNotBlank() && isEmailValid && !isLoading

    val passwordsMatch: Boolean
        get() = password == confirmPassword

    val canSubmitNewPassword: Boolean
        get() = isPasswordValid && passwordsMatch && !isLoading

    val canSubmitCreateAccount: Boolean
        get() = isEmailValid && isPasswordValid && passwordsMatch && !isLoading
}
