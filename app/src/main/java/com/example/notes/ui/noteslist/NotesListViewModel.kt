package com.example.notes.ui.noteslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.domain.model.Note
import com.example.notes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class NotesLayoutMode {
    GRID,
    LIST
}

class NotesListViewModel(
    private val repository: NoteRepository
) : ViewModel() {

    val notes: StateFlow<List<Note>>
        get() = repository.observeNotes()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = emptyList()
                )

    val layoutMode =
        MutableStateFlow(NotesLayoutMode.GRID)
    private var recentlyDeletedNote: Note? = null

    init {
        viewModelScope.launch {
            repository.syncFromRemote()
        }
    }

    fun toggleLayout() {
        layoutMode.value =
            if (layoutMode.value == NotesLayoutMode.GRID)
                NotesLayoutMode.LIST
            else
                NotesLayoutMode.GRID
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            recentlyDeletedNote = note
            repository.deleteNote(note.id)
        }
    }

    fun undoDelete() {
        recentlyDeletedNote?.let { note ->
            viewModelScope.launch {
                repository.saveNote(note)
                recentlyDeletedNote = null
            }
        }
    }
}
