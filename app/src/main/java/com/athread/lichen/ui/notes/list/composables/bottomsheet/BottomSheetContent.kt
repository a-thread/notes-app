package com.athread.lichen.ui.notes.list.composables.bottomsheet

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.athread.lichen.domain.model.NotesSort

@Composable
fun BottomSheetContent(
    isDarkMode: Boolean,
    currentSort: NotesSort,
    onToggleDarkMode: () -> Unit,
    onImport: () -> Unit,
    onExport: () -> Unit,
    onOpenSort: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {

        /* ───────────── Appearance ───────────── */

        BottomSheetItem(
            text = if (isDarkMode) "Light mode" else "Dark mode",
            icon = Icons.Default.DarkMode,
            onClick = onToggleDarkMode
        )

        DividerSection()

        /* ───────────── Import / Export ───────────── */

        BottomSheetItem(
            "Import from text file",
            Icons.Default.Upload,
            onImport
        )

        BottomSheetItem(
            "Export notes",
            Icons.Default.Download,
            onExport
        )

        DividerSection()

        /* ───────────── Sort ───────────── */

        BottomSheetItem(
            text = "Sort notes · ${currentSort.label()}",
            icon = Icons.AutoMirrored.Filled.Sort,
            onClick = onOpenSort
        )

        DividerSection()

        /* ───────────── Support ───────────── */

        BottomSheetItem(
            "Support development",
            Icons.Default.LocalCafe
        ) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    "https://buymeacoffee.com/athread".toUri()
                )
            )
        }

        DividerSection()

        /* ───────────── Account ───────────── */

        BottomSheetItem(
            "Logout",
            Icons.AutoMirrored.Filled.Logout,
            onLogout
        )

        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun BottomSheetItem(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(text) },
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
private fun DividerSection() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 8.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    )
}

private fun NotesSort.label(): String =
    when (this) {
        NotesSort.DATE_NEWEST -> "Newest first"
        NotesSort.DATE_OLDEST -> "Oldest first"
        NotesSort.TITLE_ASC -> "Title A–Z"
        NotesSort.TITLE_DESC -> "Title Z–A"
    }
