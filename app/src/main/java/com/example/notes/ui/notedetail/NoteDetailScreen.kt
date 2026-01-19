package com.example.notes.ui.notedetail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.notes.ui.notedetail.composables.EditableNote
import com.example.notes.ui.notedetail.composables.ReadOnlyNote

@OptIn(ExperimentalMaterial3Api::class)
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

    /* ───────────── Discard dialog ───────────── */

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Discard changes?") },
            text = { Text("Your changes will be lost.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDiscardDialog = false
                        if (viewModel.discardChanges()) {
                            onDone() // navigate back to list
                        }
                    }
                ) {
                    Text("Discard")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    /* ───────────── Scaffold ───────────── */

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = {
                            when (mode) {
                                EditorMode.ReadOnly -> onDone()
                                EditorMode.Edit -> showDiscardDialog = true
                            }
                        }
                    ) {
                        Icon(
                            imageVector =
                                if (mode == EditorMode.Edit)
                                    Icons.Default.Close
                                else
                                    Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    when (mode) {
                        EditorMode.ReadOnly -> {
                            IconButton(onClick = viewModel::enterEditMode) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit"
                                )
                            }
                        }

                        EditorMode.Edit -> {
                            IconButton(
                                onClick = { viewModel.saveAndExit(onDone) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Save"
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxSize()
        ) {
            when (mode) {
                EditorMode.ReadOnly -> {
                    ReadOnlyNote(
                        title = title,
                        body = body.text,
                        onEdit = viewModel::enterEditMode,
                        onToggleChecklist = viewModel::toggleChecklistItem
                    )
                }

                EditorMode.Edit -> {
                    EditableNote(
                        title = title,
                        body = body,
                        onTitleChange = viewModel::onTitleChange,
                        onBodyChange = viewModel::onTextChange,
                        onSave = { viewModel.saveAndExit(onDone) },
                        isNewNote = viewModel.isNewNote
                    )
                }
            }
        }
    }
}
