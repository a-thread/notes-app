package com.example.notes

import androidx.compose.runtime.Composable
import com.example.notes.domain.model.Note
import com.example.notes.domain.repository.NoteRepository
import com.example.notes.ui.editor.NoteEditorScreen
import com.example.notes.ui.editor.NoteEditorViewModel
import com.example.notes.ui.noteslist.NotesListScreen
import com.example.notes.ui.noteslist.NotesListViewModel

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
        val editorViewModel = NoteEditorViewModel(
            repository = repository,
            existingNote = editingNote
        )

        NoteEditorScreen(
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
