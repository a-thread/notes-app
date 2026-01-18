package com.example.notes.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
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
                // Status bar spacer — prevents overlap
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsTopHeight(WindowInsets.safeDrawing)
                )

                // Banner slot (fixed height to avoid layout jump)
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

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "BlackNote",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 32.dp)
            )

            // ───────────── Email ─────────────
            OutlinedTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                label = { Text("Email") },
                singleLine = true,
                isError = uiState.email.isNotBlank() && !uiState.isEmailValid,
                supportingText = {
                    if (uiState.email.isNotBlank() && !uiState.isEmailValid) {
                        Text("Enter a valid email address")
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ───────────── Password ─────────────
            OutlinedTextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                isError = uiState.password.isNotBlank() && !uiState.isPasswordValid,
                supportingText = {
                    if (uiState.password.isNotBlank() && !uiState.isPasswordValid) {
                        Text("Password must be at least 6 characters")
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ───────────── Sign In ─────────────
            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.signIn(onLoginSuccess)
                },
                enabled = uiState.canSubmit,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Sign In")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = onCreateAccount,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Create Account")
            }

            Spacer(modifier = Modifier.height(4.dp))

            TextButton(
                onClick = onForgotPassword,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Forgot password?")
            }
        }
    }
}

