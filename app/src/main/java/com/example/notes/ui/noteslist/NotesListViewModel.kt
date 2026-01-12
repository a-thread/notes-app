package com.example.notes.ui.noteslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.domain.model.Note
import com.example.notes.domain.model.NoteBody
import com.example.notes.domain.model.NoteType
import com.example.notes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID

class NotesListViewModel(
    private val repository: NoteRepository
) : ViewModel() {

    val notes: StateFlow<List<Note>> =
        repository.observeNotes()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )
    private var recentlyDeletedNote: Note? = null

    init {
        insertTestNoteIfEmpty()
    }

    private fun insertTestNoteIfEmpty() {
        viewModelScope.launch {
            val existingNotes = repository.observeNotes().first()
            if (existingNotes.isNotEmpty()) return@launch

            val now = Instant.now()
            val userId = UUID.randomUUID() // temporary, replaced later by auth

            val testNote = Note(
                id = UUID.randomUUID(),
                userId = userId,
                title = "Welcome to Notes 👋",
                type = NoteType.TEXT,
                body = NoteBody.Text(
                    text = "This is your first note.\n\nYou're ready to start building!"
                ),
                createdAt = now,
                createdBy = userId,
                updatedAt = now,
                updatedBy = userId,
                isPublic = false
            )

            repository.saveNote(testNote)
        }
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
