package com.athread.lichen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.athread.lichen.domain.model.Note
import com.athread.lichen.domain.repository.NoteRepository
import com.athread.lichen.ui.detail.NoteDetailScreen
import com.athread.lichen.ui.detail.NoteDetailViewModel
import com.athread.lichen.ui.list.NotesListScreen
import com.athread.lichen.ui.list.NotesListViewModel
import com.athread.lichen.ui.shared.theme.ThemeViewModel
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
