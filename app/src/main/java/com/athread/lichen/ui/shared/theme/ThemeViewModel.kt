package com.athread.lichen.ui.shared.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class ThemePreference {
    SYSTEM,
    LIGHT,
    DARK
}

class ThemeViewModel : ViewModel() {

    private val _themePreference =
        MutableStateFlow(ThemePreference.SYSTEM)
    val themePreference: StateFlow<ThemePreference> =
        _themePreference

    fun setDark() {
        _themePreference.value = ThemePreference.DARK
    }

    fun setLight() {
        _themePreference.value = ThemePreference.LIGHT
    }

    fun followSystem() {
        _themePreference.value = ThemePreference.SYSTEM
    }
}

@Composable
fun ThemeViewModel.isDarkTheme(): Boolean {
    val preference by themePreference.collectAsState()
    val systemIsDark = isSystemInDarkTheme()

    return when (preference) {
        ThemePreference.DARK -> true
        ThemePreference.LIGHT -> false
        ThemePreference.SYSTEM -> systemIsDark
    }
}
