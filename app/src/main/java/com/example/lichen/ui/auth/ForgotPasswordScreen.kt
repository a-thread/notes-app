package com.example.lichen.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.lichen.ui.auth.composables.*
import com.example.lichen.ui.shared.composable.AppButton
import com.example.lichen.ui.shared.composable.DetailScaffold

@Composable
fun ForgotPasswordScreen(
    uiState: AuthUiState,
    onEmailChange: (String) -> Unit,
    onSendReset: () -> Unit,
    onBack: () -> Unit
) {
    DetailScaffold(
        title = "Forgot your password?",
        subtitle = "Please enter the email address associated with your account and we’ll send you instructions for resetting your password.",
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
                imeAction = ImeAction.Done
            )
        )

        Spacer(Modifier.height(32.dp))

        AppButton(
            text = "Reset password",
            loading = uiState.isLoading,
            enabled = uiState.canSubmitReset,
            onClick = onSendReset
        )
    }
}
