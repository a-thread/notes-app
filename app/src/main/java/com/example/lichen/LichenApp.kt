package com.example.lichen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.lichen.domain.model.Note
import com.example.lichen.domain.repository.NoteRepository
import com.example.lichen.ui.detail.NoteDetailScreen
import com.example.lichen.ui.detail.NoteDetailViewModel
import com.example.lichen.ui.list.NotesListScreen
import com.example.lichen.ui.list.NotesListViewModel
import com.example.lichen.ui.shared.theme.ThemeViewModel
import java.util.UUID

@Composable
fun LichenApp(
    userId: UUID,
    isEditing: Boolean,
    listViewModel: NotesListViewModel,
    themeViewModel: ThemeViewModel,
    darkModeOverride: Boolean?,
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
            themeViewModel = themeViewModel,
            darkModeOverride = darkModeOverride,
            onCreateNote = onCreateNote,
            onEditNote = onEditNote,
            onLogout = onLogout
        )
    }
}
