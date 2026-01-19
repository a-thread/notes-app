package com.example.notes.ui.list.noteslist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.notes.domain.model.Note
import com.example.notes.ui.list.shared.SwipeToDelete

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesList(
    notes: List<Note>,
    onEditNote: (Note) -> Unit,
    onDeleteNote: (Note) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(notes, key = { it.id }) { note ->
            SwipeToDelete(
                onDelete = { onDeleteNote(note) }
            ) {
                NoteItem(note, onEditNote)
            }
        }
    }
}