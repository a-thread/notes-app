package com.example.notes.ui.editor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.notes.domain.model.NoteBody
import com.example.notes.domain.model.NoteType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    viewModel: NoteEditorViewModel,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val title by viewModel.title.collectAsState()
    val body by viewModel.body.collectAsState()
    val type by viewModel.type.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Edit note") },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ---------- Title ----------
            OutlinedTextField(
                value = title,
                onValueChange = viewModel::onTitleChange,
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // ---------- Body ----------
            when (type) {
                NoteType.TEXT -> TextEditor(
                    text = (body as NoteBody.Text).text,
                    onTextChange = viewModel::onTextChange
                )

                NoteType.BULLETED_LIST -> BulletedListEditor(
                    items = (body as NoteBody.BulletedList).items,
                    onItemsChange = viewModel::onBulletedListChange
                )
            }

            // ---------- Type switcher ----------
            TypeSwitcher(
                currentType = type,
                onSwitchToText = {
                    viewModel.onTextChange("")
                },
                onSwitchToList = {
                    viewModel.onBulletedListChange(emptyList())
                }
            )
        }
    }
}

@Composable
private fun TextEditor(
    text: String,
    onTextChange: (String) -> Unit
) {
    OutlinedTextField(
        value = text,
        onValueChange = onTextChange,
        label = { Text("Note") },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp),
        maxLines = Int.MAX_VALUE
    )
}

@Composable
private fun BulletedListEditor(
    items: List<String>,
    onItemsChange: (List<String>) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(items) { index, item ->
            OutlinedTextField(
                value = item,
                onValueChange = { newValue ->
                    val updated = items.toMutableList()
                    updated[index] = newValue
                    onItemsChange(updated)
                },
                label = { Text("Item ${index + 1}") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            TextButton(
                onClick = {
                    onItemsChange(items + "")
                }
            ) {
                Text("+ Add item")
            }
        }
    }
}

@Composable
private fun TypeSwitcher(
    currentType: NoteType,
    onSwitchToText: () -> Unit,
    onSwitchToList: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = currentType == NoteType.TEXT,
            onClick = onSwitchToText,
            label = { Text("Text") }
        )

        FilterChip(
            selected = currentType == NoteType.BULLETED_LIST,
            onClick = onSwitchToList,
            label = { Text("List") }
        )
    }
}
