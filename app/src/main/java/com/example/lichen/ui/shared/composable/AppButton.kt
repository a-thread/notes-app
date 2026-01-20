package com.example.lichen.ui.shared.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


enum class AppButtonStyle {
    Primary,
    Secondary
}


@Composable
fun AppButton(
    modifier: Modifier = Modifier,
    text: String,
    loading: Boolean,
    enabled: Boolean,
    style: AppButtonStyle = AppButtonStyle.Primary,
    onClick: () -> Unit
) {
    val content: @Composable RowScope.() -> Unit = {
        Text(text)

        if (loading) {
            Spacer(modifier = Modifier.width(8.dp))
            CircularProgressIndicator(
                strokeWidth = 2.dp,
                modifier = Modifier.size(16.dp),
                color = when (style) {
                    AppButtonStyle.Primary ->
                        MaterialTheme.colorScheme.onPrimary
                    AppButtonStyle.Secondary ->
                        MaterialTheme.colorScheme.primary
                }
            )
        }
    }

    when (style) {
        AppButtonStyle.Primary -> {
            Button(
                onClick = onClick,
                enabled = enabled && !loading,
                modifier = modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    content = content
                )
            }
        }

        AppButtonStyle.Secondary -> {
            OutlinedButton(
                onClick = onClick,
                enabled = enabled && !loading,
                modifier = modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    content = content
                )
            }
        }
    }
}
