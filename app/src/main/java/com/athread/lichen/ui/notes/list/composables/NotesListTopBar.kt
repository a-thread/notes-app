package com.athread.lichen.ui.notes.list.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.athread.lichen.ui.notes.list.state.NotesLayoutMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesListTopBar(
    layoutMode: NotesLayoutMode,
    onToggleLayout: () -> Unit,
    onMenu: () -> Unit
) {
    val toggleIcon =
        if (layoutMode == NotesLayoutMode.GRID)
            Icons.AutoMirrored.Filled.ViewList
        else
            Icons.Filled.ViewModule

    TopAppBar(
        title = {
            Text(
                text = "lichen",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.4.sp
                )
            )
        },
        actions = {
            IconButton(onClick = onToggleLayout) {
                Icon(
                    imageVector = toggleIcon,
                    contentDescription = "Toggle layout"
                )
            }
            IconButton(onClick = onMenu) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Menu"
                )
            }
        }
    )
}
