package com.athread.lichen.ui.auth.state

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.athread.lichen.domain.repository.AuthRepository
import com.athread.lichen.ui.auth.model.AuthDeepLinkResult
import io.github.jan.supabase.auth.exception.AuthErrorCode
import io.github.jan.supabase.auth.exception.AuthRestException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Get user id, allowing time for initial value
            val userId = authRepository.userId.first()
                ?: withTimeoutOrNull(500) {
                    authRepository.userId.filterNotNull().first()
                }

            // Set initial state
            _uiState.update { state ->
                state.copy(
                    userId = userId,
                    isAuthenticated = userId != null,
                    isInitializing = false,
                    screen = if (userId == null) AuthScreen.Login else state.screen
                )
            }

            // Listen for changes to user id
            // and update state accordingly
            authRepository.userId
                .distinctUntilChanged()
                .collect { userId ->
                    _uiState.update { state ->
                        state.copy(
                            userId = userId,
                            isAuthenticated = userId != null,
                            isInitializing = false
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

    fun showAbout() {
        _uiState.update {
            it.copy(
                screen = AuthScreen.About,
                errorMessage = null,
                infoMessage = null
            )
        }
    }

    /* ─────────────────────────────────────────────
     * Auth actions
     * ───────────────────────────────────────────── */

    fun signIn() {
        submit(
            block = {
                authRepository.signIn(
                    email = uiState.value.email,
                    password = uiState.value.password
                )
            },
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
                        screen = AuthScreen.Login,
                        password = "",
                        confirmPassword = "",
                        infoMessage =
                            "A confirmation email has been sent to you! " +
                                    "Please confirm your email to get access to your account."
                    )
                }

            } catch (e: Exception) {
                Log.e("AuthViewModel", "Sign Up failed", e)
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
                        screen = AuthScreen.Login,
                        infoMessage = "Check your email for a password reset link"
                    )
                }

            } catch (e: Exception) {
                Log.e("AuthViewModel", "Reset Password failed", e)

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

            } catch (e: AuthRestException) {
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
                Log.e("AuthViewModel", "Update Password failed", e)

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
     * Deep link handling
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
                            screen = AuthScreen.Login,
                            infoMessage = "Welcome! Your email has been confirmed."
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

            } catch (e: Exception) {
                val message = when (e) {
                    is AuthRestException -> {
                        when (e.errorCode) {
                            AuthErrorCode.InvalidCredentials ->
                                "Incorrect email or password"
                            AuthErrorCode.EmailNotConfirmed ->
                                "Please confirm your email before signing in"
                            else ->
                                "Unable to sign in. Please try again."
                        }
                    }
                    else -> "Something went wrong. Please try again."
                }

                _uiState.update {
                    it.copy(errorMessage = message)
                }
            } finally {
                _uiState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }
}
