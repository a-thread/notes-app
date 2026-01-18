package com.example.notes.ui.auth

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.domain.repository.AuthRepository
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
        submit(
            block = {
                authRepository.signUp(
                    email = uiState.value.email,
                    password = uiState.value.password
                )
            },
            successMessage = "Registration successful! Check your email to confirm."
        )
    }

    fun resetPassword() {
        submit(
            block = {
                authRepository.resetPassword(uiState.value.email)
            },
            successMessage = "Password reset link sent to your email."
        )
    }

    /* ─────────────────────────────────────────────
     * Deep link handling (Supabase)
     * ───────────────────────────────────────────── */

    /**
     * Supabase automatically processes the auth link internally.
     * This function ONLY updates UI messaging.
     */
    fun handleAuthDeepLink(uri: Uri) {
        _uiState.update {
            it.copy(
                infoMessage = "Authentication successful. You may continue.",
                errorMessage = null
            )
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
