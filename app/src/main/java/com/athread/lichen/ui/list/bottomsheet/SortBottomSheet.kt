package com.athread.lichen.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.athread.lichen.domain.model.NotesSort

@Composable
fun SortBottomSheet(
    currentSort: NotesSort,
    onSortSelected: (NotesSort) -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {

        Text(
            text = "Sort notes",
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        SortItem(
            text = "Newest first",
            selected = currentSort == NotesSort.DATE_NEWEST
        ) {
            onSortSelected(NotesSort.DATE_NEWEST)
            onDismiss()
        }

        SortItem(
            text = "Oldest first",
            selected = currentSort == NotesSort.DATE_OLDEST
        ) {
            onSortSelected(NotesSort.DATE_OLDEST)
            onDismiss()
        }

        SortItem(
            text = "Title A–Z",
            selected = currentSort == NotesSort.TITLE_ASC
        ) {
            onSortSelected(NotesSort.TITLE_ASC)
            onDismiss()
        }

        SortItem(
            text = "Title Z–A",
            selected = currentSort == NotesSort.TITLE_DESC
        ) {
            onSortSelected(NotesSort.TITLE_DESC)
            onDismiss()
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/* ───────────────────────── Reused helper ───────────────────────── */

@Composable
private fun SortItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        trailingContent = {
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Save note",
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    )
}
