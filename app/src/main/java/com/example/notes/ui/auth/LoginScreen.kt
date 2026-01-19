package com.example.notes.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.example.notes.ui.auth.composables.LoginLayout
import com.example.notes.ui.shared.composable.AlertBanner
import com.example.notes.ui.shared.model.AlertType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onCreateAccount: () -> Unit,
    onForgotPassword: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsTopHeight(WindowInsets.safeDrawing)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                ) {
                    when {
                        uiState.errorMessage != null -> {
                            AlertBanner(
                                message = uiState.errorMessage,
                                type = AlertType.Error,
                                onDismiss = viewModel::clearMessage
                            )
                        }

                        uiState.infoMessage != null -> {
                            AlertBanner(
                                message = uiState.infoMessage,
                                type = AlertType.Info,
                                onDismiss = viewModel::clearMessage
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->

        LoginLayout(
            uiState = uiState,
            onEmailChange = viewModel::onEmailChange,
            onPasswordChange = viewModel::onPasswordChange,
            onSignIn = {
                focusManager.clearFocus()
                viewModel.signIn(onLoginSuccess)
            },
            onCreateAccount = onCreateAccount,
            onForgotPassword = onForgotPassword,
            modifier = Modifier.padding(padding)
        )
    }
}
