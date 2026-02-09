package com.athread.lichen.ui.notes.detail.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.athread.lichen.domain.model.Note
import com.athread.lichen.domain.repository.NoteRepository
import java.util.UUID

class NoteDetailViewModelFactory(
    private val repository: NoteRepository,
    private val userId: UUID,
    private val existingNote: Note?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return NoteDetailViewModel(
            repository = repository,
            userId = userId,
            existingNote = existingNote
        ) as T
    }
}
