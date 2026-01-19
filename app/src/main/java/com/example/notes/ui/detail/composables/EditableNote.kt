package com.example.notes.ui.detail.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.sp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.graphics.Color
import com.example.notes.ui.detail.logic.transformEditorInput

@Composable
fun EditableNote(
    title: String,
    body: TextFieldValue,
    onTitleChange: (String) -> Unit,
    onBodyChange: (TextFieldValue) -> Unit,
    onSave: () -> Unit,
    isNewNote: Boolean
) {
    val titleFocus = remember { FocusRequester() }
    val bodyFocus = remember { FocusRequester() }
    var didRequestInitialFocus by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!didRequestInitialFocus) {
            didRequestInitialFocus = true

            if (isNewNote) {
                titleFocus.requestFocus()
            } else {
                bodyFocus.requestFocus()
            }
        }
    }

    // ---------- TITLE ----------
    TextField(
        value = title,
        onValueChange = onTitleChange,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(titleFocus),
        placeholder = { Text("Title") },
        singleLine = true,
        textStyle = TextStyle(
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground
        ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )

    // ---------- BODY ----------
    TextField(
        value = body,
        onValueChange = { newValue ->
            onBodyChange(
                transformEditorInput(
                    oldValue = body,
                    newValue = newValue
                )
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(bodyFocus)
            .onPreviewKeyEvent {
                if (it.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false

                when {
                    it.key == Key.Tab && it.isShiftPressed -> {
                        titleFocus.requestFocus()
                        true
                    }
                    it.key == Key.Enter && it.isCtrlPressed -> {
                        onSave()
                        true
                    }
                    else -> false
                }
            },
        placeholder = {
            Text(
                text = "Start typing…",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        textStyle = TextStyle(
            fontSize = 16.sp,
            lineHeight = 22.sp,
            color = MaterialTheme.colorScheme.onBackground
        ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )

}
