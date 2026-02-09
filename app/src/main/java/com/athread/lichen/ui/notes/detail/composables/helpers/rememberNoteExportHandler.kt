package com.athread.lichen.ui.notes.detail.composables.helpers

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.athread.lichen.ui.notes.detail.state.NoteDetailViewModel
import kotlinx.coroutines.launch

@Composable
fun rememberNoteExportHandler(
    viewModel: NoteDetailViewModel
): (String) -> Unit {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/plain")
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            val (_, content) = viewModel.exportNote()
            context.contentResolver
                .openOutputStream(uri)
                ?.bufferedWriter()
                ?.use { it.write(content) }
        }
    }

    return launcher::launch
}
