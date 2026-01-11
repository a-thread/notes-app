package com.example.notes.ui.editor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.saveNote()
                            onDone()
                        }
                    ) {
                        Text("Save")
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

            OutlinedTextField(
                value = title,
                onValueChange = viewModel::onTitleChange,
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

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

            TypeSwitcher(
                currentType = type,
                onTypeChange = {
                    if (it == NoteType.TEXT) {
                        viewModel.onTextChange("")
                    } else {
                        viewModel.onBulletedListChange(emptyList())
                    }
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
            .height(200.dp),
        maxLines = Int.MAX_VALUE
    )
}

@Composable
private fun BulletedListEditor(
    items: List<String>,
    onItemsChange: (List<String>) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        itemsIndexed(items) { index, item ->
            OutlinedTextField(
                value = item,
                onValueChange = {
                    val updated = items.toMutableList()
                    updated[index] = it
                    onItemsChange(updated)
                },
                label = { Text("Item ${index + 1}") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            TextButton(onClick = {
                onItemsChange(items + "")
            }) {
                Text("+ Add item")
            }
        }
    }
}

@Composable
private fun TypeSwitcher(
    currentType: NoteType,
    onTypeChange: (NoteType) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = currentType == NoteType.TEXT,
            onClick = { onTypeChange(NoteType.TEXT) },
            label = { Text("Text") }
        )

        FilterChip(
            selected = currentType == NoteType.BULLETED_LIST,
            onClick = { onTypeChange(NoteType.BULLETED_LIST) },
            label = { Text("List") }
        )
    }
}
