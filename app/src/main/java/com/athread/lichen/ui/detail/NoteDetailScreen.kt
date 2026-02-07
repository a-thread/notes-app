package com.athread.lichen.ui.detail

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.athread.lichen.ui.detail.composables.EditableNote
import com.athread.lichen.ui.detail.composables.ReadOnlyNote
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    viewModel: NoteDetailViewModel,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val mode by viewModel.mode.collectAsState()
    val title by viewModel.title.collectAsState()
    val body by viewModel.text.collectAsState()

    var showDiscardDialog by remember { mutableStateOf(false) }

    /* ───────────── Export launcher ───────────── */

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/plain")
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        scope.launch {
            val (_, content) = viewModel.exportNote()

            context.contentResolver
                .openOutputStream(uri)
                ?.bufferedWriter()
                ?.use { it.write(content) }
        }
    }

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
                            onDone()
                        }
                    }
                ) { Text("Discard") }
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
                                onClick = {
                                    scope.launch {
                                        val (fileName, _) = viewModel.exportNote()
                                        exportLauncher.launch(fileName)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = "Export note",
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }

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

        val scrollModifier =
            if (mode == EditorMode.ReadOnly)
                Modifier.verticalScroll(rememberScrollState())
            else
                Modifier

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 0.dp,
                    bottom = 12.dp
                ).fillMaxSize()
                .then(scrollModifier)
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
