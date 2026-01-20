package com.example.lichen.ui.detail.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.lichen.ui.shared.model.EditorBlock
import com.example.lichen.ui.detail.logic.parseEditorBlocks

@Composable
fun ReadOnlyNote(
    title: String,
    body: String,
    onEdit: () -> Unit,
    onToggleChecklist: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (title.isNotBlank()) {
            Text(
                title,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.height(12.dp))
        }

        val blocks = remember(body) {
            TextFieldValue(body).parseEditorBlocks()
        }

        blocks.forEach { block ->
            when (block) {
                is EditorBlock.TextBlock -> {
                    if (block.text.isNotBlank()) {
                        Text(
                            block.text,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }

                is EditorBlock.ChecklistBlock -> {
                    block.items.forEach { item ->
                        ChecklistItemRow(
                            item = item,
                            onToggle = { onToggleChecklist(item.lineIndex) },
                            onEdit = onEdit
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}
