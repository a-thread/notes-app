package com.example.notes.ui.auth.composables

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation

@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean,
    errorText: String,
    imeAction: ImeAction,
    modifier: Modifier = Modifier
) {
    AuthTextField(
        value,
        onValueChange,
        label,
        isError,
        errorText,
        modifier,
        PasswordVisualTransformation(),
        KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        )
    )
}
