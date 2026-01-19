package com.example.notes.ui.noteslist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val notes by viewModel.notes.collectAsState()
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
                        ),
                        color = MaterialTheme.colorScheme.onSurface
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
                elevation = FloatingActionButtonDefaults.elevation(0.dp),
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = CircleShape
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "New note"
                )
            }
        }

    ) { padding ->

        /* ───────────── Snackbar undo logic ───────────── */

        LaunchedEffect(recentlyDeletedNote) {
            val note = recentlyDeletedNote ?: return@LaunchedEffect

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

        /* ───────────── Content ───────────── */

        when {
            notes.isEmpty() -> {
                EmptyState(Modifier.padding(padding))
            }

            layoutMode == NotesLayoutMode.GRID -> {
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

            else -> {
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

/* ───────────────────────── Empty ───────────────────────── */

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
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/* ───────────────────────── Grid ───────────────────────── */

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
            SwipeToDelete(
                onDelete = { onDeleteNote(note) }
            ) {
                GridNoteCard(note, onEditNote)
            }
        }
    }
}

/* ───────────────────────── List ───────────────────────── */

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
            SwipeToDelete(
                onDelete = { onDeleteNote(note) }
            ) {
                NoteItem(note, onEditNote)
            }
        }
    }
}

/* ───────────────────────── Swipe Wrapper ───────────────────────── */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDelete(
    onDelete: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState()

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onDelete()
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = { DeleteSwipeBackground() },
    ) {
        content()
    }
}

/* ───────────────────────── Cards ───────────────────────── */

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
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(
            modifier = Modifier.padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
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
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick(note) },
        shape = NoteShape,
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(8.dp))

            NoteListPreview(
                body = bodyText
            )
        }
    }
}

/* ───────────────────────── Swipe Background ───────────────────────── */

@Composable
private fun DeleteSwipeBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(NoteShape)
            .background(
                MaterialTheme.colorScheme.errorContainer
            )
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
