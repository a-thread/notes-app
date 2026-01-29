package com.athread.lichen.ui.list

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.athread.lichen.domain.model.NotesSort

@Composable
fun BottomSheetContent(
    darkModeOverride: Boolean?,
    systemIsDark: Boolean,
    currentSort: NotesSort,
    onSortSelected: (NotesSort) -> Unit,
    onToggleDarkMode: (Boolean) -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {

        BottomSheetItem(
            text = if (darkModeOverride == true) "Light mode" else "Dark mode",
            icon = Icons.Default.DarkMode
        ) {
            val isCurrentlyDark = darkModeOverride ?: systemIsDark
            onToggleDarkMode(!isCurrentlyDark)
        }

        BottomSheetItem(
            text = "Support development",
            icon = Icons.Default.LocalCafe
        ) {
            val intent = Intent(
                Intent.ACTION_VIEW,
                "https://buymeacoffee.com/athread".toUri()
            )
            context.startActivity(intent)
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )

        Text(
            text = "Sort by",
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        SortItem(
            text = "Newest first",
            selected = currentSort == NotesSort.DATE_NEWEST
        ) {
            onSortSelected(NotesSort.DATE_NEWEST)
        }

        SortItem(
            text = "Oldest first",
            selected = currentSort == NotesSort.DATE_OLDEST
        ) {
            onSortSelected(NotesSort.DATE_OLDEST)
        }

        SortItem(
            text = "Title A–Z",
            selected = currentSort == NotesSort.TITLE_ASC
        ) {
            onSortSelected(NotesSort.TITLE_ASC)
        }

        SortItem(
            text = "Title Z–A",
            selected = currentSort == NotesSort.TITLE_DESC
        ) {
            onSortSelected(NotesSort.TITLE_DESC)
        }


        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )

        BottomSheetItem(
            text = "Logout",
            icon = Icons.AutoMirrored.Filled.Logout
        ) {
            onLogout()
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun BottomSheetItem(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    )
}

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
                Text(
                    text = "✓",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    )
}

