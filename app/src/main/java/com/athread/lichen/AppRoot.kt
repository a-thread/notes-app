package com.athread.lichen

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.athread.lichen.di.AppModule
import com.athread.lichen.ui.auth.AuthFlow
import com.athread.lichen.ui.auth.state.AuthViewModel
import com.athread.lichen.ui.auth.state.AuthViewModelFactory
import com.athread.lichen.ui.notes.NotesFlow
import com.athread.lichen.ui.notes.shared.composable.LoadingScreen
import com.athread.lichen.ui.notes.shared.theme.ThemeViewModel

@Composable
fun AppRoot(
    themeViewModel: ThemeViewModel,
    deepLink: Uri?
) {
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(
            AppModule.provideAuthRepository()
        )
    )

    val authState by authViewModel.uiState.collectAsState()

    LaunchedEffect(deepLink) {
        deepLink?.let { uri ->
            if (uri.scheme == "lichenapp" && uri.host == "auth") {
                authViewModel.handleAuthDeepLink(uri)
            }
        }
    }

    when {
        authState.isInitializing -> {
            LoadingScreen()
        }

        authState.isAuthenticated -> {
            NotesFlow(
                userId = authState.userId!!,
                themeViewModel = themeViewModel,
                onLogout = authViewModel::signOut
            )
        }

        authState.screen != null -> {
            AuthFlow(
                authViewModel = authViewModel,
                themeViewModel = themeViewModel,
                authState = authState,
            )
        }
    }

}
