package com.example.lichen.ui.detail

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
import com.example.lichen.ui.detail.composables.EditableNote
import com.example.lichen.ui.detail.composables.ReadOnlyNote

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
            title = {
                Text(
                    "Discard changes?",
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    "Your changes will be lost.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDiscardDialog = false
                        if (viewModel.discardChanges()) {
                            onDone()
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
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    /* ───────────── Scaffold ───────────── */

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {},

                // 🔹 Explicit colors for accessibility + consistency
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                ),

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
                            contentDescription =
                                if (mode == EditorMode.Edit)
                                    "Discard changes"
                                else
                                    "Back"
                        )
                    }
                },

                actions = {
                    when (mode) {
                        EditorMode.ReadOnly -> {
                            IconButton(
                                onClick = viewModel::enterEditMode,
                                colors = IconButtonDefaults.iconButtonColors(
                                    contentColor = MaterialTheme.colorScheme.tertiary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit note"
                                )
                            }
                        }

                        EditorMode.Edit -> {
                            IconButton(
                                onClick = { viewModel.saveAndExit(onDone) },
                                colors = IconButtonDefaults.iconButtonColors(
                                    contentColor = MaterialTheme.colorScheme.tertiary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Save note"
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
                .padding(horizontal = 16.dp, vertical = 12.dp)
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
