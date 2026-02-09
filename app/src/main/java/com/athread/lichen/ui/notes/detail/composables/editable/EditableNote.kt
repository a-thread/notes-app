package com.athread.lichen.ui.notes.detail.composables.editable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    var isBodyFocused by remember { mutableStateOf(false) }
    var didRequestInitialFocus by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!didRequestInitialFocus) {
            didRequestInitialFocus = true
            if (isNewNote) titleFocus.requestFocus()
            else bodyFocus.requestFocus()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {

        /* ---------- TITLE ---------- */
        TextField(
            value = title,
            onValueChange = onTitleChange,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(titleFocus),
            placeholder = { Text("Title") },
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            ),
            colors = transparentTextFieldColors()
        )

        Spacer(Modifier.height(6.dp))

        /* ---------- FORMATTING BAR (INLINE / SNACKBAR STYLE) ---------- */
        if (isBodyFocused) {
            FormattingToolbar(
                body = body,
                onApply = { onBodyChange(it) },
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        /* ---------- BODY ---------- */
        TextField(
            value = body,
            onValueChange = {
                onBodyChange(
                    transformEditorInput(
                        oldValue = body,
                        newValue = it
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .focusRequester(bodyFocus)
                .onFocusChanged { state ->
                    isBodyFocused = state.isFocused
                }
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
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.onBackground
            ),
            colors = transparentTextFieldColors()
        )
    }
}

/* ---------- STYLING ---------- */
@Composable
private fun transparentTextFieldColors() =
    TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent
    )
