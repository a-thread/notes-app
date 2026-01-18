package com.example.notes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.notes.domain.model.Note
import com.example.notes.domain.repository.NoteRepository
import com.example.notes.ui.notedetail.NoteDetailScreen
import com.example.notes.ui.notedetail.NoteDetailViewModel
import com.example.notes.ui.noteslist.NotesListScreen
import com.example.notes.ui.noteslist.NotesListViewModel
import java.util.UUID

@Composable
fun NotesApp(
    userId: UUID,
    isEditing: Boolean,
    listViewModel: NotesListViewModel,
    editingNote: Note?,
    onCreateNote: () -> Unit,
    onEditNote: (Note) -> Unit,
    onCloseEditor: () -> Unit,
    onLogout: () -> Unit,
    repository: NoteRepository
) {
    if (isEditing) {
        val editorViewModel = remember(editingNote) {
            NoteDetailViewModel(
                repository = repository,
                userId,
                existingNote = editingNote
            )
        }

        NoteDetailScreen(
            viewModel = editorViewModel,
            onDone = onCloseEditor
        )
    } else {
        NotesListScreen(
            viewModel = listViewModel,
            onCreateNote = onCreateNote,
            onEditNote = onEditNote,
            onLogout = onLogout
        )
    }
}
