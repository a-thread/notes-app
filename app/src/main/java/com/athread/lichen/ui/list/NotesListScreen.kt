package com.athread.lichen.ui.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.athread.lichen.domain.model.Note
import com.athread.lichen.ui.list.notesgrid.NotesGrid
import com.athread.lichen.ui.list.noteslist.NotesList
import com.athread.lichen.ui.shared.composable.LoadingScreen
import com.athread.lichen.ui.shared.theme.ThemeViewModel
import com.athread.lichen.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesListScreen(
    viewModel: NotesListViewModel,
    themeViewModel: ThemeViewModel,
    darkModeOverride: Boolean?,
    onCreateNote: () -> Unit,
    onEditNote: (Note) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val layoutMode by viewModel.layoutMode.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var recentlyDeletedNote by remember { mutableStateOf<Note?>(null) }
    val systemIsDark = isSystemInDarkTheme()
    val currentSort by viewModel.sort.collectAsState()
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

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

                    IconButton(onClick = { showBottomSheet = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateNote,
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary
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
                    LoadingScreen()
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

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = bottomSheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp
        ) {
            BottomSheetContent(
                darkModeOverride = darkModeOverride,
                systemIsDark = darkModeOverride ?: systemIsDark,
                currentSort = currentSort,
                onSortSelected = {
                    viewModel.setSort(it)
                    showBottomSheet = false
                },
                onToggleDarkMode = { enabled ->
                    if (enabled) themeViewModel.setDark() else themeViewModel.setLight()
                    showBottomSheet = false
                },
                onLogout = {
                    showBottomSheet = false
                    onLogout()
                }
            )
        }
    }

}

/* ───────────────────────── Empty ───────────────────────── */

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.ic_lichen_mark),
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .padding(bottom = 16.dp),
            colorFilter = ColorFilter.tint(
                MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        Text(
            text = "No notes yet",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

