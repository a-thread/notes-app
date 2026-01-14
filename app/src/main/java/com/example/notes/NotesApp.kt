package com.example.notes

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import com.example.notes.domain.model.Note
import com.example.notes.domain.repository.NoteRepository
import com.example.notes.ui.notedetail.NoteDetailScreen
import com.example.notes.ui.notedetail.NoteDetailViewModel
import com.example.notes.ui.noteslist.NotesListScreen
import com.example.notes.ui.noteslist.NotesListViewModel

@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun NotesApp(
    isEditing: Boolean,
    listViewModel: NotesListViewModel,
    editingNote: Note?,
    onCreateNote: () -> Unit,
    onEditNote: (Note) -> Unit,
    onCloseEditor: () -> Unit,
    repository: NoteRepository
) {
    if (isEditing) {
        val editorViewModel = NoteDetailViewModel(
            repository = repository,
            existingNote = editingNote
        )

        NoteDetailScreen(
            viewModel = editorViewModel,
            onDone = onCloseEditor
        )
    } else {
        NotesListScreen(
            viewModel = listViewModel,
            onCreateNote = onCreateNote,
            onEditNote = onEditNote
        )
    }
}
