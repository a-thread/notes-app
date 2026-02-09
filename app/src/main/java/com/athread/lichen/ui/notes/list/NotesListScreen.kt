package com.athread.lichen.ui.notes.list

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.athread.lichen.domain.model.Note
import com.athread.lichen.domain.repository.NoteRepository
import com.athread.lichen.ui.notes.list.composables.NotesListTopBar
import com.athread.lichen.ui.notes.list.composables.bottomsheet.NotesListBottomSheets
import com.athread.lichen.ui.notes.list.composables.content.NotesListContent
import com.athread.lichen.ui.notes.list.composables.helpers.rememberImportExportHandlers
import com.athread.lichen.ui.notes.list.state.NotesListEffects
import com.athread.lichen.ui.notes.list.state.NotesListViewModel
import com.athread.lichen.ui.notes.list.state.NotesListViewModelFactory
import com.athread.lichen.ui.notes.shared.theme.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesListScreen(
    noteRepository: NoteRepository,
    themeViewModel: ThemeViewModel,
    onCreateNote: () -> Unit,
    onEditNote: (Note) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: NotesListViewModel = viewModel(
        factory = NotesListViewModelFactory(noteRepository)
    )
    val uiState by viewModel.uiState.collectAsState()
    val layoutMode by viewModel.layoutMode.collectAsState()
    val currentSort by viewModel.sort.collectAsState()

    var showMainSheet by remember { mutableStateOf(false) }
    var showSortSheet by remember { mutableStateOf(false) }
    var recentlyDeletedNote by remember { mutableStateOf<Note?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val (launchImport, launchExport) =
        rememberImportExportHandlers(viewModel, snackbarHostState)

    Scaffold(
        modifier = modifier,
        topBar = {
            NotesListTopBar(
                layoutMode = layoutMode,
                onToggleLayout = viewModel::toggleLayout,
                onMenu = { showMainSheet = true }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateNote) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { padding ->

        NotesListEffects(
            viewModel = viewModel,
            snackbarHostState = snackbarHostState,
            recentlyDeletedNote = recentlyDeletedNote,
            onUndoHandled = { recentlyDeletedNote = null }
        )

        NotesListContent(
            uiState = uiState,
            layoutMode = layoutMode,
            onEditNote = onEditNote,
            onDeleteNote = {
                viewModel.deleteNote(it)
                recentlyDeletedNote = it
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        )
    }

    NotesListBottomSheets(
        showMainSheet = showMainSheet,
        showSortSheet = showSortSheet,
        themeViewModel = themeViewModel,
        currentSort = currentSort,
        onDismissMain = { showMainSheet = false },
        onDismissSort = { showSortSheet = false },
        onImport = { launchImport(arrayOf("text/plain")) },
        onExport = { launchExport("lichen-notes.txt") },
        onLogout = onLogout,
        onSortSelected = viewModel::setSort
    )
}

