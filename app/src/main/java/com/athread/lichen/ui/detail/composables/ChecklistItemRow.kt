package com.athread.lichen.ui.detail.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.athread.lichen.ui.detail.model.ChecklistItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChecklistItemRow(
    item: ChecklistItem,
    onToggle: () -> Unit,
    onEdit: () -> Unit
) {
    val textColor =
        if (item.checked)
            MaterialTheme.colorScheme.onSurfaceVariant
        else
            MaterialTheme.colorScheme.onBackground

    val decoration =
        if (item.checked)
            TextDecoration.LineThrough
        else
            TextDecoration.None

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .combinedClickable(
                onClick = onToggle,
                onLongClick = onEdit
            )
            .padding(vertical = 6.dp),
    ) {
        Icon(
            imageVector =
                if (item.checked)
                    Icons.Default.CheckBox
                else
                    Icons.Default.CheckBoxOutlineBlank,
            contentDescription = null,
            tint =
                if (item.checked)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(end = 12.dp)
        )


        Text(
            text = item.text,
            color = textColor,
            textDecoration = decoration
        )
    }
}
