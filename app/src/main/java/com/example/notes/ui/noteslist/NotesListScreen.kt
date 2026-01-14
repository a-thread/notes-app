package com.example.notes.ui.noteslist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.notes.domain.model.Note
import com.example.notes.domain.model.NoteBody
import com.example.notes.ui.noteslist.composables.NoteListPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val NoteShape = RoundedCornerShape(12.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesListScreen(
    viewModel: NotesListViewModel,
    onCreateNote: () -> Unit,
    onEditNote: (Note) -> Unit,
    modifier: Modifier = Modifier
) {
    val notes by viewModel.notes.collectAsState()
    val layoutMode by viewModel.layoutMode.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var recentlyDeletedNote by remember { mutableStateOf<Note?>(null) }

    val toggleIcon =
        if (layoutMode == NotesLayoutMode.GRID)
            Icons.AutoMirrored.Filled.ViewList
        else
            Icons.Filled.ViewModule

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("BlackNote") },
                actions = {
                    IconButton(onClick = viewModel::toggleLayout) {
                        Icon(toggleIcon, contentDescription = "Toggle layout")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateNote) {
                Icon(Icons.Default.Add, contentDescription = "Add note")
            }
        }
    ) { padding ->

        // Snackbar undo logic
        LaunchedEffect(recentlyDeletedNote) {
            if (recentlyDeletedNote != null)  return@LaunchedEffect

            val job = launch {
                val result = snackbarHostState.showSnackbar(
                    message = "Note deleted",
                    actionLabel = "Undo",
                    withDismissAction = true,
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

        if (notes.isEmpty()) {
            EmptyState(Modifier.padding(padding))
        } else {
            when (layoutMode) {
                NotesLayoutMode.GRID -> {
                    NotesGrid(
                        notes = notes,
                        onEditNote = onEditNote,
                        onDeleteNote = {
                            viewModel.deleteNote(it)
                            recentlyDeletedNote = it
                        },
                        modifier = Modifier.padding(padding)
                    )
                }

                NotesLayoutMode.LIST -> {
                    NotesList(
                        notes = notes,
                        onEditNote = onEditNote,
                        onDeleteNote = {
                            viewModel.deleteNote(it)
                            recentlyDeletedNote = it
                        },
                        modifier = Modifier.padding(padding)
                    )
                }
            }
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
private fun NotesGrid(
    notes: List<Note>,
    onEditNote: (Note) -> Unit,
    onDeleteNote: (Note) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(notes, key = { it.id }) { note ->
            SwipeToDismissBox(
                state = rememberSwipeToDismissBoxState(
                    confirmValueChange = {
                        if (it == SwipeToDismissBoxValue.EndToStart) {
                            onDeleteNote(note)
                            true
                        } else false
                    }
                ),
                backgroundContent = { DeleteSwipeBackground() },
                content = {
                    GridNoteCard(note, onEditNote)
                }
            )
        }
    }
}

@Composable
private fun GridNoteCard(
    note: Note,
    onClick: (Note) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(note) },
        shape = NoteShape,
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(
            modifier = Modifier.padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium
            )
        }
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
        items(notes, key = { it.id }) { note ->
            SwipeToDismissBox(
                state = rememberSwipeToDismissBoxState(
                    confirmValueChange = {
                        if (it == SwipeToDismissBoxValue.EndToStart) {
                            onDeleteNote(note)
                            true
                        } else false
                    }
                ),
                backgroundContent = { DeleteSwipeBackground() },
                content = {
                    NoteItem(note, onEditNote)
                }
            )
        }
    }
}

@Composable
private fun DeleteSwipeBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(NoteShape)
            .background(MaterialTheme.colorScheme.error)
            .padding(end = 24.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            Icons.Default.Delete,
            contentDescription = "Delete",
            tint = MaterialTheme.colorScheme.onError
        )
    }
}

@Composable
private fun NoteItem(
    note: Note,
    onClick: (Note) -> Unit
) {
    val bodyText = (note.body as? NoteBody.Text)?.text.orEmpty()

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

            Spacer(Modifier.height(8.dp))

            // ✅ Rich semantic preview (checkboxes, strikethrough, etc.)
            NoteListPreview(
                body = bodyText
            )
        }
    }
}
