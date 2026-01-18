package com.example.notes.ui.shared.composable

import androidx.compose.runtime.Composable

@Composable
fun AppRouter(
    isLoading: Boolean,
    isAuthenticated: Boolean,
    loading: @Composable () -> Unit,
    unauthenticated: @Composable () -> Unit,
    authenticated: @Composable () -> Unit
) {
    when {
        isLoading -> loading()
        !isAuthenticated -> unauthenticated()
        else -> authenticated()
    }
}
