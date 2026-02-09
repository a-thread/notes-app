package com.athread.lichen.ui.auth.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.athread.lichen.ui.auth.composables.shared.AuthTextField
import com.athread.lichen.ui.auth.composables.shared.PasswordField
import com.athread.lichen.ui.auth.state.AuthUiState
import com.athread.lichen.ui.auth.composables.shared.AppButton
import com.athread.lichen.ui.auth.composables.shared.DetailScaffold

@Composable
fun CreateAccountScreen(
    uiState: AuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    DetailScaffold(
        title = "Let's get started",
        subtitle = "Fill in your email and create your free account.",
        onBack = onBack
    ) {

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
            imeAction = ImeAction.Next,
            onDone = {}
        )

        Spacer(Modifier.height(16.dp))

        PasswordField(
            value = uiState.confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = "Confirm password",
            isError = uiState.confirmPassword.isNotBlank() && !uiState.passwordsMatch,
            errorText = "Passwords do not match",
            imeAction = ImeAction.Done,
            onDone = {
                if (uiState.canSubmitCreateAccount) {
                    onSubmit()
                }
            }
        )

        Spacer(Modifier.height(32.dp))

        AppButton(
            text = "Create account",
            loading = uiState.isLoading,
            enabled = uiState.canSubmitCreateAccount,
            onClick = onSubmit
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "By signing up, you agree to our terms of use and privacy policy.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
