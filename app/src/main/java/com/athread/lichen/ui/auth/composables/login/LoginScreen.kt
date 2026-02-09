package com.athread.lichen.ui.auth.composables.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import com.athread.lichen.ui.auth.state.AuthViewModel
import com.athread.lichen.ui.auth.composables.shared.alertbanner.AlertBanner
import com.athread.lichen.ui.notes.shared.composable.LoadingScreen
import com.athread.lichen.ui.auth.composables.shared.alertbanner.AlertType
import com.athread.lichen.ui.notes.shared.theme.ThemeViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    themeViewModel: ThemeViewModel,
    onCreateAccount: () -> Unit,
    onAbout: () -> Unit,
    onForgotPassword: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    Box(modifier = Modifier.fillMaxSize()) {

        // ───────────── Main content ─────────────
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.surface
        ) { padding ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                LoginLayout(
                    uiState = uiState,
                    themeViewModel,
                    onEmailChange = viewModel::onEmailChange,
                    onPasswordChange = viewModel::onPasswordChange,
                    onSignIn = {
                        focusManager.clearFocus()
                        viewModel.signIn()
                    },
                    onCreateAccount = onCreateAccount,
                    onAbout = onAbout,
                    onForgotPassword = onForgotPassword,
                    modifier = Modifier.padding(padding)
                )
            }

            // ───────────── Error / Info banner (top overlay) ─────────────
            if (uiState.errorMessage != null || uiState.infoMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .align(Alignment.TopCenter)
                ) {
                    when {
                        uiState.errorMessage != null -> {
                            AlertBanner(
                                message = uiState.errorMessage,
                                type = AlertType.Error,
                                onDismiss = viewModel::clearMessage
                            )
                        }

                        else -> {
                            AlertBanner(
                                message = uiState.infoMessage,
                                type = AlertType.Info,
                                onDismiss = viewModel::clearMessage
                            )
                        }
                    }
                }
            }

            // ───────────── Loading overlay (full-screen overlay) ─────────────
            if (uiState.isLoading) {
                LoadingScreen()
            }
        }
    }
}
