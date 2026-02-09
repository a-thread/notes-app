package com.athread.lichen.ui.notes.list.composables.helpers

import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.athread.lichen.ui.notes.list.state.NotesListViewModel
import kotlinx.coroutines.launch

@Composable
fun rememberImportExportHandlers(
    viewModel: NotesListViewModel,
    snackbarHostState: SnackbarHostState
): Pair<(Array<String>) -> Unit, (String) -> Unit> {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val importLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri == null) return@rememberLauncherForActivityResult

            val resolver = context.contentResolver
            val fileName =
                resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
                    ?.use { it.takeIf { it.moveToFirst() }?.getString(0) }
                    ?: "Imported note"

            val text =
                resolver.openInputStream(uri)?.bufferedReader()?.readText().orEmpty()

            if (text.isBlank()) return@rememberLauncherForActivityResult

            viewModel.importTextFile(fileName, text)
            scope.launch { snackbarHostState.showSnackbar("Note imported") }
        }

    val exportLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) { uri ->
            if (uri == null) return@rememberLauncherForActivityResult

            scope.launch {
                val text = viewModel.exportAllNotes()
                context.contentResolver
                    .openOutputStream(uri)
                    ?.bufferedWriter()
                    ?.use { it.write(text) }

                snackbarHostState.showSnackbar("Export complete")
            }
        }

    return Pair(
        { importLauncher.launch(it) },
        { exportLauncher.launch(it) }
    )
}
