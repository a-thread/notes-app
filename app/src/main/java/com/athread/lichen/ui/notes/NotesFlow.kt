package com.athread.lichen.ui.notes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.athread.lichen.ui.notes.shared.state.AppNavigationViewModel
import com.athread.lichen.ui.notes.shared.state.AppRoute
import com.athread.lichen.di.AppModule
import com.athread.lichen.ui.notes.detail.NoteDetailScreen
import com.athread.lichen.ui.notes.detail.state.NoteDetailViewModel
import com.athread.lichen.ui.notes.detail.state.NoteDetailViewModelFactory
import com.athread.lichen.ui.notes.list.NotesListScreen
import com.athread.lichen.ui.notes.shared.theme.ThemeViewModel
import java.util.UUID

@Composable
fun NotesFlow(
    userId: UUID,
    themeViewModel: ThemeViewModel,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember(context) {
        AppModule.provideNoteRepository(context)
    }

    val navVm: AppNavigationViewModel = viewModel()

    when (val route = navVm.route) {

        AppRoute.List -> {
            NotesListScreen(
                noteRepository = repository,
                themeViewModel = themeViewModel,
                onCreateNote = navVm::createNote,
                onEditNote = navVm::editNote,
                onLogout = onLogout
            )
        }

        AppRoute.Create,
        is AppRoute.Edit -> {

            val note = (route as? AppRoute.Edit)?.note

            val editorKey = remember(route) {
                note?.id?.toString() ?: UUID.randomUUID().toString()
            }

            val editorVm: NoteDetailViewModel = viewModel(
                key = editorKey,
                factory = NoteDetailViewModelFactory(
                    repository = repository,
                    userId = userId,
                    existingNote = note
                )
            )

            NoteDetailScreen(
                viewModel = editorVm,
                onDone = navVm::closeEditor
            )
        }
    }
}
