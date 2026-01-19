package com.example.notes.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.notes.ui.auth.composables.*
import com.example.notes.ui.shared.composable.AppButton
import com.example.notes.ui.shared.composable.DetailScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(
    uiState: AuthUiState,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit
) {
    DetailScaffold(
        title = "Set new password",
        onBack = onCancel
    ) {

        Text(
            text = "Choose a new password for your account.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        PasswordField(
            value = uiState.password,
            onValueChange = onPasswordChange,
            label = "New password",
            isError = uiState.password.isNotBlank() && !uiState.isPasswordValid,
            errorText = "Password must be at least 6 characters",
            imeAction = ImeAction.Next,
            onDone = {}
        )

        Spacer(Modifier.height(12.dp))

        PasswordField(
            value = uiState.confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = "Confirm password",
            isError = uiState.confirmPassword.isNotBlank() && !uiState.passwordsMatch,
            errorText = "Passwords do not match",
            imeAction = ImeAction.Done,
            onDone = {
                if (uiState.canSubmitNewPassword) onSubmit()
            }
        )

        Spacer(Modifier.height(24.dp))

        AppButton(
            text = "Update password",
            loading = uiState.isLoading,
            enabled = uiState.canSubmitNewPassword,
            onClick = onSubmit
        )
    }
}