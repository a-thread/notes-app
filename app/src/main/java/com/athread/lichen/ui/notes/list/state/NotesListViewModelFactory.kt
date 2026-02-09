package com.athread.lichen.ui.notes.list.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.athread.lichen.domain.repository.NoteRepository

class NotesListViewModelFactory(
    private val repository: NoteRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotesListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotesListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}