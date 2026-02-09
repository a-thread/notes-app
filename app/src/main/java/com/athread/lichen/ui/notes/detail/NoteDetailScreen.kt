package com.athread.lichen.ui.notes.detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.athread.lichen.ui.notes.detail.composables.DiscardChangesDialog
import com.athread.lichen.ui.notes.detail.composables.NoteDetailContent
import com.athread.lichen.ui.notes.detail.composables.NoteDetailTopBar
import com.athread.lichen.ui.notes.detail.composables.helpers.rememberNoteExportHandler
import com.athread.lichen.ui.notes.detail.state.EditorMode
import com.athread.lichen.ui.notes.detail.state.NoteDetailViewModel
import kotlinx.coroutines.runBlocking

@Composable
fun NoteDetailScreen(
    viewModel: NoteDetailViewModel,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mode by viewModel.mode.collectAsState()
    val title by viewModel.title.collectAsState()
    val body by viewModel.text.collectAsState()

    var showDiscardDialog by remember { mutableStateOf(false) }

    val exportNote = rememberNoteExportHandler(viewModel)

    if (showDiscardDialog) {
        DiscardChangesDialog(
            onConfirm = {
                showDiscardDialog = false
                if (viewModel.discardChanges()) onDone()
            },
            onDismiss = { showDiscardDialog = false }
        )
    }


    BackHandler {
        when {
            mode == EditorMode.ReadOnly -> onDone()
            viewModel.hasUnsavedChanges -> showDiscardDialog = true
            else -> onDone()
        }
    }


    Scaffold(
        modifier = modifier,
        topBar = {
            NoteDetailTopBar(
                mode = mode,
                onBack = onDone,
                onDiscard = {
                    if (viewModel.hasUnsavedChanges) {
                        showDiscardDialog = true
                    } else {
                        viewModel.exitEditMode()
                    }
                },
                onEdit = viewModel::enterEditMode,
                onSave = viewModel::save,
                onExport = {
                    val (fileName, _) = runBlocking { viewModel.exportNote() }
                    exportNote(fileName)
                }
            )
        }
    ) { padding ->
        NoteDetailContent(
            mode = mode,
            title = title,
            body = body,
            isNewNote = viewModel.isNewNote,
            onTitleChange = viewModel::onTitleChange,
            onBodyChange = viewModel::onTextChange,
            onSave = viewModel::save,
            onEdit = viewModel::enterEditMode,
            onToggleChecklist = viewModel::toggleChecklistItem,
            modifier = Modifier
                .padding(padding)
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 12.dp
                )
                .fillMaxSize()
        )
    }
}
