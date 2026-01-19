package com.example.notes.ui.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notes.domain.model.Note
import com.example.notes.ui.list.notesgrid.NotesGrid
import com.example.notes.ui.list.noteslist.NotesList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesListScreen(
    viewModel: NotesListViewModel,
    onCreateNote: () -> Unit,
    onEditNote: (Note) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val layoutMode by viewModel.layoutMode.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var recentlyDeletedNote by remember { mutableStateOf<Note?>(null) }
    var menuExpanded by remember { mutableStateOf(false) }

    val toggleIcon =
        if (layoutMode == NotesLayoutMode.GRID)
            Icons.AutoMirrored.Filled.ViewList
        else
            Icons.Filled.ViewModule

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "lichen",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.4.sp
                        )
                    )
                },
                actions = {
                    IconButton(onClick = viewModel::toggleLayout) {
                        Icon(toggleIcon, contentDescription = "Toggle layout")
                    }

                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = {
                                menuExpanded = false
                                onLogout()
                            }
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateNote,
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Icon(Icons.Default.Add, contentDescription = "New note")
            }
        }
    ) { padding ->

        /* ───────────── Snackbar undo ───────────── */

        LaunchedEffect(recentlyDeletedNote) {
            val note = recentlyDeletedNote ?: return@LaunchedEffect

            val job = launch {
                val result = snackbarHostState.showSnackbar(
                    message = "Note deleted",
                    actionLabel = "Undo",
                    duration = SnackbarDuration.Indefinite
                )
                if (result == SnackbarResult.ActionPerformed) {
                    viewModel.undoDelete()
                }
            }

            launch {
                delay(10_000)
                snackbarHostState.currentSnackbarData?.dismiss()
            }

            job.join()
            recentlyDeletedNote = null
        }

        /* ───────────── Content ───────────── */

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (uiState) {
                NotesUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                NotesUiState.Empty -> {
                    EmptyState(Modifier.fillMaxSize())
                }

                is NotesUiState.Content -> {
                    val notes = (uiState as NotesUiState.Content).notes

                    if (layoutMode == NotesLayoutMode.GRID) {
                        NotesGrid(
                            notes = notes,
                            onEditNote = onEditNote,
                            onDeleteNote = {
                                viewModel.deleteNote(it)
                                recentlyDeletedNote = it
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        NotesList(
                            notes = notes,
                            onEditNote = onEditNote,
                            onDeleteNote = {
                                viewModel.deleteNote(it)
                                recentlyDeletedNote = it
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

/* ───────────────────────── Empty ───────────────────────── */

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No notes yet",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
