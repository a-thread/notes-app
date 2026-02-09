package com.athread.lichen.ui.notes.list.state

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.athread.lichen.domain.model.Note

@Composable
fun NotesListEffects(
    viewModel: NotesListViewModel,
    snackbarHostState: SnackbarHostState,
    recentlyDeletedNote: Note?,
    onUndoHandled: () -> Unit
) {
    LaunchedEffect(recentlyDeletedNote) {
        val note = recentlyDeletedNote ?: return@LaunchedEffect

        val result = snackbarHostState.showSnackbar(
            message = "Note deleted",
            actionLabel = "Undo",
            duration = SnackbarDuration.Indefinite
        )

        if (result == SnackbarResult.ActionPerformed) {
            viewModel.undoDelete()
        }

        onUndoHandled()
    }
}
