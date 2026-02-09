package com.athread.lichen.ui.notes.shared.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.athread.lichen.domain.model.Note

sealed interface AppRoute {
    data object List : AppRoute
    data object Create : AppRoute
    data class Edit(val note: Note) : AppRoute
}

class AppNavigationViewModel : ViewModel() {

    var route: AppRoute by mutableStateOf(AppRoute.List)
        private set

    fun createNote() {
        route = AppRoute.Create
    }

    fun editNote(note: Note) {
        route = AppRoute.Edit(note)
    }

    fun closeEditor() {
        route = AppRoute.List
    }
}
