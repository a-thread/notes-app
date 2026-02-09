package com.athread.lichen.ui.notes.detail.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import com.athread.lichen.ui.notes.detail.state.EditorMode
import com.athread.lichen.ui.notes.detail.composables.editable.EditableNote
import com.athread.lichen.ui.notes.detail.composables.readonly.ReadOnlyNote

@Composable
fun NoteDetailContent(
    mode: EditorMode,
    title: String,
    body: TextFieldValue,
    isNewNote: Boolean,
    onTitleChange: (String) -> Unit,
    onBodyChange: (TextFieldValue) -> Unit,
    onSave: () -> Unit,
    onEdit: () -> Unit,
    onToggleChecklist: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollModifier =
        if (mode == EditorMode.ReadOnly)
            Modifier.verticalScroll(rememberScrollState())
        else Modifier

    Column(
        modifier = modifier.then(scrollModifier)
    ) {
        when (mode) {
            EditorMode.ReadOnly -> {
                ReadOnlyNote(
                    title = title,
                    body = body.text,
                    onEdit = onEdit,
                    onToggleChecklist = onToggleChecklist
                )
            }
            EditorMode.Edit -> {
                EditableNote(
                    title = title,
                    body = body,
                    onTitleChange = onTitleChange,
                    onBodyChange = onBodyChange,
                    onSave = onSave,
                    isNewNote = isNewNote
                )
            }
        }
    }
}
