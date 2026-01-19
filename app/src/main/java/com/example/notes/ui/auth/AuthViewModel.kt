package com.example.notes.ui.auth

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.domain.repository.AuthRepository
import com.example.notes.ui.auth.model.AuthDeepLinkResult
import io.github.jan.supabase.auth.exception.AuthErrorCode
import io.github.jan.supabase.auth.exception.AuthRestException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    /* ─────────────────────────────────────────────
     * Observe auth state ONCE (source of truth)
     * ───────────────────────────────────────────── */

    init {
        viewModelScope.launch {
            authRepository.userId.collect { userId: UUID? ->
                _uiState.update {
                    it.copy(
                        isAuthenticated = userId != null,
                        userId = userId
                    )
                }
            }
        }
    }

    /* ─────────────────────────────────────────────
     * Input handlers
     * ───────────────────────────────────────────── */

    fun onEmailChange(value: String) {
        _uiState.update {
            it.copy(
                email = value,
                errorMessage = null
            )
        }
    }

    fun onPasswordChange(value: String) {
        _uiState.update {
            it.copy(
                password = value,
                errorMessage = null
            )
        }
    }

    fun clearMessage() {
        _uiState.update {
            it.copy(
                errorMessage = null,
                infoMessage = null
            )
        }
    }

    fun showForgotPassword() {
        _uiState.update {
            it.copy(
                screen = AuthScreen.ForgotPassword,
                errorMessage = null,
                infoMessage = null
            )
        }
    }

    fun showCreateAccount() {
        _uiState.update {
            it.copy(
                screen = AuthScreen.CreateAccount,
                errorMessage = null,
                infoMessage = null
            )
        }
    }

    fun showLogin() {
        _uiState.update {
            it.copy(
                screen = AuthScreen.Login,
                errorMessage = null,
                infoMessage = null
            )
        }
    }

    /* ─────────────────────────────────────────────
     * Auth actions
     * ───────────────────────────────────────────── */

    fun signIn(onSuccess: () -> Unit) {
        submit(
            block = {
                authRepository.signIn(
                    email = uiState.value.email,
                    password = uiState.value.password
                )
            },
            onSuccess = onSuccess
        )
    }

    fun signUp() {
        val state = uiState.value
        if (!state.isEmailValid || !state.isPasswordValid || !state.passwordsMatch) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    infoMessage = null
                )
            }

            try {
                authRepository.signUp(state.email, state.password)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        screen = AuthScreen.Login, // ✅ return to login
                        password = "",
                        confirmPassword = "",
                        infoMessage =
                            "A confirmation email has been sent to you! " +
                                    "Please confirm your email to get access to your account."
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Unable to create account. Please try again."
                    )
                }
            }
        }
    }

    fun resetPassword() {
        val email = uiState.value.email
        if (email.isBlank() || !uiState.value.isEmailValid) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            try {
                authRepository.resetPassword(email)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        screen = AuthScreen.Login, // ✅ go back
                        infoMessage = "Check your email for a password reset link"
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Unable to send reset email. Please try again."
                    )
                }
            }
        }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update { it.copy(confirmPassword = value) }
    }

    fun updatePassword(onSuccess: () -> Unit) {
        val state = uiState.value

        if (!state.isPasswordValid || !state.passwordsMatch) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null)
            }

            try {
                authRepository.updatePassword(state.password)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        password = "",
                        confirmPassword = "",
                        screen = AuthScreen.Login,
                        infoMessage = "Password updated successfully"
                    )
                }

                onSuccess()

            } catch (e:AuthRestException) {
                // Supabase-specific errors
                val message = when (e.errorCode) {
                    AuthErrorCode.SamePassword ->
                        "New password must be different from your old password"
                    AuthErrorCode.WeakPassword ->
                        "Password is too weak"
                    else ->
                        e.message ?: "Unable to update password"
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = message
                    )
                }

            } catch (e: Exception) {
                // Safety net
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Something went wrong. Please try again."
                    )
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }

    /* ─────────────────────────────────────────────
     * Deep link handling (Supabase)
     * ───────────────────────────────────────────── */
    fun handleAuthDeepLink(uri: Uri) {
        viewModelScope.launch {
            when (authRepository.handleDeepLink(uri)) {

                AuthDeepLinkResult.ResetPassword -> {
                    _uiState.update {
                        it.copy(
                            screen = AuthScreen.ResetPassword,
                            password = "",
                            confirmPassword = "",
                            errorMessage = null,
                            infoMessage = null
                        )
                    }
                }

                AuthDeepLinkResult.SignupConfirmed -> {
                    _uiState.update {
                        it.copy(
                            isAuthenticated = true, // ✅ auto sign-in
                            screen = AuthScreen.Login, // router will move past this
                            infoMessage =
                                "Welcome! Your email has been confirmed and you're now signed in.",
                            errorMessage = null
                        )
                    }
                }

                null -> {
                    _uiState.update {
                        it.copy(
                            errorMessage = "Invalid or expired link"
                        )
                    }
                }
            }
        }
    }


    /* ─────────────────────────────────────────────
     * Shared submit logic
     * ───────────────────────────────────────────── */

    private fun submit(
        block: suspend () -> Unit,
        successMessage: String? = null,
        onSuccess: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    infoMessage = null
                )
            }

            try {
                block()

                successMessage?.let { msg ->
                    _uiState.update { s ->
                        s.copy(infoMessage = msg)
                    }
                }

                onSuccess?.invoke()

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = e.message ?: "Something went wrong"
                    )
                }
            } finally {
                _uiState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }
}
