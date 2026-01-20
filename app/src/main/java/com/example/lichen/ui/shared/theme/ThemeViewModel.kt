package com.example.lichen.ui.shared.theme

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ThemeViewModel : ViewModel() {

    // null = follow system, true = dark, false = light
    private val _darkModeOverride = MutableStateFlow<Boolean?>(null)
    val darkModeOverride: StateFlow<Boolean?> = _darkModeOverride

    fun setDarkMode(enabled: Boolean) {
        _darkModeOverride.value = enabled
    }
}

