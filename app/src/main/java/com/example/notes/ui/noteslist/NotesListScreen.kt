package com.example.notes.ui.noteslist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.notes.domain.model.Note

@Composable
fun NotesListScreen(
    viewModel: NotesListViewModel,
    onCreateNote: () -> Unit,
    onEditNote: (Note) -> Unit,
    modifier: Modifier = Modifier
) {
    val notes by viewModel.notes.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // State used to trigger snackbar side-effect
    var recentlyDeletedNote by remember { mutableStateOf<Note?>(null) }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateNote) {
                Icon(Icons.Default.Add, contentDescription = "Add note")
            }
        }
    ) { padding ->

        // Side-effect: show undo snackbar when a note is deleted
        LaunchedEffect(recentlyDeletedNote) {
            val note = recentlyDeletedNote ?: return@LaunchedEffect

            val result = snackbarHostState.showSnackbar(
                message = "Note deleted",
                actionLabel = "Undo"
            )

            if (result == SnackbarResult.ActionPerformed) {
                viewModel.undoDelete()
            }

            recentlyDeletedNote = null
        }

        if (notes.isEmpty()) {
            EmptyState(modifier = Modifier.padding(padding))
        } else {
            NotesList(
                notes = notes,
                onEditNote = onEditNote,
                onDeleteNote = { note ->
                    viewModel.deleteNote(note)
                    recentlyDeletedNote = note
                },
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No notes yet",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotesList(
    notes: List<Note>,
    onEditNote: (Note) -> Unit,
    onDeleteNote: (Note) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = notes,
            key = { it.id }
        ) { note ->

            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = { value ->
                    if (value == SwipeToDismissBoxValue.EndToStart) {
                        onDeleteNote(note)
                        true
                    } else {
                        false
                    }
                }
            )

            SwipeToDismissBox(
                state = dismissState,
                backgroundContent = {},
                content = {
                    NoteItem(
                        note = note,
                        onClick = onEditNote
                    )
                }
            )
        }
    }
}

@Composable
private fun NoteItem(
    note: Note,
    onClick: (Note) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(note) },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
