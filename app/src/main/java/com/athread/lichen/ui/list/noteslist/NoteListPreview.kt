package com.athread.lichen.ui.list.noteslist

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.athread.lichen.ui.detail.logic.parseEditorBlocks
import com.athread.lichen.ui.shared.model.EditorBlock

@Composable
fun NoteListPreview(
    body: String,
    maxLines: Int = 3
) {
    val blocks = remember(body) {
        TextFieldValue(body).parseEditorBlocks()
    }

    var renderedLines = 0

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        blocks.forEach { block ->
            if (renderedLines >= maxLines) return@forEach

            when (block) {
                is EditorBlock.TextBlock -> {
                    if (block.text.isNotBlank()) {
                        Text(
                            text = block.text,
                            maxLines = maxLines - renderedLines,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        renderedLines++
                    }
                }

                is EditorBlock.ChecklistBlock -> {
                    block.items.forEach { item ->
                        if (renderedLines >= maxLines) return@forEach

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector =
                                    if (item.checked)
                                        Icons.Filled.CheckBox
                                    else
                                        Icons.Filled.CheckBoxOutlineBlank,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )

                            Text(
                                text = item.text,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodyMedium,
                                textDecoration =
                                    if (item.checked)
                                        TextDecoration.LineThrough
                                    else
                                        TextDecoration.None
                            )
                        }

                        renderedLines++
                    }
                }
            }
        }
    }
}
