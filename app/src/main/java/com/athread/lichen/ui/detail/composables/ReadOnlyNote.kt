package com.athread.lichen.ui.detail.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.athread.lichen.ui.detail.logic.parseEditorBlocks
import com.athread.lichen.ui.shared.model.EditorBlock

@Composable
fun ReadOnlyNote(
    title: String,
    body: String,
    onEdit: () -> Unit,
    onToggleChecklist: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {

        /* ---------- TITLE ---------- */

        if (title.isNotBlank()) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(12.dp))
        }

        /* ---------- BODY ---------- */

        val blocks = remember(body) {
            TextFieldValue(body).parseEditorBlocks()
        }

        blocks.forEach { block ->
            when (block) {
                is EditorBlock.TextBlock -> {
                    if (block.text.isNotBlank()) {
                        Text(
                            text = block.text,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
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
