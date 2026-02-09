package com.athread.lichen.ui.notes.list.composables.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.athread.lichen.domain.model.Note
import com.athread.lichen.ui.notes.list.composables.content.grid.NotesGrid
import com.athread.lichen.ui.notes.list.composables.content.list.EmptyState
import com.athread.lichen.ui.notes.list.composables.content.list.NotesList
import com.athread.lichen.ui.notes.list.state.NotesLayoutMode
import com.athread.lichen.ui.notes.list.state.NotesUiState
import com.athread.lichen.ui.notes.shared.composable.LoadingScreen

@Composable
fun NotesListContent(
    uiState: NotesUiState,
    layoutMode: NotesLayoutMode,
    onEditNote: (Note) -> Unit,
    onDeleteNote: (Note) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier) {
        when (uiState) {
            NotesUiState.Loading -> LoadingScreen()
            NotesUiState.Empty -> EmptyState(Modifier.fillMaxSize())

            is NotesUiState.Content -> {
                if (layoutMode == NotesLayoutMode.GRID) {
                    NotesGrid(
                        notes = uiState.notes,
                        onEditNote = onEditNote,
                        onDeleteNote = onDeleteNote
                    )
                } else {
                    NotesList(
                        notes = uiState.notes,
                        onEditNote = onEditNote,
                        onDeleteNote = onDeleteNote
                    )
                }
            }
        }
    }
}
