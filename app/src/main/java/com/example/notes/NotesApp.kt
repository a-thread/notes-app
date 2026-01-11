package com.example.notes

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.notes.ui.noteslist.NotesListScreen
import com.example.notes.ui.noteslist.NotesListViewModel

@Composable
fun NotesApp(
    viewModel: NotesListViewModel,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier) {
        NotesListScreen(viewModel = viewModel)
    }
}
