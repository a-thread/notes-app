package com.athread.lichen.ui.notes.detail.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import com.athread.lichen.ui.notes.detail.state.EditorMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailTopBar(
    mode: EditorMode,
    onBack: () -> Unit,
    onDiscard: () -> Unit,
    onEdit: () -> Unit,
    onSave: () -> Unit,
    onExport: () -> Unit
) {
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(
                onClick = {
                    if (mode == EditorMode.Edit) onDiscard()
                    else onBack()
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
                    IconButton(onClick = onExport) {
                        Icon(Icons.Default.Download, contentDescription = null)
                    }
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                    }
                }
                EditorMode.Edit -> {
                    IconButton(onClick = onSave) {
                        Icon(Icons.Default.Check, contentDescription = null)
                    }
                }
            }
        }
    )
}
