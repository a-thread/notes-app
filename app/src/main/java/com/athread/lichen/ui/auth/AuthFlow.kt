package com.athread.lichen.ui.auth

import androidx.compose.runtime.Composable
import com.athread.lichen.ui.notes.AboutScreen
import com.athread.lichen.ui.auth.composables.CreateAccountScreen
import com.athread.lichen.ui.auth.composables.ForgotPasswordScreen
import com.athread.lichen.ui.auth.composables.login.LoginScreen
import com.athread.lichen.ui.auth.composables.ResetPasswordScreen
import com.athread.lichen.ui.auth.state.AuthScreen
import com.athread.lichen.ui.auth.state.AuthUiState
import com.athread.lichen.ui.auth.state.AuthViewModel
import com.athread.lichen.ui.notes.shared.theme.ThemeViewModel

@Composable
fun AuthFlow(
    authViewModel: AuthViewModel,
    themeViewModel: ThemeViewModel,
    authState: AuthUiState
) {
    when (authState.screen!!) {

        AuthScreen.Login -> {
            LoginScreen(
                viewModel = authViewModel,
                themeViewModel = themeViewModel,
                onCreateAccount = authViewModel::showCreateAccount,
                onAbout = authViewModel::showAbout,
                onForgotPassword = authViewModel::showForgotPassword
            )
        }

        AuthScreen.ForgotPassword -> {
            ForgotPasswordScreen(
                uiState = authState,
                onEmailChange = authViewModel::onEmailChange,
                onSendReset = authViewModel::resetPassword,
                onBack = authViewModel::showLogin
            )
        }

        AuthScreen.CreateAccount -> {
            CreateAccountScreen(
                uiState = authState,
                onEmailChange = authViewModel::onEmailChange,
                onPasswordChange = authViewModel::onPasswordChange,
                onConfirmPasswordChange = authViewModel::onConfirmPasswordChange,
                onSubmit = authViewModel::signUp,
                onBack = authViewModel::showLogin
            )
        }

        AuthScreen.ResetPassword -> {
            ResetPasswordScreen(
                uiState = authState,
                onPasswordChange = authViewModel::onPasswordChange,
                onConfirmPasswordChange = authViewModel::onConfirmPasswordChange,
                onSubmit = {
                    authViewModel.updatePassword {
                        authViewModel.showLogin()
                    }
                },
                onCancel = authViewModel::showLogin
            )
        }

        AuthScreen.About -> {
            AboutScreen(
                onBack = authViewModel::showLogin
            )
        }
    }
}
