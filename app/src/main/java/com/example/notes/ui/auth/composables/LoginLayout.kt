package com.example.notes.ui.auth.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.notes.ui.auth.AuthUiState
import com.example.notes.ui.shared.composable.*

@Composable
fun LoginLayout(
    uiState: AuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignIn: () -> Unit,
    onCreateAccount: () -> Unit,
    onAbout: () -> Unit,
    onForgotPassword: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "lichen",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 8.dp)
        )

        Text(
            text = "Ideas, resilient by design.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 32.dp)
        )

        AuthTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            label = "Email address",
            isError = uiState.email.isNotBlank() && !uiState.isEmailValid,
            errorText = "Enter a valid email address",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        Spacer(Modifier.height(16.dp))

        PasswordField(
            value = uiState.password,
            onValueChange = onPasswordChange,
            label = "Password",
            isError = uiState.password.isNotBlank() && !uiState.isPasswordValid,
            errorText = "Password must be at least 6 characters",
            imeAction = ImeAction.Done,
            onDone = {
                if (uiState.canSubmit && !uiState.isLoading) {
                    onSignIn()
                }
            }
        )


        Spacer(Modifier.height(32.dp))

        AppButton(
            text = "Sign in",
            loading = uiState.isLoading,
            enabled = uiState.canSubmit,
            onClick = onSignIn
        )

        Spacer(Modifier.height(16.dp))

        TextButton(
            onClick = onForgotPassword,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                "Forgot password?",
                color = MaterialTheme.colorScheme.tertiary
            )
        }

        Spacer(Modifier.height(8.dp))

        TextButton(
            onClick = onCreateAccount,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                "Create an account",
                color = MaterialTheme.colorScheme.tertiary
            )
        }

        TextButton(
            onClick = onAbout,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "About Lichen",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
